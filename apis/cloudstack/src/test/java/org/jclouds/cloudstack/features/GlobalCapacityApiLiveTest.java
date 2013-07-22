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

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.Capacity;
import org.jclouds.cloudstack.internal.BaseCloudStackApiLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code GlobalCapacityApi}
 *
 * @author Richard Downer
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalCapacityApiLiveTest")
public class GlobalCapacityApiLiveTest extends BaseCloudStackApiLiveTest {

   @Test(groups = "live", enabled = true)
   public void testListCapacity() throws Exception {
      skipIfNotGlobalAdmin();

      final Set<Capacity> response = globalAdminClient.getCapacityClient().listCapacity();
      assertNotNull(response);
      assertNotEquals(0, response.size());
      int count = 0;
      for (Capacity capacity : response) {
         assertTrue(capacity.getCapacityTotal() >= 0);
         assertTrue(capacity.getCapacityUsed() >= 0);
         assertTrue(capacity.getPercentUsed() >= 0);
         assertNotEquals(Capacity.Type.UNRECOGNIZED, capacity.getType());
         assertNotNull(capacity.getZoneName());
         count++;
      }
      assertTrue(count > 0, "No capacities were returned, so I couldn't test");
   }

}
