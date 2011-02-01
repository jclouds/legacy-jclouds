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
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_FENCEMODE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jclouds.http.HttpRequest;
import org.jclouds.logging.Logger;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.NetworkConfig;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindInstantiateVAppTemplateParamsToXmlPayload implements MapBinder {
   @Resource
   protected Logger logger = Logger.NULL;

   protected final String ns;
   protected final String schema;
   protected final BindToStringPayload stringBinder;
   protected final URI defaultNetwork;
   protected final FenceMode defaultFenceMode;
   protected final DefaultNetworkNameInTemplate defaultNetworkNameInTemplate;
   protected final VCloudClient client;

   @Inject
   public BindInstantiateVAppTemplateParamsToXmlPayload(DefaultNetworkNameInTemplate defaultNetworkNameInTemplate,
            BindToStringPayload stringBinder, @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns,
            @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema, @Network URI network,
            @Named(PROPERTY_VCLOUD_DEFAULT_FENCEMODE) String fenceMode, VCloudClient client) {
      this.defaultNetworkNameInTemplate = defaultNetworkNameInTemplate;
      this.ns = ns;
      this.schema = schema;
      this.stringBinder = stringBinder;
      this.defaultNetwork = network;
      this.defaultFenceMode = FenceMode.fromValue(fenceMode);
      this.client = client;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, String> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest<?>,
               "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest<?> gRequest = (GeneratedHttpRequest<?>) request;
      checkState(gRequest.getArgs() != null, "args should be initialized at this point");
      String name = checkNotNull(postParams.remove("name"), "name");
      final URI template = URI.create(checkNotNull(postParams.remove("template"), "template"));

      boolean deploy = true;
      boolean powerOn = true;
      Boolean customizeOnInstantiate = null;

      Set<? extends NetworkConfig> networkConfig = null;

      NetworkConfigDecorator networknetworkConfigDecorator = new NetworkConfigDecorator(template, defaultNetwork,
               defaultFenceMode, defaultNetworkNameInTemplate);

      InstantiateVAppTemplateOptions options = findOptionsInArgsOrNull(gRequest);

      if (options != null) {
         if (options.getNetworkConfig().size() > 0)
            networkConfig = Sets.newLinkedHashSet(Iterables.transform(options.getNetworkConfig(),
                     networknetworkConfigDecorator));
         deploy = ifNullDefaultTo(options.shouldDeploy(), deploy);
         powerOn = ifNullDefaultTo(options.shouldPowerOn(), powerOn);
         customizeOnInstantiate = options.shouldCustomizeOnInstantiate();
      }

      if (networkConfig == null)
         networkConfig = ImmutableSet.of(networknetworkConfigDecorator.apply(null));

      try {
         return stringBinder.bindToRequest(request, generateXml(name, deploy, powerOn, template, networkConfig,
                  customizeOnInstantiate));
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      } catch (FactoryConfigurationError e) {
         throw new RuntimeException(e);
      } catch (TransformerException e) {
         throw new RuntimeException(e);
      }

   }

   @VisibleForTesting
   Set<? extends Vm> ifCustomizationScriptIsSetGetVmsInTemplate(String customizationScript, final URI template) {
      Set<? extends Vm> vms = Sets.newLinkedHashSet();
      if (customizationScript != null) {
         VAppTemplate vAppTemplate = client.getVAppTemplate(template);
         checkArgument(vAppTemplate != null, "vAppTemplate %s not found!", template);
         vms = vAppTemplate.getChildren();
         checkArgument(vms.size() > 0, "no vms found in vAppTemplate %s", vAppTemplate);
      }
      return vms;
   }

   protected static final class NetworkConfigDecorator implements Function<NetworkConfig, NetworkConfig> {
      private final URI template;
      private final URI defaultNetwork;
      private final FenceMode defaultFenceMode;
      private final DefaultNetworkNameInTemplate defaultNetworkNameInTemplate;

      protected NetworkConfigDecorator(URI template, URI defaultNetwork, FenceMode defaultFenceMode,
               DefaultNetworkNameInTemplate defaultNetworkNameInTemplate) {
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
         String networkName = from.getNetworkName() != null ? from.getNetworkName() : defaultNetworkNameInTemplate
                  .apply(template);
         return new NetworkConfig(networkName, network, fenceMode);
      }
   }

   @Singleton
   public static class DefaultNetworkNameInTemplate implements Function<URI, String> {
      @Resource
      protected Logger logger = Logger.NULL;

      private final VCloudClient client;

      @Inject
      DefaultNetworkNameInTemplate(VCloudClient client) {
         this.client = client;
      }

      @Override
      public String apply(URI template) {
         String networkName;
         VAppTemplate vAppTemplate = client.getVAppTemplate(template);
         checkArgument(vAppTemplate != null, "vAppTemplate %s not found!", template);
         Set<org.jclouds.vcloud.domain.ovf.network.Network> networks = vAppTemplate.getNetworkSection().getNetworks();
         checkArgument(networks.size() > 0, "no networks found in vAppTemplate %s", vAppTemplate);
         if (networks.size() > 1)
            logger.warn("multiple networks found for %s, choosing first from: %s", vAppTemplate.getName(), networks);
         networkName = Iterables.get(networks, 0).getName();
         return networkName;
      }
   }

   protected String generateXml(String name, boolean deploy, boolean powerOn, URI template,
            Iterable<? extends NetworkConfig> networkConfig, @Nullable Boolean customizeOnInstantiate)
            throws ParserConfigurationException, FactoryConfigurationError, TransformerException {
      XMLBuilder rootBuilder = buildRoot(name).a("deploy", deploy + "").a("powerOn", powerOn + "");
      XMLBuilder instantiationParamsBuilder = rootBuilder.e("InstantiationParams");
      addNetworkConfig(instantiationParamsBuilder, networkConfig);
      addCustomizationConfig(instantiationParamsBuilder, customizeOnInstantiate);
      rootBuilder.e("Source").a("href", template.toASCIIString());
      rootBuilder.e("AllEULAsAccepted").t("true");

      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      return rootBuilder.asString(outputProperties);
   }

   protected void addCustomizationConfig(XMLBuilder instantiationParamsBuilder, Boolean customizeOnInstantiate) {
      if (customizeOnInstantiate != null) {
//         XMLBuilder customizationSectionBuilder = instantiationParamsBuilder.e("CustomizationSection");
//         customizationSectionBuilder.e("ovf:Info").t("VApp template customization section");
//         customizationSectionBuilder.e("CustomizeOnInstantiate").t(customizeOnInstantiate.toString());
      }
   }

   protected void addNetworkConfig(XMLBuilder instantiationParamsBuilder,
            Iterable<? extends NetworkConfig> networkConfig) {
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
      return XMLBuilder.create("InstantiateVAppTemplateParams").a("name", name).a("xmlns", ns).a("xmlns:ovf",
               "http://schemas.dmtf.org/ovf/envelope/1");
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
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException("InstantiateVAppTemplateParams is needs parameters");
   }

   public static <T> T ifNullDefaultTo(T value, T defaultValue) {
      return value != null ? value : checkNotNull(defaultValue, "defaultValue");
   }
}
