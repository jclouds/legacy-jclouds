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
package org.jclouds.openstack.nova.v2_0.extensions;

import org.jclouds.openstack.nova.v2_0.domain.Volume;
import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.nova.v2_0.domain.VolumeSnapshot;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeOptions;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeSnapshotOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Volumes.
 * <p/>
 * 
 * @see VolumeAsyncApi
 * @see org.jclouds.openstack.nova.v2_0.extensions.VolumeAsyncApi
 * @author Adam Lowe
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.VOLUMES)
public interface VolumeApi {
   /**
    * Returns a summary list of snapshots.
    *
    * @return the list of snapshots
    */
   FluentIterable<? extends Volume> list();

   /**
    * Returns a detailed list of volumes.
    *
    * @return the list of volumes.
    */
   FluentIterable<? extends Volume> listInDetail();

   /**
    * Return data about the given volume.
    *
    * @return details of a specific snapshot.
    */
   Volume get(String volumeId);

   /**
    * Creates a new Snapshot
    *
    * @return the new Snapshot
    */
   Volume create(int sizeGB, CreateVolumeOptions... options);

   /**
    * Delete a snapshot.
    *
    * @return true if successful
    */
   boolean delete(String volumeId);
   
   /**
    * List volume attachments for a given instance.
    * 
    * @return all Floating IPs
    * @deprecated To be removed in jclouds 1.7
    * @see VolumeAttachmentApi#listAttachmentsOnServer(String)
    */
   @Deprecated FluentIterable<? extends VolumeAttachment> listAttachmentsOnServer(String serverId);

   /**
    * Get a specific attached volume.
    * 
    * @return data about the given volume attachment.
    * @deprecated To be removed in jclouds 1.7
    * @see VolumeAttachmentApi#getAttachmentForVolumeOnServer(String, String)
    */
   @Deprecated VolumeAttachment getAttachmentForVolumeOnServer(String volumeId, String serverId);

   /**
    * Attach a volume to an instance
    * 
    * @return data about the new volume attachment
    * @deprecated To be removed in jclouds 1.7
    * @see VolumeAttachmentApi#attachVolumeToServerAsDevice(String, String, String)
    */
   @Deprecated VolumeAttachment attachVolumeToServerAsDevice(String volumeId, String serverId, String device);

   /**
    * Detach a Volume from an instance.
    * 
    * @return true if successful
    * @deprecated To be removed in jclouds 1.7
    * @see VolumeAttachmentApi#detachVolumeFromServer(String, String)
    */
   @Deprecated Boolean detachVolumeFromServer(String server_id, String volumeId);

   /**
    * Returns a summary list of snapshots.
    *
    * @return the list of snapshots
    */
   FluentIterable<? extends VolumeSnapshot> listSnapshots();

   /**
    * Returns a summary list of snapshots.
    *
    * @return the list of snapshots
    */
   FluentIterable<? extends VolumeSnapshot> listSnapshotsInDetail();

   /**
    * Return data about the given snapshot.
    *
    * @return details of a specific snapshot.
    */
   VolumeSnapshot getSnapshot(String snapshotId);

   /**
    * Creates a new Snapshot
    *
    * @return the new Snapshot
    */
   VolumeSnapshot createSnapshot(String volumeId, CreateVolumeSnapshotOptions... options);

   /**
    * Delete a snapshot.
    *
    * @return true if successful
    */
   boolean deleteSnapshot(String snapshotId);
   
}
