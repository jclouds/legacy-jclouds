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
import java.util.Set;

import org.jclouds.cloudloadbalancers.domain.NodeAttributes;
import org.jclouds.cloudloadbalancers.domain.NodeRequest;
import org.jclouds.cloudloadbalancers.domain.NodeAttributes.Builder;
import org.jclouds.cloudloadbalancers.domain.internal.BaseNode.Condition;
import org.jclouds.cloudloadbalancers.internal.BaseCloudLoadBalancersAsyncClientTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code NodeAsyncClient}
 * 
 * @author Dan Lo Bianco
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "NodeAsyncClientTest")
public class NodeAsyncClientTest extends BaseCloudLoadBalancersAsyncClientTest<NodeAsyncClient> {

   public void testListNodes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = NodeAsyncClient.class.getMethod("listNodes", int.class);
      HttpRequest httpRequest = processor.createRequest(method, 2);

      assertRequestLineEquals(httpRequest,
               "GET https://lon.loadbalancers.api.rackspacecloud.com/v1.0/10001786/loadbalancers/2/nodes HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetNodeInLoadBalancer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = NodeAsyncClient.class.getMethod("getNodeInLoadBalancer", int.class, int.class);
      HttpRequest httpRequest = processor.createRequest(method, 3, 2);

      assertRequestLineEquals(httpRequest,
               "GET https://lon.loadbalancers.api.rackspacecloud.com/v1.0/10001786/loadbalancers/2/nodes/3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void createNodesInLoadBalancerWithType() throws SecurityException, NoSuchMethodException, IOException {
      Method method = NodeAsyncClient.class.getMethod("createNodesInLoadBalancer", Set.class, int.class);
      HttpRequest httpRequest = processor.createRequest(method, ImmutableList.<NodeRequest>of(NodeRequest.builder().
    		  address("192.168.1.1").port(8080).build()), 3);

      assertRequestLineEquals(httpRequest,
               "POST https://lon.loadbalancers.api.rackspacecloud.com/v1.0/10001786/loadbalancers/3/nodes HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(
               httpRequest,
               "{\"nodes\":[{\"address\":\"192.168.1.1\",\"port\":8080,\"condition\":\"ENABLED\"}]}",
               "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testUpdateAttributesForNodeInLoadBalancer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = NodeAsyncClient.class.getMethod("updateAttributesForNodeInLoadBalancer", NodeAttributes.class, 
    		  int.class, int.class);
      HttpRequest httpRequest = processor.createRequest(method, Builder.condition(Condition.DISABLED).weight(13), 8, 7);

      assertRequestLineEquals(httpRequest,
               "PUT https://lon.loadbalancers.api.rackspacecloud.com/v1.0/10001786/loadbalancers/7/nodes/8 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, "{\"node\":{\"condition\":\"DISABLED\",\"weight\":13}}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testRemoveNodeFromLoadBalancer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = NodeAsyncClient.class.getMethod("removeNodeFromLoadBalancer", int.class, int.class);
      HttpRequest httpRequest = processor.createRequest(method, 9, 4);

      assertRequestLineEquals(httpRequest,
               "DELETE https://lon.loadbalancers.api.rackspacecloud.com/v1.0/10001786/loadbalancers/4/nodes/9 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: */*\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<NodeAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<NodeAsyncClient>>() {
      };
   }
   
}
