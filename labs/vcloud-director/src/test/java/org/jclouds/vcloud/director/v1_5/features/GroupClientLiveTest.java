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

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Group;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests live behavior of {@link AdminCatalogClient}.
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin", "group" }, singleThreaded = true, testName = "CatalogClientLiveTest")
public class GroupClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String GROUP = "admin group";

   /*
    * Convenience references to API clients.
    */

   private GroupClient groupClient;

   /*
    * Shared state between dependant tests.
    */
   private ReferenceType<?> groupRef;
   private Group group;

   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      groupClient = context.getApi().getGroupClient();
      groupRef = Reference.builder()
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/???"))
         .build();
   }
   
   @Test(testName = "GET /admin/group/{id}", enabled = false)
   public void testGetGroup() {
      assertNotNull(groupRef, String.format(REF_REQ_LIVE, "Group"));
      group = groupClient.getGroup(groupRef.getURI());
      
      Checks.checkGroup(group);
   }
}
