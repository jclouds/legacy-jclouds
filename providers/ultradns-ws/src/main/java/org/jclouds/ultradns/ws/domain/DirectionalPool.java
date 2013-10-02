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
   private final Optional<String> name;
   private final String dname;
   private final Type type;
   private final TieBreak tieBreak;

   private DirectionalPool(String zoneId, String id, Optional<String> name, String dname, Type type,
         TieBreak tieBreak) {
      this.zoneId = checkNotNull(zoneId, "zoneId");
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name for %s", id);
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
   public String getDName() {
      return dname;
   }

   /**
    * The name of the pool. ex. {@code My Pool}
    */
   public Optional<String> getName() {
      return name;
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

   /**
    * currently supported {@link ResourceRecord#getType() types} for directional
    * groups.
    * 
    */
   public static enum RecordType {
      // A/CNAME
      IPV4(1),

      // AAAA/CNAME
      IPV6(28),

      TXT(16),

      SRV(33),

      PTR(12),

      RP(17),

      HINFO(13),

      NAPTR(35),

      MX(15);

      private final int code;

      private RecordType(int code) {
         this.code = code;
      }

      /**
       * The {@link ResourceRecord#getType() type} that can be used in
       * directional groups.
       */
      public int getCode() {
         return code;
      }
   }

   public static enum TieBreak {
      GEOLOCATION, SOURCEIP;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(zoneId, id, name, dname);
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
            && Objects.equal(this.name, that.name) && Objects.equal(this.dname, that.dname);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("zoneId", zoneId).add("id", id).add("dname", dname)
            .add("name", name.orNull()).add("type", type).add("tieBreak", tieBreak).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String zoneId;
      private String id;
      private Optional<String> name = Optional.absent();
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
       * @see DirectionalPool#getDName()
       */
      public Builder dname(String dname) {
         this.dname = dname;
         return this;
      }

      /**
       * @see DirectionalPool#getName()
       */
      public Builder name(String name) {
         this.name = Optional.fromNullable(name);
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
         return new DirectionalPool(zoneId, id, name, dname, type, tieBreak);
      }

      public Builder from(DirectionalPool in) {
         return this.zoneId(in.zoneId).id(in.id).name(in.name.orNull()).dname(in.dname).type(in.type)
               .tieBreak(in.tieBreak);
      }
   }
}
