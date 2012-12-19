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
import static org.testng.Assert.assertTrue;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.util.Config;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Live integration tests for the {@link Blade} domain class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "ucs", testName = "BladeLiveUcsTest")
public class BladeLiveUcsTest extends BaseAbiquoApiLiveApiTest {
   Blade blade;

   public void testFindAvailableVirtualSwitch() {
      String vswitch = Config.get("abiquo.hypervisor.vswitch");
      NetworkInterface found = env.machine.findAvailableVirtualSwitch(vswitch);
      assertEquals(found, vswitch);
   }

   public void testGetRack() {
      ManagedRack rack = blade.getRack();
      assertNotNull(rack);
      assertEquals(rack.getId(), env.ucsRack.getId());
   }

   public void testListBlades() {
      Iterable<Blade> blades = env.ucsRack.listMachines();
      assertTrue(Iterables.size(blades) > 0);
   }

   public void testGetLogicServer() {
      LogicServer logicServer = blade.getLogicServer();
      assertNotNull(logicServer);
      assertNotNull(logicServer.getName());
   }

   public void testLedOn() {
      blade.ledOn();
      BladeLocatorLed led = blade.getLocatorLed();
      assertNotNull(led);
      assertEquals(led.getAdminStatus(), "on");
   }

   public void testLedOff() {
      blade.ledOff();
      BladeLocatorLed led = blade.getLocatorLed();
      assertNotNull(led);
      assertEquals(led.getAdminStatus(), "off");
   }

   @BeforeClass
   public void setup() {
      blade = env.ucsRack.listMachines().get(0);
      assertNotNull(blade);
   }
}
