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
package org.jclouds.softlayer.features;

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.reflect.Invokable;
/**
 * Tests annotation parsing of {@code DatacenterAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class DatacenterAsyncClientTest extends BaseSoftLayerAsyncClientTest<DatacenterAsyncClient> {

   public void testListDatacenters() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(DatacenterAsyncClient.class, "listDatacenters");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Location_Datacenter/Datacenters.json?objectMask=locationAddress%3Bregions HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Location_Datacenter/Datacenters.json?objectMask=locationAddress%3Bregions HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/json\nAuthorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetDatacenter() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(DatacenterAsyncClient.class, "getDatacenter", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Location_Datacenter/1234.json?objectMask=locationAddress%3Bregions HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
}
