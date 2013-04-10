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
package org.jclouds.ultradns.ws.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public class DirectionalGroup {
   private final String id;
   private final String name;

   private DirectionalGroup(String id, String name) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      DirectionalGroup that = DirectionalGroup.class.cast(obj);
      return equal(this.id, that.id) && equal(this.name, that.name);
   }

   @Override
   public String toString() {
      return toStringHelper(this).omitNullValues().add("id", id).add("name", name).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public final static class Builder {
      private String id;
      private String name;

      /**
       * @see DirectionalGroup#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see DirectionalGroup#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public DirectionalGroup build() {
         return new DirectionalGroup(id, name);
      }

      public Builder from(DirectionalGroup in) {
         return id(in.id).name(in.name);
      }
   }
}
