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

package org.jclouds.abiquo.domain.cloud;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.HypervisorType;

/**
 * Live integration tests for the {@link HardDisk} domain class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "HardDiskLiveApiTest")
public class HardDiskLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private HardDisk hardDisk;

   public void createHardDisk() {
      skipIfUnsupported();

      hardDisk = HardDisk.builder(env.context.getApiContext(), env.virtualDatacenter).sizeInMb(64L).build();
      hardDisk.save();

      assertNotNull(hardDisk.getId());
      assertNotNull(hardDisk.getSequence());

      assertNotNull(env.virtualDatacenter.getHardDisk(hardDisk.getId()));
   }

   @Test(dependsOnMethods = "createHardDisk")
   public void deleteHardDisk() {
      skipIfUnsupported();

      HardDisk hd = env.virtualDatacenter.getHardDisk(hardDisk.getId());
      assertNotNull(hd);

      Integer id = hd.getId();
      hardDisk.delete();
      assertNull(env.virtualDatacenter.getHardDisk(id));
   }

   protected static void skipIfUnsupported() {
      if (!env.machine.getType().equals(HypervisorType.VMX_04)) {
         throw new SkipException(
               "Cannot perform this test because hard disk actions are not available for this hypervisor");
      }
   }
}
