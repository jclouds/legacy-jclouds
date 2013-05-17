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
package org.jclouds.cloudstack.features;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNotSame;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.DiskOffering;
import org.jclouds.cloudstack.domain.Snapshot;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Volume;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.options.ListVolumesOptions;
import org.jclouds.logging.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code VolumeClient}
 *
 * @author Vijay Kiran, Alex Heneveld
 */
@Test(groups = "live", singleThreaded = true, testName = "VolumeClientLiveTest")
public class VolumeClientLiveTest extends BaseCloudStackClientLiveTest {

   @Resource Logger logger = Logger.NULL;

   protected String prefix = System.getProperty("user.name")+"-"+getClass().getSimpleName();

   private String zoneId;

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
      Iterable<String> volumeIds = Iterables.transform(client.getVolumeClient().listVolumes(), new Function<Volume, String>() {
         public String apply(Volume input) {
            return input.getId();
         }
      });
      assertNotNull(volumeIds);
      assertFalse(Iterables.isEmpty(volumeIds));

      for (String id : volumeIds) {
         Set<Volume> found = client.getVolumeClient().listVolumes(ListVolumesOptions.Builder.id(id));
         assertNotNull(found);
         assertEquals(1, found.size());
         Volume volume = Iterables.getOnlyElement(found);
         assertEquals(id, volume.getId());
         checkVolume(volume);
      }
   }

   public void testListVolumesNonexistantId() {
      Set<Volume> found = client.getVolumeClient().listVolumes(ListVolumesOptions.Builder.id("foo"));
      assertNotNull(found);
      assertTrue(found.isEmpty());
   }

   public void testGetVolumeById() {
      Iterable<String> volumeIds = Iterables.transform(client.getVolumeClient().listVolumes(), new Function<Volume, String>() {
         public String apply(Volume input) {
            return input.getId();
         }
      });
      assertNotNull(volumeIds);
      assertFalse(Iterables.isEmpty(volumeIds));

      for (String id : volumeIds) {
         Volume found = client.getVolumeClient().getVolume(id);
         assertNotNull(found);
         assertEquals(id, found.getId());
         checkVolume(found);
      }
   }

   public void testGetVolumeNonexistantId() {
      Volume found = client.getVolumeClient().getVolume("foo");
      assertNull(found);
   }

   protected DiskOffering getPreferredDiskOffering() {
      for (DiskOffering candidate : client.getOfferingClient().listDiskOfferings()) {
         //any will do
         return candidate;
      }
      throw new AssertionError("No suitable DiskOffering found.");
   }
   protected Snapshot getPreferredSnapshot() {
      for (Snapshot candidate : client.getSnapshotClient().listSnapshots()) {
         if (candidate.getState()==Snapshot.State.BACKED_UP)
            return candidate;
      }
      throw new AssertionError("No suitable Snapshot found.");
   }

   protected VirtualMachine getPreferredVirtualMachine() {
      for (VirtualMachine candidate : client.getVirtualMachineClient().listVirtualMachines()) {
//         this is a guess::
         if (candidate.getState()==VirtualMachine.State.RUNNING || candidate.getState()==VirtualMachine.State.STOPPED)
            return candidate;
      }
      throw new AssertionError("No suitable VirtualMachine found.");
   }

   protected Volume createPreferredVolumeFromDisk() {
      AsyncCreateResponse job = client.getVolumeClient().createVolumeFromDiskOfferingInZone(prefix + "-jclouds-volume",
            getPreferredDiskOffering().getId(), zoneId);
      assertTrue(jobComplete.apply(job.getJobId()));
      logger.info("created volume " + job.getId());
      return findVolumeWithId(job.getId());
   }

   public void testCreateVolumeFromDiskofferingInZoneAndDeleteVolume() {
      logger.info("testCreateVolumeFromDiskofferingInZoneAndDeleteVolume");
      Volume volume = createPreferredVolumeFromDisk();
      checkVolume(volume);
      client.getVolumeClient().deleteVolume(volume.getId());
   }

   /** Test requires a custom disk offering to be available */
   public void testCreateVolumeFromCustomDiskOffering() {
      final int size = 1;
      DiskOffering offering = null;
      for (DiskOffering candidate : client.getOfferingClient().listDiskOfferings()) {
         if (candidate.isCustomized()) {
            offering = candidate;
            break;
         }
      }
      
      assertNotNull("No custom disk offering found!", offering);
      
      AsyncCreateResponse job = client.getVolumeClient().createVolumeFromCustomDiskOfferingInZone(
                prefix + "-jclouds-volume", offering.getId(), zoneId, size);
      assertTrue(jobComplete.apply(job.getJobId()));
      logger.info("created volume "+job.getId());
      
      Volume volume = findVolumeWithId(job.getId());
      try {
         checkVolume(volume);
         assertEquals(volume.getSize(), size * 1024 * 1024 * 1024);
      } finally {
         client.getVolumeClient().deleteVolume(volume.getId());
      }
   }

   /** test requires that a VM exist */
   public void testCreateVolumeFromDiskofferingInZoneAndAttachVolumeToVirtualMachineAndDetachAndDelete() {
      logger.info("testCreateVolumeFromDiskofferingInZoneAndAttachVolumeToVirtualMachineAndDetachAndDelete");
      final Volume volume = createPreferredVolumeFromDisk();
      try {

         checkVolume(volume);

         VirtualMachine virtualMachine = getPreferredVirtualMachine();

         logger.info("attaching volume %s to vm %s", volume, virtualMachine);
         AsyncCreateResponse job = client.getVolumeClient().attachVolume(volume.getId(), virtualMachine.getId());
         assertTrue(jobComplete.apply(job.getJobId()));
         Volume attachedVolume = findVolumeWithId(volume.getId());

         checkVolume(attachedVolume);
         assertEquals(virtualMachine.getId(), attachedVolume.getVirtualMachineId());
         assertNotNull(attachedVolume.getAttached());

         logger.info("detaching volume %s from vm %s", volume, virtualMachine);
         job = client.getVolumeClient().detachVolume(volume.getId());
         assertTrue(jobComplete.apply(job.getJobId()));
         Volume detachedVolume = findVolumeWithId(volume.getId());

         checkVolume(detachedVolume);
         assertNull(detachedVolume.getAttached());

      } finally {
         client.getVolumeClient().deleteVolume(volume.getId());
      }
   }

   public void testCreateVolumeFromSnapshotInZoneAndDeleteVolume() {
      logger.info("testCreateVolumeFromSnapshotInZoneAndDeleteVolume (takes ~3m)");
      assertNotNull(getPreferredSnapshot());

      AsyncCreateResponse job = client.getVolumeClient().createVolumeFromSnapshotInZone(prefix + "-jclouds-volume",
            getPreferredSnapshot().getId(), zoneId);
      assertTrue(jobComplete.apply(job.getJobId()));
      Volume volume = findVolumeWithId(job.getId());

      checkVolume(volume);
      client.getVolumeClient().deleteVolume(volume.getId());
   }

   static void checkVolume(final Volume volume) {
      assertNotNull(volume.getId());
      assertNotNull(volume.getName());
      assertNotSame(Volume.Type.UNRECOGNIZED, volume.getType());
   }

   Volume findVolumeWithId(final String id) {
      return findVolumeWithId(client, id);
   }

   static Volume findVolumeWithId(final CloudStackClient client, final String id) {
      for (Volume v: client.getVolumeClient().listVolumes())
         if (v.getId().equals(id)) return v;
      throw new NoSuchElementException("no volume with id "+id);
   }

//   //uncomment to force a cleanup of volumes (since test failures can leave messes) 
//   public void deleteAllWeUsed() {
//      for (Volume v: client.getVolumeClient().listVolumes()) {
//         if (v.getName().startsWith(prefix)) {
//            logger.warn("found apparent detritus, deleting: %s", v);
//            try {
//               client.getVolumeClient().deleteVolume(v.getId());
//            } catch (Exception e) {
//               logger.warn(e, "failed to delete %s: %s", v, e);
//            }
//         }
//       }
//   }
}
