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

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.MediaInsertOrEjectParams;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.RasdItemsList;
import org.jclouds.vcloud.director.v1_5.domain.RecomposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.RuntimeInfoSection;
import org.jclouds.vcloud.director.v1_5.domain.ScreenTicket;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VmPendingQuestion;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswer;
import org.jclouds.vcloud.director.v1_5.domain.ovf.NetworkSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.RASD;
import org.jclouds.vcloud.director.v1_5.domain.ovf.StartupSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.VirtualHardwareSection;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;

/**
 * Allows us to test the {@link VAppClient} via its side effects.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "unit", "user", "vapp" }, singleThreaded = true, testName = "VAppClientExpectTest")
public class VAppClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {
   
   private String vAppId = "";
   private URI vAppURI = URI.create(endpoint + vAppId);
   
   @BeforeClass
   public void before() {
   }
   
   @Test(enabled = false)//TODO
   public void testGetVapp() {
      VCloudDirectorClient client = orderedRequestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", vAppId)
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vApp/vApp.xml", VCloudDirectorMediaType.VAPP)
               .httpResponseBuilder().build());
      
      VApp expected = getVApp();

      assertEquals(client.getVAppClient().getVApp(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVApp() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId)
            .xmlFilePayload("/vApp/modifyVApp.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifiedVapp.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());
		         
		VApp modified = getVApp();
		modified.setName("new-name");
		modified.setDescription("New Description");
		
		Task expected = modifyVAppTask();
		
		assertEquals(client.getVAppClient().modifyVApp(vAppURI, modified), expected);
   }

   @Test(enabled = false)
   public void testDeleteVApp() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("DELETE", vAppId)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/deleteVAppTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		
		Task expected = deleteVAppTask();
		
		assertEquals(client.getVAppClient().deleteVApp(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testConsolidateVApp() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/consolidate")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/consolidateVAppTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		
		Task expected = consolidateVAppTask();
		
		assertEquals(client.getVAppClient().consolidateVApp(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testControlAccess() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/controlAccess")
            .xmlFilePayload("/vApp/controlAccessParams.xml", VCloudDirectorMediaType.CONTROL_ACCESS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/controlAccessParams.xml", VCloudDirectorMediaType.CONTROL_ACCESS)
            .httpResponseBuilder().build());
		
		ControlAccessParams params = controlAccessParams();
		
		ControlAccessParams expected = controlAccessParams();
		         
		assertEquals(client.getVAppClient().controlAccess(vAppURI, params), expected);
   }

   @Test(enabled = false)
   public void testDeploy() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/deploy")
            .xmlFilePayload("/vApp/deployParams.xml", VCloudDirectorMediaType.DEPLOY_VAPP_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/deployTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
      
      DeployVAppParams params = DeployVAppParams.builder()
            .build();
		
		Task expected = deployTask();
		
		assertEquals(client.getVAppClient().deploy(vAppURI, params), expected);
   }

   @Test(enabled = false)
   public void testDiscardSuspendedState() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/discardSuspendedState")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/discardSuspendedStateTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		
		Task expected = discardSuspendedStateTask();
		
		assertEquals(client.getVAppClient().discardSuspendedState(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testEnterMaintenanceMode() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/enterMaintenanceMode")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .httpResponseBuilder().statusCode(204).build());
		
		// TODO how to test?
		client.getVAppClient().enterMaintenanceMode(vAppURI);
   }

   @Test(enabled = false)
   public void testExitMaintenanceMode() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/exitMaintenanceMode")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .httpResponseBuilder().statusCode(204).build());
		
		// TODO how to test?
		client.getVAppClient().exitMaintenanceMode(vAppURI);
   }

   @Test(enabled = false)
   public void testInstallVMwareTools() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/installVMwareTools")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/installVMwareToolsTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		
		Task expected = installVMwareToolsTask();
		
		assertEquals(client.getVAppClient().installVMwareTools(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testRecomposeVApp() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/recomposeVApp")
            .xmlFilePayload("/vApp/recomposeVAppParams.xml", VCloudDirectorMediaType.RECOMPOSE_VAPP_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/recomposeVAppTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
     
      RecomposeVAppParams params = RecomposeVAppParams.builder()
            .build();
		
		Task expected = recomposeVAppTask();
		
		assertEquals(client.getVAppClient().recomposeVApp(vAppURI, params), expected);
   }

   @Test(enabled = false)
   public void testRelocate() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/relocate")
            .xmlFilePayload("/vApp/relocateParams.xml", VCloudDirectorMediaType.RELOCATE_VM_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/relocateTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
     
      RelocateParams params = RelocateParams.builder()
            .build();
		
		Task expected = relocateTask();
		
		assertEquals(client.getVAppClient().relocate(vAppURI, params), expected);
   }

   @Test(enabled = false)
   public void testUndeploy() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/undeploy")
            .xmlFilePayload("/vApp/undeployParams.xml", VCloudDirectorMediaType.UNDEPLOY_VAPP_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/undeployTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
      
      UndeployVAppParams params = UndeployVAppParams.builder()
            .build();
		
		Task expected = undeployTask();
		
		assertEquals(client.getVAppClient().undeploy(vAppURI, params), expected);
   }

   @Test(enabled = false)
   public void testUpgradeHardwareVersion() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/upgradeHardwareVersion")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/upgradeHardwareVersionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		
		Task expected = upgradeHardwareVersionTask();
		
		assertEquals(client.getVAppClient().upgradeHardwareVersion(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testPowerOff() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/power/action/powerOff")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/powerOffTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = powerOffTask();

      assertEquals(client.getVAppClient().powerOff(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testPowerOn() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/power/action/powerOn")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/powerOnTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = powerOnTask();

      assertEquals(client.getVAppClient().powerOn(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testReboot() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/power/action/reboot")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/rebootTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = rebootTask();

      assertEquals(client.getVAppClient().reboot(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testReset() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/power/action/reset")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/resetTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = resetTask();

      assertEquals(client.getVAppClient().reset(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testShutdown() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/power/action/shutdown")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/shutdownTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = shutdownTask();

      assertEquals(client.getVAppClient().shutdown(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testSuspend() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/power/action/suspend")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/suspend.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		
		Task expected = suspendTask();
		
		assertEquals(client.getVAppClient().suspend(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetControlAccesss() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/controlAccess")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getControlAccess.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

      ControlAccessParams expected = getControlAccessParams();

      assertEquals(client.getVAppClient().getControlAccess(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetGuestCustomizationSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/guestCustomizationSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getGuestCustomizationSection.xml", VCloudDirectorMediaType.GUEST_CUSTOMIZATION_SECTION)
            .httpResponseBuilder().build());
		
		GuestCustomizationSection expected = getGuestCustomizationSection();
		
		assertEquals(client.getVAppClient().getGuestCustomizationSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyGuestCustomizationSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/guestCustomizationSection")
            .xmlFilePayload("/vApp/modifyGuestCustomizationSection.xml", VCloudDirectorMediaType.GUEST_CUSTOMIZATION_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyGuestCustomizationSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
      
      GuestCustomizationSection section = getGuestCustomizationSection().toBuilder()
            .build();

      Task expected = modifyGuestCustomizationSectionTask();

      assertEquals(client.getVAppClient().modifyGuestCustomizationSection(vAppURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetLeaseSettingsSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/leaseSettingsSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getLeaseSettingsSection.xml", VCloudDirectorMediaType.LEASE_SETTINGS_SECTION)
            .httpResponseBuilder().build());

      LeaseSettingsSection expected = getLeaseSettingsSection();

      assertEquals(client.getVAppClient().getLeaseSettingsSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyLeaseSettingsSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/leaseSettingsSection")
            .xmlFilePayload("/vApp/modifyLeaseSettingsSection.xml", VCloudDirectorMediaType.LEASE_SETTINGS_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyLeaseSettingsSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
      
      LeaseSettingsSection section = getLeaseSettingsSection().toBuilder()
            .build();
		
		Task expected = modifyLeaseSettingsSectionTask();
		
		assertEquals(client.getVAppClient().modifyLeaseSettingsSection(vAppURI, section), expected);
   }

   @Test(enabled = false)
   public void testEjectMedia() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/media/action/ejectMedia")
            .xmlFilePayload("/vApp/ejectMediaParams.xml", VCloudDirectorMediaType.MEDIA_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/ejectMediaTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
      
      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .build();

      Task expected = ejectMediaTask();

      assertEquals(client.getVAppClient().ejectMedia(vAppURI, params), expected);
   }

   @Test(enabled = false)
   public void testInsertMedia() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/media/action/insertMedia")
            .xmlFilePayload("/vApp/insertMediaParams.xml", VCloudDirectorMediaType.MEDIA_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/insertMediaTask.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());
      
      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .build();

      Task expected = insertMediaTask();

      assertEquals(client.getVAppClient().insertMedia(vAppURI, params), expected);
   }

   @Test(enabled = false)
   public void testGetNetworkConfigSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/networkConfigSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getNetworkConfigSection.xml", VCloudDirectorMediaType.NETWORK_CONFIG_SECTION)
            .httpResponseBuilder().build());
			
		NetworkConfigSection expected = getNetworkConfigSection();
		
		assertEquals(client.getVAppClient().getNetworkConfigSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyNetworkConfigSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/networkConfigSection")
            .xmlFilePayload("/vApp/modifyNetworkConfigSection.xml", VCloudDirectorMediaType.NETWORK_CONFIG_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyNetworkConfigSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		      
		NetworkConfigSection section = getNetworkConfigSection().toBuilder()
		      .build();
		
		Task expected = modifyNetworkConfigSectionTask();
		
		assertEquals(client.getVAppClient().modifyNetworkConfigSection(vAppURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetNetworkConnectionSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/networkConnectionSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getNetworkConnectionSection.xml", VCloudDirectorMediaType.NETWORK_CONNECTION_SECTION)
            .httpResponseBuilder().build());

      NetworkConnectionSection expected = getNetworkConnectionSection();

         assertEquals(client.getVAppClient().getNetworkConnectionSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyNetworkConnectionSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/networkConnectionSection")
            .xmlFilePayload("/vApp/modifyNetworkConnectionSection.xml", VCloudDirectorMediaType.NETWORK_CONNECTION_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyNetworkConnectionSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		      
		NetworkConnectionSection section = getNetworkConnectionSection().toBuilder()
		      .build();
		
		Task expected = modifyNetworkConnectionSectionTask();
		
		assertEquals(client.getVAppClient().modifyNetworkConnectionSection(vAppURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetNetworkSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/networkSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getNetworkSection.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		
		NetworkSection expected = getNetworkSection();
		
		assertEquals(client.getVAppClient().getNetworkSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetOperatingSystemSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/operatingSystemSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getOperatingSystemSection.xml", VCloudDirectorMediaType.OPERATING_SYSTEM_SECTION)
            .httpResponseBuilder().build());

		OperatingSystemSection expected = getOperatingSystemSection();
		
		assertEquals(client.getVAppClient().getOperatingSystemSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyOperatingSystemSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/operatingSystemSection")
            .xmlFilePayload("/vApp/modifyOperatingSystemSection.xml", VCloudDirectorMediaType.OPERATING_SYSTEM_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyOperatingSystemSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
		      
      OperatingSystemSection section = getOperatingSystemSection().toBuilder()
		      .build();
		
		Task expected = modifyOperatingSystemSectionTask();
		
		assertEquals(client.getVAppClient().modifyOperatingSystemSection(vAppURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetOwner() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/owner")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getOwner.xml", VCloudDirectorMediaType.OWNER)
            .httpResponseBuilder().build());

         Owner expected = getOwner();

         assertEquals(client.getVAppClient().getOwner(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyOwner() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/owner")
            .xmlFilePayload("/vApp/modifyOwner.xml", VCloudDirectorMediaType.OWNER)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .httpResponseBuilder().statusCode(204).build());
      
      Owner owner = Owner.builder()
            .build();
		
		client.getVAppClient().modifyOwner(vAppURI, owner);
   }

   @Test(enabled = false)
   public void testGetProductSections() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/productSections")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getProductSections.xml", VCloudDirectorMediaType.PRODUCT_SECTION_LIST)
            .httpResponseBuilder().build());

         ProductSectionList expected = getProductSections();

         assertEquals(client.getVAppClient().getProductSections(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyProductSections() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/productSections")
            .xmlFilePayload("/vApp/modifyProductSections.xml", VCloudDirectorMediaType.PRODUCT_SECTION_LIST)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyProductSections.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         Task expected = modifyProductSectionsTask();

         assertEquals(client.getVAppClient().modifyProductSections(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetPendingQuestion() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/question")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getPendingQuestion.xml", VCloudDirectorMediaType.VM_PENDING_QUESTION)
            .httpResponseBuilder().build());

         VmPendingQuestion expected = getPendingQuestion();

         assertEquals(client.getVAppClient().getPendingQuestion(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testAnswerQuestion() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/question/action/answer")
            .xmlFilePayload("/vApp/answerQuestion.xml", VCloudDirectorMediaType.VM_PENDING_ANSWER)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .httpResponseBuilder().statusCode(204).build());

         VmQuestionAnswer answer = null; // = VmQuestionAnswer.builder();
//               .build;

         client.getVAppClient().answerQuestion(vAppURI, answer);
   }

   @Test(enabled = false)
   public void testGetRuntimeInfoSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/runtimeInfoSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getRuntimeInfoSection.xml", VCloudDirectorMediaType.RUNTIME_INFO_SECTION)
            .httpResponseBuilder().build());

      RuntimeInfoSection expected = getRuntimeInfoSection();

      assertEquals(client.getVAppClient().getRuntimeInfoSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetScreenImage() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/screen")
            .acceptMedia(VCloudDirectorMediaType.ANY_IMAGE)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .httpResponseBuilder()
            .headers(Multimaps.forMap(ImmutableMap.of("Content-Type", "image/png")))
            .message(new String(getScreenImage()))
            .build());
		
		byte[] expected = getScreenImage();
		
		assertEquals(client.getVAppClient().getScreenImage(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetScreenTicket() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/screen/action/acquireTicket")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getScreenTicket.xml", VCloudDirectorMediaType.SCREEN_TICKET)
            .httpResponseBuilder().build());
		
		ScreenTicket expected = getScreenTicket();
		
		assertEquals(client.getVAppClient().getScreenTicket(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetStartupSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/startupSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getStartupSection.xml", VCloudDirectorMediaType.STARTUP_SECTION)
            .httpResponseBuilder().build());
		
		StartupSection expected = getStartupSection();
		
		assertEquals(client.getVAppClient().getStartupSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyStartupSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/startupSection")
            .xmlFilePayload("/vApp/modifyStartupSection.xml", VCloudDirectorMediaType.STARTUP_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyStartupSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());
      
      StartupSection section = null; // getStartupSection().toBuilder()
//            .build();
		
		Task expected = modifyStartupSectionTask();
		
		assertEquals(client.getVAppClient().modifyStartupSection(vAppURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/virtualHardwareSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSection.xml", VCloudDirectorMediaType.VIRTUAL_HARDWARE_SECTION)
            .httpResponseBuilder().build());

      VirtualHardwareSection expected = getVirtualHardwareSection();

		assertEquals(client.getVAppClient().getVirtualHardwareSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/virtualHardwareSection")
            .xmlFilePayload("/vApp/modifyVirtualHardwareSection.xml", VCloudDirectorMediaType.VIRTUAL_HARDWARE_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      VirtualHardwareSection section = getVirtualHardwareSection().toBuilder()
            .build();

		Task expected = modifyVirtualHardwareSectionTask();
		
		assertEquals(client.getVAppClient().modifyVirtualHardwareSection(vAppURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionCpu() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/virtualHardwareSection/cpu")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionCpu.xml", VCloudDirectorMediaType.OVF_RASD_ITEM)
            .httpResponseBuilder().build());

         RASD expected = getVirtualHardwareSectionCpu();

         assertEquals(client.getVAppClient().getVirtualHardwareSectionCpu(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionCpu() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", vAppId + "/virtualHardwareSection/cpu")
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionCpu.xml", VCloudDirectorMediaType.OVF_RASD_ITEM)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionCpuTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

         RASD cpu = getVirtualHardwareSectionCpu(); // .toBuilder();
//               .build();

         Task expected = modifyVirtualHardwareSectionCpuTask();

         assertEquals(client.getVAppClient().modifyVirtualHardwareSectionCpu(vAppURI, cpu), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionDisks() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/virtualHardwareSection/disks")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionDisks.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .httpResponseBuilder().build());

         RasdItemsList expected = getVirtualHardwareSectionDisks();

         assertEquals(client.getVAppClient().getVirtualHardwareSectionDisks(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionDisks() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/virtualHardwareSection/disks")
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionDisks.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionDisksTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

         RasdItemsList disks = getVirtualHardwareSectionDisks().toBuilder()
               .build();

         Task expected = modifyVirtualHardwareSectionDisksTask();

         assertEquals(client.getVAppClient().modifyVirtualHardwareSectionDisks(vAppURI, disks), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionMedia() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/virtualHardwareSection/media")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionMedia.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .httpResponseBuilder().build());

      RasdItemsList expected = getVirtualHardwareSectionMedia();

      assertEquals(client.getVAppClient().getVirtualHardwareSectionMedia(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionMemory() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/virtualHardwareSection/memory")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionMemory.xml", VCloudDirectorMediaType.OVF_RASD_ITEM)
            .httpResponseBuilder().build());

         RASD expected = getVirtualHardwareSectionMemory();

         assertEquals(client.getVAppClient().getVirtualHardwareSectionMemory(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionMemory() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/virtualHardwareSection/memory")
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionMemory.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionMemoryTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

         RASD memory = getVirtualHardwareSectionCpu(); // .toBuilder();
//               .build();

         Task expected = modifyVirtualHardwareSectionMemoryTask();

         assertEquals(client.getVAppClient().modifyVirtualHardwareSectionMemory(vAppURI, memory), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionNetworkCards() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/virtualHardwareSection/networkCards")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionNetworkCards.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .httpResponseBuilder().build());

         RasdItemsList expected = getVirtualHardwareSectionNetworkCards();

         assertEquals(client.getVAppClient().getVirtualHardwareSectionNetworkCards(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionNetworkCards() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/virtualHardwareSection/networkCards")
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionNetworkCards.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionNetworkCardsTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

         RasdItemsList networkCards = getVirtualHardwareSectionNetworkCards().toBuilder()
               .build();

         Task expected = modifyVirtualHardwareSectionNetworkCardsTask();

         assertEquals(client.getVAppClient().modifyVirtualHardwareSectionNetworkCards(vAppURI, networkCards), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionSerialPorts() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/virtualHardwareSection/serialPorts")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionSerialPorts.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         RasdItemsList expected = getVirtualHardwareSectionSerialPorts();

         assertEquals(client.getVAppClient().getVirtualHardwareSectionSerialPorts(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionSerialPorts() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/virtualHardwareSection/serialPorts")
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionSerialPorts.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifyVirtualHardwareSectionSerialPortsTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

         RasdItemsList serialPorts = getVirtualHardwareSectionSerialPorts().toBuilder()
               .build();

         Task expected = modifyVirtualHardwareSectionSerialPortsTask();

         assertEquals(client.getVAppClient().modifyVirtualHardwareSectionSerialPorts(vAppURI, serialPorts), expected);
   }

   public static VApp getVApp() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static Task modifyVAppTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task deleteVAppTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task consolidateVAppTask() {
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

   public static Task recomposeVAppTask() {
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

   public static ControlAccessParams getControlAccessParams() {
      ControlAccessParams params = ControlAccessParams.builder()
            .build();

      return params;
   }

   public static GuestCustomizationSection getGuestCustomizationSection() {
      GuestCustomizationSection section = GuestCustomizationSection.builder()
            .build();

      return section;
   }

   public static Task modifyGuestCustomizationSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static LeaseSettingsSection getLeaseSettingsSection() {
      LeaseSettingsSection section = LeaseSettingsSection.builder()
            .build();

      return section;
   }

   public static Task modifyLeaseSettingsSectionTask() {
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

   public static Task modifyNetworkConfigSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static NetworkConnectionSection getNetworkConnectionSection() {
      NetworkConnectionSection section = NetworkConnectionSection.builder()
            .build();

      return section;
   }

   public static Task modifyNetworkConnectionSectionTask() {
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

   public static Task modifyOperatingSystemSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Owner getOwner() {
      Owner owner = Owner.builder()
            .build();

      return owner;
   }

   public static Task modifyOwnerTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static ProductSectionList getProductSections() {
      ProductSectionList sectionItems = ProductSectionList.builder()
            .build();

      return sectionItems;
   }

   public static Task modifyProductSectionsTask() {
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

   public static Task modifyStartupSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static VirtualHardwareSection getVirtualHardwareSection() {
      VirtualHardwareSection section = VirtualHardwareSection.builder()
            .build();

      return section;
   }

   public static Task modifyVirtualHardwareSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RASD getVirtualHardwareSectionCpu() {
      RASD cpu = RASD.builder()
            .build();

      return cpu;
   }

   public static Task modifyVirtualHardwareSectionCpuTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionDisks() {
      RasdItemsList disks = RasdItemsList.builder()
            .build();

      return disks;
   }

   public static Task modifyVirtualHardwareSectionDisksTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionMedia() {
      RasdItemsList media = RasdItemsList.builder()
            .build();

      return media;
   }

   public static RASD getVirtualHardwareSectionMemory() {
      RASD memory = RASD.builder()
            .build();

      return memory;
   }

   public static Task modifyVirtualHardwareSectionMemoryTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionNetworkCards() {
      RasdItemsList networkCards = RasdItemsList.builder()
            .build();

      return networkCards;
   }

   public static Task modifyVirtualHardwareSectionNetworkCardsTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionSerialPorts() {
      RasdItemsList serialPorts = RasdItemsList.builder()
            .build();

      return serialPorts;
   }

   public static Task modifyVirtualHardwareSectionSerialPortsTask() {
      return task("id", "name", "description", "status", "operation", "operationName", "startTime");
   }

   /** Used by other methods to create a custom {@link Task} object. */
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