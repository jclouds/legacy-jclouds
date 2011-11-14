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
package org.jclouds.cloudstack.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.cloudstack.domain.Volume;
import org.jclouds.cloudstack.options.ListVolumesOptions;
import org.jclouds.concurrent.Timeout;

/**
 * Provides synchronous access to CloudStack Event features.
 * <p/>
 *
 * @author Vijay Kiran
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface VolumeClient {

   /**
    * Create a volume with given name and diskOfferingId
    *
    * @param name           name of the volume
    * @param diskOfferingId the ID of the disk offering.
    * @param zoneId the ID of the availability zone
    * @return Volume
    */
   Volume createVolumeFromDiskOfferingInZone(String name, long diskOfferingId, long zoneId);

   /**
    * Create a volume with given name and snapshotId
    *
    * @param name       name of the volume
    * @param snapshotId Snapshot id to be used while creating the volume
    * @return Volume
    */
   Volume createVolumeWithSnapshot(String name, long snapshotId);

   /**
    * List volumes
    *
    * @return volume list or null if not found
    */
   Set<Volume> listVolumes(ListVolumesOptions... options);

   /**
    * Deletes a attached disk volume
    *
    * @param id id of the volume
    */
   void deleteVolume(long id);

}
