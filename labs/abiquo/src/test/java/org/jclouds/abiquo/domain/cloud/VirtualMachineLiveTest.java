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
import static org.jclouds.abiquo.util.Assert.assertHasError;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.domain.network.Ip;
import org.jclouds.abiquo.domain.task.AsyncTask;
import org.jclouds.abiquo.features.services.MonitoringService;
import org.jclouds.abiquo.internal.BaseAbiquoLiveApiTest;
import org.jclouds.abiquo.predicates.cloud.VirtualMachinePredicates;
import org.jclouds.abiquo.predicates.network.IpPredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualMachineState;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

/**
 * Live integration tests for the {@link VirtualMachine} domain class.
 * 
 * @author Susana Acedo
 */
@Test(groups = "live", testName = "VirtualMachineLiveTest")
public class VirtualMachineLiveTest extends BaseAbiquoLiveApiTest {
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

      vapp = VirtualAppliance.builder(view.getApiContext(), vdc).name(PREFIX + "Virtual Appliance Wahine").build();
      vapp.save();
      assertNotNull(vapp.getId());
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDownContext() {
      vapp.delete();
      super.tearDownContext();
   }

   @Test
   public void testCreateVirtualMachine() {
      vm = VirtualMachine.builder(view.getApiContext(), vapp, vmt).cpu(1).nameLabel(PREFIX + "VM Kane").ram(128)
            .build();

      vm.save();
      assertNotNull(vm.getId());
   }

   @Test(dependsOnMethods = "testCreateVirtualMachine")
   public void testUpdateVirtualMachineWhenNotDeployed() {
      vm.setNameLabel(PREFIX + "VM Kane Updated");
      AsyncTask task = vm.update();
      assertNull(task);

      VirtualMachine updated = vapp.findVirtualMachine(VirtualMachinePredicates.nameLabel(PREFIX + "VM Kane Updated"));
      assertNotNull(updated);
   }

   @Test(dependsOnMethods = "testUpdateVirtualMachineWhenNotDeployed")
   public void testDeployVirtualMachine() {
      AsyncTask task = vm.deploy(true);
      assertNotNull(task);

      monitoringService.getVirtualMachineMonitor().awaitCompletionDeploy(MAX_WAIT, TimeUnit.MINUTES, vm);
      assertEquals(vm.getState(), VirtualMachineState.ON);
   }

   @Test(dependsOnMethods = "testDeployVirtualMachine")
   public void testChangeVirtualMachineState() {
      AsyncTask task = vm.changeState(VirtualMachineState.OFF);
      assertNotNull(task);

      monitoringService.getVirtualMachineMonitor().awaitState(MAX_WAIT, TimeUnit.MINUTES, VirtualMachineState.OFF, vm);
      assertEquals(vm.getState(), VirtualMachineState.OFF);
   }

   @Test(dependsOnMethods = "testChangeVirtualMachineState")
   public void testReconfigure() {
      Ip<?, ?> ip = getLast(vdc.getDefaultNetwork().listUnusedIps());

      AsyncTask task = vm.setNics(Lists.<Ip<?, ?>> newArrayList(ip));
      assertNotNull(task);

      monitoringService.getVirtualMachineMonitor().awaitState(MAX_WAIT, TimeUnit.MINUTES, VirtualMachineState.OFF, vm);
      assertNotNull(vm.findAttachedNic(IpPredicates.address(ip.getIp())));
   }

   @Test(dependsOnMethods = "testReconfigure")
   public void testUndeployVirtualMachine() {
      AsyncTask task = vm.undeploy();
      assertNotNull(task);

      monitoringService.getVirtualMachineMonitor().awaitCompletionUndeploy(MAX_WAIT, TimeUnit.MINUTES, vm);
      assertEquals(vm.getState(), VirtualMachineState.NOT_ALLOCATED);
   }

   @Test(dependsOnMethods = "testUndeployVirtualMachine")
   public void testDeployFailsWhenHardLimitsAreExceeded() {
      Enterprise ent = view.getAdministrationService().getCurrentEnterprise();

      if (vdc.getCpuCountHardLimit() != 0) {
         vm.setCpu(vdc.getCpuCountHardLimit() + 1);
      } else if (ent.getCpuCountHardLimit() != 0) {
         vm.setCpu(ent.getCpuCountHardLimit() + 1);
      }

      AsyncTask task = vm.update();
      assertNull(task);

      try {
         vm.deploy(true);
         fail("Deployments over the hard limits should not be allowed");
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.CONFLICT, "LIMIT_EXCEEDED");
      }
   }

   @Test(dependsOnMethods = "testDeployFailsWhenHardLimitsAreExceeded")
   public void tesDeleteVirtualMachine() {
      Integer vmId = vm.getId();
      vm.delete();
      assertNull(vapp.getVirtualMachine(vmId));
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
