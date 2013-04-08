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
 * Records are resolved in consideration of the location of the requestor.
 * 
 * @author Adrian Cole
 */
public final class DirectionalPool {

   private final String zoneId;
   private final String id;
   private final Optional<String> description;
   private final String dname;
   private final Type type;
   private final TieBreak tieBreak;

   private DirectionalPool(String zoneId, String id, Optional<String> description, String dname, Type type,
         TieBreak tieBreak) {
      this.zoneId = checkNotNull(zoneId, "zoneId");
      this.id = checkNotNull(id, "id");
      this.description = checkNotNull(description, "description for %s", id);
      this.dname = checkNotNull(dname, "dname for %s", id);
      this.type = type;
      this.tieBreak = tieBreak;
   }

   public String getZoneId() {
      return zoneId;
   }

   public String getId() {
      return id;
   }

   /**
    * The dname of the pool. ex. {@code jclouds.org.}
    */
   public String getName() {
      return dname;
   }

   /**
    * The description of the pool. ex. {@code My Pool}
    */
   public Optional<String> getDescription() {
      return description;
   }

   public Type getType() {
      return type;
   }

   /**
    * if {@link #getType} is {@link Type#MIXED}, this can be
    * {@link TieBreak#SOURCEIP} or {@link TieBreak#GEOLOCATION}, otherwise
    * {@link TieBreak#GEOLOCATION}
    */
   public TieBreak getTieBreak() {
      return tieBreak;
   }

   public static enum Type {
      GEOLOCATION, SOURCEIP, MIXED;
   }

   public static enum TieBreak {
      GEOLOCATION, SOURCEIP;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(zoneId, id, description, dname);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DirectionalPool that = DirectionalPool.class.cast(obj);
      return Objects.equal(this.zoneId, that.zoneId) && Objects.equal(this.id, that.id)
            && Objects.equal(this.description, that.description) && Objects.equal(this.dname, that.dname);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("zoneId", zoneId).add("id", id).add("name", dname)
            .add("description", description.orNull()).add("type", type).add("tieBreak", tieBreak).toString();
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
      private Optional<String> description = Optional.absent();
      private String dname;
      private Type type = Type.GEOLOCATION;
      private TieBreak tieBreak = TieBreak.GEOLOCATION;

      /**
       * @see DirectionalPool#getZoneId()
       */
      public Builder zoneId(String zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      /**
       * @see DirectionalPool#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see DirectionalPool#getName()
       */
      public Builder name(String dname) {
         this.dname = dname;
         return this;
      }

      /**
       * @see DirectionalPool#getDescription()
       */
      public Builder description(String description) {
         this.description = Optional.fromNullable(description);
         return this;
      }

      /**
       * @see DirectionalPool#getType()
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      /**
       * @see DirectionalPool#getTieBreak()
       */
      public Builder tieBreak(TieBreak tieBreak) {
         this.tieBreak = tieBreak;
         return this;
      }

      public DirectionalPool build() {
         return new DirectionalPool(zoneId, id, description, dname, type, tieBreak);
      }

      public Builder from(DirectionalPool in) {
         return this.zoneId(in.zoneId).id(in.id).description(in.description.orNull()).name(in.dname).type(in.type)
               .tieBreak(in.tieBreak);
      }
   }
}
