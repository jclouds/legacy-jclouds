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
package org.jclouds.hpcloud.compute;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * This test ensures that the wiring in {@link HPCloudComputeProviderMetadata} is correct.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "HPCloudComputeProviderMetadataExpectTest")
public class HPCloudComputeProviderMetadataExpectTest extends BaseNovaApiExpectTest {

   public HPCloudComputeProviderMetadataExpectTest() {
      this.provider = "hpcloud-compute";
      this.identity = "tenant:username";
      this.credential = "password";
   }

   public void testCanGetConfiguredZones() {
      
      HttpRequest authenticate = HttpRequest.builder().method("POST")
            .endpoint("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/tokens")
            .addHeader("Accept", "application/json")
            .payload(payloadFromStringWithContentType(
                     "{\"auth\":{\"passwordCredentials\":{\"username\":\"username\",\"password\":\"password\"},\"tenantName\":\"tenant\"}}"
                     , "application/json")).build();
      

      HttpResponse authenticationResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/access_hpcloud.json", "application/json")).build();

      NovaApi whenNovaRegionExists = requestSendsResponse(authenticate, authenticationResponse);

      assertEquals(whenNovaRegionExists.getConfiguredZones(), ImmutableSet.of("az-3.region-a.geo-1", "az-2.region-a.geo-1", "az-1.region-a.geo-1"));

   }

}
