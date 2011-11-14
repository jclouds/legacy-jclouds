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

import static org.testng.AssertJUnit.assertNotNull;

import java.util.Set;

import org.jclouds.cloudstack.domain.DiskOffering;
import org.jclouds.cloudstack.domain.Volume;
import org.jclouds.cloudstack.domain.Zone;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code SSHKeyPairClient}
 *
 * @author Vijay Kiran
 */
@Test(groups = "live", singleThreaded = true, testName = "VolumeClientLiveTest")
public class VolumeClientLiveTest extends BaseCloudStackClientLiveTest {

   protected String prefix = System.getProperty("user.name");

   public void testListVolumes() {
      final Set<Volume> volumes = client.getVolumeClient().listVolumes();
      for (Volume volume : volumes) {
         checkVolume(volume);
      }
   }

   public void testCreateVolumeFromDiskofferingInZoneAndDeleteVolume() {

      final Set<Zone> zones = client.getZoneClient().listZones();
      assertNotNull(zones);
      final Zone zone = zones.iterator().next();

      final Set<DiskOffering> diskOfferings = client.getOfferingClient().listDiskOfferings();
      assertNotNull(diskOfferings);

      //Pick some disk offering
      final DiskOffering diskOffering = diskOfferings.iterator().next();
      final VolumeClient volumeClient = client.getVolumeClient();

      final Volume volumeWithDiskOffering =
            volumeClient.createVolumeFromDiskOfferingInZone(prefix + "-jclouds-volume",
                  diskOffering.getId(),
                  zone.getId());
      checkVolume(volumeWithDiskOffering);
      volumeClient.deleteVolume(volumeWithDiskOffering.getId());
   }


   private void checkVolume(Volume volume) {
      assertNotNull(volume.getId());
      assertNotNull(volume.getName());
   }
}
