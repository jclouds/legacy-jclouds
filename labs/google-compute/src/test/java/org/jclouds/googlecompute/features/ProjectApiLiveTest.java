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

package org.jclouds.googlecompute.features;

/**
 * @author David Alves
 */

import org.jclouds.googlecompute.domain.Metadata;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.domain.Project;
import org.jclouds.googlecompute.internal.BaseGoogleComputeApiLiveTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * @author David Alves
 */
public class ProjectApiLiveTest extends BaseGoogleComputeApiLiveTest {

   private static final String METADATA_ITEM_KEY = "projectLiveTestTestProp";
   private static final String METADATA_ITEM_VALUE = "projectLiveTestTestValue";

   private ProjectApi projectApi() {
      return context.getApi().getProjectApi();
   }

   private Project project;
   private int initialMetadataSize;

   @Test(groups = "live")
   public void testGetProjectWhenExists() {
      this.project = projectApi().get(getUserProject());
      assertNotNull(project);
      assertNotNull(project.getId());
      assertNotNull(project.getName());
   }

   @Test(groups = "live")
   public void testGetProjectWhenNotExists() {
      Project project = projectApi().get("0000000000");
      assertNull(project);
   }

   @Test(groups = "live", dependsOnMethods = "testGetProjectWhenExists")
   public void addItemToMetadata() {
      this.initialMetadataSize = project.getCommonInstanceMetadata().getItems().size();
      assertOperationDoneSucessfully(addItemToMetadata(projectApi(), getUserProject(), METADATA_ITEM_KEY,
              METADATA_ITEM_VALUE), 20);
      this.project = projectApi().get(getUserProject());
      assertNotNull(project);
      assertTrue(this.project.getCommonInstanceMetadata().asItemsMap().containsKey(METADATA_ITEM_KEY),
              this.project.toString());
      assertEquals(this.project.getCommonInstanceMetadata().asItemsMap().get(METADATA_ITEM_KEY),
              METADATA_ITEM_VALUE);
   }

   @Test(groups = "live", dependsOnMethods = "addItemToMetadata")
   public void testDeleteItemFromMetadata() {
      assertOperationDoneSucessfully(deleteItemFromMetadata(projectApi(), getUserProject(), METADATA_ITEM_KEY), 20);
      this.project = projectApi().get(getUserProject());
      assertNotNull(project);
      assertFalse(project.getCommonInstanceMetadata().asItemsMap().containsKey(METADATA_ITEM_KEY));
      assertSame(this.project.getCommonInstanceMetadata().asItemsMap().size(), initialMetadataSize);
   }

   /**
    * Adds an item to the Projects's metadata
    * <p/>
    * Beyond it's use here it is also used as a cheap way of generating Operations to both test the OperationApi and
    * the pagination system.
    */
   public static Operation addItemToMetadata(ProjectApi projectApi, String projectName, String key, String value) {
      Project project = projectApi.get(projectName);
      assertNotNull(project);
      Metadata metadata = project.getCommonInstanceMetadata().toBuilder()
              .addItem(key, value)
              .build();
      return projectApi.setCommonInstanceMetadata(projectName, metadata);
   }

   /**
    * Deletes an item from the Projects's metadata
    * <p/>
    * Beyond it's use here it is also used as a cheap way of generating Operation's to both test the OperationApi and
    * the pagination system.
    */
   public static Operation deleteItemFromMetadata(ProjectApi projectApi, String projectName, String key) {
      Project project = projectApi.get(projectName);
      assertNotNull(project);
      Metadata metadata = project.getCommonInstanceMetadata().toBuilder()
              .removeItem(key)
              .build();
      return projectApi.setCommonInstanceMetadata(projectName, metadata);
   }


}
