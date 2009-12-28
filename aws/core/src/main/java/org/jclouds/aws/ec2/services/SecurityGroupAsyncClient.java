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

import java.util.SortedSet;
import java.util.concurrent.Future;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.binders.BindGroupNameToIndexedFormParams;
import org.jclouds.aws.ec2.binders.BindUserIdGroupPairToSourceSecurityGroupFormParams;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.SecurityGroup;
import org.jclouds.aws.ec2.domain.UserIdGroupPair;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.functions.RegionToEndpoint;
import org.jclouds.aws.ec2.functions.ReturnVoidOnGroupNotFound;
import org.jclouds.aws.ec2.xml.DescribeSecurityGroupsResponseHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to EC2 via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Endpoint(EC2.class)
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = "2009-11-30")
@VirtualHost
public interface SecurityGroupAsyncClient {

   /**
    * @see BaseEC2Client#createSecurityGroupInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateSecurityGroup")
   Future<Void> createSecurityGroupInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @FormParam("GroupName") String name, @FormParam("GroupDescription") String description);

   /**
    * @see BaseEC2Client#deleteSecurityGroupInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteSecurityGroup")
   @ExceptionParser(ReturnVoidOnGroupNotFound.class)
   Future<Void> deleteSecurityGroupInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @FormParam("GroupName") String name);

   /**
    * @see BaseEC2Client#describeSecurityGroupsInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSecurityGroups")
   @XMLResponseParser(DescribeSecurityGroupsResponseHandler.class)
   Future<? extends SortedSet<SecurityGroup>> describeSecurityGroupsInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @BinderParam(BindGroupNameToIndexedFormParams.class) String... securityGroupNames);

   /**
    * @see BaseEC2Client#authorizeSecurityGroupIngressInRegion(Region, String,UserIdGroupPair)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AuthorizeSecurityGroupIngress")
   Future<Void> authorizeSecurityGroupIngressInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @FormParam("GroupName") String groupName,
            @BinderParam(BindUserIdGroupPairToSourceSecurityGroupFormParams.class) UserIdGroupPair sourceSecurityGroup);

   /**
    * @see BaseEC2Client#authorizeSecurityGroupIngressInRegion(Region,
    *      String,IpProtocol,int,int,String)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AuthorizeSecurityGroupIngress")
   Future<Void> authorizeSecurityGroupIngressInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @FormParam("GroupName") String groupName,
            @FormParam("IpProtocol") IpProtocol ipProtocol, @FormParam("FromPort") int fromPort,
            @FormParam("ToPort") int toPort, @FormParam("CidrIp") String cidrIp);

   /**
    * @see BaseEC2Client#revokeSecurityGroupIngressInRegion(Region, String,UserIdGroupPair)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RevokeSecurityGroupIngress")
   Future<Void> revokeSecurityGroupIngressInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @FormParam("GroupName") String groupName,
            @BinderParam(BindUserIdGroupPairToSourceSecurityGroupFormParams.class) UserIdGroupPair sourceSecurityGroup);

   /**
    * @see BaseEC2Client#revokeSecurityGroupIngressInRegion(Region,
    *      String,IpProtocol,int,int,String)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RevokeSecurityGroupIngress")
   Future<Void> revokeSecurityGroupIngressInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @FormParam("GroupName") String groupName,
            @FormParam("IpProtocol") IpProtocol ipProtocol, @FormParam("FromPort") int fromPort,
            @FormParam("ToPort") int toPort, @FormParam("CidrIp") String cidrIp);
}
