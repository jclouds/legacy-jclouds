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

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.jclouds.cloudloadbalancers.CloudLoadBalancersAsyncClient;
import org.jclouds.cloudloadbalancers.CloudLoadBalancersClient;
import org.jclouds.cloudloadbalancers.CloudLoadBalancersContextBuilder;
import org.jclouds.cloudloadbalancers.domain.NodeAttributes;
import org.jclouds.cloudloadbalancers.domain.NodeAttributes.Builder;
import org.jclouds.cloudloadbalancers.domain.NodeRequest;
import org.jclouds.cloudloadbalancers.domain.internal.BaseNode.Condition;
import org.jclouds.cloudloadbalancers.functions.UnwrapNode;
import org.jclouds.cloudloadbalancers.functions.UnwrapNodes;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

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
               "GET https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/1234/loadbalancers/2/nodes HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapNodes.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = NodeAsyncClient.class.getMethod("getNode", int.class, int.class);
      HttpRequest httpRequest = processor.createRequest(method, 2, 3);

      assertRequestLineEquals(httpRequest,
               "GET https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/1234/loadbalancers/2/nodes/3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapNode.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateNodeWithType() throws SecurityException, NoSuchMethodException, IOException {
      Method method = NodeAsyncClient.class.getMethod("createNode", int.class, NodeRequest.class);
      HttpRequest httpRequest = processor.createRequest(method, 3, NodeRequest.builder().
    		  address("192.168.1.1").port(8080).build());

      assertRequestLineEquals(httpRequest,
               "POST https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/1234/loadbalancers/3/nodes HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(
               httpRequest,
               "{\"node\":{\"address\":\"192.168.1.1\",\"port\":8080,\"condition\":\"ENABLED\"}}",
               "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapNode.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testModifyNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = NodeAsyncClient.class.getMethod("modifyNode", int.class, int.class,
               NodeAttributes.class);
      HttpRequest httpRequest = processor.createRequest(method, 7, 8, Builder.condition(Condition.DISABLED).weight(13));

      assertRequestLineEquals(httpRequest,
               "PUT https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/1234/loadbalancers/7/nodes/8 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, "{\"node\":{\"condition\":\"DISABLED\",\"weight\":13}}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testRemoveLoadBalancer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = NodeAsyncClient.class.getMethod("removeNode", int.class, int.class);
      HttpRequest httpRequest = processor.createRequest(method, 4, 9);

      assertRequestLineEquals(httpRequest,
               "DELETE https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/1234/loadbalancers/4/nodes/9 HTTP/1.1");
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
   
   protected String provider = "cloudloadbalancers";

   @Override
   public RestContextSpec<CloudLoadBalancersClient, CloudLoadBalancersAsyncClient> createContextSpec() {
      return new RestContextFactory(getProperties()).createContextSpec(provider, "user", "password", new Properties());
   }
   
   @Override
   protected Properties getProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_REGIONS, "US");
      overrides.setProperty(PROPERTY_API_VERSION, "1");
      overrides.setProperty(provider + ".endpoint", "https://auth");
      overrides.setProperty(provider + ".contextbuilder", CloudLoadBalancersContextBuilder.class.getName());
      return overrides;
   }
}
