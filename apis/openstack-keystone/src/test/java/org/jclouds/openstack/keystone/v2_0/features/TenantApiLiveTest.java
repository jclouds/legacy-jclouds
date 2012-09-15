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
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneApiLiveTest;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.base.Optional;

/**
 * Tests {@link TenantApi}.
 * 
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "TenantApiLiveTest")
public class TenantApiLiveTest extends BaseKeystoneApiLiveTest {

    @Test(description = "/v2.0/tenants")
    public void testListTenants() {
        try {
            Optional<? extends TenantApi> api = keystoneContext.getApi().getTenantApi();
            if (api.isPresent()) {
                TenantApi tenantApi = api.get();
                Set<? extends Tenant> result = tenantApi.list();
                assertNotNull(result);
                assertFalse(result.isEmpty());

                for (Tenant tenant : result) {
                    assertNotNull(tenant.getId());
                }
            }
        } catch (AuthorizationException ae) {
            // Ignore, some Keystone implementations treat this as Admin only
        }
    }

    @Test(description = "/v2.0/tenants/{tenantId}", dependsOnMethods = { "testListTenants" })
    public void testGetTenant() {
        try {
            Optional<? extends TenantApi> api = keystoneContext.getApi().getTenantApi();
            if (api.isPresent()) {
                TenantApi tenantApi = api.get();
                Set<? extends Tenant> result = tenantApi.list();

                for (Tenant tenant : result) {
                    assertNotNull(tenant.getId());

                    Tenant aTenant = tenantApi.get(tenant.getId());
                    assertNotNull(aTenant, "get returned null for tenant: " + tenant);

                    assertEquals(aTenant, tenant);
                }
            }
        } catch (AuthorizationException ae) {
            // Ignore, some Keystone implementations treat this as Admin only
        }
    }

    @Test(description = "/v2.0/tenants/?name={tenantName}", dependsOnMethods = { "testListTenants" })
    public void testGetTenantByName() {
        try {
            Optional<? extends TenantApi> api = keystoneContext.getApi().getTenantApi();
            if (api.isPresent()) {
                TenantApi tenantApi = api.get();

                for (Tenant tenant : tenantApi.list()) {
                    Tenant aTenant = tenantApi.getByName(tenant.getName());
                    assertNotNull(aTenant, "get returned null for tenant: " + tenant);

                    assertEquals(aTenant, tenant);
                }
            }
        } catch (AuthorizationException ae) {
            // Ignore, some Keystone implementations treat this as Admin only
        }
    }
}
