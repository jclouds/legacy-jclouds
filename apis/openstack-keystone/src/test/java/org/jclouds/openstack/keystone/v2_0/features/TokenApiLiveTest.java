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
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Properties;
import java.util.Set;

import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests TokenApi
 * 
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "TokenApiLiveTest", singleThreaded = true)
public class TokenApiLiveTest extends BaseKeystoneApiLiveTest {

   protected String token;
   
   @Override
   protected KeystoneApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      grabToken(injector.getInstance(AuthenticateRequest.class));
      return injector.getInstance(KeystoneApi.class);
   }

   // Get the token currently in use (there's currently no listTokens())
   private void grabToken(AuthenticateRequest ar) {
      HttpRequest test = ar.filter(HttpRequest.builder().method("GET").endpoint(endpoint).build());
      token = Iterables.getOnlyElement(test.getHeaders().get("X-Auth-Token"));
   }

   public void testToken() {

      TokenApi tokenApi = api.getTokenApi().get();
      assertTrue(tokenApi.isValid(token));
      Token result = tokenApi.get(token);
      assertNotNull(result);
      assertEquals(result.getId(), token);
      assertNotNull(result.getTenant());

      User user = tokenApi.getUserOfToken(token);
      assertNotNull(user);
      assertNotNull(user.getId());
      assertNotNull(user.getName());

   }

   public void testInvalidToken() {

      TokenApi tokenApi = api.getTokenApi().get();
      assertFalse(tokenApi.isValid("thisisnotarealtoken!"));
      assertNull(tokenApi.get("thisisnotarealtoken!"));

   }

   public void testTokenEndpoints() {

      TokenApi tokenApi = api.getTokenApi().get();
      Set<? extends Endpoint> endpoints = tokenApi.listEndpointsForToken(token);
      assertNotNull(endpoints);
      assertFalse(endpoints.isEmpty());

   }

   public void testInvalidTokenEndpoints() {

      TokenApi tokenApi = api.getTokenApi().get();
      assertTrue(tokenApi.listEndpointsForToken("thisisnotarealtoken!").isEmpty());

   }
}
