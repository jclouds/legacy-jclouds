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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.ErrorCode;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.functions.admin.UnregisterMachineIfExistsAndDeleteItsMedia;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VBoxException;

/**
 * @author Mattias Holmqvist
 */
public class CreateAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest extends BaseVirtualBoxClientLiveTest {

   @Test
   public void testCreateNewMachine() throws Exception {
      String vmName = "jclouds-test-create-1-node";
      new UnregisterMachineIfExistsAndDeleteItsMedia(manager, CleanupMode.Full).apply(vmName);
      VmSpec launchSpecification = VmSpec.builder().id(vmName).name(vmName)
              .osTypeId("Debian").forceOverwrite(true).build();
      IMachine debianNode = new CreateAndRegisterMachineFromIsoIfNotAlreadyExists(manager).apply(launchSpecification);
      IMachine machine = manager.getVBox().findMachine(vmName);
      assertEquals(debianNode.getName(), machine.getName());
      new UnregisterMachineIfExistsAndDeleteItsMedia(manager, CleanupMode.Full).apply(vmName);
   }

   @Test
   public void testCreateNewMachineWithBadOsType() throws Exception {
      String vmName = "jclouds-test-create-2-node";
      new UnregisterMachineIfExistsAndDeleteItsMedia(manager, CleanupMode.Full).apply(vmName);
      VmSpec launchSpecification = VmSpec.builder().id(vmName).name(vmName)
              .osTypeId("SomeWeirdUnknownOs").forceOverwrite(true).build();
      try {
         new CreateAndRegisterMachineFromIsoIfNotAlreadyExists(manager).apply(launchSpecification);
         fail();
      } catch (VBoxException e) {
         ErrorCode errorCode = ErrorCode.valueOf(e);
         // According to the documentation VBOX_E_OBJECT_NOT_FOUND
         // if osTypeId is not found.
         assertEquals(errorCode, ErrorCode.VBOX_E_OBJECT_NOT_FOUND);
      }
   }

}
