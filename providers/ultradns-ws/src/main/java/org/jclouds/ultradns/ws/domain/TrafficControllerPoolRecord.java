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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * 
 * @author Adrian Cole
 */
public final class TrafficControllerPoolRecord {

   private final String id;
   private final String poolId;
   private final String pointsTo;
   private final int weight;
   private final int priority;
   private final String type;
   private final String forceAnswer;
   private final boolean probingEnabled;
   private final Status status;
   private final boolean serving;
   private final String description;

   private TrafficControllerPoolRecord(String id, String poolId, String pointsTo, int weight, int priority,
         String type, String forceAnswer, boolean probingEnabled, Status status, boolean serving, String description) {
      this.id = checkNotNull(id, "id");
      this.poolId = checkNotNull(poolId, "poolId for %s", id);
      this.pointsTo = checkNotNull(pointsTo, "pointsTo for %s", poolId);
      checkArgument(weight >= 0, "weight of %s must be >= 0", id);
      this.weight = weight;
      checkArgument(priority >= 0, "priority of %s must be >= 0", id);
      this.priority = priority;
      this.type = checkNotNull(type, "type for %s", poolId);
      this.forceAnswer = checkNotNull(forceAnswer, "forceAnswer for %s", poolId);
      this.probingEnabled = probingEnabled;
      this.status = checkNotNull(status, "status for %s", poolId);
      this.serving = serving;
      this.description = checkNotNull(description, "description for %s", description);
   }

   /**
    * The ID of the zone.
    */
   public String getId() {
      return id;
   }

   /**
    * The pool this record belongs to.
    */
   public String getPoolId() {
      return poolId;
   }

   /**
    * address or cname this points to. ex. {@code jclouds.org.} or
    * {@code 1.2.3.4}
    */
   public String getPointsTo() {
      return pointsTo;
   }

   /**
    * 0 or even numbers from 2–100. Determines the traffic load to send to each
    * server in a Traffic Controller pool. The value 0 indicates that Traffic
    * Controller always serves the record.
    */
   public int getWeight() {
      return weight;
   }

   /**
    * the default value is 1. The value 0 is the special All Fail priority.
    */
   public int getPriority() {
      return priority;
   }

   /**
    * the type of the record, either {@code A} or {@code CNAME}
    */
   public String getType() {
      return type;
   }

   public String getForceAnswer() {
      return forceAnswer;
   }

   public boolean isProbingEnabled() {
      return probingEnabled;
   }

   /**
    * status of the record
    */
   public Status getStatus() {
      return status;
   }

   public boolean isServing() {
      return serving;
   }

   /**
    * description of the record
    */
   public String getDescription() {
      return description;
   }

   public static enum Status {

      OK, DISABLED, UNRECOGNIZED;

      public static Status fromValue(String status) {
         try {
            return valueOf(checkNotNull(status, "status"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, poolId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TrafficControllerPoolRecord that = TrafficControllerPoolRecord.class.cast(obj);
      return Objects.equal(this.id, that.id) && Objects.equal(this.poolId, that.poolId);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("id", id).add("poolId", poolId).add("pointsTo", pointsTo)
            .add("weight", weight).add("priority", priority).add("type", type).add("forceAnswer", forceAnswer)
            .add("probingEnabled", probingEnabled).add("status", status).add("serving", serving)
            .add("description", description).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public final static class Builder {
      private String id;
      private String poolId;
      private String pointsTo;
      private int weight;
      private int priority;
      private String type;
      private String forceAnswer;
      private boolean probingEnabled;
      private Status status;
      private boolean serving;
      private String description;

      /**
       * @see TrafficControllerPoolRecord#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecord#getPoolId()
       */
      public Builder poolId(String poolId) {
         this.poolId = poolId;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecord#getPointsTo()
       */
      public Builder pointsTo(String pointsTo) {
         this.pointsTo = pointsTo;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecord#getWeight()
       */
      public Builder weight(int weight) {
         this.weight = weight;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecord#getPriority()
       */
      public Builder priority(int priority) {
         this.priority = priority;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecord#getType()
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecord#getForceAnswer()
       */
      public Builder forceAnswer(String forceAnswer) {
         this.forceAnswer = forceAnswer;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecord#isProbingEnabled()
       */
      public Builder probingEnabled(boolean probingEnabled) {
         this.probingEnabled = probingEnabled;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecord#getStatus()
       */
      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecord#isServing()
       */
      public Builder serving(boolean serving) {
         this.serving = serving;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecord#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public TrafficControllerPoolRecord build() {
         return new TrafficControllerPoolRecord(id, poolId, pointsTo, weight, priority, type, forceAnswer,
               probingEnabled, status, serving, description);
      }

      public Builder from(TrafficControllerPoolRecord in) {
         return this.id(in.id).poolId(in.poolId).weight(in.weight).pointsTo(in.pointsTo).priority(in.priority)
               .type(in.type).forceAnswer(in.forceAnswer).probingEnabled(in.probingEnabled).status(in.status)
               .serving(in.serving).description(in.description);
      }
   }
}
