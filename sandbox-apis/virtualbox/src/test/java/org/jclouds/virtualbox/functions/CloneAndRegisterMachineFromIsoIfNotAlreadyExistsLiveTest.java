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
import static org.virtualbox_4_1.NetworkAttachmentType.Bridged;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.Credentials;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.VirtualBoxManager;

/**
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "CloneAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest")
public class CloneAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest extends
      BaseVirtualBoxClientLiveTest {

   private String settingsFile = null;
   private boolean forceOverwrite = true;
   private String vmId = "jclouds-image-iso-1";
   private String osTypeId = "";
   private String controllerIDE = "IDE Controller";
   private String diskFormat = "";
   private String adminDisk = "testadmin.vdi";
   private String guestId = "guest";
   private String hostId = "host";
   private String snapshotName = "snap";
   private String snapshotDesc = "snapDesc";

   private String vmName = "jclouds-image-virtualbox-iso-to-machine-test";
   private String cloneName = vmName + "_clone";
   private String isoName = "ubuntu-11.04-server-i386.iso";

   @Test
   public void testCloneMachineFromAnotherMachine() throws Exception {
      VirtualBoxManager manager = (VirtualBoxManager) context
            .getProviderSpecificContext().getApi();
      ComputeServiceContext localHostContext = computeServiceForLocalhostAndGuest(
            hostId, "localhost", guestId, "localhost", new Credentials("toor",
                  "password"));

      IMachine master = null;
      try {
         master = new IsoToIMachine(manager, adminDisk, diskFormat,
               settingsFile, vmName, osTypeId, vmId, forceOverwrite,
               controllerIDE, localHostContext, hostId, guestId,
               new Credentials("toor", "password")).apply(isoName);
      } catch (IllegalStateException e) {
         // already created
         master = manager.getVBox().findMachine(vmName);
      }

      if (master.getCurrentSnapshot() != null) {
         ISession session = manager.openMachineSession(master);
         session.getConsole().deleteSnapshot(
               master.getCurrentSnapshot().getId());
         session.unlockMachine();
      }

      IMachine clone = new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(
            manager, localHostContext, settingsFile, osTypeId, vmId,
            forceOverwrite, cloneName, hostId, snapshotName, snapshotDesc)
            .apply(master);
      assertEquals(clone.getNetworkAdapter(0L).getAttachmentType(), Bridged);
   }

}
