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

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceState;

/**
 * Live integration tests for the {@link VirtualAppliance} domain class.
 * 
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "VirtualApplianceLiveApiTest")
public class VirtualApplianceLiveApiTest extends BaseAbiquoApiLiveApiTest {

   public void testUpdate() {
      env.virtualAppliance.setName("Virtual AppAloha updated");
      env.virtualAppliance.update();

      // Recover the updated virtual appliance
      VirtualApplianceDto updated = env.cloudApi.getVirtualAppliance(env.virtualDatacenter.unwrap(),
            env.virtualAppliance.getId());

      assertEquals(updated.getName(), "Virtual AppAloha updated");
   }

   public void testCreateRepeated() {
      VirtualAppliance repeated = VirtualAppliance.Builder.fromVirtualAppliance(env.virtualAppliance).build();

      repeated.save();

      List<VirtualApplianceDto> virtualAppliances = env.cloudApi.listVirtualAppliances(env.virtualDatacenter.unwrap())
            .getCollection();

      assertEquals(virtualAppliances.size(), 2);
      repeated.delete();
   }

   public void testGetState() {
      assertEquals(env.virtualAppliance.getState(), VirtualApplianceState.NOT_DEPLOYED);
   }

   public void testListVirtualMachinesReturnsAll() {
      List<VirtualMachine> vms = new ArrayList<VirtualMachine>();

      // Pagination by default is set to 25 items per page, so create a few more
      // to verify that
      // all are returned when listing
      int numVms = 30;

      for (int i = 0; i < numVms; i++) {
         VirtualMachine vm = VirtualMachine.Builder.fromVirtualMachine(env.virtualMachine).build();
         vm.save();
         vms.add(vm);
      }

      try {
         Iterable<VirtualMachine> all = env.virtualAppliance.listVirtualMachines();

         assertNotNull(all);
         assertTrue(size(all) >= numVms);
      } finally {
         for (VirtualMachine vm : vms) {
            vm.delete();
         }
      }
   }
}
