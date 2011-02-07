/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.options.ListZonesOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code ZoneClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ZoneClientLiveTest")
public class ZoneClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testListZones() throws Exception {
      Set<Zone> response = client.getZoneClient().listZones();
      assert null != response;
      long zoneCount = response.size();
      assertTrue(zoneCount >= 0);
      for (Zone zone : response) {
         Zone newDetails = Iterables.getOnlyElement(client.getZoneClient().listZones(
                  ListZonesOptions.Builder.id(zone.getId())));
         assertEquals(zone, newDetails);
         assertEquals(zone, client.getZoneClient().getZone(zone.getId()));
         assert zone.getId() != null : zone;
         assert zone.getName() != null : zone;
         assert zone.getDNS().size() != 0 : zone;
         assert zone.getInternalDNS().size() != 0 : zone;
         assert zone.getNetworkType() != null && zone.getNetworkType() != NetworkType.UNRECOGNIZED : zone;
         switch (zone.getNetworkType()) {
            case ADVANCED:
               assert zone.getVLAN() != null : zone;
               assert zone.getDomain() == null : zone;
               assert zone.getDomainId() == null : zone;
               assert zone.getGuestCIDRAddress() != null : zone;
               break;
            case BASIC:
               assert zone.getVLAN() == null : zone;
               assert zone.getDomain() != null : zone;
               assert zone.getDomainId() != null : zone;
               assert zone.getGuestCIDRAddress() == null : zone;
               break;
         }

      }
   }

}
