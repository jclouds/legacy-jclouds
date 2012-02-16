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
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
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
      
      // Check test requirements
      assertFalse(Iterables.isEmpty(orgList.getOrgs()), String.format(NOT_EMPTY_OBJECT_FMT, "Org", "OrgList"));
      
      for (Reference orgRef : orgList.getOrgs()) {
         assertEquals(orgRef.getType(), VCloudDirectorMediaType.ORG, String.format(CONDITION_FMT, "Reference.Type", VCloudDirectorMediaType.ORG, orgRef.getType()));
         checkReferenceType(orgRef);
      }
   }

   @Test(testName = "GET /org/{id}", dependsOnMethods = { "testGetOrgList" })
   public void testGetOrg() {
      orgRef = Iterables.getFirst(orgList.getOrgs(), null);

      // Call the method being tested
      org = orgClient.getOrg(orgRef);

      checkOrg(org);
   }
   
   @Test(testName = "GET /org/{id}/metadata/", dependsOnMethods = { "testGetOrg" })
   public void testGetOrgMetadata() {
      // Call the method being tested
      Metadata metadata = orgClient.getOrgMetadata(orgRef);
      
      // NOTE The environment MUST have at one metadata entry for the first organisation configured
      
      checkMetadata(metadata);
      
      // Check requirements for this test
      assertFalse(Iterables.isEmpty(metadata.getMetadataEntries()), String.format(NOT_EMPTY_OBJECT_FMT, "MetadataEntry", "Org"));
   }
   
   @Test(testName = "GET /org/{id}/metadata/{key}", dependsOnMethods = { "testGetOrgMetadata" })
   public void testGetOrgMetadataValue() {
      // Call the method being tested
      MetadataValue value = orgClient.getOrgMetadataValue(orgRef, "KEY");
      
      // NOTE The environment MUST have configured the metadata entry as '{ key="KEY", value="VALUE" )'

      String expected = "VALUE";

      checkMetadataValue(value);
      assertEquals(value.getValue(), expected, String.format(CORRECT_VALUE_OBJECT_FMT, "Value", "MetadataValue", expected, value.getValue()));
   }
}
