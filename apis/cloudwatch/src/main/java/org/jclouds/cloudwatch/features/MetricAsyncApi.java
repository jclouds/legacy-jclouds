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

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Amazon CloudWatch via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference" />
 * @author Jeremy Whitlock
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface MetricAsyncApi {

   /**
    * @see MetricApi#list()
    */
   @Named("ListMetrics")
   @POST
   @Path("/")
   @XMLResponseParser(ListMetricsResponseHandler.class)
   @Transform(MetricsToPagedIterable.class)
   @FormParams(keys = "Action", values = "ListMetrics")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<Metric>> list();

   /**
    * @see MetricApi#list(ListMetricsOptions)
    */
   @Named("ListMetrics")
   @POST
   @Path("/")
   @XMLResponseParser(ListMetricsResponseHandler.class)
   @FormParams(keys = "Action", values = "ListMetrics")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<? extends IterableWithMarker<Metric>> list(ListMetricsOptions options);

   /**
    * @see MetricApi#getMetricStatistics(GetMetricStatistics)
    */
   @Named("GetMetricStatistics")
   @POST
   @Path("/")
   @XMLResponseParser(GetMetricStatisticsResponseHandlerV2.class)
   @FormParams(keys = "Action", values = "GetMetricStatistics")
   ListenableFuture<? extends GetMetricStatisticsResponse> getMetricStatistics(
            @BinderParam(GetMetricStatisticsBinder.class) GetMetricStatistics statistics);

   /**
    * @see MetricApi#getMetricStatistics(GetMetricStatistics, GetMetricStatisticsOptions)
    */
   @Named("GetMetricStatistics")
   @POST
   @Path("/")
   @XMLResponseParser(GetMetricStatisticsResponseHandlerV2.class)
   @FormParams(keys = "Action", values = "GetMetricStatistics")
   ListenableFuture<? extends GetMetricStatisticsResponse> getMetricStatistics(
            @BinderParam(GetMetricStatisticsBinder.class) GetMetricStatistics statistics,
            GetMetricStatisticsOptions options);

   /**
    * @see MetricApi#putMetricsInNamespace(Iterable, String)
    */
   @Named("PutMetricData")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "PutMetricData")
   ListenableFuture<Void> putMetricsInNamespace(@BinderParam(MetricDataBinder.class) Iterable<MetricDatum> metrics,
                                        @FormParam("Namespace") String namespace);

}
