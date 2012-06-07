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

import java.net.URI;
import java.util.Set;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponseException;
import org.jclouds.openstack.keystone.v2_0.KeystoneClient;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of TokenClient
 *
 * @author Adam Lowe
 */
@Test(testName = "TokenClientExpectTest")
public class TokenClientExpectTest extends BaseKeystoneRestClientExpectTest<KeystoneClient> {

   public TokenClientExpectTest(){
      endpoint = "https://csnode.jclouds.org:35357";
   }
   
   private DateService dateService = new SimpleDateFormatDateService();

   public void testGetToken() {
      TokenClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/token_details.json", APPLICATION_JSON)).build())
            .getTokenClient().get();
      Token token = client.get("sometokenorother");
      assertNotNull(token);
      assertEquals(token, 
            Token.builder().id("167eccdc790946969ced473732e8109b").expires(dateService.iso8601SecondsDateParse("2012-04-28T12:42:50Z"))
                  .tenant(Tenant.builder().id("4cea93f5464b4f1c921fb3e0461d72b5").name("demo").build()).build());
   }

   public void testGetTokenFailNotFound() {
      TokenClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").build(),
            standardResponseBuilder(404).build())
            .getTokenClient().get();
      assertNull(client.get("sometokenorother"));
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testGetTokenFail500() {
      TokenClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").build(),
            standardResponseBuilder(500).build()).getTokenClient().get();
      client.get("sometokenorother");
   }
   
   public void testGetUserOfToken() {
      TokenClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/token_details.json", APPLICATION_JSON)).build())
            .getTokenClient().get();
      User user = client.getUserOfToken("sometokenorother");
      assertNotNull(user);
      assertEquals(user, User.builder().id("2b9b606181634ae9ac86fd95a8bc2cde").name("admin")
            .roles(ImmutableSet.of(Role.builder().id("79cada5c02814b57a52e0eed4dd388cb").name("admin").build()))
            .build());
   }

   public void testGetUserOfTokenFailNotFound() {
      TokenClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").build(),
            standardResponseBuilder(404).build()).getTokenClient().get();
      assertNull(client.getUserOfToken("sometokenorother"));
   }

   public void testCheckTokenIsValid() {
      TokenClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").method("HEAD")
                  .headers(ImmutableMultimap.of("X-Auth-Token", authToken)).build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/token_details.json", APPLICATION_JSON)).build())
            .getTokenClient().get();
      assertTrue(client.isValid("sometokenorother"));
   }

   public void testCheckTokenIsValidFailNotValid() {
      TokenClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/sometokenorother").method("HEAD")
                  .headers(ImmutableMultimap.of("X-Auth-Token", authToken)).build(),
            standardResponseBuilder(404).build()).getTokenClient().get();
      assertFalse(client.isValid("sometokenorother"));
   }

   @Test
   public void testGetEndpointsForToken() {
      TokenClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/XXXXXX/endpoints").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/user_endpoints.json", APPLICATION_JSON)).build())
            .getTokenClient().get();
      Set<Endpoint> endpoints = client.listEndpointsForToken("XXXXXX");
      
      assertEquals(endpoints, ImmutableSet.of(
            Endpoint.builder().publicURL(URI.create("https://csnode.jclouds.org/v2.0/"))
                  .adminURL(URI.create("https://csnode.jclouds.org:35357/v2.0/"))
                  .region("region-a.geo-1").versionId("2.0").build()
      ));
   }
   
   @Test
   public void testGetEndpointsForTokenFailNotFound() {
      TokenClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tokens/XXXXXX/endpoints").build(),
            standardResponseBuilder(404).build())
            .getTokenClient().get();
      assertTrue(client.listEndpointsForToken("XXXXXX").isEmpty());
   }

   
}
