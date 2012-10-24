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
import org.jclouds.abiquo.predicates.infrastructure.StorageDevicePredicates;
import org.testng.annotations.Test;

import com.abiquo.server.core.infrastructure.storage.StorageDeviceDto;
import com.google.common.collect.Iterables;

/**
 * Live integration tests for the {@link StorageDevice} domain class.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "StorageDeviceLiveApiTest")
public class StorageDeviceLiveApiTest extends BaseAbiquoApiLiveApiTest {

   public void testUpdate() {
      env.storageDevice.setName("Updated storage device");
      env.storageDevice.update();

      // Recover the updated storage device
      StorageDeviceDto updated = env.infrastructureApi.getStorageDevice(env.datacenter.unwrap(),
            env.storageDevice.getId());

      assertEquals(updated.getName(), "Updated storage device");
   }

   public void testListStorageDevices() {
      Iterable<StorageDevice> storageDevices = env.datacenter.listStorageDevices();
      assertEquals(Iterables.size(storageDevices), 1);

      storageDevices = env.datacenter.listStorageDevices(StorageDevicePredicates.name(env.storageDevice.getName()));
      assertEquals(Iterables.size(storageDevices), 1);

      storageDevices = env.datacenter.listStorageDevices(StorageDevicePredicates.name(env.storageDevice.getName()
            + "FAIL"));
      assertEquals(Iterables.size(storageDevices), 0);
   }

   public void testFindStorageDevice() {
      StorageDevice storageDevice = env.datacenter.findStorageDevice(StorageDevicePredicates.name(env.storageDevice
            .getName()));
      assertNotNull(storageDevice);

      storageDevice = env.datacenter.findStorageDevice(StorageDevicePredicates.name(env.storageDevice.getName()
            + "FAIL"));
      assertNull(storageDevice);
   }

}
