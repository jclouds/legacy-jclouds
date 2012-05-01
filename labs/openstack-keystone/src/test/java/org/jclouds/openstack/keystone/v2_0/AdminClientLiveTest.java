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
package org.jclouds.openstack.keystone.v2_0;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.net.URI;
import java.util.Set;

import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneClientLiveTest;
import org.jclouds.openstack.reference.AuthHeaders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests AdminClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "AdminClientLiveTest", singleThreaded = true)
public class AdminClientLiveTest extends BaseKeystoneClientLiveTest {

   protected String token;

   // Get the token currently in use (there's currently no listTokens())
   @BeforeMethod
   public void grabToken() {
      AuthenticateRequest ar = keystoneContext.getUtils().getInjector().getInstance(AuthenticateRequest.class);
      HttpRequest test = ar.filter(HttpRequest.builder().method("GET").endpoint(URI.create(endpoint)).build());
      token = Iterables.getOnlyElement(test.getHeaders().get(AuthHeaders.AUTH_TOKEN));
   }
   
   public void testGetApiMetaData() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         ApiMetadata result = keystoneContext.getApi().getAdminClientForRegion(regionId).getApiMetadata();
         assertNotNull(result);
         assertNotNull(result.getId());
         assertNotNull(result.getStatus());
         assertNotNull(result.getUpdated());
      }
   }

   public void testTenants() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         AdminClient client = keystoneContext.getApi().getAdminClientForRegion(regionId);
         Set<Tenant> result = client.listTenants();
         assertNotNull(result);
         assertFalse(result.isEmpty());

         for (Tenant tenant : result) {
            assertNotNull(tenant.getId());

            Tenant aTenant = client.getTenant(tenant.getId());
            assertEquals(aTenant, tenant);
         }
      }
   }

   public void testToken() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         AdminClient client = keystoneContext.getApi().getAdminClientForRegion(regionId);
         assertTrue(client.checkTokenIsValid(token));
         Token result = client.getToken(token);
         assertNotNull(result);
         assertEquals(result.getId(), token);
         assertNotNull(result.getTenant());
         
         User user = client.getUserOfToken(token);
         assertNotNull(user);
         assertNotNull(user.getId());
         assertNotNull(user.getName());
      }
   }
   
   public void testInvalidToken() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         AdminClient client = keystoneContext.getApi().getAdminClientForRegion(regionId);
         assertFalse(client.checkTokenIsValid("thisisnotarealtoken!"));
         assertNull(client.getToken("thisisnotarealtoken!"));
      }
   }

   // TODO this threw 501 not implemented
   @Test(enabled = false)
   public void testTokenEndpoints() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         AdminClient client = keystoneContext.getApi().getAdminClientForRegion(regionId);
         Set<Endpoint> endpoints = client.getEndpointsForToken(token);
         assertNotNull(endpoints);
         assertFalse(endpoints.isEmpty());
      }
   }

   // TODO this threw 501 not implemented
   @Test(enabled = false)
   public void testInvalidTokenEndpoints() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         AdminClient client = keystoneContext.getApi().getAdminClientForRegion(regionId);
         assertTrue(client.getEndpointsForToken("thisisnotarealtoken!").isEmpty());
      }
   }

   public void testUsers() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         AdminClient client = keystoneContext.getApi().getAdminClientForRegion(regionId);
         Set<User> users = client.listUsers();
         assertNotNull(users);
         assertFalse(users.isEmpty());
         for (User user : users) {
            User aUser = client.getUser(user.getId());
            assertEquals(aUser, user);
         }
      }
   }

   @Test(dependsOnMethods = {"testUsers", "testTenants"})
   public void testUserRolesOnTenant() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         AdminClient client = keystoneContext.getApi().getAdminClientForRegion(regionId);
         Set<User> users = client.listUsers();
         Set<Tenant> tenants = client.listTenants();

         for (User user : users) {
            for (Tenant tenant : tenants) {
               Set<Role> roles = client.listRolesOfUserOnTenant(user.getId(), tenant.getId());
               for (Role role : roles) {
                  assertNotNull(role.getId());
               }
            }
         }
      }
   }

   // TODO this functionality is currently broken in openstack
   @Test(enabled = false, dependsOnMethods = "testUsers")
   public void testListRolesOfUser() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         AdminClient client = keystoneContext.getApi().getAdminClientForRegion(regionId);
         for (User user : client.listUsers()) {
            Set<Role> roles = client.listRolesOfUser(user.getId());
            for (Role role : roles) {
               assertNotNull(role.getId());
            }
         }
      }
   }

   // TODO this functionality is currently broken in openstack (possibly deprecated?)
   @Test(enabled=false, dependsOnMethods = {"testUsers", "testTenants"})
   public void testUsersAndTenantsByName() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         AdminClient client = keystoneContext.getApi().getAdminClientForRegion(regionId);
         for (User user : client.listUsers()) {
            User aUser = client.getUserByName(user.getName());
            assertEquals(aUser, user);
         }
         for (Tenant tenant : client.listTenants()) {
            Tenant aTenant = client.getTenantByName(tenant.getName());
            assertEquals(aTenant, tenant);
         }
      }
   }
}