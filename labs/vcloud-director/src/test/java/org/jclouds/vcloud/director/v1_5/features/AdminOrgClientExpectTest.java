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
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.OrgEmailSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgGeneralSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgLdapSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgPasswordPolicySettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgVAppTemplateLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.SmtpServerSettings;
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
   
// GET /admin/org/{id}
   
// POST /admin/org/{id}/catalogs
 
// POST /admin/org/{id}/groups
 
// GET /admin/org/{id}/settings
 
// PUT /admin/org/{id}/settings
 
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

      assertEquals(client.getAdminOrgClient().getEmailSettings(orgRef.getURI()), expected);
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

      assertEquals(client.getAdminOrgClient().updateEmailSettings(orgRef.getURI(), expected), expected);
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

      assertEquals(client.getAdminOrgClient().getGeneralSettings(orgRef.getURI()), expected);
   }
   
   public static final OrgGeneralSettings generalSettings() {
      return OrgGeneralSettings.builder()
         
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

      assertEquals(client.getAdminOrgClient().updateGeneralSettings(orgRef.getURI(), expected), expected);
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

      assertEquals(client.getAdminOrgClient().getLdapSettings(orgRef.getURI()), expected);
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

      OrgPasswordPolicySettings expected = orgPasswordPolicy();

      assertEquals(client.getAdminOrgClient().getPasswordPolicy(orgRef.getURI()), expected);
   }
   
   public static final OrgPasswordPolicySettings orgPasswordPolicy() {
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
   public void testUpdateOrgPasswordPolicy() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/org/???/settings/passwordPolicy")
            .xmlFilePayload("/org/admin/updatePasswordPolicySource.xml", 
                  VCloudDirectorMediaType.ORG_PASSWORD_POLICY_SETTINGS)
            .acceptMedia(VCloudDirectorMediaType.ORG_PASSWORD_POLICY_SETTINGS)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/updatePasswordPolicy.xml", 
                  VCloudDirectorMediaType.ORG_PASSWORD_POLICY_SETTINGS)
            .httpResponseBuilder().build());

      OrgPasswordPolicySettings expected = updateOrgPasswordPolicy();

      assertEquals(client.getAdminOrgClient().updatePasswordPolicy(orgRef.getURI(), expected), expected);
   }
   
   public static final OrgPasswordPolicySettings updateOrgPasswordPolicy() {
      return orgPasswordPolicy().toBuilder()
         .accountLockoutEnabled(true)
         .invalidLoginsBeforeLockout(6)
         .accountLockoutIntervalMinutes(11)
         .build();
   }
 
   @Test(enabled = false)
   public void testGetVAppLeaseSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/org/???/settings/vAppLeaseSettings")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/vAppLeaseSettings.xml", 
                  VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
            .httpResponseBuilder().build());

      OrgLeaseSettings expected = orgVAppLeaseSettings();

      assertEquals(client.getAdminOrgClient().getVAppLeaseSettings(orgRef.getURI()), expected);
   }
   
   public static final OrgLeaseSettings orgVAppLeaseSettings() {
      return OrgLeaseSettings.builder()
         
         .build();
   }
   
   @Test(enabled = false)
   public void testUpdateOrgVAppLeaseSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/org/???/settings/vAppLeaseSettings")
            .xmlFilePayload("/org/admin/updateVAppLeaseSettingsSource.xml", 
                  VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
            .acceptMedia(VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/updateVAppLeaseSettings.xml", 
                  VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
            .httpResponseBuilder().build());

      OrgLeaseSettings expected = updateOrgVAppLeaseSettings();

      assertEquals(client.getAdminOrgClient().updateVAppLeaseSettings(orgRef.getURI(), expected), expected);
   }
   
   public static final OrgLeaseSettings updateOrgVAppLeaseSettings() {
      return orgVAppLeaseSettings().toBuilder()
         
         .build();
   }
 
   @Test(enabled = false)
   public void testGetVAppTemplateLeaseSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/org/???/settings/vAppTemplateLeaseSettings")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/vAppTemplateLeaseSettings.xml", 
                  VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
            .httpResponseBuilder().build());

      OrgVAppTemplateLeaseSettings expected = orgVAppTemplateLeaseSettings();

      assertEquals(client.getAdminOrgClient().getVAppTemplateLeaseSettings(orgRef.getURI()), expected);
   }
   
   public static final OrgVAppTemplateLeaseSettings orgVAppTemplateLeaseSettings() {
      return OrgVAppTemplateLeaseSettings.builder()
         
         .build();
   }
   
   @Test(enabled = false)
   public void testUpdateOrgVAppTemplateLeaseSettings() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/org/???/settings/vAppTemplateLeaseSettings")
            .xmlFilePayload("/org/admin/updateVAppLeaseSettingsSource.xml", 
                  VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
            .acceptMedia(VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/org/admin/updateVAppLeaseSettings.xml", 
                  VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
            .httpResponseBuilder().build());

      OrgVAppTemplateLeaseSettings expected = updateOrgVAppTemplateLeaseSettings();

      assertEquals(client.getAdminOrgClient().updateVAppTemplateLeaseSettings(orgRef.getURI(), expected), expected);
   }
   
   public static final OrgVAppTemplateLeaseSettings updateOrgVAppTemplateLeaseSettings() {
      return orgVAppTemplateLeaseSettings().toBuilder()
         
         .build();
   }
}
