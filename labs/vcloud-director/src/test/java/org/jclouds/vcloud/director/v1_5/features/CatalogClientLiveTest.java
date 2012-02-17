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

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.domain.Error;
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
import com.google.common.collect.Iterables;

/**
 * Tests live behavior of {@link CatalogClient}.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "live", "apitests" }, testName = "CatalogClientLiveTest", singleThreaded = true)
public class CatalogClientLiveTest extends BaseVCloudDirectorClientLiveTest {

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
   private Metadata catalogItemMetadata;

   @Test(testName = "GET /catalog/{id}")
   public void testGetCatalog() {
      CatalogReferences catalogReferences = queryClient.catalogReferencesQuery(String.format("name==%s", catalogName));
      assertEquals(Iterables.size(catalogReferences.getReferences()), 1, String.format(MUST_EXIST_FMT, catalogName, "Catalog"));
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
   @Test(testName = "POST /catalog/{id}/catalogItems", dependsOnMethods = { "testGetCatalog" }, enabled = false)
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

   @Test(testName = "DELETE /catalogItem/{id}", dependsOnMethods = { "testAddCatalogItem" }, enabled = false)
   public void testDeleteCatalogItem() {
      catalogClient.deleteCatalogItem(newCatalogItemRef);
      try {
         catalogClient.getCatalogItem(newCatalogItemRef);
         fail("The CatalogItem should have been deleted");
      } catch (VCloudDirectorException vcde) {
         checkError(vcde.getError());
      // XXX
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
   public void testGetCatalogMetadataValue() {
      MetadataEntry existingMetadataEntry = Iterables.find(catalogMetadata.getMetadataEntries(), new Predicate<MetadataEntry>() {
         @Override
         public boolean apply(MetadataEntry input) {
            return input.getKey().equals("KEY");
         }
      });
      MetadataValue metadataValue = catalogClient.getCatalogMetadataValue(catalogRef, "KEY");
      // XXX
      assertEquals(metadataValue.getValue(), existingMetadataEntry.getValue(),
            "The MetadataValue for KEY should have the same Value as the existing MetadataEntry");
      checkMetadataValue(metadataValue);
   }

   @Test(testName = "GET /catalogItem/{id}/metadata", dependsOnMethods = { "testGetCatalogItem" })
   public void testGetCatalogItemMetadata() {
      resetCatalogItemMetadata(catalogItemRef);
      catalogItemMetadata = catalogClient.getCatalogItemMetadata(catalogItemRef);
      // XXX
      assertEquals(catalogItemMetadata.getMetadataEntries().size(), 1, "There should be a single MetadataEntry");
      checkMetadata(catalogItemMetadata);
   }

   @Test(testName = "POST /catalogItem/{id}/metadata", dependsOnMethods = { "testGetCatalogItemMetadata" })
   public void testMergeCatalogItemMetadata() {
      Metadata newMetadata = Metadata.builder()
            .entry(MetadataEntry.builder().entry("KEY", "MARMALADE").build())
            .entry(MetadataEntry.builder().entry("VEGIMITE", "VALUE").build())
            .build();

      Task mergeCatalogItemMetadata = catalogClient.mergeCatalogItemMetadata(catalogItemRef, newMetadata);
      checkTask(mergeCatalogItemMetadata);
      // TODO requires code from dan to be merged
//      assertTrue(taskTester.apply(mergeCatalogItemMetadata.getHref()),
//            String.format(TASK_COMPLETE_TIMELY, "mergeCatalogItemMetadata"));
      
      Metadata mergedCatalogItemMetadata = catalogClient.getCatalogItemMetadata(catalogItemRef);
      // XXX
      assertEquals(mergedCatalogItemMetadata.getMetadataEntries().size(), catalogItemMetadata.getMetadataEntries().size() + 1,
            "Should have added another MetadataEntry to the CatalogItem");
      
      MetadataValue keyMetadataValue = catalogClient.getCatalogItemMetadataValue(catalogItemRef, "KEY");
      // XXX
      assertEquals(keyMetadataValue.getValue(), "MARMALADE", "The Value of the MetadataValue for KEY should have changed");
      checkMetadataValue(keyMetadataValue);
      
      MetadataValue newKeyMetadataValue = catalogClient.getCatalogItemMetadataValue(catalogItemRef, "VEGIMITE");
      // XXX
      assertEquals(newKeyMetadataValue.getValue(), "VALUE", "The Value of the MetadataValue for NEW_KEY should have been set");
      checkMetadataValue(newKeyMetadataValue);
   }

   // TODO escalate
   // XXX org.jclouds.vcloud.director.v1_5.VCloudDirectorException: Error: The access to the resource metadata_item with id KEY is forbidden
   @Test(testName = "GET /catalog/{id}/metadata/{key}", dependsOnMethods = { "testGetCatalogItemMetadata" })
   public void testGetCatalogItemMetadataValue() {
      MetadataEntry existingMetadataEntry = Iterables.find(catalogItemMetadata.getMetadataEntries(), new Predicate<MetadataEntry>() {
         @Override
         public boolean apply(MetadataEntry input) {
            return input.getKey().equals("KEY");
         }
      });
      MetadataValue metadataValue = catalogClient.getCatalogItemMetadataValue(catalogItemRef, "KEY");
      assertEquals(existingMetadataEntry.getValue(), metadataValue.getValue());
      checkMetadataValue(metadataValue);
   }

   @Test(testName = "PUT /catalog/{id}/metadata/{key}", dependsOnMethods = { "testGetCatalogItemMetadataValue" })
   public void testSetCatalogItemMetadataValue() {
      MetadataValue newMetadataValue = MetadataValue.builder().value("NEW").build();

      Task setCatalogItemMetadataValue = catalogClient.setCatalogItemMetadataValue(catalogItemRef, "KEY", newMetadataValue);
      checkTask(setCatalogItemMetadataValue);
      // TODO requires code from dan to be merged
//      assertTrue(taskTester.apply(setCatalogItemMetadataValue.getHref()),
//            String.format(TASK_COMPLETE_TIMELY, "setCatalogItemMetadataValue"));
      
      MetadataValue updatedMetadataValue = catalogClient.getCatalogItemMetadataValue(catalogItemRef, "KEY");
      assertEquals(updatedMetadataValue.getValue(), newMetadataValue.getValue(),
               String.format(CORRECT_VALUE_OBJECT_FMT, "Value", "MetadataValue", newMetadataValue.getValue(), updatedMetadataValue.getValue()));
      checkMetadataValue(updatedMetadataValue);
   }

   @Test(testName = "DELETE /catalog/{id}/metadata/{key}", dependsOnMethods = { "testSetCatalogItemMetadataValue" })
   public void testDeleteCatalogItemMetadataValue() {
      Task deleteCatalogItemMetadataValue = catalogClient.deleteCatalogItemMetadataValue(catalogItemRef, "KEY");
      checkTask(deleteCatalogItemMetadataValue);
      // TODO requires code from dan to be merged
//      assertTrue(taskTester.apply(deleteCatalogItemMetadataValue.getHref()),
//            String.format(TASK_COMPLETE_TIMELY, "deleteCatalogItemMetadataValue"));
      try {
	      catalogClient.getCatalogItemMetadataValue(catalogItemRef, "KEY");
	      fail("The CatalogItem MetadataValue for KEY should have been deleted");
      } catch (VCloudDirectorException vcde) {
         Error error = vcde.getError();
         checkError(error);
         Integer majorErrorCode = error.getMajorErrorCode();
         assertEquals(majorErrorCode, Integer.valueOf(403),
               String.format(CORRECT_VALUE_OBJECT_FMT, "MajorErrorCode", "Error", "403",Integer.toString(majorErrorCode)));
      }
   }

   private void deleteAllCatalogItemMetadata(ReferenceType<?> catalogItemRef) {
      Metadata currentMetadata = catalogClient.getCatalogItemMetadata(catalogItemRef);
      for (MetadataEntry currentMetadataEntry : currentMetadata.getMetadataEntries()) {
         catalogClient.deleteCatalogItemMetadataValue(catalogItemRef, currentMetadataEntry.getKey());
      }
      Metadata emptyMetadata = catalogClient.getCatalogItemMetadata(catalogItemRef);
      assertTrue(emptyMetadata.getMetadataEntries().isEmpty(), "The catalogItem Metadata should be empty");
   }

   private void resetCatalogItemMetadata(ReferenceType<?> catalogItemRef) {
      deleteAllCatalogItemMetadata(catalogItemRef);
      Metadata newMetadata = Metadata.builder().entry(MetadataEntry.builder().entry("KEY", "VALUE").build()).build();
      Task mergeCatalogItemMetadata = catalogClient.mergeCatalogItemMetadata(catalogItemRef, newMetadata);
      // TODO requires code from dan to be merged
//    assertTrue(taskTester.apply(mergeCatalogItemMetadata.getHref()),
//          String.format(TASK_COMPLETE_TIMELY, "mergeCatalogItemMetadata"));
   }
}
