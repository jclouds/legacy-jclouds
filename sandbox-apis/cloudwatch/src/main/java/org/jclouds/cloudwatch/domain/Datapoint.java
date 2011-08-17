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
package org.jclouds.cloudwatch.domain;

import java.util.Date;

import javax.annotation.Nullable;

/**
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/DeveloperGuide/index.html?DT_Datapoint.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class Datapoint {

   private final Double average;
   private final Double maximum;
   private final Double minimum;
   private final Date timestamp;
   private final Double samples;
   private final Double sum;
   private final StandardUnit unit;
   private final String customUnit;

   public Datapoint(@Nullable Double average, @Nullable Double maximum, @Nullable Double minimum,
            @Nullable Date timestamp, @Nullable Double samples, @Nullable Double sum, @Nullable StandardUnit unit,
            @Nullable String customUnit) {
      this.average = average;
      this.maximum = maximum;
      this.minimum = minimum;
      this.timestamp = timestamp;
      this.samples = samples;
      this.sum = sum;
      this.unit = unit;
      this.customUnit = customUnit;
   }

   /**
    * return Average of samples for the datapoint.
    */
   @Nullable
   Double getAverage() {
      return average;
   }

   /**
    * return Maximum of the samples used for the datapoint.
    */
   @Nullable
   Double getMaximum() {
      return maximum;
   }

   /**
    * return Minimum of samples for the datapoint.
    */
   @Nullable
   Double getMinimum() {
      return minimum;
   }

   /**
    * return Indicates the beginning of the time aggregation for this value and samples.
    */
   @Nullable
   Date getTimestamp() {
      return timestamp;
   }

   /**
    * return The number of Measurements that contributed to the aggregate value of this datapoint.
    */
   @Nullable
   Double getSamples() {
      return samples;
   }

   /**
    * return Sum of samples for the datapoint.
    */
   @Nullable
   Double getSum() {
      return sum;
   }

   /**
    * return Standard unit used for the datapoint.
    */
   @Nullable
   StandardUnit getUnit() {
      return unit;
   }

   /**
    * return CustomUnit defined for the datapoint.
    */
   @Nullable
   String getCustomUnit() {
      return customUnit;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((average == null) ? 0 : average.hashCode());
      result = prime * result + ((customUnit == null) ? 0 : customUnit.hashCode());
      result = prime * result + ((maximum == null) ? 0 : maximum.hashCode());
      result = prime * result + ((minimum == null) ? 0 : minimum.hashCode());
      result = prime * result + ((samples == null) ? 0 : samples.hashCode());
      result = prime * result + ((sum == null) ? 0 : sum.hashCode());
      result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
      result = prime * result + ((unit == null) ? 0 : unit.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Datapoint other = (Datapoint) obj;
      if (average == null) {
         if (other.average != null)
            return false;
      } else if (!average.equals(other.average))
         return false;
      if (customUnit == null) {
         if (other.customUnit != null)
            return false;
      } else if (!customUnit.equals(other.customUnit))
         return false;
      if (maximum == null) {
         if (other.maximum != null)
            return false;
      } else if (!maximum.equals(other.maximum))
         return false;
      if (minimum == null) {
         if (other.minimum != null)
            return false;
      } else if (!minimum.equals(other.minimum))
         return false;
      if (samples == null) {
         if (other.samples != null)
            return false;
      } else if (!samples.equals(other.samples))
         return false;
      if (sum == null) {
         if (other.sum != null)
            return false;
      } else if (!sum.equals(other.sum))
         return false;
      if (timestamp == null) {
         if (other.timestamp != null)
            return false;
      } else if (!timestamp.equals(other.timestamp))
         return false;
      if (unit == null) {
         if (other.unit != null)
            return false;
      } else if (!unit.equals(other.unit))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[average=" + average + ", customUnit=" + customUnit + ", maximum=" + maximum + ", minimum=" + minimum
               + ", samples=" + samples + ", sum=" + sum + ", timestamp=" + timestamp + ", unit=" + unit + "]";
   }

}
