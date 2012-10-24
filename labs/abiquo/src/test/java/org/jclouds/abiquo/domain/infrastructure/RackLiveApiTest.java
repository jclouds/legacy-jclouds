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

import static org.jclouds.abiquo.predicates.infrastructure.RackPredicates.name;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.abiquo.server.core.infrastructure.RackDto;
import com.google.common.collect.Iterables;

/**
 * Live integration tests for the {@link Rack} domain class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "RackLiveApiTest")
public class RackLiveApiTest extends BaseAbiquoApiLiveApiTest {

   public void testUpdate() {
      env.rack.setName("Updated rack");
      env.rack.update();

      // Recover the updated rack
      RackDto updated = env.infrastructureApi.getRack(env.datacenter.unwrap(), env.rack.getId());

      assertEquals(updated.getName(), "Updated rack");
   }

   public void testListRacks() {
      Iterable<Rack> racks = env.datacenter.listRacks();
      assertEquals(Iterables.size(racks), 1);

      racks = env.datacenter.listRacks(name(env.rack.getName()));
      assertEquals(Iterables.size(racks), 1);

      racks = env.datacenter.listRacks(name(env.rack.getName() + "FAIL"));
      assertEquals(Iterables.size(racks), 0);
   }

   public void testFindRack() {
      Rack rack = env.datacenter.findRack(name(env.rack.getName()));
      assertNotNull(rack);

      rack = env.datacenter.findRack(name(env.rack.getName() + "FAIL"));
      assertNull(rack);
   }

}
