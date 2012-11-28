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

import com.google.common.net.HttpHeaders;
import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.dmtf.ovf.StartupSection;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.RasdItemsList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ScreenTicket;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VmPendingQuestion;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswer;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.RasdItem;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RecomposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.section.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.section.RuntimeInfoSection;
import org.jclouds.vcloud.director.v1_5.domain.section.VirtualHardwareSection;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

/**
 * Allows us to test the {@link VAppApi} via its side effects.
 *
 * @author grkvlt@apache.org
 */
@Test(groups = { "unit", "user" }, singleThreaded = true, testName = "VAppApiExpectTest")
public class VAppApiExpectTest extends VCloudDirectorAdminApiExpectTest {

   private static final String id = "6ef7767a-9522-4f8a-aa61-772ea1dc3145";
   private static final String vAppId = "vapp-" + id;
   private static final String vAppUrn = "urn:vcloud:vapp:" + id;

   private static final URI vAppURI = URI.create(endpoint + "/vApp/" + vAppId);


   @BeforeClass
   public void before() {
   }

   @Test(enabled = false)//TODO
   public void testGetVapp() {
      VCloudDirectorApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", vAppId)
               .acceptMedia(VCloudDirectorMediaType.VAPP)
               .httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vApp/vApp.xml", VCloudDirectorMediaType.VAPP)
               .httpResponseBuilder().build());

      VApp expected = getVApp();

      assertEquals(api.getVAppApi().get(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testEditVApp() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId)
            .xmlFilePayload("/vApp/editVApp.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/modifiedVapp.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

		VApp modified = getVApp();
		modified.setName("new-name");
		modified.setDescription("New Description");

		Task expected = editVAppTask();

		assertEquals(api.getVAppApi().edit(vAppURI, modified), expected);
   }

   @Test(enabled = false)
   public void testRemoveVApp() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("DELETE", vAppId)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/removeVAppTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

		Task expected = removeVAppTask();

		assertEquals(api.getVAppApi().remove(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testControlAccess() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
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

		assertEquals(api.getVAppApi().editControlAccess(vAppURI, params), expected);
   }

   @Test(enabled = false)
   public void testDeploy() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
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

		assertEquals(api.getVAppApi().deploy(vAppURI, params), expected);
   }

   @Test(enabled = false)
   public void testDiscardSuspendedState() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/discardSuspendedState")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/discardSuspendedStateTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

		Task expected = discardSuspendedStateTask();

		assertEquals(api.getVAppApi().discardSuspendedState(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testEnterMaintenanceMode() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/enterMaintenanceMode")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .httpResponseBuilder().statusCode(204).build());

		// TODO how to test?
		api.getVAppApi().enterMaintenanceMode(vAppURI);
   }

   @Test(enabled = false)
   public void testExitMaintenanceMode() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/action/exitMaintenanceMode")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .httpResponseBuilder().statusCode(204).build());

		// TODO how to test?
		api.getVAppApi().exitMaintenanceMode(vAppURI);
   }

   @Test(enabled = false)
   public void testRecomposeVApp() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
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

		assertEquals(api.getVAppApi().recompose(vAppURI, params), expected);
   }

   @Test(enabled = false)
   public void testUndeploy() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
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

		assertEquals(api.getVAppApi().undeploy(vAppURI, params), expected);
   }

   @Test(enabled = false)
   public void testPowerOff() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/power/action/powerOff")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/powerOffTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = powerOffTask();

      assertEquals(api.getVAppApi().powerOff(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testPowerOn() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/power/action/powerOn")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/powerOnTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = powerOnTask();

      assertEquals(api.getVAppApi().powerOn(vAppURI), expected);
   }


   @Test(enabled = true)
   public void testReboot() {

      URI vAppRebootUri = URI.create(endpoint + "/vApp/" + vAppId + "/power/action/reboot");

      HttpRequest vAppEntityRequest = HttpRequest.builder()
            .method("GET")
            .endpoint(URI.create(endpoint + "/entity/" + vAppUrn))
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

      HttpResponse vAppEntityResponse = HttpResponse.builder()
            .payload(payloadFromResourceWithContentType("/vapp/vAppEntity.xml", VCloudDirectorMediaType.ENTITY))
            .statusCode(200)
            .build();

      HttpRequest vAppRebootRequest = HttpRequest.builder()
            .method("POST")
            .endpoint(vAppRebootUri)
            .addHeader("Accept", "application/vnd.vmware.vcloud.task+xml")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

      HttpResponse vAppRebootResponse = HttpResponse.builder()
            .payload(payloadFromResourceWithContentType("/vapp/vAppRebootTask.xml", VCloudDirectorMediaType.TASK))
            .statusCode(200)
            .build();

      VCloudDirectorApi vCloudDirectorApi = requestsSendResponses(
            loginRequest, sessionResponse,
            vAppEntityRequest, vAppEntityResponse,
            vAppRebootRequest, vAppRebootResponse
      );

      Task actual = vCloudDirectorApi.getVAppApi().reboot(vAppUrn);
      Task expected = rebootTask();

      assertEquals(actual, expected);
   }

   @Test(enabled = false)
   public void testReset() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/power/action/reset")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/resetTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = resetTask();

      assertEquals(api.getVAppApi().reset(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testShutdown() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/power/action/shutdown")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/shutdownTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = shutdownTask();

      assertEquals(api.getVAppApi().shutdown(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testSuspend() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vAppId + "/power/action/suspend")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/suspend.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

		Task expected = suspendTask();

		assertEquals(api.getVAppApi().suspend(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetControlAccesss() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/controlAccess")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getAccessControl.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

      ControlAccessParams expected = getAccessControlParams();

      assertEquals(api.getVAppApi().getAccessControl(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetLeaseSettingsSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/leaseSettingsSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getLeaseSettingsSection.xml", VCloudDirectorMediaType.LEASE_SETTINGS_SECTION)
            .httpResponseBuilder().build());

      LeaseSettingsSection expected = getLeaseSettingsSection();

      assertEquals(api.getVAppApi().getLeaseSettingsSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testEditLeaseSettingsSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/leaseSettingsSection")
            .xmlFilePayload("/vApp/editLeaseSettingsSection.xml", VCloudDirectorMediaType.LEASE_SETTINGS_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editLeaseSettingsSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      LeaseSettingsSection section = getLeaseSettingsSection().toBuilder()
            .build();

		Task expected = editLeaseSettingsSectionTask();

		assertEquals(api.getVAppApi().editLeaseSettingsSection(vAppURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetNetworkConfigSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/networkConfigSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getNetworkConfigSection.xml", VCloudDirectorMediaType.NETWORK_CONFIG_SECTION)
            .httpResponseBuilder().build());

		NetworkConfigSection expected = getNetworkConfigSection();

		assertEquals(api.getVAppApi().getNetworkConfigSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testEditNetworkConfigSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/networkConfigSection")
            .xmlFilePayload("/vApp/editNetworkConfigSection.xml", VCloudDirectorMediaType.NETWORK_CONFIG_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editNetworkConfigSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

		NetworkConfigSection section = getNetworkConfigSection().toBuilder()
		      .build();

		Task expected = editNetworkConfigSectionTask();

		assertEquals(api.getVAppApi().editNetworkConfigSection(vAppURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetNetworkSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/networkSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getNetworkSection.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

		NetworkSection expected = getNetworkSection();

		assertEquals(api.getVAppApi().getNetworkSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testGetOwner() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/owner")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getOwner.xml", VCloudDirectorMediaType.OWNER)
            .httpResponseBuilder().build());

         Owner expected = getOwner();

         assertEquals(api.getVAppApi().getOwner(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testEditOwner() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/owner")
            .xmlFilePayload("/vApp/editOwner.xml", VCloudDirectorMediaType.OWNER)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .httpResponseBuilder().statusCode(204).build());

      Owner owner = Owner.builder()
            .build();

		api.getVAppApi().editOwner(vAppURI, owner);
   }

   @Test(enabled = false)
   public void testGetProductSections() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/productSections")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getProductSections.xml", VCloudDirectorMediaType.PRODUCT_SECTION_LIST)
            .httpResponseBuilder().build());

         ProductSectionList expected = getProductSections();

         assertEquals(api.getVAppApi().getProductSections(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testEditProductSections() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/productSections")
            .xmlFilePayload("/vApp/editProductSections.xml", VCloudDirectorMediaType.PRODUCT_SECTION_LIST)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editProductSections.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         Task expected = editProductSectionsTask();

         assertEquals(api.getVAppApi().editProductSections(vAppURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetStartupSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vAppId + "/startupSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getStartupSection.xml", VCloudDirectorMediaType.STARTUP_SECTION)
            .httpResponseBuilder().build());

		StartupSection expected = getStartupSection();

		assertEquals(api.getVAppApi().getStartupSection(vAppURI), expected);
   }

   @Test(enabled = false)
   public void testEditStartupSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vAppId + "/startupSection")
            .xmlFilePayload("/vApp/editStartupSection.xml", VCloudDirectorMediaType.STARTUP_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editStartupSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      StartupSection section = null; // getStartupSection().toBuilder()
//            .build();

		Task expected = editStartupSectionTask();

		assertEquals(api.getVAppApi().editStartupSection(vAppURI, section), expected);
   }

   public static VApp getVApp() {
      // FIXME Does not match XML
      VApp vApp = VApp.builder()
            .href(URI.create("https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be"))
//            .link(Link.builder()
//                     .href(URI.create())
//                     .build())
            .build();

//      <Link rel="power:powerOn" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/power/action/powerOn"/>
//      <Link rel="deploy" type="application/vnd.vmware.vcloud.deployVAppParams+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/action/deploy"/>
//      <Link rel="down" type="application/vnd.vmware.vcloud.vAppNetwork+xml" name="orgNet-cloudsoft-External" href="https://mycloud.greenhousedata.com/api/network/2a2e2da4-446a-4ebc-a086-06df7c9570f0"/>
//      <Link rel="down" type="application/vnd.vmware.vcloud.controlAccess+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/controlAccess/"/>
//      <Link rel="controlAccess" type="application/vnd.vmware.vcloud.controlAccess+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/action/controlAccess"/>
//      <Link rel="recompose" type="application/vnd.vmware.vcloud.recomposeVAppParams+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/action/recomposeVApp"/>
//      <Link rel="up" type="application/vnd.vmware.vcloud.vdc+xml" href="https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f"/>
//      <Link rel="edit" type="application/vnd.vmware.vcloud.vApp+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be"/>
//      <Link rel="remove" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be"/>
//      <Link rel="down" type="application/vnd.vmware.vcloud.owner+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/owner"/>
//      <Link rel="down" type="application/vnd.vmware.vcloud.metadata+xml" href="https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/metadata"/>

      return vApp;
   }

   public static Task editVAppTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task removeVAppTask() {
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
      return Task.builder()
            .id("urn:vcloud:task:5bc25a1b-79cb-4904-b53f-4ca09088e434")
            .name("task")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/task/5bc25a1b-79cb-4904-b53f-4ca09088e434"))
            .link(Link.builder()
                  .rel(Link.Rel.TASK_CANCEL)
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/task/5bc25a1b-79cb-4904-b53f-4ca09088e434/action/cancel"))
                  .build())
            .org(Reference.builder()
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/org/4559b367-8af2-4ee1-8429-a0d39e7df3de"))
                  .name("jclouds")
                  .type(VCloudDirectorMediaType.ORG)
                  .build())
            .operation("Rebooting Virtual Machine damn small(b7e995a7-1468-4873-af39-bc703feefd63)")
            .operationName("vappReboot")
            .progress(0)
            .startTime(dateService.cDateParse("Thu Oct 25 13:39:25 EDT 2012"))
            .expiryTime(dateService.cDateParse("Wed Jan 23 13:39:25 EST 2013"))
            .status(Task.Status.RUNNING)
            .type(VCloudDirectorMediaType.TASK)
            .build();
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

   public static ControlAccessParams getAccessControlParams() {
      ControlAccessParams params = ControlAccessParams.builder()
            .build();

      return params;
   }

   public static GuestCustomizationSection getGuestCustomizationSection() {
      GuestCustomizationSection section = GuestCustomizationSection.builder()
            .build();

      return section;
   }

   public static Task editGuestCustomizationSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static LeaseSettingsSection getLeaseSettingsSection() {
      LeaseSettingsSection section = LeaseSettingsSection.builder()
            .build();

      return section;
   }

   public static Task editLeaseSettingsSectionTask() {
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

   public static Task editNetworkConfigSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static NetworkConnectionSection getNetworkConnectionSection() {
      NetworkConnectionSection section = NetworkConnectionSection.builder()
            .build();

      return section;
   }

   public static Task editNetworkConnectionSectionTask() {
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

   public static Task editOperatingSystemSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Owner getOwner() {
      Owner owner = Owner.builder()
            .build();

      return owner;
   }

   public static Task editOwnerTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static ProductSectionList getProductSections() {
      ProductSectionList sectionItems = ProductSectionList.builder()
            .build();

      return sectionItems;
   }

   public static Task editProductSectionsTask() {
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

   public static Task editStartupSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static VirtualHardwareSection getVirtualHardwareSection() {
      VirtualHardwareSection section = VirtualHardwareSection.builder()
            .build();

      return section;
   }

   public static Task editVirtualHardwareSectionTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItem getVirtualHardwareSectionCpu() {
      RasdItem cpu = RasdItem.builder()
            .build();

      return cpu;
   }

   public static Task editVirtualHardwareSectionCpuTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionDisks() {
      RasdItemsList disks = RasdItemsList.builder()
            .build();

      return disks;
   }

   public static Task editVirtualHardwareSectionDisksTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionMedia() {
      RasdItemsList media = RasdItemsList.builder()
            .build();

      return media;
   }

   public static RasdItem getVirtualHardwareSectionMemory() {
      RasdItem memory = RasdItem.builder()
            .build();

      return memory;
   }

   public static Task editVirtualHardwareSectionMemoryTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionNetworkCards() {
      RasdItemsList networkCards = RasdItemsList.builder()
            .build();

      return networkCards;
   }

   public static Task editVirtualHardwareSectionNetworkCardsTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static RasdItemsList getVirtualHardwareSectionSerialPorts() {
      RasdItemsList serialPorts = RasdItemsList.builder()
            .build();

      return serialPorts;
   }

   public static Task editVirtualHardwareSectionSerialPortsTask() {
      return task("id", "name", "description", "status", "operation", "operationName", "startTime");
   }

   /** Used by other methods to add a custom {@link Task} object. */
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
