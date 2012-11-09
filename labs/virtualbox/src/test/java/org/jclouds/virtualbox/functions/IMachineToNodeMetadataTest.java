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

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_NAME_SEPARATOR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_PREFIX;
import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.virtualbox.config.VirtualBoxComputeServiceContextModule;
import org.jclouds.virtualbox.util.NetworkUtils;
import org.testng.annotations.Test;
import org.virtualbox_4_2.IGuest;
import org.virtualbox_4_2.IGuestOSType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.INATEngine;
import org.virtualbox_4_2.INetworkAdapter;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.MachineState;
import org.virtualbox_4_2.NetworkAttachmentType;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;

public class IMachineToNodeMetadataTest {

   private static final String MASTER_NAME = "mock-image-of-a-server";
   Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
   }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
            .getInstance(Json.class));

   @Test(enabled=false)
   public void testCreateFromMaster() throws Exception {

      IMachine vm = createNiceMock(IMachine.class);
      VirtualBoxManager vbm = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createNiceMock(IVirtualBox.class);
      IGuestOSType iGuestOSType = createNiceMock(IGuestOSType.class);
      INetworkAdapter nat = createNiceMock(INetworkAdapter.class);
      INATEngine natEng = createNiceMock(INATEngine.class);

      expect(vm.getName()).andReturn(VIRTUALBOX_IMAGE_PREFIX + MASTER_NAME).anyTimes();
      expect(vm.getState()).andReturn(MachineState.PoweredOff).anyTimes();
      expect(vm.getNetworkAdapter(eq(0l))).andReturn(nat).once();
      expect(vm.getNetworkAdapter(eq(1l))).andReturn(null).once();
      expect(nat.getAttachmentType()).andReturn(NetworkAttachmentType.NAT).once();
      expect(nat.getNATEngine()).andReturn(natEng).anyTimes();
      expect(natEng.getHostIP()).andReturn("127.0.0.1").once();
      expect(natEng.getRedirects()).andReturn(ImmutableList.of("0,1,127.0.0.1,2222,,22"));
      
      expect(vbm.getVBox()).andReturn(vBox).anyTimes();
      expect(vm.getOSTypeId()).andReturn("RedHat_64").anyTimes();
      expect(vBox.getGuestOSType(vm.getOSTypeId())).andReturn(iGuestOSType);
      
      INetworkAdapter hostOnly = createNiceMock(INetworkAdapter.class);
      NetworkUtils networkUtils = createNiceMock(NetworkUtils.class);

      replay(vm, vBox, iGuestOSType, nat, natEng, hostOnly, networkUtils);

      NodeMetadata node = new IMachineToNodeMetadata(Suppliers
              .ofInstance(vbm), VirtualBoxComputeServiceContextModule.toPortableNodeStatus,
            networkUtils, map).apply(vm);

      assertEquals(MASTER_NAME, node.getName());
      assertEquals(1, node.getPrivateAddresses().size());
      assertEquals(1, node.getPublicAddresses().size());
      assertEquals("127.0.0.1", Iterables.get(node.getPublicAddresses(), 0));
      assertEquals(NetworkUtils.MASTER_PORT, node.getLoginPort());
      assertEquals("", node.getGroup());
   }

   @Test(enabled=false)
   public void testCreateFromNode() throws Exception {

      IMachine vm = createNiceMock(IMachine.class);
      VirtualBoxManager vbm = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createNiceMock(IVirtualBox.class);
      expect(vbm.getVBox()).andReturn(vBox).anyTimes();
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
      expect(nat.getNATEngine()).andReturn(natEng).anyTimes();
      expect(natEng.getHostIP()).andReturn("127.0.0.1").once();
      expect(natEng.getRedirects()).andReturn(ImmutableList.of("0,1,127.0.0.1,3000,,22"));
      NetworkUtils networkUtils = createNiceMock(NetworkUtils.class);

      replay(vm, nat, natEng, hostOnly, networkUtils);

      NodeMetadata node = new IMachineToNodeMetadata(Suppliers
              .ofInstance(vbm), VirtualBoxComputeServiceContextModule.toPortableNodeStatus,
            networkUtils, map).apply(vm);

      assertEquals(name, node.getName());
      assertEquals(group, node.getGroup());
      assertEquals(1, node.getPublicAddresses().size());
   }
}
