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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPClient;
import org.jclouds.openstack.nova.v2_0.features.ServerClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;

/**
 * Tests behavior of {@code ServerClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "FloatingIPClientLiveTest")
public class FloatingIPClientLiveTest extends BaseNovaClientLiveTest {

   private static final int INCONSISTENCY_WINDOW = 5000;

   @Test
   public void testListFloatingIPs() throws Exception {
      for (String zoneId : novaContext.getApi().getConfiguredZones()) {
         Optional<FloatingIPClient> clientOption = novaContext.getApi().getFloatingIPExtensionForZone(zoneId);
         if (!clientOption.isPresent())
            continue;
         FloatingIPClient client = clientOption.get();
         Set<FloatingIP> response = client.listFloatingIPs();
         assert null != response;
         assertTrue(response.size() >= 0);
         for (FloatingIP ip : response) {
            FloatingIP newDetails = client.getFloatingIP(ip.getId());

            assertEquals(newDetails.getId(), ip.getId());
            assertEquals(newDetails.getIp(), ip.getIp());
            assertEquals(newDetails.getFixedIp(), ip.getFixedIp());
            assertEquals(newDetails.getInstanceId(), ip.getInstanceId());

         }
      }
   }

   @Test
   public void testAllocateAndDeallocateFloatingIPs() throws Exception {
      for (String zoneId : novaContext.getApi().getConfiguredZones()) {
         Optional<FloatingIPClient> clientOption = novaContext.getApi().getFloatingIPExtensionForZone(zoneId);
         if (!clientOption.isPresent())
            continue;
         FloatingIPClient client = clientOption.get();
         FloatingIP floatingIP = client.allocate();
         assertNotNull(floatingIP);

         Set<FloatingIP> response = client.listFloatingIPs();
         boolean ipInSet = false;
         for (FloatingIP ip : response) {
            if (ip.getId().equals(floatingIP.getId()))
               ipInSet = true;
         }
         assertTrue(ipInSet);

         client.deallocate(floatingIP.getId());

         response = client.listFloatingIPs();
         ipInSet = false;
         for (FloatingIP ip : response) {
            if (ip.getId().equals(floatingIP.getId())) {
               ipInSet = true;
            }
         }
         assertFalse(ipInSet);
      }
   }

   @Test
   public void testAddAndRemoveFloatingIp() throws Exception {
      for (String zoneId : novaContext.getApi().getConfiguredZones()) {
         Optional<FloatingIPClient> clientOption = novaContext.getApi().getFloatingIPExtensionForZone(zoneId);
         if (!clientOption.isPresent())
            continue;
         FloatingIPClient client = clientOption.get();
         ServerClient serverClient = novaContext.getApi().getServerClientForZone(zoneId);
         Server server = createServerInZone(zoneId);
         FloatingIP floatingIP = client.allocate();
         assertNotNull(floatingIP);
         try {
            client.addFloatingIPToServer(floatingIP.getIp(), server.getId());
            assertEventually(new ServerHasFloatingIP(serverClient, server.getId(), floatingIP.getIp()));
         } finally {
            client.removeFloatingIPFromServer(floatingIP.getIp(), server.getId());
            serverClient.deleteServer(server.getId());
         }
      }
   }

   protected static void assertEventually(Runnable assertion) {
      long start = System.currentTimeMillis();
      AssertionError error = null;
      for (int i = 0; i < 30; i++) {
         try {
            assertion.run();
            if (i > 0)
               System.err.printf("%d attempts and %dms asserting %s%n", i + 1, System.currentTimeMillis() - start,
                     assertion.getClass().getSimpleName());
            return;
         } catch (AssertionError e) {
            error = e;
         }
         try {
            Thread.sleep(INCONSISTENCY_WINDOW / 30);
         } catch (InterruptedException e) {
         }
      }
      if (error != null)
         throw error;

   }

   public static final class ServerHasFloatingIP implements Runnable {
      private final ServerClient client;
      private final String serverId;
      private final String floatingIP;

      public ServerHasFloatingIP(ServerClient serverClient, String serverId, String floatingIP) {
         this.client = serverClient;
         this.serverId = serverId;
         this.floatingIP = floatingIP;
      }

      public void run() {
         try {
            Server server = client.getServer(serverId);
            boolean ipInServerAddresses = false;
            Multimap<String, Address> addresses = server.getAddresses();
            for (Address address : addresses.values()) {
               if (address.getAddr().equals(floatingIP)) {
                  ipInServerAddresses = true;
               }
            }
            assertTrue(ipInServerAddresses);
         } catch (Exception e) {
            throw new AssertionError(e);
         }
      }
   }

}
