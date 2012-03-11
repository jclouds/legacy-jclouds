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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.isEmpty;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.GETTER_RETURNS_SAME_OBJ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_DEL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_ATTRB_DEL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_CLONE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_CONTAINS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_REQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_UPDATABLE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REF_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.relEquals;
import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.typeEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URISyntaxException;
import java.util.Set;

import org.jclouds.io.Payloads;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
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
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code MediaClient}
 * 
 * @author danikov
 */
@Test(groups = { "live", "user", "media" }, singleThreaded = true, testName = "MediaClientLiveTest")
public class MediaClientLiveTest extends BaseVCloudDirectorClientLiveTest {

   public static final String MEDIA = "media";
   public static final String VDC = "vdc";

   /*
    * Convenience references to API clients.
    */
   protected VdcClient vdcClient;
   protected MediaClient mediaClient;

   @Override
   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      vdcClient = context.getApi().getVdcClient();
      mediaClient = context.getApi().getMediaClient();
   }

   /*
    * Shared state between dependent tests.
    */
   private Media media, oldMedia;
   private Owner owner;
   private Metadata metadata;
   private MetadataValue metadataValue;
   private String metadataEntryValue = "value";
   
   @Test(testName = "POST /vdc/{id}/media")
   public void testCreateMedia() throws URISyntaxException {
      assertNotNull(vdcURI, String.format(REF_REQ_LIVE, VDC));
      Vdc vdc = vdcClient.getVdc(vdcURI); 
      assertNotNull(vdc, String.format(OBJ_REQ_LIVE, VDC));
      Link addMedia = find(vdc.getLinks(), and(relEquals("add"), typeEquals(VCloudDirectorMediaType.MEDIA)));
      
      // TODO: generate an iso
      byte[] iso = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
      
      Media sourceMedia = Media.builder()
            .type(VCloudDirectorMediaType.MEDIA)
            .name("Test media 1")
            .size(iso.length)
            .imageType(Media.ImageType.ISO)
            .description("Test media generated by testCreateMedia()")
            .build();
      media = mediaClient.createMedia(addMedia.getHref(), sourceMedia);
      
      Checks.checkMediaFor(MEDIA, media);
      
      assertNotNull(media.getFiles(), String.format(OBJ_FIELD_REQ, MEDIA, "files"));
      assertTrue(media.getFiles().getFiles().size() == 1, String.format(OBJ_FIELD_EQ, MEDIA, "files.size()", "1", 
            media.getFiles().getFiles().size()));
      File uploadFile = getFirst(media.getFiles().getFiles(), null);
      assertNotNull(uploadFile, String.format(OBJ_FIELD_REQ, MEDIA, "files.first"));
      assertTrue(equal(uploadFile.getSize(), sourceMedia.getSize()), String.format(OBJ_FIELD_EQ, MEDIA, "uploadFile.size()",
            sourceMedia.getSize(), uploadFile.getSize()));
      
      Set<Link> links = uploadFile.getLinks();
      assertNotNull(links, String.format(OBJ_FIELD_REQ, MEDIA, "uploadFile.links"));
      assertTrue(links.size() == 1, String.format(OBJ_FIELD_EQ, MEDIA, "uploadfile.links.size()", "1", 
            links.size()));
      Link uploadLink = getFirst(links, null);
      assertTrue(equal(uploadLink.getRel(), Link.Rel.UPLOAD_DEFAULT), String.format(OBJ_FIELD_REQ, MEDIA, "uploadFile.links.first"));

      context.getApi().getUploadClient().upload(uploadLink.getHref(), Payloads.newByteArrayPayload(iso));
      
      media = mediaClient.getMedia(media.getHref());
      
      Task task = Iterables.getOnlyElement(media.getTasksInProgress().getTasks());

      assertEquals(task.getStatus(), "running");
      
      File file = Iterables.getOnlyElement(media.getFiles().getFiles());
      assertEquals(file.getSize(), new Long(iso.length));
      assertEquals(file.getBytesTransferred(), new Long(iso.length));

   }
   
   @Test(testName = "GET /media/{id}", dependsOnMethods = { "testCreateMedia" })
   public void testGetMedia() {
      media = mediaClient.getMedia(media.getHref());
      assertNotNull(media, String.format(OBJ_REQ_LIVE, MEDIA));
      
      owner = media.getOwner();
      assertNotNull(owner, String.format(OBJ_FIELD_REQ_LIVE, MEDIA, "owner"));
      Checks.checkResourceType(media.getOwner());
      
      Checks.checkMediaFor(MEDIA, media);
   }
   
   @Test(testName = "GET /media/{id}/owner",
         dependsOnMethods = { "testGetMedia" })
   public void testGetMediaOwner() {
      Owner directOwner = mediaClient.getOwner(media.getHref());
      assertEquals(owner, directOwner, String.format(GETTER_RETURNS_SAME_OBJ,
            "getOwner()", "owner", "media.getOwner()", owner.toString(), directOwner.toString()));
      
      // parent type
      Checks.checkResourceType(directOwner);
      
      // required
      assertNotNull(directOwner.getUser(), String.format(OBJ_FIELD_REQ, "Owner", "user"));
      Checks.checkReferenceType(directOwner.getUser());
   }
   
   @Test(testName = "POST /vdc/{id}/action/cloneMedia",
         dependsOnMethods = { "testGetMediaOwner" })
   public void testCloneMedia() {
      oldMedia = media;
      media = vdcClient.cloneMedia(vdcURI, CloneMediaParams.builder()
            .source(Reference.builder().fromEntity(media).build())
            .name("copied test media")
            .description("copied by testCloneMedia()")
            .build());
      
      Checks.checkMediaFor(VDC, media);
      
      if (media.getTasksInProgress() != null) {
         Task copyTask = getFirst(media.getTasksInProgress().getTasks(), null);
         if (copyTask != null) {
            Checks.checkTask(copyTask);
            assertTrue(retryTaskSuccess.apply(copyTask), String.format(TASK_COMPLETE_TIMELY, "copyTask"));
            media = mediaClient.getMedia(media.getHref());
         }
      }
      
      Checks.checkMediaFor(MEDIA, media);
      assertTrue(media.clone(oldMedia), String.format(OBJ_FIELD_CLONE, MEDIA, "copied media", 
            media.toString(), oldMedia.toString()));
      
      mediaClient.getMetadataClient().setMetadata(media.getHref(), "key", MetadataValue.builder().value("value").build());
      
      media = vdcClient.cloneMedia(vdcURI, CloneMediaParams.builder()
            .source(Reference.builder().fromEntity(media).build())
            .name("moved test media")
            .description("moved by testCloneMedia()")
            .isSourceDelete(true)
            .build());
      
      Checks.checkMediaFor(VDC, media);
      
      if (media.getTasksInProgress() != null) {
         Task copyTask = getFirst(media.getTasksInProgress().getTasks(), null);
         if (copyTask != null) {
            Checks.checkTask(copyTask);
            assertTrue(retryTaskSuccess.apply(copyTask), String.format(TASK_COMPLETE_TIMELY, "copyTask"));
            media = mediaClient.getMedia(media.getHref());
         }
      }
      
      Checks.checkMediaFor(MEDIA, media);
      assertTrue(media.clone(oldMedia), String.format(OBJ_FIELD_CLONE, MEDIA, "moved media", 
            media.toString(), oldMedia.toString()));
   }
   
   @Test(testName = "PUT /media/{id}",
         dependsOnMethods = { "testCloneMedia" })
   public void testSetMedia() {
      String oldName = media.getName();
      String newName = "new "+oldName;
      String oldDescription = media.getDescription();
      String newDescription = "new "+oldDescription;
      media = media.toBuilder().name(newName).description(newDescription).build();
      
      Task updateMedia = mediaClient.updateMedia(media.getHref(), media);
      Checks.checkTask(updateMedia);
      assertTrue(retryTaskSuccess.apply(updateMedia), String.format(TASK_COMPLETE_TIMELY, "updateMedia"));
      media = mediaClient.getMedia(media.getHref());
      
      assertTrue(equal(media.getName(), newName), String.format(OBJ_FIELD_UPDATABLE, MEDIA, "name"));
      assertTrue(equal(media.getDescription(), newDescription),
            String.format(OBJ_FIELD_UPDATABLE, MEDIA, "description"));
      
      //TODO negative tests?
      
      Checks.checkMediaFor(MEDIA, media);
      
      media = media.toBuilder().name(oldName).description(oldDescription).build();
      
      updateMedia = mediaClient.updateMedia(media.getHref(), media);
      Checks.checkTask(updateMedia);
      assertTrue(retryTaskSuccess.apply(updateMedia), String.format(TASK_COMPLETE_TIMELY, "updateMedia"));
      media = mediaClient.getMedia(media.getHref());
   }
   
   @Test(testName = "GET /media/{id}/metadata",
         dependsOnMethods = { "testGetMedia" })
   public void testGetMetadata() {
      metadata = mediaClient.getMetadataClient().getMetadata(media.getHref());
      // required for testing
      assertFalse(isEmpty(metadata.getMetadataEntries()),
            String.format(OBJ_FIELD_REQ_LIVE, MEDIA, "metadata.entries"));
      
      Checks.checkMetadataFor(MEDIA, metadata);
   }
   
   @Test(testName = "POST /media/{id}/metadata",
         dependsOnMethods = { "testGetMetadata" })
   public void testMergeMetadata() {
      // test new
      Set<MetadataEntry> inputEntries = ImmutableSet.of(MetadataEntry.builder().entry("testKey", "testValue").build());
      Metadata inputMetadata = Metadata.builder()
            .entries(inputEntries)
            .build();
      
      Task mergeMetadata = mediaClient.getMetadataClient().mergeMetadata(media.getHref(), inputMetadata);
      Checks.checkTask(mergeMetadata);
      assertTrue(retryTaskSuccess.apply(mergeMetadata), String.format(TASK_COMPLETE_TIMELY, "mergeMetadata(new)"));
      metadata = mediaClient.getMetadataClient().getMetadata(media.getHref());
      Checks.checkMetadataFor(MEDIA, metadata);
      checkMetadataContainsEntries(metadata, inputEntries);
      
      media = mediaClient.getMedia(media.getHref());
      Checks.checkMediaFor(MEDIA, media);
      
      // test modify
      inputEntries = ImmutableSet.of(MetadataEntry.builder().entry("testKey", "new testValue").build());
      inputMetadata = Metadata.builder()
            .entries(inputEntries)
            .build();
      
      mergeMetadata = mediaClient.getMetadataClient().mergeMetadata(media.getHref(), inputMetadata);
      Checks.checkTask(mergeMetadata);
      assertTrue(retryTaskSuccess.apply(mergeMetadata), String.format(TASK_COMPLETE_TIMELY, "mergeMetadata(modify)"));
      metadata = mediaClient.getMetadataClient().getMetadata(media.getHref());
      Checks.checkMetadataFor(MEDIA, metadata);
      checkMetadataContainsEntries(metadata, inputEntries);
      
      media = mediaClient.getMedia(media.getHref());
      Checks.checkMediaFor(MEDIA, media);
   }
   
   private void checkMetadataContainsEntries(Metadata metadata, Set<MetadataEntry> entries) {
      for (MetadataEntry inputEntry : entries) {
         boolean found = false;
         for (MetadataEntry entry : metadata.getMetadataEntries()) {
            if (equal(inputEntry.getKey(), entry.getKey())) {
               found = true; break;
            }
         }
         
         if (!found) {
            String.format(OBJ_FIELD_CONTAINS, MEDIA, "metadata",
                  Iterables.toString(metadata.getMetadataEntries()),
                  Iterables.toString(entries));
         }
      }
   }
   
   @Test(testName = "GET /media/{id}/metadata/{key}",
         dependsOnMethods = { "testMergeMetadata" })
   public void testGetMetadataValue() {
      metadataValue = mediaClient.getMetadataClient().getMetadataValue(media.getHref(), "key");
      Checks.checkMetadataValueFor(MEDIA, metadataValue);
   }
   
   @Test(testName = "PUT /media/{id}/metadata/{key}",
         dependsOnMethods = { "testGetMetadataValue" })
   public void testSetMetadataValue() {
      metadataEntryValue = "newValue";
      MetadataValue newValue = MetadataValue.builder().value(metadataEntryValue).build();
      
      Task setMetadataEntry = mediaClient.getMetadataClient().setMetadata(media.getHref(), "key", newValue);
      Checks.checkTask(setMetadataEntry);
      assertTrue(retryTaskSuccess.apply(setMetadataEntry),
            String.format(TASK_COMPLETE_TIMELY, "setMetadataEntry"));
      metadataValue = mediaClient.getMetadataClient().getMetadataValue(media.getHref(), "key");
      Checks.checkMetadataValueFor(MEDIA, metadataValue);
   }
   
   @Test(testName = "DELETE /media/{id}/metadata/{key}",
         dependsOnMethods = { "testSetMetadataValue" } )
   public void testDeleteMetadata() {
      Task deleteMetadataEntry = mediaClient.getMetadataClient().deleteMetadataEntry(media.getHref(), "testKey");
      Checks.checkTask(deleteMetadataEntry);
      assertTrue(retryTaskSuccess.apply(deleteMetadataEntry),
            String.format(TASK_COMPLETE_TIMELY, "deleteMetadataEntry"));
      
      Error expected = Error.builder()
            .message("The access to the resource metadata_item with id testKey is forbidden")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();
      
      try {
         metadataValue = mediaClient.getMetadataClient().getMetadataValue(media.getHref(), "testKey");
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
         metadataValue = null;
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
      
      if (metadataValue != null) { // guard against NPE on the .toStrings
         assertNull(metadataValue, String.format(OBJ_FIELD_ATTRB_DEL, MEDIA,
               "Metadata", metadataValue.toString(),
               "metadataEntry", metadataValue.toString()));
      }
      
      metadataValue = mediaClient.getMetadataClient().getMetadataValue(media.getHref(), "key");
      Checks.checkMetadataValueFor(MEDIA, metadataValue);
      
      media = mediaClient.getMedia(media.getHref());
      Checks.checkMediaFor(MEDIA, media);
   }
   
   @Test(testName = "DELETE /media/{id}",
         dependsOnMethods = { "testDeleteMetadata" } )
   public void testDeleteMedia() {
      Task deleteMedia = mediaClient.deleteMedia(media.getHref());
      Checks.checkTask(deleteMedia);
      assertTrue(retryTaskSuccess.apply(deleteMedia),
            String.format(TASK_COMPLETE_TIMELY, "deleteMedia"));
      
      Error expected = Error.builder()
            .message(String.format(
                  "No access to entity \"(com.vmware.vcloud.entity.media:%s)\".",
                  media.getId()))
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();
      
      try {
         media = mediaClient.getMedia(media.getHref());
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
         media = null;
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
      
      if (media != null) { // guard against NPE on the .toStrings
         assertNull(metadataValue, String.format(OBJ_DEL, MEDIA, media.toString()));
      }
      
      deleteMedia = mediaClient.deleteMedia(oldMedia.getHref());
      Checks.checkTask(deleteMedia);
   }
}
