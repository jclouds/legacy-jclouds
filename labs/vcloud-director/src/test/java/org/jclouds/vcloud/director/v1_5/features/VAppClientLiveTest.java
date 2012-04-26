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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.dmtf.ovf.MsgType;
import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.dmtf.ovf.ProductSection;
import org.jclouds.dmtf.ovf.StartupSection;
import org.jclouds.io.Payloads;
import org.jclouds.vcloud.director.v1_5.AbstractVAppClientLiveTest;
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
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RecomposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of the {@link VAppClient}.
 *
 * @author grkvlt@apache.org
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "VAppClientLiveTest")
public class VAppClientLiveTest extends AbstractVAppClientLiveTest {

   private MetadataValue metadataValue;
   private String key;
   private URI testUserURI;
   private boolean mediaCreated = false;
   private boolean testUserCreated = false;
   
   @BeforeClass(alwaysRun = true, dependsOnMethods = { "setupRequiredClients" })
   protected void setupRequiredEntities() {
      Set<Link> links = vdcClient.getVdc(vdcURI).getLinks();

      if (mediaURI == null) {
         Predicate<Link> addMediaLink = and(relEquals(Link.Rel.ADD), typeEquals(VCloudDirectorMediaType.MEDIA));
         
         if (contains(links, addMediaLink)) {
            Link addMedia = find(links, addMediaLink);
            byte[] iso = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
            
            Media sourceMedia = Media.builder()
                  .type(VCloudDirectorMediaType.MEDIA)
                  .name("Test media "+random.nextInt())
                  .size(iso.length)
                  .imageType(Media.ImageType.ISO)
                  .description("Test media generated by vAppClientLiveTest")
                  .build();
            Media media = context.getApi().getMediaClient().createMedia(addMedia.getHref(), sourceMedia);
            
            Link uploadLink = getFirst(getFirst(media.getFiles(), null).getLinks(), null);
            context.getApi().getUploadClient().upload(uploadLink.getHref(), Payloads.newByteArrayPayload(iso));
            
            media = context.getApi().getMediaClient().getMedia(media.getHref());
            
            if (media.getTasks().size() == 1) {
               Task uploadTask = Iterables.getOnlyElement(media.getTasks());
               Checks.checkTask(uploadTask);
               assertEquals(uploadTask.getStatus(), Task.Status.RUNNING);
               assertTrue(retryTaskSuccess.apply(uploadTask), String.format(TASK_COMPLETE_TIMELY, "uploadTask"));
               media = context.getApi().getMediaClient().getMedia(media.getHref());
            }
            
            mediaURI = media.getHref();
            mediaCreated = true;
         }
      }
      
      if (adminContext != null) {
         Link orgLink = find(links, and(relEquals("up"), typeEquals(VCloudDirectorMediaType.ORG)));
         testUserURI = adminContext.getApi().getUserClient().createUser(toAdminUri(orgLink), randomTestUser("VAppAccessTest")).getHref();
      } else {
         testUserURI = userURI;
      }
   }
   
   @Override
   @AfterClass(alwaysRun = true)
   public void cleanUp() {
      if (adminContext != null && mediaCreated && mediaURI != null) {
         try {
	         Task delete = context.getApi().getMediaClient().deleteMedia(mediaURI);
	         taskDoneEventually(delete);
         } catch (Exception e) {
            logger.warn("Error when deleting media: %s", e.getMessage());
         }
      }
      if (adminContext != null && testUserCreated && testUserURI != null) {
         try {
	         adminContext.getApi().getUserClient().deleteUser(testUserURI);
         } catch (Exception e) {
            logger.warn("Error when deleting user: %s", e.getMessage());
         }
      }
   }

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
      vApp = powerOffVApp(vApp.getHref());

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
      vApp = powerOnVApp(vApp.getHref());
 
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
      vApp = powerOnVApp(vApp.getHref());

      // The method under test
      Task shutdown = vAppClient.shutdown(vAppURI);
      assertTaskSucceedsLong(shutdown);

      // Get the updated VApp
      vApp = vAppClient.getVApp(vAppURI);

      // Check status
      assertVAppStatus(vAppURI, Status.POWERED_OFF);

      // Power on the VApp again
      vApp = powerOnVApp(vApp.getHref());
   }

   @Test(description = "POST /vApp/{id}/power/action/suspend", dependsOnMethods = { "testDeployVApp" })
   public void testSuspend() {
      // Power on VApp
      vApp = powerOnVApp(vApp.getHref());

      // The method under test
      Task suspend = vAppClient.suspend(vAppURI);
      assertTaskSucceedsLong(suspend);

      // Get the updated VApp
      vApp = vAppClient.getVApp(vApp.getHref());

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
      vApp = powerOnVApp(vApp.getHref());

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
      vApp = powerOnVApp(vApp.getHref());
      
      // The method under test
      Task powerOffVApp = vAppClient.powerOff(vApp.getHref());
      assertTrue(retryTaskSuccess.apply(powerOffVApp), String.format(TASK_COMPLETE_TIMELY, "powerOffVApp"));

      // Get the updated VApp
      vApp = vAppClient.getVApp(vAppURI);

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
      vApp = suspendVApp(vApp.getHref());
      
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

   @Test(description = "GET /vApp/{id}/controlAccess", dependsOnMethods = { "testGetVApp" })
   public void testGetControlAccess() {
      // The method under test
      ControlAccessParams controlAccess = vAppClient.getControlAccess(vApp.getHref());

      // Check the retrieved object is well formed
      checkControlAccessParams(controlAccess);
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

   @Test(description = "GET /vApp/{id}/networkSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkSection() {
      // The method under test
      NetworkSection section = vAppClient.getNetworkSection(vApp.getHref());

      // Check the retrieved object is well formed
      checkNetworkSection(section);
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
      Owner newOwner = Owner.builder().user(Reference.builder().href(testUserURI).type(ADMIN_USER).build()).build();

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

      VApp deleted = vAppClient.getVApp(temp.getHref());
      assertNull(deleted, "The VApp "+temp.getName()+" should have been deleted");
   }
}
