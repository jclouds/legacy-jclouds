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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.hardware.HardwareConfiguration;
import org.jclouds.tmrk.enterprisecloud.domain.internal.AnonymousResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;
import org.jclouds.tmrk.enterprisecloud.domain.layout.LayoutRequest;
import org.jclouds.tmrk.enterprisecloud.domain.network.*;
import org.jclouds.tmrk.enterprisecloud.domain.vm.CreateVirtualMachine;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachine;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineConfigurationOptions;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachines;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * Tests behavior of {@code VirtualMachineClient}
 * TODO: don't hard-code uri's it should be possible to determine them but that means chaining the tests potentially.
 * @author Jason King
 */
@Test(groups = "live", testName = "VirtualMachineClientLiveTest")
public class VirtualMachineClientLiveTest extends BaseTerremarkEnterpriseCloudClientLiveTest {
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getVirtualMachineClient();
   }

   private VirtualMachineClient client;

   public void testGetVirtualMachines() throws Exception {
       VirtualMachines virtualMachines = client.getVirtualMachines(new URI("/cloudapi/ecloud/virtualMachines/computePools/89"));
       for( VirtualMachine vm : virtualMachines.getVirtualMachines()) {
           VirtualMachine virtualMachine = client.getVirtualMachine(vm.getHref());
           assertNotNull(virtualMachine);
           assertEquals(virtualMachine.getStatus(),VirtualMachine.VirtualMachineStatus.DEPLOYED);
       }
   }

   public void testGetVirtualMachinesWhenMissing() throws Exception {
       VirtualMachines result = client.getVirtualMachines(new URI("/cloudapi/ecloud/virtualMachines/computePools/-1"));
       assertEquals(result, VirtualMachines.builder().build());
   }

   public void testGetVirtualMachine() throws Exception {
       VirtualMachine virtualMachine = client.getVirtualMachine(new URI("/cloudapi/ecloud/virtualMachines/5504"));
       assertNotNull(virtualMachine,"virtualMachine should not be null");
       assertEquals(virtualMachine.getStatus(), VirtualMachine.VirtualMachineStatus.DEPLOYED);
   }

   public void testGetVirtualMachineWhenMissing() throws Exception {
       VirtualMachine virtualMachine = client.getVirtualMachine(new URI("/cloudapi/ecloud/virtualMachines/-1"));
       assertNull(virtualMachine);
   }

   public void testGetAssignedIpAddresses() throws Exception {
        AssignedIpAddresses assignedIpAddresses = client.getAssignedIpAddresses(new URI("/cloudapi/ecloud/virtualMachines/5504/assignedips"));
        assertNotNull(assignedIpAddresses);
        DeviceNetwork network = Iterables.getOnlyElement(assignedIpAddresses.getNetworks().getDeviceNetworks());
        Set<String> ipAddresses = network.getIpAddresses().getIpAddresses();
        assertTrue(ipAddresses.size()>0, "vm has no assigned ip addresses");
   }

   public void testGetAssignedIpAddressesWhenMissing() throws Exception {
        AssignedIpAddresses assignedIpAddresses = client.getAssignedIpAddresses(new URI("/cloudapi/ecloud/virtualMachines/-1/assignedips"));
        assertNull(assignedIpAddresses);
   }

   public void testGetConfigurationOptions() throws Exception {
      VirtualMachineConfigurationOptions configurationOptions = client.getConfigurationOptions(new URI("/cloudapi/ecloud/virtualmachines/5504/configurationoptions"));
      assertNotNull(configurationOptions);
   }

   public void testGetHardwareConfiguration() throws Exception {
      HardwareConfiguration hardwareConfiguration = client.getHardwareConfiguration(new URI("/cloudapi/ecloud/virtualmachines/5504/hardwareconfiguration"));
      assertNotNull(hardwareConfiguration);
   }

   public void testGetHardwareConfigurationWhenMissing() throws Exception {
      HardwareConfiguration result = client.getHardwareConfiguration(new URI("/cloudapi/ecloud/virtualmachines/-1/hardwareconfiguration"));
      assertNull(result);
   }

   public void testCreateVirtualMachineFromTemplate() throws Exception {
      CreateVirtualMachine.Builder builder = CreateVirtualMachine.builder();
      builder.name("VirtualMachine2")
            .processorCount(2)
            .memory(ResourceCapacity.builder().value(1024).unit("MB").build());

      AnonymousResource group = AnonymousResource.builder().href(URI.create("/cloudapi/ecloud/layoutgroups/308")).type("application/vnd.tmrk.cloud.layoutGroup").build();
      builder.layout(LayoutRequest.builder().group(group).build());
      builder.description("This is my first VM");
      builder.tags(ImmutableSet.of("Web"));
      AnonymousResource sshKey = AnonymousResource.builder().href(URI.create("/cloudapi/ecloud/admin/sshkeys/77")).type("application/vnd.tmrk.cloud.admin.sshKey").build();

      NamedResource network = NamedResource.builder()
            .href(URI.create("/cloudapi/ecloud/networks/3936"))
            .name("10.146.204.64/28")
            .type("application/vnd.tmrk.cloud.network")
            .build();

      NetworkAdapterSetting adapterSetting = NetworkAdapterSetting.builder()
            .network(network)
            .ipAddress("10.146.204.68")
            .build();

      NetworkAdapterSettings adapterSettings = NetworkAdapterSettings.builder()
            .addNetworkAdapterSetting(adapterSetting).build();
      NetworkSettings networkSettings = NetworkSettings.builder().networkAdapterSettings(adapterSettings).build();

      LinuxCustomization linuxCustomization = LinuxCustomization.builder()
            .sshKey(sshKey)
            .networkSettings(networkSettings)
            .build();
      builder.linuxCustomization(linuxCustomization);

      AnonymousResource template = AnonymousResource.builder().href(URI.create("/cloudapi/ecloud/templates/6/computepools/89")).type("application/vnd.tmrk.cloud.template").build();
      builder.template(template);

      VirtualMachine vm = client.createVirtualMachineFromTemplate(URI.create("/cloudapi/ecloud/virtualMachines/computePools/89/action/createVirtualMachine"), builder.build());
      assertNotNull(vm);

      // TODO: Check that the VM is created OK.
      // TODO: DNSSettings are missing
      //client.remove(vm.getHref()); //remove once verified - there needs to be no running tasks.
   }
}
