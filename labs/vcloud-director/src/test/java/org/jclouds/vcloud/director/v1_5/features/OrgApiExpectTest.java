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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ADMIN_ORG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ENTITY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.METADATA;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.METADATA_VALUE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ORG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ORG_LIST;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.org.Org;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgList;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;

/**
 * Allows us to test the {@link OrgApi} via its side effects.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit", "user" }, singleThreaded = true, testName = "OrgApiExpectTest")
public class OrgApiExpectTest extends VCloudDirectorAdminApiExpectTest {

   @Test
   public void testGetOrgList() {
      HttpRequest list = HttpRequest.builder()
               .method("GET")
               .endpoint(endpoint + "/org/")
               .addHeader("Accept", "*/*")
               .addHeader("x-vcloud-authorization", token)
               .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
               .build();

      HttpResponse listResponse = HttpResponse.builder()
               .statusCode(200)
               .payload(payloadFromResourceWithContentType("/org/orglist.xml", ORG_LIST + ";version=1.5"))
               .build();
      
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, list, listResponse);


      OrgList expected = OrgList.builder()
            .org(Reference.builder()
               .type("application/vnd.vmware.vcloud.org+xml")
               .name("JClouds")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
               .build())
            .build();

      assertEquals(api.getOrgApi().list(), expected);
   }
   
   static String org = "6f312e42-cd2b-488d-a2bb-97519cd57ed0";
   static String orgUrn = "urn:vcloud:org:" + org;
   static URI orgHref = URI.create(endpoint + "/org/" + org);
   
   HttpRequest get = HttpRequest.builder()
            .method("GET")
            .endpoint(orgHref)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

    HttpResponse getResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/org/org.xml", ORG + ";version=1.5"))
            .build();
    
   @Test
   public void testGetOrgHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, get, getResponse);
      assertEquals(api.getOrgApi().get(orgHref), org());
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
                                                             .a("href", endpoint + "/entity/" + orgUrn)
                                  .e("Link").a("rel", "alternate").a("type", ORG).a("href", orgHref.toString()).up()
                                  // TODO: remove this when VCloudDirectorApiExpectTest no longer inherits from VCloudDirectorAdminApiExpectTest
                                  .e("Link").a("rel", "alternate").a("type", ADMIN_ORG).a("href", orgHref.toString()).up());
   
   HttpResponse resolveOrgResponse = HttpResponse.builder()
           .statusCode(200)
           .payload(payloadFromStringWithContentType(orgEntity, ENTITY + ";version=1.5"))
           .build();
   
   @Test
   public void testGetOrgUrn() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, resolveOrg, resolveOrgResponse, get, getResponse);
      assertEquals(api.getOrgApi().get(orgUrn), org());
   }
   

   HttpRequest getMetadata = HttpRequest.builder()
            .method("GET")
            .endpoint(orgHref + "/metadata")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();

   HttpResponse getMetadataResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/org/orgMetadata.xml", METADATA))
            .build();

   @Test
   public void testGetOrgMetadataHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, getMetadata, getMetadataResponse);
      assertEquals(api.getMetadataApi(orgHref).get(), metadata());
   }

   static Metadata metadata() {
      return Metadata.builder()
                     .type("application/vnd.vmware.vcloud.metadata+xml")
                     .href(URI.create(endpoint + "/org/" + org + "/metadata"))
                     .link(Link.builder()
                               .rel("up")
                               .type("application/vnd.vmware.vcloud.org+xml")
                               .href(orgHref)
                               .build())
                     .entries(ImmutableSet.of(metadataEntry()))
                     .build();
   }
   
   HttpRequest getMetadataValue = HttpRequest.builder()
            .method("GET")
            .endpoint(orgHref + "/metadata/KEY")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();

   HttpResponse getMetadataValueResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/org/orgMetadataValue.xml", METADATA_VALUE))
            .build();

   @Test
   public void testGetOrgMetadataEntryHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, getMetadataValue, getMetadataValueResponse);
      assertEquals(api.getMetadataApi(orgHref).get("KEY"), "VALUE");
   }
   
   public static Org org() {
      return Org.builder()
         .name("JClouds")
         .description("")
         .fullName("JClouds")
         .id("urn:vcloud:org:6f312e42-cd2b-488d-a2bb-97519cd57ed0")
         .type("application/vnd.vmware.vcloud.org+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.vdc+xml")
            .name("Cluster01-JClouds")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/vdc/d16d333b-e3c0-4176-845d-a5ee6392df07"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.tasksList+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/tasksList/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.catalog+xml")
            .name("Public")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.controlAccess+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/catalog/9e08c2f6-077a-42ce-bece-d5332e2ebb5c/controlAccess/"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.orgNetwork+xml")
            .name("ilsolation01-Jclouds")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/network/f3ba8256-6f48-4512-aad6-600e85b4dc38"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.orgNetwork+xml")
            .name("internet01-Jclouds")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/network/55a677cf-ab3f-48ae-b880-fab90421980c"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/metadata"))
            .build())
         .build();
   }
   
   public static MetadataEntry metadataEntry() {
      return MetadataEntry.builder()
            .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/metadata/KEY"))
            .link(Link.builder()
                  .rel("up")
                  .type("application/vnd.vmware.vcloud.metadata+xml")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/metadata"))
                  .build())
            .entry("KEY", "VALUE")
            .build();
   }
}
