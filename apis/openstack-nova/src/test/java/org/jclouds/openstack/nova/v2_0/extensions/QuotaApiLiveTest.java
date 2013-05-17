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
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.nova.v2_0.domain.Quota;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of QuotaApi
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "QuotaApiLiveTest", singleThreaded = true)
public class QuotaApiLiveTest extends BaseNovaApiLiveTest {
   private Optional<? extends QuotaApi> apiOption;
   private String tenant;

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();
      tenant = identity.split(":")[0];
      String zone = Iterables.getLast(api.getConfiguredZones(), "nova");
      apiOption = api.getQuotaExtensionForZone(zone);
   }

   public void testGetQuotasForCurrentTenant() {
      if (apiOption.isPresent()) {
         Quota quota = apiOption.get().getByTenant(tenant);
         assertQuotasIsValid(quota);
      }
   }

   public void testGetDefaultQuotasForCurrentTenant() {
      if (apiOption.isPresent()) {
         Quota quota = apiOption.get().getDefaultsForTenant(tenant);
         assertQuotasIsValid(quota);
      }
   }

   public void testUpdateQuotasOfCurrentTenantThenReset() {
      if (apiOption.isPresent()) {
         QuotaApi api = apiOption.get();
         Quota before = api.getByTenant(tenant);
         assertQuotasIsValid(before);

         Quota modified = before.toBuilder()
               .cores(before.getCores() - 1)
               .instances(before.getInstances() - 1)
               .metadataItems(before.getMetadatas() - 1)
               .ram(before.getRam() - 1)
               .volumes(before.getVolumes() - 1)
               .build();

         assertTrue(api.updateQuotaOfTenant(modified, tenant));

         assertEquals(api.getByTenant(tenant), modified);

         assertTrue(api.updateQuotaOfTenant(before, tenant));

         assertEquals(api.getByTenant(tenant), before);
      }
   }

   protected void assertQuotasIsValid(Quota quota) {
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
