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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.ENTITY_EQUAL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.MATCHES_STRING_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ADMIN_USER;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.MEDIA;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkControlAccessParams;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkGuestCustomizationSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkLeaseSettingsSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataKeyAbsentFor;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.AccessSetting;
import org.jclouds.vcloud.director.v1_5.domain.AccessSettings;
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

   /**
    * @see VAppClient#getVApp(URI)
    */
   @Test(testName = "GET /vApp/{id}")
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
      Status poweredOffStatus = Status.POWERED_OFF;
      assertEquals(vApp.getStatus(), poweredOffStatus.getValue(),String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOffStatus.toString(), Status.fromValue(vApp.getStatus()).toString()));
   }

   /**
    * @see VAppClient#modifyVApp(URI, VApp)
    */
   @Test(testName = "PUT /vApp/{id}", dependsOnMethods = { "testGetVApp" })
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

   @Test(testName = "POST /vApp/{id}/action/deploy", dependsOnMethods = { "testGetVApp" })
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
      assertEquals(vApp.isDeployed(), Boolean.TRUE, String.format(OBJ_FIELD_EQ, VAPP, "deployed", "TRUE", vApp.isDeployed().toString()));

      // Check status
      Status deployedStatus = Status.POWERED_OFF;
      assertEquals(vApp.getStatus(), deployedStatus.getValue(), String.format(OBJ_FIELD_EQ, VAPP, "status", deployedStatus.toString(), Status.fromValue(vApp.getStatus()).toString()));
   }

   @Test(testName = "POST /vApp/{id}/power/action/powerOn", dependsOnMethods = { "testDeployVApp" })
   public void testPowerOnVApp() {
      // The method under test
      Task powerOnVApp = vAppClient.powerOn(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(powerOnVApp), String.format(TASK_COMPLETE_TIMELY, "powerOnVApp"));

      // Get the updated VApp
      vApp = vAppClient.getVApp(vApp.getHref());

      // Check status
      Status poweredOnStatus = Status.POWERED_ON;
      assertEquals(vApp.getStatus(), poweredOnStatus.getValue(), String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOnStatus.toString(), Status.fromValue(vApp.getStatus()).toString()));
   }

   @Test(testName = "POST /vApp/{id}/power/action/reboot", dependsOnMethods = { "testPowerOnVApp" })
   public void testReboot() {
      // The method under test
      Task reboot = vAppClient.reboot(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(reboot), String.format(TASK_COMPLETE_TIMELY, "reboot"));

      // Get the updated VApp
      vApp = vAppClient.getVApp(vApp.getHref());

      // Check status
      Status poweredOffStatus = Status.POWERED_OFF;
      assertEquals(vApp.getStatus(), poweredOffStatus.getValue(), String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOffStatus.toString(), Status.fromValue(vApp.getStatus()).toString()));
   }

   @Test(testName = "POST /vApp/{id}/power/action/shutdown", dependsOnMethods = { "testReboot" })
   public void testShutdown() {
      // The method under test
      Task shutdown = vAppClient.shutdown(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(shutdown), String.format(TASK_COMPLETE_TIMELY, "shutdown"));

      // Get the updated VApp
      vApp = vAppClient.getVApp(vApp.getHref());

      // Check status
      Status poweredOnStatus = Status.POWERED_ON;
      assertEquals(vApp.getStatus(), poweredOnStatus.getValue(), String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOnStatus.toString(), Status.fromValue(vApp.getStatus()).toString()));
   }

   @Test(testName = "POST /vApp/{id}/power/action/suspend", dependsOnMethods = { "testShutdown" })
   public void testSuspend() {
      // The method under test
      Task suspend = vAppClient.suspend(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(suspend), String.format(TASK_COMPLETE_TIMELY, "suspend"));

      // Get the updated VApp
      vApp = vAppClient.getVApp(vApp.getHref());

      // Check status
      Status poweredOnStatus = Status.POWERED_ON;
      assertEquals(vApp.getStatus(), poweredOnStatus.getValue(), String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOnStatus.toString(), Status.fromValue(vApp.getStatus()).toString()));
   }

   @Test(testName = "POST /vApp/{id}/power/action/reset", dependsOnMethods = { "testSuspend" })
   public void testReset() {
      // The method under test
      Task reset = vAppClient.reset(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(reset), String.format(TASK_COMPLETE_TIMELY, "reset"));

      // Get the updated VApp
      vApp = vAppClient.getVApp(vApp.getHref());

      // Check status
      Status poweredOnStatus = Status.POWERED_ON;
      assertEquals(vApp.getStatus(), poweredOnStatus.getValue(), String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOnStatus.toString(), Status.fromValue(vApp.getStatus()).toString()));
   }

   @Test(testName = "POST /vApp/{id}/action/undeploy", dependsOnMethods = { "testReset" })
   public void testUndeployVApp() {
      UndeployVAppParams params = UndeployVAppParams.builder().build();

      // The method under test
      Task undeploy = vAppClient.undeploy(vApp.getHref(), params);
      assertTrue(retryTaskSuccess.apply(undeploy), String.format(TASK_COMPLETE_TIMELY, "undeploy"));

      // Get the updated VApp
      vApp = vAppClient.getVApp(vApp.getHref());

      // Check status
      Status poweredOnStatus = Status.POWERED_ON;
      assertEquals(vApp.getStatus(), poweredOnStatus.getValue(), String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOnStatus.toString(), Status.fromValue(vApp.getStatus()).toString()));
   }

   @Test(testName = "POST /vApp/{id}/power/action/powerOff", dependsOnMethods = { "testUndeployVApp" })
   public void testPowerOffVApp() {
      // The method under test
      Task powerOffVApp = vAppClient.powerOff(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(powerOffVApp), String.format(TASK_COMPLETE_TIMELY, "powerOffVApp"));

      // Get the updated VApp
      vApp = vAppClient.getVApp(vApp.getHref());

      // Check status
      Status poweredOffStatus = Status.POWERED_OFF;
      assertEquals(vApp.getStatus(), poweredOffStatus.getValue(), String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOffStatus.toString(), Status.fromValue(vApp.getStatus()).toString()));
   }

   @Test(testName = "POST /vApp/{id}/action/consolidate", dependsOnMethods = { "testPowerOnVApp" })
   public void testConsolidateVApp() {
      // The method under test
      Task consolidateVApp = vAppClient.consolidateVApp(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(consolidateVApp), String.format(TASK_COMPLETE_TIMELY, "consolidateVApp"));
   }

   @Test(testName = "POST /vApp/{id}/action/controlAccess", dependsOnMethods = { "testGetVApp" })
   public void testControlAccessUser() {
      ControlAccessParams params = ControlAccessParams.builder()
            .notSharedToEveryone()
            .accessSettings(AccessSettings.builder()
                  .accessSetting(AccessSetting.builder()
                        .subject(Reference.builder().href(userURI).type(ADMIN_USER).build())
                        .accessLevel("ReadOnly")
                        .build())
                  .build())
            .build();

      // The method under test
      ControlAccessParams modified = vAppClient.controlAccess(vApp.getHref(), params);

      // Check the retrieved object is well formed
      checkControlAccessParams(modified);
      // Check the required fields are set
      assertEquals(modified, params, String.format(ENTITY_EQUAL, "ControlAccessParams"));
   }

   @Test(testName = "POST /vApp/{id}/action/controlAccess", dependsOnMethods = { "testControlAccessUser" })
   public void testControlAccessEveryone() {
      ControlAccessParams params = ControlAccessParams.builder()
            .sharedToEveryone()
            .everyoneAccessLevel("FullControl")
            .build();

      // The method under test
      ControlAccessParams modified = vAppClient.controlAccess(vApp.getHref(), params);

      // Check the retrieved object is well formed
      checkControlAccessParams(modified);

      // Check entities are equal
      assertEquals(modified, params, String.format(ENTITY_EQUAL, "ControlAccessParams"));
   }

   @Test(testName = "POST /vApp/{id}/action/discardSuspendedState", dependsOnMethods = { "testSuspend" })
   public void testDiscardSuspendedState() {
      // The method under test
      Task discardSuspendedState = vAppClient.discardSuspendedState(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(discardSuspendedState), String.format(TASK_COMPLETE_TIMELY, "discardSuspendedState"));
   }

   @Test(testName = "POST /vApp/{id}/action/enterMaintenanceMode", dependsOnMethods = { "testPowerOnVApp" })
   public void testEnterMaintenanceMode() {
      // The method under test
      vAppClient.enterMaintenanceMode(vApp.getHref());

      vApp = vAppClient.getVApp(vApp.getHref());
      assertTrue(vApp.isInMaintenanceMode(), String.format(CONDITION_FMT, "InMaintenanceMode", "TRUE", vApp.isInMaintenanceMode()));
   }

   @Test(testName = "POST /vApp/{id}/action/exitMaintenanceMode", dependsOnMethods = { "testEnterMaintenanceMode" })
   public void testExitMaintenanceMode() {
      // The method under test
      vAppClient.exitMaintenanceMode(vApp.getHref());

      vApp = vAppClient.getVApp(vApp.getHref());
      assertFalse(vApp.isInMaintenanceMode(), String.format(CONDITION_FMT, "InMaintenanceMode", "FALSE", vApp.isInMaintenanceMode()));
   }

   @Test(testName = "POST /vApp/{id}/action/installVMwareTools", dependsOnMethods = { "testPowerOnVApp" })
   public void testInstallVMwareTools() {
      // The method under test
      Task installVMwareTools = vAppClient.installVMwareTools(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(installVMwareTools), String.format(TASK_COMPLETE_TIMELY, "installVMwareTools"));
   }

   // FIXME "Could not bind object to request[method=POST, endpoint=https://mycloud.greenhousedata.com/api/vApp/vapp-e124f3f0-adb9-4268-ad49-e54fb27e40af/action/recomposeVApp,
   //    headers={Accept=[application/vnd.vmware.vcloud.task+xml]}, payload=[content=true, contentMetadata=[contentDisposition=null, contentEncoding=null, contentLanguage=null,
   //    contentLength=0, contentMD5=null, contentType=application/vnd.vmware.vcloud.recomposeVAppParams+xml], written=false]]: Could not marshall object"
   @Test(testName = "POST /vApp/{id}/action/recomposeVApp", dependsOnMethods = { "testGetVApp" })
   public void testRecomposeVApp() {
      RecomposeVAppParams params = RecomposeVAppParams.builder().build();

      // The method under test
      Task recomposeVApp = vAppClient.recomposeVApp(vApp.getHref(), params);
      assertTrue(retryTaskSuccess.apply(recomposeVApp), String.format(TASK_COMPLETE_TIMELY, "recomposeVApp"));
   }

   // NOTE This test is disabled, as it is not possible to look up datastores using the User API
   @Test(testName = "POST /vApp/{id}/action/relocate", dependsOnMethods = { "testGetVApp" })
   public void testRelocate() {
      // Relocate to the last of the available datastores
      QueryResultRecords records = context.getApi().getQueryClient().queryAll("datastore");
      QueryResultRecordType datastore = Iterables.getLast(records.getRecords());
      RelocateParams params = RelocateParams.builder().datastore(Reference.builder().href(datastore.getHref()).build()).build();

      // The method under test
      Task relocate = vAppClient.relocate(vApp.getHref(), params);
      assertTrue(retryTaskSuccess.apply(relocate), String.format(TASK_COMPLETE_TIMELY, "relocate"));
   }

   @Test(testName = "POST /vApp/{id}/action/upgradeHardwareVersion", dependsOnMethods = { "testGetVApp" })
   public void testUpgradeHardwareVersion() {
      // The method under test
      Task upgradeHardwareVersion = vAppClient.upgradeHardwareVersion(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(upgradeHardwareVersion), String.format(TASK_COMPLETE_TIMELY, "upgradeHardwareVersion"));
   }

   @Test(testName = "GET /vApp/{id}/controlAccess", dependsOnMethods = { "testGetVApp" })
   public void testGetControlAccess() {
      // The method under test
      ControlAccessParams controlAccess = vAppClient.getControlAccess(vApp.getHref());

      // Check the retrieved object is well formed
      checkControlAccessParams(controlAccess);
   }

   @Test(testName = "GET /vApp/{id}/guestCustomizationSection", dependsOnMethods = { "testGetVApp" })
   public void testGetGuestCustomizationSection() {
      getGuestCustomizationSection(new Function<URI, GuestCustomizationSection>() {
         @Override
         public GuestCustomizationSection apply(URI uri) {
            return vAppClient.getGuestCustomizationSection(uri);
         }
      });
   }

   @Test(testName = "PUT /vApp/{id}/guestCustomizationSection", dependsOnMethods = { "testGetGuestCustomizationSection" })
   public void testModifyGuestCustomizationSection() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // Copy existing section and update fields
      GuestCustomizationSection oldSection = vAppClient.getGuestCustomizationSection(vmURI);
      GuestCustomizationSection newSection = oldSection.toBuilder()
            .computerName("newComputerName")
            .enabled(Boolean.FALSE)
            .adminPassword(null) // Not allowed
            .build();

      // The method under test
      Task modifyGuestCustomizationSection = vAppClient.modifyGuestCustomizationSection(vmURI, newSection);
      assertTrue(retryTaskSuccess.apply(modifyGuestCustomizationSection), String.format(TASK_COMPLETE_TIMELY, "modifyGuestCustomizationSection"));

      // Retrieve the modified section
      GuestCustomizationSection modified = vAppClient.getGuestCustomizationSection(vmURI);

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

   @Test(testName = "GET /vApp/{id}/leaseSettingsSection", dependsOnMethods = { "testGetVApp" })
   public void testGetLeaseSettingsSection() {
      // The method under test
      LeaseSettingsSection section = vAppClient.getLeaseSettingsSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkLeaseSettingsSection(section);
   }

   @Test(testName = "PUT /vApp/{id}/leaseSettingsSection", dependsOnMethods = { "testGetLeaseSettingsSection" })
   public void testModifyLeaseSettingsSection() {
      // Copy existing section
      LeaseSettingsSection oldSection = vAppClient.getLeaseSettingsSection(vApp.getHref());
      LeaseSettingsSection newSection = oldSection.toBuilder().build();

      // The method under test
      Task modifyLeaseSettingsSection = vAppClient.modifyLeaseSettingsSection(vApp.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyLeaseSettingsSection), String.format(TASK_COMPLETE_TIMELY, "modifyLeaseSettingsSection"));

      // Retrieve the modified section
      LeaseSettingsSection modified = vAppClient.getLeaseSettingsSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkLeaseSettingsSection(modified);

      // Check the date fields
      if (modified.getDeploymentLeaseExpiration() != null) {
         assertTrue(modified.getDeploymentLeaseExpiration().after(newSection.getDeploymentLeaseExpiration()),
               String.format("The new deploymentLeaseExpiration timestamp must be later than the original: %s > %s",
                     dateService.iso8601DateFormat(modified.getDeploymentLeaseExpiration()),
                     dateService.iso8601DateFormat(newSection.getDeploymentLeaseExpiration())));
      }
      if (modified.getStorageLeaseExpiration() != null) {
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
      assertEquals(modified, newSection, String.format(ENTITY_EQUAL, "LeaseSettingsSection"));
   }

   // FIXME "Error: The requested operation on media "com.vmware.vcloud.entity.media:abfcb4b7-809f-4b50-a0aa-8c97bf09a5b0" is not supported in the current state."
   @Test(testName = "PUT /vApp/{id}/media/action/insertMedia", dependsOnMethods = { "testGetVApp" })
   public void testInsertMedia() {
      // Setup media params from configured media id
      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .media(Reference.builder().href(mediaURI).type(MEDIA).build())
            .build();

      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // The method under test
      Task insertMedia = vAppClient.insertMedia(vmURI, params);
      assertTrue(retryTaskSuccess.apply(insertMedia), String.format(TASK_COMPLETE_TIMELY, "insertMedia"));
   }

   @Test(testName = "PUT /vApp/{id}/media/action/ejectMedia", dependsOnMethods = { "testInsertMedia" })
   public void testEjectMedia() {
      // Setup media params from configured media id
      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .media(Reference.builder().href(mediaURI).type(MEDIA).build())
            .build();

      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // The method under test
      Task ejectMedia = vAppClient.ejectMedia(vmURI, params);
      assertTrue(retryTaskSuccess.apply(ejectMedia), String.format(TASK_COMPLETE_TIMELY, "ejectMedia"));
   }

   @Test(testName = "GET /vApp/{id}/networkConfigSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkConfigSection() {
      // The method under test
      NetworkConfigSection section = vAppClient.getNetworkConfigSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkNetworkConfigSection(section);
   }

   @Test(testName = "PUT /vApp/{id}/networkConfigSection", dependsOnMethods = { "testGetNetworkConfigSection" })
   public void testModifyNetworkConfigSection() {
      // Copy existing section and update fields
      NetworkConfigSection oldSection = vAppClient.getNetworkConfigSection(vApp.getHref());
      NetworkConfigSection newSection = oldSection.toBuilder()
//            .info("New NetworkConfigSection Info")
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

   @Test(testName = "GET /vApp/{id}/networkConnectionSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkConnectionSection() {
      getNetworkConnectionSection(new Function<URI, NetworkConnectionSection>() {
         @Override
         public NetworkConnectionSection apply(URI uri) {
            return vAppClient.getNetworkConnectionSection(uri);
         }
      });
   }

   // FIXME "Task error: Unable to perform this action. Contact your cloud administrator."
   @Test(testName = "PUT /vApp/{id}/networkConnectionSection", dependsOnMethods = { "testGetNetworkConnectionSection" })
   public void testModifyNetworkConnectionSection() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // Look up a network in the Vdc
      Set<Reference> networks = vdc.getAvailableNetworks().getNetworks();
      Reference network = Iterables.getLast(networks);

      // Copy existing section and update fields
      NetworkConnectionSection oldSection = vAppClient.getNetworkConnectionSection(vmURI);
      NetworkConnectionSection newSection = oldSection.toBuilder()
            .networkConnection(NetworkConnection.builder()
                  .ipAddressAllocationMode(IpAddressAllocationMode.DHCP.toString())
                  .network(network.getName())
                  .build())
            .build();

      // The method under test
      Task modifyNetworkConnectionSection = vAppClient.modifyNetworkConnectionSection(vmURI, newSection);
      assertTrue(retryTaskSuccess.apply(modifyNetworkConnectionSection), String.format(TASK_COMPLETE_TIMELY, "modifyNetworkConnectionSection"));

      // Retrieve the modified section
      NetworkConnectionSection modified = vAppClient.getNetworkConnectionSection(vmURI);

      // Check the retrieved object is well formed
      checkNetworkConnectionSection(modified);

      // Check the modified section has an extra network connection
      assertEquals(modified.getNetworkConnections().size(), newSection.getNetworkConnections().size() + 1);

      // Check the section was modified correctly
      assertEquals(modified, newSection, String.format(ENTITY_EQUAL, "NetworkConnectionSection"));
   }

   @Test(testName = "GET /vApp/{id}/networkSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkSection() {
      // The method under test
      NetworkSection section = vAppClient.getNetworkSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkNetworkSection(section);
   }

   @Test(testName = "GET /vApp/{id}/operatingSystemSection", dependsOnMethods = { "testGetVApp" })
   public void testGetOperatingSystemSection() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // The method under test
      OperatingSystemSection section = vAppClient.getOperatingSystemSection(vmURI);

      // Check the retrieved object is well formed
      checkOperatingSystemSection(section);
   }

   @Test(testName = "PUT /vApp/{id}/operatingSystemSection", dependsOnMethods = { "testGetOperatingSystemSection" })
   public void testModifyOperatingSystemSection() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // Create new OperatingSystemSection
      OperatingSystemSection newSection = OperatingSystemSection.builder()
            .info("") // NOTE Required OVF field, ignored
            .id(OSType.RHEL_64.getCode())
            .osType("rhel5_64Guest")
            .build();

      // The method under test
      Task modifyOperatingSystemSection = vAppClient.modifyOperatingSystemSection(vmURI, newSection);
      assertTrue(retryTaskSuccess.apply(modifyOperatingSystemSection), String.format(TASK_COMPLETE_TIMELY, "modifyOperatingSystemSection"));

      // Retrieve the modified section
      OperatingSystemSection modified = vAppClient.getOperatingSystemSection(vmURI);

      // Check the retrieved object is well formed
      checkOperatingSystemSection(modified);

      // Check the modified section fields are set correctly
      assertEquals(modified.getId(), newSection.getId());
   }

   @Test(testName = "GET /vApp/{id}/owner", dependsOnMethods = { "testGetVApp" })
   public void testGetOwner() {
      // The method under test
      Owner owner = vAppClient.getOwner(vApp.getHref());

      // Check the retrieved object is well formed
      checkOwner(owner);
   }

   @Test(testName = "PUT /vApp/{id}/owner", dependsOnMethods = { "testGetOwner" })
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

   @Test(testName = "GET /vApp/{id}/productSections", dependsOnMethods = { "testGetVApp" })
   public void testGetProductSections() {
      // The method under test
      ProductSectionList sectionList = vAppClient.getProductSections(vApp.getHref());

      // Check the retrieved object is well formed
      checkProductSectionList(sectionList);
   }

   @Test(testName = "PUT /vApp/{id}/productSections", dependsOnMethods = { "testGetProductSections" })
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

   @Test(testName = "GET /vApp/{id}/question", dependsOnMethods = { "testPowerOnVApp" })
   public void testGetPendingQuestion() {
      // TODO how to test?
      // The method under test
      VmPendingQuestion question = vAppClient.getPendingQuestion(vApp.getHref());

      // Check the retrieved object is well formed
      checkVmPendingQuestion(question);
   }

   @Test(testName = "PUT /vApp/{id}/question/action/answer", dependsOnMethods = { "testGetPendingQuestion" })
   public void testAnswerQuestion() {
      // TODO add builder
      // VmQuestionAnswer answer = VmQuestionAnswer.builer()
      // .build();

      // The method under test
      // vAppClient.answerQuestion(vApp.getHref(), answer);
      // TODO how to test?
   }

   @Test(testName = "GET /vApp/{id}/runtimeInfoSection", dependsOnMethods = { "testGetVApp" })
   public void testGetRuntimeInfoSection() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // The method under test
      RuntimeInfoSection section = vAppClient.getRuntimeInfoSection(vmURI);

      // Check the retrieved object is well formed
      checkRuntimeInfoSection(section);
   }

   @Test(testName = "GET /vApp/{id}/screen", dependsOnMethods = { "testPowerOnVApp" })
   public void testGetScreenImage() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // The method under test
      byte[] image = vAppClient.getScreenImage(vmURI);

      // Check returned bytes against PNG header magic number
      byte[] pngHeaderBytes = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
      assertNotNull(image);
      assertTrue(image.length > pngHeaderBytes.length);
      for (int i = 0; i < pngHeaderBytes.length; i++) {
         assertEquals(image[i], pngHeaderBytes[i], String.format("Image differs from PNG format at byte %d of header", i));
      }
   }

   @Test(testName = "GET /vApp/{id}/screen/action/acquireTicket", dependsOnMethods = { "testGetVApp" })
   public void testGetScreenTicket() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // The method under test
      ScreenTicket ticket = vAppClient.getScreenTicket(vmURI);

      // Check the retrieved object is well formed
      checkScreenTicket(ticket);
   }

   @Test(testName = "GET /vApp/{id}/startupSection", dependsOnMethods = { "testGetVApp" })
   public void testGetStartupSection() {
      // The method under test
      StartupSection section = vAppClient.getStartupSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkStartupSection(section);
   }

   @Test(testName = "PUT /vApp/{id}/startupSection", dependsOnMethods = { "testGetStartupSection" })
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

   @Test(testName = "GET /vApp/{id}/virtualHardwareSection", dependsOnMethods = { "testGetVApp" })
   public void testGetVirtualHardwareSection() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // Method under test
      VirtualHardwareSection hardware = vAppClient.getVirtualHardwareSection(vmURI);

      // Check the retrieved object is well formed
      checkVirtualHardwareSection(hardware);
   }

   @Test(testName = "PUT /vApp/{id}/virtualHardwareSection", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testModifyVirtualHardwareSection() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // Copy existing section and update fields
      VirtualHardwareSection oldSection = vAppClient.getVirtualHardwareSection(vmURI);
      Set<ResourceAllocationSettingData> oldItems = oldSection.getItems();
      ResourceAllocationSettingData oldMemory = Iterables.find(oldItems, new Predicate<ResourceAllocationSettingData>() {
         @Override
         public boolean apply(ResourceAllocationSettingData rasd) {
            return rasd.getResourceType() == ResourceAllocationSettingData.ResourceType.MEMORY;
         }
      });
      ResourceAllocationSettingData newMemory = oldMemory.toBuilder()
            .elementName("1024 MB of memory")
            .virtualQuantity(BigInteger.valueOf(1024L))
            .build();
      Set<ResourceAllocationSettingData> newItems = Sets.newLinkedHashSet(oldItems);
      newItems.remove(oldMemory);
      newItems.add(newMemory);
      VirtualHardwareSection newSection = oldSection.toBuilder()
            .items(newItems)
            .build();

      // The method under test
      Task modifyVirtualHardwareSection = vAppClient.modifyVirtualHardwareSection(vmURI, newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSection), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSection"));

      // Retrieve the modified section
      VirtualHardwareSection modifiedSection = vAppClient.getVirtualHardwareSection(vmURI);

      // Check the retrieved object is well formed
      checkVirtualHardwareSection(modifiedSection);

      // Check the modified section fields are set correctly
      Set<ResourceAllocationSettingData> modifiedItems = modifiedSection.getItems();
      ResourceAllocationSettingData modifiedMemory = Iterables.find(modifiedItems, new Predicate<ResourceAllocationSettingData>() {
         @Override
         public boolean apply(ResourceAllocationSettingData rasd) {
            return rasd.getResourceType() == ResourceAllocationSettingData.ResourceType.MEMORY;
         }
      });
      assertEquals(modifiedMemory.getVirtualQuantity(), BigInteger.valueOf(1024L));
      assertEquals(modifiedMemory, newMemory);
      assertEquals(modifiedSection, newSection);
   }

   @Test(testName = "GET /vApp/{id}/virtualHardwareSection/cpu", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionCpu() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();
      
      // Method under test
      ResourceAllocationSettingData rasd = vAppClient.getVirtualHardwareSectionCpu(vmURI);

      // Check the retrieved object is well formed
      checkResourceAllocationSettingData(rasd);
   }

   @Test(testName = "PUT /vApp/{id}/virtualHardwareSection/cpu", dependsOnMethods = { "testGetVirtualHardwareSectionCpu" })
   public void testModifyVirtualHardwareSectionCpu() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();
      
      // Copy existing section and update fields
      ResourceAllocationSettingData oldItem = vAppClient.getVirtualHardwareSectionCpu(vmURI);
      ResourceAllocationSettingData newItem = oldItem.toBuilder()
            .build();
      
      // Method under test
      Task modifyVirtualHardwareSectionCpu = vAppClient.modifyVirtualHardwareSectionCpu(vmURI, newItem);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionCpu), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionCpu"));

      // Retrieve the modified section
      ResourceAllocationSettingData modified = vAppClient.getVirtualHardwareSectionCpu(vmURI);
      
      // Check the retrieved object
      checkResourceAllocationSettingData(modified);
      
      // TODO What is modifiable? What can we change, so we can assert the change took effect? 
      // I tried changing "weight", but it continued to have the value zero when looked up post-modify.
      //
      // long weight = random.nextInt(Integer.MAX_VALUE);
      // ResourceAllocationSettingData newSection = origSection.toBuilder()
      //         .weight(newCimUnsignedInt(weight))
      //         .build();
      // ...
      // assertEquals(modified.getWeight().getValue(), weight, String.format(OBJ_FIELD_EQ, VAPP, "virtualHardwareSection/cpu/weight", weight, ""+modified.getWeight()));
   }

   @Test(testName = "GET /vApp/{id}/virtualHardwareSection/disks", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionDisks() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();
      
      // Method under test
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionDisks(vmURI);

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(testName = "PUT /vApp/{id}/virtualHardwareSection/disks", dependsOnMethods = { "testGetVirtualHardwareSectionDisks" })
   public void testModifyVirtualHardwareSectionDisks() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();
      
      // Copy the existing items list and modify the name of an item
      RasdItemsList oldSection = vAppClient.getVirtualHardwareSectionDisks(vmURI);
      RasdItemsList newSection = oldSection.toBuilder().build();

      // Method under test
      Task modifyVirtualHardwareSectionDisks = vAppClient.modifyVirtualHardwareSectionDisks(vmURI, newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionDisks), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionDisks"));

      // Retrieve the modified section
      RasdItemsList modified = vAppClient.getVirtualHardwareSectionDisks(vmURI);

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

   @Test(testName = "GET /vApp/{id}/virtualHardwareSection/media", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionMedia() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();
      
      // Method under test
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionMedia(vmURI);

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(testName = "GET /vApp/{id}/virtualHardwareSection/memory", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionMemory() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();
      
      // Method under test
      ResourceAllocationSettingData rasd = vAppClient.getVirtualHardwareSectionCpu(vmURI);

      // Check the retrieved object is well formed
      checkResourceAllocationSettingData(rasd);
   }

   @Test(testName = "PUT /vApp/{id}/virtualHardwareSection/memory", dependsOnMethods = { "testGetVirtualHardwareSectionMemory" })
   public void testModifyVirtualHardwareSectionMemory() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();
      
      ResourceAllocationSettingData origSection = vAppClient.getVirtualHardwareSectionMemory(vmURI);
      ResourceAllocationSettingData newSection = origSection.toBuilder().build();
      
      // Method under test
      Task modifyVirtualHardwareSectionMemory = vAppClient.modifyVirtualHardwareSectionMemory(vmURI, newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionMemory), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionMemory"));

      // Retrieve the modified section
      ResourceAllocationSettingData modified = vAppClient.getVirtualHardwareSectionMemory(vmURI);
      
      // Check the retrieved object
      checkResourceAllocationSettingData(modified);
      
      // TODO What is modifiable? What can we change, so we can assert the change took effect? 
      // I tried changing "weight", but it continued to have the value zero when looked up post-modify.
      // See description under testModifyVirtualHardwareSectionMemoryCpu
   }

   @Test(testName = "GET /vApp/{id}/virtualHardwareSection/networkCards", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionNetworkCards() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();
      
      // Method under test
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionNetworkCards(vmURI);

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(testName = "PUT /vApp/{id}/virtualHardwareSection/networkCards", dependsOnMethods = { "testGetVirtualHardwareSectionNetworkCards" })
   public void testModifyVirtualHardwareSectionNetworkCards() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();
      
      RasdItemsList oldSection = vAppClient.getVirtualHardwareSectionNetworkCards(vmURI);
      RasdItemsList newSection = oldSection.toBuilder().build();

      // Method under test
      Task modifyVirtualHardwareSectionNetworkCards = vAppClient.modifyVirtualHardwareSectionNetworkCards(vmURI, newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionNetworkCards), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionNetworkCards"));

      // Retrieve the modified section
      RasdItemsList modified = vAppClient.getVirtualHardwareSectionNetworkCards(vmURI);

      // Check the retrieved object is well formed
      checkRasdItemsList(modified);
      
      // TODO What is modifiable? What can we change, so we can assert the change took effect? 
      // I tried changing "elementName" of one of the items, but it continued to have the old value when looked up post-modify.
      // See the description in testModifyVirtualHardwareSectionDisks
   }

   @Test(testName = "GET /vApp/{id}/virtualHardwareSection/serialPorts", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionSerialPorts() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();
      
      // Method under test
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionSerialPorts(vmURI);

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(testName = "PUT /vApp/{id}/virtualHardwareSection/serialPorts", dependsOnMethods = { "testGetVirtualHardwareSectionSerialPorts" })
   public void testModifyVirtualHardwareSectionSerialPorts() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();
      
      RasdItemsList oldSection = vAppClient.getVirtualHardwareSectionSerialPorts(vmURI);
      RasdItemsList newSection = oldSection.toBuilder().build();

      // Method under test
      Task modifyVirtualHardwareSectionSerialPorts = vAppClient.modifyVirtualHardwareSectionSerialPorts(vmURI, newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionSerialPorts), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionSerialPorts"));

      // Retrieve the modified section
      RasdItemsList modified = vAppClient.getVirtualHardwareSectionSerialPorts(vmURI);

      // Check the retrieved object is well formed
      checkRasdItemsList(modified);
      
      // TODO What is modifiable? What can we change, so we can assert the change took effect? 
      // I tried changing "elementName" of one of the items, but it continued to have the old value when looked up post-modify.
      // See the description in testModifyVirtualHardwareSectionDisks
   }

   @Test(testName = "GET /vApp/{id}/metadata", dependsOnMethods = { "testGetVApp" })
   public void testGetMetadata() {
      Metadata metadata = vAppClient.getMetadataClient().getMetadata(vApp.getHref());

      // Check the retrieved object is well formed
      checkMetadataFor(VAPP, metadata);
   }

   @Test(testName = "PUT & GET /vApp/{id}/metadata", dependsOnMethods = { "testGetMetadata" })
   public void testSetAndGetMetadataValue() {
      // Store a value
      String key = name("key-");
      String value = name("value-");
      MetadataValue metadataValue = MetadataValue.builder().value(value).build();
      vAppClient.getMetadataClient().setMetadata(vApp.getHref(), key, metadataValue);

      // Retrieve the value, and assert it was set correctly
      MetadataValue newMetadataValue = vAppClient.getMetadataClient().getMetadataValue(vApp.getHref(), key);

      // Check the retrieved object is well formed
      checkMetadataValueFor(VAPP, newMetadataValue, value);
   }

   @Test(testName = "DELETE /vApp/{id}/metadata/{key}", dependsOnMethods = { "testSetAndGetMetadataValue" })
   public void testDeleteMetadataEntry() {
      // Store a value, to be deleted
      String key = name("key-");
      MetadataValue metadataValue = MetadataValue.builder().value("myval").build();
      vAppClient.getMetadataClient().setMetadata(vApp.getHref(), key, metadataValue);

      // Delete the entry
      Task task = vAppClient.getMetadataClient().deleteMetadataEntry(vApp.getHref(), key);
      retryTaskSuccess.apply(task);

      // Confirm the entry has been deleted
      Metadata newMetadata = vAppClient.getMetadataClient().getMetadata(vApp.getHref());

      // Check the retrieved object is well formed
      checkMetadataKeyAbsentFor(VAPP, newMetadata, key);
   }

   @Test(testName = "POST /vApp/{id}/metadata", dependsOnMethods = { "testGetMetadata" })
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
   @Test(testName = "DELETE /vApp/{id}")
   public void testDeleteVApp() {
      // Create a temporary VApp to delete
      VApp temp = instantiateVApp();
      DeployVAppParams params = DeployVAppParams.builder()
            .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS))
            .notForceCustomization()
            .notPowerOn()
            .build();
      Task deployVApp = vAppClient.deploy(temp.getHref(), params);
      assertTrue(retryTaskSuccessLong.apply(deployVApp), String.format(TASK_COMPLETE_TIMELY, "deployVApp"));

      // The method under test
      Task deleteVApp = vAppClient.deleteVApp(temp.getHref());
      assertTrue(retryTaskSuccess.apply(deleteVApp), String.format(TASK_COMPLETE_TIMELY, "deleteVApp"));

      try {
         vAppClient.getVApp(temp.getHref());
         fail("The VApp should have been deleted");
      } catch (VCloudDirectorException vcde) {
         assertEquals(vcde.getError().getMajorErrorCode(), Integer.valueOf(403), "The error code should have been 'Forbidden' (403)");
      }
   }
}
