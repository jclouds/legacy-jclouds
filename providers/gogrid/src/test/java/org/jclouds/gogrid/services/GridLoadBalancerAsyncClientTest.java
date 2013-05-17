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
package org.jclouds.gogrid.services;

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;
import java.util.List;

import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.IpPortPair;
import org.jclouds.gogrid.domain.LoadBalancerPersistenceType;
import org.jclouds.gogrid.domain.LoadBalancerType;
import org.jclouds.gogrid.functions.ParseLoadBalancerFromJsonResponse;
import org.jclouds.gogrid.functions.ParseLoadBalancerListFromJsonResponse;
import org.jclouds.gogrid.options.AddLoadBalancerOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code GridLoadBalancerAsyncClient}
 *
 * @author Oleksiy Yarmula
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GridLoadBalancerAsyncClientTest")
public class GridLoadBalancerAsyncClientTest extends BaseGoGridAsyncClientTest<GridLoadBalancerAsyncClient> {

   @Test
   public void testGetLoadBalancerList() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridLoadBalancerAsyncClient.class, "getLoadBalancerList");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/list?v=1.5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/list?v=1.5&"
            + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   HttpRequest addLoadBalancer = HttpRequest.builder().method("GET")
                                            .endpoint("https://api.gogrid.com/api/grid/loadbalancer/add")
                                            .addQueryParam("v", "1.5")
                                            .addQueryParam("name", "BalanceIt")
                                            .addQueryParam("loadbalancer.type", "Least Connect")
                                            .addQueryParam("loadbalancer.persistence", "SSL Sticky")
                                            .addQueryParam("virtualip.ip", "127.0.0.1")
                                            .addQueryParam("virtualip.port", "80")
                                            .addQueryParam("realiplist.0.ip", "127.0.0.1")
                                            .addQueryParam("realiplist.0.port", "8080")
                                            .addQueryParam("realiplist.1.ip", "127.0.0.1")
                                            .addQueryParam("realiplist.1.port", "9090")
                                            .addQueryParam("sig", "e9aafd0a5d4c69bb24536be4bce8a528")
                                            .addQueryParam("api_key", "identity").build();

   @Test
   public void testAddLoadBalancer() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridLoadBalancerAsyncClient.class, "addLoadBalancer", String.class, IpPortPair.class,
            List.class, AddLoadBalancerOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("BalanceIt",
            IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(80).build(),
            ImmutableList.of(IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(8080).build(),
                  IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(9090).build()),
            new AddLoadBalancerOptions.Builder().create(
                  LoadBalancerType.LEAST_CONNECTED, LoadBalancerPersistenceType.SSL_STICKY)));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, addLoadBalancer.getRequestLine());
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseLoadBalancerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   @Test
   public void testEditLoadBalancer() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridLoadBalancerAsyncClient.class, "editLoadBalancer", long.class, List.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1l, ImmutableList.of(
            IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(8080).build(),
            IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(9090).build())));

      assertRequestLineEquals(
            httpRequest,
            "GET https://api.gogrid.com/api/grid/loadbalancer/edit?v=1.5&id=1&realiplist.0.ip=127.0.0.1&realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(
            httpRequest,
            "GET https://api.gogrid.com/api/grid/loadbalancer/edit?v=1.5&id=1&realiplist.0.ip=127.0.0.1&realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090&sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testEditLoadBalancerNamed() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridLoadBalancerAsyncClient.class, "editLoadBalancerNamed", String.class, List.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("BalanceIt", ImmutableList.of(
            IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(8080).build(),
            IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(9090).build())));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
            + "edit?v=1.5&name=BalanceIt&realiplist.0.ip=127.0.0.1&"
            + "realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(
            httpRequest,
            "GET https://api.gogrid.com/api/grid/loadbalancer/edit?v=1.5&name=BalanceIt&realiplist.0.ip=127.0.0.1&realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090&sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testGetLoadBalancersByName() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridLoadBalancerAsyncClient.class, "getLoadBalancersByName", String[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(
            "My Load Balancer", "My Load Balancer 2"));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
            + "get?v=1.5&name=My%20Load%20Balancer&name=My%20Load%20Balancer%202 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
            + "get?v=1.5&name=My%20Load%20Balancer&name=My%20Load%20Balancer%202&"
            + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testDeleteLoadBalancerById() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridLoadBalancerAsyncClient.class, "deleteById", Long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(55L));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
            + "delete?v=1.5&id=55 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/" + "delete?v=1.5&id=55&"
            + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }
}
