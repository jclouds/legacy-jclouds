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

package org.jclouds.virtualbox.functions;

import static org.jclouds.virtualbox.experiment.TestUtils.computeServiceForLocalhostAndGuest;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.Credentials;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.functions.admin.UnregisterMachineIfExistsAndDeleteItsMedia;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.StorageBus;
import org.virtualbox_4_1.VirtualBoxManager;

/**
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "CloneAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest")
public class CloneAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest extends
      BaseVirtualBoxClientLiveTest {

   private static final boolean IS_LINKED_CLONE = true;
   private String vmId = "jclouds-image-iso-1";
   private String osTypeId = "DEBIAN";
   private String guestId = "guest";
   private String hostId = "host";

   private String vmName = "jclouds-image-virtualbox-iso-to-machine-test";
   private String cloneName = vmName + "_clone";
   private VmSpec clonedVmSpec;

   private String ideControllerName = "IDE Controller";
   private CleanupMode mode = CleanupMode.Full;

   @Test
   public void testCloneMachineFromAnotherMachine() throws Exception {
      VirtualBoxManager manager = (VirtualBoxManager) context
            .getProviderSpecificContext().getApi();
      ComputeServiceContext localHostContext = computeServiceForLocalhostAndGuest(
            hostId, "localhost", guestId, "localhost", new Credentials("toor",
                  "password"));

      IMachine master = getMasterNode(manager, localHostContext);

      if (master.getCurrentSnapshot() != null) {
         ISession session = manager.openMachineSession(master);
         session.getConsole().deleteSnapshot(
               master.getCurrentSnapshot().getId());
         session.unlockMachine();
      }

      clonedVmSpec = VmSpec.builder().id(cloneName).name(cloneName).memoryMB(512)
            .cleanUpMode(mode)
            .forceOverwrite(true).build();
      IMachine clone = new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(
            manager, workingDir, clonedVmSpec, IS_LINKED_CLONE).apply(master);
      assertEquals(clone.getName(), clonedVmSpec.getVmName());
      new UnregisterMachineIfExistsAndDeleteItsMedia(manager).apply(clonedVmSpec);
      new UnregisterMachineIfExistsAndDeleteItsMedia(manager).apply(new IMachineToVmSpec().apply(master));
   }

   private IMachine getMasterNode(VirtualBoxManager manager,
         ComputeServiceContext localHostContext) {
      try {
         StorageController ideController = StorageController
               .builder()
               .name(ideControllerName)
               .bus(StorageBus.IDE)
               .attachISO(0, 0, operatingSystemIso)
               .attachHardDisk(
                     HardDisk.builder().diskpath(adminDisk)
                           .controllerPort(0).deviceSlot(1).build())
               .attachISO(1, 1, guestAdditionsIso)
               .build();
         VmSpec vmSpec = VmSpec.builder().id(vmId).name(vmName)
               .osTypeId(osTypeId).memoryMB(512).cleanUpMode(CleanupMode.Full)
               .controller(ideController).forceOverwrite(true).build();
         return new CreateAndRegisterMachineFromIsoIfNotAlreadyExists(manager, workingDir).apply(vmSpec);

      } catch (IllegalStateException e) {
         // already created
         return manager.getVBox().findMachine(vmName);
      }
   }
}