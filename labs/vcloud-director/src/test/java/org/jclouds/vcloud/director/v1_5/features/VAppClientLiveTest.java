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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.*;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.*;
import static org.testng.Assert.*;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants;
import org.jclouds.vcloud.director.v1_5.domain.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.InstantiateVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.InstantiationParams;
import org.jclouds.vcloud.director.v1_5.domain.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.MediaInsertOrEjectParams;
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
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppNetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.VmPendingQuestion;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswer;
import org.jclouds.vcloud.director.v1_5.domain.ovf.NetworkSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.RASD;
import org.jclouds.vcloud.director.v1_5.domain.ovf.StartupSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.VirtualHardwareSection;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code VAppClient}
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "live", "user", "vapp" }, testName = "VAppClientLiveTest")
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
   protected MetadataClient metadataClient;

   /*
    * Objects shared between tests.
    */

   VApp vApp;

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

   /**
    * @see VAppClient#getVApp(URI)
    */
   @Test(testName = "GET /vApp/{id}")
   public void testGetVApp() {
      VApp vAppInstantiated = instantiateVApp();

      // The method under test
      vApp = vAppClient.getVApp(vAppInstantiated.getHref());

      // Check the retrieved object is well formed
      checkVApp(vApp);

      assertEquals(vApp, vAppInstantiated, String.format(ENTITY_EQUAL, VAPP));
   }

   /**
    * @see VAppClient#modifyVApp(URI, VApp)
    */
   @Test(testName = "PUT /vApp/{id}", dependsOnMethods = { "testGetVApp" })
   public void testModifyVApp() {
      vApp.setName("new-name");
      vApp.setDescription("New Description");

      // The method under test
      Task modifyVApp = vAppClient.modifyVApp(vApp.getHref(), vApp);
      assertTrue(retryTaskSuccess.apply(modifyVApp), String.format(TASK_COMPLETE_TIMELY, "modifyVApp"));

      vApp = vAppClient.getVApp(vApp.getHref());
      assertEquals(vApp.getName(), "new-name", String.format(OBJ_FIELD_EQ, VAPP, "name", "new-name", "modified"));
      assertEquals(vApp.getDescription(), "New Description", String.format(OBJ_FIELD_EQ, VAPP, "description", "New Description", "modified"));
   }

   @Test(testName = "POST /vApp/{id}/action/deploy", dependsOnMethods = { "testGetVApp" })
   public void testDeployVApp() {
      DeployVAppParams params = DeployVAppParams.builder()
            .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS))
            .forceCustomization()
            .notPowerOn()
            .build();

      // The method under test
      Task deployVApp = vAppClient.deploy(vApp.getHref(), params);
      assertTrue(retryTaskSuccess.apply(deployVApp), String.format(TASK_COMPLETE_TIMELY, "deployVApp"));

      vApp = vAppClient.getVApp(vApp.getHref());
      Integer deployedStatus = Status.DEPLOYED.getValue();
      assertEquals(vApp.getStatus(), deployedStatus, String.format(OBJ_FIELD_EQ, VAPP, "status", deployedStatus, "deployed"));
   }

   @Test(testName = "POST /vApp/{id}/power/action/powerOn", dependsOnMethods = { "testDeployVApp" })
   public void testPowerOnVApp() {
      // The method under test
      Task powerOnVApp = vAppClient.powerOn(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(powerOnVApp), String.format(TASK_COMPLETE_TIMELY, "powerOnVApp"));

      vApp = vAppClient.getVApp(vApp.getHref());
      Integer poweredOnStatus = Status.POWERED_ON.getValue();
      assertEquals(vApp.getStatus(), poweredOnStatus, String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOnStatus, "powered on"));
   }

   @Test(testName = "POST /vApp/{id}/power/action/powerOff", dependsOnMethods = { "testPowerOnVApp" })
   public void testPowerOffVApp() {
      // The method under test
      Task powerOffVApp = vAppClient.powerOff(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(powerOffVApp), String.format(TASK_COMPLETE_TIMELY, "powerOffVApp"));

      vApp = vAppClient.getVApp(vApp.getHref());
      Integer poweredOffStatus = Status.POWERED_OFF.getValue();
      assertEquals(vApp.getStatus(), poweredOffStatus, String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOffStatus, "powered off"));
   }

   @Test(enabled = false, testName = "POST /vApp/{id}/action/consolidate", dependsOnMethods = { "testGetVApp" })
   public void testConsolidateVApp(URI vAppURI) {
      // The method under test
      Task consolidateVApp = vAppClient.consolidateVApp(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(consolidateVApp), String.format(TASK_COMPLETE_TIMELY, "consolidateVApp"));
   }

   @Test(enabled = false, testName = "POST /vApp/{id}/action/controlAccess", dependsOnMethods = { "testGetVApp" })
   public void testControlAccess() {
      ControlAccessParams params = ControlAccessParams.builder()
            .sharedToEveryone()
            .build();
      
      // The method under test
      ControlAccessParams modified = vAppClient.controlAccess(vApp.getHref(), params);
      checkControlAccessParams(modified);
      // TODO check individual fields
      assertEquals(modified, params, String.format(ENTITY_EQUAL, "ControlAccessParams"));
   }

   @Test(enabled = false, testName = "POST /vApp/{id}/action/discardSuspendedState", dependsOnMethods = { "testGetVApp" })
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

   @Test(enabled = false, testName = "POST /vApp/{id}/action/installVMwareTools", dependsOnMethods = { "testGetVApp" })
   public void testInstallVMwareTools() {
      // The method under test
      Task installVMwareTools = vAppClient.installVMwareTools(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(installVMwareTools), String.format(TASK_COMPLETE_TIMELY, "installVMwareTools"));
   }

   @Test(enabled = false, testName = "POST /vApp/{id}/action/recomposeVApp", dependsOnMethods = { "testGetVApp" })
   public void testRecomposeVApp() {
      RecomposeVAppParams params = RecomposeVAppParams.builder()
            
            .build();

      // The method under test
      Task recomposeVApp = vAppClient.recomposeVApp(vApp.getHref(), params);
      assertTrue(retryTaskSuccess.apply(recomposeVApp), String.format(TASK_COMPLETE_TIMELY, "recomposeVApp"));
   }

   @Test(enabled = false, testName = "POST /vApp/{id}/action/relocate", dependsOnMethods = { "testGetVApp" })
   public void testRelocate() {
      RelocateParams params = RelocateParams.builder()
            
            .build();

      // The method under test
      Task relocate = vAppClient.relocate(vApp.getHref(), params);
      assertTrue(retryTaskSuccess.apply(relocate), String.format(TASK_COMPLETE_TIMELY, "relocate"));
   }

   @Test(enabled = false, testName = "POST /vApp/{id}/action/undeploy", dependsOnMethods = { "testGetVApp" })
   public void testUndeploy() {
      UndeployVAppParams params = UndeployVAppParams.builder()
            
            .build();

      // The method under test
      Task undeploy = vAppClient.undeploy(vApp.getHref(), params);
      assertTrue(retryTaskSuccess.apply(undeploy), String.format(TASK_COMPLETE_TIMELY, "undeploy"));
   }

   @Test(enabled = false, testName = "POST /vApp/{id}/action/upgradeHardwareVersion", dependsOnMethods = { "testGetVApp" })
   public void testUpgradeHardwareVersion() {
      // The method under test
      Task upgradeHardwareVersion = vAppClient.upgradeHardwareVersion(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(upgradeHardwareVersion), String.format(TASK_COMPLETE_TIMELY, "upgradeHardwareVersion"));
   }

   @Test(enabled = false, testName = "POST /vApp/{id}/power/action/reboot", dependsOnMethods = { "testGetVApp" })
   public void testReboot() {
      // The method under test
      Task powerOffVApp = vAppClient.reboot(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(powerOffVApp), String.format(TASK_COMPLETE_TIMELY, "powerOffVApp"));

      vApp = vAppClient.getVApp(vApp.getHref());
      Integer poweredOnStatus = Status.POWERED_ON.getValue();
      assertEquals(vApp.getStatus(), poweredOnStatus, String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOnStatus, "powered off"));
   }

   @Test(enabled = false, testName = "POST /vApp/{id}/power/action/reset", dependsOnMethods = { "testGetVApp" })
   public void testReset() {
      // The method under test
      Task powerOffVApp = vAppClient.reset(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(powerOffVApp), String.format(TASK_COMPLETE_TIMELY, "powerOffVApp"));

      vApp = vAppClient.getVApp(vApp.getHref());
      Integer poweredOffStatus = Status.POWERED_ON.getValue();
      assertEquals(vApp.getStatus(), poweredOffStatus, String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOffStatus, "powered off"));
   }

   @Test(enabled = false, testName = "POST /vApp/{id}/power/action/shutdown", dependsOnMethods = { "testGetVApp" })
   public void testShutdown() {
      // The method under test
      Task powerOffVApp = vAppClient.shutdown(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(powerOffVApp), String.format(TASK_COMPLETE_TIMELY, "powerOffVApp"));

      vApp = vAppClient.getVApp(vApp.getHref());
      Integer poweredOffStatus = Status.POWERED_OFF.getValue();
      assertEquals(vApp.getStatus(), poweredOffStatus, String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOffStatus, "powered off"));
   }

   @Test(enabled = false, testName = "POST /vApp/{id}/power/action/suspend", dependsOnMethods = { "testGetVApp" })
   public void testSuspend() {
      // The method under test
      Task powerOffVApp = vAppClient.suspend(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(powerOffVApp), String.format(TASK_COMPLETE_TIMELY, "powerOffVApp"));

      vApp = vAppClient.getVApp(vApp.getHref());
      Integer poweredOffStatus = Status.POWERED_OFF.getValue();
      assertEquals(vApp.getStatus(), poweredOffStatus, String.format(OBJ_FIELD_EQ, VAPP, "status", poweredOffStatus, "powered off"));
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/controlAccess", dependsOnMethods = { "testGetVApp" })
   public void testGetControlAccess() {
      // The method under test
      ControlAccessParams controlAccess = vAppClient.getControlAccess(vApp.getHref());
      // checkControlAccessParams(controlAccess);
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/guestCustomizationSection", dependsOnMethods = { "testGetVApp" })
   public void testGetGuestCustomizationSection() {
      // The method under test
      GuestCustomizationSection section = vAppClient.getGuestCustomizationSection(vApp.getHref());
      // checkGuestCustomizationSection(section);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/guestCustomizationSection", dependsOnMethods = { "testGetGuestCustomizationSection" })
   public void testModifyGuestCustomizationSection() {
      GuestCustomizationSection section = vAppClient.getGuestCustomizationSection(vApp.getHref());

      // section.setX()

      // The method under test
      Task modifyGuestCustomizationSection = vAppClient.modifyGuestCustomizationSection(vApp.getHref(), section);
      assertTrue(retryTaskSuccess.apply(modifyGuestCustomizationSection), String.format(TASK_COMPLETE_TIMELY, "modifyGuestCustomizationSection"));

      GuestCustomizationSection modified = vAppClient.getGuestCustomizationSection(vApp.getHref());
      // assertEquals(modified.getX, "");
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/leaseSettingsSection", dependsOnMethods = { "testGetVApp" })
   public void testGetLeaseSettingsSection() {
      // The method under test
      LeaseSettingsSection section = vAppClient.getLeaseSettingsSection(vApp.getHref());
      // checkLeaseSettingsSection(section);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/leaseSettingsSection", dependsOnMethods = { "testGetLeaseSettingsSection" })
   public void testModifyLeaseSettingsSection() {
      LeaseSettingsSection section = vAppClient.getLeaseSettingsSection(vApp.getHref());

      // section.setX()

      // The method under test
      Task modifyLeaseSettingsSection = vAppClient.modifyLeaseSettingsSection(vApp.getHref(), section);
      assertTrue(retryTaskSuccess.apply(modifyLeaseSettingsSection), String.format(TASK_COMPLETE_TIMELY, "modifyLeaseSettingsSection"));

      LeaseSettingsSection modified = vAppClient.getLeaseSettingsSection(vApp.getHref());
      // assertEquals(modified.getX, "");
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/media/action/insertMedia", dependsOnMethods = { "testGetVApp" })
   public void testInsertMedia() {
      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .build();
      
      // The method under test
      Task insertMedia = vAppClient.insertMedia(vApp.getHref(), params);
      assertTrue(retryTaskSuccess.apply(insertMedia), String.format(TASK_COMPLETE_TIMELY, "insertMedia"));
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/media/action/ejectMedia", dependsOnMethods = { "testInsertMedia" })
   public void testEjectMedia() {
      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .build();
      
      // The method under test
      Task ejectMedia = vAppClient.ejectMedia(vApp.getHref(), params);
      assertTrue(retryTaskSuccess.apply(ejectMedia), String.format(TASK_COMPLETE_TIMELY, "ejectMedia"));
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/networkConfigSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkConfigSection() {
      // The method under test
      NetworkConfigSection section = vAppClient.getNetworkConfigSection(vApp.getHref());
      // checkNetworkConfigSection(section);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/networkConfigSection", dependsOnMethods = { "testGetNetworkConfigSection" })
   public void testModifyNetworkConfigSection() {
      NetworkConfigSection section = vAppClient.getNetworkConfigSection(vApp.getHref());

      // section.setX()

      // The method under test
      Task modifyNetworkConfigSection = vAppClient.modifyNetworkConfigSection(vApp.getHref(), section);
      assertTrue(retryTaskSuccess.apply(modifyNetworkConfigSection), String.format(TASK_COMPLETE_TIMELY, "modifyNetworkConfigSection"));

      NetworkConfigSection modified = vAppClient.getNetworkConfigSection(vApp.getHref());
      // assertEquals(modified.getX, "");
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/networkConnectionSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkConnectionSection() {
      // The method under test
      NetworkConnectionSection section = vAppClient.getNetworkConnectionSection(vApp.getHref());
      // checkNetworkConnectionSection(section);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/networkConnectionSection", dependsOnMethods = { "testGetNetworkConnectionSection" })
   public void testModifyNetworkConnectionSection() {
      NetworkConnectionSection section = vAppClient.getNetworkConnectionSection(vApp.getHref());

      // section.setX()

      // The method under test
      Task modifyNetworkConnectionSection = vAppClient.modifyNetworkConnectionSection(vApp.getHref(), section);
      assertTrue(retryTaskSuccess.apply(modifyNetworkConnectionSection), String.format(TASK_COMPLETE_TIMELY, "modifyNetworkConnectionSection"));

      NetworkConnectionSection modified = vAppClient.getNetworkConnectionSection(vApp.getHref());
      // assertEquals(modified.getX, "");
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/networkSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkSection() {
      // The method under test
      NetworkSection section = vAppClient.getNetworkSection(vApp.getHref());
      // checkNetworkSection(section);
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/operatingSystemSection", dependsOnMethods = { "testGetVApp" })
   public void testGetOperatingSystemSection() {
      // The method under test
      OperatingSystemSection section = vAppClient.getOperatingSystemSection(vApp.getHref());
      // checkOperatingSystemSection(section);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/operatingSystemSection", dependsOnMethods = { "testGetOperatingSystemSection" })
   public void testModifyOperatingSystemSection() {
      OperatingSystemSection section = vAppClient.getOperatingSystemSection(vApp.getHref());

      // section.setX()

      // The method under test
      Task modifySection = vAppClient.modifyOperatingSystemSection(vApp.getHref(), section);
      assertTrue(retryTaskSuccess.apply(modifySection), String.format(TASK_COMPLETE_TIMELY, "modifySection"));

      OperatingSystemSection modified = vAppClient.getOperatingSystemSection(vApp.getHref());
      // assertEquals(modified.getX, "");
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/owner", dependsOnMethods = { "testGetVApp" })
   public void testGetOwner() {
      // The method under test
      Owner owner = vAppClient.getOwner(vApp.getHref());
      checkOwner(owner);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/owner", dependsOnMethods = { "testGetOwner" })
   public void testModifyOwner() {
      Owner newOwner = Owner.builder()
            .build();

      // The method under test
      Task modifyOwner = vAppClient.modifyOwner(vApp.getHref(), newOwner);
      assertTrue(retryTaskSuccess.apply(modifyOwner), String.format(TASK_COMPLETE_TIMELY, "modifyOwner"));

      Owner modified = vAppClient.getOwner(vApp.getHref());
      assertEquals(modified, newOwner);
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/productSections", dependsOnMethods = { "testGetVApp" })
   public void testGetProductSections() {
      // The method under test
      ProductSectionList sectionList = vAppClient.getProductSections(vApp.getHref());
      // checkProductSectionList(sectionList);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/productSections", dependsOnMethods = { "testGetProductSections" })
   public void testModifyProductSections() {
      ProductSectionList sectionList = vAppClient.getProductSections(vApp.getHref());

      // sectionList.setX()

      // The method under test
      Task modifyProductSections = vAppClient.modifyProductSections(vApp.getHref(), sectionList);
      assertTrue(retryTaskSuccess.apply(modifyProductSections), String.format(TASK_COMPLETE_TIMELY, "modifyProductSections"));

      ProductSectionList modifiedList = vAppClient.getProductSections(vApp.getHref());
      // assertEquals(modified.getX, "");
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/question", dependsOnMethods = { "testGetVApp" })
   public void testGetPendingQuestion() {
      // TODO how to test?
      // The method under test
      VmPendingQuestion question = vAppClient.getPendingQuestion(vApp.getHref());
//      checkQuestion(question);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/question/action/answer", dependsOnMethods = { "testGetPendingQuestion" })
   public void testAnswerQuestion() {
      // TODO add builder
//      VmQuestionAnswer answer = VmQuestionAnswer.builer()
//            .build();

      // The method under test
//      vAppClient.answerQuestion(vApp.getHref(), answer);
      // TODO how to test?
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/runtimeInfoSection", dependsOnMethods = { "testGetVApp" })
   public void testGetRuntimeInfoSection() {
      // The method under test
      RuntimeInfoSection section = vAppClient.getRuntimeInfoSection(vApp.getHref());
      // checkRuntimeInfoSection(section);
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/screen", dependsOnMethods = { "testGetVApp" })
   public void testGetScreenImage() {
      // The method under test
      byte[] image = vAppClient.getScreenImage(vApp.getHref());
      // TODO how to check?
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/screen/action/acquireTicket", dependsOnMethods = { "testGetVApp" })
   public void testGetScreenTicket() {
      // The method under test
      byte[] image = vAppClient.getScreenImage(vApp.getHref());
      // TODO how to check?

   }

   @Test(enabled = false, testName = "GET /vApp/{id}/startupSection", dependsOnMethods = { "testGetVApp" })
   public void testGetStartupSection() {
      // The method under test
      StartupSection section = vAppClient.getStartupSection(vApp.getHref());
      // checkStartupSection(section);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/startupSection", dependsOnMethods = { "testGetStartupSection" })
   public void testModifyStartupSection() {
      StartupSection section = vAppClient.getStartupSection(vApp.getHref());

      // section.setX()

      // The method under test
      Task modifyStartupSection = vAppClient.modifyStartupSection(vApp.getHref(), section);
      assertTrue(retryTaskSuccess.apply(modifyStartupSection), String.format(TASK_COMPLETE_TIMELY, "modifyStartupSection"));

      StartupSection modified = vAppClient.getStartupSection(vApp.getHref());
      // assertEquals(modified.getX, "");
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/virtualHardwareSection", dependsOnMethods = { "testGetVApp" })
   public void testGetVirtualHardwareSection() {
      // Method under test
      VirtualHardwareSection hardware = vAppClient.getVirtualHardwareSection(vApp.getHref());
      checkVirtualHardwareSection(hardware);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/virtualHardwareSection", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testModifyVirtualHardwareSection() {
      VirtualHardwareSection hardware = vAppClient.getVirtualHardwareSection(vApp.getHref());
      hardware.setInfo("New Info");

      // Method under test
      Task modifyVirtualHardwareSection = vAppClient.modifyVirtualHardwareSection(vApp.getHref(), hardware);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSection), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSection"));

      VirtualHardwareSection modified = vAppClient.getVirtualHardwareSection(vApp.getHref());
      assertEquals(modified.getInfo(), "New Info");
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/virtualHardwareSection/cpu", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionCpu() {
      RASD rasd = vAppClient.getVirtualHardwareSectionCpu(vApp.getHref());
      // checkRASD(rasd);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/virtualHardwareSection/cpu", dependsOnMethods = { "testGetVirtualHardwareSectionCpu" })
   public void testModifyVirtualHardwareSectionCpu() {
      RASD rasd = vAppClient.getVirtualHardwareSectionCpu(vApp.getHref());
      // rasd.setX("New Info");

      // Method under test
      Task modifyVirtualHardwareSectionCpu = vAppClient.modifyVirtualHardwareSectionCpu(vApp.getHref(), rasd);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionCpu), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionCpu"));

      RASD modified = vAppClient.getVirtualHardwareSectionCpu(vApp.getHref());
      // assertEquals(modified.getInfo(), "New Info");

   }

   @Test(enabled = false, testName = "GET /vApp/{id}/virtualHardwareSection/disks", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionDisks() {
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionDisks(vApp.getHref());
      // checkRasdItemsList(rasdItems);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/virtualHardwareSection/disks", dependsOnMethods = { "testGetVirtualHardwareSectionDisks" })
   public void testModifyVirtualHardwareSectionDisks() {
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionDisks(vApp.getHref());
      // rasd.setX("New Info");

      // Method under test
      Task modifyVirtualHardwareSectionDisks = vAppClient.modifyVirtualHardwareSectionDisks(vApp.getHref(), rasdItems);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionDisks), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionDisks"));

      RasdItemsList modified = vAppClient.getVirtualHardwareSectionDisks(vApp.getHref());
      // assertEquals(modified.getInfo(), "New Info");
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/virtualHardwareSection/media", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionMedia() {
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionMedia(vApp.getHref());
      // checkRasdItemsList(rasdItems);
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/virtualHardwareSection/memory", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionMemory() {
      RASD rasd = vAppClient.getVirtualHardwareSectionCpu(vApp.getHref());
      // checkRASD(rasd);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/virtualHardwareSection/memory", dependsOnMethods = { "testGetVirtualHardwareSectionMemory" })
   public void testModifyVirtualHardwareSectionMemory() {
      RASD rasd = vAppClient.getVirtualHardwareSectionMemory(vApp.getHref());
      // rasd.setX("New Info");

      // Method under test
      Task modifyVirtualHardwareSectionMemory = vAppClient.modifyVirtualHardwareSectionMemory(vApp.getHref(), rasd);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionMemory), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionMemory"));

      RASD modified = vAppClient.getVirtualHardwareSectionMemory(vApp.getHref());
      // assertEquals(modified.getInfo(), "New Info");
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/virtualHardwareSection/networkCards", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionNetworkCards() {
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionNetworkCards(vApp.getHref());
      // checkRasdItemsList(rasdItems);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/virtualHardwareSection/networkCards", dependsOnMethods = { "testGetVirtualHardwareSectionNetworkCards" })
   public void testModifyVirtualHardwareSectionNetworkCards() {
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionNetworkCards(vApp.getHref());
      // rasd.setX("New Info");

      // Method under test
      Task modifyVirtualHardwareSectionNetworkCards = vAppClient.modifyVirtualHardwareSectionNetworkCards(vApp.getHref(), rasdItems);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionNetworkCards), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionNetworkCards"));

      RasdItemsList modified = vAppClient.getVirtualHardwareSectionNetworkCards(vApp.getHref());
      // assertEquals(modified.getInfo(), "New Info");
   }

   @Test(enabled = false, testName = "GET /vApp/{id}/virtualHardwareSection/serialPorts", dependsOnMethods = { "testGetVirtualHardwareSection" })
   public void testGetVirtualHardwareSectionSerialPorts() {
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionSerialPorts(vApp.getHref());
      // checkRasdItemsList(rasdItems);
   }

   @Test(enabled = false, testName = "PUT /vApp/{id}/virtualHardwareSection/serialPorts", dependsOnMethods = { "testGetVirtualHardwareSectionSerialPorts" })
   public void testModifyVirtualHardwareSectionSerialPorts() {
      RasdItemsList rasdItems = vAppClient.getVirtualHardwareSectionSerialPorts(vApp.getHref());
      // rasd.setX("New Info");

      // Method under test
      Task modifyVirtualHardwareSectionSerialPorts = vAppClient.modifyVirtualHardwareSectionSerialPorts(vApp.getHref(), rasdItems);
      assertTrue(retryTaskSuccess.apply(modifyVirtualHardwareSectionSerialPorts), String.format(TASK_COMPLETE_TIMELY, "modifyVirtualHardwareSectionSerialPorts"));

      RasdItemsList modified = vAppClient.getVirtualHardwareSectionSerialPorts(vApp.getHref());
      // assertEquals(modified.getInfo(), "New Info");
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

   @AfterClass(groups = { "live" })
   public void cleanUp() {
      if (vApp != null) {
	      Task deleteVApp = vAppClient.deleteVApp(vApp.getHref());
	      retryTaskSuccess.apply(deleteVApp);
      }
   }

   private VApp instantiateVApp() {
      Vdc vdc = vdcClient.getVdc(vdcURI);
      assertNotNull(vdc, String.format(OBJ_REQ_LIVE, VDC));

      VAppTemplate vAppTemplate = vAppTemplateClient.getVAppTemplate(vAppTemplateURI);
      assertNotNull(vAppTemplate, String.format(OBJ_REQ_LIVE, VAPP_TEMPLATE));

      InstantiateVAppTemplateParams instantiate = InstantiateVAppTemplateParams.builder().name("test-vapp").notDeploy().notPowerOn().description("Test VApp").instantiationParams(
            InstantiationParams.builder().sections(
                  ImmutableSet.of(NetworkConfigSection.builder().info("Configuration parameters for logical networks").networkConfigs(
                        ImmutableSet.of(VAppNetworkConfiguration.builder().networkName("vAppNetwork").configuration(
                              NetworkConfiguration.builder().parentNetwork(Iterables.find(vdc.getAvailableNetworks().getNetworks(), new Predicate<Reference>() {
                                 @Override
                                 public boolean apply(Reference reference) {
                                    return reference.getHref().equals(networkURI);
                                 }
                              })).fenceMode("bridged").build()).build())).build())).build()).source(Reference.builder().href(vAppTemplateURI).build()).build();

      VApp vAppInstantiated = vdcClient.instantiateVApp(vdcURI, instantiate);
      assertNotNull(vAppInstantiated, String.format(VCloudDirectorLiveTestConstants.NOT_NULL_OBJECT_FMT, VAPP));
      return vAppInstantiated;
   }
}
