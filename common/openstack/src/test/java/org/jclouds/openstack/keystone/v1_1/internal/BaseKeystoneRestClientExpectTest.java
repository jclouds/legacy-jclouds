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
package org.jclouds.openstack.keystone.v1_1.internal;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v1_1.config.AuthenticationServiceModule;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;

/**
 * Base class for writing KeyStone Rest Client Expect tests
 * 
 * @author Adrian Cole
 */
public class BaseKeystoneRestClientExpectTest<S> extends BaseRestClientExpectTest<S> {

   protected String endpoint = "http://localhost:5000";

   public BaseKeystoneRestClientExpectTest() {
      identity = "user@jclouds.org";
      credential = "Password1234";
   }

   protected HttpRequest initialAuth = HttpRequest
         .builder()
         .method("POST")
         .endpoint(URI.create(endpoint + "/v1.1/auth"))
         .headers(ImmutableMultimap.of(HttpHeaders.ACCEPT, "application/json"))
         .payload(
               payloadFromStringWithContentType(
                     "{\"credentials\":{\"username\":\"user@jclouds.org\",\"key\":\"Password1234\"}}",
                     "application/json")).build();

   protected String authToken = "118fb907-0786-4799-88f0-9a5b7963d1ab";

   protected HttpResponse responseWithAuth = HttpResponse.builder().statusCode(200).message("HTTP/1.1 200")
         .payload(payloadFromResourceWithContentType("/auth1_1.json", "application/json")).build();

   /**
    * in case you need to override anything
    */
   public static class TestKeystoneAuthenticationModule extends AuthenticationServiceModule {
      @Override
      protected void configure() {
         super.configure();
      }

   }

}
