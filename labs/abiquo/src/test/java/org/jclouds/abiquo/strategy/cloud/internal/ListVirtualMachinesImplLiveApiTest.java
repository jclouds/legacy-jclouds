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

package org.jclouds.abiquo.strategy.cloud.internal;

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.predicates.cloud.VirtualMachinePredicates;
import org.jclouds.abiquo.strategy.BaseAbiquoStrategyLiveApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Live tests for the {@link ListVirtualMachinesImpl} strategy.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "ListVirtualMachinesImplLiveApiTest")
public class ListVirtualMachinesImplLiveApiTest extends BaseAbiquoStrategyLiveApiTest {
   private ListVirtualMachinesImpl strategy;

   @Override
   @BeforeClass(groups = "api")
   protected void setupStrategy() {
      this.strategy = env.context.getUtils().getInjector().getInstance(ListVirtualMachinesImpl.class);
   }

   public void testExecute() {
      Iterable<VirtualMachine> vms = strategy.execute();
      assertNotNull(vms);
      assertTrue(size(vms) > 0);
   }

   public void testExecutePredicateWithoutResults() {
      Iterable<VirtualMachine> vms = strategy.execute(VirtualMachinePredicates.internalName("UNEXISTING"));
      assertNotNull(vms);
      assertEquals(size(vms), 0);
   }

   public void testExecutePredicateWithResults() {
      Iterable<VirtualMachine> vms = strategy.execute(VirtualMachinePredicates.internalName(env.virtualMachine
            .getInternalName()));
      assertNotNull(vms);
      assertEquals(size(vms), 1);
   }

   public void testExecuteWhenExceedsPagination() {
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
         Iterable<VirtualMachine> all = strategy.execute();

         assertNotNull(all);
         assertTrue(size(all) >= numVms);
      } finally {
         for (VirtualMachine vm : vms) {
            vm.delete();
         }
      }
   }
}
