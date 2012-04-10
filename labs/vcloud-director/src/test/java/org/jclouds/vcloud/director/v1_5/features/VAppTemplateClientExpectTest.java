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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ANY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CUSTOMIZATION_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ERROR;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.GUEST_CUSTOMIZATION_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.LEASE_SETTINGS_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.METADATA;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.METADATA_ENTRY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK_CONFIG_SECTION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.RELOCATE_TEMPLATE;
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
import org.jclouds.vcloud.director.v1_5.domain.CustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.FirewallRule;
import org.jclouds.vcloud.director.v1_5.domain.FirewallRuleProtocols;
import org.jclouds.vcloud.director.v1_5.domain.FirewallService;
import org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.IpRange;
import org.jclouds.vcloud.director.v1_5.domain.IpRanges;
import org.jclouds.vcloud.director.v1_5.domain.IpScope;
import org.jclouds.vcloud.director.v1_5.domain.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.NatOneToOneVmRule;
import org.jclouds.vcloud.director.v1_5.domain.NatRule;
import org.jclouds.vcloud.director.v1_5.domain.NatService;
import org.jclouds.vcloud.director.v1_5.domain.Network.FenceMode;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.NetworkFeatures;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VAppNetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests the request/response behavior of {@link org.jclouds.vcloud.director.v1_5.features.VAppTemplateClient}
 *
 * @author Adam Lowe
 */
@Test(groups = { "unit", "user", "vapptemplate" }, testName = "VAppTemplateClientExpectTest")
public class VAppTemplateClientExpectTest extends VCloudDirectorAdminClientExpectTest {

   public VAppTemplateClientExpectTest() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }
   
   @Test
   public void testVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId).acceptMedia(VAPP_TEMPLATE).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/vAppTemplate.xml", VAPP_TEMPLATE).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId).xmlFilePayload("/vapptemplate/vAppTemplate.xml", VAPP_TEMPLATE).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("DELETE", templateId).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      VAppTemplate template = client.getVAppTemplate(uri);

      assertEquals(template, exampleTemplate());

      Task task = client.modifyVAppTemplate(uri, exampleTemplate());
      assertNotNull(task);

      task = client.deleteVappTemplate(uri);
      assertNotNull(task);
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testErrorGetVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId).acceptMedia(VAPP_TEMPLATE).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateClient();

      client.getVAppTemplate(uri);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testErrorEditVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId).xmlFilePayload("/vapptemplate/vAppTemplate.xml", VAPP_TEMPLATE).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateClient();

      client.modifyVAppTemplate(uri, exampleTemplate());
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testDeleteMissingVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("DELETE", templateId).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateClient();
      
      client.deleteVappTemplate(uri);
   }
   
   public void testConsolidateVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/consolidate").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      Task task = client.consolidateVm(uri);
      assertNotNull(task);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testConsolidateMissingVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/consolidate").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateClient();

      client.consolidateVm(uri);
   }

   public void testDisableDownloadVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/disableDownload").httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      client.disableDownload(uri);
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testDisableDownloadMissingVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/disableDownload").httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateClient();

      client.disableDownload(uri);
   }

   public void testEnableDownloadVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/enableDownload").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      Task task = client.enableDownload(uri);
      assertNotNull(task);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testEnableDownloadMissingVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/enableDownload").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateClient();

      client.enableDownload(uri);
   }
   
   public void testRelocateVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/relocate").xmlFilePayload("/vapptemplate/relocateParams.xml", RELOCATE_TEMPLATE).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);

      Reference datastore = Reference.builder().href(URI.create("https://vcloud.example.com/api/admin/extension/datastore/607")).build();
      RelocateParams params = RelocateParams.builder().datastore(datastore).build();

      Task task = client.relocateVm(uri, params);
      assertNotNull(task);
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testRelocateMissingVAppTemplate() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/relocate").xmlFilePayload("/vapptemplate/relocateParams.xml", RELOCATE_TEMPLATE).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateClient();

      Reference datastore = Reference.builder().href(URI.create("https://vcloud.example.com/api/admin/extension/datastore/607")).build();
      RelocateParams params = RelocateParams.builder().datastore(datastore).build();

      client.relocateVm(uri, params);
   }

   @Test
   public void testCustomizationSection() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/customizationSection").acceptMedia(CUSTOMIZATION_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/customizationSection.xml", CUSTOMIZATION_SECTION).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/customizationSection").xmlFilePayload("/vapptemplate/customizationSection.xml", CUSTOMIZATION_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      CustomizationSection section = client.getCustomizationSection(uri);

      assertEquals(section, exampleCustomizationSection());

      Task task = client.modifyCustomizationSection(uri, exampleCustomizationSection());
      assertNotNull(task);
   }

   public void testErrorGetCustomizationSection() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/customizationSection").acceptMedia(CUSTOMIZATION_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateClient();

      assertNull(client.getCustomizationSection(uri));
   }
   
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testErrorEditCustomizationSection() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/customizationSection").xmlFilePayload("/vapptemplate/customizationSection.xml", CUSTOMIZATION_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateClient();

      client.modifyCustomizationSection(uri, exampleCustomizationSection());
   }
   
   public void testGuestCustomizationSection() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/guestCustomizationSection").acceptMedia(GUEST_CUSTOMIZATION_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/guestCustomizationSection.xml", GUEST_CUSTOMIZATION_SECTION).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/guestCustomizationSection").xmlFilePayload("/vapptemplate/guestCustomizationSection.xml", GUEST_CUSTOMIZATION_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      GuestCustomizationSection section = client.getGuestCustomizationSection(uri);

      assertEquals(section, exampleGuestCustomizationSection());

      Task task = client.modifyGuestCustomizationSection(uri, exampleGuestCustomizationSection());
      assertNotNull(task);
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testErrorGetGuestCustomizationSection() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/guestCustomizationSection").acceptMedia(GUEST_CUSTOMIZATION_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateClient();

      client.getGuestCustomizationSection(uri);
   }
   
   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testErrorEditGuestCustomizationSection() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/guestCustomizationSection").xmlFilePayload("/vapptemplate/guestCustomizationSection.xml", GUEST_CUSTOMIZATION_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateClient();

      client.modifyGuestCustomizationSection(uri, exampleGuestCustomizationSection());
   }

   public void testLeaseSettingsSection() throws ParseException {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/leaseSettingsSection").acceptMedia(LEASE_SETTINGS_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/leaseSettingsSection.xml", LEASE_SETTINGS_SECTION).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/leaseSettingsSection").xmlFilePayload("/vapptemplate/leaseSettingsSection.xml", LEASE_SETTINGS_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      LeaseSettingsSection section = client.getLeaseSettingsSection(uri);

      assertEquals(section, exampleLeaseSettingsSection());

      Task task = client.modifyLeaseSettingsSection(uri, exampleLeaseSettingsSection());
      assertNotNull(task);
   }

   public void testErrorGetLeaseSettingsSection() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/leaseSettingsSection").acceptMedia(LEASE_SETTINGS_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateClient();

      assertNull(client.getLeaseSettingsSection(uri));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testErrorEditLeaseSettingsSection() throws ParseException {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/leaseSettingsSection").xmlFilePayload("/vapptemplate/leaseSettingsSection.xml", LEASE_SETTINGS_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateClient();

      client.modifyLeaseSettingsSection(uri, exampleLeaseSettingsSection());
   }

   public void testVappTemplateMetadata() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/metadata").acceptMedia(ANY).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/metadata.xml", METADATA).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/metadata").xmlFilePayload("/vapptemplate/metadata.xml", METADATA).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      Metadata metadata = client.getMetadataClient().getMetadata(uri);

      assertEquals(metadata, exampleMetadata());

      Task task = client.getMetadataClient().mergeMetadata(uri, exampleMetadata());
      assertNotNull(task);
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testErrorGetMetadata() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/metadata").acceptMedia(ANY).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateClient();

      client.getMetadataClient().getMetadata(uri);
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testErrorEditMetadata() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/metadata").xmlFilePayload("/vapptemplate/metadata.xml", METADATA).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateClient();

      client.getMetadataClient().mergeMetadata(uri, exampleMetadata());
   }
   
   public void testVappTemplateMetadataValue() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/metadata/12345").acceptMedia(METADATA_ENTRY).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/metadataValue.xml", METADATA_ENTRY).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/metadata/12345").xmlFilePayload("/vapptemplate/metadataValue.xml", METADATA_ENTRY).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("DELETE", templateId + "/metadata/12345").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      MetadataValue metadata = client.getMetadataClient().getMetadataValue(uri, "12345");

      assertEquals(metadata, exampleMetadataValue());

      Task task = client.getMetadataClient().setMetadata(uri, "12345", exampleMetadataValue());
      assertNotNull(task);

      task = client.getMetadataClient().deleteMetadataEntry(uri, "12345");
      assertNotNull(task);
   }

   public void testErrorGetMetadataValue() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/metadata/12345").acceptMedia(METADATA_ENTRY).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateClient();

      assertNull(client.getMetadataClient().getMetadataValue(uri, "12345"));
   }
   
   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testErrorEditMetadataValue() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/metadata/12345").xmlFilePayload("/vapptemplate/metadataValue.xml", METADATA_ENTRY).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateClient();

      client.getMetadataClient().setMetadata(uri, "12345", exampleMetadataValue());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testDeleteMissingMetadataValue() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("DELETE", templateId + "/metadata/12345").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error403.xml", ERROR).httpResponseBuilder().statusCode(403).build()).getVAppTemplateClient();

      client.getMetadataClient().deleteMetadataEntry(uri, "12345");
   }
   
   public void testNetworkConfigSection() throws ParseException {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/networkConfigSection").acceptMedia(NETWORK_CONFIG_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/networkConfigSection.xml", NETWORK_CONFIG_SECTION).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/networkConfigSection").xmlFilePayload("/vapptemplate/networkConfigSection.xml", NETWORK_CONFIG_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);

      NetworkConfigSection section = client.getNetworkConfigSection(uri);

      assertEquals(section, exampleNetworkConfigSection());

      Task task = client.modifyNetworkConfigSection(uri, exampleNetworkConfigSection());
      assertNotNull(task);
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testErrorGetNetworkConfigSection() {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/networkConfigSection").acceptMedia(NETWORK_CONFIG_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateClient();

      client.getNetworkConfigSection(uri);
   }

   @Test(expectedExceptions = VCloudDirectorException.class)
   public void testErrorEditNetworkConfigSection() throws ParseException {
      final String templateId = "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      URI uri = URI.create(endpoint + templateId);

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/networkConfigSection").xmlFilePayload("/vapptemplate/networkConfigSection.xml", NETWORK_CONFIG_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/error400.xml", ERROR).httpResponseBuilder().statusCode(400).build()).getVAppTemplateClient();

      client.modifyNetworkConfigSection(uri, exampleNetworkConfigSection());
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
            .children(ImmutableSet.<VAppTemplate>of())
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

   private CustomizationSection exampleCustomizationSection() {
      return CustomizationSection.builder()
            .links(ImmutableSet.of(
                  Link.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9/customizationSection/"))
                        .type("application/vnd.vmware.vcloud.customizationSection+xml").rel("edit").build()
            ))
            .type("application/vnd.vmware.vcloud.customizationSection+xml")
            .info("VApp template customization section")
            .customizeOnInstantiate(true)
            .href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9/customizationSection/"))
            .required(false)
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

   private Metadata exampleMetadata() {
      return Metadata.builder()
            .href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9/metadata"))
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .link(Link.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9"))
                  .type("application/vnd.vmware.vcloud.vAppTemplate+xml").rel("up").build())
            .entry(MetadataEntry.builder().key("key").value("value").build()).build();
   }

   private MetadataValue exampleMetadataValue() {
      return MetadataValue.builder().value("some value").build();
   }

   private NetworkConfigSection exampleNetworkConfigSection() throws ParseException {
      
      FirewallService firewallService =
            FirewallService.builder()
                  .enabled(true)
                  .firewallRules(
                  ImmutableSet.of(
                        FirewallRule.builder()
                              .isEnabled(true)
                              .description("FTP Rule")
                              .policy("allow")
                              .protocols(FirewallRuleProtocols.builder().tcp(true).build())
                              .port(21)
                              .destinationIp("10.147.115.1")
                              .build(),
                        FirewallRule.builder()
                              .isEnabled(true)
                              .description("SSH Rule")
                              .policy("allow")
                              .protocols(FirewallRuleProtocols.builder().tcp(true).build())
                              .port(22)
                              .destinationIp("10.147.115.1")
                              .build())).build();

      NatService natService = NatService.builder().enabled(true).natType("ipTranslation").policy("allowTraffic")
            .natRules(ImmutableSet.of(NatRule.builder().oneToOneVmRule(
                  NatOneToOneVmRule.builder().mappingMode("manual").externalIpAddress("64.100.10.1").vAppScopedVmId("20ea086f-1a6a-4fb2-8e2e-23372facf7de").vmNicId(0).build()).build()
            )).build();

      NetworkConfiguration networkConfiguration = NetworkConfiguration.builder().ipScope(
            IpScope.builder()
                  .isInherited(false)
                  .gateway("10.147.56.253")
                  .netmask("255.255.255.0")
                  .dns1("10.147.115.1")
                  .dns2("10.147.115.2")
                  .dnsSuffix("example.com")
                  .ipRanges(IpRanges.builder().ipRange(IpRange.builder().startAddress("10.147.56.1").endAddress("10.147.56.1").build()).build())
                  .build())
            .parentNetwork(Reference.builder().href(URI.create("http://vcloud.example.com/api/v1.0/network/54")).type("application/vnd.vmware.vcloud.network+xml").name("Internet").build())
            .fenceMode(FenceMode.NAT_ROUTED)
            .features(NetworkFeatures.builder().services(ImmutableSet.of(firewallService, natService)).build())
            .build();
      
      return NetworkConfigSection.builder()
            .info("Configuration parameters for logical networks")
            .networkConfigs(
                  ImmutableSet.of(
                        VAppNetworkConfiguration.builder()
                              .networkName("vAppNetwork")
                              .configuration(
                                    networkConfiguration
                              ).build()
                  )).build();
   }
}
