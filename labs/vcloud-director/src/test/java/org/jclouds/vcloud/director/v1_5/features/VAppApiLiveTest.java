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
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkControlAccessParams;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkLeaseSettingsSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadata;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataKeyAbsentFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkNetworkConfigSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkNetworkSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkOwner;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkProductSectionList;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkStartupSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkVApp;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.dmtf.ovf.MsgType;
import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.dmtf.ovf.ProductSection;
import org.jclouds.dmtf.ovf.StartupSection;
import org.jclouds.vcloud.director.v1_5.AbstractVAppApiLiveTest;
import org.jclouds.vcloud.director.v1_5.domain.AccessSetting;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.network.DhcpService;
import org.jclouds.vcloud.director.v1_5.domain.network.FirewallService;
import org.jclouds.vcloud.director.v1_5.domain.network.IpRange;
import org.jclouds.vcloud.director.v1_5.domain.network.IpRanges;
import org.jclouds.vcloud.director.v1_5.domain.network.IpScope;
import org.jclouds.vcloud.director.v1_5.domain.network.NatService;
import org.jclouds.vcloud.director.v1_5.domain.network.Network;
import org.jclouds.vcloud.director.v1_5.domain.network.Network.FenceMode;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkAssignment;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConnection;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConnection.IpAddressAllocationMode;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkFeatures;
import org.jclouds.vcloud.director.v1_5.domain.network.SyslogServerSettings;
import org.jclouds.vcloud.director.v1_5.domain.network.VAppNetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.params.ComposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.InstantiationParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RecomposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.SourcedCompositionItemParam;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.CharMatcher;
import com.google.common.base.Optional;
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
@Test(singleThreaded = true, testName = "VAppApiLiveTest")
public class VAppApiLiveTest extends AbstractVAppApiLiveTest {

   private String key;
   private boolean testUserCreated = false;
   private User user;

   @BeforeClass(alwaysRun = true)
   protected void setupRequiredEntities() {

      if (adminContext != null) {
         userUrn = adminContext.getApi().getUserApi().addUserToOrg(randomTestUser("VAppAccessTest"), org.getId())
                  .getId();
      }
      user = lazyGetUser();
   }

   @AfterClass(alwaysRun = true, dependsOnMethods = { "cleanUpEnvironment" })
   public void cleanUp() {
      if (adminContext != null && testUserCreated && userUrn != null) {
         try {
            adminContext.getApi().getUserApi().remove(userUrn);
         } catch (Exception e) {
            logger.warn(e, "Error when deleting user");
         }
      }
   }

   /**
    * @see VAppApi#get(URI)
    */
   @Test(groups = { "live", "user" }, description = "GET /vApp/{id}")
   public void testGetVApp() {
      // The method under test
      vApp = vAppApi.get(vAppUrn);

      // Check the retrieved object is well formed
      checkVApp(vApp);

      // Check the required fields are set
      assertEquals(vApp.isDeployed(), Boolean.FALSE,
               String.format(OBJ_FIELD_EQ, VAPP, "deployed", "FALSE", vApp.isDeployed().toString()));
      assertTrue(vApp.getName().startsWith("test-vapp-"),
               String.format(MATCHES_STRING_FMT, "name", "test-vapp-*", vApp.getName()));
      assertEquals(vApp.getDescription(), "Test VApp",
               String.format(OBJ_FIELD_EQ, VAPP, "Description", "Test VApp", vApp.getDescription()));

      // TODO instantiationParams instantiationParams()
      // TODO source.href vAppTemplateURI

      // Check status
      assertVAppStatus(vAppUrn, Status.POWERED_OFF);
   }

   @Test(groups = { "live", "user" }, description = "POST /vApp/{id}/action/recomposeVApp")
   public void testRecomposeVApp() {
      
      VApp composedVApp = vdcApi.composeVApp(vdcUrn, ComposeVAppParams.builder()
               .name(name("composed-"))
               .instantiationParams(instantiationParams())
               .build());
      
      Set<Vm> vms = getAvailableVMsFromVAppTemplate(vAppTemplate);
          
      // get the first vm to be added to vApp
      Vm toAddVm = Iterables.get(vms, 0);

      // TODO clean up network config of the vm
      //cleanUpNetworkConnectionSection(toAddVm);
      
      RecomposeVAppParams params = addRecomposeParams(composedVApp, toAddVm);

      // The method under test
      Task recomposeVApp = vAppApi.recompose(composedVApp.getId(), params);
      assertTaskSucceedsLong(recomposeVApp);

      // remove a vm
      VApp configured = vAppApi.get(composedVApp.getId());
      List<Vm> vmsToBeDeleted = configured.getChildren().getVms();
      Vm toBeDeleted = Iterables.find(vmsToBeDeleted, new Predicate<Vm>() {

         @Override
         public boolean apply(Vm vm) {
            return vm.getName().startsWith("vm-");
         }
      
      });
      Task removeVm = vmApi.remove(toBeDeleted.getId());
      assertTaskSucceedsLong(removeVm);
      
      Task deleteVApp = vAppApi.remove(composedVApp.getHref());
      assertTaskSucceedsLong(deleteVApp);
   }

   private Set<Vm> getAvailableVMsFromVAppTemplate(VAppTemplate vAppTemplate) {
      return ImmutableSet.copyOf(Iterables.filter(vAppTemplate.getChildren(), new Predicate<Vm>() {
         // filter out vms in the vApp template with computer name that contains underscores, dots,
         // or both.
         @Override
         public boolean apply(Vm input) {
            GuestCustomizationSection guestCustomizationSection = vmApi.getGuestCustomizationSection(input.getId());
            String computerName = guestCustomizationSection.getComputerName();
            String retainComputerName = CharMatcher.inRange('0', '9').or(CharMatcher.inRange('a', 'z'))
                     .or(CharMatcher.inRange('A', 'Z')).or(CharMatcher.is('-')).retainFrom(computerName);
            return computerName.equals(retainComputerName);
         }
      }));
   }

   /**
    * @see VAppApi#edit(URI, VApp)
    */
   @Test(groups = { "live", "user" }, description = "PUT /vApp/{id}", dependsOnMethods = { "testGetVApp" })
   public void testEditVApp() {
      VApp newVApp = VApp.builder().name(name("new-name-")).description("New Description").build();
      vAppNames.add(newVApp.getName());

      // The method under test
      Task editVApp = vAppApi.edit(vAppUrn, newVApp);
      assertTrue(retryTaskSuccess.apply(editVApp), String.format(TASK_COMPLETE_TIMELY, "editVApp"));

      // Get the edited VApp
      vApp = vAppApi.get(vAppUrn);

      // Check the required fields are set
      assertEquals(vApp.getName(), newVApp.getName(),
               String.format(OBJ_FIELD_EQ, VAPP, "Name", newVApp.getName(), vApp.getName()));
      assertEquals(vApp.getDescription(), newVApp.getDescription(),
               String.format(OBJ_FIELD_EQ, VAPP, "Description", newVApp.getDescription(), vApp.getDescription()));
   }

   @Test(groups = { "live", "user" }, description = "POST /vApp/{id}/action/deploy", dependsOnMethods = { "testGetVApp" })
   public void testDeployVApp() {
      DeployVAppParams params = DeployVAppParams.builder()
               .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS)).notForceCustomization()
               .notPowerOn().build();

      // The method under test
      Task deployVApp = vAppApi.deploy(vAppUrn, params);
      assertTrue(retryTaskSuccessLong.apply(deployVApp), String.format(TASK_COMPLETE_TIMELY, "deployVApp"));

      // Get the edited VApp
      vApp = vAppApi.get(vAppUrn);

      // Check the required fields are set
      assertTrue(vApp.isDeployed(), String.format(OBJ_FIELD_EQ, VAPP, "deployed", "TRUE", vApp.isDeployed().toString()));

      // Check status
      assertVAppStatus(vAppUrn, Status.POWERED_OFF);
   }

   @Test(groups = { "live", "user" }, description = "POST /vApp/{id}/power/action/powerOn", dependsOnMethods = { "testDeployVApp" })
   public void testPowerOnVApp() {
      // Power off VApp
      vApp = powerOffVApp(vAppUrn);

      // The method under test
      Task powerOnVApp = vAppApi.powerOn(vAppUrn);
      assertTaskSucceedsLong(powerOnVApp);

      // Get the edited VApp
      vApp = vAppApi.get(vAppUrn);

      // Check status
      assertVAppStatus(vAppUrn, Status.POWERED_ON);
   }

   @Test(groups = { "live", "user" }, description = "POST /vApp/{id}/power/action/reboot", dependsOnMethods = { "testDeployVApp" })
   public void testReboot() {
      // Power on VApp
      vApp = powerOnVApp(vAppUrn);

      // The method under test
      Task reboot = vAppApi.reboot(vAppUrn);
      assertTaskSucceedsLong(reboot);

      // Get the edited VApp
      vApp = vAppApi.get(vAppUrn);

      // Check status
      assertVAppStatus(vAppUrn, Status.POWERED_OFF);
   }

   @Test(groups = { "live", "user" }, description = "POST /vApp/{id}/power/action/shutdown", dependsOnMethods = { "testDeployVApp" })
   public void testShutdown() {
      // Power on VApp
      vApp = powerOnVApp(vAppUrn);

      vApp = vAppApi.get(vAppUrn);
      
      // The method under test
      Task shutdown = vAppApi.shutdown(vAppUrn);
      assertTaskSucceedsLong(shutdown);

      // Get the edited VApp
      vApp = vAppApi.get(vAppUrn);

      // Check status
      assertVAppStatus(vAppUrn, Status.POWERED_OFF);

      // Power on the VApp again
      vApp = powerOnVApp(vAppUrn);
   }

   @Test(groups = { "live", "user" }, description = "POST /vApp/{id}/power/action/suspend", dependsOnMethods = { "testDeployVApp" })
   public void testSuspend() {
      // Power on VApp
      vApp = powerOnVApp(vAppUrn);

      // The method under test
      Task suspend = vAppApi.suspend(vAppUrn);
      assertTaskSucceedsLong(suspend);

      // Get the edited VApp
      vApp = vAppApi.get(vAppUrn);

      // Check status
      assertVAppStatus(vAppUrn, Status.SUSPENDED);

      // Power on the VApp again
      vApp = powerOnVApp(vAppUrn);
   }

   @Test(groups = { "live", "user" }, description = "POST /vApp/{id}/power/action/reset", dependsOnMethods = { "testDeployVApp" })
   public void testReset() {
      // Power on VApp
      vApp = powerOnVApp(vAppUrn);

      // The method under test
      Task reset = vAppApi.reset(vAppUrn);
      assertTaskSucceedsLong(reset);

      // Get the edited VApp
      vApp = vAppApi.get(vAppUrn);

      // Check status
      assertVAppStatus(vAppUrn, Status.POWERED_ON);
   }

   @Test(groups = { "live", "user" }, description = "POST /vApp/{id}/action/undeploy", dependsOnMethods = { "testDeployVApp" })
   public void testUndeployVApp() {
      // Power on VApp
      vApp = powerOnVApp(vAppUrn);

      UndeployVAppParams params = UndeployVAppParams.builder().build();

      // The method under test
      Task undeploy = vAppApi.undeploy(vAppUrn, params);
      assertTrue(retryTaskSuccess.apply(undeploy), String.format(TASK_COMPLETE_TIMELY, "undeploy"));

      // Get the edited VApp
      vApp = vAppApi.get(vAppUrn);

      // Check status
      assertFalse(vApp.isDeployed(),
               String.format(OBJ_FIELD_EQ, VAPP, "deployed", "FALSE", vApp.isDeployed().toString()));
      assertVAppStatus(vAppUrn, Status.POWERED_OFF);
   }

   @Test(groups = { "live", "user" }, description = "POST /vApp/{id}/power/action/powerOff", dependsOnMethods = { "testUndeployVApp" })
   public void testPowerOffVApp() {
      // Power on VApp
      vApp = powerOnVApp(vAppUrn);

      // The method under test
      Task powerOffVApp = vAppApi.powerOff(vAppUrn);
      assertTrue(retryTaskSuccess.apply(powerOffVApp), String.format(TASK_COMPLETE_TIMELY, "powerOffVApp"));

      // Get the edited VApp
      vApp = vAppApi.get(vAppUrn);

      // Check status
      assertVAppStatus(vAppUrn, Status.POWERED_OFF);
   }

   @Test(groups = { "live", "user" }, description = "POST /vApp/{id}/action/controlAccess", dependsOnMethods = { "testGetVApp" })
   public void testControlAccessUser() {
      ControlAccessParams params = ControlAccessParams
               .builder()
               .notSharedToEveryone()
               .accessSetting(
                        AccessSetting.builder()
                                 .subject(Reference.builder().href(user.getHref()).type(ADMIN_USER).build())
                                 .accessLevel("ReadOnly").build()).build();

      // The method under test
      ControlAccessParams modified = vAppApi.editControlAccess(vAppUrn, params);

      // Check the retrieved object is well formed
      checkControlAccessParams(modified);
      // Check the required fields are set
      assertEquals(modified, params, String.format(ENTITY_EQUAL, "ControlAccessParams"));
   }

   @Test(groups = { "live", "user" }, description = "POST /vApp/{id}/action/controlAccess", dependsOnMethods = { "testControlAccessUser" })
   public void testControlAccessEveryone() {

      ControlAccessParams params = ControlAccessParams.builder().sharedToEveryone().everyoneAccessLevel("FullControl")
               .build();

      // The method under test
      ControlAccessParams modified = vAppApi.editControlAccess(vAppUrn, params);

      // Check the retrieved object is well formed
      checkControlAccessParams(modified);

      // Check entities are equal
      assertEquals(modified, params, String.format(ENTITY_EQUAL, "ControlAccessParams"));
   }

   @Test(groups = { "live", "user" }, description = "POST /vApp/{id}/action/discardSuspendedState", dependsOnMethods = { "testDeployVApp" })
   public void testDiscardSuspendedState() {
      // Power on, then suspend the VApp
      vApp = powerOnVApp(vAppUrn);
      vApp = suspendVApp(vAppUrn);

      // The method under test
      Task discardSuspendedState = vAppApi.discardSuspendedState(vAppUrn);
      assertTrue(retryTaskSuccess.apply(discardSuspendedState),
               String.format(TASK_COMPLETE_TIMELY, "discardSuspendedState"));
   }

   @Test(groups = { "live", "user" }, description = "GET /vApp/{id}/controlAccess", dependsOnMethods = { "testGetVApp" })
   public void testGetControlAccess() {
      // The method under test
      ControlAccessParams controlAccess = vAppApi.getAccessControl(vAppUrn);

      // Check the retrieved object is well formed
      checkControlAccessParams(controlAccess);
   }

   @Test(description = "GET /vApp/{id}/leaseSettingsSection", dependsOnMethods = { "testGetVApp" })
   public void testGetLeaseSettingsSection() {
      // The method under test
      LeaseSettingsSection section = vAppApi.getLeaseSettingsSection(vAppUrn);

      // Check the retrieved object is well formed
      checkLeaseSettingsSection(section);
   }

   @Test(groups = { "live", "user" }, description = "PUT /vApp/{id}/leaseSettingsSection", dependsOnMethods = { "testGetLeaseSettingsSection" })
   public void testEditLeaseSettingsSection() {
      // Copy existing section
      LeaseSettingsSection oldSection = vAppApi.getLeaseSettingsSection(vAppUrn);
      Integer twoHours = (int) TimeUnit.SECONDS.convert(2L, TimeUnit.HOURS);
      LeaseSettingsSection newSection = oldSection.toBuilder().deploymentLeaseInSeconds(twoHours).build();

      // The method under test
      Task editLeaseSettingsSection = vAppApi.editLeaseSettingsSection(vAppUrn, newSection);
      assertTrue(retryTaskSuccess.apply(editLeaseSettingsSection),
               String.format(TASK_COMPLETE_TIMELY, "editLeaseSettingsSection"));

      // Retrieve the modified section
      LeaseSettingsSection modified = vAppApi.getLeaseSettingsSection(vAppUrn);

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
         assertTrue(modified.getStorageLeaseExpiration().after(newSection.getStorageLeaseExpiration()), String.format(
                  "The new storageLeaseExpiration timestamp must be later than the original: %s > %s",
                  dateService.iso8601DateFormat(modified.getStorageLeaseExpiration()),
                  dateService.iso8601DateFormat(newSection.getStorageLeaseExpiration())));
      }

      // Reset the date fields
      modified = modified.toBuilder().deploymentLeaseExpiration(null).storageLeaseExpiration(null).build();
      newSection = newSection.toBuilder().deploymentLeaseExpiration(null).storageLeaseExpiration(null).build();

      // Check the section was modified correctly
      assertEquals(
               modified.getDeploymentLeaseInSeconds(),
               twoHours,
               String.format(OBJ_FIELD_EQ, "LeaseSettingsSection", "DeploymentLeaseInSeconds",
                        Integer.toString(twoHours), modified.getDeploymentLeaseInSeconds().toString()));
      assertEquals(modified, newSection, String.format(ENTITY_EQUAL, "LeaseSettingsSection"));
   }

   @Test(groups = { "live", "user" }, description = "GET /vApp/{id}/networkConfigSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkConfigSection() {
      // The method under test
      NetworkConfigSection section = vAppApi.getNetworkConfigSection(vAppUrn);

      // Check the retrieved object is well formed
      checkNetworkConfigSection(section);
   }

   @Test(description = "PUT /vApp/{id}/networkConfigSection", dependsOnMethods = { "testGetNetworkConfigSection" })
   public void testEditNetworkConfigSection() {
      
      // Copy existing section and update fields
      NetworkConfigSection oldSection = vAppApi.getNetworkConfigSection(vAppUrn);
      Network network = lazyGetNetwork();
      
      tryFindBridgedNetworkInOrg();
      IpRange ipRange = ipRange();
      NetworkConfiguration newConfiguration = NetworkConfiguration.builder()
               .ipScope(ipScope(ipRange))
               .parentNetwork(Reference.builder().fromEntity(network).build())
               .fenceMode(FenceMode.NAT_ROUTED)
               .retainNetInfoAcrossDeployments(false)
               .syslogServerSettings(SyslogServerSettings.builder().syslogServerIp1("192.168.14.27").build())
               .features(NetworkFeatures.builder()
                        .service(DhcpService.builder()
                                 .ipRange(ipRange)
                                 .build())
                        .service(FirewallService.builder()
                                 .logDefaultAction(false)
                                 .defaultAction("drop")
                                 .build())
                        .service(NatService.builder()
                                 .natType("portForwarding")
                                 .enabled(false)
                                 .build())                               
                        .build())
               .build();
           
      final String networkName = name("vAppNetwork-");
      VAppNetworkConfiguration newVAppNetworkConfiguration = VAppNetworkConfiguration.builder()
               .networkName(networkName)
               .description(name("description-"))
               .configuration(newConfiguration)
               .build();

      NetworkConfigSection newSection = oldSection.toBuilder().networkConfigs(ImmutableSet.of(newVAppNetworkConfiguration)).build();

      // The method under test
      Task editNetworkConfigSection = vAppApi.editNetworkConfigSection(vAppUrn, newSection);
      assertTrue(retryTaskSuccess.apply(editNetworkConfigSection),
               String.format(TASK_COMPLETE_TIMELY, "editNetworkConfigSection"));

      // Retrieve the modified section
      NetworkConfigSection modified = vAppApi.getNetworkConfigSection(vAppUrn);

      // Check the retrieved object is well formed
      checkNetworkConfigSection(modified);
      
      Optional<VAppNetworkConfiguration> modifiedOptionalVAppNetwork = Iterables.tryFind(modified.getNetworkConfigs(), new IsVAppNetworkNamed(networkName));
      if(!modifiedOptionalVAppNetwork.isPresent())
         fail(String.format("Could not find vApp network named %s", networkName));
      
      Optional<VAppNetworkConfiguration> newOptionalVAppNetwork = Iterables.tryFind(newSection.getNetworkConfigs(), new IsVAppNetworkNamed(networkName));
      if(!newOptionalVAppNetwork.isPresent())
         fail(String.format("Could not find vApp network named %s", networkName));

      assertEquals(modifiedOptionalVAppNetwork.get().getNetworkName(), newOptionalVAppNetwork.get().getNetworkName(), String.format(ENTITY_EQUAL, "NetworkName"));
      assertEquals(modifiedOptionalVAppNetwork.get().getConfiguration().getFenceMode(), newOptionalVAppNetwork.get().getConfiguration().getFenceMode(), String.format(ENTITY_EQUAL, "FenceMode"));
      assertEquals(modifiedOptionalVAppNetwork.get().getConfiguration().getIpScope(), newOptionalVAppNetwork.get().getConfiguration().getIpScope(), String.format(ENTITY_EQUAL, "IpScope"));
      assertEquals(modifiedOptionalVAppNetwork.get().getConfiguration().getNetworkFeatures(), newOptionalVAppNetwork.get().getConfiguration().getNetworkFeatures(), String.format(ENTITY_EQUAL, "NetworkFeatures"));
   }

   private IpRange ipRange() {
      return IpRange.builder()
               .startAddress("192.168.2.100")
               .endAddress("192.168.2.199")
               .build();
   }

   private IpScope ipScope(IpRange ipRange) {
      IpRanges newIpRanges = IpRanges.builder()
               .ipRange(ipRange)
               .build();
      return IpScope.builder()
               .isInherited(false)
               .gateway("192.168.2.1")
               .netmask("255.255.0.0")
               .ipRanges(newIpRanges).build();
   }

   @Test(groups = { "live", "user" }, description = "GET /vApp/{id}/networkSection", dependsOnMethods = { "testGetVApp" })
   public void testGetNetworkSection() {
      // The method under test
      NetworkSection section = vAppApi.getNetworkSection(vAppUrn);

      // Check the retrieved object is well formed
      checkNetworkSection(section);
   }

   @Test(groups = { "live", "user" }, description = "GET /vApp/{id}/owner", dependsOnMethods = { "testGetVApp" })
   public void testGetOwner() {
      // The method under test
      Owner owner = vAppApi.getOwner(vAppUrn);

      // Check the retrieved object is well formed
      checkOwner(owner);
   }

   @Test(groups = { "live", "user" }, description = "PUT /vApp/{id}/owner", dependsOnMethods = { "testGetOwner" })
   public void testEditOwner() {
      Owner newOwner = Owner.builder().user(Reference.builder().href(user.getHref()).type(ADMIN_USER).build()).build();

      // The method under test
      vAppApi.editOwner(vAppUrn, newOwner);

      // Get the new VApp owner
      Owner modified = vAppApi.getOwner(vAppUrn);

      // Check the retrieved object is well formed
      checkOwner(modified);

      // Check the href fields match
      assertEquals(modified.getUser().getHref(), newOwner.getUser().getHref());
   }

   @Test(groups = { "live", "user" }, description = "GET /vApp/{id}/productSections", dependsOnMethods = { "testGetVApp" })
   public void testGetProductSections() {
      // The method under test
      ProductSectionList sectionList = vAppApi.getProductSections(vAppUrn);

      // Check the retrieved object is well formed
      checkProductSectionList(sectionList);
   }

   @Test(groups = { "live", "user" }, description = "PUT /vApp/{id}/productSections", dependsOnMethods = { "testGetProductSections" })
   public void testEditProductSections() {
      // Copy existing section and edit fields
      ProductSectionList oldSections = vAppApi.getProductSections(vAppUrn);
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
      Task editProductSections = vAppApi.editProductSections(vAppUrn, newSections);
      assertTrue(retryTaskSuccess.apply(editProductSections),
               String.format(TASK_COMPLETE_TIMELY, "editProductSections"));

      // Retrieve the modified section
      ProductSectionList modified = vAppApi.getProductSections(vAppUrn);

      // Check the retrieved object is well formed
      checkProductSectionList(modified);

      // Check the modified object has an extra ProductSection
      assertEquals(modified.getProductSections().size(), oldSections.getProductSections().size() + 1);

      // Check the section was modified correctly
      assertEquals(modified, newSections);
   }

   @Test(groups = { "live", "user" }, description = "GET /vApp/{id}/startupSection", dependsOnMethods = { "testGetVApp" })
   public void testGetStartupSection() {
      // The method under test
      StartupSection section = vAppApi.getStartupSection(vAppUrn);

      // Check the retrieved object is well formed
      checkStartupSection(section);
   }

   @Test(groups = { "live", "user" }, description = "PUT /vApp/{id}/startupSection", dependsOnMethods = { "testGetStartupSection" })
   public void testEditStartupSection() {
      // Copy existing section and edit fields
      StartupSection oldSection = vAppApi.getStartupSection(vAppUrn);
      StartupSection newSection = oldSection.toBuilder().build();

      // The method under test
      Task editStartupSection = vAppApi.editStartupSection(vAppUrn, newSection);
      assertTrue(retryTaskSuccess.apply(editStartupSection), String.format(TASK_COMPLETE_TIMELY, "editStartupSection"));

      // Retrieve the modified section
      StartupSection modified = vAppApi.getStartupSection(vAppUrn);

      // Check the retrieved object is well formed
      checkStartupSection(modified);

      // Check the modified section fields are set correctly
      assertEquals(modified, newSection);
   }

   @Test(groups = { "live", "user" }, description = "PUT /vApp/{id}/metadata", dependsOnMethods = { "testGetVApp" })
   public void testSetMetadataValue() {
      key = name("key-");
      String value = name("value-");
      context.getApi().getMetadataApi(vAppUrn).put(key, value);

      // Retrieve the value, and assert it was set correctly
      String newMetadataValue = context.getApi().getMetadataApi(vAppUrn).get(key);

      // Check the retrieved object is well formed
      assertEquals(newMetadataValue, value);
   }

   @Test(groups = { "live", "user" }, description = "GET /vApp/{id}/metadata", dependsOnMethods = { "testSetMetadataValue" })
   public void testGetMetadata() {
      key = name("key-");
      String value = name("value-");
      context.getApi().getMetadataApi(vAppUrn).put(key, value);

      // Call the method being tested
      Metadata metadata = context.getApi().getMetadataApi(vAppUrn).get();

      checkMetadata(metadata);

      // Check requirements for this test
      assertFalse(Iterables.isEmpty(metadata.getMetadataEntries()),
               String.format(NOT_EMPTY_OBJECT_FMT, "MetadataEntry", "vApp"));
   }

   @Test(groups = { "live", "user" }, description = "GET /vApp/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadata" })
   public void testGetOrgMetadataValue() {
      
      key = name("key-");
      String value = name("value-");
      context.getApi().getMetadataApi(vAppUrn).put(key, value);
      
      // Call the method being tested
      String newValue = context.getApi().getMetadataApi(vAppUrn).get(key);

      assertEquals(newValue, value, String.format(CORRECT_VALUE_OBJECT_FMT, "Value", "MetadataValue", value, newValue));
   }

   @Test(groups = { "live", "user" }, description = "DELETE /vApp/{id}/metadata/{key}", dependsOnMethods = { "testSetMetadataValue" })
   public void testRemoveMetadataEntry() {
      // Delete the entry
      Task task = context.getApi().getMetadataApi(vAppUrn).remove(key);
      retryTaskSuccess.apply(task);

      // Confirm the entry has been removed
      Metadata newMetadata = context.getApi().getMetadataApi(vAppUrn).get();

      // Check the retrieved object is well formed
      checkMetadataKeyAbsentFor(VAPP, newMetadata, key);
   }

   @Test(groups = { "live", "user" }, description = "POST /vApp/{id}/metadata", dependsOnMethods = { "testGetMetadata" })
   public void testMergeMetadata() {
      Metadata oldMetadata = context.getApi().getMetadataApi(vAppUrn).get();
      Map<String, String> oldMetadataMap = Checks.metadataToMap(oldMetadata);

      // Store a value, to be removed
      String key = name("key-");
      String value = name("value-");
      Task task = context.getApi().getMetadataApi(vAppUrn).putAll(ImmutableMap.of(key, value));
      retryTaskSuccess.apply(task);

      // Confirm the entry contains everything that was there, and everything that was being added
      Metadata newMetadata = context.getApi().getMetadataApi(vAppUrn).get();
      Map<String, String> expectedMetadataMap = ImmutableMap.<String, String> builder().putAll(oldMetadataMap)
               .put(key, value).build();

      // Check the retrieved object is well formed
      checkMetadataFor(VAPP, newMetadata, expectedMetadataMap);
   }

   /**
    * @see VAppApi#remove(URI)
    */
   @Test(groups = { "live", "user" }, description = "DELETE /vApp/{id}")
   public void testRemoveVApp() {
      // Create a temporary VApp to remove
      VApp temp = instantiateVApp();

      // The method under test
      Task removeVApp = vAppApi.remove(temp.getId());
      assertTrue(retryTaskSuccess.apply(removeVApp), String.format(TASK_COMPLETE_TIMELY, "removeVApp"));

      VApp removed = vAppApi.get(temp.getId());
      assertNull(removed, "The VApp " + temp.getName() + " should have been removed");
   }

   /**
    * Create the recompose vapp params.
    * 
    * @param vappTemplateRef
    * @param vdc
    * @return
    * @throws VCloudException
    */
   public RecomposeVAppParams addRecomposeParams(VApp vApp, Vm vm) {

      // creating an item element. this item will contain the vm which should be added to the vapp.
      Reference reference = Reference.builder().name(name("vm-")).href(vm.getHref()).type(vm.getType()).build();
      SourcedCompositionItemParam vmItem = SourcedCompositionItemParam.builder().source(reference).build();

      InstantiationParams vmInstantiationParams = null;

      Set<NetworkAssignment> networkAssignments = Sets.newLinkedHashSet();

      // if the vm contains a network connection and the vApp does not contain any configured
      // network
      if (vmHasNetworkConnectionConfigured(vm)) {
         if (!vAppHasNetworkConfigured(vApp)) {
            // add a new network connection section for the vm.
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
            for (VAppNetworkConfiguration vAppNetworkConfiguration : vAppNetworkConfigurations) {
               NetworkAssignment networkAssignment = NetworkAssignment.builder()
                        .innerNetwork(vAppNetworkConfiguration.getNetworkName())
                        .containerNetwork(vAppNetworkConfiguration.getNetworkName()).build();
               networkAssignments.add(networkAssignment);
            }
         }
      }

      // if the vm does not contain any network connection sections and if the
      // vapp contains a network configuration. we should add the vm to this
      // vapp network
      else {
         if (vAppHasNetworkConfigured(vApp)) {
            VAppNetworkConfiguration vAppNetworkConfiguration = getVAppNetworkConfig(vApp);
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
                  .instantiationParams(vmInstantiationParams).build();

      if (networkAssignments != null)
         vmItem = SourcedCompositionItemParam.builder().fromSourcedCompositionItemParam(vmItem)
                  .networkAssignment(networkAssignments).build();

      return RecomposeVAppParams.builder().name(name("recompose-"))
      // adding the vm item.
               .sourcedItems(ImmutableList.of(vmItem)).build();
   }
   
   private final class IsVAppNetworkNamed implements Predicate<VAppNetworkConfiguration> {
      private final String networkName;

      private IsVAppNetworkNamed(String networkName) {
         this.networkName = networkName;
      }

      @Override
      public boolean apply(VAppNetworkConfiguration input) {
         return input.getNetworkName().equals(networkName);
      }
   }
   
   private void cleanUpNetworkConnectionSection(Vm toAddVm) {
      NetworkConnectionSection networkConnectionSection = vmApi.getNetworkConnectionSection(toAddVm.getId());
      Set<NetworkConnection> networkConnections = networkConnectionSection.getNetworkConnections();
      for (NetworkConnection networkConnection : networkConnections) {
         NetworkConnection newNetworkConnection = networkConnection.toBuilder().isConnected(false).build();
         networkConnectionSection = networkConnectionSection.toBuilder().networkConnection(newNetworkConnection)
                  .build();
      }

      Task configureNetwork = vmApi.editNetworkConnectionSection(toAddVm.getId(), networkConnectionSection);
      assertTaskSucceedsLong(configureNetwork);
   }

}
