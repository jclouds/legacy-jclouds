/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.chef;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.jclouds.chef.config.ChefRestClientModule;
import org.jclouds.chef.domain.Organization;
import org.jclouds.chef.domain.User;
import org.jclouds.chef.filters.SignedHeaderAuth;
import org.jclouds.chef.filters.SignedHeaderAuthTest;
import org.jclouds.chef.functions.ParseKeyFromJson;
import org.jclouds.chef.functions.ParseKeySetFromJson;
import org.jclouds.chef.functions.ParseOrganizationFromJson;
import org.jclouds.chef.functions.ParseUserFromJson;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code ChefAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "chef.ChefAsyncClientTest")
public class ChefAsyncClientTest extends RestClientTest<ChefAsyncClient> {
   public void testClientExistsInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("clientExistsInOrg", String.class,
               String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "org",
               "client");
      assertRequestLineEquals(httpRequest,
               "HEAD https://api.opscode.com/organizations/org/clients/client HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteClientInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("deleteClientInOrg", String.class,
               String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "org",
               "client");
      assertRequestLineEquals(httpRequest,
               "DELETE https://api.opscode.com/organizations/org/clients/client HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGenerateKeyForClientInOrg() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = ChefAsyncClient.class.getMethod("generateKeyForClientInOrg", String.class,
               String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "org",
               "client");
      assertRequestLineEquals(httpRequest,
               "PUT https://api.opscode.com/organizations/org/clients/client HTTP/1.1");
      assertHeadersEqual(httpRequest,
               "Accept: application/json\nContent-Length: 44\nContent-Type: application/json\n");
      assertPayloadEquals(httpRequest, "{\"clientname\":\"client\", \"private_key\": true}");

      assertResponseParserClassEquals(method, httpRequest, ParseKeyFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateClientInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("createClientInOrg", String.class,
               String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "org",
               "client");

      assertRequestLineEquals(httpRequest,
               "POST https://api.opscode.com/organizations/org/clients HTTP/1.1");
      assertHeadersEqual(httpRequest,
               "Accept: application/json\nContent-Length: 23\nContent-Type: application/json\n");
      assertPayloadEquals(httpRequest, "{\"clientname\":\"client\"}");

      assertResponseParserClassEquals(method, httpRequest, ParseKeyFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListClientsInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("listClientsInOrg", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "org");

      assertRequestLineEquals(httpRequest,
               "GET https://api.opscode.com/organizations/org/clients HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateUser() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("createUser", User.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, new User(
               "myuser"));

      assertRequestLineEquals(httpRequest, "POST https://api.opscode.com/users HTTP/1.1");
      assertHeadersEqual(httpRequest,
               "Accept: application/json\nContent-Length: 21\nContent-Type: application/json\n");
      assertPayloadEquals(httpRequest, "{\"username\":\"myuser\"}");

      // now make sure request filters apply by replaying
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "POST https://api.opscode.com/users HTTP/1.1");
      assertHeadersEqual(
               httpRequest,
               new StringBuilder("Accept: application/json")
                        .append("\n")
                        .append("Content-Length: 21")
                        .append("\n")
                        .append("Content-Type: application/json")
                        .append("\n")
                        .append(
                                 "X-Ops-Authorization-1: kfrkDpfgNU26k70R1vl1bEWk0Q0f9Fs/3kxOX7gHd7iNoJq03u7RrcrAOSgL")
                        .append("\n")
                        .append(
                                 "X-Ops-Authorization-2: ETj5JNeCk18BmFkHMAbCA9hXVo1T4rlHCpbuzAzFlFxUGAT4wj8UoO7V886X")
                        .append("\n")
                        .append(
                                 "X-Ops-Authorization-3: Kf8DvihP6ElthCNuu1xuhN0B4GEmWC9+ut7UMLe0L2T34VzkbCtuInGbf42/")
                        .append("\n")
                        .append(
                                 "X-Ops-Authorization-4: G7iu94/xFOT1gN9cex4pNyTnRCHzob4JVU1usxt/2g5grN2SyYwRS5+4MNLN")
                        .append("\n")
                        .append(
                                 "X-Ops-Authorization-5: WY/iLUPb/9dwtiIQsnUOXqDrs28zNswZulQW4AzYRd7MczJVKU4y4+4XRcB4")
                        .append("\n")
                        .append(
                                 "X-Ops-Authorization-6: 2+BFLT5o6P6G0D+eCu3zSuaqEJRucPJPaDGWdKIMag==")
                        .append("\n").append("X-Ops-Content-Hash: yLHOxvgIEtNw5UrZDxslOeMw1gw=")
                        .append("\n").append("X-Ops-Sign: version=1.0").append("\n").append(
                                 "X-Ops-Timestamp: timestamp").append("\n").append(
                                 "X-Ops-Userid: user").append("\n").toString());
      assertPayloadEquals(httpRequest, "{\"username\":\"myuser\"}");

      assertResponseParserClassEquals(method, httpRequest, ParseKeyFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testUpdateUser() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("updateUser", User.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, new User(
               "myuser"));

      assertRequestLineEquals(httpRequest, "PUT https://api.opscode.com/users/myuser HTTP/1.1");
      assertHeadersEqual(httpRequest,
               "Accept: application/json\nContent-Length: 21\nContent-Type: application/json\n");
      assertPayloadEquals(httpRequest, "{\"username\":\"myuser\"}");

      assertResponseParserClassEquals(method, httpRequest, ParseUserFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testGetUser() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("getUser", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "myuser");

      assertRequestLineEquals(httpRequest, "GET https://api.opscode.com/users/myuser HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseUserFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteUser() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("deleteUser", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "myuser");

      assertRequestLineEquals(httpRequest, "DELETE https://api.opscode.com/users/myuser HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseUserFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("createOrg", Organization.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method,
               new Organization("myorganization"));

      assertRequestLineEquals(httpRequest, "POST https://api.opscode.com/organizations HTTP/1.1");
      assertHeadersEqual(httpRequest,
               "Accept: application/json\nContent-Length: 25\nContent-Type: application/json\n");
      assertPayloadEquals(httpRequest, "{\"name\":\"myorganization\"}");

      assertResponseParserClassEquals(method, httpRequest, ParseKeyFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testUpdateOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("updateOrg", Organization.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method,
               new Organization("myorganization"));

      assertRequestLineEquals(httpRequest,
               "PUT https://api.opscode.com/organizations/myorganization HTTP/1.1");
      assertHeadersEqual(httpRequest,
               "Accept: application/json\nContent-Length: 25\nContent-Type: application/json\n");
      assertPayloadEquals(httpRequest, "{\"name\":\"myorganization\"}");

      assertResponseParserClassEquals(method, httpRequest, ParseOrganizationFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testGetOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("getOrg", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method,
               "myorganization");

      assertRequestLineEquals(httpRequest,
               "GET https://api.opscode.com/organizations/myorganization HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseOrganizationFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("deleteOrg", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method,
               "myorganization");

      assertRequestLineEquals(httpRequest,
               "DELETE https://api.opscode.com/organizations/myorganization HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseOrganizationFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<ChefAsyncClient> httpRequest) {
      assertEquals(httpRequest.getFilters().size(), 1);
      assertEquals(httpRequest.getFilters().get(0).getClass(), SignedHeaderAuth.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ChefAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ChefAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new ChefRestClientModule() {
         @Override
         protected void configure() {
            Jsr330.bindProperties(binder(), new ChefPropertiesBuilder(new Properties())
                     .withCredentials("user", SignedHeaderAuthTest.PRIVATE_KEY).build());
            install(new NullLoggingModule());
         }

         @Override
         protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
            return "timestamp";
         }
      };
   }
}
