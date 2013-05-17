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
package org.jclouds.openstack.keystone.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.Set;

import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneApiLiveTest;
import org.testng.annotations.Test;

/**
 * Tests TenantApi
 * 
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "TenantApiLiveTest")
public class TenantApiLiveTest extends BaseKeystoneApiLiveTest {

   public void testTenants() {
      TenantApi tenantApi = api.getTenantApi().get();
      Set<? extends Tenant> result = tenantApi.list().concat().toSet();
      assertNotNull(result);
      assertFalse(result.isEmpty());

      for (Tenant tenant : result) {
         assertNotNull(tenant.getId());

         Tenant aTenant = tenantApi.get(tenant.getId());
         assertNotNull(aTenant, "get returned null for tenant: " + tenant);

         assertEquals(aTenant, tenant);
      }
   }

   public void testTenantsByName() {

      TenantApi tenantApi = api.getTenantApi().get();

      for (Tenant tenant : tenantApi.list().concat()) {
         Tenant aTenant = tenantApi.getByName(tenant.getName());
         assertNotNull(aTenant, "get returned null for tenant: " + tenant);

         assertEquals(aTenant, tenant);
      }

   }
}
