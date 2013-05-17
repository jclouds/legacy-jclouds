/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.blobstore.integration.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.inDirectory;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.io.Payloads;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Tests to cover @{link LiveS3ObjectMap}
 * 
 * @author Adrian Cole
 */
public abstract class BaseBlobMapIntegrationTest extends BaseMapIntegrationTest<Blob> {

   private static class StringToBlob implements Function<String, Blob> {
      private final BlobMap map;

      private StringToBlob(BlobMap map) {
         this.map = map;
      }

      @Override
      public Blob apply(String arg0) {
         return map.blobBuilder().name(arg0).payload(arg0).build();
      }
   }

   @Override
   @Test(groups = { "integration", "live" })
   public void testValues() throws IOException, InterruptedException {
      String bucketName = getContainerName();
      try {
         BlobMap map = createMap(view, bucketName);

         putFiveStrings(map);
         putFiveStringsUnderPath(map);

         Collection<Blob> blobs = map.values();
         assertConsistencyAwareMapSize(map, 5);
         Set<String> blobsAsString = Sets.newLinkedHashSet();
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
         Map<String, Blob> map = createMap(view, bucketName);
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
         final BlobMap map = createMap(view, bucketName);
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
         Map<String, Blob> map = createMap(view, bucketName);
         putStringWithMD5(map, "one", "apple");
         Blob blob = view.getBlobStore().blobBuilder("one").payload("apple").calculateMD5().build();
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
         Map<String, Blob> map = createMap(view, bucketName);
         Blob blob = view.getBlobStore().blobBuilder("one").payload(Strings2.toInputStream("apple")).calculateMD5()
               .build();
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
         Map<String, Blob> map = createMap(view, bucketName);
         ImmutableMap.Builder<String, Blob> newMap = ImmutableMap.builder();
         for (Map.Entry<String, InputStream> entry : fiveInputs.entrySet()) {
            String key = entry.getKey();
            newMap.put(
                  key,
                  view.getBlobStore().blobBuilder(key).payload(entry.getValue())
                        .contentLength((long) fiveBytes.get(key).length).build());
         }
         map.putAll(newMap.build());
         assertConsistencyAwareMapSize(map, 5);
         assertConsistencyAwareKeySetEquals(map, ImmutableSet.copyOf(fiveInputs.keySet()));
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
         BlobMap map = createMap(view, bucketName);
         Builder<String> keySet = ImmutableSet.builder();
         for (int i = 0; i < maxResultsForTestListings() + 1; i++) {
            keySet.add(i + "");
         }

         Map<String, Blob> newMap = Maps.newLinkedHashMap();
         for (String key : keySet.build()) {
            newMap.put(key, map.blobBuilder().name(key).payload(key).build());
         }
         map.putAll(newMap);
         newMap.clear();

         assertConsistencyAwareMapSize(map, maxResultsForTestListings() + 1);
         assertConsistencyAwareKeySetEquals(map, keySet.build());
         map.clear();
         assertConsistencyAwareMapSize(map, 0);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Override
   protected void putStringWithMD5(Map<String, Blob> map, String key, String text) throws IOException {
      map.put(key, view.getBlobStore().blobBuilder(key).payload(text).calculateMD5().build());
   }

   protected void putFiveStrings(BlobMap map) {
      map.putAll(Maps.transformValues(fiveStrings, new StringToBlob(map)));
   }

   protected void putFiveStringsUnderPath(BlobMap map) {
      map.putAll(Maps.transformValues(fiveStringsUnderPath, new StringToBlob(map)));
   }

   protected int maxResultsForTestListings() {
      return 100;
   }

   @Override
   protected BlobMap createMap(BlobStoreContext context, String bucket) {
      return createMap(context, bucket, maxResults(maxResultsForTestListings()));
   }
   
   @Override
   protected BlobMap createMap(BlobStoreContext context, String bucket, ListContainerOptions options) {
      return context.createBlobMap(bucket, options);
   }

   @Override
   protected void addTenObjectsUnderPrefix(String containerName, String prefix) throws InterruptedException {
      BlobMap blobMap = createMap(view, containerName, inDirectory(prefix));
      for (int i = 0; i < 10; i++) {
         blobMap.put(i + "", blobMap.blobBuilder().name(i + "").payload(i + "content").build());
      }
   }

   @Override
   protected void addTenObjectsUnderRoot(String containerName) throws InterruptedException {
      BlobMap blobMap = createMap(view, containerName, ListContainerOptions.NONE);
      for (int i = 0; i < 10; i++) {
         blobMap.put(i + "", blobMap.blobBuilder().name(i + "").payload(i + "content").build());
      }
   }
}
