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
package org.jclouds.openstack.keystone.v2_0.internal;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.KeystoneApiMetadata;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMultimap;

/**
 * Base class for writing KeyStone 2.0 Rest Client Expect tests
 * 
 * @author Adam Lowe
 */
public class BaseKeystoneRestClientExpectTest<S> extends BaseRestClientExpectTest<S> {
   protected HttpRequest keystoneAuthWithUsernameAndPassword;
   protected HttpRequest keystoneAuthWithAccessKeyAndSecretKey;
   protected String authToken;
   protected HttpResponse responseWithKeystoneAccess;
   protected String endpoint = "http://localhost:5000";

   public BaseKeystoneRestClientExpectTest() {
      provider = "openstack-keystone";
      keystoneAuthWithUsernameAndPassword = KeystoneFixture.INSTANCE.initialAuthWithUsernameAndPasswordAndTenantName(identity,
               credential);
      keystoneAuthWithAccessKeyAndSecretKey = KeystoneFixture.INSTANCE.initialAuthWithAccessKeyAndSecretKeyAndTenantName(identity,
               credential);

      authToken = KeystoneFixture.INSTANCE.getAuthToken();
      responseWithKeystoneAccess = KeystoneFixture.INSTANCE.responseWithAccess();
      // now, createContext arg will need tenant prefix
      identity = KeystoneFixture.INSTANCE.getTenantName() + ":" + identity;
   }

   protected HttpRequest.Builder standardRequestBuilder(String endpoint) {
      return HttpRequest.builder().method("GET").headers(
               ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken)).endpoint(
               URI.create(endpoint));
   }

   protected HttpResponse.Builder standardResponseBuilder(int status) {
      return HttpResponse.builder().statusCode(status);
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(SERVICE_TYPE, ServiceType.IDENTITY);
      return props;
   }

   @Override
   protected HttpRequestComparisonType compareHttpRequestAsType(HttpRequest input) {
      return Objects.equal("HEAD", input.getMethod()) ? HttpRequestComparisonType.DEFAULT
               : HttpRequestComparisonType.JSON;
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new KeystoneApiMetadata();
   }

}
