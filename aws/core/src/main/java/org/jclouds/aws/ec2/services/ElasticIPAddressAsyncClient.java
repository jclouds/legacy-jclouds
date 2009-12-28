/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.services;

import static org.jclouds.aws.ec2.reference.EC2Parameters.ACTION;
import static org.jclouds.aws.ec2.reference.EC2Parameters.VERSION;

import java.net.InetAddress;
import java.util.Set;
import java.util.concurrent.Future;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.binders.BindInetAddressesToIndexedFormParams;
import org.jclouds.aws.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.functions.RegionToEndpoint;
import org.jclouds.aws.ec2.xml.AllocateAddressResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeAddressesResponseHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.InetAddressToHostAddress;

/**
 * Provides access to EC2 Elastic IP Addresses via REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Endpoint(EC2.class)
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = "2009-11-30")
@VirtualHost
public interface ElasticIPAddressAsyncClient {

   /**
    * @see BaseEC2Client#allocateAddressInRegion
    */
   @POST
   @Path("/")
   @XMLResponseParser(AllocateAddressResponseHandler.class)
   @FormParams(keys = ACTION, values = "AllocateAddress")
   Future<InetAddress> allocateAddressInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region);

   /**
    * @see BaseEC2Client#associateAddressInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AssociateAddress")
   Future<Void> associateAddressInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @FormParam("PublicIp") @ParamParser(InetAddressToHostAddress.class) InetAddress publicIp,
            @FormParam("InstanceId") String instanceId);

   /**
    * @see BaseEC2Client#disassociateAddressInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DisassociateAddress")
   Future<Void> disassociateAddressInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @FormParam("PublicIp") @ParamParser(InetAddressToHostAddress.class) InetAddress publicIp);

   /**
    * @see BaseEC2Client#releaseAddressInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReleaseAddress")
   Future<Void> releaseAddressInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @FormParam("PublicIp") @ParamParser(InetAddressToHostAddress.class) InetAddress publicIp);

   /**
    * @see BaseEC2Client#describeAddressesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeAddresses")
   @XMLResponseParser(DescribeAddressesResponseHandler.class)
   Future<? extends Set<PublicIpInstanceIdPair>> describeAddressesInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @BinderParam(BindInetAddressesToIndexedFormParams.class) InetAddress... publicIps);

}
