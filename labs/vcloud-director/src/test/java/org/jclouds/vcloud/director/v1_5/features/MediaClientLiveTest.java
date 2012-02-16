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

import java.net.URI;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
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
@Test(groups = { "live", "apitests", "User" }, testName = "MediaClientLiveTest")
public class MediaClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   public static final String MEDIA = "media";
   public static Predicate<URI> taskTester;

   /*
    * Convenience references to API clients.
    */
   protected MediaClient mediaClient;

   /*
    * Shared state between dependant tests.
    */
   private Reference mediaRef;
   private Media media;
   private Owner owner;
   private Metadata metadata;
   private MetadataEntry metadataEntry;
   private String metadataEntryValue = "value";
   
   @BeforeGroups(groups = { "live" }, dependsOnMethods = { "setupClient" })
   public void before() {
      mediaRef = Reference.builder()
            .type("application/vnd.vmware.vcloud.media+xml")
            .name("")
            .href(URI.create(endpoint+"media/794eb334-754e-4917-b5a0-5df85cbd61d1")) //GREENHOUSE ONLY
            .build();
      mediaClient = context.getApi().getMediaClient();
      taskTester = new RetryablePredicate<URI>(new TaskSuccess(context), 10, 1,
            TimeUnit.SECONDS);
   }
   
   @Test(testName = "GET /media/{id}")
   public void testWhenResponseIs2xxLoginReturnsValidMedia() {
      // required for testing
      assertNotNull(mediaRef, String.format(REF_REQ_LIVE, MEDIA));
      
      media = mediaClient.getMedia(mediaRef);
      assertNotNull(media, String.format(OBJ_REQ_LIVE, MEDIA));
      
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
            "getOwner()", "owner", "media.getOwner()", owner, directOwner));
      
      // parent type
      Checks.checkResourceType(directOwner);
      
      // required
      assertNotNull(directOwner.getUser(), String.format(OBJ_FIELD_REQ, "Owner", "user"));
      Checks.checkReferenceType(directOwner.getUser());
   }
   
   @Test(testName = "PUT /media/{id}", 
         dependsOnMethods = { "testWhenResponseIs2xxLoginReturnsValidMedia" })
   public void testWhenResponseIs2xxLoginReturnsValidNetwork() {
      String newName = "new "+media.getName();
      String newDescription = "new "+media.getDescription();
      media.setName(newName);
      media.setDescription(newDescription);
      
      Task updateMedia = mediaClient.updateMedia(mediaRef, media);
      Checks.checkTask(updateMedia);
      assertTrue(taskTester.apply(updateMedia.getHref()), String.format(TASK_COMPLETE_TIMELY, "updateMedia"));
      media = mediaClient.getMedia(mediaRef);
      
      assertTrue(equal(media.getName(), newName), String.format(OBJ_FIELD_UPDATABLE, MEDIA, "name"));
      assertTrue(equal(media.getDescription(), newDescription), 
            String.format(OBJ_FIELD_UPDATABLE, MEDIA, "description"));
      
      //TODO negative tests?
      
      // ensure media remains valid
      testWhenResponseIs2xxLoginReturnsValidMedia();
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
               String.format(OBJ_FIELD_ATTRB_REQ, MEDIA, "MetadataEntry", metadataEntry, "key"));
         assertNotNull(entry.getValue(), 
               String.format(OBJ_FIELD_ATTRB_REQ, MEDIA, "MetadataEntry", metadataEntry, "value"));
         
         // parent type
         checkResourceType(entry);
      }
   }
   
   @Test(testName = "POST /media/{id}/metadata", 
         dependsOnMethods = { "testWhenResponseIs2xxLoginReturnsValidMetadata" })
   public void testWhenResponseIs2xxLoginMergedMetadata() {
      Set<MetadataEntry> inputEntries = null;
      MetadataEntry keyEntry = null;
      MetadataEntry updateEntry = null;
      MetadataEntry newEntry = null;
      
      for (MetadataEntry entry : metadata.getMetadataEntries() ) {
         if (entry.getKey().equals("key")){
            keyEntry = entry;
         } else {
            updateEntry = entry;
         }
         
         if (updateEntry != null && keyEntry != null) {
            newEntry = MetadataEntry.builder()
                  .entry("new "+updateEntry.getKey(), "new "+updateEntry.getValue())
                  .build();
            break;
         }
      }
      
      if (newEntry != null ) { // found both, made new
         inputEntries = ImmutableSet.of(keyEntry, updateEntry, newEntry);
      } else if (keyEntry != null) { // update missing, use key to update, make entirely new entry
         metadataEntryValue = "new "+updateEntry.getValue();
         updateEntry = MetadataEntry.builder()
               .entry(keyEntry.getKey(), metadataEntryValue)
               .build();
         newEntry = MetadataEntry.builder()
               .entry("new key", "new value")
               .build();
         inputEntries = ImmutableSet.of(updateEntry, newEntry);
      } else if (updateEntry != null) { // key missing, use new to make it
         keyEntry = newEntry = MetadataEntry.builder()
               .entry("key", "value")
               .build();
         inputEntries = ImmutableSet.of(updateEntry, newEntry);
      }
      
      Metadata inputMetadata = Metadata.builder()
            .entries(inputEntries)
            .build();
      
      Task mergeMetadata = mediaClient.mergeMetadata(mediaRef, inputMetadata);
      Checks.checkTask(mergeMetadata);
      assertTrue(taskTester.apply(mergeMetadata.getHref()), String.format(TASK_COMPLETE_TIMELY, "mergeMetadata"));
      metadata = mediaClient.getMetadata(mediaRef);
      
      assertEquals(metadata.getMetadataEntries(), inputEntries, 
            String.format(OBJ_FIELD_EQ, MEDIA, "metadata", metadata.getMetadataEntries(), inputEntries));
      
      // ensure metadata remains valid
      testWhenResponseIs2xxLoginReturnsValidMetadata();
   }
   
   @Test(testName = "GET /media/{id}/metadata/{key}", 
         dependsOnMethods = { "testWhenResponseIs2xxLoginMergedMetadata" })
   public void testWhenResponseIs2xxLoginReturnsValidMetadataEntry() {
      metadataEntry = mediaClient.getMetadataEntry(mediaRef, "key");
      
      // Check parent type
      checkResourceType(metadataEntry);
      
      // Check required elements and attributes
      String key = metadataEntry.getKey();
      String value = metadataEntry.getValue();
      assertNotNull(key, String.format(OBJ_FIELD_ATTRB_REQ, MEDIA, "MetadataEntry", metadataEntry, "key"));
      assertEquals(key, "key", String.format(OBJ_FIELD_EQ, MEDIA, "metadataEntry.key", "key", key));
      assertNotNull(value, 
            String.format(OBJ_FIELD_ATTRB_REQ, MEDIA, "MetadataEntry", metadataEntry, metadataEntryValue));
      assertEquals(value, metadataEntryValue, 
            String.format(OBJ_FIELD_EQ, MEDIA, "metadataEntry.value", metadataEntryValue, value));
   }
   
   @Test(testName = "PUT /media/{id}/metadata/{key}", 
         dependsOnMethods = { "testWhenResponseIs2xxLoginReturnsValidMetadataEntry" })
   public void testWhenResponseIs2xxLoginUpdatesMetadataEntry() {
      metadataEntryValue = "newValue";
      MetadataValue newValue = MetadataValue.builder().value(metadataEntryValue).build();
      
      Task setMetadataEntry = mediaClient.setMetadata(mediaRef, "key", newValue);
      Checks.checkTask(setMetadataEntry);
      assertTrue(taskTester.apply(setMetadataEntry.getHref()), 
            String.format(TASK_COMPLETE_TIMELY, "setMetadataEntry"));
      metadataEntry = mediaClient.getMetadataEntry(mediaRef, "key");
      
      // ensure metadataEntry remains valid
      testWhenResponseIs2xxLoginReturnsValidMetadataEntry();
   }
   
   @Test(testName = "DELETE /media/{id}/metadata/{key}", 
         dependsOnMethods = { "testWhenResponseIs2xxLoginUpdatesMetadataEntry" },
         enabled = false )
   public void testWhenResponseIs2xxLoginDeletesMetadataEntry() {
      Task deleteMetadataEntry = mediaClient.deleteMetadataEntry(mediaRef, "key");
      Checks.checkTask(deleteMetadataEntry);
      assertTrue(taskTester.apply(deleteMetadataEntry.getHref()), 
            String.format(TASK_COMPLETE_TIMELY, "deleteMetadataEntry"));
      metadataEntry = mediaClient.getMetadataEntry(mediaRef, "key");
      
      assertNull(metadataEntry, String.format(OBJ_FIELD_ATTRB_DEL, MEDIA, 
            "Metadata", metadataEntry, "metadataEntry", metadataEntry));
      
      // ensure metadata and media remains valid
      testWhenResponseIs2xxLoginReturnsValidMetadata();
      testWhenResponseIs2xxLoginReturnsValidMedia();
   }
   @Test(testName = "DELETE /media/{id}", 
         dependsOnMethods = { "testWhenResponseIs2xxLoginDeletesMetadataEntry" },
         enabled = false )
   public void testWhenResponseIs2xxLoginDeletesMedia() {
      
      Task deleteMedia = mediaClient.deleteMedia(mediaRef);
      Checks.checkTask(deleteMedia);
      assertTrue(taskTester.apply(deleteMedia.getHref()), 
            String.format(TASK_COMPLETE_TIMELY, "deleteMedia"));
      media = mediaClient.getMedia(mediaRef);
      
      assertNull(media, String.format(OBJ_DEL, MEDIA, media));
   }
}
