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

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.or;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.GuestIPType;
import org.jclouds.cloudstack.domain.NIC;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.jclouds.cloudstack.options.ListVirtualMachinesOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

/**
 * Tests behavior of {@code VirtualMachineClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "VirtualMachineClientLiveTest")
public class VirtualMachineClientLiveTest extends BaseCloudStackClientLiveTest {
   private VirtualMachine vm = null;

   static final Ordering<ServiceOffering> DEFAULT_SIZE_ORDERING = new Ordering<ServiceOffering>() {
      public int compare(ServiceOffering left, ServiceOffering right) {
         return ComparisonChain.start().compare(left.getCpuNumber(), right.getCpuNumber())
               .compare(left.getMemory(), right.getMemory()).result();
      }
   };

   public static VirtualMachine createVirtualMachine(CloudStackClient client, RetryablePredicate<Long> jobComplete,
         RetryablePredicate<VirtualMachine> virtualMachineRunning) {
      final Zone zone = get(client.getZoneClient().listZones(), 0);

      long serviceOfferingId = DEFAULT_SIZE_ORDERING.min(client.getOfferingClient().listServiceOfferings()).getId();

      Iterable<Template> templates = filter(client.getTemplateClient().listTemplates(), new Predicate<Template>() {

         @Override
         public boolean apply(Template arg0) {
            return arg0.getZoneId() == zone.getId() && arg0.isFeatured() && arg0.isReady()
                  && or(equalTo("Ubuntu 10.04 (64-bit)"), equalTo("CentOS 5.3 (64-bit)")).apply(arg0.getOSType());
         }

      });
      if (Iterables.size(templates) == 0) {
         throw new NoSuchElementException();
      }
      long templateId;
      try {
         // prefer password enabled
         templateId = find(templates, new Predicate<Template>() {

            @Override
            public boolean apply(Template arg0) {
               return arg0.isPasswordEnabled();
            }

         }).getId();
      } catch (NoSuchElementException e) {
         templateId = get(templates, 0).getId();
      }

      DeployVirtualMachineOptions options = new DeployVirtualMachineOptions();
      if (zone.getNetworkType() == NetworkType.ADVANCED) {
         options.networkId(find(client.getNetworkClient().listNetworks(), new Predicate<Network>() {

            @Override
            public boolean apply(Network arg0) {
               return arg0.getZoneId() == zone.getId();
            }

         }).getId());
      } else {
         options.securityGroupId(find(client.getSecurityGroupClient().listSecurityGroups(),
               new Predicate<SecurityGroup>() {

                  @Override
                  public boolean apply(SecurityGroup arg0) {
                     return arg0.getName().equals("default");
                  }

               }).getId());
      }
      System.out.printf("serviceOfferingId %d, templateId %d, zoneId %d, options %s%n", serviceOfferingId, templateId,
            zone.getId(), options);
      AsyncCreateResponse job = client.getVirtualMachineClient().deployVirtualMachine(serviceOfferingId, templateId,
            zone.getId(), options);
      assert jobComplete.apply(job.getJobId());
      VirtualMachine vm = client.getAsyncJobClient().<VirtualMachine> getAsyncJob(job.getJobId()).getResult();
      if (vm.isPasswordEnabled())
         assert vm.getPassword() != null : vm;
      assert virtualMachineRunning.apply(vm);
      assertEquals(vm.getServiceOfferingId(), serviceOfferingId);
      assertEquals(vm.getTemplateId(), templateId);
      assertEquals(vm.getZoneId(), zone.getId());
      return vm;
   }

   public void testCreateVirtualMachine() throws Exception {
      vm = createVirtualMachine(client, jobComplete, virtualMachineRunning);
      assert or(equalTo("NetworkFilesystem"), equalTo("IscsiLUN")).apply(vm.getRootDeviceType()) : vm;
      checkVm(vm);
   }

   @Test(dependsOnMethods = "testCreateVirtualMachine")
   public void testLifeCycle() throws Exception {
      Long job = client.getVirtualMachineClient().stopVirtualMachine(vm.getId());
      assert jobComplete.apply(job);
      vm = client.getVirtualMachineClient().getVirtualMachine(vm.getId());
      assertEquals(vm.getState(), VirtualMachine.State.STOPPED);

      job = client.getVirtualMachineClient().resetPasswordForVirtualMachine(vm.getId());
      assert jobComplete.apply(job);
      vm = client.getVirtualMachineClient().getVirtualMachine(vm.getId());
      // TODO: check if error or not base on isPasswordEnabled

      job = client.getVirtualMachineClient().startVirtualMachine(vm.getId());
      assert jobComplete.apply(job);
      vm = client.getVirtualMachineClient().getVirtualMachine(vm.getId());
      assertEquals(vm.getState(), VirtualMachine.State.RUNNING);

      job = client.getVirtualMachineClient().rebootVirtualMachine(vm.getId());
      assert jobComplete.apply(job);
      vm = client.getVirtualMachineClient().getVirtualMachine(vm.getId());
      assertEquals(vm.getState(), VirtualMachine.State.RUNNING);
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (vm != null) {
         Long job = client.getVirtualMachineClient().destroyVirtualMachine(vm.getId());
         assert job != null;
         assert jobComplete.apply(job);
         assert virtualMachineDestroyed.apply(vm);
      }
      super.tearDown();
   }

   public void testListVirtualMachines() throws Exception {
      Set<VirtualMachine> response = client.getVirtualMachineClient().listVirtualMachines();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (VirtualMachine vm : response) {
         VirtualMachine newDetails = getOnlyElement(client.getVirtualMachineClient().listVirtualMachines(
               ListVirtualMachinesOptions.Builder.id(vm.getId())));
         assertEquals(vm.getId(), newDetails.getId());
         checkVm(vm);
      }
   }

   protected void checkVm(VirtualMachine vm) {
      assertEquals(vm.getId(), client.getVirtualMachineClient().getVirtualMachine(vm.getId()).getId());
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
      // assert vm.getRootDeviceType() != null : vm;
      if (vm.getJobId() != null)
         assert vm.getJobStatus() != null : vm;
      assert vm.getNICs() != null && vm.getNICs().size() > 0 : vm;
      for (NIC nic : vm.getNICs()) {
         assert nic.getId() > 0 : vm;
         assert nic.getNetworkId() > 0 : vm;
         assert nic.getTrafficType() != null : vm;
         assert nic.getGuestIPType() != null : vm;
         switch (vm.getState()) {
         case RUNNING:
            assert nic.getNetmask() != null : vm;
            assert nic.getGateway() != null : vm;
            assert nic.getIPAddress() != null : vm;
            break;
         default:
            if (nic.getGuestIPType() == GuestIPType.VIRTUAL) {
               assert nic.getNetmask() != null : vm;
               assert nic.getGateway() != null : vm;
               assert nic.getIPAddress() != null : vm;
            } else {
               assert nic.getNetmask() == null : vm;
               assert nic.getGateway() == null : vm;
               assert nic.getIPAddress() == null : vm;
            }
            break;
         }

      }
      assert vm.getSecurityGroups() != null && vm.getSecurityGroups().size() >= 0 : vm;
      assert vm.getHypervisor() != null : vm;
   }
}
