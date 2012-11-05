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
package org.jclouds.cloudstack.internal;

import static org.jclouds.crypto.CryptoStreams.md5Hex;
import static org.jclouds.util.Strings2.urlEncode;

import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

import com.google.common.base.Function;
import com.google.inject.Module;

/**
 * Base class for writing CloudStack Rest Client Expect tests
 * 
 * @author Andrei Savu
 */
public abstract class BaseCloudStackExpectTest<S> extends BaseRestClientExpectTest<S> {

   public BaseCloudStackExpectTest() {
      provider = "cloudstack";
   }
   
   @Override
   protected ApiMetadata createApiMetadata() {
      return new CloudStackApiMetadata();
   }

   @Override
   public S createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return (S) clientFrom(createInjector(fn, module, props).getInstance(CloudStackContext.class));
   }

   protected abstract S clientFrom(CloudStackContext context);

   protected final HttpRequest login = HttpRequest.builder().method("GET")
      .endpoint("http://localhost:8080/client/api")
      .addQueryParam("response", "json")
      .addQueryParam("command", "login")
      .addQueryParam("username", "identity")
      .addQueryParam("password", md5Hex("credential"))
      .addQueryParam("domain", "")
      .addHeader("Accept", "application/json")
      .build();

   protected final String jSessionId = "90DD65D13AEAA590ECCA312D150B9F6D";
   protected final String sessionKey = "uYT4/MNiglgAKiZRQkvV8QP8gn0=";
   
   protected final HttpResponse loginResponse = HttpResponse.builder().statusCode(200)
      .addHeader("Set-Cookie", "JSESSIONID=" + jSessionId + "; Path=/client")
      .payload(payloadFromResource("/loginresponse.json"))
      .build();

   protected final HttpRequest logout = HttpRequest.builder().method("GET")
      .endpoint("http://localhost:8080/client/api")
      .addQueryParam("response", "json")
      .addQueryParam("command", "logout")
      .addQueryParam("sessionkey", urlEncode(sessionKey))
      .build();
   
   protected final HttpResponse logoutResponse = HttpResponse.builder().statusCode(200).build();

}
