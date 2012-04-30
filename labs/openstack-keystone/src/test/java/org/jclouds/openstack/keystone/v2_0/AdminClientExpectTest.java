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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponseException;
import org.jclouds.openstack.domain.Link;
import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.MediaType;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestClientExpectTest;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of UserClient
 *
 * @author Adam Lowe
 */
public class AdminClientExpectTest extends BaseKeystoneRestClientExpectTest<KeystoneClient> {
   private DateService dateService = new SimpleDateFormatDateService();

   public AdminClientExpectTest() {
      endpoint = "https://csnode.jclouds.org:35357";
   }

   public void testGetApiMetaData() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/").
                  headers(ImmutableMultimap.of("Accept", APPLICATION_JSON)).build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/api_metadata.json", APPLICATION_JSON)).build())
            .getAdminClientForRegion("region-a.geo-1");
      ApiMetadata metadata = client.getApiMetadata();
      assertNotNull(metadata);
      assertEquals(metadata.getId(), "v2.0");

      ApiMetadata expected = ApiMetadata.builder().id("v2.0")
            .links(ImmutableSet.of(Link.builder().relation(Link.Relation.SELF).href(URI.create("http://172.16.89.140:5000/v2.0/")).build(),
                  Link.builder().relation(Link.Relation.DESCRIBEDBY).type("text/html").href(URI.create("http://docs.openstack.org/api/openstack-identity-service/2.0/content/")).build(),
                  Link.builder().relation(Link.Relation.DESCRIBEDBY).type("application/pdf").href(URI.create("http://docs.openstack.org/api/openstack-identity-service/2.0/identity-dev-guide-2.0.pdf")).build()
            ))
            .status("beta")
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-11-19T00:00:00Z"))
            .mediaTypes(ImmutableSet.of(
                  MediaType.builder().base("application/json").type("application/vnd.openstack.identity-v2.0+json").build(),
                  MediaType.builder().base("application/xml").type("application/vnd.openstack.identity-v2.0+xml").build()
            ))
            .build();

      assertEquals(metadata, expected);
   }

   public void testGetApiMetaDataFailNotFound() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/").headers(ImmutableMultimap.of("Accept", APPLICATION_JSON)).build(),
            standardResponseBuilder(404).build()).getAdminClientForRegion("region-a.geo-1");
      assertNull(client.getApiMetadata());
   }

   public void testListTenants() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tenants").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/tenant_list.json", APPLICATION_JSON)).build())
            .getAdminClientForRegion("region-a.geo-1");
      Set<Tenant> tenants = client.listTenants();
      assertNotNull(tenants);
      assertFalse(tenants.isEmpty());

      Set<Tenant> expected = ImmutableSet.of(
            Tenant.builder().name("demo").id("05d1dc7af71646deba64cfc17b81bec0").build(),
            Tenant.builder().name("admin").id("7aa2e17ec29f44d193c48feaba0852cc").build()
      );

      assertEquals(tenants, expected);
   }

   public void testListTenantsFailNotFound() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tenants").build(),
            standardResponseBuilder(404).build()).getAdminClientForRegion("region-a.geo-1");
      assertTrue(client.listTenants().isEmpty());
   }
   
   public void testGetTenant() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tenants/013ba41150a14830bec85ffe93353bcc").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/tenant_details.json", APPLICATION_JSON)).build())
            .getAdminClientForRegion("region-a.geo-1");
      Tenant tenant = client.getTenant("013ba41150a14830bec85ffe93353bcc");
      assertNotNull(tenant);
      assertEquals(tenant, Tenant.builder().id("013ba41150a14830bec85ffe93353bcc").name("admin").build());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testListTenantsFailNotAuthorized() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tenants/013ba41150a14830bec85ffe93353bcc").build(),
            standardResponseBuilder(401).build()).getAdminClientForRegion("region-a.geo-1");
      client.getTenant("013ba41150a14830bec85ffe93353bcc");
   }
   
   public void testGetTenantByName() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tenants?name=admin").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/tenant_details.json", APPLICATION_JSON)).build())
            .getAdminClientForRegion("region-a.geo-1");
      Tenant tenant = client.getTenantByName("admin");
      assertNotNull(tenant);
      assertEquals(tenant, Tenant.builder().id("013ba41150a14830bec85ffe93353bcc").name("admin").build());
   }

   public void testGetTenantByNameFailNotFound() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tenants?name=admin").build(),
            standardResponseBuilder(404).build()).getAdminClientForRegion("region-a.geo-1");
      assertNull(client.getTenantByName("admin"));
   }

   public void testGetToken() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/token_details.json", APPLICATION_JSON)).build())
            .getAdminClientForRegion("region-a.geo-1");
      Token token = client.getToken("sometokenorother");
      assertNotNull(token);
      assertEquals(token, 
            Token.builder().id("167eccdc790946969ced473732e8109b").expires(dateService.iso8601SecondsDateParse("2012-04-28T12:42:50Z"))
                  .tenant(Tenant.builder().id("4cea93f5464b4f1c921fb3e0461d72b5").name("demo").build()).build());
   }

   public void testGetTokenFailNotFound() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").build(),
            standardResponseBuilder(404).build())
            .getAdminClientForRegion("region-a.geo-1");
      assertNull(client.getToken("sometokenorother"));
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testGetTokenFail500() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").build(),
            standardResponseBuilder(500).build()).getAdminClientForRegion("region-a.geo-1");
      client.getToken("sometokenorother");
   }
   
   public void testGetUserOfToken() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/token_details.json", APPLICATION_JSON)).build())
            .getAdminClientForRegion("region-a.geo-1");
      User user = client.getUserOfToken("sometokenorother");
      assertNotNull(user);
      assertEquals(user, User.builder().id("2b9b606181634ae9ac86fd95a8bc2cde").name("admin")
            .roles(ImmutableSet.of(Role.builder().id("79cada5c02814b57a52e0eed4dd388cb").name("admin").build()))
            .build());
   }

   public void testGetUserOfTokenFailNotFound() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").build(),
            standardResponseBuilder(404).build()).getAdminClientForRegion("region-a.geo-1");
      assertNull(client.getUserOfToken("sometokenorother"));
   }

   public void testCheckTokenIsValid() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").method("HEAD")
                  .headers(ImmutableMultimap.of("X-Auth-Token", authToken)).build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/token_details.json", APPLICATION_JSON)).build())
            .getAdminClientForRegion("region-a.geo-1");
      assertTrue(client.checkTokenIsValid("sometokenorother"));
   }

   public void testCheckTokenIsValidFailNotValid() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").method("HEAD")
                  .headers(ImmutableMultimap.of("X-Auth-Token", authToken)).build(),
            standardResponseBuilder(404).build()).getAdminClientForRegion("region-a.geo-1");
      assertFalse(client.checkTokenIsValid("sometokenorother"));
   }

   @Test
   public void testGetEndpointsForToken() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/XXXXXX/endpoints").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/user_endpoints.json", APPLICATION_JSON)).build())
            .getAdminClientForRegion("region-a.geo-1");
      Set<Endpoint> endpoints = client.getEndpointsForToken("XXXXXX");
      
      assertEquals(endpoints, ImmutableSet.of(
            Endpoint.builder().publicURL(URI.create("https://csnode.jclouds.org/v2.0/"))
                  .adminURL(URI.create("https://csnode.jclouds.org:35357/v2.0/"))
                  .region("region-a.geo-1").versionId("2.0").build()
      ));
   }
   
   @Test
   public void testGetEndpointsForTokenFailNotFound() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/XXXXXX/endpoints").build(),
            standardResponseBuilder(404).build())
            .getAdminClientForRegion("region-a.geo-1");
      assertTrue(client.getEndpointsForToken("XXXXXX").isEmpty());
   }

   public void testListUsers() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/user_list.json", APPLICATION_JSON)).build())
            .getAdminClientForRegion("region-a.geo-1");
      Set<User> users = client.listUsers();
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
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users").build(),
            standardResponseBuilder(401).build()).getAdminClientForRegion("region-a.geo-1");
      client.listUsers();
   }

   public void testGetUser() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/e021dfd758eb44a89f1c57c8ef3be8e2").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/user_details.json", APPLICATION_JSON)).build())
            .getAdminClientForRegion("region-a.geo-1");
      User user = client.getUser("e021dfd758eb44a89f1c57c8ef3be8e2");
      assertNotNull(user);
      assertEquals(user, User.builder().name("nova").id("e021dfd758eb44a89f1c57c8ef3be8e2").build());
   }

   public void testGetUserFailNotFound() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/f021dfd758eb44a89f1c57c8ef3be8e2").build(),
            standardResponseBuilder(404).build()).getAdminClientForRegion("region-a.geo-1");
      assertNull(client.getUser("f021dfd758eb44a89f1c57c8ef3be8e2"));
   }

   public void testGetUserByName() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users?name=nova").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/user_details.json", APPLICATION_JSON)).build())
            .getAdminClientForRegion("region-a.geo-1");
      User user = client.getUserByName("nova");
      assertNotNull(user);
      assertEquals(user, User.builder().name("nova").id("e021dfd758eb44a89f1c57c8ef3be8e2").build());
   }

   public void testGetUserByNameFailNotFound() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users?name=fred").build(),
            standardResponseBuilder(404).build()).getAdminClientForRegion("region-a.geo-1");
      assertNull(client.getUserByName("fred"));
   }
   
   public void testListRolesOfUser() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/3f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/user_role_list.json", APPLICATION_JSON)).build())
            .getAdminClientForRegion("region-a.geo-1");
      Set<Role> roles = client.listRolesOfUser("3f6c1c9ba993495ead7d2eb2192e284f");
      assertNotNull(roles);
      assertFalse(roles.isEmpty());
      assertEquals(roles, ImmutableSet.of(
            Role.builder().id("79cada5c02814b57a52e0eed4dd388cb").name("admin").build()
      ));
   }

   public void testListRolesOfUserFailNotFound() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/4f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            standardResponseBuilder(404).build()).getAdminClientForRegion("region-a.geo-1");
      assertTrue(client.listRolesOfUser("4f6c1c9ba993495ead7d2eb2192e284f").isEmpty());
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testListRolesOfUserFailNotImplemented() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/5f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            standardResponseBuilder(501).build()).getAdminClientForRegion("region-a.geo-1");
      assertTrue(client.listRolesOfUser("5f6c1c9ba993495ead7d2eb2192e284f").isEmpty());
   }

   public void testListRolesOfUserInTenant() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/3f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/user_tenant_role_list.json", APPLICATION_JSON)).build())
            .getAdminClientForRegion("region-a.geo-1");
      Set<Role> roles = client.listRolesOfUser("3f6c1c9ba993495ead7d2eb2192e284f");
      assertNotNull(roles);
      assertFalse(roles.isEmpty());
      assertEquals(roles, ImmutableSet.of(
         Role.builder().id("31c451195aac49b386039341e2c92a16").name("KeystoneServiceAdmin").build(),
         Role.builder().id("79cada5c02814b57a52e0eed4dd388cb").name("admin").build(),
         Role.builder().id("6ea17ddd37a6447794cb0e164d4db894").name("KeystoneAdmin").build()));
   }

   public void testListRolesOfUserInTenantFailNotFound() {
      AdminClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/users/3f6c1c9ba993495ead7d2eb2192e284f/roles").build(),
            standardResponseBuilder(404).build()).getAdminClientForRegion("region-a.geo-1");
      assertTrue(client.listRolesOfUser("3f6c1c9ba993495ead7d2eb2192e284f").isEmpty());
   }
   
}
