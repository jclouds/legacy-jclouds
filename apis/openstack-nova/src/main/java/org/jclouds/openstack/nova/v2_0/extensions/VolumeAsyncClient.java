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
package org.jclouds.openstack.nova.v2_0.extensions;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.Volume;
import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.nova.v2_0.domain.VolumeSnapshot;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeOptions;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeSnapshotOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides synchronous access to Volumes.
 * <p/>
 * 
 * @see org.jclouds.openstack.nova.v2_0.extensions.VolumeAsyncClient
 * @author Adam Lowe
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.VOLUMES)
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
public interface VolumeAsyncClient {
   /**
    * Returns a summary list of volumes.
    *
    * @return the list of volumes
    */
   @GET
   @Path("/os-volumes")
   @SelectJson("volumes")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Volume>> listVolumes();

   /**
    * Returns a detailed list of volumes.
    *
    * @return the list of volumes.
    */
   @GET
   @Path("/os-volumes/detail")
   @SelectJson("volumes")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Volume>> listVolumesInDetail();

   /**
    * Return data about the given volume.
    *
    * @return details of a specific volume.
    */
   @GET
   @Path("/os-volumes/{id}")
   @SelectJson("volume")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Volume> getVolume(@PathParam("id") String volumeId);

   /**
    * Creates a new volume
    *
    * @return the new Snapshot
    */
   @POST
   @Path("/os-volumes")
   @SelectJson("volume")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(CreateVolumeOptions.class)
   ListenableFuture<Volume> createVolume(@PayloadParam("size") int sizeGB, CreateVolumeOptions... options);

   /**
    * Delete a volume.
    *
    * @return true if successful
    */
   @DELETE
   @Path("/os-volumes/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> deleteVolume(@PathParam("id") String volumeId);
   
   /**
    * List volume attachments for a given instance.
    * 
    * @return all Floating IPs
    */
   @GET
   @Path("/servers/{server_id}/os-volume_attachments")
   @SelectJson("volumeAttachments")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<VolumeAttachment>> listAttachmentsOnServer(@PathParam("server_id") String serverId);

   /**
    * Get a specific attached volume.
    * 
    * @return data about the given volume attachment.
    */
   @GET
   @Path("/servers/{server_id}/os-volume_attachments/{id}")
   @SelectJson("volumeAttachment")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VolumeAttachment> getAttachmentForVolumeOnServer(@PathParam("id") String volumeId,
                                                                     @PathParam("server_id") String serverId);

   /**
    * Attach a volume to an instance
    *
    * @return the new Attachment
    */
   @POST
   @Path("/servers/{server_id}/os-volume_attachments")
   @SelectJson("volumeAttachment")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @WrapWith("volumeAttachment")
   ListenableFuture<VolumeAttachment> attachVolumeToServerAsDevice(@PayloadParam("volumeId") String volumeId,
                                             @PathParam("server_id") String serverId, @PayloadParam("device") String device);

   /**
    * Detach a Volume from an instance.
    * 
    * @return true if successful
    */
   @DELETE
   @Path("/servers/{server_id}/os-volume_attachments/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> detachVolumeFromServer(@PathParam("id") String volumeId, @PathParam("server_id") String serverId);

   /**
    * Returns a summary list of snapshots.
    *
    * @return the list of snapshots
    */
   @GET
   @Path("/os-snapshots")
   @SelectJson("snapshots")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<VolumeSnapshot>> listSnapshots();

   /**
    * Returns a summary list of snapshots.
    *
    * @return the list of snapshots
    */
   @GET
   @Path("/os-snapshots/detail")
   @SelectJson("snapshots")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<VolumeSnapshot>> listSnapshotsInDetail();

   /**
    * Return data about the given snapshot.
    *
    * @return details of a specific snapshot.
    */
   @GET
   @Path("/os-snapshots/{id}")
   @SelectJson("snapshot")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VolumeSnapshot> getSnapshot(@PathParam("id") String snapshotId);

   /**
    * Creates a new Snapshot
    *
    * @return the new Snapshot
    */
   @POST
   @Path("/os-snapshots")
   @SelectJson("snapshot")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @MapBinder(CreateVolumeSnapshotOptions.class)
   ListenableFuture<VolumeSnapshot> createSnapshot(@PayloadParam("volume_id") String volumeId, CreateVolumeSnapshotOptions... options);

   /**
    * Delete a snapshot.
    *
    * @return true if successful
    */
   @DELETE
   @Path("/os-snapshots/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> deleteSnapshot(@PathParam("id") String snapshotId);
   
}
