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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.vsphere.config.VSphereComputeServiceContextModule;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineToolsStatus;
import com.vmware.vim25.mo.VirtualMachine;

public class VirtualMachineToNodeMetadataTest {

   @Test
   public void testCreateFromMaster() throws Exception {
      String masterName = "mock-image-of-a-server";
      String ipAddress = "127.0.0.1";
      VirtualMachine vm = createNiceMock(VirtualMachine.class);
      GuestInfo guestInfo = createNiceMock(GuestInfo.class);
      VirtualMachineRuntimeInfo runtime = createNiceMock(VirtualMachineRuntimeInfo.class);
      GroupNamingConvention.Factory namingConventionFactory = createMock(GroupNamingConvention.Factory.class);
      GroupNamingConvention namingConvention = createMock(GroupNamingConvention.class);
      expect(namingConventionFactory.create()).andReturn(namingConvention).anyTimes();
      expect(namingConvention.sharedNameForGroup("group")).andReturn("jclouds#group").anyTimes();
      expect(namingConvention.extractGroup("mock-image-of-a-server")).andReturn("group").anyTimes();
      replay(namingConventionFactory);
      replay(namingConvention);

      expect(vm.getName()).andReturn(masterName).anyTimes();
      expect(vm.getGuest()).andReturn(guestInfo).anyTimes();
      expect(guestInfo.getIpAddress()).andReturn(ipAddress).anyTimes();
      expect(guestInfo.getToolsStatus()).andReturn(VirtualMachineToolsStatus.toolsOk).anyTimes();
      expect(vm.getRuntime()).andReturn(runtime).anyTimes();
      expect(runtime.getPowerState()).andReturn(VirtualMachinePowerState.poweredOn).anyTimes();
      
      replay(vm, guestInfo, runtime);

      NodeMetadata node = new VirtualMachineToNodeMetadata(VSphereComputeServiceContextModule.toPortableNodeStatus, namingConventionFactory).apply(vm);
      assertEquals(node.getName(), masterName);
      assertEquals(node.getPrivateAddresses().size(), 1);
      assertEquals(node.getPublicAddresses().size(), 1);
      assertEquals(Iterables.get(node.getPublicAddresses(), 0), ipAddress);
   }

}