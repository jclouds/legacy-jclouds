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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.Utils.replaceTokens;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.util.Patterns;
import org.jclouds.util.Utils;

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

   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      String ipAddress = checkNotNull(postParams.get("ipAddress"),
               "ipAddress parameter not present");
      String name = checkNotNull(postParams.get("name"), "name parameter not present");
      String port = checkNotNull(postParams.get("port"), "port parameter not present");
      String enabled = checkNotNull(postParams.get("enabled"), "enabled parameter not present");
      String description = postParams.get("description");

      String payload = replaceTokens(xmlTemplate, ImmutableMap.of("name", name, "ipAddress",
               ipAddress, "port", port, "enabled", enabled));
      payload = Utils.replaceAll(payload, Patterns.TOKEN_TO_PATTERN.get("description"),
               description == null ? "" : String.format("\n    <Description>%s</Description>",
                        description));
      stringBinder.bindToRequest(request, payload);
   }

   public void bindToRequest(HttpRequest request, Object input) {
      throw new IllegalStateException("CreateNodeService needs parameters");

   }

}
