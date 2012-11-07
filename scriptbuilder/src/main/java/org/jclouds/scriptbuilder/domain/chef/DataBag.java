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
package org.jclouds.scriptbuilder.domain.chef;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * A Data bag to be configured for a Chef Solo run.
 * 
 * @author Ignasi Barrera
 * @since Chef 0.10.4
 */
public class DataBag {

   public static class Item {
      public static Builder builder() {
         return new Builder();
      }

      public static class Builder {
         private String name;
         private String jsonData;

         public Builder name(String name) {
            this.name = checkNotNull(name, "name must be set");
            return this;
         }

         public Builder jsonData(String jsonData) {
            this.jsonData = checkNotNull(jsonData, "jsonData must be set");
            return this;
         }

         public Item build() {
            return new Item(name, jsonData);
         }
      }

      private String name;
      private String jsonData;

      public Item(String name, String jsonData) {
         this.name = checkNotNull(name, "name must be set");
         this.jsonData = checkNotNull(jsonData, "jsonData must be set");
      }

      public String getName() {
         return name;
      }

      public String getJsonData() {
         return jsonData;
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
         Item other = (Item) obj;
         return Objects.equal(name, other.name);
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this).add("name", name).toString();
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String name;
      private List<Item> items = Lists.newArrayList();

      public Builder name(String name) {
         this.name = checkNotNull(name, "name must be set");
         return this;
      }

      public Builder item(String name, String jsonData) {
         Item item = Item.builder().name(checkNotNull(name, "name must be set"))
               .jsonData(checkNotNull(jsonData, "jsonData must be set")).build();
         this.items.add(item);
         return this;
      }

      public Builder items(Iterable<Item> items) {
         this.items = ImmutableList.copyOf(checkNotNull(items, "items must be set"));
         return this;
      }

      public DataBag build() {
         return new DataBag(name, items);
      }

   }

   private String name;
   private List<Item> items;

   public DataBag(String name, List<Item> items) {
      this.name = checkNotNull(name, "name must be set");
      this.items = ImmutableList.copyOf(checkNotNull(items, "items must be set"));
   }

   public String getName() {
      return name;
   }

   public List<Item> getItems() {
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
      Item other = (Item) obj;
      return Objects.equal(name, other.name);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("name", name).toString();
   }
}
