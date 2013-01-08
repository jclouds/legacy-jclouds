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

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.AssignVirtualMachineOptions;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.jclouds.cloudstack.options.ListVirtualMachinesOptions;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.reflect.Invokable;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code VirtualMachineAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "VirtualMachineAsyncClientTest")
public class VirtualMachineAsyncClientTest extends BaseCloudStackAsyncClientTest<VirtualMachineAsyncClient> {
   public void testListVirtualMachines() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VirtualMachineAsyncClient.class.getMethod("listVirtualMachines",
            ListVirtualMachinesOptions[].class));
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listVirtualMachines&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListVirtualMachinesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VirtualMachineAsyncClient.class.getMethod("listVirtualMachines",
            ListVirtualMachinesOptions[].class));
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(
            ListVirtualMachinesOptions.Builder.accountInDomain("adrian", "6").usesVirtualNetwork(true)));

      assertRequestLineEquals(
            httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listVirtualMachines&listAll=true&account=adrian&domainid=6&forvirtualnetwork=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetVirtualMachine() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VirtualMachineAsyncClient.class.getMethod("getVirtualMachine", String.class));
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listVirtualMachines&listAll=true&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest,
            Functions.compose(IdentityFunction.INSTANCE, IdentityFunction.INSTANCE).getClass());
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   HttpRequest deployVirtualMachine = HttpRequest.builder().method("GET")
                                                 .endpoint("http://localhost:8080/client/api")
                                                 .addQueryParam("response", "json")
                                                 .addQueryParam("command", "deployVirtualMachine")
                                                 .addQueryParam("zoneid", "6")
                                                 .addQueryParam("serviceofferingid", "4")
                                                 .addQueryParam("templateid", "5").build();

   public void testDeployVirtualMachineInZone() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VirtualMachineAsyncClient.class.getMethod("deployVirtualMachineInZone", String.class, String.class,
            String.class, DeployVirtualMachineOptions[].class));
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(6, 4, 5));

      assertRequestLineEquals(httpRequest, deployVirtualMachine.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testRebootVirtualMachine() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VirtualMachineAsyncClient.class.getMethod("rebootVirtualMachine", String.class));
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=rebootVirtualMachine&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testStartVirtualMachine() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VirtualMachineAsyncClient.class.getMethod("startVirtualMachine", String.class));
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=startVirtualMachine&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testStopVirtualMachine() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VirtualMachineAsyncClient.class.getMethod("stopVirtualMachine", String.class));
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=stopVirtualMachine&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testResetPasswordForVirtualMachine() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VirtualMachineAsyncClient.class.getMethod("resetPasswordForVirtualMachine", String.class));
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=resetPasswordForVirtualMachine&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testChangeServiceForVirtualMachine() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VirtualMachineAsyncClient.class.getMethod("changeServiceForVirtualMachine", String.class));
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=changeServiceForVirtualMachine&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testUpdateVirtualMachine() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VirtualMachineAsyncClient.class.getMethod("updateVirtualMachine", String.class));
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=updateVirtualMachine&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testDestroyVirtualMachine() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VirtualMachineAsyncClient.class.getMethod("destroyVirtualMachine", String.class));
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=destroyVirtualMachine&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
   
   public void testAssignVirtualMachine() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VirtualMachineAsyncClient.class.getMethod("assignVirtualMachine", String.class,
            AssignVirtualMachineOptions[].class));
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("abcd",
            AssignVirtualMachineOptions.Builder.accountInDomain("adrian", "6")));

      assertRequestLineEquals(
            httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=assignVirtualMachine&virtualmachineid=abcd&account=adrian&domainid=6 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }
}
