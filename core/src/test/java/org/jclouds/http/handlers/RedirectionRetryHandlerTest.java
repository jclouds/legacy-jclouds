/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.http.handlers;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.BaseRestClientTest.MockModule;
import org.jclouds.rest.config.RestModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Guice;

/**
 * Tests behavior of {@code RedirectionRetryHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class RedirectionRetryHandlerTest {

   @Test
   public void test302DoesNotRetry() {

      HttpCommand command = createMock(HttpCommand.class);
      HttpResponse response = new HttpResponse(302, "HTTP/1.1 302 Found", null);

      expect(command.incrementRedirectCount()).andReturn(0);

      replay(command);

      RedirectionRetryHandler retry = Guice.createInjector(new MockModule(), new RestModule()).getInstance(
            RedirectionRetryHandler.class);

      assert !retry.shouldRetryRequest(command, response);

      verify(command);

   }

   @Test
   public void test302DoesNotRetryAfterLimit() {

      HttpCommand command = createMock(HttpCommand.class);
      HttpResponse response = new HttpResponse(302, "HTTP/1.1 302 Found", null, ImmutableMultimap.of(
            HttpHeaders.LOCATION, "/api/v0.8b-ext2.5/Error.aspx?aspxerrorpath=/api/v0.8b-ext2.5/org.svc/1906645"));

      expect(command.incrementRedirectCount()).andReturn(5);

      replay(command);

      RedirectionRetryHandler retry = Guice.createInjector(new MockModule(), new RestModule()).getInstance(
            RedirectionRetryHandler.class);

      assert !retry.shouldRetryRequest(command, response);

      verify(command);

   }

   @Test
   public void test302WithPathOnlyHeader() {

      verifyRedirectRoutes(
            new HttpRequest("GET",
                  URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645")),
            new HttpResponse(302, "HTTP/1.1 302 Found", null, ImmutableMultimap.of(HttpHeaders.LOCATION,
                  "/api/v0.8b-ext2.5/Error.aspx?aspxerrorpath=/api/v0.8b-ext2.5/org.svc/1906645")),
            new HttpRequest(
                  "GET",
                  URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/Error.aspx?aspxerrorpath=/api/v0.8b-ext2.5/org.svc/1906645")));

   }

   @Test
   public void test302ToHttps() {

      verifyRedirectRoutes(
            new HttpRequest("GET",
                  URI.create("http://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645")),
            new HttpResponse(302, "HTTP/1.1 302 Found", null, ImmutableMultimap.of(HttpHeaders.LOCATION,
                  "https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645")),//
            new HttpRequest("GET", URI
                  .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645")));

   }

   @Test
   public void test302ToDifferentPort() {

      verifyRedirectRoutes(
            new HttpRequest("GET",
                  URI.create("http://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645")),
            new HttpResponse(302, "HTTP/1.1 302 Found", null, ImmutableMultimap.of(HttpHeaders.LOCATION,
                  "http://services.enterprisecloud.terremark.com:3030/api/v0.8b-ext2.5/org/1906645")),//
            new HttpRequest("GET", URI
                  .create("http://services.enterprisecloud.terremark.com:3030/api/v0.8b-ext2.5/org/1906645")));

   }

   @Test
   public void test302WithHeader() {

      verifyRedirectRoutes(
            new HttpRequest("GET",
                  URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645")),
            new HttpResponse(302, "HTTP/1.1 302 Found", null, ImmutableMultimap.of(HttpHeaders.LOCATION,
                  "https://services1.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645")), new HttpRequest(
                  "GET", URI.create("https://services1.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645")));

   }

   @Test
   public void test302WithHeaderReplacesHostHeader() {

      verifyRedirectRoutes(
            new HttpRequest("GET",
                  URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645"),
                  ImmutableMultimap.of(HttpHeaders.HOST, "services.enterprisecloud.terremark.com")),
            new HttpResponse(302, "HTTP/1.1 302 Found", null, ImmutableMultimap.of(HttpHeaders.LOCATION,
                  "https://services1.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645")),//
            new HttpRequest("GET", URI
                  .create("https://services1.enterprisecloud.terremark.com/api/v0.8b-ext2.5/org/1906645"),
                  ImmutableMultimap.of(HttpHeaders.HOST, "services1.enterprisecloud.terremark.com")));

   }

   protected void verifyRedirectRoutes(HttpRequest request, HttpResponse response, HttpRequest expected) {
      HttpCommand command = createMock(HttpCommand.class);

      expect(command.incrementRedirectCount()).andReturn(0);
      expect(command.getCurrentRequest()).andReturn(request);
      command.setCurrentRequest(expected);

      replay(command);

      RedirectionRetryHandler retry = Guice.createInjector(new MockModule(), new RestModule()).getInstance(
            RedirectionRetryHandler.class);

      assert retry.shouldRetryRequest(command, response);
      verify(command);
   }
}
