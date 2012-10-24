/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.compute.functions;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.easymock.EasyMock;
import org.jclouds.abiquo.domain.cloud.VirtualAppliance;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplateInVirtualDatacenter;
import org.jclouds.abiquo.domain.network.ExternalIp;
import org.jclouds.abiquo.domain.network.Ip;
import org.jclouds.abiquo.domain.network.PrivateIp;
import org.jclouds.abiquo.domain.network.PublicIp;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.domain.Location;
import org.jclouds.rest.RestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.infrastructure.network.ExternalIpDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Unit tests for the {@link VirtualMachineToNodeMetadata} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "VirtualMachineToNodeMetadataTest")
public class VirtualMachineToNodeMetadataTest {
   private VirtualMachineToNodeMetadata function;

   private VirtualMachineWithNodeExtendedDto vm;

   private PrivateIpDto privNic;

   private PublicIpDto pubNic;

   private ExternalIpDto extNic;

   private Hardware hardware;

   @BeforeMethod
   public void setup() {
      vm = new VirtualMachineWithNodeExtendedDto();
      vm.setNodeName("VM");
      vm.setName("Internal name");
      vm.setId(5);
      vm.setVdrpPort(22);
      vm.setRam(2048);
      vm.setCpu(2);
      vm.setState(VirtualMachineState.ON);
      vm.addLink(new RESTLink("edit", "http://foo/bar"));

      privNic = new PrivateIpDto();
      privNic.setIp("192.168.1.2");
      privNic.setMac("2a:6e:40:69:84:e0");

      pubNic = new PublicIpDto();
      pubNic.setIp("80.80.80.80");
      pubNic.setMac("2a:6e:40:69:84:e1");

      extNic = new ExternalIpDto();
      extNic.setIp("10.10.10.10");
      extNic.setMac("2a:6e:40:69:84:e2");

      hardware = new HardwareBuilder() //
            .ids("1") //
            .build();

      function = new VirtualMachineToNodeMetadata(templateToImage(), templateToHardware(), stateToNodeState(),
            virtualDatacenterToLocation());
   }

   public void testVirtualMachineToNodeMetadata() {
      VirtualAppliance vapp = EasyMock.createMock(VirtualAppliance.class);
      VirtualMachine mockVm = mockVirtualMachine(vapp);

      NodeMetadata node = function.apply(mockVm);

      verify(mockVm);

      assertEquals(node.getId(), vm.getId().toString());
      assertEquals(node.getUri(), URI.create("http://foo/bar"));
      assertEquals(node.getName(), vm.getNodeName());
      assertEquals(node.getGroup(), "VAPP");
      assertEquals(node.getLocation().getId(), "1");
      assertEquals(node.getLocation().getDescription(), "Mock Location");
      assertEquals(node.getImageId(), "1");
      assertEquals(node.getHardware().getId(), "1");
      assertEquals(node.getHardware().getRam(), vm.getRam());
      assertEquals(node.getHardware().getProcessors().get(0).getCores(), (double) vm.getCpu());
      assertEquals(node.getLoginPort(), vm.getVdrpPort());
      assertEquals(node.getPrivateAddresses().size(), 1);
      assertEquals(node.getPublicAddresses().size(), 2);
      assertEquals(Iterables.get(node.getPrivateAddresses(), 0), privNic.getIp());
      assertEquals(Iterables.get(node.getPublicAddresses(), 0), pubNic.getIp());
      assertEquals(Iterables.get(node.getPublicAddresses(), 1), extNic.getIp());
   }

   private VirtualMachineTemplateToImage templateToImage() {
      VirtualMachineTemplateToImage templateToImage = EasyMock.createMock(VirtualMachineTemplateToImage.class);
      Image image = EasyMock.createMock(Image.class);

      expect(image.getId()).andReturn("1");
      expect(image.getOperatingSystem()).andReturn(null);
      expect(templateToImage.apply(anyObject(VirtualMachineTemplate.class))).andReturn(image);

      replay(image);
      replay(templateToImage);

      return templateToImage;
   }

   private VirtualMachineTemplateInVirtualDatacenterToHardware templateToHardware() {
      VirtualMachineTemplateInVirtualDatacenterToHardware virtualMachineTemplateToHardware = EasyMock
            .createMock(VirtualMachineTemplateInVirtualDatacenterToHardware.class);

      expect(virtualMachineTemplateToHardware.apply(anyObject(VirtualMachineTemplateInVirtualDatacenter.class)))
            .andReturn(hardware);

      replay(virtualMachineTemplateToHardware);

      return virtualMachineTemplateToHardware;
   }

   private VirtualDatacenterToLocation virtualDatacenterToLocation() {
      VirtualDatacenterToLocation datacenterToLocation = EasyMock.createMock(VirtualDatacenterToLocation.class);
      Location location = EasyMock.createMock(Location.class);

      expect(location.getId()).andReturn("1");
      expect(location.getDescription()).andReturn("Mock Location");

      expect(datacenterToLocation.apply(anyObject(VirtualDatacenter.class))).andReturn(location);

      replay(location);
      replay(datacenterToLocation);

      return datacenterToLocation;
   }

   private VirtualMachineStateToNodeState stateToNodeState() {
      VirtualMachineStateToNodeState stateToNodeState = EasyMock.createMock(VirtualMachineStateToNodeState.class);
      expect(stateToNodeState.apply(anyObject(VirtualMachineState.class))).andReturn(Status.RUNNING);
      replay(stateToNodeState);
      return stateToNodeState;
   }

   private VirtualDatacenter mockVirtualDatacenter() {
      VirtualDatacenter vdc = EasyMock.createMock(VirtualDatacenter.class);
      expect(vdc.getHypervisorType()).andReturn(HypervisorType.VMX_04);
      expect(vdc.getDatacenter()).andReturn(null);
      replay(vdc);
      return vdc;
   }

   private VirtualMachineTemplate mockTemplate() {
      return EasyMock.createMock(VirtualMachineTemplate.class);
   }

   @SuppressWarnings("unchecked")
   private VirtualMachine mockVirtualMachine(final VirtualAppliance vapp) {
      VirtualMachine mockVm = EasyMock.createMock(VirtualMachine.class);

      Ip<?, ?> mockPrivNic = wrap(EasyMock.createMock(RestContext.class), PrivateIp.class, privNic);
      Ip<?, ?> mockPubNic = wrap(EasyMock.createMock(RestContext.class), PublicIp.class, pubNic);
      Ip<?, ?> mockExtNic = wrap(EasyMock.createMock(RestContext.class), ExternalIp.class, extNic);

      expect(mockVm.getId()).andReturn(vm.getId());
      expect(mockVm.getURI()).andReturn(URI.create(vm.getEditLink().getHref()));
      expect(mockVm.getNameLabel()).andReturn(vm.getNodeName());
      expect(mockVm.getTemplate()).andReturn(mockTemplate());
      expect(mockVm.getState()).andReturn(vm.getState());
      expect(mockVm.listAttachedNics()).andReturn(ImmutableList.<Ip<?, ?>> of(mockPubNic, mockPrivNic, mockExtNic));
      expect(mockVm.getVirtualAppliance()).andReturn(vapp);
      expect(vapp.getName()).andReturn("VAPP");
      expect(mockVm.getVirtualDatacenter()).andReturn(mockVirtualDatacenter());
      expect(mockVm.getRam()).andReturn(vm.getRam());
      expect(mockVm.getCpu()).andReturn(vm.getCpu());

      replay(mockVm);
      replay(vapp);

      return mockVm;
   }
}
