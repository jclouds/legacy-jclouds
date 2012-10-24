/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.cloud;

import static org.jclouds.abiquo.reference.AbiquoTestConstants.PREFIX;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.abiquo.domain.infrastructure.Tier;
import org.jclouds.abiquo.domain.task.AsyncTask;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.infrastructure.TierPredicates;
import org.testng.annotations.Test;

/**
 * Live integration tests for the {@link VirtualMachine} storage operations.
 * 
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "VirtualMachineStorageLiveApiTest")
public class VirtualMachineStorageLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private Volume volume;

   private HardDisk hardDisk;

   public void testAttachVolumes() {
      volume = createVolume();

      // Since the virtual machine is not deployed, this should not generate a
      // task
      AsyncTask task = env.virtualMachine.attachVolumes(volume);
      assertNull(task);

      List<Volume> attached = env.virtualMachine.listAttachedVolumes();
      assertEquals(attached.size(), 1);
      assertEquals(attached.get(0).getId(), volume.getId());
   }

   @Test(dependsOnMethods = "testAttachVolumes")
   public void detachVolume() {
      env.virtualMachine.detachVolumes(volume);
      List<Volume> attached = env.virtualMachine.listAttachedVolumes();
      assertTrue(attached.isEmpty());
   }

   @Test(dependsOnMethods = "detachVolume")
   public void detachAllVolumes() {
      // Since the virtual machine is not deployed, this should not generate a
      // task
      AsyncTask task = env.virtualMachine.attachVolumes(volume);
      assertNull(task);

      env.virtualMachine.detachAllVolumes();
      List<Volume> attached = env.virtualMachine.listAttachedVolumes();
      assertTrue(attached.isEmpty());

      deleteVolume(volume);
   }

   public void testAttachHardDisks() {
      hardDisk = createHardDisk();

      // Since the virtual machine is not deployed, this should not generate a
      // task
      AsyncTask task = env.virtualMachine.attachHardDisks(hardDisk);
      assertNull(task);

      List<HardDisk> attached = env.virtualMachine.listAttachedHardDisks();
      assertEquals(attached.size(), 1);
      assertEquals(attached.get(0).getId(), hardDisk.getId());
   }

   @Test(dependsOnMethods = "testAttachHardDisks")
   public void detachHardDisk() {
      env.virtualMachine.detachHardDisks(hardDisk);
      List<HardDisk> attached = env.virtualMachine.listAttachedHardDisks();
      assertTrue(attached.isEmpty());
   }

   @Test(dependsOnMethods = "detachHardDisk")
   public void detachAllHardDisks() {
      // Since the virtual machine is not deployed, this should not generate a
      // task
      AsyncTask task = env.virtualMachine.attachHardDisks(hardDisk);
      assertNull(task);

      env.virtualMachine.detachAllHardDisks();
      List<HardDisk> attached = env.virtualMachine.listAttachedHardDisks();
      assertTrue(attached.isEmpty());

      deleteHardDisk(hardDisk);
   }

   private Volume createVolume() {
      Tier tier = env.virtualDatacenter.findStorageTier(TierPredicates.name(env.tier.getName()));

      Volume volume = Volume.builder(env.context.getApiContext(), env.virtualDatacenter, tier)
            .name(PREFIX + "Hawaian volume").sizeInMb(32).build();
      volume.save();

      assertNotNull(volume.getId());
      assertNotNull(env.virtualDatacenter.getVolume(volume.getId()));

      return volume;
   }

   private void deleteVolume(final Volume volume) {
      Integer id = volume.getId();
      volume.delete();
      assertNull(env.virtualDatacenter.getVolume(id));
   }

   private HardDisk createHardDisk() {
      HardDisk hardDisk = HardDisk.builder(env.context.getApiContext(), env.virtualDatacenter).sizeInMb(64L).build();
      hardDisk.save();

      assertNotNull(hardDisk.getId());
      assertNotNull(hardDisk.getSequence());

      return hardDisk;
   }

   private void deleteHardDisk(final HardDisk hardDisk) {
      Integer id = hardDisk.getId();
      hardDisk.delete();
      assertNull(env.virtualDatacenter.getHardDisk(id));
   }
}
