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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.Pod;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.internal.BaseCloudStackApiLiveTest;
import org.jclouds.cloudstack.options.CreatePodOptions;
import org.jclouds.cloudstack.options.ListPodsOptions;
import org.jclouds.cloudstack.options.UpdatePodOptions;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code GlobalPodApi}
 *
 * @author Richard Downer
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalPodApiLiveTest")
public class GlobalPodApiLiveTest extends BaseCloudStackApiLiveTest {

   private Zone zone;
   private Pod pod;

   public void testListPods() throws Exception {
      skipIfNotGlobalAdmin();

      Set<Pod> response = globalAdminClient.getPodClient().listPods();
      assert null != response;
      long podCount = response.size();
      assertTrue(podCount >= 0);

      for (Pod pod : response) {
         Pod newDetails = Iterables.getOnlyElement(globalAdminClient.getPodClient().listPods(
            ListPodsOptions.Builder.id(pod.getId())));
         assertEquals(pod, newDetails);
         assertEquals(pod, globalAdminClient.getPodClient().getPod(pod.getId()));
         assertNotNull(pod.getId());
         assertFalse(Strings.isNullOrEmpty(pod.getName()));
         assertNotNull(pod.getZoneId());
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
      skipIfNotGlobalAdmin();

      zone = globalAdminClient.getZoneApi().createZone(prefix + "-zone-for-pod", NetworkType.BASIC, "8.8.8.8", "10.10.10.10");
      pod = globalAdminClient.getPodClient().createPod(prefix + "-pod", zone.getId(), "172.20.0.1", "172.20.0.250", "172.20.0.254", "255.255.255.0",
      CreatePodOptions.Builder.allocationState(AllocationState.ENABLED));

      assertNotNull(pod);
      assertEquals(pod.getName(), prefix + "-pod");
      assertEquals(pod.getZoneId(), zone.getId());
      assertEquals(pod.getZoneName(), prefix + "-zone-for-pod");
      assertEquals(pod.getStartIp(), "172.20.0.1");
      assertEquals(pod.getEndIp(), "172.20.0.250");
      assertEquals(pod.getGateway(), "172.20.0.254");
      assertEquals(pod.getNetmask(), "255.255.255.0");
      assertEquals(pod.getAllocationState(), AllocationState.ENABLED);
   }

   @Test(dependsOnMethods = "testCreatePod")
   public void testUpdatePod() {
      Pod updated = globalAdminClient.getPodClient().updatePod(pod.getId(), UpdatePodOptions.Builder
         .name(prefix + "-updatedpod")
         .startIp("172.21.0.129")
         .endIp("172.21.0.250")
         .gateway("172.21.0.254")
         .netmask("255.255.255.128")
         .allocationState(AllocationState.DISABLED)
      );

      assertNotNull(updated);
      assertEquals(updated.getName(), prefix + "-updatedpod");
      assertEquals(updated.getZoneId(), zone.getId());
      assertEquals(updated.getZoneName(), prefix + "-zone-for-pod");
      assertEquals(updated.getStartIp(), "172.21.0.129");
      assertEquals(updated.getEndIp(), "172.21.0.250");
      assertEquals(updated.getGateway(), "172.21.0.254");
      assertEquals(updated.getNetmask(), "255.255.255.128");
      assertEquals(updated.getAllocationState(), AllocationState.DISABLED);
   }

   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      if (pod != null) {
         globalAdminClient.getPodClient().deletePod(pod.getId());
         pod = null;
      }
      if (zone != null) {
         globalAdminClient.getZoneApi().deleteZone(zone.getId());
         zone = null;
      }
      super.tearDownContext();
   }
}
