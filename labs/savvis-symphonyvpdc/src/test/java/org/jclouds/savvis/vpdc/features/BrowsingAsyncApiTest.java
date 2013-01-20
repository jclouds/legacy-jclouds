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
package org.jclouds.savvis.vpdc.features;
import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;
import java.net.URI;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.savvis.vpdc.options.GetVMOptions;
import org.jclouds.savvis.vpdc.xml.FirewallServiceHandler;
import org.jclouds.savvis.vpdc.xml.NetworkHandler;
import org.jclouds.savvis.vpdc.xml.OrgHandler;
import org.jclouds.savvis.vpdc.xml.TaskHandler;
import org.jclouds.savvis.vpdc.xml.VDCHandler;
import org.jclouds.savvis.vpdc.xml.VMHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;

/**
 * Tests annotation parsing of {@code BrowsingAsyncApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "BrowsingAsyncApiTest")
public class BrowsingAsyncApiTest extends BaseVPDCAsyncApiTest<BrowsingAsyncApi> {

   public void testOrg() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(BrowsingAsyncApi.class, "getOrg", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("11"));

      assertRequestLineEquals(request, "GET https://api.savvis.net/vpdc/v1.0/org/11 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, OrgHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testOrgNull() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(BrowsingAsyncApi.class, "getOrg", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((String) null));

      assertRequestLineEquals(request, "GET https://api.savvis.net/vpdc/v1.0/org/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, OrgHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testVDC() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(BrowsingAsyncApi.class, "getVDCInOrg", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("11", "22"));

      assertRequestLineEquals(request, "GET https://api.savvis.net/vpdc/v1.0/org/11/vdc/22 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VDCHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testVDCWhenOrgNull() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(BrowsingAsyncApi.class, "getVDCInOrg", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((String) null, "22"));

      assertRequestLineEquals(request, "GET https://api.savvis.net/vpdc/v1.0/org/1/vdc/22 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VDCHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testNetwork() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(BrowsingAsyncApi.class, "getNetworkInVDC", String.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("11", "22", "VM-Tier01"));

      assertRequestLineEquals(request,
               "GET https://api.savvis.net/vpdc/v1.0/org/11/vdc/22/network/VM-Tier01 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NetworkHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testNetworkWhenOrgNull() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(BrowsingAsyncApi.class, "getNetworkInVDC", String.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((String) null, "22", "VM-Tier01"));

      assertRequestLineEquals(request,
               "GET https://api.savvis.net/vpdc/v1.0/org/1/vdc/22/network/VM-Tier01 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NetworkHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testVMVDC() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(BrowsingAsyncApi.class, "getVMInVDC", String.class, String.class, String.class,
               GetVMOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("11", "22", "33"));

      assertRequestLineEquals(request,
               "GET https://api.savvis.net/vpdc/v1.0/org/11/vdc/22/vApp/33 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VMHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testVM() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(BrowsingAsyncApi.class, "getVM", URI.class, GetVMOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI
               .create("https://api.savvis.net/vpdc/v1.0/org/11/vdc/22/vApp/33")));

      assertRequestLineEquals(request,
               "GET https://api.savvis.net/vpdc/v1.0/org/11/vdc/22/vApp/33 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VMHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testVMWithPowerState() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(BrowsingAsyncApi.class, "getVMInVDC", String.class, String.class, String.class,
               GetVMOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("11", "22", "VM-Tier01", GetVMOptions.Builder
               .withPowerState()));

      assertRequestLineEquals(request,
               "GET https://api.savvis.net/vpdc/v1.0/org/11/vdc/22/vApp/VM-Tier01/withpowerstate HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VMHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testVMWhenOrgNull() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(BrowsingAsyncApi.class, "getVMInVDC", String.class, String.class, String.class,
               GetVMOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((String) null, "22", "VM-Tier01"));

      assertRequestLineEquals(request,
               "GET https://api.savvis.net/vpdc/v1.0/org/1/vdc/22/vApp/VM-Tier01 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VMHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetTask() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(BrowsingAsyncApi.class, "getTask", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("1"));

      assertRequestLineEquals(request, "GET https://api.savvis.net/vpdc/v1.0/task/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListFirewallRules() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(BrowsingAsyncApi.class, "listFirewallRules", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("11", "22"));

      assertRequestLineEquals(request,
               "GET https://api.savvis.net/vpdc/v1.0/org/11/vdc/22/FirewallService HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, FirewallServiceHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }
}
