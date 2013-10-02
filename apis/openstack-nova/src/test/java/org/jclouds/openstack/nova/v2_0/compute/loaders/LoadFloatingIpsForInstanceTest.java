/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.nova.v2_0.compute.loaders;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndId;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", singleThreaded = true, testName = "LoadFloatingIpsForInstanceTest")
public class LoadFloatingIpsForInstanceTest {

   @Test
   public void testReturnsPublicIpOnMatch() throws Exception {
      NovaApi api = createMock(NovaApi.class);
      FloatingIPApi ipApi = createMock(FloatingIPApi.class);
      FloatingIP testIp = FloatingIP.builder().id("1").ip("1.1.1.1").fixedIp("10.1.1.1").instanceId("i-blah").build();

      expect(api.getFloatingIPExtensionForZone("Zone")).andReturn((Optional) Optional.of(ipApi)).atLeastOnce();
      expect(ipApi.list()).andReturn((FluentIterable) FluentIterable.from(ImmutableSet.<FloatingIP> of(testIp)))
               .atLeastOnce();

      replay(api);
      replay(ipApi);

      LoadFloatingIpsForInstance parser = new LoadFloatingIpsForInstance(api);

      assertEquals(ImmutableSet.copyOf(parser.load(ZoneAndId.fromZoneAndId("Zone", "i-blah"))), ImmutableSet.of(testIp));

      verify(api);
      verify(ipApi);
   }

   @Test
   public void testReturnsNullWhenNotFound() throws Exception {
      NovaApi api = createMock(NovaApi.class);
      FloatingIPApi ipApi = createMock(FloatingIPApi.class);

      expect(api.getFloatingIPExtensionForZone("Zone")).andReturn((Optional) Optional.of(ipApi)).atLeastOnce();

      expect(ipApi.list()).andReturn((FluentIterable) FluentIterable.from(ImmutableSet.<FloatingIP> of()))
      .atLeastOnce();

      replay(api);
      replay(ipApi);

      LoadFloatingIpsForInstance parser = new LoadFloatingIpsForInstance(api);

      assertFalse(parser.load(ZoneAndId.fromZoneAndId("Zone", "i-blah")).iterator().hasNext());

      verify(api);
      verify(ipApi);

   }

   @Test
   public void testReturnsNullWhenNotAssigned() throws Exception {
      NovaApi api = createMock(NovaApi.class);
      FloatingIPApi ipApi = createMock(FloatingIPApi.class);

      expect(api.getFloatingIPExtensionForZone("Zone")).andReturn((Optional) Optional.of(ipApi)).atLeastOnce();

      expect(ipApi.list()).andReturn((FluentIterable) FluentIterable.from(ImmutableSet.<FloatingIP> of(FloatingIP.builder().id("1").ip("1.1.1.1").build())))
      .atLeastOnce();

      replay(api);
      replay(ipApi);

      LoadFloatingIpsForInstance parser = new LoadFloatingIpsForInstance(api);

      assertFalse(parser.load(ZoneAndId.fromZoneAndId("Zone", "i-blah")).iterator().hasNext());

      verify(api);
      verify(ipApi);

   }

}
