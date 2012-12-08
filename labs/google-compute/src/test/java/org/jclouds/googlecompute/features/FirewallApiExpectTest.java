/*
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

package org.jclouds.googlecompute.features;

import org.jclouds.googlecompute.domain.Firewall;
import org.jclouds.googlecompute.domain.FirewallRule;
import org.jclouds.googlecompute.internal.BaseGoogleComputeApiExpectTest;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.googlecompute.parse.ParseFirewallListTest;
import org.jclouds.googlecompute.parse.ParseFirewallTest;
import org.jclouds.googlecompute.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNull;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class FirewallApiExpectTest extends BaseGoogleComputeApiExpectTest {

   public void testGetFirewallResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/google/firewalls/default-allow-internal")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/firewall_get.json")).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getFirewallApi();

      assertEquals(api.get("google", "default-allow-internal"),
              new ParseFirewallTest().expected());
   }

   public void testGetFirewallResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/google/firewalls/default-allow-internal")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getFirewallApi();

      assertNull(api.get("google", "default-allow-internal"));
   }

   public void testInsertFirewallResponseIs2xx() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1beta13/projects/myproject/firewalls")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/firewall_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertFirewallResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation.json")).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert,
              insertFirewallResponse).getFirewallApi();

      assertEquals(api.insert("myproject", Firewall.builder()
              .name("myfw")
              .network("https://www.googleapis.com/compute/v1beta13/projects/myproject/networks/default")
              .addAllowed(FirewallRule.builder()
                      .IPProtocol("tcp")
                      .addPort("22")
                      .addPort("23-24").build())
              .addSourceTag("tag1")
              .addSourceRange("10.0.1.0/32")
              .addTargetTag("tag2")
              .build()), new ParseOperationTest().expected());
   }

   public void testDeleteFirewallResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/firewalls/default-allow-internal")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation.json")).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getFirewallApi();

      assertEquals(api.delete("myproject", "default-allow-internal"),
              new ParseOperationTest().expected());
   }

   public void testDeleteFirewallResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/firewalls/default-allow-internal")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getFirewallApi();

      assertNull(api.delete("myproject", "default-allow-internal"));
   }

   public void testListFirewallsResponseIs2xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/firewalls")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/firewall_list.json")).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getFirewallApi();

      assertEquals(api.list("myproject", ListOptions.NONE).toString(),
              toPagedIterable(new ParseFirewallListTest().expected()).toString());
   }

   public void testListFirewallsResponseIs4xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/firewalls")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getFirewallApi();

      assertTrue(api.list("myproject", ListOptions.NONE).concat().isEmpty());
   }
}
