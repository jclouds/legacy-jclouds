/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.Zone;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code HypervisorClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "HypervisorClientLiveTest")
public class HypervisorClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testListHypervisors() throws Exception {
      Set<String> response = client.getHypervisorClient().listHypervisors();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (Zone zone : client.getZoneClient().listZones()) {
         Set<String> zoneHype = client.getHypervisorClient().listHypervisorsInZone(zone.getId());
         assert response.containsAll(zoneHype);
      }
   }

}
