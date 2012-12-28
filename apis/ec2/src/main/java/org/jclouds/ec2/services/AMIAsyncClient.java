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

import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.binders.BindUserGroupsToIndexedFormParams;
import org.jclouds.ec2.binders.BindUserIdsToIndexedFormParams;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Image.EbsBlockDevice;
import org.jclouds.ec2.domain.Permission;
import org.jclouds.ec2.options.CreateImageOptions;
import org.jclouds.ec2.options.DescribeImagesOptions;
import org.jclouds.ec2.options.RegisterImageBackedByEbsOptions;
import org.jclouds.ec2.options.RegisterImageOptions;
import org.jclouds.ec2.xml.BlockDeviceMappingHandler;
import org.jclouds.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.ec2.xml.ImageIdHandler;
import org.jclouds.ec2.xml.PermissionHandler;
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
 * Provides access to AMI Services.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface AMIAsyncClient {

   /**
    * @see AMIClient#describeImagesInRegion
    */
   @Named("ec2:DescribeImages")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeImages")
   @XMLResponseParser(DescribeImagesResponseHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<? extends Image>> describeImagesInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            DescribeImagesOptions... options);

   /**
    * @see AMIClient#createImageInRegion
    */
   @Named("ec2:CreateImage")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateImage")
   @XMLResponseParser(ImageIdHandler.class)
   ListenableFuture<String> createImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("Name") String name, @FormParam("InstanceId") String instanceId, CreateImageOptions... options);

   /**
    * @see AMIClient#deregisterImageInRegion
    */
   @Named("ec2:DeregisterImage")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeregisterImage")
   ListenableFuture<Void> deregisterImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#registerImageFromManifestInRegion
    */
   @Named("ec2:RegisterImage")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RegisterImage")
   @XMLResponseParser(ImageIdHandler.class)
   ListenableFuture<String> registerImageFromManifestInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("Name") String imageName, @FormParam("ImageLocation") String pathToManifest,
            RegisterImageOptions... options);

   /**
    * @see AMIClient#registerUnixImageBackedByEbsInRegion
    */
   @Named("ec2:RegisterImage")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "RootDeviceName", "BlockDeviceMapping.0.DeviceName" }, values = { "RegisterImage",
            "/dev/sda1", "/dev/sda1" })
   @XMLResponseParser(ImageIdHandler.class)
   ListenableFuture<String> registerUnixImageBackedByEbsInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("Name") String imageName,
            @FormParam("BlockDeviceMapping.0.Ebs.SnapshotId") String ebsSnapshotId,
            RegisterImageBackedByEbsOptions... options);

   /**
    * @see AMIClient#resetLaunchPermissionsOnImageInRegion
    */
   @Named("ec2:ResetImageAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ResetImageAttribute", "launchPermission" })
   ListenableFuture<Void> resetLaunchPermissionsOnImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#addLaunchPermissionsToImageInRegion
    */
   @Named("ec2:ModifyImageAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute", "add",
            "launchPermission" })
   ListenableFuture<Void> addLaunchPermissionsToImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindUserIdsToIndexedFormParams.class) Iterable<String> userIds,
            @BinderParam(BindUserGroupsToIndexedFormParams.class) Iterable<String> userGroups,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#removeLaunchPermissionsToImageInRegion
    */
   @Named("ec2:ModifyImageAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute", "remove",
            "launchPermission" })
   ListenableFuture<Void> removeLaunchPermissionsFromImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindUserIdsToIndexedFormParams.class) Iterable<String> userIds,
            @BinderParam(BindUserGroupsToIndexedFormParams.class) Iterable<String> userGroups,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#getLaunchPermissionForImageInRegion
    */
   @Named("ec2:DescribeImageAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeImageAttribute", "launchPermission" })
   @XMLResponseParser(PermissionHandler.class)
   ListenableFuture<Permission> getLaunchPermissionForImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#getBlockDeviceMappingsForImageInRegion
    */
   @Named("ec2:DescribeImageAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeImageAttribute", "blockDeviceMapping" })
   @XMLResponseParser(BlockDeviceMappingHandler.class)
   ListenableFuture<Map<String, EbsBlockDevice>> getBlockDeviceMappingsForImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("ImageId") String imageId);
}
