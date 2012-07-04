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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.ISO;
import org.jclouds.cloudstack.domain.ISOPermissions;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.options.ListISOsOptions;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@link ISOClient} and {@link ISOAsyncClient}
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "live", singleThreaded = true, testName = "ISOClientLiveTest")
public class ISOClientLiveTest extends BaseCloudStackClientLiveTest {
   public void testListPublicISOs() throws Exception {
      Set<ISO> response = client.getISOClient().listISOs(ListISOsOptions.Builder.isPublic());
      assertNotNull(response);
      assertFalse(response.isEmpty());
      long isoCount = response.size();
      assertTrue(isoCount >= 0);

      for (ISO iso : response) {
         ISO query = client.getISOClient().getISO(iso.getId());
         assertEquals(query.getId(), iso.getId());
      }
   }

   public void testListISOPermissions() throws Exception {
      Set<ISO> response = client.getISOClient().listISOs(ListISOsOptions.Builder.isPublic());
      assertNotNull(response);
      assertFalse(response.isEmpty());
      long isoCount = response.size();
      assertTrue(isoCount >= 0);

      for (ISO iso : response) {
         ISOPermissions perms = client.getISOClient().listISOPermissions(iso.getId());
         assertNotNull(perms);
      }
   }
}
