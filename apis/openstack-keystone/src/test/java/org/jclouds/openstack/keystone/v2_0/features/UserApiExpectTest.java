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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestApiExpectTest;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of UserApi
 *
 * @author Adam Lowe
 */
@Test(singleThreaded = true, testName = "UserApiExpectTest")
public class UserApiExpectTest extends BaseKeystoneRestApiExpectTest<KeystoneApi> {
   
   public UserApiExpectTest(){
      endpoint = "https://csnode.jclouds.org:35357";
   }
   
   Set<User> expectedUsers = ImmutableSet.of(
            User.builder().name("nova").id("e021dfd758eb44a89f1c57c8ef3be8e2").build(),
            User.builder().name("glance").id("3f6c1c9ba993495ead7d2eb2192e284f").build(),
            User.builder().name("demo").id("667b2e1420604df8b67cd8ea57d4ee64").build(),
            User.builder().name("admin").id("2b9b606181634ae9ac86fd95a8bc2cde").build()
      );
   
   public void testListUsers() {
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/users").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/user_list.json", APPLICATION_JSON)).build())
            .getUserApi().get();
     
      assertEquals(api.list().concat().toSet(), expectedUsers);
   }
   
   public void testListUsersPage() {
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/users").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/user_list.json", APPLICATION_JSON)).build())
            .getUserApi().get();
      PaginatedCollection<? extends User> users = api.list(new PaginationOptions());
      assertNotNull(users);
      assertFalse(users.isEmpty());


      assertEquals(users.toSet(), expectedUsers);
   }
   
   public void testListUsersNotFound() {
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/users").build(),
            HttpResponse.builder().statusCode(404).build()).getUserApi().get();
      assertEquals( api.list(new PaginationOptions()).size(), 0);
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testListUsersFailNotAuth() {
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/users").build(),
            HttpResponse.builder().statusCode(401).build()).getUserApi().get();
      api.list(new PaginationOptions());
   }

   public void testGetUser() {
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/users/e021dfd758eb44a89f1c57c8ef3be8e2").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/user_details.json", APPLICATION_JSON)).build())
            .getUserApi().get();
      User user = api.get("e021dfd758eb44a89f1c57c8ef3be8e2");
      assertNotNull(user);
      assertEquals(user, User.builder().name("nova").id("e021dfd758eb44a89f1c57c8ef3be8e2").build());
   }

   public void testGetUserFailNotFound() {
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/users/f021dfd758eb44a89f1c57c8ef3be8e2").build(),
            HttpResponse.builder().statusCode(404).build()).getUserApi().get();
      assertNull(api.get("f021dfd758eb44a89f1c57c8ef3be8e2"));
   }

   public void testGetUserByName() {
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/users?name=nova").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/user_details.json", APPLICATION_JSON)).build())
            .getUserApi().get();
      User user = api.getByName("nova");
      assertNotNull(user);
      assertEquals(user, User.builder().name("nova").id("e021dfd758eb44a89f1c57c8ef3be8e2").build());
   }

   public void testGetUserByNameFailNotFound() {
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/users?name=fred").build(),
            HttpResponse.builder().statusCode(404).build()).getUserApi().get();
      assertNull(api.getByName("fred"));
   }
   
   public void testListRolesOfUser() {
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/users/3f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/user_role_list.json", APPLICATION_JSON)).build())
            .getUserApi().get();
      Set<? extends Role> roles = api.listRolesOfUser("3f6c1c9ba993495ead7d2eb2192e284f");
      assertNotNull(roles);
      assertFalse(roles.isEmpty());
      assertEquals(roles, ImmutableSet.of(
            Role.builder().id("79cada5c02814b57a52e0eed4dd388cb").name("admin").build()
      ));
   }

   public void testListRolesOfUserFailNotFound() {
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/users/4f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            HttpResponse.builder().statusCode(404).build()).getUserApi().get();
      assertTrue(api.listRolesOfUser("4f6c1c9ba993495ead7d2eb2192e284f").isEmpty());
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testListRolesOfUserFailNotImplemented() {
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/users/5f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            HttpResponse.builder().statusCode(501).build()).getUserApi().get();
      assertTrue(api.listRolesOfUser("5f6c1c9ba993495ead7d2eb2192e284f").isEmpty());
   }

   public void testListRolesOfUserInTenant() {
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/users/3f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/user_tenant_role_list.json", APPLICATION_JSON)).build())
            .getUserApi().get();
      Set<? extends Role> roles = api.listRolesOfUser("3f6c1c9ba993495ead7d2eb2192e284f");
      assertNotNull(roles);
      assertFalse(roles.isEmpty());
      assertEquals(roles, ImmutableSet.of(
         Role.builder().id("31c451195aac49b386039341e2c92a16").name("KeystoneServiceAdmin").build(),
         Role.builder().id("79cada5c02814b57a52e0eed4dd388cb").name("admin").build(),
         Role.builder().id("6ea17ddd37a6447794cb0e164d4db894").name("KeystoneAdmin").build()));
   }

   public void testListRolesOfUserInTenantFailNotFound() {
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/users/3f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            HttpResponse.builder().statusCode(404).build()).getUserApi().get();
      assertTrue(api.listRolesOfUser("3f6c1c9ba993495ead7d2eb2192e284f").isEmpty());
   }
   
}
