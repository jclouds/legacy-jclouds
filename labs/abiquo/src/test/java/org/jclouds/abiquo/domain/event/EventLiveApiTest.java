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

package org.jclouds.abiquo.domain.event;

import static org.jclouds.abiquo.reference.AbiquoTestConstants.PREFIX;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.UUID;

import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.cloud.Volume;
import org.jclouds.abiquo.domain.enterprise.User;
import org.jclouds.abiquo.domain.event.options.EventOptions;
import org.jclouds.abiquo.domain.infrastructure.Tier;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.infrastructure.TierPredicates;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.ComponentType;
import com.abiquo.model.enumerator.EventType;
import com.abiquo.model.enumerator.SeverityType;
import com.google.common.collect.Iterables;

/**
 * Live integration tests for the {@link Event} domain class.
 * 
 * @author Vivien Mahé
 */
@Test(groups = "api", testName = "EventLiveApiTest")
public class EventLiveApiTest extends BaseAbiquoApiLiveApiTest {
   public void testListEventsFilteredByDatacenter() {
      String name = randomName();
      env.datacenter.setName(name);
      env.datacenter.update();

      EventOptions options = EventOptions.builder().dateFrom(new Date()).datacenterName(name).build();
      assertEvents(options);
   }

   public void testListEventsFilteredByRack() {
      String name = randomName();
      env.rack.setName(name);
      env.rack.update();

      EventOptions options = EventOptions.builder().dateFrom(new Date()).rackName(name).build();
      assertEvents(options);
   }

   public void testListEventsFilteredByPM() {
      String name = randomName();
      env.machine.setName(name);
      env.machine.update();

      EventOptions options = EventOptions.builder().dateFrom(new Date()).physicalMachineName(name).build();
      assertEvents(options);
   }

   public void testListEventsFilteredByStorageDevice() {
      String name = randomName();
      env.storageDevice.setName(name);
      env.storageDevice.update();

      EventOptions options = EventOptions.builder().dateFrom(new Date()).storageSystemName(name).build();
      assertEvents(options);
   }

   public void testListEventsFilteredByStoragePool() {
      Tier tier = env.datacenter.findTier(TierPredicates.name("Default Tier 2"));
      assertNotNull(tier);

      try {
         env.storagePool.setTier(tier);
         env.storagePool.update();

         EventOptions options = EventOptions.builder().dateFrom(new Date()).storagePoolName(env.storagePool.getName())
               .build();
         assertEvents(options);
      } finally {
         // Restore the original tier
         env.storagePool.setTier(env.tier);
         env.storagePool.update();
      }
   }

   public void testListEventsFilteredByEnterprise() {
      String entName = env.enterprise.getName();
      String name = randomName();
      env.enterprise.setName(name);
      env.enterprise.update();

      // Enterprise current =
      // env.enterpriseAdminContext.getAdministrationService().getCurrentEnterprise();
      // current.setName("Enterprise updated");
      // current.update();

      EventOptions options = EventOptions.builder().dateFrom(new Date()).enterpriseName(name).build();
      assertEvents(options);

      env.enterprise.setName(entName);
      env.enterprise.update();
   }

   /**
    * TODO: Using the painUserContext, modifying the user returns this error:
    * HTTP/1.1 401 Unauthorized
    **/
   @Test(enabled = false)
   public void testListEventsFilteredByUser() {
      User current = env.plainUserContext.getAdministrationService().getCurrentUser();
      current.setEmail("test@test.com");
      current.update();

      EventOptions options = EventOptions.builder().dateFrom(new Date()).userName(current.getName()).build();
      assertEvents(options);
   }

   public void testListEventsFilteredByVDC() {
      String name = randomName();
      env.virtualDatacenter.setName(name);
      env.virtualDatacenter.update();

      EventOptions options = EventOptions.builder().dateFrom(new Date()).virtualDatacenterName(name).build();
      assertEvents(options);
   }

   public void testListEventsFilteredByVapp() {
      String name = randomName();
      env.virtualAppliance.setName(name);
      env.virtualAppliance.update();

      EventOptions options = EventOptions.builder().dateFrom(new Date()).virtualAppName(name).build();
      assertEvents(options);
   }

   public void testListEventsFilteredByVM() {
      VirtualMachine vm = createVirtualMachine();
      vm.delete();

      EventOptions options = EventOptions.builder().dateFrom(new Date()).actionPerformed(EventType.VM_DELETE).build();
      assertEvents(options);
   }

   public void testListEventsFilteredByVolume() {
      String name = randomName();
      Volume volume = createVolume();
      volume.setName(name);
      volume.update();
      volume.delete(); // We don't need it any more. events already exist

      EventOptions options = EventOptions.builder().dateFrom(new Date()).volumeName(name).build();
      assertEvents(options);
   }

   public void testListEventsFilteredBySeverity() {
      String name = randomName();
      env.virtualAppliance.setName(name);
      env.virtualAppliance.update();

      EventOptions options = EventOptions.builder().dateFrom(new Date()).virtualAppName(name)
            .severity(SeverityType.INFO).build();
      assertEvents(options);
   }

   public void testListEventsFilteredByActionPerformed() {
      String name = randomName();
      env.virtualAppliance.setName(name);
      env.virtualAppliance.update();

      EventOptions options = EventOptions.builder().dateFrom(new Date()).virtualAppName(name)
            .actionPerformed(EventType.VAPP_MODIFY).build();
      assertEvents(options);
   }

   public void testListEventsFilteredByComponent() {
      String name = randomName();
      env.virtualAppliance.setName(name);
      env.virtualAppliance.update();

      EventOptions options = EventOptions.builder().dateFrom(new Date()).virtualAppName(name)
            .component(ComponentType.VIRTUAL_APPLIANCE).build();
      assertEvents(options);
   }

   public void testListEventsFilteredByDescription() {
      String name = randomName();
      env.virtualAppliance.setName(name);
      env.virtualAppliance.update();

      EventOptions options = EventOptions.builder().dateFrom(new Date()).virtualAppName(name)
            .description("Virtual appliance '" + name + "' has been modified.").build();
      assertEvents(options);
   }

   // Helpers

   private void assertEvents(final EventOptions options) {
      Iterable<Event> events = env.eventService.listEvents(options);
      assertTrue(Iterables.size(events) >= 1);
   }

   private Volume createVolume() {
      Tier tier = env.virtualDatacenter.findStorageTier(TierPredicates.name(env.tier.getName()));
      Volume volume = Volume.builder(env.context.getApiContext(), env.virtualDatacenter, tier)
            .name(PREFIX + "Event vol").sizeInMb(32).build();

      volume.save();
      assertNotNull(volume.getId());

      return volume;
   }

   private VirtualMachine createVirtualMachine() {
      VirtualMachine virtualMachine = VirtualMachine
            .builder(env.context.getApiContext(), env.virtualAppliance, env.template).cpu(2).ram(128).build();

      virtualMachine.save();
      assertNotNull(virtualMachine.getId());

      return virtualMachine;
   }

   private static String randomName() {
      return PREFIX + UUID.randomUUID().toString().substring(0, 12);
   }
}
