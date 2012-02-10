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

import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.OrgList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Allows us to test a client via its side effects.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "OrgClientExpectTest")
public class OrgClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {

   @Test
   public void testWhenResponseIs2xxLoginReturnsValidOrgList() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", "/org"),
            getStandardPayloadResponse("/org/orglist.xml", VCloudDirectorMediaType.ORG_LIST));

      OrgList expected = OrgList.builder()
            .org(Reference.builder()
               .type("application/vnd.vmware.vcloud.org+xml")
               .name("JClouds")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
               .build())
            .build();

      assertEquals(client.getOrgClient().getOrgList(), expected);
   }

   @Test
   public void testWhenResponseIs2xxLoginReturnsValidOrgFromListByReference() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", "/org"),
            getStandardPayloadResponse("/org/orglist.xml", VCloudDirectorMediaType.ORG_LIST));
      Reference org = Iterables.getOnlyElement(client.getOrgClient().getOrgList().getOrgs());
      
      client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", org.getHref()),
            getStandardPayloadResponse("/org/org.xml", VCloudDirectorMediaType.ORG));

      Org expected = Org
         .builder()
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

      assertEquals(client.getOrgClient().getOrg(org), expected);
   }

   @Test
   public void testWhenResponseIs2xxLoginReturnsValidOrgFromListById() {
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", "/org"),
            getStandardPayloadResponse("/org/orglist.xml", VCloudDirectorMediaType.ORG_LIST));
      Reference org = Iterables.getOnlyElement(client.getOrgClient().getOrgList().getOrgs());
      client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", "/org/" + org.getUuid()),
            getStandardPayloadResponse("/org/org.xml", VCloudDirectorMediaType.ORG));

      Org expected = Org
         .builder()
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

      assertEquals(org.getUuid(), "6f312e42-cd2b-488d-a2bb-97519cd57ed0");
      assertEquals(client.getOrgClient().getOrg(org.getUuid()), expected);
   }

   @Test
   public void testWhenResponseIs2xxLoginReturnsValidOrg() {
      URI orgRef = URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", "/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"),
            getStandardPayloadResponse("/org/org.xml", VCloudDirectorMediaType.ORG));
      
      Org expected = Org
         .builder()
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

      String orgId = getUuidFromReference.apply(Reference.builder().href(orgRef).build());
      assertEquals(client.getOrgClient().getOrg(orgId), expected);
   }

   @Test
   public void testWhenResponseIs400ForInvalidOrgId() {
      URI orgRef = URI.create("https://vcloudbeta.bluelock.com/api/org/NOTAUUID");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
            getStandardRequest("GET", "/org/NOTAUUID"),
            getStandardPayloadResponse(400, "/org/error400.xml", VCloudDirectorMediaType.ERROR));

      Error expected = Error.builder()
            .message("validation error on field 'id': String value has invalid format or length")
            .majorErrorCode(400)
            .minorErrorCode("BAD_REQUEST")
            .build();

      String orgId = getUuidFromReference.apply(Reference.builder().href(orgRef).build());
      try {
         client.getOrgClient().getOrg(orgId);
         fail("Should give HTTP 400 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }

   @Test
   public void testWhenResponseIs403ForCatalogIdUsedAsOrgId() {
      URI orgRef = URI.create("https://vcloudbeta.bluelock.com/api/org/9e08c2f6-077a-42ce-bece-d5332e2ebb5c");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
            getStandardRequest("GET", "/org/9e08c2f6-077a-42ce-bece-d5332e2ebb5c"),
            getStandardPayloadResponse(403, "/org/error403-catalog.xml", VCloudDirectorMediaType.ERROR));

      Error expected = Error.builder()
            .message("No access to entity \"com.vmware.vcloud.entity.org:9e08c2f6-077a-42ce-bece-d5332e2ebb5c\".")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();

      String orgId = getUuidFromReference.apply(Reference.builder().href(orgRef).build());
      try {
         client.getOrgClient().getOrg(orgId);
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }

   @Test
   public void testWhenResponseIs403ForFakeOrgId() {
      URI orgRef = URI.create("https://vcloudbeta.bluelock.com/api/org/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
            getStandardRequest("GET", "/org/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
            getStandardPayloadResponse(403, "/org/error403-fake.xml", VCloudDirectorMediaType.ERROR));

      Error expected = Error.builder()
            .message("No access to entity \"com.vmware.vcloud.entity.org:aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee\".")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();

      String orgId = getUuidFromReference.apply(Reference.builder().href(orgRef).build());
      try {
         client.getOrgClient().getOrg(orgId);
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }
   
   @Test
   public void testWhenResponseIs2xxLoginReturnsValidMetadataList() {
      URI orgRef = URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", "/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/metadata"),
            getStandardPayloadResponse("/org/metadata.xml", VCloudDirectorMediaType.METADATA));
      
      Metadata expected = Metadata.builder()
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/metadata"))
            .link(Link.builder()
                  .rel("up")
                  .type("application/vnd.vmware.vcloud.org+xml")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
                  .build())
            .build();

      String orgId = getUuidFromReference.apply(Reference.builder().href(orgRef).build());
      assertEquals(client.getOrgClient().getMetadata(orgId), expected);
   }
   
   @Test(enabled=false) // No metadata in exemplar xml...
   public void testWhenResponseIs2xxLoginReturnsValidMetadata() {
      URI orgRef = URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", "/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/metadata/KEY"),
            getStandardPayloadResponse("/org/metadata.xml", VCloudDirectorMediaType.METADATA_ENTRY));
      
      MetadataEntry expected = MetadataEntry.builder()
            .key("KEY")
            .build();

      String orgId = getUuidFromReference.apply(Reference.builder().href(orgRef).build());
      assertEquals(client.getOrgClient().getMetadataEntry(orgId, "KEY"), expected);
   }
}
