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
package org.jclouds.tmrk.enterprisecloud.features;

import org.jclouds.tmrk.enterprisecloud.domain.Location;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Tests behavior of {@code LocationClient}
 * 
 * @author Jason King
 */
@Test(groups = "live", testName = "LocationClientLiveTest")
public class LocationClientLiveTest extends BaseTerremarkEnterpriseCloudClientLiveTest {
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getLocationClient();
   }

   private LocationClient client;

   public void testGetLocationById() throws Exception {
      Location location = client.getLocationById(URI.create("/cloudapi/ecloud/locations/1"));
      Location expected = Location.builder().href(URI.create("/cloudapi/ecloud/locations/1")).name("Terremark - Richardson").type("application/vnd.tmrk.cloud.location")
            .friendlyName("Terremark - Richardson").locode("DAC").iso3166("US-TX").build();
      assertEquals(location,expected);
   }

   public void testMissingLocation() {
      assertNull(client.getLocationById(URI.create("/cloudapi/ecloud/locations/-1")));
   }
}
