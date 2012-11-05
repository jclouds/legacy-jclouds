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
package org.jclouds.vcloud.director.v1_5.features.admin;

import static com.google.common.base.Objects.equal;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.NOT_NULL_OBJ_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_DEL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_UPDATABLE;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkControlAccessParams;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.vcloud.director.v1_5.domain.AdminCatalog;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.domain.org.Org;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.PublishCatalogParams;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests live behavior of {@link AdminCatalogApi}.
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin" }, singleThreaded = true, testName = "CatalogApiLiveTest")
public class AdminCatalogApiLiveTest extends BaseVCloudDirectorApiLiveTest {

   public static final String CATALOG = "admin catalog";

   /*
    * Convenience references to API apis.
    */

   private AdminCatalogApi catalogApi;

   /*
    * Shared state between dependant tests.
    */

   private Org org;
   private AdminCatalog catalog;
   private Owner owner;

   @Override
   @BeforeClass(alwaysRun = true)
   protected void setupRequiredApis() {
      catalogApi = adminContext.getApi().getCatalogApi();
      org = context.getApi().getOrgApi().get(Iterables.get(context.getApi().getOrgApi().list(), 0).getHref());
   }

   @AfterClass(alwaysRun = true)
   protected void tidyUp() {
      if (catalog != null) {
         try {
            catalogApi.remove(catalog.getId());
         } catch (Exception e) {
            logger.warn(e, "Error deleting admin catalog '%s'", catalog.getName());
         }
      }
   }

   @Test(description = "POST /admin/org/{id}/catalogs")
   public void testAddCatalog() {
      AdminCatalog newCatalog = AdminCatalog.builder().name(name("Test Catalog "))
               .description("created by testAddCatalog()").build();
      catalog = catalogApi.addCatalogToOrg(newCatalog, org.getId());

      Checks.checkAdminCatalog(catalog);

      // FIXME: documentation suggests we should wait for a task here
   }

   @Test(description = "GET /admin/catalog/{id}", dependsOnMethods = { "testAddCatalog" })
   public void testGetCatalog() {
      catalog = catalogApi.get(catalog.getId());

      Checks.checkAdminCatalog(catalog);
   }

   @Test(description = "GET /admin/catalog/{id}/owner", dependsOnMethods = { "testGetCatalog" })
   public void testGetCatalogOwner() {
      owner = catalogApi.getOwner(catalog.getId());
      Checks.checkOwner(owner);
   }

   @Test(description = "PUT /admin/catalog/{id}/owner", dependsOnMethods = { "testGetCatalog" })
   public void editCatalogOwner() {
      User newOwnerUser = randomTestUser("testEditCatalogOwner");
      newOwnerUser = adminContext.getApi().getUserApi().addUserToOrg(newOwnerUser, org.getId());
      assertNotNull(newOwnerUser, "failed to add temp user to test editCatalogOwner");

      Owner oldOwner = owner;
      Owner newOwner = Owner.builder().type("application/vnd.vmware.vcloud.owner+xml")
               .user(Reference.builder().fromEntity(newOwnerUser).build()).build();

      try {
         catalogApi.setOwner(catalog.getId(), newOwner);
         owner = catalogApi.getOwner(catalog.getId());
         Checks.checkOwner(owner);
         assertTrue(
                  equal(owner.toBuilder().links(ImmutableSet.<Link> of()).build(),
                           newOwner.toBuilder().user(newOwner.getUser()).build()),
                  String.format(OBJ_FIELD_UPDATABLE, CATALOG, "owner"));
      } finally {
         catalogApi.setOwner(catalog.getId(), oldOwner);
         owner = catalogApi.getOwner(catalog.getId());
         adminContext.getApi().getUserApi().remove(newOwnerUser.getHref());
      }
   }

   @Test(description = "PUT /admin/catalog/{id}", dependsOnMethods = { "testGetCatalogOwner" })
   public void testEditCatalog() {
      String oldName = catalog.getName();
      String newName = "new " + oldName;
      String oldDescription = catalog.getDescription();
      String newDescription = "new " + oldDescription;
      // TODO: can we edit/manage catalogItems directly like this? or does it just do a merge
      // (like metadata)
      // CatalogItems oldCatalogItems = catalog.getCatalogItems();
      // CatalogItems newCatalogItems = CatalogItems.builder().build();

      try {
         catalog = catalog.toBuilder().name(newName).description(newDescription)
         // .catalogItems(newCatalogItems)
                  .build();

         catalog = catalogApi.edit(catalog.getId(), catalog);

         assertTrue(equal(catalog.getName(), newName), String.format(OBJ_FIELD_UPDATABLE, CATALOG, "name"));
         assertTrue(equal(catalog.getDescription(), newDescription),
                  String.format(OBJ_FIELD_UPDATABLE, CATALOG, "description"));
         // assertTrue(equal(catalog.getCatalogItems(), newCatalogItems),
         // String.format(OBJ_FIELD_UPDATABLE, CATALOG, "catalogItems"));

         // TODO negative tests?

         Checks.checkAdminCatalog(catalog);
      } finally {
         catalog = catalog.toBuilder().name(oldName).description(oldDescription)
         // .catalogItems(oldCatalogItems)
                  .build();

         catalog = catalogApi.edit(catalog.getId(), catalog);
      }
   }

   // FIXME fails with a 403
   @Test(description = "POST /admin/catalog/{id}/action/publish", dependsOnMethods = { "testEditCatalog" })
   public void testPublishCatalog() {
      assertNotNull(catalog, String.format(NOT_NULL_OBJ_FMT, "Catalog"));
      assertFalse(catalog.isPublished(),
               String.format(OBJ_FIELD_EQ, CATALOG, "isPublished", false, catalog.isPublished()));

      PublishCatalogParams params = PublishCatalogParams.builder().isPublished(true).build();

      catalogApi.publish(catalog.getId(), params);
      catalog = catalogApi.get(catalog.getId());

      assertTrue(catalog.isPublished(),
               String.format(OBJ_FIELD_EQ, CATALOG, "isPublished", true, catalog.isPublished()));
   }

   @Test(description = "GET /org/{id}/catalog/{catalogId}/controlAccess", dependsOnMethods = { "testAddCatalog" })
   public void testGetControlAccessControl() {
      // Call the method being tested
      ControlAccessParams params = catalogApi.getAccessControl(catalog.getId());

      // Check params are well formed
      checkControlAccessParams(params);
   }

   @Test(description = "POST /org/{id}/catalog/{catalogId}/action/controlAccess", dependsOnMethods = { "testAddCatalog" })
   public void testEditAccessControl() {
      // Setup params
      ControlAccessParams params = catalogApi.getAccessControl(catalog.getId());

      // Call the method being tested
      ControlAccessParams modified = catalogApi.editAccessControl(catalog.getId(), params);

      // Check params are well formed
      checkControlAccessParams(modified);
   }
   
   @Test(description = "DELETE /admin/catalog/{id}", dependsOnMethods = { "testAddCatalog" })
   public void testRemoveCatalog() {
      // assertEquals(catalog.getCatalogItems().getCatalogItems().size(), 0,
      // String.format(OBJ_FIELD_EMPTY_TO_DELETE, "Catalog", "CatalogItems",
      // catalog.getCatalogItems().getCatalogItems().toString()));
      AdminCatalog removeCatalog = AdminCatalog.builder().name(name("Test Catalog "))
               .description("created by testAddCatalog()").build();
      removeCatalog = catalogApi.addCatalogToOrg(removeCatalog, org.getId());
      catalogApi.remove(removeCatalog.getId());

      removeCatalog = catalogApi.get(removeCatalog.getId());
      assertNull(removeCatalog, String.format(OBJ_DEL, CATALOG, removeCatalog != null ? removeCatalog.toString() : ""));
   }
   

}
