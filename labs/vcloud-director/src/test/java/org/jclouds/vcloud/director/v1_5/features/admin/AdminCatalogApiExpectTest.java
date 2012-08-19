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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ADMIN_CATALOG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ADMIN_ORG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CATALOG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ENTITY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ORG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.OWNER;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.PUBLISH_CATALOG_PARAMS;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminApi;
import org.jclouds.vcloud.director.v1_5.domain.AdminCatalog;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.params.PublishCatalogParams;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;

/**
 * Test the {@link AdminCatalogApi} by observing its side effects.
 * 
 * @author grkvlt@apache.org, Adrian Cole
 */
@Test(groups = { "unit", "admin" }, singleThreaded = true, testName = "CatalogApiExpectTest")
public class AdminCatalogApiExpectTest extends VCloudDirectorAdminApiExpectTest {
   
   static String catalog = "7212e451-76e1-4631-b2de-ba1dfd8080e4";
   static String catalogUrn = "urn:vcloud:catalog:" + catalog;
   static URI catalogAdminHref = URI.create(endpoint + "/admin/catalog/" + catalog);
   static URI catalogHref = URI.create(endpoint + "/catalog/" + catalog);
   
   HttpRequest get = HttpRequest.builder()
            .method("GET")
            .endpoint(catalogAdminHref)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

    HttpResponse getResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/admin/catalog.xml", ADMIN_CATALOG + ";version=1.5"))
            .build();
    
   @Test
   public void testGetCatalogHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, get, getResponse);
      assertEquals(api.getCatalogApi().get(catalogAdminHref), catalog());
   }
   
   HttpRequest resolveCatalog = HttpRequest.builder()
            .method("GET")
            .endpoint(endpoint + "/entity/" + catalogUrn)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();
   
   String catalogEntity = asString(createXMLBuilder("Entity").a("xmlns", "http://www.vmware.com/vcloud/v1.5")
                                                             .a("name", catalogUrn)
                                                             .a("id", catalogUrn)
                                                             .a("type", ENTITY)
                                                             .a("href", endpoint + "/entity/" + catalogUrn)
                                  .e("Link").a("rel", "alternate").a("type", CATALOG).a("href", catalogHref.toString()).up()
                                  .e("Link").a("rel", "alternate").a("type", ADMIN_CATALOG).a("href", catalogAdminHref.toString()).up());
   
   HttpResponse resolveCatalogResponse = HttpResponse.builder()
           .statusCode(200)
           .payload(payloadFromStringWithContentType(catalogEntity, ENTITY + ";version=1.5"))
           .build();
   
   @Test
   public void testGetCatalogUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveCatalog, resolveCatalogResponse, get, getResponse);
      assertEquals(api.getCatalogApi().get(catalogUrn), catalog());
   }
   
   static String org = "7212e451-76e1-4631-b2de-asdasdasd";
   static String orgUrn = "urn:vcloud:org:" + org;
   static URI orgHref = URI.create(endpoint + "/org/" + org);
   static URI orgAdminHref = URI.create(endpoint + "/admin/org/" + org);
   
   HttpRequest add = HttpRequest.builder()
            .method("POST")
            .endpoint(orgAdminHref + "/catalogs")
            .addHeader("Accept", ADMIN_CATALOG)
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .payload(payloadFromResourceWithContentType("/catalog/admin/addCatalogSource.xml", VCloudDirectorMediaType.ADMIN_CATALOG))
            .build();

    HttpResponse addResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/admin/addCatalog.xml", ADMIN_CATALOG + ";version=1.5"))
            .build();
    
   @Test
   public void testAddCatalogHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, add, addResponse);
      assertEquals(api.getCatalogApi().addCatalogToOrg(addCatalogToOrgSource(), orgAdminHref), addCatalogToOrg());
   }
   
   HttpRequest resolveOrg = HttpRequest.builder()
            .method("GET")
            .endpoint(endpoint + "/entity/" + orgUrn)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();
   
   String orgEntity = asString(createXMLBuilder("Entity").a("xmlns", "http://www.vmware.com/vcloud/v1.5")
                                                             .a("name", orgUrn)
                                                             .a("id", orgUrn)
                                                             .a("type", ENTITY)
                                                             .a("href", endpoint + "/entity/" + catalogUrn)
                                  .e("Link").a("rel", "alternate").a("type", ORG).a("href", orgHref.toString()).up()
                                  .e("Link").a("rel", "alternate").a("type", ADMIN_ORG).a("href", orgAdminHref.toString()).up());
   
   HttpResponse resolveOrgResponse = HttpResponse.builder()
           .statusCode(200)
           .payload(payloadFromStringWithContentType(orgEntity, ENTITY + ";version=1.5"))
           .build();
   
   @Test
   public void testAddCatalogUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveOrg, resolveOrgResponse, add, addResponse);
      assertEquals(api.getCatalogApi().addCatalogToOrg(addCatalogToOrgSource(), orgUrn), addCatalogToOrg());
   }
   
   HttpRequest edit = HttpRequest.builder()
            .method("PUT")
            .endpoint(catalogAdminHref)
            .addHeader("Accept", ADMIN_CATALOG)
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .payload(payloadFromResourceWithContentType("/catalog/admin/editCatalogSource.xml", VCloudDirectorMediaType.ADMIN_CATALOG))
            .build();

    HttpResponse editResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/admin/editCatalog.xml", ADMIN_CATALOG + ";version=1.5"))
            .build();
    
   @Test
   public void testEditCatalogHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, edit, editResponse);
      assertEquals(api.getCatalogApi().edit(catalogAdminHref, editCatalog()), editCatalog());
   }
  
   @Test
   public void testEditCatalogUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveCatalog, resolveCatalogResponse, edit, editResponse);
      assertEquals(api.getCatalogApi().edit(catalogUrn, editCatalog()), editCatalog());
   }
   
   HttpRequest getOwner = HttpRequest.builder()
            .method("GET")
            .endpoint(catalogAdminHref + "/owner")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

    HttpResponse getOwnerResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/admin/owner.xml", OWNER + ";version=1.5"))
            .build();
    
    Owner expectedGetOwner = owner().toBuilder()
             .link(Link.builder()
                      .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
                      .type("application/vnd.vmware.vcloud.catalog+xml")
                      .rel("up")
                      .build())
             .link(Link.builder()
                      .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/owner"))
                      .type("application/vnd.vmware.vcloud.owner+xml")
                      .rel("edit")
                      .build())
             .build();
    
   @Test
   public void testGetCatalogOwnerHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, getOwner, getOwnerResponse);
      assertEquals(api.getCatalogApi().getOwner(catalogAdminHref), expectedGetOwner);
   }
   
   @Test
   public void testGetCatalogOwnerUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveCatalog, resolveCatalogResponse, getOwner, getOwnerResponse);
      assertEquals(api.getCatalogApi().getOwner(catalogUrn), expectedGetOwner);
   }
   
   HttpRequest setOwner = HttpRequest.builder()
            .method("PUT")
            .endpoint(catalogAdminHref + "/owner")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .payload(payloadFromResourceWithContentType("/catalog/admin/editOwnerSource.xml", OWNER + ";version=1.5"))
            .build();

   HttpResponse setOwnerResponse = HttpResponse.builder()
            .statusCode(204)
            .build();
   
   Owner ownerToSet = Owner.builder()
            .type("application/vnd.vmware.vcloud.owner+xml")
            .user(Reference.builder()
                  .type("application/vnd.vmware.admin.user+xml")
                  .name("adk@cloudsoftcorp.com")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/e9eb1b29-0404-4c5e-8ef7-e584acc51da9"))
                  .build())
            .build();
   
   @Test
   public void testSetCatalogOwnerHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, setOwner, setOwnerResponse);
      api.getCatalogApi().setOwner(catalogAdminHref, ownerToSet);
   }
   
   @Test
   public void testSetCatalogOwnerUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveCatalog, resolveCatalogResponse, setOwner, setOwnerResponse);
      api.getCatalogApi().setOwner(catalogUrn, ownerToSet);
   }
   
   HttpRequest publishCatalog = HttpRequest.builder()
            .method("POST")
            .endpoint(catalogAdminHref + "/action/publish")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .payload(payloadFromResourceWithContentType("/catalog/admin/publishCatalogParams.xml", PUBLISH_CATALOG_PARAMS + ";version=1.5"))
            .build();

   HttpResponse publishCatalogResponse = HttpResponse.builder()
            .statusCode(204)
            .build();
   
   @Test
   public void testPublishCatalogHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, publishCatalog,
               publishCatalogResponse);
      api.getCatalogApi().publish(catalogAdminHref, PublishCatalogParams.builder().isPublished(true).build());
   }

   @Test
   public void testPublishCatalogUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveCatalog,
               resolveCatalogResponse, publishCatalog, publishCatalogResponse);
      api.getCatalogApi().publish(catalogUrn, PublishCatalogParams.builder().isPublished(true).build());
   }

   
   HttpRequest removeCatalog = HttpRequest.builder()
            .method("DELETE")
            .endpoint(catalogAdminHref)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

   HttpResponse removeCatalogResponse = HttpResponse.builder()
            .statusCode(204)
            .build();
   
   @Test
   public void testRemoveCatalogHref() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, removeCatalog,
               removeCatalogResponse);
      api.getCatalogApi().remove(catalogAdminHref);
   }

   @Test
   public void testRemoveCatalogUrn() {
      VCloudDirectorAdminApi api = requestsSendResponses(loginRequest, sessionResponse, resolveCatalog,
               resolveCatalogResponse, removeCatalog, removeCatalogResponse);
      api.getCatalogApi().remove(catalogUrn);
   }

   //TODO: tests for access control!
   
   public static final AdminCatalog addCatalogToOrgSource() {
      return AdminCatalog.builder()
         .name("Test Catalog")
         .description("created by testCreateCatalog()")
         .build();
   }
   
   public static final AdminCatalog addCatalogToOrg() {
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
         .tasks(ImmutableSet.<Task>builder()
            .add(Task.builder()
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
   
   public static final AdminCatalog editCatalog() {
      return catalog().toBuilder()
         .name("new QunyingTestCatalog")
         .description("new Testing")
         .build();
   }
}
