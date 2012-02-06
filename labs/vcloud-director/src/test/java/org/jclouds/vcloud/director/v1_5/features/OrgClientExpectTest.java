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
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.OrgList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Allows us to test a client via its side effects.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "OrgClientExpectTest")
public class OrgClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {
   @Test
   public void testWhenResponseIs2xxLoginReturnsValidOrgList() {
      HttpRequest orgListRequest = HttpRequest.builder()
            .method("GET")
            .endpoint(URI.create("http://localhost/api/org/"))
            .headers(ImmutableMultimap.<String, String> builder()
                  .put("Accept", "*/*")
                  .put("x-vcloud-authorization",token)
                  .build())
            .build();

      HttpResponse orgListResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/org/orglist.xml", VCloudDirectorMediaType.ORGLIST_XML
                  +";version=1.5"))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, orgListRequest,
               orgListResponse);
      
      OrgList actual = OrgList.builder()
            .org(OrgLink.builder()
               .type("application/vnd.vmware.vcloud.org+xml")
               .name("JClouds")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
               .build())
            .build();

      assertEquals(client.getOrgClient().getOrgList(), actual);
   }

   @Test
   public void testWhenResponseIs2xxLoginReturnsValidOrg() {
      URI orgRef = URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0");

      HttpRequest orgRequest = HttpRequest.builder()
            .method("GET")
            .endpoint(orgRef)
            .headers(ImmutableMultimap.<String, String> builder()
                  .put("Accept", "*/*")
                  .put("x-vcloud-authorization", token)
                  .build())
            .build();

      HttpResponse orgResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/org/org.xml", VCloudDirectorMediaType.ORG_XML + ";version=1.5"))
            .build();

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, orgRequest, orgResponse);
      
      Org actual = Org
         .builder()
         .name("JClouds")
         .description("")
         .fullName("JClouds")
         .id("urn:vcloud:org:6f312e42-cd2b-488d-a2bb-97519cd57ed0")
         .type(VCloudDirectorMediaType.ORG_XML)
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
            .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/9e08c2f6-077a-42ce-bece-d5332e2ebb5c"))
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
            .href(URI.create("https://vcloudbeta.bluelock.com/api/network/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
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

      assertEquals(client.getOrgClient().getOrg(orgRef), actual);
   }
   
   @Test
   public void testWhenResponseIs2xxLoginReturnsValidMetadataList() {
      URI orgRef = URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", orgRef),
            getStandardPaylodResponse("/orglist.xml"));
      
      Metadata actual = Metadata.builder()
//            .link(Link.builder()
//                  .rel("up")
//                  .type("application/vnd.vmware.vcloud.vdc+xml")
//                  .name("Cluster01-JClouds")
//                  .href(URI.create("https://vcloudbeta.bluelock.com/api/vdc/d16d333b-e3c0-4176-845d-a5ee6392df07"))
//                  .build())
            .build();

      assertEquals(client.getOrgClient().getMetadata(orgRef), actual);
   }
   
   @Test(enabled=false)
   public void testWhenResponseIs2xxLoginReturnsValidMetadata() {
      URI metadataRef = URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/metadataKEY");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", metadataRef),
            getStandardPaylodResponse("/org/metadata.xml"));
      
      MetadataEntry actual = MetadataEntry.builder()
            .build();

      assertEquals(client.getOrgClient().getMetadataEntry(metadataRef), actual);
   }
}
