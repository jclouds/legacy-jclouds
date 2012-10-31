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

import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.options.VirtualDatacenterOptions;
import org.jclouds.abiquo.predicates.cloud.VirtualDatacenterPredicates;
import org.jclouds.abiquo.strategy.BaseAbiquoStrategyLiveApiTest;
import org.jclouds.abiquo.strategy.cloud.ListVirtualDatacenters;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Live tests for the {@link ListVirtualDatacenters} strategy.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "ListVirtualDatacentersLiveApiTest")
public class ListVirtualDatacentersLiveApiTest extends BaseAbiquoStrategyLiveApiTest {
   private ListVirtualDatacenters strategy;

   @Override
   @BeforeClass(groups = "api")
   protected void setupStrategy() {
      this.strategy = env.context.getUtils().getInjector().getInstance(ListVirtualDatacenters.class);
   }

   public void testExecute() {
      Iterable<VirtualDatacenter> vdcs = strategy.execute();
      assertNotNull(vdcs);
      assertTrue(size(vdcs) > 0);
   }

   public void testExecutePredicateWithoutResults() {
      Iterable<VirtualDatacenter> vdcs = strategy.execute(VirtualDatacenterPredicates.name("UNEXISTING"));
      assertNotNull(vdcs);
      assertEquals(size(vdcs), 0);
   }

   public void testExecutePredicateWithResults() {
      Iterable<VirtualDatacenter> vdcs = strategy.execute(VirtualDatacenterPredicates.name(env.virtualDatacenter
            .getName()));
      assertNotNull(vdcs);
      assertEquals(size(vdcs), 1);
   }

   public void testExecutePredicateOptionsWithResults() {
      Iterable<VirtualDatacenter> vdcs = strategy.execute(VirtualDatacenterOptions.builder()
            .datacenterId(env.datacenter.getId()).enterpriseId(env.defaultEnterprise.getId()).build());
      assertNotNull(vdcs);
      assertEquals(size(vdcs), 1);
   }

   public void testExecutePredicateOptionsWithoutResults() {
      Iterable<VirtualDatacenter> vdcs = strategy.execute(VirtualDatacenterOptions.builder()
            .enterpriseId(env.enterprise.getId()).build());
      assertNotNull(vdcs);
      assertEquals(size(vdcs), 0);
   }
}
