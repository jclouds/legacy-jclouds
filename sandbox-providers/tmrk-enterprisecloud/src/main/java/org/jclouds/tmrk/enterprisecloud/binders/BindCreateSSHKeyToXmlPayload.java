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
package org.jclouds.tmrk.enterprisecloud.binders;

import com.jamesmurty.utils.XMLBuilder;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author Jason King
 */
@Singleton
public class BindCreateSSHKeyToXmlPayload implements MapBinder {

   private final BindToStringPayload stringBinder;

   @Inject
   BindCreateSSHKeyToXmlPayload(BindToStringPayload stringBinder) {
      this.stringBinder = stringBinder;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, String> params) {
      checkNotNull(request, "request");
      checkNotNull(params, "params");
      String name = checkNotNull(params.get("name"), "name");
      String isDefault = checkNotNull(params.get("isDefault"), "isDefault");

      String payload = createXMLPayload(name,isDefault);
      return stringBinder.bindToRequest(request, payload);
   }
   
   private String createXMLPayload(String name, String isDefault) {
      try {
         Properties outputProperties = new Properties();
         outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
         return XMLBuilder.create("CreateSshKey").a("name",name)
                                           .e("Default").t(isDefault)
                                           .asString(outputProperties);
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      } catch (TransformerException t) {
         throw new RuntimeException(t);
      }
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException("BindCreateKey needs parameters");
   }
}
