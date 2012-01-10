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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.CloudStackGlobalClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.ConfigurationEntry;
import org.jclouds.cloudstack.domain.FirewallRule;
import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Test the CloudStack GlobalConfigurationClient
 *
 * @author Andrei Savu
 */
@Test(groups = "unit", testName = "GlobalConfigurationClientExpectTest")
public class GlobalConfigurationClientExpectTest extends BaseRestClientExpectTest<CloudStackGlobalClient> {

   public GlobalConfigurationClientExpectTest() {
      provider = "cloudstack";
   }

   @Test(enabled = false)
   public void testListConfigurationEntriesWhenResponseIs2xx() {
      GlobalConfigurationClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=listFirewallRules&" +
                  "apiKey=identity&signature=MktZKKH3USVKiC9SlYTSHMCaCcg%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listconfigurationsresponse.json"))
            .build())
         .getConfigurationClient();

      assertEquals(client.listConfigurationEntries(),
         ImmutableSet.of(
            ConfigurationEntry.builder().category("Advanced").name("account.cleanup.interval").value("86400")
               .description("The interval (in seconds) between cleanup for removed accounts").build(),
            ConfigurationEntry.builder().category("Advanced").name("agent.lb.enabled").value("true")
               .description("If agent load balancing enabled in cluster setup").build()
         ));
   }
}