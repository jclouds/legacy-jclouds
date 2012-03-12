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

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.AdminOrg;
import org.jclouds.vcloud.director.v1_5.domain.CatalogsList;
import org.jclouds.vcloud.director.v1_5.domain.GroupsList;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Networks;
import org.jclouds.vcloud.director.v1_5.domain.OrgEmailSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgGeneralSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgLdapSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgPasswordPolicySettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgVAppTemplateLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.SmtpServerSettings;
import org.jclouds.vcloud.director.v1_5.domain.UsersList;
import org.jclouds.vcloud.director.v1_5.domain.Vdcs;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

/**
 * Test the {@link GroupClient} by observing its side effects.
 * 
 * @author danikov
 */
@Test(groups = { "unit", "user", "org"}, singleThreaded = true, testName = "AdminOrgClientExpectTest")
public class AdminOrgClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {
   
   private Reference orgRef = Reference.builder()
         .href(URI.create(endpoint + "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
         .build();
   
   @Test
   public void testGetOrg() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/org.xml", 
                  VCloudDirectorMediaType.ADMIN_ORG)
            .httpResponseBuilder().build());

      AdminOrg expected = adminOrg();

      assertEquals(client.getAdminOrgClient().getOrg(orgRef.getHref()), expected);
   }
   
   public static final AdminOrg adminOrg() {
      return AdminOrg.builder()
         .name("JClouds")
         .id("urn:vcloud:org:6f312e42-cd2b-488d-a2bb-97519cd57ed0")
         .type("application/vnd.vmware.admin.organization+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.tasksList+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/tasksList/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/metadata"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.admin.catalog+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/catalogs"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.admin.user+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/users"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.admin.group+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/groups"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.admin.orgNetwork+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/networks"))
            .build())
         .link(Link.builder()
            .rel("edit")
            .type("application/vnd.vmware.admin.organization+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
            .build())
         .link(Link.builder()
            .rel("remove")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
            .build())
         .link(Link.builder()
            .rel("alternate")
            .type("application/vnd.vmware.vcloud.org+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
            .build())
         .description("")
         .fullName("JClouds")
         .isEnabled(true)
         .settings(settings())
         .users(UsersList.builder()
            .user(Reference.builder()
               .type("application/vnd.vmware.admin.user+xml")
               .name("adam.lowe@cloudsoftcorp.com")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/672ebb67-d8ff-4201-9c1b-c1be869e526c"))
               .build())
            .user(Reference.builder()
               .type("application/vnd.vmware.admin.user+xml")
               .name("adrian@jclouds.org")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/8c360b93-ed25-4c9a-8e24-d48cd9966d93"))
               .build())
            .user(Reference.builder()
               .type("application/vnd.vmware.admin.user+xml")
               .name("qunying.huang@enstratus.com")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/967d317c-4273-4a95-b8a4-bf63b78e9c69"))
               .build())
            .user(Reference.builder()
               .type("application/vnd.vmware.admin.user+xml")
               .name("dan@cloudsoftcorp.com")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/ae75edd2-12de-414c-8e85-e6ea10442c08"))
               .build())
            .user(Reference.builder()
               .type("application/vnd.vmware.admin.user+xml")
               .name("adk@cloudsoftcorp.com")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/e9eb1b29-0404-4c5e-8ef7-e584acc51da9"))
               .build())
            .build())
         .groups(GroupsList.builder()
            .build())
         .catalogs(CatalogsList.builder()
            .catalog(Reference.builder()
               .type("application/vnd.vmware.admin.catalog+xml")
               .name("QunyingTestCatalog")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
               .build())
            .catalog(Reference.builder()
               .type("application/vnd.vmware.admin.catalog+xml")
               .name("Public")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/9e08c2f6-077a-42ce-bece-d5332e2ebb5c"))
               .build())
            .catalog(Reference.builder()
               .type("application/vnd.vmware.admin.catalog+xml")
               .name("dantest")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/b542aff4-9f97-4f51-a126-4330fbf62f02"))
               .build())
            .catalog(Reference.builder()
               .type("application/vnd.vmware.admin.catalog+xml")
               .name("test")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/b7289d54-4ca4-497f-9a93-2d4afc97e3da"))
               .build())
            .build())
         .vdcs(Vdcs.builder()
            .vdc(Reference.builder()
               .type("application/vnd.vmware.vcloud.vdc+xml")
               .name("Cluster01-JClouds")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/vdc/d16d333b-e3c0-4176-845d-a5ee6392df07"))
               .build())
            .build())
         .networks(Networks.builder()
            .network(Reference.builder()
               .type("application/vnd.vmware.admin.network+xml")
               .name("ilsolation01-Jclouds")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/network/f3ba8256-6f48-4512-aad6-600e85b4dc38"))
               .build())
            .network(Reference.builder()
               .type("application/vnd.vmware.admin.network+xml")
               .name("internet01-Jclouds")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/network/55a677cf-ab3f-48ae-b880-fab90421980c"))
               .build())
            .build())
         .build();
   }
   
   @Test(enabled = false)
   public void testGetSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/settings.xml", 
                  VCloudDirectorMediaType.ORG_SETTINGS)
            .httpResponseBuilder().build());

      OrgSettings expected = settings();

      assertEquals(client.getAdminOrgClient().getSettings(orgRef.getHref()), expected);
   }
   
   public static final OrgSettings settings() {
      return OrgSettings.builder()
         .type("application/vnd.vmware.admin.orgSettings+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings"))
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.admin.vAppTemplateLeaseSettings+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/vAppTemplateLeaseSettings"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.admin.organizationEmailSettings+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/email"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.admin.vAppLeaseSettings+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/vAppLeaseSettings"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.admin.organizationPasswordPolicySettings+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/passwordPolicy"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.admin.organizationGeneralSettings+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/general"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.admin.organizationLdapSettings+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/ldap"))
            .build())
         .link(Link.builder()
            .rel("edit")
            .type("application/vnd.vmware.admin.orgSettings+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings"))
            .build())
         .generalSettings(generalSettings())
         .vAppLeaseSettings(vAppLeaseSettings())
         .vAppTemplateLeaseSettings(vAppTemplateLeaseSettings())
         .ldapSettings(ldapSettings())
         .emailSettings(emailSettings())
         .passwordPolicy(passwordPolicy())
         .build();
   }
   
   @Test(enabled = false)
   public void testUpdateSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/")
            .xmlFilePayload("/org/admin/updateSettingsSource.xml", 
                  VCloudDirectorMediaType.ORG_SETTINGS)
            .acceptMedia(VCloudDirectorMediaType.ORG_SETTINGS)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/updateSettings.xml", 
                  VCloudDirectorMediaType.ORG_SETTINGS)
            .httpResponseBuilder().build());

      OrgSettings expected = updateSettings();

      assertEquals(client.getAdminOrgClient().updateSettings(orgRef.getHref(), expected), expected);
   }
   
   @Test
   public static final OrgSettings updateSettings() {
      return settings().toBuilder()
         .build();
   }
 
   @Test
   public void testGetEmailSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/email")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/emailSettings.xml", 
                  VCloudDirectorMediaType.ORG_GENERAL_SETTINGS)
            .httpResponseBuilder().build());

      OrgEmailSettings expected = emailSettings();

      assertEquals(client.getAdminOrgClient().getEmailSettings(orgRef.getHref()), expected);
   }
   
   public static final OrgEmailSettings emailSettings() {
      return OrgEmailSettings.builder()
         .type("application/vnd.vmware.admin.organizationEmailSettings+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/email"))
         .link(Link.builder()
               .rel("edit")
               .type("application/vnd.vmware.admin.organizationEmailSettings+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/email"))
               .build())
         .isDefaultSmtpServer(true)
         .isDefaultOrgEmail(true)
         .fromEmailAddress("")
         .defaultSubjectPrefix("")
         .isAlertEmailToAllAdmins(true)
         .smtpServerSettings(SmtpServerSettings.builder()
            .useAuthentication(false)
            .host("")
            .username("")
            .password("")
            .build())
         .build();
   }
   
   @Test
   public void testUpdateEmailSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/email")
            .xmlFilePayload("/org/admin/updateEmailSettingsSource.xml", 
                  VCloudDirectorMediaType.ORG_EMAIL_SETTINGS)
            .acceptMedia(VCloudDirectorMediaType.ORG_EMAIL_SETTINGS)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/updateEmailSettings.xml", 
                  VCloudDirectorMediaType.ORG_EMAIL_SETTINGS)
            .httpResponseBuilder().build());

      OrgEmailSettings expected = updateEmailSettings();

      assertEquals(client.getAdminOrgClient().updateEmailSettings(orgRef.getHref(), expected), expected);
   }
   
   @Test
   public static final OrgEmailSettings updateEmailSettings() {
      return emailSettings().toBuilder()
         .isDefaultSmtpServer(false)
         .isDefaultOrgEmail(false)
         .fromEmailAddress("test@test.com")
         .defaultSubjectPrefix("new")
         .isAlertEmailToAllAdmins(false)
         .smtpServerSettings(emailSettings().getSmtpServerSettings().toBuilder()
            .useAuthentication(true)
            .host("new")
            .username("new")
            .build())
         .build();
   }
   
   @Test(enabled = false)
   public void testGetGeneralSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/general")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/generalSettings.xml", 
                  VCloudDirectorMediaType.ORG_GENERAL_SETTINGS)
            .httpResponseBuilder().build());

      OrgGeneralSettings expected = generalSettings();

      assertEquals(client.getAdminOrgClient().getGeneralSettings(orgRef.getHref()), expected);
   }
   
   public static final OrgGeneralSettings generalSettings() {
      return OrgGeneralSettings.builder()
         .type("application/vnd.vmware.admin.organizationGeneralSettings+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/general"))
         .link(Link.builder()
            .rel("edit")
            .type("application/vnd.vmware.admin.organizationGeneralSettings+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/general"))
            .build())
         .canPublishCatalogs(false)
         .deployedVMQuota(0)
         .storedVmQuota(0)
         .useServerBootSequence(false)
         .delayAfterPowerOnSeconds(0)
         .build();
   }
   
   @Test(enabled = false)
   public void testUpdateGeneralSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/general")
            .xmlFilePayload("/org/admin/updateGeneralSettingsSource.xml", 
                  VCloudDirectorMediaType.ORG_GENERAL_SETTINGS)
            .acceptMedia(VCloudDirectorMediaType.ORG_GENERAL_SETTINGS)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/updateGeneralSettings.xml", 
                  VCloudDirectorMediaType.ORG_GENERAL_SETTINGS)
            .httpResponseBuilder().build());

      OrgGeneralSettings expected = updateGeneralSettings();

      assertEquals(client.getAdminOrgClient().updateGeneralSettings(orgRef.getHref(), expected), expected);
   }
   
   public static final OrgGeneralSettings updateGeneralSettings() {
      return generalSettings().toBuilder()
         
         .build();
   }
 
   @Test
   public void testGetLdapSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/ldap")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/ldapSettings.xml", 
                  VCloudDirectorMediaType.ORG_LDAP_SETTINGS)
            .httpResponseBuilder().build());

      OrgLdapSettings expected = ldapSettings();

      assertEquals(client.getAdminOrgClient().getLdapSettings(orgRef.getHref()), expected);
   }
   
   public static final OrgLdapSettings ldapSettings() {
      return OrgLdapSettings.builder()
         .type("application/vnd.vmware.admin.organizationLdapSettings+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/ldap"))
         .ldapMode("NONE")
         .build();
   }
 
   @Test
   public void testGetPasswordPolicy() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/passwordPolicy")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/passwordPolicy.xml", 
                  VCloudDirectorMediaType.ORG_PASSWORD_POLICY_SETTINGS)
            .httpResponseBuilder().build());

      OrgPasswordPolicySettings expected = passwordPolicy();

      assertEquals(client.getAdminOrgClient().getPasswordPolicy(orgRef.getHref()), expected);
   }
   
   public static final OrgPasswordPolicySettings passwordPolicy() {
      return OrgPasswordPolicySettings.builder()
         .type("application/vnd.vmware.admin.organizationPasswordPolicySettings+xml")
         .link(Link.builder()
               .rel("edit")
               .type("application/vnd.vmware.admin.organizationPasswordPolicySettings+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/passwordPolicy"))
               .build())
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/passwordPolicy"))
         .accountLockoutEnabled(false)
         .invalidLoginsBeforeLockout(5)
         .accountLockoutIntervalMinutes(10)
         .build();
   }
   
   @Test
   public void testUpdatePasswordPolicy() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/passwordPolicy")
            .xmlFilePayload("/org/admin/updatePasswordPolicySource.xml", 
                  VCloudDirectorMediaType.ORG_PASSWORD_POLICY_SETTINGS)
            .acceptMedia(VCloudDirectorMediaType.ORG_PASSWORD_POLICY_SETTINGS)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/updatePasswordPolicy.xml", 
                  VCloudDirectorMediaType.ORG_PASSWORD_POLICY_SETTINGS)
            .httpResponseBuilder().build());

      OrgPasswordPolicySettings expected = updateOrgPasswordPolicy();

      assertEquals(client.getAdminOrgClient().updatePasswordPolicy(orgRef.getHref(), expected), expected);
   }
   
   public static final OrgPasswordPolicySettings updateOrgPasswordPolicy() {
      return passwordPolicy().toBuilder()
         .accountLockoutEnabled(true)
         .invalidLoginsBeforeLockout(6)
         .accountLockoutIntervalMinutes(11)
         .build();
   }
 
   @Test(enabled = false)
   public void testGetVAppLeaseSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/vAppLeaseSettings")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/vAppLeaseSettings.xml", 
                  VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
            .httpResponseBuilder().build());

      OrgLeaseSettings expected = vAppLeaseSettings();

      assertEquals(client.getAdminOrgClient().getVAppLeaseSettings(orgRef.getHref()), expected);
   }
   
   public static final OrgLeaseSettings vAppLeaseSettings() {
      return OrgLeaseSettings.builder()
         .type("application/vnd.vmware.admin.vAppLeaseSettings+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/vAppLeaseSettings"))
         .link(Link.builder()
            .rel("edit")
            .type("application/vnd.vmware.admin.vAppLeaseSettings+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/vAppLeaseSettings"))
            .build())
         .deleteOnStorageLeaseExpiration(false)
         .deploymentLeaseSeconds(0)
         .storageLeaseSeconds(0)
         .build();
   }
   
   @Test(enabled = false)
   public void testUpdateOrgVAppLeaseSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/vAppLeaseSettings")
            .xmlFilePayload("/org/admin/updateVAppLeaseSettingsSource.xml", 
                  VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
            .acceptMedia(VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/updateVAppLeaseSettings.xml", 
                  VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
            .httpResponseBuilder().build());

      OrgLeaseSettings expected = updateVAppLeaseSettings();

      assertEquals(client.getAdminOrgClient().updateVAppLeaseSettings(orgRef.getHref(), expected), expected);
   }
   
   public static final OrgLeaseSettings updateVAppLeaseSettings() {
      return vAppLeaseSettings().toBuilder()
         
         .build();
   }
 
   @Test(enabled = false)
   public void testGetVAppTemplateLeaseSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/vAppTemplateLeaseSettings")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/vAppTemplateLeaseSettings.xml", 
                  VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
            .httpResponseBuilder().build());

      OrgVAppTemplateLeaseSettings expected = vAppTemplateLeaseSettings();

      assertEquals(client.getAdminOrgClient().getVAppTemplateLeaseSettings(orgRef.getHref()), expected);
   }
   
   public static final OrgVAppTemplateLeaseSettings vAppTemplateLeaseSettings() {
      return OrgVAppTemplateLeaseSettings.builder()
         .type("application/vnd.vmware.admin.vAppTemplateLeaseSettings+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/vAppTemplateLeaseSettings"))
         .link(Link.builder()
               .rel("edit")
               .type("application/vnd.vmware.admin.vAppTemplateLeaseSettings+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/vAppTemplateLeaseSettings"))
               .build())
         .deleteOnStorageLeaseExpiration(false)
         .storageLeaseSeconds(0)
         .build();
   }
   
   @Test(enabled = false)
   public void testUpdateOrgVAppTemplateLeaseSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/settings/vAppTemplateLeaseSettings")
            .xmlFilePayload("/org/admin/updateVAppLeaseSettingsSource.xml", 
                  VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
            .acceptMedia(VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/updateVAppLeaseSettings.xml", 
                  VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
            .httpResponseBuilder().build());

      OrgVAppTemplateLeaseSettings expected = updateVAppTemplateLeaseSettings();

      assertEquals(client.getAdminOrgClient().updateVAppTemplateLeaseSettings(orgRef.getHref(), expected), expected);
   }
   
   public static final OrgVAppTemplateLeaseSettings updateVAppTemplateLeaseSettings() {
      return vAppTemplateLeaseSettings().toBuilder()
         
         .build();
   }
}
