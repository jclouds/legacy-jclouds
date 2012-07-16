/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jclouds.http.HttpRequest;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.NetworkConfig;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
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
   protected final BindToStringPayload stringBinder;
   protected final Supplier<ReferenceType> defaultNetwork;
   protected final FenceMode defaultFenceMode;
   protected final LoadingCache<URI, VAppTemplate> templateCache;
   protected final Function<VAppTemplate, String> defaultNetworkNameInTemplate;

   @Inject
   public BindInstantiateVAppTemplateParamsToXmlPayload(LoadingCache<URI, VAppTemplate> templateCache,
            @Network Function<VAppTemplate, String> defaultNetworkNameInTemplate, BindToStringPayload stringBinder,
            @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns, @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema,
            @Network Supplier<ReferenceType> network, FenceMode fenceMode) {
      this.templateCache = templateCache;
      this.defaultNetworkNameInTemplate = defaultNetworkNameInTemplate;
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
      checkState(gRequest.getArgs() != null, "args should be initialized at this point");
      String name = checkNotNull(postParams.remove("name"), "name").toString();
      URI template = URI.create(checkNotNull(postParams.remove("template"), "template").toString());

      Set<NetworkConfig> networkConfig = null;

      NetworkConfigDecorator networkConfigDecorator = new NetworkConfigDecorator(templateCache.getUnchecked(template),
            defaultNetwork.get().getHref(), defaultFenceMode, defaultNetworkNameInTemplate);

      InstantiateVAppTemplateOptions options = findOptionsInArgsOrNull(gRequest);

      if (options != null) {
         if (options.getNetworkConfig().size() > 0)
            networkConfig = ImmutableSet
                     .copyOf(transform(options.getNetworkConfig(), networkConfigDecorator));
      } else {
         options = new InstantiateVAppTemplateOptions();
      }

      if (networkConfig == null)
         networkConfig = ImmutableSet.of(networkConfigDecorator.apply(null));

      try {
         return stringBinder.bindToRequest(request, generateXml(name, options.getDescription(), options.shouldDeploy(),
                  options.shouldPowerOn(), template, networkConfig));
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      } catch (FactoryConfigurationError e) {
         throw new RuntimeException(e);
      } catch (TransformerException e) {
         throw new RuntimeException(e);
      }

   }

   @VisibleForTesting
   Set<Vm> ifCustomizationScriptIsSetGetVmsInTemplate(String customizationScript, final URI template) {
      Set<Vm> vms = ImmutableSet.of();
      if (customizationScript != null) {
         VAppTemplate vAppTemplate = templateCache.getUnchecked(template);
         checkArgument(vAppTemplate != null, "vAppTemplate %s not found!", template);
         vms = vAppTemplate.getChildren();
         checkArgument(vms.size() > 0, "no vms found in vAppTemplate %s", vAppTemplate);
      }
      return vms;
   }

   protected static final class NetworkConfigDecorator implements Function<NetworkConfig, NetworkConfig> {
      private final VAppTemplate template;
      private final URI defaultNetwork;
      private final FenceMode defaultFenceMode;
      private final Function<VAppTemplate, String> defaultNetworkNameInTemplate;

      protected NetworkConfigDecorator(VAppTemplate template, URI defaultNetwork, FenceMode defaultFenceMode,
               Function<VAppTemplate, String> defaultNetworkNameInTemplate) {
         this.template = checkNotNull(template, "template");
         this.defaultNetwork = checkNotNull(defaultNetwork, "defaultNetwork");
         this.defaultFenceMode = checkNotNull(defaultFenceMode, "defaultFenceMode");
         this.defaultNetworkNameInTemplate = checkNotNull(defaultNetworkNameInTemplate, "defaultNetworkNameInTemplate");
      }

      @Override
      public NetworkConfig apply(NetworkConfig from) {
         if (from == null)
            return new NetworkConfig(defaultNetworkNameInTemplate.apply(template), defaultNetwork, defaultFenceMode);
         URI network = ifNullDefaultTo(from.getParentNetwork(), defaultNetwork);
         FenceMode fenceMode = ifNullDefaultTo(from.getFenceMode(), defaultFenceMode);
         // using conditional statement instead of ifNullDefaultTo so that we lazy invoke the
         // function, as it is an expensive one.
         String networkName = from.getNetworkName() != null ? from.getNetworkName() : defaultNetworkNameInTemplate
                  .apply(template);
         return new NetworkConfig(networkName, network, fenceMode);
      }
   }

   protected String generateXml(String name, @Nullable String description, boolean deploy, boolean powerOn,
         URI template, Iterable<NetworkConfig> networkConfig)
         throws ParserConfigurationException, FactoryConfigurationError, TransformerException {
      XMLBuilder rootBuilder = buildRoot(name).a("deploy", deploy + "").a("powerOn", powerOn + "");
      if (description != null)
         rootBuilder.e("Description").t(description);
      XMLBuilder instantiationParamsBuilder = rootBuilder.e("InstantiationParams");
      addNetworkConfig(instantiationParamsBuilder, networkConfig);
      rootBuilder.e("Source").a("href", template.toASCIIString());
      rootBuilder.e("AllEULAsAccepted").t("true");

      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      return rootBuilder.asString(outputProperties);
   }

   protected void addNetworkConfig(XMLBuilder instantiationParamsBuilder,
         Iterable<NetworkConfig> networkConfig) {
      XMLBuilder networkConfigBuilder = instantiationParamsBuilder.e("NetworkConfigSection");
      networkConfigBuilder.e("ovf:Info").t("Configuration parameters for logical networks");
      for (NetworkConfig n : networkConfig) {
         XMLBuilder configurationBuilder = networkConfigBuilder.e("NetworkConfig").a("networkName", n.getNetworkName())
               .e("Configuration");
         configurationBuilder.e("ParentNetwork").a("href", n.getParentNetwork().toASCIIString());
         if (n.getFenceMode() != null) {
            configurationBuilder.e("FenceMode").t(n.getFenceMode().toString());
         }
      }
   }

   protected XMLBuilder buildRoot(String name) throws ParserConfigurationException, FactoryConfigurationError {
      return XMLBuilder.create("InstantiateVAppTemplateParams").a("name", name).a("xmlns", ns)
            .a("xmlns:ovf", "http://schemas.dmtf.org/ovf/envelope/1");
   }

   protected InstantiateVAppTemplateOptions findOptionsInArgsOrNull(GeneratedHttpRequest gRequest) {
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

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException("InstantiateVAppTemplateParams is needs parameters");
   }

   public static <T> T ifNullDefaultTo(T value, T defaultValue) {
      return value != null ? value : checkNotNull(defaultValue, "defaultValue");
   }
}
