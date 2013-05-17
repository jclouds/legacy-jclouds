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
package org.jclouds.cloudwatch.options;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.http.options.BaseHttpRequestOptions;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Options used to control metric statistics are returned
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/index.html?API_GetMetricStatistics.html"
 *      />
 * @author Andrei Savu, Jeremy Whitlock
 */
public class GetMetricStatisticsOptions extends BaseHttpRequestOptions {

   private Set<Dimension> dimensions;
   
   /**
    * A dimension describing qualities of the metric.
    *
    * @param dimension the dimension describing the qualities of the metric
    *
    * @return this {@code Builder} object
    */
   public GetMetricStatisticsOptions dimension(Dimension dimension) {
      if (dimensions == null) {
         dimensions = Sets.newLinkedHashSet();
      }
      this.dimensions.add(dimension);
      return this;
   }
   
   /**
    * A list of dimensions describing qualities of the metric.
    *
    * @param dimensions the dimensions describing the qualities of the metric
    *
    * @return this {@code Builder} object
    */
   public GetMetricStatisticsOptions dimensions(Set<Dimension> dimensions) {
      this.dimensions = dimensions;
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
      if (dimensions != null) {
         for (Dimension dimension : dimensions) {
            formParameters.put("Dimensions.member." + dimensionIndex + ".Name", dimension.getName());
            formParameters.put("Dimensions.member." + dimensionIndex + ".Value", dimension.getValue());
            dimensionIndex++;
         }
      }
      return formParameters;
   }

}
