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

import static org.jclouds.abiquo.domain.DomainWrapper.wrap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.easymock.EasyMock;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Volume;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;

/**
 * Unit tests for the {@link VirtualMachineTemplateToHardware} function.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "VirtualMachineTemplateToHardwareTest")
public class VirtualMachineTemplateToHardwareTest
{
    @SuppressWarnings("unchecked")
    public void testVirtualMachineTemplateToHardware()
    {
        RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
        VirtualMachineTemplateToHardware function = new VirtualMachineTemplateToHardware();

        // VirtualMachineTemplate domain object does not have a builder, it is read only
        VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
        dto.setId(5);
        dto.setName("Template");
        dto.setDescription("Template description");
        dto.setHdRequired(50L * 1024 * 1024 * 1024); // 50 GB
        dto.setCpuRequired(5);
        dto.setRamRequired(2048);
        dto.addLink(new RESTLink("edit", "http://foo/bar"));

        Hardware hardware = function.apply(wrap(context, VirtualMachineTemplate.class, dto));

        assertEquals(hardware.getId(), dto.getId().toString());
        assertEquals(hardware.getName(), dto.getName());
        assertEquals(hardware.getUri(), URI.create("http://foo/bar"));

        assertEquals(hardware.getRam(), dto.getRamRequired());
        assertEquals(hardware.getProcessors().size(), 1);
        assertEquals(hardware.getProcessors().get(0).getCores(), (double) dto.getCpuRequired());
        assertEquals(hardware.getProcessors().get(0).getSpeed(),
            VirtualMachineTemplateToHardware.DEFAULT_CORE_SPEED);

        assertEquals(hardware.getVolumes().size(), 1);
        assertEquals(hardware.getVolumes().get(0).getSize(), 50F);
        assertEquals(hardware.getVolumes().get(0).getType(), Volume.Type.LOCAL);
        assertEquals(hardware.getVolumes().get(0).isBootDevice(), true);
        assertEquals(hardware.getVolumes().get(0).isDurable(), false);
    }

    @SuppressWarnings("unchecked")
    public void testConvertWithoutEditLink()
    {
        RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
        VirtualMachineTemplateToHardware function = new VirtualMachineTemplateToHardware();

        // VirtualMachineTemplate domain object does not have a builder, it is read only
        VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
        dto.setId(5);
        dto.setName("Template");
        dto.setDescription("Template description");
        dto.setHdRequired(50L * 1024 * 1024 * 1024); // 50 GB
        dto.setCpuRequired(5);
        dto.setRamRequired(2048);

        Hardware hardware = function.apply(wrap(context, VirtualMachineTemplate.class, dto));

        assertNull(hardware.getUri());
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = NullPointerException.class)
    public void testConvertWithoutId()
    {
        RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
        VirtualMachineTemplateToHardware function = new VirtualMachineTemplateToHardware();

        // VirtualMachineTemplate domain object does not have a builder, it is read only
        VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
        function.apply(wrap(context, VirtualMachineTemplate.class, dto));
    }

    @SuppressWarnings("unchecked")
    public void testConvertWithoutCpu()
    {
        RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
        VirtualMachineTemplateToHardware function = new VirtualMachineTemplateToHardware();

        // VirtualMachineTemplate domain object does not have a builder, it is read only
        VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
        dto.setId(5);
        dto.setName("Template");
        dto.setDescription("Template description");
        dto.setHdRequired(50L * 1024 * 1024 * 1024); // 50 GB
        dto.setRamRequired(2048);

        Hardware hardware = function.apply(wrap(context, VirtualMachineTemplate.class, dto));

        assertEquals(hardware.getProcessors().size(), 1);
        assertEquals(hardware.getProcessors().get(0).getCores(), 0D);
    }

    @SuppressWarnings("unchecked")
    public void testConvertWithoutRam()
    {
        RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
        VirtualMachineTemplateToHardware function = new VirtualMachineTemplateToHardware();

        // VirtualMachineTemplate domain object does not have a builder, it is read only
        VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
        dto.setId(5);
        dto.setName("Template");
        dto.setDescription("Template description");
        dto.setHdRequired(50L * 1024 * 1024 * 1024); // 50 GB
        dto.setCpuRequired(5);

        Hardware hardware = function.apply(wrap(context, VirtualMachineTemplate.class, dto));

        assertEquals(hardware.getRam(), 0);
    }

    @SuppressWarnings("unchecked")
    public void testConvertWithoutHd()
    {
        RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
        VirtualMachineTemplateToHardware function = new VirtualMachineTemplateToHardware();

        // VirtualMachineTemplate domain object does not have a builder, it is read only
        VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
        dto.setId(5);
        dto.setName("Template");
        dto.setDescription("Template description");
        dto.setCpuRequired(5);
        dto.setRamRequired(2048);

        Hardware hardware = function.apply(wrap(context, VirtualMachineTemplate.class, dto));

        assertEquals(hardware.getVolumes().size(), 1);
        assertEquals(hardware.getVolumes().get(0).getSize(), 0F);
    }
}
