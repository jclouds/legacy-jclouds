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
package org.jclouds.samples.googleappengine.domain;

import static com.google.common.base.Objects.equal;

import com.google.common.base.Objects;

/**
 * 
 * @author Adrian Cole
 */
public class ResourceResult {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String provider;
      protected String location;
      protected String type;
      protected String id;
      protected String name;

      public Builder provider(String provider) {
         this.provider = provider;
         return this;
      }

      public Builder location(String location) {
         this.location = location;
         return this;
      }

      public Builder type(String type) {
         this.type = type;
         return this;
      }

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public ResourceResult build() {
         return new ResourceResult(provider, location, type, id, name);
      }

   }

   private final String provider;
   private final String location;
   private final String type;
   private final String id;
   private final String name;

   protected ResourceResult(String provider, String location, String type, String id, String name) {
      this.provider = provider;
      this.type = type;
      this.location = location;
      this.id = id;
      this.name = name;
   }

   public String getProvider() {
      return provider;
   }

   public String getLocation() {
      return location;
   }

   public String getType() {
      return type;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ResourceResult that = ResourceResult.class.cast(o);
      return equal(this.provider, that.provider) && equal(this.location, that.location) && equal(this.type, that.type)
            && equal(this.id, that.id) && equal(this.name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(provider, location, type, id, name);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("provider", provider).add("location", location).add("type", type)
            .add("id", id).add("name", name).toString();
   }
}
