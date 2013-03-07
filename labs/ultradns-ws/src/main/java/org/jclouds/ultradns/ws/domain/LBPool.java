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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * 
 * @author Adrian Cole
 */
public final class LBPool {

   private final String zoneId;
   private final String id;
   private final String name;
   private final Type type;
   private final Optional<Type> responseMethod;

   private LBPool(String zoneId, String id, String name, Type type, Optional<Type> responseMethod) {
      this.zoneId = checkNotNull(zoneId, "zoneId");
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name for %s", id);
      this.type = checkNotNull(type, "type for %s", name);
      this.responseMethod = checkNotNull(responseMethod, "responseMethod for %s", name);
   }

   /**
    * The ID of the zone.
    */
   public String getZoneId() {
      return zoneId;
   }

   /**
    * The ID of the pool.
    */
   public String getId() {
      return id;
   }

   /**
    * The name of the pool. ex. {@code jclouds.org.}
    */
   public String getName() {
      return name;
   }

   /**
    * The type of the pool
    */
   public Type getType() {
      return type;
   }

   /**
    * The response method
    */
   public Optional<Type> getResponseMethod() {
      return responseMethod;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(zoneId, id, name, type, responseMethod);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      LBPool that = LBPool.class.cast(obj);
      return Objects.equal(this.zoneId, that.zoneId) && Objects.equal(this.id, that.id)
            && Objects.equal(this.name, that.name) && Objects.equal(this.type, that.type)
            && Objects.equal(this.responseMethod, that.responseMethod);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("zoneId", zoneId).add("id", id).add("name", name)
            .add("type", type).add("responseMethod", responseMethod.orNull()).toString();
   }

   public static enum Type {

      RD, RR, SB, TC, UNRECOGNIZED;

      public static Type fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public final static class Builder {
      private String zoneId;
      private String id;
      private String name;
      private Type type;
      private Optional<Type> responseMethod = Optional.absent();

      /**
       * @see LBPool#getZoneId()
       */
      public Builder zoneId(String zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      /**
       * @see LBPool#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see LBPool#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see LBPool#getType()
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      /**
       * @see LBPool#getResponseMethod()
       */
      public Builder responseMethod(Type responseMethod) {
         this.responseMethod = Optional.fromNullable(responseMethod);
         return this;
      }

      public LBPool build() {
         return new LBPool(zoneId, id, name, type, responseMethod);
      }

      public Builder from(LBPool in) {
         return this.zoneId(in.zoneId).id(in.id).name(in.name).type(in.type).responseMethod(in.responseMethod.orNull());
      }
   }
}
