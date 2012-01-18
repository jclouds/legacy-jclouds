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
package org.jclouds.cloudstack.features;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.Pod;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.options.CreatePodOptions;
import org.jclouds.cloudstack.options.ListPodsOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.Set;

import static org.jclouds.cloudstack.options.UpdateZoneOptions.Builder.name;
import static org.testng.Assert.*;

/**
 * Tests behavior of {@code GlobalPodClient}
 *
 * @author Richard Downer
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalPodClientLiveTest")
public class GlobalPodClientLiveTest extends BaseCloudStackClientLiveTest {

   private Zone zone;
   private Pod pod;

   public void testListPods() throws Exception {
      Set<Pod> response = globalAdminClient.getPodClient().listPods();
      assert null != response;
      long podCount = response.size();
      assertTrue(podCount >= 0);
      for (Pod pod : response) {
         Pod newDetails = Iterables.getOnlyElement(globalAdminClient.getPodClient().listPods(
            ListPodsOptions.Builder.id(pod.getId())));
         assertEquals(pod, newDetails);
         assertEquals(pod, globalAdminClient.getPodClient().getPod(pod.getId()));
         assertFalse(pod.getId() <= 0);
         assertFalse(Strings.isNullOrEmpty(pod.getName()));
         assertFalse(pod.getZoneId() <= 0);
         assertFalse(Strings.isNullOrEmpty(pod.getZoneName()));
         assertFalse(Strings.isNullOrEmpty(pod.getGateway()));
         assertFalse(Strings.isNullOrEmpty(pod.getNetmask()));
         assertFalse(Strings.isNullOrEmpty(pod.getStartIp()));
         assertFalse(Strings.isNullOrEmpty(pod.getEndIp()));
         assertNotEquals(pod.getAllocationState(), AllocationState.UNKNOWN);
      }
   }

   @Test
   public void testCreatePod() {
      assertTrue(globalAdminEnabled, "Global admin credentials must be given");

      zone = globalAdminClient.getZoneClient().createZone(prefix + "-zone", NetworkType.BASIC, "8.8.8.8", "10.10.10.10");
      pod = globalAdminClient.getPodClient().createPod(prefix + "-pod", zone.getId(), "172.20.0.1", "172.20.0.250", "172.20.0.254", "255.255.255.0",
      CreatePodOptions.Builder.allocationState(AllocationState.ENABLED));

      assertNotNull(pod);
      assertEquals(pod.getName(), prefix + "-pod");
      assertEquals(pod.getZoneId(), zone.getId());
      assertEquals(pod.getZoneName(), prefix + "-zone");
      assertEquals(pod.getStartIp(), "172.20.0.1");
      assertEquals(pod.getEndIp(), "172.20.0.250");
      assertEquals(pod.getGateway(), "172.20.0.254");
      assertEquals(pod.getNetmask(), "255.255.255.0");
      assertEquals(pod.getAllocationState(), AllocationState.ENABLED);
   }

   @AfterClass
   public void testFixtureTearDown() {
      if (pod != null) {
         globalAdminClient.getPodClient().deletePod(pod.getId());
         pod = null;
      }
      if (zone != null) {
         globalAdminClient.getZoneClient().deleteZone(zone.getId());
         zone = null;
      }
   }
}
