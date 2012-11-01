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

package org.jclouds.abiquo.strategy.cloud;

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.abiquo.domain.cloud.VirtualAppliance;
import org.jclouds.abiquo.predicates.cloud.VirtualAppliancePredicates;
import org.jclouds.abiquo.strategy.BaseAbiquoStrategyLiveApiTest;
import org.jclouds.abiquo.strategy.cloud.ListVirtualAppliances;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Live tests for the {@link ListVirtualAppliances} strategy.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "ListVirtualAppliancesLiveApiTest")
public class ListVirtualAppliancesLiveApiTest extends BaseAbiquoStrategyLiveApiTest {
   private ListVirtualAppliances strategy;

   @Override
   @BeforeClass(groups = "api")
   protected void setupStrategy() {
      this.strategy = env.context.getUtils().getInjector().getInstance(ListVirtualAppliances.class);
   }

   public void testExecute() {
      Iterable<VirtualAppliance> vapps = strategy.execute();
      assertNotNull(vapps);
      assertTrue(size(vapps) > 0);
   }

   public void testExecutePredicateWithoutResults() {
      Iterable<VirtualAppliance> vapps = strategy.execute(VirtualAppliancePredicates.name("UNEXISTING"));
      assertNotNull(vapps);
      assertEquals(size(vapps), 0);
   }

   public void testExecutePredicateWithResults() {
      Iterable<VirtualAppliance> vapps = strategy.execute(VirtualAppliancePredicates.name(env.virtualAppliance
            .getName()));
      assertNotNull(vapps);
      assertEquals(size(vapps), 1);
   }
}
