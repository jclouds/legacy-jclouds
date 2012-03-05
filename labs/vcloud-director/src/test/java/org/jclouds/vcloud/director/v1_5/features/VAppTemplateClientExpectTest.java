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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.jclouds.vcloud.director.v1_5.domain.*;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests the request/response behavior of {@link org.jclouds.vcloud.director.v1_5.features.VAppTemplateClient}
 *
 * @author Adam Lowe
 */
@Test(groups = {"unit", "user"}, testName = "VAppTemplateClientExpectTest")
public class VAppTemplateClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {

   public void testVAppTemplate() {
      final String templateId = "/vAppTemplate/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      Reference vappTemplateRef = Reference.builder().href(URI.create(endpoint + templateId)).build();

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId).acceptMedia(VAPP_TEMPLATE).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/vAppTemplate.xml", VAPP_TEMPLATE).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId).xmlFilePayload("/vapptemplate/vAppTemplate.xml", VAPP_TEMPLATE).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("DELETE", templateId).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      VAppTemplate template = client.getVAppTemplate(vappTemplateRef);

      assertEquals(template, exampleTemplate());

      Task task = client.editVAppTemplate(vappTemplateRef, template);
      assertNotNull(task);

      task = client.deleteVappTemplate(vappTemplateRef);
      assertNotNull(task);
   }

   public void testConsolidateVAppTemplate() {
      final String templateId = "/vAppTemplate/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      Reference vappTemplateRef = Reference.builder().href(URI.create(endpoint + templateId)).build();

      VAppTemplateClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/consolidate").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      Task task = client.consolidateVappTemplate(vappTemplateRef);
      assertNotNull(task);
   }

   public void testDisableDownloadVAppTemplate() {
      final String templateId = "/vAppTemplate/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      Reference vappTemplateRef = Reference.builder().href(URI.create(endpoint + templateId)).build();

      VAppTemplateClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/disableDownload").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      Task task = client.disableDownloadVappTemplate(vappTemplateRef);
      assertNotNull(task);
   }

   public void testEnableDownloadVAppTemplate() {
      final String templateId = "/vAppTemplate/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      Reference vappTemplateRef = Reference.builder().href(URI.create(endpoint + templateId)).build();

      VAppTemplateClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/enableDownload").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      Task task = client.enableDownloadVappTemplate(vappTemplateRef);
      assertNotNull(task);
   }

   public void testRelocateVAppTemplate() {
      final String templateId = "/vAppTemplate/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      Reference vappTemplateRef = Reference.builder().href(URI.create(endpoint + templateId)).build();

      VAppTemplateClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("POST", templateId + "/action/enableDownload").xmlFilePayload("/vappTemplate/relocateParams.xml", RELOCATE_TEMPLATE).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);

      Reference datastore = Reference.builder().href(URI.create("https://vcloud.example.com/api/admin/extension/datastore/607")).build();
      RelocateParams params = RelocateParams.builder().datastore(datastore).build();

      Task task = client.relocateVappTemplate(vappTemplateRef, params);
      assertNotNull(task);
   }

   public void testCustomizationSection() {
      final String templateId = "/vAppTemplate/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      Reference vappTemplateRef = Reference.builder().href(URI.create(endpoint + templateId)).build();

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/customizationSection").acceptMedia(CUSTOMIZATION_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/customizationSection.xml", CUSTOMIZATION_SECTION).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/customizationSection").xmlFilePayload("/vapptemplate/customizationSection.xml", CUSTOMIZATION_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      CustomizationSection section = client.getVAppTemplateCustomizationSection(vappTemplateRef);

      assertEquals(section, exampleCustomizationSection());

      Task task = client.editVAppTemplateCustomizationSection(vappTemplateRef, section);
      assertNotNull(task);
   }

   public void testGuestCustomizationSection() {
      final String templateId = "/vAppTemplate/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      Reference vappTemplateRef = Reference.builder().href(URI.create(endpoint + templateId)).build();

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/guestCustomizationSection").acceptMedia(GUEST_CUSTOMIZATION_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/guestCustomizationSection.xml", GUEST_CUSTOMIZATION_SECTION).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/guestCustomizationSection").xmlFilePayload("/vapptemplate/guestCustomizationSection.xml", GUEST_CUSTOMIZATION_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      GuestCustomizationSection section = client.getVAppTemplateGuestCustomizationSection(vappTemplateRef);

      assertEquals(section, exampleGuestCustomizationSection());

      Task task = client.editVAppTemplateGuestCustomizationSection(vappTemplateRef, section);
      assertNotNull(task);
   }

   public void testLeaseSettingsSection() throws ParseException {
      final String templateId = "/vAppTemplate/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      Reference vappTemplateRef = Reference.builder().href(URI.create(endpoint + templateId)).build();

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/leaseSettingsSection").acceptMedia(LEASE_SETTINGS_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/leaseSettingsSection.xml", LEASE_SETTINGS_SECTION).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/leaseSettingsSection").xmlFilePayload("/vapptemplate/leaseSettingsSection.xml", LEASE_SETTINGS_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      LeaseSettingsSection section = client.getVappTemplateLeaseSettingsSection(vappTemplateRef);

      assertEquals(section, exampleLeaseSettingsSection());

      Task task = client.editVappTemplateLeaseSettingsSection(vappTemplateRef, section);
      assertNotNull(task);
   }

   public void testVappTemplateMetadata() {
      final String templateId = "/vAppTemplate/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      Reference vappTemplateRef = Reference.builder().href(URI.create(endpoint + templateId)).build();

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/metadata").acceptMedia(METADATA).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/metadata.xml", METADATA).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/metadata").xmlFilePayload("/vapptemplate/metadata.xml", METADATA).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      Metadata metadata = client.getVAppTemplateMetadata(vappTemplateRef);

      assertEquals(metadata, exampleMetadata());

      Task task = client.editVAppTemplateMetadata(vappTemplateRef, metadata);
      assertNotNull(task);
   }

   public void testVappTemplateMetadataValue() {
      final String templateId = "/vAppTemplate/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      Reference vappTemplateRef = Reference.builder().href(URI.create(endpoint + templateId)).build();

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/metadata/12345").acceptMedia(METADATA_ENTRY).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/metadataValue.xml", METADATA_ENTRY).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/metadata/12345").xmlFilePayload("/vapptemplate/metadataValue.xml", METADATA_ENTRY).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("DELETE", templateId + "/metadata/12345").acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);
      MetadataValue metadata = client.getVAppTemplateMetadataValue(vappTemplateRef, "12345");

      assertEquals(metadata, exampleMetadataValue());

      Task task = client.editVAppTemplateMetadataValue(vappTemplateRef, "12345", metadata);
      assertNotNull(task);

      task = client.deleteVAppTemplateMetadataValue(vappTemplateRef, "12345");
      assertNotNull(task);
   }

   public void testNetworkConfigSection() throws ParseException {
      final String templateId = "/vAppTemplate/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9";
      Reference vappTemplateRef = Reference.builder().href(URI.create(endpoint + templateId)).build();

      VAppTemplateClient client = orderedRequestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer().apiCommand("GET", templateId + "/networkConfigSection").acceptMedia(NETWORK_CONFIG_SECTION).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/vapptemplate/networkConfigSection.xml", NETWORK_CONFIG_SECTION).httpResponseBuilder().build(),
            new VcloudHttpRequestPrimer().apiCommand("PUT", templateId + "/networkConfigSection").xmlFilePayload("/vapptemplate/networkConfigSection.xml", NETWORK_CONFIG_SECTION).acceptMedia(TASK).httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer().xmlFilePayload("/task/task.xml", TASK).httpResponseBuilder().build()
      ).getVAppTemplateClient();

      assertNotNull(client);

      NetworkConfigSection section = client.getVAppTemplateNetworkConfigSection(vappTemplateRef);

      assertEquals(section, exampleNetworkConfigSection());

      Task task = client.editVAppTemplateNetworkConfigSection(vappTemplateRef, section);
      assertNotNull(task);
   }

   private VAppTemplate exampleTemplate() {
      Link aLink = Link.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/vdc/d16d333b-e3c0-4176-845d-a5ee6392df07"))
            .type("application/vnd.vmware.vcloud.vdc+xml").rel("up").build();
      Link bLink = Link.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9"))
            .rel("remove").build();

      Owner owner = Owner.builder().user(Reference.builder().href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/967d317c-4273-4a95-b8a4-bf63b78e9c69")).name("x@jclouds.org").type("application/vnd.vmware.admin.user+xml").build()).build();

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
            FirewallService.builder().firewallRules(
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
            .fenceMode("natRouted")
            .features(NetworkFeatures.builder().services(ImmutableSet.<NetworkServiceType>of(firewallService, natService)).build())
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
