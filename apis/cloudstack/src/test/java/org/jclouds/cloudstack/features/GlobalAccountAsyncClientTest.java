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
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.CreateAccountOptions;
import org.jclouds.cloudstack.options.UpdateAccountOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code GlobalAccountAsyncClient}
 *
 * @author Adrian
 */
@Test(groups = "unit", testName = "GlobalAccountAsyncClientTest")
public class GlobalAccountAsyncClientTest extends BaseCloudStackAsyncClientTest<GlobalAccountAsyncClient> {

   HttpRequest create = HttpRequest.builder().method("GET")
                                   .endpoint("http://localhost:8080/client/api")
                                   .addQueryParam("response", "json")
                                   .addQueryParam("command", "createAccount")
                                   .addQueryParam("username", "user")
                                   .addQueryParam("accounttype", "0")
                                   .addQueryParam("email", "email@example.com")
                                   .addQueryParam("firstname", "FirstName")
                                   .addQueryParam("lastname", "LastName")
                                   .addQueryParam("password", "hashed-password")
                                   .build();

   public void testCreateAccount() throws Exception {
      Invokable<?, ?> method = method(GlobalAccountAsyncClient.class, "createAccount", String.class, Account.Type.class,
         String.class, String.class, String.class, String.class, CreateAccountOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("user", Account.Type.USER, "email@example.com",
         "FirstName", "LastName", "hashed-password"));

      assertRequestLineEquals(httpRequest, create.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   HttpRequest update = HttpRequest.builder().method("GET")
                                   .endpoint("http://localhost:8080/client/api")
                                   .addQueryParam("response", "json")
                                   .addQueryParam("command", "updateAccount")
                                   .addQueryParam("account", "account")
                                   .addQueryParam("domainid", "42")
                                   .addQueryParam("newname", "new-account-name")
                                   .build();

   public void testUpdateAccount() throws Exception {
      Invokable<?, ?> method = method(GlobalAccountAsyncClient.class, "updateAccount", String.class, String.class,
         String.class, UpdateAccountOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("account", 42L, "new-account-name"));

      assertRequestLineEquals(httpRequest, update.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testDeleteAccount() throws Exception {
      Invokable<?, ?> method = method(GlobalAccountAsyncClient.class, "deleteAccount", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(42L));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=deleteAccount&id=42 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }
}
