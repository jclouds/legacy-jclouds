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
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.FirewallRule;
import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Test the CloudStack FirewallClient
 *
 * @author Andrei Savu
 */
@Test(groups = "unit", testName = "FirewallClientExpectTest")
public class FirewallClientExpectTest extends BaseCloudStackRestClientExpectTest {

   public void testListFirewallRulesWhenResponseIs2xx() {
      FirewallClient client = requestSendsResponse(
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
            .payload(payloadFromResource("/listfirewallrulesresponse.json"))
            .build())
         .getFirewallClient();

      Set<String> CIDRs  = ImmutableSet.of("0.0.0.0/0");
      assertEquals(client.listFirewallRules(),
         ImmutableSet.of(
            FirewallRule.builder().id(2017).protocol(FirewallRule.Protocol.TCP).startPort(30)
               .endPort(35).ipAddressId(2).ipAddress("10.27.27.51").state(FirewallRule.State.ACTIVE)
               .CIDRs(CIDRs).build(),
            FirewallRule.builder().id(2016).protocol(FirewallRule.Protocol.TCP).startPort(22)
               .endPort(22).ipAddressId(2).ipAddress("10.27.27.51").state(FirewallRule.State.ACTIVE)
               .CIDRs(CIDRs).build(),
            FirewallRule.builder().id(10).protocol(FirewallRule.Protocol.TCP).startPort(22)
               .endPort(22).ipAddressId(8).ipAddress("10.27.27.57").state(FirewallRule.State.ACTIVE)
               .CIDRs(CIDRs).build()
         ));
   }

   public void testListFirewallRulesWhenReponseIs404() {
      FirewallClient client = requestSendsResponse(
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
            .statusCode(404)
            .build())
         .getFirewallClient();

      assertEquals(client.listFirewallRules(), ImmutableSet.of());
   }

   public void testGetFirewallRuleWhenResponseIs2xx() {
      FirewallClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=listFirewallRules&" +
                  "id=2017&apiKey=identity&signature=0r5iL%2Bzix9rmD07lJIOhY68mYY0%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/getfirewallrulesresponse.json"))
            .build())
         .getFirewallClient();

      assertEquals(client.getFirewallRule(2017),
         FirewallRule.builder().id(2017).protocol(FirewallRule.Protocol.TCP).startPort(30)
            .endPort(35).ipAddressId(2).ipAddress("10.27.27.51").state(FirewallRule.State.ACTIVE)
            .CIDRs(ImmutableSet.of("0.0.0.0/0")).build()
      );
   }

   public void testGetFirewallRuleWhenResponseIs404() {
      FirewallClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=listFirewallRules&" +
                  "id=4&apiKey=identity&signature=PPX5U9kmaS116SgG4Ihf8xK%2BcSE%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build())
         .getFirewallClient();

      assertNull(client.getFirewallRule(4));
   }

   public void testCreateFirewallRuleForIpAndProtocol() {
      FirewallClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=createFirewallRule&" +
                  "ipaddressid=2&protocol=TCP&apiKey=identity&signature=d0MZ%2FyhQPAaV%2BYQmfZsQtQL2C28%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/createfirewallrulesresponse.json"))
            .build())
         .getFirewallClient();

      AsyncCreateResponse response = client.createFirewallRuleForIpAndProtocol(2, FirewallRule.Protocol.TCP);
      assertEquals(response.getJobId(), 2036);
      assertEquals(response.getId(), 2017);
   }

   public void testDeleteFirewallRule() {
      FirewallClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=deleteFirewallRule&id=2015&apiKey=identity&signature=%2FT5FAO2yGPctaPmg7TEtIEFW3EU%3D"))
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/deletefirewallrulesresponse.json"))
            .build())
         .getFirewallClient();

      client.deleteFirewallRule(2015);
   }

   public void testListPortForwardingRulesWhenResponseIs2xx() {
      FirewallClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listPortForwardingRules&apiKey=identity&signature=YFBu1VOSkiDKxm0K42sIXJWy%2BBo%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listportforwardingrulesresponse.json"))
            .build())
         .getFirewallClient();

      Set<String> cidrs = ImmutableSet.of("0.0.0.0/1", "128.0.0.0/1");

      assertEquals(client.listPortForwardingRules(),
         ImmutableSet.<PortForwardingRule>of(
            PortForwardingRule.builder().id(15).privatePort(22).protocol(PortForwardingRule.Protocol.TCP)
               .publicPort(2022).virtualMachineId(3).virtualMachineName("i-3-3-VM").IPAddressId(3)
               .IPAddress("72.52.126.32").state(PortForwardingRule.State.ACTIVE).CIDRs(cidrs).build(),
            PortForwardingRule.builder().id(18).privatePort(22).protocol(PortForwardingRule.Protocol.TCP)
               .publicPort(22).virtualMachineId(89).virtualMachineName("i-3-89-VM").IPAddressId(34)
               .IPAddress("72.52.126.63").state(PortForwardingRule.State.ACTIVE).build())
      );
   }

   public void testListPortForwardingRulesWhenReponseIs404() {
      FirewallClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listPortForwardingRules&apiKey=identity&signature=YFBu1VOSkiDKxm0K42sIXJWy%2BBo%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build())
         .getFirewallClient();

      assertEquals(client.listPortForwardingRules(), ImmutableSet.of());
   }

   public void testGetPortForwardingRuleWhenResponseIs2xx() {
      FirewallClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listPortForwardingRules&id=15&apiKey=identity&signature=ABJsciF4n2tXaiyUmEvc3oYh9MA%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/getportforwardingrulesresponse.json"))
            .build())
         .getFirewallClient();

      Set<String> cidrs = ImmutableSet.of("0.0.0.0/1", "128.0.0.0/1");

      assertEquals(client.getPortForwardingRule(15),
         PortForwardingRule.builder().id(15).privatePort(22).protocol(PortForwardingRule.Protocol.TCP)
            .publicPort(2022).virtualMachineId(3).virtualMachineName("i-3-3-VM").IPAddressId(3)
            .IPAddress("72.52.126.32").state(PortForwardingRule.State.ACTIVE).CIDRs(cidrs).build());
   }

   public void testGetPortForwardingRuleWhenResponseIs404() {
      FirewallClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listPortForwardingRules&id=4&apiKey=identity&signature=CTOmmIOGIiZx0YATqh%2FFk0zIplw%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build())
         .getFirewallClient();

      assertNull(client.getPortForwardingRule(4));
   }

   public void testCreatePortForwardingRuleForVirtualMachine() {
      FirewallClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=createPortForwardingRule&" +
                  "ipaddressid=2&publicport=22&protocol=tcp&virtualmachineid=1234&privateport=22&" +
                  "apiKey=identity&signature=84dtGzQp0G6k3z3Gkc3F%2FHBNS2Y%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/createportforwardingrulesresponse.json"))
            .build())
         .getFirewallClient();

      AsyncCreateResponse response = client.createPortForwardingRuleForVirtualMachine(
         2, PortForwardingRule.Protocol.TCP, 22, 1234, 22);
      assertEquals(response.getJobId(), 2035);
      assertEquals(response.getId(), 2015);
   }

   public void testDeletePortForwardingRule() {
      FirewallClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=deletePortForwardingRule&id=2015&apiKey=identity&signature=2UE7KB3wm5ocmR%2BGMNFKPKfiDo8%3D"))
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/deleteportforwardingrulesresponse.json"))
            .build())
         .getFirewallClient();

      client.deletePortForwardingRule(2015);
   }
}