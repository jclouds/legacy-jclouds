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
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.jclouds.virtualbox.domain.StorageController;
import org.testng.annotations.Test;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IStorageController;
import org.virtualbox_4_2.StorageBus;
import org.virtualbox_4_2.VBoxException;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AddIDEControllerIfNotExistsTest")
public class AddIDEControllerIfNotExistsTest {

   @Test
   public void testFine() throws Exception {
      IMachine vm = createMock(IMachine.class);

      String controllerName = "IDE Controller";
      StorageController storageController = StorageController.builder().bus(StorageBus.IDE).name(controllerName).build();

      expect(vm.addStorageController(controllerName, StorageBus.IDE)).andReturn(
              createNiceMock(IStorageController.class));
      vm.saveSettings();

      replay(vm);

      new AddIDEControllerIfNotExists(storageController).apply(vm);

      verify(vm);
   }

   @Test
   public void testAcceptableException() throws Exception {
      IMachine vm = createMock(IMachine.class);

      String controllerName = "IDE Controller";
      StorageController storageController = StorageController.builder().bus(StorageBus.IDE).name(controllerName).build();

      expect(vm.addStorageController(controllerName, StorageBus.IDE)).andThrow(
              new VBoxException(createNiceMock(Throwable.class),
                      "VirtualBox error: Storage controller named 'IDE Controller' already exists (0x80BB000C)"));

      replay(vm);

      new AddIDEControllerIfNotExists(storageController).apply(vm);

      verify(vm);
   }

   @Test(expectedExceptions = VBoxException.class)
   public void testUnacceptableException() throws Exception {
      IMachine vm = createMock(IMachine.class);

      String controllerName = "IDE Controller";
      StorageController storageController = StorageController.builder().bus(StorageBus.IDE).name(controllerName).build();

      expect(vm.addStorageController(controllerName, StorageBus.IDE)).andThrow(
              new VBoxException(createNiceMock(Throwable.class), "VirtualBox error: General Error"));

      replay(vm);

      new AddIDEControllerIfNotExists(storageController).apply(vm);

      verify(vm);
   }
}
