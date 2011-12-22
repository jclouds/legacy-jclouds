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

package org.jclouds.virtualbox.functions;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.easymock.EasyMock;
import org.jclouds.virtualbox.domain.VmSpec;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

/**
 * @author Mattias Holmqvist
 */
@Test(groups = "unit", testName = "CreateAndRegisterMachineFromIsoIfNotAlreadyExistsTest")
public class CreateAndRegisterMachineFromIsoIfNotAlreadyExistsTest {

   @Test
   public void testCreateIfNotAlreadyExists() throws Exception {

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      String vmName = "jclouds-image-my-ubuntu-image";

      VmSpec launchSpecification = VmSpec.builder().id(vmName).name(vmName).osTypeId("").memoryMB(1024).cleanUpMode(CleanupMode.Full).build();

      IMachine createdMachine = createMock(IMachine.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();

      StringBuilder errorMessageBuilder = new StringBuilder();
      errorMessageBuilder.append("VirtualBox error: Could not find a registered machine named ");
      errorMessageBuilder.append("'jclouds-image-virtualbox-iso-to-machine-test' (0x80BB0001)");
      String errorMessage = errorMessageBuilder.toString();
      VBoxException vBoxException = new VBoxException(createNiceMock(Throwable.class), errorMessage);

      vBox.findMachine(vmName);
      expectLastCall().andThrow(vBoxException);

      expect(vBox.createMachine(anyString(), eq(vmName), anyString(), anyString(), anyBoolean())).andReturn(
              createdMachine).anyTimes();

      vBox.registerMachine(createdMachine);

      replay(manager, vBox);

      new CreateAndRegisterMachineFromIsoIfNotAlreadyExists(manager).apply(launchSpecification);

      verify(manager, vBox);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testFailIfMachineIsAlreadyRegistered() throws Exception {

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createNiceMock(IVirtualBox.class);
      String vmName = "jclouds-image-my-ubuntu-image";

      IMachine registeredMachine = createMock(IMachine.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.findMachine(vmName)).andReturn(registeredMachine).anyTimes();

      replay(manager, vBox);

      VmSpec launchSpecification = VmSpec.builder().id("").name(vmName).osTypeId("").memoryMB(1024).cleanUpMode(CleanupMode.Full).build();
      new CreateAndRegisterMachineFromIsoIfNotAlreadyExists(manager).apply(launchSpecification);
   }

   @Test(expectedExceptions = VBoxException.class)
   public void testFailIfOtherVBoxExceptionIsThrown() throws Exception {

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createNiceMock(IVirtualBox.class);
      String vmName = "jclouds-image-my-ubuntu-image";

      String errorMessage = "VirtualBox error: Soem other VBox error";
      VBoxException vBoxException = new VBoxException(createNiceMock(Throwable.class), errorMessage);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();

      vBox.findMachine(vmName);
      expectLastCall().andThrow(vBoxException);

      replay(manager, vBox);

      VmSpec launchSpecification = VmSpec.builder().id("").name(vmName).osTypeId("").cleanUpMode(CleanupMode.Full).memoryMB(1024).build();
      new CreateAndRegisterMachineFromIsoIfNotAlreadyExists(manager).apply(launchSpecification);

   }

   private String anyString() {
      return EasyMock.<String>anyObject();
   }
}
