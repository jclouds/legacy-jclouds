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
import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.Pod;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.cloudstack.options.CreatePodOptions;
import org.jclouds.cloudstack.options.UpdatePodOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Test the CloudStack PodClient
 *
 * @author Richard Downer
 */
@Test(groups = "unit", testName = "GlobalPodClientExpectTest")
public class GlobalPodClientExpectTest extends BaseCloudStackExpectTest<GlobalPodClient> {

   public void testListPodsWhenResponseIs2xx() {
      GlobalPodClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listPods&listAll=true&apiKey=identity&signature=MuowIOuZqOpKTPVQOfrDZEmpepw%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listpodsresponse.json"))
            .build());

      Pod pod1 = Pod.builder()
         .id("1")
         .name("Dev Pod 1")
         .zoneId("1")
         .zoneName("Dev Zone 1")
         .gateway("10.26.26.254")
         .netmask("255.255.255.0")
         .startIp("10.26.26.50")
         .endIp("10.26.26.100")
         .allocationState(AllocationState.ENABLED)
         .build();
      Pod pod2 = Pod.builder()
         .id("2")
         .name("Dev Pod 2")
         .zoneId("2")
         .zoneName("Dev Zone 2")
         .gateway("10.22.22.254")
         .netmask("255.255.255.0")
         .startIp("10.22.22.25")
         .endIp("10.22.22.50")
         .allocationState(AllocationState.ENABLED)
         .build();

      assertEquals(client.listPods(), ImmutableSet.of(pod1, pod2));
   }

   public void testListPodsWhenResponseIs404() {
      GlobalPodClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listPods&listAll=true&apiKey=identity&signature=MuowIOuZqOpKTPVQOfrDZEmpepw%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertEquals(client.listPods(), ImmutableSet.of());
   }

   HttpRequest createPod = HttpRequest.builder().method("GET")
                                      .endpoint("http://localhost:8080/client/api")
                                      .addQueryParam("response", "json")
                                      .addQueryParam("command", "createPod")
                                      .addQueryParam("name", "richard-pod")
                                      .addQueryParam("zoneid", "10")
                                      .addQueryParam("startip", "172.20.0.1")
                                      .addQueryParam("endip", "172.20.0.250")
                                      .addQueryParam("gateway", "172.20.0.254")
                                      .addQueryParam("netmask", "255.255.255.0")
                                      .addQueryParam("allocationstate", "Enabled")
                                      .addQueryParam("apiKey", "identity")
                                      .addQueryParam("signature", "fwsoQ77BmNQWfuqv4nVlPcKvKbU=")
                                      .addHeader("Accept", "application/json").build();

   public void testCreatePodWhenResponseIs2xx() {
      GlobalPodClient client = requestSendsResponse(createPod,
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/createpodresponse.json"))
            .build());

      Pod expected = Pod.builder()
         .id("6")
         .name("richard-pod")
         .zoneId("10")
         .zoneName("richard-zone")
         .gateway("172.20.0.254")
         .netmask("255.255.255.0")
         .startIp("172.20.0.1")
         .endIp("172.20.0.250")
         .allocationState(AllocationState.ENABLED)
         .build();

      Pod actual = client.createPod("richard-pod", "10", "172.20.0.1", "172.20.0.250", "172.20.0.254", "255.255.255.0",
         CreatePodOptions.Builder.allocationState(AllocationState.ENABLED));

      assertEquals(actual, expected);
   }

   public void testUpdatePodWhenResponseIs2xx() {
      GlobalPodClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=updatePod&id=7&netmask=255.255.255.128&name=richard-updatepod&startip=172.21.0.129&endip=172.21.0.250&gateway=172.21.0.254&allocationstate=Disabled&apiKey=identity&signature=QpdbRyyF/xJ78ioJWhPKXEWhthY%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/updatepodresponse.json"))
            .build());

      Pod expected = Pod.builder()
         .id("7")
         .name("richard-updatedpod")
         .zoneId("11")
         .zoneName("richard-zone")
         .gateway("172.21.0.254")
         .netmask("255.255.255.128")
         .startIp("172.21.0.129")
         .endIp("172.21.0.250")
         .allocationState(AllocationState.DISABLED)
         .build();

      Pod actual = client.updatePod("7", UpdatePodOptions.Builder
         .netmask("255.255.255.128")
         .name("richard-updatepod")
         .startIp("172.21.0.129")
         .endIp("172.21.0.250")
         .gateway("172.21.0.254")
         .allocationState(AllocationState.DISABLED)
      );

      assertEquals(actual, expected);
   }

   public void testDeletePodWhenResponseIs2xx() {
      GlobalPodClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=deletePod&id=3&apiKey=identity&signature=rm4ItuAL1Ztnj%2BHFFvBFzvHAIog%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .build());

      client.deletePod("3");
   }

   @Override
   protected GlobalPodClient clientFrom(CloudStackContext context) {
      return context.getGlobalContext().getApi().getPodClient();
   }
}
