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

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;

import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

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

   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      try {
         stringBinder.bindToRequest(request, generateXml(postParams));
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private String generateXml(Map<String, String> postParams) throws ParserConfigurationException,
            FactoryConfigurationError, TransformerException {
      XMLBuilder rootBuilder = XMLBuilder.create(element);
      for (Entry<String, String> entry : postParams.entrySet())
         rootBuilder.a(entry.getKey(), entry.getValue());
      rootBuilder.a("xmlns", ns);
      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      return rootBuilder.asString(outputProperties);
   }

   @Override
   public void bindToRequest(HttpRequest request, Object input) {
      throw new IllegalArgumentException("incorrect usage");
   }
}
