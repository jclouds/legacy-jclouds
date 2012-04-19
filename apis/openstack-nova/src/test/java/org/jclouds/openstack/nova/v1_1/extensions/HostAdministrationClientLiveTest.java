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
package org.jclouds.openstack.nova.v1_1.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Set;

import org.jclouds.openstack.nova.v1_1.domain.Host;
import org.jclouds.openstack.nova.v1_1.domain.HostResourceUsage;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaClientLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Optional;

/**
 * Tests behavior of HostAdministrationClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "HostAdministrationClientLiveTest")
public class HostAdministrationClientLiveTest extends BaseNovaClientLiveTest {

   public void testListAndGet() throws Exception {
      for (String zoneId : novaContext.getApi().getConfiguredZones()) {
         Optional<HostAdministrationClient> optClient = novaContext.getApi().getHostAdministrationExtensionForZone(zoneId);
         if (optClient.isPresent() && identity.endsWith(":admin")) {
            HostAdministrationClient client = optClient.get();
            Set<Host> hosts = client.listHosts();
            assertNotNull(hosts);
            for(Host host : hosts) {
               for (HostResourceUsage usage : client.getHostResourceUsage(host.getName())) {
                  assertEquals(usage.getHost(), host.getName());
                  assertNotNull(usage);
               }
            }
         }
      }
   }

}
