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
package org.jclouds.openstack.nova.v2_0;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests to ensure that we can pick the only endpoint of a service
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "EndpointIdIsRandomExpectTest")
public class EndpointIdIsRandomExpectTest extends BaseNovaApiExpectTest {

   public EndpointIdIsRandomExpectTest() {
      this.identity = "demo:demo";
      this.credential = "password";
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.setProperty(PROPERTY_ENDPOINT, "http://10.10.10.10:5000/v2.0/");
      return overrides;
   }

   public void testVersionMatchOnConfiguredZonesWhenResponseIs2xx() {

      HttpRequest authenticate = HttpRequest
            .builder()
            .method("POST")
            .endpoint("http://10.10.10.10:5000/v2.0/tokens")
            .addHeader("Accept", "application/json")
            .payload(
                  payloadFromStringWithContentType(
                        "{\"auth\":{\"passwordCredentials\":{\"username\":\"demo\",\"password\":\"password\"},\"tenantName\":\"demo\"}}",
                        "application/json")).build();

      HttpResponse authenticationResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/access_version_uids.json", "application/json")).build();

      NovaApi whenNovaRegionExists = requestSendsResponse(authenticate, authenticationResponse);

      assertEquals(whenNovaRegionExists.getConfiguredZones(), ImmutableSet.of("RegionOne"));

   }

}
