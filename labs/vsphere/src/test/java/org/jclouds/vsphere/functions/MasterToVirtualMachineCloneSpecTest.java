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

package org.jclouds.vsphere.functions;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineSnapshotInfo;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.VirtualMachineSnapshot;

@Test(groups = "unit", testName = "VirtualMachineToHardwareTest")
public class MasterToVirtualMachineCloneSpecTest {

   @Test
   public void testMasterToVirtualMachineCloneSpecConversion() throws Exception {
      VirtualMachineSnapshotInfo info = createNiceMock(VirtualMachineSnapshotInfo.class);
      VirtualMachineSnapshot snapshot = createNiceMock(VirtualMachineSnapshot.class);
      Datastore datastore = createNiceMock(Datastore.class);
      ResourcePool resourcePool = createNiceMock(ResourcePool.class);
      VirtualMachine master = createNiceMock(VirtualMachine.class);

      expect(master.getSnapshot()).andReturn(info).anyTimes();
      expect(master.getCurrentSnapShot()).andReturn(snapshot).anyTimes();
      replay(master, datastore, info,  resourcePool);

      VirtualMachineCloneSpec cloneSpec = new MasterToVirtualMachineCloneSpec(resourcePool, datastore, "full").apply(master);

      assertTrue(cloneSpec.isPowerOn());
      assertFalse(cloneSpec.isTemplate());
   }

}
