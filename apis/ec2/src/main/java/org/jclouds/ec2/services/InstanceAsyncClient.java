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
import org.jclouds.ec2.binders.BindBlockDeviceMappingToIndexedFormParams;
import org.jclouds.ec2.binders.BindInstanceIdsToIndexedFormParams;
import org.jclouds.ec2.binders.IfNotNullBindAvailabilityZoneToFormParam;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.InstanceStateChange;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.Volume.InstanceInitiatedShutdownBehavior;
import org.jclouds.ec2.functions.ConvertUnencodedBytesToBase64EncodedString;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.xml.BlockDeviceMappingHandler;
import org.jclouds.ec2.xml.BooleanValueHandler;
import org.jclouds.ec2.xml.DescribeInstancesResponseHandler;
import org.jclouds.ec2.xml.GetConsoleOutputResponseHandler;
import org.jclouds.ec2.xml.InstanceInitiatedShutdownBehaviorHandler;
import org.jclouds.ec2.xml.InstanceStateChangeHandler;
import org.jclouds.ec2.xml.InstanceTypeHandler;
import org.jclouds.ec2.xml.RunInstancesResponseHandler;
import org.jclouds.ec2.xml.StringValueHandler;
import org.jclouds.ec2.xml.UnencodeStringValueHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to EC2 Instance Services via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface InstanceAsyncClient {

   /**
    * @see InstanceClient#describeInstancesInRegion
    */
   @Named("DescribeInstances")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeInstances")
   @XMLResponseParser(DescribeInstancesResponseHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<? extends Reservation<? extends RunningInstance>>> describeInstancesInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see InstanceClient#runInstancesInRegion
    */
   @Named("RunInstances")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RunInstances")
   @XMLResponseParser(RunInstancesResponseHandler.class)
   ListenableFuture<? extends Reservation<? extends RunningInstance>> runInstancesInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @Nullable @BinderParam(IfNotNullBindAvailabilityZoneToFormParam.class) String nullableAvailabilityZone,
         @FormParam("ImageId") String imageId, @FormParam("MinCount") int minCount,
         @FormParam("MaxCount") int maxCount, RunInstancesOptions... options);

   /**
    * @see InstanceClient#rebootInstancesInRegion
    */
   @Named("RebootInstances")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RebootInstances")
   ListenableFuture<Void> rebootInstancesInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see InstanceClient#terminateInstancesInRegion
    */
   @Named("TerminateInstances")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "TerminateInstances")
   @XMLResponseParser(InstanceStateChangeHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<? extends InstanceStateChange>> terminateInstancesInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see InstanceClient#stopInstancesInRegion
    */
   @Named("StopInstances")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "StopInstances")
   @XMLResponseParser(InstanceStateChangeHandler.class)
   ListenableFuture<Set<? extends InstanceStateChange>> stopInstancesInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("Force") boolean force,
         @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see InstanceClient#startInstancesInRegion
    */
   @Named("StartInstances")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "StartInstances")
   @XMLResponseParser(InstanceStateChangeHandler.class)
   ListenableFuture<Set<? extends InstanceStateChange>> startInstancesInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see AMIClient#getUserDataForInstanceInRegion
    */
   @Named("DescribeInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "userData" })
   @XMLResponseParser(UnencodeStringValueHandler.class)
   ListenableFuture<String> getUserDataForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#getRootDeviceNameForInstanceInRegion
    */
   @Named("DescribeInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "rootDeviceName" })
   @XMLResponseParser(StringValueHandler.class)
   ListenableFuture<String> getRootDeviceNameForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#getRamdiskForInstanceInRegion
    */
   @Named("DescribeInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "ramdisk" })
   @XMLResponseParser(StringValueHandler.class)
   ListenableFuture<String> getRamdiskForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#getKernelForInstanceInRegion
    */
   @Named("DescribeInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "kernel" })
   @XMLResponseParser(StringValueHandler.class)
   ListenableFuture<String> getKernelForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#isApiTerminationDisabledForInstanceInRegion
    */
   @Named("DescribeInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "disableApiTermination" })
   @XMLResponseParser(BooleanValueHandler.class)
   ListenableFuture<Boolean> isApiTerminationDisabledForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#getInstanceTypeForInstanceInRegion
    */
   @Named("DescribeInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "instanceType" })
   @XMLResponseParser(InstanceTypeHandler.class)
   ListenableFuture<String> getInstanceTypeForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#getInstanceInitiatedShutdownBehaviorForInstanceInRegion
    */
   @Named("DescribeInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute",
         "instanceInitiatedShutdownBehavior" })
   @XMLResponseParser(InstanceInitiatedShutdownBehaviorHandler.class)
   ListenableFuture<InstanceInitiatedShutdownBehavior> getInstanceInitiatedShutdownBehaviorForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId);

   /**
    * @see InstanceClient#getBlockDeviceMappingForInstanceInRegion
    */
   @Named("DescribeInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "blockDeviceMapping" })
   @XMLResponseParser(BlockDeviceMappingHandler.class)
   ListenableFuture<? extends Map<String, BlockDevice>> getBlockDeviceMappingForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#resetRamdiskForInstanceInRegion
    */
   @Named("ResetInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ResetInstanceAttribute", "ramdisk" })
   ListenableFuture<Void> resetRamdiskForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#resetKernelForInstanceInRegion
    */
   @Named("ResetInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ResetInstanceAttribute", "kernel" })
   ListenableFuture<Void> resetKernelForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#setUserDataForInstanceInRegion
    */
   @Named("ModifyInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ModifyInstanceAttribute", "userData" })
   ListenableFuture<Void> setUserDataForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId,
         @FormParam("Value") @ParamParser(ConvertUnencodedBytesToBase64EncodedString.class) byte[] unencodedData);

   /**
    * @see AMIClient#setRamdiskForInstanceInRegion
    */
   @Named("ModifyInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ModifyInstanceAttribute", "ramdisk" })
   ListenableFuture<Void> setRamdiskForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId, @FormParam("Value") String ramdisk);

   /**
    * @see AMIClient#setKernelForInstanceInRegion
    */
   @Named("ModifyInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ModifyInstanceAttribute", "kernel" })
   ListenableFuture<Void> setKernelForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId, @FormParam("Value") String kernel);

   /**
    * @see AMIClient#setApiTerminationDisabledForInstanceInRegion
    */
   @Named("ModifyInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ModifyInstanceAttribute", "disableApiTermination" })
   ListenableFuture<Void> setApiTerminationDisabledForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId, @FormParam("Value") boolean apiTerminationDisabled);

   /**
    * @see AMIClient#setInstanceTypeForInstanceInRegion
    */
   @Named("ModifyInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ModifyInstanceAttribute", "instanceType" })
   ListenableFuture<Void> setInstanceTypeForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId, @FormParam("Value") String instanceType);

   /**
    * @see AMIClient#setInstanceInitiatedShutdownBehaviorForInstanceInRegion
    */
   @Named("ModifyInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ModifyInstanceAttribute",
         "instanceInitiatedShutdownBehavior" })
   ListenableFuture<Void> setInstanceInitiatedShutdownBehaviorForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId,
         @FormParam("Value") InstanceInitiatedShutdownBehavior instanceInitiatedShutdownBehavior);

   /**
    * @see InstanceClient#setBlockDeviceMappingForInstanceInRegion
    */
   @Named("ModifyInstanceAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION }, values = { "ModifyInstanceAttribute" })
   ListenableFuture<Void> setBlockDeviceMappingForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId,
         @BinderParam(BindBlockDeviceMappingToIndexedFormParams.class) Map<String, BlockDevice> blockDeviceMapping);

   /**
    * @see InstanceClient#getConsoleOutputForInstanceInRegion(String, String)
    */
   @Named("GetConsoleOutput")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION }, values = { "GetConsoleOutput" })
   @XMLResponseParser(GetConsoleOutputResponseHandler.class)
   ListenableFuture<String> getConsoleOutputForInstanceInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId);
}
