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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * specifications and status of a current record in the pool.
 * 
 * @author Adrian Cole
 */
public final class PoolRecordSpec {
   private final String description;
   private final String state;
   private final boolean probingEnabled;
   private final boolean allFailEnabled;
   private final int weight;
   private final int failOverDelay;
   private final int threshold;
   private final int ttl;

   private PoolRecordSpec(String description, String state, boolean probingEnabled, boolean allFailEnabled, int weight,
         int failOverDelay, int threshold, int ttl) {
      this.description = checkNotNull(description, "description");
      this.state = checkNotNull(state, "state for %s", description);
      this.probingEnabled = probingEnabled;
      this.allFailEnabled = allFailEnabled;
      this.weight = weight;
      checkArgument(weight >= 0, "weight of %s must be >= 0", description);
      this.failOverDelay = failOverDelay;
      checkArgument(failOverDelay >= 0, "failOverDelay of %s must be >= 0", description);
      this.threshold = threshold;
      checkArgument(threshold >= 0, "threshold of %s must be >= 0", description);
      this.ttl = ttl;
      checkArgument(ttl >= 0, "ttl of %s must be >= 0", description);
   }

   /**
    * correlates to {@link TrafficControllerPoolRecordDetail#getDescription()}
    */
   public String getDescription() {
      return description;
   }

   /**
    * known values include {@code Normal} and {@code Normal-NoTest}
    */
   public String getState() {
      return state;
   }

   /**
    * correlates to {@link TrafficControllerPoolRecordDetail#isProbingEnabled()}
    */
   public boolean isProbingEnabled() {
      return probingEnabled;
   }

   /**
    * undocumented
    */
   public boolean isAllFailEnabled() {
      return allFailEnabled;
   }

   /**
    * correlates to {@link TrafficControllerPoolRecordDetail#getWeight()}
    */
   public int getWeight() {
      return weight;
   }

   /**
    * at the time of writing, between 0–30 (minutes).
    */
   public int getFailOverDelay() {
      return failOverDelay;
   }

   /**
    * how many probes in a region must fail in order for this to fail.
    */
   public int getThreshold() {
      return threshold;
   }

   /**
    * The resource record cache time to live (TTL), in seconds.
    */
   public int getTTL() {
      return ttl;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(description, state, probingEnabled, allFailEnabled, weight, failOverDelay, threshold,
            ttl);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      PoolRecordSpec that = PoolRecordSpec.class.cast(obj);
      return equal(this.description, that.description) && equal(this.state, that.state)
            && equal(this.probingEnabled, that.probingEnabled) && equal(this.allFailEnabled, that.allFailEnabled)
            && equal(this.weight, that.weight) && equal(this.failOverDelay, that.failOverDelay)
            && equal(this.threshold, that.threshold) && equal(this.ttl, that.ttl);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("description", description).add("state", state)
            .add("probingEnabled", probingEnabled).add("allFailEnabled", allFailEnabled).add("weight", weight)
            .add("failOverDelay", failOverDelay).add("threshold", threshold).add("ttl", ttl).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String description;
      private String state;
      private boolean probingEnabled;
      private boolean allFailEnabled;
      private int weight;
      private int failOverDelay;
      private int threshold;
      private int ttl;

      /**
       * @see PoolRecordSpec#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see PoolRecordSpec#getState()
       */
      public Builder state(String state) {
         this.state = state;
         return this;
      }

      /**
       * @see PoolRecordSpec#isProbingEnabled()
       */
      public Builder probingEnabled(boolean probingEnabled) {
         this.probingEnabled = probingEnabled;
         return this;
      }

      /**
       * @see PoolRecordSpec#isAllFailEnabled()
       */
      public Builder allFailEnabled(boolean allFailEnabled) {
         this.allFailEnabled = allFailEnabled;
         return this;
      }

      /**
       * @see PoolRecordSpec#getWeight()
       */
      public Builder weight(int weight) {
         this.weight = weight;
         return this;
      }

      /**
       * @see PoolRecordSpec#getFailOverDelay()
       */
      public Builder failOverDelay(int failOverDelay) {
         this.failOverDelay = failOverDelay;
         return this;
      }

      /**
       * @see PoolRecordSpec#getThreshold()
       */
      public Builder threshold(int threshold) {
         this.threshold = threshold;
         return this;
      }

      /**
       * @see PoolRecordSpec#getTTL()
       */
      public Builder ttl(int ttl) {
         this.ttl = ttl;
         return this;
      }

      public PoolRecordSpec build() {
         return new PoolRecordSpec(description, state, probingEnabled, allFailEnabled, weight, failOverDelay,
               threshold, ttl);
      }

      public Builder from(PoolRecordSpec in) {
         return this.description(in.description).state(in.state).probingEnabled(in.probingEnabled)
               .allFailEnabled(in.allFailEnabled).weight(in.weight).failOverDelay(in.failOverDelay)
               .threshold(in.threshold).ttl(in.ttl);
      }
   }
}
