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

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.savvis.vpdc.domain.FirewallRule;
import org.jclouds.savvis.vpdc.xml.TaskHandler;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
/**
 * Tests annotation parsing of {@code FirewallAsyncApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class FirewallAsyncApiTest extends BaseVPDCAsyncApiTest<FirewallAsyncApi> {

   public void testAddFirewallRule() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(FirewallAsyncApi.class, "addFirewallRule", String.class, String.class,
               FirewallRule.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("11", "22", FirewallRule.builder().firewallType(
               "SERVER_TIER_FIREWALL").isEnabled(true).source("internet").destination("VM Tier01").port("22").protocol(
               "Tcp").policy("allow").description("Server Tier Firewall Rule").isLogged(false).build()));

      assertRequestLineEquals(request,
               "PUT https://api.savvis.net/vpdc/v1.0/org/11/vdc/22/FirewallService HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/firewallService-default.xml")), "application/xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);

      checkFilters(request);
   }

   public void testDeleteFirewallRule() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(FirewallAsyncApi.class, "deleteFirewallRule", String.class, String.class,
               FirewallRule.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("11", "22", FirewallRule.builder().firewallType(
               "SERVER_TIER_FIREWALL").isEnabled(true).source("internet").destination("VM Tier01").port("22").protocol(
               "Tcp").policy("allow").description("Server Tier Firewall Rule").isLogged(false).build()));

      assertRequestLineEquals(request,
               "DELETE https://api.savvis.net/vpdc/v1.0/org/11/vdc/22/FirewallService HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/firewallService-default.xml")), "application/xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }
}
