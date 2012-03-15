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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REF_REQ_LIVE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Group;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests live behavior of {@link AdminGroupClient}.
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin", "group" }, singleThreaded = true, testName = "GroupClientLiveTest")
public class GroupClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String GROUP = "admin group";

   /*
    * Convenience references to API clients.
    */

   private GroupClient groupClient;

   /*
    * Shared state between dependant tests.
    */
   private Reference groupRef;
   private Group group;

   @Override
   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      groupClient = context.getApi().getGroupClient();
      groupRef = Reference.builder()
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/group/???"))
         .build();
   }
   
   @Test(testName = "GET /admin/group/{id}", enabled = false)
   public void testGetGroup() {
      assertNotNull(groupRef, String.format(REF_REQ_LIVE, "Group"));
      group = groupClient.getGroup(groupRef.getHref());
      
      Checks.checkGroup(group);
   }
   
   @Test(testName = "PUT /admin/group/{id}") // TODO: depends on?
   public void updateGroup() {
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
         
         group = groupClient.updateGroup(group.getHref(), group);
         
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
         
         group = groupClient.updateGroup(group.getHref(), group);
      }
   }
   
   @Test(testName = "DELETE /admin/group/{id}", enabled = false )
   public void testDeleteCatalog() {
      groupClient.deleteGroup(groupRef.getHref());
      
      Error expected = Error.builder()
            .message("???")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();
      
      try {
         group = groupClient.getGroup(groupRef.getHref());
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
         group = null;
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
      
      if (group != null) { // guard against NPE on the .toStrings
         assertNull(group, String.format(OBJ_DEL, GROUP, group.toString()));
      }
   }
}
