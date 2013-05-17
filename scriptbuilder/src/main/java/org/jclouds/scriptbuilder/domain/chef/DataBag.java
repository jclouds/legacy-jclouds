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
package org.jclouds.scriptbuilder.domain.chef;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;

/**
 * A Data bag to be configured for a Chef Solo run.
 * 
 * @author Ignasi Barrera
 * @since Chef 0.10.4
 */
public class DataBag extends ForwardingMap<String, String> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String name;
      private ImmutableMap.Builder<String, String> items = ImmutableMap.builder();

      public Builder name(String name) {
         this.name = checkNotNull(name, "name must be set");
         return this;
      }

      public Builder item(String name, String jsonData) {
         this.items.put(checkNotNull(name, "name must be set"), checkNotNull(jsonData, "jsonData must be set"));
         return this;
      }

      public Builder items(Map<String, String> items) {
         this.items.putAll(checkNotNull(items, "items must be set"));
         return this;
      }

      public DataBag build() {
         return new DataBag(name, items.build());
      }

   }

   private String name;

   private Map<String, String> items;

   public DataBag(String name, Map<String, String> items) {
      this.name = checkNotNull(name, "name must be set");
      this.items = ImmutableMap.copyOf(checkNotNull(items, "items must be set"));
   }

   @Override
   protected Map<String, String> delegate() {
      return items;
   }

   public String getName() {
      return name;
   }

   public Map<String, String> getItems() {
      return items;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      DataBag other = (DataBag) obj;
      return Objects.equal(name, other.name);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("name", name).toString();
   }

}
