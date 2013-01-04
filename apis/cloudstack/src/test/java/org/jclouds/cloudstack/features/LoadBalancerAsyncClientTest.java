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
package org.jclouds.cloudstack.features;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.cloudstack.domain.LoadBalancerRule.Algorithm;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.CreateLoadBalancerRuleOptions;
import org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions;
import org.jclouds.cloudstack.options.UpdateLoadBalancerRuleOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

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
      Method method = LoadBalancerAsyncClient.class.getMethod("listLoadBalancerRules",
            ListLoadBalancerRulesOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listLoadBalancerRules&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListLoadBalancerRulesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = LoadBalancerAsyncClient.class.getMethod("listLoadBalancerRules",
            ListLoadBalancerRulesOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListLoadBalancerRulesOptions.Builder.publicIPId("3"));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listLoadBalancerRules&listAll=true&publicipid=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

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
      Method method = LoadBalancerAsyncClient.class.getMethod("createLoadBalancerRuleForPublicIP", String.class,
            Algorithm.class, String.class, int.class, int.class, CreateLoadBalancerRuleOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 6, Algorithm.LEASTCONN, "tcp", 22, 22);

      assertRequestLineEquals(httpRequest, createLoadBalancerRule.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testUpdateLoadBalancerRule() throws SecurityException, NoSuchMethodException, IOException {
      Method method = LoadBalancerAsyncClient.class.getMethod("updateLoadBalancerRule", String.class, UpdateLoadBalancerRuleOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 5);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=updateLoadBalancerRule&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteLoadBalancerRule() throws SecurityException, NoSuchMethodException, IOException {
      Method method = LoadBalancerAsyncClient.class.getMethod("deleteLoadBalancerRule", String.class);
      HttpRequest httpRequest = processor.createRequest(method, 5);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=deleteLoadBalancerRule&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListVirtualMachinesAssignedToLoadBalancerRule() throws SecurityException, NoSuchMethodException,
         IOException {
      Method method = LoadBalancerAsyncClient.class.getMethod("listVirtualMachinesAssignedToLoadBalancerRule",
            String.class);
      HttpRequest httpRequest = processor.createRequest(method, 5);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listLoadBalancerRuleInstances&listAll=true&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<LoadBalancerAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<LoadBalancerAsyncClient>>() {
      };
   }
}
