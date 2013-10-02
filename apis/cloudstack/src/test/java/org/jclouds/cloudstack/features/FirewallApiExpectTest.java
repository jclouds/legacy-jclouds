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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;
import java.util.Set;

import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.FirewallRule;
import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Test the CloudStack FirewallApi
 *
 * @author Andrei Savu
 */
@Test(groups = "unit", testName = "FirewallApiExpectTest")
public class FirewallApiExpectTest extends BaseCloudStackExpectTest<FirewallApi> {

   public void testListFirewallRulesWhenResponseIs2xx() {
      FirewallApi client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=listFirewallRules&listAll=true&" +
                  "apiKey=identity&signature=9%2BtdTXe2uYLzAexPNgrMy5Tq8hE%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listfirewallrulesresponse.json"))
            .build());

      Set<String> CIDRs  = ImmutableSet.of("0.0.0.0/0");
      assertEquals(client.listFirewallRules(),
         ImmutableSet.of(
            FirewallRule.builder().id("2017").protocol(FirewallRule.Protocol.TCP).startPort(30)
               .endPort(35).ipAddressId("2").ipAddress("10.27.27.51").state(FirewallRule.State.ACTIVE)
               .CIDRs(CIDRs).build(),
            FirewallRule.builder().id("2016").protocol(FirewallRule.Protocol.TCP).startPort(22)
               .endPort(22).ipAddressId("2").ipAddress("10.27.27.51").state(FirewallRule.State.ACTIVE)
               .CIDRs(CIDRs).build(),
            FirewallRule.builder().id("10").protocol(FirewallRule.Protocol.TCP).startPort(22)
            .endPort(22).ipAddressId("8").ipAddress("10.27.27.57").state(FirewallRule.State.ACTIVE)
               .CIDRs(CIDRs).build()
         ));
   }

   public void testListFirewallRulesWhenReponseIs404() {
      FirewallApi client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=listFirewallRules&listAll=true&" +
                  "apiKey=identity&signature=9%2BtdTXe2uYLzAexPNgrMy5Tq8hE%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertEquals(client.listFirewallRules(), ImmutableSet.of());
   }

   public void testGetFirewallRuleWhenResponseIs2xx() {
      FirewallApi client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=listFirewallRules&listAll=true&" +
                  "id=2017&apiKey=identity&signature=6coh9Qdwla94TN1Dl008WlhzZUY%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/getfirewallrulesresponse.json"))
            .build());

      assertEquals(client.getFirewallRule("2017"),
         FirewallRule.builder().id("2017").protocol(FirewallRule.Protocol.TCP).startPort(30)
            .endPort(35).ipAddressId("2").ipAddress("10.27.27.51").state(FirewallRule.State.ACTIVE)
            .CIDRs(ImmutableSet.of("0.0.0.0/0")).build()
      );
   }

   public void testGetFirewallRuleWhenResponseIs404() {
      FirewallApi client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=listFirewallRules&listAll=true&" +
                  "id=4&apiKey=identity&signature=rYd8gr7ptdSZlIehBEMQEKsm07Q%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertNull(client.getFirewallRule("4"));
   }

   public void testCreateFirewallRuleForIpAndProtocol() {
      FirewallApi client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=createFirewallRule&" +
                  "ipaddressid=2&protocol=TCP&apiKey=identity&signature=d0MZ/yhQPAaV%2BYQmfZsQtQL2C28%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/createfirewallrulesresponse.json"))
            .build());

      AsyncCreateResponse response = client.createFirewallRuleForIpAndProtocol("2", FirewallRule.Protocol.TCP);
      assertEquals(response.getJobId(), "2036");
      assertEquals(response.getId(), "2017");
   }

   public void testDeleteFirewallRule() {
      FirewallApi client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=deleteFirewallRule&id=2015&apiKey=identity&signature=/T5FAO2yGPctaPmg7TEtIEFW3EU%3D"))
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/deletefirewallrulesresponse.json"))
            .build());

      client.deleteFirewallRule("2015");
   }

   public void testListPortForwardingRulesWhenResponseIs2xx() {
      FirewallApi client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listPortForwardingRules&listAll=true&apiKey=identity&signature=8SXGJZWdcJfVz4V90Pyod12x9dM%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listportforwardingrulesresponse.json"))
            .build());

      Set<String> cidrs = ImmutableSet.of("0.0.0.0/1", "128.0.0.0/1");

      assertEquals(client.listPortForwardingRules(),
         ImmutableSet.<PortForwardingRule>of(
            PortForwardingRule.builder().id("15").privatePort(22).protocol(PortForwardingRule.Protocol.TCP)
               .publicPort(2022).virtualMachineId("3").virtualMachineName("i-3-3-VM").IPAddressId("3")
               .IPAddress("72.52.126.32").state(PortForwardingRule.State.ACTIVE).CIDRs(cidrs).build(),
            PortForwardingRule.builder().id("18").privatePort(22).protocol(PortForwardingRule.Protocol.TCP)
               .publicPort(22).virtualMachineId("89").virtualMachineName("i-3-89-VM").IPAddressId("34")
               .IPAddress("72.52.126.63").state(PortForwardingRule.State.ACTIVE).build())
      );
   }

   public void testListPortForwardingRulesWhenReponseIs404() {
      FirewallApi client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listPortForwardingRules&listAll=true&apiKey=identity&signature=8SXGJZWdcJfVz4V90Pyod12x9dM%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertEquals(client.listPortForwardingRules(), ImmutableSet.of());
   }

   public void testGetPortForwardingRuleWhenResponseIs2xx() {
      FirewallApi client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listPortForwardingRules&listAll=true&id=15&apiKey=identity&signature=JL63p6cJzbb9vaffeV4u60IGlWE%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/getportforwardingrulesresponse.json"))
            .build());

      Set<String> cidrs = ImmutableSet.of("0.0.0.0/1", "128.0.0.0/1");

      assertEquals(client.getPortForwardingRule("15"),
         PortForwardingRule.builder().id("15").privatePort(22).protocol(PortForwardingRule.Protocol.TCP)
            .publicPort(2022).virtualMachineId("3").virtualMachineName("i-3-3-VM").IPAddressId("3")
            .IPAddress("72.52.126.32").state(PortForwardingRule.State.ACTIVE).CIDRs(cidrs).build());
   }

   public void testGetPortForwardingRuleWhenResponseIs404() {
      FirewallApi client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listPortForwardingRules&listAll=true&id=4&apiKey=identity&signature=4blbBVn2%2BZfF8HwoglbmtYoDAjs%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertNull(client.getPortForwardingRule("4"));
   }

   public void testCreatePortForwardingRuleForVirtualMachine() {
      FirewallApi client = requestSendsResponse(
         HttpRequest.builder().method("GET")
                    .endpoint("http://localhost:8080/client/api")
                    .addQueryParam("response", "json")
                    .addQueryParam("command", "createPortForwardingRule")
                    .addQueryParam("ipaddressid", "2")
                    .addQueryParam("protocol", "tcp")
                    .addQueryParam("publicport", "22")
                    .addQueryParam("virtualmachineid", "1234")
                    .addQueryParam("privateport", "22")
                    .addQueryParam("apiKey", "identity")
                    .addQueryParam("signature", "84dtGzQp0G6k3z3Gkc3F/HBNS2Y%3D")
                    .addHeader("Accept", "application/json")
                    .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/createportforwardingrulesresponse.json"))
            .build());

      AsyncCreateResponse response = client.createPortForwardingRuleForVirtualMachine(
         "2", PortForwardingRule.Protocol.TCP, 22, "1234", 22);
      assertEquals(response.getJobId(), "2035");
      assertEquals(response.getId(), "2015");
   }

   public void testDeletePortForwardingRule() {
      FirewallApi client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=deletePortForwardingRule&id=2015&apiKey=identity&signature=2UE7KB3wm5ocmR%2BGMNFKPKfiDo8%3D"))
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/deleteportforwardingrulesresponse.json"))
            .build());

      client.deletePortForwardingRule("2015");
   }

   public void testListEgressFirewallRulesWhenResponseIs2xx() {
      FirewallApi client = requestSendsResponse(
              HttpRequest.builder()
                      .method("GET")
                      .endpoint(
                              URI.create("http://localhost:8080/client/api?response=json&command=listEgressFirewallRules&listAll=true&" +
                                      "apiKey=identity&signature=j3OpRXs7mEwVKs9KIb4ncRKVO9A%3D"))
                      .addHeader("Accept", "application/json")
                      .build(),
              HttpResponse.builder()
                      .statusCode(200)
                      .payload(payloadFromResource("/listegressfirewallrulesresponse.json"))
                      .build());

      Set<String> CIDRs  = ImmutableSet.of("0.0.0.0/0");
      assertEquals(client.listEgressFirewallRules(),
              ImmutableSet.of(
                      FirewallRule.builder().id("2017").protocol(FirewallRule.Protocol.TCP).startPort(30)
                              .endPort(35).ipAddressId("2").ipAddress("10.27.27.51").state(FirewallRule.State.ACTIVE)
                              .CIDRs(CIDRs).build(),
                      FirewallRule.builder().id("2016").protocol(FirewallRule.Protocol.TCP).startPort(22)
                              .endPort(22).ipAddressId("2").ipAddress("10.27.27.51").state(FirewallRule.State.ACTIVE)
                              .CIDRs(CIDRs).build(),
                      FirewallRule.builder().id("10").protocol(FirewallRule.Protocol.TCP).startPort(22)
                              .endPort(22).ipAddressId("8").ipAddress("10.27.27.57").state(FirewallRule.State.ACTIVE)
                              .CIDRs(CIDRs).build()
              ));
   }

   public void testListEgressFirewallRulesWhenReponseIs404() {
      FirewallApi client = requestSendsResponse(
              HttpRequest.builder()
                      .method("GET")
                      .endpoint(
                              URI.create("http://localhost:8080/client/api?response=json&command=listEgressFirewallRules&listAll=true&" +
                                      "apiKey=identity&signature=j3OpRXs7mEwVKs9KIb4ncRKVO9A%3D"))
                      .addHeader("Accept", "application/json")
                      .build(),
              HttpResponse.builder()
                      .statusCode(404)
                      .build());

      assertEquals(client.listEgressFirewallRules(), ImmutableSet.of());
   }

   public void testGetEgressFirewallRuleWhenResponseIs2xx() {
      FirewallApi client = requestSendsResponse(
              HttpRequest.builder()
                      .method("GET")
                      .endpoint(
                              URI.create("http://localhost:8080/client/api?response=json&command=listEgressFirewallRules&listAll=true&" +
                                      "id=2017&apiKey=identity&signature=Hi1K5VA3yd3mk0AmgJ2F6y%2BVzMo%3D"))
                      .addHeader("Accept", "application/json")
                      .build(),
              HttpResponse.builder()
                      .statusCode(200)
                      .payload(payloadFromResource("/getegressfirewallrulesresponse.json"))
                      .build());

      assertEquals(client.getEgressFirewallRule("2017"),
              FirewallRule.builder().id("2017").protocol(FirewallRule.Protocol.TCP).startPort(30)
                      .endPort(35).ipAddressId("2").ipAddress("10.27.27.51").state(FirewallRule.State.ACTIVE)
                      .CIDRs(ImmutableSet.of("0.0.0.0/0")).build()
      );
   }

   public void testGetEgressFirewallRuleWhenResponseIs404() {
      FirewallApi client = requestSendsResponse(
              HttpRequest.builder()
                      .method("GET")
                      .endpoint(
                              URI.create("http://localhost:8080/client/api?response=json&command=listEgressFirewallRules&listAll=true&" +
                                      "id=4&apiKey=identity&signature=dzb5azKxXZsuGrNRJbRHfna7FMo%3D"))
                      .addHeader("Accept", "application/json")
                      .build(),
              HttpResponse.builder()
                      .statusCode(404)
                      .build());

      assertNull(client.getEgressFirewallRule("4"));
   }

   public void testCreateEgressFirewallRuleForIpAndProtocol() {
      FirewallApi client = requestSendsResponse(
              HttpRequest.builder()
                      .method("GET")
                      .endpoint(
                              URI.create("http://localhost:8080/client/api?response=json&command=createEgressFirewallRule&" +
                                      "ipaddressid=2&protocol=TCP&apiKey=identity&signature=%2BlfEJ5zB7lxqRAn0rY0Rcfg9buw%3D"))
                      .addHeader("Accept", "application/json")
                      .build(),
              HttpResponse.builder()
                      .statusCode(200)
                      .payload(payloadFromResource("/createegressfirewallrulesresponse.json"))
                      .build());

      AsyncCreateResponse response = client.createEgressFirewallRuleForIpAndProtocol("2", FirewallRule.Protocol.TCP);
      assertEquals(response.getJobId(), "2036");
      assertEquals(response.getId(), "2017");
   }

   public void testDeleteEgressFirewallRule() {
      FirewallApi client = requestSendsResponse(
              HttpRequest.builder()
                      .method("GET")
                      .endpoint(
                              URI.create("http://localhost:8080/client/api?response=json&" +
                                      "command=deleteEgressFirewallRule&id=2015&apiKey=identity&signature=S119WNmamKwc5d9qvvkIJznXytg%3D"))
                      .build(),
              HttpResponse.builder()
                      .statusCode(200)
                      .payload(payloadFromResource("/deleteegressfirewallrulesresponse.json"))
                      .build());

      client.deleteEgressFirewallRule("2015");
   }
   @Override
   protected FirewallApi clientFrom(CloudStackContext context) {
      return context.getApi().getFirewallApi();
   }
}
