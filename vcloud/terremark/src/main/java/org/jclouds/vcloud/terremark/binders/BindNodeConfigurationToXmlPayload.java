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

package org.jclouds.vcloud.terremark.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.terremark.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NS;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.vcloud.terremark.domain.NodeConfiguration;

import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindNodeConfigurationToXmlPayload extends BindToStringPayload {

   private final String ns;

   @Inject
   BindNodeConfigurationToXmlPayload(@Named(PROPERTY_TERREMARK_EXTENSION_NS) String ns) {
      this.ns = ns;
   }

   @Override
   public void bindToRequest(HttpRequest request, Object input) {
      NodeConfiguration nodeConfiguration = (NodeConfiguration) checkNotNull(input,
               "nodeConfiguration");
      checkArgument(nodeConfiguration.getDescription() != null
               || nodeConfiguration.getEnabled() != null || nodeConfiguration.getName() != null,
               "no configuration set");
      try {
         super.bindToRequest(request, generateXml(nodeConfiguration));
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      } catch (FactoryConfigurationError e) {
         throw new RuntimeException(e);
      } catch (TransformerException e) {
         throw new RuntimeException(e);
      }

   }

   protected String generateXml(NodeConfiguration nodeConfiguration)
            throws ParserConfigurationException, FactoryConfigurationError, TransformerException {
      XMLBuilder rootBuilder = XMLBuilder.create("NodeService").a("xmlns", ns).a("xmlns:xsi",
               "http://www.w3.org/2001/XMLSchema-instance").a("xmlns:xsd",
               "http://www.w3.org/2001/XMLSchema");
      if (nodeConfiguration.getDescription() != null)
         rootBuilder.e("Description").t(nodeConfiguration.getDescription());
      if (nodeConfiguration.getName() != null)
         rootBuilder.e("Name").t(nodeConfiguration.getName());
      if (nodeConfiguration.getEnabled() != null)
         rootBuilder.e("Enabled").t(nodeConfiguration.getEnabled());
      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      return rootBuilder.asString(outputProperties);
   }

   protected String ifNullDefaultTo(String value, String defaultValue) {
      return value != null ? value : checkNotNull(defaultValue, "defaultValue");
   }
}
