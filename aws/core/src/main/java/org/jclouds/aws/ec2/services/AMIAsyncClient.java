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

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.binders.BindProductCodesToIndexedFormParams;
import org.jclouds.aws.ec2.binders.BindUserGroupsToIndexedFormParams;
import org.jclouds.aws.ec2.binders.BindUserIdsToIndexedFormParams;
import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.Permission;
import org.jclouds.aws.ec2.domain.Image.EbsBlockDevice;
import org.jclouds.aws.ec2.functions.RegionToEndpoint;
import org.jclouds.aws.ec2.options.CreateImageOptions;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.aws.ec2.options.RegisterImageBackedByEbsOptions;
import org.jclouds.aws.ec2.options.RegisterImageOptions;
import org.jclouds.aws.ec2.xml.BlockDeviceMappingHandler;
import org.jclouds.aws.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.aws.ec2.xml.ImageIdHandler;
import org.jclouds.aws.ec2.xml.PermissionHandler;
import org.jclouds.aws.ec2.xml.ProductCodesHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
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
@FormParams(keys = VERSION, values = "2009-11-30")
@VirtualHost
public interface AMIAsyncClient {

   /**
    * @see AMIClient#describeImagesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeImages")
   @XMLResponseParser(DescribeImagesResponseHandler.class)
   ListenableFuture<? extends Set<Image>> describeImagesInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable Region region,
            DescribeImagesOptions... options);

   /**
    * @see AMIClient#createImageInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateImage")
   @XMLResponseParser(ImageIdHandler.class)
   ListenableFuture<String> createImageInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable Region region,
            @FormParam("Name") String name, @FormParam("InstanceId") String instanceId,
            CreateImageOptions... options);

   /**
    * @see AMIClient#deregisterImageInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeregisterImage")
   ListenableFuture<Void> deregisterImageInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable Region region,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#registerImageFromManifestInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RegisterImage")
   @XMLResponseParser(ImageIdHandler.class)
   ListenableFuture<String> registerImageFromManifestInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable Region region,
            @FormParam("Name") String imageName, @FormParam("ImageLocation") String pathToManifest,
            RegisterImageOptions... options);

   /**
    * @see AMIClient#registerUnixImageBackedByEbsInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "RootDeviceName", "BlockDeviceMapping.0.DeviceName" }, values = {
            "RegisterImage", "/dev/sda1", "/dev/sda1" })
   @XMLResponseParser(ImageIdHandler.class)
   ListenableFuture<String> registerUnixImageBackedByEbsInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable Region region,
            @FormParam("Name") String imageName,
            @FormParam("BlockDeviceMapping.0.Ebs.SnapshotId") String ebsSnapshotId,
            RegisterImageBackedByEbsOptions... options);

   /**
    * @see AMIClient#resetLaunchPermissionsOnImageInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ResetImageAttribute", "launchPermission" })
   ListenableFuture<Void> resetLaunchPermissionsOnImageInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable Region region,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#addLaunchPermissionsToImageInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute",
            "add", "launchPermission" })
   ListenableFuture<Void> addLaunchPermissionsToImageInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable Region region,
            @BinderParam(BindUserIdsToIndexedFormParams.class) Iterable<String> userIds,
            @BinderParam(BindUserGroupsToIndexedFormParams.class) Iterable<String> userGroups,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#removeLaunchPermissionsToImageInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute",
            "remove", "launchPermission" })
   ListenableFuture<Void> removeLaunchPermissionsFromImageInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable Region region,
            @BinderParam(BindUserIdsToIndexedFormParams.class) Iterable<String> userIds,
            @BinderParam(BindUserGroupsToIndexedFormParams.class) Iterable<String> userGroups,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#getLaunchPermissionForImageInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeImageAttribute",
            "launchPermission" })
   @XMLResponseParser(PermissionHandler.class)
   ListenableFuture<Permission> getLaunchPermissionForImageInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable Region region,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#getProductCodesForImageInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeImageAttribute", "productCodes" })
   @XMLResponseParser(ProductCodesHandler.class)
   ListenableFuture<? extends Set<String>> getProductCodesForImageInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable Region region,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#getBlockDeviceMappingsForImageInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeImageAttribute",
            "blockDeviceMapping" })
   @XMLResponseParser(BlockDeviceMappingHandler.class)
   ListenableFuture<? extends Map<String, EbsBlockDevice>> getBlockDeviceMappingsForImageInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable Region region,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#addProductCodesToImageInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute",
            "add", "productCodes" })
   ListenableFuture<Void> addProductCodesToImageInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable Region region,
            @BinderParam(BindProductCodesToIndexedFormParams.class) Iterable<String> productCodes,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#removeProductCodesToImageInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute",
            "remove", "productCodes" })
   ListenableFuture<Void> removeProductCodesFromImageInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable Region region,
            @BinderParam(BindProductCodesToIndexedFormParams.class) Iterable<String> productCodes,
            @FormParam("ImageId") String imageId);
}
