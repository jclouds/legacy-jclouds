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
package org.jclouds.virtualbox.functions.admin;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IMedium;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.StorageBus;
import org.virtualbox_4_1.VirtualBoxManager;

@Test(groups = "unit", testName = "UnregisterMachineIfExistsTest")
public class UnregisterMachineIfExistsAndDeleteItsMediaTest {

   private String ideControllerName = "IDE Controller";
   private CleanupMode mode = CleanupMode.Full;
   private String vmName = "jclouds-image-example-machine-to-be-destroyed";
   private String vmId = "jclouds-image-iso-unregister";
   private String osTypeId = "";

   @Test
   public void testUnregisterExistingMachine() throws Exception {
      VirtualBoxManager manager = createMock(VirtualBoxManager.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMachine registeredMachine = createMock(IMachine.class);
      IProgress progress = createNiceMock(IProgress.class);
      List<IMedium> media = Lists.newArrayList();
      List<IMedium> mediums = Collections.unmodifiableList(media);
      
      StorageController ideController = StorageController.builder().name(ideControllerName).bus(StorageBus.IDE)
              .attachISO(0, 0, "/tmp/ubuntu-11.04-server-i386.iso")
              .attachHardDisk(HardDisk.builder().diskpath("/tmp/testadmin.vdi").controllerPort(0).deviceSlot(1).build())
              .attachISO(1, 1, "/tmp/VBoxGuestAdditions_4.1.2.iso").build();
      VmSpec vmSpecification = VmSpec.builder().id(vmId).name(vmName).memoryMB(512).osTypeId(osTypeId)
              .controller(ideController)
              .forceOverwrite(true)
              .cleanUpMode(CleanupMode.Full).build();

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.findMachine(vmName)).andReturn(registeredMachine);

      expect(registeredMachine.unregister(mode)).andReturn(mediums);
      expectLastCall().anyTimes();
      
      expect(registeredMachine.delete(mediums)).andReturn(progress);
      expectLastCall().anyTimes();

      replay(manager, vBox, registeredMachine, progress);

      new UnregisterMachineIfExistsAndDeleteItsMedia(vmSpecification).apply(registeredMachine);
   }

}
