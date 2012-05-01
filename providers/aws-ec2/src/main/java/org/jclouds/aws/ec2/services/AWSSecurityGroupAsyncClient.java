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

import org.jclouds.aws.ec2.options.CreateSecurityGroupOptions;
import org.jclouds.aws.ec2.xml.CreateSecurityGroupResponseHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.binders.BindGroupIdsToIndexedFormParams;
import org.jclouds.ec2.binders.BindIpPermissionToIndexedFormParams;
import org.jclouds.ec2.binders.BindIpPermissionsToIndexedFormParams;
import org.jclouds.ec2.domain.IpPermission;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.services.SecurityGroupAsyncClient;
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

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to EC2 SecurityGroup Services via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
@Beta
public interface AWSSecurityGroupAsyncClient extends SecurityGroupAsyncClient {
   /**
    * @see AWSSecurityGroupClient#createSecurityGroupInRegion
    */
   @POST
   @Path("/")
   @XMLResponseParser(CreateSecurityGroupResponseHandler.class)
   @FormParams(keys = ACTION, values = "CreateSecurityGroup")
   ListenableFuture<String> createSecurityGroupInRegionAndReturnId(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("GroupName") String name, @FormParam("GroupDescription") String description,
         CreateSecurityGroupOptions... options);

   /**
    * @see AWSSecurityGroupClient#authorizeSecurityGroupIngressInRegion(String,
    *      String,IpPermission)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AuthorizeSecurityGroupIngress")
   ListenableFuture<Void> authorizeSecurityGroupIngressInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("GroupId") String groupId, @BinderParam(BindIpPermissionToIndexedFormParams.class) IpPermission perm);

   /**
    * @see AWSSecurityGroupClient#authorizeSecurityGroupIngressInRegion(String,
    *      String,Iterable)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AuthorizeSecurityGroupIngress")
   ListenableFuture<Void> authorizeSecurityGroupIngressInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("GroupId") String groupId,
         @BinderParam(BindIpPermissionsToIndexedFormParams.class) Iterable<IpPermission> perms);

   /**
    * @see AWSSecurityGroupClient#revokeSecurityGroupIngressInRegion(@Nullable
    *      Region, String,IpPermission)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RevokeSecurityGroupIngress")
   ListenableFuture<Void> revokeSecurityGroupIngressInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("GroupId") String groupId, @BinderParam(BindIpPermissionToIndexedFormParams.class) IpPermission perm);

   /**
    * @see AWSSecurityGroupClient#revokeSecurityGroupIngressInRegion(@Nullable
    *      Region, String,Iterable)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RevokeSecurityGroupIngress")
   ListenableFuture<Void> revokeSecurityGroupIngressInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("GroupId") String groupId,
         @BinderParam(BindIpPermissionsToIndexedFormParams.class) Iterable<IpPermission> perms);

   /**
    * @see AWSSecurityGroupClient#deleteSecurityGroupInRegionById
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteSecurityGroup")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteSecurityGroupInRegionById(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region, @FormParam("GroupId") String name);

   /**
    * @see AWSSecurityGroupClient#describeSecurityGroupsInRegionById
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSecurityGroups")
   @XMLResponseParser(DescribeSecurityGroupsResponseHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<SecurityGroup>> describeSecurityGroupsInRegionById(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindGroupIdsToIndexedFormParams.class) String... securityGroupNames);

}
