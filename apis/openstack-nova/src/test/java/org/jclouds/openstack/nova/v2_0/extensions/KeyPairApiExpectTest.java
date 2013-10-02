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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseKeyPairListTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseKeyPairTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code KeyPairAsyncApi}
 * 
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "KeyPairApiExpectTest")
public class KeyPairApiExpectTest extends BaseNovaApiExpectTest {

   public void testListKeyPairsWhenResponseIs2xx() throws Exception {
      HttpRequest list = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-keypairs")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/keypair_list.json")).build();

      NovaApi apiWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, list, listResponse);

      assertEquals(apiWhenServersExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      // NOTE this required a change to the KeyPair domain object toString method
      assertEquals(apiWhenServersExist.getKeyPairExtensionForZone("az-1.region-a.geo-1").get().list().toString(),
            new ParseKeyPairListTest().expected().toString());
   }

   public void testListKeyPairsWhenResponseIs404() throws Exception {
      HttpRequest list = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-keypairs")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, list, listResponse);

      assertTrue(apiWhenNoServersExist.getKeyPairExtensionForZone("az-1.region-a.geo-1").get().list().isEmpty());

   }

   public void testCreateKeyPair() throws Exception {
      HttpRequest create = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-keypairs")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType("{\"keypair\":{\"name\":\"testkeypair\"}}", "application/json"))
            .build();

      HttpResponse createResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/keypair_created.json")).build();

      NovaApi apiWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, create, createResponse);

      assertEquals(apiWhenServersExist.getKeyPairExtensionForZone("az-1.region-a.geo-1").get().create("testkeypair")
            .toString(), new ParseKeyPairTest().expected().toString());

   }

   public void testCreateKeyPairWithPublicKey() throws Exception {
      HttpRequest create = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-keypairs")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(
                  payloadFromStringWithContentType(
                        "{\"keypair\":{\"name\":\"testkeypair\",\"public_key\":\"ssh-rsa AAAXB3NzaC1yc2EAAAADAQABAAAAgQDFNyGjgs6c9akgmZ2ou/fJf7Pdrc23hC95/gM/33OrG4GZABACE4DTioa/PGN+7rHv9YUavUCtXrWayhGniKq/wCuI5fo5TO4AmDNv7/sCGHIHFumADSIoLx0vFhGJIetXEWxL9r0lfFC7//6yZM2W3KcGjbMtlPXqBT9K9PzdyQ== nova@nv-aw2az1-api0001\n\"}}",
                        "application/json")).build();

      HttpResponse createResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/keypair_created.json")).build();

      NovaApi apiWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, create, createResponse);

      assertEquals(
            apiWhenServersExist
                  .getKeyPairExtensionForZone("az-1.region-a.geo-1")
                  .get()
                  .createWithPublicKey(
                        "testkeypair",
                        "ssh-rsa AAAXB3NzaC1yc2EAAAADAQABAAAAgQDFNyGjgs6c9akgmZ2ou/fJf7Pdrc23hC95/gM/33OrG4GZABACE4DTioa/PGN+7rHv9YUavUCtXrWayhGniKq/wCuI5fo5TO4AmDNv7/sCGHIHFumADSIoLx0vFhGJIetXEWxL9r0lfFC7//6yZM2W3KcGjbMtlPXqBT9K9PzdyQ== nova@nv-aw2az1-api0001\n")
                  .toString(), new ParseKeyPairTest().expected().toString());
   }

   public void testDeleteKeyPair() throws Exception {
      HttpRequest delete = HttpRequest
            .builder()
            .method("DELETE")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-keypairs/testkeypair")
            .addHeader("Accept", "*/*")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(202).build();

      NovaApi apiWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, delete, deleteResponse);

      assertTrue(apiWhenServersExist.getKeyPairExtensionForZone("az-1.region-a.geo-1").get().delete("testkeypair"));
   }
}
