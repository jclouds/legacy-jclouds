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
package org.jclouds.trystack.nova;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * This test ensures that the wiring in {@link TryStackNovaProviderMetadata} is correct.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "TryStackNovaProviderMetadataExpectTest")
public class TryStackNovaProviderMetadataExpectTest extends BaseNovaApiExpectTest {

   public TryStackNovaProviderMetadataExpectTest() {
      this.provider = "trystack-nova";
      this.identity = "demo:demo";
      this.credential = "password";
   }

   public void testCanGetConfiguredZones() {
      
      HttpRequest authenticate = HttpRequest.builder().method("POST")
            .endpoint("https://nova-api.trystack.org:5443/v2.0/tokens")
            .addHeader("Accept", "application/json")
            .payload(payloadFromStringWithContentType(
                     "{\"auth\":{\"passwordCredentials\":{\"username\":\"demo\",\"password\":\"password\"},\"tenantName\":\"demo\"}}"
                     , "application/json")).build();
      

      HttpResponse authenticationResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/access_trystack.json", "application/json")).build();

      NovaApi whenNovaRegionExists = requestSendsResponse(authenticate, authenticationResponse);

      assertEquals(whenNovaRegionExists.getConfiguredZones(), ImmutableSet.of("RegionOne"));

   }

}
