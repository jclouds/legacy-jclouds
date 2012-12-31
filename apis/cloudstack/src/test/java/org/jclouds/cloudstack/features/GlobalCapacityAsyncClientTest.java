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
import org.jclouds.cloudstack.domain.Capacity;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.ListCapacityOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code GlobalCapacityAsyncClient}
 *
 * @author Richard Downer
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "GlobalCapacityAsyncClientTest")
public class GlobalCapacityAsyncClientTest extends BaseCloudStackAsyncClientTest<GlobalCapacityAsyncClient> {

   public void testListCapacity() throws SecurityException, NoSuchMethodException, IOException {
      Method method = GlobalCapacityAsyncClient.class.getMethod("listCapacity", ListCapacityOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listCapacity&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testListCapacityOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = GlobalCapacityAsyncClient.class.getMethod("listCapacity", ListCapacityOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListCapacityOptions.Builder.hostId("3").keyword("fred").podId("4").type(Capacity.Type.CPU_ALLOCATED_MHZ).zoneId("6"));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listCapacity&listAll=true&hostid=3&keyword=fred&podid=4&type=1&zoneid=6 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }
}
