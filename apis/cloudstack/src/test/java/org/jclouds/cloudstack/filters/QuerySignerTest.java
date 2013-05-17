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
package org.jclouds.cloudstack.filters;

import static org.testng.Assert.assertEquals;

import org.jclouds.ContextBuilder;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.rest.internal.BaseRestApiTest.MockModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of {@code QuerySigner}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "QuerySignerTest")
public class QuerySignerTest {
   public static final Injector INJECTOR = ContextBuilder
         .newBuilder(
               AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(IntegrationTestClient.class, IntegrationTestAsyncClient.class,
                     "http://localhost:8080/client/api"))
         .credentials("apiKey", "secretKey")
         .apiVersion("2.2")
         .modules(ImmutableList.<Module> of(new MockModule(), new NullLoggingModule())).buildInjector();


   @Test
   void testCreateStringToSign() {
      QuerySigner filter = INJECTOR.getInstance(QuerySigner.class);

      assertEquals(
            filter.createStringToSign(HttpRequest.builder().method("GET")
                  .endpoint("http://localhost:8080/client/api?command=listZones").build()),
            "apikey=apikey&command=listzones");
   }

   @Test
   void testCreateStringToSignWithBrackets() {
      // This test asserts that key *names* are not URL-encoded - only values
      // should be encoded, according to "CloudStack API Developer’s Guide".
      QuerySigner filter = INJECTOR.getInstance(QuerySigner.class);

      assertEquals(
            filter.createStringToSign(HttpRequest.builder().method("GET")
                  .endpoint("http://localhost:8080/client/api?command=deployVirtualMachine&iptonetworklist[0].ip=127.0.0.1&iptonetworklist[0].networkid=1").build()),
            "apikey=apikey&command=deployvirtualmachine&iptonetworklist[0].ip=127.0.0.1&iptonetworklist[0].networkid=1");
   }

   @Test
   void testFilter() {
      QuerySigner filter = INJECTOR.getInstance(QuerySigner.class);

      assertEquals(
               filter.filter(
                        HttpRequest.builder().method("GET")
                                 .endpoint("http://localhost:8080/client/api?command=listZones").build())
                        .getRequestLine(),
               "GET http://localhost:8080/client/api?command=listZones&apiKey=apiKey&signature=2UG8AcnMaozL3BINdjgkJ%2BRzjEY%3D HTTP/1.1");
   }

   @Test
   void testFilterTwice() {
      QuerySigner filter = INJECTOR.getInstance(QuerySigner.class);
      HttpRequest request = HttpRequest.builder().method("GET")
               .endpoint("http://localhost:8080/client/api?command=listZones").build();
      for (int i = 0; i < 2; i++) {
         request = filter.filter(request);
         assertEquals(
               request.getRequestLine(),
               "GET http://localhost:8080/client/api?command=listZones&apiKey=apiKey&signature=2UG8AcnMaozL3BINdjgkJ%2BRzjEY%3D HTTP/1.1");
      }
   }
}
