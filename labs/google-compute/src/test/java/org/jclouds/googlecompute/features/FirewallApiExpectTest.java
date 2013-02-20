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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import org.jclouds.googlecompute.domain.Firewall;
import org.jclouds.googlecompute.internal.BaseGoogleComputeApiExpectTest;
import org.jclouds.googlecompute.options.FirewallOptions;
import org.jclouds.googlecompute.parse.ParseFirewallListTest;
import org.jclouds.googlecompute.parse.ParseFirewallTest;
import org.jclouds.googlecompute.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Iterables.transform;
import static java.lang.String.format;
import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_SCOPE;
import static org.jclouds.googlecompute.domain.Firewall.Rule.IPProtocol;
import static org.jclouds.io.Payloads.newStringPayload;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNull;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class FirewallApiExpectTest extends BaseGoogleComputeApiExpectTest {

   public static final HttpRequest GET_FIREWALL_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint("https://www.googleapis" +
                   ".com/compute/v1beta13/projects/myproject/firewalls/jclouds-test")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static HttpResponse GET_FIREWALL_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/firewall_get.json")).build();

   public void testGetFirewallResponseIs2xx() throws Exception {

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_FIREWALL_REQUEST, GET_FIREWALL_RESPONSE).getFirewallApiForProject("myproject");

      assertEquals(api.get("jclouds-test"), new ParseFirewallTest().expected());
   }


   public static Payload firewallPayloadFirewallOfName(String firewallName,
                                                       String networkName,
                                                       Set<String> sourceRanges,
                                                       Set<String> sourceTags,
                                                       Set<String> targetTags,
                                                       Set<String> portRanges) throws IOException {
      Function<String, String> addQuotes = new Function<String, String>() {
         @Override
         public String apply(String input) {
            return "\"" + input + "\"";
         }
      };

      String ports = on(",").skipNulls().join(transform(portRanges, addQuotes));

      Payload payload = newStringPayload(
              format(toStringAndClose(FirewallApiExpectTest.class.getResourceAsStream("/firewall_insert.json")),
                      firewallName,
                      networkName,
                      on(",").skipNulls().join(transform(sourceRanges, addQuotes)),
                      on(",").skipNulls().join(transform(sourceTags, addQuotes)),
                      on(",").skipNulls().join(transform(targetTags, addQuotes)),
                      ports,
                      ports));
      payload.getContentMetadata().setContentType(MediaType.APPLICATION_JSON);
      return payload;
   }


   public void testGetFirewallResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/firewalls/jclouds-test")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getFirewallApiForProject("myproject");

      assertNull(api.get("jclouds-test"));
   }

   public void testInsertFirewallResponseIs2xx() throws IOException {

      HttpRequest request = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1beta13/projects/myproject/firewalls")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(firewallPayloadFirewallOfName(
                      "myfw",
                      "default",
                      ImmutableSet.<String>of("10.0.1.0/32"),
                      ImmutableSet.<String>of("tag1"),
                      ImmutableSet.<String>of("tag2"),
                      ImmutableSet.<String>of("22", "23-24")))
              .build();

      HttpResponse insertFirewallResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation.json")).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, insertFirewallResponse).getFirewallApiForProject("myproject");

      assertEquals(api.createInNetwork("myfw", URI.create("https://www.googleapis" +
              ".com/compute/v1beta13/projects/myproject/networks/default"),
              new FirewallOptions()
                      .addAllowedRule(Firewall.Rule.builder()
                              .IPProtocol(IPProtocol.TCP)
                              .addPort(22)
                              .addPortRange(23, 24).build())
                      .addSourceTag("tag1")
                      .addSourceRange("10.0.1.0/32")
                      .addTargetTag("tag2")), new ParseOperationTest().expected());

   }

   public void testUpdateFirewallResponseIs2xx() throws IOException {
      HttpRequest update = HttpRequest
              .builder()
              .method("PUT")
              .endpoint("https://www.googleapis.com/compute/v1beta13/projects/myproject/firewalls/myfw")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(firewallPayloadFirewallOfName(
                      "myfw",
                      "default",
                      ImmutableSet.<String>of("10.0.1.0/32"),
                      ImmutableSet.<String>of("tag1"),
                      ImmutableSet.<String>of("tag2"),
                      ImmutableSet.<String>of("22", "23-24")))
              .build();

      HttpResponse updateFirewallResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation.json")).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, update,
              updateFirewallResponse).getFirewallApiForProject("myproject");

      assertEquals(api.update("myfw",
              new FirewallOptions()
                      .name("myfw")
                      .network(URI.create("https://www.googleapis" +
                              ".com/compute/v1beta13/projects/myproject/networks/default"))
                      .addAllowedRule(Firewall.Rule.builder()
                              .IPProtocol(IPProtocol.TCP)
                              .addPort(22)
                              .addPortRange(23, 24).build())
                      .addSourceTag("tag1")
                      .addSourceRange("10.0.1.0/32")
                      .addTargetTag("tag2")), new ParseOperationTest().expected());
   }

   public void testPatchFirewallResponseIs2xx() throws IOException {
      HttpRequest update = HttpRequest
              .builder()
              .method("PATCH")
              .endpoint("https://www.googleapis.com/compute/v1beta13/projects/myproject/firewalls/myfw")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(firewallPayloadFirewallOfName(
                      "myfw",
                      "default",
                      ImmutableSet.<String>of("10.0.1.0/32"),
                      ImmutableSet.<String>of("tag1"),
                      ImmutableSet.<String>of("tag2"),
                      ImmutableSet.<String>of("22", "23-24")))
              .build();

      HttpResponse updateFirewallResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation.json")).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, update,
              updateFirewallResponse).getFirewallApiForProject("myproject");

      assertEquals(api.patch("myfw",
              new FirewallOptions()
                      .name("myfw")
                      .network(URI.create("https://www.googleapis" +
                              ".com/compute/v1beta13/projects/myproject/networks/default"))
                      .addAllowedRule(Firewall.Rule.builder()
                              .IPProtocol(IPProtocol.TCP)
                              .addPort(22)
                              .addPortRange(23, 24).build())
                      .addSourceTag("tag1")
                      .addSourceRange("10.0.1.0/32")
                      .addTargetTag("tag2")), new ParseOperationTest().expected());
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
              TOKEN_RESPONSE, delete, deleteResponse).getFirewallApiForProject("myproject");

      assertEquals(api.delete("default-allow-internal"),
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
              TOKEN_RESPONSE, delete, deleteResponse).getFirewallApiForProject("myproject");

      assertNull(api.delete("default-allow-internal"));
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
              TOKEN_RESPONSE, list, operationResponse).getFirewallApiForProject("myproject");

      assertEquals(api.listFirstPage().toString(),
              new ParseFirewallListTest().expected().toString());
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
              TOKEN_RESPONSE, list, operationResponse).getFirewallApiForProject("myproject");

      assertTrue(api.list().concat().isEmpty());
   }
}
