/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.nova.v1_1.internal;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.internal.KeystoneFixture;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

import com.google.common.collect.ImmutableMultimap;

/**
 * Base class for writing Nova Expect tests
 * 
 * @author Adrian Cole
 */
public class BaseNovaExpectTest<T> extends BaseRestClientExpectTest<T> {
   protected HttpRequest keystoneAuthWithUsernameAndPassword;
   protected HttpRequest keystoneAuthWithAccessKeyAndSecretKey;
   protected String authToken;
   protected HttpResponse responseWithKeystoneAccess;
   protected HttpRequest extensionsOfNovaRequest;
   protected HttpResponse extensionsOfNovaResponse;
   protected HttpResponse unmatchedExtensionsOfNovaResponse;

   public BaseNovaExpectTest() {
      provider = "openstack-nova";
      keystoneAuthWithUsernameAndPassword = KeystoneFixture.INSTANCE.initialAuthWithUsernameAndPassword(identity,
            credential);
      keystoneAuthWithAccessKeyAndSecretKey = KeystoneFixture.INSTANCE.initialAuthWithAccessKeyAndSecretKey(identity,
            credential);
      
      authToken = KeystoneFixture.INSTANCE.getAuthToken();
      responseWithKeystoneAccess = KeystoneFixture.INSTANCE.responseWithAccess();
      // now, createContext arg will need tenant prefix
      identity = KeystoneFixture.INSTANCE.getTenantName() + ":" + identity;
      
      extensionsOfNovaRequest = HttpRequest
            .builder()
            .method("GET")
             // NOTE THIS IS NOVA, NOT KEYSTONE
            .endpoint(URI.create("https://compute.north.host/v1.1/3456/extensions"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      extensionsOfNovaResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/extension_list_full.json")).build();
      
      unmatchedExtensionsOfNovaResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/extension_list.json")).build();
   }
   
   protected HttpRequest.Builder standardRequestBuilder(URI endpoint) {
      return HttpRequest.builder().method("GET")
            .headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
            .endpoint(endpoint);
   }

   protected HttpResponse.Builder standardResponseBuilder(int status) {
      return HttpResponse.builder().statusCode(status);
   }
}
