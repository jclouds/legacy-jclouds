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

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.binders.BindGroupNamesToIndexedFormParams;
import org.jclouds.ec2.binders.BindUserIdGroupPairToSourceSecurityGroupFormParams;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.domain.UserIdGroupPair;
import org.jclouds.ec2.xml.DescribeSecurityGroupsResponseHandler;
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
 * Provides access to EC2 via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface SecurityGroupAsyncClient {

   /**
    * @see SecurityGroupClient#createSecurityGroupInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateSecurityGroup")
   ListenableFuture<Void> createSecurityGroupInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("GroupName") String name, @FormParam("GroupDescription") String description);

   /**
    * @see SecurityGroupClient#deleteSecurityGroupInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteSecurityGroup")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteSecurityGroupInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region, @FormParam("GroupName") String name);

   /**
    * @see SecurityGroupClient#describeSecurityGroupsInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSecurityGroups")
   @XMLResponseParser(DescribeSecurityGroupsResponseHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<SecurityGroup>> describeSecurityGroupsInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindGroupNamesToIndexedFormParams.class) String... securityGroupNames);

   /**
    * @see SecurityGroupClient#authorizeSecurityGroupIngressInRegion(@ org.jclouds.javax.annotation.Nullable Region,
    *      String,UserIdGroupPair)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AuthorizeSecurityGroupIngress")
   ListenableFuture<Void> authorizeSecurityGroupIngressInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("GroupName") String groupName,
            @BinderParam(BindUserIdGroupPairToSourceSecurityGroupFormParams.class) UserIdGroupPair sourceSecurityGroup);

   /**
    * @see SecurityGroupClient#authorizeSecurityGroupIngressInRegion(@ org.jclouds.javax.annotation.Nullable Region,
    *      String,IpProtocol,int,int,String)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AuthorizeSecurityGroupIngress")
   ListenableFuture<Void> authorizeSecurityGroupIngressInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("GroupName") String groupName, @FormParam("IpProtocol") IpProtocol ipProtocol,
            @FormParam("FromPort") int fromPort, @FormParam("ToPort") int toPort, @FormParam("CidrIp") String cidrIp);

   /**
    * @see SecurityGroupClient#revokeSecurityGroupIngressInRegion(@Nullable Region,
    *      String,UserIdGroupPair)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RevokeSecurityGroupIngress")
   ListenableFuture<Void> revokeSecurityGroupIngressInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("GroupName") String groupName,
            @BinderParam(BindUserIdGroupPairToSourceSecurityGroupFormParams.class) UserIdGroupPair sourceSecurityGroup);

   /**
    * @see SecurityGroupClient#revokeSecurityGroupIngressInRegion(@ org.jclouds.javax.annotation.Nullable Region,
    *      String,IpProtocol,int,int,String)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RevokeSecurityGroupIngress")
   ListenableFuture<Void> revokeSecurityGroupIngressInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("GroupName") String groupName, @FormParam("IpProtocol") IpProtocol ipProtocol,
            @FormParam("FromPort") int fromPort, @FormParam("ToPort") int toPort, @FormParam("CidrIp") String cidrIp);
}
