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
package org.jclouds.aws.ec2.services;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.binders.BindLaunchSpecificationToFormParams;
import org.jclouds.aws.ec2.binders.BindSpotInstanceRequestIdsToIndexedFormParams;
import org.jclouds.aws.ec2.domain.LaunchSpecification;
import org.jclouds.aws.ec2.domain.Spot;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions;
import org.jclouds.aws.ec2.options.RequestSpotInstancesOptions;
import org.jclouds.aws.ec2.xml.DescribeSpotPriceHistoryResponseHandler;
import org.jclouds.aws.ec2.xml.SpotInstanceHandler;
import org.jclouds.aws.ec2.xml.SpotInstancesHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to EC2 Spot Instances via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface SpotInstanceAsyncClient {

   /**
    * @see SpotInstanceClient#describeSpotInstanceRequestsInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSpotInstanceRequests")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @XMLResponseParser(SpotInstancesHandler.class)
   ListenableFuture<? extends Set<SpotInstanceRequest>> describeSpotInstanceRequestsInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @BinderParam(BindSpotInstanceRequestIdsToIndexedFormParams.class) String... requestIds);

   /**
    * @see SpotInstanceClient#requestSpotInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RequestSpotInstances")
   @XMLResponseParser(SpotInstanceHandler.class)
   ListenableFuture<SpotInstanceRequest> requestSpotInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("SpotPrice") float spotPrice, @FormParam("LaunchSpecification.ImageId") String imageId,
         @FormParam("LaunchSpecification.InstanceType") String instanceType);

   /**
    * @see SpotInstanceClient#requestSpotInstancesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RequestSpotInstances")
   @XMLResponseParser(SpotInstancesHandler.class)
   ListenableFuture<? extends Set<SpotInstanceRequest>> requestSpotInstancesInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("SpotPrice") float spotPrice, @FormParam("InstanceCount") int instanceCount,
         @BinderParam(BindLaunchSpecificationToFormParams.class) LaunchSpecification launchSpec,
         RequestSpotInstancesOptions... options);

   /**
    * @see SpotInstanceClient#describeSpotPriceHistoryInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSpotPriceHistory")
   @XMLResponseParser(DescribeSpotPriceHistoryResponseHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<Spot>> describeSpotPriceHistoryInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         DescribeSpotPriceHistoryOptions... options);

   /**
    * @see SpotInstanceClient#cancelSpotInstanceRequestsInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CancelSpotInstanceRequests")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> cancelSpotInstanceRequestsInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @BinderParam(BindSpotInstanceRequestIdsToIndexedFormParams.class) String... requestIds);

}
