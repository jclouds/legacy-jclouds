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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.CONDITION_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.CORRECT_VALUE_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.NOT_EMPTY_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkControlAccessParams;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadata;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataValue;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkOrg;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkReferenceType;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.AdminCatalog;
import org.jclouds.vcloud.director.v1_5.domain.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.OrgList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
* Tests live behavior of {@link OrgClient}.
* 
* @author grkvlt@apache.org
*/
@Test(groups = { "live", "user", "org" }, singleThreaded = true, testName = "OrgClientLiveTest")
public class OrgClientLiveTest extends BaseVCloudDirectorClientLiveTest {

   /*
    * Convenience references to API clients.
    */

   private OrgClient orgClient;
   private URI catalogRef;
   private String testCatalogId;

   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredClients() {
      orgClient = context.getApi().getOrgClient();
      testCatalogId = catalogId;
   }
   
   @AfterClass(alwaysRun = true)
   public void cleanUp() throws Exception {
      if (adminMembersSet) {
         adminContext.getApi().getOrgClient().getMetadataClient()
            .deleteMetadataEntry(toAdminUri(orgURI), "KEY");
         
         adminContext.getApi().getCatalogClient()
            .deleteCatalog(catalogRef);
      }
   }

   /*
    * Shared state between dependent tests.
    */

   private OrgList orgList;
   private URI orgURI;
   private Org org;
   private boolean adminMembersSet = false; // track if test entities have been created

   @Test(description = "GET /org")
   public void testGetOrgList() {
      // Call the method being tested
      orgList = orgClient.getOrgList();
      
      // NOTE The environment MUST have at least one organisation configured
      
      // Check test requirements
      assertFalse(Iterables.isEmpty(orgList.getOrgs()), String.format(NOT_EMPTY_OBJECT_FMT, "Org", "OrgList"));
      
      for (Reference orgRef : orgList.getOrgs()) {
         assertEquals(orgRef.getType(), VCloudDirectorMediaType.ORG, String.format(CONDITION_FMT, "Reference.Type", VCloudDirectorMediaType.ORG, orgRef.getType()));
         checkReferenceType(orgRef);
      }
   }

   @Test(description = "GET /org/{id}", dependsOnMethods = { "testGetOrgList" })
   public void testGetOrg() {
      Reference orgRef = Iterables.getFirst(orgList.getOrgs(), null);
      assertNotNull(orgRef);
      
      orgURI = orgRef.getHref();
      
      // Call the method being tested
      org = orgClient.getOrg(orgURI);

      checkOrg(org);
      
      if (adminContext != null) {
         setupAdminMembers();
      }
   }
   
   /**
    * If we're running in an admin context, it's cleaner to make temporary entities, plus eliminates the need for configuration
    */
   private void setupAdminMembers() {
      adminContext.getApi().getOrgClient().getMetadataClient().setMetadata(toAdminUri(orgURI), 
            "KEY", MetadataValue.builder().value("VALUE").build());
      
      AdminCatalog newCatalog = AdminCatalog.builder()
            .name("Test Catalog "+getTestDateTimeStamp())
            .description("created by testOrg()")
            .build();
      newCatalog = adminContext.getApi().getCatalogClient().createCatalog(toAdminUri(orgURI), newCatalog);
      
      catalogRef = newCatalog.getHref();
      testCatalogId = newCatalog.getId().substring("urn:vcloud:catalog:".length()); 
      
      adminMembersSet = true;
   }
   
   @Test(description = "GET /org/{id}/metadata", dependsOnMethods = { "testGetOrg" })
   public void testGetOrgMetadata() {
      
      // Call the method being tested
      Metadata metadata = orgClient.getMetadataClient().getMetadata(orgURI);
      
      // NOTE The environment MUST have at one metadata entry for the first organisation configured
      
      checkMetadata(metadata);
      
      // Check requirements for this test
      assertFalse(Iterables.isEmpty(metadata.getMetadataEntries()), String.format(NOT_EMPTY_OBJECT_FMT, "MetadataEntry", "Org"));
   }
   
   @Test(description = "GET /org/{id}/metadata/{key}", dependsOnMethods = { "testGetOrgMetadata" })
   public void testGetOrgMetadataValue() {
      // Call the method being tested
      MetadataValue value = orgClient.getMetadataClient().getMetadataValue(orgURI, "KEY");
      
      // NOTE The environment MUST have configured the metadata entry as '{ key="KEY", value="VALUE" )'

      String expected = "VALUE";

      checkMetadataValue(value);
      assertEquals(value.getValue(), expected, String.format(CORRECT_VALUE_OBJECT_FMT, "Value", "MetadataValue", expected, value.getValue()));
   }

   @Test(description = "GET /org/{id}/catalog/{catalogId}/controlAccess", dependsOnMethods = { "testGetOrg" })
   public void testGetControlAccess() {
      // Call the method being tested
      ControlAccessParams params = orgClient.getControlAccess(orgURI, testCatalogId);

      // Check params are well formed
      checkControlAccessParams(params);
   }

   @Test(description = "GET /org/{id}/catalog/{catalogId}/action/controlAccess", dependsOnMethods = { "testGetControlAccess" })
   public void testModifyControlAccess() {
      // Setup params
      ControlAccessParams params = orgClient.getControlAccess(orgURI, testCatalogId);

      // Call the method being tested
      ControlAccessParams modified = orgClient.modifyControlAccess(orgURI, testCatalogId, params);

      // Check params are well formed
      checkControlAccessParams(modified);
   }
}
