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
package org.jclouds.gogrid.services;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.IpPortPair;
import org.jclouds.gogrid.domain.LoadBalancerPersistenceType;
import org.jclouds.gogrid.domain.LoadBalancerType;
import org.jclouds.gogrid.functions.ParseLoadBalancerFromJsonResponse;
import org.jclouds.gogrid.functions.ParseLoadBalancerListFromJsonResponse;
import org.jclouds.gogrid.options.AddLoadBalancerOptions;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

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
      Method method = GridLoadBalancerAsyncClient.class.getMethod("getLoadBalancerList");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/list?v=1.5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/list?v=1.5&"
            + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testAddLoadBalancer() throws NoSuchMethodException, IOException {
      Method method = GridLoadBalancerAsyncClient.class.getMethod("addLoadBalancer", String.class, IpPortPair.class,
            List.class, AddLoadBalancerOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "BalanceIt",
            IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(80).build(),
            ImmutableList.of(IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(8080).build(),
                  IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(9090).build()),
            new AddLoadBalancerOptions.Builder().create(
                  LoadBalancerType.LEAST_CONNECTED, LoadBalancerPersistenceType.SSL_STICKY));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
            + "add?v=1.5&name=BalanceIt&loadbalancer.type=Least%20Connect&"
            + "loadbalancer.persistence=SSL%20Sticky&realiplist.0.ip=127.0.0.1&"
            + "realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090&"
            + "virtualip.ip=127.0.0.1&virtualip.port=80 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
            + "add?v=1.5&name=BalanceIt&loadbalancer.type=Least%20Connect&"
            + "loadbalancer.persistence=SSL%20Sticky&realiplist.0.ip=127.0.0.1&"
            + "realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090&"
            + "virtualip.ip=127.0.0.1&virtualip.port=80&" + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity "
            + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testEditLoadBalancer() throws NoSuchMethodException, IOException {
      Method method = GridLoadBalancerAsyncClient.class.getMethod("editLoadBalancer", long.class, List.class);
      HttpRequest httpRequest = processor.createRequest(method, 1l, ImmutableList.of(
            IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(8080).build(),
            IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(9090).build()));

      assertRequestLineEquals(
            httpRequest,
            "GET https://api.gogrid.com/api/grid/loadbalancer/edit?v=1.5&id=1&realiplist.0.ip=127.0.0.1&realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(
            httpRequest,
            "GET https://api.gogrid.com/api/grid/loadbalancer/edit?v=1.5&id=1&realiplist.0.ip=127.0.0.1&realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090&sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testEditLoadBalancerNamed() throws NoSuchMethodException, IOException {
      Method method = GridLoadBalancerAsyncClient.class.getMethod("editLoadBalancerNamed", String.class, List.class);
      HttpRequest httpRequest = processor.createRequest(method, "BalanceIt", ImmutableList.of(
            IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(8080).build(),
            IpPortPair.builder().ip(Ip.builder().ip("127.0.0.1").build()).port(9090).build()));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
            + "edit?v=1.5&name=BalanceIt&realiplist.0.ip=127.0.0.1&"
            + "realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(
            httpRequest,
            "GET https://api.gogrid.com/api/grid/loadbalancer/edit?v=1.5&name=BalanceIt&realiplist.0.ip=127.0.0.1&realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090&sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testGetLoadBalancersByName() throws NoSuchMethodException, IOException {
      Method method = GridLoadBalancerAsyncClient.class.getMethod("getLoadBalancersByName", String[].class);
      HttpRequest httpRequest = processor.createRequest(method,
            "My Load Balancer", "My Load Balancer 2");

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
            + "get?v=1.5&name=My%20Load%20Balancer&name=My%20Load%20Balancer%202 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
            + "get?v=1.5&name=My%20Load%20Balancer&name=My%20Load%20Balancer%202&"
            + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testDeleteLoadBalancerById() throws NoSuchMethodException, IOException {
      Method method = GridLoadBalancerAsyncClient.class.getMethod("deleteById", Long.class);
      HttpRequest httpRequest = processor.createRequest(method, 55L);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
            + "delete?v=1.5&id=55 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/" + "delete?v=1.5&id=55&"
            + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }
}
