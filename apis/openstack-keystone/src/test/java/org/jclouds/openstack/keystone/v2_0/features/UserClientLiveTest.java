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
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests UserClient
 * 
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "UserClientLiveTest", singleThreaded = true)
public class UserClientLiveTest extends BaseKeystoneClientLiveTest {

   public void testUsers() {

      UserClient client = keystoneContext.getApi().getUserClient().get();
      Set<User> users = client.list();
      assertNotNull(users);
      assertFalse(users.isEmpty());
      for (User user : users) {
         User aUser = client.get(user.getId());
         assertEquals(aUser, user);
      }

   }

   public void testUserRolesOnTenant() {

      UserClient client = keystoneContext.getApi().getUserClient().get();
      Set<User> users = client.list();
      Set<Tenant> tenants = keystoneContext.getApi().getTenantClient().get().list();

      for (User user : users) {
         for (Tenant tenant : tenants) {
            Set<Role> roles = client.listRolesOfUserOnTenant(user.getId(), tenant.getId());
            for (Role role : roles) {
               assertNotNull(role.getId());
            }
         }
      }

   }

   public void testListRolesOfUser() {

      UserClient client = keystoneContext.getApi().getUserClient().get();
      for (User user : client.list()) {
         Set<Role> roles = client.listRolesOfUser(user.getId());
         for (Role role : roles) {
            assertNotNull(role.getId());
         }
      }

   }

   public void testUsersByName() {

      UserClient client = keystoneContext.getApi().getUserClient().get();
      for (User user : client.list()) {
         User aUser = client.getByName(user.getName());
         assertEquals(aUser, user);
      }

   }
}