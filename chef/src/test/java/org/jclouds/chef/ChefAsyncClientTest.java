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
 *http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Set;

import org.jclouds.chef.config.ChefRestClientModule;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.filters.SignedHeaderAuth;
import org.jclouds.chef.filters.SignedHeaderAuthTest;
import org.jclouds.chef.functions.ParseKeyFromJson;
import org.jclouds.chef.functions.ParseKeySetFromJson;
import org.jclouds.date.TimeStamp;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Bytes;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code ChefAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "chef.ChefAsyncClientTest")
public class ChefAsyncClientTest extends RestClientTest<ChefAsyncClient> {

   public void testCommitSandbox() throws SecurityException, NoSuchMethodException, IOException {

      Method method = ChefAsyncClient.class.getMethod("commitSandbox", String.class, boolean.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method,
            "0189e76ccc476701d6b374e5a1a27347", true);
      assertRequestLineEquals(httpRequest,
            "PUT http://localhost:4000/sandboxes/0189e76ccc476701d6b374e5a1a27347 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.6\n");
      assertPayloadEquals(httpRequest, "{\"is_completed\":\"true\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testGetUploadSandboxForChecksums() throws SecurityException, NoSuchMethodException, IOException {
      EncryptionService encryptionService = injector.getInstance(EncryptionService.class);
      Method method = ChefAsyncClient.class.getMethod("getUploadSandboxForChecksums", Set.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, ImmutableSet.of(Bytes
            .asList(encryptionService.fromHex("0189e76ccc476701d6b374e5a1a27347")), Bytes.asList(encryptionService
            .fromHex("0c5ecd7788cf4f6c7de2a57193897a6c")), Bytes.asList(encryptionService
            .fromHex("1dda05ed139664f1f89b9dec482b77c0"))));
      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/sandboxes HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.6\n");
      assertPayloadEquals(
            httpRequest,
            "{\"checksums\":{\"0189e76ccc476701d6b374e5a1a27347\":null,\"0c5ecd7788cf4f6c7de2a57193897a6c\":null,\"1dda05ed139664f1f89b9dec482b77c0\":null}}",
            "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testGetCookbook() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("getCookbook", String.class, String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "cookbook", "1.0.0");
      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/cookbooks/cookbook/1.0.0 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.6\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteCookbook() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("deleteCookbook", String.class, String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "cookbook", "1.0.0");
      assertRequestLineEquals(httpRequest, "DELETE http://localhost:4000/cookbooks/cookbook/1.0.0 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.6\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testUpdateCookbook() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("updateCookbook", String.class, String.class,
            CookbookVersion.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "cookbook", "1.0.1",
            new CookbookVersion("cookbook", "1.0.1"));

      assertRequestLineEquals(httpRequest, "PUT http://localhost:4000/cookbooks/cookbook/1.0.1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.6\n");
      assertPayloadEquals(
            httpRequest,
            "{\"name\":\"cookbook-1.0.1\",\"definitions\":[],\"attributes\":[],\"files\":[],\"metadata\":{\"suggestions\":{},\"dependencies\":{},\"conflicting\":{},\"providing\":{},\"platforms\":{},\"recipes\":{},\"replacing\":{},\"groupings\":{},\"attributes\":{},\"recommendations\":{}},\"providers\":[],\"cookbook_name\":\"cookbook\",\"resources\":[],\"templates\":[],\"libraries\":[],\"version\":\"1.0.1\",\"recipes\":[],\"root_files\":[],\"json_class\":\"Chef::CookbookVersion\",\"chef_type\":\"cookbook_version\"}",
            "application/json", false);
      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListCookbooks() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("listCookbooks");
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/cookbooks HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.6\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testClientExists() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("clientExists", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "client");
      assertRequestLineEquals(httpRequest, "HEAD http://localhost:4000/clients/client HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.6\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteClient() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("deleteClient", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "client");
      assertRequestLineEquals(httpRequest, "DELETE http://localhost:4000/clients/client HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.6\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReturnStringIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGenerateKeyForClient() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("generateKeyForClient", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "client");
      assertRequestLineEquals(httpRequest, "PUT http://localhost:4000/clients/client HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.6\n");
      assertPayloadEquals(httpRequest, "{\"clientname\":\"client\", \"private_key\": true}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeyFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateClient() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("createClient", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "client");

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/clients HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.6\n");
      assertPayloadEquals(httpRequest, "{\"clientname\":\"client\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeyFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListClients() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("listClients");
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/clients HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.6\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SignedHeaderAuth.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ChefAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ChefAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new TestChefRestClientModule();
   }

   @RequiresHttp
   @ConfiguresRestClient
   static class TestChefRestClientModule extends ChefRestClientModule {
      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "timestamp";
      }

   }

   @Override
   public ContextSpec<ChefClient, ChefAsyncClient> createContextSpec() {
      return new RestContextFactory().createContextSpec("chef", "user", SignedHeaderAuthTest.PRIVATE_KEY,
            new Properties());
   }
}
