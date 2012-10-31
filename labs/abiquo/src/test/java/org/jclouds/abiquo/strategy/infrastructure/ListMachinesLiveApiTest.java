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

package org.jclouds.abiquo.strategy.infrastructure;

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.abiquo.domain.infrastructure.Machine;
import org.jclouds.abiquo.predicates.infrastructure.MachinePredicates;
import org.jclouds.abiquo.strategy.BaseAbiquoStrategyLiveApiTest;
import org.jclouds.abiquo.strategy.infrastructure.ListMachines;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Live tests for the {@link ListMachines} strategy.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "ListMachinesLiveApiTest")
public class ListMachinesLiveApiTest extends BaseAbiquoStrategyLiveApiTest {
   private ListMachines strategy;

   @Override
   @BeforeClass(groups = "api")
   protected void setupStrategy() {
      this.strategy = env.context.getUtils().getInjector().getInstance(ListMachines.class);
   }

   public void testExecute() {
      Iterable<Machine> machines = strategy.execute();
      assertNotNull(machines);
      assertTrue(size(machines) > 0);
   }

   public void testExecutePredicateWithoutResults() {
      Iterable<Machine> machines = strategy.execute(MachinePredicates.name("UNEXISTING"));
      assertNotNull(machines);
      assertEquals(size(machines), 0);
   }

   public void testExecutePredicateWithResults() {
      Iterable<Machine> machines = strategy.execute(MachinePredicates.name(env.machine.getName()));
      assertNotNull(machines);
      assertEquals(size(machines), 1);
   }
}
