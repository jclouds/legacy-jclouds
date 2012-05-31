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

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_NAME_SEPARATOR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_PREFIX;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.virtualbox.config.VirtualBoxComputeServiceContextModule;
import org.jclouds.virtualbox.util.MachineUtils;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.INATEngine;
import org.virtualbox_4_1.INetworkAdapter;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.NetworkAttachmentType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class IMachineToNodeMetadataTest {

   private static final String MASTER_NAME = "mock-image-of-a-server";

   @Test
   public void testCreateFromMaster() throws Exception {

      IMachine vm = createNiceMock(IMachine.class);

      expect(vm.getName()).andReturn(VIRTUALBOX_IMAGE_PREFIX + MASTER_NAME).anyTimes();
      expect(vm.getState()).andReturn(MachineState.PoweredOff).anyTimes();

      INetworkAdapter nat = createNiceMock(INetworkAdapter.class);
      INATEngine natEng = createNiceMock(INATEngine.class);

      expect(vm.getNetworkAdapter(eq(0l))).andReturn(nat).once();
      expect(vm.getNetworkAdapter(eq(1l))).andReturn(null).once();
      expect(nat.getAttachmentType()).andReturn(NetworkAttachmentType.NAT).once();
      expect(nat.getNatDriver()).andReturn(natEng).anyTimes();
      expect(natEng.getHostIP()).andReturn("127.0.0.1").once();
      expect(natEng.getRedirects()).andReturn(ImmutableList.of("0,1,127.0.0.1,2222,,22"));

      INetworkAdapter hostOnly = createNiceMock(INetworkAdapter.class);
      MachineUtils machineUtils = createNiceMock(MachineUtils.class);

      replay(vm, nat, natEng, hostOnly, machineUtils);

      NodeMetadata node = new IMachineToNodeMetadata(VirtualBoxComputeServiceContextModule.toPortableNodeStatus,
               machineUtils).apply(vm);

      assertEquals(MASTER_NAME, node.getName());
      assertEquals(1, node.getPrivateAddresses().size());
      assertEquals(1, node.getPublicAddresses().size());
      assertEquals("127.0.0.1", Iterables.get(node.getPublicAddresses(), 0));
      assertEquals(MastersLoadingCache.MASTER_PORT, node.getLoginPort());
      assertEquals("", node.getGroup());
   }

   @Test
   public void testCreateFromNode() throws Exception {

      IMachine vm = createNiceMock(IMachine.class);

      String group = "my-cluster-group";
      String name = "a-name-with-a-code-338";

      expect(vm.getName()).andReturn(
               VIRTUALBOX_NODE_PREFIX + MASTER_NAME + VIRTUALBOX_NODE_NAME_SEPARATOR + group
                        + VIRTUALBOX_NODE_NAME_SEPARATOR + name).anyTimes();
      expect(vm.getState()).andReturn(MachineState.PoweredOff).anyTimes();

      INetworkAdapter nat = createNiceMock(INetworkAdapter.class);
      INATEngine natEng = createNiceMock(INATEngine.class);
      
      INetworkAdapter hostOnly = createNiceMock(INetworkAdapter.class);

      expect(vm.getNetworkAdapter(eq(0l))).andReturn(nat).once();
      expect(vm.getNetworkAdapter(eq(1l))).andReturn(hostOnly).once();
      expect(nat.getAttachmentType()).andReturn(NetworkAttachmentType.NAT).once();
      expect(nat.getNatDriver()).andReturn(natEng).anyTimes();
      expect(natEng.getHostIP()).andReturn("127.0.0.1").once();
      expect(natEng.getRedirects()).andReturn(ImmutableList.of("0,1,127.0.0.1,3000,,22"));
      MachineUtils machineUtils = createNiceMock(MachineUtils.class);

      replay(vm, nat, natEng, hostOnly, machineUtils);

      NodeMetadata node = new IMachineToNodeMetadata(VirtualBoxComputeServiceContextModule.toPortableNodeStatus,
               machineUtils).apply(vm);

      assertEquals(name, node.getName());
      assertEquals(group, node.getGroup());
      assertEquals(1, node.getPublicAddresses().size());
   }
}
