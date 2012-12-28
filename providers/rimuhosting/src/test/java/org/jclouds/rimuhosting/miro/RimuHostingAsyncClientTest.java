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
package org.jclouds.rimuhosting.miro;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.rimuhosting.miro.binder.CreateServerOptions;
import org.jclouds.rimuhosting.miro.fallbacks.ParseRimuHostingException;
import org.jclouds.rimuhosting.miro.filters.RimuHostingAuthentication;
import org.jclouds.rimuhosting.miro.functions.ParseServersFromJsonResponse;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code RimuHostingAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "RimuHostingAsyncClientTest")
public class RimuHostingAsyncClientTest extends BaseAsyncClientTest<RimuHostingAsyncClient> {

   public void testCreateServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = RimuHostingAsyncClient.class.getMethod("createServer", String.class, String.class, String.class,
               CreateServerOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, "test.ivan.api.com",
               "lenny", "MIRO4B");

      assertRequestLineEquals(httpRequest, "POST https://api.rimuhosting.com/r/orders/new-vps HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nHost: api.rimuhosting.com\n");
      assertPayloadEquals(
               httpRequest,
               "{\"request\":{\"instantiation_options\":{\"distro\":\"lenny\",\"domain_name\":\"test.ivan.api.com\"},\"pricing_plan_code\":\"MIRO4B\",\"meta_data\":[]}}",
               "application/json", false);
      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, ParseRimuHostingException.class);

      checkFilters(httpRequest);

   }

   public void testGetServerList() throws SecurityException, NoSuchMethodException, IOException {
      Method method = RimuHostingAsyncClient.class.getMethod("getServerList");
      GeneratedHttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.rimuhosting.com/r/orders;include_inactive=N HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nHost: api.rimuhosting.com\n");
      assertPayloadEquals(httpRequest, null, null, false);
      assertResponseParserClassEquals(method, httpRequest, ParseServersFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, ParseRimuHostingException.class);

      checkFilters(httpRequest);

   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), RimuHostingAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<RimuHostingAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<RimuHostingAsyncClient>>() {
      };
   }

   @Override
   public ProviderMetadata createProviderMetadata() {
      return new RimuHostingProviderMetadata();
   }
}
