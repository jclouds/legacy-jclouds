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

import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.functions.admin.UnregisterMachineIfExists;
import org.jclouds.virtualbox.util.MachineUtils;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMedium;
import org.virtualbox_4_1.LockType;

import java.io.File;

import static org.jclouds.virtualbox.util.MachineUtils.lockMachineAndApply;
import static org.testng.Assert.assertTrue;

/**
 * @author Mattias Holmqvist
 */
public class AttachDistroMediumToMachineLiveTest extends BaseVirtualBoxClientLiveTest {

   @Test
   public void testAttachMediumToMachine() throws Exception {
      String nodeName = "test-attach-medium-node-1";
      String controllerName = "My Controller";
      new UnregisterMachineIfExists(manager, CleanupMode.Full).apply(nodeName);
      String path = System.getProperty("user.home") + "/jclouds-virtualbox-test/test-attach-medium-1.vdi";
      new CreateAndRegisterMachineFromIsoIfNotAlreadyExists("Debian", nodeName, true, manager)
              .apply(nodeName);
      lockMachineAndApply(manager, LockType.Write, nodeName,
              new AddIDEControllerIfNotExists(controllerName));
      IMedium medium = new CreateMediumIfNotAlreadyExists(manager, "vdi", true).apply(path);
      lockMachineAndApply(manager, LockType.Write, nodeName,
              new AttachDistroMediumToMachine(controllerName, medium));
      new UnregisterMachineIfExists(manager, CleanupMode.Full).apply(nodeName);
      medium.close();
      assertFileCanBeDeleted(path);
   }

   @Test
   public void testAttachMediumToMachineTwice() throws Exception {
      String nodeName = "test-attach-medium-node-2";
      String controllerName = "My Controller";
      new UnregisterMachineIfExists(manager, CleanupMode.Full).apply(nodeName);
      String path = System.getProperty("user.home") + "/jclouds-virtualbox-test/test-attach-medium-2.vdi";
      new CreateAndRegisterMachineFromIsoIfNotAlreadyExists("Debian", nodeName, true, manager)
              .apply(nodeName);
      lockMachineAndApply(manager, LockType.Write, nodeName, new AddIDEControllerIfNotExists(controllerName));
      IMedium medium = new CreateMediumIfNotAlreadyExists(manager, "vdi", true).apply(path);
      lockMachineAndApply(manager, LockType.Write, nodeName, new AttachDistroMediumToMachine(controllerName, medium));
      lockMachineAndApply(manager, LockType.Write, nodeName, new AttachDistroMediumToMachine(controllerName, medium));
      new UnregisterMachineIfExists(manager, CleanupMode.Full).apply(nodeName);
      medium.close();
      assertFileCanBeDeleted(path);
   }

   private void assertFileCanBeDeleted(String path) {
      File file = new File(path);
      boolean mediumDeleted = file.delete();
      assertTrue(mediumDeleted);
   }
}
