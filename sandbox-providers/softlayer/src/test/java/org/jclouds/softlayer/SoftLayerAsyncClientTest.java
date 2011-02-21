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

package org.jclouds.softlayer;

import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code SoftLayerAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class SoftLayerAsyncClientTest extends RestClientTest<SoftLayerAsyncClient> {

   public void testListVirtualGuests() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SoftLayerAsyncClient.class.getMethod("listVirtualGuests");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET https://api.softlayer.com/rest/v3/SoftLayer_Account/VirtualGuests.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest,
               "GET https://api.softlayer.com/rest/v3/SoftLayer_Account/VirtualGuests.json HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest,
               "Accept: application/json\nAuthorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetVirtualGuest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SoftLayerAsyncClient.class.getMethod("getVirtualGuest", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 1234);

      assertRequestLineEquals(httpRequest,
               "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SoftLayerAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SoftLayerAsyncClient>>() {
      };
   }

   @Override
   public RestContextSpec<SoftLayerClient, SoftLayerAsyncClient> createContextSpec() {
      return contextSpec("softlayer", "https://api.softlayer.com/rest", "3", "", "identity", "credential",
               SoftLayerClient.class, SoftLayerAsyncClient.class);
   }
}
