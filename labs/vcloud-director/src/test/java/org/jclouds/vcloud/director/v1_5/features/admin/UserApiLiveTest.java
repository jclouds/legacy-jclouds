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
package org.jclouds.vcloud.director.v1_5.features.admin;

import static com.google.common.base.Objects.equal;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_UPDATABLE;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkUser;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertFalse;

import java.net.URI;

import org.jclouds.rest.AuthorizationException;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Role.DefaultRoles;
import org.jclouds.vcloud.director.v1_5.domain.SessionWithToken;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgPasswordPolicySettings;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.jclouds.vcloud.director.v1_5.login.SessionApi;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests live behavior of {@link UserApi}.
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin" }, singleThreaded = true, testName = "UserApiLiveTest")
public class UserApiLiveTest extends BaseVCloudDirectorApiLiveTest {
   
   public static final String USER = "admin user";

   /*
    * Convenience references to API apis.
    */
   UserApi userApi;

   /*
    * Shared state between dependant tests.
    */
   private Reference orgRef;
   private User user;

   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredApis() {
      userApi = adminContext.getApi().getUserApi();
      orgRef = Iterables.getFirst(context.getApi().getOrgApi().getOrgList(), null).toAdminReference(endpoint);
   }
   
   @AfterClass(alwaysRun = true)
   public void cleanUp() throws Exception {
      if (user != null) {
         try {
            userApi.deleteUser(user.getHref());
         } catch (Exception e) {
            logger.warn(e, "Error deleting user '%s'", user.getName());
         }
      }
   }
   
   @Test(description = "POST /admin/org/{id}/users")
   public void testCreateUser() {
      User newUser = randomTestUser("testCreateUser");
      user = userApi.createUser(orgRef.getHref(), newUser);
      checkUser(newUser);
   }
   
   @Test(description = "GET /admin/user/{id}", dependsOnMethods = { "testCreateUser" })
   public void testGetUser() {
      user = userApi.getUser(user.getHref());
      
      checkUser(user);
   }
 
   @Test(description = "PUT /admin/user/{id}", dependsOnMethods = { "testGetUser" })
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
         .role(getRoleReferenceFor(DefaultRoles.AUTHOR.value()))
         .build();
      
      userApi.updateUser(user.getHref(), newUser);
      user = userApi.getUser(user.getHref());
      
      checkUser(user);
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
           
      // session api isn't typically exposed to the user, as it is implicit
      SessionApi sessionApi = context.utils().injector().getInstance(SessionApi.class);

      // Check the user can really login with the changed password
      // NOTE: the password is NOT returned in the User object returned from the server
      SessionWithToken sessionWithToken = sessionApi.loginUserInOrgWithPassword(URI.create(endpoint + "/sessions"), user.getName(), orgRef.getName(), "newPassword");
      assertNotNull(sessionWithToken.getToken());
      sessionApi.logoutSessionWithToken(sessionWithToken.getSession().getHref(), sessionWithToken.getToken());
   }
 
   @Test(description = "POST /admin/user/{id}/action/unlock", dependsOnMethods = { "testUpdateUser" })
   public void testUnlockUser() {
      // Need to know how many times to fail login to lock account
      AdminOrgApi adminOrgApi = adminContext.getApi().getOrgApi();
      OrgPasswordPolicySettings settingsToRevertTo = null;

      // session api isn't typically exposed to the user, as it is implicit
      SessionApi sessionApi = context.utils().injector().getInstance(SessionApi.class);
      
      OrgPasswordPolicySettings settings = adminOrgApi.getSettings(orgRef.getHref()).getPasswordPolicy();
      assertNotNull(settings);

      // Adjust account settings so we can lock the account - be careful to not set invalidLoginsBeforeLockout too low!
      if (!settings.isAccountLockoutEnabled()) {
         settingsToRevertTo = settings;
         settings = settings.toBuilder().accountLockoutEnabled(true).invalidLoginsBeforeLockout(5).build();
         settings = adminOrgApi.updatePasswordPolicy(orgRef.getHref(), settings);
      }

      assertTrue(settings.isAccountLockoutEnabled());
      
      for (int i = 0; i < settings.getInvalidLoginsBeforeLockout() + 1; i++) {
         try {
            sessionApi.loginUserInOrgWithPassword(URI.create(endpoint + "/sessions"), user.getName(), orgRef.getName(), "wrongpassword!");
            fail("Managed to login using the wrong password!");
         } catch (AuthorizationException e) {
         } catch (Exception e) {
            fail("Expected AuthorizationException", e);
         }
      }
      
      user = userApi.getUser(user.getHref());
      assertTrue(user.isLocked());

      try {
         sessionApi.loginUserInOrgWithPassword(URI.create(endpoint + "/sessions"), user.getName(), orgRef.getName(), "newPassword");
         fail("Managed to login to locked account!");
      } catch (AuthorizationException e) {
      } catch (Exception e) {
         fail("Expected AuthorizationException", e);
      }
      
      userApi.unlockUser(user.getHref());

      user = userApi.getUser(user.getHref());
      assertFalse(user.isLocked());

      // Double-check the user can now login again
      SessionWithToken sessionWithToken = sessionApi.loginUserInOrgWithPassword(URI.create(endpoint + "/sessions"), user.getName(), orgRef.getName(), "newPassword");
      assertNotNull(sessionWithToken.getToken());
      sessionApi.logoutSessionWithToken(sessionWithToken.getSession().getHref(), sessionWithToken.getToken());
      
      // Return account settings to the previous values, if necessary
      if (settingsToRevertTo != null) {
         adminOrgApi.updatePasswordPolicy(orgRef.getHref(), settingsToRevertTo);
      }
   }
 
   @Test(description = "DELETE /admin/user/{id}", dependsOnMethods = { "testCreateUser" })
   public void testDeleteUser() {
      // Create a user to be deleted (so we remove dependencies on test ordering)
      User newUser = randomTestUser("testDeleteUser"+getTestDateTimeStamp());
      User userToBeDeleted = userApi.createUser(orgRef.getHref(), newUser);

      // Delete the user
      userApi.deleteUser(userToBeDeleted.getHref());

      // Confirm cannot no longer be accessed 
      User deleted = userApi.getUser(userToBeDeleted.getHref());
      assertNull(deleted);
   }
}
