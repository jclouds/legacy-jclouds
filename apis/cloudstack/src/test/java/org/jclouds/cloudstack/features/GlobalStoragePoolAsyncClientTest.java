/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.features;

import static org.jclouds.reflect.Reflection2.method;

import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.ListStoragePoolsOptions;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code GlobalStoragePoolAsyncClient}
 *
 * @author Richard Downer
 */
@Test(groups = "unit", testName = "GlobalStoragePoolAsyncClientTest")
public class GlobalStoragePoolAsyncClientTest extends BaseCloudStackAsyncClientTest<GlobalStoragePoolAsyncClient> {

   public void testListStoragePools() throws NoSuchMethodException {
      Invokable<?, ?> method = method(GlobalStoragePoolAsyncClient.class, "listStoragePools", ListStoragePoolsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

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
      Invokable<?, ?> method = method(GlobalStoragePoolAsyncClient.class, "listStoragePools", ListStoragePoolsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(ListStoragePoolsOptions.Builder.clusterId("3").id("4").ipAddress("192.168.42.42").keyword("fred").name("bob").path("/mnt/store42").podId("4").zoneId("5")));

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
