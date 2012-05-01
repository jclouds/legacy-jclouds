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

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.cloudwatch.domain.GetMetricStatisticsResponse;
import org.jclouds.cloudwatch.domain.ListMetricsResponse;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.functions.ISO8601Format;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptionsV2;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.cloudwatch.xml.GetMetricStatisticsResponseHandler;
import org.jclouds.cloudwatch.xml.GetMetricStatisticsResponseHandlerV2;
import org.jclouds.cloudwatch.xml.ListMetricsResponseHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Date;
import java.util.Set;

/**
 * Provides access to Amazon CloudWatch via the Query API
 * <p/>
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/DeveloperGuide/index.html"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface CloudWatchAsyncClient {
   public static final String VERSION = "2010-08-01";

   /**
    * @see CloudWatchClient#getMetricStatisticsInRegion
    */
   @Deprecated
   @POST
   @Path("/")
   @XMLResponseParser(GetMetricStatisticsResponseHandler.class)
   @FormParams(keys = "Action", values = "GetMetricStatistics")
   ListenableFuture<? extends Set<Datapoint>> getMetricStatisticsInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("MetricName") String metricName,
         @FormParam("Namespace") String namespace,
         @FormParam("StartTime") @ParamParser(ISO8601Format.class) Date startTime,
         @FormParam("EndTime") @ParamParser(ISO8601Format.class) Date endTime,
         @FormParam("Period") int period,
         @FormParam("Statistics.member.1") Statistics statistics,
         GetMetricStatisticsOptions... options);

   /**
    * @see CloudWatchClient#listMetrics(String, org.jclouds.cloudwatch.options.ListMetricsOptions)
    */
   @POST
   @Path("/")
   @XMLResponseParser(ListMetricsResponseHandler.class)
   @FormParams(keys = "Action", values = "ListMetrics")
   ListenableFuture<? extends ListMetricsResponse> listMetrics(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         ListMetricsOptions options);

   /**
    * @see CloudWatchClient#getMetricStatistics(String, org.jclouds.cloudwatch.options.GetMetricStatisticsOptionsV2)
    */
   @POST
   @Path("/")
   @XMLResponseParser(GetMetricStatisticsResponseHandlerV2.class)
   @FormParams(keys = "Action", values = "GetMetricStatistics")
   ListenableFuture<? extends GetMetricStatisticsResponse> getMetricStatistics(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         GetMetricStatisticsOptionsV2 options);

}
