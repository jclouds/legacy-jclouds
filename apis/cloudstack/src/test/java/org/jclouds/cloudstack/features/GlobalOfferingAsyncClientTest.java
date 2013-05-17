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
package org.jclouds.cloudstack.features;
import static org.jclouds.reflect.Reflection2.method;

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
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

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
      Invokable<?, ?> method = method(GlobalOfferingAsyncClient.class, "createServiceOffering",
         String.class, String.class, int.class, int.class, int.class, CreateServiceOfferingOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("name", "displayText", 1, 2, 3));

      assertRequestLineEquals(httpRequest, createServiceOffering.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testUpdateServiceOffering() throws Exception {
      Invokable<?, ?> method = method(GlobalOfferingAsyncClient.class, "updateServiceOffering",
         String.class, UpdateServiceOfferingOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1L));

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
      Invokable<?, ?> method = method(GlobalOfferingAsyncClient.class, "deleteServiceOffering", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1L));

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
      Invokable<?, ?> method = method(GlobalOfferingAsyncClient.class, "createDiskOffering",
         String.class, String.class, CreateDiskOfferingOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("name", "displayText"));

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
      Invokable<?, ?> method = method(GlobalOfferingAsyncClient.class, "updateDiskOffering",
         String.class, UpdateDiskOfferingOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1L));

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
      Invokable<?, ?> method = method(GlobalOfferingAsyncClient.class, "deleteDiskOffering", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1L));

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
      Invokable<?, ?> method = method(GlobalOfferingAsyncClient.class, "updateNetworkOffering",
         String.class, UpdateNetworkOfferingOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1L));

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
