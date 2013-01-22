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
package org.jclouds.route53.features;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.route53.domain.Zone;
import org.jclouds.route53.internal.BaseRoute53ApiLiveTest;
import org.jclouds.route53.options.ListZonesOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ZoneApiLiveTest")
public class ZoneApiLiveTest extends BaseRoute53ApiLiveTest {

   private void checkZone(Zone zone) {
      checkNotNull(zone.getId(), "Id cannot be null for a Zone.");
      checkNotNull(zone.getName(),  "Id cannot be null for a Zone.");
      checkNotNull(zone.getCallerReference(),  "CallerReference cannot be null for a Zone.");
      checkNotNull(zone.getComment(), "While Comment can be null for a Zone, its Optional wrapper cannot.");
   }

   @Test
   protected void testListZones() {
      IterableWithMarker<Zone> response = api().list().get(0);
      
      for (Zone zone : response) {
         checkZone(zone);
      }
      
      if (Iterables.size(response) > 0) {
         Zone zone = response.iterator().next();
         Assert.assertEquals(api().get(zone.getId()).getZone(), zone);
      }

      // Test with a Marker, even if it's null
      response = api().list(ListZonesOptions.Builder.afterMarker(response.nextMarker().orNull()));
      for (Zone zone : response) {
         checkZone(zone);
      }
   }

   protected ZoneApi api() {
      return context.getApi().getZoneApi();
   }
}
