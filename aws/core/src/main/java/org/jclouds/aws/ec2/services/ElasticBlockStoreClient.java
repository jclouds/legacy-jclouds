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

import java.util.concurrent.TimeUnit;

import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.Region;
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
   String createVolumeFromSnapshotInAvailabilityZone(AvailabilityZone availabilityZone,
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
   String createVolumeInAvailabilityZone(AvailabilityZone availabilityZone, int size);

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
   String describeVolumesInRegion(Region region, String... volumeIds);

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
    * @see #describeVolumes
    * @see #createVolume
    * @see #attachVolume
    * @see #detachVolume
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeleteVolume.html"
    *      />
    */
   void deleteVolumeInRegion(Region region, String volumeId);

}
