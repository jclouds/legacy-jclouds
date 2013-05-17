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
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.CreateIPForwardingRuleOptions;
import org.jclouds.cloudstack.options.ListIPForwardingRulesOptions;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code NATAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "NATAsyncClientTest")
public class NATAsyncClientTest extends BaseCloudStackAsyncClientTest<NATAsyncClient> {
   public void testListIPForwardingRules() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(NATAsyncClient.class, "listIPForwardingRules", ListIPForwardingRulesOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listIpForwardingRules&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListIPForwardingRulesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(NATAsyncClient.class, "listIPForwardingRules", ListIPForwardingRulesOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(
            ListIPForwardingRulesOptions.Builder.virtualMachineId("3")));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listIpForwardingRules&listAll=true&virtualmachineid=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetIPForwardingRule() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(NATAsyncClient.class, "getIPForwardingRule", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listIpForwardingRules&listAll=true&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   HttpRequest createIpForwardingRule = HttpRequest.builder().method("GET")
                                                             .endpoint("http://localhost:8080/client/api")
                                                             .addQueryParam("response", "json")
                                                             .addQueryParam("command", "createIpForwardingRule")
                                                             .addQueryParam("ipaddressid", "7")
                                                             .addQueryParam("protocol", "tcp")
                                                             .addQueryParam("startport", "22").build();

   public void testCreateIPForwardingRuleForVirtualMachine() throws SecurityException, NoSuchMethodException,
         IOException {
      Invokable<?, ?> method = method(NATAsyncClient.class, "createIPForwardingRule", String.class, String.class, int.class,
            CreateIPForwardingRuleOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(7, "tcp", 22));

      assertRequestLineEquals(httpRequest, createIpForwardingRule.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   HttpRequest createIpForwardingRuleOptions = HttpRequest.builder().method("GET")
                                                          .endpoint("http://localhost:8080/client/api")
                                                          .addQueryParam("response", "json")
                                                          .addQueryParam("command", "createIpForwardingRule")
                                                          .addQueryParam("ipaddressid", "7")
                                                          .addQueryParam("protocol", "tcp")
                                                          .addQueryParam("startport", "22")
                                                          .addQueryParam("endport", "22").build();

   public void testCreateIPForwardingRuleForVirtualMachineOptions() throws SecurityException, NoSuchMethodException,
         IOException {
      Invokable<?, ?> method = method(NATAsyncClient.class, "createIPForwardingRule", String.class, String.class, int.class,
            CreateIPForwardingRuleOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(7, "tcp", 22,
            CreateIPForwardingRuleOptions.Builder.endPort(22)));

      assertRequestLineEquals(httpRequest, createIpForwardingRuleOptions.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testEnableStaticNATForVirtualMachine() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(NATAsyncClient.class, "enableStaticNATForVirtualMachine", String.class, String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5, 6));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=enableStaticNat&virtualmachineid=5&ipaddressid=6 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testDisableStaticNATOnPublicIP() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(NATAsyncClient.class, "disableStaticNATOnPublicIP", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=disableStaticNat&ipaddressid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testDeleteIPForwardingRule() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(NATAsyncClient.class, "deleteIPForwardingRule", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=deleteIpForwardingRule&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
}
