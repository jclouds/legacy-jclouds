/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.features.systemadmin;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.CONDITION_FMT;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.jclouds.vcloud.director.v1_5.AbstractVAppApiLiveTest;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.features.VAppApi;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Allows us to test the {@link VAppApi} allowed to system administrators
 * 
 * @author andrea turli
 */
@Test(singleThreaded = true, testName = "SystemAdminVAppApiLiveTest")
public class SystemAdminVAppApiLiveTest extends AbstractVAppApiLiveTest {

   private boolean testUserCreated = false;

   @BeforeClass(alwaysRun = true)
   protected void setupRequiredEntities() {

      if (adminContext != null) {
         userUrn = adminContext.getApi().getUserApi().addUserToOrg(randomTestUser("VAppAccessTest"), org.getId())
                  .getId();
      }
   }

   @AfterClass(alwaysRun = true, dependsOnMethods = { "cleanUpEnvironment" })
   public void cleanUp() {
      if (adminContext != null && testUserCreated && userUrn != null) {
         try {
            adminContext.getApi().getUserApi().remove(userUrn);
         } catch (Exception e) {
            logger.warn(e, "Error when deleting user");
         }
      }
   }

   @Test(description = "POST /vApp/{id}/action/enterMaintenanceMode", groups = { "systemAdmin" })
   public void testEnterMaintenanceMode() {

      // Do this to a new vApp, so don't mess up subsequent tests by making the vApp read-only
      VApp temp = instantiateVApp();
      DeployVAppParams params = DeployVAppParams.builder()
               .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS)).notForceCustomization()
               .notPowerOn().build();
      Task deployVApp = vAppApi.deploy(temp.getId(), params);
      assertTaskSucceedsLong(deployVApp);

      try {
         // Method under test
         vAppApi.enterMaintenanceMode(temp.getId());

         temp = vAppApi.get(temp.getId());
         assertTrue(temp.isInMaintenanceMode(),
                  String.format(CONDITION_FMT, "InMaintenanceMode", "TRUE", temp.isInMaintenanceMode()));

         // Exit maintenance mode
         vAppApi.exitMaintenanceMode(temp.getId());
      } finally {
         cleanUpVApp(temp);
      }
   }

   @Test(description = "POST /vApp/{id}/action/exitMaintenanceMode", dependsOnMethods = { "testEnterMaintenanceMode" }, groups = { "systemAdmin" })
   public void testExitMaintenanceMode() {
      // Do this to a new vApp, so don't mess up subsequent tests by making the vApp read-only
      VApp temp = instantiateVApp();
      DeployVAppParams params = DeployVAppParams.builder()
               .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS)).notForceCustomization()
               .notPowerOn().build();
      Task deployVApp = vAppApi.deploy(temp.getId(), params);
      assertTaskSucceedsLong(deployVApp);

      try {
         // Enter maintenance mode
         vAppApi.enterMaintenanceMode(temp.getId());

         // Method under test
         vAppApi.exitMaintenanceMode(temp.getId());

         temp = vAppApi.get(temp.getId());
         assertFalse(temp.isInMaintenanceMode(),
                  String.format(CONDITION_FMT, "InMaintenanceMode", "FALSE", temp.isInMaintenanceMode()));
      } finally {
         cleanUpVApp(temp);
      }
   }

}
