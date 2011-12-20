/*
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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jclouds.virtualbox.domain.VmSpec;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IMedium;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VirtualBoxManager;

@Test(groups = "unit", testName = "UnregisterMachineIfExistsTest")
public class UnregisterMachineIfExistsAndDeleteItsMediaTest {

   @Test
   public void testUnregisterExistingMachine() throws Exception {
      VirtualBoxManager manager = createMock(VirtualBoxManager.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMachine registeredMachine = createMock(IMachine.class);
      IProgress progress = createNiceMock(IProgress.class);
      List<IMedium> media = new ArrayList<IMedium>();
      List<IMedium> mediums = Collections.unmodifiableList(media);

      CleanupMode mode = CleanupMode.Full;
      String vmName = "jclouds-image-example-machine-to-be-destroyed";
      
      String vmId = "jclouds-image-iso-unregister";
      VmSpec vmSpecification = VmSpec.builder().id(vmId).name(vmName).memoryMB(512) .cleanUpMode(mode)
            .build();

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.findMachine(vmName)).andReturn(registeredMachine);

      expect(registeredMachine.unregister(mode)).andReturn(mediums);
      expectLastCall().anyTimes();
      
      expect(registeredMachine.delete(mediums)).andReturn(progress);
      expectLastCall().anyTimes();

      replay(manager, vBox, registeredMachine, progress);

      new UnregisterMachineIfExistsAndDeleteItsMedia(manager).apply(vmSpecification);
   }

}
