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

import java.net.URI;

import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.VlanIPRange;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.cloudstack.options.CreateVlanIPRangeOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Test the CloudStack VlanClient
 *
 * @author Richard Downer
 */
@Test(groups = "unit", testName = "GlobalVlanClientExpectTest")
public class GlobalVlanClientExpectTest extends BaseCloudStackExpectTest<GlobalVlanClient> {

   public void testListVlanIpRangesWhenResponseIs2xx() {
      GlobalVlanClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listVlanIpRanges&listAll=true&apiKey=identity&signature=xPwCeAcMp9kDGbD5oPgztLtSdnU%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listvlaniprangesresponse.json"))
            .build());

      VlanIPRange range1 = VlanIPRange.builder()
         .id("1")
         .forVirtualNetwork(true)
         .zoneId("1")
         .vlan("127")
         .account("system")
         .domainId("1")
         .domain("ROOT")
         .gateway("10.27.27.254")
         .netmask("255.255.255.0")
         .startIP("10.27.27.50")
         .endIP("10.27.27.100")
         .networkId("200")
         .build();

      VlanIPRange range2 = VlanIPRange.builder()
         .id("2")
         .forVirtualNetwork(false)
         .zoneId("2")
         .vlan("untagged")
         .account("system")
         .domainId("1")
         .domain("ROOT")
         .podId("2")
         .podName("Dev Pod 2")
         .gateway("10.22.22.254")
         .netmask("255.255.255.0")
         .startIP("10.22.22.51")
         .endIP("10.22.22.100")
         .networkId("209")
         .build();

      assertEquals(client.listVlanIPRanges(), ImmutableSet.of(range1, range2));
   }

   public void testListVlanIpRangesWhenResponseIs404() {
      GlobalVlanClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listVlanIpRanges&listAll=true&apiKey=identity&signature=xPwCeAcMp9kDGbD5oPgztLtSdnU%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertEquals(client.listVlanIPRanges(), ImmutableSet.of());
   }

   public void testCreateVlanIpRangeWhenResponseIs2xx() {
      GlobalVlanClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=createVlanIpRange&startip=10.22.22.51&endip=10.22.22.100&forvirtualnetwork=false&zoneid=2&vlan=untagged&account=system&domainid=1&podid=2&gateway=10.22.22.254&netmask=255.255.255.0&networkid=209&apiKey=identity&signature=XgDjPYAQNLMVCuSMGRA6QjV8mOY%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/createvlaniprangeresponse.json"))
            .build());

      VlanIPRange actual = client.createVlanIPRange("10.22.22.51", "10.22.22.100", new CreateVlanIPRangeOptions()
         .forVirtualNetwork(false)
         .zoneId("2")
         .vlan("untagged")
         .accountInDomain("system", "1")
         .podId("2")
         .gateway("10.22.22.254")
         .netmask("255.255.255.0")
         .networkId("209"));

      VlanIPRange expected = VlanIPRange.builder()
         .id("2")
         .forVirtualNetwork(false)
         .zoneId("2")
         .vlan("untagged")
         .account("system")
         .domainId("1")
         .domain("ROOT")
         .podId("2")
         .podName("Dev Pod 2")
         .gateway("10.22.22.254")
         .netmask("255.255.255.0")
         .startIP("10.22.22.51")
         .endIP("10.22.22.100")
         .networkId("209")
         .build();

      assertEquals(actual, expected);
   }

   public void testDeleteVlanIpRangeWhenResponseIs2xx() {
      GlobalVlanClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=deleteVlanIpRange&id=1&apiKey=identity&signature=tTBbpdCndgHXdR397fbbJaN1RZU%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/createvlaniprangeresponse.json"))
            .build());

      client.deleteVlanIPRange("1");
   }

   @Override
   protected GlobalVlanClient clientFrom(CloudStackContext context) {
      return context.getGlobalContext().getApi().getVlanClient();
   }
}
