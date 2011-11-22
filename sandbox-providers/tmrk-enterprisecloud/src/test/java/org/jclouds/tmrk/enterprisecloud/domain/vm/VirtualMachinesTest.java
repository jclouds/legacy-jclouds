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
package org.jclouds.tmrk.enterprisecloud.domain.vm;

import org.jclouds.tmrk.enterprisecloud.domain.Actions;
import org.jclouds.tmrk.enterprisecloud.domain.Links;
import org.jclouds.tmrk.enterprisecloud.domain.Tasks;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Jason King
 */
@Test(groups = "unit", testName = "ActionsTest")
public class VirtualMachinesTest {

   private VirtualMachine virtualMachine;
   private VirtualMachines virtualMachines;

   @BeforeMethod()
   public void setUp() throws URISyntaxException {
      virtualMachine = VirtualMachine.builder()
            .description("This is a test VM")
            .name("Test VM")
            .href(new URI("/test"))
            .type("Test VM")
            .actions(Actions.builder().build().getActions())
            .links(Links.builder().build().getLinks())
            .tasks(Tasks.builder().build().getTasks())
            .status(VirtualMachine.VirtualMachineStatus.NOT_DEPLOYED)
            .ipAddresses(new VirtualMachineIpAddresses())
            .build();

      virtualMachines = VirtualMachines.builder().addVirtualMachine(virtualMachine).build();
   }

   @Test
   public void testAddAction() throws URISyntaxException {
      VirtualMachine virtualMachine2 = VirtualMachine.builder()
                .description("This is a test VM 2")
                .name("Test VM 2")
                .href(new URI("/test/2"))
                .type("Test VM 2")
                .actions(Actions.builder().build().getActions())
                .links(Links.builder().build().getLinks())
                .tasks(Tasks.builder().build().getTasks())
                .status(VirtualMachine.VirtualMachineStatus.NOT_DEPLOYED)
                .ipAddresses(new VirtualMachineIpAddresses())
                .build();

      VirtualMachines twoVirtualMachines = virtualMachines.toBuilder().addVirtualMachine(virtualMachine2).build();
      Set<VirtualMachine> virtualMachineSet = twoVirtualMachines.getVirtualMachines();
      assertEquals(2, virtualMachineSet.size());
      assertTrue(virtualMachineSet.contains(virtualMachine));
      assertTrue(virtualMachineSet.contains(virtualMachine2));
   }
}
