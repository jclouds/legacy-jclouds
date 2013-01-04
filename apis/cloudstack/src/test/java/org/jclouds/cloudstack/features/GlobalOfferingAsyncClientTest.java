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

import java.lang.reflect.Method;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.CreateDiskOfferingOptions;
import org.jclouds.cloudstack.options.CreateServiceOfferingOptions;
import org.jclouds.cloudstack.options.UpdateDiskOfferingOptions;
import org.jclouds.cloudstack.options.UpdateNetworkOfferingOptions;
import org.jclouds.cloudstack.options.UpdateServiceOfferingOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code GlobalOfferingAsyncClient}
 *
 * @author Andrei Savu
 */
@Test(groups = "unit", testName = "GlobalOfferingAsyncClientTest")
public class GlobalOfferingAsyncClientTest extends BaseCloudStackAsyncClientTest<GlobalOfferingAsyncClient> {

   HttpRequest createServiceOffering = HttpRequest.builder().method("GET")
                                                  .endpoint("http://localhost:8080/client/api")
                                                  .addQueryParam("response", "json")
                                                  .addQueryParam("command", "createServiceOffering")
                                                  .addQueryParam("name", "name")
                                                  .addQueryParam("displaytext", "displayText")
                                                  .addQueryParam("cpunumber", "1")
                                                  .addQueryParam("cpuspeed", "2")
                                                  .addQueryParam("memory", "3").build();

   public void testCreateServiceOffering() throws Exception {
      Method method = GlobalOfferingAsyncClient.class.getMethod("createServiceOffering",
         String.class, String.class, int.class, int.class, int.class, CreateServiceOfferingOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "name", "displayText", 1, 2, 3);

      assertRequestLineEquals(httpRequest, createServiceOffering.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testUpdateServiceOffering() throws Exception {
      Method method = GlobalOfferingAsyncClient.class.getMethod("updateServiceOffering",
         String.class, UpdateServiceOfferingOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 1L);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateServiceOffering&id=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testDeleteServiceOffering() throws Exception {
      Method method = GlobalOfferingAsyncClient.class.getMethod("deleteServiceOffering", String.class);
      HttpRequest httpRequest = processor.createRequest(method, 1L);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=deleteServiceOffering&id=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testCreateDiskOffering() throws Exception {
      Method method = GlobalOfferingAsyncClient.class.getMethod("createDiskOffering",
         String.class, String.class, CreateDiskOfferingOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "name", "displayText");

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=createDiskOffering&name=name&displaytext=displayText HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testUpdateDiskOffering() throws Exception {
      Method method = GlobalOfferingAsyncClient.class.getMethod("updateDiskOffering",
         String.class, UpdateDiskOfferingOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 1L);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateDiskOffering&id=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testDeleteDiskOffering() throws Exception {
      Method method = GlobalOfferingAsyncClient.class.getMethod("deleteDiskOffering", String.class);
      HttpRequest httpRequest = processor.createRequest(method, 1L);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=deleteDiskOffering&id=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testUpdateNetworkOffering() throws Exception {
      Method method = GlobalOfferingAsyncClient.class.getMethod("updateNetworkOffering",
         String.class, UpdateNetworkOfferingOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 1L);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateNetworkOffering&id=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }
}
