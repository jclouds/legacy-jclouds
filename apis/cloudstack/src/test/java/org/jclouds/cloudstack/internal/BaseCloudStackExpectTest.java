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
package org.jclouds.cloudstack.internal;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.hash.Hashing.md5;
import static com.google.common.io.BaseEncoding.base16;
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
      return clientFrom(createInjector(fn, module, props).getInstance(CloudStackContext.class));
   }

   protected abstract S clientFrom(CloudStackContext context);

   protected final HttpRequest login = HttpRequest.builder().method("GET")
      .endpoint("http://localhost:8080/client/api")
      .addQueryParam("response", "json")
      .addQueryParam("command", "login")
      .addQueryParam("username", "identity")
      .addQueryParam("domain", "")
      .addQueryParam("password", base16().lowerCase().encode(md5().hashString("credential", UTF_8).asBytes()))
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
