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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_LIST_SIZE_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_LIST_SIZE_GE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_REQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_UPDATABLE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.relEquals;
import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.typeEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.Set;

import org.jclouds.io.Payloads;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.File;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneMediaParams;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code MediaApi}
 * 
 * @author danikov
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "MediaApiLiveTest")
public class MediaApiLiveTest extends BaseVCloudDirectorApiLiveTest {

   public static final String MEDIA = "media";
   public static final String VDC = "vdc";

   /*
    * Convenience references to API apis.
    */
   protected VdcApi vdcApi;
   protected MediaApi mediaApi;

   /*
    * Shared state between dependent tests.
    */
   private Media media, oldMedia;
   private Owner owner;
   private Metadata metadata;
   private String metadataValue;
   private String metadataEntryValue = "value";

   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredApis() {
      vdcApi = context.getApi().getVdcApi();
      mediaApi = context.getApi().getMediaApi();
   }

   @AfterClass(alwaysRun = true)
   protected void tidyUp() {
      if (media != null) {
         try {
            Task remove = mediaApi.remove(media.getId());
            taskDoneEventually(remove);
         } catch (Exception e) {
            logger.warn(e, "Error when deleting media '%s': %s", media.getName());
         }
      }
      if (oldMedia != null) {
         try {
            Task remove = mediaApi.remove(oldMedia.getId());
            taskDoneEventually(remove);
         } catch (Exception e) {
            logger.warn(e, "Error when deleting media '%s': %s", oldMedia.getName());
         }
      }
   }

   @Test(description = "POST /vdc/{id}/media")
   public void testAddMedia() throws URISyntaxException {
      Vdc vdc = lazyGetVdc();
      Link addMedia = find(vdc.getLinks(), and(relEquals("add"), typeEquals(VCloudDirectorMediaType.MEDIA)));

      // TODO: generate an iso
      byte[] iso = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

      Media sourceMedia = Media.builder().type(VCloudDirectorMediaType.MEDIA).name("Test media " + random.nextInt())
               .size(iso.length).imageType(Media.ImageType.ISO).description("Test media generated by testAddMedia()")
               .build();
      media = mediaApi.add(addMedia.getHref(), sourceMedia);

      Checks.checkMediaFor(MEDIA, media);

      assertNotNull(media.getFiles(), String.format(OBJ_FIELD_REQ, MEDIA, "files"));
      assertEquals(1, media.getFiles().size(),
               String.format(OBJ_FIELD_LIST_SIZE_EQ, MEDIA, "files", 1, media.getFiles().size()));
      File uploadFile = getFirst(media.getFiles(), null);
      assertNotNull(uploadFile, String.format(OBJ_FIELD_REQ, MEDIA, "files.first"));
      assertEquals(uploadFile.getSize(), Long.valueOf(iso.length));
      assertEquals(uploadFile.getSize().longValue(), sourceMedia.getSize(),
               String.format(OBJ_FIELD_EQ, MEDIA, "uploadFile.size()", sourceMedia.getSize(), uploadFile.getSize()));

      Set<Link> links = uploadFile.getLinks();
      assertNotNull(links, String.format(OBJ_FIELD_REQ, MEDIA, "uploadFile.links"));
      assertTrue(links.size() >= 1, String.format(OBJ_FIELD_LIST_SIZE_GE, MEDIA, "uploadfile.links", 1, links.size()));
      assertTrue(
               Iterables.all(
                        links,
                        Predicates.or(LinkPredicates.relEquals(Link.Rel.UPLOAD_DEFAULT),
                                 LinkPredicates.relEquals(Link.Rel.UPLOAD_ALTERNATE))),
               String.format(OBJ_FIELD_REQ, MEDIA, "uploadFile.links.first"));

      Link uploadLink = Iterables.find(links, LinkPredicates.relEquals(Link.Rel.UPLOAD_DEFAULT));
      context.getApi().getUploadApi().upload(uploadLink.getHref(), Payloads.newByteArrayPayload(iso));

      media = mediaApi.get(media.getId());
      if (media.getTasks().size() == 1) {
         Task uploadTask = Iterables.getOnlyElement(media.getTasks());
         Checks.checkTask(uploadTask);
         assertEquals(uploadTask.getStatus(), Task.Status.RUNNING);
         assertTrue(retryTaskSuccess.apply(uploadTask), String.format(TASK_COMPLETE_TIMELY, "uploadTask"));
         media = mediaApi.get(media.getId());
      }
   }

   @Test(description = "GET /media/{id}", dependsOnMethods = { "testAddMedia" })
   public void testGetMedia() {
      media = mediaApi.get(media.getId());
      assertNotNull(media, String.format(OBJ_REQ_LIVE, MEDIA));

      owner = media.getOwner();
      assertNotNull(owner, String.format(OBJ_FIELD_REQ_LIVE, MEDIA, "owner"));
      Checks.checkResource(media.getOwner());

      Checks.checkMediaFor(MEDIA, media);
   }

   @Test(description = "GET /media/{id}/owner", dependsOnMethods = { "testGetMedia" })
   public void testGetMediaOwner() {
      Owner directOwner = mediaApi.getOwner(media.getId());
      assertEquals(owner.toBuilder().user(owner.getUser()).build(),
               directOwner.toBuilder().links(ImmutableSet.<Link> of()).build(), String.format(
                        GETTER_RETURNS_SAME_OBJ, "getOwner()", "owner", "media.getOwner()", owner.toString(),
                        directOwner.toString()));

      // parent type
      Checks.checkResource(directOwner);

      // required
      assertNotNull(directOwner.getUser(), String.format(OBJ_FIELD_REQ, "Owner", "user"));
      Checks.checkReferenceType(directOwner.getUser());
   }

   @Test(description = "POST /vdc/{id}/action/cloneMedia", dependsOnMethods = { "testGetMediaOwner" })
   public void testCloneMedia() {
      oldMedia = media;
      media = vdcApi.cloneMedia(
               vdcUrn,
               CloneMediaParams.builder().source(Reference.builder().fromEntity(media).build())
                        .name("copied " + media.getName()).description("copied by testCloneMedia()").build());

      Checks.checkMediaFor(VDC, media);

      if (media.getTasks() != null) {
         Task copyTask = getFirst(media.getTasks(), null);
         if (copyTask != null) {
            Checks.checkTask(copyTask);
            assertTrue(retryTaskSuccess.apply(copyTask), String.format(TASK_COMPLETE_TIMELY, "copyTask"));
            media = mediaApi.get(media.getId());
         }
      }

      Checks.checkMediaFor(MEDIA, media);
      assertTrue(media.clone(oldMedia),
               String.format(OBJ_FIELD_CLONE, MEDIA, "copied media", media.toString(), oldMedia.toString()));

      context.getApi().getMetadataApi(media.getId()).put("key", "value");

      media = vdcApi
               .cloneMedia(vdcUrn, CloneMediaParams.builder().source(Reference.builder().fromEntity(media).build())
                        .name("moved test media").description("moved by testCloneMedia()").isSourceDelete(true).build());

      Checks.checkMediaFor(VDC, media);

      if (media.getTasks() != null) {
         Task copyTask = getFirst(media.getTasks(), null);
         if (copyTask != null) {
            Checks.checkTask(copyTask);
            assertTrue(retryTaskSuccess.apply(copyTask), String.format(TASK_COMPLETE_TIMELY, "copyTask"));
            media = mediaApi.get(media.getId());
         }
      }

      Checks.checkMediaFor(MEDIA, media);
      assertTrue(media.clone(oldMedia),
               String.format(OBJ_FIELD_CLONE, MEDIA, "moved media", media.toString(), oldMedia.toString()));
   }

   @Test(description = "PUT /media/{id}", dependsOnMethods = { "testCloneMedia" })
   public void testSetMedia() {
      String oldName = media.getName();
      String newName = "new " + oldName;
      String oldDescription = media.getDescription();
      String newDescription = "new " + oldDescription;
      media = media.toBuilder().name(newName).description(newDescription).build();

      Task editMedia = mediaApi.edit(media.getId(), media);
      Checks.checkTask(editMedia);
      assertTrue(retryTaskSuccess.apply(editMedia), String.format(TASK_COMPLETE_TIMELY, "editMedia"));
      media = mediaApi.get(media.getId());

      assertTrue(equal(media.getName(), newName), String.format(OBJ_FIELD_UPDATABLE, MEDIA, "name"));
      assertTrue(equal(media.getDescription(), newDescription),
               String.format(OBJ_FIELD_UPDATABLE, MEDIA, "description"));

      // TODO negative tests?

      Checks.checkMediaFor(MEDIA, media);

      media = media.toBuilder().name(oldName).description(oldDescription).build();

      editMedia = mediaApi.edit(media.getId(), media);
      Checks.checkTask(editMedia);
      assertTrue(retryTaskSuccess.apply(editMedia), String.format(TASK_COMPLETE_TIMELY, "editMedia"));
      media = mediaApi.get(media.getId());
   }

   @Test(description = "GET /media/{id}/metadata", dependsOnMethods = { "testSetMetadataValue" })
   public void testGetMetadata() {
      metadata = context.getApi().getMetadataApi(media.getId()).get();
      // required for testing
      assertFalse(isEmpty(metadata.getMetadataEntries()), String.format(OBJ_FIELD_REQ_LIVE, MEDIA, "metadata.entries"));

      Checks.checkMetadataFor(MEDIA, metadata);
   }

   @Test(description = "POST /media/{id}/metadata", dependsOnMethods = { "testGetMedia" })
   public void testMergeMetadata() {
      // test new
      Task mergeMetadata = context.getApi().getMetadataApi(media.getId()).putAll(ImmutableMap.of("testKey", "testValue"));
      Checks.checkTask(mergeMetadata);
      assertTrue(retryTaskSuccess.apply(mergeMetadata), String.format(TASK_COMPLETE_TIMELY, "mergeMetadata(new)"));
      metadata = context.getApi().getMetadataApi(media.getId()).get();
      Checks.checkMetadataFor(MEDIA, metadata);
      assertEquals(metadata.get("testKey"), "testValue");

      media = mediaApi.get(media.getId());
      Checks.checkMediaFor(MEDIA, media);

      // test edit
      mergeMetadata = context.getApi().getMetadataApi(media.getId()).put("testKey", "new testValue");
      Checks.checkTask(mergeMetadata);
      assertTrue(retryTaskSuccess.apply(mergeMetadata), String.format(TASK_COMPLETE_TIMELY, "mergeMetadata(edit)"));
      metadata = context.getApi().getMetadataApi(media.getId()).get();
      Checks.checkMetadataFor(MEDIA, metadata);
      assertEquals(metadata.get("testKey"), "new testValue");

      media = mediaApi.get(media.getId());
      Checks.checkMediaFor(MEDIA, media);
   }

   @Test(description = "GET /media/{id}/metadata/{key}", dependsOnMethods = { "testSetMetadataValue" })
   public void testGetMetadataValue() {
      metadataValue = context.getApi().getMetadataApi(media.getId()).get("key");
      assertNotNull(metadataValue);
   }

   @Test(description = "PUT /media/{id}/metadata/{key}", dependsOnMethods = { "testMergeMetadata" })
   public void testSetMetadataValue() {
      metadataEntryValue = "value";
      
      Task setMetadataEntry = context.getApi().getMetadataApi(media.getId()).put("key", metadataEntryValue);
      Checks.checkTask(setMetadataEntry);
      assertTrue(retryTaskSuccess.apply(setMetadataEntry), String.format(TASK_COMPLETE_TIMELY, "setMetadataEntry"));
      metadataValue = context.getApi().getMetadataApi(media.getId()).get("key");
      assertNotNull(metadataValue);
   }

   @Test(description = "DELETE /media/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadata",
            "testGetMetadataValue" })
   public void testRemoveMetadata() {
      Task remove = context.getApi().getMetadataApi(media.getId()).remove("testKey");
      Checks.checkTask(remove);
      assertTrue(retryTaskSuccess.apply(remove), String.format(TASK_COMPLETE_TIMELY, "remove"));

      metadataValue = context.getApi().getMetadataApi(media.getId()).get("testKey");
      assertNull(metadataValue, String.format(OBJ_FIELD_ATTRB_DEL, MEDIA, "Metadata",
               metadataValue != null ? metadataValue.toString() : "", "MetadataEntry",
               metadataValue != null ? metadataValue.toString() : ""));

      metadataValue = context.getApi().getMetadataApi(media.getId()).get("key");
      assertNotNull(metadataValue);

      media = mediaApi.get(media.getId());
      Checks.checkMediaFor(MEDIA, media);
   }

   @Test(description = "DELETE /media/{id}", dependsOnMethods = { "testRemoveMetadata" })
   public void testRemoveMedia() {
      Task removeMedia = mediaApi.remove(media.getId());
      Checks.checkTask(removeMedia);
      assertTrue(retryTaskSuccess.apply(removeMedia), String.format(TASK_COMPLETE_TIMELY, "removeMedia"));

      media = mediaApi.get(media.getId());
      assertNull(media, String.format(OBJ_DEL, MEDIA, media != null ? media.toString() : ""));

      removeMedia = mediaApi.remove(oldMedia.getId());
      Checks.checkTask(removeMedia);
      assertTrue(retryTaskSuccess.apply(removeMedia), String.format(TASK_COMPLETE_TIMELY, "removeMedia"));
      oldMedia = null;
   }
}
