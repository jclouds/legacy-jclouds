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
package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.internal.BaseCloudStackApiLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code HypervisorApiLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "HypervisorApiLiveTest")
public class HypervisorApiLiveTest extends BaseCloudStackApiLiveTest {

   public void testListHypervisors() throws Exception {
      Set<String> response = client.getHypervisorApi().listHypervisors();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (Zone zone : client.getZoneApi().listZones()) {
         Set<String> zoneHype = client.getHypervisorApi().listHypervisorsInZone(zone.getId());
         assert response.containsAll(zoneHype);
      }
   }

}
