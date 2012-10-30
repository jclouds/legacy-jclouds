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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkVm;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.jclouds.vcloud.director.v1_5.AbstractVAppApiLiveTest;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecordType;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.features.VmApi;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of the {@link VmApi}.
 * 
 * @author andrea turli
 */
@Test(groups = { "live", "systemAdmin" }, singleThreaded = true, testName = "SystemAdminVmApiLiveTest")
public class SystemAdminVmApiLiveTest extends AbstractVAppApiLiveTest {

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
            logger.warn("Error when deleting user: %s", e.getMessage());
         }
      }
   }

   /**
    * @see VmApi#get(String)
    */
   @Test(description = "GET /vApp/{id}")
   public void testGetVm() {
      // The method under test
      vm = vmApi.get(vmUrn);

      // Check the retrieved object is well formed
      checkVm(vm);

      // Check the required fields are set
      assertEquals(vm.isDeployed(), Boolean.FALSE,
               String.format(OBJ_FIELD_EQ, VM, "deployed", "FALSE", vm.isDeployed().toString()));

      // Check status
      assertVmStatus(vmUrn, Status.POWERED_OFF);
   }
   
   // NOTE This test is disabled, as it is not possible to look up datastores using the User API
   @Test(description = "POST /vApp/{id}/action/relocate", dependsOnMethods = { "testGetVm" })
   public void testRelocate() {
      // Relocate to the last of the available datastores
      QueryResultRecords records = adminContext.getApi().getQueryApi().queryAll("datastore");
      QueryResultRecordType datastore = Iterables.getLast(records.getRecords());
      RelocateParams params = RelocateParams.builder().datastore(Reference.builder().href(datastore.getHref()).build())
               .build();

      // The method under test
      Task relocate = vmApi.relocate(vmUrn, params);
      assertTrue(retryTaskSuccess.apply(relocate), String.format(TASK_COMPLETE_TIMELY, "relocate"));
   }
   
   @Test(description = "POST /vApp/{id}/action/deploy", dependsOnMethods = { "testGetVm" })
   public void testDeployVm() {
      DeployVAppParams params = DeployVAppParams.builder()
               .deploymentLeaseSeconds((int) TimeUnit.SECONDS.convert(1L, TimeUnit.HOURS)).notForceCustomization()
               .notPowerOn().build();

      // The method under test
      Task deployVm = vmApi.deploy(vmUrn, params);
      assertTrue(retryTaskSuccessLong.apply(deployVm), String.format(TASK_COMPLETE_TIMELY, "deployVm"));

      // Get the edited Vm
      vm = vmApi.get(vmUrn);

      // Check the required fields are set
      assertTrue(vm.isDeployed(), String.format(OBJ_FIELD_EQ, VM, "deployed", "TRUE", vm.isDeployed().toString()));

      // Check status
      assertVmStatus(vmUrn, Status.POWERED_OFF);
   }   
   
   @Test(description = "POST /vApp/{id}/action/consolidate", dependsOnMethods = { "testDeployVm" })
   public void testConsolidateVm() {
      // Power on Vm
      vm = powerOnVm(vmUrn);

      // The method under test
      Task consolidateVm = vmApi.consolidate(vmUrn);
      assertTrue(retryTaskSuccess.apply(consolidateVm), String.format(TASK_COMPLETE_TIMELY, "consolidateVm"));
   }
}
