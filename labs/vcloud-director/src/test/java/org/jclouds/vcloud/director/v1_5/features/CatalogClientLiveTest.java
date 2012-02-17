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

import static org.jclouds.vcloud.director.v1_5.domain.Checks.*;
import static org.testng.Assert.*;

import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.query.CatalogReferences;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Tests live behavior of {@link CatalogClient}.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "live", "apitests" }, testName = "CatalogClientLiveTest")
public class CatalogClientLiveTest extends BaseVCloudDirectorClientLiveTest {

   private static final String CATALOG_NAME = "QunyingTestCatalog"; // TODO add as test configuration property

   /*
    * Convenience references to API clients.
    */

   private CatalogClient catalogClient;
   private QueryClient queryClient;

   /*
    * Shared state between dependant tests.
    */

   private ReferenceType<?> catalogRef;
   private ReferenceType<?> catalogItemRef;
   private ReferenceType<?> newCatalogItemRef;
   private Catalog catalog;
   private CatalogItem catalogItem;
   private CatalogItem newCatalogItem;
   private Metadata catalogMetadata;

   @BeforeGroups(groups = { "live" })
   public void setupClients() {
      catalogClient = context.getApi().getCatalogClient();
      queryClient = context.getApi().getQueryClient();
   }

   @Test(testName = "GET /catalog/{id}")
   public void testGetCatalog() {
      CatalogReferences catalogReferences = queryClient.catalogReferencesQuery(String.format("name==%s", CATALOG_NAME));
      assertEquals(Iterables.size(catalogReferences.getReferences()), 1, String.format("The %s Catalog must exist", CATALOG_NAME));
      catalogRef = Iterables.getOnlyElement(catalogReferences.getReferences());
      catalog = catalogClient.getCatalog(catalogRef);
   }

   @Test(testName = "GET /catalogItem/{id}", dependsOnMethods = { "testGetCatalog" })
   public void testGetCatalogItem() {
      assertFalse(Iterables.isEmpty(catalog.getCatalogItems().getCatalogItems()));
      catalogItemRef = Iterables.get(catalog.getCatalogItems().getCatalogItems(), 0);
      catalogItem = catalogClient.getCatalogItem(catalogItemRef);
      checkCatalogItem(catalogItem);
   }

   // NOTE for this test to work, we need to be able to upload a new vAppTemplate to a vDC first
   // NOTE we could do this with a test environment property -Dtest.vcloud-director.vappTemplateId=vapptemplate-abcd
   @Test(testName = "POST /catalog/{id}/catalogItems", dependsOnMethods = { "testGetCatalogItem" }, enabled = false)
   public void testAddCatalogItem() {
      CatalogItem editedCatalogItem = CatalogItem.builder()
            .name("newitem")
            .description("New Item")
            // XXX org.jclouds.vcloud.director.v1_5.VCloudDirectorException: Error: The VCD entity image already exists.
            // .entity(Reference.builder().href(catalogItem.getEntity().getHref()).build())
            // XXX org.jclouds.vcloud.director.v1_5.VCloudDirectorException: Error: The VCD entity ubuntu10 already exists.
            // .entity(Reference.builder().href(URI.create(endpoint + "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9")).build())
            .build();
      newCatalogItem = catalogClient.addCatalogItem(catalogRef, editedCatalogItem);
      checkCatalogItem(newCatalogItem);
      assertEquals(newCatalogItem.getName(), "newitem");
   }

   @Test(testName = "PUT /catalogItem/{id}", dependsOnMethods = { "testAddCatalogItem" }, enabled = false)
   public void testUpdateCatalogItem() {
      Catalog catalog = catalogClient.getCatalog(catalogRef);
      newCatalogItemRef = Iterables.find(catalog.getCatalogItems().getCatalogItems(), new Predicate<Reference>() {
         @Override
         public boolean apply(Reference input) {
            return input.getHref().equals(newCatalogItem.getHref());
         }
      });
      CatalogItem updatedCatalogItem = CatalogItem.builder().fromCatalogItem(catalogItem).name("UPDATEDNAME").build();
      newCatalogItem = catalogClient.updateCatalogItem(catalogRef, updatedCatalogItem);
      checkCatalogItem(newCatalogItem);
      assertEquals(newCatalogItem.getName(), "UPDATEDNAME");
   }

   @Test(testName = "DELETE /catalogItem/{id}", dependsOnMethods = { "testUpdateCatalogItem" }, enabled = false)
   public void testDeleteCatalogItem() {
      catalogClient.deleteCatalogItem(newCatalogItemRef);
      try {
         catalogClient.getCatalogItem(newCatalogItemRef);
         fail("The CatalogItem should have been deleted");
      } catch (VCloudDirectorException vcde) {
         checkError(vcde.getError());
         assertEquals(vcde.getError().getMajorErrorCode(), Integer.valueOf(403), "The majorErrorCode should be 403 since the item has been deleted");
      }
   }

   // NOTE for this test to work, we need to be able to create metadata on a Catalog, specifically { "KEY", "VALUE" }
   @Test(testName = "GET /catalog/{id}/metadata", dependsOnMethods = { "testGetCatalog" }, enabled = false)
   public void testGetCatalogMetadata() {
      catalogMetadata = catalogClient.getCatalogMetadata(catalogRef);
      checkMetadata(catalogMetadata);
   }

   // NOTE for this test to work, we need to be able to create metadata on a Catalog, specifically { "KEY", "VALUE" }
   @Test(testName = "GET /catalog/{id}/metadata/{key}", dependsOnMethods = { "testGetCatalogMetadata" }, enabled = false)
   public void testGetCatalogMetadataEntry() {
      MetadataEntry existingMetadataEntry = Iterables.find(catalogMetadata.getMetadataEntries(), new Predicate<MetadataEntry>() {
         @Override
         public boolean apply(MetadataEntry input) {
            return input.getKey().equals("KEY");
         }
      });
      MetadataEntry metadataEntry = catalogClient.getCatalogMetadataEntry(catalogRef, "KEY");
      assertEquals(existingMetadataEntry.getValue(), metadataEntry.getValue());
      checkMetadataEntry(metadataEntry);
   }

   @Test(testName = "POST /catalogItem/{id}/metadata", dependsOnMethods = { "testGetCatalogItem" })
   public void testMergeCatalogItemMetadata() {
      Metadata newMetadata = Metadata.builder()
            .entry(MetadataEntry.builder().entry("KEY", "VALUE").build())
            .build();

      Task task = catalogClient.mergeCatalogItemMetadata(catalogItemRef, newMetadata);
      // TODO wait until task no longer running...
   }

   @Test(testName = "GET /catalogItem/{id}/metadata", dependsOnMethods = { "testMergeCatalogItemMetadata" })
   public void testGetCatalogItemMetadata() {
      Metadata metadata = catalogClient.getCatalogItemMetadata(catalogItemRef);
   }

   // XXX org.jclouds.vcloud.director.v1_5.VCloudDirectorException: Error: The access to the resource metadata_item with id KEY is forbidden
   @Test(testName = "GET /catalog/{id}/metadata/{key}", dependsOnMethods = { "testMergeCatalogItemMetadata" }, enabled = false)
   public void testGetCatalogItemMetadataEntry() {
      MetadataEntry metadataEntry = catalogClient.getCatalogItemMetadataEntry(catalogItemRef, "KEY");
   }

   @Test(testName = "PUT /catalog/{id}/metadata/{key}", dependsOnMethods = { "testMergeCatalogItemMetadata" })
   public void testSetCatalogItemMetadataEntry() {
      MetadataValue newMetadataValue = MetadataValue.builder().value("NEW").build();

      Task task = catalogClient.setCatalogItemMetadataEntry(catalogItemRef, "KEY", newMetadataValue);
      // TODO wait until task no longer running...
   }

   @Test(testName = "DELETE /catalog/{id}/metadata/{key}", dependsOnMethods = { "testMergeCatalogItemMetadata" })
   public void testDeleteCatalogItemMetadataEntry() {
      Task task = catalogClient.deleteCatalogItemMetadataEntry(catalogItemRef, "KEY");
      // TODO wait until task no longer running...
   }
}
