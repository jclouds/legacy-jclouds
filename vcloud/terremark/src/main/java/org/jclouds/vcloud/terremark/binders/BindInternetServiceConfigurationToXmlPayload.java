/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static com.google.common.base.Preconditions.checkState;

import java.util.Properties;

import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.terremark.domain.InternetServiceConfiguration;

import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindInternetServiceConfigurationToXmlPayload extends BindToStringPayload {

   @SuppressWarnings("unchecked")
   @Override
   public void bindToRequest(HttpRequest request, Object input) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
               "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      checkState(gRequest.getArgs() != null, "args should be initialized at this point");
      InternetServiceConfiguration internetServiceConfiguration = (InternetServiceConfiguration) checkNotNull(
               input, "configuration");
      checkArgument(internetServiceConfiguration.getDescription() != null
               || internetServiceConfiguration.getEnabled() != null
               || internetServiceConfiguration.getName() != null || internetServiceConfiguration.getTimeout() != null, "no configuration set");
      try {
         super.bindToRequest(request, generateXml(internetServiceConfiguration));
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      } catch (FactoryConfigurationError e) {
         throw new RuntimeException(e);
      } catch (TransformerException e) {
         throw new RuntimeException(e);
      }

   }

   protected String generateXml(InternetServiceConfiguration internetServiceConfiguration)
            throws ParserConfigurationException, FactoryConfigurationError, TransformerException {
      XMLBuilder rootBuilder = XMLBuilder.create("InternetService").a("xmlns",
               "urn:tmrk:vCloudExpress-1.0").a("xmlns:i",
               "http://www.w3.org/2001/XMLSchema-instance");
      if (internetServiceConfiguration.getDescription() != null)
         rootBuilder.e("Description").t(internetServiceConfiguration.getDescription());
      if (internetServiceConfiguration.getName() != null)
         rootBuilder.e("Name").t(internetServiceConfiguration.getName());
      if (internetServiceConfiguration.getEnabled() != null)
         rootBuilder.e("Enabled").t(internetServiceConfiguration.getEnabled());
      if (internetServiceConfiguration.getTimeout() != null)
         rootBuilder.e("Timeout").t(internetServiceConfiguration.getTimeout());
      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      return rootBuilder.asString(outputProperties);
   }

   protected String ifNullDefaultTo(String value, String defaultValue) {
      return value != null ? value : checkNotNull(defaultValue, "defaultValue");
   }
}
