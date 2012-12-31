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

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.CreateVMGroupOptions;
import org.jclouds.cloudstack.options.ListVMGroupsOptions;
import org.jclouds.cloudstack.options.UpdateVMGroupOptions;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.testng.annotations.Test;

import com.google.common.base.Functions;

/**
 * Tests behavior of {@code VMGroupAsyncClient}
 *
 * @author Richard Downer
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "VMGroupAsyncClientTest")
public class VMGroupAsyncClientTest extends BaseCloudStackAsyncClientTest<VMGroupAsyncClient> {

   public void testListVMGroups() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VMGroupAsyncClient.class.getMethod("listInstanceGroups", ListVMGroupsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listInstanceGroups&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListVMGroupsOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VMGroupAsyncClient.class.getMethod("listInstanceGroups", ListVMGroupsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListVMGroupsOptions.Builder.account("fred")
         .domainId("5").id("6"));

      assertRequestLineEquals(
         httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listInstanceGroups&listAll=true&account=fred&domainid=5&id=6 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetVMGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VMGroupAsyncClient.class.getMethod("getInstanceGroup", String.class);
      HttpRequest httpRequest = processor.createRequest(method, 5);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listInstanceGroups&listAll=true&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest,
         Functions.compose(IdentityFunction.INSTANCE, IdentityFunction.INSTANCE).getClass());
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateVMGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VMGroupAsyncClient.class.getMethod("createInstanceGroup", String.class, CreateVMGroupOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "goo");

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=createInstanceGroup&name=goo HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testCreateVMGroupOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VMGroupAsyncClient.class.getMethod("createInstanceGroup", String.class, CreateVMGroupOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "goo", CreateVMGroupOptions.Builder.account("foo").domainId("42"));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=createInstanceGroup&name=goo&account=foo&domainid=42 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testUpdateVMGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VMGroupAsyncClient.class.getMethod("updateInstanceGroup", String.class, UpdateVMGroupOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 5, UpdateVMGroupOptions.Builder.name("fred"));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateInstanceGroup&id=5&name=fred HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testDeleteVMGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VMGroupAsyncClient.class.getMethod("deleteInstanceGroup", String.class);
      HttpRequest httpRequest = processor.createRequest(method, 5);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=deleteInstanceGroup&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
}
