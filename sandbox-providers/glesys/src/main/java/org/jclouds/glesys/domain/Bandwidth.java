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
package org.jclouds.glesys.domain;

import com.google.common.base.Objects;

/**
 * Detailed information on Server bandwidth
 *
 * @author Adam Lowe
 * @see ServerStatus
 */
public class Bandwidth {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long today;
      private long last30Days;
      private long max;

      public Builder today(long today) {
         this.today = today;
         return this;
      }

      public Builder last30Days(long last30Days) {
         this.last30Days = last30Days;
         return this;
      }

      public Builder max(long max) {
         this.max = max;
         return this;
      }

      public Bandwidth build() {
         return new Bandwidth(today, last30Days, max);
      }

      public Builder fromBandwidth(Bandwidth in) {
         return today(in.getToday()).last30Days(in.getLast30Days()).max(in.getMax());
      }
   }

   private final long today;
   private final long last30Days;
   private final long max;

   public Bandwidth(long today, long last30Days, long max) {
      this.today = today;
      this.last30Days = last30Days;
      this.max = max;
   }

   /**
    * @return the bandwidth used today in MB
    */
   public long getToday() {
      return today;
   }

   /**
    * @return the bandwidth used in the past 30 days in GB
    */
   public long getLast30Days() {
      return last30Days;
   }

   /**
    * @return the max bandwidth allowed over a 30 day period in GB
    */
   public long getMax() {
      return max;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(today, last30Days, max);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Bandwidth) {
         Bandwidth other = (Bandwidth) object;
         return Objects.equal(today, other.today)
               && Objects.equal(last30Days, other.last30Days)
               && Objects.equal(max, other.max);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return String.format("[today=%d, last30Days=%d, max=%d]", today, last30Days, max);
   }

}
