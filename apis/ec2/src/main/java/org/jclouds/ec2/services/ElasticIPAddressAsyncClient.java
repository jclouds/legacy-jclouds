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
package org.jclouds.ec2.services;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.binders.BindPublicIpsToIndexedFormParams;
import org.jclouds.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.ec2.xml.AllocateAddressResponseHandler;
import org.jclouds.ec2.xml.DescribeAddressesResponseHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to EC2 Elastic IP Addresses via REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface ElasticIPAddressAsyncClient {

   /**
    * @see BaseEC2Client#allocateAddressInRegion
    */
   @Named("ec2:AllocateAddress")
   @POST
   @Path("/")
   @XMLResponseParser(AllocateAddressResponseHandler.class)
   @FormParams(keys = ACTION, values = "AllocateAddress")
   ListenableFuture<String> allocateAddressInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * @see BaseEC2Client#associateAddressInRegion
    */
   @Named("ec2:AssociateAddress")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AssociateAddress")
   ListenableFuture<Void> associateAddressInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("PublicIp") String publicIp, @FormParam("InstanceId") String instanceId);

   /**
    * @see BaseEC2Client#disassociateAddressInRegion
    */
   @Named("ec2:DisassociateAddress")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DisassociateAddress")
   ListenableFuture<Void> disassociateAddressInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("PublicIp") String publicIp);

   /**
    * @see BaseEC2Client#releaseAddressInRegion
    */
   @Named("ec2:ReleaseAddress")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReleaseAddress")
   ListenableFuture<Void> releaseAddressInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("PublicIp") String publicIp);

   /**
    * @see BaseEC2Client#describeAddressesInRegion
    */
   @Named("ec2:DescribeAddresses")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeAddresses")
   @XMLResponseParser(DescribeAddressesResponseHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<PublicIpInstanceIdPair>> describeAddressesInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindPublicIpsToIndexedFormParams.class) String... publicIps);

}
