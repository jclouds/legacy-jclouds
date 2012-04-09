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
import org.jclouds.ec2.binders.BindUserGroupsToIndexedFormParams;
import org.jclouds.ec2.binders.BindUserIdsToIndexedFormParams;
import org.jclouds.ec2.binders.BindVolumeIdsToIndexedFormParams;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.Permission;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.functions.ReturnVoidOnVolumeAvailable;
import org.jclouds.ec2.options.CreateSnapshotOptions;
import org.jclouds.ec2.options.DescribeSnapshotsOptions;
import org.jclouds.ec2.options.DetachVolumeOptions;
import org.jclouds.ec2.xml.AttachmentHandler;
import org.jclouds.ec2.xml.CreateVolumeResponseHandler;
import org.jclouds.ec2.xml.DescribeSnapshotsResponseHandler;
import org.jclouds.ec2.xml.DescribeVolumesResponseHandler;
import org.jclouds.ec2.xml.PermissionHandler;
import org.jclouds.ec2.xml.SnapshotHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to EC2 Elastic Block Store services via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface ElasticBlockStoreAsyncClient {

   /**
    * @see ElasticBlockStoreClient#createVolumeFromSnapshotInAvailabilityZone
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateVolume")
   @XMLResponseParser(CreateVolumeResponseHandler.class)
   ListenableFuture<Volume> createVolumeFromSnapshotInAvailabilityZone(
            @EndpointParam(parser = ZoneToEndpoint.class) @FormParam("AvailabilityZone") String availabilityZone,
            @FormParam("SnapshotId") String snapshotId);

   /**
    * @see ElasticBlockStoreClient#createVolumeFromSnapshotInAvailabilityZone
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateVolume")
   @XMLResponseParser(CreateVolumeResponseHandler.class)
   ListenableFuture<Volume> createVolumeFromSnapshotInAvailabilityZone(
            @EndpointParam(parser = ZoneToEndpoint.class) @FormParam("AvailabilityZone") String availabilityZone,
            @FormParam("Size") int size, @FormParam("SnapshotId") String snapshotId);

   /**
    * @see ElasticBlockStoreClient#createVolumeInAvailabilityZone
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateVolume")
   @XMLResponseParser(CreateVolumeResponseHandler.class)
   ListenableFuture<Volume> createVolumeInAvailabilityZone(
            @EndpointParam(parser = ZoneToEndpoint.class) @FormParam("AvailabilityZone") String availabilityZone,
            @FormParam("Size") int size);

   /**
    * @see ElasticBlockStoreClient#describeVolumesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeVolumes")
   @XMLResponseParser(DescribeVolumesResponseHandler.class)
   ListenableFuture<? extends Set<Volume>> describeVolumesInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindVolumeIdsToIndexedFormParams.class) String... volumeIds);

   /**
    * @see ElasticBlockStoreClient#deleteVolumeInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteVolume")
   ListenableFuture<Void> deleteVolumeInRegion(@EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("VolumeId") String volumeId);

   /**
    * @see ElasticBlockStoreClient#detachVolumeInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DetachVolume")
   @ExceptionParser(ReturnVoidOnVolumeAvailable.class)
   ListenableFuture<Void> detachVolumeInRegion(@EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("VolumeId") String volumeId, @FormParam("Force") boolean force, DetachVolumeOptions... options);

   /**
    * @see ElasticBlockStoreClient#attachVolumeInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AttachVolume")
   @XMLResponseParser(AttachmentHandler.class)
   ListenableFuture<Attachment> attachVolumeInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("VolumeId") String volumeId, @FormParam("InstanceId") String instanceId,
            @FormParam("Device") String device);

   /**
    * @see ElasticBlockStoreClient#createSnapshotInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateSnapshot")
   @XMLResponseParser(SnapshotHandler.class)
   ListenableFuture<Snapshot> createSnapshotInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("VolumeId") String volumeId, CreateSnapshotOptions... options);

   /**
    * @see ElasticBlockStoreClient#describeSnapshotsInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSnapshots")
   @XMLResponseParser(DescribeSnapshotsResponseHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<Snapshot>> describeSnapshotsInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            DescribeSnapshotsOptions... options);

   /**
    * @see ElasticBlockStoreClient#deleteSnapshotInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteSnapshot")
   ListenableFuture<Void> deleteSnapshotInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("SnapshotId") String snapshotId);

   /**
    * @see ElasticBlockStoreClient#addCreateVolumePermissionsToSnapshotInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifySnapshotAttribute", "add",
            "createVolumePermission" })
   ListenableFuture<Void> addCreateVolumePermissionsToSnapshotInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindUserIdsToIndexedFormParams.class) Iterable<String> userIds,
            @BinderParam(BindUserGroupsToIndexedFormParams.class) Iterable<String> userGroups,
            @FormParam("SnapshotId") String snapshotId);

   /**
    * @see ElasticBlockStoreClient#removeCreateVolumePermissionsFromSnapshotInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifySnapshotAttribute", "remove",
            "createVolumePermission" })
   ListenableFuture<Void> removeCreateVolumePermissionsFromSnapshotInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindUserIdsToIndexedFormParams.class) Iterable<String> userIds,
            @BinderParam(BindUserGroupsToIndexedFormParams.class) Iterable<String> userGroups,
            @FormParam("SnapshotId") String snapshotId);

   /**
    * @see ElasticBlockStoreClient#getCreateVolumePermissionForSnapshotInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeSnapshotAttribute", "createVolumePermission" })
   @XMLResponseParser(PermissionHandler.class)
   ListenableFuture<Permission> getCreateVolumePermissionForSnapshotInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("SnapshotId") String snapshotId);

   /**
    * @see ElasticBlockStoreClient#resetCreateVolumePermissionsOnSnapshotInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ResetSnapshotAttribute", "createVolumePermission" })
   ListenableFuture<Void> resetCreateVolumePermissionsOnSnapshotInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("SnapshotId") String snapshotId);

}
