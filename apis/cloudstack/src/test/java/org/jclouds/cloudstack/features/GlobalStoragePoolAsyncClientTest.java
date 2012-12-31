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

import java.lang.reflect.Method;

import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.ListStoragePoolsOptions;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code GlobalStoragePoolAsyncClient}
 *
 * @author Richard Downer
 */
@Test(groups = "unit", testName = "GlobalStoragePoolAsyncClientTest")
public class GlobalStoragePoolAsyncClientTest extends BaseCloudStackAsyncClientTest<GlobalStoragePoolAsyncClient> {

   public void testListStoragePools() throws NoSuchMethodException {
      Method method = GlobalStoragePoolAsyncClient.class.getMethod("listStoragePools", ListStoragePoolsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listStoragePools&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListStoragePoolsOptions() throws NoSuchMethodException {
      Method method = GlobalStoragePoolAsyncClient.class.getMethod("listStoragePools", ListStoragePoolsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListStoragePoolsOptions.Builder.clusterId("3").id("4").ipAddress("192.168.42.42").keyword("fred").name("bob").path("/mnt/store42").podId("4").zoneId("5"));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listStoragePools&listAll=true&clusterid=3&id=4&ipaddress=192.168.42.42&keyword=fred&name=bob&path=/mnt/store42&podid=4&zoneid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }
}
