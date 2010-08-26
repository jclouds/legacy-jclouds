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
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

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

   @Inject
   public BindInstantiateVAppTemplateParamsToXmlPayload(BindToStringPayload stringBinder,
            @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns, @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema,
            @Network URI network, @Named(PROPERTY_VCLOUD_DEFAULT_FENCEMODE) String fenceMode) {
      this.ns = ns;
      this.schema = schema;
      this.stringBinder = stringBinder;
      this.defaultNetwork = network;
      this.defaultFenceMode = FenceMode.fromValue(fenceMode);
   }

   @SuppressWarnings("unchecked")
   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
               "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      checkState(gRequest.getArgs() != null, "args should be initialized at this point");
      String name = checkNotNull(postParams.remove("name"), "name");
      String template = checkNotNull(postParams.remove("template"), "template");

      String network = defaultNetwork.toASCIIString();
      FenceMode fenceMode = defaultFenceMode;
      logger.warn("hack alert; we need to actually get the network name from the vAppTemplate's ovf:Network ovf:name");
      String networkName = "vAppNet-vApp Internal";
      boolean deploy = true;
      boolean powerOn = true;

      InstantiateVAppTemplateOptions options = findOptionsInArgsOrNull(gRequest);
      if (options != null) {
         network = ifNullDefaultTo(options.getNetwork(), network);
         fenceMode = ifNullDefaultTo(options.getFenceMode(), defaultFenceMode);

         networkName = ifNullDefaultTo(options.getNetworkName(), networkName);
         deploy = ifNullDefaultTo(options.shouldDeploy(), deploy);
         powerOn = ifNullDefaultTo(options.shouldPowerOn(), powerOn);
      }
      try {
         stringBinder.bindToRequest(request, generateXml(name, deploy, powerOn, template, networkName, fenceMode, URI
                  .create(network)));
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      } catch (FactoryConfigurationError e) {
         throw new RuntimeException(e);
      } catch (TransformerException e) {
         throw new RuntimeException(e);
      }

   }

   protected String generateXml(String name, boolean deploy, boolean powerOn, String template, String networkName,
            FenceMode fenceMode, URI network) throws ParserConfigurationException, FactoryConfigurationError,
            TransformerException {
      XMLBuilder rootBuilder = buildRoot(name).a("deploy", deploy + "").a("powerOn", powerOn + "");

      XMLBuilder instantiationParamsBuilder = rootBuilder.e("InstantiationParams");
      addNetworkConfig(instantiationParamsBuilder, networkName, fenceMode, network);

      rootBuilder.e("Source").a("href", template);
      rootBuilder.e("AllEULAsAccepted").t("true");

      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      return rootBuilder.asString(outputProperties);
   }

   protected void addNetworkConfig(XMLBuilder instantiationParamsBuilder, String name, FenceMode fenceMode, URI network) {
      XMLBuilder networkConfigBuilder = instantiationParamsBuilder.e("NetworkConfigSection");
      networkConfigBuilder.e("ovf:Info").t("Configuration parameters for logical networks");
      XMLBuilder configurationBuilder = networkConfigBuilder.e("NetworkConfig").a("networkName", name).e(
               "Configuration");
      configurationBuilder.e("ParentNetwork").a("href", network.toASCIIString());
      if (fenceMode != null) {
         configurationBuilder.e("FenceMode").t(fenceMode.toString());
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

   public void bindToRequest(HttpRequest request, Object input) {
      throw new IllegalStateException("InstantiateVAppTemplateParams is needs parameters");
   }

   protected <T> T ifNullDefaultTo(T value, T defaultValue) {
      return value != null ? value : checkNotNull(defaultValue, "defaultValue");
   }
}
