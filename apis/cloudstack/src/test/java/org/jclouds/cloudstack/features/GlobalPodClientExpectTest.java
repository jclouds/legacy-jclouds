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
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.Pod;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;

/**
 * Test the CloudStack PodClient
 *
 * @author Richard Downer
 */
@Test(groups = "unit", testName = "GlobalPodClientExpectTest")
public class GlobalPodClientExpectTest extends BaseCloudStackRestClientExpectTest<GlobalPodClient> {

   public void testListPodsWhenResponseIs2xx() {
      GlobalPodClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listPods&apiKey=identity&signature=asx1px2NQkW4R44%2FDgdozuu9wQg%3D"))
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
         .id(1)
         .name("Dev Pod 1")
         .zoneId(1)
         .zoneName("Dev Zone 1")
         .gateway("10.26.26.254")
         .netmask("255.255.255.0")
         .startIp("10.26.26.50")
         .endIp("10.26.26.100")
         .allocationState(AllocationState.ENABLED)
         .build();
      Pod pod2 = Pod.builder()
         .id(2)
         .name("Dev Pod 2")
         .zoneId(2)
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
                  "command=listPods&apiKey=identity&signature=asx1px2NQkW4R44%2FDgdozuu9wQg%3D"))
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

   @Override
   protected GlobalPodClient clientFrom(CloudStackContext context) {
      return context.getGlobalContext().getApi().getPodClient();
   }
}