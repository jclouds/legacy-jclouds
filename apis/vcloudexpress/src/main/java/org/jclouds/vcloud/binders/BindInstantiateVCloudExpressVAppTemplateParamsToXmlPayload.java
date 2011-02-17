/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_FENCEMODE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.domain.network.NetworkConfig;
import org.jclouds.vcloud.domain.ovf.ResourceType;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

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
public class BindInstantiateVCloudExpressVAppTemplateParamsToXmlPayload implements MapBinder {

   protected final String ns;
   protected final String schema;
   private final BindToStringPayload stringBinder;
   protected final Map<ResourceType, String> virtualHardwareToInstanceId = ImmutableMap.of(ResourceType.PROCESSOR, "1",
            ResourceType.MEMORY, "2", ResourceType.DISK_DRIVE, "9");
   private final URI defaultNetwork;
   private final String defaultFenceMode;
   private final String apiVersion;

   @Inject
   public BindInstantiateVCloudExpressVAppTemplateParamsToXmlPayload(BindToStringPayload stringBinder,
            @Named(PROPERTY_API_VERSION) String apiVersion, @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns,
            @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema, @Network URI network,
            @Named(PROPERTY_VCLOUD_DEFAULT_FENCEMODE) String fenceMode) {
      this.ns = ns;
      this.apiVersion = apiVersion;
      this.schema = schema;
      this.stringBinder = stringBinder;
      this.defaultNetwork = network;
      this.defaultFenceMode = fenceMode;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, String> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest<?>,
               "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest<?> gRequest = (GeneratedHttpRequest<?>) request;
      checkState(gRequest.getArgs() != null, "args should be initialized at this point");
      String name = checkNotNull(postParams.remove("name"), "name");
      String template = checkNotNull(postParams.remove("template"), "template");

      SortedMap<ResourceType, String> virtualHardwareQuantity = Maps.newTreeMap();

      InstantiateVAppTemplateOptions options = findOptionsInArgsOrNull(gRequest);
      String network = (defaultNetwork != null) ? defaultNetwork.toASCIIString() : null;
      String fenceMode = defaultFenceMode;
      String networkName = name;
      if (options != null) {
         if (options.getNetworkConfig().size() > 0) {
            NetworkConfig config = Iterables.get(options.getNetworkConfig(), 0);
            network = ifNullDefaultTo(config.getParentNetwork(), network);
            fenceMode = ifNullDefaultTo(config.getFenceMode(), defaultFenceMode);
            if (apiVersion.indexOf("0.8") != -1 && fenceMode.equals("bridged"))
               fenceMode = "allowInOut";
            networkName = ifNullDefaultTo(config.getNetworkName(), networkName);
         }
         addQuantity(options, virtualHardwareQuantity);
      }
      try {
         return stringBinder.bindToRequest(request, generateXml(name, template, virtualHardwareQuantity, networkName,
                  fenceMode, URI.create(network)));
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
      XMLBuilder networkConfigBuilder = instantiationParamsBuilder.e("NetworkConfigSection").e("NetworkConfig").a(
               "name", name);
      if (fenceMode != null) {
         XMLBuilder featuresBuilder = networkConfigBuilder.e("Features");
         featuresBuilder.e("FenceMode").t(fenceMode);
      }
      networkConfigBuilder.e("NetworkAssociation").a("href", network.toASCIIString());
   }

   protected void addVirtualQuantityIfPresent(XMLBuilder instantiationParamsBuilder,
            SortedMap<ResourceType, String> virtualHardwareQuantity) {
      if (virtualHardwareQuantity.size() > 0) {
         XMLBuilder virtualHardwareSectionBuilder = instantiationParamsBuilder.e("VirtualHardwareSection").a(
                  "xmlns:q1", ns);
         for (Entry<ResourceType, String> entry : virtualHardwareQuantity.entrySet()) {
            XMLBuilder itemBuilder = virtualHardwareSectionBuilder.e("Item").a("xmlns",
                     "http://schemas.dmtf.org/ovf/envelope/1");
            itemBuilder.e("InstanceID").a("xmlns",
                     "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData").t(
                     virtualHardwareToInstanceId.get(entry.getKey()));
            itemBuilder.e("ResourceType").a("xmlns",
                     "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData").t(
                     entry.getKey().value());
            itemBuilder.e("VirtualQuantity").a("xmlns",
                     "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData").t(
                     entry.getValue());
         }
      }
   }

   protected XMLBuilder buildRoot(String name) throws ParserConfigurationException, FactoryConfigurationError {
      XMLBuilder rootBuilder = XMLBuilder.create("InstantiateVAppTemplateParams").a("name", name).a("xmlns", ns).a(
               "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance").a("xsi:schemaLocation", ns + " " + schema).a(
               "xmlns:ovf", "http://schemas.dmtf.org/ovf/envelope/1");
      return rootBuilder;
   }

   protected InstantiateVAppTemplateOptions findOptionsInArgsOrNull(GeneratedHttpRequest<?> gRequest) {
      for (Object arg : gRequest.getArgs()) {
         if (arg instanceof InstantiateVAppTemplateOptions) {
            return (InstantiateVAppTemplateOptions) arg;
         } else if (arg instanceof InstantiateVAppTemplateOptions[]) {
            InstantiateVAppTemplateOptions[] options = (InstantiateVAppTemplateOptions[]) arg;
            return (options.length > 0) ? options[0] : null;
         }
      }
      return null;
   }

   private void addQuantity(InstantiateVAppTemplateOptions options, Map<ResourceType, String> virtualHardwareQuantity) {
      if (options.getCpuCount() != null) {
         virtualHardwareQuantity.put(ResourceType.PROCESSOR, options.getCpuCount());
      }
      if (options.getMemorySizeMegabytes() != null) {
         virtualHardwareQuantity.put(ResourceType.MEMORY, options.getMemorySizeMegabytes());
      }
      if (options.getDiskSizeKilobytes() != null) {
         virtualHardwareQuantity.put(ResourceType.DISK_DRIVE, options.getDiskSizeKilobytes());
      }
   }
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException("InstantiateVAppTemplateParams is needs parameters");
   }

   protected String ifNullDefaultTo(Object value, String defaultValue) {
      return value != null ? value.toString() : checkNotNull(defaultValue, "defaultValue");
   }
}
