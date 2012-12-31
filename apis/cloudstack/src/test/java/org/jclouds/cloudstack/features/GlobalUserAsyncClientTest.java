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
package org.jclouds.cloudstack.features;

import java.lang.reflect.Method;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.CreateUserOptions;
import org.jclouds.cloudstack.options.UpdateUserOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code GlobalUserAsyncClient}
 */
@Test(groups = "unit", testName = "GlobalUserAsyncClientTest")
public class GlobalUserAsyncClientTest extends BaseCloudStackAsyncClientTest<GlobalUserAsyncClient> {

   public void testCreateAccount() throws Exception {
      Method method = GlobalUserAsyncClient.class.getMethod("createUser", String.class, String.class,
         String.class, String.class, String.class, String.class, CreateUserOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "user", "account", "email@example.com",
         "hashed-password", "FirstName", "LastName");

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=createUser&lastname=LastName&" +
            "username=user&email=email%40example.com&account=account&password=hashed-password&firstname=FirstName HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testUpdateUser() throws Exception {
      Method method = GlobalUserAsyncClient.class.getMethod("updateUser", String.class, UpdateUserOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 42L);

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
      Method method = GlobalUserAsyncClient.class.getMethod("deleteUser", String.class);
      HttpRequest httpRequest = processor.createRequest(method, 42L);

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
