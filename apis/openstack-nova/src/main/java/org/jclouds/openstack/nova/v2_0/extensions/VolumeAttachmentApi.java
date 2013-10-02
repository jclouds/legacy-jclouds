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

import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Volume Attachments.
 * 
 * This API strictly handles attaching Volumes to Servers. To create and manage Volumes you need to use one of the 
 * following APIs:
 * 
 * 1. The Cinder API
 *    If your OpenStack deployment is Folsom or later and it supports the Cinder block storage service, use this API.
 *    @see org.jclouds.openstack.cinder.v1.features.VolumeApi
 *    
 * 2. The nova-volume API
 *    If your OpenStack deployment is Essex or earlier and it supports the nova-volume extension, use this API.
 *    @see org.jclouds.openstack.nova.v2_0.extensions.VolumeApi
 * 
 * @see VolumeAttachmentAsyncApi
 * @author Everett Toews
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.VOLUMES)
public interface VolumeAttachmentApi {
   /**
    * List Volume Attachments for a given Server.
    * 
    * @param serverId The ID of the Server
    * @return All VolumeAttachments for the Server
    */
   FluentIterable<? extends VolumeAttachment> listAttachmentsOnServer(String serverId);

   /**
    * Get a specific Volume Attachment for a Volume and Server.
    * 
    * @param volumeId The ID of the Volume
    * @param serverId The ID of the Server
    * @return The Volume Attachment.
    */
   VolumeAttachment getAttachmentForVolumeOnServer(String volumeId, String serverId);

   /**
    * Attach a Volume to a Server.
    * 
    * Note: If you are using KVM as your hypervisor then the actual device name in the Server will be different than 
    * the one specified. When the Server sees a new device, it picks the next available name (which in most cases is
    * /dev/vdc) and the disk shows up there on the Server.
    * 
    * @param serverId The ID of the Server
    * @param volumeId The ID of the Volume
    * @param device The name of the device this Volume will be identified as in the Server (e.g. /dev/vdc) 
    * @return The Volume Attachment.
    */
   VolumeAttachment attachVolumeToServerAsDevice(String volumeId, String serverId, String device);

   /**
    * Detach a Volume from a server.
    * 
    * Note: Make sure you've unmounted the volume first. Failure to do so could result in failure or data loss.
    * 
    * @param volumeId The ID of the Volume
    * @param serverId The ID of the Server
    * @return true if successful
    */
   boolean detachVolumeFromServer(String volumeId, String serverId);
}
