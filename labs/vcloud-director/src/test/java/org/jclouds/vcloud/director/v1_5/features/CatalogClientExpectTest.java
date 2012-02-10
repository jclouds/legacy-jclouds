/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.features;

import static org.testng.Assert.*;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItems;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Test the {@link CatalogClient} by observing its side effects.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "unit", singleThreaded = true, testName = "CatalogClientExpectTest")
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

      Catalog expected = Catalog.builder()
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
              .build();

      Reference catalogRef = Reference.builder()
            .type("application/vnd.vmware.vcloud.catalog+xml")
            .name("QunyingTestCatalog")
            .href(URI.create(endpoint + "/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
            .build();
      
      assertEquals(client.getCatalogClient().getCatalog(catalogRef), expected);
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
              .payload(payloadFromResourceWithContentType("/catalog/catalogItem.xml", VCloudDirectorMediaType.CATALOG_ITEM + ";version=1.5"))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogItemRequest, catalogItemResponse);

      Reference catalogRef = Reference.builder()
            .type("application/vnd.vmware.vcloud.catalog+xml")
            .name("QunyingTestCatalog")
            .href(URI.create(endpoint + "/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
            .build();
      
      Reference ubuntu = Reference.builder()
            .type("application/vnd.vmware.vcloud.vAppTemplate+xml")
            .name("ubuntu10")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/vAppTemplate/vappTemplate-ef4415e6-d413-4cbb-9262-f9bbec5f2ea9"))
            .build();
      
      CatalogItem newItem = CatalogItem.builder()
            .name("newCatalogItem")
            .description("New Catalog Item")
            .entity(ubuntu)
            .build();

      CatalogItem expected = CatalogItem.builder()
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
            .entity(ubuntu)
            .build();
      
      assertEquals(client.getCatalogClient().addCatalogItem(catalogRef, newItem), expected);
   }

   @Test
   public void testGetCatalogMetadata() {
      HttpRequest catalogItemRequest = HttpRequest.builder()
            .method("GET")
            .endpoint(URI.create(endpoint + "/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/metadata"))
            .headers(ImmutableMultimap.<String, String> builder()
                              .put("Accept", "*/*")
                              .put("x-vcloud-authorization", token)
                              .build())
            .build();

      HttpResponse catalogItemResponse = HttpResponse.builder()
              .statusCode(200)
              .payload(payloadFromResourceWithContentType("/catalog/catalogMetadata.xml", VCloudDirectorMediaType.METADATA + ";version=1.5"))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, catalogItemRequest, catalogItemResponse);

      Reference catalogRef = Reference.builder()
            .type("application/vnd.vmware.vcloud.catalog+xml")
            .name("QunyingTestCatalog")
            .href(URI.create(endpoint + "/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
            .build();
      
      Metadata expected = Metadata.builder()
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api//catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4/metadata"))
            .link(Link.builder()
                  .rel("up")
                  .type("application/vnd.vmware.vcloud.catalog+xml")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
                  .build())
            .metadata(ImmutableSet.of(MetadataEntry.builder().entry("key", "value").build()))
            .build();
      
      assertEquals(client.getCatalogClient().getCatalogMetadata(catalogRef), expected);
   }
}
