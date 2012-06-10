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
package org.jclouds.openstack.nova.v2_0.compute.loaders;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.compute.loaders.LoadFloatingIpsForInstance;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndId;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPClient;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", singleThreaded = true, testName = "LoadFloatingIpsForInstanceTest")
public class LoadFloatingIpsForInstanceTest {

   @Test
   public void testReturnsPublicIpOnMatch() throws Exception {
      NovaClient client = createMock(NovaClient.class);
      FloatingIPClient ipClient = createMock(FloatingIPClient.class);

      expect(client.getFloatingIPExtensionForZone("Zone")).andReturn(Optional.of(ipClient)).atLeastOnce();
      expect(ipClient.listFloatingIPs()).andReturn(
            ImmutableSet.<FloatingIP>of(FloatingIP.builder().id("1").ip("1.1.1.1").fixedIp("10.1.1.1").instanceId("i-blah").build()))
            .atLeastOnce();

      replay(client);
      replay(ipClient);

      LoadFloatingIpsForInstance parser = new LoadFloatingIpsForInstance(client);

      assertEquals(ImmutableSet.copyOf(parser.load(ZoneAndId.fromZoneAndId("Zone", "i-blah"))), ImmutableSet.of("1.1.1.1"));

      verify(client);
      verify(ipClient);
   }

   @Test
   public void testReturnsNullWhenNotFound() throws Exception {
      NovaClient client = createMock(NovaClient.class);
      FloatingIPClient ipClient = createMock(FloatingIPClient.class);

      expect(client.getFloatingIPExtensionForZone("Zone")).andReturn(Optional.of(ipClient)).atLeastOnce();

      expect(ipClient.listFloatingIPs()).andReturn(ImmutableSet.<FloatingIP>of()).atLeastOnce();

      replay(client);
      replay(ipClient);

      LoadFloatingIpsForInstance parser = new LoadFloatingIpsForInstance(client);

      assertFalse(parser.load(ZoneAndId.fromZoneAndId("Zone", "i-blah")).iterator().hasNext());

      verify(client);
      verify(ipClient);

   }

   @Test
   public void testReturnsNullWhenNotAssigned() throws Exception {
      NovaClient client = createMock(NovaClient.class);
      FloatingIPClient ipClient = createMock(FloatingIPClient.class);

      expect(client.getFloatingIPExtensionForZone("Zone")).andReturn(Optional.of(ipClient)).atLeastOnce();

      expect(ipClient.listFloatingIPs()).andReturn(
            ImmutableSet.<FloatingIP>of(FloatingIP.builder().id("1").ip("1.1.1.1").build()))
            .atLeastOnce();

      replay(client);
      replay(ipClient);

      LoadFloatingIpsForInstance parser = new LoadFloatingIpsForInstance(client);

      assertFalse(parser.load(ZoneAndId.fromZoneAndId("Zone", "i-blah")).iterator().hasNext());

      verify(client);
      verify(ipClient);

   }

}
