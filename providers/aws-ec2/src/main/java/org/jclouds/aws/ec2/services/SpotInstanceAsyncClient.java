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

package org.jclouds.aws.ec2.services;

import static org.jclouds.aws.reference.FormParameters.ACTION;
import static org.jclouds.aws.reference.FormParameters.VERSION;

import javax.annotation.Nullable;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.AWSEC2AsyncClient;
import org.jclouds.aws.ec2.binders.BindSpotInstanceRequestIdsToIndexedFormParams;
import org.jclouds.aws.ec2.binders.IfNotNullBindAvailabilityZoneToLaunchSpecificationFormParam;
import org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions;
import org.jclouds.aws.ec2.options.RequestSpotInstancesOptions;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to EC2 Spot Instances via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = AWSEC2AsyncClient.VERSION)
@VirtualHost
public interface SpotInstanceAsyncClient {

   /**
    * @see SpotInstanceClient#describeSpotInstanceRequestsInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSpotInstanceRequests")
   ListenableFuture<String> describeSpotInstanceRequestsInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @BinderParam(BindSpotInstanceRequestIdsToIndexedFormParams.class) String... requestIds);

   /**
    * @see SpotInstanceClient#requestSpotInstancesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RequestSpotInstances")
   ListenableFuture<String> requestSpotInstancesInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @Nullable @BinderParam(IfNotNullBindAvailabilityZoneToLaunchSpecificationFormParam.class) String nullableAvailabilityZone,
         @FormParam("LaunchSpecification.ImageId") String imageId,@FormParam("InstanceCount") int instanceCount, @FormParam("SpotPrice") float spotPrice,
         RequestSpotInstancesOptions... options);

   /**
    * @see SpotInstanceClient#describeSpotPriceHistoryInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSpotPriceHistory")
   ListenableFuture<String> describeSpotPriceHistoryInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         DescribeSpotPriceHistoryOptions... options);

   /**
    * @see SpotInstanceClient#cancelSpotInstanceRequestsInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CancelSpotInstanceRequests")
   ListenableFuture<String> cancelSpotInstanceRequestsInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @BinderParam(BindSpotInstanceRequestIdsToIndexedFormParams.class) String... requestIds);

}
