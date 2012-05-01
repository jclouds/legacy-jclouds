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
package org.jclouds.cloudwatch.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Options used to control metric statistics are returned
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/index.html?API_GetMetricStatistics.html"
 *      />
 * @author Andrei Savu, Jeremy Whitlock
 */
public class GetMetricStatisticsOptions extends BaseHttpRequestOptions {

   private Set<Dimension> dimensions = Sets.newLinkedHashSet();
   
   /**
    * A dimension describing qualities of the metric.  (Can be called multiple times up to a maximum of 10 times.)
    *
    * @param dimension the dimension describing the qualities of the metric
    *
    * @return this {@code Builder} object
    *
    * @throws IllegalArgumentException if the number of dimensions would be greater than 10 after adding
    */
   public GetMetricStatisticsOptions dimension(Dimension dimension) {
      if (dimension != null) {
         Preconditions.checkArgument(dimensions.size() < 10, "dimension member maximum count of 10 exceeded.");
         this.dimensions.add(dimension);
      }
      return this;
   }
   
   /**
    * A list of dimensions describing qualities of the metric.  (Set can be 10 or less items.)
    *
    * @param dimensions the dimensions describing the qualities of the metric
    *
    * @return this {@code Builder} object
    *
    * @throws IllegalArgumentException if this is invoked more than 10 times
    */
   public GetMetricStatisticsOptions dimensions(Set<Dimension> dimensions) {
      if (dimensions != null) {
         Preconditions.checkArgument(dimensions.size() <= 10, "dimensions can have 10 or fewer members.");
         this.dimensions = ImmutableSet.<Dimension>copyOf(dimensions);
      }
      return this;
   }
   
   /**
    * @param instanceId
    *           filter metrics by instance Id
    */
   public GetMetricStatisticsOptions instanceId(String instanceId) {
      String[] parts = AWSUtils.parseHandle(instanceId);
      this.formParameters.put("Dimensions.member.1.Name", "InstanceId");
      this.formParameters.put("Dimensions.member.1.Value", checkNotNull(parts[1]));
      return this;
   }

   /**
    * @param unit
    *          the unit of the metric
    */
   public GetMetricStatisticsOptions unit(Unit unit) {
      this.formParameters.put("Unit", unit.toString());
      return this;
   }

   public static class Builder {

      /**
       * @see GetMetricStatisticsOptions#instanceId
       */
      public static GetMetricStatisticsOptions instanceId(String instanceId) {
         GetMetricStatisticsOptions options = new GetMetricStatisticsOptions();
         return options.instanceId(instanceId);
      }

      /**
       * @see GetMetricStatisticsOptions#unit
       */
      public static GetMetricStatisticsOptions unit(Unit unit) {
         GetMetricStatisticsOptions options = new GetMetricStatisticsOptions();
         return options.unit(unit);
      }
      
      /**
       * @see GetMetricStatisticsOptions#dimension
       */
      public static GetMetricStatisticsOptions dimension(Dimension dimension) {
         GetMetricStatisticsOptions options = new GetMetricStatisticsOptions();
         return options.dimension(dimension);
      }
      
      /**
       * @see GetMetricStatisticsOptions#dimensions
       */
      public static GetMetricStatisticsOptions dimensions(Set<Dimension> dimensions) {
         GetMetricStatisticsOptions options = new GetMetricStatisticsOptions();
         return options.dimensions(dimensions);
      }
    }

   @Override
   public Multimap<String, String> buildFormParameters() {
      Multimap<String, String> formParameters = super.buildFormParameters();
      int dimensionIndex = 1;
      for (Dimension dimension : dimensions) {
         formParameters.put("Dimensions.member." + dimensionIndex + ".Name", dimension.getName());
         formParameters.put("Dimensions.member." + dimensionIndex + ".Value", dimension.getValue());
         dimensionIndex++;
      }
      return formParameters;
   }

}
