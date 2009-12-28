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

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.ec2.domain.Attachment;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.Volume;
import org.jclouds.aws.ec2.options.DetachVolumeOptions;
import org.jclouds.concurrent.Timeout;

/**
 * Provides access to EC2 Elastic Block Store services.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface ElasticBlockStoreClient {

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
    * @see AvailabilityZoneAndRegionClient#describeAvailabilityZonesInRegion
    * 
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateVolume.html"
    *      />
    */
   Volume createVolumeFromSnapshotInAvailabilityZone(AvailabilityZone availabilityZone,
            String snapshotId);

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
    * @see AvailabilityZoneAndRegionClient#describeAvailabilityZonesInRegion
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateVolume.html"
    *      />
    */
   Volume createVolumeInAvailabilityZone(AvailabilityZone availabilityZone, int size);

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
   Set<Volume> describeVolumesInRegion(Region region, String... volumeIds);

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
   void deleteVolumeInRegion(Region region, String volumeId);

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
   Attachment detachVolumeInRegion(Region region, String volumeId, DetachVolumeOptions... options);

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
   Attachment attachVolumeInRegion(Region region, String volumeId, String instanceId, String device);
}
