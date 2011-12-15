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

import com.google.common.base.Predicate;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.Credentials;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpecification;
import org.jclouds.virtualbox.util.PropertyUtils;
import org.testng.annotations.Test;
import org.virtualbox_4_1.*;

import java.util.concurrent.TimeUnit;

import static org.jclouds.virtualbox.domain.ExecutionType.HEADLESS;
import static org.jclouds.virtualbox.experiment.TestUtils.computeServiceForLocalhostAndGuest;
import static org.testng.Assert.assertEquals;
import static org.virtualbox_4_1.NetworkAttachmentType.Bridged;

/**
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "CloneAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest")
public class CloneAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest extends BaseVirtualBoxClientLiveTest {

   private String settingsFile = null;
   private boolean forceOverwrite = true;
   private String vmId = "jclouds-image-iso-1";
   private String osTypeId = "";
   private String controllerIDE = "IDE Controller";
   private String guestId = "guest";
   private String hostId = "host";
   private String snapshotName = "snap";
   private String snapshotDesc = "snapDesc";

   private String vmName = "jclouds-image-virtualbox-iso-to-machine-test";
   private String cloneName = vmName + "_clone";
   private String isoName = "ubuntu-11.04-server-i386.iso";

   @Test
   public void testCloneMachineFromAnotherMachine() throws Exception {
      VirtualBoxManager manager = (VirtualBoxManager) context.getProviderSpecificContext().getApi();
      ComputeServiceContext localHostContext =
              computeServiceForLocalhostAndGuest(hostId, "localhost", guestId, "localhost", new Credentials("toor", "password"));

      IMachine master = getMasterNode(manager, localHostContext);

      if (master.getCurrentSnapshot() != null) {
         ISession session = manager.openMachineSession(master);
         session.getConsole().deleteSnapshot(master.getCurrentSnapshot().getId());
         session.unlockMachine();
      }

      IMachine clone = new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(
              manager, localHostContext, settingsFile, osTypeId, vmId,
              forceOverwrite, cloneName, hostId, snapshotName, snapshotDesc,
              controllerIDE).apply(master);
      assertEquals(clone.getNetworkAdapter(0L).getAttachmentType(), Bridged);
   }

   private IMachine getMasterNode(VirtualBoxManager manager, ComputeServiceContext localHostContext) {
      try {
         Predicate<IPSocket> socketTester = new RetryablePredicate<IPSocket>(new InetSocketAddressConnect(), 10, 1, TimeUnit.SECONDS);
         String workingDir = PropertyUtils.getWorkingDirFromProperty();
         StorageController ideController = StorageController.builder().name(controllerIDE).bus(StorageBus.IDE)
         .attachISO(0, 0, workingDir + "/ubuntu-11.04-server-i386.iso")
         .attachHardDisk(0, 1, workingDir + "/testadmin.vdi")
         .attachISO(1, 1, workingDir + "/VBoxGuestAdditions_4.1.2.iso").build();
         VmSpecification vmSpecification = VmSpecification.builder().id(vmId).name(vmName).osTypeId(osTypeId)
                 .controller(ideController)
                 .forceOverwrite(true).build();
         return new IsoToIMachine(manager, guestId, localHostContext, hostId, socketTester,
                 "127.0.0.1", 8080, HEADLESS).apply(vmSpecification);
      } catch (IllegalStateException e) {
         // already created
         return manager.getVBox().findMachine(vmName);
      }
   }
}