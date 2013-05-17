/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_FENCEMODE;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedMap;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.http.HttpRequest;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.endpoints.Network;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions.NetworkConfig;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindInstantiateVAppTemplateParamsToXmlPayload implements MapBinder {

   protected final String ns;
   protected final String schema;
   private final BindToStringPayload stringBinder;
   protected final Map<ResourceType, String> virtualHardwareToInstanceId = ImmutableMap.of(ResourceType.PROCESSOR, "1",
         ResourceType.MEMORY, "2", ResourceType.DISK_DRIVE, "9");
   private final Supplier<ReferenceType> defaultNetwork;
   private final String defaultFenceMode;

   @Inject
   public BindInstantiateVAppTemplateParamsToXmlPayload(BindToStringPayload stringBinder,
         @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns, @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema,
         @Network Supplier<ReferenceType> network, @Named(PROPERTY_VCLOUD_DEFAULT_FENCEMODE) String fenceMode) {
      this.ns = ns;
      this.schema = schema;
      this.stringBinder = stringBinder;
      this.defaultNetwork = network;
      this.defaultFenceMode = fenceMode;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
            "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      String name = checkNotNull(postParams.remove("name"), "name").toString();
      String template = checkNotNull(postParams.remove("template"), "template").toString();

      SortedMap<ResourceType, String> virtualHardwareQuantity = Maps.newTreeMap();

      InstantiateVAppTemplateOptions options = findOptionsInArgsOrNull(gRequest);
      String network = (defaultNetwork != null) ? defaultNetwork.get().getHref().toASCIIString() : null;
      String fenceMode = defaultFenceMode;
      String networkName = name;
      if (options != null) {
         if (options.getNetworkConfig().size() > 0) {
            NetworkConfig config = Iterables.get(options.getNetworkConfig(), 0);
            network = ifNullDefaultTo(config.getParentNetwork(), network);
            fenceMode = ifNullDefaultTo(config.getFenceMode(), defaultFenceMode);
            networkName = ifNullDefaultTo(config.getNetworkName(), networkName);
         }
         addQuantity(options, virtualHardwareQuantity);
      }
      try {
         return stringBinder.bindToRequest(request,
               generateXml(name, template, virtualHardwareQuantity, networkName, fenceMode, URI.create(network)));
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      } catch (FactoryConfigurationError e) {
         throw new RuntimeException(e);
      } catch (TransformerException e) {
         throw new RuntimeException(e);
      }

   }

   protected String generateXml(String name, String template, SortedMap<ResourceType, String> virtualHardwareQuantity,
         String networkName, @Nullable String fenceMode, URI network) throws ParserConfigurationException,
         FactoryConfigurationError, TransformerException {
      XMLBuilder rootBuilder = buildRoot(name);

      rootBuilder.e("VAppTemplate").a("href", template);

      XMLBuilder instantiationParamsBuilder = rootBuilder.e("InstantiationParams");
      addVirtualQuantityIfPresent(instantiationParamsBuilder, virtualHardwareQuantity);
      addNetworkConfig(instantiationParamsBuilder, networkName, fenceMode, network);
      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      return rootBuilder.asString(outputProperties);
   }

   protected void addNetworkConfig(XMLBuilder instantiationParamsBuilder, String name, @Nullable String fenceMode,
         URI network) {
      XMLBuilder networkConfigBuilder = instantiationParamsBuilder.e("NetworkConfigSection").e("NetworkConfig")
            .a("name", name);
      if (fenceMode != null) {
         XMLBuilder featuresBuilder = networkConfigBuilder.e("Features");
         featuresBuilder.e("FenceMode").t(fenceMode);
      }
      networkConfigBuilder.e("NetworkAssociation").a("href", network.toASCIIString());
   }

   protected XMLBuilder buildRoot(String name) throws ParserConfigurationException, FactoryConfigurationError {
      XMLBuilder rootBuilder = XMLBuilder.create("InstantiateVAppTemplateParams").a("name", name).a("xmlns", ns)
            .a("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance").a("xsi:schemaLocation", ns + " " + schema)
            .a("xmlns:ovf", "http://schemas.dmtf.org/ovf/envelope/1");
      return rootBuilder;
   }

   private void addQuantity(InstantiateVAppTemplateOptions options, Map<ResourceType, String> virtualHardwareQuantity) {
      if (options.getCpuCount() != null) {
         virtualHardwareQuantity.put(ResourceType.PROCESSOR, options.getCpuCount());
      }
      if (options.getMemorySizeMegabytes() != null) {
         virtualHardwareQuantity.put(ResourceType.MEMORY, options.getMemorySizeMegabytes());
      }
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException("InstantiateVAppTemplateParams is needs parameters");
   }

   protected String ifNullDefaultTo(Object value, String defaultValue) {
      return value != null ? value.toString() : checkNotNull(defaultValue, "defaultValue");
   }

   ThreadLocal<Map<String, String>> propLocal = new ThreadLocal<Map<String, String>>();

   protected InstantiateVAppTemplateOptions findOptionsInArgsOrNull(GeneratedHttpRequest gRequest) {
      InstantiateVAppTemplateOptions options = null;
      for (Object arg : gRequest.getInvocation().getArgs()) {
         if (arg instanceof InstantiateVAppTemplateOptions) {
            options = (InstantiateVAppTemplateOptions) arg;
         } else if (arg instanceof InstantiateVAppTemplateOptions[]) {
            InstantiateVAppTemplateOptions[] optionsA = (InstantiateVAppTemplateOptions[]) arg;
            options = (optionsA.length > 0) ? optionsA[0] : null;
         }
      }
      if (options != null)
         propLocal.set(options.getProperties());
      return options;
   }

   protected void addVirtualQuantityIfPresent(XMLBuilder instantiationParamsBuilder,
         SortedMap<ResourceType, String> virtualHardwareQuantity) {
      XMLBuilder productSectionBuilder = instantiationParamsBuilder.e("ProductSection").a("xmlns:q1", ns)
            .a("xmlns:ovf", "http://schemas.dmtf.org/ovf/envelope/1");
      if (propLocal.get() != null) {
         for (Entry<String, String> entry : propLocal.get().entrySet()) {
            productSectionBuilder.e("Property").a("xmlns", "http://schemas.dmtf.org/ovf/envelope/1")
                  .a("ovf:key", entry.getKey()).a("ovf:value", entry.getValue());
         }
         propLocal.set(null);
      }
      if (virtualHardwareQuantity.size() > 0) {
         XMLBuilder virtualHardwareSectionBuilder = instantiationParamsBuilder.e("VirtualHardwareSection").a(
               "xmlns:q1", ns);
         for (Entry<ResourceType, String> entry : virtualHardwareQuantity.entrySet()) {
            XMLBuilder itemBuilder = virtualHardwareSectionBuilder.e("Item").a("xmlns",
                  "http://schemas.dmtf.org/ovf/envelope/1");
            itemBuilder.e("InstanceID")
                  .a("xmlns", "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData")
                  .t(virtualHardwareToInstanceId.get(entry.getKey()));
            itemBuilder.e("ResourceType")
                  .a("xmlns", "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData")
                  .t(entry.getKey().value());
            itemBuilder.e("VirtualQuantity")
                  .a("xmlns", "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData")
                  .t(entry.getValue());
         }
      }
   }
}
