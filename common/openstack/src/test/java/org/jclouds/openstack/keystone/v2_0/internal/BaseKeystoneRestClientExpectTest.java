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
package org.jclouds.openstack.keystone.v2_0.internal;

import static java.lang.String.format;
import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.rest.BaseRestClientExpectTest;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;

/**
 * Base class for writing KeyStone Rest Client Expect tests
 * 
 * @author Adrian Cole
 */
public class BaseKeystoneRestClientExpectTest<S> extends BaseRestClientExpectTest<S> {

   protected static final String tenantId = "12346637803162";

   protected static final String username = "user@jclouds.org";
   protected static final String password = "Password1234";
   protected static final HttpRequest initialAuthWithPasswordCredentials = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("http://localhost:5000/v2.0/tokens"))
            .headers(ImmutableMultimap.of(HttpHeaders.ACCEPT, "application/json"))
            .payload(
                     payloadFromStringWithContentType(
                              format(
                                       "{\"auth\":{\"passwordCredentials\":{\"username\":\"%s\",\"password\":\"%s\"},\"tenantId\":\"%s\"}}",
                                       username, password, tenantId), "application/json")).build();

   protected static final String accessKey = "FH6FU8GMZFLKP5BUR2X1";
   protected static final String secretKey = "G4QWed0lh5SH7kBrcvOM1cHygKWk81EBt+Hr1dsl";
   protected static final HttpRequest initialAuthWithApiAccessKeyCredentials = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("http://localhost:5000/v2.0/tokens"))
            .headers(ImmutableMultimap.of(HttpHeaders.ACCEPT, "application/json"))
            .payload(
                     payloadFromStringWithContentType(
                              format(
                                       "{\"auth\":{\"apiAccessKeyCredentials\":{\"accessKey\":\"%s\",\"secretKey\":\"%s\"},\"tenantId\":\"%s\"}}",
                                       accessKey, secretKey, tenantId), "application/json")).build();

   public BaseKeystoneRestClientExpectTest() {
      identity = tenantId + ":" + accessKey;
      credential = secretKey;
   }


   protected String authToken = "Auth_4f173437e4b013bee56d1007";

   protected HttpResponse responseWithAccess = HttpResponse.builder().statusCode(200).message("HTTP/1.1 200").payload(
            payloadFromResourceWithContentType("/keystoneAuthResponse.json", "application/json")).build();


   /**
    * in case you need to override anything
    */
   public static class TestKeystoneAuthenticationModule extends KeystoneAuthenticationModule {
      @Override
      protected void configure() {
         super.configure();
      }

   }

}
