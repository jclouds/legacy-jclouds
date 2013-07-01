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
package org.jclouds.aws.ec2.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
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
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to EC2 Spot Instances via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface SpotInstanceApi {

   /**
    * Describes Spot Instance requests. Spot Instances are instances that Amazon EC2 starts on your
    * behalf when the maximum price that you specify exceeds the current Spot Price. Amazon EC2
    * periodically sets the Spot Price based on available Spot Instance capacity and current spot
    * instance requests. For conceptual information about Spot Instances, refer to the Amazon
    * Elastic Compute Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    * 
    * @param region
    *           Region where the spot instance service is running
    * @param requestIds
    *           Specifies the ID of the Spot Instance request.
    * 
    * @see #requestSpotInstancesInRegion
    * @see #cancelSpotInstanceRequestsInRegion
    * @see #describeSpotPriceHistoryInRegion
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSpotInstanceRequests.html"
    *      />
    * @return TODO
    */
   @Named("DescribeSpotInstanceRequests")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSpotInstanceRequests")
   @Fallback(EmptySetOnNotFoundOr404.class)
   @XMLResponseParser(SpotInstancesHandler.class)
   Set<SpotInstanceRequest> describeSpotInstanceRequestsInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @BinderParam(BindSpotInstanceRequestIdsToIndexedFormParams.class) String... requestIds);

   /**
    * request a single spot instance
    * 
    * @param region
    *           Region where the spot instance service is running
    * @param spotPrice
    *           Specifies the maximum hourly price for any Spot Instance launched to fulfill the
    *           request.
    * @param imageId
    *           The AMI ID.
    * @param instanceType
    *           The instance type (ex. m1.small)
    * @return spot instance request
    * @see #requestSpotInstancesInRegion
    */
   @Named("RequestSpotInstances")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RequestSpotInstances")
   @XMLResponseParser(SpotInstanceHandler.class)
   SpotInstanceRequest requestSpotInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("SpotPrice") float spotPrice, @FormParam("LaunchSpecification.ImageId") String imageId,
         @FormParam("LaunchSpecification.InstanceType") String instanceType);

   /**
    * Creates a Spot Instance request. Spot Instances are instances that Amazon EC2 starts on your
    * behalf when the maximum price that you specify exceeds the current Spot Price. Amazon EC2
    * periodically sets the Spot Price based on available Spot Instance capacity and current spot
    * instance requests. For conceptual information about Spot Instances, refer to the Amazon
    * Elastic Compute Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    * 
    * @param region
    *           Region where the spot instance service is running
    * @param spotPrice
    *           Specifies the maximum hourly price for any Spot Instance launched to fulfill the
    *           request.
    * @param instanceCount
    *           number of instances to request
    * @param launchSpec
    *           includes at least The AMI ID and instance type (ex. m1.small)
    * @param options
    *           options including expiration time or grouping
    * 
    * @see #describeSpotInstanceRequestsInRegion
    * @see #cancelSpotInstanceRequestsInRegion
    * @see #describeSpotPriceHistoryInRegion
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-RequestSpotInstances.html"
    *      />
    * @return set of spot instance requests
    */
   @Named("RequestSpotInstances")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RequestSpotInstances")
   @XMLResponseParser(SpotInstancesHandler.class)
   Set<SpotInstanceRequest> requestSpotInstancesInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("SpotPrice") float spotPrice, @FormParam("InstanceCount") int instanceCount,
         @BinderParam(BindLaunchSpecificationToFormParams.class) LaunchSpecification launchSpec,
         RequestSpotInstancesOptions... options);

   /**
    * 
    * Describes Spot Price history. Spot Instances are instances that Amazon EC2 starts on your
    * behalf when the maximum price that you specify exceeds the current Spot Price. Amazon EC2
    * periodically sets the Spot Price based on available Spot Instance capacity and current spot
    * instance requests. For conceptual information about Spot Instances, refer to the Amazon
    * Elastic Compute Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    * 
    * @param region
    *           Region where the spot instance service is running
    * @param options
    *           options to control the list
    * 
    * @see #describeSpotInstanceRequestsInRegion
    * @see #requestSpotInstancesInRegion
    * @see #cancelSpotInstanceRequestsInRegion
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSpotInstanceRequests.html"
    *      />
    * @return TODO
    */
   @Named("DescribeSpotPriceHistory")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSpotPriceHistory")
   @XMLResponseParser(DescribeSpotPriceHistoryResponseHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Spot> describeSpotPriceHistoryInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         DescribeSpotPriceHistoryOptions... options);

   /**
    * Cancels one or more Spot Instance requests. Spot Instances are instances that Amazon EC2
    * starts on your behalf when the maximum price that you specify exceeds the current Spot Price.
    * Amazon EC2 periodically sets the Spot Price based on available Spot Instance capacity and
    * current spot instance requests. For conceptual information about Spot Instances, refer to the
    * Amazon Elastic Compute Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    * 
    * @param region
    *           Region where the spot instance service is running
    * @param requestIds
    *           Specifies the ID of the Spot Instance request.
    * 
    * @see #describeSpotInstanceRequestsInRegion
    * @see #requestSpotInstancesInRegion
    * @see #describeSpotPriceHistoryInRegion
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CancelSpotInstanceRequests.html"
    *      />
    * @return TODO
    */
   @Named("CancelSpotInstanceRequests")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CancelSpotInstanceRequests")
   @Fallback(VoidOnNotFoundOr404.class)
   void cancelSpotInstanceRequestsInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @BinderParam(BindSpotInstanceRequestIdsToIndexedFormParams.class) String... requestIds);

}
