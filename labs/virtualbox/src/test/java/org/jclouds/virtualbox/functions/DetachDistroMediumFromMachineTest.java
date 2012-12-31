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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.testng.annotations.Test;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.VBoxException;

/**
 * @author Andrea Turli
 */
@Test(groups = "unit", testName = "DetachDistroMediumFromMachineTest")
public class DetachDistroMediumFromMachineTest {

   @Test
   public void testDetachDistroMedium() throws Exception {

      String controller = "IDE Controller";
      IMachine machine = createMock(IMachine.class);

      int controllerPort = 0;
      int device = 1;

      machine.saveSettings();
      machine.detachDevice(controller, controllerPort, device);

      replay(machine);

      new DetachDistroMediumFromMachine(controller, controllerPort, device).apply(machine);

      verify(machine);

   }

   @Test
   public void testAcceptAlreadyDetachedDistroMedium() throws Exception {

      String controller = "IDE Controller";

      IMachine machine = createNiceMock(IMachine.class);

      final StringBuilder errorBuilder = new StringBuilder();
      errorBuilder.append("VirtualBox error: ");
      errorBuilder.append("Medium '/Users/johndoe/jclouds-virtualbox-test/ubuntu-11.04-server-i386.iso' ");
      errorBuilder.append("is already detached from port 0, device 0 of controller 'IDE Controller' ");
      errorBuilder.append("of this virtual machine (0x80BB000C)");
      String isoAlreadyAttachedException = errorBuilder.toString();

      int controllerPort = 0;
      int device = 1;

      VBoxException isoAttachedException = new VBoxException(createNiceMock(Throwable.class), isoAlreadyAttachedException);
      machine.detachDevice(controller, controllerPort, device);
      expectLastCall().andThrow(isoAttachedException);

      replay(machine);

      new DetachDistroMediumFromMachine(controller, controllerPort, device).apply(machine);

      verify(machine);

   }

   @Test(expectedExceptions = VBoxException.class)
   public void testFailOnOtherVBoxErrors() throws Exception {

      String controllerName = "IDE Controller";

      IMachine machine = createNiceMock(IMachine.class);

      final StringBuilder errorBuilder = new StringBuilder();
      errorBuilder.append("VirtualBox error: ");
      errorBuilder.append("Some other VBox error");
      String isoAlreadyAttachedException = errorBuilder.toString();

      int controllerPort = 0;
      int device = 1;

      VBoxException isoAttachedException = new VBoxException(createNiceMock(Throwable.class), isoAlreadyAttachedException);
      machine.detachDevice(controllerName, controllerPort, device);
      expectLastCall().andThrow(isoAttachedException);

      replay(machine);

      new DetachDistroMediumFromMachine(controllerName, controllerPort, device).apply(machine);

   }

}
