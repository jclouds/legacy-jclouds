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
package org.jclouds.openstack.nova.v1_1.compute.functions;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.compute.domain.RegionAndName;
import org.jclouds.openstack.nova.v1_1.domain.FloatingIP;
import org.jclouds.openstack.nova.v1_1.extensions.FloatingIPClient;
import org.testng.annotations.Test;

import java.util.Set;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", singleThreaded = true, testName = "LoadFloatingIpsForInstanceTest")
public class LoadFloatingIpsForInstanceTest {

   @Test
   public void testReturnsPublicIpOnMatch() throws Exception {
      NovaClient client = createMock(NovaClient.class);
      FloatingIPClient ipClient = createMock(FloatingIPClient.class);

      expect(client.getConfiguredRegions()).andReturn(ImmutableSet.of("region")).atLeastOnce();
      expect(client.getFloatingIPExtensionForRegion("region")).andReturn(Optional.of(ipClient)).atLeastOnce();
      expect(ipClient.listFloatingIPs()).andReturn(
            ImmutableSet.<FloatingIP>of(FloatingIP.builder().id("1").ip("1.1.1.1").fixedIp("10.1.1.1").instanceId("i-blah").build()))
            .atLeastOnce();

      replay(client);
      replay(ipClient);

      LoadFloatingIpsForInstance parser = new LoadFloatingIpsForInstance(client);

      assertEquals(ImmutableSet.copyOf(parser.load(new RegionAndName("region", "i-blah"))), ImmutableSet.of("1.1.1.1"));

      verify(client);
      verify(ipClient);
   }

   @Test
   public void testReturnsNullWhenNotFound() throws Exception {
      NovaClient client = createMock(NovaClient.class);
      FloatingIPClient ipClient = createMock(FloatingIPClient.class);

      expect(client.getConfiguredRegions()).andReturn(ImmutableSet.of("region")).atLeastOnce();
      expect(client.getFloatingIPExtensionForRegion("region")).andReturn(Optional.of(ipClient)).atLeastOnce();

      expect(ipClient.listFloatingIPs()).andReturn(ImmutableSet.<FloatingIP>of()).atLeastOnce();

      replay(client);
      replay(ipClient);

      LoadFloatingIpsForInstance parser = new LoadFloatingIpsForInstance(client);

      assertFalse(parser.load(new RegionAndName("region", "i-blah")).iterator().hasNext());

      verify(client);
      verify(ipClient);

   }

   @Test
   public void testReturnsNullWhenNotAssigned() throws Exception {
      NovaClient client = createMock(NovaClient.class);
      FloatingIPClient ipClient = createMock(FloatingIPClient.class);

      expect(client.getConfiguredRegions()).andReturn(ImmutableSet.of("region")).atLeastOnce();
      expect(client.getFloatingIPExtensionForRegion("region")).andReturn(Optional.of(ipClient)).atLeastOnce();

      expect(ipClient.listFloatingIPs()).andReturn(
            ImmutableSet.<FloatingIP>of(FloatingIP.builder().id("1").ip("1.1.1.1").build()))
            .atLeastOnce();

      replay(client);
      replay(ipClient);

      LoadFloatingIpsForInstance parser = new LoadFloatingIpsForInstance(client);

      assertFalse(parser.load(new RegionAndName("region", "i-blah")).iterator().hasNext());

      verify(client);
      verify(ipClient);

   }

}
