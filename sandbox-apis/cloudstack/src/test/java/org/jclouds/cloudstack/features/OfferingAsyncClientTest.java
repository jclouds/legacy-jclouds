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

import org.jclouds.cloudstack.options.ListDiskOfferingsOptions;
import org.jclouds.cloudstack.options.ListNetworkOfferingsOptions;
import org.jclouds.cloudstack.options.ListServiceOfferingsOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValueInSet;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code OfferingAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ServiceOfferingAsyncClientTest")
public class OfferingAsyncClientTest extends BaseCloudStackAsyncClientTest<OfferingAsyncClient> {
   public void testListDiskOfferings() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OfferingAsyncClient.class.getMethod("listDiskOfferings", ListDiskOfferingsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listDiskOfferings HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListDiskOfferingsOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OfferingAsyncClient.class.getMethod("listDiskOfferings", ListDiskOfferingsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListDiskOfferingsOptions.Builder.domainId("domainId")
               .id("id"));

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listDiskOfferings&domainid=domainId&id=id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetDiskOffering() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OfferingAsyncClient.class.getMethod("getDiskOffering", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "id");

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listDiskOfferings&id=id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValueInSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListNetworkOfferings() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OfferingAsyncClient.class.getMethod("listNetworkOfferings", ListNetworkOfferingsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listNetworkOfferings HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListNetworkOfferingsOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OfferingAsyncClient.class.getMethod("listNetworkOfferings", ListNetworkOfferingsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListNetworkOfferingsOptions.Builder.availability(
               "Default").isShared(true).id("id"));

      assertRequestLineEquals(
               httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listNetworkOfferings&availability=Default&isshared=true&id=id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetNetworkOffering() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OfferingAsyncClient.class.getMethod("getNetworkOffering", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "id");

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listNetworkOfferings&id=id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValueInSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListServiceOfferings() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OfferingAsyncClient.class.getMethod("listServiceOfferings", ListServiceOfferingsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listServiceOfferings HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListServiceOfferingsOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OfferingAsyncClient.class.getMethod("listServiceOfferings", ListServiceOfferingsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListServiceOfferingsOptions.Builder.virtualMachineId(
               "vmId").domainId("domainId").id("id"));

      assertRequestLineEquals(
               httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listServiceOfferings&virtualmachineid=vmId&domainid=domainId&id=id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetServiceOffering() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OfferingAsyncClient.class.getMethod("getServiceOffering", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "id");

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listServiceOfferings&id=id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValueInSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<OfferingAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<OfferingAsyncClient>>() {
      };
   }
}
