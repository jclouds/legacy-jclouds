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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.CONDITION_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.CORRECT_VALUE_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.ENTITY_EQUAL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.MATCHES_STRING_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.NOT_EMPTY_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ADMIN_USER;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.MEDIA;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkControlAccessParams;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkGuestCustomizationSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkLeaseSettingsSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadata;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataKeyAbsentFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataValue;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataValueFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkNetworkConfigSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkNetworkConnectionSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkNetworkSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkOperatingSystemSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkOwner;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkProductSectionList;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkRasdItemsList;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkResourceAllocationSettingData;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkRuntimeInfoSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkScreenTicket;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkStartupSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkVApp;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkVirtualHardwareSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkVmPendingQuestion;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.AccessSetting;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.MediaInsertOrEjectParams;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConnection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConnection.IpAddressAllocationMode;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.RasdItemsList;
import org.jclouds.vcloud.director.v1_5.domain.RecomposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status;
import org.jclouds.vcloud.director.v1_5.domain.RuntimeInfoSection;
import org.jclouds.vcloud.director.v1_5.domain.ScreenTicket;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VmPendingQuestion;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswer;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswerChoice;
import org.jclouds.vcloud.director.v1_5.domain.cim.OSType;
import org.jclouds.vcloud.director.v1_5.domain.cim.ResourceAllocationSettingData;
import org.jclouds.vcloud.director.v1_5.domain.ovf.MsgType;
import org.jclouds.vcloud.director.v1_5.domain.ovf.NetworkSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.ProductSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.StartupSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.VirtualHardwareSection;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecordType;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Tests behavior of the {@link VAppClient}.
 *
 * @author grkvlt@apache.org
 */
@Test(groups = { "live", "user", "vapp" }, singleThreaded = true, testName = "VAppClientLiveTest")
public class VAppClientLiveTest extends AbstractVAppClientLiveTest {

   private MetadataValue metadataValue;
   private String key;

   /**
    * @see VAppClient#getVApp(URI)
    */
   @Test(description = "GET /vApp/{id}")
   public void testGetVApp() {
      // The method under test
      vApp = vAppClient.getVApp(vAppURI);

      // Check the retrieved object is well formed
      checkVApp(vApp);

      // Check the required fields are set
      assertEquals(vApp.isDeployed(), Boolean.FALSE, String.format(OBJ_FIELD_EQ, VAPP, "deployed", "FALSE", vApp.isDeployed().toString()));
      assertTrue(vApp.getName().startsWith("test-vapp-"), String.format(MATCHES_STRING_FMT, "name", "test-vapp-*", vApp.getName()));
      assertEquals(vApp.getDescription(), "Test VApp", String.format(OBJ_FIELD_EQ, VAPP, "Description", "Test VApp", vApp.getDescription()));

      // TODO instantiationParams instantiationParams()
      // TODO source.href vAppTemplateURI

      // Check status
      assertVAppStatus(vAppURI, Status.POWERED_OFF);
   }

   /**
    * @see VAppClient#modifyVApp(URI, VApp)
    */
   @Test(description = "PUT /vApp/{id}", dependsOnMethods = { "testGetVApp" })
   public void testModifyVApp() {
      VApp newVApp = VApp.builder()
            .name(name("new-name-"))
            .description("New Description")
            .build();
      vAppNames.add(newVApp.getName());

      // The method under test
      Task modifyVApp = vAppClient.modifyVApp(vApp.getHref(), newVApp);
      assertTrue(retryTaskSuccess.apply(modifyVApp), String.format(TASK_COMPLETE_TIMELY, "modifyVApp"));

      // Get the updated VApp
      vApp = vAppClient.getVApp(vApp.getHref());

      // Check the required fields are set
      assertEquals(vApp.getName(), newVApp.getName(), String.format(OBJ_FIELD_EQ, VAPP, "Name", newVApp.getName(), vApp.getName()));
      assertEquals(vApp.getDescription(), newVApp.getDescription(), String.format(OBJ_FIELD_EQ, VAPP, "Description", newVApp.getDescription(), vApp.getDescription()));
   }

   @Test(description = "POST /vApp/{id}/action/deploy", dependsOnMethods = { "testGetVApp" })
   public void testDeployVApp() {
      DeployVAppParams params = DeployVAppParams.builder()
            .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS))
            .notForceCustomization()
            .notPowerOn()
            .build();

      // The method under test
      Task deployVApp = vAppClient.deploy(vApp.getHref(), params);
      assertTrue(retryTaskSuccessLong.apply(deployVApp), String.format(TASK_COMPLETE_TIMELY, "deployVApp"));

      // Get the updated VApp
      vApp = vAppClient.getVApp(vApp.getHref());

      // Check the required fields are set
      assertTrue(vApp.isDeployed(), String.format(OBJ_FIELD_EQ, VAPP, "deployed", "TRUE", vApp.isDeployed().toString()));

      // Check status
      assertVAppStatus(vAppURI, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/power/action/powerOn", dependsOnMethods = { "testDeployVApp" })
   public void testPowerOnVApp() {
      // Power off VApp
      vApp = powerOff(vApp);

      // The method under test
      Task powerOnVApp = vAppClient.powerOn(vApp.getHref());
      assertTaskSucceedsLong(powerOnVApp);

      // Get the updated VApp
      vApp = vAppClient.getVApp(vApp.getHref());

      // Check status
      assertVAppStatus(vAppURI, Status.POWERED_ON);
   }

   @Test(description = "POST /vApp/{id}/power/action/reboot", dependsOnMethods = { "testDeployVApp" })
   public void testReboot() {
      // Power on VApp
      vApp = powerOn(vApp);
 
      // The method under test
      Task reboot = vAppClient.reboot(vApp.getHref());
      assertTaskSucceedsLong(reboot);

      // Get the updated VApp
      vApp = vAppClient.getVApp(vApp.getHref());

      // Check status
      assertVAppStatus(vAppURI, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/power/action/shutdown", dependsOnMethods = { "testDeployVApp" })
   public void testShutdown() {
      // Power on VApp
      vApp = powerOn(vApp);

      // The method under test
      Task shutdown = vAppClient.shutdown(vAppURI);
      assertTaskSucceedsLong(shutdown);

      // Get the updated VApp
      vApp = vAppClient.getVApp(vAppURI);

      // Check status
      assertVAppStatus(vAppURI, Status.POWERED_OFF);

      // Power on the VApp again
      vApp = powerOn(vApp);
   }

   @Test(description = "POST /vApp/{id}/power/action/suspend", dependsOnMethods = { "testDeployVApp" })
   public void testSuspend() {
      // Power on VApp
      vApp = powerOn(vApp);

      // The method under test
      Task suspend = vAppClient.suspend(vAppURI);
      assertTaskSucceedsLong(suspend);

      // Get the updated VApp
      vApp = vAppClient.getVApp(vApp.getHref());

      // Check status
      assertVAppStatus(vAppURI, Status.SUSPENDED);

      // Power on the VApp again
      vApp = powerOn(vApp);
   }

   @Test(description = "POST /vApp/{id}/power/action/reset", dependsOnMethods = { "testDeployVApp" })
   public void testReset() {
      // Power on VApp
      vApp = powerOn(vApp);

      // The method under test
      Task reset = vAppClient.reset(vAppURI);
      assertTaskSucceedsLong(reset);

      // Get the updated VApp
      vApp = vAppClient.getVApp(vAppURI);

      // Check status
      assertVAppStatus(vAppURI, Status.POWERED_ON);
   }

   @Test(description = "POST /vApp/{id}/action/undeploy", dependsOnMethods = { "testDeployVApp" })
   public void testUndeployVApp() {
      // Power on VApp
      vApp = powerOn(vApp);

      UndeployVAppParams params = UndeployVAppParams.builder().build();

      // The method under test
      Task undeploy = vAppClient.undeploy(vApp.getHref(), params);
      assertTrue(retryTaskSuccess.apply(undeploy), String.format(TASK_COMPLETE_TIMELY, "undeploy"));

      // Get the updated VApp
      vApp = vAppClient.getVApp(vAppURI);

      // Check status
      assertFalse(vApp.isDeployed(), String.format(OBJ_FIELD_EQ, VAPP, "deployed", "FALSE", vApp.isDeployed().toString()));
      assertVAppStatus(vAppURI, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/power/action/powerOff", dependsOnMethods = { "testUndeployVApp" })
   public void testPowerOffVApp() {
      // Power on VApp
      vApp = powerOn(vApp);
      
      // The method under test
      Task powerOffVApp = vAppClient.powerOff(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(powerOffVApp), String.format(TASK_COMPLETE_TIMELY, "powerOffVApp"));

      // Get the updated VApp
      vApp = vAppClient.getVApp(vAppURI);

      // Check status
      assertVAppStatus(vAppURI, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/action/consolidate", dependsOnMethods = { "testDeployVApp" })
   public void testConsolidateVApp() {
      // Power on VApp
      vApp = powerOn(vApp);

      // The method under test
      Task consolidateVApp = vAppClient.consolidateVm(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(consolidateVApp), String.format(TASK_COMPLETE_TIMELY, "consolidateVApp"));
   }

   @Test(description = "POST /vApp/{id}/action/controlAccess", dependsOnMethods = { "testGetVApp" })
   public void testControlAccessUser() {
      ControlAccessParams params = ControlAccessParams.builder()
            .notSharedToEveryone()
            .accessSetting(AccessSetting.builder()
                  .subject(Reference.builder().href(userURI).type(ADMIN_USER).build())
                  .accessLevel("ReadOnly")
                  .build())
            .build();

      // The method under test
      ControlAccessParams modified = vAppClient.modifyControlAccess(vApp.getHref(), params);

      // Check the retrieved object is well formed
      checkControlAccessParams(modified);
      // Check the required fields are set
      assertEquals(modified, params, String.format(ENTITY_EQUAL, "ControlAccessParams"));
   }

   @Test(description = "POST /vApp/{id}/action/controlAccess", dependsOnMethods = { "testControlAccessUser" })
   public void testControlAccessEveryone() {
      ControlAccessParams params = ControlAccessParams.builder()
            .sharedToEveryone()
            .everyoneAccessLevel("FullControl")
            .build();

      // The method under test
      ControlAccessParams modified = vAppClient.modifyControlAccess(vApp.getHref(), params);

      // Check the retrieved object is well formed
      checkControlAccessParams(modified);

      // Check entities are equal
      assertEquals(modified, params, String.format(ENTITY_EQUAL, "ControlAccessParams"));
   }

   @Test(description = "POST /vApp/{id}/action/discardSuspendedState", dependsOnMethods = { "testDeployVApp" })
   public void testDiscardSuspendedState() {
      // Suspend the VApp
      vApp = suspend(vAppURI);
      
      // The method under test
      Task discardSuspendedState = vAppClient.discardSuspendedState(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(discardSuspendedState), String.format(TASK_COMPLETE_TIMELY, "discardSuspendedState"));
   }

   @Test(description = "POST /vApp/{id}/action/enterMaintenanceMode")
   public void testEnterMaintenanceMode() {
      // Do this to a new vApp, so don't mess up subsequent tests by making the vApp read-only
      VApp temp = instantiateVApp();
      DeployVAppParams params = DeployVAppParams.builder()
            .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS))
            .notForceCustomization()
            .notPowerOn()
            .build();
      Task deployVApp = vAppClient.deploy(temp.getHref(), params);
      assertTaskSucceedsLong(deployVApp);
      
      try {
         // Method under test
         vAppClient.enterMaintenanceMode(temp.getHref());
   
         temp = vAppClient.getVApp(temp.getHref());
         assertTrue(temp.isInMaintenanceMode(), String.format(CONDITION_FMT, "InMaintenanceMode", "TRUE", temp.isInMaintenanceMode()));

         // Exit maintenance mode
         vAppClient.exitMaintenanceMode(temp.getHref());
      } finally {
         cleanUpVApp(temp);
      }
   }

   @Test(description = "POST /vApp/{id}/action/exitMaintenanceMode", dependsOnMethods = { "testEnterMaintenanceMode" })
   public void testExitMaintenanceMode() {
      // Do this to a new vApp, so don't mess up subsequent tests by making the vApp read-only
      VApp temp = instantiateVApp();
      DeployVAppParams params = DeployVAppParams.builder()
            .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS))
            .notForceCustomization()
            .notPowerOn()
            .build();
      Task deployVApp = vAppClient.deploy(temp.getHref(), params);
      assertTaskSucceedsLong(deployVApp);
      
      try {
         // Enter maintenance mode
         vAppClient.enterMaintenanceMode(temp.getHref());
   
         // Method under test
         vAppClient.exitMaintenanceMode(temp.getHref());

         temp = vAppClient.getVApp(temp.getHref());
         assertFalse(temp.isInMaintenanceMode(), String.format(CONDITION_FMT, "InMaintenanceMode", "FALSE", temp.isInMaintenanceMode()));
      } finally {
         cleanUpVApp(temp);
      }
   }

   @Test(description = "POST /vApp/{id}/action/installVMwareTools", dependsOnMethods = { "testDeployVApp" })
   public void testInstallVMwareTools() {
      // First ensure the vApp is powered n
      vApp = powerOn(vApp);

      // The method under test
      Task installVMwareTools = vAppClient.installVMwareTools(vm.getHref());
      assertTrue(retryTaskSuccess.apply(installVMwareTools), String.format(TASK_COMPLETE_TIMELY, "installVMwareTools"));
   }

   // FIXME "Could not bind object to request[method=POST, endpoint=https://mycloud.greenhousedata.com/api/vApp/vapp-e124f3f0-adb9-4268-ad49-e54fb27e40af/action/recomposeVApp,
   //    headers={Accept=[application/vnd.vmware.vcloud.task+xml]}, payload=[content=true, contentMetadata=[contentDisposition=null, contentEncoding=null, contentLanguage=null,
   //    contentLength=0, contentMD5=null, contentType=application/vnd.vmware.vcloud.recomposeVAppParams+xml], written=false]]: Could not marshall object"
   @Test(description = "POST /vApp/{id}/action/recomposeVApp", dependsOnMethods = { "testGetVApp" })
   public void testRecomposeVApp() {
      RecomposeVAppParams params = RecomposeVAppParams.builder().build();

      // The method under test
      Task recomposeVApp = vAppClient.recompose(vApp.getHref(), params);
      assertTrue(retryTaskSuccess.apply(recomposeVApp), String.format(TASK_COMPLETE_TIMELY, "recomposeVApp"));
   }

   // NOTE This test is disabled, as it is not possible to look up datastores using the User API
   @Test(description = "POST /vApp/{id}/action/relocate", dependsOnMethods = { "testGetVApp" })
   public void testRelocate() {
      // Relocate to the last of the available datastores
      QueryResultRecords records = context.getApi().getQueryClient().queryAll("datastore");
      QueryResultRecordType datastore = Iterables.getLast(records.getRecords());
      RelocateParams params = RelocateParams.builder().datastore(Reference.builder().href(datastore.getHref()).build()).build();

      // The method under test
      Task relocate = vAppClient.relocateVm(vApp.getHref(), params);
      assertTrue(retryTaskSuccess.apply(relocate), String.format(TASK_COMPLETE_TIMELY, "relocate"));
   }

   @Test(description = "POST /vApp/{id}/action/upgradeHardwareVersion", dependsOnMethods = { "testGetVApp" })
   public void testUpgradeHardwareVersion() {
      // Power off VApp
      vApp = powerOff(vApp);

      // The method under test
      Task upgradeHardwareVersion = vAppClient.upgradeHardwareVersion(vm.getHref());
      assertTrue(retryTaskSuccess.apply(upgradeHardwareVersion), String.format(TASK_COMPLETE_TIMELY, "upgradeHardwareVersion"));
   }

   @Test(description = "GET /vApp/{id}/controlAccess", dependsOnMethods = { "testGetVApp" })
   public void testGetControlAccess() {
      // The method under test
      ControlAccessParams controlAccess = vAppClient.getControlAccess(vApp.getHref());

      // Check the retrieved object is well formed
      checkControlAccessParams(controlAccess);
   }

   @Test(description = "GET /vApp/{id}/guestCustomizationSection", dependsOnMethods = { "testGetVApp" })
   public void testGetGuestCustomizationSection() {
      getGuestCustomizationSection(new Function<URI, GuestCustomizationSection>() {
         @Override
         public GuestCustomizationSection apply(URI uri) {
            return vAppClient.getGuestCustomizationSection(uri);
         }
      });
   }

   @Test(description = "PUT /vApp/{id}/guestCustomizationSection", dependsOnMethods = { "testGetGuestCustomizationSection" })
   public void testModifyGuestCustomizationSection() {
      // Copy existing section and update fields
      GuestCustomizationSection oldSection = vAppClient.getGuestCustomizationSection(vm.getHref());
      GuestCustomizationSection newSection = oldSection.toBuilder()
            .computerName(name("n"))
            .enabled(Boolean.FALSE)
            .adminPassword(null) // Not allowed
            .build();

      // The method under test
      Task modifyGuestCustomizationSection = vAppClient.modifyGuestCustomizationSection(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyGuestCustomizationSection), String.format(TASK_COMPLETE_TIMELY, "modifyGuestCustomizationSection"));

      // Retrieve the modified section
      GuestCustomizationSection modified = vAppClient.getGuestCustomizationSection(vm.getHref());

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

   @Test(description = "GET /vApp/{id}/leaseSettingsSection", dependsOnMethods = { "testGetVApp" })
   public void testGetLeaseSettingsSection() {
      // The method under test
      LeaseSettingsSection section = vAppClient.getLeaseSettingsSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkLeaseSettingsSection(section);
   }

   @Test(description = "PUT /vApp/{id}/leaseSettingsSection", dependsOnMethods = { "testGetLeaseSettingsSection" })
   public void testModifyLeaseSettingsSection() {
      // Copy existing section
      LeaseSettingsSection oldSection = vAppClient.getLeaseSettingsSection(vApp.getHref());
      Integer twoHours = (int) TimeUnit.SECONDS.convert(2L, TimeUnit.HOURS);
      LeaseSettingsSection newSection = oldSection.toBuilder()
            .deploymentLeaseInSeconds(twoHours)
            .build();

      // The method under test
      Task modifyLeaseSettingsSection = vAppClient.modifyLeaseSettingsSection(vApp.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyLeaseSettingsSection), String.format(TASK_COMPLETE_TIMELY, "modifyLeaseSettingsSection"));

      // Retrieve the modified section
      LeaseSettingsSection modified = vAppClient.getLeaseSettingsSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkLeaseSettingsSection(modified);

      // Check the date fields
      if (modified.getDeploymentLeaseExpiration() != null && newSection.getDeploymentLeaseExpiration() != null) {
         assertTrue(modified.getDeploymentLeaseExpiration().after(newSection.getDeploymentLeaseExpiration()),
               String.format("The new deploymentLeaseExpiration timestamp must be later than the original: %s > %s",
                     dateService.iso8601DateFormat(modified.getDeploymentLeaseExpiration()),
                     dateService.iso8601DateFormat(newSection.getDeploymentLeaseExpiration())));
      }
      if (modified.getStorageLeaseExpiration() != null && newSection.getStorageLeaseExpiration() != null) {
         assertTrue(modified.getStorageLeaseExpiration().after(newSection.getStorageLeaseExpiration()),
               String.format("The new storageLeaseExpiration timestamp must be later than the original: %s > %s",
                     dateService.iso8601DateFormat(modified.getStorageLeaseExpiration()),
                     dateService.iso8601DateFormat(newSection.getStorageLeaseExpiration())));
      }

      // Reset the date fields
      modified = modified.toBuilder()
            .deploymentLeaseExpiration(null)
            .storageLeaseExpiration(null)
            .build();
      newSection = newSection.toBuilder()
            .deploymentLeaseExpiration(null)
            .storageLeaseExpiration(null)
            .build();

      // Check the section was modified correctly
      assertEquals(modified.getDeploymentLeaseInSeconds(), twoHours,
            String.format(OBJ_FIELD_EQ, "LeaseSettingsSection", "DeploymentLeaseInSeconds", Integer.toString(twoHours), modified.getDeploymentLeaseInSeconds().toString()));
      assertEquals(modified, newSection, String.format(ENTITY_EQUAL, "LeaseSettingsSection"));
   }

   // FIXME "Error: The requested operation on media "com.vmware.vcloud.entity.media:abfcb4b7-809f-4b50-a0aa-8c97bf09a5b0" is not supported in the current state."
   @Test(description = "PUT /vApp/{id}/media/action/insertMedia", dependsOnMethods = { "testGetVApp" })
   public void testInsertMedia() {
      // Setup media params from configured media id
      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .media(Reference.builder().href(mediaURI).type(MEDIA).build())
            .build();

      // The method under test
      Task insertMedia = vAppClient.insertMedia(vm.getHref(), params);
      assertTrue(retryTaskSuccess.apply(insertMedia), String.format(TASK_COMPLETE_TIMELY, "insertMedia"));
   }

   @Test(description = "PUT /vApp/{id}/media/action/ejectMedia", dependsOnMethods = { "testInsertMedia" })
   public void testEjectMedia() {
      // Setup media params from configured media id
      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .media(Reference.builder().href(mediaURI).type(MEDIA).build())
            .build();

      // The method under test
      Task ejectMedia = vAppClient.ejectMedia(vm.getHref(), params);
      assertTrue(retryTaskSuccess.apply(ejectMedia), String.format(TASK_COMPLETE_TIMELY, "ejectMedia"));
   }

   @Test(description = "GET /vApp/{id}/networkConfigSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkConfigSection() {
      // The method under test
      NetworkConfigSection section = vAppClient.getNetworkConfigSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkNetworkConfigSection(section);
   }

   @Test(description = "PUT /vApp/{id}/networkConfigSection", dependsOnMethods = { "testGetNetworkConfigSection" })
   public void testModifyNetworkConfigSection() {
      // Copy existing section and update fields
      NetworkConfigSection oldSection = vAppClient.getNetworkConfigSection(vApp.getHref());
      NetworkConfigSection newSection = oldSection.toBuilder()
            .build();

      // The method under test
      Task modifyNetworkConfigSection = vAppClient.modifyNetworkConfigSection(vApp.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyNetworkConfigSection), String.format(TASK_COMPLETE_TIMELY, "modifyNetworkConfigSection"));

      // Retrieve the modified section
      NetworkConfigSection modified = vAppClient.getNetworkConfigSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkNetworkConfigSection(modified);

      // Check the modified section fields are set correctly
//      assertEquals(modified.getInfo(), newSection.getInfo());

      // Check the section was modified correctly
      assertEquals(modified, newSection, String.format(ENTITY_EQUAL, "NetworkConfigSection"));
   }

   @Test(description = "GET /vApp/{id}/networkConnectionSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkConnectionSection() {
      getNetworkConnectionSection(new Function<URI, NetworkConnectionSection>() {
         @Override
         public NetworkConnectionSection apply(URI uri) {
            return vAppClient.getNetworkConnectionSection(uri);
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
      NetworkConnectionSection oldSection = vAppClient.getNetworkConnectionSection(vm.getHref());
      NetworkConnectionSection newSection = oldSection.toBuilder()
            .networkConnection(NetworkConnection.builder()
                  .ipAddressAllocationMode(IpAddressAllocationMode.DHCP.toString())
                  .network(network.getName())
                  .build())
            .build();

      // The method under test
      Task modifyNetworkConnectionSection = vAppClient.modifyNetworkConnectionSection(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyNetworkConnectionSection), String.format(TASK_COMPLETE_TIMELY, "modifyNetworkConnectionSection"));

      // Retrieve the modified section
      NetworkConnectionSection modified = vAppClient.getNetworkConnectionSection(vm.getHref());

      // Check the retrieved object is well formed
      checkNetworkConnectionSection(modified);

      // Check the modified section has an extra network connection
      assertEquals(modified.getNetworkConnections().size(), newSection.getNetworkConnections().size() + 1);

      // Check the section was modified correctly
      assertEquals(modified, newSection, String.format(ENTITY_EQUAL, "NetworkConnectionSection"));
   }

   @Test(description = "GET /vApp/{id}/networkSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkSection() {
      // The method under test
      NetworkSection section = vAppClient.getNetworkSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkNetworkSection(section);
   }

   @Test(description = "GET /vApp/{id}/operatingSystemSection", dependsOnMethods = { "testGetVApp" })
   public void testGetOperatingSystemSection() {
      // The method under test
      OperatingSystemSection section = vAppClient.getOperatingSystemSection(vm.getHref());

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
      Task modifyOperatingSystemSection = vAppClient.modifyOperatingSystemSection(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyOperatingSystemSection), String.format(TASK_COMPLETE_TIMELY, "modifyOperatingSystemSection"));

      // Retrieve the modified section
      OperatingSystemSection modified = vAppClient.getOperatingSystemSection(vm.getHref());

      // Check the retrieved object is well formed
      checkOperatingSystemSection(modified);

      // Check the modified section fields are set correctly
      assertEquals(modified.getId(), newSection.getId());
   }

   @Test(description = "GET /vApp/{id}/owner", dependsOnMethods = { "testGetVApp" })
   public void testGetOwner() {
      // The method under test
      Owner owner = vAppClient.getOwner(vApp.getHref());

      // Check the retrieved object is well formed
      checkOwner(owner);
   }

   @Test(description = "PUT /vApp/{id}/owner", dependsOnMethods = { "testGetOwner" })
   public void testModifyOwner() {
      Owner newOwner = Owner.builder().user(Reference.builder().href(userURI).type(ADMIN_USER).build()).build();

      // The method under test
      vAppClient.modifyOwner(vApp.getHref(), newOwner);

      // Get the new VApp owner
      Owner modified = vAppClient.getOwner(vApp.getHref());

      // Check the retrieved object is well formed
      checkOwner(modified);

      // Check the href fields match
      assertEquals(modified.getUser().getHref(), newOwner.getUser().getHref());
   }

   @Test(description = "GET /vApp/{id}/productSections", dependsOnMethods = { "testGetVApp" })
   public void testGetProductSections() {
      // The method under test
      ProductSectionList sectionList = vAppClient.getProductSections(vApp.getHref());

      // Check the retrieved object is well formed
      checkProductSectionList(sectionList);
   }

   @Test(description = "PUT /vApp/{id}/productSections", dependsOnMethods = { "testGetProductSections" })
   public void testModifyProductSections() {
      // Copy existing section and update fields
      ProductSectionList oldSections = vAppClient.getProductSections(vApp.getHref());
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
      Task modifyProductSections = vAppClient.modifyProductSections(vApp.getHref(), newSections);
      assertTrue(retryTaskSuccess.apply(modifyProductSections), String.format(TASK_COMPLETE_TIMELY, "modifyProductSections"));

      // Retrieve the modified section
      ProductSectionList modified = vAppClient.getProductSections(vApp.getHref());

      // Check the retrieved object is well formed
      checkProductSectionList(modified);

      // Check the modified object has an extra ProductSection
      assertEquals(modified.getProductSections().size(), oldSections.getProductSections().size() + 1);

      // Check the section was modified correctly
      assertEquals(modified, newSections, String.format(ENTITY_EQUAL, "ProductSectionList"));
   }

   // FIXME How do we force it to ask a question?
   @Test(description = "GET /vApp/{id}/question", dependsOnMethods = { "testDeployVApp" })
   public void testGetPendingQuestion() {
      // Power on VApp
      vApp = powerOn(vAppURI);

      // TODO how to test?

      // The method under test
      VmPendingQuestion question = vAppClient.getPendingQuestion(vm.getHref());

      // Check the retrieved object is well formed
      checkVmPendingQuestion(question);
   }

   @Test(description = "POST /vApp/{id}/question/action/answer", dependsOnMethods = { "testGetPendingQuestion" })
   public void testAnswerQuestion() {
      // TODO check that the question has been answered (e.g. asking for getPendingQuestion does not
      // include our answered question).

      VmPendingQuestion question = vAppClient.getPendingQuestion(vm.getHref());
      List<VmQuestionAnswerChoice> answerChoices = question.getChoices();
      VmQuestionAnswerChoice answerChoice = Iterables.getFirst(answerChoices, null);
      assertNotNull(answerChoice, "Question "+question+" must have at least once answer-choice");
      
      VmQuestionAnswer answer = VmQuestionAnswer.builder()
               .choiceId(answerChoice.getId())
               .questionId(question.getQuestionId())
               .build();
      
      vAppClient.answerQuestion(vm.getHref(), answer);
   }

   @Test(description = "GET /vApp/{id}/runtimeInfoSection", dependsOnMethods = { "testGetVApp" })
   public void testGetRuntimeInfoSection() {
      // The method under test
      RuntimeInfoSection section = vAppClient.getRuntimeInfoSection(vm.getHref());

      // Check the retrieved object is well formed
      checkRuntimeInfoSection(section);
   }

   // FIXME If still failing, consider escalating?
   @Test(description = "GET /vApp/{id}/screen", dependsOnMethods = { "testDeployVApp" })
   public void testGetScreenImage() {
      // Power on VApp
      vApp = powerOn(vApp);
      
      // The method under test
      byte[] image = vAppClient.getScreenImage(vm.getHref());

      // Check returned bytes against PNG header magic number
      byte[] pngHeaderBytes = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
      assertNotNull(image);
      assertTrue(image.length > pngHeaderBytes.length);
      for (int i = 0; i < pngHeaderBytes.length; i++) {
         assertEquals(image[i], pngHeaderBytes[i], String.format("Image differs from PNG format at byte %d of header", i));
      }
   }

   @Test(description = "GET /vApp/{id}/screen/action/acquireTicket", dependsOnMethods = { "testDeployVApp" })
   public void testGetScreenTicket() {
      // Power on VApp
      vApp = powerOn(vApp);
      
      // The method under test
      ScreenTicket ticket = vAppClient.getScreenTicket(vm.getHref());

      // Check the retrieved object is well formed
      checkScreenTicket(ticket);
   }

   @Test(description = "GET /vApp/{id}/startupSection", dependsOnMethods = { "testGetVApp" })
   public void testGetStartupSection() {
      // The method under test
      StartupSection section = vAppClient.getStartupSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkStartupSection(section);
   }

   @Test(description = "PUT /vApp/{id}/startupSection", dependsOnMethods = { "testGetStartupSection" })
   public void testModifyStartupSection() {
      // Copy existing section and update fields
      StartupSection oldSection = vAppClient.getStartupSection(vApp.getHref());
      StartupSection newSection = oldSection.toBuilder().build();

      // The method under test
      Task modifyStartupSection = vAppClient.modifyStartupSection(vApp.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyStartupSection), String.format(TASK_COMPLETE_TIMELY, "modifyStartupSection"));

      // Retrieve the modified section
      StartupSection modified = vAppClient.getStartupSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkStartupSection(modified);

      // Check the modified section fields are set correctly
      // assertEquals(modified.getX(), "");
      assertEquals(modified, newSection);
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection", dependsOnMethods = { "testGetVApp" })
   public void testGetVirtualHardwareSection() {
      // Method under test
      VirtualHardwareSection hardware = vAppClient.getVirtualHardwareSection(vm.getHref());

      // Check the retrieved object is well formed
      checkVirtualHardwareSection(hardware);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testModifyVirtualHardwareSection() {
      // Power off VApp
      vApp = powerOff(vApp);

      // Copy existing section and update fields
      VirtualHardwareSection oldSection = vAppClient.getVirtualHardwareSection(vm.getHref());
      Set<ResourceAllocationSettingData> oldItems = oldSection.getItems();
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
      Task modifyVirtualHardwareSection = vAppClient.modifyVirtualHardwareSection(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSection), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSection"));

      // Retrieve the modified section
      VirtualHardwareSection modifiedSection = vAppClient.getVirtualHardwareSection(vm.getHref());

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
      ResourceAllocationSettingData rasd = vAppClient.getVirtualHardwareSectionCpu(vm.getHref());

      // Check the retrieved object is well formed
      checkResourceAllocationSettingData(rasd);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/cpu", dependsOnMethods = { "testGetVirtualHardwareSectionCpu" })
   public void testModifyVirtualHardwareSectionCpu() {
      // Copy existing section and update fields
      ResourceAllocationSettingData oldItem = vAppClient.getVirtualHardwareSectionCpu(vm.getHref());
      ResourceAllocationSettingData newItem = oldItem.toBuilder()
            .elementName("2 virtual CPU(s)")
            .virtualQuantity(new BigInteger("2"))
            .build();
      
      // Method under test
      Task modifyVirtualHardwareSectionCpu = vAppClient.modifyVirtualHardwareSectionCpu(vm.getHref(), newItem);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionCpu), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionCpu"));

      // Retrieve the modified section
      ResourceAllocationSettingData modified = vAppClient.getVirtualHardwareSectionCpu(vm.getHref());
      
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
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionDisks(vm.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/disks", dependsOnMethods = { "testGetVirtualHardwareSectionDisks" })
   public void testModifyVirtualHardwareSectionDisks() {
      // Copy the existing items list and modify the name of an item
      RasdItemsList oldSection = vAppClient.getVirtualHardwareSectionDisks(vm.getHref());
      RasdItemsList newSection = oldSection.toBuilder().build();

      // Method under test
      Task modifyVirtualHardwareSectionDisks = vAppClient.modifyVirtualHardwareSectionDisks(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionDisks), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionDisks"));

      // Retrieve the modified section
      RasdItemsList modified = vAppClient.getVirtualHardwareSectionDisks(vm.getHref());

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
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionMedia(vm.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection/memory", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionMemory() {
      // Method under test
      ResourceAllocationSettingData rasd = vAppClient.getVirtualHardwareSectionCpu(vm.getHref());

      // Check the retrieved object is well formed
      checkResourceAllocationSettingData(rasd);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/memory", dependsOnMethods = { "testGetVirtualHardwareSectionMemory" })
   public void testModifyVirtualHardwareSectionMemory() {
      ResourceAllocationSettingData origItem = vAppClient.getVirtualHardwareSectionMemory(vm.getHref());
      ResourceAllocationSettingData newItem = origItem.toBuilder()
            .elementName("1024 MB of memory")
            .virtualQuantity(new BigInteger("1024"))
            .build();
      
      // Method under test
      Task modifyVirtualHardwareSectionMemory = vAppClient.modifyVirtualHardwareSectionMemory(vm.getHref(), newItem);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionMemory), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionMemory"));

      // Retrieve the modified section
      ResourceAllocationSettingData modified = vAppClient.getVirtualHardwareSectionMemory(vm.getHref());
      
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
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionNetworkCards(vm.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/networkCards", dependsOnMethods = { "testGetVirtualHardwareSectionNetworkCards" })
   public void testModifyVirtualHardwareSectionNetworkCards() {
      RasdItemsList oldSection = vAppClient.getVirtualHardwareSectionNetworkCards(vm.getHref());
      RasdItemsList newSection = oldSection.toBuilder().build();

      // Method under test
      Task modifyVirtualHardwareSectionNetworkCards = vAppClient.modifyVirtualHardwareSectionNetworkCards(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionNetworkCards), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionNetworkCards"));

      // Retrieve the modified section
      RasdItemsList modified = vAppClient.getVirtualHardwareSectionNetworkCards(vm.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(modified);
      
      // TODO What is modifiable? What can we change, so we can assert the change took effect? 
      // I tried changing "elementName" of one of the items, but it continued to have the old value when looked up post-modify.
      // See the description in testModifyVirtualHardwareSectionDisks
   }

   @Test(description = "GET /vApp/{id}/virtualHardwareSection/serialPorts", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionSerialPorts() {
      // Method under test
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionSerialPorts(vm.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(description = "PUT /vApp/{id}/virtualHardwareSection/serialPorts", dependsOnMethods = { "testGetVirtualHardwareSectionSerialPorts" })
   public void testModifyVirtualHardwareSectionSerialPorts() {
      RasdItemsList oldSection = vAppClient.getVirtualHardwareSectionSerialPorts(vm.getHref());
      RasdItemsList newSection = oldSection.toBuilder().build();

      // Method under test
      Task modifyVirtualHardwareSectionSerialPorts = vAppClient.modifyVirtualHardwareSectionSerialPorts(vm.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionSerialPorts), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionSerialPorts"));

      // Retrieve the modified section
      RasdItemsList modified = vAppClient.getVirtualHardwareSectionSerialPorts(vm.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(modified);
      
      // TODO What is modifiable? What can we change, so we can assert the change took effect? 
      // I tried changing "elementName" of one of the items, but it continued to have the old value when looked up post-modify.
      // See the description in testModifyVirtualHardwareSectionDisks
   }

   @Test(description = "PUT /vApp/{id}/metadata", dependsOnMethods = { "testGetVApp" })
   public void testSetMetadataValue() {
      key = name("key-");
      String value = name("value-");
      metadataValue = MetadataValue.builder().value(value).build();
      vAppClient.getMetadataClient().setMetadata(vApp.getHref(), key, metadataValue);

      // Retrieve the value, and assert it was set correctly
      MetadataValue newMetadataValue = vAppClient.getMetadataClient().getMetadataValue(vApp.getHref(), key);

      // Check the retrieved object is well formed
      checkMetadataValueFor(VAPP, newMetadataValue, value);
   }
   
   @Test(description = "GET /vApp/{id}/metadata", dependsOnMethods = { "testSetMetadataValue" })
   public void testGetMetadata() {
      // Call the method being tested
      Metadata metadata = vAppClient.getMetadataClient().getMetadata(vApp.getHref());
      
      checkMetadata(metadata);
      
      // Check requirements for this test
      assertFalse(Iterables.isEmpty(metadata.getMetadataEntries()), String.format(NOT_EMPTY_OBJECT_FMT, "MetadataEntry", "vApp"));
   }
   
   @Test(description = "GET /vApp/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadata" })
   public void testGetOrgMetadataValue() {
      // Call the method being tested
      MetadataValue value = vAppClient.getMetadataClient().getMetadataValue(vApp.getHref(), key);
      
      String expected = metadataValue.getValue();

      checkMetadataValue(value);
      assertEquals(value.getValue(), expected, String.format(CORRECT_VALUE_OBJECT_FMT, "Value", "MetadataValue", expected, value.getValue()));
   }

   @Test(description = "DELETE /vApp/{id}/metadata/{key}", dependsOnMethods = { "testSetMetadataValue" })
   public void testDeleteMetadataEntry() {
      // Delete the entry
      Task task = vAppClient.getMetadataClient().deleteMetadataEntry(vApp.getHref(), key);
      retryTaskSuccess.apply(task);

      // Confirm the entry has been deleted
      Metadata newMetadata = vAppClient.getMetadataClient().getMetadata(vApp.getHref());

      // Check the retrieved object is well formed
      checkMetadataKeyAbsentFor(VAPP, newMetadata, key);
   }

   @Test(description = "POST /vApp/{id}/metadata", dependsOnMethods = { "testGetMetadata" })
   public void testMergeMetadata() {
      Metadata oldMetadata = vAppClient.getMetadataClient().getMetadata(vApp.getHref());
      Map<String, String> oldMetadataMap = Checks.metadataToMap(oldMetadata);

      // Store a value, to be deleted
      String key = name("key-");
      String value = name("value-");
      Metadata addedMetadata = Metadata.builder()
            .entry(MetadataEntry.builder().key(key).value(value).build())
            .build();
      Task task = vAppClient.getMetadataClient().mergeMetadata(vApp.getHref(), addedMetadata);
      retryTaskSuccess.apply(task);

      // Confirm the entry contains everything that was there, and everything that was being added
      Metadata newMetadata = vAppClient.getMetadataClient().getMetadata(vApp.getHref());
      Map<String, String> expectedMetadataMap = ImmutableMap.<String, String>builder()
            .putAll(oldMetadataMap)
            .put(key, value)
            .build();

      // Check the retrieved object is well formed
      checkMetadataFor(VAPP, newMetadata, expectedMetadataMap);
   }

   /**
    * @see VAppClient#deleteVApp(URI)
    */
   @Test(description = "DELETE /vApp/{id}")
   public void testDeleteVApp() {
      // Create a temporary VApp to delete
      VApp temp = instantiateVApp();

      // The method under test
      Task deleteVApp = vAppClient.deleteVApp(temp.getHref());
      assertTrue(retryTaskSuccess.apply(deleteVApp), String.format(TASK_COMPLETE_TIMELY, "deleteVApp"));

      try {
         vAppClient.getVApp(temp.getHref());
         fail("The VApp "+temp+" should have been deleted");
      } catch (VCloudDirectorException vcde) {
         assertEquals(vcde.getError().getMajorErrorCode(), Integer.valueOf(403), "The error code for deleted vApp should have been 'Forbidden' (403)");
      }
   }
}
