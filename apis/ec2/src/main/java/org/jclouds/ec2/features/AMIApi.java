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

/**
 * Provides access to AMI Services.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface AMIApi {

   /**
    * Returns information about AMIs, AKIs, and ARIs. This includes image type, product codes,
    * architecture, and kernel and RAM disk IDs. Images available to you include public images,
    * private images that you own, and private images owned by other users for which you have
    * explicit launch permissions.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @see InstanceApi#describeInstances
    * @see #describeImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImages.html"
    *      />
    * @see DescribeImagesOptions
    */
   @Named("DescribeImages")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeImages")
   @XMLResponseParser(DescribeImagesResponseHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<? extends Image> describeImagesInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            DescribeImagesOptions... options);

   /**
    * Creates an AMI that uses an Amazon EBS root device from a "running" or "stopped" instance.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param name
    *           The name of the AMI that was provided during image creation. 3-128 alphanumeric
    *           characters, parenthesis (()), commas (,), slashes (/), dashes (-), or underscores(_)
    * @param instanceId
    *           The ID of the instance.
    * @return imageId
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImages.html"
    *      />
    * @see CreateImageOptions
    * @see InstanceApi#runInstances
    * @see InstanceApi#describeInstances
    * @see InstanceApi#terminateInstances
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateImage.html"
    *      />
    */
   @Named("CreateImage")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateImage")
   @XMLResponseParser(ImageIdHandler.class)
   String createImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("Name") String name, @FormParam("InstanceId") String instanceId, CreateImageOptions... options);

   /**
    * 
    * Deregisters the specified AMI. Once deregistered, the AMI cannot be used to launch new
    * instances.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param imageId
    *           Unique ID of the AMI which was assigned during registration. To register an AMI, use
    *           RegisterImage. To view the AMI IDs of AMIs that belong to your identity. use
    *           DescribeImages.
    * 
    * @see #describeImages
    * @see #registerImage
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeregisterImage.html"
    *      />
    */
   @Named("DeregisterImage")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeregisterImage")
   void deregisterImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("ImageId") String imageId);

   /**
    * Registers an AMI with Amazon EC2. Images must be registered before they can be launched. To
    * launch instances, use the {@link InstanceApi#runInstances} operation.
    * <p/>
    * Each AMI is associated with an unique ID which is provided by the Amazon EC2 service through
    * this operation. If needed, you can deregister an AMI at any time.
    * <p/>
    * <h3>Note</h3> Any modifications to an AMI backed by Amazon S3 invalidates this registration.
    * If you make changes to an image, deregister the previous image and register the new image.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param name
    *           The name of the AMI that was provided during image creation. 3-128 alphanumeric
    *           characters, parenthesis (()), commas (,), slashes (/), dashes (-), or underscores(_)
    * @param pathToManifest
    *           Full path to your AMI manifest in Amazon S3 storage.
    * @param options
    *           Options to specify metadata such as architecture or secondary volumes to be
    *           associated with this image.
    * @return imageId
    * 
    * @see #describeImages
    * @see #deregisterImage
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-RegisterImage.html"
    *      />
    */
   @Named("RegisterImage")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RegisterImage")
   @XMLResponseParser(ImageIdHandler.class)
   String registerImageFromManifestInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("Name") String imageName, @FormParam("ImageLocation") String pathToManifest,
            RegisterImageOptions... options);

   /**
    * Registers an AMI with Amazon EC2. Images must be registered before they can be launched. To
    * launch instances, use the {@link InstanceApi#runInstances} operation. The root device name
    * is /dev/sda1
    * <p/>
    * Each AMI is associated with an unique ID which is provided by the Amazon EC2 service through
    * this operation. If needed, you can deregister an AMI at any time.
    * <p/>
    * <h3>Note</h3> AMIs backed by Amazon EBS are automatically registered when you create the
    * image. However, you can use this to register a snapshot of an instance backed by Amazon EBS.
    * <p/>
    * Amazon EBS snapshots are not guaranteed to be bootable.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param name
    *           The name of the AMI that was provided during image creation. 3-128 alphanumeric
    *           characters, parenthesis (()), commas (,), slashes (/), dashes (-), or underscores(_)
    * 
    * @param ebsSnapshotId
    *           The id of the root snapshot (e.g., snap-6eba6e06).
    * @param options
    *           Options to specify metadata such as architecture or secondary volumes to be
    *           associated with this image.
    * @return imageId
    * 
    * @see #describeImages
    * @see #deregisterImage
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-RegisterImage.html"
    *      />
    */
   @Named("RegisterImage")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "RootDeviceName", "BlockDeviceMapping.0.DeviceName" }, values = { "RegisterImage",
            "/dev/sda1", "/dev/sda1" })
   @XMLResponseParser(ImageIdHandler.class)
   String registerUnixImageBackedByEbsInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("Name") String imageName,
            @FormParam("BlockDeviceMapping.0.Ebs.SnapshotId") String ebsSnapshotId,
            RegisterImageBackedByEbsOptions... options);

   /**
    * Resets the {@code launchPermission}s on an AMI.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param imageId
    *           ID of the AMI on which the attribute will be reset.
    * 
    * @see #addLaunchPermissionsToImage
    * @see #describeImageAttribute
    * @see #removeProductCodesFromImage
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ResetImageAttribute.html"
    *      />
    */
   @Named("ResetImageAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ResetImageAttribute", "launchPermission" })
   void resetLaunchPermissionsOnImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("ImageId") String imageId);

   /**
    * Adds {@code launchPermission}s to an AMI.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param userIds
    *           AWS Access Key ID.
    * @param userGroups
    *           Name of the groups. Currently supports \"all.\""
    * @param imageId
    *           The AMI ID.
    * 
    * @see #removeLaunchPermissionsFromImage
    * @see #describeImageAttribute
    * @see #resetImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyImageAttribute.html"
    *      />
    */
   @Named("ModifyImageAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute", "add",
            "launchPermission" })
   void addLaunchPermissionsToImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindUserIdsToIndexedFormParams.class) Iterable<String> userIds,
            @BinderParam(BindUserGroupsToIndexedFormParams.class) Iterable<String> userGroups,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIApi#removeLaunchPermissionsToImageInRegion
    */
   @Named("ModifyImageAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute", "remove",
            "launchPermission" })
   void removeLaunchPermissionsFromImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindUserIdsToIndexedFormParams.class) Iterable<String> userIds,
            @BinderParam(BindUserGroupsToIndexedFormParams.class) Iterable<String> userGroups,
            @FormParam("ImageId") String imageId);

   /**
    * Returns the {@link Permission}s of an image.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param imageId
    *           The ID of the AMI for which an attribute will be described
    * @see #describeImages
    * @see #modifyImageAttribute
    * @see #resetImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImageAttribute.html"
    *      />
    * @see DescribeImagesOptions
    */
   @Named("DescribeImageAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeImageAttribute", "launchPermission" })
   @XMLResponseParser(PermissionHandler.class)
   Permission getLaunchPermissionForImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("ImageId") String imageId);

   /**
    * Returns a map of device name to block device for the image.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param imageId
    *           The ID of the AMI for which an attribute will be described
    * @see #describeImages
    * @see #modifyImageAttribute
    * @see #resetImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImageAttribute.html"
    *      />
    * @see DescribeImagesOptions
    */
   @Named("DescribeImageAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeImageAttribute", "blockDeviceMapping" })
   @XMLResponseParser(BlockDeviceMappingHandler.class)
   Map<String, EbsBlockDevice> getBlockDeviceMappingsForImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("ImageId") String imageId);
}
