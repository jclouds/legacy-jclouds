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
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.infrastructure.LogicServerPredicates;
import org.jclouds.abiquo.predicates.infrastructure.ManagedRackPredicates;
import org.testng.annotations.Test;

import com.abiquo.server.core.infrastructure.UcsRackDto;
import com.google.common.collect.Iterables;

/**
 * Live integration tests for the {@link ManagedRack} domain class.
 * 
 * @author Francesc Montserrat
 */
@Test(groups = "ucs", testName = "ManagedRackLiveUcsTest")
public class ManagedRackLiveUcsTest extends BaseAbiquoApiLiveApiTest {
   private LogicServer logicServer;

   private Organization organization;

   public void testUpdate() {
      env.ucsRack.setShortDescription("Updated description");
      env.ucsRack.update();

      // Recover the updated rack
      UcsRackDto updated = env.infrastructureApi.getManagedRack(env.datacenter.unwrap(), env.ucsRack.getId());

      assertEquals(updated.getShortDescription(), "Updated description");
   }

   public void testListManagedRacks() {
      Iterable<ManagedRack> racks = env.datacenter.listManagedRacks();
      assertEquals(Iterables.size(racks), 1);

      racks = env.datacenter.listManagedRacks(ManagedRackPredicates.name(env.ucsRack.getName()));
      assertEquals(Iterables.size(racks), 1);
   }

   public void testFindRack() {
      ManagedRack rack = env.datacenter.findManagedRack(ManagedRackPredicates.name(env.ucsRack.getName()));
      assertNotNull(rack);

      rack = env.datacenter.findManagedRack(ManagedRackPredicates.name(env.ucsRack.getName() + "FAIL"));
      assertNull(rack);
   }

   public void testCloneLogicServer() {
      List<LogicServer> originals = env.ucsRack.listServiceProfiles();
      assertNotNull(originals);
      assertTrue(originals.size() > 0);
      LogicServer original = originals.get(0);

      List<Organization> organizations = env.ucsRack.listOrganizations();
      assertNotNull(organizations);
      assertTrue(organizations.size() > 0);
      organization = organizations.get(0);

      env.ucsRack.cloneLogicServer(original, organization, "jclouds");

      logicServer = env.ucsRack
            .findServiceProfile(LogicServerPredicates.name(organization.getDn() + "/" + "ls-jclouds"));
      assertNotNull(logicServer);

      String name = logicServer.getName();
      assertEquals(name.substring(name.length() - 7, name.length()), "jclouds");
   }

   @Test(dependsOnMethods = "testCloneLogicServer")
   public void testListFsms() {
      List<Fsm> fsms = env.ucsRack.listFsm(logicServer.getName());
      assertNotNull(fsms);
      assertTrue(fsms.size() > 0);
   }

   @Test(dependsOnMethods = { "testCloneLogicServer", "testListFsms" })
   public void testDeleteLogicServer() {
      String name = logicServer.getName();

      env.ucsRack.deleteLogicServer(logicServer);

      LogicServer profile = env.ucsRack.findServiceProfile(LogicServerPredicates.name(name));
      assertNull(profile);
   }
}
