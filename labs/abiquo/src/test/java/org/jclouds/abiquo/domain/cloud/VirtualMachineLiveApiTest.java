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

import static org.jclouds.abiquo.reference.AbiquoTestConstants.PREFIX;
import static org.jclouds.abiquo.util.Assert.assertHasError;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.domain.task.AsyncTask;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineState;

/**
 * Live integration tests for the {@link VirtualMachine} domain class.
 * 
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "VirtualMachineLiveApiTest")
public class VirtualMachineLiveApiTest extends BaseAbiquoApiLiveApiTest {
   public void testHasDataFromNode() {
      assertNotNull(env.virtualMachine.getNameLabel());
      assertNotNull(env.virtualMachine.getInternalName());
      assertNotNull(env.virtualMachine.getOwnerName());
   }

   public void testUpdateInfoFromNode() {
      env.virtualMachine.setNameLabel(PREFIX + "-label-updated");
      env.virtualMachine.update();
      env.virtualMachine.refresh();

      assertEquals(env.virtualMachine.getNameLabel(), PREFIX + "-label-updated");
   }

   public void testGetTasks() {
      List<AsyncTask> tasks = env.virtualMachine.listTasks();
      assertNotNull(tasks);
   }

   public void testGetState() {
      VirtualMachineState state = env.virtualMachine.getState();
      assertEquals(state, VirtualMachineState.NOT_ALLOCATED);
   }

   public void testIsPersistent() {
      assertFalse(env.virtualMachine.isPersistent());
   }

   public void testGetVirtualAppliance() {
      VirtualAppliance vapp = env.virtualMachine.getVirtualAppliance();
      assertNotNull(vapp);
      assertEquals(vapp.getId(), env.virtualAppliance.getId());
   }

   public void testRebootVirtualMachineFailsWhenNotAllocated() {
      // Since the virtual machine is not deployed, this should not generate a
      // task

      try {
         env.virtualMachine.reboot();
         fail("Reboot should have failed for the NOT_ALLOCATED virtual machine");
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.CONFLICT, "VM-11");
      }
   }

   public void testUpdateForcingLimits() {
      int originalHard = env.virtualDatacenter.getCpuCountHardLimit();
      int originalSoft = env.virtualDatacenter.getCpuCountSoftLimit();

      env.virtualDatacenter.setCpuCountHardLimit(10);
      env.virtualDatacenter.setCpuCountSoftLimit(5);
      env.virtualDatacenter.update();

      try {
         VirtualMachine vm = env.virtualAppliance.getVirtualMachine(env.virtualMachine.getId());
         vm.setCpu(7);
         AsyncTask task = vm.update(true);

         assertNull(task);
         assertEquals(vm.getCpu(), 7);
      } finally {
         env.virtualDatacenter.setCpuCountHardLimit(originalHard);
         env.virtualDatacenter.setCpuCountSoftLimit(originalSoft);
         env.virtualDatacenter.update();
      }
   }

   public void testAttachDvd() {
      VirtualMachine vm = VirtualMachine.Builder.fromVirtualMachine(env.virtualMachine).dvd(true).build();
      vm.save();

      VirtualMachineDto updated = env.cloudApi.getVirtualMachine(env.virtualAppliance.unwrap(), vm.getId());

      assertNotNull(updated.getDvd());
   }
}
