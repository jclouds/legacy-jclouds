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
package org.jclouds.openstack.cinder.v1.internal;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.internal.KeystoneFixture;
import org.jclouds.rest.internal.BaseRestApiExpectTest;

/**
 * Base class for writing Nova Expect tests
 * 
 * @author Adrian Cole
 */
public class BaseCinderExpectTest<T> extends BaseRestApiExpectTest<T> {
   protected HttpRequest keystoneAuthWithUsernameAndPassword;
   protected HttpRequest keystoneAuthWithUsernameAndPasswordAndTenantName;
   protected HttpRequest keystoneAuthWithAccessKeyAndSecretKeyAndTenantName;
   protected String authToken;
   protected HttpResponse responseWithKeystoneAccess;
   protected HttpRequest extensionsOfNovaRequest;
   protected HttpResponse extensionsOfNovaResponse;
   protected HttpResponse unmatchedExtensionsOfNovaResponse;
   protected HttpRequest keystoneAuthWithAccessKeyAndSecretKeyAndTenantId;
   protected String identityWithTenantId;

   public BaseCinderExpectTest() {
      provider = "openstack-cinder";
      keystoneAuthWithUsernameAndPassword = KeystoneFixture.INSTANCE.initialAuthWithUsernameAndPassword(identity,
            credential);
      keystoneAuthWithUsernameAndPasswordAndTenantName = KeystoneFixture.INSTANCE.initialAuthWithUsernameAndPasswordAndTenantName(identity,
            credential);
      keystoneAuthWithAccessKeyAndSecretKeyAndTenantName = KeystoneFixture.INSTANCE.initialAuthWithAccessKeyAndSecretKeyAndTenantName(identity,
            credential);
      keystoneAuthWithAccessKeyAndSecretKeyAndTenantId = KeystoneFixture.INSTANCE.initialAuthWithAccessKeyAndSecretKeyAndTenantId(identity,
              credential);
      
      authToken = KeystoneFixture.INSTANCE.getAuthToken();
      responseWithKeystoneAccess = KeystoneFixture.INSTANCE.responseWithAccess();
      // now, createContext arg will need tenant prefix
      identityWithTenantId = KeystoneFixture.INSTANCE.getTenantId() + ":" + identity;
      identity = KeystoneFixture.INSTANCE.getTenantName() + ":" + identity;
   }

   @Override
   protected HttpRequestComparisonType compareHttpRequestAsType(HttpRequest input) {
      return HttpRequestComparisonType.JSON;
   }
   
   protected HttpRequest.Builder<?> authenticatedGET() {
      return HttpRequest.builder()
                        .method("GET")
                        .addHeader("Accept", MediaType.APPLICATION_JSON)
                        .addHeader("X-Auth-Token", authToken);
   }
}
