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
package org.jclouds.cloudwatch.features;

import org.jclouds.cloudwatch.domain.GetMetricStatistics;
import org.jclouds.cloudwatch.domain.GetMetricStatisticsResponse;
import org.jclouds.cloudwatch.domain.Metric;
import org.jclouds.cloudwatch.domain.MetricDatum;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;

/**
 * Provides access to Amazon CloudWatch via the Query API
 * <p/>
 * 
 * @see MetricAsyncApi
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference" />
 * @author Jeremy Whitlock
 */
public interface MetricApi {

   /**
    * Returns a list of valid metrics stored for the AWS account owner.
    * 
    * <p/>
    * <h3>Note</h3> Up to 500 results are returned for any one call. To retrieve further results,
    * use returned NextToken (
    * {@link org.jclouds.cloudwatch.domain.ListMetricsResponse#getNextToken()}) value with
    * subsequent calls .To retrieve all available metrics with one call, use
    * {@link org.jclouds.cloudwatch.CloudWatch#listMetrics(MetricApi,
    * org.jclouds.cloudwatch.options.ListMetricsOptions)}
    * 
    * @param options the options describing the metrics query
    * 
    * @return the response object
    */
   IterableWithMarker<Metric> list(ListMetricsOptions options);

   PagedIterable<Metric> list();

   /**
    * Gets statistics for the specified metric.
    * 
    * @param statistics the statistics to gather
    * @param options the options describing the metric statistics query
    * 
    * @return the response object
    */
   GetMetricStatisticsResponse getMetricStatistics(GetMetricStatistics statistics, GetMetricStatisticsOptions options);

   GetMetricStatisticsResponse getMetricStatistics(GetMetricStatistics statistics);

   /**
    * Publishes metric data points to Amazon CloudWatch.
    *
    * @param metrics the metrics to publish
    * @param namespace the namespace to publish the metrics to
    */
   void putMetricsInNamespace(Iterable<MetricDatum> metrics, String namespace);

}
