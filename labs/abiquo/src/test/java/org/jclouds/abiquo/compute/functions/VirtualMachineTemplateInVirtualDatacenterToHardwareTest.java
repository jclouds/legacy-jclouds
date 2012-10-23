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
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.easymock.EasyMock;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplateInVirtualDatacenter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Volume;
import org.jclouds.domain.Location;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.google.common.base.Function;

/**
 * Unit tests for the
 * {@link VirtualMachineTemplateInVirtualDatacenterToHardware} function.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "VirtualMachineTemplateInVirtualDatacenterToHardwareTest")
public class VirtualMachineTemplateInVirtualDatacenterToHardwareTest {
   @SuppressWarnings("unchecked")
   public void testVirtualMachineTemplateToHardware() {
      RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
      Function<VirtualDatacenter, Location> vdcToLocation = mockVirtualDatacenterToLocation();
      VirtualMachineTemplateInVirtualDatacenterToHardware function = new VirtualMachineTemplateInVirtualDatacenterToHardware(
            vdcToLocation);

      VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
      dto.setId(5);
      dto.setName("Template");
      dto.setDescription("Template description");
      dto.setHdRequired(50L * 1024 * 1024 * 1024); // 50 GB
      dto.setCpuRequired(5);
      dto.setRamRequired(2048);
      dto.addLink(new RESTLink("edit", "http://foo/bar"));
      VirtualMachineTemplate template = wrap(context, VirtualMachineTemplate.class, dto);

      VirtualDatacenterDto vdcDto = new VirtualDatacenterDto();
      vdcDto.setId(6);
      vdcDto.setHypervisorType(HypervisorType.VMX_04);
      VirtualDatacenter vdc = wrap(context, VirtualDatacenter.class, vdcDto);

      Hardware hardware = function.apply(new VirtualMachineTemplateInVirtualDatacenter(template, vdc));

      verify(vdcToLocation);

      assertEquals(hardware.getProviderId(), template.getId().toString());
      assertEquals(hardware.getId(), template.getId() + "/" + vdc.getId());
      assertEquals(hardware.getName(), template.getName());
      assertEquals(hardware.getUri(), URI.create("http://foo/bar"));

      assertEquals(hardware.getRam(), template.getRamRequired());
      assertEquals(hardware.getProcessors().size(), 1);
      assertEquals(hardware.getProcessors().get(0).getCores(), (double) template.getCpuRequired());
      assertEquals(hardware.getProcessors().get(0).getSpeed(),
            VirtualMachineTemplateInVirtualDatacenterToHardware.DEFAULT_CORE_SPEED);

      assertEquals(hardware.getVolumes().size(), 1);
      assertEquals(hardware.getVolumes().get(0).getSize(), 50F);
      assertEquals(hardware.getVolumes().get(0).getType(), Volume.Type.LOCAL);
      assertEquals(hardware.getVolumes().get(0).isBootDevice(), true);
      assertEquals(hardware.getVolumes().get(0).isDurable(), false);
   }

   @SuppressWarnings("unchecked")
   public void testConvertWithoutEditLink() {
      RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
      Function<VirtualDatacenter, Location> vdcToLocation = mockVirtualDatacenterToLocation();
      VirtualMachineTemplateInVirtualDatacenterToHardware function = new VirtualMachineTemplateInVirtualDatacenterToHardware(
            vdcToLocation);

      VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
      dto.setId(5);
      dto.setName("Template");
      dto.setDescription("Template description");
      dto.setHdRequired(50L * 1024 * 1024 * 1024); // 50 GB
      dto.setCpuRequired(5);
      dto.setRamRequired(2048);
      VirtualMachineTemplate template = wrap(context, VirtualMachineTemplate.class, dto);

      VirtualDatacenterDto vdcDto = new VirtualDatacenterDto();
      vdcDto.setId(6);
      vdcDto.setHypervisorType(HypervisorType.VMX_04);
      VirtualDatacenter vdc = wrap(context, VirtualDatacenter.class, vdcDto);

      Hardware hardware = function.apply(new VirtualMachineTemplateInVirtualDatacenter(template, vdc));

      verify(vdcToLocation);

      assertNull(hardware.getUri());
   }

   @SuppressWarnings("unchecked")
   @Test(expectedExceptions = NullPointerException.class)
   public void testConvertWithoutId() {
      RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
      Function<VirtualDatacenter, Location> vdcToLocation = mockVirtualDatacenterToLocation();
      VirtualMachineTemplateInVirtualDatacenterToHardware function = new VirtualMachineTemplateInVirtualDatacenterToHardware(
            vdcToLocation);

      VirtualMachineTemplate template = wrap(context, VirtualMachineTemplate.class, new VirtualMachineTemplateDto());
      VirtualDatacenter vdc = wrap(context, VirtualDatacenter.class, new VirtualDatacenterDto());

      function.apply(new VirtualMachineTemplateInVirtualDatacenter(template, vdc));
   }

   @SuppressWarnings("unchecked")
   public void testConvertWithoutCpu() {
      RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
      Function<VirtualDatacenter, Location> vdcToLocation = mockVirtualDatacenterToLocation();
      VirtualMachineTemplateInVirtualDatacenterToHardware function = new VirtualMachineTemplateInVirtualDatacenterToHardware(
            vdcToLocation);

      VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
      dto.setId(5);
      dto.setName("Template");
      dto.setDescription("Template description");
      dto.setHdRequired(50L * 1024 * 1024 * 1024); // 50 GB
      dto.setRamRequired(2048);
      VirtualMachineTemplate template = wrap(context, VirtualMachineTemplate.class, dto);

      VirtualDatacenterDto vdcDto = new VirtualDatacenterDto();
      vdcDto.setId(6);
      vdcDto.setHypervisorType(HypervisorType.VMX_04);
      VirtualDatacenter vdc = wrap(context, VirtualDatacenter.class, vdcDto);

      Hardware hardware = function.apply(new VirtualMachineTemplateInVirtualDatacenter(template, vdc));

      verify(vdcToLocation);

      assertEquals(hardware.getProcessors().size(), 1);
      assertEquals(hardware.getProcessors().get(0).getCores(), 0D);
   }

   @SuppressWarnings("unchecked")
   public void testConvertWithoutRam() {
      RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
      Function<VirtualDatacenter, Location> vdcToLocation = mockVirtualDatacenterToLocation();
      VirtualMachineTemplateInVirtualDatacenterToHardware function = new VirtualMachineTemplateInVirtualDatacenterToHardware(
            vdcToLocation);

      VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
      dto.setId(5);
      dto.setName("Template");
      dto.setDescription("Template description");
      dto.setHdRequired(50L * 1024 * 1024 * 1024); // 50 GB
      dto.setCpuRequired(5);
      VirtualMachineTemplate template = wrap(context, VirtualMachineTemplate.class, dto);

      VirtualDatacenterDto vdcDto = new VirtualDatacenterDto();
      vdcDto.setId(6);
      vdcDto.setHypervisorType(HypervisorType.VMX_04);
      VirtualDatacenter vdc = wrap(context, VirtualDatacenter.class, vdcDto);

      Hardware hardware = function.apply(new VirtualMachineTemplateInVirtualDatacenter(template, vdc));

      verify(vdcToLocation);

      assertEquals(hardware.getRam(), 0);
   }

   @SuppressWarnings("unchecked")
   public void testConvertWithoutHd() {
      RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
      Function<VirtualDatacenter, Location> vdcToLocation = mockVirtualDatacenterToLocation();
      VirtualMachineTemplateInVirtualDatacenterToHardware function = new VirtualMachineTemplateInVirtualDatacenterToHardware(
            vdcToLocation);

      // VirtualMachineTemplate domain object does not have a builder, it is
      // read only
      VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
      dto.setId(5);
      dto.setName("Template");
      dto.setDescription("Template description");
      dto.setCpuRequired(5);
      dto.setRamRequired(2048);
      VirtualMachineTemplate template = wrap(context, VirtualMachineTemplate.class, dto);

      VirtualDatacenterDto vdcDto = new VirtualDatacenterDto();
      vdcDto.setId(6);
      vdcDto.setHypervisorType(HypervisorType.VMX_04);
      VirtualDatacenter vdc = wrap(context, VirtualDatacenter.class, vdcDto);

      Hardware hardware = function.apply(new VirtualMachineTemplateInVirtualDatacenter(template, vdc));

      verify(vdcToLocation);

      assertEquals(hardware.getVolumes().size(), 1);
      assertEquals(hardware.getVolumes().get(0).getSize(), 0F);
   }

   @SuppressWarnings("unchecked")
   private static Function<VirtualDatacenter, Location> mockVirtualDatacenterToLocation() {
      Function<VirtualDatacenter, Location> mock = EasyMock.createMock(Function.class);
      expect(mock.apply(anyObject(VirtualDatacenter.class))).andReturn(null);
      replay(mock);
      return mock;
   }
}
