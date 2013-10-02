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
package org.jclouds.dynect.v3.features;

import static java.util.logging.Logger.getAnonymousLogger;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.dynect.v3.domain.GeoService;
import org.jclouds.dynect.v3.domain.GeoRegionGroup;
import org.jclouds.dynect.v3.internal.BaseDynECTApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "GeoServiceApiLiveTest")
public class GeoServiceApiLiveTest extends BaseDynECTApiLiveTest {

   @Test
   protected void testListAndGetGeoServices() {
      ImmutableList<String> geos = api().list().toList();
      getAnonymousLogger().info("geo services: " + geos.size());
      for (String fqdn : geos) {
         GeoService geo = api().get(fqdn);
         checkGeoService(geo);
      }
   }

   static void checkGeoService(GeoService service) {
      assertNotNull(service.getName(), "Name cannot be null " + service);
      assertTrue(service.getTTL() >= 0, "TTL cannot be negative " + service);
      assertTrue(service.getNodes().size() > 0, "Nodes must be assigned " + service);
      assertTrue(service.getGroups().size() > 0, "Groups must be assigned " + service);
      for (GeoRegionGroup group : service.getGroups())
         GeoRegionGroupApiLiveTest.checkGeoRegionGroup(group);
   }

   protected GeoServiceApi api() {
      return api.getGeoServiceApi();
   }
}
