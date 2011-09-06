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
package org.jclouds.vcloud.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class CatalogItemOptions {

   private String description;
   private Map<String, String> properties = Maps.newLinkedHashMap();

   /**
    * optional description for the CatalogItem
    */
   public CatalogItemOptions description(String description) {
      this.description = checkNotNull(description, "description");
      return this;
   }

   /**
    * optional properties for the CatalogItem
    */
   public CatalogItemOptions properties(Map<String, String> properties) {
      this.properties = ImmutableMap.copyOf(checkNotNull(properties, "properties"));
      return this;
   }

   public String getDescription() {
      return description;
   }

   public Map<String, String> getProperties() {
      return properties;
   }

   public static class Builder {

      /**
       * @see CatalogItemOptions#description
       */
      public static CatalogItemOptions description(String description) {
         return new CatalogItemOptions().description(description);
      }

      /**
       * @see CatalogItemOptions#properties
       */
      public static CatalogItemOptions properties(Map<String, String> properties) {
         return new CatalogItemOptions().properties(properties);
      }
   }

}
