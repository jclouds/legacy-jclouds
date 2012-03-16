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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.ENTITY_NON_NULL;
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
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkRASD;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkRasdItemsList;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkRuntimeInfoSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkScreenTicket;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkStartupSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkVApp;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkVirtualHardwareSection;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.AccessSetting;
import org.jclouds.vcloud.director.v1_5.domain.AccessSettings;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.InstantiateVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.InstantiationParams;
import org.jclouds.vcloud.director.v1_5.domain.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.MediaInsertOrEjectParams;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfiguration;
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
import org.jclouds.vcloud.director.v1_5.domain.VAppNetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.VmPendingQuestion;
import org.jclouds.vcloud.director.v1_5.domain.ovf.NetworkSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.RASD;
import org.jclouds.vcloud.director.v1_5.domain.ovf.StartupSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.VirtualHardwareSection;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecordType;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.jclouds.vcloud.director.v1_5.predicates.ReferenceTypePredicates;
import org.jclouds.xml.internal.JAXBParser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code VAppClient}
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "live", "user", "vapp" }, singleThreaded = true, testName = "VAppClientLiveTest")
public class VAppClientLiveTest extends BaseVCloudDirectorClientLiveTest {

   public static final String VAPP = "vApp";
   public static final String VAPP_TEMPLATE = "vAppTemplate";
   public static final String VDC = "vdc";

   /*
   * Convenience reference to API clients.
   */

   protected CatalogClient catalogClient;
   protected OrgClient orgClient;
   protected VAppClient vAppClient;
   protected VAppTemplateClient vAppTemplateClient;
   protected VdcClient vdcClient;
   protected MetadataClient.Writeable metadataClient;

   /*
   * Objects shared between tests.
   */

   private Vdc vdc;
   private VApp vApp;
   private VAppTemplate vAppTemplate;

   private final Random random = new Random();

   @BeforeClass(inheritGroups = true)
   @Override
   public void setupRequiredClients() {
     catalogClient = context.getApi().getCatalogClient();
     orgClient = context.getApi().getOrgClient();
     vAppClient = context.getApi().getVAppClient();
     vAppTemplateClient = context.getApi().getVAppTemplateClient();
     vdcClient = context.getApi().getVdcClient();
     metadataClient = vAppClient.getMetadataClient();
   }

   @BeforeClass(inheritGroups = true)
   public void setupEnvironment() {
     vdc = vdcClient.getVdc(vdcURI);
     assertNotNull(vdc, String.format(ENTITY_NON_NULL, VDC));

     vAppTemplate = vAppTemplateClient.getVAppTemplate(vAppTemplateURI);
     assertNotNull(vAppTemplate, String.format(ENTITY_NON_NULL, VAPP_TEMPLATE));

     cleanUp();
   }

   /**
   * @see VAppClient#getVApp(URI)
   */
   @Test(testName = "GET /vApp/{id}")
   public void testGetVApp() {
     VApp vAppInstantiated = instantiateVApp();

     // Wait for the task to complete
     Task instantiateTask = Iterables.getOnlyElement(vAppInstantiated.getTasks());
     assertTrue(retryTaskSuccessLong.apply(instantiateTask), String.format(TASK_COMPLETE_TIMELY, "instantiateTask"));

     // The method under test
     vApp = vAppClient.getVApp(vAppInstantiated.getHref());

     // Check the retrieved object is well formed
     checkVApp(vApp);
     debug(vApp);

     // Check the required fields are set
     assertEquals(vApp.isDeployed(), Boolean.FALSE, String.format(OBJ_FIELD_EQ, VAPP, "deployed", "FALSE", vApp.isDeployed().toString()));
     assertEquals(vApp.getName(), "test-vapp", String.format(OBJ_FIELD_EQ, VAPP, "name", "test-vapp", vApp.getName()));
     assertEquals(vApp.getDescription(), "Test VApp", String.format(OBJ_FIELD_EQ, VAPP, "Description", "Test VApp", vApp.getDescription()));

     // TODO instantiationParams instantiationParams()
     // TODO source.href vAppTemplateURI

     // Check status
     Status poweredOffStatus = Status.POWERED_OFF;
     assertEquals(vApp.getStatus(), poweredOffStatus.getValue(), String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOffStatus.toString(), Status.fromValue(vApp.getStatus()).toString()));
   }

   /**
   * @see VAppClient#modifyVApp(URI, VApp)
   */
   @Test(testName = "PUT /vApp/{id}", dependsOnMethods = { "testGetVApp" })
   public void testModifyVApp() {
     VApp newVApp = VApp.builder()
		      .name("new-name")
		      .description("New Description")
		      .build();

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

   @Test(testName = "POST /vApp/{id}/action/enterMaintenanceMode", dependsOnMethods = { "testGetVApp" })
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

   @Test(testName = "POST /vApp/{id}/action/installVMwareTools", dependsOnMethods = { "testGetVApp" })
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
     RecomposeVAppParams params = RecomposeVAppParams.builder()
           .build();

     // The method under test
     Task recomposeVApp = vAppClient.recomposeVApp(vApp.getHref(), params);
     assertTrue(retryTaskSuccess.apply(recomposeVApp), String.format(TASK_COMPLETE_TIMELY, "recomposeVApp"));
   }

   // NOTE This test is disabled, as it is not possible to look up datastores using the User API
   @Test(enabled = false, testName = "POST /vApp/{id}/action/relocate", dependsOnMethods = { "testGetVApp" })
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
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // The method under test
      GuestCustomizationSection section = vAppClient.getGuestCustomizationSection(vmURI);

      // Check the retrieved object is well formed
      checkGuestCustomizationSection(section);
   }

   @Test(testName = "PUT /vApp/{id}/guestCustomizationSection", dependsOnMethods = { "testGetGuestCustomizationSection" })
   public void testModifyGuestCustomizationSection() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // Copy existing section and update fields
      GuestCustomizationSection oldSection = vAppClient.getGuestCustomizationSection(vmURI);
      GuestCustomizationSection newSection = oldSection.toBuilder()
            .computerName("newComputerName")
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
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // The method under test
      NetworkConnectionSection section = vAppClient.getNetworkConnectionSection(vmURI);

      // Check the retrieved object is well formed
      checkNetworkConnectionSection(section);
   }

   @Test(testName = "PUT /vApp/{id}/networkConnectionSection", dependsOnMethods = { "testGetNetworkConnectionSection" })
   public void testModifyNetworkConnectionSection() {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // Copy existing section and update fields
      NetworkConnectionSection oldSection = vAppClient.getNetworkConnectionSection(vmURI);
      NetworkConnectionSection newSection = oldSection.toBuilder()
            .info("Changed NetworkConnectionSection Info")
            .build();

      // The method under test
      Task modifyNetworkConnectionSection = vAppClient.modifyNetworkConnectionSection(vmURI, newSection);
      assertTrue(retryTaskSuccess.apply(modifyNetworkConnectionSection), String.format(TASK_COMPLETE_TIMELY, "modifyNetworkConnectionSection"));

      // Retrieve the modified section
      NetworkConnectionSection modified = vAppClient.getNetworkConnectionSection(vmURI);

      // Check the retrieved object is well formed
      checkNetworkConnectionSection(modified);

      // Check the modified section fields are set correctly
      assertEquals(modified.getInfo(), newSection.getInfo());

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

      // Copy existing section and update fields
      OperatingSystemSection oldSection = vAppClient.getOperatingSystemSection(vmURI);
      OperatingSystemSection newSection = oldSection.toBuilder()
           .description("Changed OperatingSystemSection Description")
           .build();

      // The method under test
      Task modifyOperatingSystemSection = vAppClient.modifyOperatingSystemSection(vmURI, newSection);
      assertTrue(retryTaskSuccess.apply(modifyOperatingSystemSection), String.format(TASK_COMPLETE_TIMELY, "modifyOperatingSystemSection"));

      // Retrieve the modified section
      OperatingSystemSection modified = vAppClient.getOperatingSystemSection(vmURI);

      // Check the retrieved object is well formed
      checkOperatingSystemSection(modified);

      // Check the modified section fields are set correctly
      assertEquals(modified.getInfo(), newSection.getInfo());
      assertEquals(modified.getDescription(), newSection.getDescription());

      // Check the section was modified correctly
      assertEquals(modified, newSection, String.format(ENTITY_EQUAL, "OperatingSystemSection"));
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
      ProductSectionList sectionList = vAppClient.getProductSections(vApp.getHref());

      // sectionList.setX()

      // The method under test
      Task modifyProductSections = vAppClient.modifyProductSections(vApp.getHref(), sectionList);
      assertTrue(retryTaskSuccess.apply(modifyProductSections), String.format(TASK_COMPLETE_TIMELY, "modifyProductSections"));

      ProductSectionList modifiedList = vAppClient.getProductSections(vApp.getHref());
      // assertEquals(modified.getX, "");
   }

   @Test(testName = "GET /vApp/{id}/question", dependsOnMethods = { "testGetVApp" })
   public void testGetPendingQuestion() {
      // TODO how to test?
      // The method under test
      VmPendingQuestion question = vAppClient.getPendingQuestion(vApp.getHref());

      // Check the retrieved object is well formed
      // checkQuestion(question);
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

   @Test(testName = "GET /vApp/{id}/screen", dependsOnMethods = { "testGetVApp" })
   public void testGetScreenImage() {
      // The method under test
      byte[] image = vAppClient.getScreenImage(vApp.getHref());

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
      // The method under test
      ScreenTicket ticket = vAppClient.getScreenTicket(vApp.getHref());

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
      VirtualHardwareSection newSection = oldSection.toBuilder().build();

      // The method under test
      Task modifyVirtualHardwareSection = vAppClient.modifyVirtualHardwareSection(vmURI, newSection);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSection), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSection"));

      // Retrieve the modified section
      VirtualHardwareSection modified = vAppClient.getVirtualHardwareSection(vmURI);

      // Check the retrieved object is well formed
      checkVirtualHardwareSection(modified);

      // Check the modified section fields are set correctly
      // assertEquals(modified.getX(), "");
      assertEquals(modified, newSection);
   }

   @Test(testName = "GET /vApp/{id}/virtualHardwareSection/cpu", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionCpu() {
      RASD rasd = vAppClient.getVirtualHardwareSectionCpu(vApp.getHref());
      checkRASD(rasd);
   }

   @Test(testName = "PUT /vApp/{id}/virtualHardwareSection/cpu", dependsOnMethods = { "testGetVirtualHardwareSectionCpu" })
   public void testModifyVirtualHardwareSectionCpu() {
      RASD rasd = vAppClient.getVirtualHardwareSectionCpu(vApp.getHref());
      // rasd.setX("New Info");

      // Method under test
      Task modifyVirtualHardwareSectionCpu = vAppClient.modifyVirtualHardwareSectionCpu(vApp.getHref(), rasd);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionCpu), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionCpu"));

      // Retrieve the modified section
      RASD modified = vAppClient.getVirtualHardwareSectionCpu(vApp.getHref());

      // Check the retrieved object is well formed
      checkRASD(modified);

      // assertEquals(modified.getInfo(), "New Info");
   }

   @Test(testName = "GET /vApp/{id}/virtualHardwareSection/disks", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionDisks() {
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionDisks(vApp.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(testName = "PUT /vApp/{id}/virtualHardwareSection/disks", dependsOnMethods = { "testGetVirtualHardwareSectionDisks" })
   public void testModifyVirtualHardwareSectionDisks() {
      // Copy the existing items list and add a new item
      RasdItemsList oldRasdItems = vAppClient.getVirtualHardwareSectionDisks(vApp.getHref());
      RASD item = RASD.builder().build();
      RasdItemsList newRasdItems = oldRasdItems.toBuilder().item(item).build();

      // Method under test
      Task modifyVirtualHardwareSectionDisks = vAppClient.modifyVirtualHardwareSectionDisks(vApp.getHref(), newRasdItems);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionDisks), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionDisks"));

      // Retrieve the modified section
      RasdItemsList modified = vAppClient.getVirtualHardwareSectionDisks(vApp.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(modified);

      // Check the modified items list
      // assertEquals(modified.getX(), "");
      assertTrue(modified.getItems().contains(item));
      assertEquals(modified, newRasdItems);
   }

   @Test(testName = "GET /vApp/{id}/virtualHardwareSection/media", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionMedia() {
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionMedia(vApp.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(testName = "GET /vApp/{id}/virtualHardwareSection/memory", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionMemory() {
      RASD rasd = vAppClient.getVirtualHardwareSectionCpu(vApp.getHref());

      // Check the retrieved object is well formed
      checkRASD(rasd);
   }

   @Test(testName = "PUT /vApp/{id}/virtualHardwareSection/memory", dependsOnMethods = { "testGetVirtualHardwareSectionMemory" })
   public void testModifyVirtualHardwareSectionMemory() {
      RASD rasd = vAppClient.getVirtualHardwareSectionMemory(vApp.getHref());
      // rasd.setX("New Info");

      // Method under test
      Task modifyVirtualHardwareSectionMemory = vAppClient.modifyVirtualHardwareSectionMemory(vApp.getHref(), rasd);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionMemory), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionMemory"));

      RASD modified = vAppClient.getVirtualHardwareSectionMemory(vApp.getHref());
      // assertEquals(modified.getInfo(), "New Info");
   }

   @Test(testName = "GET /vApp/{id}/virtualHardwareSection/networkCards", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionNetworkCards() {
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionNetworkCards(vApp.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(testName = "PUT /vApp/{id}/virtualHardwareSection/networkCards", dependsOnMethods = { "testGetVirtualHardwareSectionNetworkCards" })
   public void testModifyVirtualHardwareSectionNetworkCards() {
      // Copy the existing items list and add a new item
      RasdItemsList oldRasdItems = vAppClient.getVirtualHardwareSectionNetworkCards(vApp.getHref());
      RASD item = RASD.builder().build();
      RasdItemsList newRasdItems = oldRasdItems.toBuilder().item(item).build();

      // Method under test
      Task modifyVirtualHardwareSectionNetworkCards = vAppClient.modifyVirtualHardwareSectionNetworkCards(vApp.getHref(), newRasdItems);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionNetworkCards), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionNetworkCards"));

      // Retrieve the modified section
      RasdItemsList modified = vAppClient.getVirtualHardwareSectionNetworkCards(vApp.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(modified);

      // Check the modified items list
      // assertEquals(modified.getX(), "");
      assertTrue(modified.getItems().contains(item));
      assertEquals(modified, newRasdItems);
   }

   @Test(testName = "GET /vApp/{id}/virtualHardwareSection/serialPorts", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionSerialPorts() {
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionSerialPorts(vApp.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(rasdItems);
   }

   @Test(testName = "PUT /vApp/{id}/virtualHardwareSection/serialPorts", dependsOnMethods = { "testGetVirtualHardwareSectionSerialPorts" })
   public void testModifyVirtualHardwareSectionSerialPorts() {
      // Copy the existing items list and add a new item
      RasdItemsList oldRasdItems = vAppClient.getVirtualHardwareSectionSerialPorts(vApp.getHref());
      RASD item = RASD.builder().build();
      RasdItemsList newRasdItems = oldRasdItems.toBuilder().item(item).build();

      // Method under test
      Task modifyVirtualHardwareSectionSerialPorts = vAppClient.modifyVirtualHardwareSectionSerialPorts(vApp.getHref(), newRasdItems);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionSerialPorts), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionSerialPorts"));

      // Retrieve the modified section
      RasdItemsList modified = vAppClient.getVirtualHardwareSectionSerialPorts(vApp.getHref());

      // Check the retrieved object is well formed
      checkRasdItemsList(modified);

      // Check the modified items list
      // assertEquals(modified.getX(), "");
      assertTrue(modified.getItems().contains(item));
      assertEquals(modified, newRasdItems);
   }

   @Test(testName = "GET /vApp/{id}/metadata", dependsOnMethods = { "testGetVApp" })
   public void testGetMetadata() {
      Metadata metadata = metadataClient.getMetadata(vApp.getHref());

      // Check the retrieved object is well formed
      checkMetadataFor(VAPP, metadata);
   }

   @Test(testName = "PUT & GET /vApp/{id}/metadata", dependsOnMethods = { "testGetMetadata" })
   public void testSetAndGetMetadataValue() {
      // Store a value
      String key = Integer.toString(random.nextInt());
      String value = Integer.toString(random.nextInt());
      MetadataValue metadataValue = MetadataValue.builder().value(value).build();
      metadataClient.setMetadata(vApp.getHref(), key, metadataValue);

      // Retrieve the value, and assert it was set correctly
      MetadataValue newMetadataValue = metadataClient.getMetadataValue(vApp.getHref(), key);

      // Check the retrieved object is well formed
      checkMetadataValueFor(VAPP, newMetadataValue, value);
   }

   @Test(testName = "DELETE /vApp/{id}/metadata/{key}", dependsOnMethods = { "testSetAndGetMetadataValue" })
   public void testDeleteMetadataEntry() {
      // Store a value, to be deleted
      String key = Integer.toString(random.nextInt());
      MetadataValue metadataValue = MetadataValue.builder().value("myval").build();
      metadataClient.setMetadata(vApp.getHref(), key, metadataValue);

      // Delete the entry
      Task task = metadataClient.deleteMetadataEntry(vApp.getHref(), key);
      retryTaskSuccess.apply(task);

      // Confirm the entry has been deleted
      Metadata newMetadata = metadataClient.getMetadata(vApp.getHref());

      // Check the retrieved object is well formed
      checkMetadataKeyAbsentFor(VAPP, newMetadata, key);
   }

   @Test(testName = "POST /vApp/{id}/metadata", dependsOnMethods = { "testGetMetadata" })
   public void testMergeMetadata() {
      Metadata oldMetadata = metadataClient.getMetadata(vApp.getHref());
      Map<String, String> oldMetadataMap = Checks.metadataToMap(oldMetadata);

      // Store a value, to be deleted
      String key = Integer.toString(random.nextInt());
      String value = Integer.toString(random.nextInt());
      Metadata addedMetadata = Metadata.builder()
            .entry(MetadataEntry.builder().key(key).value(value).build())
            .build();
      Task task = metadataClient.mergeMetadata(vApp.getHref(), addedMetadata);
      retryTaskSuccess.apply(task);

      // Confirm the entry contains everything that was there, and everything that was being added
      Metadata newMetadata = metadataClient.getMetadata(vApp.getHref());
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
   @Test(testName = "DELETE /vApp/{id}", dependsOnMethods = { "testPowerOffVApp" })
   public void testDeleteVApp() {
      // The method under test
      Task deleteVApp = vAppClient.deleteVApp(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(deleteVApp), String.format(TASK_COMPLETE_TIMELY, "deleteVApp"));

      try {
         vApp = vAppClient.getVApp(vApp.getHref());
         fail("The VApp should have been deleted");
      } catch (VCloudDirectorException vcde) {
         assertEquals(vcde.getError().getMajorErrorCode(), Integer.valueOf(403), "The error code should have been 'Forbidden' (403)");
         vApp = null;
      }
   }

   // NOTE This method is also called by the BeforeClass method setupRequiredClients
   @AfterClass(alwaysRun = true, description = "Clean up the environment by deleting created VApps named 'test-vapp' or 'new-name'")
   public void cleanUp() {
      // Find references in the Vdc with the VApp type and named 'test-vapp' or 'new-name'
      Iterable<Reference> vApps = Iterables.filter(
            vdc.getResourceEntities().getResourceEntities(),
            Predicates.and(
                  ReferenceTypePredicates.<Reference> typeEquals(VCloudDirectorMediaType.VAPP),
                  Predicates.or(
                        ReferenceTypePredicates.<Reference> nameEquals("test-vapp"),
                        ReferenceTypePredicates.<Reference> nameEquals("new-name")
		                  )
		            )
		      );

      // If we found any references, delete the VApp they point to
      if (vApps != null && !Iterables.isEmpty(vApps)) {
         for (Reference each : vApps) {
            VApp found = vAppClient.getVApp(each.getHref());
            // debug(found);

            // Shutdown and power off the VApp if necessary
            if (found.getStatus().equals(Status.POWERED_ON.getValue())) {
               Task shutdownTask = vAppClient.shutdown(found.getHref());
               retryTaskSuccess.apply(shutdownTask);
            }

            // Undeploy the VApp if necessary
            if (found.isDeployed()) {
               UndeployVAppParams params = UndeployVAppParams.builder().build();
               Task undeployTask = vAppClient.undeploy(found.getHref(), params);
               retryTaskSuccess.apply(undeployTask);
            }

            // Delete the VApp
            Task deleteTask = vAppClient.deleteVApp(found.getHref());
            retryTaskSuccess.apply(deleteTask);
         }
      }
   }

   /**
    * Instantiate a {@link VApp} in a {@link Vdc} using the {@link VAppTemplate} we have configured for the tests.
    * 
    * @return the VApp that is being instantiated
    */
   private VApp instantiateVApp() {
      InstantiateVAppTemplateParams instantiate = InstantiateVAppTemplateParams.builder()
            .name("test-vapp")
            .notDeploy()
            .notPowerOn()
            .description("Test VApp")
            .instantiationParams(instantiationParams())
            .source(Reference.builder().href(vAppTemplateURI).build())
            .build();

      VApp vAppInstantiated = vdcClient.instantiateVApp(vdcURI, instantiate);
      assertNotNull(vAppInstantiated, String.format(ENTITY_NON_NULL, VAPP));

      return vAppInstantiated;
   }

   /** Build an {@link InstantiationParams} object. */
   private InstantiationParams instantiationParams() {
      InstantiationParams instantiationParams = InstantiationParams.builder()
            .sections(ImmutableSet.of(networkConfigSection()))
            .build();

      return instantiationParams;
   }

   /** Build a {@link NetworkConfigSection} object. */
   private NetworkConfigSection networkConfigSection() {
      NetworkConfigSection networkConfigSection = NetworkConfigSection.builder()
            .info("Configuration parameters for logical networks")
            .networkConfigs(
		            ImmutableSet.of(
		                  VAppNetworkConfiguration.builder()
		                        .networkName("vAppNetwork")
		                        .configuration(networkConfiguration())
		                        .build()))
		      .build();

      return networkConfigSection;
   }

   /** Build a {@link NetworkConfiguration} object. */
   private NetworkConfiguration networkConfiguration() {
      Set<Reference> networks = vdc.getAvailableNetworks().getNetworks();

      // Look up the network in the Vdc with the id configured for the tests
      Optional<Reference> parentNetwork = Iterables.tryFind(networks, new Predicate<Reference>() {
         @Override
         public boolean apply(Reference reference) {
            return reference.getHref().equals(networkURI);
         }
      });

      // Check we actually found a network reference
      if (!parentNetwork.isPresent()) {
         fail(String.format("Could not find network %s in vdc", networkURI.toASCIIString()));
      }

      // Build the configuration object
      NetworkConfiguration networkConfiguration = NetworkConfiguration.builder()
            .parentNetwork(parentNetwork.get())
            .fenceMode("bridged")
            .build();

      return networkConfiguration;
   }

   /**
    * Marshals a JAXB annotated object into XML. The XML is output on {@link System#err}.
    */
   private void debug(Object object) {
      JAXBParser parser = new JAXBParser();
      try {
         String xml = parser.toXML(object);

         System.err.println(Strings.repeat("-", 80));
         System.err.println(xml);
         System.err.println(Strings.repeat("-", 80));
      } catch (IOException ioe) {
         Throwables.propagate(ioe);
      }
   }
}
