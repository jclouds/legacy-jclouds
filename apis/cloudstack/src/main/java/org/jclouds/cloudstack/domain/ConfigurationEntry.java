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

package org.jclouds.cloudstack.domain;

/**
 * Representation of the API configuration entry response
 *
 * @author Andrei Savu
 */
public class ConfigurationEntry implements Comparable<ConfigurationEntry> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String category;
      private String description;
      private String name;
      private String value;

      public Builder category(String category) {
         this.category = category;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder value(String value) {
         this.value = value;
         return this;
      }

      public ConfigurationEntry build() {
         return new ConfigurationEntry(category, description, name, value);
      }
   }

   // for deserialization
   ConfigurationEntry() {
   }

   private String category;
   private String description;
   private String name;
   private String value;

   public ConfigurationEntry(String category, String description, String name, String value) {
      this.category = category;
      this.description = description;
      this.name = name;
      this.value = value;
   }

   @Override
   public int compareTo(ConfigurationEntry arg0) {
      return name.compareTo(arg0.getName());
   }

   public String getCategory() {
      return category;
   }

   public String getDescription() {
      return description;
   }

   public String getName() {
      return name;
   }

   public String getValue() {
      return value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ConfigurationEntry that = (ConfigurationEntry) o;

      if (category != null ? !category.equals(that.category) : that.category != null)
         return false;
      if (description != null ? !description.equals(that.description) : that.description != null)
         return false;
      if (name != null ? !name.equals(that.name) : that.name != null)
         return false;
      if (value != null ? !value.equals(that.value) : that.value != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = category != null ? category.hashCode() : 0;
      result = 31 * result + (description != null ? description.hashCode() : 0);
      result = 31 * result + (name != null ? name.hashCode() : 0);
      result = 31 * result + (value != null ? value.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "ConfigurationEntry{" +
         "category='" + category + '\'' +
         ", description='" + description + '\'' +
         ", name='" + name + '\'' +
         ", value='" + value + '\'' +
         '}';
   }
}
