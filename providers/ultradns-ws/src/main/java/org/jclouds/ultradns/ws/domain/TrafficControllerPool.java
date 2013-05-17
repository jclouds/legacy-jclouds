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
 * Traffic Controller pools are used to create weighted {@code A} and {@code CNAME} records.
 * 
 * @author Adrian Cole
 */
public final class TrafficControllerPool {

   private final String zoneId;
   private final String id;
   private final String name;
   private final String dname;
   private final int statusCode;
   private final boolean failOverEnabled;
   private final boolean probingEnabled;

   private TrafficControllerPool(String zoneId, String id, String name, String dname, int statusCode,
         boolean failOverEnabled, boolean probingEnabled) {
      this.zoneId = checkNotNull(zoneId, "zoneId");
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name for %s", id);
      this.dname = checkNotNull(dname, "dname for %s", id);
      this.statusCode = statusCode;
      this.failOverEnabled = failOverEnabled;
      this.probingEnabled = probingEnabled;
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
    * status of the pool
    */
   public int getStatusCode() {
      return statusCode;
   }

   /**
    * {@code true} if the pool is active and serving records.
    */
   public boolean isFailOverEnabled() {
      return failOverEnabled;
   }

   /**
    * {@code true} indicates the pool is functioning normally. {@code false}
    * indicates testing only—probing records and reporting results, but not
    * acting on the results
    */
   public boolean isProbingEnabled() {
      return probingEnabled;
   }

   /**
    * currently supported {@link ResourceRecord#getType() types} for traffic
    * controller pools.
    * 
    */
   public static enum RecordType {
      // A/CNAME
      IPV4(1),

      // AAAA/CNAME
      IPV6(28);

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
      TrafficControllerPool that = TrafficControllerPool.class.cast(obj);
      return Objects.equal(this.zoneId, that.zoneId) && Objects.equal(this.id, that.id)
            && Objects.equal(this.name, that.name) && Objects.equal(this.dname, that.dname);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("zoneId", zoneId).add("id", id).add("name", name)
            .add("dname", dname).add("status", statusCode).add("failOverEnabled", failOverEnabled)
            .add("probingEnabled", probingEnabled).toString();
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
      private int statusCode;
      private boolean failOverEnabled;
      private boolean probingEnabled;

      /**
       * @see TrafficControllerPool#getZoneId()
       */
      public Builder zoneId(String zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      /**
       * @see TrafficControllerPool#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see TrafficControllerPool#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see TrafficControllerPool#getDName()
       */
      public Builder dname(String dname) {
         this.dname = dname;
         return this;
      }

      /**
       * @see TrafficControllerPool#getStatusCode()
       */
      public Builder statusCode(int statusCode) {
         this.statusCode = statusCode;
         return this;
      }
      
      /**
       * @see TrafficControllerPool#isFailOverEnabled()
       */
      public Builder failOverEnabled(boolean failOverEnabled) {
         this.failOverEnabled = failOverEnabled;
         return this;
      }

      /**
       * @see TrafficControllerPool#isProbingEnabled()
       */
      public Builder probingEnabled(boolean probingEnabled) {
         this.probingEnabled = probingEnabled;
         return this;
      }

      public TrafficControllerPool build() {
         return new TrafficControllerPool(zoneId, id, name, dname, statusCode, failOverEnabled, probingEnabled);
      }

      public Builder from(TrafficControllerPool in) {
         return this.zoneId(in.zoneId).id(in.id).name(in.name).dname(in.dname).statusCode(in.statusCode)
               .failOverEnabled(in.failOverEnabled).probingEnabled(in.probingEnabled);
      }
   }
}
