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

import java.util.Map;
import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Host;
import org.jclouds.openstack.nova.v2_0.domain.HostAggregate;
import org.jclouds.openstack.nova.v2_0.extensions.HostAdministrationClient;
import org.jclouds.openstack.nova.v2_0.extensions.HostAggregateClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of AggregateClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "AggregateClientLiveTest", singleThreaded = true)
public class HostAggregateClientLiveTest extends BaseNovaClientLiveTest {
   private Optional<HostAggregateClient> clientOption;
   private Optional<HostAdministrationClient> hostAdminOption;

   private HostAggregate testAggregate;

   @BeforeGroups(groups = {"integration", "live"})
   @Override
   public void setupContext() {
      super.setupContext();
      String zone = Iterables.getLast(novaContext.getApi().getConfiguredZones(), "nova");
      clientOption = novaContext.getApi().getHostAggregateExtensionForZone(zone);
      hostAdminOption = novaContext.getApi().getHostAdministrationExtensionForZone(zone);
   }

   @Override
   @AfterGroups(groups = {"integration", "live"})
   public void tearDown() {
      if (testAggregate != null) {
         assertTrue(clientOption.get().deleteAggregate(testAggregate.getId()));
      }
      super.tearDown();
   }

   public void testCreateAggregate() {
      if (clientOption.isPresent()) {
         // TODO assuming "nova" availability zone is present
         testAggregate = clientOption.get().createAggregate("jclouds-test-a", "nova");
      }
   }

   @Test(dependsOnMethods = "testCreateAggregate")
   public void testListAndGetAggregate() {
      if (clientOption.isPresent()) {
         HostAggregateClient client = clientOption.get();
         Set<HostAggregate> aggregates = client.listAggregates();
         for (HostAggregate aggregate : aggregates) {
            assertNotNull(aggregate.getId());
            assertNotNull(aggregate.getName());
            assertNotNull(aggregate.getAvailabilityZone());

            HostAggregate details = client.getAggregate(aggregate.getId());
            assertEquals(details.getId(), aggregate.getId());
            assertEquals(details.getName(), aggregate.getName());
            assertEquals(details.getAvailabilityZone(), aggregate.getAvailabilityZone());
            assertEquals(details.getHosts(), aggregate.getHosts());
         }
      }
   }

   @Test(dependsOnMethods = "testCreateAggregate")
   public void testModifyMetadata() {
      if (clientOption.isPresent()) {
         HostAggregateClient client = clientOption.get();
         for (Map<String, String> theMetaData : ImmutableSet.of(
               ImmutableMap.of("somekey", "somevalue"),
               ImmutableMap.of("somekey", "some other value", "anotherkey", "another val")
         )) {
            // Apply changes
            HostAggregate details = client.setMetadata(testAggregate.getId(), theMetaData);
            
            //  bug in openstack - metadata values are never removed, so we just checking what we've set
            for (String key : theMetaData.keySet()) {
               assertEquals(details.getMetadata().get(key), theMetaData.get(key));
            }

            // Re-fetch to double-check
            details = client.getAggregate(testAggregate.getId());
            for (String key : theMetaData.keySet()) {
               assertEquals(details.getMetadata().get(key), theMetaData.get(key));
            }
         }
      }
   }

   // Note the host will be added, but cannot remove it til
   @Test(enabled = false, dependsOnMethods = "testCreateAggregate")
   public void testModifyHosts() {
      if (clientOption.isPresent() && hostAdminOption.isPresent()) {
         HostAggregateClient client = clientOption.get();
         Host host = Iterables.getFirst(hostAdminOption.get().listHosts(), null);
         assertNotNull(host);

         String host_id = host.getName();
         assertNotNull(host_id);
         HostAggregate details;

         try {
            details = client.addHost(testAggregate.getId(), host_id);

            assertEquals(details.getHosts(), ImmutableSet.of(host_id));

            // re-fetch to double-check
            details = client.getAggregate(testAggregate.getId());
            assertEquals(details.getHosts(), ImmutableSet.of(host_id));

            // TODO wait until status of aggregate isn't CHANGING (hostAdministration.shutdown?)
         } finally {
            details = client.removeHost(testAggregate.getId(), host_id);
         }

         assertEquals(details.getHosts(), ImmutableSet.of());
      }
   }
}
