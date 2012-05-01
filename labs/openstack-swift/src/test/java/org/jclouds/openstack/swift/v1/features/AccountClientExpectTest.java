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
 * Unles required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either expres or implied.  See the License for the
 * specific language governing permisions and limitations
 * under the License.
 */
package org.jclouds.openstack.swift.v1.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.swift.v1.SwiftClient;
import org.jclouds.openstack.swift.v1.domain.AccountMetadata;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftClientExpectTest;
import org.jclouds.openstack.swift.v1.parse.ParseContainerListTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AccountClientExpectTest")
public class AccountClientExpectTest extends BaseSwiftClientExpectTest {
   
   public void testGetAccountMetadataWhenResponseIs2xx() throws Exception {

      HttpRequest getAccountMetadata = HttpRequest
            .builder()
            .method("HEAD")
            .endpoint(URI.create("https://objects.jclouds.org/v1.0/40806637803162/"))
            .headers(
                  ImmutableMultimap.<String, String> builder()
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listContainersResponse = HttpResponse.builder().statusCode(204)
            .headers(ImmutableMultimap.of(
                     "X-Account-Container-Count", "3",
                     "X-Account-Bytes-Used", "323479")).build();

      SwiftClient clientWhenContainersExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, getAccountMetadata, listContainersResponse);

      assertEquals(
            clientWhenContainersExist.getAccountClientForRegion("region-a.geo-1").getAccountMetadata(),
            AccountMetadata.builder().containerCount(3).bytesUsed(323479).build());
   }
   
   public void testListContainersWhenResponseIs2xx() throws Exception {

      HttpRequest listContainers = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://objects.jclouds.org/v1.0/40806637803162/?format=json"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listContainersResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/container_list.json")).build();

      SwiftClient clientWhenContainersExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, listContainers, listContainersResponse);

      assertEquals(
            clientWhenContainersExist.getAccountClientForRegion("region-a.geo-1").listContainers()
                  .toString(), new ParseContainerListTest().expected().toString());
   }

   public void testListContainersWhenResponseIs404() throws Exception {
      HttpRequest listContainers = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://objects.jclouds.org/v1.0/40806637803162/?format=json"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listContainersResponse = HttpResponse.builder().statusCode(404).build();

      SwiftClient clientWhenNoContainersExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, listContainers, listContainersResponse);

      assertEquals(clientWhenNoContainersExist.getAccountClientForRegion("region-a.geo-1").listContainers(), ImmutableSet.of());

   }

}
