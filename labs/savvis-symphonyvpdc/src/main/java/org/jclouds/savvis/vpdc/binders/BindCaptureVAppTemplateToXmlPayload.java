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
package org.jclouds.savvis.vpdc.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Properties;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.savvis.vpdc.domain.FirewallRule;

import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Kedar Dave
 * 
 */
@Singleton
public class BindCaptureVAppTemplateToXmlPayload extends BindToStringPayload implements MapBinder {
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("BindFirewallRuleToXmlPayload needs parameters");

   }

   protected URI findVAppURIInArgsOrNull(GeneratedHttpRequest gRequest) {
      for (Object arg : gRequest.getInvocation().getArgs()) {
         if (arg instanceof URI) {
            return (URI) arg;
         } else if (arg instanceof FirewallRule[]) {
        	 URI[] rules = (URI[]) arg;
            return (rules.length > 0) ? rules[0] : null;
         }
      }
      return null;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
            "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      request = super.bindToRequest(request,
            generateXml(findVAppURIInArgsOrNull(gRequest)));
      request.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_XML);
      return request;
   }

   public String generateXml(URI vAppURI) {
      checkNotNull(vAppURI, "vAppURI");

      try {
         XMLBuilder rootBuilder = buildRoot();
         addSourceSection(rootBuilder, vAppURI);
         Properties outputProperties = new Properties();
         return rootBuilder.asString(outputProperties);
      } catch (Exception e) {
         return null;
      }
   }

   void addSourceSection(XMLBuilder rootBuilder, URI vAppURI) {
      rootBuilder.e("Description").t("Save Template");
      rootBuilder.e("Source").a("href", vAppURI.toString());
   }

   protected XMLBuilder buildRoot() throws ParserConfigurationException, FactoryConfigurationError {
      XMLBuilder rootBuilder = XMLBuilder.create("CaptureVAppParams")
            .a("xmlns", "http://schemas.api.sandbox.savvis.net/vpdci")
            .a("name", "CaptureTemplate");
      return rootBuilder;
   }

   protected String ifNullDefaultTo(String value, String defaultValue) {
      return value != null ? value : checkNotNull(defaultValue, "defaultValue");
   }

}
