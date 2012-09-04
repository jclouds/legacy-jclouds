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

import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Optional;

/**
 * Tests {@link UserApi}.
 * 
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "UserApiLiveTest", singleThreaded = true)
public class UserApiLiveTest extends BaseKeystoneApiLiveTest {

    @Test(description = "GET /v2.0/users")
    public void testListUsers() {
        Optional<? extends UserApi> api = keystoneContext.getApi().getUserApi();
        if (api.isPresent()) {
            UserApi userApi = api.get();

            Set<? extends User> users = userApi.list();
            assertNotNull(users);
            assertFalse(users.isEmpty());
        }
    }

    @Test(description = "GET /v2.0/users/{userId}", dependsOnMethods = { "testListUsers" })
    public void testGetUser() {
        Optional<? extends UserApi> api = keystoneContext.getApi().getUserApi();
        if (api.isPresent()) {
            UserApi userApi = api.get();

            Set<? extends User> users = userApi.list();
            for (User user : users) {
                User aUser = userApi.get(user.getId());
                assertEquals(aUser, user);
            }
        }
    }

    @Test(description = "GET /v2.0/tenants/{tenantId}/users/{userId}/roles", dependsOnMethods = { "testListUsers" })
    public void testUserRolesOnTenant() {
        Optional<? extends UserApi> api = keystoneContext.getApi().getUserApi();

        if (api.isPresent()) {
            UserApi userApi = api.get();
            Set<? extends User> users = userApi.list();
            Set<? extends Tenant> tenants = keystoneContext.getApi().getServiceApi().listTenants();

            for (User user : users) {
                for (Tenant tenant : tenants) {
                    Set<? extends Role> roles = userApi.listRolesOfUserOnTenant(user.getId(), tenant.getId());
                    for (Role role : roles) {
                        assertNotNull(role.getId());
                    }
                }
            }
        }
    }

    @Test(description = "GET /v2.0/users/{userId}/roles", dependsOnMethods = { "testListUsers" })
    public void testListRolesOfUser() {
        Optional<? extends UserApi> api = keystoneContext.getApi().getUserApi();

        if (api.isPresent()) {
            UserApi userApi = api.get();
            Set<? extends User> users = userApi.list();
            for (User user : users) {
                Set<? extends Role> roles = userApi.listRolesOfUser(user.getId());
                for (Role role : roles) {
                    assertNotNull(role.getId());
                }
            }
        }

    }

    @Test(description = "GET /v2.0/users/?name={userName}", dependsOnMethods = { "testListUsers" })
    public void testGetUserByName() {
        Optional<? extends UserApi> api = keystoneContext.getApi().getUserApi();

        if (api.isPresent()) {
            UserApi userApi = api.get();
            Set<? extends User> users = userApi.list();
            for (User user : users) {
	            User aUser = userApi.getByName(user.getName());
	            assertEquals(aUser, user);
	        }
        }
    }
}
