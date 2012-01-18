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
import com.google.common.collect.Iterables;
import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.Pod;
import org.jclouds.cloudstack.options.ListPodsOptions;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.*;

/**
 * Tests behavior of {@code GlobalPodClient}
 *
 * @author Richard Downer
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalPodClientLiveTest")
public class GlobalPodClientLiveTest extends BaseCloudStackClientLiveTest {

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

}
