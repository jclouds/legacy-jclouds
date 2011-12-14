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
import org.jclouds.cloudstack.domain.StoragePool;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.*;

/**
 * Tests behavior of {@code GlobalStoragePoolClient}
 *
 * @author Richard Downer
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalStoragePoolClientLiveTest")
public class GlobalStoragePoolClientLiveTest extends BaseCloudStackClientLiveTest {

   @Test(groups = "live", enabled = true)
   public void testListStoragePools() throws Exception {
      assertTrue(globalAdminEnabled, "Test cannot run without global admin identity and credentials");

      Set<StoragePool> result = globalAdminClient.getStoragePoolClient().listStoragePools();
      assertNotNull(result);
      assertTrue(result.size() > 0);
      for(StoragePool pool : result) {
         assertTrue(pool.getId() > 0);
         assertFalse(Strings.isNullOrEmpty(pool.getName()));
         assertFalse(Strings.isNullOrEmpty(pool.getPath()));
         assertNotNull(pool.getTags());
         assertTrue(pool.getState() != StoragePool.State.UNRECOGNIZED);
         assertTrue(pool.getType() != StoragePool.Type.UNRECOGNIZED);
         assertTrue(pool.getZoneId() > 0);
         assertFalse(Strings.isNullOrEmpty(pool.getZoneName()));
         assertTrue(pool.getPodId() > 0);
         assertFalse(Strings.isNullOrEmpty(pool.getPodName()));
         assertTrue(pool.getClusterId() > 0);
         assertFalse(Strings.isNullOrEmpty(pool.getClusterName()));
         assertNotNull(pool.getCreated());
         assertTrue(pool.getDiskSizeTotal() > 0);
      }
   }

}
