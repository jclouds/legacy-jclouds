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
package org.jclouds.glesys.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.glesys.domain.IpDetails;
import org.jclouds.glesys.internal.BaseGleSYSApiWithAServerLiveTest;
import org.jclouds.glesys.options.ListIpOptions;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code IpApi}
 *
 * @author Adrian Cole, Mattias Holmqvist
 */
@Test(groups = "live", testName = "IpApiLiveTest", singleThreaded = true)
public class IpApiLiveTest extends BaseGleSYSApiWithAServerLiveTest {

   @BeforeMethod
   public void setupApi() {
      api = gleContext.getApi().getIpApi();
   }

   @AfterGroups(groups = {"live"})
   public void releaseIp() {
      if (reservedIp != null) {
         api.release(reservedIp.getAddress());
      }
   }

   private IpApi api;
   private IpDetails reservedIp;

   @Test
   public void testListFree() throws Exception {
      Set<String> freeIps = api.listFree(4, "Falkenberg", "Xen");
      assertFalse(freeIps.isEmpty());
   }

   @Test
   public void reserveIp() throws Exception {
      Set<String> openVzIps = api.listFree(4, "Falkenberg", "OpenVZ");
      assertFalse(openVzIps.isEmpty());
      reservedIp = api.take(Iterables.get(openVzIps, 0));
      assertTrue(reservedIp.isReserved());
      checkOpenVZDefailsInFalkenberg(reservedIp);
   }

   @Test(dependsOnMethods = "reserveIp")
   public void reserveAndReleaseIp() throws Exception {
      IpDetails details = api.release(reservedIp.getAddress());
      assertEquals(details.getAddress(), reservedIp.getAddress());
      assertFalse(details.isReserved());
      
      // reserve an address again!
      reserveIp();
   }

   @Test(dependsOnMethods = "reserveIp")
   public void testList() throws Exception {
      Set<IpDetails> ownIps = api.listIps();
      assertTrue(ownIps.contains(reservedIp));
      ownIps = api.listIps(ListIpOptions.Builder.datacenter(reservedIp.getDatacenter()));
      assertTrue(ownIps.contains(reservedIp));
      ownIps = api.listIps(ListIpOptions.Builder.platform(reservedIp.getPlatform()));
      assertTrue(ownIps.contains(reservedIp));
      ownIps = api.listIps(ListIpOptions.Builder.ipVersion(reservedIp.getVersion()));
      assertTrue(ownIps.contains(reservedIp));

      ownIps = api.listIps(ListIpOptions.Builder.datacenter(reservedIp.getDatacenter()),
            ListIpOptions.Builder.platform(reservedIp.getPlatform()),
            ListIpOptions.Builder.ipVersion(reservedIp.getVersion()));
      assertTrue(ownIps.contains(reservedIp));

      ownIps = api.listIps(ListIpOptions.Builder.serverId("xmthisisnotaserverid"));
      assertTrue(ownIps.isEmpty());
   }
   
   private void checkOpenVZDefailsInFalkenberg(IpDetails ipDetails) {
      assertEquals(ipDetails.getDatacenter(), "Falkenberg");
      assertEquals(ipDetails.getPlatform(), "OpenVZ");
      assertEquals(ipDetails.getVersion(), 4);
      assertFalse(ipDetails.getPtr().isEmpty());
      // broadcast, gateway and netmask are null for OpenVZ
      assertFalse(ipDetails.getNameServers().isEmpty());
   }
   
   @Test
   public void testGetOpenVZDetails() throws Exception {
      Set<String> openVzIps = api.listFree(4, "Falkenberg", "OpenVZ");
      assertFalse(openVzIps.isEmpty());
      String openVzIp = openVzIps.iterator().next();
      IpDetails ipDetails = api.getIp(openVzIp);
      checkOpenVZDefailsInFalkenberg(ipDetails);
      assertEquals(ipDetails.getAddress(), openVzIp);
   }

   @Test
   public void testGetXenDetails() throws Exception {
      Set<String> xenVzIps = api.listFree(4, "Falkenberg", "Xen");
      assertFalse(xenVzIps.isEmpty());
      String xenIp = xenVzIps.iterator().next();
      IpDetails ipDetails = api.getIp(xenIp);
      assertEquals(ipDetails.getDatacenter(), "Falkenberg");
      assertEquals(ipDetails.getPlatform(), "Xen");
      assertEquals(ipDetails.getVersion(), 4);
      assertEquals(ipDetails.getAddress(), xenIp);
      assertFalse(ipDetails.getPtr().isEmpty());
      assertNotNull(ipDetails.getBroadcast());
      assertNotNull(ipDetails.getGateway());
      assertNotNull(ipDetails.getNetmask());
      assertFalse(ipDetails.getNameServers().isEmpty());
   }

   @Test(dependsOnMethods = "reserveIp")
   public void testPtrSetReset() throws Exception {
      IpDetails original = reservedIp;

      IpDetails modified = api.setPtr(reservedIp.getAddress(), "wibble.");
      IpDetails modified2 = api.getIp(reservedIp.getAddress());

      assertEquals(modified.getPtr(), "wibble.");
      assertEquals(modified2, modified);
      
      reservedIp = api.resetPtr(reservedIp.getAddress());
      
      assertEquals(reservedIp, original);
  }

   @Test(dependsOnMethods = "reserveIp")
   public void testAddRemove() throws Exception {
      IpDetails added = api.addIpToServer(reservedIp.getAddress(), serverId);

      assertEquals(added.getAddress(), reservedIp.getAddress());
      assertEquals(added.getPtr(), reservedIp.getPtr());
      assertEquals(added.getServerId(), serverId);
      
      IpDetails again = api.getIp(reservedIp.getAddress());
      assertEquals(again, added);

      IpDetails removed = api.removeIpFromServer(reservedIp.getAddress(), serverId);
      assertEquals(removed, added.toBuilder().serverId(null).build());

      assertEquals(removed, reservedIp);

      Set<String> openVzIps = Sets.newHashSet(api.listFree(4, "Falkenberg", "OpenVZ"));
      openVzIps.remove(reservedIp.getAddress());
      assertFalse(openVzIps.isEmpty());
      
      added = api.addIpToServer(reservedIp.getAddress(), serverId);
      
      assertEquals(added.getServerId(), serverId);

      removed = api.removeIpFromServerAndRelease(reservedIp.getAddress(), serverId);
      
      assertNull(removed.getServerId());
      assertFalse(removed.isReserved());
      
      
      // reserve an address again!
      reserveIp();
   }
}
