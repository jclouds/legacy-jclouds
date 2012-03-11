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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REF_REQ_LIVE;
import static org.testng.Assert.assertNotNull;
import org.jclouds.vcloud.director.v1_5.domain.AdminOrg;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Group;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.OrgEmailSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgGeneralSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgLdapSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgPasswordPolicySettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgVAppTemplateLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;
import org.jclouds.vcloud.director.v1_5.domain.SmtpServerSettings;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests live behavior of {@link AdminGroupClient}.
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin", "org" }, singleThreaded = true, testName = "AdminOrgClientLiveTest")
public class AdminOrgClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String ORG = "admin org";

   /*
    * Convenience references to API clients.
    */

   private AdminOrgClient orgClient;

   /*
    * Shared state between dependant tests.
    */
   private ReferenceType<?> orgRef;
   private OrgSettings settings, newSettings;
   private OrgEmailSettings emailSettings, newEmailSettings;
   private OrgGeneralSettings generalSettings, newGeneralSettings;
   private OrgLdapSettings ldapSettings, newLdapSettings;
   private OrgPasswordPolicySettings passwordPolicy, newPasswordPolicy;
   private OrgLeaseSettings vAppLeaseSettings, newVAppLeaseSettings;
   private OrgVAppTemplateLeaseSettings vAppTemplateLeaseSettings, newVAppTemplateLeaseSettings;

   @Override
   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      orgClient = context.getApi().getAdminOrgClient();
      orgRef = Iterables.getFirst(orgClient.getOrgList().getOrgs(), null).toAdminReference(endpoint);
      assertNotNull(orgRef, String.format(REF_REQ_LIVE, "admin org"));
   }
   
   @Test(testName = "GET /admin/org/{id}")
   public void testGetAdminOrg() {
      AdminOrg adminOrg = orgClient.getOrg(orgRef.getURI());
      
      Checks.checkAdminOrg(adminOrg);
   }
   
   @Test(testName = "GET /admin/org/{id}/settings/emailSettings")
   public void testGetEmailSettings() {
      emailSettings = orgClient.getEmailSettings(orgRef.getURI());
      
      Checks.checkEmailSettings(emailSettings);
   }
   
   @Test(testName = "PUT /admin/org/{id}/settings/emailSettings", 
         dependsOnMethods = { "testGetEmailSettings" })
   public void testUpdateEmailSettings() {
      boolean isDefaultSmtpServer = emailSettings.isDefaultSmtpServer();
      boolean isDefaultOrgEmail = emailSettings.isDefaultOrgEmail();
      String oldFromEmailAddress = emailSettings.getFromEmailAddress();
      String newFromEmailAddress = "test@test.com";
      String oldDefaultSubjectPrefix = emailSettings.getDefaultSubjectPrefix();
      String newDefaultSubjectPrefix = "new"+oldDefaultSubjectPrefix;
      boolean isAlertEmailToAllAdmins = emailSettings.isAlertEmailToAllAdmins();
      SmtpServerSettings oldSmtpServerSettings = emailSettings.getSmtpServerSettings();
      SmtpServerSettings newSmtpServerSettings = oldSmtpServerSettings.toBuilder()
         .useAuthentication(!oldSmtpServerSettings.useAuthentication())
         .host("new"+oldSmtpServerSettings.getHost())
         .username("new"+oldSmtpServerSettings.getUsername())
         .password("new"+oldSmtpServerSettings.getPassword())
         .build();
      
      try {
         newEmailSettings = emailSettings.toBuilder()
               .isDefaultSmtpServer(!isDefaultSmtpServer)
               .isDefaultOrgEmail(!isDefaultOrgEmail)
               .fromEmailAddress(newFromEmailAddress)
               .defaultSubjectPrefix(newDefaultSubjectPrefix)
               .isAlertEmailToAllAdmins(!isAlertEmailToAllAdmins)
               .smtpServerSettings(newSmtpServerSettings)
               .build();
         
         emailSettings = orgClient.updateEmailSettings(
               orgRef.getURI(), newEmailSettings);
         
         assertTrue(equal(emailSettings.isDefaultSmtpServer(), !isDefaultSmtpServer), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "emailSettings", "isDefaultSmtpServer"));
         assertTrue(equal(emailSettings.isDefaultOrgEmail(), !isDefaultOrgEmail), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "emailSettings", "isDefaultOrgEmail"));
         assertTrue(equal(emailSettings.getFromEmailAddress(), newFromEmailAddress), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "emailSettings", "fromEmailAddress"));
         assertTrue(equal(emailSettings.getDefaultSubjectPrefix(), newDefaultSubjectPrefix), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "emailSettings", "defaultSubjectPrefix"));
         assertTrue(equal(emailSettings.isAlertEmailToAllAdmins(), !isAlertEmailToAllAdmins), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "emailSettings", "isAlertEmailToAllAdmins"));
         assertTrue(equal(emailSettings.getSmtpServerSettings(), newSmtpServerSettings), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "emailSettings", "smtpServerSettings"));
         
         //TODO negative tests?
         
         Checks.checkEmailSettings(emailSettings);
      } finally {
         emailSettings = emailSettings.toBuilder()
               .isDefaultSmtpServer(isDefaultSmtpServer)
               .isDefaultOrgEmail(isDefaultOrgEmail)
               .fromEmailAddress(oldFromEmailAddress)
               .defaultSubjectPrefix(oldDefaultSubjectPrefix)
               .isAlertEmailToAllAdmins(isAlertEmailToAllAdmins)
               .smtpServerSettings(oldSmtpServerSettings)
               .build();
         
         emailSettings = orgClient.updateEmailSettings(
               orgRef.getURI(), emailSettings);
      }
   }
 
   @Test(testName = "GET /admin/org/{id}/settings/generalSettings")
   public void testGetGeneralSettings() {
      generalSettings = orgClient.getGeneralSettings(orgRef.getURI());
      
      Checks.checkGeneralSettings(generalSettings);
   }
   
   @Test(testName = "PUT /admin/org/{id}/settings/generalSettings", 
         dependsOnMethods = { "testGetGeneralSettings" }, enabled = false )
   public void testUpdateGeneralSettings() {
//      boolean canPublishCatalogs = generalSettings.canPublishCatalogs(); // FIXME: did not update
      Integer deployedVMQuota = generalSettings.getDeployedVMQuota();
      Integer storedVmQuota = generalSettings.getStoredVmQuota();
      boolean useServerBootSequence = generalSettings.useServerBootSequence();
      Integer delayAfterPowerOnSeconds = generalSettings.getDelayAfterPowerOnSeconds();
      
      try {
         newGeneralSettings = generalSettings.toBuilder()
//               .canPublishCatalogs(!canPublishCatalogs)
               .deployedVMQuota(deployedVMQuota+1)
               .storedVmQuota(storedVmQuota+1)
               .useServerBootSequence(!useServerBootSequence)
               .delayAfterPowerOnSeconds(delayAfterPowerOnSeconds+1)
               .build();
         
         generalSettings = orgClient.updateGeneralSettings(
               orgRef.getURI(), newGeneralSettings);
         
//         assertTrue(equal(generalSettings.canPublishCatalogs(), !canPublishCatalogs), 
//               String.format(OBJ_FIELD_UPDATABLE, 
//               "generalSettings", "canPublishCatalogs"));
         assertTrue(equal(generalSettings.getDeployedVMQuota(), deployedVMQuota+1), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "generalSettings", "deployedVMQuota"));
         assertTrue(equal(generalSettings.getStoredVmQuota(), storedVmQuota+1), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "generalSettings", "storedVmQuota"));
         assertTrue(equal(generalSettings.useServerBootSequence(), !useServerBootSequence), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "generalSettings", "useServerBootSequence"));
         assertTrue(equal(generalSettings.getDelayAfterPowerOnSeconds(), delayAfterPowerOnSeconds+1), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "generalSettings", "delayAfterPowerOnSeconds"));
         
         //TODO negative tests?
         
         Checks.checkGeneralSettings(generalSettings);
      } finally {
         generalSettings = generalSettings.toBuilder()
//               .canPublishCatalogs(canPublishCatalogs)
               .deployedVMQuota(deployedVMQuota)
               .storedVmQuota(storedVmQuota)
               .useServerBootSequence(useServerBootSequence)
               .delayAfterPowerOnSeconds(delayAfterPowerOnSeconds)
               .build();
         
         generalSettings = orgClient.updateGeneralSettings(
               orgRef.getURI(), generalSettings);
      }
   }
 
   @Test(testName = "GET /admin/org/{id}/settings/ldap")
   public void testGetLdapSettings() {
      ldapSettings = orgClient.getLdapSettings(orgRef.getURI());
      
      Checks.checkLdapSettings(ldapSettings);
   }
 
   @Test(testName = "GET /admin/org/{id}/settings/passwordPolicy")
   public void testGetPasswordPolicy() {
      passwordPolicy = orgClient.getPasswordPolicy(orgRef.getURI());
      
      Checks.checkPasswordPolicySettings(passwordPolicy);
   }
   
   @Test(testName = "PUT /admin/org/{id}/settings/passwordPolicy", 
         dependsOnMethods = { "testGetPasswordPolicy" })
   public void testUpdatePasswordPolicy() {
      boolean accountLockoutEnabled = passwordPolicy.isAccountLockoutEnabled();
      Integer invalidLoginsBeforeLockout = passwordPolicy.getInvalidLoginsBeforeLockout();
      Integer accountLockoutIntervalMinutes = passwordPolicy.getAccountLockoutIntervalMinutes();
      
      try {
         newPasswordPolicy = passwordPolicy.toBuilder()
               .accountLockoutEnabled(!accountLockoutEnabled)
               .invalidLoginsBeforeLockout(invalidLoginsBeforeLockout+1)
               .accountLockoutIntervalMinutes(accountLockoutIntervalMinutes+1)
               .build();
         
         passwordPolicy = orgClient.updatePasswordPolicy(
               orgRef.getURI(), newPasswordPolicy);
         
         assertTrue(equal(passwordPolicy.isAccountLockoutEnabled(), !accountLockoutEnabled), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "PasswordPolicySettings", "deleteOnStorageLeaseExpiration"));
         assertTrue(equal(passwordPolicy.getInvalidLoginsBeforeLockout(), invalidLoginsBeforeLockout+1), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "PasswordPolicySettings", "storageLeaseSeconds"));
         assertTrue(equal(passwordPolicy.getAccountLockoutIntervalMinutes(), accountLockoutIntervalMinutes+1), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "PasswordPolicySettings", "deploymentLeaseSeconds"));
         
         //TODO negative tests?
         
         Checks.checkPasswordPolicySettings(passwordPolicy);
      } finally {
         passwordPolicy = passwordPolicy.toBuilder()
               .accountLockoutEnabled(accountLockoutEnabled)
               .invalidLoginsBeforeLockout(invalidLoginsBeforeLockout)
               .accountLockoutIntervalMinutes(accountLockoutIntervalMinutes)
               .build();
         
         passwordPolicy = orgClient.updatePasswordPolicy(
               orgRef.getURI(), passwordPolicy);
      }
   }
   
   @Test(testName = "GET /admin/org/{id}/settings/vAppLeaseSettings")
   public void testGetVAppLeaseSettings() {
      vAppLeaseSettings = orgClient.getVAppLeaseSettings(orgRef.getURI());
      
      Checks.checkVAppLeaseSettings(vAppLeaseSettings);
   }
   
   @Test(testName = "PUT /admin/org/{id}/settings/vAppLeaseSettings", 
         dependsOnMethods = { "testGetVAppLeaseSettings" }, enabled = false) // FIXME: fails with 403 forbidden
   public void testUpdateVAppLeaseSettings() {
      boolean deleteOnStorageLeaseExpiration = vAppLeaseSettings.deleteOnStorageLeaseExpiration();
      Integer storageLeaseSeconds = vAppLeaseSettings.getStorageLeaseSeconds();
      Integer deploymentLeaseSeconds = vAppLeaseSettings.getDeploymentLeaseSeconds();
      
      try {
         newVAppLeaseSettings = vAppLeaseSettings.toBuilder()
               .deleteOnStorageLeaseExpiration(!deleteOnStorageLeaseExpiration)
               .storageLeaseSeconds(storageLeaseSeconds+1)
               .deploymentLeaseSeconds(deploymentLeaseSeconds+1)
               .build();
         
         vAppLeaseSettings = orgClient.updateVAppLeaseSettings(
               orgRef.getURI(), newVAppLeaseSettings);
         
         assertTrue(equal(vAppLeaseSettings.deleteOnStorageLeaseExpiration(), !deleteOnStorageLeaseExpiration), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "vAppLeaseSettings", "deleteOnStorageLeaseExpiration"));
         assertTrue(equal(vAppLeaseSettings.getStorageLeaseSeconds(), storageLeaseSeconds+1), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "vAppLeaseSettings", "storageLeaseSeconds"));
         assertTrue(equal(vAppLeaseSettings.getDeploymentLeaseSeconds(), deploymentLeaseSeconds+1), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "vAppLeaseSettings", "deploymentLeaseSeconds"));
         
         //TODO negative tests?
         
         Checks.checkVAppLeaseSettings(vAppLeaseSettings);
      } finally {
         vAppLeaseSettings = vAppLeaseSettings.toBuilder()
               .deleteOnStorageLeaseExpiration(deleteOnStorageLeaseExpiration)
               .storageLeaseSeconds(storageLeaseSeconds)
               .deploymentLeaseSeconds(deploymentLeaseSeconds)
               .build();
         
         vAppLeaseSettings = orgClient.updateVAppLeaseSettings(
               orgRef.getURI(), vAppLeaseSettings);
      }
   }
 
   @Test(testName = "GET /admin/org/{id}/settings/vAppTemplateLeaseSettings")
   public void testGetVAppTemplateLeaseSettings() {
      vAppTemplateLeaseSettings = orgClient.getVAppTemplateLeaseSettings(orgRef.getURI());
      
      Checks.checkVAppTemplateLeaseSettings(vAppTemplateLeaseSettings);
   }
   
   @Test(testName = "PUT /admin/org/{id}/settings/vAppTemplateLeaseSettings", 
         dependsOnMethods = { "testGetVAppTemplateLeaseSettings" }, enabled = false) // FIXME: fails with 403 forbidden
   public void testUpdateVAppTemplateLeaseSettings() {
      boolean deleteOnStorageLeaseExpiration = vAppTemplateLeaseSettings.deleteOnStorageLeaseExpiration();
      Integer storageLeaseSeconds = vAppTemplateLeaseSettings.getStorageLeaseSeconds();
      
      try {
         newVAppTemplateLeaseSettings = vAppTemplateLeaseSettings.toBuilder()
               .deleteOnStorageLeaseExpiration(!deleteOnStorageLeaseExpiration)
               .storageLeaseSeconds(storageLeaseSeconds+1)
               .build();
         
         vAppTemplateLeaseSettings = orgClient.updateVAppTemplateLeaseSettings(
               orgRef.getURI(), newVAppTemplateLeaseSettings);
         
         assertTrue(equal(vAppTemplateLeaseSettings.deleteOnStorageLeaseExpiration(), !deleteOnStorageLeaseExpiration), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "vAppTemplateLeaseSettings", "deleteOnStorageLeaseExpiration"));
         assertTrue(equal(vAppTemplateLeaseSettings.getStorageLeaseSeconds(), storageLeaseSeconds+1), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "vAppTemplateLeaseSettings", "storageLeaseSeconds"));
         
         //TODO negative tests?
         
         Checks.checkVAppTemplateLeaseSettings(vAppTemplateLeaseSettings);
      } finally {
         vAppTemplateLeaseSettings = vAppTemplateLeaseSettings.toBuilder()
               .deleteOnStorageLeaseExpiration(deleteOnStorageLeaseExpiration)
               .storageLeaseSeconds(storageLeaseSeconds)
               .build();
         
         vAppTemplateLeaseSettings = orgClient.updateVAppTemplateLeaseSettings(
               orgRef.getURI(), vAppTemplateLeaseSettings);
      }
   }
   
   @Test(testName = "GET /admin/org/{id}/settings/settings",
      dependsOnMethods = { "testGetGeneralSettings", 
         "testGetVAppLeaseSettings", 
         "testGetVAppTemplateLeaseSettings", 
         "testGetLdapSettings", 
         "testGetEmailSettings", 
         "testGetPasswordPolicy"})
   public void testGetSettings() {
      settings = orgClient.getSettings(orgRef.getURI());
      
      Checks.checkOrgSettings(settings);
   }
   
   @Test(testName = "PUT /admin/org/{id}/settings/settings",
         dependsOnMethods = { "testUpdateGeneralSettings", 
         "testUpdateVAppLeaseSettings", 
         "testUpdateVAppTemplateLeaseSettings", 
         "testUpdateEmailSettings", 
         "testUpdatePasswordPolicy"}, 
          enabled = false )
   public void testUpdateSettings() {
      try {
         newSettings = settings.toBuilder()
               .generalSettings(newGeneralSettings)
               .vAppLeaseSettings(newVAppLeaseSettings)
               .vAppTemplateLeaseSettings(newVAppTemplateLeaseSettings)
               .ldapSettings(newLdapSettings)
               .emailSettings(newEmailSettings)
               .passwordPolicy(newPasswordPolicy)
               .build();
         
         settings = orgClient.updateSettings(
               orgRef.getURI(), newSettings);
         
         assertTrue(equal(settings.getGeneralSettings(), newGeneralSettings), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "orgSettings", "generalSettings"));
         assertTrue(equal(settings.getVAppLeaseSettings(), newVAppLeaseSettings), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "orgSettings", "vAppLeaseSettings"));
         assertTrue(equal(settings.getVAppTemplateLeaseSettings(), newVAppTemplateLeaseSettings), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "orgSettings", "vAppTemplateLeaseSettings"));
         assertTrue(equal(settings.getLdapSettings(), newLdapSettings), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "orgSettings", "ldapSettings"));
         assertTrue(equal(settings.getEmailSettings(), newEmailSettings), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "orgSettings", "emailSettings"));
         assertTrue(equal(settings.getPasswordPolicy(), newPasswordPolicy), 
               String.format(OBJ_FIELD_UPDATABLE, 
               "orgSettings", "passwordPolicy"));
         
         //TODO negative tests?
         
         Checks.checkOrgSettings(settings);
      } finally {
         settings = settings.toBuilder()
               .generalSettings(generalSettings)
               .vAppLeaseSettings(vAppLeaseSettings)
               .vAppTemplateLeaseSettings(vAppTemplateLeaseSettings)
               .ldapSettings(ldapSettings)
               .emailSettings(emailSettings)
               .passwordPolicy(passwordPolicy)
               .build();
         
         settings = orgClient.updateSettings(
               orgRef.getURI(), settings);
      }
   }
}
