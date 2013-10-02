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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Host;
import org.jclouds.openstack.nova.v2_0.domain.HostResourceUsage;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of HostAdministrationApi
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "HostAdministrationApiLiveTest", singleThreaded = true)
public class HostAdministrationApiLiveTest extends BaseNovaApiLiveTest {
   private Optional<? extends HostAdministrationApi> optApi = Optional.absent();

   Predicate<Host> isComputeHost = new Predicate<Host>() {
      @Override
      public boolean apply(Host input) {
         return Objects.equal("compute", input.getService());
      }
   };

   @BeforeGroups(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();

      if (identity.endsWith(":admin")) {
         String zone = Iterables.getLast(api.getConfiguredZones(), "nova");
         optApi = api.getHostAdministrationExtensionForZone(zone);
      }
   }

   public void testListAndGet() throws Exception {
      if (optApi.isPresent()) {
         HostAdministrationApi api = optApi.get();
         Set<? extends Host> hosts = api.list().toSet();
         assertNotNull(hosts);
         for (Host host : hosts) {
            for (HostResourceUsage usage : api.listResourceUsage(host.getName())) {
               assertEquals(usage.getHost(), host.getName());
               assertNotNull(usage);
            }
         }
      }
   }

   @Test(enabled = false)
   public void testEnableDisable() throws Exception {
      if (optApi.isPresent()) {
         HostAdministrationApi api = optApi.get();
         Host host = Iterables.find(api.list(), isComputeHost);

         assertTrue(api.disable(host.getName()));
         assertTrue(api.enable(host.getName()));
      }
   }

   @Test(enabled = false)
   public void testMaintenanceMode() throws Exception {
      if (optApi.isPresent()) {
         HostAdministrationApi api = optApi.get();
         Host host = Iterables.find(api.list(), isComputeHost);
         assertTrue(api.startMaintenance(host.getName()));
         assertTrue(api.stopMaintenance(host.getName()));
      }
   }

   @Test(enabled = false)
   public void testReboot() throws Exception {
      if (optApi.isPresent()) {
         HostAdministrationApi api = optApi.get();
         Host host = Iterables.find(api.list(), isComputeHost);
         assertTrue(api.reboot(host.getName()));
      }
   }

   @Test(enabled = false)
   public void testShutdownAndStartup() throws Exception {
      if (optApi.isPresent()) {
         HostAdministrationApi api = optApi.get();
         Host host = Iterables.find(api.list(), isComputeHost);
         assertTrue(api.shutdown(host.getName()));
         assertTrue(api.startup(host.getName()));
      }
   }
}
