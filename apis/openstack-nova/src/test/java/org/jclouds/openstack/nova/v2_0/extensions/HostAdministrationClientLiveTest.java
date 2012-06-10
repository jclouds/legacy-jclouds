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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Host;
import org.jclouds.openstack.nova.v2_0.domain.HostResourceUsage;
import org.jclouds.openstack.nova.v2_0.extensions.HostAdministrationClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of HostAdministrationClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "HostAdministrationClientLiveTest", singleThreaded = true)
public class HostAdministrationClientLiveTest extends BaseNovaClientLiveTest {
   private Optional<HostAdministrationClient> optClient = Optional.absent();

   Predicate<Host> isComputeHost = new Predicate<Host>() {
      @Override
      public boolean apply(Host input) {
         return Objects.equal("compute", input.getService());
      }
   };

   @BeforeGroups(groups = {"integration", "live"})
   @Override
   public void setupContext() {
      super.setupContext();

      if (identity.endsWith(":admin")) {
         String zone = Iterables.getLast(novaContext.getApi().getConfiguredZones(), "nova");
         optClient = novaContext.getApi().getHostAdministrationExtensionForZone(zone);
      }
   }

   public void testListAndGet() throws Exception {
      if (optClient.isPresent()) {
         HostAdministrationClient client = optClient.get();
         Set<Host> hosts = client.listHosts();
         assertNotNull(hosts);
         for (Host host : hosts) {
            for (HostResourceUsage usage : client.getHostResourceUsage(host.getName())) {
               assertEquals(usage.getHost(), host.getName());
               assertNotNull(usage);
            }
         }
      }
   }

   @Test(enabled = false)
   public void testEnableDisable() throws Exception {
      if (optClient.isPresent()) {
         HostAdministrationClient client = optClient.get();
         Host host = Iterables.find(client.listHosts(), isComputeHost);

         assertTrue(client.disableHost(host.getName()));
         assertTrue(client.enableHost(host.getName()));
      }
   }

   @Test(enabled = false)
   public void testMaintenanceMode() throws Exception {
      if (optClient.isPresent()) {
         HostAdministrationClient client = optClient.get();
         Host host = Iterables.find(client.listHosts(), isComputeHost);
         assertTrue(client.startHostMaintenance(host.getName()));
         assertTrue(client.stopHostMaintenance(host.getName()));
      }
   }

   @Test(enabled = false)
   public void testReboot() throws Exception {
      if (optClient.isPresent()) {
         HostAdministrationClient client = optClient.get();
         Host host = Iterables.find(client.listHosts(), isComputeHost);
         assertTrue(client.rebootHost(host.getName()));
      }
   }

   @Test(enabled = false)
   public void testShutdownAndStartup() throws Exception {
      if (optClient.isPresent()) {
         HostAdministrationClient client = optClient.get();
         Host host = Iterables.find(client.listHosts(), isComputeHost);
         assertTrue(client.shutdownHost(host.getName()));
         assertTrue(client.startupHost(host.getName()));
      }
   }
}
