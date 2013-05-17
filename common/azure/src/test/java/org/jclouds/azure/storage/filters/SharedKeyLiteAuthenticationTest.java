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
package org.jclouds.azure.storage.filters;

import static org.jclouds.reflect.Reflection2.typeToken;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.ContextBuilder;
import org.jclouds.azure.storage.config.AzureStorageRestClientModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.AnonymousRestApiMetadata;
import org.jclouds.rest.internal.BaseRestApiTest.MockModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class SharedKeyLiteAuthenticationTest {

   private static final String ACCOUNT = "foo";
   private Injector injector;
   private SharedKeyLiteAuthentication filter;

   @DataProvider(parallel = true)
   public Object[][] dataProvider() {
      return new Object[][] {
            { HttpRequest.builder().method(HttpMethod.PUT).endpoint("http://" + ACCOUNT
                  + ".blob.core.windows.net/movies/MOV1.avi?comp=block&blockid=BlockId1&timeout=60").build() },
            { HttpRequest.builder().method(HttpMethod.PUT).endpoint("http://" + ACCOUNT
                  + ".blob.core.windows.net/movies/MOV1.avi?comp=blocklist&timeout=120").build() },
            { HttpRequest.builder().method(HttpMethod.GET).endpoint("http://" + ACCOUNT + ".blob.core.windows.net/movies/MOV1.avi").build() } };
   }

   /**
    * NOTE this test is dependent on how frequently the timestamp updates. At
    * the time of writing, this was once per second. If this timestamp update
    * interval is increased, it could make this test appear to hang for a long
    * time.
    */
   @Test(threadPoolSize = 3, dataProvider = "dataProvider", timeOut = 3000)
   void testIdempotent(HttpRequest request) {
      request = filter.filter(request);
      String signature = request.getFirstHeaderOrNull(HttpHeaders.AUTHORIZATION);
      String date = request.getFirstHeaderOrNull(HttpHeaders.DATE);
      int iterations = 1;
      while (request.getFirstHeaderOrNull(HttpHeaders.DATE).equals(date)) {
         date = request.getFirstHeaderOrNull(HttpHeaders.DATE);
         iterations++;
         assertEquals(signature, request.getFirstHeaderOrNull(HttpHeaders.AUTHORIZATION));
         request = filter.filter(request);
      }
      System.out.printf("%s: %d iterations before the timestamp updated %n", Thread.currentThread().getName(),
            iterations);
   }

   @Test
   void testAclQueryStringRoot() {
      URI host = URI.create("http://" + ACCOUNT + ".blob.core.windows.net/?comp=list");
      HttpRequest request = HttpRequest.builder().method(HttpMethod.GET).endpoint(host).build();
      StringBuilder builder = new StringBuilder();
      filter.appendUriPath(request, builder);
      assertEquals(builder.toString(), "/?comp=list");
   }

   @Test
   void testAclQueryStringResTypeNotSignificant() {
      URI host = URI.create("http://" + ACCOUNT + ".blob.core.windows.net/mycontainer?restype=container");
      HttpRequest request = HttpRequest.builder().method(HttpMethod.GET).endpoint(host).build();
      StringBuilder builder = new StringBuilder();
      filter.appendUriPath(request, builder);
      assertEquals(builder.toString(), "/mycontainer");
   }

   @Test
   void testAclQueryStringComp() {
      URI host = URI.create("http://" + ACCOUNT + ".blob.core.windows.net/mycontainer?comp=list");
      HttpRequest request = HttpRequest.builder().method(HttpMethod.GET).endpoint(host).build();
      StringBuilder builder = new StringBuilder();
      filter.appendUriPath(request, builder);
      assertEquals(builder.toString(), "/mycontainer?comp=list");
   }

   @Test
   void testAclQueryStringRelativeWithExtraJunk() {
      URI host = URI.create("http://" + ACCOUNT
            + ".blob.core.windows.net/mycontainer?comp=list&marker=marker&maxresults=1&prefix=prefix");
      HttpRequest request = HttpRequest.builder().method(HttpMethod.GET).endpoint(host).build();
      StringBuilder builder = new StringBuilder();
      filter.appendUriPath(request, builder);
      assertEquals(builder.toString(), "/mycontainer?comp=list");
   }

   /**
    * before class, as we need to ensure that the filter is threadsafe.
    * 
    * @throws IOException
    * 
    */
   @BeforeClass
   protected void createFilter() throws IOException {
      injector = ContextBuilder
            .newBuilder(
                  AnonymousRestApiMetadata
                        .forClientMappedToAsyncClient(IntegrationTestClient.class, IntegrationTestAsyncClient.class)
                        .toBuilder().build())
            .endpoint("https://${jclouds.identity}.blob.core.windows.net")
            .credentials(ACCOUNT, "credential")
            .modules(
                  ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule(),
                        new AzureStorageRestClientModule<IntegrationTestClient, IntegrationTestAsyncClient>(
                              typeToken(IntegrationTestClient.class), typeToken(IntegrationTestAsyncClient.class))))
            .buildInjector();
      filter = injector.getInstance(SharedKeyLiteAuthentication.class);
   }
}
