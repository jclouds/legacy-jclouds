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

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import org.jclouds.javax.annotation.Nullable;

import java.util.Set;

/**
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/API_GetMetricStatistics.html" />
 *
 * @author Jeremy Whitlock
 */
public class GetMetricStatisticsResponse {

   private final Set<Datapoint> datapoints;
   private final String label;

   public GetMetricStatisticsResponse(@Nullable Set<Datapoint> datapoints, String label) {
      if (datapoints == null) {
         this.datapoints = Sets.newLinkedHashSet();
      } else {
         this.datapoints = datapoints;
      }
      this.label = label;
   }

   /**
    * return the list of {@link Datapoint} for the metric
    */
   public Set<Datapoint> getDatapoints() {
      return datapoints;
   }

   /**
    * return the label describing the specified metric
    */
   public String getLabel() {
      return label;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(datapoints, label);
   }

    /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      GetMetricStatisticsResponse other = (GetMetricStatisticsResponse)obj;
      return Objects.equal(this.datapoints, other.datapoints) &&
             Objects.equal(this.label, other.label);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this)
                    .add("label", label)
                    .add("datapoints", datapoints).toString();
   }

}
