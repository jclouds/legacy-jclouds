/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.cloudstack.features;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.cloudstack.options.ListZonesOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValueInSet;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ZoneAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ZoneAsyncClientTest")
public class ZoneAsyncClientTest extends BaseCloudStackAsyncClientTest<ZoneAsyncClient> {
   public void testListZones() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ZoneAsyncClient.class.getMethod("listZones", ListZonesOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listZones HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(
               httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listZones&apiKey=apiKey&signature=hNz838u4Z1ofz9vRaqYo9GDv1Io%3D HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListZonesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ZoneAsyncClient.class.getMethod("listZones", ListZonesOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListZonesOptions.Builder.available(true).domainId(
               "domainId").id("id"));

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listZones&available=true&domainid=domainId&id=id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetZone() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ZoneAsyncClient.class.getMethod("getZone", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "id");

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listZones&id=id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValueInSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ZoneAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ZoneAsyncClient>>() {
      };
   }
}
