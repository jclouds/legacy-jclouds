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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.*;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.*;
import static org.testng.Assert.*;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.OrgList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
* Tests live behavior of {@link OrgClient}.
* 
* @author grkvlt@apache.org
*/
@Test(groups = { "live", "apitests" }, testName = "OrgClientLiveTest")
public class OrgClientLiveTest extends BaseVCloudDirectorClientLiveTest {

   /*
    * Convenience references to API clients.
    */

   private OrgClient orgClient;

   @BeforeGroups(groups = { "live" })
   public void setupClients() {
      orgClient = context.getApi().getOrgClient();
   }

   /*
    * Shared state between dependant tests.
    */

   private OrgList orgList;
   private Reference orgRef;
   private Org org;

   @Test(testName = "GET /org/")
   public void testGetOrgList() {
      // Call the method being tested
      orgList = orgClient.getOrgList();
      
      // NOTE The environment MUST have at least one organisation configured
      
      // Check required elements and attributes
      assertFalse(Iterables.isEmpty(orgList.getOrgs()), "There must always be Org elements in the OrgList");
      
      for (Reference orgRef : orgList.getOrgs()) {
         assertEquals(orgRef.getType(), VCloudDirectorMediaType.ORG, "The Refernce must be to an Org type");
         checkReferenceType(orgRef);
      }
   }

   @Test(testName = "GET /org/{id}", dependsOnMethods = { "testGetOrgList" })
   public void testGetOrg() {
      orgRef = Iterables.getFirst(orgList.getOrgs(), null);

      // Call the method being tested
      org = orgClient.getOrg(orgRef);

      // Check required elements and attributes
      assertNotNull(org.getFullName(), String.format(FIELD_NOT_NULL_FMT, "FullName", "Org"));

      // Check parent type
      checkEntityType(org);
   }
   
   @Test(testName = "GET /org/{id}/metadata/", dependsOnMethods = { "testGetOrg" })
   public void testGetOrgMetadata() {
      // Call the method being tested
      Metadata metadata = orgClient.getOrgMetadata(orgRef);
      
      // NOTE The environment MUST have at one metadata entry for the first organisation configured
      
      // Check required elements and attributes
      assertFalse(Iterables.isEmpty(metadata.getMetadataEntries()), "There must always be MetadataEntry elements in the Org");
      
      // Check parent type
      checkResourceType(metadata);
      
      for (MetadataEntry entry : metadata.getMetadataEntries()) {
         // Check required elements and attributes
         assertNotNull(entry.getKey(), String.format(FIELD_NOT_NULL_FMT, "Key", "MetadataEntry"));
         assertNotNull(entry.getValue(), String.format(FIELD_NOT_NULL_FMT, "Value", "MetadataEntry"));
         
         // Check parent type
         checkResourceType(entry);
      }
   }
   
   @Test(testName = "GET /org/{id}/metadata/{key}", dependsOnMethods = { "testGetOrgMetadata" })
   public void testGetOrgMetadataEntry() {
      // Call the method being tested
      MetadataEntry entry = orgClient.getOrgMetadataEntry(orgRef, "KEY");
      
      // NOTE The environment MUST have configured the metadata entry as '{ key="KEY", value="VALUE" )'

      // Check required elements and attributes
      assertNotNull(entry.getKey(), String.format(FIELD_NOT_NULL_FMT, "Key", "MetadataEntry"));
      assertEquals(entry.getKey(), "KEY", "The Key field must have the value \"KEY\"");
      assertNotNull(entry.getValue(), String.format(FIELD_NOT_NULL_FMT, "Value", "MetadataEntry"));
      assertEquals(entry.getValue(), "VALUE", "The Value field must have the value \"VALUE\"");
      
      // Check parent type
      checkResourceType(entry);
   }
}
