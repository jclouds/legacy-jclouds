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
package org.jclouds.gogrid.services;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.IpPortPair;
import org.jclouds.gogrid.domain.LoadBalancerPersistenceType;
import org.jclouds.gogrid.domain.LoadBalancerType;
import org.jclouds.gogrid.functions.ParseLoadBalancerFromJsonResponse;
import org.jclouds.gogrid.functions.ParseLoadBalancerListFromJsonResponse;
import org.jclouds.gogrid.options.AddLoadBalancerOptions;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * @author Oleksiy Yarmula
 */
public class GridLoadBalancerAsyncClientTest extends
         BaseGoGridAsyncClientTest<GridLoadBalancerAsyncClient> {

   @Test
   public void testGetLoadBalancerList() throws NoSuchMethodException, IOException {
      Method method = GridLoadBalancerAsyncClient.class.getMethod("getLoadBalancerList");
      GeneratedHttpRequest<GridLoadBalancerAsyncClient> httpRequest = processor
               .createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET https://api.gogrid.com/api/grid/loadbalancer/list?v=1.4 HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest,
               ParseLoadBalancerListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest,
               "GET https://api.gogrid.com/api/grid/loadbalancer/list?v=1.4&"
                        + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);
   }

   @Test
   public void testAddLoadBalancer() throws NoSuchMethodException, IOException {
      Method method = GridLoadBalancerAsyncClient.class.getMethod("addLoadBalancer", String.class,
               IpPortPair.class, List.class, AddLoadBalancerOptions[].class);
      GeneratedHttpRequest<GridLoadBalancerAsyncClient> httpRequest = processor.createRequest(
               method, "BalanceIt", new IpPortPair(new Ip("127.0.0.1"), 80), Arrays.asList(
                        new IpPortPair(new Ip("127.0.0.1"), 8080), new IpPortPair(new Ip(
                                 "127.0.0.1"), 9090)), new AddLoadBalancerOptions.Builder().create(
                        LoadBalancerType.LEAST_CONNECTED, LoadBalancerPersistenceType.SSL_STICKY));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
               + "add?v=1.4&name=BalanceIt&loadbalancer.type=Least%20Connect&"
               + "loadbalancer.persistence=SSL%20Sticky&realiplist.0.ip=127.0.0.1&"
               + "realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090&"
               + "virtualip.ip=127.0.0.1&virtualip.port=80 HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
               + "add?v=1.4&name=BalanceIt&loadbalancer.type=Least%20Connect&"
               + "loadbalancer.persistence=SSL%20Sticky&realiplist.0.ip=127.0.0.1&"
               + "realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090&"
               + "virtualip.ip=127.0.0.1&virtualip.port=80&"
               + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);
   }

   @Test
   public void testEditLoadBalancer() throws NoSuchMethodException, IOException {
      Method method = GridLoadBalancerAsyncClient.class.getMethod("editLoadBalancer", long.class,
               List.class);
      GeneratedHttpRequest<GridLoadBalancerAsyncClient> httpRequest = processor.createRequest(
               method, 1l, Arrays.asList(new IpPortPair(new Ip("127.0.0.1"), 8080), new IpPortPair(
                        new Ip("127.0.0.1"), 9090)));

      assertRequestLineEquals(
               httpRequest,
               "GET https://api.gogrid.com/api/grid/loadbalancer/edit?v=1.4&id=1&realiplist.0.ip=127.0.0.1&realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090 HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(
               httpRequest,
               "GET https://api.gogrid.com/api/grid/loadbalancer/edit?v=1.4&id=1&realiplist.0.ip=127.0.0.1&realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090&sig=3f446f171455fbb5574aecff4997b273&api_key=foo HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);
   }

   @Test
   public void testEditLoadBalancerNamed() throws NoSuchMethodException, IOException {
      Method method = GridLoadBalancerAsyncClient.class.getMethod("editLoadBalancerNamed",
               String.class, List.class);
      GeneratedHttpRequest<GridLoadBalancerAsyncClient> httpRequest = processor.createRequest(
               method, "BalanceIt", Arrays.asList(new IpPortPair(new Ip("127.0.0.1"), 8080),
                        new IpPortPair(new Ip("127.0.0.1"), 9090)));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
               + "edit?v=1.4&name=BalanceIt&realiplist.0.ip=127.0.0.1&"
               + "realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090 HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(
               httpRequest,
               "GET https://api.gogrid.com/api/grid/loadbalancer/edit?v=1.4&name=BalanceIt&realiplist.0.ip=127.0.0.1&realiplist.0.port=8080&realiplist.1.ip=127.0.0.1&realiplist.1.port=9090&sig=3f446f171455fbb5574aecff4997b273&api_key=foo HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);
   }

   @Test
   public void testGetLoadBalancersByName() throws NoSuchMethodException, IOException {
      Method method = GridLoadBalancerAsyncClient.class.getMethod("getLoadBalancersByName",
               String[].class);
      GeneratedHttpRequest<GridLoadBalancerAsyncClient> httpRequest = processor.createRequest(
               method, "My Load Balancer", "My Load Balancer 2");

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
               + "get?v=1.4&name=My%20Load%20Balancer&name=My%20Load%20Balancer%202 HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest,
               ParseLoadBalancerListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
               + "get?v=1.4&name=My%20Load%20Balancer&name=My%20Load%20Balancer%202&"
               + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);
   }

   @Test
   public void testDeleteLoadBalancerById() throws NoSuchMethodException, IOException {
      Method method = GridLoadBalancerAsyncClient.class.getMethod("deleteById", Long.class);
      GeneratedHttpRequest<GridLoadBalancerAsyncClient> httpRequest = processor.createRequest(
               method, 55L);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
               + "delete?v=1.4&id=55 HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseLoadBalancerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/loadbalancer/"
               + "delete?v=1.4&id=55&" + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo "
               + "HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<GridLoadBalancerAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<GridLoadBalancerAsyncClient>>() {
      };
   }

}
