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
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.nova.v2_0.domain.Volume;
import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.nova.v2_0.domain.VolumeSnapshot;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeOptions;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeSnapshotOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

/**
 * Provides synchronous access to Volumes.
 * <p/>
 * 
 * @see org.jclouds.openstack.nova.v2_0.extensions.VolumeAsyncClient
 * @author Adam Lowe
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.VOLUMES)
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface VolumeClient {
   /**
    * Returns a summary list of snapshots.
    *
    * @return the list of snapshots
    */
   Set<Volume> listVolumes();

   /**
    * Returns a detailed list of volumes.
    *
    * @return the list of volumes.
    */
   Set<Volume> listVolumesInDetail();

   /**
    * Return data about the given volume.
    *
    * @return details of a specific snapshot.
    */
   Volume getVolume(String volumeId);

   /**
    * Creates a new Snapshot
    *
    * @return the new Snapshot
    */
   Volume createVolume(int sizeGB, CreateVolumeOptions... options);

   /**
    * Delete a snapshot.
    *
    * @return true if successful
    */
   Boolean deleteVolume(String volumeId);
   
   /**
    * List volume attachments for a given instance.
    * 
    * @return all Floating IPs
    */
   Set<VolumeAttachment> listAttachmentsOnServer(String serverId);

   /**
    * Get a specific attached volume.
    * 
    * @return data about the given volume attachment.
    */
   VolumeAttachment getAttachmentForVolumeOnServer(String volumeId, String serverId);

   /**
    * Attach a volume to an instance
    * 
    * @return data about the new volume attachment
    */
   VolumeAttachment attachVolumeToServerAsDevice(String volumeId, String serverId, String device);

   /**
    * Detach a Volume from an instance.
    * 
    * @return true if successful
    */
   Boolean detachVolumeFromServer(String server_id, String volumeId);

   /**
    * Returns a summary list of snapshots.
    *
    * @return the list of snapshots
    */
   Set<VolumeSnapshot> listSnapshots();

   /**
    * Returns a summary list of snapshots.
    *
    * @return the list of snapshots
    */
   Set<VolumeSnapshot> listSnapshotsInDetail();

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
   Boolean deleteSnapshot(String snapshotId);
   
}
