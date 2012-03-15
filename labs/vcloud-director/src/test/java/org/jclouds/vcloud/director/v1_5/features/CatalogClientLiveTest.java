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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.CORRECT_VALUE_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.MUST_EXIST_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkCatalogItem;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkError;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadata;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataValue;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkTask;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.domain.CatalogType;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.query.CatalogReferences;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests live behavior of {@link CatalogClient}.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "live", "user", "catalog" }, singleThreaded = true, testName = "CatalogClientLiveTest")
public class CatalogClientLiveTest extends BaseVCloudDirectorClientLiveTest {

   /*
    * Convenience references to API clients.
    */

   private CatalogClient catalogClient;
   private QueryClient queryClient;

   /*
    * Shared state between dependant tests.
    */

   private ReferenceType catalogRef;
   private ReferenceType catalogItemRef;
   private ReferenceType newCatalogItemRef;
   private CatalogType catalog;
   private CatalogItem catalogItem;
   private CatalogItem newCatalogItem;
   private Metadata catalogMetadata;

   @Override
   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      catalogClient = context.getApi().getCatalogClient();
      queryClient = context.getApi().getQueryClient();
   }

   private Metadata catalogItemMetadata;

   @Test(testName = "GET /catalog/{id}")
   public void testGetCatalog() {
      // TODO use property from default property set
      CatalogReferences catalogReferences = queryClient.catalogReferencesQuery(String.format("name==%s", catalogName));
      assertEquals(Iterables.size(catalogReferences.getReferences()), 1, String.format(MUST_EXIST_FMT, catalogName, "Catalog"));
      catalogRef = Iterables.getOnlyElement(catalogReferences.getReferences());
      catalog = catalogClient.getCatalog(catalogRef.getHref());
   }

   @Test(testName = "GET /catalogItem/{id}", dependsOnMethods = { "testGetCatalog" })
   public void testGetCatalogItem() {
      assertFalse(Iterables.isEmpty(catalog.getCatalogItems().getCatalogItems()));
      catalogItemRef = Iterables.get(catalog.getCatalogItems().getCatalogItems(), 0);
      catalogItem = catalogClient.getCatalogItem(catalogItemRef.getHref());
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
      newCatalogItem = catalogClient.addCatalogItem(catalogRef.getHref(), editedCatalogItem);
      checkCatalogItem(newCatalogItem);
      assertEquals(newCatalogItem.getName(), "newitem");
   }

   @Test(testName = "PUT /catalogItem/{id}", dependsOnMethods = { "testAddCatalogItem" }, enabled = false)
   public void testUpdateCatalogItem() {
      CatalogType catalog = catalogClient.getCatalog(catalogRef.getHref());
      newCatalogItemRef = Iterables.find(catalog.getCatalogItems().getCatalogItems(), new Predicate<Reference>() {
         @Override
         public boolean apply(Reference input) {
            return input.getHref().equals(newCatalogItem.getHref());
         }
      });
      CatalogItem updatedCatalogItem = CatalogItem.builder().fromCatalogItem(catalogItem).name("UPDATEDNAME").build();
      newCatalogItem = catalogClient.updateCatalogItem(catalogRef.getHref(), updatedCatalogItem);
      checkCatalogItem(newCatalogItem);
      assertEquals(newCatalogItem.getName(), "UPDATEDNAME");
   }

   @Test(testName = "DELETE /catalogItem/{id}", dependsOnMethods = { "testAddCatalogItem" }, enabled = false)
   public void testDeleteCatalogItem() {
      catalogClient.deleteCatalogItem(newCatalogItemRef.getHref());
      try {
         catalogClient.getCatalogItem(newCatalogItemRef.getHref());
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
      catalogMetadata = catalogClient.getMetadataClient().getMetadata(catalogRef.getHref());
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
      MetadataValue metadataValue = catalogClient.getMetadataClient().getMetadataValue(catalogRef.getHref(), "KEY");
      assertEquals(metadataValue.getValue(), existingMetadataEntry.getValue(),
            String.format(CORRECT_VALUE_OBJECT_FMT, "Value", "MetadataValue", existingMetadataEntry.getValue(), metadataValue.getValue()));
      checkMetadataValue(metadataValue);
   }

   @Test(testName = "GET /catalogItem/{id}/metadata", dependsOnMethods = { "testGetCatalogItem" })
   public void testGetCatalogItemMetadata() {
      resetCatalogItemMetadata(catalogItemRef.getHref());
      catalogItemMetadata = catalogClient.getMetadataClient().getMetadata(catalogItemRef.getHref());
      assertEquals(catalogItemMetadata.getMetadataEntries().size(), 1, String.format(MUST_EXIST_FMT, "MetadataEntry", "CatalogItem"));
      checkMetadata(catalogItemMetadata);
   }

   @Test(testName = "POST /catalogItem/{id}/metadata", dependsOnMethods = { "testGetCatalogItemMetadata" })
   public void testMergeCatalogItemMetadata() {
      Metadata newMetadata = Metadata.builder()
            .entry(MetadataEntry.builder().entry("KEY", "MARMALADE").build())
            .entry(MetadataEntry.builder().entry("VEGIMITE", "VALUE").build())
            .build();

      Task mergeCatalogItemMetadata = catalogClient.getMetadataClient().mergeMetadata(catalogItemRef.getHref(), newMetadata);
      checkTask(mergeCatalogItemMetadata);
      assertTrue(retryTaskSuccess.apply(mergeCatalogItemMetadata),
            String.format(TASK_COMPLETE_TIMELY, "mergeCatalogItemMetadata"));
      
      Metadata mergedCatalogItemMetadata = catalogClient.getMetadataClient().getMetadata(catalogItemRef.getHref());
      // XXX
      assertEquals(mergedCatalogItemMetadata.getMetadataEntries().size(), catalogItemMetadata.getMetadataEntries().size() + 1,
            "Should have added another MetadataEntry to the CatalogItem");
      
      MetadataValue keyMetadataValue = catalogClient.getMetadataClient().getMetadataValue(catalogItemRef.getHref(), "KEY");
      // XXX
      assertEquals(keyMetadataValue.getValue(), "MARMALADE", "The Value of the MetadataValue for KEY should have changed");
      checkMetadataValue(keyMetadataValue);
      
      MetadataValue newKeyMetadataValue = catalogClient.getMetadataClient().getMetadataValue(catalogItemRef.getHref(), "VEGIMITE");
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
      MetadataValue metadataValue = catalogClient.getMetadataClient().getMetadataValue(catalogItemRef.getHref(), "KEY");
      assertEquals(existingMetadataEntry.getValue(), metadataValue.getValue());
      checkMetadataValue(metadataValue);
   }

   @Test(testName = "PUT /catalog/{id}/metadata/{key}", dependsOnMethods = { "testGetCatalogItemMetadataValue" })
   public void testSetCatalogItemMetadataValue() {
      MetadataValue newMetadataValue = MetadataValue.builder().value("NEW").build();

      Task setCatalogItemMetadataValue = catalogClient.getMetadataClient().setMetadata(catalogItemRef.getHref(), "KEY", newMetadataValue);
      checkTask(setCatalogItemMetadataValue);
      assertTrue(retryTaskSuccess.apply(setCatalogItemMetadataValue), 
            String.format(TASK_COMPLETE_TIMELY, "setCatalogItemMetadataValue"));
      
      MetadataValue updatedMetadataValue = catalogClient.getMetadataClient().getMetadataValue(catalogItemRef.getHref(), "KEY");
      assertEquals(updatedMetadataValue.getValue(), newMetadataValue.getValue(),
               String.format(CORRECT_VALUE_OBJECT_FMT, "Value", "MetadataValue", newMetadataValue.getValue(), updatedMetadataValue.getValue()));
      checkMetadataValue(updatedMetadataValue);
   }

   @Test(testName = "DELETE /catalog/{id}/metadata/{key}", dependsOnMethods = { "testSetCatalogItemMetadataValue" })
   public void testDeleteCatalogItemMetadataValue() {
      Task deleteCatalogItemMetadataValue = catalogClient.getMetadataClient().deleteMetadataEntry(catalogItemRef.getHref(), "KEY");
      checkTask(deleteCatalogItemMetadataValue);
      assertTrue(retryTaskSuccess.apply(deleteCatalogItemMetadataValue), 
            String.format(TASK_COMPLETE_TIMELY, "deleteCatalogItemMetadataValue"));
      try {
	      catalogClient.getMetadataClient().getMetadataValue(catalogItemRef.getHref(), "KEY");
	      fail("The CatalogItem MetadataValue for KEY should have been deleted");
      } catch (VCloudDirectorException vcde) {
         Error error = vcde.getError();
         checkError(error);
         Integer majorErrorCode = error.getMajorErrorCode();
         assertEquals(majorErrorCode, Integer.valueOf(403),
               String.format(CORRECT_VALUE_OBJECT_FMT, "MajorErrorCode", "Error", "403",Integer.toString(majorErrorCode)));
      }
   }

   private void deleteAllCatalogItemMetadata(URI catalogItemURI) {
      Metadata currentMetadata = catalogClient.getMetadataClient().getMetadata(catalogItemURI);
      for (MetadataEntry currentMetadataEntry : currentMetadata.getMetadataEntries()) {
         retryTaskSuccess.apply(catalogClient.getMetadataClient().deleteMetadataEntry(catalogItemURI, currentMetadataEntry.getKey()));
      }
      Metadata emptyMetadata = catalogClient.getMetadataClient().getMetadata(catalogItemURI);
      assertTrue(emptyMetadata.getMetadataEntries().isEmpty(), "The catalogItem Metadata should be empty");
   }

   private void resetCatalogItemMetadata(URI catalogItemURI) {
      deleteAllCatalogItemMetadata(catalogItemURI);
      Metadata newMetadata = Metadata.builder().entry(MetadataEntry.builder().entry("KEY", "VALUE").build()).build();
      Task mergeCatalogItemMetadata = catalogClient.getMetadataClient().mergeMetadata(catalogItemURI, newMetadata);
      checkTask(mergeCatalogItemMetadata);
      assertTrue(retryTaskSuccess.apply(mergeCatalogItemMetadata), 
            String.format(TASK_COMPLETE_TIMELY, "mergeCatalogItemMetadata"));
   }
}
