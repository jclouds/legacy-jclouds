/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.infrastructure;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.infrastructure.StoragePoolPredicates;
import org.jclouds.abiquo.predicates.infrastructure.TierPredicates;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Live integration tests for the {@link StorageDevice} domain class.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "StoragePoolLiveApiTest")
public class StoragePoolLiveApiTest extends BaseAbiquoApiLiveApiTest {
   public void testGetDevice() {
      StorageDevice device = env.storagePool.getStorageDevice();
      assertNotNull(device);
      assertEquals(device.getId(), env.storageDevice.getId());
   }

   public void testUpdate() {
      try {
         Tier tier3 = env.datacenter.findTier(TierPredicates.name("Default Tier 3"));
         assertNotNull(tier3);
         env.storagePool.setTier(tier3);
         env.storagePool.update();

         assertEquals(env.storagePool.getTier().getName(), "Default Tier 3");
      } finally {
         // Restore the original tier
         env.storagePool.setTier(env.tier);
         env.storagePool.update();
         assertEquals(env.storagePool.getTier().getId(), env.tier.getId());
      }
   }

   public void testListStoragePool() {
      Iterable<StoragePool> storagePools = env.storageDevice.listStoragePools();
      assertEquals(Iterables.size(storagePools), 1);

      storagePools = env.storageDevice.listStoragePools(StoragePoolPredicates.name(env.storagePool.getName()));
      assertEquals(Iterables.size(storagePools), 1);

      storagePools = env.storageDevice.listStoragePools(StoragePoolPredicates.name(env.storagePool.getName() + "FAIL"));
      assertEquals(Iterables.size(storagePools), 0);
   }

   public void testFindStoragePool() {
      StoragePool storagePool = env.storageDevice
            .findStoragePool(StoragePoolPredicates.name(env.storagePool.getName()));
      assertNotNull(storagePool);

      storagePool = env.storageDevice.findStoragePool(StoragePoolPredicates.name(env.storagePool.getName() + "FAIL"));
      assertNull(storagePool);
   }

   public void testRefreshStoragePool() {
      env.storagePool.refresh();
   }

}
