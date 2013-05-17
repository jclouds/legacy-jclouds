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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * 
 * @author Adrian Cole
 */
public final class TrafficControllerPoolRecordDetail {

   private final String id;
   private final String poolId;
   private final TrafficControllerPoolRecord record;
   private final int weight;
   private final int priority;
   private final String forceAnswer;
   private final boolean probingEnabled;
   private final Status status;
   private final boolean serving;
   private final String description;

   private TrafficControllerPoolRecordDetail(String id, String poolId, TrafficControllerPoolRecord record, int weight,
         int priority, String forceAnswer, boolean probingEnabled, Status status, boolean serving, String description) {
      this.id = checkNotNull(id, "id");
      this.poolId = checkNotNull(poolId, "poolId for %s", id);
      this.record = checkNotNull(record, "record for %s", poolId);
      checkArgument(weight >= 0, "weight of %s must be >= 0", id);
      this.weight = weight;
      checkArgument(priority >= 0, "priority of %s must be >= 0", id);
      this.priority = priority;
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
    * the record pointed to
    */
   public TrafficControllerPoolRecord getRecord() {
      return record;
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

      OK, WARNING, CRITICAL, FAILURE, DISABLED, UNRECOGNIZED;

      public static Status fromValue(String status) {
         try {
            return valueOf(checkNotNull(status, "status").toUpperCase());
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
      TrafficControllerPoolRecordDetail that = TrafficControllerPoolRecordDetail.class.cast(obj);
      return Objects.equal(this.id, that.id) && Objects.equal(this.poolId, that.poolId);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("id", id).add("poolId", poolId).add("record", record)
            .add("weight", weight).add("priority", priority).add("forceAnswer", forceAnswer)
            .add("probingEnabled", probingEnabled).add("status", status).add("serving", serving)
            .add("description", description).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String id;
      private String poolId;
      private TrafficControllerPoolRecord record;
      private int weight;
      private int priority;
      private String forceAnswer;
      private boolean probingEnabled;
      private Status status;
      private boolean serving;
      private String description;

      /**
       * @see TrafficControllerPoolRecordDetail#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecordDetail#getPoolId()
       */
      public Builder poolId(String poolId) {
         this.poolId = poolId;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecordDetail#getRecord()
       */
      public Builder record(TrafficControllerPoolRecord record) {
         this.record = record;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecordDetail#getWeight()
       */
      public Builder weight(int weight) {
         this.weight = weight;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecordDetail#getPriority()
       */
      public Builder priority(int priority) {
         this.priority = priority;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecordDetail#getForceAnswer()
       */
      public Builder forceAnswer(String forceAnswer) {
         this.forceAnswer = forceAnswer;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecordDetail#isProbingEnabled()
       */
      public Builder probingEnabled(boolean probingEnabled) {
         this.probingEnabled = probingEnabled;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecordDetail#getStatus()
       */
      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecordDetail#isServing()
       */
      public Builder serving(boolean serving) {
         this.serving = serving;
         return this;
      }

      /**
       * @see TrafficControllerPoolRecordDetail#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public TrafficControllerPoolRecordDetail build() {
         return new TrafficControllerPoolRecordDetail(id, poolId, record, weight, priority, forceAnswer,
               probingEnabled, status, serving, description);
      }

      public Builder from(TrafficControllerPoolRecordDetail in) {
         return this.id(in.id).poolId(in.poolId).weight(in.weight).record(in.record).priority(in.priority)
               .forceAnswer(in.forceAnswer).probingEnabled(in.probingEnabled).status(in.status).serving(in.serving)
               .description(in.description);
      }
   }
}
