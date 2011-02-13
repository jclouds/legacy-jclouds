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

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.NIC;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
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
   public void testCreateDestroyVirtualMachine() throws Exception {
      VirtualMachine vm = null;
      try {
         long serviceOfferingId = 1;//Iterables.get(client.getOfferingClient().listServiceOfferings(), 0).getId();
         long templateId = 2;//Iterables.get(client.getTemplateClient().listTemplates(), 0).getId();
         long zoneId = 1;//Iterables.get(client.getZoneClient().listZones(), 0).getId();
         long networkId = 204;//Iterables.get(client.getNetworkClient().listNetworks(), 0).getId();
         System.out.printf("serviceOfferingId %d, templateId %d, zoneId %d, networkId %d%n", serviceOfferingId, templateId, zoneId, networkId);
         AsyncCreateResponse job = client.getVirtualMachineClient().deployVirtualMachine(serviceOfferingId, templateId, zoneId,  DeployVirtualMachineOptions.Builder.networkId(networkId));
         System.out.println("job: " + job);
         vm = client.getVirtualMachineClient().getVirtualMachine(job.getId());
         System.out.println("vm: " + vm);
         assertEquals(vm.getServiceOfferingId(), serviceOfferingId);
         assertEquals(vm.getTemplateId(), templateId);
         assertEquals(vm.getZoneId(), zoneId);
         checkVm(vm);
      } finally {
         if (vm != null) {
            Long job = client.getVirtualMachineClient().destroyVirtualMachine(vm.getId());
            assert job != null;
            assertEquals(client.getVirtualMachineClient().getVirtualMachine(vm.getId()), null);
         }
      }

   }

   public void testListVirtualMachines() throws Exception {
      Set<VirtualMachine> response = client.getVirtualMachineClient().listVirtualMachines();
      System.out.println(response);
      assert null != response;
      assertTrue(response.size() >= 0);
      for (VirtualMachine vm : response) {
         VirtualMachine newDetails = Iterables.getOnlyElement(client.getVirtualMachineClient().listVirtualMachines(
               ListVirtualMachinesOptions.Builder.id(vm.getId())));
         assertEquals(vm, newDetails);
         checkVm(vm);
      }
   }

   protected void checkVm(VirtualMachine vm) {
      assertEquals(vm, client.getVirtualMachineClient().getVirtualMachine(vm.getId()));
      assert vm.getId() > 0 : vm;
      assert vm.getName() != null : vm;
      assert vm.getDisplayName() != null : vm;
      assert vm.getAccount() != null : vm;
      assert vm.getDomain() != null : vm;
      assert vm.getDomainId() > 0 : vm;
      assert vm.getCreated() != null : vm;
      assert vm.getState() != null : vm;
      assert vm.getZoneId() > 0 : vm;
      assert vm.getZoneName() != null : vm;
      assert vm.getTemplateId() > 0 : vm;
      assert vm.getTemplateName() != null : vm;
      assert vm.getTemplateDisplayText() != null : vm;
      assert vm.getServiceOfferingId() > 0 : vm;
      assert vm.getServiceOfferingName() != null : vm;
      assert vm.getCpuCount() > 0 : vm;
      assert vm.getCpuSpeed() > 0 : vm;
      assert vm.getMemory() > 0 : vm;
      assert vm.getGuestOSId() > 0 : vm;
      assert vm.getRootDeviceId() >= 0 : vm;
      assert vm.getRootDeviceType() != null : vm;
      if (vm.getJobId() != null)
         assert vm.getJobStatus() != null : vm;
      assert vm.getNICs() != null && vm.getNICs().size() > 0 : vm;
      for (NIC nic : vm.getNICs()) {
         assert nic.getId() > 0 : vm;
         assert nic.getNetworkId() > 0 : vm;
         assert nic.getNetmask() != null : vm;
         assert nic.getGateway() != null : vm;
         assert nic.getIPAddress() != null : vm;
         assert nic.getTrafficType() != null : vm;
         assert nic.getGuestIPType() != null : vm;
      }
      assert vm.getSecurityGroups() != null && vm.getSecurityGroups().size() >= 0 : vm;
      assert vm.getHypervisor() != null : vm;
   }
}
