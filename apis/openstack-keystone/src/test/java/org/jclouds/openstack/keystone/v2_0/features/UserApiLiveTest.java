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

import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneApiLiveTest;
import org.testng.annotations.Test;

/**
 * Tests UserApi
 * 
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "UserApiLiveTest", singleThreaded = true)
public class UserApiLiveTest extends BaseKeystoneApiLiveTest {

   public void testUsers() {

      UserApi userApi = api.getUserApi().get();
      Set<? extends User> users = userApi.list().concat().toSet();
      assertNotNull(users);
      assertFalse(users.isEmpty());
      for (User user : users) {
         User aUser = userApi.get(user.getId());
         assertEquals(aUser, user);
      }

   }

   public void testUserRolesOnTenant() {

      UserApi userApi = api.getUserApi().get();
      Set<? extends Tenant> tenants = api.getTenantApi().get().list().concat().toSet();

      for (User user : userApi.list().concat()) {
         for (Tenant tenant : tenants) {
            Set<? extends Role> roles = userApi.listRolesOfUserOnTenant(user.getId(), tenant.getId());
            for (Role role : roles) {
               assertNotNull(role.getId());
            }
         }
      }

   }

   public void testListRolesOfUser() {

      UserApi userApi = api.getUserApi().get();
      for (User user : userApi.list().concat()) {
         Set<? extends Role> roles = userApi.listRolesOfUser(user.getId());
         for (Role role : roles) {
            assertNotNull(role.getId());
         }
      }

   }

   public void testUsersByName() {

      UserApi userApi = api.getUserApi().get();
      for (User user : userApi.list().concat()) {
         User aUser = userApi.getByName(user.getName());
         assertEquals(aUser, user);
      }

   }
}
