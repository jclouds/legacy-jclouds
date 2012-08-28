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

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.CatalogReference;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.query.CatalogReferences;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultCatalogRecord;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;
import org.testng.annotations.Test;

/**
 * Test the {@link QueryApi} by observing its side effects.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "unit", "user" }, singleThreaded = true, testName = "QueryApiExpectTest")
public class QueryApiExpectTest extends VCloudDirectorAdminApiExpectTest {

   @Test
   public void testQueryAllCatalogs() {
      HttpRequest queryRequest = HttpRequest.builder()
              .method("GET")
              .endpoint(endpoint + "/catalogs/query")
              .addHeader("Accept", "*/*")
              .addHeader("x-vcloud-authorization", token)
              .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
              .build();

      HttpResponse queryResponse= HttpResponse.builder()
              .statusCode(200)
              .payload(payloadFromResourceWithContentType("/query/allCatalogs.xml", VCloudDirectorMediaType.QUERY_RESULT_RECORDS + ";version=1.5"))
              .build();

      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, queryRequest, queryResponse);
      
      QueryResultRecords expected = QueryResultRecords.builder()
            .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogs/query?page=1&pageSize=25&format=records"))
            .type("application/vnd.vmware.vcloud.query.records+xml")
            .name("catalog")
            .page(1)
            .pageSize(25)
            .total(3L)
            .link(Link.builder()
                        .rel("alternate")
                        .type("application/vnd.vmware.vcloud.query.references+xml")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogs/query?page=1&pageSize=25&format=references"))
                        .build())
            .link(Link.builder()
                        .rel("alternate")
                        .type("application/vnd.vmware.vcloud.query.idrecords+xml")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogs/query?page=1&pageSize=25&format=idrecords"))
                        .build())
            .record(QueryResultCatalogRecord.builder()
                        .ownerName("qunying.huang@enstratus.com")
                        .owner(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/967d317c-4273-4a95-b8a4-bf63b78e9c69"))
                        .orgName("JClouds")
                        .numberOfVAppTemplates(0)
                        .numberOfMedia(0)
                        .name("QunyingTestCatalog")
                        .shared()
                        .notPublished()
                        .creationDate(dateService.iso8601DateParse("2012-02-07T00:16:28.323-05:00"))
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
                        .build())
            .record(QueryResultCatalogRecord.builder()
                        .ownerName("system")
                        .owner(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/0ebf2453-5e95-48ab-b223-02671965ee91"))
                        .orgName("Bluelock")
                        .numberOfVAppTemplates(0)
                        .numberOfMedia(0)
                        .name("Public")
                        .notShared()
                        .published()
                        .creationDate(dateService.iso8601DateParse("2011-09-28T13:45:44.207-04:00"))
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/9e08c2f6-077a-42ce-bece-d5332e2ebb5c"))
                        .build())
            .record(QueryResultCatalogRecord.builder()
                        .ownerName("adk@cloudsoftcorp.com")
                        .owner(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/e9eb1b29-0404-4c5e-8ef7-e584acc51da9"))
                        .orgName("JClouds")
                        .numberOfVAppTemplates(0)
                        .numberOfMedia(0)
                        .name("test")
                        .shared()
                        .notPublished()
                        .creationDate(dateService.iso8601DateParse("2012-02-09T12:32:17.723-05:00"))
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/b7289d54-4ca4-497f-9a93-2d4afc97e3da"))
                        .build())
            .build();
      
      assertEquals(api.getQueryApi().catalogsQueryAll(), expected);
   }

   @Test
   public void testQueryAllCatalogReferences() {
      HttpRequest queryRequest = HttpRequest.builder()
              .method("GET")
              .endpoint(endpoint + "/catalogs/query?format=references")
              .addHeader("Accept", "*/*")
              .addHeader("x-vcloud-authorization", token)
              .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
              .build();

      HttpResponse queryResponse= HttpResponse.builder()
              .statusCode(200)
              .payload(payloadFromResourceWithContentType("/query/allCatalogReferences.xml", VCloudDirectorMediaType.QUERY_RESULT_RECORDS + ";version=1.5"))
              .build();

      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, queryRequest, queryResponse);
      
      CatalogReferences expected = CatalogReferences.builder()
            .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogs/query?page=1&pageSize=25&format=references"))
            .type("application/vnd.vmware.vcloud.query.references+xml")
            .name("catalog")
            .page(1)
            .pageSize(25)
            .total(4L)
            .link(Link.builder()
                        .rel("alternate")
                        .type("application/vnd.vmware.vcloud.query.records+xml")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogs/query?page=1&pageSize=25&format=records"))
                        .build())
            .link(Link.builder()
                        .rel("alternate")
                        .type("application/vnd.vmware.vcloud.query.idrecords+xml")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalogs/query?page=1&pageSize=25&format=idrecords"))
                        .build())
            .reference(CatalogReference.builder()
		                  .type("application/vnd.vmware.vcloud.catalog+xml")
                        .name("QunyingTestCatalog")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
                        .build())
            .reference(CatalogReference.builder()
		                  .type("application/vnd.vmware.vcloud.catalog+xml")
                        .name("Public")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/9e08c2f6-077a-42ce-bece-d5332e2ebb5c"))
                        .build())
            .reference(CatalogReference.builder()
		                  .type("application/vnd.vmware.vcloud.catalog+xml")
                        .name("dantest")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/b542aff4-9f97-4f51-a126-4330fbf62f02"))
                        .build())
            .reference(CatalogReference.builder()
		                  .type("application/vnd.vmware.vcloud.catalog+xml")
                        .name("test")
                        .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/b7289d54-4ca4-497f-9a93-2d4afc97e3da"))
                        .build())
            .build();
      
      assertEquals(api.getQueryApi().catalogReferencesQueryAll(), expected);
   }
}
		
