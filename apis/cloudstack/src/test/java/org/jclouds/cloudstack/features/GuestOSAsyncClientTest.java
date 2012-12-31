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

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.functions.ParseIdToNameEntryFromHttpResponse;
import org.jclouds.cloudstack.functions.ParseIdToNameFromHttpResponse;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.ListOSTypesOptions;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.testng.annotations.Test;

import com.google.common.base.Functions;

/**
 * Tests behavior of {@code GuestOSAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "GuestOSAsyncClientTest")
public class GuestOSAsyncClientTest extends BaseCloudStackAsyncClientTest<GuestOSAsyncClient> {

   public void testGetOSCategory() throws SecurityException, NoSuchMethodException, IOException {
      Method method = GuestOSAsyncClient.class.getMethod("getOSCategory", String.class);
      HttpRequest httpRequest = processor.createRequest(method, 11l);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listOsCategories&listAll=true&id=11 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseIdToNameEntryFromHttpResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListOSCategories() throws SecurityException, NoSuchMethodException, IOException {
      Method method = GuestOSAsyncClient.class.getMethod("listOSCategories");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listOsCategories&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseIdToNameFromHttpResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetOSType() throws SecurityException, NoSuchMethodException, IOException {
      Method method = GuestOSAsyncClient.class.getMethod("getOSType", String.class);
      HttpRequest httpRequest = processor.createRequest(method, 11l);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listOsTypes&listAll=true&id=11 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest,
            Functions.compose(IdentityFunction.INSTANCE, IdentityFunction.INSTANCE).getClass());
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListOSTypes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = GuestOSAsyncClient.class.getMethod("listOSTypes", ListOSTypesOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listOsTypes&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListOSTypesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = GuestOSAsyncClient.class.getMethod("listOSTypes", ListOSTypesOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListOSTypesOptions.Builder.OSCategoryId("11"));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listOsTypes&listAll=true&oscategoryid=11 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
}
