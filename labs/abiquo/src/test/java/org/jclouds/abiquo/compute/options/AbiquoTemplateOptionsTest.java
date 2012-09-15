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

package org.jclouds.abiquo.compute.options;

import static org.jclouds.abiquo.domain.DomainWrapper.wrap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.easymock.EasyMock;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.network.Ip;
import org.jclouds.abiquo.domain.network.PrivateIp;
import org.jclouds.abiquo.domain.network.PrivateNetwork;
import org.jclouds.abiquo.domain.network.UnmanagedNetwork;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.server.core.infrastructure.network.PrivateIpDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;

/**
 * Unit tests for the {@link AbiquoTemplateOptions} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "AbiquoTemplateOptionsTest")
public class AbiquoTemplateOptionsTest
{
    public void testAs()
    {
        TemplateOptions options = new AbiquoTemplateOptions();
        assertEquals(options.as(AbiquoTemplateOptions.class), options);
    }

    public void testOverrideCores()
    {
        TemplateOptions options = new AbiquoTemplateOptions().overrideCores(5);
        assertEquals(options.as(AbiquoTemplateOptions.class).getOverrideCores(), Integer.valueOf(5));
    }

    public void testOverrideRam()
    {
        TemplateOptions options = new AbiquoTemplateOptions().overrideRam(2048);
        assertEquals(options.as(AbiquoTemplateOptions.class).getOverrideRam(),
            Integer.valueOf(2048));
    }

    public void testVncPassword()
    {
        TemplateOptions options = new AbiquoTemplateOptions().vncPassword("foo");
        assertEquals(options.as(AbiquoTemplateOptions.class).getVncPassword(), "foo");
    }

    public void testVirtualDatacenter()
    {
        TemplateOptions options = new AbiquoTemplateOptions().virtualDatacenter("foo");
        assertEquals(options.as(AbiquoTemplateOptions.class).getVirtualDatacenter(), "foo");
    }

    @SuppressWarnings("unchecked")
    public void testIps()
    {
        RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);

        PrivateIpDto dto1 = new PrivateIpDto();
        dto1.setIp("10.60.0.1");
        PrivateIpDto dto2 = new PrivateIpDto();
        dto2.setIp("10.60.0.2");

        PrivateIp ip1 = wrap(context, PrivateIp.class, dto1);
        PrivateIp ip2 = wrap(context, PrivateIp.class, dto2);

        TemplateOptions options = new AbiquoTemplateOptions().ips(ip1, ip2);

        Ip< ? , ? >[] ips = options.as(AbiquoTemplateOptions.class).getIps();
        assertNotNull(ips);
        assertEquals(ips[0].getIp(), "10.60.0.1");
        assertEquals(ips[1].getIp(), "10.60.0.2");
    }

    @SuppressWarnings("unchecked")
    public void testGatewayNetwork()
    {
        RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);

        VLANNetworkDto dto = new VLANNetworkDto();
        dto.setAddress("10.0.0.0");
        dto.setMask(24);
        dto.setGateway("10.0.0.1");
        dto.setType(NetworkType.INTERNAL);

        PrivateNetwork gateway = wrap(context, PrivateNetwork.class, dto);

        TemplateOptions options = new AbiquoTemplateOptions().gatewayNetwork(gateway);
        assertEquals(options.as(AbiquoTemplateOptions.class).getGatewayNetwork(), gateway);
    }

    @SuppressWarnings("unchecked")
    public void testUnmanagedIps()
    {
        RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);

        VLANNetworkDto dto1 = new VLANNetworkDto();
        dto1.setAddress("10.0.0.0");
        dto1.setMask(24);
        dto1.setGateway("10.0.0.1");
        dto1.setType(NetworkType.UNMANAGED);

        VLANNetworkDto dto2 = new VLANNetworkDto();
        dto2.setAddress("10.1.0.0");
        dto2.setMask(24);
        dto2.setGateway("10.1.0.1");
        dto2.setType(NetworkType.UNMANAGED);

        UnmanagedNetwork net1 = wrap(context, UnmanagedNetwork.class, dto1);
        UnmanagedNetwork net2 = wrap(context, UnmanagedNetwork.class, dto2);

        TemplateOptions options = new AbiquoTemplateOptions().unmanagedIps(net1, net2);

        UnmanagedNetwork[] nets = options.as(AbiquoTemplateOptions.class).getUnmanagedIps();
        assertNotNull(nets);
        assertEquals(nets[0].getAddress(), "10.0.0.0");
        assertEquals(nets[1].getAddress(), "10.1.0.0");
    }
}
