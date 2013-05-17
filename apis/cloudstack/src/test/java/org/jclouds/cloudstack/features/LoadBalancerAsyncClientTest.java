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
import org.jclouds.cloudstack.domain.LoadBalancerRule.Algorithm;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.CreateLoadBalancerRuleOptions;
import org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions;
import org.jclouds.cloudstack.options.UpdateLoadBalancerRuleOptions;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code LoadBalancerAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "LoadBalancerAsyncClientTest")
public class LoadBalancerAsyncClientTest extends BaseCloudStackAsyncClientTest<LoadBalancerAsyncClient> {
   public void testListLoadBalancerRules() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(LoadBalancerAsyncClient.class, "listLoadBalancerRules",
            ListLoadBalancerRulesOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listLoadBalancerRules&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListLoadBalancerRulesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(LoadBalancerAsyncClient.class, "listLoadBalancerRules",
            ListLoadBalancerRulesOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(ListLoadBalancerRulesOptions.Builder.publicIPId("3")));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listLoadBalancerRules&listAll=true&publicipid=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   HttpRequest createLoadBalancerRule = HttpRequest.builder().method("GET")
                                                   .endpoint("http://localhost:8080/client/api")
                                                   .addQueryParam("response", "json")
                                                   .addQueryParam("command", "createLoadBalancerRule")
                                                   .addQueryParam("publicipid", "6")
                                                   .addQueryParam("algorithm", "leastconn")
                                                   .addQueryParam("name", "tcp")
                                                   .addQueryParam("privateport", "22")
                                                   .addQueryParam("publicport", "22").build();

   public void testCreateLoadBalancerRuleForPublicIP() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(LoadBalancerAsyncClient.class, "createLoadBalancerRuleForPublicIP", String.class,
            Algorithm.class, String.class, int.class, int.class, CreateLoadBalancerRuleOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(6, Algorithm.LEASTCONN, "tcp", 22, 22));

      assertRequestLineEquals(httpRequest, createLoadBalancerRule.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testUpdateLoadBalancerRule() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(LoadBalancerAsyncClient.class, "updateLoadBalancerRule", String.class, UpdateLoadBalancerRuleOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=updateLoadBalancerRule&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteLoadBalancerRule() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(LoadBalancerAsyncClient.class, "deleteLoadBalancerRule", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=deleteLoadBalancerRule&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListVirtualMachinesAssignedToLoadBalancerRule() throws SecurityException, NoSuchMethodException,
         IOException {
      Invokable<?, ?> method = method(LoadBalancerAsyncClient.class, "listVirtualMachinesAssignedToLoadBalancerRule",
            String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listLoadBalancerRuleInstances&listAll=true&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
}
