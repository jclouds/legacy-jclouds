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

import org.testng.annotations.Test;
import org.virtualbox_4_1.*;

import java.util.Collections;
import java.util.List;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;

@Test(groups = "unit", testName = "UnregisterMachineIfExistsTest")
public class UnregisterMachineIfExistsTest {

   @Test
   public void testUnregisterExistingMachine() throws Exception {
      VirtualBoxManager manager = createMock(VirtualBoxManager.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMachine registeredMachine = createMock(IMachine.class);
      List<IMedium> mediums = Collections.emptyList();

      CleanupMode mode = CleanupMode.Full;
      String vmName = "jclouds-image-example-machine";

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.findMachine(vmName)).andReturn(registeredMachine);

      expect(registeredMachine.unregister(mode)).andReturn(mediums);
      expectLastCall().anyTimes();

      replay(manager, vBox, registeredMachine);

      new UnregisterMachineIfExists(manager, mode).apply(vmName);
   }

}
