/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.aws.cloudwatch;

import static org.jclouds.aws.ec2.reference.EC2Parameters.ACTION;
import static org.jclouds.aws.ec2.reference.EC2Parameters.VERSION;

import java.util.Date;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.cloudwatch.domain.Datapoint;
import org.jclouds.aws.cloudwatch.functions.ISO8601Format;
import org.jclouds.aws.cloudwatch.xml.GetMetricStatisticsResponseHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.functions.RegionToEndpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

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
@FormParams(keys = VERSION, values = CloudWatchAsyncClient.VERSION)
@VirtualHost
public interface CloudWatchAsyncClient {
   public static final String VERSION = "2009-05-15";

   /**
    * @see CloudWatchClient#getMetricStatisticsInRegion
    */
   @POST
   @Path("/")
   @XMLResponseParser(GetMetricStatisticsResponseHandler.class)
   @FormParams(keys = ACTION, values = "GetMetricStatistics")
   ListenableFuture<? extends Set<Datapoint>> getMetricStatisticsInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("MeasureName") String measureName,
            @FormParam("StartTime") @ParamParser(ISO8601Format.class) Date startTime,
            @FormParam("EndTime") @ParamParser(ISO8601Format.class) Date endTime, @FormParam("Period") int period,
            @FormParam("Statistics.member.1") String statistics);
}
