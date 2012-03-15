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

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItems;
import org.jclouds.vcloud.director.v1_5.domain.CatalogType;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Test the {@link CatalogClient} by observing its side effects.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "unit", "user", "catalog" }, singleThreaded = true, testName = "CatalogClientExpectTest")
public class CatalogClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {

   @Test
   public void testGetCatalog() {
      HttpRequest catalogRequest = HttpRequest.builder()
              .method("GET")
              .endpoint(URI.create(endpoint + "/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
              .headers(ImmutableMultimap.<String, String> builder()
                                .put("Accept", "*/*")
                                .put("x-vcloud-authorization", token)
                                .build())
              .build();

      HttpResponse catalogResponse = HttpResponse.builder()
              .statusCode(200)
              .payload(payloadFromResourceWithContentType("/catalog/catalog.xml", VCloudDirectorMediaType.CATALOG + ";version=1.5"))
              .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogRequest, catalogResponse);

      CatalogType expected = catalog();

      URI catalogURI = URI.create(endpoint + "/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4");      
      assertEquals(client.getCatalogClient().getCatalog(catalogURI), expected);
   }

   @Test
   public void testAddCatalogItem() {
      HttpRequest catalogItemRequest = HttpRequest.builder()
            .method("POST")
            .endpoint(URI.create(endpoint + "/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/catalogItems"))
            .headers(ImmutableMultimap.<String, String> builder()
                              .put("Accept", "application/vnd.vmware.vcloud.catalogItem+xml")
                              .put("x-vcloud-authorization", token)
                              .build())
            .payload(payloadFromResourceWithContentType("/catalog/newCatalogItem.xml", VCloudDirectorMediaType.CATALOG_ITEM))
            .build();

      HttpResponse catalogItemResponse = HttpResponse.builder()
              .statusCode(200)
              .payload(payloadFromResourceWithContentType("/catalog/createdCatalogItem.xml", VCloudDirectorMediaType.CATALOG_ITEM + ";version=1.5"))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogItemRequest, catalogItemResponse);

      URI catalogURI = URI.create(endpoint + "/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"); 
      
      CatalogItem newItem = CatalogItem.builder()
            .name("newCatalogItem")
            .description("New Catalog Item")
            .entity(ubuntuVappTemplateReference())
            .build();

      CatalogItem expected = createdCatalogItem();
      
      assertEquals(client.getCatalogClient().addCatalogItem(catalogURI, newItem), expected);
   }

   @Test
   public void testGetCatalogMetadata() {
      HttpRequest catalogRequest = HttpRequest.builder()
            .method("GET")
            .endpoint(URI.create(endpoint + "/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/metadata"))
            .headers(ImmutableMultimap.<String, String> builder()
                              .put("Accept", "*/*")
                              .put("x-vcloud-authorization", token)
                              .build())
            .build();

      HttpResponse catalogResponse = HttpResponse.builder()
              .statusCode(200)
              .payload(payloadFromResourceWithContentType("/catalog/catalogMetadata.xml", VCloudDirectorMediaType.METADATA))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogRequest, catalogResponse);

      URI catalogURI = URI.create(endpoint + "/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4");
      
      Metadata expected = Metadata.builder()
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/metadata"))
            .link(Link.builder()
                  .rel("up")
                  .type("application/vnd.vmware.vcloud.catalog+xml")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
                  .build())
            .entries(ImmutableSet.of(metadataEntry()))
            .build();
      
      assertEquals(client.getCatalogClient().getMetadataClient().getMetadata(catalogURI), expected);
   }

   @Test
   public void testGetCatalogMetadataEntry() {
      HttpRequest catalogRequest = HttpRequest.builder()
            .method("GET")
            .endpoint(URI.create(endpoint + "/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/metadata/KEY"))
            .headers(ImmutableMultimap.<String, String> builder()
                              .put("Accept", "*/*")
                              .put("x-vcloud-authorization", token)
                              .build())
            .build();

      HttpResponse catalogResponse = HttpResponse.builder()
              .statusCode(200)
              .payload(payloadFromResourceWithContentType("/catalog/catalogMetadataValue.xml", VCloudDirectorMediaType.METADATA_VALUE))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogRequest, catalogResponse);

      URI catalogURI = URI.create(endpoint + "/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4");
      
      MetadataValue expected = metadataValue();
      
      assertEquals(client.getCatalogClient().getMetadataClient().getMetadataValue(catalogURI, "KEY"), expected);
   }
   
   @Test
   public void testGetCatalogItem() {
      HttpRequest catalogItemRequest = HttpRequest.builder()
            .method("GET")
            .endpoint(URI.create(endpoint + "/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
            .headers(ImmutableMultimap.<String, String> builder()
                              .put("Accept", "*/*")
                              .put("x-vcloud-authorization", token)
                              .build())
            .build();

      HttpResponse catalogItemResponse = HttpResponse.builder()
              .statusCode(200)
              .payload(payloadFromResourceWithContentType("/catalog/catalogItem.xml", VCloudDirectorMediaType.CATALOG_ITEM))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogItemRequest, catalogItemResponse);

		URI catalogItemURI =	URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df");
			
		CatalogItem expected = catalogItem();
		
		assertEquals(client.getCatalogClient().getCatalogItem(catalogItemURI), expected);
   }

   @Test
   public void testUpdateCatalogItem() {
      HttpRequest catalogItemRequest = HttpRequest.builder()
            .method("PUT")
            .endpoint(URI.create(endpoint + "/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
            .headers(ImmutableMultimap.<String, String> builder()
                              .put("Accept", "application/vnd.vmware.vcloud.catalogItem+xml")
                              .put("x-vcloud-authorization", token)
                              .build())
            .payload(payloadFromResourceWithContentType("/catalog/updateCatalogItem.xml", VCloudDirectorMediaType.CATALOG_ITEM))
            .build();

      HttpResponse catalogItemResponse = HttpResponse.builder()
              .statusCode(200)
              .payload(payloadFromResourceWithContentType("/catalog/updateCatalogItem.xml", VCloudDirectorMediaType.CATALOG_ITEM + ";version=1.5"))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogItemRequest, catalogItemResponse);

      URI catalogItemURI = URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df");         
      CatalogItem expected = catalogItem();
      
      assertEquals(client.getCatalogClient().updateCatalogItem(catalogItemURI, expected), expected);
   }
   
   @Test
   public void testDeleteCatalogItem() {
      HttpRequest catalogItemRequest = HttpRequest.builder()
            .method("DELETE")
            .endpoint(URI.create(endpoint + "/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
            .headers(ImmutableMultimap.<String, String> builder()
                              .put("Accept", "*/*")
                              .put("x-vcloud-authorization", token)
                              .build())
            .build();

      HttpResponse catalogItemResponse = HttpResponse.builder()
            .statusCode(200)
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogItemRequest, catalogItemResponse);

      URI catalogItemURI = URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df");
         
      client.getCatalogClient().deleteCatalogItem(catalogItemURI);
   }

   @Test
   public void testGetCatalogItemMetadata() {
      HttpRequest catalogItemRequest = HttpRequest.builder()
            .method("GET")
            .endpoint(URI.create(endpoint + "/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df/metadata"))
            .headers(ImmutableMultimap.<String, String> builder()
                              .put("Accept", "*/*")
                              .put("x-vcloud-authorization", token)
                              .build())
            .build();

      HttpResponse catalogItemResponse = HttpResponse.builder()
              .statusCode(200)
              .payload(payloadFromResourceWithContentType("/catalog/catalogItemMetadata.xml", VCloudDirectorMediaType.METADATA))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogItemRequest, catalogItemResponse);

      URI catalogItemURI = URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df");
      
      Metadata expected = Metadata.builder()
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df/metadata"))
            .link(Link.builder()
                  .rel("up")
                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
                  .build())
            .entries(ImmutableSet.of(itemMetadataEntry()))
            .build();
      
      assertEquals(client.getCatalogClient().getMetadataClient().getMetadata(catalogItemURI), expected);
   }

   @Test
   public void testMergeCatalogItemMetadata() {
      HttpRequest catalogItemRequest = HttpRequest.builder()
            .method("POST")
            .endpoint(URI.create(endpoint + "/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df/metadata"))
            .headers(ImmutableMultimap.<String, String> builder()
                              .put("Accept", "application/vnd.vmware.vcloud.task+xml")
                              .put("x-vcloud-authorization", token)
                              .build())
            .payload(payloadFromResourceWithContentType("/catalog/mergeCatalogItemMetadata.xml", VCloudDirectorMediaType.METADATA))
            .build();

      HttpResponse catalogItemResponse = HttpResponse.builder()
              .statusCode(200)
              .payload(payloadFromResourceWithContentType("/catalog/mergeMetadataTask.xml", VCloudDirectorMediaType.TASK + ";version=1.5"))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogItemRequest, catalogItemResponse);

      URI catalogItemURI = URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df");
 
      Metadata metadata = Metadata.builder().entry(MetadataEntry.builder().entry("KEY", "VALUE").build()).build();
      
      Task expected = mergeMetadataTask();
      
      assertEquals(client.getCatalogClient().getMetadataClient().mergeMetadata(catalogItemURI, metadata), expected);
   }

   @Test
   public void testGetCatalogItemMetadataEntry() {
      HttpRequest catalogItemRequest = HttpRequest.builder()
            .method("GET")
            .endpoint(URI.create(endpoint + "/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df/metadata/KEY"))
            .headers(ImmutableMultimap.<String, String> builder()
                              .put("Accept", "*/*")
                              .put("x-vcloud-authorization", token)
                              .build())
            .build();

      HttpResponse catalogItemResponse = HttpResponse.builder()
              .statusCode(200)
              .payload(payloadFromResourceWithContentType("/catalog/catalogItemMetadataValue.xml", VCloudDirectorMediaType.METADATA_VALUE + ";version=1.5"))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogItemRequest, catalogItemResponse);

      URI catalogItemURI = URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df");
      
      MetadataValue expected = itemMetadataValue();
      
      assertEquals(client.getCatalogClient().getMetadataClient().getMetadataValue(catalogItemURI, "KEY"), expected);
   }

   @Test
   public void testSetCatalogItemMetadataEntry() {
      HttpRequest catalogItemRequest = HttpRequest.builder()
            .method("PUT")
            .endpoint(URI.create(endpoint + "/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df/metadata/KEY"))
            .headers(ImmutableMultimap.<String, String> builder()
                              .put("Accept", "application/vnd.vmware.vcloud.task+xml")
                              .put("x-vcloud-authorization", token)
                              .build())
            .payload(payloadFromResourceWithContentType("/catalog/setCatalogItemMetadataValue.xml", VCloudDirectorMediaType.METADATA_VALUE))
            .build();

      HttpResponse catalogItemResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/setMetadataValueTask.xml", VCloudDirectorMediaType.TASK + ";version=1.5"))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogItemRequest, catalogItemResponse);

      URI catalogItemURI = URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df");
      
      MetadataValue value = MetadataValue.builder().value("KITTENS").build();
      
      Task expected = setMetadataValueTask();
      
      assertEquals(client.getCatalogClient().getMetadataClient().setMetadata(catalogItemURI, "KEY", value), expected);
   }

   @Test
   public void testDeleteCatalogItemMetadataEntry() {
      HttpRequest catalogItemRequest = HttpRequest.builder()
            .method("DELETE")
            .endpoint(URI.create(endpoint + "/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df/metadata/KEY"))
            .headers(ImmutableMultimap.<String, String> builder()
                              .put("Accept", "application/vnd.vmware.vcloud.task+xml")
                              .put("x-vcloud-authorization", token)
                              .build())
            .build();

      HttpResponse catalogItemResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/catalog/deleteMetadataEntryTask.xml", VCloudDirectorMediaType.TASK))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogItemRequest, catalogItemResponse);

      URI catalogItemURI = URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df");
      
      Task expected = deleteMetadataEntryTask();
      
      assertEquals(client.getCatalogClient().getMetadataClient().deleteMetadataEntry(catalogItemURI, "KEY"), expected);
   }

   @SuppressWarnings("unchecked")
   public static final CatalogType catalog() {
      return CatalogType.builder()
      		      .name("QunyingTestCatalog")
      		      .type("application/vnd.vmware.vcloud.catalog+xml")
      		      .id("urn:vcloud:catalog:7212e451-76e1-4631-b2de-ba1dfd8080e4")
      		      .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
      		      .link(Link.builder()
      		            .rel("up")
      		            .type("application/vnd.vmware.vcloud.org+xml")
      		            .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
      		            .build())
      		      .link(Link.builder()
      		            .rel("add")
      		            .type("application/vnd.vmware.vcloud.catalogItem+xml")
      		            .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/catalogItems"))
      		            .build())
      		      .link(Link.builder()
      		            .rel("down")
      		            .type("application/vnd.vmware.vcloud.metadata+xml")
      		            .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/metadata"))
      		            .build())
      		      .catalogItems(CatalogItems.builder()
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
      		            .build())
      		      .description("Testing")
      		      .isPublished(false)
      		      .build();
   }

   public static CatalogItem createdCatalogItem() {
      return CatalogItem.builder()
                  .name("newCatalogItem")
                  .id("urn:vcloud:catalogitem:a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df")
                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
                  .link(Link.builder()
                        .rel("up")
                        .type("application/vnd.vmware.vcloud.catalog+xml")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
                        .build())
                  .link(Link.builder()
                        .rel("down")
                        .type("application/vnd.vmware.vcloud.metadata+xml")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df/metadata"))
                        .build())
                  .link(Link.builder()
                        .rel("edit")
                        .type("application/vnd.vmware.vcloud.catalogItem+xml")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
                        .build())
                  .link(Link.builder()
                        .rel("remove")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
                        .build())
                  .description("New Catalog Item")
                  .entity(ubuntuVappTemplateReference())
                  .build();
   }
   
   public static Reference ubuntuVappTemplateReference() {
      return Reference.builder()
				      .type("application/vnd.vmware.vcloud.vAppTemplate+xml")
				      .name("ubuntu10")
				      .href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9"))
				      .build();
   }

   public static MetadataEntry metadataEntry() {
      return  MetadataEntry.builder()
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/metadata/KEY"))
                  .link(Link.builder()
                        .rel("up")
                        .type("application/vnd.vmware.vcloud.metadata+xml")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/metadata"))
                        .build())
                  .entry("KEY", "VALUE")
                  .build();
   }

   public static MetadataEntry itemMetadataEntry() {
      return  MetadataEntry.builder()
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df/metadata/KEY"))
                  .link(Link.builder()
                        .rel("up")
                        .type("application/vnd.vmware.vcloud.metadata+xml")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df/metadata"))
                        .build())
                  .entry("KEY", "VALUE")
                  .build();
   }

   public static MetadataValue metadataValue() {
      return  MetadataValue.builder()
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/metadata/KEY"))
                  .link(Link.builder()
                        .rel("up")
                        .type("application/vnd.vmware.vcloud.metadata+xml")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/metadata"))
                        .build())
                  .value("VALUE")
                  .build();
   }

   public static MetadataValue itemMetadataValue() {
      return  MetadataValue.builder()
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df/metadata/KEY"))
                  .link(Link.builder()
                        .rel("up")
                        .type("application/vnd.vmware.vcloud.metadata+xml")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df/metadata"))
                        .build())
                  .value("VALUE")
                  .build();
   }
   
   public static CatalogItem catalogItem() {
      return CatalogItem.builder()
            .name("ubuntu10")
            .id("urn:vcloud:catalogitem:a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df")
            .type("application/vnd.vmware.vcloud.catalogItem+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
            .link(Link.builder()
                  .rel("up")
                  .type("application/vnd.vmware.vcloud.catalog+xml")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
                  .build())
            .link(Link.builder()
                  .rel("down")
                  .type("application/vnd.vmware.vcloud.metadata+xml")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df/metadata"))
                  .build())
            .link(Link.builder()
                  .rel("edit")
                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
                  .build())
            .link(Link.builder()
                  .rel("remove")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
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
            .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c"))
            .status("running")
            .startTime(dateService.iso8601DateParse("2012-02-13T06:35:08.011-05:00"))
            .expiryTime(dateService.iso8601DateParse("2012-05-13T06:35:08.011-04:00"))
            .operationName("metadataUpdate")
            .operation("Updating metadata for Catalog Item (a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df)")
            .link(Link.builder()
                  .rel("task:cancel")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c/action/cancel"))
                  .build())
            .owner(Reference.builder()
                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
                  .name("")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
                  .build())
            .user(Reference.builder()
                  .type("application/vnd.vmware.admin.user+xml")
                  .name("adk@cloudsoftcorp.com")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/e9eb1b29-0404-4c5e-8ef7-e584acc51da9"))
                  .build())
            .org(Reference.builder()
                  .type("application/vnd.vmware.vcloud.org+xml")
                  .name("JClouds")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
                  .build())
            .build();
   }

   public static Task setMetadataValueTask() {
      return Task.builder()
            .name("task")
            .id("urn:vcloud:task:c6dca927-eab4-41fa-ad6a-3ac58602541c")
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c"))
            .status("running")
            .startTime(dateService.iso8601DateParse("2012-02-13T06:35:08.011-05:00"))
            .expiryTime(dateService.iso8601DateParse("2012-05-13T06:35:08.011-04:00"))
            .operationName("metadataSet")
            .operation("Setting metadata for Catalog Item (a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df)")
            .link(Link.builder()
                  .rel("task:cancel")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c/action/cancel"))
                  .build())
            .owner(Reference.builder()
                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
                  .name("")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
                  .build())
            .user(Reference.builder()
                  .type("application/vnd.vmware.admin.user+xml")
                  .name("adk@cloudsoftcorp.com")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/e9eb1b29-0404-4c5e-8ef7-e584acc51da9"))
                  .build())
            .org(Reference.builder()
                  .type("application/vnd.vmware.vcloud.org+xml")
                  .name("JClouds")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
                  .build())
            .build();
   }

   public static Task deleteMetadataEntryTask() {
      return Task.builder()
            .name("task")
            .id("urn:vcloud:task:c6dca927-eab4-41fa-ad6a-3ac58602541c")
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c"))
            .status("running")
            .startTime(dateService.iso8601DateParse("2012-02-13T06:35:08.011-05:00"))
            .expiryTime(dateService.iso8601DateParse("2012-05-13T06:35:08.011-04:00"))
            .operationName("metadataDelete")
            .operation("Deleting metadata for Catalog Item (a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df)")
            .link(Link.builder()
                  .rel("task:cancel")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c/action/cancel"))
                  .build())
            .owner(Reference.builder()
                  .type("application/vnd.vmware.vcloud.catalogItem+xml")
                  .name("")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/a36fdac9-b8c2-43e2-9a4c-2ffaf3ee13df"))
                  .build())
            .user(Reference.builder()
                  .type("application/vnd.vmware.admin.user+xml")
                  .name("adk@cloudsoftcorp.com")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/e9eb1b29-0404-4c5e-8ef7-e584acc51da9"))
                  .build())
            .org(Reference.builder()
                  .type("application/vnd.vmware.vcloud.org+xml")
                  .name("JClouds")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
                  .build())
            .build();
   }
}
		
