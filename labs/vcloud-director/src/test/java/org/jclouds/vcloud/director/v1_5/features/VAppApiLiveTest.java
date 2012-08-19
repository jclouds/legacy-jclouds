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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.CONDITION_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.CORRECT_VALUE_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.ENTITY_EQUAL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.MATCHES_STRING_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.NOT_EMPTY_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ADMIN_USER;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkControlAccessParams;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkLeaseSettingsSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadata;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataKeyAbsentFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataValue;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataValueFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkNetworkConfigSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkNetworkSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkOwner;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkProductSectionList;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkStartupSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkVApp;
import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.relEquals;
import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.typeEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.dmtf.ovf.MsgType;
import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.dmtf.ovf.ProductSection;
import org.jclouds.dmtf.ovf.StartupSection;
import org.jclouds.io.Payloads;
import org.jclouds.vcloud.director.v1_5.AbstractVAppApiLiveTest;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.AccessSetting;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkAssignment;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConnection;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConnection.IpAddressAllocationMode;
import org.jclouds.vcloud.director.v1_5.domain.network.VAppNetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.params.ComposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.InstantiationParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RecomposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.SourcedCompositionItemParam;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecordType;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.CharMatcher;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Tests behavior of the {@link VAppApi}.
 *
 * @author grkvlt@apache.org
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "VAppApiLiveTest")
public class VAppApiLiveTest extends AbstractVAppApiLiveTest {

   private MetadataValue metadataValue;
   private String key;
   private URI testUserURI;
   private boolean mediaCreated = false;
   private boolean testUserCreated = false;
   
   @BeforeClass(alwaysRun = true)
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
                  .description("Test media generated by VAppApiLiveTest")
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
            logger.warn(e, "Error when deleting media");
         }
      }
      if (adminContext != null && testUserCreated && testUserURI != null) {
         try {
            adminContext.getApi().getUserApi().deleteUser(testUserURI);
         } catch (Exception e) {
            logger.warn(e, "Error when deleting user");
         }
      }
   }

   /**
    * @see VAppApi#getVApp(URI)
    */
   @Test(description = "GET /vApp/{id}")
   public void testGetVApp() {
      // The method under test
      vApp = vAppApi.getVApp(vAppURI);

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
   
   @Test(description = "POST /vApp/{id}/action/recomposeVApp")
   public void testRecomposeVApp() {
      Set<Vm> vms = getAvailableVMsFromVAppTemplates();
  
      VApp composedVApp = vdcApi.composeVApp(vdcURI, ComposeVAppParams.builder()
            .name(name("composed-"))
            .instantiationParams(instantiationParams())
            .build());
      
      // get the first vm to be added to vApp
      Vm toAddVm = Iterables.get(vms, 0);
      RecomposeVAppParams params = createRecomposeParams(composedVApp, toAddVm); 
      
      // The method under test
      Task recomposeVApp = vAppApi.recompose(composedVApp.getHref(), params);
      assertTaskSucceedsLong(recomposeVApp);
      
      // add another vm instance to vApp
      params = createRecomposeParams(composedVApp, toAddVm); 
      recomposeVApp = vAppApi.recompose(composedVApp.getHref(), params);
      assertTaskSucceedsLong(recomposeVApp);
      
      // delete a vm
      VApp configured = vAppApi.getVApp(composedVApp.getHref());
      List<Vm> vmsToBeDeleted = configured.getChildren().getVms();
      Vm toBeDeleted = Iterables.get(vmsToBeDeleted, 0);
      Task deleteVm = vmApi.deleteVm(toBeDeleted.getHref());
      assertTaskSucceedsLong(deleteVm);
      
      Task deleteVApp = vAppApi.deleteVApp(composedVApp.getHref());
      assertTaskSucceedsLong(deleteVApp);
   }

   private Set<Vm> getAvailableVMsFromVAppTemplates() {
      Set<Vm> vms = Sets.newLinkedHashSet();
      QueryResultRecords templatesRecords = queryApi.vAppTemplatesQueryAll();
      for (QueryResultRecordType templateRecord : templatesRecords.getRecords()) {
         VAppTemplate vAppTemplate = vAppTemplateApi.getVAppTemplate(templateRecord.getHref());
         vms.addAll(vAppTemplate.getChildren());
      }
      return ImmutableSet.copyOf(Iterables.filter(vms, new Predicate<Vm>() {
         // filter out vms in the vApp template with computer name that contains underscores, dots, or both.
         @Override
         public boolean apply(Vm input) {
            GuestCustomizationSection guestCustomizationSection = vmApi.getGuestCustomizationSection(input.getHref());
            String computerName = guestCustomizationSection.getComputerName();
            String retainComputerName = CharMatcher.inRange('0', '9')
                     .or(CharMatcher.inRange('a', 'z'))
                     .or(CharMatcher.inRange('A', 'Z'))
                     .or(CharMatcher.is('-'))
                     .retainFrom(computerName);
            return computerName.equals(retainComputerName);
         }
      }));
   }
   
   /**
    * @see VAppApi#modifyVApp(URI, VApp)
    */
   @Test(description = "PUT /vApp/{id}", dependsOnMethods = { "testGetVApp" })
   public void testModifyVApp() {
      VApp newVApp = VApp.builder()
            .name(name("new-name-"))
            .description("New Description")
            .build();
      vAppNames.add(newVApp.getName());

      // The method under test
      Task modifyVApp = vAppApi.modifyVApp(vApp.getHref(), newVApp);
      assertTrue(retryTaskSuccess.apply(modifyVApp), String.format(TASK_COMPLETE_TIMELY, "modifyVApp"));

      // Get the updated VApp
      vApp = vAppApi.getVApp(vApp.getHref());

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
      Task deployVApp = vAppApi.deploy(vApp.getHref(), params);
      assertTrue(retryTaskSuccessLong.apply(deployVApp), String.format(TASK_COMPLETE_TIMELY, "deployVApp"));

      // Get the updated VApp
      vApp = vAppApi.getVApp(vApp.getHref());

      // Check the required fields are set
      assertTrue(vApp.isDeployed(), String.format(OBJ_FIELD_EQ, VAPP, "deployed", "TRUE", vApp.isDeployed().toString()));

      // Check status
      assertVAppStatus(vAppURI, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/power/action/powerOn", dependsOnMethods = { "testDeployVApp" })
   public void testPowerOnVApp() {
      // Power off VApp
      vApp = powerOffVApp(vApp.getHref());

      // The method under test
      Task powerOnVApp = vAppApi.powerOn(vApp.getHref());
      assertTaskSucceedsLong(powerOnVApp);

      // Get the updated VApp
      vApp = vAppApi.getVApp(vApp.getHref());

      // Check status
      assertVAppStatus(vAppURI, Status.POWERED_ON);
   }

   @Test(description = "POST /vApp/{id}/power/action/reboot", dependsOnMethods = { "testDeployVApp" })
   public void testReboot() {
      // Power on VApp
      vApp = powerOnVApp(vApp.getHref());
 
      // The method under test
      Task reboot = vAppApi.reboot(vApp.getHref());
      assertTaskSucceedsLong(reboot);

      // Get the updated VApp
      vApp = vAppApi.getVApp(vApp.getHref());

      // Check status
      assertVAppStatus(vAppURI, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/power/action/shutdown", dependsOnMethods = { "testDeployVApp" })
   public void testShutdown() {
      // Power on VApp
      vApp = powerOnVApp(vApp.getHref());

      // The method under test
      Task shutdown = vAppApi.shutdown(vApp.getHref());
      assertTaskSucceedsLong(shutdown);

      // Get the updated VApp
      vApp = vAppApi.getVApp(vApp.getHref());

      // Check status
      assertVAppStatus(vApp.getHref(), Status.POWERED_OFF);

      // Power on the VApp again
      vApp = powerOnVApp(vApp.getHref());
   }

   @Test(description = "POST /vApp/{id}/power/action/suspend", dependsOnMethods = { "testDeployVApp" })
   public void testSuspend() {
      // Power on VApp
      vApp = powerOnVApp(vApp.getHref());

      // The method under test
      Task suspend = vAppApi.suspend(vAppURI);
      assertTaskSucceedsLong(suspend);

      // Get the updated VApp
      vApp = vAppApi.getVApp(vApp.getHref());

      // Check status
      assertVAppStatus(vAppURI, Status.SUSPENDED);

      // Power on the VApp again
      vApp = powerOnVApp(vApp.getHref());
   }

   @Test(description = "POST /vApp/{id}/power/action/reset", dependsOnMethods = { "testDeployVApp" })
   public void testReset() {
      // Power on VApp
      vApp = powerOnVApp(vApp.getHref());

      // The method under test
      Task reset = vAppApi.reset(vAppURI);
      assertTaskSucceedsLong(reset);

      // Get the updated VApp
      vApp = vAppApi.getVApp(vAppURI);

      // Check status
      assertVAppStatus(vAppURI, Status.POWERED_ON);
   }

   @Test(description = "POST /vApp/{id}/action/undeploy", dependsOnMethods = { "testDeployVApp" })
   public void testUndeployVApp() {
      // Power on VApp
      vApp = powerOnVApp(vApp.getHref());

      UndeployVAppParams params = UndeployVAppParams.builder().build();

      // The method under test
      Task undeploy = vAppApi.undeploy(vApp.getHref(), params);
      assertTrue(retryTaskSuccess.apply(undeploy), String.format(TASK_COMPLETE_TIMELY, "undeploy"));

      // Get the updated VApp
      vApp = vAppApi.getVApp(vAppURI);

      // Check status
      assertFalse(vApp.isDeployed(), String.format(OBJ_FIELD_EQ, VAPP, "deployed", "FALSE", vApp.isDeployed().toString()));
      assertVAppStatus(vAppURI, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/power/action/powerOff", dependsOnMethods = { "testUndeployVApp" })
   public void testPowerOffVApp() {
      // Power on VApp
      vApp = powerOnVApp(vApp.getHref());
      
      // The method under test
      Task powerOffVApp = vAppApi.powerOff(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(powerOffVApp), String.format(TASK_COMPLETE_TIMELY, "powerOffVApp"));

      // Get the updated VApp
      vApp = vAppApi.getVApp(vAppURI);

      // Check status
      assertVAppStatus(vAppURI, Status.POWERED_OFF);
   }

   @Test(description = "POST /vApp/{id}/action/controlAccess", dependsOnMethods = { "testGetVApp" })
   public void testControlAccessUser() {
      ControlAccessParams params = ControlAccessParams.builder()
            .notSharedToEveryone()
            .accessSetting(AccessSetting.builder()
                  .subject(Reference.builder().href(testUserURI).type(ADMIN_USER).build())
                  .accessLevel("ReadOnly")
                  .build())
            .build();

      // The method under test
      ControlAccessParams modified = vAppApi.modifyControlAccess(vApp.getHref(), params);

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
      ControlAccessParams modified = vAppApi.modifyControlAccess(vApp.getHref(), params);

      // Check the retrieved object is well formed
      checkControlAccessParams(modified);

      // Check entities are equal
      assertEquals(modified, params, String.format(ENTITY_EQUAL, "ControlAccessParams"));
   }

   @Test(description = "POST /vApp/{id}/action/discardSuspendedState", dependsOnMethods = { "testDeployVApp" })
   public void testDiscardSuspendedState() {
      // Power on, then suspend the VApp
      vApp = powerOnVApp(vAppURI);
      vApp = suspendVApp(vAppURI);
      
      // The method under test
      Task discardSuspendedState = vAppApi.discardSuspendedState(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(discardSuspendedState), String.format(TASK_COMPLETE_TIMELY, "discardSuspendedState"));
   }

   @Test(description = "POST /vApp/{id}/action/enterMaintenanceMode", groups = {"systemAdmin"})
   public void testEnterMaintenanceMode() {

      // Do this to a new vApp, so don't mess up subsequent tests by making the vApp read-only
      VApp temp = instantiateVApp();
      DeployVAppParams params = DeployVAppParams.builder()
            .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS))
            .notForceCustomization()
            .notPowerOn()
            .build();
      Task deployVApp = vAppApi.deploy(temp.getHref(), params);
      assertTaskSucceedsLong(deployVApp);
      
      try {
         // Method under test
         vAppApi.enterMaintenanceMode(temp.getHref());
   
         temp = vAppApi.getVApp(temp.getHref());
         assertTrue(temp.isInMaintenanceMode(), String.format(CONDITION_FMT, "InMaintenanceMode", "TRUE", temp.isInMaintenanceMode()));

         // Exit maintenance mode
         vAppApi.exitMaintenanceMode(temp.getHref());
      } finally {
         cleanUpVApp(temp);
      }
   }

   @Test(description = "POST /vApp/{id}/action/exitMaintenanceMode", dependsOnMethods = { "testEnterMaintenanceMode" }, groups = {"systemAdmin"})
   public void testExitMaintenanceMode() {
      // Do this to a new vApp, so don't mess up subsequent tests by making the vApp read-only
      VApp temp = instantiateVApp();
      DeployVAppParams params = DeployVAppParams.builder()
            .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS))
            .notForceCustomization()
            .notPowerOn()
            .build();
      Task deployVApp = vAppApi.deploy(temp.getHref(), params);
      assertTaskSucceedsLong(deployVApp);
      
      try {
         // Enter maintenance mode
         vAppApi.enterMaintenanceMode(temp.getHref());
   
         // Method under test
         vAppApi.exitMaintenanceMode(temp.getHref());

         temp = vAppApi.getVApp(temp.getHref());
         assertFalse(temp.isInMaintenanceMode(), String.format(CONDITION_FMT, "InMaintenanceMode", "FALSE", temp.isInMaintenanceMode()));
      } finally {
         cleanUpVApp(temp);
      }
   }

   @Test(description = "GET /vApp/{id}/controlAccess", dependsOnMethods = { "testGetVApp" })
   public void testGetControlAccess() {
      // The method under test
      ControlAccessParams controlAccess = vAppApi.getControlAccess(vApp.getHref());

      // Check the retrieved object is well formed
      checkControlAccessParams(controlAccess);
   }

   @Test(description = "GET /vApp/{id}/leaseSettingsSection", dependsOnMethods = { "testGetVApp" })
   public void testGetLeaseSettingsSection() {
      // The method under test
      LeaseSettingsSection section = vAppApi.getLeaseSettingsSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkLeaseSettingsSection(section);
   }

   @Test(description = "PUT /vApp/{id}/leaseSettingsSection", dependsOnMethods = { "testGetLeaseSettingsSection" })
   public void testModifyLeaseSettingsSection() {
      // Copy existing section
      LeaseSettingsSection oldSection = vAppApi.getLeaseSettingsSection(vApp.getHref());
      Integer twoHours = (int) TimeUnit.SECONDS.convert(2L, TimeUnit.HOURS);
      LeaseSettingsSection newSection = oldSection.toBuilder()
            .deploymentLeaseInSeconds(twoHours)
            .build();

      // The method under test
      Task modifyLeaseSettingsSection = vAppApi.modifyLeaseSettingsSection(vApp.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyLeaseSettingsSection), String.format(TASK_COMPLETE_TIMELY, "modifyLeaseSettingsSection"));

      // Retrieve the modified section
      LeaseSettingsSection modified = vAppApi.getLeaseSettingsSection(vApp.getHref());

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

   @Test(description = "GET /vApp/{id}/networkConfigSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkConfigSection() {
      // The method under test
      NetworkConfigSection section = vAppApi.getNetworkConfigSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkNetworkConfigSection(section);
   }

   @Test(description = "PUT /vApp/{id}/networkConfigSection", dependsOnMethods = { "testGetNetworkConfigSection" })
   public void testModifyNetworkConfigSection() {
      // Copy existing section and update fields
      NetworkConfigSection oldSection = vAppApi.getNetworkConfigSection(vApp.getHref());
      VAppNetworkConfiguration networkConfig = VAppNetworkConfiguration.builder().build();
      NetworkConfigSection newSection = oldSection.toBuilder().networkConfigs(ImmutableSet.of(networkConfig))
            .build();

      // The method under test
      Task modifyNetworkConfigSection = vAppApi.modifyNetworkConfigSection(vApp.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyNetworkConfigSection), String.format(TASK_COMPLETE_TIMELY, "modifyNetworkConfigSection"));

      // Retrieve the modified section
      NetworkConfigSection modified = vAppApi.getNetworkConfigSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkNetworkConfigSection(modified);

      // Check the modified section fields are set correctly
//      assertEquals(modified.getInfo(), newSection.getInfo());

      // Check the section was modified correctly
      assertEquals(modified, newSection, String.format(ENTITY_EQUAL, "NetworkConfigSection"));
   }

   @Test(description = "GET /vApp/{id}/networkSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkSection() {
      // The method under test
      NetworkSection section = vAppApi.getNetworkSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkNetworkSection(section);
   }

   @Test(description = "GET /vApp/{id}/owner", dependsOnMethods = { "testGetVApp" })
   public void testGetOwner() {
      // The method under test
      Owner owner = vAppApi.getOwner(vApp.getHref());

      // Check the retrieved object is well formed
      checkOwner(owner);
   }

   @Test(description = "PUT /vApp/{id}/owner", dependsOnMethods = { "testGetOwner" })
   public void testModifyOwner() {
      Owner newOwner = Owner.builder().user(Reference.builder().href(testUserURI).type(ADMIN_USER).build()).build();

      // The method under test
      vAppApi.modifyOwner(vApp.getHref(), newOwner);

      // Get the new VApp owner
      Owner modified = vAppApi.getOwner(vApp.getHref());

      // Check the retrieved object is well formed
      checkOwner(modified);

      // Check the href fields match
      assertEquals(modified.getUser().getHref(), newOwner.getUser().getHref());
   }

   @Test(description = "GET /vApp/{id}/productSections", dependsOnMethods = { "testGetVApp" })
   public void testGetProductSections() {
      // The method under test
      ProductSectionList sectionList = vAppApi.getProductSections(vApp.getHref());

      // Check the retrieved object is well formed
      checkProductSectionList(sectionList);
   }

   @Test(description = "PUT /vApp/{id}/productSections", dependsOnMethods = { "testGetProductSections" })
   public void testModifyProductSections() {
      // Copy existing section and update fields
      ProductSectionList oldSections = vAppApi.getProductSections(vApp.getHref());
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
      Task modifyProductSections = vAppApi.modifyProductSections(vApp.getHref(), newSections);
      assertTrue(retryTaskSuccess.apply(modifyProductSections), String.format(TASK_COMPLETE_TIMELY, "modifyProductSections"));

      // Retrieve the modified section
      ProductSectionList modified = vAppApi.getProductSections(vApp.getHref());

      // Check the retrieved object is well formed
      checkProductSectionList(modified);

      // Check the modified object has an extra ProductSection
      assertEquals(modified.getProductSections().size(), oldSections.getProductSections().size() + 1);

      // Check the section was modified correctly
      assertEquals(modified, newSections);
   }

   @Test(description = "GET /vApp/{id}/startupSection", dependsOnMethods = { "testGetVApp" })
   public void testGetStartupSection() {
      // The method under test
      StartupSection section = vAppApi.getStartupSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkStartupSection(section);
   }

   @Test(description = "PUT /vApp/{id}/startupSection", dependsOnMethods = { "testGetStartupSection" })
   public void testModifyStartupSection() {
      // Copy existing section and update fields
      StartupSection oldSection = vAppApi.getStartupSection(vApp.getHref());
      StartupSection newSection = oldSection.toBuilder().build();

      // The method under test
      Task modifyStartupSection = vAppApi.modifyStartupSection(vApp.getHref(), newSection);
      assertTrue(retryTaskSuccess.apply(modifyStartupSection), String.format(TASK_COMPLETE_TIMELY, "modifyStartupSection"));

      // Retrieve the modified section
      StartupSection modified = vAppApi.getStartupSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkStartupSection(modified);

      // Check the modified section fields are set correctly
      // assertEquals(modified.getX(), "");
      assertEquals(modified, newSection);
   }

   @Test(description = "PUT /vApp/{id}/metadata", dependsOnMethods = { "testGetVApp" })
   public void testSetMetadataValue() {
      key = name("key-");
      String value = name("value-");
      metadataValue = MetadataValue.builder().value(value).build();
      vAppApi.getMetadataApi().putEntry(vApp.getHref(), key, metadataValue);

      // Retrieve the value, and assert it was set correctly
      MetadataValue newMetadataValue = vAppApi.getMetadataApi().getValue(vApp.getHref(), key);

      // Check the retrieved object is well formed
      checkMetadataValueFor(VAPP, newMetadataValue, value);
   }
   
   @Test(description = "GET /vApp/{id}/metadata", dependsOnMethods = { "testSetMetadataValue" })
   public void testGetMetadata() {
      key = name("key-");
      String value = name("value-");
      metadataValue = MetadataValue.builder().value(value).build();
      vAppApi.getMetadataApi().putEntry(vApp.getHref(), key, metadataValue);
      
      // Call the method being tested
      Metadata metadata = vAppApi.getMetadataApi().get(vApp.getHref());
      
      checkMetadata(metadata);
      
      // Check requirements for this test
      assertFalse(Iterables.isEmpty(metadata.getMetadataEntries()), String.format(NOT_EMPTY_OBJECT_FMT, "MetadataEntry", "vApp"));
   }
   
   @Test(description = "GET /vApp/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadata" })
   public void testGetOrgMetadataValue() {
      // Call the method being tested
      MetadataValue value = vAppApi.getMetadataApi().getValue(vApp.getHref(), key);
      
      String expected = metadataValue.getValue();

      checkMetadataValue(value);
      assertEquals(value.getValue(), expected, String.format(CORRECT_VALUE_OBJECT_FMT, "Value", "MetadataValue", expected, value.getValue()));
   }

   @Test(description = "DELETE /vApp/{id}/metadata/{key}", dependsOnMethods = { "testSetMetadataValue" })
   public void testDeleteMetadataEntry() {
      // Delete the entry
      Task task = vAppApi.getMetadataApi().deleteEntry(vApp.getHref(), key);
      retryTaskSuccess.apply(task);

      // Confirm the entry has been deleted
      Metadata newMetadata = vAppApi.getMetadataApi().get(vApp.getHref());

      // Check the retrieved object is well formed
      checkMetadataKeyAbsentFor(VAPP, newMetadata, key);
   }

   @Test(description = "POST /vApp/{id}/metadata", dependsOnMethods = { "testGetMetadata" })
   public void testMergeMetadata() {
      Metadata oldMetadata = vAppApi.getMetadataApi().get(vApp.getHref());
      Map<String, String> oldMetadataMap = Checks.metadataToMap(oldMetadata);

      // Store a value, to be deleted
      String key = name("key-");
      String value = name("value-");
      Metadata addedMetadata = Metadata.builder()
            .entry(MetadataEntry.builder().key(key).value(value).build())
            .build();
      Task task = vAppApi.getMetadataApi().merge(vApp.getHref(), addedMetadata);
      retryTaskSuccess.apply(task);

      // Confirm the entry contains everything that was there, and everything that was being added
      Metadata newMetadata = vAppApi.getMetadataApi().get(vApp.getHref());
      Map<String, String> expectedMetadataMap = ImmutableMap.<String, String>builder()
            .putAll(oldMetadataMap)
            .put(key, value)
            .build();

      // Check the retrieved object is well formed
      checkMetadataFor(VAPP, newMetadata, expectedMetadataMap);
   }

   /**
    * @see VAppApi#deleteVApp(URI)
    */
   @Test(description = "DELETE /vApp/{id}")
   public void testDeleteVApp() {
      // Create a temporary VApp to delete
      VApp temp = instantiateVApp();

      // The method under test
      Task deleteVApp = vAppApi.deleteVApp(temp.getHref());
      assertTrue(retryTaskSuccess.apply(deleteVApp), String.format(TASK_COMPLETE_TIMELY, "deleteVApp"));

      VApp deleted = vAppApi.getVApp(temp.getHref());
      assertNull(deleted, "The VApp "+temp.getName()+" should have been deleted");
   }
   
   /**
    * Create the recompose vapp params.
    *
    * @param vappTemplateRef
    * @param vdc
    * @return
    * @throws VCloudException
    */
   public RecomposeVAppParams createRecomposeParams(VApp vApp, Vm vm) {

      // creating an item element. this item will contain the vm which should be added to the vapp.
      Reference reference = Reference.builder().name(name("vm-")).href(vm.getHref()).type(vm.getType()).build();
      SourcedCompositionItemParam vmItem = SourcedCompositionItemParam.builder().source(reference).build();

      InstantiationParams vmInstantiationParams = null;

      Set<NetworkAssignment> networkAssignments = Sets.newLinkedHashSet();

      // if the vm contains a network connection and the vApp does not contain any configured network
      if (vmHasNetworkConnectionConfigured(vm)) {
         if (!vAppHasNetworkConfigured(vApp)) {
            // create a new network connection section for the vm.
            NetworkConnectionSection networkConnectionSection = NetworkConnectionSection.builder()
                     .info("Empty network configuration parameters").build();
            // adding the network connection section to the instantiation params of the vapp.
            vmInstantiationParams = InstantiationParams.builder().sections(ImmutableSet.of(networkConnectionSection))
                     .build();
         }

         // if the vm already contains a network connection section and if the vapp contains a
         // configured network -> vm could be mapped to that network.
         else {
            Set<VAppNetworkConfiguration> vAppNetworkConfigurations = listVappNetworkConfigurations(vApp);
            Set<NetworkConnection> listVmNetworkConnections = listNetworkConnections(vm);
            for (NetworkConnection networkConnection : listVmNetworkConnections) {
               for (VAppNetworkConfiguration vAppNetworkConfiguration : vAppNetworkConfigurations) {
                  NetworkAssignment networkAssignment = NetworkAssignment.builder()
                           .innerNetwork(vAppNetworkConfiguration.getNetworkName())
                           .containerNetwork(vAppNetworkConfiguration.getNetworkName()).build();
                  networkAssignments.add(networkAssignment);
               }
            }
         }
      }

      // if the vm does not contain any network connection sections and if the
      // vapp contains a network configuration. we should add the vm to this
      // vapp network
      else {
         if (vAppHasNetworkConfigured(vApp)) {

            VAppNetworkConfiguration vAppNetworkConfiguration = getVAppNetworkConfig(vApp);
            System.out.println(vAppNetworkConfiguration.getNetworkName());
            System.out.println(vAppNetworkConfiguration.getDescription());

            NetworkConnection networkConnection = NetworkConnection.builder()
                     .network(vAppNetworkConfiguration.getNetworkName())
                     .ipAddressAllocationMode(IpAddressAllocationMode.DHCP).build();

            NetworkConnectionSection networkConnectionSection = NetworkConnectionSection.builder().info("networkInfo")
                     .primaryNetworkConnectionIndex(0).networkConnection(networkConnection).build();

            // adding the network connection section to the instantiation params of the vapp.
            vmInstantiationParams = InstantiationParams.builder().sections(ImmutableSet.of(networkConnectionSection))
                     .build();
         }
      }

      if (vmInstantiationParams != null)
         vmItem = SourcedCompositionItemParam.builder().fromSourcedCompositionItemParam(vmItem)
                  .instantiationParams(vmInstantiationParams)
                  .build();
      
      if (networkAssignments != null)
         vmItem = SourcedCompositionItemParam.builder().fromSourcedCompositionItemParam(vmItem)
                  .networkAssignment(networkAssignments)
                  .build();
      
      return RecomposeVAppParams.builder().name(name("recompose-"))
               // adding the vm item.
               .sourcedItems(ImmutableList.of(vmItem)).build();

   }

}
