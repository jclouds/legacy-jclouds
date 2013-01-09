/*
x * Licensed to jclouds, Inc. (jclouds) under one or more
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

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.CORRECT_VALUE_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkCatalogItem;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadata;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkTask;
import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.relEquals;
import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.typeEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.AdminCatalog;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminCatalogApi;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests live behavior of {@link CatalogApi}.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "CatalogApiLiveTest")
public class CatalogApiLiveTest extends BaseVCloudDirectorApiLiveTest {

   /*
    * Convenience references to API apis.
    */
   private CatalogApi catalogApi;

   /*
    * Shared state between dependant tests.
    */
   private AdminCatalog adminCatalog;
   private Media media;
   private CatalogItem catalogItem;
   private boolean createdByTest = false;
   private AdminCatalogApi adminCatalogApi;
   
   @Override
   protected void setupRequiredApis() {
      catalogApi = context.getApi().getCatalogApi();
      adminCatalogApi = adminContext.getApi().getCatalogApi();
     
      if(catalogUrn == null) {
         AdminCatalog newCatalog = AdminCatalog.builder().name(name("Test Catalog "))
                  .description("created by CatalogApiLiveTest").build();

         adminCatalog = adminCatalogApi.addCatalogToOrg(newCatalog, org.getId());
         catalogUrn = catalogApi.get(
                  find(adminCatalog.getLinks(),
                           and(relEquals("alternate"), typeEquals(VCloudDirectorMediaType.CATALOG))).getHref()).getId();

         createdByTest = true;
      }
   }

   @AfterClass(alwaysRun = true)
   @Override
   protected void tearDownContext() {
      if (media != null) {
         try {
            Task remove = context.getApi().getMediaApi().remove(media.getId());
            taskDoneEventually(remove);
         } catch (Exception e) {
            logger.warn(e, "Error when deleting media '%s'", media.getName());
         }
      }
      if(createdByTest) {
         if (catalogItem != null) {
            try {
               catalogApi.removeItem(catalogItem.getId());
            } catch (Exception e) {
               logger.warn(e, "Error when deleting catalog item '%s'", catalogItem.getName());
            }
         }
         if (adminContext != null && adminCatalog != null) {
            try {
               adminContext.getApi().getCatalogApi().remove(adminCatalog.getId());
            } catch (Exception e) {
               logger.warn(e, "Error when deleting catalog '%s'", adminCatalog.getName());
            }
         }
      }
      super.tearDownContext();
   }

   @Test(description = "GET /catalog/{id}")
   public void testGetCatalog() {
      Catalog catalog = lazyGetCatalog();
      assertNotNull(catalog);
      // Double check it's pointing at the correct catalog
      assertEquals(catalog.getId(), catalogUrn);
   }

   @Test(description = "GET /catalogItem/{id}", dependsOnMethods = "testAddCatalogItem")
   public void testGetCatalogItem() {
      CatalogItem catalogItem = catalogApi.getItem(this.catalogItem.getId());
      checkCatalogItem(catalogItem);
      assertEquals(catalogItem.getEntity().getHref(), this.catalogItem.getEntity().getHref());
   }

   @Test(description = "POST /catalog/{id}/catalogItems")
   public void testAddCatalogItem() {
      byte[] iso = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
      Vdc vdc = lazyGetVdc();
      Link addMedia = find(vdc.getLinks(), and(relEquals("add"), typeEquals(VCloudDirectorMediaType.MEDIA)));

      Media sourceMedia = Media.builder().type(VCloudDirectorMediaType.MEDIA).name(name("TestMedia-")).size(iso.length)
               .imageType(Media.ImageType.ISO).description("Test media generated by testAddMedia()").build();
      media = context.getApi().getMediaApi().add(addMedia.getHref(), sourceMedia);

      Checks.checkMediaFor(VCloudDirectorMediaType.MEDIA, media);

      CatalogItem editedCatalogItem = CatalogItem.builder().name("newitem").description("New Item")
               .type(VCloudDirectorMediaType.CATALOG_ITEM).entity(Reference.builder().href(media.getHref()).build())
               .build();
      catalogItem = catalogApi.addItem(catalogUrn, editedCatalogItem);
      checkCatalogItem(catalogItem);
      assertEquals(catalogItem.getName(), "newitem");
      assertEquals(catalogItem.getDescription(), "New Item");
   }

   @Test(description = "PUT /catalogItem/{id}", dependsOnMethods = "testAddCatalogItem")
   public void testEditCatalogItem() {
      CatalogItem editedCatalogItem = CatalogItem.builder().fromCatalogItem(catalogItem).name("UPDATEDNAME").build();
      catalogItem = catalogApi.editItem(catalogItem.getId(), editedCatalogItem);
      checkCatalogItem(catalogItem);
      assertEquals(catalogItem.getName(), "UPDATEDNAME");
   }

   // Note this runs after all the metadata tests
   @Test(description = "DELETE /catalogItem/{id}", dependsOnMethods = "testRemoveCatalogItemMetadataValue")
   public void testRemoveCatalogItem() {
      removeMediaAttachedToCatalogItem(catalogItem);
      catalogApi.removeItem(catalogItem.getId());
      catalogItem = catalogApi.getItem(catalogItem.getId());
      assertNull(catalogItem);
   }

   @Test(description = "GET /catalog/{id}/metadata")
   public void testGetCatalogMetadata() {
      Metadata catalogMetadata = context.getApi().getMetadataApi(catalogUrn).get();
      checkMetadata(catalogMetadata);
   }

   @Test(description = "GET /catalog/{id}/metadata/{key}")
   public void testGetCatalogMetadataValue() {

      Task mergeCatalogMetadata = context.getApi().getMetadataApi(catalogUrn)
            .putAll(ImmutableMap.of("KEY", "MARMALADE"));
      assertTaskSucceedsLong(mergeCatalogMetadata);

      Metadata catalogMetadata = context.getApi().getMetadataApi(catalogUrn).get();

      String metadataValue = context.getApi().getMetadataApi(catalogUrn).get("KEY");
      assertEquals(metadataValue, catalogMetadata.get("KEY"), String.format(CORRECT_VALUE_OBJECT_FMT, "Value",
            "MetadataValue", catalogMetadata.get("KEY"), metadataValue));
   }

   @Test(description = "GET /catalogItem/{id}/metadata", dependsOnMethods = "testAddCatalogItem")
   public void testGetCatalogItemMetadata() {
      Metadata catalogItemMetadata = context.getApi().getMetadataApi(catalogItem.getId()).get();
      checkMetadata(catalogItemMetadata);
   }

   @Test(description = "POST /catalogItem/{id}/metadata", dependsOnMethods = "testAddCatalogItem")
   public void testMergeCatalogItemMetadata() {
      Metadata before = context.getApi().getMetadataApi(catalogItem.getId()).get();

      Task mergeCatalogItemMetadata = context.getApi().getMetadataApi(catalogItem.getId()).putAll(
            ImmutableMap.of("KEY", "MARMALADE", "VEGIMITE", "VALUE"));
      checkTask(mergeCatalogItemMetadata);
      assertTrue(retryTaskSuccess.apply(mergeCatalogItemMetadata),
            String.format(TASK_COMPLETE_TIMELY, "mergeCatalogItemMetadata"));
      Metadata mergedCatalogItemMetadata = context.getApi().getMetadataApi(catalogItem.getId()).get();

      assertTrue(mergedCatalogItemMetadata.getMetadataEntries().size() > before.getMetadataEntries().size(),
            "Should have added at least one other MetadataEntry to the CatalogItem");

      String keyMetadataValue = context.getApi().getMetadataApi(catalogItem.getId()).get("KEY");
      assertEquals(keyMetadataValue, "MARMALADE", "The Value of the MetadataValue for KEY should have changed");

      String newKeyMetadataValue = context.getApi().getMetadataApi(catalogItem.getId()).get("VEGIMITE");

      assertEquals(newKeyMetadataValue, "VALUE", "The Value of the MetadataValue for NEW_KEY should have been set");
   }

   @Test(description = "GET /catalogItem/{id}/metadata/{key}", dependsOnMethods = "testSetCatalogItemMetadataValue")
   public void testGetCatalogItemMetadataValue() {
      String metadataValue = context.getApi().getMetadataApi(catalogItem.getId()).get("KEY");
      assertNotNull(metadataValue);
   }

   @Test(description = "PUT /catalogItem/{id}/metadata/{key}", dependsOnMethods = "testMergeCatalogItemMetadata")
   public void testSetCatalogItemMetadataValue() {

      Task setCatalogItemMetadataValue = context.getApi().getMetadataApi(catalogItem.getId()).put("KEY", "NEW");
      checkTask(setCatalogItemMetadataValue);
      assertTrue(retryTaskSuccess.apply(setCatalogItemMetadataValue),
            String.format(TASK_COMPLETE_TIMELY, "setCatalogItemMetadataValue"));

      String editedMetadataValue = context.getApi().getMetadataApi(catalogItem.getId()).get("KEY");
      assertEquals(editedMetadataValue, "NEW",
            String.format(CORRECT_VALUE_OBJECT_FMT, "Value", "MetadataValue", "NEW", editedMetadataValue));
   }

   @Test(description = "DELETE /catalogItem/{id}/metadata/{key}", dependsOnMethods = "testGetCatalogItemMetadataValue")
   public void testRemoveCatalogItemMetadataValue() {
      Task removeCatalogItemMetadataValue = context.getApi().getMetadataApi(catalogItem.getId()).remove("KEY");
      checkTask(removeCatalogItemMetadataValue);
      assertTrue(retryTaskSuccess.apply(removeCatalogItemMetadataValue),
            String.format(TASK_COMPLETE_TIMELY, "removeCatalogItemMetadataValue"));
      String removed = context.getApi().getMetadataApi(catalogItem.getId()).get("KEY");
      assertNull(removed);
   }
   
   private void removeMediaAttachedToCatalogItem(CatalogItem catalogItem) {
      if (media != null) {
         if (catalogItem.getEntity().getHref().equals(media.getHref())) {
            try {
               Task remove = context.getApi().getMediaApi().remove(media.getId());
               taskDoneEventually(remove);
               media = null;
            } catch (Exception e) {
               logger.warn(e, "Error when deleting media '%s'", media.getName());
            }
         }
      }
   }   
}
