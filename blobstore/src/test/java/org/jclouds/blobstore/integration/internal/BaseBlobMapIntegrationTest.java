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
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * Tests to cover @{link LiveS3ObjectMap}
 * 
 * @author Adrian Cole
 */
public class BaseBlobMapIntegrationTest extends BaseMapIntegrationTest<Blob> {

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
            valuesAsString.add(BlobStoreUtils.getContentAsStringOrNullAndClose(object));
         }
         valuesAsString.removeAll(fiveStrings.values());
         assert valuesAsString.size() == 0;
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
               assertEquals(BlobStoreUtils.getContentAsStringOrNullAndClose(old), value);
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
         Map<String, Blob> map = createMap(context, bucketName);
         putFiveStrings(map);
         Set<Entry<String, Blob>> entries = map.entrySet();
         assertEquals(entries.size(), 5);
         for (Entry<String, Blob> entry : entries) {
            assertEquals(fiveStrings.get(entry.getKey()), BlobStoreUtils
                     .getContentAsStringOrNullAndClose(entry.getValue()));
            Blob value = entry.getValue();
            value.setPayload("");
            value.generateMD5();
            entry.setValue(value);
         }
         assertConsistencyAwareMapSize(map, 5);
         for (Blob value : map.values()) {
            assertEquals(BlobStoreUtils.getContentAsStringOrNullAndClose(value), null);
         }
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
      assertEquals(BlobStoreUtils.getContentAsStringOrNullAndClose(map.get("one")), "apple");
      assertConsistencyAwareMapSize(map, 1);
   }

   void getOneReturnsBearAndOldValueIsApple(Map<String, Blob> map, Blob oldValue)
            throws IOException, InterruptedException {
      assertEquals(BlobStoreUtils.getContentAsStringOrNullAndClose(map.get("one")), "bear");
      assertEquals(BlobStoreUtils.getContentAsStringOrNullAndClose(oldValue), "apple");
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
         assertConsistencyAwareKeySetEquals(map, new TreeSet<String>(fiveInputs.keySet()));
         fourLeftRemovingOne(map);
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
