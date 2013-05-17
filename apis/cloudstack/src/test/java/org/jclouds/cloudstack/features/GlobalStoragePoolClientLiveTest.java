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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.StoragePool;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Strings;

/**
 * Tests behavior of {@code GlobalStoragePoolClient}
 *
 * @author Richard Downer
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalStoragePoolClientLiveTest")
public class GlobalStoragePoolClientLiveTest extends BaseCloudStackClientLiveTest {

   @Test(groups = "live", enabled = true)
   public void testListStoragePools() throws Exception {
      skipIfNotGlobalAdmin();

      Set<StoragePool> result = globalAdminClient.getStoragePoolClient().listStoragePools();
      assertNotNull(result);
      assertTrue(result.size() > 0);
      for(StoragePool pool : result) {
         assertNotNull(pool.getId());
         assertFalse(Strings.isNullOrEmpty(pool.getName()));
         assertFalse(Strings.isNullOrEmpty(pool.getPath()));
         assertNotNull(pool.getTags());
         assertNotEquals(StoragePool.State.UNRECOGNIZED, pool.getState());
         assertNotEquals(StoragePool.Type.UNRECOGNIZED, pool.getType());
         assertNotNull(pool.getZoneId());
         assertFalse(Strings.isNullOrEmpty(pool.getZoneName()));
         assertNotNull(pool.getPodId());
         assertFalse(Strings.isNullOrEmpty(pool.getPodName()));
         assertNotNull(pool.getClusterId());
         assertFalse(Strings.isNullOrEmpty(pool.getClusterName()));
         assertNotNull(pool.getCreated());
         assertTrue(pool.getDiskSizeTotal() > 0);
      }
   }

}
