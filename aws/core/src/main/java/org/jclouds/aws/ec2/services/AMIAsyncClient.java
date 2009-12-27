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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.binders.BindProductCodesToIndexedFormParams;
import org.jclouds.aws.ec2.binders.BindUserGroupsToIndexedFormParams;
import org.jclouds.aws.ec2.binders.BindUserIdsToIndexedFormParams;
import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.LaunchPermission;
import org.jclouds.aws.ec2.domain.Image.EbsBlockDevice;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.options.CreateImageOptions;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.aws.ec2.options.RegisterImageBackedByEbsOptions;
import org.jclouds.aws.ec2.options.RegisterImageOptions;
import org.jclouds.aws.ec2.xml.BlockDeviceMappingHandler;
import org.jclouds.aws.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.aws.ec2.xml.ImageIdHandler;
import org.jclouds.aws.ec2.xml.LaunchPermissionHandler;
import org.jclouds.aws.ec2.xml.ProductCodesHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
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
@Endpoint(EC2.class)
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = "2009-11-30")
@VirtualHost
public interface AMIAsyncClient {

   /**
    * @see AMIClient#describeImages
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeImages")
   @XMLResponseParser(DescribeImagesResponseHandler.class)
   Future<? extends Set<Image>> describeImages(DescribeImagesOptions... options);

   /**
    * @see AMIClient#createImage
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateImage")
   @XMLResponseParser(ImageIdHandler.class)
   Future<String> createImage(@FormParam("Name") String name,
            @FormParam("InstanceId") String instanceId, CreateImageOptions... options);

   /**
    * @see AMIClient#deregisterImage
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeregisterImage")
   Future<Void> deregisterImage(@FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#registerImageFromManifest
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RegisterImage")
   @XMLResponseParser(ImageIdHandler.class)
   Future<String> registerImageFromManifest(@FormParam("Name") String imageName,
            @FormParam("ImageLocation") String pathToManifest, RegisterImageOptions... options);

   /**
    * @see AMIClient#registerImageBackedByEbs
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "RootDeviceName", "BlockDeviceMapping.0.DeviceName" }, values = {
            "RegisterImage", "/dev/sda1", "/dev/sda1" })
   @XMLResponseParser(ImageIdHandler.class)
   Future<String> registerImageBackedByEbs(@FormParam("Name") String imageName,
            @FormParam("BlockDeviceMapping.0.Ebs.SnapshotId") String ebsSnapshotId,
            RegisterImageBackedByEbsOptions... options);

   /**
    * @see AMIClient#resetLaunchPermissionsOnImage
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ResetImageAttribute", "launchPermission" })
   Future<Void> resetLaunchPermissionsOnImage(@FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#addLaunchPermissionsToImage
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute",
            "add", "launchPermission" })
   Future<Void> addLaunchPermissionsToImage(
            @BinderParam(BindUserIdsToIndexedFormParams.class) Iterable<String> userIds,
            @BinderParam(BindUserGroupsToIndexedFormParams.class) Iterable<String> userGroups,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#removeLaunchPermissionsToImage
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute",
            "remove", "launchPermission" })
   Future<Void> removeLaunchPermissionsFromImage(
            @BinderParam(BindUserIdsToIndexedFormParams.class) Iterable<String> userIds,
            @BinderParam(BindUserGroupsToIndexedFormParams.class) Iterable<String> userGroups,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#getLaunchPermissionForImage
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeImageAttribute",
            "launchPermission" })
   @XMLResponseParser(LaunchPermissionHandler.class)
   Future<LaunchPermission> getLaunchPermissionForImage(@FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#getProductCodesForImage
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeImageAttribute", "productCodes" })
   @XMLResponseParser(ProductCodesHandler.class)
   Future<? extends Set<String>> getProductCodesForImage(@FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#getBlockDeviceMappingsForImage
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeImageAttribute",
            "blockDeviceMapping" })
   @XMLResponseParser(BlockDeviceMappingHandler.class)
   Future<? extends Map<String, EbsBlockDevice>> getBlockDeviceMappingsForImage(
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#addProductCodesToImage
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute",
            "add", "productCodes" })
   Future<Void> addProductCodesToImage(
            @BinderParam(BindProductCodesToIndexedFormParams.class) Iterable<String> productCodes,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#removeProductCodesToImage
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute",
            "remove", "productCodes" })
   Future<Void> removeProductCodesFromImage(
            @BinderParam(BindProductCodesToIndexedFormParams.class) Iterable<String> productCodes,
            @FormParam("ImageId") String imageId);
}
