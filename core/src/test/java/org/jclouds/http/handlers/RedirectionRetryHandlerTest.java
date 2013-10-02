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
package org.jclouds.http.handlers;

import static com.google.common.net.HttpHeaders.HOST;
import static com.google.common.net.HttpHeaders.LOCATION;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.jclouds.ContextBuilder;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.rest.internal.BaseRestApiTest.MockModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of {@code RedirectionRetryHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class RedirectionRetryHandlerTest {
   Injector injector = ContextBuilder.newBuilder(
            AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(IntegrationTestClient.class,
                     IntegrationTestAsyncClient.class, "http://localhost")).modules(
            ImmutableSet.<Module> of(new MockModule())).buildInjector();

   @Test
   public void test302DoesNotRetry() {

      HttpCommand command = createMock(HttpCommand.class);
      HttpResponse response = HttpResponse.builder()
                                          .statusCode(302)
                                          .message("HTTP/1.1 302 Found").build();

      expect(command.isReplayable()).andReturn(true);
      expect(command.incrementRedirectCount()).andReturn(0);

      replay(command);

      RedirectionRetryHandler retry = injector.getInstance(RedirectionRetryHandler.class);

      assert !retry.shouldRetryRequest(command, response);

      verify(command);

   }

   @Test
   public void test302DoesNotRetryAfterLimit() {

      HttpCommand command = createMock(HttpCommand.class);
      HttpResponse response = HttpResponse.builder()
                                          .statusCode(302)
                                          .message("HTTP/1.1 302 Found")
                                          .addHeader(LOCATION, "/api/v0.8b-ext2.5/Error.aspx?aspxerrorpath=/api/v0.8b-ext2.5/org.svc/1906645").build(); 

      expect(command.isReplayable()).andReturn(true);
      expect(command.incrementRedirectCount()).andReturn(6);

      replay(command);

      RedirectionRetryHandler retry = injector.getInstance(RedirectionRetryHandler.class);

      assert !retry.shouldRetryRequest(command, response);

      verify(command);

   }

   @Test
   public void test302WithPathOnlyHeader() {

      verifyRedirectRoutes(
               HttpRequest.builder()
                          .method("GET")
                          .endpoint("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645").build(),
               HttpResponse.builder()
                           .statusCode(302)
                           .message("HTTP/1.1 302 Found")
                           .addHeader(LOCATION, "/api/v0.8b-ext2.5/Error.aspx?aspxerrorpath=/api/v0.8b-ext2.5/org.svc/1906645").build(),
               HttpRequest.builder()
                          .method("GET")
                          .endpoint("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/Error.aspx?aspxerrorpath=/api/v0.8b-ext2.5/org.svc/1906645").build());
   }

   @Test
   public void test302ToHttps() {

      verifyRedirectRoutes(
               HttpRequest.builder()
                          .method("GET")
                          .endpoint("http://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645").build(),
               HttpResponse.builder()
                           .statusCode(302)
                           .message("HTTP/1.1 302 Found")
                           .addHeader(LOCATION, "https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645").build(),
               HttpRequest.builder()
                          .method("GET")
                          .endpoint("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645").build());
   }

   @Test
   public void test302ToDifferentPort() {
      verifyRedirectRoutes(
               HttpRequest.builder()
                          .method("GET")
                          .endpoint("http://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645").build(),
               HttpResponse.builder()
                           .statusCode(302)
                           .message("HTTP/1.1 302 Found")
                           .addHeader(LOCATION, "http://services.enterprisecloud.terremark.com:3030/api/v0.8b-ext2.5/org/1906645").build(),
               HttpRequest.builder()
                          .method("GET")
                          .endpoint("http://services.enterprisecloud.terremark.com:3030/api/v0.8b-ext2.5/org/1906645").build());
   }

   @Test
   public void test302WithHeader() {
      verifyRedirectRoutes(
               HttpRequest.builder()
                          .method("GET")
                          .endpoint("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645").build(),
               HttpResponse.builder()
                           .statusCode(302)
                           .message("HTTP/1.1 302 Found")
                           .addHeader(LOCATION, "https://services1.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645").build(),
               HttpRequest.builder()
                          .method("GET")
                          .endpoint("https://services1.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645").build());
   }

   @Test
   public void test302WithHeaderReplacesHostHeader() {
      verifyRedirectRoutes(
               HttpRequest.builder()
                          .method("GET")
                          .endpoint("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645")
                          .addHeader(HOST, "services.enterprisecloud.terremark.com").build(),
               HttpResponse.builder()
                           .statusCode(302)
                           .message("HTTP/1.1 302 Found")
                           .addHeader(LOCATION, "https://services1.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645").build(),
               HttpRequest.builder()
                          .method("GET")
                          .endpoint("https://services1.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645")
                          .addHeader(HOST, "services1.enterprisecloud.terremark.com").build());
   }

   protected void verifyRedirectRoutes(HttpRequest request, HttpResponse response, HttpRequest expected) {
      HttpCommand command = createMock(HttpCommand.class);

      expect(command.isReplayable()).andReturn(true);
      expect(command.incrementRedirectCount()).andReturn(0);
      expect(command.getCurrentRequest()).andReturn(request);
      command.setCurrentRequest(expected);

      replay(command);

      RedirectionRetryHandler retry = injector.getInstance(RedirectionRetryHandler.class);

      assert retry.shouldRetryRequest(command, response);
      verify(command);
   }
}
