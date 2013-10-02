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
package org.jclouds.cloudwatch.domain;

import com.google.common.base.Objects;
import org.jclouds.javax.annotation.Nullable;

import java.util.Date;

/**
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference?DT_Datapoint.html"
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
   private final Unit unit;
   private final String customUnit;

   public Datapoint(@Nullable Double average, @Nullable Double maximum, @Nullable Double minimum,
            @Nullable Date timestamp, @Nullable Double samples, @Nullable Double sum, @Nullable Unit unit,
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
   public Double getAverage() {
      return average;
   }

   /**
    * return Maximum of the samples used for the datapoint.
    */
   @Nullable
   public Double getMaximum() {
      return maximum;
   }

   /**
    * return Minimum of samples for the datapoint.
    */
   @Nullable
   public Double getMinimum() {
      return minimum;
   }

   /**
    * return Indicates the beginning of the time aggregation for this value and samples.
    */
   @Nullable
   public Date getTimestamp() {
      return timestamp;
   }

   /**
    * return The number of Measurements that contributed to the aggregate value of this datapoint.
    */
   @Nullable
   public Double getSamples() {
      return samples;
   }

   /**
    * return Sum of samples for the datapoint.
    */
   @Nullable
   public Double getSum() {
      return sum;
   }

   /**
    * return Standard unit used for the datapoint.
    */
   @Nullable
   public Unit getUnit() {
      return unit;
   }

   /**
    * return CustomUnit defined for the datapoint.
    */
   @Nullable
   public String getCustomUnit() {
      return customUnit;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(average, customUnit, maximum, minimum, samples, sum, timestamp, unit);
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
      return Objects.equal(this.average, other.average) &&
             Objects.equal(this.customUnit, other.customUnit) &&
             Objects.equal(this.maximum, other.maximum) &&
             Objects.equal(this.minimum, other.minimum) &&
             Objects.equal(this.samples, other.samples) &&
             Objects.equal(this.sum, other.sum) &&
             Objects.equal(this.timestamp, other.timestamp) &&
             Objects.equal(this.unit, other.unit);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
                    .add("timestamp", timestamp)
                    .add("customUnit", customUnit)
                    .add("maximum", maximum)
                    .add("minimum", minimum)
                    .add("average", average)
                    .add("sum", sum)
                    .add("samples", samples)
                    .add("unit", unit).toString();
   }

}
