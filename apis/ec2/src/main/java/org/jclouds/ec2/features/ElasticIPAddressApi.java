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
package org.jclouds.ec2.features;

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

/**
 * Provides access to EC2 Elastic IP Addresses via REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface ElasticIPAddressApi {

   /**
    * Acquires an elastic IP address for use with your identity.
    * 
    * @param region
    *           Elastic IP addresses are tied to a Region and cannot be mapped across Regions.
    * @see #describeAddresses
    * @see #releaseAddress
    * @see #associateAddress
    * @see #disassociateAddress
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-AllocateAddress.html"
    */
   @Named("AllocateAddress")
   @POST
   @Path("/")
   @XMLResponseParser(AllocateAddressResponseHandler.class)
   @FormParams(keys = ACTION, values = "AllocateAddress")
   String allocateAddressInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Associates an elastic IP address with an instance. If the IP address is currently assigned to
    * another instance, the IP address is assigned to the new instance. This is an idempotent
    * operation. If you enter it more than once, Amazon EC2 does not return an error.
    * 
    * @param region
    *           Elastic IP addresses are tied to a Region and cannot be mapped across Regions.
    * @param publicIp
    *           IP address that you are assigning to the instance.
    * @param instanceId
    *           The instance to associate with the IP address.
    * 
    * @see #allocateAddress
    * @see #describeAddresses
    * @see #releaseAddress
    * @see #disassociateAddress
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-query-AssociateAddress.html"
    */
   @Named("AssociateAddress")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AssociateAddress")
   void associateAddressInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("PublicIp") String publicIp, @FormParam("InstanceId") String instanceId);

   /**
    * Disassociates the specified elastic IP address from the instance to which it is assigned. This
    * is an idempotent operation. If you enter it more than once, Amazon EC2 does not return an
    * error.
    * 
    * @param region
    *           Elastic IP addresses are tied to a Region and cannot be mapped across Regions.
    * @param publicIp
    *           IP address that you are assigning to the instance.
    * 
    * @see #allocateAddress
    * @see #describeAddresses
    * @see #releaseAddress
    * @see #associateAddress
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-query-DisassociateAddress.html"
    */
   @Named("DisassociateAddress")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DisassociateAddress")
   void disassociateAddressInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("PublicIp") String publicIp);

   /**
    * Releases an elastic IP address associated with your identity.
    * 
    * @param region
    *           Elastic IP addresses are tied to a Region and cannot be mapped across Regions.
    * @param publicIp
    *           The IP address that you are releasing from your identity.
    * 
    * @see #allocateAddress
    * @see #describeAddresses
    * @see #associateAddress
    * @see #disassociateAddress
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-query-ReleaseAddress.html"
    */
   @Named("ReleaseAddress")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReleaseAddress")
   void releaseAddressInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("PublicIp") String publicIp);

   /**
    * Lists elastic IP addresses assigned to your identity or provides information about a specific
    * address.
    * 
    * @param region
    *           Elastic IP addresses are tied to a Region and cannot be mapped across Regions.
    * @param publicIps
    *           Elastic IP address to describe.
    * 
    * @throws AWSResponseException
    *            if the requested publicIp is not found
    * @see #allocateAddress
    * @see #releaseAddress
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeAddresses.html"
    *      />
    */
   @Named("DescribeAddresses")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeAddresses")
   @XMLResponseParser(DescribeAddressesResponseHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<PublicIpInstanceIdPair> describeAddressesInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindPublicIpsToIndexedFormParams.class) String... publicIps);

}
