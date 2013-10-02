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
package org.jclouds.http;

import static com.google.common.base.Throwables.propagate;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLException;

import org.jclouds.providers.JcloudsTestBlobStoreProviderMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.BaseRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/**
 * 
 * Allows us to test a client via its side effects.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "IntegrationTestClientExpectTest")
// only needed as IntegrationTestClient is not registered in rest.properties
public class IntegrationTestClientExpectTest extends BaseRestClientExpectTest<IntegrationTestClient> {
   
   public void testRetryOnSSLExceptionClose() {
      // keeps track of request count
      final AtomicInteger counter = new AtomicInteger(0);

      IntegrationTestClient client = createClient(new Function<HttpRequest, HttpResponse>() {
         @Override
         public HttpResponse apply(HttpRequest input) {
            // on first request, throw an SSL close_notify exception
            if (counter.getAndIncrement() == 0)
               throw propagate(new SSLException("Received close_notify during handshake"));
            
            // on other requests, just validate and return 200
            assertEquals(renderRequest(input), renderRequest(HttpRequest.builder().method("HEAD").endpoint(
                     URI.create("http://mock/objects/rabbit")).build()));
            return HttpResponse.builder().statusCode(200).build();
         }
      });

      // try three times, first should fail quietly due to retry logic
      for (int i = 0; i < 3; i++)
         assertEquals(client.exists("rabbit"), true);

      // should be an extra request relating to the retry
      assertEquals(counter.get(), 4);

   }
   
   public void testWhenResponseIs2xxExistsReturnsTrue() {

      IntegrationTestClient client = requestSendsResponse(HttpRequest.builder().method("HEAD").endpoint(
               URI.create("http://mock/objects/rabbit")).build(), HttpResponse.builder().statusCode(200).build());

      assertEquals(client.exists("rabbit"), true);

   }

   public void testWhenResponseIs404ExistsReturnsFalse() {

      IntegrationTestClient client = requestSendsResponse(HttpRequest.builder().method("HEAD").endpoint(
               URI.create("http://mock/objects/rabbit")).build(), HttpResponse.builder().statusCode(404).build());

      assertEquals(client.exists("rabbit"), false);

   }

   @Override
   public ProviderMetadata createProviderMetadata() {
      return new JcloudsTestBlobStoreProviderMetadata();
   }
}
