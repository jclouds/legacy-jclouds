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

import static com.google.common.base.Objects.equal;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_DEL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_UPDATABLE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Collections;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.AdminCatalog;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.PublishCatalogParams;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests live behavior of {@link AdminCatalogClient}.
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin", "catalog" }, singleThreaded = true, testName = "CatalogClientLiveTest")
public class AdminCatalogClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String CATALOG = "admin catalog";

   /*
    * Convenience references to API clients.
    */

   private AdminCatalogClient catalogClient;

   /*
    * Shared state between dependant tests.
    */

   private Reference orgRef;
   private AdminCatalog catalog;
   private Owner owner;

   @Override
   @BeforeClass(alwaysRun = true)
   protected void setupRequiredClients() {
      catalogClient = context.getApi().getAdminCatalogClient();
      orgRef = Iterables.getFirst(context.getApi().getOrgClient().getOrgList().getOrgs(), null).toAdminReference(endpoint);
   }
   
   @Test(testName = "POST /admin/org/{id}/catalogs")
   public void testCreateCatalog() {
      AdminCatalog newCatalog = AdminCatalog.builder()
         .name(name("Test Catalog "))
         .description("created by testCreateCatalog()")
         .build();
      catalog = catalogClient.createCatalog(orgRef.getHref(), newCatalog);
      
      Checks.checkAdminCatalog(catalog);
      
      // FIXME: documentation suggests we should wait for a task here
   }

   @Test(testName = "GET /admin/catalog/{id}",
         dependsOnMethods = { "testCreateCatalog" })
   public void testGetCatalog() {
      catalog = catalogClient.getCatalog(catalog.getHref());
      
      Checks.checkAdminCatalog(catalog);
   }
   
   @Test(testName = "GET /admin/catalog/{id}/owner",
         dependsOnMethods = { "testGetCatalog" })
   public void testGetCatalogOwner() {
      owner = catalogClient.getOwner(catalog.getHref());
      Checks.checkOwner(owner);
   }
   
   @Test(testName = "PUT /admin/catalog/{id}/owner",
         dependsOnMethods = { "testGetCatalog" })
   public void updateCatalogOwner() {
      User newOwnerUser = UserClientLiveTest.randomTestUser("testUpdateCatalogOwner", context);
      newOwnerUser = context.getApi().getUserClient().createUser(orgRef.getHref(), newOwnerUser);
      assertNotNull(newOwnerUser, "failed to create temp user to test updateCatalogOwner");
      
      Owner oldOwner = owner;
      Owner newOwner = Owner.builder() 
            .type("application/vnd.vmware.vcloud.owner+xml")
            .user(Reference.builder().fromEntity(newOwnerUser).build())
            .build();
      
      try {
         catalogClient.setOwner(catalog.getHref(), newOwner);
         owner = catalogClient.getOwner(catalog.getHref());
         Checks.checkOwner(owner);
         assertTrue(equal(owner.toBuilder().links(Collections.<Link>emptySet()).build(), 
               newOwner.toBuilder().user(newOwner.getUser().toBuilder().id(null).build()).build()), 
            String.format(OBJ_FIELD_UPDATABLE, CATALOG, "owner"));
      } finally {
         catalogClient.setOwner(catalog.getHref(), oldOwner);
         owner = catalogClient.getOwner(catalog.getHref());
         context.getApi().getUserClient().deleteUser(newOwnerUser.getHref());
      }
   }
   
   @Test(testName = "PUT /admin/catalog/{id}", dependsOnMethods = { "testGetCatalogOwner" })
   public void testUpdateCatalog() {
      String oldName = catalog.getName();
      String newName = "new "+oldName;
      String oldDescription = catalog.getDescription();
      String newDescription = "new "+oldDescription;
      // TODO: can we update/manage catalogItems directly like this? or does it just do a merge (like metadata)
//      CatalogItems oldCatalogItems = catalog.getCatalogItems();
//      CatalogItems newCatalogItems = CatalogItems.builder().build();
      
      try {
         catalog = catalog.toBuilder()
               .name(newName)
               .description(newDescription)
//               .catalogItems(newCatalogItems)
               .build();
         
         catalog = catalogClient.updateCatalog(catalog.getHref(), catalog);
         
         assertTrue(equal(catalog.getName(), newName), String.format(OBJ_FIELD_UPDATABLE, CATALOG, "name"));
         assertTrue(equal(catalog.getDescription(), newDescription),
               String.format(OBJ_FIELD_UPDATABLE, CATALOG, "description"));
//         assertTrue(equal(catalog.getCatalogItems(), newCatalogItems), String.format(OBJ_FIELD_UPDATABLE, CATALOG, "catalogItems"));
         
         //TODO negative tests?
         
         Checks.checkAdminCatalog(catalog);
      } finally {
         catalog = catalog.toBuilder()
               .name(oldName)
               .description(oldDescription)
//               .catalogItems(oldCatalogItems)
               .build();
         
         catalog = catalogClient.updateCatalog(catalog.getHref(), catalog);
      }
   }
   
   @Test(testName = "POST /admin/catalog/{id}/action/publish",
         dependsOnMethods = { "testUpdateCatalog" } ) // FIXME: fails with a 403
   public void testPublishCatalog() {
      assertTrue(!catalog.isPublished(), String.format(OBJ_FIELD_EQ, 
            CATALOG, "isPublished", false, catalog.isPublished()));
      
      PublishCatalogParams params = PublishCatalogParams.builder()
         .isPublished(true)
         .build();
      
      catalogClient.publishCatalog(catalog.getHref(), params);
      catalog = catalogClient.getCatalog(catalog.getHref());
      
      assertTrue(catalog.isPublished(), String.format(OBJ_FIELD_EQ, 
            CATALOG, "isPublished", true, catalog.isPublished()));
   }
   
   @Test(testName = "DELETE /admin/catalog/{id}",
         dependsOnMethods = { "testCreateCatalog" } )
   public void testDeleteCatalog() {
//      assertEquals(catalog.getCatalogItems().getCatalogItems().size(), 0, 
//            String.format(OBJ_FIELD_EMPTY_TO_DELETE, "Catalog", "CatalogItems", 
//                  catalog.getCatalogItems().getCatalogItems().toString()));
      AdminCatalog deleteCatalog = AdminCatalog.builder()
            .name(name("Test Catalog "))
            .description("created by testCreateCatalog()")
            .build();
      deleteCatalog = catalogClient.createCatalog(orgRef.getHref(), deleteCatalog);
      catalogClient.deleteCatalog(deleteCatalog.getHref());
      
      Error expected = Error.builder()
            .message("No access to entity \"(com.vmware.vcloud.entity.catalog:"+
                  deleteCatalog.getId().substring("urn:vcloud:catalog:".length())+")\".")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();
      
      try {
         deleteCatalog = catalogClient.getCatalog(deleteCatalog.getHref());
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
         deleteCatalog = null;
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
      
      if (deleteCatalog != null) { // guard against NPE on the .toStrings
         assertNull(deleteCatalog, String.format(OBJ_DEL, CATALOG, deleteCatalog.toString()));
      }
   }
   
   @AfterClass
   protected void tidyUp() {
      if (catalog != null) {
         catalogClient.deleteCatalog(catalog.getHref());
      }
   }
}
