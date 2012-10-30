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

package org.jclouds.abiquo.domain.cloud;

import static com.google.common.collect.Iterables.getLast;
import static org.jclouds.abiquo.reference.AbiquoTestConstants.PREFIX;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.concurrent.TimeUnit;

import org.jclouds.abiquo.domain.task.AsyncTask;
import org.jclouds.abiquo.features.services.MonitoringService;
import org.jclouds.abiquo.internal.BaseAbiquoLiveApiTest;
import org.jclouds.abiquo.predicates.cloud.VirtualAppliancePredicates;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

/**
 * Live integration tests for the {@link VirtualAppliance} domain class.
 * 
 * @author Susana Acedo
 */
@Test(groups = "live", testName = "VirtualApplianceLiveTest")
public class VirtualApplianceLiveTest extends BaseAbiquoLiveApiTest {
   private static final long MAX_WAIT = 2;

   private VirtualDatacenter vdc;

   private VirtualAppliance vapp;

   private VirtualMachine vm;

   private VirtualMachineTemplate vmt;

   private MonitoringService monitoringService;

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      monitoringService = view.getMonitoringService();
      vdc = getLast(view.getCloudService().listVirtualDatacenters());
      vmt = templateBySize().min(vdc.listAvailableTemplates());
   }

   public void testCreateVirtualAppliance() {
      vapp = VirtualAppliance.builder(view.getApiContext(), vdc).name(PREFIX + "Virtual Appliance Ohana").build();
      vapp.save();

      assertNotNull(vapp.getId());
   }

   @Test(dependsOnMethods = "testCreateVirtualAppliance")
   public void testUpdateVirtualAppliance() {
      vapp.setName(PREFIX + "Virtual Appliance Updated");
      vapp.update();

      // Reload the appliance to check the updated name
      VirtualAppliance updated = vdc.getVirtualAppliance(vapp.getId());
      assertEquals(updated.getName(), PREFIX + "Virtual Appliance Updated");
   }

   @Test(dependsOnMethods = "testUpdateVirtualAppliance")
   public void testDeployVirtualAppliance() {
      vm = VirtualMachine.builder(view.getApiContext(), vapp, vmt).cpu(1).nameLabel(PREFIX + "VM Makua").ram(128)
            .build();

      vm.save();
      assertNotNull(vm.getId());

      AsyncTask[] tasks = vapp.deploy();
      assertEquals(tasks.length, 1); // One task for each VM in the VAPP

      monitoringService.getVirtualApplianceMonitor().awaitCompletionDeploy(MAX_WAIT, TimeUnit.MINUTES, vapp);
      assertEquals(vapp.getState(), VirtualApplianceState.DEPLOYED);
      assertEquals(vm.getState(), VirtualMachineState.ON);
   }

   @Test(dependsOnMethods = "testDeployVirtualAppliance")
   public void testUndeployVirtualAppliance() {
      AsyncTask[] tasks = vapp.undeploy();
      assertEquals(tasks.length, 1); // One task for each VM in the VAPP

      monitoringService.getVirtualApplianceMonitor().awaitCompletionUndeploy(MAX_WAIT, TimeUnit.MINUTES, vapp);
      assertEquals(vapp.getState(), VirtualApplianceState.NOT_DEPLOYED);
      assertEquals(vm.getState(), VirtualMachineState.NOT_ALLOCATED);
   }

   @Test(dependsOnMethods = "testUndeployVirtualAppliance")
   public void testDeleteVirtualAppliance() {
      vapp.delete();
      assertNull(view.getCloudService().findVirtualAppliance(
            VirtualAppliancePredicates.name(PREFIX + "Virtual Appliance Updated")));
   }

   private static Ordering<VirtualMachineTemplate> templateBySize() {
      return new Ordering<VirtualMachineTemplate>() {
         @Override
         public int compare(final VirtualMachineTemplate left, final VirtualMachineTemplate right) {
            return Longs.compare(left.getDiskFileSize(), right.getDiskFileSize());
         }
      };
   }
}
