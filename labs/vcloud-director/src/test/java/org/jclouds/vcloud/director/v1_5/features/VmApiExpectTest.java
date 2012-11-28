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
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.VmPendingQuestion;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswer;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.RasdItem;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.MediaInsertOrEjectParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RelocateParams;
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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;

/**
 * Allows us to test the {@link VmApi} via its side effects.
 *
 * @author grkvlt@apache.org
 */
@Test(groups = { "unit", "user" }, singleThreaded = true, testName = "VmApiExpectTest")
public class VmApiExpectTest extends VCloudDirectorAdminApiExpectTest {

   private static final String id = "dea05479-d7c1-4710-ba1a-a1a18cd0d455";
   private static final String vmId = "vm-dea05479-d7c1-4710-ba1a-a1a18cd0d455";
   private static final URI vmURI = URI.create(endpoint + "/vApp/" + vmId);
   private static final String vmUrn = "urn:vcloud:vm:" + id;

   @BeforeClass
   public void before() {
   }

   @Test(enabled = false)//TODO
   public void testGetVm() {
      VCloudDirectorApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", vmId)
               .acceptMedia(VCloudDirectorMediaType.VM)
               .httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vm/vm.xml", VCloudDirectorMediaType.VM)
               .httpResponseBuilder().build());

      Vm expected = getVm();

      assertEquals(api.getVmApi().get(vmURI), expected);
   }

   @Test(enabled = false)
   public void testEditVm() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId)
            .xmlFilePayload("/vm/editVm.xml", VCloudDirectorMediaType.VM)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vm/modifiedVm.xml", VCloudDirectorMediaType.VM)
            .httpResponseBuilder().build());

		Vm modified = getVm();
		modified.setName("new-name");
		modified.setDescription("New Description");

		Task expected = editVmTask();

		assertEquals(api.getVmApi().edit(vmURI, modified), expected);
   }

   @Test(enabled = false)
   public void testRemoveVm() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("DELETE", vmId)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vm/removeVmTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

		Task expected = removeVmTask();

		assertEquals(api.getVmApi().remove(vmURI), expected);
   }

   @Test(enabled = false)
   public void testConsolidateVm() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/consolidate")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vm/consolidateVmTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

		Task expected = consolidateVmTask();

		assertEquals(api.getVmApi().consolidate(vmURI), expected);
   }

   @Test(enabled = false)
   public void testDeploy() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/deploy")
            .xmlFilePayload("/vm/deployParams.xml", VCloudDirectorMediaType.DEPLOY_VAPP_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/deployTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      DeployVAppParams params = DeployVAppParams.builder()
            .build();

		Task expected = deployTask();

		assertEquals(api.getVmApi().deploy(vmURI, params), expected);
   }

   @Test(enabled = false)
   public void testDiscardSuspendedState() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/discardSuspendedState")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/discardSuspendedStateTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

		Task expected = discardSuspendedStateTask();

		assertEquals(api.getVmApi().discardSuspendedState(vmURI), expected);
   }

   @Test(enabled = false)
   public void testInstallVMwareTools() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/installVMwareTools")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/installVMwareToolsTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

		Task expected = installVMwareToolsTask();

		assertEquals(api.getVmApi().installVMwareTools(vmURI), expected);
   }

   @Test(enabled = false)
   public void testRelocate() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/relocate")
            .xmlFilePayload("/vApp/relocateParams.xml", VCloudDirectorMediaType.RELOCATE_VM_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/relocateTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      RelocateParams params = RelocateParams.builder()
            .build();

		Task expected = relocateTask();

		assertEquals(api.getVmApi().relocate(vmURI, params), expected);
   }

   @Test(enabled = false)
   public void testUndeploy() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/undeploy")
            .xmlFilePayload("/vApp/undeployParams.xml", VCloudDirectorMediaType.UNDEPLOY_VAPP_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/undeployTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      UndeployVAppParams params = UndeployVAppParams.builder()
            .build();

		Task expected = undeployTask();

		assertEquals(api.getVmApi().undeploy(vmURI, params), expected);
   }

   @Test(enabled = false)
   public void testUpgradeHardwareVersion() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/action/upgradeHardwareVersion")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/upgradeHardwareVersionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

		Task expected = upgradeHardwareVersionTask();

		assertEquals(api.getVmApi().upgradeHardwareVersion(vmURI), expected);
   }

   @Test(enabled = false)
   public void testPowerOff() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/power/action/powerOff")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/powerOffTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = powerOffTask();

      assertEquals(api.getVmApi().powerOff(vmURI), expected);
   }

   @Test(enabled = false)
   public void testPowerOn() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/power/action/powerOn")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/powerOnTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = powerOnTask();

      assertEquals(api.getVmApi().powerOn(vmURI), expected);
   }

   @Test(enabled = true)
   public void testReboot() {

      HttpRequest vmEntityRequest = HttpRequest.builder()
            .method("GET")
            .endpoint(URI.create(endpoint + "/entity/" + vmUrn))
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

      HttpResponse vmEntityResponse = HttpResponse.builder()
            .payload(payloadFromResourceWithContentType("/vm/vmEntity.xml", VCloudDirectorMediaType.ENTITY))
            .statusCode(200)
            .build();

      URI vmRebootUri = URI.create(endpoint + "/vApp/" + vmId + "/power/action/reboot");
      HttpRequest vmRebootRequest = HttpRequest.builder()
            .method("POST")
            .endpoint(vmRebootUri)
            .addHeader("Accept", "application/vnd.vmware.vcloud.task+xml")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

      HttpResponse vmRebootResponse = HttpResponse.builder()
            .payload(payloadFromResourceWithContentType("/vm/vmRebootTask.xml", VCloudDirectorMediaType.TASK))
            .statusCode(200)
            .build();

      VCloudDirectorApi vCloudDirectorApi = requestsSendResponses(
            loginRequest, sessionResponse,
            vmEntityRequest, vmEntityResponse,
            vmRebootRequest, vmRebootResponse
      );

      Task actual = vCloudDirectorApi.getVmApi().reboot(vmUrn);

      Task expected = rebootTask();

      assertEquals(actual, expected);
   }

   @Test(enabled = false)
   public void testReset() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/power/action/reset")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/resetTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = resetTask();

      assertEquals(api.getVmApi().reset(vmURI), expected);
   }

   @Test(enabled = false)
   public void testShutdown() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/power/action/shutdown")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/shutdownTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      Task expected = shutdownTask();

      assertEquals(api.getVmApi().shutdown(vmURI), expected);
   }

   @Test(enabled = false)
   public void testSuspend() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/power/action/suspend")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/suspend.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

		Task expected = suspendTask();

		assertEquals(api.getVmApi().suspend(vmURI), expected);
   }

   @Test(enabled = false)
   public void testGetGuestCustomizationSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/guestCustomizationSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getGuestCustomizationSection.xml", VCloudDirectorMediaType.GUEST_CUSTOMIZATION_SECTION)
            .httpResponseBuilder().build());

		GuestCustomizationSection expected = getGuestCustomizationSection();

		assertEquals(api.getVmApi().getGuestCustomizationSection(vmURI), expected);
   }

   @Test(enabled = false)
   public void testEditGuestCustomizationSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/guestCustomizationSection")
            .xmlFilePayload("/vApp/editGuestCustomizationSection.xml", VCloudDirectorMediaType.GUEST_CUSTOMIZATION_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editGuestCustomizationSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      GuestCustomizationSection section = getGuestCustomizationSection().toBuilder()
            .build();

      Task expected = editGuestCustomizationSectionTask();

      assertEquals(api.getVmApi().editGuestCustomizationSection(vmURI, section), expected);
   }

   @Test(enabled = false)
   public void testEjectMedia() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/media/action/ejectMedia")
            .xmlFilePayload("/vApp/ejectMediaParams.xml", VCloudDirectorMediaType.MEDIA_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/ejectMediaTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .build();

      Task expected = ejectMediaTask();

      assertEquals(api.getVmApi().ejectMedia(vmURI, params), expected);
   }

   @Test(enabled = false)
   public void testInsertMedia() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/media/action/insertMedia")
            .xmlFilePayload("/vApp/insertMediaParams.xml", VCloudDirectorMediaType.MEDIA_PARAMS)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/insertMediaTask.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

      MediaInsertOrEjectParams params = MediaInsertOrEjectParams.builder()
            .build();

      Task expected = insertMediaTask();

      assertEquals(api.getVmApi().insertMedia(vmURI, params), expected);
   }

   @Test(enabled = false)
   public void testGetNetworkConnectionSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/networkConnectionSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getNetworkConnectionSection.xml", VCloudDirectorMediaType.NETWORK_CONNECTION_SECTION)
            .httpResponseBuilder().build());

      NetworkConnectionSection expected = getNetworkConnectionSection();

         assertEquals(api.getVmApi().getNetworkConnectionSection(vmURI), expected);
   }

   @Test(enabled = false)
   public void testEditNetworkConnectionSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/networkConnectionSection")
            .xmlFilePayload("/vApp/editNetworkConnectionSection.xml", VCloudDirectorMediaType.NETWORK_CONNECTION_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editNetworkConnectionSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

		NetworkConnectionSection section = getNetworkConnectionSection().toBuilder()
		      .build();

		Task expected = editNetworkConnectionSectionTask();

		assertEquals(api.getVmApi().editNetworkConnectionSection(vmURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetOperatingSystemSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/operatingSystemSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getOperatingSystemSection.xml", VCloudDirectorMediaType.OPERATING_SYSTEM_SECTION)
            .httpResponseBuilder().build());

		OperatingSystemSection expected = getOperatingSystemSection();

		assertEquals(api.getVmApi().getOperatingSystemSection(vmURI), expected);
   }

   @Test(enabled = false)
   public void testEditOperatingSystemSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/operatingSystemSection")
            .xmlFilePayload("/vApp/editOperatingSystemSection.xml", VCloudDirectorMediaType.OPERATING_SYSTEM_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editOperatingSystemSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      OperatingSystemSection section = getOperatingSystemSection().toBuilder()
		      .build();

		Task expected = editOperatingSystemSectionTask();

		assertEquals(api.getVmApi().editOperatingSystemSection(vmURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetProductSections() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/productSections")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getProductSections.xml", VCloudDirectorMediaType.PRODUCT_SECTION_LIST)
            .httpResponseBuilder().build());

         ProductSectionList expected = getProductSections();

         assertEquals(api.getVmApi().getProductSections(vmURI), expected);
   }

   @Test(enabled = false)
   public void testEditProductSections() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/productSections")
            .xmlFilePayload("/vApp/editProductSections.xml", VCloudDirectorMediaType.PRODUCT_SECTION_LIST)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editProductSections.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         Task expected = editProductSectionsTask();

         assertEquals(api.getVmApi().editProductSections(vmURI, null), expected);
   }

   @Test(enabled = false)
   public void testGetPendingQuestion() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/question")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getPendingQuestion.xml", VCloudDirectorMediaType.VM_PENDING_QUESTION)
            .httpResponseBuilder().build());

         VmPendingQuestion expected = getPendingQuestion();

         assertEquals(api.getVmApi().getPendingQuestion(vmURI), expected);
   }

   @Test(enabled = false)
   public void testAnswerQuestion() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/question/action/answer")
            .xmlFilePayload("/vApp/answerQuestion.xml", VCloudDirectorMediaType.VM_PENDING_ANSWER)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .httpResponseBuilder().statusCode(204).build());

         VmQuestionAnswer answer = null; // = VmQuestionAnswer.builder();
//               .build;

         api.getVmApi().answerQuestion(vmURI, answer);
   }

   @Test(enabled = false)
   public void testGetRuntimeInfoSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/runtimeInfoSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getRuntimeInfoSection.xml", VCloudDirectorMediaType.RUNTIME_INFO_SECTION)
            .httpResponseBuilder().build());

      RuntimeInfoSection expected = getRuntimeInfoSection();

      assertEquals(api.getVmApi().getRuntimeInfoSection(vmURI), expected);
   }

   @Test(enabled = false)
   public void testGetScreenImage() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/screen")
            .acceptMedia(VCloudDirectorMediaType.ANY_IMAGE)
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .httpResponseBuilder()
            .headers(Multimaps.forMap(ImmutableMap.of("Content-Type", "image/png")))
            .message(new String(getScreenImage()))
            .build());

		byte[] expected = getScreenImage();

		assertEquals(api.getVmApi().getScreenImage(vmURI), expected);
   }

   @Test(enabled = false)
   public void testGetScreenTicket() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", vmId + "/screen/action/acquireTicket")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getScreenTicket.xml", VCloudDirectorMediaType.SCREEN_TICKET)
            .httpResponseBuilder().build());

		ScreenTicket expected = getScreenTicket();

		assertEquals(api.getVmApi().getScreenTicket(vmURI), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSection.xml", VCloudDirectorMediaType.VIRTUAL_HARDWARE_SECTION)
            .httpResponseBuilder().build());

      VirtualHardwareSection expected = getVirtualHardwareSection();

		assertEquals(api.getVmApi().getVirtualHardwareSection(vmURI), expected);
   }

   @Test(enabled = false)
   public void testEditVirtualHardwareSection() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/virtualHardwareSection")
            .xmlFilePayload("/vApp/editVirtualHardwareSection.xml", VCloudDirectorMediaType.VIRTUAL_HARDWARE_SECTION)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editVirtualHardwareSectionTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      VirtualHardwareSection section = getVirtualHardwareSection().toBuilder()
            .build();

		Task expected = editVirtualHardwareSectionTask();

		assertEquals(api.getVmApi().editVirtualHardwareSection(vmURI, section), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionCpu() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection/cpu")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionCpu.xml", VCloudDirectorMediaType.OVF_RASD_ITEM)
            .httpResponseBuilder().build());

      RasdItem expected = getVirtualHardwareSectionCpu();

         assertEquals(api.getVmApi().getVirtualHardwareSectionCpu(vmURI), expected);
   }

   @Test(enabled = false)
   public void testEditVirtualHardwareSectionCpu() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("", vmId + "/virtualHardwareSection/cpu")
            .xmlFilePayload("/vApp/editVirtualHardwareSectionCpu.xml", VCloudDirectorMediaType.OVF_RASD_ITEM)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editVirtualHardwareSectionCpuTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      RasdItem cpu = getVirtualHardwareSectionCpu(); // .toBuilder();
//               .build();

         Task expected = editVirtualHardwareSectionCpuTask();

         assertEquals(api.getVmApi().editVirtualHardwareSectionCpu(vmURI, cpu), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionDisks() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection/disks")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionDisks.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .httpResponseBuilder().build());

         RasdItemsList expected = getVirtualHardwareSectionDisks();

         assertEquals(api.getVmApi().getVirtualHardwareSectionDisks(vmURI), expected);
   }

   @Test(enabled = false)
   public void testEditVirtualHardwareSectionDisks() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/virtualHardwareSection/disks")
            .xmlFilePayload("/vApp/editVirtualHardwareSectionDisks.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editVirtualHardwareSectionDisksTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

         RasdItemsList disks = getVirtualHardwareSectionDisks().toBuilder()
               .build();

         Task expected = editVirtualHardwareSectionDisksTask();

         assertEquals(api.getVmApi().editVirtualHardwareSectionDisks(vmURI, disks), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionMedia() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection/media")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionMedia.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .httpResponseBuilder().build());

      RasdItemsList expected = getVirtualHardwareSectionMedia();

      assertEquals(api.getVmApi().getVirtualHardwareSectionMedia(vmURI), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionMemory() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection/memory")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionMemory.xml", VCloudDirectorMediaType.OVF_RASD_ITEM)
            .httpResponseBuilder().build());

      RasdItem expected = getVirtualHardwareSectionMemory();

         assertEquals(api.getVmApi().getVirtualHardwareSectionMemory(vmURI), expected);
   }

   @Test(enabled = false)
   public void testEditVirtualHardwareSectionMemory() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/virtualHardwareSection/memory")
            .xmlFilePayload("/vApp/editVirtualHardwareSectionMemory.xml", VCloudDirectorMediaType.VAPP)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editVirtualHardwareSectionMemoryTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

      RasdItem memory = getVirtualHardwareSectionCpu(); // .toBuilder();
//               .build();

         Task expected = editVirtualHardwareSectionMemoryTask();

         assertEquals(api.getVmApi().editVirtualHardwareSectionMemory(vmURI, memory), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionNetworkCards() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection/networkCards")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionNetworkCards.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .httpResponseBuilder().build());

         RasdItemsList expected = getVirtualHardwareSectionNetworkCards();

         assertEquals(api.getVmApi().getVirtualHardwareSectionNetworkCards(vmURI), expected);
   }

   @Test(enabled = false)
   public void testEditVirtualHardwareSectionNetworkCards() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/virtualHardwareSection/networkCards")
            .xmlFilePayload("/vApp/editVirtualHardwareSectionNetworkCards.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editVirtualHardwareSectionNetworkCardsTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

         RasdItemsList networkCards = getVirtualHardwareSectionNetworkCards().toBuilder()
               .build();

         Task expected = editVirtualHardwareSectionNetworkCardsTask();

         assertEquals(api.getVmApi().editVirtualHardwareSectionNetworkCards(vmURI, networkCards), expected);
   }

   @Test(enabled = false)
   public void testGetVirtualHardwareSectionSerialPorts() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", vmId + "/virtualHardwareSection/serialPorts")
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/getVirtualHardwareSectionSerialPorts.xml", VCloudDirectorMediaType.VAPP)
            .httpResponseBuilder().build());

         RasdItemsList expected = getVirtualHardwareSectionSerialPorts();

         assertEquals(api.getVmApi().getVirtualHardwareSectionSerialPorts(vmURI), expected);
   }

   @Test(enabled = false)
   public void testEditVirtualHardwareSectionSerialPorts() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", vmId + "/virtualHardwareSection/serialPorts")
            .xmlFilePayload("/vApp/editVirtualHardwareSectionSerialPorts.xml", VCloudDirectorMediaType.OVF_RASD_ITEMS_LIST)
            .acceptAnyMedia()
            .httpRequestBuilder().build(),
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/vApp/editVirtualHardwareSectionSerialPortsTask.xml", VCloudDirectorMediaType.TASK)
            .httpResponseBuilder().build());

         RasdItemsList serialPorts = getVirtualHardwareSectionSerialPorts().toBuilder()
               .build();

         Task expected = editVirtualHardwareSectionSerialPortsTask();

         assertEquals(api.getVmApi().editVirtualHardwareSectionSerialPorts(vmURI, serialPorts), expected);
   }

   public static Vm getVm() {
      // FIXME Does not match XML
      Vm vm = Vm.builder()
            .href(URI.create("https://mycloud.greenhousedata.com/api/vApp/vm-d0e2b6b9-4381-4ddc-9572-cdfae54059be"))
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

      return vm;
   }

   public static Task editVmTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task removeVmTask() {
      Task task = Task.builder()
            .build();

      return task;
   }

   public static Task consolidateVmTask() {
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

   public static Task recomposeVmTask() {
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
            .id("urn:vcloud:task:8d188b18-c2dd-4e29-a1b2-118e5f6a8276")
            .name("task")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/task/8d188b18-c2dd-4e29-a1b2-118e5f6a8276"))
            .link(Link.builder()
                  .rel(Link.Rel.TASK_CANCEL)
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/task/8d188b18-c2dd-4e29-a1b2-118e5f6a8276/action/cancel"))
                  .build())
            .org(Reference.builder()
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/org/4559b367-8af2-4ee1-8429-a0d39e7df3de"))
                  .name("jclouds")
                  .type(VCloudDirectorMediaType.ORG)
                  .build())
            .operation("Rebooting Virtual Machine ubuntu(dea05479-d7c1-4710-ba1a-a1a18cd0d455)")
            .operationName("vappRebootGuest")
            .progress(0)
            .startTime(dateService.cDateParse("Wed Nov 21 08:51:42 EST 2012"))
            .expiryTime(dateService.cDateParse("Tue Feb 19 08:51:42 EST 2013"))
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
