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

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.EC2Fallbacks.VoidOnVolumeAvailable;
import org.jclouds.ec2.binders.BindUserGroupsToIndexedFormParams;
import org.jclouds.ec2.binders.BindUserIdsToIndexedFormParams;
import org.jclouds.ec2.binders.BindVolumeIdsToIndexedFormParams;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.Permission;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.domain.Volume;
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
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to EC2 Elastic Block Store services via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface ElasticBlockStoreApi {

   /**
    * Creates a new Amazon EBS volume to which any Amazon EC2 instance can attach within the same
    * Availability Zone. For more information about Amazon EBS, go to the Amazon Elastic Compute
    * Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    * 
    * @param availabilityZone
    *           An Amazon EBS volume must be located within the same Availability Zone as the
    *           instance to which it attaches.
    * @param snapshotId
    *           The snapshot from which to create the new volume.
    * 
    * @see #describeVolumesInRegion
    * @see #deleteVolumeInRegion
    * @see #attachVolumeInRegion
    * @see #detachVolumeInRegion
    * @see AvailabilityZoneAndRegionApi#describeAvailabilityZonesInRegion
    * 
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateVolume.html"
    *      />
    */
   @Named("CreateVolume")   
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateVolume")
   @XMLResponseParser(CreateVolumeResponseHandler.class)
   Volume createVolumeFromSnapshotInAvailabilityZone(
            @EndpointParam(parser = ZoneToEndpoint.class) @FormParam("AvailabilityZone") String availabilityZone,
            @FormParam("SnapshotId") String snapshotId);

   /**
    * Creates a new Amazon EBS volume to which any Amazon EC2 instance can attach within the same
    * Availability Zone. This is overloaded {@link #createVolumeFromSnapshotInAvailabilityZone},
    * which creates a volume with a specific size.
    * For more information about Amazon EBS, go to the Amazon Elastic Compute
    * Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    *
    * @param availabilityZone
    *           An Amazon EBS volume must be located within the same Availability Zone as the
    *           instance to which it attaches.
    * @param size
    *           Size of volume to be created
    * @param snapshotId
    *           The snapshot from which to create the new volume.
    *
    * @see #createVolumeFromSnapshotInAvailabilityZone
    * @see #describeVolumesInRegion
    * @see #deleteVolumeInRegion
    * @see #attachVolumeInRegion
    * @see #detachVolumeInRegion
    * @see AvailabilityZoneAndRegionApi#describeAvailabilityZonesInRegion
    *
    *
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateVolume.html"
    *      />
    */
   @Named("CreateVolume")   
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateVolume")
   @XMLResponseParser(CreateVolumeResponseHandler.class)
   Volume createVolumeFromSnapshotInAvailabilityZone(
            @EndpointParam(parser = ZoneToEndpoint.class) @FormParam("AvailabilityZone") String availabilityZone,
            @FormParam("Size") int size, @FormParam("SnapshotId") String snapshotId);

   /**
    * Creates a new Amazon EBS volume to which any Amazon EC2 instance can attach within the same
    * Availability Zone. For more information about Amazon EBS, go to the Amazon Elastic Compute
    * Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    * 
    * @param availabilityZone
    *           An Amazon EBS volume must be located within the same Availability Zone as the
    *           instance to which it attaches.
    * @param size
    *           The size of the volume, in GiBs (1-1024). Required if you are not creating a volume
    *           from a snapshot.
    * 
    * 
    * @see #describeVolumesInRegion
    * @see #deleteVolumeInRegion
    * @see #attachVolumeInRegion
    * @see #detachVolumeInRegion
    * @see AvailabilityZoneAndRegionApi#describeAvailabilityZonesInRegion
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateVolume.html"
    *      />
    */
   @Named("CreateVolume")   
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateVolume")
   @XMLResponseParser(CreateVolumeResponseHandler.class)
   Volume createVolumeInAvailabilityZone(
            @EndpointParam(parser = ZoneToEndpoint.class) @FormParam("AvailabilityZone") String availabilityZone,
            @FormParam("Size") int size);

   /**
    * Describes the specified Amazon EBS volumes that you own. If you do not specify one or more
    * volume IDs, Amazon EBS describes all volumes that you own. For more information about Amazon
    * EBS, go to the Amazon Elastic Compute Cloud Developer Guide or Amazon Elastic Compute Cloud
    * User Guide.
    * 
    * @param region
    *           region where the volume is defined
    * @param volumeIds
    *           The ID of the volume to list. Defaults to describe all volumes that you own.
    * 
    * @see #createSnapshotInRegion
    * @see #describeSnapshotInRegion
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeVolumes.html"
    *      />
    */
   @POST
   @Named("DescribeVolumes")   
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeVolumes")
   @XMLResponseParser(DescribeVolumesResponseHandler.class)
   Set<Volume> describeVolumesInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindVolumeIdsToIndexedFormParams.class) String... volumeIds);

   /**
    * Deletes an Amazon EBS volume that you own. For more information about Amazon EBS, go to the
    * Amazon Elastic Compute Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    * 
    * @param region
    *           region where the volume is defined
    * @param volumeId
    *           The ID of the volume to delete. The volume remains in the deleting state for several
    *           minutes after entering this command.
    * 
    * @see #describeVolumesInRegion
    * @see #createVolumeInRegion
    * @see #attachVolumeInRegion
    * @see #detachVolumeInRegion
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeleteVolume.html"
    *      />
    */
   @Named("DeleteVolume")   
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteVolume")
   void deleteVolumeInRegion(@EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("VolumeId") String volumeId);

   /**
    * Attaches an Amazon EBS volume to a running instance and exposes it as the specified device.
    * <p/>
    * 
    * <h3>Note</h3>
    * 
    * Windows instances currently support devices xvda through xvdp. Devices xvda and xvdb are
    * reserved by the operating system, xvdc is assigned to drive C:\, and, depending on the
    * instance type, devices xvdd through xvde might be reserved by the instance stores. Any device
    * that is not reserved can be attached to an Amazon EBS volume. For a list of devices that are
    * reserved by the instance stores, go to the Amazon Elastic Compute Cloud Developer Guide or
    * Amazon Elastic Compute Cloud User Guide.
    * 
    * @param region
    *           region where the volume is defined
    * @param volumeId
    *           The ID of the volume to delete. The volume remains in the deleting state for several
    *           minutes after entering this command.
    * @param force
    *           Forces detachment if the previous detachment attempt did not occur cleanly (logging
    *           into an instance, unmounting the volume, and detaching normally). This option can
    *           lead to data loss or a corrupted file system. Use this option only as a last resort
    *           to detach a volume from a failed instance. The instance will not have an opportunity
    *           to flush file system caches nor file system meta data. If you use this option, you
    *           must perform file system check and repair procedures.
    * 
    * @param options
    *           options like force()
    * 
    * @see #describeVolumesInRegion
    * @see #createVolumeInRegion
    * @see #attachVolumeInRegion
    * @see #deleteVolumeInRegion
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DetachVolume.html"
    *      />
    */
   @Named("DetachVolume")   
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DetachVolume")
   @Fallback(VoidOnVolumeAvailable.class)
   void detachVolumeInRegion(@EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("VolumeId") String volumeId, @FormParam("Force") boolean force, DetachVolumeOptions... options);

   /**
    * Attaches an Amazon EBS volume to a running instance and exposes it as the specified device.
    * 
    * <h3>Note</h3> Windows instances currently support devices xvda through xvdp. Devices xvda and
    * xvdb are reserved by the operating system, xvdc is assigned to drive C:\, and, depending on
    * the instance type, devices xvdd through xvde might be reserved by the instance stores. Any
    * device that is not reserved can be attached to an Amazon EBS volume. For a list of devices
    * that are reserved by the instance stores, go to the Amazon Elastic Compute Cloud Developer
    * Guide or Amazon Elastic Compute Cloud User Guide.
    * 
    * @param region
    *           region where the volume is defined
    * @param volumeId
    *           The ID of the Amazon EBS volume. The volume and instance must be within the same
    *           Availability Zone and the instance must be running.
    * @param instanceId
    *           The ID of the instance to which the volume attaches. The volume and instance must be
    *           within the same Availability Zone and the instance must be running.
    * @param device
    *           Specifies how the device is exposed to the instance (e.g., /dev/sdh).
    * 
    * @see #describeVolumesInRegion
    * @see #createVolumeInRegion
    * @see #detachVolumeInRegion
    * @see #deleteVolumeInRegion
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-AttachVolume.html"
    *      />
    */
   @Named("AttachVolume")   
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AttachVolume")
   @XMLResponseParser(AttachmentHandler.class)
   Attachment attachVolumeInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("VolumeId") String volumeId, @FormParam("InstanceId") String instanceId,
            @FormParam("Device") String device);

   /**
    * Creates a snapshot of an Amazon EBS volume and stores it in Amazon S3. You can use snapshots
    * for backups, to make identical copies of instance devices, and to save data before shutting
    * down an instance. For more information about Amazon EBS, go to the Amazon Elastic Compute
    * Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    * <p/>
    * When taking a snapshot of a file system, we recommend unmounting it first. This ensures the
    * file system metadata is in a consistent state, that the 'mounted indicator' is cleared, and
    * that all applications using that file system are stopped and in a consistent state. Some file
    * systems, such as xfs, can freeze and unfreeze activity so a snapshot can be made without
    * unmounting.
    * <p/>
    * For Linux/UNIX, enter the following command from the command line.
    * 
    * <pre>
    * umount - d / dev / sdh
    * </pre>
    * <p/>
    * For Windows, open Disk Management, right-click the volume to unmount, and select Change Drive
    * Letter and Path. Then, select the mount point to remove and click Remove.
    * 
    * @param region
    *           Snapshots are tied to Regions and can only be used for volumes within the same
    *           Region.
    * @param volumeId
    *           The ID of the Amazon EBS volume of which to take a snapshot.
    * @param options
    *           options like passing a description.
    * @return the Snapshot in progress
    * 
    * @see #describeSnapshotsInRegion
    * @see #deleteSnapshotInRegion
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateSnapshot.html"
    *      />
    */
   @Named("CreateSnapshot")   
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateSnapshot")
   @XMLResponseParser(SnapshotHandler.class)
   Snapshot createSnapshotInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("VolumeId") String volumeId, CreateSnapshotOptions... options);

   /**
    * Returns information about Amazon EBS snapshots available to the user. Information returned
    * includes volume ID, status, start time, progress, owner ID, volume size, and description.
    * Snapshots available to the user include public snapshots available for any user to
    * createVolume, private snapshots owned by the user making the request, and private snapshots
    * owned by other users for which the user granted explicit create volume permissions.
    * <p/>
    * The create volume permissions fall into 3 categories:
    * <p/>
    * <table>
    * <tr>
    * <td>Permission</td>
    * <td>Description</td>
    * </tr>
    * <tr>
    * <td>public</td>
    * <td>The owner of the snapshot granted create volume permissions for the snapshot to the all
    * group. All users have create volume permissions for these snapshots.</td>
    * </tr>
    * <tr>
    * <td>explicit</td>
    * <td>The owner of the snapshot granted create volume permissions to a specific user.</td>
    * </tr>
    * <tr>
    * <td>implicit</td>
    * <td>A user has implicit create volume permissions for all snapshots he or she owns.</td>
    * </tr>
    * </table>
    * <p/>
    * 
    * The list of snapshots returned can be modified by specifying snapshot IDs, snapshot owners, or
    * users with create volume permissions. If no options are specified, Amazon EC2 returns all
    * snapshots for which the user has create volume permissions.
    * <p/>
    * If you specify one or more snapshot IDs, only snapshots that have the specified IDs are
    * returned. If you specify an invalid snapshot ID, a fault is returned. If you specify a
    * snapshot ID for which you do not have access, it will not be included in the returned results.
    * <p/>
    * If you specify one or more snapshot owners, only snapshots from the specified owners and for
    * which you have access are returned. The results can include the AWS Account IDs of the
    * specified owners, amazon for snapshots owned by Amazon or self for snapshots that you own.
    * <p/>
    * If you specify a list of restorable users, only users that have create snapshot permissions
    * for the snapshots are returned. You can specify AWS Account IDs (if you own the snapshot(s)),
    * self for snapshots for which you own or have explicit permissions, or all for public
    * snapshots.
    * 
    * @param region
    *           Snapshots are tied to Regions and can only be used for volumes within the same
    *           Region.
    * @param options
    *           specify the snapshot ids or other parameters to clarify the list.
    * @return matching snapshots.
    * 
    * @see #createSnapshotsInRegion
    * @see #deleteSnapshotInRegion
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSnapshots.html"
    *      />
    */
   @Named("DescribeSnapshots")   
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSnapshots")
   @XMLResponseParser(DescribeSnapshotsResponseHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Snapshot> describeSnapshotsInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            DescribeSnapshotsOptions... options);

   /**
    * Deletes a snapshot of an Amazon EBS volume that you own. For more information, go to the
    * Amazon Elastic Compute Cloud Developer Guide or Amazon Elastic Compute Cloud User Guide.
    * 
    * @param region
    *           Snapshots are tied to Regions and can only be used for volumes within the same
    *           Region.
    * @param snapshotId
    *           The ID of the Amazon EBS snapshot to delete.
    * 
    * @see #createSnapshotInRegion
    * @see #deleteSnapshotInRegion
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeleteSnapshot.html"
    *      />
    */
   @Named("DeleteSnapshot")   
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteSnapshot")
   void deleteSnapshotInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("SnapshotId") String snapshotId);

   /**
    * Adds {@code createVolumePermission}s to an EBS snapshot.
    * 
    * @param region
    *           Snapshots are tied to Regions and can only be used for volumes within the same
    *           Region.
    * @param userIds
    *           AWS Access Key ID.
    * @param userGroups
    *           Name of the groups. Currently supports \"all.\""
    * @param snapshotId
    *           The ID of the Amazon EBS snapshot.
    * 
    * @see #removeCreateVolumePermissionsFromSnapshot
    * @see #describeSnapshotAttribute
    * @see #resetSnapshotAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifySnapshotAttribute.html"
    *      />
    */
   @Named("ModifySnapshotAttribute")   
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifySnapshotAttribute", "add",
            "createVolumePermission" })
   void addCreateVolumePermissionsToSnapshotInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindUserIdsToIndexedFormParams.class) Iterable<String> userIds,
            @BinderParam(BindUserGroupsToIndexedFormParams.class) Iterable<String> userGroups,
            @FormParam("SnapshotId") String snapshotId);

   /**
    * Removes {@code createVolumePermission}s from an EBS snapshot.
    * 
    * @param region
    *           Snapshots are tied to Regions and can only be used for volumes within the same
    *           Region.
    * @param userIds
    *           AWS Access Key ID.
    * @param userGroups
    *           Name of the groups. Currently supports \"all.\""
    * @param snapshotId
    *           The ID of the Amazon EBS snapshot.
    * 
    * @see #addCreateVolumePermissionsToSnapshot
    * @see #describeSnapshotAttribute
    * @see #resetSnapshotAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifySnapshotAttribute.html"
    *      />
    */
   @Named("ModifySnapshotAttribute")   
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifySnapshotAttribute", "remove",
            "createVolumePermission" })
   void removeCreateVolumePermissionsFromSnapshotInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindUserIdsToIndexedFormParams.class) Iterable<String> userIds,
            @BinderParam(BindUserGroupsToIndexedFormParams.class) Iterable<String> userGroups,
            @FormParam("SnapshotId") String snapshotId);

   /**
    * Returns the {@link Permission}s of an snapshot.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param snapshotId
    *           The ID of the AMI for which an attribute will be described
    * @see #describeSnapshots
    * @see #modifySnapshotAttribute
    * @see #resetSnapshotAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSnapshotAttribute.html"
    *      />
    * @see DescribeSnapshotsOptions
    */
   @Named("DescribeSnapshotAttribute")   
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeSnapshotAttribute", "createVolumePermission" })
   @XMLResponseParser(PermissionHandler.class)
   Permission getCreateVolumePermissionForSnapshotInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("SnapshotId") String snapshotId);

   /**
    * Resets the {@code createVolumePermission}s on an EBS snapshot.
    * 
    * @param region
    *           Snapshots are tied to Regions and can only be used for volumes within the same
    *           Region.
    * @param snapshotId
    *           The ID of the Amazon EBS snapshot.
    * 
    * @see #addCreateVolumePermissionsToSnapshot
    * @see #describeSnapshotAttribute
    * @see #removeProductCodesFromSnapshot
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ResetSnapshotAttribute.html"
    *      />
    */
   @Named("ResetSnapshotAttribute")   
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "ResetSnapshotAttribute", "createVolumePermission" })
   void resetCreateVolumePermissionsOnSnapshotInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("SnapshotId") String snapshotId);

}
