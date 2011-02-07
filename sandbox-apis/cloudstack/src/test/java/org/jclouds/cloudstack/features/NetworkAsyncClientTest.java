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

import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.options.ListNetworksOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValueInSet;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code NetworkAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "NetworkAsyncClientTest")
public class NetworkAsyncClientTest extends BaseCloudStackAsyncClientTest<NetworkAsyncClient> {
   public void testListNetworks() throws SecurityException, NoSuchMethodException, IOException {
      Method method = NetworkAsyncClient.class.getMethod("listNetworks", ListNetworksOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listNetworks HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListNetworksOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = NetworkAsyncClient.class.getMethod("listNetworks", ListNetworksOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListNetworksOptions.Builder.type(NetworkType.ADVANCED).domainId(
               "domainId").id("id"));

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listNetworks&type=Advanced&domainid=domainId&id=id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetNetwork() throws SecurityException, NoSuchMethodException, IOException {
      Method method = NetworkAsyncClient.class.getMethod("getNetwork", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "id");

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listNetworks&id=id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValueInSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<NetworkAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<NetworkAsyncClient>>() {
      };
   }
}
