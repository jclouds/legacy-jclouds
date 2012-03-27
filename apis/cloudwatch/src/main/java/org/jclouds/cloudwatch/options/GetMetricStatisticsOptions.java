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

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options used to control metric statistics are returned
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/index.html?API_GetMetricStatistics.html"
 *      />
 * @author Andrei Savu
 */
public class GetMetricStatisticsOptions extends BaseHttpRequestOptions {

   public static final GetMetricStatisticsOptions NONE = new GetMetricStatisticsOptions();

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
    }
}
