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

import static com.google.common.collect.Iterables.find;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNotSame;

import java.util.Set;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.Volume;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code SSHKeyPairClient}
 *
 * @author Vijay Kiran
 */
@Test(groups = "live", singleThreaded = true, testName = "VolumeClientLiveTest")
public class VolumeClientLiveTest extends BaseCloudStackClientLiveTest {

   protected String prefix = System.getProperty("user.name");
   private long zoneId;
   private long diskOfferingId;
   private long snapshotId;
   private Volume volume;

   public void testListVolumes() {
      final Set<Volume> volumes = client.getVolumeClient().listVolumes();
      for (Volume volume : volumes) {
         checkVolume(volume);
      }
   }

   public void testCreateVolumeFromDiskofferingInZoneAndDeleteVolume() {

      zoneId = Iterables.getFirst(client.getZoneClient().listZones(), null).getId();
      //Pick some disk offering
      diskOfferingId = Iterables.getFirst(client.getOfferingClient().listDiskOfferings(), null).getId();


      while (volume == null) {
         try {
            AsyncCreateResponse job = client.getVolumeClient().createVolumeFromDiskOfferingInZone(prefix + "-jclouds-volume",
                  diskOfferingId, zoneId);
            assert jobComplete.apply(job.getJobId());
            volume = findVolumeWithId(job.getId());
         } catch (IllegalStateException e) {
            //TODO volume creation failed - retry?
         }
      }

      checkVolume(volume);
      //Delete the volume
      client.getVolumeClient().deleteVolume(volume.getId());
   }

   public void testCreateVolumeFromDiskofferingInZoneAndAttachVolumeToVirtualMachineAndDetachAndDelete() {

      zoneId = Iterables.getFirst(client.getZoneClient().listZones(), null).getId();
      //Pick some disk offering
      diskOfferingId = Iterables.getFirst(client.getOfferingClient().listDiskOfferings(), null).getId();


      while (volume == null) {
         try {
            AsyncCreateResponse job = client.getVolumeClient().createVolumeFromDiskOfferingInZone(prefix + "-jclouds-volume",
                  diskOfferingId, zoneId);
            assert jobComplete.apply(job.getJobId());
            volume = findVolumeWithId(job.getId());
         } catch (IllegalStateException e) {
            //TODO volume creation failed - retry?
         }
      }

      checkVolume(volume);
      long virtualMachineId = Iterables.getFirst(client.getVirtualMachineClient().listVirtualMachines(), null).getId();
      //Attach Volume
      Volume attachedVolume = null;
      while (attachedVolume == null) {
         try {
            AsyncCreateResponse job = client.getVolumeClient().attachVolume(volume.getId(), virtualMachineId);
            assert jobComplete.apply(job.getJobId());
            attachedVolume = findVolumeWithId(volume.getId());
            assert attachedVolume.getVirtualMachineId() == virtualMachineId;
            assert attachedVolume.getAttached() != null;
         } catch (IllegalStateException e) {
            //TODO volume creation failed - retry?
         }
      }

      //Detach Volume
      Volume detachedVolume = null;
      while (detachedVolume == null) {
         try {
            AsyncCreateResponse job = client.getVolumeClient().detachVolume(volume.getId());
            assert jobComplete.apply(job.getJobId());
            detachedVolume = findVolumeWithId(volume.getId());
            checkVolume(detachedVolume);
         } catch (IllegalStateException e) {
            //TODO volume creation failed - retry?
         }
      }

      //Cleanup
      client.getVolumeClient().deleteVolume(volume.getId());

   }


   /*
   TODO Uncomment this test after SnapshotClient has test coverage.
   public void testCreateVolumeFromSnapshotInZoneAndDeleteVolume() {

      zoneId = Iterables.getFirst(client.getZoneClient().listZones(), null).getId();
      final Set<Snapshot> snapshots = client.getSnapshotClient().listSnapshots();
      assertNotNull(snapshots);
      assertNotSame(0, snapshots.size() );
      snapshotId = Iterables.getFirst(snapshots, null).getId();
      while (volume == null) {
         try {
            AsyncCreateResponse job = client.getVolumeClient().createVolumeFromSnapshotInZone(prefix + "-jclouds-volume",
                  snapshotId, zoneId);
            assert jobComplete.apply(job.getJobId());
            volume = findVolumeWithId(job.getId());
         } catch (IllegalStateException e) {
            //TODO volume creation failed - retry?
         }
      }

      checkVolume(volume);
      //Delete the volume
      client.getVolumeClient().deleteVolume(volume.getId());
   }
   */

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
