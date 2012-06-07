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
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.net.URI;
import java.util.Set;

import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneClientLiveTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests TokenClient
 * 
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "TokenClientLiveTest", singleThreaded = true)
public class TokenClientLiveTest extends BaseKeystoneClientLiveTest {

   protected String token;

   // Get the token currently in use (there's currently no listTokens())
   @BeforeMethod
   public void grabToken() {
      AuthenticateRequest ar = keystoneContext.getUtils().getInjector().getInstance(AuthenticateRequest.class);
      HttpRequest test = ar.filter(HttpRequest.builder().method("GET").endpoint(URI.create(endpoint)).build());
      token = Iterables.getOnlyElement(test.getHeaders().get("X-Auth-Token"));
   }

   public void testToken() {

      TokenClient client = keystoneContext.getApi().getTokenClient().get();
      assertTrue(client.isValid(token));
      Token result = client.get(token);
      assertNotNull(result);
      assertEquals(result.getId(), token);
      assertNotNull(result.getTenant());

      User user = client.getUserOfToken(token);
      assertNotNull(user);
      assertNotNull(user.getId());
      assertNotNull(user.getName());

   }

   public void testInvalidToken() {

      TokenClient client = keystoneContext.getApi().getTokenClient().get();
      assertFalse(client.isValid("thisisnotarealtoken!"));
      assertNull(client.get("thisisnotarealtoken!"));

   }

   public void testTokenEndpoints() {

      TokenClient client = keystoneContext.getApi().getTokenClient().get();
      Set<Endpoint> endpoints = client.listEndpointsForToken(token);
      assertNotNull(endpoints);
      assertFalse(endpoints.isEmpty());

   }

   public void testInvalidTokenEndpoints() {

      TokenClient client = keystoneContext.getApi().getTokenClient().get();
      assertTrue(client.listEndpointsForToken("thisisnotarealtoken!").isEmpty());

   }
}