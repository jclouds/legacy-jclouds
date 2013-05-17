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

import java.io.IOException;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.CreateNetworkOptions;
import org.jclouds.cloudstack.options.ListNetworksOptions;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code NetworkAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "NetworkAsyncClientTest")
public class NetworkAsyncClientTest extends BaseCloudStackAsyncClientTest<NetworkAsyncClient> {
   public void testListNetworks() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(NetworkAsyncClient.class, "listNetworks", ListNetworksOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listNetworks&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListNetworksOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(NetworkAsyncClient.class, "listNetworks", ListNetworksOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(ListNetworksOptions.Builder.type(NetworkType.ADVANCED)
            .domainId("6").id("5")));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listNetworks&listAll=true&type=Advanced&domainid=6&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetNetwork() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(NetworkAsyncClient.class, "getNetwork", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("id"));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listNetworks&listAll=true&id=id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest,
            Functions.compose(IdentityFunction.INSTANCE, IdentityFunction.INSTANCE).getClass());
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   HttpRequest createNetwork = HttpRequest.builder().method("GET")
                                          .endpoint("http://localhost:8080/client/api")
                                          .addQueryParam("response", "json")
                                          .addQueryParam("command", "createNetwork")
                                          .addQueryParam("zoneid", "1")
                                          .addQueryParam("networkofferingid", "2")
                                          .addQueryParam("name", "named")
                                          .addQueryParam("displaytext", "lovely").build();

   public void testCreateNetworkInZone() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(NetworkAsyncClient.class, "createNetworkInZone", String.class, String.class, String.class,
            String.class, CreateNetworkOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1, 2, "named", "lovely"));

      assertRequestLineEquals(httpRequest, createNetwork.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   HttpRequest createNetworkOptions = HttpRequest.builder().method("GET")
                                                 .endpoint("http://localhost:8080/client/api")
                                                 .addQueryParam("response", "json")
                                                 .addQueryParam("command", "createNetwork")
                                                 .addQueryParam("zoneid", "1")
                                                 .addQueryParam("networkofferingid", "2")
                                                 .addQueryParam("name", "named")
                                                 .addQueryParam("displaytext", "lovely")
                                                 .addQueryParam("netmask", "255.255.255.0")
                                                 .addQueryParam("domainid", "6").build();

   public void testCreateNetworkInZoneOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(NetworkAsyncClient.class, "createNetworkInZone", String.class, String.class, String.class,
            String.class, CreateNetworkOptions[].class);

      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1, 2, "named", "lovely", CreateNetworkOptions.Builder
            .netmask("255.255.255.0").domainId("6")));

      assertRequestLineEquals(httpRequest, createNetworkOptions.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testDeleteNetwork() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(NetworkAsyncClient.class, "deleteNetwork", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=deleteNetwork&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
}
