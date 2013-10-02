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

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.cloudwatch.binders.GetMetricStatisticsBinder;
import org.jclouds.cloudwatch.binders.MetricDataBinder;
import org.jclouds.cloudwatch.domain.GetMetricStatistics;
import org.jclouds.cloudwatch.domain.GetMetricStatisticsResponse;
import org.jclouds.cloudwatch.domain.Metric;
import org.jclouds.cloudwatch.domain.MetricDatum;
import org.jclouds.cloudwatch.functions.MetricsToPagedIterable;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.cloudwatch.xml.GetMetricStatisticsResponseHandlerV2;
import org.jclouds.cloudwatch.xml.ListMetricsResponseHandler;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to Amazon CloudWatch via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference" />
 * @author Jeremy Whitlock
 */
@RequestFilters(FormSigner.class)
@VirtualHost
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
   @Named("ListMetrics")
   @POST
   @Path("/")
   @XMLResponseParser(ListMetricsResponseHandler.class)
   @FormParams(keys = "Action", values = "ListMetrics")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<Metric> list(ListMetricsOptions options);

   @Named("ListMetrics")
   @POST
   @Path("/")
   @XMLResponseParser(ListMetricsResponseHandler.class)
   @Transform(MetricsToPagedIterable.class)
   @FormParams(keys = "Action", values = "ListMetrics")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Metric> list();

   /**
    * Gets statistics for the specified metric.
    * 
    * @param statistics the statistics to gather
    * @param options the options describing the metric statistics query
    * 
    * @return the response object
    */
   @Named("GetMetricStatistics")
   @POST
   @Path("/")
   @XMLResponseParser(GetMetricStatisticsResponseHandlerV2.class)
   @FormParams(keys = "Action", values = "GetMetricStatistics")
   GetMetricStatisticsResponse getMetricStatistics(
            @BinderParam(GetMetricStatisticsBinder.class) GetMetricStatistics statistics,
            GetMetricStatisticsOptions options);

   @Named("GetMetricStatistics")
   @POST
   @Path("/")
   @XMLResponseParser(GetMetricStatisticsResponseHandlerV2.class)
   @FormParams(keys = "Action", values = "GetMetricStatistics")
   GetMetricStatisticsResponse getMetricStatistics(
            @BinderParam(GetMetricStatisticsBinder.class) GetMetricStatistics statistics);

   /**
    * Publishes metric data points to Amazon CloudWatch.
    *
    * @param metrics the metrics to publish
    * @param namespace the namespace to publish the metrics to
    */
   @Named("PutMetricData")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "PutMetricData")
   void putMetricsInNamespace(@BinderParam(MetricDataBinder.class) Iterable<MetricDatum> metrics,
                                        @FormParam("Namespace") String namespace);

}
