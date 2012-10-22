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

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.infrastructure.TierPredicates;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Live integration tests for the {@link StorageDevice} domain class.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "TierLiveApiTest")
public class TierLiveApiTest extends BaseAbiquoApiLiveApiTest {

   public void testUpdate() {
      Tier tier = env.datacenter.listTiers().get(0);
      assertNotNull(tier);

      String previousName = tier.getName();
      tier.setName("Updated tier");
      tier.update();

      // Recover the updated tier
      Tier updated = env.datacenter.findTier(TierPredicates.name("Updated tier"));
      assertEquals(updated.getName(), "Updated tier");

      // Set original name
      tier.setName(previousName);
      tier.update();
   }

   public void testListTiers() {
      Iterable<Tier> tiers = env.datacenter.listTiers();
      assertEquals(Iterables.size(tiers), 4);

      tiers = env.datacenter.listTiers(TierPredicates.name("FAIL"));
      assertEquals(Iterables.size(tiers), 0);
   }
}
