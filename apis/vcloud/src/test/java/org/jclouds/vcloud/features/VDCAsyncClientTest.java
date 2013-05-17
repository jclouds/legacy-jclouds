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
package org.jclouds.vcloud.features;

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;
import java.net.URI;
import java.util.NoSuchElementException;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.internal.BaseVCloudAsyncClientTest;
import org.jclouds.vcloud.xml.VDCHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code VDCAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "VDCAsyncClientTest")
public class VDCAsyncClientTest extends BaseVCloudAsyncClientTest<VDCAsyncClient> {

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testFindVDCInOrgNamedBadVDC() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VDCAsyncClient.class, "findVDCInOrgNamed", String.class, String.class);
      processor.createRequest(method, ImmutableList.<Object> of("org", "vdc1"));
   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testFindVDCInOrgNamedBadOrg() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VDCAsyncClient.class, "findVDCInOrgNamed", String.class, String.class);
      processor.createRequest(method, ImmutableList.<Object> of("org1", "vdc"));
   }

   public void testFindVDCInOrgNamedNullOrg() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VDCAsyncClient.class, "findVDCInOrgNamed", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "vdc"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/vdc/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VDCHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testFindVDCInOrgNamedNullOrgAndVDC() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VDCAsyncClient.class, "findVDCInOrgNamed", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, null));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/vdc/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VDCHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetVDC() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VDCAsyncClient.class, "getVDC", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1")));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/vdc/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VDCHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }
}
