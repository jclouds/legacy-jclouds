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

import java.net.URI;
import java.util.Set;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of TokenApi
 *
 * @author Adam Lowe
 */
@Test(testName = "TokenApiExpectTest")
public class TokenApiExpectTest extends BaseKeystoneRestApiExpectTest<KeystoneApi> {

   public TokenApiExpectTest(){
      endpoint = "https://csnode.jclouds.org:35357";
   }

   private DateService dateService = new SimpleDateFormatDateService();

   public void testGetToken() {
      TokenApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/tokens/sometokenorother").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/token_details.json", APPLICATION_JSON)).build())
            .getTokenApi().get();
      Token token = api.get("sometokenorother");
      assertNotNull(token);
      assertEquals(token,
            Token.builder().id("167eccdc790946969ced473732e8109b").expires(dateService.iso8601SecondsDateParse("2012-04-28T12:42:50Z"))
                  .tenant(Tenant.builder().id("4cea93f5464b4f1c921fb3e0461d72b5").name("demo").build()).build());
   }

   public void testGetTokenFailNotFound() {
      TokenApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/tokens/sometokenorother").build(),
            HttpResponse.builder().statusCode(404).build())
            .getTokenApi().get();
      assertNull(api.get("sometokenorother"));
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testGetTokenFail500() {
      TokenApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/tokens/sometokenorother").build(),
            HttpResponse.builder().statusCode(500).build()).getTokenApi().get();
      api.get("sometokenorother");
   }

   public void testGetUserOfToken() {
      TokenApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/tokens/sometokenorother").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/token_details.json", APPLICATION_JSON)).build())
            .getTokenApi().get();
      User user = api.getUserOfToken("sometokenorother");
      assertNotNull(user);
      assertEquals(user, User.builder().id("2b9b606181634ae9ac86fd95a8bc2cde").name("admin")
            .roles(ImmutableSet.of(Role.builder().id("79cada5c02814b57a52e0eed4dd388cb").name("admin").build()))
            .build());
   }

   public void testGetUserOfTokenFailNotFound() {
      TokenApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/tokens/sometokenorother").build(),
            HttpResponse.builder().statusCode(404).build()).getTokenApi().get();
      assertNull(api.getUserOfToken("sometokenorother"));
   }

   public void testCheckTokenIsValid() {
      TokenApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            HttpRequest.builder().method("HEAD")
                       .endpoint(endpoint + "/v2.0/tokens/sometokenorother")
                       .addHeader("X-Auth-Token", authToken).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/token_details.json", APPLICATION_JSON)).build())
            .getTokenApi().get();
      assertTrue(api.isValid("sometokenorother"));
   }

   public void testCheckTokenIsValidFailNotValid() {
      TokenApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            HttpRequest.builder().method("HEAD")
                       .endpoint(endpoint + "/v2.0/tokens/sometokenorother")
                       .addHeader("X-Auth-Token", authToken).build(),
            HttpResponse.builder().statusCode(404).build()).getTokenApi().get();
      assertFalse(api.isValid("sometokenorother"));
   }

   @Test
   public void testGetEndpointsForToken() {
      TokenApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/tokens/XXXXXX/endpoints").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/user_endpoints.json", APPLICATION_JSON)).build())
            .getTokenApi().get();
      Set<? extends Endpoint> endpoints = api.listEndpointsForToken("XXXXXX");

      assertEquals(endpoints, ImmutableSet.of(
            Endpoint.builder().publicURL(URI.create("https://csnode.jclouds.org/v2.0/"))
                  .adminURL(URI.create("https://csnode.jclouds.org:35357/v2.0/"))
                  .region("region-a.geo-1").id("2.0").versionId("2.0").build()
      ));
   }

   @Test
   public void testGetEndpointsForTokenFailNotFound() {
      TokenApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/tokens/XXXXXX/endpoints").build(),
            HttpResponse.builder().statusCode(404).build())
            .getTokenApi().get();
      assertTrue(api.listEndpointsForToken("XXXXXX").isEmpty());
   }


}
