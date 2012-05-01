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

import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.cloudwatch.domain.GetMetricStatisticsResponse;
import org.jclouds.cloudwatch.domain.ListMetricsResponse;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptionsV2;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Provides access to Amazon CloudWatch via the Query API
 * <p/>
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/DeveloperGuide/index.html"
 *      />
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface CloudWatchClient {

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
    */
   @Deprecated
   Set<Datapoint> getMetricStatisticsInRegion(@Nullable String region, String metricName, String namespace,
          Date startTime, Date endTime, int period, Statistics statistics, GetMetricStatisticsOptions... options);

   /**
    * Returns a list of valid metrics stored for the AWS account owner.
    *
    * <p/>
    * <h3>Note</h3> Up to 500 results are returned for any one call. To retrieve further results, use returned
    * NextToken ({@link org.jclouds.cloudwatch.domain.ListMetricsResponse#getNextToken()})
    * value with subsequent calls  .To retrieve all available metrics with one call, use
    * {@link CloudWatch#listMetrics(CloudWatchClient, String, org.jclouds.cloudwatch.options.ListMetricsOptions)}.
    *
    * @param region the region to query metrics in
    * @param options the options describing the metrics query
    *
    * @return the response object
    */
   ListMetricsResponse listMetrics(@Nullable String region, ListMetricsOptions options);

    /**
     * Gets statistics for the specified metric.
     *
     * @param region the region to gather metrics in
     * @param options the options describing the metric statistics query
     *
     * @return the response object
     */
   GetMetricStatisticsResponse getMetricStatistics(@Nullable String region, GetMetricStatisticsOptionsV2 options);

}
