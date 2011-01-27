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
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
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
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * Tests to cover @{link LiveS3ObjectMap}
 * 
 * @author Adrian Cole
 */
public abstract class BaseInputStreamMapIntegrationTest extends BaseMapIntegrationTest<InputStream> {

   @Override
   @Test(groups = { "integration", "live" })
   public void testValues() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);
         map.putAll(this.fiveInputs);
         // this will cause us to block until the bucket updates.
         assertConsistencyAwareMapSize(map, 5);
         Collection<InputStream> values = map.values();
         assertEquals(values.size(), 5);
         Set<String> valuesAsString = new HashSet<String>();
         for (InputStream stream : values) {
            valuesAsString.add(Strings2.toStringAndClose(stream));
         }
         valuesAsString.removeAll(fiveStrings.values());
         assert valuesAsString.size() == 0 : valuesAsString.size() + ": " + values + ": " + valuesAsString;
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPutMoreThanSingleListing() throws InterruptedException, ExecutionException, TimeoutException {
      String containerName = getContainerName();
      try {
         InputStreamMap map = createMap(context, containerName);
         Set<String> keySet = Sets.newHashSet();
         for (int i = 0; i < maxResultsForTestListings() + 1; i++) {
            keySet.add(i + "");
         }

         Map<String, String> newMap = new HashMap<String, String>();
         for (String key : keySet) {
            newMap.put(key, key);
         }
         map.putAllStrings(newMap);
         newMap.clear();

         assertConsistencyAwareMapSize(map, maxResultsForTestListings() + 1);
         assertConsistencyAwareKeySetEquals(map, keySet);
         map.clear();
         assertConsistencyAwareMapSize(map, 0);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testRemove() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);
         putStringWithMD5(map, "one", "two");
         InputStream old = map.remove("one");
         assertEquals(Strings2.toStringAndClose(old), "two");
         assertConsistencyAwareKeySize(map, 0);
         old = map.remove("one");
         assert old == null;
         old = map.get("one");
         assert old == null;
         assertConsistencyAwareKeySize(map, 0);
      } finally {
         returnContainer(containerName);
      }
   }

   @Override
   @Test(groups = { "integration", "live" })
   public void testEntrySet() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);
         ((InputStreamMap) map).putAllStrings(this.fiveStrings);
         // this will cause us to block until the bucket updates.
         assertConsistencyAwareKeySize(map, 5);
         Set<Entry<String, InputStream>> entries = map.entrySet();
         assertEquals(entries.size(), 5);
         for (Entry<String, InputStream> entry : entries) {
            assertEquals(fiveStrings.get(entry.getKey()), Strings2.toStringAndClose(entry.getValue()));
            entry.setValue(Strings2.toInputStream(""));
         }
         assertConsistencyAwareMapSize(map, 5);
         for (Entry<String, InputStream> entry : map.entrySet()) {
            assertEquals(Strings2.toStringAndClose(checkNotNull(entry.getValue(), entry.getKey())), "");
         }
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testContainsStringValue() throws InterruptedException, ExecutionException, TimeoutException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);
         ((InputStreamMap) map).putString("one", String.format(XML_STRING_FORMAT, "apple"));
         assertConsistencyAwareContainsValue(map, fiveStrings.get("one"));
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testContainsFileValue() throws InterruptedException, ExecutionException, TimeoutException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);
         ((InputStreamMap) map).putString("one", String.format(XML_STRING_FORMAT, "apple"));
         assertConsistencyAwareContainsValue(map, fiveFiles.get("one"));
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testContainsInputStreamValue() throws InterruptedException, ExecutionException, TimeoutException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);
         ((InputStreamMap) map).putString("one", String.format(XML_STRING_FORMAT, "apple"));
         assertConsistencyAwareContainsValue(map, this.fiveInputs.get("one"));
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testContainsBytesValue() throws InterruptedException, ExecutionException, TimeoutException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);
         ((InputStreamMap) map).putString("one", String.format(XML_STRING_FORMAT, "apple"));
         assertConsistencyAwareContainsValue(map, this.fiveBytes.get("one"));
      } finally {
         returnContainer(containerName);
      }
   }

   @Override
   @Test(groups = { "integration", "live" })
   public void testPutAll() throws InterruptedException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);
         map.putAll(this.fiveInputs);
         assertConsistencyAwareMapSize(map, 5);
         assertConsistencyAwareKeySetEquals(map, new TreeSet<String>(fiveInputs.keySet()));
         fourLeftRemovingOne(map);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPutAllBytes() throws InterruptedException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);

         ((InputStreamMap) map).putAllBytes(this.fiveBytes);
         assertConsistencyAwareMapSize(map, 5);
         assertConsistencyAwareKeySetEquals(map, new TreeSet<String>(fiveBytes.keySet()));
         fourLeftRemovingOne(map);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPutAllFiles() throws InterruptedException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);

         ((InputStreamMap) map).putAllFiles(this.fiveFiles);
         assertConsistencyAwareMapSize(map, 5);
         assertConsistencyAwareKeySetEquals(map, new TreeSet<String>(fiveFiles.keySet()));
         fourLeftRemovingOne(map);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPutAllStrings() throws InterruptedException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);

         ((InputStreamMap) map).putAllStrings(this.fiveStrings);
         assertConsistencyAwareMapSize(map, 5);
         assertConsistencyAwareKeySetEquals(map, new TreeSet<String>(fiveStrings.keySet()));
         fourLeftRemovingOne(map);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPutString() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);

         InputStream old = ((InputStreamMap) map).putString("one", fiveStrings.get("one"));
         getOneReturnsAppleAndOldValueIsNull(map, old);
         InputStream apple = ((InputStreamMap) map).putString("one", fiveStrings.get("two"));
         getOneReturnsBearAndOldValueIsApple(map, apple);
      } finally {
         returnContainer(containerName);
      }
   }

   void getOneReturnsAppleAndOldValueIsNull(Map<String, InputStream> map, InputStream old) throws IOException,
            InterruptedException {
      assert old == null;
      assertEquals(Strings2.toStringAndClose(map.get("one")), String.format(XML_STRING_FORMAT, "apple"));
      assertConsistencyAwareMapSize(map, 1);
   }

   void getOneReturnsBearAndOldValueIsApple(Map<String, InputStream> map, InputStream oldValue) throws IOException,
            InterruptedException {
      assertEquals(Strings2.toStringAndClose(map.get("one")), String.format(XML_STRING_FORMAT, "bear"));
      assertEquals(Strings2.toStringAndClose(oldValue), String.format(XML_STRING_FORMAT, "apple"));
      assertConsistencyAwareMapSize(map, 1);
   }

   @Test(groups = { "integration", "live" })
   public void testPutFile() throws IOException, InterruptedException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);

         InputStream old = ((InputStreamMap) map).putFile("one", fiveFiles.get("one"));
         getOneReturnsAppleAndOldValueIsNull(map, old);
         InputStream apple = ((InputStreamMap) map).putFile("one", fiveFiles.get("two"));
         getOneReturnsBearAndOldValueIsApple(map, apple);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPutBytes() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);

         InputStream old = ((InputStreamMap) map).putBytes("one", fiveBytes.get("one"));
         getOneReturnsAppleAndOldValueIsNull(map, old);
         InputStream apple = ((InputStreamMap) map).putBytes("one", fiveBytes.get("two"));
         getOneReturnsBearAndOldValueIsApple(map, apple);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPut() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {
         Map<String, InputStream> map = createMap(context, containerName);

         InputStream old = map.put("one", fiveInputs.get("one"));
         getOneReturnsAppleAndOldValueIsNull(map, old);
         InputStream apple = map.put("one", fiveInputs.get("two"));
         getOneReturnsBearAndOldValueIsApple(map, apple);
      } finally {
         returnContainer(containerName);
      }
   }

   @Override
   protected void putStringWithMD5(Map<String, InputStream> map, String key, String value) throws InterruptedException {
      ((InputStreamMap) map).putString(key, value);
   }

   protected int maxResultsForTestListings() {
      return 100;
   }

   protected InputStreamMap createMap(BlobStoreContext context, String bucket) {
      return createMap(context, bucket, maxResults(maxResultsForTestListings()));
   }

   protected InputStreamMap createMap(BlobStoreContext context, String bucket, ListContainerOptions options) {
      return context.createInputStreamMap(bucket, options);
   }
}
