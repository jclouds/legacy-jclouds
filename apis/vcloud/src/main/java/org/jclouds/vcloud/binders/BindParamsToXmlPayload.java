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

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;

import com.google.inject.Inject;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindParamsToXmlPayload implements MapBinder {

   protected final String ns;
   protected final BindToStringPayload stringBinder;
   protected final String element;

   @Inject
   public BindParamsToXmlPayload(String element, BindToStringPayload stringBinder,
            @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns) {
      this.element = element;
      this.ns = ns;
      this.stringBinder = stringBinder;
   }
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      try {
         return stringBinder.bindToRequest(request, generateXml(postParams));
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private String generateXml(Map<String, Object> postParams) throws ParserConfigurationException,
            FactoryConfigurationError, TransformerException {
      XMLBuilder rootBuilder = XMLBuilder.create(element);
      for (Entry<String, Object> entry : postParams.entrySet())
         rootBuilder.a(entry.getKey(), (String) entry.getValue());
      rootBuilder.a("xmlns", ns);
      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      return rootBuilder.asString(outputProperties);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalArgumentException("incorrect usage");
   }
}
