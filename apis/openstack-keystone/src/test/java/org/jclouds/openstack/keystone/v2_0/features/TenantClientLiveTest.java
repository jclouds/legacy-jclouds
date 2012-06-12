/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 1.1 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-1.1
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.keystone.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.Set;

import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests TenantClient
 * 
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "TenantClientLiveTest")
public class TenantClientLiveTest extends BaseKeystoneClientLiveTest {

   public void testTenants() {
      TenantClient client = keystoneContext.getApi().getTenantClient().get();
      Set<Tenant> result = client.list();
      assertNotNull(result);
      assertFalse(result.isEmpty());

      for (Tenant tenant : result) {
         assertNotNull(tenant.getId());

         Tenant aTenant = client.get(tenant.getId());
         assertNotNull(aTenant, "get returned null for tenant: " + tenant);

         assertEquals(aTenant, tenant);
      }
   }

   public void testTenantsByName() {

      TenantClient client = keystoneContext.getApi().getTenantClient().get();

      for (Tenant tenant : client.list()) {
         Tenant aTenant = client.getByName(tenant.getName());
         assertNotNull(aTenant, "get returned null for tenant: " + tenant);

         assertEquals(aTenant, tenant);
      }

   }
}
