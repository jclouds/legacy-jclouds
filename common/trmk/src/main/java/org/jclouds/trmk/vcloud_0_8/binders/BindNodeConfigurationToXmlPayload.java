/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.binders;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NS;

import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;

import com.google.common.base.Throwables;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindNodeConfigurationToXmlPayload implements MapBinder {

   private final String ns;
   private final BindToStringPayload stringBinder;

   @Inject
   BindNodeConfigurationToXmlPayload(@Named(PROPERTY_TERREMARK_EXTENSION_NS) String ns, BindToStringPayload stringBinder) {
      this.ns = ns;
      this.stringBinder = stringBinder;
   }

   protected String generateXml(Map<String, Object> postParams) throws ParserConfigurationException,
         FactoryConfigurationError, TransformerException {
      XMLBuilder rootBuilder = XMLBuilder.create("NodeService").a("xmlns", ns).a("xmlns:xsi",
            "http://www.w3.org/2001/XMLSchema-instance").a("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
      rootBuilder.e("Name").t(checkNotNull(postParams.get("name"), "name").toString());
      rootBuilder.e("Enabled").t(checkNotNull(postParams.get("enabled"), "enabled").toString());
      if (postParams.containsKey("description") && postParams.get("description") != null)
         rootBuilder.e("Description").t((String) postParams.get("description"));
      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      return rootBuilder.asString(outputProperties);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      try {
         return stringBinder.bindToRequest(request, generateXml(postParams));
      } catch (Exception e) {
         Throwables.propagate(e);
      }
      return request;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalArgumentException("this is a map binder");
   }
}
