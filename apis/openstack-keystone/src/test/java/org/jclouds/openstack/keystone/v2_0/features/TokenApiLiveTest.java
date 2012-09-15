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
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Tests {@link TokenApi}.
 * 
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "TokenApiLiveTest", singleThreaded = true)
public class TokenApiLiveTest extends BaseKeystoneApiLiveTest {

    protected String token;

    /** Gets the token currently in use (there's currently no {@code listTokens()}). */
    @BeforeMethod
    public void grabToken() {
        AuthenticateRequest ar = keystoneContext.getUtils().getInjector().getInstance(AuthenticateRequest.class);
        HttpRequest test = ar.filter(HttpRequest.builder().method("GET").endpoint(context.getProviderMetadata().getEndpoint()).build());
        token = Iterables.getOnlyElement(test.getHeaders().get("X-Auth-Token"));
    }

    @Test(description = "GET /v2.0/tokens/{token}")
    public void testGetToken() {
        try {
            Optional<? extends TokenApi> api = keystoneContext.getApi().getTokenApi();
            if (api.isPresent()) {
                TokenApi tokenApi = api.get();

                Token result = tokenApi.get(token);
                assertNotNull(result);
                assertEquals(result.getId(), token);
                assertNotNull(result.getTenant());

                assertNull(tokenApi.get("thisisnotarealtoken!"));

                User user = tokenApi.getUserOfToken(token);
                assertNotNull(user);
                assertNotNull(user.getId());
                assertNotNull(user.getName());
            }
        } catch (AuthorizationException ae) {
            // Ignore, some Keystone implementations treat this as Admin only
        }
    }

    @Test(description = "HEAD /v2.0/tokens/{token}")
    public void testValidToken() {
        try {
            Optional<? extends TokenApi> api = keystoneContext.getApi().getTokenApi();
            if (api.isPresent()) {
                TokenApi tokenApi = api.get();

                assertTrue(tokenApi.isValid(token));

                assertFalse(tokenApi.isValid("thisisnotarealtoken"));
            }
        } catch (AuthorizationException ae) {
            // Ignore, some Keystone implementations treat this as Admin only
        }
    }

    @Test(description = "GET /v2.0/tokens/{token}/endpoints")
    public void testTokenEndpoints() {
        try {
            Optional<? extends TokenApi> api = keystoneContext.getApi().getTokenApi();
            if (api.isPresent()) {
                TokenApi tokenApi = api.get();

                Set<? extends Endpoint> endpoints = tokenApi.listEndpointsForToken(token);
                assertNotNull(endpoints);
                assertFalse(endpoints.isEmpty());

                assertTrue(tokenApi.listEndpointsForToken("thisisnotarealtoken!").isEmpty());
            }
        } catch (AuthorizationException ae) {
            // Ignore, some Keystone implementations treat this as Admin only
        }
    }
}
