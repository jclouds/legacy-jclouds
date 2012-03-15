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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_DEL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_UPDATABLE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests live behavior of {@link AdminGroupClient}.
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

   @Override
   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      userClient = context.getApi().getUserClient();
      orgRef = Iterables.getFirst(context.getApi().getOrgClient().getOrgList().getOrgs(), null).toAdminReference(endpoint);
   }
   
   @Test(testName = "POST /admin/org/{id}/users")
   public void testCreateUser() {
      User newUser = User.builder()
         .name("test")
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
         .role(Reference.builder() // FIXME: auto-fetch a role? or inject
            .name("vApp User")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/role/ff1e0c91-1288-3664-82b7-a6fa303af4d1"))
            .build())
         .password("password")
//       .group()
         .build();
      user = userClient.createUser(orgRef.getHref(), newUser);
      
      Checks.checkUser(user);
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
//         .name("new"+oldUser.getName())
         .fullName("new"+oldUser.getFullName())
         .emailAddress("new"+oldUser.getEmailAddress())
         .telephone("1-"+oldUser.getTelephone())
         .isEnabled(true)
         .im("new"+oldUser.getIM())
         .isAlertEnabled(true)
         .alertEmailPrefix("new"+oldUser.getAlertEmailPrefix())
         .alertEmail("new"+oldUser.getAlertEmail())
//         .role(Reference.builder() // FIXME: auto-fetch a role? or inject
//            .name("vApp Author")
//            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/role/1bf4457f-a253-3cf1-b163-f319f1a31802"))
//            .build())
         .storedVmQuota(1)
         .deployedVmQuota(1)
         .password("newPassword")
         .build();
      
      try {
         userClient.updateUser(user.getHref(), newUser);
         user = userClient.getUser(user.getHref());
         Checks.checkUser(user);
//         assertTrue(equal(user.getName(), newUser.getName()), 
//               String.format(OBJ_FIELD_UPDATABLE, USER, "name"));
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
         
         // FIXME: assert password is changed with session client?
      } finally {
         userClient.updateUser(user.getHref(), oldUser);
         user = userClient.getUser(user.getHref());
      }
   }
 
// 

   
   @Test(testName = "POST /admin/user/{id}/action/unlock",
         dependsOnMethods = { "testUpdateUser" } )
   public void testUnlockUser() {
      //TODO: check previous tests a) enabled lockout, b) set password
      //TODO: attempt too many times with the wrong password
      //TODO: verify access is denied
      //TODO: unlock user
      //TODO: verify access is renewed
   }
 
   @Test(testName = "DELETE /admin/user/{id}",
         dependsOnMethods = { "testUnlockUser" } )
   public void testDeleteUser() {
      userClient.deleteUser(user.getHref());
      
      Error expected = Error.builder()
            .message("No access to entity \"(com.vmware.vcloud.entity.user:"+
                  user.getId().substring("urn:vcloud:user:".length())+")\".")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();
      
      try {
         user = userClient.getUser(user.getHref());
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
         user = null;
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
      
      if (user != null) { // guard against NPE on the .toStrings
         assertNull(user, String.format(OBJ_DEL, USER, user.toString()));
      }
   }
}
