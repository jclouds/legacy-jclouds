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

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getFirst;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.CORRECT_VALUE_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.ENTITY_EQUAL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.NOT_EMPTY_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.MEDIA;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkGuestCustomizationSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadata;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataKeyAbsentFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataValue;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataValueFor;
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
import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.relEquals;
import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.typeEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.dmtf.cim.OSType;
import org.jclouds.dmtf.cim.ResourceAllocationSettingData;
import org.jclouds.dmtf.ovf.MsgType;
import org.jclouds.dmtf.ovf.ProductSection;
import org.jclouds.io.Payloads;
import org.jclouds.vcloud.director.v1_5.AbstractVAppApiLiveTest;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
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
import org.jclouds.vcloud.director.v1_5.domain.params.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecordType;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
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

   private MetadataValue metadataValue;
   private String key;
   private URI testUserURI;
   private boolean mediaCreated = false;
   private boolean testUserCreated = false;
   
   @BeforeClass(alwaysRun = true, dependsOnMethods = { "setupRequiredApis" })
   protected void setupRequiredEntities() {
      Set<Link> links = vdcApi.getVdc(vdcURI).getLinks();

      if (mediaURI == null) {
         Predicate<Link> addMediaLink = and(relEquals(Link.Rel.ADD), typeEquals(VCloudDirectorMediaType.MEDIA));
         
         if (contains(links, addMediaLink)) {
            Link addMedia = find(links, addMediaLink);
            byte[] iso = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
            
            Media sourceMedia = Media.builder()
                  .type(VCloudDirectorMediaType.MEDIA)
                  .name(name("media"))
                  .size(iso.length)
                  .imageType(Media.ImageType.ISO)
                  .description("Test media generated by VmApiLiveTest")
                  .build();
            Media media = context.getApi().getMediaApi().createMedia(addMedia.getHref(), sourceMedia);
            
            Link uploadLink = getFirst(getFirst(media.getFiles(), null).getLinks(), null);
            context.getApi().getUploadApi().upload(uploadLink.getHref(), Payloads.newByteArrayPayload(iso));
            
            media = context.getApi().getMediaApi().getMedia(media.getHref());
            
            if (media.getTasks().size() == 1) {
               Task uploadTask = Iterables.getOnlyElement(media.getTasks());
               Checks.checkTask(uploadTask);
               assertEquals(uploadTask.getStatus(), Task.Status.RUNNING);
               assertTrue(retryTaskSuccess.apply(uploadTask), String.format(TASK_COMPLETE_TIMELY, "uploadTask"));
               media = context.getApi().getMediaApi().getMedia(media.getHref());
            }
            
            mediaURI = media.getHref();
            mediaCreated = true;
         }
      }
      
      if (adminContext != null) {
         Link orgLink = find(links, and(relEquals("up"), typeEquals(VCloudDirectorMediaType.ORG)));
         testUserURI = adminContext.getApi().getUserApi().createUser(toAdminUri(orgLink), randomTestUser("VAppAccessTest")).getHref();
      } else {
         testUserURI = userURI;
      }
   }
   
   @AfterClass(alwaysRun = true, dependsOnMethods = { "cleanUpEnvironment" })
   public void cleanUp() {
      if (adminContext != null && mediaCreated && mediaURI != null) {
         try {
	         Task delete = context.getApi().getMediaApi().deleteMedia(mediaURI);
	         taskDoneEventually(delete);
         } catch (Exception e) {
            logger.warn("Error when deleting media: %s", e.getMessage());
         }
      }
      if (adminContext != null && testUserCreated && testUserURI != null) {
         try {
	         adminContext.getApi().getUserApi().deleteUser(testUserURI);
         } catch (Exception e) {
            logger.warn("Error when deleting user: %s", e.getMessage());
         }
      }
   }

   /**
    * @see VmApi#getVm(URI)
    */
   @Test(description = "GET /vApp/{id}")
   public void testGetVm() {
      // The method under test
      vm = vmApi.getVm(vmURI);

      // Check the retrieved object is well formed
      checkVm(vm);

      // Check the required fields are set
      assertEquals(vm.isDeployed(), Boolean.FALSE, String.format(OBJ_FIELD_EQ, VM, "deployed", "FALSE", vm.isDeployed().toString()));

      // Check status
      assertVmStatus(vm.getHref(), Status.POWERED_OFF);
   }

   /**
    * @see VmApi#modifyVm(URI, Vm)
    */
   @Test(description = "PUT /vApp/{id}", dependsOnMethods = { "testGetVm" })
   public void testModifyVm() {
      Vm newVm = Vm.builder()
            .name(name("new-name-"))
            .description("New Description")
            .build();

      // The method under test
      Task modifyVm = vmApi.modifyVm(vm.getHref(), newVm);
      assertTrue(retryTaskSuccess.apply(modifyVm), String.format(TASK_COMPLETE_TIMELY, "modifyVm"));

      // Get the updated Vm
      vm = vmApi.getVm(vm.getHref());

      // Check the required fields are set
      assertEquals(vm.getName(), newVm.getName(), String.format(OBJ_FIELD_EQ, VM, "Name", newVm.getName(), vm.getName()));
      assertEquals(vm.getDescription(), newVm.getDescription(), String.format(OBJ_FIELD_EQ, VM, "Description", newVm.getDescription(), vm.getDescription()));
   }

   @Test(description = "POST /vApp/{id}/action/deploy", dependsOnMethods = { "testGetVm" })
   public void testDeployVm() {
      DeployVAppParams params = DeployVAppParams.builder()
            .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS))
            .notForceCustomization()
            .notPowerOn()
            .build();

      // The method under test
      Task deployVm = vmApi.deploy(vm.getHref(), params);
      assertTrue(retryTaskSuccessLong.apply(deployVm), String.format(TASK_COMPLETE_TIMELY, "deployVm"));

      // Get the updated Vm
      vm = vmApi.getVm(vm.getHref());

      // Check the required fields are set
      assertTrue(vm.isDeployed(), String.format(OBJ_FIELD_EQ, VM, "deployed", "TRUE", vm.isDeployed().toString()));

      // Check status
      assertVmStatus(vmURI, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/power/action/powerOn", dependsOnMethods = { "testDeployVm" })
   public void testPowerOnVm() {
      // Power off Vm
      vm = powerOffVm(vm.getHref());

      // The method under test
      Task powerOnVm = vmApi.powerOn(vm.getHref());
      assertTaskSucceedsLong(powerOnVm);

      // Get the updated Vm
      vm = vmApi.getVm(vm.getHref());

      // Check status
      assertVmStatus(vm.getHref(), Status.POWERED_ON);
   }

   @Test(description = "POST /vApp/{id}/power/action/reboot", dependsOnMethods = { "testDeployVm" })
   public void testReboot() {
      // Power on Vm
      vm = powerOnVm(vm.getHref());
 
      // The method under test
      Task reboot = vmApi.reboot(vm.getHref());
      assertTaskSucceedsLong(reboot);

      // Get the updated Vm
      vm = vmApi.getVm(vm.getHref());

      // Check status
      assertVmStatus(vmURI, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/power/action/shutdown", dependsOnMethods = { "testDeployVm" })
   public void testShutdown() {
      // Power on Vm
      vm = powerOnVm(vm.getHref());

      // The method under test
      Task shutdown = vmApi.shutdown(vmURI);
      assertTaskSucceedsLong(shutdown);

      // Get the updated Vm
      vm = vmApi.getVm(vmURI);

      // Check status
      assertVmStatus(vmURI, Status.POWERED_OFF);

      // Power on the Vm again
      vm = powerOnVm(vm.getHref());
   }

   @Test(description = "POST /vApp/{id}/power/action/suspend", dependsOnMethods = { "testDeployVm" })
   public void testSuspend() {
      // Power on Vm
      vm = powerOnVm(vm.getHref());

      // The method under test
      Task suspend = vmApi.suspend(vmURI);
      assertTaskSucceedsLong(suspend);

      // Get the updated Vm
      vm = vmApi.getVm(vm.getHref());

      // Check status
      assertVmStatus(vmURI, Status.SUSPENDED);

      // Power on the Vm again
      vm = powerOnVm(vm.getHref());
   }

   @Test(description = "POST /vApp/{id}/power/action/reset", dependsOnMethods = { "testDeployVm" })
   public void testReset() {
      // Power on Vm
      vm = powerOnVm(vm.getHref());

      // The method under test
      Task reset = vmApi.reset(vmURI);
      assertTaskSucceedsLong(reset);

      // Get the updated Vm
      vm = vmApi.getVm(vm.getHref());

      // Check status
      assertVmStatus(vmURI, Status.POWERED_ON);
   }

   @Test(description = "POST /vApp/{id}/action/undeploy", dependsOnMethods = { "testDeployVm" })
   public void testUndeployVm() {
      // Power on Vm
      vm = powerOnVm(vm.getHref());

      UndeployVAppParams params = UndeployVAppParams.builder().build();

      // The method under test
      Task undeploy = vmApi.undeploy(vm.getHref(), params);
      assertTrue(retryTaskSuccess.apply(undeploy), String.format(TASK_COMPLETE_TIMELY, "undeploy"));

      // Get the updated Vm
      vm = vmApi.getVm(vm.getHref());

      // Check status
      assertFalse(vm.isDeployed(), String.format(OBJ_FIELD_EQ, VM, "deployed", "FALSE", vm.isDeployed().toString()));
      assertVmStatus(vmURI, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/power/action/powerOff", dependsOnMethods = { "testUndeployVm" })
   public void testPowerOffVm() {
      // Power on Vm
      vm = powerOnVm(vm.getHref());
      
      // The method under test
      Task powerOffVm = vmApi.powerOff(vm.getHref());
      assertTrue(retryTaskSuccess.apply(powerOffVm), String.format(TASK_COMPLETE_TIMELY, "powerOffVm"));

      // Get the updated Vm
      vm = vmApi.getVm(vmURI);

      // Check status
      assertVmStatus(vmURI, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/action/consolidate", dependsOnMethods = { "testDeployVm" })
   public void testConsolidateVm() {
      // Power on Vm
      vm = powerOnVm(vm.getHref());

      // The method under test
      Task consolidateVm = vmApi.consolidateVm(vm.getHref());
      assertTrue(retryTaskSuccess.apply(consolidateVm), String.format(TASK_COMPLETE_TIMELY, "consolidateVm"));
   }

   @Test(description = "POST /vApp/{id}/action/discardSuspendedState", dependsOnMethods = { "testDeployVm" })
   public void testDiscardSuspendedState() {
      // Suspend the Vm
      vm = suspendVm(vm.getHref());
      
      // The method under test
      Task discardSuspendedState = vmApi.discardSuspendedState(vm.getHref());
      assertTrue(retryTaskSuccess.apply(discardSuspendedState), String.format(TASK_COMPLETE_TIMELY, "discardSuspendedState"));
   }

   @Test(description = "POST /vApp/{id}/action/installVMwareTools", dependsOnMethods = { "testDeployVm" })
   public void testInstallVMwareTools() {
      // First ensure the vApp is powered n
      vm = powerOnVm(vm.getHref());

      // The method under test
      Task installVMwareTools = vmApi.installVMwareTools(vm.getHref());
      assertTrue(retryTaskSuccess.apply(installVMwareTools), String.format(TASK_COMPLETE_TIMELY, "installVMwareTools"));
   }

   // NOTE This test is disabled, as it is not possible to look up datastores using the User API
   @Test(description = "POST /vApp/{id}/action/relocate", dependsOnMethods = { "testGetVm" })
   public void testRelocate() {
      // Relocate to the last of the available datastores
      QueryResultRecords records = context.getApi().getQueryApi().queryAll("datastore");
      QueryResultRecordType datastore = Iterables.getLast(records.getRecords());
      RelocateParams params = RelocateParams.builder().datastore(Reference.builder().href(datastore.getHref()).build()).build();

      // The method under test
      Task relocate = vmApi.relocateVm(vm.getHref(), params);
      assertTrue(retryTaskSuccess.apply(relocate), String.format(TASK_COMPLETE_TIMELY, "relocate"));
   }

   @Test(description = "POST /vApp/{id}/action/upgradeHardwareVersion", dependsOnMethods = { "testGetVm" })
   public void testUpgradeHardwareVersion() {
      // Power off Vm
      vm = powerOffVm(vm.getHref());

      // The method under test
      Task upgradeHardwareVersion = vmApi.upgradeHardwareVersion(vm.getHref());
      assertTrue(retryTaskSuccess.apply(upgradeHardwareVersion), String.format(TASK_COMPLETE_TIMELY, "upgradeHardwareVersion"));
   }

   @Test(description = "GET /vApp/{id}/guestCustomizationSection", dependsOnMethods = { "testGetVm" })
   public void testGetGuestCustomizationSection() {
      getGuestCustomizationSection(new Function<URI, GuestCustomizationSection>() {
         @Override
         public GuestCustomizationSection apply(URI uri) {
            return vmApi.getGuestCustomizationSection(uri);
         }
      });
   }

   @Test(description = "PUT /vApp/{id}/guestCustomizationSection", dependsOnMethods = { "testGetGuestCustomizationSection" })
   public void testModifyGuestCustomizationSection() {
      // Copy existing section and update fields
      GuestCustomizationSection oldSection = vmApi.getGuestCustomizationSection(vm.getHref());
      GuestCustomizationSection newSection = oldSection.toBuilder()
            .computerName(name("n"))
            .enabled(Boolean.FALSE)
            .adminPassword(null) // Not allowed
            .build();

      // The method under test
      Task modifyGuestCustomizationSection = vmApi.modifyGuestCustomizationSection(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyGuestCustomizationSection), String.format(TASK_COMPLETE_TIMELY, "modifyGuestCustomizationSection"));

      // Retrieve the modified section
      GuestCustomizationSection modified = vmApi.getGuestCustomizationSection(vm.getHref());

      // Check the retrieved object is well formed
      checkGuestCustomizationSection(modified);

      // Check the modified section fields are set correctly
      assertEquals(modified.getComputerName(), newSection.getComputerName());
      assertFalse(modified.isEnabled());

      // Reset the admin password in the retrieved GuestCustomizationSection for equality check
      modified = modified.toBuilder().adminPassword(null).build();

      // Check the section was modified correctly
      assertEquals(modified, newSection, String.format(ENTITY_EQUAL, "GuestCustomizationSection"));
   }

   // FIXME "Error: The requested operation on media "com.vmware.vcloud.entity.media:abfcb4b7-809f-4b50-a0aa-8c97bf09a5b0" is not supported in the current state."
   @Test(description = "PUT /vApp/{id}/media/action/insertMedia", dependsOnMethods = { "testGetVm" })
   public void testInsertMedia() {
      // Setup media params from configured media id
      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .media(Reference.builder().href(mediaURI).type(MEDIA).build())
            .build();

      // The method under test
      Task insertMedia = vmApi.insertMedia(vm.getHref(), params);
      assertTrue(retryTaskSuccess.apply(insertMedia), String.format(TASK_COMPLETE_TIMELY, "insertMedia"));
   }

   @Test(description = "PUT /vApp/{id}/media/action/ejectMedia", dependsOnMethods = { "testInsertMedia" })
   public void testEjectMedia() {
      // Setup media params from configured media id
      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .media(Reference.builder().href(mediaURI).type(MEDIA).build())
            .build();

      // The method under test
      Task ejectMedia = vmApi.ejectMedia(vm.getHref(), params);
      assertTrue(retryTaskSuccess.apply(ejectMedia), String.format(TASK_COMPLETE_TIMELY, "ejectMedia"));
   }

   @Test(description = "GET /vApp/{id}/networkConnectionSection", dependsOnMethods = { "testGetVm" })
   public void testGetNetworkConnectionSection() {
      getNetworkConnectionSection(new Function<URI, NetworkConnectionSection>() {
         @Override
         public NetworkConnectionSection apply(URI uri) {
            return vmApi.getNetworkConnectionSection(uri);
         }
      });
   }

   // FIXME "Task error: Unable to perform this action. Contact your cloud administrator."
   @Test(description = "PUT /vApp/{id}/networkConnectionSection", dependsOnMethods = { "testGetNetworkConnectionSection" })
   public void testModifyNetworkConnectionSection() {
      // Look up a network in the Vdc
      Set<Reference> networks = vdc.getAvailableNetworks();
      Reference network = Iterables.getLast(networks);

      // Copy existing section and update fields
      NetworkConnectionSection oldSection = vmApi.getNetworkConnectionSection(vm.getHref());
      NetworkConnectionSection newSection = oldSection.toBuilder()
            .networkConnection(NetworkConnection.builder()
                  .ipAddressAllocationMode(IpAddressAllocationMode.DHCP.toString())
                  .network(network.getName())
                  .build())
            .build();

      // The method under test
      Task modifyNetworkConnectionSection = vmApi.modifyNetworkConnectionSection(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyNetworkConnectionSection), String.format(TASK_COMPLETE_TIMELY, "modifyNetworkConnectionSection"));

      // Retrieve the modified section
      NetworkConnectionSection modified = vmApi.getNetworkConnectionSection(vm.getHref());

      // Check the retrieved object is well formed
      checkNetworkConnectionSection(modified);

      // Check the modified section has an extra network connection
      assertEquals(modified.getNetworkConnections().size(), newSection.getNetworkConnections().size() + 1);

      // Check the section was modified correctly
      assertEquals(modified, newSection, String.format(ENTITY_EQUAL, "NetworkConnectionSection"));
   }

   @Test(description = "GET /vApp/{id}/operatingSystemSection", dependsOnMethods = { "testGetVm" })
   public void testGetOperatingSystemSection() {
      // The method under test
      OperatingSystemSection section = vmApi.getOperatingSystemSection(vm.getHref());

      // Check the retrieved object is well formed
      checkOperatingSystemSection(section);
   }

   @Test(description = "PUT /vApp/{id}/operatingSystemSection", dependsOnMethods = { "testGetOperatingSystemSection", "testModifyVirtualHardwareSection" })
   public void testModifyOperatingSystemSection() {
      // Create new OperatingSystemSection
      OperatingSystemSection newSection = OperatingSystemSection.builder()
            .info("") // NOTE Required OVF field, ignored
            .id(OSType.RHEL_64.getCode())
            .osType("rhel5_64Guest")
            .build();

      // The method under test
      Task modifyOperatingSystemSection = vmApi.modifyOperatingSystemSection(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyOperatingSystemSection), String.format(TASK_COMPLETE_TIMELY, "modifyOperatingSystemSection"));

      // Retrieve the modified section
      OperatingSystemSection modified = vmApi.getOperatingSystemSection(vm.getHref());

      // Check the retrieved object is well formed
      checkOperatingSystemSection(modified);

      // Check the modified section fields are set correctly
      assertEquals(modified.getId(), newSection.getId());
   }

   @Test(description = "GET /vApp/{id}/productSections", dependsOnMethods = { "testGetVm" })
   public void testGetProductSections() {
      // The method under test
      ProductSectionList sectionList = vmApi.getProductSections(vm.getHref());

      // Check the retrieved object is well formed
      checkProductSectionList(sectionList);
   }

   @Test(description = "PUT /vApp/{id}/productSections", dependsOnMethods = { "testGetProductSections" })
   public void testModifyProductSections() {
      // Copy existing section and update fields
      ProductSectionList oldSections = vmApi.getProductSections(vm.getHref());
      ProductSectionList newSections = oldSections.toBuilder()
            .productSection(ProductSection.builder()
                  .info("Information about the installed software") // Default ovf:Info text
                  .required()
                  .product(MsgType.builder().value("jclouds").build())
                  .vendor(MsgType.builder().value("jclouds Inc.").build())
                  // NOTE other ProductSection elements not returned by vCloud
                  .build())
            .build();

      // The method under test
      Task modifyProductSections = vmApi.modifyProductSections(vm.getHref(), newSections);
      assertTrue(retryTaskSuccess.apply(modifyProductSections), String.format(TASK_COMPLETE_TIMELY, "modifyProductSections"));

      // Retrieve the modified section
      ProductSectionList modified = vmApi.getProductSections(vm.getHref());

      // Check the retrieved object is well formed
      checkProductSectionList(modified);

      // Check the modified object has an extra ProductSection
      assertEquals(modified.getProductSections().size(), oldSections.getProductSections().size() + 1);

      // Check the section was modified correctly
      assertEquals(modified, newSections, String.format(ENTITY_EQUAL, "ProductSectionList"));
   }

   // FIXME How do we force it to ask a question?
   @Test(description = "GET /vApp/{id}/question", dependsOnMethods = { "testDeployVm" })
   public void testGetPendingQuestion() {
      // Power on Vm
      vm = powerOnVm(vm.getHref());

      // TODO how to test?

      // The method under test
      VmPendingQuestion question = vmApi.getPendingQuestion(vm.getHref());

      // Check the retrieved object is well formed
      checkVmPendingQuestion(question);
   }

   @Test(description = "POST /vApp/{id}/question/action/answer", dependsOnMethods = { "testGetPendingQuestion" })
   public void testAnswerQuestion() {
      // TODO check that the question has been answered (e.g. asking for getPendingQuestion does not
      // include our answered question).

      VmPendingQuestion question = vmApi.getPendingQuestion(vm.getHref());
      List<VmQuestionAnswerChoice> answerChoices = question.getChoices();
      VmQuestionAnswerChoice answerChoice = Iterables.getFirst(answerChoices, null);
      assertNotNull(answerChoice, "Question "+question+" must have at least once answer-choice");
      
      VmQuestionAnswer answer = VmQuestionAnswer.builder()
               .choiceId(answerChoice.getId())
               .questionId(question.getQuestionId())
               .build();
      
      vmApi.answerQuestion(vm.getHref(), answer);
   }

   @Test(description = "GET /vApp/{id}/runtimeInfoSection", dependsOnMethods = { "testGetVm" })
   public void testGetRuntimeInfoSection() {
      // The method under test
      RuntimeInfoSection section = vmApi.getRuntimeInfoSection(vm.getHref());

      // Check the retrieved object is well formed
      checkRuntimeInfoSection(section);
   }

   // FIXME If still failing, consider escalating?
   @Test(description = "GET /vApp/{id}/screen", dependsOnMethods = { "testDeployVm" })
   public void testGetScreenImage() {
      // Power on Vm
      vm = powerOnVm(vm.getHref());
      
      // The method under test
      byte[] image = vmApi.getScreenImage(vm.getHref());

      // Check returned bytes against PNG header magic number
      byte[] pngHeaderBytes = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
      assertNotNull(image);
      assertTrue(image.length > pngHeaderBytes.length);
      for (int i = 0; i < pngHeaderBytes.length; i++) {
         assertEquals(image[i], pngHeaderBytes[i], String.format("Image differs from PNG format at byte %d of header", i));
      }
   }

   @Test(description = "POST /vApp/{id}/screen/action/acquireTicket", dependsOnMethods = { "testDeployVm" })
   public void testGetScreenTicket() {
      // Power on Vm
      vm = powerOnVm(vm.getHref());
      
      // The method under test
      ScreenTicket ticket = vmApi.getScreenTicket(vm.getHref());

      // Check the retrieved object is well formed
      checkScreenTicket(ticket);
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection", dependsOnMethods = { "testGetVm" })
   public void testGetVirtualHardwareSection() {
      // Method under test
      VirtualHardwareSection hardware = vmApi.getVirtualHardwareSection(vm.getHref());

      // Check the retrieved object is well formed
      checkVirtualHardwareSection(hardware);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testModifyVirtualHardwareSection() {
      // Power off Vm
      vm = powerOffVm(vm.getHref());

      // Copy existing section and update fields
      VirtualHardwareSection oldSection = vmApi.getVirtualHardwareSection(vm.getHref());
      Set<? extends ResourceAllocationSettingData> oldItems = oldSection.getItems();
      Set<ResourceAllocationSettingData> newItems = Sets.newLinkedHashSet(oldItems);
      ResourceAllocationSettingData oldMemory = Iterables.find(oldItems, new Predicate<ResourceAllocationSettingData>() {
         @Override
         public boolean apply(ResourceAllocationSettingData rasd) {
            return rasd.getResourceType() == ResourceAllocationSettingData.ResourceType.MEMORY;
         }
      });
      ResourceAllocationSettingData newMemory = oldMemory.toBuilder()
            .elementName("1024 MB of memory")
            .virtualQuantity(new BigInteger("1024"))
            .build();
      newItems.remove(oldMemory);
      newItems.add(newMemory);
      VirtualHardwareSection newSection = oldSection.toBuilder()
            .items(newItems)
            .build();

      // The method under test
      Task modifyVirtualHardwareSection = vmApi.modifyVirtualHardwareSection(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSection), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSection"));

      // Retrieve the modified section
      VirtualHardwareSection modifiedSection = vmApi.getVirtualHardwareSection(vm.getHref());

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
      RasdItem rasd = vmApi.getVirtualHardwareSectionCpu(vm.getHref());

      // Check the retrieved object is well formed
      checkResourceAllocationSettingData(rasd);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/cpu", dependsOnMethods = { "testGetVirtualHardwareSectionCpu" })
   public void testModifyVirtualHardwareSectionCpu() {
      // Copy existing section and update fields
      RasdItem oldItem = vmApi.getVirtualHardwareSectionCpu(vm.getHref());
      RasdItem newItem = oldItem.toBuilder()
            .elementName("2 virtual CPU(s)")
            .virtualQuantity(new BigInteger("2"))
            .build();
      
      // Method under test
      Task modifyVirtualHardwareSectionCpu = vmApi.modifyVirtualHardwareSectionCpu(vm.getHref(), newItem);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionCpu), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionCpu"));

      // Retrieve the modified section
      RasdItem modified = vmApi.getVirtualHardwareSectionCpu(vm.getHref());
      
      // Check the retrieved object
      checkResourceAllocationSettingData(modified);
      
      // Check modified item
      assertEquals(modified.getVirtualQuantity(), new BigInteger("2"),
            String.format(OBJ_FIELD_EQ, "ResourceAllocationSettingData", "VirtualQuantity", "2", modified.getVirtualQuantity().toString()));
      assertEquals(modified, newItem);
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection/disks", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionDisks() {
      // Method under test
      RasdItemsList rasdItems = vmApi.getVirtualHardwareSectionDisks(vm.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/disks", dependsOnMethods = { "testGetVirtualHardwareSectionDisks" })
   public void testModifyVirtualHardwareSectionDisks() {
      // Copy the existing items list and modify the name of an item
      RasdItemsList oldSection = vmApi.getVirtualHardwareSectionDisks(vm.getHref());
      RasdItemsList newSection = oldSection.toBuilder().build();

      // Method under test
      Task modifyVirtualHardwareSectionDisks = vmApi.modifyVirtualHardwareSectionDisks(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionDisks), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionDisks"));

      // Retrieve the modified section
      RasdItemsList modified = vmApi.getVirtualHardwareSectionDisks(vm.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(modified);
      
      // TODO What is modifiable? What can we change, so we can assert the change took effect? 
      // I tried changing "elementName" of one of the items, but it continued to have the old value when looked up post-modify.
      //
      // List<ResourceAllocationSettingData> newItems = new ArrayList<ResourceAllocationSettingData>(oldSection.getItems());
      // ResourceAllocationSettingData item0 = newItems.get(0);
      // String item0InstanceId = item0.getInstanceID().getValue();
      // String item0ElementName = item0.getElementName().getValue()+"-"+random.nextInt(Integer.MAX_VALUE);
      // newItems.set(0, item0.toBuilder().elementName(newCimString(item0ElementName)).build());
      // RasdItemsList newSection = oldSection.toBuilder()
      //       .items(newItems)
      //       .build();
      // ...
      // long weight = random.nextInt(Integer.MAX_VALUE);
      // ResourceAllocationSettingData newSection = origSection.toBuilder()
      //         .weight(newCimUnsignedInt(weight))
      //         .build();
      // ...
      // checkHasMatchingItem("virtualHardwareSection/disk", modified, item0InstanceId, item0ElementName);
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection/media", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionMedia() {
      // Method under test
      RasdItemsList rasdItems = vmApi.getVirtualHardwareSectionMedia(vm.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection/memory", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionMemory() {
      // Method under test
      RasdItem rasd = vmApi.getVirtualHardwareSectionCpu(vm.getHref());

      // Check the retrieved object is well formed
      checkResourceAllocationSettingData(rasd);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/memory", dependsOnMethods = { "testGetVirtualHardwareSectionMemory" })
   public void testModifyVirtualHardwareSectionMemory() {
      RasdItem origItem = vmApi.getVirtualHardwareSectionMemory(vm.getHref());
      RasdItem newItem = origItem.toBuilder()
            .elementName("1024 MB of memory")
            .virtualQuantity(new BigInteger("1024"))
            .build();
      
      // Method under test
      Task modifyVirtualHardwareSectionMemory = vmApi.modifyVirtualHardwareSectionMemory(vm.getHref(), newItem);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionMemory), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionMemory"));

      // Retrieve the modified section
      RasdItem modified = vmApi.getVirtualHardwareSectionMemory(vm.getHref());
      
      // Check the retrieved object
      checkResourceAllocationSettingData(modified);
      
      // Check modified item
      assertEquals(modified.getVirtualQuantity(), new BigInteger("1024"),
            String.format(OBJ_FIELD_EQ, "ResourceAllocationSettingData", "VirtualQuantity", "1024", modified.getVirtualQuantity().toString()));
      assertEquals(modified, newItem);
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection/networkCards", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionNetworkCards() {
      // Method under test
      RasdItemsList rasdItems = vmApi.getVirtualHardwareSectionNetworkCards(vm.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/networkCards", dependsOnMethods = { "testGetVirtualHardwareSectionNetworkCards" })
   public void testModifyVirtualHardwareSectionNetworkCards() {
      RasdItemsList oldSection = vmApi.getVirtualHardwareSectionNetworkCards(vm.getHref());
      RasdItemsList newSection = oldSection.toBuilder().build();

      // Method under test
      Task modifyVirtualHardwareSectionNetworkCards = vmApi.modifyVirtualHardwareSectionNetworkCards(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionNetworkCards), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionNetworkCards"));

      // Retrieve the modified section
      RasdItemsList modified = vmApi.getVirtualHardwareSectionNetworkCards(vm.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(modified);
      
      // TODO What is modifiable? What can we change, so we can assert the change took effect? 
      // I tried changing "elementName" of one of the items, but it continued to have the old value when looked up post-modify.
      // See the description in testModifyVirtualHardwareSectionDisks
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection/serialPorts", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionSerialPorts() {
      // Method under test
      RasdItemsList rasdItems = vmApi.getVirtualHardwareSectionSerialPorts(vm.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/serialPorts", dependsOnMethods = { "testGetVirtualHardwareSectionSerialPorts" })
   public void testModifyVirtualHardwareSectionSerialPorts() {
      RasdItemsList oldSection = vmApi.getVirtualHardwareSectionSerialPorts(vm.getHref());
      RasdItemsList newSection = oldSection.toBuilder().build();

      // Method under test
      Task modifyVirtualHardwareSectionSerialPorts = vmApi.modifyVirtualHardwareSectionSerialPorts(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionSerialPorts), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionSerialPorts"));

      // Retrieve the modified section
      RasdItemsList modified = vmApi.getVirtualHardwareSectionSerialPorts(vm.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(modified);
      
      // TODO What is modifiable? What can we change, so we can assert the change took effect? 
      // I tried changing "elementName" of one of the items, but it continued to have the old value when looked up post-modify.
      // See the description in testModifyVirtualHardwareSectionDisks
   }

   @Test(description = "PUT /vApp/{id}/metadata/{key}", dependsOnMethods = { "testGetVm" })
   public void testSetMetadataValue() {
      key = name("key-");
      String value = name("value-");
      metadataValue = MetadataValue.builder().value(value).build();
      vmApi.getMetadataApi().setMetadata(vm.getHref(), key, metadataValue);

      // Retrieve the value, and assert it was set correctly
      MetadataValue newMetadataValue = vmApi.getMetadataApi().getMetadataValue(vm.getHref(), key);

      // Check the retrieved object is well formed
      checkMetadataValueFor(VM, newMetadataValue, value);
   }
   
   @Test(description = "GET /vApp/{id}/metadata", dependsOnMethods = { "testSetMetadataValue" })
   public void testGetMetadata() {
      // Call the method being tested
      Metadata metadata = vmApi.getMetadataApi().getMetadata(vm.getHref());
      
      checkMetadata(metadata);
      
      // Check requirements for this test
      assertFalse(Iterables.isEmpty(metadata.getMetadataEntries()), String.format(NOT_EMPTY_OBJECT_FMT, "MetadataEntry", "vm"));
   }
   
   @Test(description = "GET /vApp/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadata" })
   public void testGetOrgMetadataValue() {
      // Call the method being tested
      MetadataValue value = vmApi.getMetadataApi().getMetadataValue(vm.getHref(), key);
      
      String expected = metadataValue.getValue();

      checkMetadataValue(value);
      assertEquals(value.getValue(), expected, String.format(CORRECT_VALUE_OBJECT_FMT, "Value", "MetadataValue", expected, value.getValue()));
   }

   @Test(description = "DELETE /vApp/{id}/metadata/{key}", dependsOnMethods = { "testSetMetadataValue" })
   public void testDeleteMetadataEntry() {
      // Delete the entry
      Task task = vmApi.getMetadataApi().deleteMetadataEntry(vm.getHref(), key);
      retryTaskSuccess.apply(task);

      // Confirm the entry has been deleted
      Metadata newMetadata = vmApi.getMetadataApi().getMetadata(vm.getHref());

      // Check the retrieved object is well formed
      checkMetadataKeyAbsentFor(VM, newMetadata, key);
   }

   @Test(description = "POST /vApp/{id}/metadata", dependsOnMethods = { "testGetMetadata" })
   public void testMergeMetadata() {
      Metadata oldMetadata = vmApi.getMetadataApi().getMetadata(vm.getHref());
      Map<String, String> oldMetadataMap = Checks.metadataToMap(oldMetadata);

      // Store a value, to be deleted
      String key = name("key-");
      String value = name("value-");
      Metadata addedMetadata = Metadata.builder()
            .entry(MetadataEntry.builder().key(key).value(value).build())
            .build();
      Task task = vmApi.getMetadataApi().mergeMetadata(vm.getHref(), addedMetadata);
      retryTaskSuccess.apply(task);

      // Confirm the entry contains everything that was there, and everything that was being added
      Metadata newMetadata = vmApi.getMetadataApi().getMetadata(vm.getHref());
      Map<String, String> expectedMetadataMap = ImmutableMap.<String, String>builder()
            .putAll(oldMetadataMap)
            .put(key, value)
            .build();

      // Check the retrieved object is well formed
      checkMetadataFor(VM, newMetadata, expectedMetadataMap);
   }

   /**
    * @see VmApi#deleteVm(URI)
    */
   @Test(description = "DELETE /vApp/{id}")
   public void testDeleteVm() {
      // Create a temporary VApp to delete
      VApp delete = instantiateVApp();
      DeployVAppParams params = DeployVAppParams.builder()
            .deploymentLeaseSeconds((int)TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS))
            .notForceCustomization()
            .powerOn()
            .build();
      Task deployVApp = vAppApi.deploy(delete.getHref(), params);
      assertTaskSucceedsLong(deployVApp);

      // Get the updated VApp and the Vm
      delete = vAppApi.getVApp(delete.getHref());
      Vm temp = Iterables.getOnlyElement(delete.getChildren().getVms());

      // Power off the Vm
      temp = powerOffVm(temp.getHref());

      // The method under test
      Task deleteVm = vmApi.deleteVm(temp.getHref());
      assertTrue(retryTaskSuccess.apply(deleteVm), String.format(TASK_COMPLETE_TIMELY, "deleteVm"));

      Vm deleted = vmApi.getVm(temp.getHref());
      assertNull(deleted, "The Vm "+temp.getName()+" should have been deleted");
   }
}
