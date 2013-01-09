/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CUSTOMIZATION_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ERROR;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.LEASE_SETTINGS_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK_CONFIG_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VAPP_TEMPLATE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.section.CustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests the request/response behavior of {@link org.jclouds.vcloud.director.v1_5.features.VAppTemplateApi}
 *
 * @author Adam Lowe
 */
@Test(groups = { "unit", "user" }, testName = "VAppTemplateApiExpectTest")
public class VAppTemplateApiExpectTest extends VCloudDirectorAdminApiExpectTest {

   public VAppTemplateApiExpectTest() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }
   
   @Test
   public void testVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId).acceptMedia(VAPP_TEMPLATE).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/vAppTemplate.xml", VAPP_TEMPLATE).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId).xmlFilePayload("/vapptemplate/vAppTemplate.xml", VAPP_TEMPLATE).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("DELETE", templateId).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateApi();

      assertNotNull(api);
      VAppTemplate template = api.get(uri);

      assertEquals(template, exampleTemplate());

      Task task = api.edit(uri, exampleTemplate());
      assertNotNull(task);

      task = api.remove(uri);
      assertNotNull(task);
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testErrorGetVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId).acceptMedia(VAPP_TEMPLATE).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateApi();

      api.get(uri);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testErrorEditVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId).xmlFilePayload("/vapptemplate/vAppTemplate.xml", VAPP_TEMPLATE).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateApi();

      api.edit(uri, exampleTemplate());
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testRemoveMissingVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("DELETE", templateId).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateApi();
      
      api.remove(uri);
   }

   public void testDisableDownloadVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateApi api = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/disableDownload").httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().httpResponseBuilder().build()
      ).getVAppTemplateApi();

      assertNotNull(api);
      api.disableDownload(uri);
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testDisableDownloadMissingVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/disableDownload").httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateApi();

      api.disableDownload(uri);
   }

   public void testEnableDownloadVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateApi api = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/enableDownload").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateApi();

      assertNotNull(api);
      Task task = api.enableDownload(uri);
      assertNotNull(task);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testEnableDownloadMissingVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/enableDownload").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateApi();

      api.enableDownload(uri);
   }

   public void testErrorGetCustomizationSection() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/customizationSection").acceptMedia(CUSTOMIZATION_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateApi();

      assertNull(api.getCustomizationSection(uri));
   }
   
   public void testLeaseSettingsSection() throws ParseException {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/leaseSettingsSection").acceptMedia(LEASE_SETTINGS_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/leaseSettingsSection.xml", LEASE_SETTINGS_SECTION).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/leaseSettingsSection").xmlFilePayload("/vapptemplate/leaseSettingsSection.xml", LEASE_SETTINGS_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateApi();

      assertNotNull(api);
      LeaseSettingsSection section = api.getLeaseSettingsSection(uri);

      assertEquals(section, exampleLeaseSettingsSection());

      Task task = api.editLeaseSettingsSection(uri, exampleLeaseSettingsSection());
      assertNotNull(task);
   }

   public void testErrorGetLeaseSettingsSection() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/leaseSettingsSection").acceptMedia(LEASE_SETTINGS_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateApi();

      assertNull(api.getLeaseSettingsSection(uri));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testErrorEditLeaseSettingsSection() throws ParseException {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/leaseSettingsSection").xmlFilePayload("/vapptemplate/leaseSettingsSection.xml", LEASE_SETTINGS_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateApi();

      api.editLeaseSettingsSection(uri, exampleLeaseSettingsSection());
   }


   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testErrorGetNetworkConfigSection() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateApi api = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/networkConfigSection").acceptMedia(NETWORK_CONFIG_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateApi();

      api.getNetworkConfigSection(uri);
   }

   private VAppTemplate exampleTemplate() {
      Link aLink = Link.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/vdc/d16d333b-e3c0-4176-845d-a5ee6392df07"))
            .type("application/vnd.vmware.vcloud.vdc+xml").rel("up").build();
      Link bLink = Link.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9"))
            .rel("remove").build();

      Owner owner = Owner.builder().type("application/vnd.vmware.vcloud.owner+xml").user(Reference.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/967d317c-4273-4a95-b8a4-bf63b78e9c69")).name("x@jclouds.org").type("application/vnd.vmware.admin.user+xml").build()).build();

      LeaseSettingsSection leaseSettings = LeaseSettingsSection.builder().type("application/vnd.vmware.vcloud.leaseSettingsSection+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9/leaseSettingsSection/"))
            .info("Lease settings section")
            .links(ImmutableSet.of(Link.builder().rel("edit").type("application/vnd.vmware.vcloud.leaseSettingsSection+xml")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9/leaseSettingsSection/")).build()))
            .storageLeaseInSeconds(0)
            .required(false)
            .build();
      CustomizationSection customizationSection = CustomizationSection.builder()
            .type("application/vnd.vmware.vcloud.customizationSection+xml")
            .info("VApp template customization section")
            .customizeOnInstantiate(true)
            .href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9/customizationSection/"))
            .required(false)
            .build();

      return VAppTemplate.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9"))
            .links(ImmutableSet.of(aLink, bLink))
            .children(ImmutableSet.<Vm>of())
            .type("application/vnd.vmware.vcloud.vAppTemplate+xml")
            .description("For testing")
            .id("urn:vcloud:vapptemplate:ef4415e6-d413-4cbb-9262-f9bbec5f2ea9")
            .name("ubuntu10")
            .sections(ImmutableSet.of(leaseSettings, customizationSection))
            .status(-1)
            .owner(owner)
            .ovfDescriptorUploaded(true)
            .goldMaster(false)
            .build();
   }

   private GuestCustomizationSection exampleGuestCustomizationSection() {
      return GuestCustomizationSection.builder()
            .links(ImmutableSet.of(
                  Link.builder().href(URI.create("http://vcloud.example.com/api/v1.5/vAppTemplate/vAppTemplate-12/guestCustomizationSection/"))
                        .type("application/vnd.vmware.vcloud.guestCustomizationSection+xml").rel("edit").build()
            ))
            .enabled(false)
            .changeSid(false)
            .virtualMachineId("4")
            .joinDomainEnabled(false)
            .useOrgSettings(false)
            .adminPasswordEnabled(false)
            .adminPasswordAuto(true)
            .resetPasswordRequired(false)
            .type("application/vnd.vmware.vcloud.guestCustomizationSection+xml")
            .info("Specifies Guest OS Customization Settings")
            .computerName("ubuntu10-x86")
            .customizationScript("ls")
            .href(URI.create("http://vcloud.example.com/api/v1.5/vAppTemplate/vAppTemplate-12/guestCustomizationSection/"))
            .required(false)
            .build();
   }

   private LeaseSettingsSection exampleLeaseSettingsSection() throws ParseException {
      SimpleDateFormat iso8601SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
      return LeaseSettingsSection.builder().type("application/vnd.vmware.vcloud.leaseSettingsSection+xml")
            .href(URI.create("http://vcloud.example.com/api/v1.5/vAppTemplate/vAppTemplate-7/leaseSettingsSection/"))
            .info("VApp lease settings")
            .links(ImmutableSet.of(Link.builder().rel("edit").type("application/vnd.vmware.vcloud.leaseSettingsSection+xml")
                  .href(URI.create("http://vcloud.example.com/api/v1.5/vAppTemplate/vAppTemplate-7/leaseSettingsSection/")).build()))
            .storageLeaseInSeconds(3600)
            .deploymentLeaseInSeconds(3600)
                  // note adjusted to UTC
            .deploymentLeaseExpiration(iso8601SimpleDateFormat.parse("2010-01-21T21:50:59.764"))
            .required(false)
            .build();
   }

}
