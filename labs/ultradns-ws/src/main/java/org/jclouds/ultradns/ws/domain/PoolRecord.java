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

/**
 * 
 * @author Adrian Cole
 */
public final class PoolRecord {

   private final String poolId;
   private final String id;
   private final String description;
   private final String type;
   private final String pointsTo;

   private PoolRecord(String poolId, String id, String description, String type, String pointsTo) {
      this.poolId = checkNotNull(poolId, "poolId");
      this.id = checkNotNull(id, "id");
      this.description = checkNotNull(description, "description for %s", id);
      this.type = checkNotNull(type, "type for %s", description);
      this.pointsTo = checkNotNull(pointsTo, "pointsTo for %s", description);
   }

   /**
    * The ID of the pool.
    */
   public String getPoolId() {
      return poolId;
   }

   /**
    * The ID of the record.
    */
   public String getId() {
      return id;
   }

   /**
    * The description of the record. ex. {@code SiteBacker pool via API}
    */
   public String getDescription() {
      return description;
   }

   /**
    * The type of the record ex. ex. {@code A}
    */
   public String getType() {
      return type;
   }

   /**
    * What the record points to ex. {@code 172.16.8.1}
    */
   public String getPointsTo() {
      return pointsTo;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(poolId, id, description, type, pointsTo);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      PoolRecord that = PoolRecord.class.cast(obj);
      return Objects.equal(this.poolId, that.poolId) && Objects.equal(this.id, that.id)
            && Objects.equal(this.description, that.description) && Objects.equal(this.type, that.type)
            && Objects.equal(this.pointsTo, that.pointsTo);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("poolId", poolId).add("id", id).add("description", description)
            .add("type", type).add("pointsTo", pointsTo).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public final static class Builder {
      private String poolId;
      private String id;
      private String description;
      private String type;
      private String pointsTo;

      /**
       * @see PoolRecord#getPoolId()
       */
      public Builder poolId(String poolId) {
         this.poolId = poolId;
         return this;
      }

      /**
       * @see PoolRecord#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see PoolRecord#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see PoolRecord#getType()
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see PoolRecord#getPointsTo()
       */
      public Builder pointsTo(String pointsTo) {
         this.pointsTo = pointsTo;
         return this;
      }

      public PoolRecord build() {
         return new PoolRecord(poolId, id, description, type, pointsTo);
      }

      public Builder from(PoolRecord in) {
         return this.poolId(in.poolId).id(in.id).description(in.description).type(in.type).pointsTo(in.pointsTo);
      }
   }
}
