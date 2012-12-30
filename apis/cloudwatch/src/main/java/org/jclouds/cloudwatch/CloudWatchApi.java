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
package org.jclouds.cloudwatch;

import com.google.inject.Provides;
import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.features.MetricApi;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import java.util.Date;
import java.util.Set;
/**
 * Provides access to Amazon CloudWatch via the Query API
 * <p/>
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference"
 *      />
 * @author Adrian Cole
 */
public interface CloudWatchApi {
   /**
    * 
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();
   
   /**
    * This call returns data for one or more statistics of given a metric. For more information, see
    * Statistic and Metric.
    *
    * <p/>
    * <h3>Note</h3> The maximum number of datapoints that the Amazon CloudWatch service will return
    * in a single GetMetricStatistics request is 1,440. If a request is made that would generate
    * more datapoints than this amount, Amazon CloudWatch will return an error. You can alter your
    * request by narrowing the time range (StartTime, EndTime) or increasing the Period in your
    * single request. You may also get all of the data at the granularity you originally asked for
    * by making multiple requests with adjacent time ranges.
    * 
    * @param region
    *           region to gather metrics in
    * @param metricName
    *           The measure name that corresponds to the measure for the gathered metric.
    *           <p/>
    *           note
    *           <p/>
    *           Must be a valid collected metric with the corresponding measure name, please see
    *           Available Amazon CloudWatch Metrics
    * @param namespace
    *           The namespace of the metric (e.g. AWS/EC2)
    * @param startTime
    *           The timestamp of the first datapoint to return, inclusive. We round your value down
    *           to the nearest minute. You can set your start time for more than two weeks in the
    *           past. However, you will only get data for the past two weeks.
    * @param endTime
    *           The timestamp to use for determining the last datapoint to return. This is the last
    *           datapoint to fetch, exclusive.
    * @param period
    *           The granularity (in seconds) of the returned datapoints.
    * @param statistics
    *           The statistics to be returned for the given metric. ex. Average
    * @param options
    *          more filtering options (e.g. instance ID)
    * @see MetricApi#getMetricStatistics(org.jclouds.cloudwatch.domain.GetMetricStatistics)
    */
   @Deprecated
   Set<Datapoint> getMetricStatisticsInRegion(@Nullable String region, String metricName, String namespace,
          Date startTime, Date endTime, int period, Statistics statistics, GetMetricStatisticsOptions... options);
   
   /**
    * Provides synchronous access to Metric features.
    */
   @Delegate
   MetricApi getMetricApi();
   
   /**
    * Provides synchronous access to Metric features.
    */
   @Delegate
   MetricApi getMetricApiForRegion(@EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);
}
