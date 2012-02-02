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
 * Detailed information on usage
 *
 * @author Adam Lowe
 * @see ServerStatus
 */

public class ResourceUsage {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private double usage;
      private double max;
      private String unit;

      public Builder usage(double usage) {
         this.usage = usage;
         return this;
      }

      public Builder max(double max) {
         this.max = max;
         return this;
      }

      public Builder unit(String unit) {
         this.unit = unit;
         return this;
      }

      public ResourceUsage build() {
         return new ResourceUsage(usage, max, unit);
      }

      public Builder fromCpu(ResourceUsage in) {
         return usage(in.getUsage()).max(in.getMax()).unit(in.getUnit());
      }
   }

   private final double usage;
   private final double max;
   private final String unit;

   public ResourceUsage(double usage, double max, String unit) {
      this.usage = usage;
      this.max = max;
      this.unit = unit;
   }

   /**
    * @return the usage in #unit
    */
   public double getUsage() {
      return usage;
   }

   /**
    * @return the max usage in #unit
    */
   public double getMax() {
      return max;
   }

   /**
    * @return the unit used
    */
   public String getUnit() {
      return unit;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof ResourceUsage) {
         ResourceUsage other = (ResourceUsage) object;
         return Objects.equal(usage, other.usage)
               && Objects.equal(max, other.max)
               && Objects.equal(unit, other.unit);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(usage, max, unit);
   }
   
   @Override
   public String toString() {
      return String.format("[usage=%f, max=%f, unit=%s]",
            usage, max, unit);
   }
}
