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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.http.HttpResponseException;
import org.jclouds.openstack.keystone.v2_0.KeystoneClient;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestClientExpectTest;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of UserClient
 *
 * @author Adam Lowe
 */
@Test(testName = "UserClientExpectTest")
public class UserClientExpectTest extends BaseKeystoneRestClientExpectTest<KeystoneClient> {
   
   public UserClientExpectTest(){
      endpoint = "https://csnode.jclouds.org:35357";
   }
   
   public void testListUsers() {
      UserClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/user_list.json", APPLICATION_JSON)).build())
            .getUserClient().get();
      Set<User> users = client.list();
      assertNotNull(users);
      assertFalse(users.isEmpty());

      Set<User> expected = ImmutableSet.of(
            User.builder().name("nova").id("e021dfd758eb44a89f1c57c8ef3be8e2").build(),
            User.builder().name("glance").id("3f6c1c9ba993495ead7d2eb2192e284f").build(),
            User.builder().name("demo").id("667b2e1420604df8b67cd8ea57d4ee64").build(),
            User.builder().name("admin").id("2b9b606181634ae9ac86fd95a8bc2cde").build()
      );

      assertEquals(users, expected);
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testListUsersFailNotAuth() {
      UserClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users").build(),
            standardResponseBuilder(401).build()).getUserClient().get();
      client.list();
   }

   public void testGetUser() {
      UserClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/e021dfd758eb44a89f1c57c8ef3be8e2").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/user_details.json", APPLICATION_JSON)).build())
            .getUserClient().get();
      User user = client.get("e021dfd758eb44a89f1c57c8ef3be8e2");
      assertNotNull(user);
      assertEquals(user, User.builder().name("nova").id("e021dfd758eb44a89f1c57c8ef3be8e2").build());
   }

   public void testGetUserFailNotFound() {
      UserClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/f021dfd758eb44a89f1c57c8ef3be8e2").build(),
            standardResponseBuilder(404).build()).getUserClient().get();
      assertNull(client.get("f021dfd758eb44a89f1c57c8ef3be8e2"));
   }

   public void testGetUserByName() {
      UserClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users?name=nova").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/user_details.json", APPLICATION_JSON)).build())
            .getUserClient().get();
      User user = client.getByName("nova");
      assertNotNull(user);
      assertEquals(user, User.builder().name("nova").id("e021dfd758eb44a89f1c57c8ef3be8e2").build());
   }

   public void testGetUserByNameFailNotFound() {
      UserClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users?name=fred").build(),
            standardResponseBuilder(404).build()).getUserClient().get();
      assertNull(client.getByName("fred"));
   }
   
   public void testListRolesOfUser() {
      UserClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/3f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/user_role_list.json", APPLICATION_JSON)).build())
            .getUserClient().get();
      Set<Role> roles = client.listRolesOfUser("3f6c1c9ba993495ead7d2eb2192e284f");
      assertNotNull(roles);
      assertFalse(roles.isEmpty());
      assertEquals(roles, ImmutableSet.of(
            Role.builder().id("79cada5c02814b57a52e0eed4dd388cb").name("admin").build()
      ));
   }

   public void testListRolesOfUserFailNotFound() {
      UserClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/4f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            standardResponseBuilder(404).build()).getUserClient().get();
      assertTrue(client.listRolesOfUser("4f6c1c9ba993495ead7d2eb2192e284f").isEmpty());
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testListRolesOfUserFailNotImplemented() {
      UserClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/5f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            standardResponseBuilder(501).build()).getUserClient().get();
      assertTrue(client.listRolesOfUser("5f6c1c9ba993495ead7d2eb2192e284f").isEmpty());
   }

   public void testListRolesOfUserInTenant() {
      UserClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/3f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/user_tenant_role_list.json", APPLICATION_JSON)).build())
            .getUserClient().get();
      Set<Role> roles = client.listRolesOfUser("3f6c1c9ba993495ead7d2eb2192e284f");
      assertNotNull(roles);
      assertFalse(roles.isEmpty());
      assertEquals(roles, ImmutableSet.of(
         Role.builder().id("31c451195aac49b386039341e2c92a16").name("KeystoneServiceAdmin").build(),
         Role.builder().id("79cada5c02814b57a52e0eed4dd388cb").name("admin").build(),
         Role.builder().id("6ea17ddd37a6447794cb0e164d4db894").name("KeystoneAdmin").build()));
   }

   public void testListRolesOfUserInTenantFailNotFound() {
      UserClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/3f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            standardResponseBuilder(404).build()).getUserClient().get();
      assertTrue(client.listRolesOfUser("3f6c1c9ba993495ead7d2eb2192e284f").isEmpty());
   }
   
}
