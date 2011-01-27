/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.blobstore.integration.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.maxResults;
import static org.jclouds.blobstore.util.BlobStoreUtils.getContentAsStringOrNullAndClose;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.io.Payloads;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;

/**
 * Tests to cover @{link LiveS3ObjectMap}
 * 
 * @author Adrian Cole
 */
public abstract class BaseBlobMapIntegrationTest extends BaseMapIntegrationTest<Blob> {

   @Override
   @Test(groups = { "integration", "live" })
   public void testValues() throws IOException, InterruptedException {
      String bucketName = getContainerName();
      try {
         Map<String, Blob> map = createMap(context, bucketName);

         putFiveStrings(map);
         putFiveStringsUnderPath(map);

         Collection<Blob> blobs = map.values();
         assertConsistencyAwareMapSize(map, 5);
         Set<String> blobsAsString = new HashSet<String>();
         for (Blob blob : blobs) {
            blobsAsString.add(getContentAsStringOrNullAndClose(blob));
         }
         blobsAsString.removeAll(fiveStrings.values());
         assert blobsAsString.size() == 0 : blobsAsString.size() + ": " + blobs + ": " + blobsAsString;
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testRemove() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String bucketName = getContainerName();
      try {
         Map<String, Blob> map = createMap(context, bucketName);
         putStringWithMD5(map, "one", "two");
         assertConsistencyAwareContentEquals(map, "one", "two");
         // TODO track how often this occurs and potentially update map implementation
         assertConsistencyAwareRemoveEquals(map, "one", null);
         assertConsistencyAwareGetEquals(map, "one", null);
         assertConsistencyAwareKeySize(map, 0);
      } finally {
         returnContainer(bucketName);
      }
   }

   private void assertConsistencyAwareContentEquals(final Map<String, Blob> map, final String key, final String blob)
            throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            Blob old = map.remove(key);
            try {
               assertEquals(getContentAsStringOrNullAndClose(old), blob);
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
         }
      });
   }

   @Override
   @Test(groups = { "integration", "live" })
   public void testEntrySet() throws IOException, InterruptedException {
      String bucketName = getContainerName();
      try {
         final Map<String, Blob> map = createMap(context, bucketName);
         putFiveStrings(map);
         assertConsistencyAwareMapSize(map, 5);
         Set<Entry<String, Blob>> entries = map.entrySet();
         assertEquals(entries.size(), 5);
         for (Entry<String, Blob> entry : entries) {
            assertEquals(fiveStrings.get(entry.getKey()), getContentAsStringOrNullAndClose(entry.getValue()));
            Blob blob = entry.getValue();
            blob.setPayload("");
            Payloads.calculateMD5(blob);
            entry.setValue(blob);
         }
         assertConsistencyAware(new Runnable() {
            public void run() {
               for (Blob blob : map.values()) {
                  try {
                     assertEquals(getContentAsStringOrNullAndClose(blob), "");
                  } catch (IOException e) {
                     Throwables.propagate(e);
                  }
               }
            }
         });

      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testContains() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String bucketName = getContainerName();
      try {
         Map<String, Blob> map = createMap(context, bucketName);
         putStringWithMD5(map, "one", "apple");
         Blob blob = context.getBlobStore().newBlob("one");
         blob.setPayload("apple");
         Payloads.calculateMD5(blob);
         assertConsistencyAwareContainsValue(map, blob);
      } finally {
         returnContainer(bucketName);
      }
   }

   void getOneReturnsAppleAndOldValueIsNull(Map<String, Blob> map, Blob old) throws IOException, InterruptedException {
      assert old == null;
      assertEquals(getContentAsStringOrNullAndClose(map.get("one")), "apple");
      assertConsistencyAwareMapSize(map, 1);
   }

   void getOneReturnsBearAndOldValueIsApple(Map<String, Blob> map, Blob oldValue) throws IOException,
            InterruptedException {
      assertEquals(getContentAsStringOrNullAndClose(checkNotNull(map.get("one"), "one")), "bear");
      assertEquals(getContentAsStringOrNullAndClose(oldValue), "apple");
      assertConsistencyAwareMapSize(map, 1);
   }

   @Test(groups = { "integration", "live" })
   public void testPut() throws IOException, InterruptedException {
      String bucketName = getContainerName();
      try {
         Map<String, Blob> map = createMap(context, bucketName);
         Blob blob = context.getBlobStore().newBlob("one");
         blob.setPayload(Strings2.toInputStream("apple"));
         Payloads.calculateMD5(blob);
         Blob old = map.put(blob.getMetadata().getName(), blob);
         getOneReturnsAppleAndOldValueIsNull(map, old);
         blob.setPayload(Strings2.toInputStream("bear"));
         Payloads.calculateMD5(blob);
         Blob apple = map.put(blob.getMetadata().getName(), blob);
         getOneReturnsBearAndOldValueIsApple(map, apple);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPutAll() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, Blob> map = createMap(context, bucketName);
         Map<String, Blob> newMap = new HashMap<String, Blob>();
         for (String key : fiveInputs.keySet()) {
            Blob blob = context.getBlobStore().newBlob(key);
            blob.setPayload(fiveInputs.get(key));
            blob.getPayload().getContentMetadata().setContentLength((long) fiveBytes.get(key).length);
            newMap.put(key, blob);
         }
         map.putAll(newMap);
         assertConsistencyAwareMapSize(map, 5);
         assertConsistencyAwareKeySetEquals(map, new HashSet<String>(fiveInputs.keySet()));
         fourLeftRemovingOne(map);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPutMoreThanSingleListing() throws InterruptedException, ExecutionException, TimeoutException {
      if (maxResultsForTestListings() == 0)
         return;
      String bucketName = getContainerName();
      try {
         Map<String, Blob> map = createMap(context, bucketName);
         Set<String> keySet = Sets.newHashSet();
         for (int i = 0; i < maxResultsForTestListings() + 1; i++) {
            keySet.add(i + "");
         }

         Map<String, Blob> newMap = new HashMap<String, Blob>();
         for (String key : keySet) {
            Blob blob = context.getBlobStore().newBlob(key);
            blob.setPayload(key);
            newMap.put(key, blob);
         }
         map.putAll(newMap);
         newMap.clear();

         assertConsistencyAwareMapSize(map, maxResultsForTestListings() + 1);
         assertConsistencyAwareKeySetEquals(map, keySet);
         map.clear();
         assertConsistencyAwareMapSize(map, 0);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Override
   protected void putStringWithMD5(Map<String, Blob> map, String key, String text) throws IOException {
      Blob blob = context.getBlobStore().newBlob(key);
      blob.setPayload(text);
      Payloads.calculateMD5(blob);
      map.put(key, blob);
   }

   protected void putFiveStrings(Map<String, Blob> map) {
      Map<String, Blob> newMap = new HashMap<String, Blob>();
      for (Map.Entry<String, String> entry : fiveStrings.entrySet()) {
         Blob blob = context.getBlobStore().newBlob(entry.getKey());
         blob.setPayload(entry.getValue());
         newMap.put(entry.getKey(), blob);
      }
      map.putAll(newMap);
   }

   protected void putFiveStringsUnderPath(Map<String, Blob> map) {
      Map<String, Blob> newMap = new HashMap<String, Blob>();
      for (Map.Entry<String, String> entry : fiveStringsUnderPath.entrySet()) {
         Blob blob = context.getBlobStore().newBlob(entry.getKey());
         blob.setPayload(entry.getValue());
         newMap.put(entry.getKey(), blob);
      }
      map.putAll(newMap);
   }

   protected int maxResultsForTestListings() {
      return 100;
   }

   protected BlobMap createMap(BlobStoreContext context, String bucket) {
      return createMap(context, bucket, maxResults(maxResultsForTestListings()));
   }

   protected BlobMap createMap(BlobStoreContext context, String bucket, ListContainerOptions options) {
      return context.createBlobMap(bucket, options);
   }
}
