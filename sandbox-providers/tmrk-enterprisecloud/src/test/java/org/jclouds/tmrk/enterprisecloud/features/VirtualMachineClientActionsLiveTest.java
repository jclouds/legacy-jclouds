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
package org.jclouds.tmrk.enterprisecloud.features;

import com.google.common.base.Predicate;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.jclouds.tmrk.enterprisecloud.domain.software.ToolsStatus;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachine;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;

/**
 * Tests behavior of {@code VirtualMachineClient} actions
 * @author Jason King
 */
@Test(groups = "live", testName = "VirtualMachineClientActionsLiveTest")
public class VirtualMachineClientActionsLiveTest extends BaseTerremarkEnterpriseCloudClientLiveTest {
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getVirtualMachineClient();
   }

   //TODO: Need a create call to make this not dependent on existing vm
   private static final String vmURI = "/cloudapi/ecloud/virtualmachines/5504";

   private VirtualMachineClient client;
   private VirtualMachine vm;

   public void testPowerOn() throws Exception {
      vm = client.getVirtualMachine(new URI(vmURI));
      assertFalse(vm.isPoweredOn());

      RetryablePredicate retryablePredicate = new RetryablePredicate(taskFinished(), 1000*60);
      if (!retryablePredicate.apply(client.powerOn(vm.getHref()))) {
         fail("Did not manage to finish powerOn task");
      }

      vm = client.getVirtualMachine(vm.getHref());
      assertTrue(vm.isPoweredOn());
   }

   @Test(dependsOnMethods = "testPowerOn")
   public void testMountTools() {
      if (!mountTools(vm.getHref())) {
         fail("Did not manage to finish mount tools task");
      }
   }

   @Test(dependsOnMethods = "testMountTools")
   public void testUnmountTools() {
      RetryablePredicate retryablePredicate = new RetryablePredicate(taskFinished(), 1000*60);
      if (!retryablePredicate.apply(client.unmountTools(vm.getHref()))) {
         fail("Did not manage finish unmount tools task");
      }
      //ToolsStatus remains in 'OutOfDate' after un-mounting.
      //There is no way to tell, other than to try un-mounting again.
   }

   @Test(dependsOnMethods = "testUnmountTools")
   public void testShutdown() {
      //Seems to work as ToolsStatus remains in OutOfDate state
      RetryablePredicate retryablePredicate = new RetryablePredicate(taskFinished(), 1000*60);
      if (!retryablePredicate.apply(client.shutdown(vm.getHref()))) {
         fail("Did not manage to finish shutdown task");
      }
      // Takes a while to powerOff
      retryablePredicate = new RetryablePredicate(poweredOff(), 1000*60);
      if (!retryablePredicate.apply(vm.getHref())) {
         fail("Did not manage to powerOff after shutdown");
      }

      vm = client.getVirtualMachine(vm.getHref());
      assertFalse(vm.isPoweredOn());
   }

   @Test(dependsOnMethods = "testShutdown")
   public void testReboot() {
      RetryablePredicate retryablePredicate = new RetryablePredicate(taskFinished(), 1000*60);
      if (!retryablePredicate.apply(client.powerOn(vm.getHref()))) {
         fail("Did not manage to finish powerOn task");
      }

      if (!mountTools(vm.getHref())) {
         fail("Did not manage to mount tools");
      }

      retryablePredicate = new RetryablePredicate(taskFinished(), 1000*60);
      if (!retryablePredicate.apply(client.reboot(vm.getHref()))) {
         fail("Did not manage to finish reboot task");
      }

      vm = client.getVirtualMachine(vm.getHref());
      assertTrue(vm.isPoweredOn());
   }

   @Test(dependsOnMethods = "testReboot")
   public void testPowerOff() {
      RetryablePredicate retryablePredicate = new RetryablePredicate(taskFinished(), 1000*60);
      if (!retryablePredicate.apply(client.powerOff(vm.getHref()))) {
         fail("Did not manage to finish powerOff task");
      }

      vm = client.getVirtualMachine(vm.getHref());
      assertFalse(vm.isPoweredOn());
   }

   /* TODO: Not ready to delete the 5504 VM until I can create one.
   @Test(dependsOnMethods = "testPowerOff")
   public void testRemove() throws URISyntaxException {
      // Don't want to delete quite yet!
      RetryablePredicate retryablePredicate = new RetryablePredicate(taskFinished(), 1000*60);
      if (!retryablePredicate.apply(client.remove(vm.getHref()))) {
         fail("Did not manage to finish remove task");
      }

      assertNull(client.getVirtualMachine(vm.getHref()));
   }
   */

   private boolean mountTools(URI uri) {
      // Wait for task to finish AND tools to get into currentOrOutOfDate state
      return new RetryablePredicate(taskFinished(), 1000*60).apply(client.mountTools(uri)) &&
             new RetryablePredicate(toolsCurrentOrOutOfDate(), 1000*60).apply(uri);
   }

   // Probably generally useful
   private Predicate taskFinished() {
      return new Predicate<Task>() {
         @Override
         public boolean apply(Task task) {
            TaskClient taskClient = context.getApi().getTaskClient();
            task = taskClient.getTask(task.getHref());
            switch(task.getStatus()) {
               case QUEUED:
               case RUNNING:
                  return false;
               case COMPLETE:
               case SUCCESS:
                  return true;
               default:
                  throw new RuntimeException("Task Failed:"+task.getHref()+", Status:"+task.getStatus());
            }
         }
      };
   }

   // Probably generally useful
   private Predicate toolsCurrentOrOutOfDate() {
      return new Predicate<URI>() {
         @Override
         public boolean apply(URI uri) {
            VirtualMachine virtualMachine = client.getVirtualMachine(uri);
            ToolsStatus toolsStatus = virtualMachine.getToolsStatus();
            switch(toolsStatus) {
               case NOT_INSTALLED:
               case NOT_RUNNING:
                  return false;
               case CURRENT:
               case OUT_OF_DATE:
                  return true;
               default:
                  throw new RuntimeException("Unable to determine toolsStatus for:"+uri+", ToolsStatus:"+toolsStatus);
            }
         }
      };
   }

   private Predicate poweredOff() {
      return new Predicate<URI>() {
         @Override
         public boolean apply(URI uri) {
            VirtualMachine virtualMachine = client.getVirtualMachine(uri);
            return !virtualMachine.isPoweredOn();
         }
      };
   }
}
