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

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.RequiresHttp;
import org.jclouds.openstack.keystone.v2_0.config.KeyStoneAuthenticationModule;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.config.NovaRestClientModule;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;
import com.google.inject.Module;

/**
 * Base class for writing Nova Rest Client Expect tests
 * 
 * @author Adrian Cole
 */
public class BaseNovaRestClientExpectTest extends BaseRestClientExpectTest<NovaClient> {

   public BaseNovaRestClientExpectTest() {
      provider = "openstack-nova";
      // username:tenantId
      identity = "user@jclouds.org:12346637803162";
      credential = "Password1234";
   }

   protected HttpRequest initialAuth = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("http://localhost:5000/v2.0/tokens"))
            .headers(ImmutableMultimap.of(HttpHeaders.ACCEPT, "application/json"))
            .payload(
                     payloadFromStringWithContentType(
                              "{\"tenantId\":\"12346637803162\",\"auth\":{\"passwordCredentials\":{\"username\":\"user@jclouds.org\",\"password\":\"Password1234\"}}}",
                              "application/json")).build();

   protected String authToken = "d6245d35-22a0-47c0-9770-2c5097da25fc";

   protected HttpResponse responseWithUrls = HttpResponse.builder().statusCode(200).message("HTTP/1.1 200").payload(
            payloadFromResourceWithContentType("/keystoneAuthResponse.json", "application/json")).build();


   /**
    * in case you need to override anything
    */
   static class TestKeyStoneAuthenticationModule extends KeyStoneAuthenticationModule {
      @Override
      protected void configure() {
         super.configure();
      }

   }

   @Override
   protected Module createModule() {
      return new TestNovaRestClientModule();
   }

   @ConfiguresRestClient
   @RequiresHttp
   protected static class TestNovaRestClientModule extends NovaRestClientModule {
      private TestNovaRestClientModule() {
         super(new TestKeyStoneAuthenticationModule());
      }
   }

}
