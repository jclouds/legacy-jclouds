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
import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 */
public class DirectionalGroup {
   private final String id;
   private final String name;
   private final Optional<String> description;

   private DirectionalGroup(String id, String name, Optional<String> description) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.description = checkNotNull(description, "description");
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public Optional<String> getDescription() {
      return description;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, description);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      DirectionalGroup that = DirectionalGroup.class.cast(obj);
      return equal(this.id, that.id) && equal(this.name, that.name) && equal(this.description, that.description);
   }

   @Override
   public String toString() {
      return toStringHelper("").omitNullValues().add("id", id).add("name", name)
            .add("description", description.orNull()).toString();
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
      private Optional<String> description = Optional.absent();

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

      /**
       * @see DirectionalGroup#getDescription()
       */
      public Builder description(String description) {
         this.description = Optional.fromNullable(description);
         return this;
      }

      public DirectionalGroup build() {
         return new DirectionalGroup(id, name, description);
      }

      public Builder from(DirectionalGroup in) {
         return id(in.id).name(in.name).description(in.description.orNull());
      }
   }
}
