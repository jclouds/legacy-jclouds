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

import static com.google.common.base.Objects.equal;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_UPDATABLE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertFalse;

import java.net.URI;
import java.util.Random;

import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.RestContext;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.OrgPasswordPolicySettings;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.RoleReferences;
import org.jclouds.vcloud.director.v1_5.domain.SessionWithToken;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.jclouds.vcloud.director.v1_5.login.SessionClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests live behavior of {@link UserClient}.
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin", "adminUser" }, singleThreaded = true, testName = "UserClientLiveTest")
public class UserClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String USER = "admin user";

   /*
    * Convenience references to API clients.
    */
   UserClient userClient;

   /*
    * Shared state between dependant tests.
    */
   private Reference orgRef;
   private User user;
   private static Random random = new Random();

   @Override
   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      userClient = context.getApi().getUserClient();
      orgRef = Iterables.getFirst(context.getApi().getOrgClient().getOrgList().getOrgs(), null).toAdminReference(endpoint);
   }
   
   @AfterClass(alwaysRun = true)
   public void cleanUp() throws Exception {
      if (user != null) {
         try {
            userClient.deleteUser(user.getHref());
         } catch (VCloudDirectorException e) {
            // ignore; user probably already deleted
         }
      }
   }
   
   @Test(testName = "POST /admin/org/{id}/users")
   public void testCreateUser() {
      User newUser = randomTestUser("testCreateUser", context);
      user = userClient.createUser(orgRef.getHref(), newUser);
      Checks.checkUser(newUser);
   }
   
   @Test(testName = "GET /admin/user/{id}",
         dependsOnMethods = { "testCreateUser" })
   public void testGetUser() {
      user = userClient.getUser(user.getHref());
      
      Checks.checkUser(user);
   }
 
   @Test(testName = "PUT /admin/user/{id}",
         dependsOnMethods = { "testGetUser" })
   public void testUpdateUser() {
      User oldUser = user.toBuilder().build();
      User newUser = user.toBuilder()
         .fullName("new"+oldUser.getFullName())
         .emailAddress("new"+oldUser.getEmailAddress())
         .telephone("1-"+oldUser.getTelephone())
         .isEnabled(true)
         .im("new"+oldUser.getIM())
         .isAlertEnabled(true)
         .alertEmailPrefix("new"+oldUser.getAlertEmailPrefix())
         .alertEmail("new"+oldUser.getAlertEmail())
         .storedVmQuota(1)
         .deployedVmQuota(1)
         .password("newPassword")
         // TODO test setting other fields?
//         .name("new"+oldUser.getName())
         .role(nonVAppUserRole(context))
         .build();
      
      userClient.updateUser(user.getHref(), newUser);
      user = userClient.getUser(user.getHref());
      
      Checks.checkUser(user);
      assertTrue(equal(user.getFullName(), newUser.getFullName()), 
            String.format(OBJ_FIELD_UPDATABLE, USER, "fullName"));
      assertTrue(equal(user.getEmailAddress(), newUser.getEmailAddress()), 
            String.format(OBJ_FIELD_UPDATABLE, USER, "emailAddress"));
      assertTrue(equal(user.getTelephone(), newUser.getTelephone()), 
            String.format(OBJ_FIELD_UPDATABLE, USER, "telephone"));
      assertTrue(equal(user.isEnabled(), newUser.isEnabled()), 
            String.format(OBJ_FIELD_UPDATABLE, USER, "isEnabled"));
      assertTrue(equal(user.getIM(), newUser.getIM()), 
            String.format(OBJ_FIELD_UPDATABLE, USER, "im"));
      assertTrue(equal(user.isAlertEnabled(), newUser.isAlertEnabled()), 
            String.format(OBJ_FIELD_UPDATABLE, USER, "isAlertEnabled"));
      assertTrue(equal(user.getAlertEmailPrefix(), newUser.getAlertEmailPrefix()), 
            String.format(OBJ_FIELD_UPDATABLE, USER, "alertEmailPrefix"));
      assertTrue(equal(user.getAlertEmail(), newUser.getAlertEmail()), 
            String.format(OBJ_FIELD_UPDATABLE, USER, "alertEmail"));
//         assertTrue(equal(user.getRole(), newUser.getRole()), 
//               String.format(OBJ_FIELD_UPDATABLE, USER, "role"));
      assertTrue(equal(user.getStoredVmQuota(), newUser.getStoredVmQuota()), 
            String.format(OBJ_FIELD_UPDATABLE, USER, "storedVmQuota"));
      assertTrue(equal(user.getDeployedVmQuota(), newUser.getDeployedVmQuota()), 
            String.format(OBJ_FIELD_UPDATABLE, USER, "deployedVmQuota"));
           
      // session client isn't typically exposed to the user, as it is implicit
      SessionClient sessionClient = context.utils().injector().getInstance(SessionClient.class);

      // Check the user can really login with the changed password
      // NOTE: the password is NOT returned in the User object returned from the server
      SessionWithToken sessionWithToken = sessionClient.loginUserInOrgWithPassword(URI.create(endpoint + "/sessions"), user.getName(), orgRef.getName(), "newPassword");
      assertNotNull(sessionWithToken.getToken());
      sessionClient.logoutSessionWithToken(sessionWithToken.getSession().getHref(), sessionWithToken.getToken());
   }
 
   @Test(testName = "POST /admin/user/{id}/action/unlock", dependsOnMethods = { "testUpdateUser" })
   public void testUnlockUser() {
      // Need to know how many times to fail login to lock account
      AdminOrgClient adminOrgClient = context.getApi().getAdminOrgClient();
      OrgPasswordPolicySettings settingsToRevertTo = null;

      // session client isn't typically exposed to the user, as it is implicit
      SessionClient sessionClient = context.utils().injector().getInstance(SessionClient.class);
      
      OrgPasswordPolicySettings settings = adminOrgClient.getSettings(orgRef.getHref()).getPasswordPolicy();
      assertNotNull(settings);

      // Adjust account settings so we can lock the account - be careful to not set invalidLoginsBeforeLockout too low!
      if (!settings.isAccountLockoutEnabled()) {
         settingsToRevertTo = settings;
         settings = settings.toBuilder().accountLockoutEnabled(true).invalidLoginsBeforeLockout(5).build();
         settings = adminOrgClient.updatePasswordPolicy(orgRef.getHref(), settings);
      }

      assertTrue(settings.isAccountLockoutEnabled());
      
      for (int i=0; i<settings.getInvalidLoginsBeforeLockout()+1; i++) {
         try {
            sessionClient.loginUserInOrgWithPassword(URI.create(endpoint + "/sessions"), user.getName(), orgRef.getName(), "wrongpassword!");
            fail("Managed to login using the wrong password!");
         } catch(AuthorizationException ex) {            
         }
      }
      
      user = userClient.getUser(user.getHref());
      assertTrue(user.isLocked());

      try {
         sessionClient.loginUserInOrgWithPassword(URI.create(endpoint + "/sessions"), user.getName(), orgRef.getName(), "newPassword");
         fail("Managed to login to locked account!");
      } catch(AuthorizationException ex) {
      }
      
      userClient.unlockUser(user.getHref());

      user = userClient.getUser(user.getHref());
      assertFalse(user.isLocked());

      // Double-check the user can now login again
      SessionWithToken sessionWithToken = sessionClient.loginUserInOrgWithPassword(URI.create(endpoint + "/sessions"), user.getName(), orgRef.getName(), "newPassword");
      assertNotNull(sessionWithToken.getToken());
      sessionClient.logoutSessionWithToken(sessionWithToken.getSession().getHref(), sessionWithToken.getToken());
      
      // Return account settings to the previous values, if necessary
      if (settingsToRevertTo != null) {
         adminOrgClient.updatePasswordPolicy(orgRef.getHref(), settingsToRevertTo);
      }
   }
 
   @Test(testName = "DELETE /admin/user/{id}",
         dependsOnMethods = { "testCreateUser" } )
   public void testDeleteUser() {
      // Create a user to be deleted (so we remove dependencies on test ordering)
      String name = name("a");
      User newUser = User.builder()
         .name(name)
         .role(Reference.builder() // FIXME: auto-fetch a role? or inject
                  .name("vApp User")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/role/ff1e0c91-1288-3664-82b7-a6fa303af4d1"))
                  .build())
         .password("password")
         .build();
      User userToBeDeleted = userClient.createUser(orgRef.getHref(), newUser);

      // Delete the user
      userClient.deleteUser(userToBeDeleted.getHref());

      // Confirm cannot no longer be accessed 
      Error expected = Error.builder()
            .message("No access to entity \"(com.vmware.vcloud.entity.user:"+
                     userToBeDeleted.getId().substring("urn:vcloud:user:".length())+")\".")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();
      
      try {
         userClient.getUser(userToBeDeleted.getHref());
         fail("Should give HTTP 403 error for accessing user after deleting it ("+userToBeDeleted+")");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      }
   }
   
   public static Reference vAppUserRole(RestContext<VCloudDirectorClient, VCloudDirectorAsyncClient> context) {
      RoleReferences roles = context.getApi().getAdminQueryClient().roleReferencesQueryAll();
      for (Reference role : roles.getReferences()) {
         if (equal(role.getName(), "vApp User")) {
            return Reference.builder().fromReference(role).build();
         }
      }
      
      return null;
   }
   
   public static Reference nonVAppUserRole(RestContext<VCloudDirectorClient, VCloudDirectorAsyncClient> context) {
      RoleReferences roles = context.getApi().getAdminQueryClient().roleReferencesQueryAll();
      for (Reference role : roles.getReferences()) {
         if (!equal(role.getName(), "vApp User")) {
            return Reference.builder().fromReference(role).build();
         }
      }
      
      return null;
   }
   
   public static User randomTestUser(String prefix, RestContext<VCloudDirectorClient, VCloudDirectorAsyncClient> context) {
      return randomTestUser(prefix, vAppUserRole(context));
   }
   
   public static User randomTestUser(String prefix, Reference role) {
      return User.builder()
         .name(name(prefix)+random.nextInt(999999))
         .fullName("testFullName")
         .emailAddress("test@test.com")
         .telephone("555-1234")
         .isEnabled(false)
         .im("testIM")
         .isAlertEnabled(false)
         .alertEmailPrefix("testPrefix")
         .alertEmail("testAlert@test.com")
         .isExternal(false)
         .isGroupRole(false)
         .role(role)
         .password("password")
         .build();
   }
}
