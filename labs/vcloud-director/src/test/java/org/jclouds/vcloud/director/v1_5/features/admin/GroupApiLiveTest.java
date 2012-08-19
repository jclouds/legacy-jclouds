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
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Group;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgLdapSettings;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests live behavior of {@link AdminGroupApi}.
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin" }, singleThreaded = true, testName = "GroupApiLiveTest")
public class GroupApiLiveTest extends BaseVCloudDirectorApiLiveTest {
   
   public static final String GROUP = "admin group";

   /*
    * Convenience references to API apis.
    */
   private GroupApi groupApi;

   /*
    * Shared state between dependant tests.
    */
   private Reference groupRef;
   private Group group;
   private OrgLdapSettings oldLdapSettings, newLdapSettings;
   
   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredApis() {
      groupApi = adminContext.getApi().getGroupApi();
      Reference orgRef = null;
      
      // TODO: requisite LDAP settings
//      oldLdapSettings = adminContext.getApi().getAdminOrgApi().getLdapSettings(orgRef.getHref());
//      OrgLdapSettings newLdapSettings = oldLdapSettings.toBuilder()
//         .ldapMode(OrgLdapSettings.LdapMode.SYSTEM)  
//         .build();
//      context.getApi().getAdminOrgApi().editLdapSettings(newLdapSettings);
   }
   
   @Test(description = "POST /admin/org/{id}/groups")
   public void testAddGroup() {
      fail("LDAP not configured, group api isn't currently testable.");
//      group = groupApi.createGroup(orgUri, Group.builder()
//         .build();
      
      Checks.checkGroup(group);
   }
   
   @Test(description = "GET /admin/group/{id}", dependsOnMethods = { "testAddGroup" })
   public void testGetGroup() {
      group = groupApi.getGroup(groupRef.getHref());
      
      Checks.checkGroup(group);
   }
   
   @Test(description = "PUT /admin/group/{id}", dependsOnMethods = { "testGetGroup" } )
   public void testEditGroup() {
      String oldName = group.getName();
      String newName = "new "+oldName;
      String oldDescription = group.getDescription();
      String newDescription = "new "+oldDescription;
      //TODO: check other modifiables
      
      try {
         group = group.toBuilder()
               .name(newName)
               .description(newDescription)
               .build();
         
         group = groupApi.editGroup(group.getHref(), group);
         
         assertTrue(equal(group.getName(), newName), String.format(OBJ_FIELD_UPDATABLE, GROUP, "name"));
         assertTrue(equal(group.getDescription(), newDescription),
               String.format(OBJ_FIELD_UPDATABLE, GROUP, "description"));
         
         //TODO negative tests?
         
         Checks.checkGroup(group);
      } finally {
         group = group.toBuilder()
               .name(oldName)
               .description(oldDescription)
               .build();
         
         group = groupApi.editGroup(group.getHref(), group);
      }
   }
   
   @Test(description = "DELETE /admin/group/{id}", dependsOnMethods = { "testEditGroup" } )
   public void testRemoveGroup() {
      groupApi.removeGroup(groupRef.getHref());
      
      // TODO stronger assertion of error expected
//      Error expected = Error.builder()
//            .message("???")
//            .majorErrorCode(403)
//            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
//            .build();
      
      try {
         group = groupApi.getGroup(groupRef.getHref());
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         // success
         group = null;
      }
   }
}
