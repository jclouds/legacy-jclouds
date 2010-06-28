/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.jclouds.aws.ec2.reference.EC2Parameters.ACTION;
import static org.jclouds.aws.ec2.reference.EC2Parameters.VERSION;

import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.binders.BindPublicIpsToIndexedFormParams;
import org.jclouds.aws.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.aws.ec2.xml.AllocateAddressResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeAddressesResponseHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.functions.RegionToEndpoint;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
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
@FormParams(keys = VERSION, values = EC2AsyncClient.VERSION)
@VirtualHost
public interface ElasticIPAddressAsyncClient {

   /**
    * @see BaseEC2Client#allocateAddressInRegion
    */
   @POST
   @Path("/")
   @XMLResponseParser(AllocateAddressResponseHandler.class)
   @FormParams(keys = ACTION, values = "AllocateAddress")
   ListenableFuture<String> allocateAddressInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * @see BaseEC2Client#associateAddressInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AssociateAddress")
   ListenableFuture<Void> associateAddressInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("PublicIp") String publicIp, @FormParam("InstanceId") String instanceId);

   /**
    * @see BaseEC2Client#disassociateAddressInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DisassociateAddress")
   ListenableFuture<Void> disassociateAddressInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("PublicIp") String publicIp);

   /**
    * @see BaseEC2Client#releaseAddressInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReleaseAddress")
   ListenableFuture<Void> releaseAddressInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("PublicIp") String publicIp);

   /**
    * @see BaseEC2Client#describeAddressesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeAddresses")
   @XMLResponseParser(DescribeAddressesResponseHandler.class)
   ListenableFuture<? extends Set<PublicIpInstanceIdPair>> describeAddressesInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @BinderParam(BindPublicIpsToIndexedFormParams.class) String... publicIps);

}
