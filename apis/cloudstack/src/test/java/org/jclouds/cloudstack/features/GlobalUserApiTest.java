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
package org.jclouds.cloudstack.features;

import static org.jclouds.reflect.Reflection2.method;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.internal.BaseCloudStackApiTest;
import org.jclouds.cloudstack.options.CreateUserOptions;
import org.jclouds.cloudstack.options.UpdateUserOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code GlobalUserApi}
 */
@Test(groups = "unit", testName = "GlobalUserApiTest")
public class GlobalUserApiTest extends BaseCloudStackApiTest<GlobalUserApi> {

   HttpRequest createUser = HttpRequest.builder().method("GET")
                                       .endpoint("http://localhost:8080/client/api")
                                       .addQueryParam("response", "json")
                                       .addQueryParam("command", "createUser")
                                       .addQueryParam("username", "user")
                                       .addQueryParam("account", "account")
                                       .addQueryParam("email", "email%40example.com")
                                       .addQueryParam("password", "hashed-password")
                                       .addQueryParam("firstname", "FirstName")
                                       .addQueryParam("lastname", "LastName").build();

   public void testCreateAccount() throws Exception {
      Invokable<?, ?> method = method(GlobalUserApi.class, "createUser", String.class, String.class,
         String.class, String.class, String.class, String.class, CreateUserOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("user", "account", "email@example.com",
         "hashed-password", "FirstName", "LastName"));

      assertRequestLineEquals(httpRequest, createUser.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testUpdateUser() throws Exception {
      Invokable<?, ?> method = method(GlobalUserApi.class, "updateUser", String.class, UpdateUserOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(42L));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateUser&id=42 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testDeleteUser() throws Exception {
      Invokable<?, ?> method = method(GlobalUserApi.class, "deleteUser", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(42L));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=deleteUser&id=42 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }
}
