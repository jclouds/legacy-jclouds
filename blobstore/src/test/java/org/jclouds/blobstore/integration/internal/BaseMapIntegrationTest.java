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

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.inDirectory;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ListableMap;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.util.Strings2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

public abstract class BaseMapIntegrationTest<V> extends BaseBlobStoreIntegrationTest {

   public abstract void testPutAll() throws InterruptedException, ExecutionException, TimeoutException;

   public abstract void testEntrySet() throws IOException, InterruptedException, ExecutionException, TimeoutException;

   public abstract void testValues() throws IOException, InterruptedException, ExecutionException, TimeoutException;

   protected Map<String, byte[]> fiveBytes = Maps.transformValues(fiveStrings, new Function<String, byte[]>() {
      public byte[] apply(String from) {
         return from.getBytes();
      }
   });
   protected Map<String, InputStream> fiveInputs;
   protected Map<String, File> fiveFiles;
   String tmpDirectory;

   @BeforeMethod(groups = { "integration", "live" })
   protected void setUpInputStreams() {
      fiveInputs = Maps.transformValues(fiveStrings, new Function<String, InputStream>() {
         public InputStream apply(String from) {
            return Strings2.toInputStream(from);
         }
      });
   }

   @BeforeClass(groups = { "integration", "live" })
   @Parameters({ "basedir" })
   protected void setUpTempDir(@Optional String basedir) throws InterruptedException, ExecutionException,
         FileNotFoundException, IOException, TimeoutException {
      if (basedir == null) {
         basedir = System.getProperty("java.io.tmpdir");
      }
      tmpDirectory = basedir + File.separator + "target" + File.separator + "testFiles" + File.separator
            + getClass().getSimpleName();
      new File(tmpDirectory).mkdirs();
      fiveFiles = Maps.newHashMap();
      for (Entry<String, String> entry : fiveStrings.entrySet()) {
         File file = new File(tmpDirectory, entry.getKey());
         Files.write(entry.getValue().getBytes(Charsets.UTF_8), file);
         fiveFiles.put(entry.getKey(), file);
      }
   }

   protected abstract Map<String, V> createMap(BlobStoreContext context, String containerName);

   protected abstract Map<String, V> createMap(BlobStoreContext context, String containerName,
         ListContainerOptions options);

   @Test(groups = { "integration", "live" })
   public void testClear() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String containerNameName = getContainerName();
      try {
         Map<String, V> map = createMap(view, containerNameName);
         assertConsistencyAwareMapSize(map, 0);
         putStringWithMD5(map, "one", "apple");
         assertConsistencyAwareMapSize(map, 1);
         map.clear();
         assertConsistencyAwareMapSize(map, 0);
      } finally {
         returnContainer(containerNameName);
      }
   }

   @Test(groups = { "integration", "live" })
   public abstract void testRemove() throws IOException, InterruptedException, ExecutionException, TimeoutException;

   @Test(groups = { "integration", "live" })
   public void testKeySet() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String containerNameName = getContainerName();
      try {
         Map<String, V> map = createMap(view, containerNameName);
         assertConsistencyAwareKeySize(map, 0);
         putStringWithMD5(map, "one", "two");
         assertConsistencyAwareKeySize(map, 1);
         assertConsistencyAwareKeySetEquals(map, ImmutableSet.of("one"));
      } finally {
         returnContainer(containerNameName);
      }
   }

   protected void addTenObjectsUnderPrefix(String containerName, String prefix) throws InterruptedException {
      for (int i = 0; i < 10; i++) {
         view.getBlobStore().putBlob(containerName,
               view.getBlobStore().blobBuilder(prefix + "/" + i).payload(i + "content").build());
      }
   }

   protected void addTenObjectsUnderRoot(String containerName) throws InterruptedException {
      for (int i = 0; i < 10; i++) {
         view.getBlobStore().putBlob(containerName,
               view.getBlobStore().blobBuilder(i + "").payload(i + "content").build());
      }
   }

   @Test(groups = { "integration", "live" })
   public void testDirectory() throws InterruptedException {
      String containerName = getContainerName();
      String directory = "apps";
      Map<String, V> rootMap = createMap(view, containerName);
      Map<String, V> rootRecursiveMap = createMap(view, containerName, recursive());
      Map<String, V> inDirectoryMap = createMap(view, containerName, inDirectory(directory));
      Map<String, V> inDirectoryRecursiveMap = createMap(view, containerName, inDirectory(directory).recursive());
      try {

         view.getBlobStore().createDirectory(containerName, directory);
         addTenObjectsUnderRoot(containerName);
         assertEquals(rootMap.size(), 10);
         assertEquals(ImmutableSortedSet.copyOf(rootMap.keySet()),
               ImmutableSortedSet.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
         assertEquals(rootRecursiveMap.size(), 10);
         assertEquals(ImmutableSortedSet.copyOf(rootRecursiveMap.keySet()),
               ImmutableSortedSet.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
         assertEquals(inDirectoryMap.size(), 0);
         assertEquals(inDirectoryRecursiveMap.size(), 0);

         addTenObjectsUnderPrefix(containerName, directory);
         assertEquals(rootMap.size(), 10);
         assertEquals(ImmutableSortedSet.copyOf(rootMap.keySet()),
               ImmutableSortedSet.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
         assertEquals(rootRecursiveMap.size(), 20);
         assertEquals(ImmutableSortedSet.copyOf(rootRecursiveMap.keySet()), ImmutableSet.of("0", "1", "2", "3", "4",
               "5", "6", "7", "8", "9", "apps/0", "apps/1", "apps/2", "apps/3", "apps/4", "apps/5", "apps/6", "apps/7",
               "apps/8", "apps/9"));
         assertEquals(inDirectoryMap.size(), 10);
         assertEquals(ImmutableSortedSet.copyOf(inDirectoryMap.keySet()),
               ImmutableSortedSet.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
         assertEquals(inDirectoryRecursiveMap.size(), 10);
         assertEquals(ImmutableSortedSet.copyOf(inDirectoryRecursiveMap.keySet()),
               ImmutableSortedSet.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));

         view.getBlobStore().createDirectory(containerName, directory + "/" + directory);
         assertEquals(rootMap.size(), 10);
         assertEquals(rootRecursiveMap.size(), 20);
         assertEquals(inDirectoryMap.size(), 10);
         assertEquals(inDirectoryRecursiveMap.size(), 10);

         rootMap.clear();
         assertEquals(rootMap.size(), 0);
         assertEquals(rootRecursiveMap.size(), 10);
         assertEquals(inDirectoryMap.size(), 10);
         assertEquals(inDirectoryRecursiveMap.size(), 10);

         inDirectoryMap.clear();
         assertEquals(rootMap.size(), 0);
         assertEquals(rootRecursiveMap.size(), 0);
         assertEquals(inDirectoryMap.size(), 0);
         assertEquals(inDirectoryRecursiveMap.size(), 0);

      } finally {
         returnContainer(containerName);
      }

   }

   protected void assertConsistencyAwareKeySetEquals(final Map<String, V> map, final Set<String> expected)
         throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            Set<String> toMatch = map.keySet();
            Set<String> shouldBeEmpty = Sets.difference(expected, toMatch);
            assert shouldBeEmpty.size() == 0 : "toMatch has less keys than expected. missing: " + shouldBeEmpty;
            shouldBeEmpty = Sets.difference(toMatch, expected);
            assert shouldBeEmpty.size() == 0 : "toMatch has more keys than expected. extras: " + shouldBeEmpty;
            assertEquals(Sets.newTreeSet(toMatch), Sets.newTreeSet(expected));
         }
      });
   }

   protected void assertConsistencyAwareRemoveEquals(final Map<String, V> map, final String key, final Object equals)
         throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assertEquals(map.remove(key), equals);
         }
      });
   }

   protected void assertConsistencyAwareGetEquals(final Map<String, V> map, final String key, final Object equals)
         throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assertEquals(map.get(key), equals);
         }
      });
   }

   protected void assertConsistencyAwareKeySize(final Map<String, V> map, final int size) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assertEquals(map.keySet().size(), size);
         }
      });
   }

   @Test(groups = { "integration", "live" })
   public void testContainsKey() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String containerNameName = getContainerName();
      try {
         Map<String, V> map = createMap(view, containerNameName);
         assertConsistencyAwareDoesntContainKey(map);
         putStringWithMD5(map, "one", "apple");
         assertConsistencyAwareContainsKey(map);
      } finally {
         returnContainer(containerNameName);
      }
   }

   /**
    * containsValue() uses eTag comparison to containerName contents, so this can be subject to
    * eventual consistency problems.
    */
   protected void assertConsistencyAwareContainsValue(final Map<String, V> map, final Object value)
         throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assert map.containsValue(value);
         }
      });
   }

   protected void assertConsistencyAwareContainsKey(final Map<String, V> map) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assert map.containsKey("one");
         }
      });
   }

   protected void assertConsistencyAwareDoesntContainKey(final Map<String, V> map) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assert !map.containsKey("one");
         }
      });
   }

   @Test(groups = { "integration", "live" })
   public void testIsEmpty() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String containerNameName = getContainerName();
      try {
         Map<String, V> map = createMap(view, containerNameName);
         assertConsistencyAwareEmpty(map);
         putStringWithMD5(map, "one", "apple");
         assertConsistencyAwareNotEmpty(map);
      } finally {
         returnContainer(containerNameName);
      }
   }

   protected void assertConsistencyAwareNotEmpty(final Map<String, V> map) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assert !map.isEmpty();
         }
      });
   }

   protected void assertConsistencyAwareEmpty(final Map<String, V> map) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assert map.isEmpty();
         }
      });
   }

   abstract protected void putStringWithMD5(Map<String, V> map, String key, String value) throws InterruptedException,
         ExecutionException, TimeoutException, IOException;

   protected void fourLeftRemovingOne(Map<String, V> map) throws InterruptedException {
      map.remove("one");
      assertConsistencyAwareMapSize(map, 4);
      assertConsistencyAwareKeySetEquals(map, new TreeSet<String>(ImmutableSet.of("two", "three", "four", "five")));
   }

   protected void assertConsistencyAwareMapSize(final Map<String, V> map, final int size) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assertEquals(map.size(), size);
         }
      });
   }

   @Test(groups = { "integration", "live" })
   public abstract void testPut() throws IOException, InterruptedException, ExecutionException, TimeoutException;

   @Test(groups = { "integration", "live" })
   public void testListContainer() throws InterruptedException, ExecutionException, TimeoutException {
      String containerNameName = getContainerName();
      try {
         ListableMap<?, ?> map = (ListableMap<?, ?>) createMap(view, containerNameName);
         assertConsistencyAwareListContainer(map, containerNameName);
      } finally {
         returnContainer(containerNameName);
      }
   }

   protected void assertConsistencyAwareListContainer(final ListableMap<?, ?> map, final String containerNameName)
         throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assertTrue(Iterables.size(map.list()) >= 0);
         }
      });
   }

}
