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

import static org.testng.Assert.*;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Allows us to test the {@link VAppClient} via its side effects.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "unit", "user", "vapp" }, singleThreaded = true, testName = "VAppClientExpectTest")
public class VAppClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {
   
   private URI vAppURI = URI.create(endpoint + "/vApp/e9cd3387-ac57-4d27-a481-9bee75e0690f");
   
   @BeforeClass
   public void before() {
   }
   
   @Test
   public void testGetVapp() {
      VCloudDirectorClient client = orderedRequestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/vApp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vapp/vapp.xml", VCloudDirectorMediaType.VAPP)
               .httpResponseBuilder().build());
      
      VApp expected = getVApp();

      assertEquals(client.getVAppClient().getVApp(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVApp() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyVApp.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyVapp.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyVApp();

         assertEquals(client.getVAppClient().modifyVApp(vAppURI, expected), expected);
   }

   @Test(enabled = false)
   public void testDeleteVApp() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/deleteVApp.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/deleteVApp.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = deleteVApp();

         assertEquals(client.getVAppClient().deleteVApp(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testConsolidateVApp() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/consolidateVApp.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/consolidateVApp.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = consolidateVApp();

         assertEquals(client.getVAppClient().consolidateVApp(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testControlAccess() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/controlAccess.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/controlAccess.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = controlAccess();

         assertEquals(client.getVAppClient().controlAccess(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testDeploy() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/deploy.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/deploy.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = deploy();

         assertEquals(client.getVAppClient().deploy(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testDiscardSuspendedState() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/discardSuspendedState.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/discardSuspendedState.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = discardSuspendedState();

         assertEquals(client.getVAppClient().discardSuspendedState(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testEnterMaintenanceMode() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/enterMaintenanceMode.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/enterMaintenanceMode.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = enterMaintenanceMode();

         // TODO how to test?
         client.getVAppClient().enterMaintenanceMode(vAppURI);
   }

   @Test(enabled = false)
   public void testExitMaintenanceMode() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/exitMaintenanceMode.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/exitMaintenanceMode.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = exitMaintenanceMode();

         // TODO how to test?
         client.getVAppClient().exitMaintenanceMode(vAppURI);
   }

   @Test(enabled = false)
   public void testInstallVMwareTools() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/installVMwareTools.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/installVMwareTools.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = installVMwareTools();

         assertEquals(client.getVAppClient().installVMwareTools(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testRecomposeVApp() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/recomposeVApp.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/recomposeVApp.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = recomposeVApp();

         assertEquals(client.getVAppClient().recomposeVApp(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testRelocate() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/relocate.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/relocate.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = relocate();

         assertEquals(client.getVAppClient().relocate(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testUndeploy() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/undeploy.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/undeploy.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = undeploy();

         assertEquals(client.getVAppClient().undeploy(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testUpgradeHardwareVersion() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/upgradeHardwareVersion.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/upgradeHardwareVersion.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = upgradeHardwareVersion();

         assertEquals(client.getVAppClient().upgradeHardwareVersion(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testPowerOff() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/powerOff.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/powerOff.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = powerOff();

         assertEquals(client.getVAppClient().powerOff(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testPowerOn() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/powerOn.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/powerOn.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = powerOn();

         assertEquals(client.getVAppClient().powerOn(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testReboot() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/reboot.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/reboot.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = reboot();

         assertEquals(client.getVAppClient().reboot(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testReset() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/reset.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/reset.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = reset();

         assertEquals(client.getVAppClient().reset(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testShutdown() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/shutdown.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/shutdown.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = shutdown();

         assertEquals(client.getVAppClient().shutdown(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testSuspend() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/suspend.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/suspend.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = suspend();

         assertEquals(client.getVAppClient().suspend(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetControlAccesss() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getControlAccess.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getControlAccess.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

      ControlAccessParams expected = getControlAccessParams();

         assertEquals(client.getVAppClient().getControlAccess(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetGuestCustomizationSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getGuestCustomizationSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getGuestCustomizationSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getGuestCustomizationSection();

         assertEquals(client.getVAppClient().getGuestCustomizationSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyGuestCustomizationSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyGuestCustomizationSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyGuestCustomizationSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyGuestCustomizationSection();

         assertEquals(client.getVAppClient().modifyGuestCustomizationSection(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetLeaseSettingsSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getLeaseSettingsSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getLeaseSettingsSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getLeaseSettingsSection();

         assertEquals(client.getVAppClient().getLeaseSettingsSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyLeaseSettingsSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyLeaseSettingsSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyLeaseSettingsSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyLeaseSettingsSection();

         assertEquals(client.getVAppClient().modifyLeaseSettingsSection(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testEjectMedia() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/ejectMedia.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/ejectMedia.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = ejectMedia();

         assertEquals(client.getVAppClient().ejectMedia(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testInsertMedia() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/insertMedia.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/insertMedia.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = insertMedia();

         assertEquals(client.getVAppClient().insertMedia(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetNetworkConfigSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getNetworkConfigSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getNetworkConfigSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getNetworkConfigSection();

         assertEquals(client.getVAppClient().getNetworkConfigSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyNetworkConfigSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyNetworkConfigSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyNetworkConfigSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyNetworkConfigSection();

         assertEquals(client.getVAppClient().modifyNetworkConfigSection(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetNetworkConnectionSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getNetworkConnectionSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getNetworkConnectionSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getNetworkConnectionSection();

         assertEquals(client.getVAppClient().getNetworkConnectionSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyNetworkConnectionSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyNetworkConnectionSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyNetworkConnectionSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyNetworkConnectionSection();

         assertEquals(client.getVAppClient().modifyNetworkConnectionSection(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetNetworkSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getNetworkSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getNetworkSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getNetworkSection();

         assertEquals(client.getVAppClient().getNetworkSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetOperatingSystemSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getOperatingSystemSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getOperatingSystemSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getOperatingSystemSection();

         assertEquals(client.getVAppClient().getOperatingSystemSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyOperatingSystemSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyOperatingSystemSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyOperatingSystemSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyOperatingSystemSection();

         assertEquals(client.getVAppClient().modifyOperatingSystemSection(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetOwner() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getOwner.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getOwner.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getOwner();

         assertEquals(client.getVAppClient().getOwner(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyOwner() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyOwner.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyOwner.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyOwner();

         assertEquals(client.getVAppClient().modifyOwner(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetProductSections() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getProductSections.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getProductSections.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getProductSections();

         assertEquals(client.getVAppClient().getProductSections(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyProductSections() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyProductSections.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyProductSections.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyProductSections();

         assertEquals(client.getVAppClient().modifyProductSections(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetPendingQuestion() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getPendingQuestion.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getPendingQuestion.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getPendingQuestion();

         assertEquals(client.getVAppClient().getPendingQuestion(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testAnswerQuestion() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/answerQuestion.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/answerQuestion.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = answerQuestion();

         client.getVAppClient().answerQuestion(vAppURI, null);
   }

   @Test(enabled = false)
   public void testGetRuntimeInfoSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getRuntimeInfoSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getRuntimeInfoSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getRuntimeInfoSection();

         assertEquals(client.getVAppClient().getRuntimeInfoSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetScreenImage() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getScreenImage.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getScreenImage.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getScreenImage();

         assertEquals(client.getVAppClient().getScreenImage(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetScreenTicket() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getScreenTicket.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getScreenTicket.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getScreenTicket();

         assertEquals(client.getVAppClient().getScreenTicket(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetStartupSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getStartupSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getStartupSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getStartupSection();

         assertEquals(client.getVAppClient().getStartupSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyStartupSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyStartupSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyStartupSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyStartupSection();

         assertEquals(client.getVAppClient().modifyStartupSection(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getVirtualHardwareSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getVirtualHardwareSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getVirtualHardwareSection();

         assertEquals(client.getVAppClient().getVirtualHardwareSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSection() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyVirtualHardwareSection.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyVirtualHardwareSection.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyVirtualHardwareSection();

         assertEquals(client.getVAppClient().modifyVirtualHardwareSection(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionCpu() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getVirtualHardwareSectionCpu.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getVirtualHardwareSectionCpu.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getVirtualHardwareSectionCpu();

         assertEquals(client.getVAppClient().getVirtualHardwareSectionCpu(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionCpu() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyVirtualHardwareSectionCpu.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyVirtualHardwareSectionCpu.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyVirtualHardwareSectionCpu();

         assertEquals(client.getVAppClient().modifyVirtualHardwareSectionCpu(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionDisks() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getVirtualHardwareSectionDisks.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getVirtualHardwareSectionDisks.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getVirtualHardwareSectionDisks();

         assertEquals(client.getVAppClient().getVirtualHardwareSectionDisks(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionDisks() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyVirtualHardwareSectionDisks.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyVirtualHardwareSectionDisks.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyVirtualHardwareSectionDisks();

         assertEquals(client.getVAppClient().modifyVirtualHardwareSectionDisks(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionMedia() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getVirtualHardwareSectionMedia.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getVirtualHardwareSectionMedia.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getVirtualHardwareSectionMedia();

         assertEquals(client.getVAppClient().getVirtualHardwareSectionMedia(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionMemory() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getVirtualHardwareSectionMemory.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getVirtualHardwareSectionMemory.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getVirtualHardwareSectionMemory();

         assertEquals(client.getVAppClient().getVirtualHardwareSectionMemory(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionMemory() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyVirtualHardwareSectionMemory.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyVirtualHardwareSectionMemory.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyVirtualHardwareSectionMemory();

         assertEquals(client.getVAppClient().modifyVirtualHardwareSectionMemory(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionNetworkCards() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getVirtualHardwareSectionNetworkCards.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getVirtualHardwareSectionNetworkCards.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getVirtualHardwareSectionNetworkCards();

         assertEquals(client.getVAppClient().getVirtualHardwareSectionNetworkCards(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionNetworkCards() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyVirtualHardwareSectionNetworkCards.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyVirtualHardwareSectionNetworkCards.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyVirtualHardwareSectionNetworkCards();

         assertEquals(client.getVAppClient().modifyVirtualHardwareSectionNetworkCards(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionSerialPorts() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/getVirtualHardwareSectionSerialPorts.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/getVirtualHardwareSectionSerialPorts.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = getVirtualHardwareSectionSerialPorts();

         assertEquals(client.getVAppClient().getVirtualHardwareSectionSerialPorts(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testModifyVirtualHardwareSectionSerialPorts() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("", "/vapp/e9cd3387-ac57-4d27-a481-9bee75e0690f")
            .xmlFilePayload("/vapp/modifyVirtualHardwareSectionSerialPorts.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vdc/modifyVirtualHardwareSectionSerialPorts.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         VApp expected = modifyVirtualHardwareSectionSerialPorts();

         assertEquals(client.getVAppClient().modifyVirtualHardwareSectionSerialPorts(vAppURI, null), expected);
   }

   public static VApp getVApp() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyVApp() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp deleteVApp() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp consolidateVApp() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp controlAccess() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp deploy() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp discardSuspendedState() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp enterMaintenanceMode() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp exitMaintenanceMode() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp installVMwareTools() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp recomposeVApp() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp relocate() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp undeploy() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp upgradeHardwareVersion() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp powerOff() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp powerOn() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp reboot() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp reset() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp shutdown() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp suspend() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static ControlAccessParams getControlAccessParams() {
      ControlAccessParams params = ControlAccessParams.builder()
            .build();

      return params;
   }

   public static VApp getGuestCustomizationSection() {
      Guest vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyGuestCustomizationSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getLeaseSettingsSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyLeaseSettingsSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp ejectMedia() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp insertMedia() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getMetadataClient() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getNetworkConfigSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyNetworkConfigSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getNetworkConnectionSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyNetworkConnectionSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getNetworkSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getOperatingSystemSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyOperatingSystemSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getOwner() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyOwner() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getProductSections() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyProductSections() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getPendingQuestion() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp answerQuestion() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getRuntimeInfoSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getScreenImage() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getScreenTicket() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getStartupSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyStartupSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getVirtualHardwareSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyVirtualHardwareSection() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getVirtualHardwareSectionCpu() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyVirtualHardwareSectionCpu() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getVirtualHardwareSectionDisks() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyVirtualHardwareSectionDisks() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getVirtualHardwareSectionMedia() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getVirtualHardwareSectionMemory() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyVirtualHardwareSectionMemory() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getVirtualHardwareSectionNetworkCards() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyVirtualHardwareSectionNetworkCards() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp getVirtualHardwareSectionSerialPorts() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }

   public static VApp modifyVirtualHardwareSectionSerialPorts() {
      VApp vApp = VApp.builder()
            .build();

      return vApp;
   }
}