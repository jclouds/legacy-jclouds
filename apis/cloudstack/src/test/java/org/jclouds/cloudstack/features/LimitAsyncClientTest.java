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
import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.ListResourceLimitsOptions;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

/**
 * Tests behavior of {@code LimitAsyncClient}
 *
 * @author Vijay Kiran
 */
@Test(groups = "unit", testName = "LimitAsyncClientTest")
public class LimitAsyncClientTest extends BaseCloudStackAsyncClientTest<LimitAsyncClient> {

   public void testListResourceLimits() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(LimitAsyncClient.class, "listResourceLimits", ListResourceLimitsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listResourceLimits&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListResourceLimitsOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(LimitAsyncClient.class, "listResourceLimits", ListResourceLimitsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(ListResourceLimitsOptions.Builder.account("jclouds" , "23")));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listResourceLimits&listAll=true&account=jclouds&domainid=23 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
}
