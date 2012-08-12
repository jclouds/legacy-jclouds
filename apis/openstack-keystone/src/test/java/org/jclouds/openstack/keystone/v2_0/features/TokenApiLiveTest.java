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

import java.util.Set;

import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneApiLiveTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests TokenApi
 * 
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "TokenApiLiveTest", singleThreaded = true)
public class TokenApiLiveTest extends BaseKeystoneApiLiveTest {

   protected String token;

   // Get the token currently in use (there's currently no listTokens())
   @BeforeMethod
   public void grabToken() {
      AuthenticateRequest ar = keystoneContext.getUtils().getInjector().getInstance(AuthenticateRequest.class);
      HttpRequest test = ar.filter(HttpRequest.builder().method("GET").endpoint(context.getProviderMetadata().getEndpoint()).build());
      token = Iterables.getOnlyElement(test.getHeaders().get("X-Auth-Token"));
   }

   public void testToken() {

      TokenApi api = keystoneContext.getApi().getTokenApi().get();
      assertTrue(api.isValid(token));
      Token result = api.get(token);
      assertNotNull(result);
      assertEquals(result.getId(), token);
      assertNotNull(result.getTenant());

      User user = api.getUserOfToken(token);
      assertNotNull(user);
      assertNotNull(user.getId());
      assertNotNull(user.getName());

   }

   public void testInvalidToken() {

      TokenApi api = keystoneContext.getApi().getTokenApi().get();
      assertFalse(api.isValid("thisisnotarealtoken!"));
      assertNull(api.get("thisisnotarealtoken!"));

   }

   public void testTokenEndpoints() {

      TokenApi api = keystoneContext.getApi().getTokenApi().get();
      Set<? extends Endpoint> endpoints = api.listEndpointsForToken(token);
      assertNotNull(endpoints);
      assertFalse(endpoints.isEmpty());

   }

   public void testInvalidTokenEndpoints() {

      TokenApi api = keystoneContext.getApi().getTokenApi().get();
      assertTrue(api.listEndpointsForToken("thisisnotarealtoken!").isEmpty());

   }
}
