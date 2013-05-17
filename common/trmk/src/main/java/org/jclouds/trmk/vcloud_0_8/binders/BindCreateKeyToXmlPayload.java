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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.util.Strings2;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindCreateKeyToXmlPayload implements MapBinder {

   private final String xmlTemplate;
   private final BindToStringPayload stringBinder;
   private final String ns;

   @Inject
   BindCreateKeyToXmlPayload(@Named(PROPERTY_TERREMARK_EXTENSION_NS) String ns, @Named("CreateKey") String xmlTemplate,
         BindToStringPayload stringBinder) {
      this.ns = ns;
      this.xmlTemplate = xmlTemplate;
      this.stringBinder = stringBinder;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      String name = checkNotNull(postParams.get("name"), "name parameter not present").toString();
      String isDefault = checkNotNull(postParams.get("isDefault"), "isDefault parameter not present").toString();

      String payload = Strings2.replaceTokens(xmlTemplate,
            ImmutableMap.of("name", name, "isDefault", isDefault, "ns", ns));
      return stringBinder.bindToRequest(request, payload);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException("CreateKey needs parameters");
   }

}
