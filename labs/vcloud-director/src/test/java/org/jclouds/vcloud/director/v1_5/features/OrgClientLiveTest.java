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

import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.OrgList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
* Tests live behavior of {@link OrgClient}.
* 
* @author grkvlt@apache.org
*/
@Test(groups = { "live", "apitests" }, testName = "OrgClientLiveTest")
public class OrgClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   @Test(testName = "GET /org/{id}")
   public void testGetOrg() {
      OrgList orgList = context.getApi().getOrgClient().getOrgList();
      assertFalse(Iterables.isEmpty(orgList.getOrgs()), "There must always be Org elements in the OrgList");
      Reference orgRef = Iterables.getFirst(orgList.getOrgs(), null);

      // Call the method being tested
      Org org = context.getApi().getOrgClient().getOrg(orgRef);

      // Check required elements and attributes of the Org
      assertNotNull(org.getFullName(), "The FullName field of the Org must not be null");

      // Check required elements and attributes of EntityType
      checkEntityType(org);
      
      assertNotNull(org.getName(), "The Name field of the Org must not be null");
      
      // Check optional elements and attributes of the Org
      org.getDescription();
      org.g
      
   }

//   @Test
//   public void testWhenResponseIs2xxLoginReturnsValidOrgList() {
//      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
//            getStandardRequest("GET", "/org"),
//            getStandardPayloadResponse("/org/orglist.xml", VCloudDirectorMediaType.ORG_LIST));
//
//      OrgList expected = OrgList.builder()
//            .org(Reference.builder()
//               .type("application/vnd.vmware.vcloud.org+xml")
//               .name("JClouds")
//               .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
//               .build())
//            .build();
//
//      assertEquals(client.getOrgClient().getOrgList(), expected);
//   }
//
//   @Test
//   public void testWhenResponseIs2xxLoginReturnsValidOrgFromListByReference() {
//      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
//            getStandardRequest("GET", "/org"),
//            getStandardPayloadResponse("/org/orglist.xml", VCloudDirectorMediaType.ORG_LIST));
//
//      Reference org = Iterables.getOnlyElement(client.getOrgClient().getOrgList().getOrgs());
//      
//      client = requestsSendResponses(loginRequest, sessionResponse, 
//            getStandardRequest("GET", org.getHref()),
//            getStandardPayloadResponse("/org/org.xml", VCloudDirectorMediaType.ORG));
//
//      Org expected = org();
//
//      assertEquals(client.getOrgClient().getOrg(org), expected);
//   }
//
//   @Test
//   public void testWhenResponseIs2xxLoginReturnsValidOrg() {
//      URI orgUri = URI.create(endpoint + "/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0");
//
//      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
//            getStandardRequest("GET", "/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"),
//            getStandardPayloadResponse("/org/org.xml", VCloudDirectorMediaType.ORG));
//      
//      Org expected = org();
//
//      Reference orgRef = Reference.builder().href(orgUri).build();
//
//      assertEquals(client.getOrgClient().getOrg(orgRef), expected);
//   }
//
//   @Test
//   public void testWhenResponseIs400ForInvalidOrgId() {
//      URI orgUri = URI.create(endpoint + "/org/NOTAUUID");
//
//      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
//            getStandardRequest("GET", "/org/NOTAUUID"),
//            getStandardPayloadResponse(400, "/org/error400.xml", VCloudDirectorMediaType.ERROR));
//
//      Error expected = Error.builder()
//            .message("validation error on field 'id': String value has invalid format or length")
//            .majorErrorCode(400)
//            .minorErrorCode("BAD_REQUEST")
//            .build();
//
//      Reference orgRef = Reference.builder().href(orgUri).build();
//      try {
//         client.getOrgClient().getOrg(orgRef);
//         fail("Should give HTTP 400 error");
//      } catch (VCloudDirectorException vde) {
//         assertEquals(vde.getError(), expected);
//      } catch (Exception e) {
//         fail("Should have thrown a VCloudDirectorException");
//      }
//   }
//
//   @Test
//   public void testWhenResponseIs403ForCatalogIdUsedAsOrgId() {
//      URI orgUri = URI.create(endpoint + "/org/9e08c2f6-077a-42ce-bece-d5332e2ebb5c");
//
//      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
//            getStandardRequest("GET", "/org/9e08c2f6-077a-42ce-bece-d5332e2ebb5c"),
//            getStandardPayloadResponse(403, "/org/error403-catalog.xml", VCloudDirectorMediaType.ERROR));
//
//      Error expected = Error.builder()
//            .message("No access to entity \"com.vmware.vcloud.entity.org:9e08c2f6-077a-42ce-bece-d5332e2ebb5c\".")
//            .majorErrorCode(403)
//            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
//            .build();
//
//      Reference orgRef = Reference.builder().href(orgUri).build();
//
//      try {
//         client.getOrgClient().getOrg(orgRef);
//         fail("Should give HTTP 403 error");
//      } catch (VCloudDirectorException vde) {
//         assertEquals(vde.getError(), expected);
//      } catch (Exception e) {
//         fail("Should have thrown a VCloudDirectorException");
//      }
//   }
//
//   @Test
//   public void testWhenResponseIs403ForFakeOrgId() {
//      URI orgUri = URI.create(endpoint + "/org/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
//
//      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
//            getStandardRequest("GET", "/org/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
//            getStandardPayloadResponse(403, "/org/error403-fake.xml", VCloudDirectorMediaType.ERROR));
//
//      Error expected = Error.builder()
//            .message("No access to entity \"com.vmware.vcloud.entity.org:aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee\".")
//            .majorErrorCode(403)
//            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
//            .build();
//
//      Reference orgRef = Reference.builder().href(orgUri).build();
//
//      try {
//         client.getOrgClient().getOrg(orgRef);
//         fail("Should give HTTP 403 error");
//      } catch (VCloudDirectorException vde) {
//         assertEquals(vde.getError(), expected);
//      } catch (Exception e) {
//         fail("Should have thrown a VCloudDirectorException");
//      }
//   }
//   
//   @Test
//   public void testWhenResponseIs2xxLoginReturnsValidMetadataList() {
//      URI orgUri = URI.create(endpoint + "/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0");
//      
//      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
//            getStandardRequest("GET", "/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/metadata"),
//            getStandardPayloadResponse("/org/metadata.xml", VCloudDirectorMediaType.METADATA));
//      
//      Metadata expected = Metadata.builder()
//            .type("application/vnd.vmware.vcloud.metadata+xml")
//            .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/metadata"))
//            .link(Link.builder()
//                  .rel("up")
//                  .type("application/vnd.vmware.vcloud.org+xml")
//                  .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
//                  .build())
//            .build();
//
//       Reference orgRef = Reference.builder().href(orgUri).build();
// 
//       assertEquals(client.getOrgClient().getMetadata(orgRef), expected);
//   }
//   
//   @Test(enabled=false) // No metadata in exemplar xml...
//   public void testWhenResponseIs2xxLoginReturnsValidMetadata() {
//      URI orgUri = URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0");
//      
//      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
//            getStandardRequest("GET", "/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0/metadata/KEY"),
//            getStandardPayloadResponse("/org/metadata.xml", VCloudDirectorMediaType.METADATA_ENTRY));
//      
//      MetadataEntry expected = MetadataEntry.builder()
//            .key("KEY")
//            .build();
//
//      Reference orgRef = Reference.builder().href(orgUri).build();
//
//      assertEquals(client.getOrgClient().getMetadataEntry(orgRef, "KEY"), expected);
//   }
}
