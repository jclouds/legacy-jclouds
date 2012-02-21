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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.GETTER_RETURNS_SAME_OBJ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_DEL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_ATTRB_DEL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_ATTRB_REQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_CONTAINS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_GTE_0;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_REQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_UPDATABLE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REF_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkResourceType;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.jclouds.vcloud.director.v1_5.predicates.TaskSuccess;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code MediaClient}
 * 
 * @author danikov
 */
@Test(groups = { "live", "api", "user" }, singleThreaded = true, testName = "MediaClientLiveTest")
public class MediaClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   public static final String MEDIA = "media";

   /*
    * Convenience references to API clients.
    */
   protected MediaClient mediaClient;

   /*
    * Shared state between dependent tests.
    */
   private Reference mediaRef;
   private Media media;
   private Owner owner;
   private Metadata metadata;
   private MetadataValue metadataValue;
   private String metadataEntryValue = "value";
   
   @BeforeGroups(groups = { "live" }, dependsOnMethods = { "setupClient" })
   public void before() {
      String mediaId = "68dc01a4-6c76-4177-9f19-ec12bf94287c"; // TODO: inject
      mediaRef = Reference.builder()
            .type("application/vnd.vmware.vcloud.media+xml")
            .name("")
            .href(URI.create(endpoint+"/media/"+mediaId)) 
            .id(mediaId)
            .build();
      mediaClient = context.getApi().getMediaClient();
      mediaClient.setMetadata(mediaRef, "key", MetadataValue.builder().value("value").build());
   }
   
   @Test(testName = "GET /media/{id}")
   public void testWhenResponseIs2xxLoginReturnsValidMedia() {
      // required for testing
      assertNotNull(mediaRef, String.format(REF_REQ_LIVE, MEDIA));
      
      media = mediaClient.getMedia(mediaRef);
      assertNotNull(media, String.format(OBJ_REQ_LIVE, MEDIA));
      assertTrue(!media.getDescription().equals("DO NOT USE"), "Media isn't to be used for testing");
      
      owner = media.getOwner();
      assertNotNull(owner, String.format(OBJ_FIELD_REQ_LIVE, MEDIA, "owner"));
      Checks.checkResourceType(media.getOwner());
      
      // parent type
      Checks.checkResourceEntityType(media);
      
      // required
      String imageType = media.getImageType();
      Checks.checkImageType(imageType);
      Long size = media.getSize();
      assertNotNull(size, String.format(OBJ_FIELD_REQ, MEDIA, "size"));
      assertTrue(size >= 0, String.format(OBJ_FIELD_GTE_0, MEDIA, "size", size));
   }
   
   @Test(testName = "GET /media/{id}/owner",
         dependsOnMethods = { "testWhenResponseIs2xxLoginReturnsValidMedia" })
   public void testWhenResponseIs2xxLoginReturnsValidMediaOwner() {
      Owner directOwner = mediaClient.getOwner(mediaRef);
      assertEquals(owner, directOwner, String.format(GETTER_RETURNS_SAME_OBJ,
            "getOwner()", "owner", "media.getOwner()", owner.toString(), directOwner.toString()));
      
      // parent type
      Checks.checkResourceType(directOwner);
      
      // required
      assertNotNull(directOwner.getUser(), String.format(OBJ_FIELD_REQ, "Owner", "user"));
      Checks.checkReferenceType(directOwner.getUser());
   }
   
   @Test(testName = "PUT /media/{id}",
         dependsOnMethods = { "testWhenResponseIs2xxLoginReturnsValidMedia" })
   public void testWhenResponseIs2xxLoginReturnsValidNetwork() {
      String oldName = media.getName();
      String newName = "new "+oldName;
      String oldDescription = media.getDescription();
      String newDescription = "new "+oldDescription;
      media.setName(newName);
      media.setDescription(newDescription);
      
      Task updateMedia = mediaClient.updateMedia(mediaRef, media);
      Checks.checkTask(updateMedia);
      assertTrue(retryTaskSuccess.apply(updateMedia.getHref()), String.format(TASK_COMPLETE_TIMELY, "updateMedia"));
      media = mediaClient.getMedia(mediaRef);
      
      assertTrue(equal(media.getName(), newName), String.format(OBJ_FIELD_UPDATABLE, MEDIA, "name"));
      assertTrue(equal(media.getDescription(), newDescription),
            String.format(OBJ_FIELD_UPDATABLE, MEDIA, "description"));
      
      //TODO negative tests?
      
      // ensure media remains valid
      testWhenResponseIs2xxLoginReturnsValidMedia();
      
      media.setName(newName);
      media.setDescription(newDescription);
      
      updateMedia = mediaClient.updateMedia(mediaRef, media);
      Checks.checkTask(updateMedia);
      assertTrue(retryTaskSuccess.apply(updateMedia.getHref()), String.format(TASK_COMPLETE_TIMELY, "updateMedia"));
      media = mediaClient.getMedia(mediaRef);
   }
   
   @Test(testName = "GET /media/{id}/metadata",
         dependsOnMethods = { "testWhenResponseIs2xxLoginReturnsValidMedia" })
   public void testWhenResponseIs2xxLoginReturnsValidMetadata() {
      metadata = mediaClient.getMetadata(mediaRef);
      // required for testing
      assertFalse(Iterables.isEmpty(metadata.getMetadataEntries()),
            String.format(OBJ_FIELD_REQ_LIVE, MEDIA, "metadata.entries"));
      
      // parent type
      checkResourceType(metadata);
      
      for (MetadataEntry entry : metadata.getMetadataEntries()) {
         // required elements and attributes
         assertNotNull(entry.getKey(),
               String.format(OBJ_FIELD_ATTRB_REQ, MEDIA, "MetadataEntry", metadataValue, "key"));
         assertNotNull(entry.getValue(),
               String.format(OBJ_FIELD_ATTRB_REQ, MEDIA, "MetadataEntry", metadataValue, "value"));
         
         // parent type
         checkResourceType(entry);
      }
   }
   
   @Test(testName = "POST /media/{id}/metadata",
         dependsOnMethods = { "testWhenResponseIs2xxLoginReturnsValidMetadata" })
   public void testWhenResponseIs2xxLoginMergedMetadata() {
      // test new
      Set<MetadataEntry> inputEntries = ImmutableSet.of(MetadataEntry.builder().entry("testKey", "testValue").build());
      Metadata inputMetadata = Metadata.builder()
            .entries(inputEntries)
            .build();
      
      Task mergeMetadata = mediaClient.mergeMetadata(mediaRef, inputMetadata);
      Checks.checkTask(mergeMetadata);
      assertTrue(retryTaskSuccess.apply(mergeMetadata.getHref()), String.format(TASK_COMPLETE_TIMELY, "mergeMetadata(new)"));
      metadata = mediaClient.getMetadata(mediaRef);
      checkMetadataContainsEntries(metadata, inputEntries);
      
      testWhenResponseIs2xxLoginReturnsValidMetadata();
      
      // test modify
      inputEntries = ImmutableSet.of(MetadataEntry.builder().entry("testKey", "new testValue").build());
      inputMetadata = Metadata.builder()
            .entries(inputEntries)
            .build();
      
      mergeMetadata = mediaClient.mergeMetadata(mediaRef, inputMetadata);
      Checks.checkTask(mergeMetadata);
      assertTrue(retryTaskSuccess.apply(mergeMetadata.getHref()), String.format(TASK_COMPLETE_TIMELY, "mergeMetadata(modify)"));
      metadata = mediaClient.getMetadata(mediaRef);
      checkMetadataContainsEntries(metadata, inputEntries);
      
      testWhenResponseIs2xxLoginReturnsValidMetadata();
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
         dependsOnMethods = { "testWhenResponseIs2xxLoginMergedMetadata" })
   public void testWhenResponseIs2xxLoginReturnsValidMetadataValue() {
      metadataValue = mediaClient.getMetadataValue(mediaRef, "key");
      
      // Check parent type
      checkResourceType(metadataValue);
      
      // Check required elements and attributes
      String value = metadataValue.getValue();
      assertNotNull(value,
            String.format(OBJ_FIELD_ATTRB_REQ, MEDIA, "MetadataEntry",
                  metadataValue.toString(), metadataEntryValue.toString()));
      assertEquals(value, metadataEntryValue,
            String.format(OBJ_FIELD_EQ, MEDIA, "metadataEntry.value", metadataEntryValue, value));
   }
   
   @Test(testName = "PUT /media/{id}/metadata/{key}",
         dependsOnMethods = { "testWhenResponseIs2xxLoginReturnsValidMetadataValue" })
   public void testWhenResponseIs2xxLoginUpdatesMetadataEntry() {
      metadataEntryValue = "newValue";
      MetadataValue newValue = MetadataValue.builder().value(metadataEntryValue).build();
      
      Task setMetadataEntry = mediaClient.setMetadata(mediaRef, "key", newValue);
      Checks.checkTask(setMetadataEntry);
      assertTrue(retryTaskSuccess.apply(setMetadataEntry.getHref()),
            String.format(TASK_COMPLETE_TIMELY, "setMetadataEntry"));
      metadataValue = mediaClient.getMetadataValue(mediaRef, "key");
      
      // ensure metadataEntry remains valid
      testWhenResponseIs2xxLoginReturnsValidMetadataValue();
   }
   
   @Test(testName = "DELETE /media/{id}/metadata/{key}",
         dependsOnMethods = { "testWhenResponseIs2xxLoginUpdatesMetadataEntry" } )
   public void testWhenResponseIs2xxLoginDeletesMetadataEntry() {
      Task deleteMetadataEntry = mediaClient.deleteMetadataEntry(mediaRef, "testKey");
      Checks.checkTask(deleteMetadataEntry);
      assertTrue(retryTaskSuccess.apply(deleteMetadataEntry.getHref()),
            String.format(TASK_COMPLETE_TIMELY, "deleteMetadataEntry"));
      
      Error expected = Error.builder()
            .message("The access to the resource metadata_item with id testKey is forbidden")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();
      
      try {
         metadataValue = mediaClient.getMetadataValue(mediaRef, "testKey");
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
      
      // ensure metadata and media remains valid
      testWhenResponseIs2xxLoginReturnsValidMetadata();
      testWhenResponseIs2xxLoginReturnsValidMedia();
   }
   @Test(testName = "DELETE /media/{id}",
         dependsOnMethods = { "testWhenResponseIs2xxLoginDeletesMetadataEntry" } )
   public void testWhenResponseIs2xxLoginDeletesMedia() {
      
      Task deleteMedia = mediaClient.deleteMedia(mediaRef);
      Checks.checkTask(deleteMedia);
      assertTrue(retryTaskSuccess.apply(deleteMedia.getHref()),
            String.format(TASK_COMPLETE_TIMELY, "deleteMedia"));
      
      Error expected = Error.builder()
            .message(String.format(
                  "No access to entity \"(com.vmware.vcloud.entity.media:%s)\".",
                  mediaRef.getId()))
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();
      
      try {
         media = mediaClient.getMedia(mediaRef);
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
   }
}
