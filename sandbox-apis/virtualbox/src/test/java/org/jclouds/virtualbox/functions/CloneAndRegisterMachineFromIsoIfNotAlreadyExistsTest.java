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

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CloneMode;
import org.virtualbox_4_1.CloneOptions;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISnapshot;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Andrea Turli
 */
@Test(groups = "unit", testName = "CloneAndRegisterMachineFromIsoIfNotAlreadyExistsTest")
public class CloneAndRegisterMachineFromIsoIfNotAlreadyExistsTest {

	@Test
   public void testCloneIfNotAlreadyExists() throws Exception {

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      String vmName = "jclouds-image-my-ubuntu-image";
      String cloneName = vmName + "_clone";
      IMachine master = createMock(IMachine.class);
      IMachine createdMachine = createMock(IMachine.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(master.getName()).andReturn(vmName).anyTimes();

      StringBuilder errorMessageBuilder = new StringBuilder();
      errorMessageBuilder.append("VirtualBox error: Could not find a registered machine named ");
      errorMessageBuilder.append("'jclouds-image-virtualbox-machine-to-machine-test' (0x80BB0001)");
      String errorMessage = errorMessageBuilder.toString();
      VBoxException vBoxException = new VBoxException(createNiceMock(Throwable.class), errorMessage);

      vBox.findMachine(cloneName);
      expectLastCall().andThrow(vBoxException);
      
      expect(vBox.createMachine(anyString(), eq(cloneName), anyString(), anyString(), anyBoolean())).andReturn(createdMachine).anyTimes();
      IProgress iProgress = createNiceMock(IProgress.class);
      List<CloneOptions> options = new ArrayList<CloneOptions>();
      options.add(CloneOptions.Link);
		ISnapshot iSnapshot = createNiceMock(ISnapshot.class);
		expect(master.getCurrentSnapshot()).andReturn(iSnapshot).anyTimes();
		expect(iSnapshot.getMachine()).andReturn(master).anyTimes();
		expect(master.cloneTo(createdMachine, CloneMode.MachineState,
            options)).andReturn(iProgress).anyTimes();
		expect(iProgress.getCompleted()).andReturn(true).anyTimes();
      vBox.registerMachine(createdMachine);

      replay(manager, vBox, master, iProgress, iSnapshot);

      new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists("", "", "", false, manager, cloneName).apply(master);

      verify(manager, vBox);
   }
   

   @Test(expectedExceptions = IllegalStateException.class)
   public void testFailIfMachineIsAlreadyRegistered() throws Exception {

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createNiceMock(IVirtualBox.class);
      String vmName = "jclouds-image-my-ubuntu-image";
      String cloneName = vmName + "_clone";

      IMachine master = createMock(IMachine.class);
      IMachine registeredMachine = createMock(IMachine.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.findMachine(cloneName)).andReturn(registeredMachine).anyTimes();

      replay(manager, vBox);

      new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists("", "", "", false, manager, cloneName).apply(master);
   }
	
   private String anyString() {
      return EasyMock.<String>anyObject();
   }
}
