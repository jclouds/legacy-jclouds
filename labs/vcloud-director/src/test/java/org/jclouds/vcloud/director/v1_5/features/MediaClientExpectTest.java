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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorRestClientExpectTest;
import org.testng.annotations.Test;

/**
 * Allows us to test a client via its side effects.
 * 
 * @author danikov
 */
@Test(groups = "unit", singleThreaded = true, testName = "NetworkClientExpectTest")
public class MediaClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {
   
   @Test
   public void testWhenResponseIs2xxLoginReturnsValidMedia() {
      URI mediaUri = URI.create(endpoint + "/media/794eb334-754e-4917-b5a0-5df85cbd61d1");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", mediaUri), 
            getStandardPayloadResponse("/media/media.xml", VCloudDirectorMediaType.MEDIA_XML));
      
      Media expected = media();

      assertEquals(client.getMediaClient().getMedia(mediaUri), expected);
   }
   
   public void testWhenResponseIs400ForInvalidNetworkId() {
      URI mediaUri = URI.create(endpoint + "/media/NOTAUUID");
 
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
            getStandardRequest("GET", mediaUri),
            getStandardPayloadResponse(400, "/media/error400.xml", VCloudDirectorMediaType.ERROR));
 
      Error expected = Error.builder()
            .message("validation error on field 'id': String value has invalid format or length")
            .majorErrorCode(400)
            .minorErrorCode("BAD_REQUEST")
            .build();
 
      try {
         client.getMediaClient().getMedia(mediaUri);
         fail("Should give HTTP 400 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }
 
   @Test
   public void testWhenResponseIs403ForCatalogIdUsedAsMediaId() {
      URI mediaUri = URI.create(endpoint + "/media/e9cd3387-ac57-4d27-a481-9bee75e0690f");
 
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
            getStandardRequest("GET", mediaUri),
            getStandardPayloadResponse(403, "/media/error403-catalog.xml", VCloudDirectorMediaType.ERROR));
 
      Error expected = Error.builder()
            .message("This operation is denied.")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();
 
      try {
         client.getMediaClient().getMedia(mediaUri);
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }
 
   @Test
   public void testWhenResponseIs403ForFakeNetworkId() {
      URI mediaUri = URI.create(endpoint + "/media/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
 
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
            getStandardRequest("GET", mediaUri),
            getStandardPayloadResponse(403, "/media/error403-fake.xml", VCloudDirectorMediaType.ERROR));
 
      Error expected = Error.builder()
            .message("No access to entity \"(com.vmware.vcloud.entity.media:aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee)\"")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();
 
      try {
         client.getMediaClient().getMedia(mediaUri);
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }
   
   @Test
   public void testWhenResponseIs2xxLoginReturnsValidMetadata() {
      URI mediaRef = URI.create("https://vcloudbeta.bluelock.com/api/media/KEY");
      URI metaRef = URI.create(mediaRef.toASCIIString()+"/metadata/");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", metaRef),
            getStandardPayloadResponse("/media/metadata.xml", VCloudDirectorMediaType.METADATA));
      
      Metadata expected = Metadata.builder()
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/media/KEY/metadata"))
            .link(Link.builder()
                  .rel("up")
                  .type("application/vnd.vmware.vcloud.network+xml")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/media/KEY"))
                  .build())
            .build();

      assertEquals(client.getMediaClient().getMetadata(mediaRef), expected);
   }
   
   @Test(enabled=false) // No metadata in exemplar xml...
   public void testWhenResponseIs2xxLoginReturnsValidMetadataEntry() {
      URI metadataRef = URI.create(
            "https://vcloudbeta.bluelock.com/api/media/KEY/metadata/KEY");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            getStandardRequest("GET", metadataRef),
            getStandardPayloadResponse("/media/metadataEntry.xml", VCloudDirectorMediaType.METADATA_ENTRY));
      
      MetadataEntry expected = MetadataEntry.builder()
            .build();

      assertEquals(client.getMediaClient().getMetadataEntry(metadataRef), expected);
   }
   
   private static Media media() {
      return Media.builder()
         .size(175163392)
         .imageType("iso")
         .status(1)
         .name("DansTestMedia")
         .id("urn:vcloud:media:794eb334-754e-4917-b5a0-5df85cbd61d1")
         .href(URI.create("https://mycloud.greenhousedata.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1"))
         .link(Link.builder()
            .rel("up")
            .type("application/vnd.vmware.vcloud.vdc+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f"))
            .build())
         .link(Link.builder()
            .rel("catalogItem")
            .type("application/vnd.vmware.vcloud.catalogItem+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/catalogItem/1979d680-304e-4118-9283-9210c3b3ed8d"))
            .build())
         .link(Link.builder()
            .rel("remove")
            .href(URI.create("https://mycloud.greenhousedata.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1"))
            .build())
         .link(Link.builder()
            .rel("edit")
            .type("application/vnd.vmware.vcloud.media+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.owner+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1/owner"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata"))
            .build())
         .description("Windows 2003 R2 Disk2 Standard 32bit &amp; 64bit")
         .owner(Owner.builder()
            .type("application/vnd.vmware.vcloud.owner+xml")
            .user(Reference.builder()
               .type("application/vnd.vmware.admin.user+xml")
               .name("acole")
               .href(URI.create("https://mycloud.greenhousedata.com/api/admin/user/c090335b-708c-4c1c-9e3d-89560d002120"))
               .build())
            .build())
         .build();
   }
}
