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
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.nova.v2_0.domain.Quotas;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of QuotaClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "QuotaClientLiveTest", singleThreaded = true)
public class QuotaClientLiveTest extends BaseNovaClientLiveTest {
   private Optional<QuotaClient> clientOption;
   private String tenant;

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setupContext() {
      super.setupContext();
      tenant = identity.split(":")[0];
      String zone = Iterables.getLast(novaContext.getApi().getConfiguredZones(), "nova");
      clientOption = novaContext.getApi().getQuotaExtensionForZone(zone);
   }

   public void testGetQuotasForCurrentTenant() {
      if (clientOption.isPresent()) {
         Quotas quota = clientOption.get().getQuotasForTenant(tenant);
         assertQuotasIsValid(quota);
      }
   }

   public void testGetDefaultQuotasForCurrentTenant() {
      if (clientOption.isPresent()) {
         Quotas quota = clientOption.get().getDefaultQuotasForTenant(tenant);
         assertQuotasIsValid(quota);
      }
   }

   public void testUpdateQuotasOfCurrentTenantThenReset() {
      if (clientOption.isPresent()) {
         QuotaClient client = clientOption.get();
         Quotas before = client.getQuotasForTenant(tenant);
         assertQuotasIsValid(before);

         Quotas modified = before.toBuilder()
               .cores(before.getCores() - 1)
               .instances(before.getInstances() - 1)
               .metadataItems(before.getMetadataItems() - 1)
               .ram(before.getRam() - 1)
               .volumes(before.getVolumes() - 1)
               .build();

         assertTrue(client.updateQuotasForTenant(tenant, modified));

         assertEquals(client.getQuotasForTenant(tenant), modified);

         assertTrue(client.updateQuotasForTenant(tenant, before));

         assertEquals(client.getQuotasForTenant(tenant), before);
      }
   }

   protected void assertQuotasIsValid(Quotas quota) {
      assertTrue(quota.getCores() > 0);
      assertTrue(quota.getFloatingIps() >= 0);
      assertTrue(quota.getGigabytes() > 0);
      assertTrue(quota.getInjectedFileContentBytes() >= 0);
      assertTrue(quota.getInjectedFiles() >= 0);
      assertTrue(quota.getInstances() > 0);
      assertTrue(quota.getKeyPairs() > 0);
      assertTrue(quota.getRam() > 0);
      assertTrue(quota.getSecurityGroups() > 0);
      assertTrue(quota.getSecurityGroupRules() > 0);
      assertTrue(quota.getVolumes() > 0);
   }
}
