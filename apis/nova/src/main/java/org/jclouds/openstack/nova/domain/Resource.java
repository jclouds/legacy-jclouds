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
package org.jclouds.openstack.nova.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;

/**
 * @author Dmitri Babaev
 */
public class Resource {

   private List<Map<String, String>> links = Lists.newArrayList();

   public URI getURI() {
      for (Map<String, String> linkProperties : links) {
         try {
            if (!Functions.forMap(linkProperties, "").apply("rel").equals("bookmark"))
               continue;
            if (!Functions.forMap(linkProperties, "").apply("type").contains("json"))
               continue;
            
            return new URI(linkProperties.get("href"));
         } catch (URISyntaxException e) {
            throw new RuntimeException(e);
         }
      }

      throw new IllegalStateException("URI is not available");
   }
}
