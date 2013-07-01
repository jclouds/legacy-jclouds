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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.Alert;
import org.jclouds.cloudstack.internal.BaseCloudStackApiLiveTest;
import org.jclouds.cloudstack.options.ListAlertsOptions;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code GlobalAlertsClient}
 *
 * @author Richard Downer
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalAlertApiLiveTest")
public class GlobalAlertApiLiveTest extends BaseCloudStackApiLiveTest {

   @Test(groups = "live", enabled = true)
   public void testListAlerts() throws Exception {
      skipIfNotGlobalAdmin();

      final Set<Alert> response = globalAdminClient.getAlertClient().listAlerts(ListAlertsOptions.Builder.id("20"));
      assert null != response;
      assertTrue(response.size() >= 0);
      int count = 0;
      for (Alert alert : response) {
         assertNotNull(alert.getDescription());
         assertNotSame(alert.getId(), 0);
         assertNotNull(alert.getType());
         assertNotNull(alert.getSent());
         count++;
      }
      assertTrue(count > 0, "No alerts were returned, so I couldn't test");
   }

}
