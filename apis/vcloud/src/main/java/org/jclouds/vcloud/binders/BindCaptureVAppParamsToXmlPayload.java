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
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.util.Map;
import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.options.CaptureVAppOptions;

import com.google.inject.Inject;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindCaptureVAppParamsToXmlPayload implements MapBinder {

   protected final String ns;
   protected final String schema;
   private final BindToStringPayload stringBinder;

   @Inject
   public BindCaptureVAppParamsToXmlPayload(BindToStringPayload stringBinder,
         @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns, @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema) {
      this.ns = ns;
      this.schema = schema;
      this.stringBinder = stringBinder;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
            "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      checkState(gRequest.getArgs() != null, "args should be initialized at this point");
      String templateName = checkNotNull(postParams.remove("templateName"), "templateName").toString();
      String vApp = checkNotNull(postParams.remove("vApp"), "vApp").toString();

      CaptureVAppOptions options = findOptionsInArgsOrNull(gRequest);
      if (options == null) {
         options = new CaptureVAppOptions();
      }
      try {
         return stringBinder.bindToRequest(request, generateXml(templateName, vApp, options));
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      } catch (FactoryConfigurationError e) {
         throw new RuntimeException(e);
      } catch (TransformerException e) {
         throw new RuntimeException(e);
      }

   }

   protected String generateXml(String templateName, String vApp, CaptureVAppOptions options)
         throws ParserConfigurationException, FactoryConfigurationError, TransformerException {
      XMLBuilder rootBuilder = buildRoot(templateName);
      if (options.getDescription() != null)
         rootBuilder.e("Description").text(options.getDescription());
      rootBuilder.e("Source").a("href", vApp).a("type", VCloudMediaType.VAPP_XML);
      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      return rootBuilder.asString(outputProperties);
   }

   protected XMLBuilder buildRoot(String name) throws ParserConfigurationException, FactoryConfigurationError {
      XMLBuilder rootBuilder = XMLBuilder.create("CaptureVAppParams").a("name", name).a("xmlns", ns)
            .a("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance").a("xsi:schemaLocation", ns + " " + schema);
      return rootBuilder;
   }

   protected CaptureVAppOptions findOptionsInArgsOrNull(GeneratedHttpRequest gRequest) {
      for (Object arg : gRequest.getArgs()) {
         if (arg instanceof CaptureVAppOptions) {
            return (CaptureVAppOptions) arg;
         } else if (arg instanceof CaptureVAppOptions[]) {
            CaptureVAppOptions[] options = (CaptureVAppOptions[]) arg;
            return (options.length > 0) ? options[0] : null;
         }
      }
      return null;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException("CaptureVAppParams is needs parameters");
   }

   protected String ifNullDefaultTo(String value, String defaultValue) {
      return value != null ? value : checkNotNull(defaultValue, "defaultValue");
   }
}
