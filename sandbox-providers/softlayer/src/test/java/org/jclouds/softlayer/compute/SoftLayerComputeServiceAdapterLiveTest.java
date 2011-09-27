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
package org.jclouds.softlayer.compute;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.jclouds.softlayer.compute.strategy.SoftLayerComputeServiceAdapter;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.features.BaseSoftLayerClientLiveTest;
import org.jclouds.softlayer.features.ProductPackageClientLiveTest;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.units;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertEquals;

@Test(groups = "live", testName = "SoftLayerComputeServiceAdapterLiveTest")
public class SoftLayerComputeServiceAdapterLiveTest extends BaseSoftLayerClientLiveTest {

   private SoftLayerComputeServiceAdapter adapter;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      adapter = new SoftLayerComputeServiceAdapter(context.getApi(), ProductPackageClientLiveTest.CLOUD_SERVER_PACKAGE_NAME);
   }

   @Test
   public void testListLocations() {
      assertFalse(Iterables.isEmpty(adapter.listLocations()));
   }

   @Test
   public void testListHardwareProfiles() {
      Iterable<Set<ProductItem>> profiles = adapter.listHardwareProfiles();
      assertFalse(Iterables.isEmpty(profiles));

      for( Set<ProductItem> profile: profiles) {
         // CPU, RAM and Volume
         assertEquals(profile.size(), 3);
         ProductItem cpuItem = Iterables.getOnlyElement(Iterables.filter(profile, units("PRIVATE_CORE")));
         ProductItem ramItem = Iterables.getOnlyElement(Iterables.filter(profile,categoryCode("ram")));
         Assert.assertEquals(cpuItem.getCapacity(),ramItem.getCapacity());
      }
   }

}
