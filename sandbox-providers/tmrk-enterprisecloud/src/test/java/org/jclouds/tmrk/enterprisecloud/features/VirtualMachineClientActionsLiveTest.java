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
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachine;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
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
      Task task = client.powerOn(vm.getHref());
      RetryablePredicate retryablePredicate = new RetryablePredicate(taskFinished(), 1000*60);
      if (!retryablePredicate.apply(task)) {
         fail("Did not manage to powerOn VM within timeout");
      }

      vm = client.getVirtualMachine(vm.getHref());
      assertTrue(vm.isPoweredOn());
   }

   @Test(dependsOnMethods = "testPowerOn")
   public void testShutdown() {
      //System.out.println("shutdown");
      //Needs tool status Current/OutOfDate
      //check state
      //shutdown
      //wait until done
      //power on
   }

   @Test(dependsOnMethods = "testShutdown")
   public void testReboot() {
      //System.out.println("reboot");
      //Needs tool status Current/OutOfDate
      //check state
      //reboot
      //wait until done
   }

   //@Test(dependsOnMethods = "testReboot")
   @Test(dependsOnMethods = "testPowerOn")
   public void testPowerOff() {
      Task task = client.powerOff(vm.getHref());
      RetryablePredicate retryablePredicate = new RetryablePredicate(taskFinished(), 1000*60);
      if (!retryablePredicate.apply(task)) {
         fail("Did not manage to powerOff VM within timeout");
      }

      vm = client.getVirtualMachine(vm.getHref());
      assertFalse(vm.isPoweredOn());
   }

   // Probably generally useful
   private Predicate taskFinished() {
      return new Predicate<Task>() {
         @Override
         public boolean apply(@Nullable Task task) {
            TaskClient taskClient = context.getApi().getTaskClient();
            task = taskClient.getTask(task.getHref());
            Task.Status status = task.getStatus();
            switch(status) {
               case QUEUED:
               case RUNNING:
                  return false;
               case COMPLETE:
               case SUCCESS:
                  return true;
               default:
                  throw new RuntimeException("Task Failed:"+task.getHref()+", Status:"+status);
            }
         }
      };
   }

}
