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
import org.jclouds.vcloud.director.v1_5.domain.CloneMediaParams;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.File;
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

import com.google.common.collect.ImmutableSet;

/**
 * Allows us to test a client via its side effects.
 * 
 * @author danikov
 */
@Test(groups = { "unit", "media" }, singleThreaded = true, testName = "MediaClientExpectTest")
public class MediaClientExpectTest extends BaseVCloudDirectorRestClientExpectTest {
   
   @Test
   public void testCreateMedia() {
      URI uploadLink = URI.create(endpoint + "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/media");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/media")
               .acceptMedia(VCloudDirectorMediaType.MEDIA)
               .xmlFilePayload("/media/createMediaSource.xml", VCloudDirectorMediaType.MEDIA)
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/createMedia.xml", VCloudDirectorMediaType.MEDIA)
               .httpResponseBuilder().statusCode(201).build());
      
      Media source = Media.builder()
         .size(0)
         .imageType("iso")
         .name("Test media 1")
         .type("application/vnd.vmware.vcloud.media+xml")
         .description("Test media generated by testCreateMedia()")
         .build();
      Media expected = createMedia();
      
      assertEquals(client.getMediaClient().createMedia(uploadLink, source), expected);
   }
   
   @Test
   public void testCloneMedia() {
      URI cloneUri = URI.create(endpoint + "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/cloneMedia");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/cloneMedia")
               .acceptMedia(VCloudDirectorMediaType.MEDIA)
               .xmlFilePayload("/media/cloneMediaParams.xml", VCloudDirectorMediaType.CLONE_MEDIA_PARAMS)
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/cloneMedia.xml", VCloudDirectorMediaType.MEDIA)
               .httpResponseBuilder().statusCode(201).build());
      
      CloneMediaParams params = CloneMediaParams.builder()
         .name("moved test media")
         .description("moved by testCloneMedia()")
         .source(Reference.builder()
               .type("application/vnd.vmware.vcloud.media+xml")
               .name("copied test media")
               .id("urn:vcloud:media:da8361af-cccd-4103-a71c-493513c49094")
               .href(URI.create("https://mycloud.greenhousedata.com/api/media/da8361af-cccd-4103-a71c-493513c49094"))
               .build())
         .isSourceDelete(false)
         .build();
      Media expected = cloneMedia();
      
      assertEquals(client.getMediaClient().cloneMedia(cloneUri, params), expected);
   }
   
   @Test
   public void testGetMedia() {
      URI mediaUri = URI.create(endpoint + "/media/794eb334-754e-4917-b5a0-5df85cbd61d1");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/media.xml", VCloudDirectorMediaType.MEDIA)
               .httpResponseBuilder().build());
      
      Media expected = getMedia();
      assertEquals(client.getMediaClient().getMedia(mediaUri), expected);
   }
   
   @Test
   public void testResponse400ForInvalidMedia() {
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
   public void testResponse403ForCatalogIdUsedAsMediaId() {
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
   public void testResponse403ForFakeMediaId() {
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
   public void testUpdateMedia() {
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
      
      assertEquals(client.getMediaClient().updateMedia(mediaUri, update), expected);
   }
   
   @Test
   public void testDeleteMedia() {
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

      assertEquals(client.getMediaClient().deleteMedia(mediaUri), expected);
   }
   
   @Test
   public void testGetMetadata() {
      URI mediaUri = URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/metadata.xml", VCloudDirectorMediaType.METADATA)
               .httpResponseBuilder().build());
      
      
      Metadata expected = metadata();

      assertEquals(client.getMediaClient().getMetadataClient().getMetadata(mediaUri), expected);
   }
   
   @Test
   public void testMergeMetadata() {
      URI mediaUri = URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata")
               .xmlFilePayload("/media/mergeMetadata.xml", VCloudDirectorMediaType.METADATA)
               .acceptMedia(VCloudDirectorMediaType.TASK)
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/mergeMetadataTask.xml", VCloudDirectorMediaType.TASK)
               .httpResponseBuilder().build());
      
      Metadata inputMetadata = metadata();
      Task expectedTask = mergeMetadataTask();

      assertEquals(client.getMediaClient().getMetadataClient().mergeMetadata(mediaUri, inputMetadata), expectedTask);
   }
   
   public void testGetMetadataValue() {
      URI mediaUri = URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata/key")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/metadataValue.xml", VCloudDirectorMediaType.METADATA_VALUE)
               .httpResponseBuilder().build());
      
      MetadataValue expected = metadataValue();
      

      assertEquals(client.getMediaClient().getMetadataClient().getMetadataValue(mediaUri, "key"), expected);
   }
   
   @Test
   public void testSetMetadataValue() {
      URI mediaUri = URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("PUT", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata/key")
               .xmlFilePayload("/media/setMetadataValue.xml", VCloudDirectorMediaType.METADATA_VALUE)
               .acceptMedia(VCloudDirectorMediaType.TASK)
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/setMetadataValueTask.xml", VCloudDirectorMediaType.TASK)
               .httpResponseBuilder().build());
      
      MetadataValue inputMetadataValue = MetadataValue.builder().value("value").build();
      
      Task expectedTask = setMetadataEntryTask();

      assertEquals(client.getMediaClient().getMetadataClient().setMetadata(mediaUri, "key", inputMetadataValue), expectedTask);
   }
   
   @Test
   public void testDeleteMetadataValue() {
      URI mediaUri = URI.create("https://vcloudbeta.bluelock.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1");
      
      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("DELETE", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1/metadata/key")
               .acceptMedia(VCloudDirectorMediaType.TASK)
               .httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/deleteMetadataEntryTask.xml", VCloudDirectorMediaType.TASK)
               .httpResponseBuilder().build());
      
      Task expectedTask = deleteMetadataEntryTask();

      assertEquals(client.getMediaClient().getMetadataClient().deleteMetadataEntry(mediaUri, "key"), expectedTask);
   }
   
   @Test
   public void testGetOwner() {
      URI mediaUri = URI.create(endpoint + "/media/794eb334-754e-4917-b5a0-5df85cbd61d1");

      VCloudDirectorClient client = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/media/794eb334-754e-4917-b5a0-5df85cbd61d1/owner")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/owner.xml", VCloudDirectorMediaType.OWNER)
               .httpResponseBuilder().build());
      
      Owner expected = owner().toBuilder()
               .link(Link.builder()
                        .rel("up")
                        .type("application/vnd.vmware.vcloud.media+xml")
                        .href(URI.create("https://mycloud.greenhousedata.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1"))
                        .build())
               .build();
      
      assertEquals(client.getMediaClient().getOwner(mediaUri), expected);
   }
   
   static Media createMedia() {
      return Media.builder()
         .size(0)
         .imageType("iso")
         .status(0)
         .name("Test media 1")
         .id("urn:vcloud:media:d51b0b9d-099c-499f-97f8-4fbe40ba06d7")
         .type("application/vnd.vmware.vcloud.media+xml")
         .href(URI.create("https://mycloud.greenhousedata.com/api/media/d51b0b9d-099c-499f-97f8-4fbe40ba06d7"))
         .link(Link.builder()
            .rel("up")
            .type("application/vnd.vmware.vcloud.vdc+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f"))
            .build())
         .link(Link.builder()
            .rel("remove")
            .href(URI.create("https://mycloud.greenhousedata.com/api/media/d51b0b9d-099c-499f-97f8-4fbe40ba06d7"))
            .build())
         .description("Test media generated by testCreateMedia()")
         .files(ImmutableSet.of(File.builder()
               .size(0l)
               .bytesTransferred(0l)
               .name("file")
               .link(Link.builder()
                  .rel("upload:default")
                  .href(URI.create("https://mycloud.greenhousedata.com:443/transfer/b1fdf2d0-feff-4414-a8d1-3a8d86c4ccc5/file"))
                  .build())
               .build()))
         .owner(owner())
         .build();
   }
   
   static Media cloneMedia() {
      return Media.builder()
         .size(175163392)
         .imageType("iso")
         .status(0)
         .name("copied test media-copy-671136ae-b8f0-4389-bca6-50e9c42268f2")
         .id("urn:vcloud:media:a6b023f2-7f90-4e89-a24d-56e0eba83a5a")
         .type("application/vnd.vmware.vcloud.media+xml")
         .href(URI.create("https://mycloud.greenhousedata.com/api/media/a6b023f2-7f90-4e89-a24d-56e0eba83a5a"))
         .link(Link.builder()
            .rel("up")
            .type("application/vnd.vmware.vcloud.vdc+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f"))
            .build())
         .link(Link.builder()
            .rel("remove")
            .href(URI.create("https://mycloud.greenhousedata.com/api/media/a6b023f2-7f90-4e89-a24d-56e0eba83a5a"))
            .build())
         .description("copied by testCloneMedia()")
         .tasks(ImmutableSet.<Task>builder()
            .add(Task.builder()
                .status("running")
                .startTime(dateService.iso8601DateParse("2012-03-02T04:58:48.754-07:00"))
                .operationName("vdcCopyMedia")
                .operation("Copying Media File copied test media-copy-671136ae-b8f0-4389-bca6-50e9c42268f2(a6b023f2-7f90-4e89-a24d-56e0eba83a5a)")
                .expiryTime(dateService.iso8601DateParse("2012-05-31T04:58:48.754-06:00"))
                .name("task")
                .id("urn:vcloud:task:7e4f6baf-7ef0-43ea-93cc-62cc329afb5d")
                .type("application/vnd.vmware.vcloud.task+xml")
                .href(URI.create("https://mycloud.greenhousedata.com/api/task/7e4f6baf-7ef0-43ea-93cc-62cc329afb5d"))
                .link(Link.builder()
                   .rel("task:cancel")
                   .href(URI.create("https://mycloud.greenhousedata.com/api/task/7e4f6baf-7ef0-43ea-93cc-62cc329afb5d/action/cancel"))
                   .build())
                .owner(Reference.builder()
                   .type("application/vnd.vmware.vcloud.media+xml")
                   .name("copied test media-copy-671136ae-b8f0-4389-bca6-50e9c42268f2")
                   .href(URI.create("https://mycloud.greenhousedata.com/api/media/a6b023f2-7f90-4e89-a24d-56e0eba83a5a"))
                   .build())
                .user(Reference.builder()
                   .type("application/vnd.vmware.admin.user+xml")
                   .name("acole")
                   .href(URI.create("https://mycloud.greenhousedata.com/api/admin/user/c090335b-708c-4c1c-9e3d-89560d002120"))
                   .build())
                .org(Reference.builder()
                   .type("application/vnd.vmware.vcloud.org+xml")
                   .name("cloudsoft")
                   .href(URI.create("https://mycloud.greenhousedata.com/api/org/c076f90a-397a-49fa-89b8-b294c1599cd0"))
                   .build())
                .build())
             .build())
         .owner(owner())
         .build();
   }
   
   private static Media getMedia() {
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
         .type("application/vnd.vmware.vcloud.media+xml")
         .description("Windows 2003 R2 Disk2 Standard 32bit & 64bit")
         .owner(owner())
         .build();
   }
   
   private static Media updateMedia() {
      return Media.builder()
         .size(175163392)
         .imageType("iso")
         .status(1)
         .name("new testMedia1")
         .id("urn:vcloud:media:c93e5cdc-f29a-4749-8ed2-093df04cc75e")
         .type("application/vnd.vmware.vcloud.media+xml")
         .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e"))
         .link(Link.builder()
            .rel("up")
            .type("application/vnd.vmware.vcloud.vdc+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f"))
            .build())
         .link(Link.builder()
            .rel("catalogItem")
            .type("application/vnd.vmware.vcloud.catalogItem+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/catalogItem/1b317eb9-0e25-429a-ada2-3c7a74a0367b"))
            .build())
         .link(Link.builder()
            .rel("remove")
            .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e"))
            .build())
         .link(Link.builder()
            .rel("edit")
            .type("application/vnd.vmware.vcloud.media+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.owner+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e/owner"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e/metadata"))
            .build())
         .description("new test description")
         .owner(Owner.builder()
            .type("application/vnd.vmware.vcloud.owner+xml")
            .user(Reference.builder()
               .type("application/vnd.vmware.admin.user+xml")
               .name("dan")
               .href(URI.create("https://mycloud.greenhousedata.com/api/admin/user/7818d31c-df33-4d77-9bbc-0a0741cf3d44"))
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
         .operationName("vdcDeleteMedia")
         .operation("Deleting Media File (794eb334-754e-4917-b5a0-5df85cbd61d1)")
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
         .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e/metadata"))
         .link(Link.builder()
            .rel("up")
            .type("application/vnd.vmware.vcloud.media+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e/metadata"))
            .build())
         .entry(MetadataEntry.builder()
             .type("application/vnd.vmware.vcloud.metadata.value+xml")
             .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e/metadata/key"))
             .link(Link.builder()
               .rel("up")
               .type("application/vnd.vmware.vcloud.metadata+xml")
               .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e/metadata"))
               .build())
            .link(Link.builder()
               .rel("edit")
               .type("application/vnd.vmware.vcloud.metadata.value+xml")
               .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e/metadata/key"))
               .build())
             .link(Link.builder()
               .rel("remove")
               .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e/metadata/key"))
               .build())
             .key("key").value("value").build())
         .build();
   }
   
   private static MetadataValue metadataValue() {
      return MetadataValue.builder()
            .type("application/vnd.vmware.vcloud.metadata.value+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e/metadata/key"))
            .link(Link.builder()
               .rel("up")
               .type("application/vnd.vmware.vcloud.metadata+xml")
               .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e/metadata"))
               .build())
            .link(Link.builder()
               .rel("edit")
               .type("application/vnd.vmware.vcloud.metadata.value+xml")
               .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e/metadata/key"))
               .build())
            .link(Link.builder()
               .rel("remove")
               .href(URI.create("https://mycloud.greenhousedata.com/api/media/c93e5cdc-f29a-4749-8ed2-093df04cc75e/metadata/key"))
               .build())
            .value("value").build();
   }
   
   private Task mergeMetadataTask() {
      return Task.builder()
         .status("running")
         .startTime(dateService.iso8601DateParse("2012-02-13T06:35:08.011-05:00"))
         .operationName("metadataUpdate")
         .operation("Updating metadata for Media File (794eb334-754e-4917-b5a0-5df85cbd61d1)")
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
         .operationName("metadataUpdate")
         .operation("Updating metadata for Media File (794eb334-754e-4917-b5a0-5df85cbd61d1)")
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
         .operation("Deleting metadata for Media File (794eb334-754e-4917-b5a0-5df85cbd61d1)")
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
