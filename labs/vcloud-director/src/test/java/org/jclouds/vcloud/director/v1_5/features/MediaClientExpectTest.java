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
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
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
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/media.xml", VCloudDirectorMediaType.MEDIA)
               .httpResponseBuilder().build());
      
      Media expected = media();
      
      Reference mediaRef = Reference.builder().href(mediaUri).build();

      assertEquals(client.getMediaClient().getMedia(mediaRef), expected);
   }
   
   public void testWhenResponseIs400ForInvalidMediaId() {
      URI mediaUri = URI.create(endpoint + "/media/NOTAUUID");
 
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/media/NOTAUUID")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/error400.xml", VCloudDirectorMediaType.ERROR)
               .httpResponseBuilder().statusCode(400).build());
 
      Error expected = Error.builder()
            .message("validation error on field 'id': String value has invalid format or length")
            .majorErrorCode(400)
            .minorErrorCode("BAD_REQUEST")
            .build();
      
      Reference mediaRef = Reference.builder().href(mediaUri).build();
 
      try {
         client.getMediaClient().getMedia(mediaRef);
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
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/media/e9cd3387-ac57-4d27-a481-9bee75e0690f")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/error403-catalog.xml", VCloudDirectorMediaType.ERROR)
               .httpResponseBuilder().statusCode(403).build());
 
      Error expected = Error.builder()
            .message("No access to entity \"(com.vmware.vcloud.entity.media:e9cd3387-ac57-4d27-a481-9bee75e0690f)\".")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();
      
      Reference mediaRef = Reference.builder().href(mediaUri).build();
 
      try {
         client.getMediaClient().getMedia(mediaRef);
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }
 
   @Test
   public void testWhenResponseIs403ForFakeMediaId() {
      URI mediaUri = URI.create(endpoint + "/media/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
 
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/media/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/error403-fake.xml", VCloudDirectorMediaType.ERROR)
               .httpResponseBuilder().statusCode(403).build());
 
      Error expected = Error.builder()
            .message("No access to entity \"(com.vmware.vcloud.entity.media:aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee)\".")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();
      
      Reference mediaRef = Reference.builder().href(mediaUri).build();
 
      try {
         client.getMediaClient().getMedia(mediaRef);
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }
   
   @Test
   public void testWhenResponseIs2xxLoginUpdateReturnsValidMedia() {
      URI mediaUri = URI.create(endpoint + "/media/794eb334-754e-4917-b5a0-5df85cbd61d1");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer()
               .apiCommand("PUT", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1")
               .xmlFilePayload("/media/updateMedia.xml", VCloudDirectorMediaType.MEDIA)
               .acceptMedia(VCloudDirectorMediaType.TASK)
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/updateMediaTask.xml", VCloudDirectorMediaType.TASK)
               .httpResponseBuilder().build());
      
      Media update = updateMedia();
      Task expected = updateMediaTask();
      Reference mediaRef = Reference.builder().href(mediaUri).build();

      assertEquals(client.getMediaClient().updateMedia(mediaRef, update), expected);
   }
   
   @Test
   public void testWhenResponseIs2xxLoginDeleteMediaReturnsValidTask() {
      URI mediaUri = URI.create(endpoint + "/media/794eb334-754e-4917-b5a0-5df85cbd61d1");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("DELETE", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1")
               .acceptMedia(VCloudDirectorMediaType.TASK)
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/deleteMediaTask.xml", VCloudDirectorMediaType.TASK)
               .httpResponseBuilder().build());
      
      Task expected = deleteMediaTask();
      Reference mediaRef = Reference.builder().href(mediaUri).build();

      assertEquals(client.getMediaClient().deleteMedia(mediaRef), expected);
   }
   
   @Test
   public void testWhenResponseIs2xxLoginReturnsValidMetadata() {
      URI mediaUri = URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/metadata.xml", VCloudDirectorMediaType.METADATA)
               .httpResponseBuilder().build());
      
      Reference mediaRef = Reference.builder().href(mediaUri).build();
      
      Metadata expected = metadata();

      assertEquals(client.getMediaClient().getMetadata(mediaRef), expected);
   }
   
   @Test
   public void testWhenResponseIs2xxLoginMergeMetadataReturnsValidTask() {
      URI mediaUri = URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata")
               .xmlFilePayload("/media/metadata.xml", VCloudDirectorMediaType.METADATA)
               .acceptMedia(VCloudDirectorMediaType.TASK)
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/mergeMetadataTask.xml", VCloudDirectorMediaType.TASK)
               .httpResponseBuilder().build());
      
      Reference mediaRef = Reference.builder().href(mediaUri).build();
      Metadata inputMetadata = metadata();
      Task expectedTask = mergeMetadataTask();

      assertEquals(client.getMediaClient().mergeMetadata(mediaRef, inputMetadata), expectedTask);
   }
   
   public void testWhenResponseIs2xxLoginReturnsValidMetadataEntry() {
      URI mediaUri = URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata/key")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/metadataEntry.xml", VCloudDirectorMediaType.METADATA_ENTRY)
               .httpResponseBuilder().build());
      
      MetadataValue expected = metadataValue();
      
      Reference mediaRef = Reference.builder().href(mediaUri).build();

      assertEquals(client.getMediaClient().getMetadataValue(mediaRef, "key"), expected);
   }
   
   @Test
   public void testWhenResponseIs2xxLoginSetMetadataReturnsValidTask() {
      URI mediaUri = URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("PUT", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata/key")
               .xmlFilePayload("/media/setMetadata.xml", VCloudDirectorMediaType.METADATA_VALUE)
               .acceptMedia(VCloudDirectorMediaType.TASK)
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/setMetadataTask.xml", VCloudDirectorMediaType.TASK)
               .httpResponseBuilder().build());
      
      Reference mediaRef = Reference.builder().href(mediaUri).build();
      MetadataValue inputMetadataValue = MetadataValue.builder().value("value").build();
      
      Task expectedTask = setMetadataEntryTask();

      assertEquals(client.getMediaClient().setMetadata(mediaRef, "key", inputMetadataValue), expectedTask);
   }
   
   @Test
   public void testWhenResponseIs2xxLoginDeleteMetadataEntryReturnsValidTask() {
      URI mediaUri = URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("DELETE", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata/key")
               .acceptMedia(VCloudDirectorMediaType.TASK)
               .httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/deleteMetadataEntryTask.xml", VCloudDirectorMediaType.TASK)
               .httpResponseBuilder().build());
      
      Reference mediaRef = Reference.builder().href(mediaUri).build();
      Task expectedTask = deleteMetadataEntryTask();

      assertEquals(client.getMediaClient().deleteMetadataEntry(mediaRef, "key"), expectedTask);
   }
   
   @Test
   public void testWhenResponseIs2xxLoginReturnsValidOwner() {
      URI mediaUri = URI.create(endpoint + "/media/794eb334-754e-4917-b5a0-5df85cbd61d1");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1/owner")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/owner.xml", VCloudDirectorMediaType.OWNER)
               .httpResponseBuilder().build());
      
      Owner expected = owner();
      
      Reference mediaRef = Reference.builder().href(mediaUri).build();

      assertEquals(client.getMediaClient().getOwner(mediaRef), expected);
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
         .owner(owner())
         .build();
   }
   
   private static Media updateMedia() {
      return Media.builder()
         .size(175163392)
         .imageType("iso")
         .owner(Owner.builder()
            .user(Reference.builder()
               .type("application/vnd.vmware.admin.user+xml")
               .name("acole")
               .href(URI.create("https://mycloud.greenhousedata.com/api/admin/user/c090335b-708c-4c1c-9e3d-89560d002120"))
               .build())
            .build())
         .build();
   }
   
   private static Task updateMediaTask() {
      return Task.builder()
            .name("task")
            .id("urn:vcloud:task:c6dca927-eab4-41fa-ad6a-3ac58602541c")
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c"))
            .status("running")
            .startTime(dateService.iso8601DateParse("2012-02-13T06:35:08.011-05:00"))
            .expiryTime(dateService.iso8601DateParse("2012-05-13T06:35:08.011-04:00"))
            .operationName("mediaUpdate")
            .operation("Updating Media (794eb334-754e-4917-b5a0-5df85cbd61d1)")
            .link(Link.builder()
                  .rel("task:cancel")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c/action/cancel"))
                  .build())
            .owner(Reference.builder()
                  .type("application/vnd.vmware.vcloud.media+xml")
                  .name("")
                  .href(URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1"))
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
   
   public static Task deleteMediaTask() {
      return Task.builder()
         .name("task")
         .id("urn:vcloud:task:c6dca927-eab4-41fa-ad6a-3ac58602541c")
         .type("application/vnd.vmware.vcloud.task+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c"))
         .status("running")
         .startTime(dateService.iso8601DateParse("2012-02-13T06:35:08.011-05:00"))
         .expiryTime(dateService.iso8601DateParse("2012-05-13T06:35:08.011-04:00"))
         .operationName("mediaDelete")
         .operation("Deleting Media (794eb334-754e-4917-b5a0-5df85cbd61d1)")
         .link(Link.builder()
               .rel("task:cancel")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c/action/cancel"))
               .build())
         .owner(Reference.builder()
               .type("application/vnd.vmware.vcloud.media+xml")
               .name("")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1"))
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
   
   private static Owner owner() {
      return Owner.builder()
      .type("application/vnd.vmware.vcloud.owner+xml")
      .user(Reference.builder()
         .type("application/vnd.vmware.admin.user+xml")
         .name("acole")
         .href(URI.create("https://mycloud.greenhousedata.com/api/admin/user/c090335b-708c-4c1c-9e3d-89560d002120"))
         .build())
      .build();
   }
   
   private static Metadata metadata() {
      return Metadata.builder()
         .type("application/vnd.vmware.vcloud.metadata+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata"))
         .link(Link.builder()
            .rel("up")
            .type("application/vnd.vmware.vcloud.media+xml")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1"))
            .build())
         .entry(MetadataEntry.builder().key("key").value("value").build())
         .build();
   }
   
   private static MetadataValue metadataValue() {
      return MetadataValue.builder()
            .href(URI.create("https://vcloudbeta.bluelock.com/api/cmedia/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata/key"))
            .link(Link.builder()
               .rel("up")
               .type("application/vnd.vmware.vcloud.metadata+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata"))
               .build())
            .value("value").build();
   }
   
   private Task mergeMetadataTask() {
      return Task.builder()
         .status("running")
         .startTime(dateService.iso8601DateParse("2012-02-13T06:35:08.011-05:00"))
         .operationName("metadataMerge")
         .operation("Merging metadata for Media (794eb334-754e-4917-b5a0-5df85cbd61d1)")
         .expiryTime(dateService.iso8601DateParse("2012-05-13T06:35:08.011-04:00"))
         .name("task")
         .id("urn:vcloud:task:c6dca927-eab4-41fa-ad6a-3ac58602541c")
         .type("application/vnd.vmware.vcloud.task+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c"))
         .link(Link.builder()
            .rel("task:cancel")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c/action/cancel"))
            .build())
         .owner(Reference.builder()
            .type("application/vnd.vmware.vcloud.media+xml")
            .name("")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1"))
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
   
   private Task setMetadataEntryTask() {
      return Task.builder()
         .status("running")
         .startTime(dateService.iso8601DateParse("2012-02-13T06:35:08.011-05:00"))
         .operationName("metadataSet")
         .operation("Setting metadata for Media (794eb334-754e-4917-b5a0-5df85cbd61d1)")
         .expiryTime(dateService.iso8601DateParse("2012-05-13T06:35:08.011-04:00"))
         .name("task")
         .id("urn:vcloud:task:c6dca927-eab4-41fa-ad6a-3ac58602541c")
         .type("application/vnd.vmware.vcloud.task+xml")
         .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c"))
         .link(Link.builder()
            .rel("task:cancel")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c/action/cancel"))
            .build())
         .owner(Reference.builder()
            .type("application/vnd.vmware.vcloud.media+xml")
            .name("")
            .href(URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1"))
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
         .operation("Deleting metadata for Media (794eb334-754e-4917-b5a0-5df85cbd61d1)")
         .link(Link.builder()
               .rel("task:cancel")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/task/c6dca927-eab4-41fa-ad6a-3ac58602541c/action/cancel"))
               .build())
         .owner(Reference.builder()
               .type("application/vnd.vmware.vcloud.media+xml")
               .name("")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1"))
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
