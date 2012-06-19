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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseKeyPairListTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseKeyPairTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code KeyPairAsyncClient}
 * 
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "KeyPairClientExpectTest")
public class KeyPairClientExpectTest extends BaseNovaClientExpectTest {

   public void testListKeyPairsWhenResponseIs2xx() throws Exception {
      HttpRequest listKeyPairs = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-keypairs"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listKeyPairsResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/keypair_list.json")).build();

      NovaClient clientWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, listKeyPairs, listKeyPairsResponse);

      assertEquals(clientWhenServersExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(clientWhenServersExist.getKeyPairExtensionForZone("az-1.region-a.geo-1").get().listKeyPairs().toString(),
            new ParseKeyPairListTest().expected().toString());
   }

   public void testListKeyPairsWhenResponseIs404() throws Exception {
      HttpRequest listKeyPairs = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-keypairs"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listKeyPairsResponse = HttpResponse.builder().statusCode(404).build();

      NovaClient clientWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, listKeyPairs, listKeyPairsResponse);

      assertTrue(clientWhenNoServersExist.getKeyPairExtensionForZone("az-1.region-a.geo-1").get().listKeyPairs().isEmpty());

   }

   public void testCreateKeyPair() throws Exception {
      HttpRequest createKeyPair = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-keypairs"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build())
            .payload(payloadFromStringWithContentType("{\"keypair\":{\"name\":\"testkeypair\"}}", "application/json"))
            .build();

      HttpResponse createKeyPairResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/keypair_created.json")).build();

      NovaClient clientWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, createKeyPair, createKeyPairResponse);

      assertEquals(clientWhenServersExist.getKeyPairExtensionForZone("az-1.region-a.geo-1").get().createKeyPair("testkeypair")
            .toString(), new ParseKeyPairTest().expected().toString());

   }

   public void testCreateKeyPairWithPublicKey() throws Exception {
      HttpRequest createKeyPair = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-keypairs"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build())
            .payload(
                  payloadFromStringWithContentType(
                        "{\"keypair\":{\"name\":\"testkeypair\",\"public_key\":\"ssh-rsa AAAXB3NzaC1yc2EAAAADAQABAAAAgQDFNyGjgs6c9akgmZ2ou/fJf7Pdrc23hC95/gM/33OrG4GZABACE4DTioa/PGN+7rHv9YUavUCtXrWayhGniKq/wCuI5fo5TO4AmDNv7/sCGHIHFumADSIoLx0vFhGJIetXEWxL9r0lfFC7//6yZM2W3KcGjbMtlPXqBT9K9PzdyQ== nova@nv-aw2az1-api0001\n\"}}",
                        "application/json")).build();

      HttpResponse createKeyPairResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/keypair_created.json")).build();

      NovaClient clientWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, createKeyPair, createKeyPairResponse);

      assertEquals(
            clientWhenServersExist
                  .getKeyPairExtensionForZone("az-1.region-a.geo-1")
                  .get()
                  .createKeyPairWithPublicKey(
                        "testkeypair",
                        "ssh-rsa AAAXB3NzaC1yc2EAAAADAQABAAAAgQDFNyGjgs6c9akgmZ2ou/fJf7Pdrc23hC95/gM/33OrG4GZABACE4DTioa/PGN+7rHv9YUavUCtXrWayhGniKq/wCuI5fo5TO4AmDNv7/sCGHIHFumADSIoLx0vFhGJIetXEWxL9r0lfFC7//6yZM2W3KcGjbMtlPXqBT9K9PzdyQ== nova@nv-aw2az1-api0001\n")
                  .toString(), new ParseKeyPairTest().expected().toString());
   }

   public void testDeleteKeyPair() throws Exception {
      HttpRequest deleteKeyPair = HttpRequest
            .builder()
            .method("DELETE")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-keypairs/testkeypair"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "*/*").put("X-Auth-Token", authToken)
                        .build()).build();

      HttpResponse deleteKeyPairResponse = HttpResponse.builder().statusCode(202).build();

      NovaClient clientWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, deleteKeyPair, deleteKeyPairResponse);

      assertTrue(clientWhenServersExist.getKeyPairExtensionForZone("az-1.region-a.geo-1").get().deleteKeyPair("testkeypair"));
   }
}
