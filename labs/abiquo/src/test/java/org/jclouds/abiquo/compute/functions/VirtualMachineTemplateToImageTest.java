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
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;

/**
 * Unit tests for the {@link VirtualMachineTemplateToImage} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "VirtualMachineTemplateToImageTest")
public class VirtualMachineTemplateToImageTest
{
    @SuppressWarnings("unchecked")
    public void testVirtualMachineTemplateToImage()
    {
        RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
        VirtualMachineTemplateToImage function = new VirtualMachineTemplateToImage();

        // VirtualMachineTemplate domain object does not have a builder, it is read only
        VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
        dto.setId(5);
        dto.setName("Template");
        dto.setDescription("Template description");
        dto.addLink(new RESTLink("diskfile", "http://foo/bar"));

        Image image = function.apply(wrap(context, VirtualMachineTemplate.class, dto));

        assertEquals(image.getId(), dto.getId().toString());
        assertEquals(image.getName(), dto.getName());
        assertEquals(image.getDescription(), dto.getDescription());
        assertEquals(image.getUri(), URI.create("http://foo/bar"));
        assertEquals(image.getOperatingSystem(),
            OperatingSystem.builder().description(dto.getName()).build());
    }

    @SuppressWarnings("unchecked")
    public void testConvertWithoutDownloadLink()
    {
        RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
        VirtualMachineTemplateToImage function = new VirtualMachineTemplateToImage();

        // VirtualMachineTemplate domain object does not have a builder, it is read only
        VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
        dto.setId(5);
        dto.setName("Template");
        dto.setDescription("Template description");

        Image image = function.apply(wrap(context, VirtualMachineTemplate.class, dto));

        assertNull(image.getUri());
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = NullPointerException.class)
    public void testConvertWithoutId()
    {
        RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
        VirtualMachineTemplateToImage function = new VirtualMachineTemplateToImage();

        // VirtualMachineTemplate domain object does not have a builder, it is read only
        VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
        function.apply(wrap(context, VirtualMachineTemplate.class, dto));
    }
}
