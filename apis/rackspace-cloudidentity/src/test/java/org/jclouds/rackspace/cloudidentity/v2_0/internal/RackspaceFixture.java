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
package org.jclouds.rackspace.cloudidentity.v2_0.internal;

import static java.lang.String.format;
import static org.jclouds.rest.internal.BaseRestApiExpectTest.payloadFromStringWithContentType;

import java.io.IOException;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.jclouds.util.Strings2;

import com.google.common.base.Throwables;
import com.google.common.net.HttpHeaders;

/**
 * @author Everett Toews
 */
public enum RackspaceFixture {
   INSTANCE;

   public String getTenantId() {
      return "123123";
   }

   public String getTenantName() {
      return "123123";
   }

   public HttpRequest initialAuthWithUsernameAndApiKey(String username, String apiKey) {
      return HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://identity.api.rackspacecloud.com/v2.0/tokens")
            .addHeader(HttpHeaders.ACCEPT, "application/json")
            .payload(payloadFromStringWithContentType(
                  format("{\"auth\":{\"RAX-KSKEY:apiKeyCredentials\":{\"username\":\"%s\",\"apiKey\":\"%s\"}}}",
                         username, apiKey), "application/json")).build();
   }

   public String getAuthToken() {
      return "cd562fe2-1f0f-43a7-a898-98cb8ede3eb3";
   }

   public HttpResponse responseWithAccess() {
      return HttpResponse.builder()
            .statusCode(200)
            .message("HTTP/1.1 200")
            .payload(payloadFromResourceWithContentType("/rackspaceAuthResponse.json", "application/json"))
            .build();
   }

   public Payload payloadFromResourceWithContentType(String resource, String contentType) {
      try {
         return payloadFromStringWithContentType(
               Strings2.toStringAndClose(getClass().getResourceAsStream(resource)), contentType);
      }
      catch (IOException e) {
         throw Throwables.propagate(e);
      }

   }
}
