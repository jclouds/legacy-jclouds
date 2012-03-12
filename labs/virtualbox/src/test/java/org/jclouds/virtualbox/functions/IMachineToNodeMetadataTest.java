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

import org.jclouds.compute.domain.NodeMetadata;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.INATEngine;
import org.virtualbox_4_1.INetworkAdapter;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.NetworkAttachmentType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class IMachineToNodeMetadataTest {

   @Test
   public void testCreate() throws Exception {

      IMachine vm = createNiceMock(IMachine.class);

      expect(vm.getName()).andReturn("mocked-vm").anyTimes();
      expect(vm.getState()).andReturn(MachineState.PoweredOff).once();

      INetworkAdapter nat = createNiceMock(INetworkAdapter.class);
      INATEngine natEng = createNiceMock(INATEngine.class);

      expect(vm.getNetworkAdapter(eq(0l))).andReturn(nat).once();
      expect(nat.getAttachmentType()).andReturn(NetworkAttachmentType.NAT).once();
      expect(nat.getNatDriver()).andReturn(natEng).anyTimes();
      expect(natEng.getHostIP()).andReturn("127.0.0.1").once();
      expect(natEng.getRedirects()).andReturn(ImmutableList.of("0,1,127.0.0.1,3001,,22"));

      INetworkAdapter hostOnly = createNiceMock(INetworkAdapter.class);

      replay(vm, nat, natEng, hostOnly);

      NodeMetadata node = new IMachineToNodeMetadata().apply(vm);

      assertEquals("mocked-vm", node.getName());
      assertEquals(1, node.getPrivateAddresses().size());
      assertEquals((NodeCreator.VMS_NETWORK + 1), Iterables.get(node.getPrivateAddresses(), 0));
      assertEquals(1, node.getPublicAddresses().size());
      assertEquals("127.0.0.1", Iterables.get(node.getPublicAddresses(), 0));
      assertEquals(3001, node.getLoginPort());
   }
}
