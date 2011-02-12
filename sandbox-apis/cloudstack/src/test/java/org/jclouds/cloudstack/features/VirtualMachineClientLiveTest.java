/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.NIC;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.options.ListVirtualMachinesOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code VirtualMachineClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "VirtualMachineClientLiveTest")
public class VirtualMachineClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testListVirtualMachines() throws Exception {
      Set<VirtualMachine> response = client.getVirtualMachineClient().listVirtualMachines();
      System.out.println(response);
      assert null != response;
      assertTrue(response.size() >= 0);
      for (VirtualMachine vm : response) {
         VirtualMachine newDetails = Iterables.getOnlyElement(client.getVirtualMachineClient().listVirtualMachines(
               ListVirtualMachinesOptions.Builder.id(vm.getId())));
         assertEquals(vm, newDetails);
         assertEquals(vm, client.getVirtualMachineClient().getVirtualMachine(vm.getId()));
         assert vm.getId() != null : vm;
         assert vm.getName() != null : vm;
         assert vm.getDisplayName() != null : vm;
         assert vm.getAccount() != null : vm;
         assert vm.getDomain() != null : vm;
         assert vm.getDomainId() != null : vm;
         assert vm.getCreated() != null : vm;
         assert vm.getState() != null : vm;
         assert vm.getZoneId() != null : vm;
         assert vm.getZoneName() != null : vm;
         assert vm.getTemplateId() != null : vm;
         assert vm.getTemplateName() != null : vm;
         assert vm.getTemplateDisplayText() != null : vm;
         assert vm.getServiceOfferingId() != null : vm;
         assert vm.getServiceOfferingName() != null : vm;
         assert vm.getCpuCount() > 0 : vm;
         assert vm.getCpuSpeed() > 0 : vm;
         assert vm.getMemory() > 0 : vm;
         assert vm.getGuestOSId() != null : vm;
         assert vm.getRootDeviceId() != null : vm;
         assert vm.getRootDeviceType() != null : vm;
         if (vm.getJobId() != null)
            assert vm.getJobStatus() != null : vm;
         assert vm.getNICs() != null && vm.getNICs().size() > 0 : vm;
         for (NIC nic : vm.getNICs()) {
            assert nic.getId() != null : vm;
            assert nic.getNetworkId() != null : vm;
            assert nic.getNetmask() != null : vm;
            assert nic.getGateway() != null : vm;
            assert nic.getIPAddress() != null : vm;
            assert nic.getTrafficType() != null : vm;
            assert nic.getGuestIPType() != null : vm;
         }
         assert vm.getHypervisor() != null : vm;
      }
   }
}
