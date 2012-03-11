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
import org.jclouds.vcloud.director.v1_5.domain.OrgLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgVAppTemplateLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;
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
   private AdminOrg org;
   OrgVAppTemplateLeaseSettings vAppTemplateLeaseSettings;
   OrgLeaseSettings vAppLeaseSettings;

   @Override
   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      orgClient = context.getApi().getAdminOrgClient();
      orgRef = Iterables.getFirst(orgClient.getOrgList().getOrgs(), null).toAdminReference(endpoint);
      assertNotNull(orgRef, String.format(REF_REQ_LIVE, "admin org"));
   }
   
// GET /admin/org/{id}
   
// POST /admin/org/{id}/catalogs
 
// POST /admin/org/{id}/groups
 
// GET /admin/org/{id}/settings
 
// PUT /admin/org/{id}/settings
 
// GET /admin/org/{id}/settings/email
 
// PUT /admin/org/{id}/settings/email
 
// GET /admin/org/{id}/settings/general
 
// PUT /admin/org/{id}/settings/general
 
// GET /admin/org/{id}/settings/ldap
 
// GET /admin/org/{id}/settings/passwordPolicy
 
// PUT /admin/org/{id}/settings/passwordPolicy
   
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
         vAppLeaseSettings = vAppLeaseSettings.toBuilder()
               .deleteOnStorageLeaseExpiration(!deleteOnStorageLeaseExpiration)
               .storageLeaseSeconds(storageLeaseSeconds+1)
               .deploymentLeaseSeconds(deploymentLeaseSeconds+1)
               .build();
         
         vAppLeaseSettings = orgClient.updateVAppLeaseSettings(
               orgRef.getURI(), vAppLeaseSettings);
         
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
         vAppTemplateLeaseSettings = vAppTemplateLeaseSettings.toBuilder()
               .deleteOnStorageLeaseExpiration(!deleteOnStorageLeaseExpiration)
               .storageLeaseSeconds(storageLeaseSeconds+1)
               .build();
         
         vAppTemplateLeaseSettings = orgClient.updateVAppTemplateLeaseSettings(
               orgRef.getURI(), vAppTemplateLeaseSettings);
         
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
}
