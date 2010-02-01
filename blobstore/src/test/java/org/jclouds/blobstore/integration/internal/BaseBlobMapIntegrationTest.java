/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.util.internal.BlobStoreUtilsImpl;
import org.jclouds.util.Utils;
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

         Collection<Blob> values = map.values();
         assertConsistencyAwareMapSize(map, 5);
         Set<String> valuesAsString = new HashSet<String>();
         for (Blob object : values) {
            valuesAsString.add(BlobStoreUtilsImpl.getContentAsStringOrNullAndClose(object));
         }
         valuesAsString.removeAll(fiveStrings.values());
         assert valuesAsString.size() == 0 : valuesAsString.size() + ": " + values + ": "
                  + valuesAsString;
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testRemove() throws InterruptedException, ExecutionException, TimeoutException {
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

   private void assertConsistencyAwareContentEquals(final Map<String, Blob> map, final String key,
            final String value) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            Blob old = map.remove(key);
            try {
               assertEquals(BlobStoreUtilsImpl.getContentAsStringOrNullAndClose(old), value);
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
            assertEquals(fiveStrings.get(entry.getKey()), BlobStoreUtilsImpl
                     .getContentAsStringOrNullAndClose(entry.getValue()));
            Blob value = entry.getValue();
            value.setPayload("");
            value.generateMD5();
            entry.setValue(value);
         }
         assertConsistencyAware(new Runnable() {
            public void run() {
               for (Blob value : map.values()) {
                  try {
                     assertEquals(BlobStoreUtilsImpl.getContentAsStringOrNullAndClose(value), "");
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
   public void testContains() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, Blob> map = createMap(context, bucketName);
         putStringWithMD5(map, "one", "apple");
         Blob object = context.getBlobStore().newBlob("one");
         object.setPayload("apple");
         object.generateMD5();
         assertConsistencyAwareContainsValue(map, object);
      } finally {
         returnContainer(bucketName);
      }
   }

   void getOneReturnsAppleAndOldValueIsNull(Map<String, Blob> map, Blob old) throws IOException,
            InterruptedException {
      assert old == null;
      assertEquals(BlobStoreUtilsImpl.getContentAsStringOrNullAndClose(map.get("one")), "apple");
      assertConsistencyAwareMapSize(map, 1);
   }

   void getOneReturnsBearAndOldValueIsApple(Map<String, Blob> map, Blob oldValue)
            throws IOException, InterruptedException {
      assertEquals(BlobStoreUtilsImpl.getContentAsStringOrNullAndClose(map.get("one")), "bear");
      assertEquals(BlobStoreUtilsImpl.getContentAsStringOrNullAndClose(oldValue), "apple");
      assertConsistencyAwareMapSize(map, 1);
   }

   @Test(groups = { "integration", "live" })
   public void testPut() throws IOException, InterruptedException {
      String bucketName = getContainerName();
      try {
         Map<String, Blob> map = createMap(context, bucketName);
         Blob object = context.getBlobStore().newBlob("one");
         object.setPayload(Utils.toInputStream("apple"));
         object.generateMD5();
         Blob old = map.put(object.getMetadata().getName(), object);
         getOneReturnsAppleAndOldValueIsNull(map, old);
         object.setPayload(Utils.toInputStream("bear"));
         object.generateMD5();
         Blob apple = map.put(object.getMetadata().getName(), object);
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
            Blob object = context.getBlobStore().newBlob(key);
            object.setPayload(fiveInputs.get(key));
            object.setContentLength(new Long(fiveBytes.get(key).length));
            newMap.put(key, object);
         }
         map.putAll(newMap);
         assertConsistencyAwareMapSize(map, 5);
         assertConsistencyAwareKeySetEquals(map, new HashSet<String>(fiveInputs.keySet()));
         fourLeftRemovingOne(map);
      } finally {
         returnContainer(bucketName);
      }
   }

   protected abstract int maxList();

   @Test(enabled = false, groups = { "integration", "live" })
   public void testPutMoreThanSingleListing() throws InterruptedException, ExecutionException,
            TimeoutException {
      if (maxList() == 0)
         return;
      String bucketName = getContainerName();
      try {
         Map<String, Blob> map = createMap(context, bucketName);
         Set<String> keySet = Sets.newHashSet();
         for (int i = 0; i < maxList() + 1; i++) {
            keySet.add(i + "");
         }

         Map<String, Blob> newMap = new HashMap<String, Blob>();
         for (String key : keySet) {
            Blob object = context.getBlobStore().newBlob(key);
            object.setPayload(key);
            newMap.put(key, object);
         }
         map.putAll(newMap);
         newMap.clear();

         assertConsistencyAwareMapSize(map, maxList() + 1);
         assertConsistencyAwareKeySetEquals(map, keySet);
         map.clear();
         assertConsistencyAwareMapSize(map, 0);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Override
   protected void putStringWithMD5(Map<String, Blob> map, String key, String value) {
      Blob object = context.getBlobStore().newBlob(key);
      object.setPayload(value);
      object.generateMD5();
      map.put(key, object);
   }

   protected void putFiveStrings(Map<String, Blob> map) {
      Map<String, Blob> newMap = new HashMap<String, Blob>();
      for (Map.Entry<String, String> entry : fiveStrings.entrySet()) {
         Blob object = context.getBlobStore().newBlob(entry.getKey());
         object.setPayload(entry.getValue());
         newMap.put(entry.getKey(), object);
      }
      map.putAll(newMap);
   }

   protected void putFiveStringsUnderPath(Map<String, Blob> map) {
      Map<String, Blob> newMap = new HashMap<String, Blob>();
      for (Map.Entry<String, String> entry : fiveStringsUnderPath.entrySet()) {
         Blob object = context.getBlobStore().newBlob(entry.getKey());
         object.setPayload(entry.getValue());
         newMap.put(entry.getKey(), object);
      }
      map.putAll(newMap);
   }

   protected Map<String, Blob> createMap(BlobStoreContext context, String bucket) {
      return context.createBlobMap(bucket);
   }

}
