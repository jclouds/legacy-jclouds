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

import static com.google.common.collect.Iterables.*;
import static org.testng.AssertJUnit.*;

import java.util.Set;

import com.google.common.base.Function;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.Snapshot;
import org.jclouds.cloudstack.domain.Volume;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.options.ListVolumesOptions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code VolumeClient}
 *
 * @author Vijay Kiran
 */
@Test(groups = "live", singleThreaded = true, testName = "VolumeClientLiveTest")
public class VolumeClientLiveTest extends BaseCloudStackClientLiveTest {
   protected String prefix = System.getProperty("user.name");

   private long zoneId;

   @BeforeMethod(groups = "live")
   public void setZoneId() {
      Set<Zone> zones = client.getZoneClient().listZones();
      assertNotNull(zones);
      assertFalse(zones.isEmpty());
      zoneId = Iterables.get(zones, 0).getId();
   }

   public void testListVolumes() {
      Set<Volume> volumes = client.getVolumeClient().listVolumes();
      assertNotNull(volumes);
      assertFalse(volumes.isEmpty());

      for (Volume volume : volumes) {
         checkVolume(volume);
      }
   }

   public void testListVolumesById() {
      Iterable<Long> volumeIds = Iterables.transform(client.getVolumeClient().listVolumes(), new Function<Volume, Long>() {
                  public Long apply(Volume input) {
                      return input.getId();
                  }
              });
      assertNotNull(volumeIds);
      assertFalse(Iterables.isEmpty(volumeIds));

      for (Long id : volumeIds) {
         Set<Volume> found = client.getVolumeClient().listVolumes(ListVolumesOptions.Builder.id(id));
         assertNotNull(found);
         assertEquals(1, found.size());
         Volume volume = Iterables.getOnlyElement(found);
         assertEquals(id.longValue(), volume.getId());
         checkVolume(volume);
      }
   }

   public void testListVolumesNonexistantId() {
      Set<Volume> found = client.getVolumeClient().listVolumes(ListVolumesOptions.Builder.id(-1));
      assertNotNull(found);
      assertTrue(found.isEmpty());
   }

   public void testGetVolumeById() {
      Iterable<Long> volumeIds = Iterables.transform(client.getVolumeClient().listVolumes(), new Function<Volume, Long>() {
                  public Long apply(Volume input) {
                      return input.getId();
                  }
              });
      assertNotNull(volumeIds);
      assertFalse(Iterables.isEmpty(volumeIds));

      for (Long id : volumeIds) {
         Volume found = client.getVolumeClient().getVolume(id);
         assertNotNull(found);
         assertEquals(id.longValue(), found.getId());
         checkVolume(found);
      }
   }

   public void testGetVolumeNonexistantId() {
      Volume found = client.getVolumeClient().getVolume(-1);
      assertNull(found);
   }

   public void testCreateVolumeFromDiskofferingInZoneAndDeleteVolume() {
      // Pick some disk offering
      long diskOfferingId = Iterables.get(client.getOfferingClient().listDiskOfferings(), 0).getId();

      Volume volume = null;
      while (volume == null) {
         try {
            AsyncCreateResponse job = client.getVolumeClient().createVolumeFromDiskOfferingInZone(prefix + "-jclouds-volume",
                  diskOfferingId, zoneId);
            assertTrue(jobComplete.apply(job.getJobId()));
            volume = findVolumeWithId(job.getId());
         } catch (IllegalStateException e) {
            // TODO volume creation failed - retry?
         }
      }

      checkVolume(volume);

      // Delete the volume
      client.getVolumeClient().deleteVolume(volume.getId());
   }

   public void testCreateVolumeFromDiskofferingInZoneAndAttachVolumeToVirtualMachineAndDetachAndDelete() {
      // Pick some disk offering
      long diskOfferingId = Iterables.get(client.getOfferingClient().listDiskOfferings(), 0).getId();

      Volume volume = null;
      while (volume == null) {
         try {
            AsyncCreateResponse job = client.getVolumeClient().createVolumeFromDiskOfferingInZone(prefix + "-jclouds-volume",
                  diskOfferingId, zoneId);
            assertTrue(jobComplete.apply(job.getJobId()));
            volume = findVolumeWithId(job.getId());
         } catch (IllegalStateException e) {
            // TODO volume creation failed - retry?
         }
      }

      checkVolume(volume);
      long virtualMachineId = Iterables.get(client.getVirtualMachineClient().listVirtualMachines(), 0).getId();

      // Attach Volume
      Volume attachedVolume = null;
      while (attachedVolume == null) {
         try {
            AsyncCreateResponse job = client.getVolumeClient().attachVolume(volume.getId(), virtualMachineId);
            assertTrue(jobComplete.apply(job.getJobId()));
            attachedVolume = findVolumeWithId(volume.getId());
            assertEquals(virtualMachineId, attachedVolume.getVirtualMachineId());
            assertNotNull(attachedVolume.getAttached());
         } catch (IllegalStateException e) {
            // TODO volume creation failed - retry?
         }
      }

      // Detach Volume
      Volume detachedVolume = null;
      while (detachedVolume == null) {
         try {
            AsyncCreateResponse job = client.getVolumeClient().detachVolume(volume.getId());
            assertTrue(jobComplete.apply(job.getJobId()));
            detachedVolume = findVolumeWithId(volume.getId());
            checkVolume(detachedVolume);
         } catch (IllegalStateException e) {
            //TODO volume creation failed - retry?
         }
      }

      // Cleanup
      client.getVolumeClient().deleteVolume(volume.getId());
   }

   public void testCreateVolumeFromSnapshotInZoneAndDeleteVolume() {
      Set<Snapshot> snapshots = client.getSnapshotClient().listSnapshots();
      assertNotNull(snapshots);
      assertFalse(snapshots.isEmpty());
      long snapshotId = Iterables.get(snapshots, 0).getId();

      Volume volume = null;
      while (volume == null) {
         try {
            AsyncCreateResponse job = client.getVolumeClient().createVolumeFromSnapshotInZone(prefix + "-jclouds-volume",
                  snapshotId, zoneId);
            assertTrue(jobComplete.apply(job.getJobId()));
            volume = findVolumeWithId(job.getId());
         } catch (IllegalStateException e) {
            // TODO volume creation failed - retry?
         }
      }

      checkVolume(volume);

      // Delete the volume
      client.getVolumeClient().deleteVolume(volume.getId());
   }

   private void checkVolume(final Volume volume) {
      assertNotNull(volume.getId());
      assertNotNull(volume.getName());
      assertNotSame(Volume.VolumeType.UNRECOGNIZED, volume.getType());
   }

   private Volume findVolumeWithId(final long id) {
      return find(client.getVolumeClient().listVolumes(), new Predicate<Volume>() {
         @Override
         public boolean apply(Volume arg0) {
            return arg0.getId() == id;
         }
      });
   }
}
