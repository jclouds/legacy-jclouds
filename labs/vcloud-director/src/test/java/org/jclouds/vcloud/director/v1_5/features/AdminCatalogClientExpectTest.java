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

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.AdminCatalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItems;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.PublishCatalogParams;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.TasksInProgress;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

/**
 * Test the {@link CatalogClient} by observing its side effects.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "unit", "user" }, singleThreaded = true, testName = "CatalogClientExpectTest")
public class AdminCatalogClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {
   
   private Reference orgRef = Reference.builder()
         .type("application/vnd.vmware.vcloud.catalog+xml")
         .name("QunyingTestCatalog")
         .href(URI.create(endpoint + "/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
         .build();
   
   private Reference catalogRef = Reference.builder()
         .type("application/vnd.vmware.vcloud.catalog+xml")
         .name("QunyingTestCatalog")
         .href(URI.create(endpoint + "/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
         .build();
   
   @Test
   public void testCreateCatalog() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("POST", "/admin/org/???/catalogs")
            .xmlFilePayload("/catalog/admin/createCatalogSource.xml", VCloudDirectorMediaType.ADMIN_CATALOG)
            .acceptMedia(VCloudDirectorMediaType.ADMIN_CATALOG)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/catalog/admin/createCatalog.xml", VCloudDirectorMediaType.ADMIN_CATALOG)
            .httpResponseBuilder().build());

      AdminCatalog source = createCatalogSource();
      AdminCatalog expected = createCatalog();

      assertEquals(client.getAdminCatalogClient().createCatalog(catalogRef.getHref(), source), expected);
   }

   @Test
   public void testGetCatalog() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("GET", "/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4")
            .acceptAnyMedia()
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/catalog/admin/catalog.xml", VCloudDirectorMediaType.ADMIN_CATALOG)
            .httpResponseBuilder().build());

      AdminCatalog expected = catalog();

      assertEquals(client.getAdminCatalogClient().getCatalog(catalogRef.getHref()), expected);
   }
   
   @Test
   public void testModifyCatalog() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
         new VcloudHttpRequestPrimer()
            .apiCommand("PUT", "/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4")
            .xmlFilePayload("/catalog/admin/updateCatalogSource.xml", VCloudDirectorMediaType.ADMIN_CATALOG)
            .acceptMedia(VCloudDirectorMediaType.ADMIN_CATALOG)
            .httpRequestBuilder().build(), 
         new VcloudHttpResponsePrimer()
            .xmlFilePayload("/catalog/admin/updateCatalog.xml", VCloudDirectorMediaType.ADMIN_CATALOG)
            .httpResponseBuilder().build());

      AdminCatalog expected = modifyCatalog();

      assertEquals(client.getAdminCatalogClient().updateCatalog(catalogRef.getHref(), expected), expected);
   }
   
   @Test
   public void testGetOwner() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/owner")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/catalog/admin/owner.xml", VCloudDirectorMediaType.OWNER)
               .httpResponseBuilder().build());
      
      Owner expected = owner();

      assertEquals(client.getAdminCatalogClient().getOwner(catalogRef.getHref()), expected);
   }
   
   @Test
   public void testSetOwner() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("PUT", "/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/owner")
               .xmlFilePayload("/catalog/admin/updateOwnerSource.xml", VCloudDirectorMediaType.OWNER)
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .httpResponseBuilder().statusCode(204).build());
      
      Owner newOwner = Owner.builder()
            .type("application/vnd.vmware.vcloud.owner+xml")
            .user(Reference.builder()
                  .type("application/vnd.vmware.admin.user+xml")
                  .name("adk@cloudsoftcorp.com")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/e9eb1b29-0404-4c5e-8ef7-e584acc51da9"))
                  .build())
            .build();
      
      client.getAdminCatalogClient().setOwner(catalogRef.getHref(), newOwner);
   }
   
   @Test
   public void testPublishCatalog() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/action/publish")
               .xmlFilePayload("/catalog/admin/publishCatalogParams.xml", VCloudDirectorMediaType.PUBLISH_CATALOG_PARAMS)
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .httpResponseBuilder().statusCode(204).build());
      
      PublishCatalogParams params = PublishCatalogParams.builder()
            .isPublished(true)
            .build();
      
      client.getAdminCatalogClient().publishCatalog(catalogRef.getHref(), params);
   }
   
   @Test
   public void testDeleteCatalog() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("DELETE", "/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .httpResponseBuilder().statusCode(204).build());
      
      client.getAdminCatalogClient().deleteCatalog(catalogRef.getHref());
   }
   
   public static final AdminCatalog createCatalogSource() {
      return AdminCatalog.builder()
         .name("Test Catalog")
         .description("created by testCreateCatalog()")
         .build();
   }
   
   public static final AdminCatalog createCatalog() {
      return AdminCatalog.builder()
         .name("Test Catalog")
         .id("urn:vcloud:catalog:c56d9159-7838-446f-bb35-9ee12dfbbef3")
         .type("application/vnd.vmware.admin.catalog+xml")
         .description("created by testCreateCatalog()")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/c56d9159-7838-446f-bb35-9ee12dfbbef3"))
         .link(Link.builder()
            .rel("up")
            .type("application/vnd.vmware.admin.organization+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
            .build())
         .link(Link.builder()
            .rel("alternate")
            .type("application/vnd.vmware.vcloud.catalog+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/c56d9159-7838-446f-bb35-9ee12dfbbef3"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.owner+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/c56d9159-7838-446f-bb35-9ee12dfbbef3/owner"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.vcloud.catalogItem+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/c56d9159-7838-446f-bb35-9ee12dfbbef3/catalogItems"))
            .build())
         .link(Link.builder()
            .rel("edit")
            .type("application/vnd.vmware.admin.catalog+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/c56d9159-7838-446f-bb35-9ee12dfbbef3"))
            .build())
         .link(Link.builder()
            .rel("remove")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/c56d9159-7838-446f-bb35-9ee12dfbbef3"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/c56d9159-7838-446f-bb35-9ee12dfbbef3/metadata"))
            .build())
         .description("created by testCreateCatalog()")
         .tasksInProgress(TasksInProgress.builder()
            .task(Task.builder()
               .status("running")
               .startTime(dateService.iso8601DateParse("2012-03-11T18:43:02.429-04:00"))
               .operationName("catalogCreateCatalog")
               .operation("Creating Catalog Test Catalog(c56d9159-7838-446f-bb35-9ee12dfbbef3)")
               .expiryTime(dateService.iso8601DateParse("2012-06-09T18:43:02.429-04:00"))
               .name("task")
               .id("urn:vcloud:task:20f556f9-9125-4090-9092-0da9f72bedf4")
               .type("application/vnd.vmware.vcloud.task+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/task/20f556f9-9125-4090-9092-0da9f72bedf4"))
               .link(Link.builder()
                  .rel("task:cancel")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/task/20f556f9-9125-4090-9092-0da9f72bedf4/action/cancel"))
                  .build())
               .owner(Reference.builder()
                  .type("application/vnd.vmware.vcloud.catalog+xml")
                  .name("Test Catalog")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/c56d9159-7838-446f-bb35-9ee12dfbbef3"))
                  .build())
               .user(Reference.builder()
                  .type("application/vnd.vmware.admin.user+xml")
                  .name("dan@cloudsoftcorp.com")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/ae75edd2-12de-414c-8e85-e6ea10442c08"))
                  .build())
               .org(Reference.builder()
                  .type("application/vnd.vmware.vcloud.org+xml")
                  .name("JClouds")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
                  .build())
               .build())
            .build())
            .catalogItems(CatalogItems.builder()
               .build())
            .isPublished(false)
         .build();
   }
   
   public static final AdminCatalog catalog() {
      return AdminCatalog.builder()
         .name("QunyingTestCatalog")
         .id("urn:vcloud:catalog:7212e451-76e1-4631-b2de-ba1dfd8080e4")
         .type("application/vnd.vmware.admin.catalog+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
         .link(Link.builder()
               .rel("up")
               .type("application/vnd.vmware.admin.organization+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
               .build())
         .link(Link.builder()
               .rel("alternate")
               .type("application/vnd.vmware.vcloud.catalog+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
               .build())
         .link(Link.builder()
               .rel("down")
               .type("application/vnd.vmware.vcloud.owner+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/owner"))
               .build())
         .link(Link.builder()
               .rel("add")
               .type("application/vnd.vmware.vcloud.catalogItem+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/catalogItems"))
               .build())
         .link(Link.builder()
               .rel("edit")
               .type("application/vnd.vmware.admin.catalog+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
               .build())
         .link(Link.builder()
               .rel("remove")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
               .build())
         .link(Link.builder()
               .rel("down")
               .type("application/vnd.vmware.vcloud.metadata+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/metadata"))
               .build())
         .description("Testing")
         .owner(owner())
         .catalogItems(CatalogItems.builder()
               .item(Reference.builder()
                     .type("application/vnd.vmware.vcloud.catalogItem+xml")
                     .name("image")
                     .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/67a469a1-aafe-4b5b-bb31-a6202ad8961f"))
                     .build())
               .item(Reference.builder()
                     .type("application/vnd.vmware.vcloud.catalogItem+xml")
                     .name("ubuntu10")
                     .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
                     .build())
               .item(Reference.builder()
                     .type("application/vnd.vmware.vcloud.catalogItem+xml")
                     .name("imageTesting")
                     .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a9e0afdb-a42b-4688-8409-2ac68cf22939"))
                     .build())
               .item(Reference.builder()
                     .type("application/vnd.vmware.vcloud.catalogItem+xml")
                     .name("TestCase")
                     .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/f7598606-aea4-41d7-8f67-2090e28e7876"))
                     .build())
               .build())
         .isPublished(false)
         .build();
   }
   
   private static Owner owner() {
      return Owner.builder()
         .type("application/vnd.vmware.vcloud.owner+xml")
         .user(Reference.builder()
               .type("application/vnd.vmware.admin.user+xml")
               .name("qunying.huang@enstratus.com")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/967d317c-4273-4a95-b8a4-bf63b78e9c69"))
               .build())
         .build();
   }
   
   public static final AdminCatalog modifyCatalog() {
      return catalog().toBuilder()
         .name("new QunyingTestCatalog")
         .description("new Testing")
         .build();
   }
}
