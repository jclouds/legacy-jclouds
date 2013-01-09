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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ADMIN_CATALOG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CATALOG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CATALOG_ITEM;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ENTITY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.METADATA;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.METADATA_VALUE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASK;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorApiExpectTest;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;

/**
 * Test the {@link CatalogApi} by observing its side effects.
 * 
 * @author grkvlt@apache.org, Adrian Cole
 */
@Test(groups = { "unit", "user" }, singleThreaded = true, testName = "CatalogApiExpectTest")
public class CatalogApiExpectTest extends VCloudDirectorApiExpectTest {
   static String catalog = "7212e451-76e1-4631-b2de-ba1dfd8080e4";
   static String catalogUrn = "urn:vcloud:catalog:" + catalog;
   static URI catalogHref = URI.create(endpoint + "/catalog/" + catalog);
   
   HttpRequest get = HttpRequest.builder()
            .method("GET")
            .endpoint(catalogHref)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

    HttpResponse getResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/catalog.xml", CATALOG + ";version=1.5"))
            .build();
    
   @Test
   public void testGetCatalogHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, get, getResponse);
      assertEquals(api.getCatalogApi().get(catalogHref), catalog());
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
                                  // TODO: remove this when VCloudDirectorApiExpectTest no longer inherits from VCloudDirectorAdminApiExpectTest
                                  .e("Link").a("rel", "alternate").a("type", ADMIN_CATALOG).a("href", catalogHref.toString()).up());
   
   HttpResponse resolveCatalogResponse = HttpResponse.builder()
           .statusCode(200)
           .payload(payloadFromStringWithContentType(catalogEntity, ENTITY + ";version=1.5"))
           .build();
   
   @Test
   public void testGetCatalogUrn() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, resolveCatalog, resolveCatalogResponse, get, getResponse);
      assertEquals(api.getCatalogApi().get(catalogUrn), catalog());
   }
   
   HttpRequest add = HttpRequest.builder()
            .method("POST")
            .endpoint(catalogHref + "/catalogItems")
            .addHeader("Accept", "application/vnd.vmware.vcloud.catalogItem+xml")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .payload(payloadFromResourceWithContentType("/catalog/newCatalogItem.xml", CATALOG_ITEM))
            .build();

   HttpResponse addResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/adddCatalogItem.xml", CATALOG_ITEM + ";version=1.5"))
            .build();
   
   CatalogItem newItem = CatalogItem.builder()
            .name("newCatalogItem")
            .description("New Catalog Item")
            .entity(ubuntuVappTemplateReference())
            .build();

   @Test
   public void testAddCatalogItemHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, add, addResponse);
      assertEquals(api.getCatalogApi().addItem(catalogHref, newItem), adddCatalogItem());
   }
   
   @Test
   public void testAddCatalogItemUrn() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, resolveCatalog, resolveCatalogResponse, add, addResponse);
      assertEquals(api.getCatalogApi().addItem(catalogUrn, newItem), adddCatalogItem());
   }
   
   HttpRequest getMetadata = HttpRequest.builder()
            .method("GET")
            .endpoint(catalogHref + "/metadata")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();

   HttpResponse getMetadataResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/catalogMetadata.xml", METADATA))
            .build();

   @Test
   public void testGetCatalogMetadataHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, getMetadata, getMetadataResponse);
      assertEquals(api.getMetadataApi(catalogHref).get(), metadata());
   }

   static Metadata metadata() {
      return Metadata.builder()
                     .type("application/vnd.vmware.vcloud.metadata+xml")
                     .href(URI.create(endpoint + "/catalog/" + catalog + "/metadata"))
                     .link(Link.builder()
                               .rel("up")
                               .type("application/vnd.vmware.vcloud.catalog+xml")
                               .href(catalogHref)
                               .build())
                     .entries(ImmutableSet.of(metadataEntry()))
                     .build();
   }
   
   HttpRequest getMetadataValue = HttpRequest.builder()
            .method("GET")
            .endpoint(catalogHref + "/metadata/KEY")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();

   HttpResponse getMetadataValueResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/catalogMetadataValue.xml", METADATA_VALUE))
            .build();

   @Test
   public void testGetCatalogMetadataEntryHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, getMetadataValue, getMetadataValueResponse);
      assertEquals(api.getMetadataApi(catalogHref).get("KEY"), "VALUE");
   }

   static String item = "a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df";
   static URI itemHref = URI.create(endpoint + "/catalogItem/" + item);
   static String itemUrn = "urn:vcloud:catalogitem:" + item;

   HttpRequest resolveItem = HttpRequest.builder()
            .method("GET")
            .endpoint(endpoint + "/entity/" + itemUrn)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();
   
   String itemEntity = asString(createXMLBuilder("Entity").a("xmlns", "http://www.vmware.com/vcloud/v1.5")
                                                             .a("name", itemUrn)
                                                             .a("id", itemUrn)
                                                             .a("type", ENTITY)
                                                             .a("href", endpoint + "/entity/" + itemUrn)
                               .e("Link").a("rel", "alternate").a("type", CATALOG_ITEM).a("href", itemHref.toString()).up());

   HttpResponse resolveItemResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromStringWithContentType(itemEntity, ENTITY + ";version=1.5"))
            .build();
   
   HttpRequest getItem = HttpRequest.builder()
            .method("GET")
            .endpoint(endpoint + "/catalogItem/" + item)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();

   HttpResponse getItemResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/catalogItem.xml", CATALOG_ITEM))
            .build();
      
   @Test
   public void testGetCatalogItemHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, getItem, getItemResponse);
      assertEquals(api.getCatalogApi().getItem(itemHref), catalogItem());
   }
   
   @Test
   public void testGetCatalogItemUrn() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, resolveItem, resolveItemResponse, getItem, getItemResponse);
      assertEquals(api.getCatalogApi().getItem(itemUrn), catalogItem());
   }
   
   HttpRequest editItem = HttpRequest.builder()
            .method("PUT")
            .endpoint(endpoint + "/catalogItem/" + item)
            .addHeader("Accept", "application/vnd.vmware.vcloud.catalogItem+xml")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .payload(payloadFromResourceWithContentType("/catalog/editCatalogItem.xml", CATALOG_ITEM))
            .build();

   HttpResponse editItemResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/editCatalogItem.xml", CATALOG_ITEM + ";version=1.5"))
            .build();

   @Test
   public void testEditCatalogItemHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, editItem, editItemResponse);
      assertEquals(api.getCatalogApi().editItem(itemHref, catalogItem()), catalogItem());
   }
   
   @Test
   public void testEditCatalogItemUrn() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, resolveItem, resolveItemResponse, editItem, editItemResponse);
      assertEquals(api.getCatalogApi().editItem(itemUrn, catalogItem()), catalogItem());
   }
   
   HttpRequest removeItem = HttpRequest.builder()
            .method("DELETE")
            .endpoint(endpoint + "/catalogItem/" + item)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();

   HttpResponse removeItemResponse = HttpResponse.builder()
            .statusCode(200)
            .build();
      
   @Test
   public void testRemoveCatalogItemHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, removeItem, removeItemResponse);
      api.getCatalogApi().removeItem(itemHref);
   }

   @Test
   public void testRemoveCatalogItemUrn() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, resolveItem, resolveItemResponse, removeItem, removeItemResponse);
      api.getCatalogApi().removeItem(itemUrn);
   }
   
   HttpRequest getItemMetadata = HttpRequest.builder()
            .method("GET")
            .endpoint(endpoint + "/catalogItem/" + item + "/metadata")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();

   HttpResponse getItemMetadataResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/catalogItemMetadata.xml", METADATA))
            .build();
      
   Metadata expected = Metadata.builder()
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create(endpoint + "/catalogItem/" + item + "/metadata"))
            .link(Link.builder()
                  .rel("up")
                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
                  .href(itemHref)
                  .build())
            .entries(ImmutableSet.of(itemMetadataEntry()))
            .build();

   @Test
   public void testGetCatalogItemMetadataHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, getItemMetadata, getItemMetadataResponse);
      assertEquals(api.getMetadataApi(itemHref).get(), expected);
   }

   HttpRequest mergeItemMetadata = HttpRequest.builder()
            .method("POST")
            .endpoint(endpoint + "/catalogItem/" + item + "/metadata")
            .addHeader("Accept", "application/vnd.vmware.vcloud.task+xml")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .payload(payloadFromResourceWithContentType("/catalog/mergeCatalogItemMetadata.xml", METADATA))
            .build();

   HttpResponse mergeItemMetadataResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/mergeMetadataTask.xml", TASK + ";version=1.5"))
            .build();
      
   @Test
   public void testMergeCatalogItemMetadataHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, mergeItemMetadata, mergeItemMetadataResponse);
      assertEquals(api.getMetadataApi(itemHref).putAll(ImmutableMap.of("KEY", "VALUE")), mergeMetadataTask());
   }
   
   HttpRequest getItemMetadataValue = HttpRequest.builder()
            .method("GET")
            .endpoint(endpoint + "/catalogItem/" + item + "/metadata/KEY")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();
   
   HttpResponse getItemMetadataValueResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/catalogItemMetadataValue.xml", METADATA_VALUE + ";version=1.5"))
            .build();
   
   @Test
   public void testGetCatalogItemMetadataEntryHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, getItemMetadataValue, getItemMetadataValueResponse);
      assertEquals(api.getMetadataApi(itemHref).get("KEY"), "VALUE");
   }
   
   HttpRequest putItemMetadata = HttpRequest.builder()
            .method("PUT")
            .endpoint(endpoint + "/catalogItem/" + item + "/metadata/KEY")
            .addHeader("Accept", "application/vnd.vmware.vcloud.task+xml")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .payload(payloadFromResourceWithContentType("/catalog/setCatalogItemMetadataValue.xml", METADATA_VALUE))
            .build();

   HttpResponse putItemMetadataResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/setMetadataValueTask.xml", TASK + ";version=1.5"))
            .build(); 
   
   @Test
   public void testSetCatalogItemMetadataEntryHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, putItemMetadata, putItemMetadataResponse);
      assertEquals(api.getMetadataApi(itemHref).put("KEY", "KITTENS"), setMetadataValueTask());
   }
   
   HttpRequest removeItemMetadataEntry = HttpRequest.builder()
            .method("DELETE")
            .endpoint(endpoint + "/catalogItem/" + item + "/metadata/KEY")
            .addHeader("Accept", "application/vnd.vmware.vcloud.task+xml")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();

   HttpResponse removeItemMetadataEntryResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/removeMetadataEntryTask.xml", TASK))
            .build();   
   
   @Test
   public void testRemoveCatalogItemMetadataEntryHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, removeItemMetadataEntry, removeItemMetadataEntryResponse);
      assertEquals(api.getMetadataApi(itemHref).remove("KEY"), removeTask());
   }
   
   public static final Catalog catalog() {
      return Catalog.builder()
      		      .name("QunyingTestCatalog")
      		      .type("application/vnd.vmware.vcloud.catalog+xml")
      		      .id(catalogUrn)
      		      .href(catalogHref)
      		      .link(Link.builder()
      		            .rel("up")
      		            .type("application/vnd.vmware.vcloud.org+xml")
      		            .href(URI.create(endpoint + "/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
      		            .build())
      		      .link(Link.builder()
      		            .rel("add")
      		            .type("application/vnd.vmware.vcloud.catalogItem+xml")
      		            .href(URI.create(endpoint + "/catalog/" + catalog + "/catalogItems"))
      		            .build())
      		      .link(Link.builder()
      		            .rel("down")
      		            .type("application/vnd.vmware.vcloud.metadata+xml")
      		            .href(URI.create(endpoint + "/catalog/" + catalog + "/metadata"))
      		            .build())
  		            .item(Reference.builder()
  		                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
  		                  .name("ubuntu10")
  		                  .href(itemHref)
  		                  .build())
  		            .item(Reference.builder()
  		                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
  		                  .name("imageTesting")
  		                  .href(URI.create(endpoint + "/catalogItem/a9e0afdb-a42b-4688-8409-2ac68cf22939"))
  		                  .build())
      		      .description("Testing")
      		      .isPublished(false)
      		      .build();
   }

   public static CatalogItem adddCatalogItem() {
      return CatalogItem.builder()
                  .name("newCatalogItem")
                  .id("urn:vcloud:catalogitem:" + item)
                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
                  .href(itemHref)
                  .link(Link.builder()
                        .rel("up")
                        .type("application/vnd.vmware.vcloud.catalog+xml")
                        .href(catalogHref)
                        .build())
                  .link(Link.builder()
                        .rel("down")
                        .type("application/vnd.vmware.vcloud.metadata+xml")
                        .href(URI.create(endpoint + "/catalogItem/" + item + "/metadata"))
                        .build())
                  .link(Link.builder()
                        .rel("edit")
                        .type("application/vnd.vmware.vcloud.catalogItem+xml")
                        .href(itemHref)
                        .build())
                  .link(Link.builder()
                        .rel("remove")
                        .href(itemHref)
                        .build())
                  .description("New Catalog Item")
                  .entity(ubuntuVappTemplateReference())
                  .build();
   }
   
   public static Reference ubuntuVappTemplateReference() {
      return Reference.builder()
                  .type("application/vnd.vmware.vcloud.vAppTemplate+xml")
                  .name("ubuntu10")
                  .href(URI.create(endpoint + "/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9"))
                  .build();
   }

   public static MetadataEntry metadataEntry() {
      return  MetadataEntry.builder()
                  .href(URI.create(endpoint + "/catalog/" + catalog + "/metadata/KEY"))
                  .link(Link.builder()
                        .rel("up")
                        .type("application/vnd.vmware.vcloud.metadata+xml")
                        .href(URI.create(endpoint + "/catalog/" + catalog + "/metadata"))
                        .build())
                  .entry("KEY", "VALUE")
                  .build();
   }

   public static MetadataEntry itemMetadataEntry() {
      return  MetadataEntry.builder()
                  .href(URI.create(endpoint + "/catalogItem/" + item + "/metadata/KEY"))
                  .link(Link.builder()
                        .rel("up")
                        .type("application/vnd.vmware.vcloud.metadata+xml")
                        .href(URI.create(endpoint + "/catalogItem/" + item + "/metadata"))
                        .build())
                  .entry("KEY", "VALUE")
                  .build();
   }
   
   public static CatalogItem catalogItem() {
      return CatalogItem.builder()
            .name("ubuntu10")
            .id("urn:vcloud:catalogitem:" + item)
            .type("application/vnd.vmware.vcloud.catalogItem+xml")
            .href(itemHref)
            .link(Link.builder()
                  .rel("up")
                  .type("application/vnd.vmware.vcloud.catalog+xml")
                  .href(catalogHref)
                  .build())
            .link(Link.builder()
                  .rel("down")
                  .type("application/vnd.vmware.vcloud.metadata+xml")
                  .href(URI.create(endpoint + "/catalogItem/" + item + "/metadata"))
                  .build())
            .link(Link.builder()
                  .rel("edit")
                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
                  .href(itemHref)
                  .build())
            .link(Link.builder()
                  .rel("remove")
                  .href(itemHref)
                  .build())
            .description("For testing")
            .entity(ubuntuVappTemplateReference())
            .build();
   }

   public static Task mergeMetadataTask() {
      return Task.builder()
            .name("task")
            .id("urn:vcloud:task:c6dca927-eab4-41fa-ad6a-3ac58602541c")
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create(endpoint + "/task/c6dca927-eab4-41fa-ad6a-3ac58602541c"))
            .status("running")
            .startTime(dateService.iso8601DateParse("2012-02-13T06:35:08.011-05:00"))
            .expiryTime(dateService.iso8601DateParse("2012-05-13T06:35:08.011-04:00"))
            .operationName("metadataUpdate")
            .operation("Updating metadata for Catalog Item (" + item + ")")
            .link(Link.builder()
                  .rel("task:cancel")
                  .href(URI.create(endpoint + "/task/c6dca927-eab4-41fa-ad6a-3ac58602541c/action/cancel"))
                  .build())
            .owner(Reference.builder()
                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
                  .name("")
                  .href(itemHref)
                  .build())
            .user(Reference.builder()
                  .type("application/vnd.vmware.admin.user+xml")
                  .name("adk@cloudsoftcorp.com")
                  .href(URI.create(endpoint + "/admin/user/e9eb1b29-0404-4c5e-8ef7-e584acc51da9"))
                  .build())
            .org(Reference.builder()
                  .type("application/vnd.vmware.vcloud.org+xml")
                  .name("JClouds")
                  .href(URI.create(endpoint + "/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
                  .build())
            .build();
   }

   public static Task setMetadataValueTask() {
      return Task.builder()
            .name("task")
            .id("urn:vcloud:task:c6dca927-eab4-41fa-ad6a-3ac58602541c")
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create(endpoint + "/task/c6dca927-eab4-41fa-ad6a-3ac58602541c"))
            .status("running")
            .startTime(dateService.iso8601DateParse("2012-02-13T06:35:08.011-05:00"))
            .expiryTime(dateService.iso8601DateParse("2012-05-13T06:35:08.011-04:00"))
            .operationName("metadataSet")
            .operation("Setting metadata for Catalog Item (" + item + ")")
            .link(Link.builder()
                  .rel("task:cancel")
                  .href(URI.create(endpoint + "/task/c6dca927-eab4-41fa-ad6a-3ac58602541c/action/cancel"))
                  .build())
            .owner(Reference.builder()
                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
                  .name("")
                  .href(itemHref)
                  .build())
            .user(Reference.builder()
                  .type("application/vnd.vmware.admin.user+xml")
                  .name("adk@cloudsoftcorp.com")
                  .href(URI.create(endpoint + "/admin/user/e9eb1b29-0404-4c5e-8ef7-e584acc51da9"))
                  .build())
            .org(Reference.builder()
                  .type("application/vnd.vmware.vcloud.org+xml")
                  .name("JClouds")
                  .href(URI.create(endpoint + "/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
                  .build())
            .build();
   }

   public static Task removeTask() {
      return Task.builder()
            .name("task")
            .id("urn:vcloud:task:c6dca927-eab4-41fa-ad6a-3ac58602541c")
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create(endpoint + "/task/c6dca927-eab4-41fa-ad6a-3ac58602541c"))
            .status("running")
            .startTime(dateService.iso8601DateParse("2012-02-13T06:35:08.011-05:00"))
            .expiryTime(dateService.iso8601DateParse("2012-05-13T06:35:08.011-04:00"))
            .operationName("metadataDelete")
            .operation("Deleting metadata for Catalog Item (" + item + ")")
            .link(Link.builder()
                  .rel("task:cancel")
                  .href(URI.create(endpoint + "/task/c6dca927-eab4-41fa-ad6a-3ac58602541c/action/cancel"))
                  .build())
            .owner(Reference.builder()
                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
                  .name("")
                  .href(itemHref)
                  .build())
            .user(Reference.builder()
                  .type("application/vnd.vmware.admin.user+xml")
                  .name("adk@cloudsoftcorp.com")
                  .href(URI.create(endpoint + "/admin/user/e9eb1b29-0404-4c5e-8ef7-e584acc51da9"))
                  .build())
            .org(Reference.builder()
                  .type("application/vnd.vmware.vcloud.org+xml")
                  .name("JClouds")
                  .href(URI.create(endpoint + "/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
                  .build())
            .build();
   }
}
      
