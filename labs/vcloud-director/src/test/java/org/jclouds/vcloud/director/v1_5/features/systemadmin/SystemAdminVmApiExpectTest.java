/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.features.systemadmin;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.dmtf.ovf.StartupSection;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.RasdItemsList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ScreenTicket;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.VmPendingQuestion;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswer;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.RasdItem;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.section.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.section.RuntimeInfoSection;
import org.jclouds.vcloud.director.v1_5.domain.section.VirtualHardwareSection;
import org.jclouds.vcloud.director.v1_5.features.VmApi;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

/**
 * Allows us to test the {@link VmApi} allowed to system administrators
 * 
 * @author andrea turli
 */
@Test(groups = { "unit", "systemAdmin" }, singleThreaded = true, testName = "SystemAdminVmApiExpectTest")
public class SystemAdminVmApiExpectTest extends VCloudDirectorAdminApiExpectTest {
   
   private String vmId = "vm-d0e2b6b9-4381-4ddc-9572-cdfae54059be";
   private URI vmURI = URI.create(endpoint + vmId);
   
   @BeforeClass
   public void before() {
   }
   
   @Test(enabled = false)
   public void testRelocate() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/relocate")
            .xmlFilePayload("/vApp/relocateParams.xml", VCloudDirectorMediaType.RELOCATE_VM_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/relocateTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
     
      RelocateParams params = RelocateParams.builder()
            .build();
		
		Task expected = relocateTask();
		
		assertEquals(api.getVmApi().relocate(vmURI, params), expected);
   }
   

   public static Vm getVm() {
      // FIXME Does not match XML
      Vm vm = Vm.builder()
            .href(URI.create("https://mycloud.greenhousedata.com/api/vApp/vm-d0e2b6b9-4381-4ddc-9572-cdfae54059be"))
//            .link(Link.builder()
//                     .href(URI.create())
//                     .build())
            .build();

//      <Link rel="power:powerOn" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/power/action/powerOn"/>
//      <Link rel="deploy" type="application/vnd.vmware.vcloud.deployVAppParams+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/action/deploy"/>
//      <Link rel="down" type="application/vnd.vmware.vcloud.vAppNetwork+xml" name="orgNet-cloudsoft-External" href="https://mycloud.greenhousedata.com/api/network/2a2e2da4-446a-4ebc-a086-06df7c9570f0"/>
//      <Link rel="down" type="application/vnd.vmware.vcloud.controlAccess+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/controlAccess/"/>
//      <Link rel="controlAccess" type="application/vnd.vmware.vcloud.controlAccess+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/action/controlAccess"/>
//      <Link rel="recompose" type="application/vnd.vmware.vcloud.recomposeVAppParams+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/action/recomposeVApp"/>
//      <Link rel="up" type="application/vnd.vmware.vcloud.vdc+xml" href="https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f"/>
//      <Link rel="edit" type="application/vnd.vmware.vcloud.vApp+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be"/>
//      <Link rel="remove" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be"/>
//      <Link rel="down" type="application/vnd.vmware.vcloud.owner+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/owner"/>
//      <Link rel="down" type="application/vnd.vmware.vcloud.metadata+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/metadata"/>
      
      return vm;
   }

   public static Task editVmTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task removeVmTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task consolidateVmTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static ControlAccessParams controlAccessParams() {
      ControlAccessParams params = ControlAccessParams.builder()
            .build();

      return params;
   }

   public static Task deployTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task discardSuspendedStateTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task installVMwareToolsTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task recomposeVmTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task relocateTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task undeployTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task upgradeHardwareVersionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task powerOffTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task powerOnTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task rebootTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task resetTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task shutdownTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task suspendTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static ControlAccessParams getAccessControlParams() {
      ControlAccessParams params = ControlAccessParams.builder()
            .build();

      return params;
   }

   public static GuestCustomizationSection getGuestCustomizationSection() {
      GuestCustomizationSection section = GuestCustomizationSection.builder()
            .build();

      return section;
   }

   public static Task editGuestCustomizationSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static LeaseSettingsSection getLeaseSettingsSection() {
      LeaseSettingsSection section = LeaseSettingsSection.builder()
            .build();

      return section;
   }

   public static Task editLeaseSettingsSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task ejectMediaTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task insertMediaTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static NetworkConfigSection getNetworkConfigSection() {
      NetworkConfigSection section = NetworkConfigSection.builder()
            .build();

      return section;
   }

   public static Task editNetworkConfigSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static NetworkConnectionSection getNetworkConnectionSection() {
      NetworkConnectionSection section = NetworkConnectionSection.builder()
            .build();

      return section;
   }

   public static Task editNetworkConnectionSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static NetworkSection getNetworkSection() {
      NetworkSection section = NetworkSection.builder()
            .build();

      return section;
   }

   public static OperatingSystemSection getOperatingSystemSection() {
      OperatingSystemSection section = OperatingSystemSection.builder()
            .build();

      return section;
   }

   public static Task editOperatingSystemSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Owner getOwner() {
      Owner owner = Owner.builder()
            .build();

      return owner;
   }

   public static Task editOwnerTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static ProductSectionList getProductSections() {
      ProductSectionList sectionItems = ProductSectionList.builder()
            .build();

      return sectionItems;
   }

   public static Task editProductSectionsTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static VmPendingQuestion getPendingQuestion() {
      VmPendingQuestion question = VmPendingQuestion.builder()
            .build();

      return question;
   }

   public static VmQuestionAnswer answerQuestion() {
      VmQuestionAnswer answer = null; // = VmQuestionAnswer.builder() 
//            .build();

      return answer;
   }

   public static RuntimeInfoSection getRuntimeInfoSection() {
      RuntimeInfoSection section = RuntimeInfoSection.builder()
            .build();

      return section;
   }

   public static byte[] getScreenImage() {
      byte[] image = new byte[0];

      return image;
   }

   public static ScreenTicket getScreenTicket() {
      ScreenTicket ticket = null; // = ScreenTicket.builder();
//            .build();

      return ticket;
   }

   public static StartupSection getStartupSection() {
      StartupSection section = null; // = StartupSection.builder();
//            .build();

      return section;
   }

   public static Task editStartupSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static VirtualHardwareSection getVirtualHardwareSection() {
      VirtualHardwareSection section = VirtualHardwareSection.builder()
            .build();

      return section;
   }

   public static Task editVirtualHardwareSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItem getVirtualHardwareSectionCpu() {
      RasdItem cpu = RasdItem.builder()
            .build();

      return cpu;
   }

   public static Task editVirtualHardwareSectionCpuTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionDisks() {
      RasdItemsList disks = RasdItemsList.builder()
            .build();

      return disks;
   }

   public static Task editVirtualHardwareSectionDisksTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionMedia() {
      RasdItemsList media = RasdItemsList.builder()
            .build();

      return media;
   }

   public static RasdItem getVirtualHardwareSectionMemory() {
      RasdItem memory = RasdItem.builder()
            .build();

      return memory;
   }

   public static Task editVirtualHardwareSectionMemoryTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionNetworkCards() {
      RasdItemsList networkCards = RasdItemsList.builder()
            .build();

      return networkCards;
   }

   public static Task editVirtualHardwareSectionNetworkCardsTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionSerialPorts() {
      RasdItemsList serialPorts = RasdItemsList.builder()
            .build();

      return serialPorts;
   }

   public static Task editVirtualHardwareSectionSerialPortsTask() {
      return task("id", "name", "description", "status", "operation", "operationName", "startTime");
   }

   /** Used by other methods to add a custom {@link Task} object. */
   private static Task task(String taskId, String name, String description, String status, String operation, String operationName, String startTime) {
      Task task = Task.builder()
            .error(Error.builder().build())
            .org(Reference.builder().build())
            .owner(Reference.builder().build())
            .user(Reference.builder().build())
            .params(null)
            .progress(0)
            .status(status)
            .operation(operation)
            .operationName(operationName)
            .startTime(dateService.iso8601DateParse(startTime))
            .endTime(null)
            .expiryTime(null)
            .tasks(Sets.<Task>newLinkedHashSet())
            .description(description)
            .name(name)
            .id("urn:vcloud:" + taskId)
            .href(URI.create(endpoint + "/task/" + taskId))
            .links(Sets.<Link>newLinkedHashSet())
            .build();

      return task;
   }
}
