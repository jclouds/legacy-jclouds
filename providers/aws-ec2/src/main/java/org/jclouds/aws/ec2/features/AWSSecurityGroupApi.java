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
import org.jclouds.aws.ec2.options.CreateSecurityGroupOptions;
import org.jclouds.aws.ec2.xml.AWSEC2DescribeSecurityGroupsResponseHandler;
import org.jclouds.aws.ec2.xml.CreateSecurityGroupResponseHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.binders.BindGroupIdsToIndexedFormParams;
import org.jclouds.ec2.binders.BindGroupNamesToIndexedFormParams;
import org.jclouds.ec2.binders.BindIpPermissionToIndexedFormParams;
import org.jclouds.ec2.binders.BindIpPermissionsToIndexedFormParams;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.features.SecurityGroupApi;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.annotations.Beta;

/**
 * Provides access to EC2 SecurityGroup Services via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
@Beta
public interface AWSSecurityGroupApi extends SecurityGroupApi {

   @Named("CreateSecurityGroup")
   @POST
   @Path("/")
   @XMLResponseParser(CreateSecurityGroupResponseHandler.class)
   @FormParams(keys = ACTION, values = "CreateSecurityGroup")
   String createSecurityGroupInRegionAndReturnId(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("GroupName") String name, @FormParam("GroupDescription") String description,
         CreateSecurityGroupOptions... options);

   @Named("AuthorizeSecurityGroupIngress")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AuthorizeSecurityGroupIngress")
   void authorizeSecurityGroupIngressInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("GroupId") String groupId, @BinderParam(BindIpPermissionToIndexedFormParams.class) IpPermission perm);

   @Named("AuthorizeSecurityGroupIngress")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AuthorizeSecurityGroupIngress")
   void authorizeSecurityGroupIngressInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("GroupId") String groupId,
         @BinderParam(BindIpPermissionsToIndexedFormParams.class) Iterable<IpPermission> perms);

   @Named("RevokeSecurityGroupIngress")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RevokeSecurityGroupIngress")
   void revokeSecurityGroupIngressInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("GroupId") String groupId, @BinderParam(BindIpPermissionToIndexedFormParams.class) IpPermission perm);

   @Named("RevokeSecurityGroupIngress")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RevokeSecurityGroupIngress")
   void revokeSecurityGroupIngressInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("GroupId") String groupId,
         @BinderParam(BindIpPermissionsToIndexedFormParams.class) Iterable<IpPermission> perms);

   @Named("DeleteSecurityGroup")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteSecurityGroup")
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteSecurityGroupInRegionById(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region, @FormParam("GroupId") String name);

   @Named("DescribeSecurityGroups")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSecurityGroups")
   @XMLResponseParser(AWSEC2DescribeSecurityGroupsResponseHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<SecurityGroup> describeSecurityGroupsInRegionById(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindGroupIdsToIndexedFormParams.class) String... securityGroupNames);

   @Named("DescribeSecurityGroups")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSecurityGroups")
   @XMLResponseParser(AWSEC2DescribeSecurityGroupsResponseHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<SecurityGroup> describeSecurityGroupsInRegion(
           @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
           @BinderParam(BindGroupNamesToIndexedFormParams.class) String... securityGroupNames);
}
