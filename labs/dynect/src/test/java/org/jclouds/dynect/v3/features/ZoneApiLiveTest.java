/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
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
package org.jclouds.dynect.v3.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.testng.Assert.assertNull;

import org.jclouds.dynect.v3.domain.Zone;
import org.jclouds.dynect.v3.internal.BaseDynECTApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ZoneApiLiveTest")
public class ZoneApiLiveTest extends BaseDynECTApiLiveTest {

   private void checkZone(Zone zone) {
      checkNotNull(zone.getName(), "Name cannot be null for a Zone: %s", zone);
      checkNotNull(zone.getSerial(), "Serial cannot be null for a Zone: %s", zone);
   }

   @Test
   protected void testListAndGetZones() {
      ImmutableList<String> zones = api().list().toList();
      getAnonymousLogger().info("zones: " + zones.size());

      for (String zoneName : zones) {
         Zone zone = api().get(zoneName);
         checkNotNull(zone, "zone was null for Zone: %s", zoneName);
         checkZone(zone);
      }
   }

   @Test
   public void testGetZoneWhenNotFound() {
      assertNull(api().get("AAAAAAAAAAAAAAAA"));
   }

   protected ZoneApi api() {
      return context.getApi().getZoneApi();
   }
}
