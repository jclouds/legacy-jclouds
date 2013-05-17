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

/**
 * 
 * @author Adrian Cole
 */
public final class RoundRobinPool {

   private final String zoneId;
   private final String id;
   private final String name;
   private final String dname;

   private RoundRobinPool(String zoneId, String id, String name, String dname) {
      this.zoneId = checkNotNull(zoneId, "zoneId");
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name for %s", id);
      this.dname = checkNotNull(dname, "dname for %s", id);
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
    * The name of the pool. ex. {@code My Pool}
    */
   public String getName() {
      return name;
   }

   /**
    * The dname of the pool. ex. {@code jclouds.org.}
    */
   public String getDName() {
      return dname;
   }

   /**
    * currently supported {@link ResourceRecord#getType() types} for round robin pools.
    * 
    */
   public static enum RecordType {
      A(1),
      AAAA(28);

      @Override
      public String toString() {
         return String.valueOf(code);
      }

      private final int code;

      private RecordType(int code) {
         this.code = code;
      }

      public int getCode() {
         return code;
      }
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
      RoundRobinPool that = RoundRobinPool.class.cast(obj);
      return Objects.equal(this.zoneId, that.zoneId) && Objects.equal(this.id, that.id)
            && Objects.equal(this.name, that.name) && Objects.equal(this.dname, that.dname);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("zoneId", zoneId).add("id", id).add("name", name)
            .add("dname", dname).toString();
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
      private String name;
      private String dname;

      /**
       * @see RoundRobinPool#getZoneId()
       */
      public Builder zoneId(String zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      /**
       * @see RoundRobinPool#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see RoundRobinPool#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see RoundRobinPool#getDName()
       */
      public Builder dname(String dname) {
         this.dname = dname;
         return this;
      }

      public RoundRobinPool build() {
         return new RoundRobinPool(zoneId, id, name, dname);
      }

      public Builder from(RoundRobinPool in) {
         return this.zoneId(in.zoneId).id(in.id).name(in.name).dname(in.dname);
      }
   }
}
