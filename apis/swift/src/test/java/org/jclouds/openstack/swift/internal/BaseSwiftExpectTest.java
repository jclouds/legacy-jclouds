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
package org.jclouds.openstack.swift.internal;

import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

/**
 * Base class for writing Swift Expect tests
 * 
 * @author Adrian Cole
 */
public class BaseSwiftExpectTest<T> extends BaseRestClientExpectTest<T> {

   protected String endpoint = "http://myhost:8080/auth";
   protected HttpRequest authRequest;
   public BaseSwiftExpectTest() {
      provider = "swift";
      identity = "test:tester";
      credential = "testing";
      authRequest = HttpRequest.builder()
            .method("GET")
            .endpoint(endpoint+ "/v1.0")
            .addHeader("X-Auth-User", identity)
            .addHeader("X-Auth-Key", credential)
            .addHeader("Accept", "*/*")
            .addHeader("Host", "myhost:8080").build();
   }
   
   protected String authToken = "AUTH_tk36dabe83ca744cc296a98ec46089ec35";

   protected String swiftEndpoint = "http://127.0.0.1:8080/v1/AUTH_test";

   /**
    * often swift returns the localhost url when requested via a dns name. this
    * test ensures that replacement works.
    */
   protected String swiftEndpointWithHostReplaced = swiftEndpoint.replace("127.0.0.1", "myhost");
   
   protected HttpResponse authResponse = HttpResponse.builder()
         .statusCode(200)
         .message("HTTP/1.1 200 OK")
         .addHeader("X-Storage-Url", swiftEndpoint)
         .addHeader("X-Auth-Token", authToken).build();
   
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(provider+".endpoint", endpoint);
      return props;
   }
}
