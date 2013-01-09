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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.CORRECT_VALUE_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.ENTITY_EQUAL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.MEDIA;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkGuestCustomizationSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadata;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataKeyAbsentFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkNetworkConnectionSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkOperatingSystemSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkProductSectionList;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkRasdItemsList;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkResourceAllocationSettingData;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkRuntimeInfoSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkScreenTicket;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkVirtualHardwareSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkVm;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkVmPendingQuestion;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.dmtf.cim.OSType;
import org.jclouds.dmtf.cim.ResourceAllocationSettingData;
import org.jclouds.dmtf.ovf.MsgType;
import org.jclouds.dmtf.ovf.ProductSection;
import org.jclouds.vcloud.director.v1_5.AbstractVAppApiLiveTest;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.RasdItemsList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status;
import org.jclouds.vcloud.director.v1_5.domain.ScreenTicket;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.VmPendingQuestion;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswer;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswerChoice;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.RasdItem;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConnection;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConnection.IpAddressAllocationMode;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.MediaInsertOrEjectParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.section.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.section.RuntimeInfoSection;
import org.jclouds.vcloud.director.v1_5.domain.section.VirtualHardwareSection;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Tests behavior of the {@link VmApi}.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "VmApiLiveTest")
public class VmApiLiveTest extends AbstractVAppApiLiveTest {

   private String metadataValue;
   private String key;
   private boolean testUserCreated = false;

   @BeforeClass(alwaysRun = true)
   protected void setupRequiredEntities() {

      if (adminContext != null) {
         userUrn = adminContext.getApi().getUserApi().addUserToOrg(randomTestUser("VAppAccessTest"), org.getId())
                  .getId();
      }
   }

   @AfterClass(alwaysRun = true, dependsOnMethods = { "cleanUpEnvironment" })
   public void cleanUp() {
      if (adminContext != null && testUserCreated && userUrn != null) {
         try {
            adminContext.getApi().getUserApi().remove(userUrn);
         } catch (Exception e) {
            logger.warn("Error when deleting user: %s", e.getMessage());
         }
      }
   }

   /**
    * @see VmApi#get(String)
    */
   @Test(description = "GET /vApp/{id}")
   public void testGetVm() {
      // The method under test
      vm = vmApi.get(vmUrn);

      // Check the retrieved object is well formed
      checkVm(vm);

      // Check the required fields are set
      assertEquals(vm.isDeployed(), Boolean.FALSE,
               String.format(OBJ_FIELD_EQ, VM, "deployed", "FALSE", vm.isDeployed().toString()));
      String vAppNetworkName = context.getApi().getNetworkApi().get(networkUrn).getName();
      attachVmToVAppNetwork(vm, vAppNetworkName);

      // Check status
      assertVmStatus(vmUrn, Status.POWERED_OFF);
   }

   /**
    * @see VmApi#edit(String, Vm)
    */
   @Test(description = "PUT /vApp/{id}", dependsOnMethods = { "testGetVm" })
   public void testEditVm() {
      Vm newVm = Vm.builder().name(name("new-name-")).description("New Description").build();

      // The method under test
      Task editVm = vmApi.edit(vmUrn, newVm);
      assertTrue(retryTaskSuccess.apply(editVm), String.format(TASK_COMPLETE_TIMELY, "editVm"));

      // Get the edited Vm
      vm = vmApi.get(vmUrn);

      // Check the required fields are set
      assertEquals(vm.getName(), newVm.getName(),
               String.format(OBJ_FIELD_EQ, VM, "Name", newVm.getName(), vm.getName()));
      assertEquals(vm.getDescription(), newVm.getDescription(),
               String.format(OBJ_FIELD_EQ, VM, "Description", newVm.getDescription(), vm.getDescription()));
   }

   @Test(description = "POST /vApp/{id}/action/deploy", dependsOnMethods = { "testGetVm" })
   public void testDeployVm() {
      DeployVAppParams params = DeployVAppParams.builder()
               .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS)).notForceCustomization()
               .notPowerOn().build();

      // The method under test
      Task deployVm = vmApi.deploy(vmUrn, params);
      assertTrue(retryTaskSuccessLong.apply(deployVm), String.format(TASK_COMPLETE_TIMELY, "deployVm"));

      // Get the edited Vm
      vm = vmApi.get(vmUrn);

      // Check the required fields are set
      assertTrue(vm.isDeployed(), String.format(OBJ_FIELD_EQ, VM, "deployed", "TRUE", vm.isDeployed().toString()));

      // Check status
      assertVmStatus(vmUrn, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/power/action/powerOn", dependsOnMethods = { "testDeployVm" })
   public void testPowerOnVm() {
      // Power off Vm
      vm = powerOffVm(vmUrn);

      // The method under test
      Task powerOnVm = vmApi.powerOn(vmUrn);
      assertTaskSucceedsLong(powerOnVm);

      // Get the edited Vm
      vm = vmApi.get(vmUrn);

      // Check status
      assertVmStatus(vmUrn, Status.POWERED_ON);
   }

   @Test(description = "POST /vApp/{id}/power/action/reboot", dependsOnMethods = { "testDeployVm" })
   public void testReboot() {
      // Power on Vm
      vm = powerOnVm(vmUrn);

      // The method under test
      Task reboot = vmApi.reboot(vmUrn);
       assertTaskSucceedsLong(reboot);

      // Get the edited Vm
      vm = vmApi.get(vmUrn);

      // Check status
      assertVmStatus(vmUrn, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/power/action/shutdown", dependsOnMethods = { "testInstallVMwareTools" })
   public void testShutdown() {
      // Power on Vm
      vm = powerOnVm(vmUrn);
      
      // The method under test
      Task shutdown = vmApi.shutdown(vmUrn);
      assertTaskSucceedsLong(shutdown);

      // Get the edited Vm
      vm = vmApi.get(vmUrn);

      // Check status
      assertVmStatus(vmUrn, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/power/action/suspend", dependsOnMethods = { "testDeployVm" })
   public void testSuspend() {
      // Power on Vm
      vm = powerOnVm(vmUrn);

      // The method under test
      Task suspend = vmApi.suspend(vmUrn);
      assertTaskSucceedsLong(suspend);

      // Get the edited Vm
      vm = vmApi.get(vmUrn);

      // Check status
      assertVmStatus(vmUrn, Status.SUSPENDED);

      // Power on the Vm again
      vm = powerOnVm(vmUrn);
   }

   @Test(description = "POST /vApp/{id}/power/action/reset", dependsOnMethods = { "testDeployVm" })
   public void testReset() {
      // Power on Vm
      vm = powerOnVm(vmUrn);

      // The method under test
      Task reset = vmApi.reset(vmUrn);
      assertTaskSucceedsLong(reset);

      // Get the edited Vm
      vm = vmApi.get(vmUrn);

      // Check status
      assertVmStatus(vmUrn, Status.POWERED_ON);
   }

   @Test(description = "POST /vApp/{id}/action/undeploy", dependsOnMethods = { "testDeployVm" })
   public void testUndeployVm() {
      // Power on Vm
      vm = powerOnVm(vmUrn);

      UndeployVAppParams params = UndeployVAppParams.builder().build();

      // The method under test
      Task undeploy = vmApi.undeploy(vmUrn, params);
      assertTrue(retryTaskSuccess.apply(undeploy), String.format(TASK_COMPLETE_TIMELY, "undeploy"));

      // Get the edited Vm
      vm = vmApi.get(vmUrn);

      // Check status
      assertFalse(vm.isDeployed(), String.format(OBJ_FIELD_EQ, VM, "deployed", "FALSE", vm.isDeployed().toString()));
      assertVmStatus(vmUrn, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/power/action/powerOff", dependsOnMethods = { "testUndeployVm" })
   public void testPowerOffVm() {
      // Power on Vm
      vm = powerOnVm(vmUrn);
      
      // The method under test
      // NB this will put the vm in partially powered off state
      Task powerOffVm = vmApi.powerOff(vmUrn);
      assertTrue(retryTaskSuccess.apply(powerOffVm), String.format(TASK_COMPLETE_TIMELY, "powerOffVm"));

      // Get the edited Vm
      vm = vmApi.get(vmUrn);

      // Check status
      assertVmStatus(vmUrn, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/action/discardSuspendedState", dependsOnMethods = { "testDeployVm" })
   public void testDiscardSuspendedState() {
      // Suspend the Vm
      vm = suspendVm(vmUrn);

      // The method under test
      Task discardSuspendedState = vmApi.discardSuspendedState(vmUrn);
      assertTrue(retryTaskSuccess.apply(discardSuspendedState),
               String.format(TASK_COMPLETE_TIMELY, "discardSuspendedState"));
   }

   @Test(description = "POST /vApp/{id}/action/installVMwareTools", dependsOnMethods = { "testDeployVm" })
   public void testInstallVMwareTools() {
      // First ensure the vApp is powered on
      vm = powerOnVm(vmUrn);

      // The method under test
      Task installVMwareTools = vmApi.installVMwareTools(vmUrn);
      assertTrue(retryTaskSuccess.apply(installVMwareTools), String.format(TASK_COMPLETE_TIMELY, "installVMwareTools"));
   }

   @Test(description = "POST /vApp/{id}/action/upgradeHardwareVersion", dependsOnMethods = { "testGetVm" })
   public void testUpgradeHardwareVersion() {
      // Power off Vm
      vm = powerOffVm(vmUrn);

      // The method under test
      Task upgradeHardwareVersion = vmApi.upgradeHardwareVersion(vmUrn);
      assertTrue(retryTaskSuccess.apply(upgradeHardwareVersion),
               String.format(TASK_COMPLETE_TIMELY, "upgradeHardwareVersion"));

      // Power on the Vm again
      vm = powerOnVm(vmUrn);
   }

   @Test(description = "GET /vApp/{id}/guestCustomizationSection", dependsOnMethods = { "testGetVm" })
   public void testGetGuestCustomizationSection() {
      getGuestCustomizationSection(new Function<String, GuestCustomizationSection>() {
         @Override
         public GuestCustomizationSection apply(String uri) {
            return vmApi.getGuestCustomizationSection(uri);
         }
      });
   }

   @Test(description = "PUT /vApp/{id}/guestCustomizationSection", dependsOnMethods = { "testGetGuestCustomizationSection" })
   public void testEditGuestCustomizationSection() {
      // Copy existing section and edit fields
      GuestCustomizationSection oldSection = vmApi.getGuestCustomizationSection(vmUrn);
      GuestCustomizationSection newSection = oldSection.toBuilder().computerName(name("n")).enabled(Boolean.TRUE)
               .adminPassword(null) // Not allowed
               .build();

      // The method under test
      Task editGuestCustomizationSection = vmApi.editGuestCustomizationSection(vmUrn, newSection);
      assertTrue(retryTaskSuccess.apply(editGuestCustomizationSection),
               String.format(TASK_COMPLETE_TIMELY, "editGuestCustomizationSection"));

      // Retrieve the modified section
      GuestCustomizationSection modified = vmApi.getGuestCustomizationSection(vmUrn);

      // Check the retrieved object is well formed
      checkGuestCustomizationSection(modified);

      // Check the modified section fields are set correctly
      assertEquals(modified.getComputerName(), newSection.getComputerName());
      assertTrue(modified.isEnabled());

      // Reset the admin password in the retrieved GuestCustomizationSection for equality check
      modified = modified.toBuilder().adminPassword(null).build();

      // Check the section was modified correctly
      assertEquals(modified, newSection, String.format(ENTITY_EQUAL, "GuestCustomizationSection"));
   }

	@Test(description = "PUT /vApp/{id}/media/action/insertMedia", dependsOnMethods = { "testGetVm" })
	public void testInsertMedia() {
		// Setup media params from configured media id
		MediaInsertOrEjectParams params = MediaInsertOrEjectParams
				.builder()
				.media(Reference.builder().href(lazyGetMedia().getHref())
						.type(MEDIA).build()).build();

		// The method under test
		Task insertMediaTask = vmApi.insertMedia(vmUrn, params);
		assertTrue(retryTaskSuccess.apply(insertMediaTask),
				String.format(TASK_COMPLETE_TIMELY, "insertMedia"));
	}

   @Test(description = "PUT /vApp/{id}/media/action/ejectMedia", dependsOnMethods = { "testInsertMedia" })
   public void testEjectMedia() {
	   
      // Setup media params from configured media id
      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
               .media(Reference.builder()
            		   .href(lazyGetMedia().getHref()).type(MEDIA).build()).build();

      // The method under test
      Task ejectMedia = vmApi.ejectMedia(vmUrn, params);
      assertTrue(retryTaskSuccess.apply(ejectMedia), String.format(TASK_COMPLETE_TIMELY, "ejectMedia"));
   }

   @Test(description = "GET /vApp/{id}/networkConnectionSection", dependsOnMethods = { "testGetVm" })
   public void testGetNetworkConnectionSection() {
      getNetworkConnectionSection(new Function<String, NetworkConnectionSection>() {
         @Override
         public NetworkConnectionSection apply(String uri) {
            return vmApi.getNetworkConnectionSection(uri);
         }
      });
   }

   @Test(description = "PUT /vApp/{id}/networkConnectionSection", dependsOnMethods = { "testEditGuestCustomizationSection" })
	public void testEditNetworkConnectionSection() {

		// Look up a network in the Vdc
		Set<Reference> networks = vdc.getAvailableNetworks();
		Reference network = Iterables.getLast(networks);

		// Copy existing section and edit fields
		NetworkConnectionSection oldSection = vmApi
				.getNetworkConnectionSection(vmUrn);
		NetworkConnection newNetworkConnection = NetworkConnection.builder()
				.network(network.getName()).networkConnectionIndex(1)
				.ipAddressAllocationMode(IpAddressAllocationMode.DHCP).build();
		NetworkConnectionSection newSection = oldSection.toBuilder()
				.networkConnection(newNetworkConnection).build();

		// The method under test
		Task editNetworkConnectionSection = vmApi.editNetworkConnectionSection(
				vmUrn, newSection);
		assertTrue(retryTaskSuccess.apply(editNetworkConnectionSection),
				String.format(TASK_COMPLETE_TIMELY,
						"editNetworkConnectionSection"));

		// Retrieve the modified section
		NetworkConnectionSection modified = vmApi
				.getNetworkConnectionSection(vmUrn);

		// Check the retrieved object is well formed
		checkNetworkConnectionSection(modified);

		// Check the section was modified correctly
		for (NetworkConnection connection : modified.getNetworkConnections()) {
			if (connection.getNetwork().equals(
					newNetworkConnection.getNetwork())) {
				assertEquals(connection.getIpAddressAllocationMode(),
						newNetworkConnection.getIpAddressAllocationMode());
				assertSame(connection.getNetworkConnectionIndex(), newNetworkConnection
						.getNetworkConnectionIndex());
			}
		}
	}

   @Test(description = "GET /vApp/{id}/operatingSystemSection", dependsOnMethods = { "testGetVm" })
   public void testGetOperatingSystemSection() {
      // The method under test
      OperatingSystemSection section = vmApi.getOperatingSystemSection(vmUrn);

      // Check the retrieved object is well formed
      checkOperatingSystemSection(section);
   }

   @Test(description = "PUT /vApp/{id}/operatingSystemSection", dependsOnMethods = { "testGetOperatingSystemSection",
            "testEditVirtualHardwareSection" })
   public void testEditOperatingSystemSection() {
      // Create new OperatingSystemSection
      OperatingSystemSection newSection = OperatingSystemSection.builder().info("") // NOTE Required
                                                                                    // OVF field,
                                                                                    // ignored
               .id(OSType.RHEL_64.getCode()).osType("rhel5_64Guest").build();

      // The method under test
      Task editOperatingSystemSection = vmApi.editOperatingSystemSection(vmUrn, newSection);
      assertTrue(retryTaskSuccess.apply(editOperatingSystemSection),
               String.format(TASK_COMPLETE_TIMELY, "editOperatingSystemSection"));

      // Retrieve the modified section
      OperatingSystemSection modified = vmApi.getOperatingSystemSection(vmUrn);

      // Check the retrieved object is well formed
      checkOperatingSystemSection(modified);

      // Check the modified section fields are set correctly
      assertEquals(modified.getId(), newSection.getId());
   }

   @Test(description = "GET /vApp/{id}/productSections", dependsOnMethods = { "testGetVm" })
   public void testGetProductSections() {
      // The method under test
      ProductSectionList sectionList = vmApi.getProductSections(vmUrn);

      // Check the retrieved object is well formed
      checkProductSectionList(sectionList);
   }

   @Test(description = "PUT /vApp/{id}/productSections", dependsOnMethods = { "testGetProductSections" })
   public void testEditProductSections() {
      powerOffVm(vmUrn);
      // Copy existing section and edit fields
      ProductSectionList oldSections = vmApi.getProductSections(vmUrn);
      ProductSectionList newSections = oldSections
               .toBuilder()
               .productSection(
                        ProductSection.builder().info("Information about the installed software")
                                 // Default ovf:Info text
                                 .required().product(MsgType.builder().value("jclouds").build())
                                 .vendor(MsgType.builder().value("jclouds Inc.").build())
                                 // NOTE other ProductSection elements not returned by vCloud
                                 .build()).build();

      // The method under test
      Task editProductSections = vmApi.editProductSections(vmUrn, newSections);
      assertTrue(retryTaskSuccess.apply(editProductSections),
               String.format(TASK_COMPLETE_TIMELY, "editProductSections"));

      // Retrieve the modified section
      ProductSectionList modified = vmApi.getProductSections(vmUrn);

      // Check the retrieved object is well formed
      checkProductSectionList(modified);

      // Check the modified object has an extra ProductSection
      assertEquals(modified.getProductSections().size(), oldSections.getProductSections().size() + 1);

      // Check the section was modified correctly
      assertEquals(modified, newSections);
   }

   // FIXME How do we force it to ask a question?
   @Test(description = "GET /vApp/{id}/question", dependsOnMethods = { "testDeployVm" })
   public void testGetPendingQuestion() {
      // Power on Vm
      vm = powerOnVm(vmUrn);

      // TODO how to test?

      // The method under test
      VmPendingQuestion question = vmApi.getPendingQuestion(vmUrn);

      // Check the retrieved object is well formed
      checkVmPendingQuestion(question);
   }

   @Test(description = "POST /vApp/{id}/question/action/answer", dependsOnMethods = { "testGetPendingQuestion" })
   public void testAnswerQuestion() {
      // TODO check that the question has been answered (e.g. asking for getPendingQuestion does not
      // include our answered question).

      VmPendingQuestion question = vmApi.getPendingQuestion(vmUrn);
      List<VmQuestionAnswerChoice> answerChoices = question.getChoices();
      VmQuestionAnswerChoice answerChoice = Iterables.getFirst(answerChoices, null);
      assertNotNull(answerChoice, "Question " + question + " must have at least once answer-choice");

      VmQuestionAnswer answer = VmQuestionAnswer.builder().choiceId(answerChoice.getId())
               .questionId(question.getQuestionId()).build();

      vmApi.answerQuestion(vmUrn, answer);
   }

   @Test(description = "GET /vApp/{id}/runtimeInfoSection", dependsOnMethods = { "testGetVm" })
   public void testGetRuntimeInfoSection() {
      // The method under test
      RuntimeInfoSection section = vmApi.getRuntimeInfoSection(vmUrn);

      // Check the retrieved object is well formed
      checkRuntimeInfoSection(section);
   }

   @Test(description = "GET /vApp/{id}/screen", dependsOnMethods = { "testInstallVMwareTools" })
   public void testGetScreenImage() {
      // Power on Vm
      vm = powerOnVm(vmUrn); // we need to have a way to wait for complete bootstrap

      // The method under test
      byte[] image = vmApi.getScreenImage(vmUrn);

      // Check returned bytes against PNG header magic number
      byte[] pngHeaderBytes = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
      assertNotNull(image);
      assertTrue(image.length > pngHeaderBytes.length);
      for (int i = 0; i < pngHeaderBytes.length; i++) {
         assertEquals(image[i], pngHeaderBytes[i],
                  String.format("Image differs from PNG format at byte %d of header", i));
      }
   }

   @Test(description = "POST /vApp/{id}/screen/action/acquireTicket", dependsOnMethods = { "testDeployVm" })
   public void testGetScreenTicket() {
      // Power on Vm
      vm = powerOnVm(vmUrn);

      // The method under test
      ScreenTicket ticket = vmApi.getScreenTicket(vmUrn);

      // Check the retrieved object is well formed
      checkScreenTicket(ticket);
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection", dependsOnMethods = { "testGetVm" })
   public void testGetVirtualHardwareSection() {
      // Method under test
      VirtualHardwareSection hardware = vmApi.getVirtualHardwareSection(vmUrn);

      // Check the retrieved object is well formed
      checkVirtualHardwareSection(hardware);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testEditVirtualHardwareSection() {
      // Power off Vm
      vm = powerOffVm(vmUrn);

      // Copy existing section and edit fields
      VirtualHardwareSection oldSection = vmApi.getVirtualHardwareSection(vmUrn);
      Set<? extends ResourceAllocationSettingData> oldItems = oldSection.getItems();
      Set<ResourceAllocationSettingData> newItems = Sets.newLinkedHashSet(oldItems);
      ResourceAllocationSettingData oldMemory = Iterables.find(oldItems,
               new Predicate<ResourceAllocationSettingData>() {
                  @Override
                  public boolean apply(ResourceAllocationSettingData rasd) {
                     return rasd.getResourceType() == ResourceAllocationSettingData.ResourceType.MEMORY;
                  }
               });
      ResourceAllocationSettingData newMemory = oldMemory.toBuilder().elementName("1024 MB of memory")
               .virtualQuantity(new BigInteger("1024")).build();
      newItems.remove(oldMemory);
      newItems.add(newMemory);
      VirtualHardwareSection newSection = oldSection.toBuilder().items(newItems).build();

      // The method under test
      Task editVirtualHardwareSection = vmApi.editVirtualHardwareSection(vmUrn, newSection);
      assertTrue(retryTaskSuccess.apply(editVirtualHardwareSection),
               String.format(TASK_COMPLETE_TIMELY, "editVirtualHardwareSection"));

      // Retrieve the modified section
      VirtualHardwareSection modifiedSection = vmApi.getVirtualHardwareSection(vmUrn);

      // Check the retrieved object is well formed
      checkVirtualHardwareSection(modifiedSection);

      // Check the modified section fields are set correctly
      ResourceAllocationSettingData modifiedMemory = Iterables.find(modifiedSection.getItems(),
               new Predicate<ResourceAllocationSettingData>() {
                  @Override
                  public boolean apply(ResourceAllocationSettingData rasd) {
                     return rasd.getResourceType() == ResourceAllocationSettingData.ResourceType.MEMORY;
                  }
               });
      assertEquals(modifiedMemory.getVirtualQuantity(), new BigInteger("1024"));
      assertEquals(modifiedMemory, newMemory);
      assertEquals(modifiedSection, newSection);
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection/cpu", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionCpu() {
      // Method under test
      RasdItem rasd = vmApi.getVirtualHardwareSectionCpu(vmUrn);

      // Check the retrieved object is well formed
      checkResourceAllocationSettingData(rasd);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/cpu", dependsOnMethods = { "testGetVirtualHardwareSectionCpu" })
   public void testEditVirtualHardwareSectionCpu() {
      // Copy existing section and edit fields
      RasdItem oldItem = vmApi.getVirtualHardwareSectionCpu(vmUrn);
      RasdItem newItem = oldItem.toBuilder().elementName("2 virtual CPU(s)").virtualQuantity(new BigInteger("2"))
               .build();

      // Method under test
      Task editVirtualHardwareSectionCpu = vmApi.editVirtualHardwareSectionCpu(vmUrn, newItem);
      assertTrue(retryTaskSuccess.apply(editVirtualHardwareSectionCpu),
               String.format(TASK_COMPLETE_TIMELY, "editVirtualHardwareSectionCpu"));

      // Retrieve the modified section
      RasdItem modified = vmApi.getVirtualHardwareSectionCpu(vmUrn);

      // Check the retrieved object
      checkResourceAllocationSettingData(modified);

      // Check modified item
      assertEquals(modified.getVirtualQuantity(), new BigInteger("2"), String.format(OBJ_FIELD_EQ,
               "ResourceAllocationSettingData", "VirtualQuantity", "2", modified.getVirtualQuantity().toString()));
      assertEquals(modified, newItem);
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection/disks", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionDisks() {
      // Method under test
      RasdItemsList rasdItems = vmApi.getVirtualHardwareSectionDisks(vmUrn);

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/disks", dependsOnMethods = { "testGetVirtualHardwareSectionDisks" })
   public void testEditVirtualHardwareSectionDisks() {
      // Copy the existing items list and edit the name of an item
      RasdItemsList oldSection = vmApi.getVirtualHardwareSectionDisks(vmUrn);
      RasdItemsList newSection = oldSection.toBuilder().build();

      // Method under test
      Task editVirtualHardwareSectionDisks = vmApi.editVirtualHardwareSectionDisks(vmUrn, newSection);
      assertTrue(retryTaskSuccess.apply(editVirtualHardwareSectionDisks),
               String.format(TASK_COMPLETE_TIMELY, "editVirtualHardwareSectionDisks"));

      // Retrieve the modified section
      RasdItemsList modified = vmApi.getVirtualHardwareSectionDisks(vmUrn);

      // Check the retrieved object is well formed
      checkRasdItemsList(modified);

      // TODO What is modifiable? What can we change, so we can assert the change took effect?
      // I tried changing "elementName" of one of the items, but it continued to have the old value
      // when looked up post-edit.
      //
      // List<ResourceAllocationSettingData> newItems = new
      // ArrayList<ResourceAllocationSettingData>(oldSection.getItems());
      // ResourceAllocationSettingData item0 = newItems.get(0);
      // String item0InstanceId = item0.getInstanceID().getValue();
      // String item0ElementName =
      // item0.getElementName().getValue()+"-"+random.nextInt(Integer.MAX_VALUE);
      // newItems.set(0, item0.toBuilder().elementName(newCimString(item0ElementName)).build());
      // RasdItemsList newSection = oldSection.toBuilder()
      // .items(newItems)
      // .build();
      // ...
      // long weight = random.nextInt(Integer.MAX_VALUE);
      // ResourceAllocationSettingData newSection = origSection.toBuilder()
      // .weight(newCimUnsignedInt(weight))
      // .build();
      // ...
      // checkHasMatchingItem("virtualHardwareSection/disk", modified, item0InstanceId,
      // item0ElementName);
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection/media", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionMedia() {
      // Method under test
      RasdItemsList rasdItems = vmApi.getVirtualHardwareSectionMedia(vmUrn);

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection/memory", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionMemory() {
      // Method under test
      RasdItem rasd = vmApi.getVirtualHardwareSectionCpu(vmUrn);

      // Check the retrieved object is well formed
      checkResourceAllocationSettingData(rasd);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/memory", dependsOnMethods = { "testGetVirtualHardwareSectionMemory" })
   public void testEditVirtualHardwareSectionMemory() {
      RasdItem origItem = vmApi.getVirtualHardwareSectionMemory(vmUrn);
      RasdItem newItem = origItem.toBuilder().elementName("1024 MB of memory").virtualQuantity(new BigInteger("1024"))
               .build();

      // Method under test
      Task editVirtualHardwareSectionMemory = vmApi.editVirtualHardwareSectionMemory(vmUrn, newItem);
      assertTrue(retryTaskSuccess.apply(editVirtualHardwareSectionMemory),
               String.format(TASK_COMPLETE_TIMELY, "editVirtualHardwareSectionMemory"));

      // Retrieve the modified section
      RasdItem modified = vmApi.getVirtualHardwareSectionMemory(vmUrn);

      // Check the retrieved object
      checkResourceAllocationSettingData(modified);

      // Check modified item
      assertEquals(modified.getVirtualQuantity(), new BigInteger("1024"), String.format(OBJ_FIELD_EQ,
               "ResourceAllocationSettingData", "VirtualQuantity", "1024", modified.getVirtualQuantity().toString()));
      assertEquals(modified, newItem);
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection/networkCards", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionNetworkCards() {
      // Method under test
      RasdItemsList rasdItems = vmApi.getVirtualHardwareSectionNetworkCards(vmUrn);

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/networkCards", dependsOnMethods = { "testGetVirtualHardwareSectionNetworkCards" })
   public void testEditVirtualHardwareSectionNetworkCards() {
      RasdItemsList oldSection = vmApi.getVirtualHardwareSectionNetworkCards(vmUrn);
      RasdItemsList newSection = oldSection.toBuilder().build();

      // Method under test
      Task editVirtualHardwareSectionNetworkCards = vmApi.editVirtualHardwareSectionNetworkCards(vmUrn, newSection);
      assertTrue(retryTaskSuccess.apply(editVirtualHardwareSectionNetworkCards),
               String.format(TASK_COMPLETE_TIMELY, "editVirtualHardwareSectionNetworkCards"));

      // Retrieve the modified section
      RasdItemsList modified = vmApi.getVirtualHardwareSectionNetworkCards(vmUrn);

      // Check the retrieved object is well formed
      checkRasdItemsList(modified);

      // TODO What is modifiable? What can we change, so we can assert the change took effect?
      // I tried changing "elementName" of one of the items, but it continued to have the old value
      // when looked up post-edit.
      // See the description in testEditVirtualHardwareSectionDisks
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection/serialPorts", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionSerialPorts() {
      // Method under test
      RasdItemsList rasdItems = vmApi.getVirtualHardwareSectionSerialPorts(vmUrn);

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/serialPorts", dependsOnMethods = { "testGetVirtualHardwareSectionSerialPorts" })
   public void testEditVirtualHardwareSectionSerialPorts() {
      RasdItemsList oldSection = vmApi.getVirtualHardwareSectionSerialPorts(vmUrn);
      RasdItemsList newSection = oldSection.toBuilder().build();

      // Method under test
      Task editVirtualHardwareSectionSerialPorts = vmApi.editVirtualHardwareSectionSerialPorts(vmUrn, newSection);
      assertTrue(retryTaskSuccess.apply(editVirtualHardwareSectionSerialPorts),
               String.format(TASK_COMPLETE_TIMELY, "editVirtualHardwareSectionSerialPorts"));

      // Retrieve the modified section
      RasdItemsList modified = vmApi.getVirtualHardwareSectionSerialPorts(vmUrn);

      // Check the retrieved object is well formed
      checkRasdItemsList(modified);

      // TODO What is modifiable? What can we change, so we can assert the change took effect?
      // I tried changing "elementName" of one of the items, but it continued to have the old value
      // when looked up post-edit.
      // See the description in testEditVirtualHardwareSectionDisks
   }

   @Test(description = "PUT /vApp/{id}/metadata/{key}", dependsOnMethods = { "testGetVm" })
   public void testSetMetadataValue() {
      key = name("key-");
      metadataValue = name("value-");
      //TODO: block!!
      context.getApi().getMetadataApi(vmUrn).put(key, metadataValue);

      // Retrieve the value, and assert it was set correctly
      String newMetadataValue = context.getApi().getMetadataApi(vmUrn).get(key);

      // Check the retrieved object is well formed
      assertEquals(newMetadataValue, metadataValue,
            String.format(CORRECT_VALUE_OBJECT_FMT, "Value", "MetadataValue", metadataValue, newMetadataValue));
   }

	@Test(description = "GET /vApp/{id}/metadata", dependsOnMethods = { "testSetMetadataValue" })
	public void testGetMetadata() {

		key = name("key-");
		metadataValue = name("value-");

		context.getApi().getMetadataApi(vmUrn).put(key, metadataValue);
		// Call the method being tested
		Metadata metadata = context.getApi().getMetadataApi(vmUrn).get();

		checkMetadata(metadata);
		
		// Check requirements for this test
		assertTrue(metadata.containsValue(metadataValue), String.format(CORRECT_VALUE_OBJECT_FMT, "Value", "MetadataValue", metadata.get(key), metadataValue));
	}

   @Test(description = "GET /vApp/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadata" })
   public void testGetOrgMetadataValue() {
      key = name("key-");
      metadataValue = name("value-");

      //TODO: block!!
      context.getApi().getMetadataApi(vmUrn).put(key, metadataValue);

      // Call the method being tested
      String newMetadataValue = context.getApi().getMetadataApi(vmUrn).get(key);
      
      assertEquals(newMetadataValue, metadataValue,
            String.format(CORRECT_VALUE_OBJECT_FMT, "Value", "MetadataValue", metadataValue, newMetadataValue));
   }

   @Test(description = "DELETE /vApp/{id}/metadata/{key}", dependsOnMethods = { "testSetMetadataValue" })
   public void testRemoveMetadataEntry() {
      // Delete the entry
      Task task = context.getApi().getMetadataApi(vmUrn).remove(key);
      retryTaskSuccess.apply(task);

      // Confirm the entry has been removed
      Metadata newMetadata = context.getApi().getMetadataApi(vmUrn).get();

      // Check the retrieved object is well formed
      checkMetadataKeyAbsentFor(VM, newMetadata, key);
   }

   @Test(description = "POST /vApp/{id}/metadata", dependsOnMethods = { "testGetMetadata" })
   public void testMergeMetadata() {
      Metadata oldMetadata = context.getApi().getMetadataApi(vmUrn).get();
      Map<String, String> oldMetadataMap = Checks.metadataToMap(oldMetadata);

      // Store a value, to be removed
      String key = name("key-");
      String value = name("value-");
      Task task = context.getApi().getMetadataApi(vmUrn).putAll(ImmutableMap.of(key, value));
      retryTaskSuccess.apply(task);

      // Confirm the entry contains everything that was there, and everything that was being added
      Metadata newMetadata = context.getApi().getMetadataApi(vmUrn).get();
      Map<String, String> expectedMetadataMap = ImmutableMap.<String, String> builder().putAll(oldMetadataMap)
               .put(key, value).build();

      // Check the retrieved object is well formed
      checkMetadataFor(VM, newMetadata, expectedMetadataMap);
   }

   /**
    * @see VmApi#remove(String)
    */
   @Test(description = "DELETE /vApp/{id}")
   public void testRemoveVm() {
      // Create a temporary VApp to remove
      VApp remove = instantiateVApp();
      DeployVAppParams params = DeployVAppParams.builder()
               .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS)).notForceCustomization()
               .powerOn().build();
      Task deployVApp = vAppApi.deploy(remove.getId(), params);
      assertTaskSucceedsLong(deployVApp);

      // Get the edited VApp and the Vm
      remove = vAppApi.get(remove.getId());
      List<Vm> vms = remove.getChildren().getVms();
      Vm temp = Iterables.get(vms, 0);

      // otherwise it's impossible to stop a running vApp with no vms
      if (vms.size() == 1) {
         UndeployVAppParams undeployParams = UndeployVAppParams.builder().build();
         Task shutdownVapp = vAppApi.undeploy(remove.getId(), undeployParams);
         assertTaskSucceedsLong(shutdownVapp);
      } else {
         powerOffVm(temp.getId());
      }
      // The method under test
      Task removeVm = vmApi.remove(temp.getId());
      assertTrue(retryTaskSuccess.apply(removeVm), String.format(TASK_COMPLETE_TIMELY, "removeVm"));

      Vm removed = vmApi.get(temp.getId());
      assertNull(removed, "The Vm " + temp.getName() + " should have been removed");
   }
}
