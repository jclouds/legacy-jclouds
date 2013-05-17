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
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.util.Patterns;
import org.jclouds.util.Strings2;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindAddNodeServiceToXmlPayload implements MapBinder {

   @Inject
   @Named("CreateNodeService")
   private String xmlTemplate;
   @Inject
   private BindToStringPayload stringBinder;
   @Inject
   @Named(PROPERTY_TERREMARK_EXTENSION_NS)
   private String ns;

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      String ipAddress = checkNotNull(postParams.get("ipAddress"), "ipAddress parameter not present").toString();
      String name = checkNotNull(postParams.get("name"), "name parameter not present").toString();
      String port = checkNotNull(postParams.get("port"), "port parameter not present").toString();
      String enabled = checkNotNull(postParams.get("enabled"), "enabled parameter not present").toString();
      String description = (String) postParams.get("description");

      String payload = Strings2.replaceTokens(xmlTemplate,
            ImmutableMap.of("name", name, "ipAddress", ipAddress, "port", port, "enabled", enabled, "ns", ns));
      try {
         payload = Strings2.replaceAll(payload, Patterns.TOKEN_TO_PATTERN.get("description"), description == null ? ""
               : String.format("\n    <Description>%s</Description>", description));
      } catch (ExecutionException e) {
         Throwables.propagate(e);
      }
      return stringBinder.bindToRequest(request, payload);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException("CreateNodeService needs parameters");
   }

}
