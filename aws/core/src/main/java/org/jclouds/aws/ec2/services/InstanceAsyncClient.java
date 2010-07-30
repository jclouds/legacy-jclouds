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
import com.google.common.util.concurrent.ListenableFuture;

import javax.annotation.Nullable;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.binders.BindBlockDeviceMappingToIndexedFormParams;
import org.jclouds.aws.ec2.binders.BindInstanceIdsToIndexedFormParams;
import org.jclouds.aws.ec2.binders.IfNotNullBindAvailabilityZoneToFormParam;
import org.jclouds.aws.ec2.domain.BlockDeviceMapping;
import org.jclouds.aws.ec2.domain.InstanceStateChange;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.domain.Volume.InstanceInitiatedShutdownBehavior;
import org.jclouds.aws.ec2.functions.ConvertUnencodedBytesToBase64EncodedString;
import org.jclouds.aws.ec2.options.RunInstancesOptions;
import org.jclouds.aws.ec2.xml.BlockDeviceMappingHandler;
import org.jclouds.aws.ec2.xml.BooleanValueHandler;
import org.jclouds.aws.ec2.xml.DescribeInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.InstanceInitiatedShutdownBehaviorHandler;
import org.jclouds.aws.ec2.xml.InstanceStateChangeHandler;
import org.jclouds.aws.ec2.xml.InstanceTypeHandler;
import org.jclouds.aws.ec2.xml.RunInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.StringValueHandler;
import org.jclouds.aws.ec2.xml.UnencodeStringValueHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.functions.RegionToEndpoint;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;

/**
 * Provides access to EC2 Instance Services via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = EC2AsyncClient.VERSION)
@VirtualHost
public interface InstanceAsyncClient {

   /**
    * @see InstanceClient#describeInstancesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeInstances")
   @XMLResponseParser(DescribeInstancesResponseHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<? extends Reservation<? extends RunningInstance>>> describeInstancesInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see InstanceClient#runInstancesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RunInstances")
   @XMLResponseParser(RunInstancesResponseHandler.class)
   ListenableFuture<Reservation<? extends RunningInstance>> runInstancesInRegion(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @Nullable @BinderParam(IfNotNullBindAvailabilityZoneToFormParam.class) String nullableAvailabilityZone,
            @FormParam("ImageId") String imageId, @FormParam("MinCount") int minCount,
            @FormParam("MaxCount") int maxCount, RunInstancesOptions... options);

   /**
    * @see InstanceClient#rebootInstancesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RebootInstances")
   ListenableFuture<Void> rebootInstancesInRegion(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see InstanceClient#terminateInstancesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "TerminateInstances")
   @XMLResponseParser(InstanceStateChangeHandler.class)
   ListenableFuture<Set<? extends InstanceStateChange>> terminateInstancesInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see InstanceClient#stopInstancesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "StopInstances")
   @XMLResponseParser(InstanceStateChangeHandler.class)
   ListenableFuture<Set<? extends InstanceStateChange>> stopInstancesInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region, @FormParam("Force") boolean force,
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see InstanceClient#startInstancesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "StartInstances")
   @XMLResponseParser(InstanceStateChangeHandler.class)
   ListenableFuture<Set<? extends InstanceStateChange>> startInstancesInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see AMIClient#getUserDataForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "userData" })
   @XMLResponseParser(UnencodeStringValueHandler.class)
   ListenableFuture<String> getUserDataForInstanceInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#getRootDeviceNameForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "rootDeviceName" })
   @XMLResponseParser(StringValueHandler.class)
   ListenableFuture<String> getRootDeviceNameForInstanceInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#getRamdiskForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "ramdisk" })
   @XMLResponseParser(StringValueHandler.class)
   ListenableFuture<String> getRamdiskForInstanceInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#getKernelForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "kernel" })
   @XMLResponseParser(StringValueHandler.class)
   ListenableFuture<String> getKernelForInstanceInRegion(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#isApiTerminationDisabledForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "disableApiTermination" })
   @XMLResponseParser(BooleanValueHandler.class)
   ListenableFuture<Boolean> isApiTerminationDisabledForInstanceInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#getInstanceTypeForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "instanceType" })
   @XMLResponseParser(InstanceTypeHandler.class)
   ListenableFuture<String> getInstanceTypeForInstanceInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#getInstanceInitiatedShutdownBehaviorForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute",
            "instanceInitiatedShutdownBehavior" })
   @XMLResponseParser(InstanceInitiatedShutdownBehaviorHandler.class)
   ListenableFuture<InstanceInitiatedShutdownBehavior> getInstanceInitiatedShutdownBehaviorForInstanceInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId);

   /**
    * @see InstanceClient#getBlockDeviceMappingForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeInstanceAttribute", "blockDeviceMapping" })
   @XMLResponseParser(BlockDeviceMappingHandler.class)
   ListenableFuture<? extends Map<String, RunningInstance.EbsBlockDevice>> getBlockDeviceMappingForInstanceInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#resetRamdiskForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ResetInstanceAttribute", "ramdisk" })
   ListenableFuture<Void> resetRamdiskForInstanceInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#resetKernelForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ResetInstanceAttribute", "kernel" })
   ListenableFuture<Void> resetKernelForInstanceInRegion(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId);

   /**
    * @see AMIClient#setUserDataForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ModifyInstanceAttribute", "userData" })
   ListenableFuture<Void> setUserDataForInstanceInRegion(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId,
            @FormParam("Value") @ParamParser(ConvertUnencodedBytesToBase64EncodedString.class) byte[] unencodedData);

   /**
    * @see AMIClient#setRamdiskForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ModifyInstanceAttribute", "ramdisk" })
   ListenableFuture<Void> setRamdiskForInstanceInRegion(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId, @FormParam("Value") String ramdisk);

   /**
    * @see AMIClient#setKernelForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ModifyInstanceAttribute", "kernel" })
   ListenableFuture<Void> setKernelForInstanceInRegion(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId, @FormParam("Value") String kernel);

   /**
    * @see AMIClient#setApiTerminationDisabledForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ModifyInstanceAttribute", "disableApiTermination" })
   ListenableFuture<Void> setApiTerminationDisabledForInstanceInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId, @FormParam("Value") boolean apiTerminationDisabled);

   /**
    * @see AMIClient#setInstanceTypeForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ModifyInstanceAttribute", "instanceType" })
   ListenableFuture<Void> setInstanceTypeForInstanceInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId, @FormParam("Value") String instanceType);

   /**
    * @see AMIClient#setInstanceInitiatedShutdownBehaviorForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ModifyInstanceAttribute",
            "instanceInitiatedShutdownBehavior" })
   ListenableFuture<Void> setInstanceInitiatedShutdownBehaviorForInstanceInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId,
            @FormParam("Value") InstanceInitiatedShutdownBehavior instanceInitiatedShutdownBehavior);

   /**
    * @see InstanceClient#setBlockDeviceMappingForInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION }, values = { "ModifyInstanceAttribute" })
   ListenableFuture<Void> setBlockDeviceMappingForInstanceInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId,
            @BinderParam(BindBlockDeviceMappingToIndexedFormParams.class) BlockDeviceMapping blockDeviceMapping);

}
