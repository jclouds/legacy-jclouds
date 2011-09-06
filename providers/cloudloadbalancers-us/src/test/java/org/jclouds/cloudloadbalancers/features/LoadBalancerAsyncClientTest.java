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
package org.jclouds.cloudloadbalancers.features;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.cloudloadbalancers.domain.LoadBalancerAttributes;
import org.jclouds.cloudloadbalancers.domain.LoadBalancerRequest;
import org.jclouds.cloudloadbalancers.domain.LoadBalancerAttributes.Builder;
import org.jclouds.cloudloadbalancers.domain.VirtualIP.Type;
import org.jclouds.cloudloadbalancers.functions.UnwrapLoadBalancer;
import org.jclouds.cloudloadbalancers.functions.UnwrapLoadBalancers;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code LoadBalancerAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "LoadBalancerAsyncClientTest")
public class LoadBalancerAsyncClientTest extends BaseCloudLoadBalancersAsyncClientTest<LoadBalancerAsyncClient> {

   public void testListLoadBalancers() throws SecurityException, NoSuchMethodException, IOException {
      Method method = LoadBalancerAsyncClient.class.getMethod("listLoadBalancers");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/1234/loadbalancers HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapLoadBalancers.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetLoadBalancer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = LoadBalancerAsyncClient.class.getMethod("getLoadBalancer", int.class);
      HttpRequest httpRequest = processor.createRequest(method, 5);

      assertRequestLineEquals(httpRequest,
               "GET https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/1234/loadbalancers/5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapLoadBalancer.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateLoadBalancerWithType() throws SecurityException, NoSuchMethodException, IOException {
      Method method = LoadBalancerAsyncClient.class.getMethod("createLoadBalancer", LoadBalancerRequest.class);
      HttpRequest httpRequest = processor.createRequest(method, LoadBalancerRequest.builder().name("goo").protocol(
               "HTTP").port(80).virtualIPType(Type.PUBLIC).build());

      assertRequestLineEquals(httpRequest,
               "POST https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/1234/loadbalancers HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(
               httpRequest,
               "{\"loadBalancer\":{\"virtualIps\":[{\"type\":\"PUBLIC\"}],\"name\":\"goo\",\"protocol\":\"HTTP\",\"port\":80,\"nodes\":[]}}",
               "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapLoadBalancer.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateLoadBalancerWithId() throws SecurityException, NoSuchMethodException, IOException {
      Method method = LoadBalancerAsyncClient.class.getMethod("createLoadBalancer", LoadBalancerRequest.class);
      HttpRequest httpRequest = processor.createRequest(method, LoadBalancerRequest.builder().name("goo").protocol(
               "HTTP").port(80).virtualIPId(4).build());

      assertRequestLineEquals(httpRequest,
               "POST https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/1234/loadbalancers HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(
               httpRequest,
               "{\"loadBalancer\":{\"virtualIps\":[{\"id\":\"4\"}],\"name\":\"goo\",\"protocol\":\"HTTP\",\"port\":80,\"nodes\":[]}}",
               "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapLoadBalancer.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testUpdateLoadBalancerAttributes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = LoadBalancerAsyncClient.class.getMethod("updateLoadBalancerAttributes", int.class,
               LoadBalancerAttributes.class);
      HttpRequest httpRequest = processor.createRequest(method, 2, Builder.name("foo"));

      assertRequestLineEquals(httpRequest,
               "PUT https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/1234/loadbalancers/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, "{\"loadBalancer\":{\"name\":\"foo\"}}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapLoadBalancer.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testRemoveLoadBalancer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = LoadBalancerAsyncClient.class.getMethod("removeLoadBalancer", int.class);
      HttpRequest httpRequest = processor.createRequest(method, 5);

      assertRequestLineEquals(httpRequest,
               "DELETE https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/1234/loadbalancers/5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: */*\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<LoadBalancerAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<LoadBalancerAsyncClient>>() {
      };
   }
}
