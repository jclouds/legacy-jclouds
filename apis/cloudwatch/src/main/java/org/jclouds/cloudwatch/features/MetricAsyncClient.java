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
package org.jclouds.cloudwatch.features;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.cloudwatch.binders.GetMetricStatisticsBinder;
import org.jclouds.cloudwatch.binders.MetricDataBinder;
import org.jclouds.cloudwatch.domain.GetMetricStatistics;
import org.jclouds.cloudwatch.domain.GetMetricStatisticsResponse;
import org.jclouds.cloudwatch.domain.ListMetricsResponse;
import org.jclouds.cloudwatch.domain.MetricDatum;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.cloudwatch.xml.GetMetricStatisticsResponseHandlerV2;
import org.jclouds.cloudwatch.xml.ListMetricsResponseHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Provides access to Amazon CloudWatch via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference" />
 * @author Jeremy Whitlock
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface MetricAsyncClient {

   /**
    * @see MetricClient#listMetrics()
    */
   @POST
   @Path("/")
   @XMLResponseParser(ListMetricsResponseHandler.class)
   @FormParams(keys = "Action", values = "ListMetrics")
   ListenableFuture<? extends ListMetricsResponse> listMetrics();

   /**
    * @see MetricClient#listMetrics(ListMetricsOptions)
    */
   @POST
   @Path("/")
   @XMLResponseParser(ListMetricsResponseHandler.class)
   @FormParams(keys = "Action", values = "ListMetrics")
   ListenableFuture<? extends ListMetricsResponse> listMetrics(ListMetricsOptions options);

   /**
    * @see MetricClient#getMetricStatistics(GetMetricStatistics)
    */
   @POST
   @Path("/")
   @XMLResponseParser(GetMetricStatisticsResponseHandlerV2.class)
   @FormParams(keys = "Action", values = "GetMetricStatistics")
   ListenableFuture<? extends GetMetricStatisticsResponse> getMetricStatistics(
            @BinderParam(GetMetricStatisticsBinder.class) GetMetricStatistics statistics);

   /**
    * @see MetricClient#getMetricStatistics(GetMetricStatistics, GetMetricStatisticsOptions)
    */
   @POST
   @Path("/")
   @XMLResponseParser(GetMetricStatisticsResponseHandlerV2.class)
   @FormParams(keys = "Action", values = "GetMetricStatistics")
   ListenableFuture<? extends GetMetricStatisticsResponse> getMetricStatistics(
            @BinderParam(GetMetricStatisticsBinder.class) GetMetricStatistics statistics,
            GetMetricStatisticsOptions options);

   /**
    * @see MetricClient#putMetricData(Iterable, String)
    */
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "PutMetricData")
   ListenableFuture<Void> putMetricData(@BinderParam(MetricDataBinder.class) Iterable<MetricDatum> metrics,
                                        @FormParam("Namespace") String namespace);

}
