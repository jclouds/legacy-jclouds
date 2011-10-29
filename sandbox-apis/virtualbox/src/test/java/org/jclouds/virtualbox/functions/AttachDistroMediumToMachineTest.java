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

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.testng.annotations.Test;
import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IMedium;
import org.virtualbox_4_1.VBoxException;

/**
 * @author Mattias Holmqvist
 */
@Test(groups = "unit", testName = "AttachDistroMediumToMachineTest")
public class AttachDistroMediumToMachineTest {

   @Test
   public void testAttachDistroMedium() throws Exception {

      String controllerIDE = "IDE Controller";
      IMedium distroMedium = createNiceMock(IMedium.class);

      IMachine machine = createMock(IMachine.class);

      machine.saveSettings();
      machine.attachDevice(controllerIDE, 0, 0, DeviceType.DVD, distroMedium);

      replay(machine, distroMedium);

      new AttachDistroMediumToMachine(controllerIDE, distroMedium).apply(machine);

      verify(machine);

   }

   @Test
   public void testAcceptAlreadyAttachedDistroMedium() throws Exception {

      String controllerIDE = "IDE Controller";
      IMedium distroMedium = createNiceMock(IMedium.class);

      IMachine machine = createNiceMock(IMachine.class);

      final StringBuilder errorBuilder = new StringBuilder();
      errorBuilder.append("VirtualBox error: ");
      errorBuilder.append("Medium '/Users/johndoe/jclouds-virtualbox-test/ubuntu-11.04-server-i386.iso' ");
      errorBuilder.append("is already attached to port 0, device 0 of controller 'IDE Controller' ");
      errorBuilder.append("of this virtual machine (0x80BB000C)");
      String isoAlreadyAttachedException = errorBuilder.toString();

      VBoxException isoAttachedException = new VBoxException(createNiceMock(Throwable.class),
            isoAlreadyAttachedException);
      machine.attachDevice(controllerIDE, 0, 0, DeviceType.DVD, distroMedium);
      expectLastCall().andThrow(isoAttachedException);

      replay(machine, distroMedium);

      new AttachDistroMediumToMachine(controllerIDE, distroMedium).apply(machine);

      verify(machine);

   }

   @Test(expectedExceptions = VBoxException.class)
   public void testFailOnOtherVBoxErrors() throws Exception {

      String controllerIDE = "IDE Controller";
      IMedium distroMedium = createNiceMock(IMedium.class);

      IMachine machine = createNiceMock(IMachine.class);

      final StringBuilder errorBuilder = new StringBuilder();
      errorBuilder.append("VirtualBox error: ");
      errorBuilder.append("Some other VBox error");
      String isoAlreadyAttachedException = errorBuilder.toString();

      VBoxException isoAttachedException = new VBoxException(createNiceMock(Throwable.class),
            isoAlreadyAttachedException);
      machine.attachDevice(controllerIDE, 0, 0, DeviceType.DVD, distroMedium);
      expectLastCall().andThrow(isoAttachedException);

      replay(machine, distroMedium);

      new AttachDistroMediumToMachine(controllerIDE, distroMedium).apply(machine);

   }

}
