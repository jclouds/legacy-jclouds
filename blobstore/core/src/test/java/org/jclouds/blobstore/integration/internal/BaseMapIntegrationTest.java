/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.blobstore.integration.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ListableMap;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public abstract class BaseMapIntegrationTest<S extends BlobStore<C, M, B>, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>, V>
         extends BaseBlobStoreIntegrationTest<S, C, M, B> {

   public abstract void testPutAll() throws InterruptedException, ExecutionException,
            TimeoutException;

   public abstract void testEntrySet() throws IOException, InterruptedException,
            ExecutionException, TimeoutException;

   public abstract void testValues() throws IOException, InterruptedException, ExecutionException,
            TimeoutException;

   protected Map<String, String> fiveStrings = ImmutableMap.of("one", "apple", "two", "bear",
            "three", "candy", "four", "dogma", "five", "emma");

   // IMPORTANT: Java 5 struggles to correctly infer types in some cases which affects
   // this ImmutableMap. The explicit typing works around the issue. Java 6 seems to cope.
   // http://groups.google.com/group/google-collections-users/browse_thread/thread/df70c482c93a25d8
   protected Map<String, byte[]> fiveBytes = ImmutableMap.<String, byte[]> of("one",
            "apple".getBytes(), // Explicit cast necessary for Java 5
            "two", "bear".getBytes(), "three", "candy".getBytes(), "four", "dogma".getBytes(),
            "five", "emma".getBytes());
   protected Map<String, InputStream> fiveInputs;
   protected Map<String, File> fiveFiles;
   String tmpDirectory;

   @BeforeMethod(groups = { "integration", "live" })
   protected void setUpInputStreams() {
      fiveInputs = ImmutableMap.of("one", IOUtils.toInputStream("apple"), "two", IOUtils
               .toInputStream("bear"), "three", IOUtils.toInputStream("candy"), "four", IOUtils
               .toInputStream("dogma"), "five", IOUtils.toInputStream("emma"));
   }

   @BeforeMethod(groups = { "integration", "live" })
   @Parameters( { "basedir" })
   protected void setUpTempDir(@Optional String basedir) throws InterruptedException,
            ExecutionException, FileNotFoundException, IOException, TimeoutException {
      if (basedir == null) {
         basedir = System.getProperty("java.io.tmpdir");
      }
      tmpDirectory = basedir + File.separator + "target" + File.separator + "testFiles"
               + File.separator + getClass().getSimpleName();
      new File(tmpDirectory).mkdirs();

      fiveFiles = ImmutableMap.of("one", new File(tmpDirectory, "apple"), "two", new File(
               tmpDirectory, "bear"), "three", new File(tmpDirectory, "candy"), "four", new File(
               tmpDirectory, "dogma"), "five", new File(tmpDirectory, "emma"));

      for (File file : fiveFiles.values()) {
         IOUtils.write(file.getName(), new FileOutputStream(file));
      }

   }

   protected abstract Map<String, V> createMap(BlobStoreContext<?, ?, ?, ?> context, String bucket);

   @Test(groups = { "integration", "live" })
   public void testClear() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, V> map = createMap(context, bucketName);
         assertEventuallyMapSize(map, 0);
         putString(map, "one", "apple");
         assertEventuallyMapSize(map, 1);
         map.clear();
         assertEventuallyMapSize(map, 0);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   public abstract void testRemove() throws IOException, InterruptedException, ExecutionException,
            TimeoutException;

   @Test(groups = { "integration", "live" })
   public void testKeySet() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, V> map = createMap(context, bucketName);
         assertEventuallyKeySize(map, 0);
         putString(map, "one", "two");
         assertEventuallyKeySize(map, 1);
         assertEventuallyKeySetEquals(map, ImmutableSet.of("one"));
      } finally {
         returnContainer(bucketName);
      }
   }

   protected void assertEventuallyKeySetEquals(final Map<String, V> map, final Object toEqual)
            throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            assertEquals(map.keySet(), toEqual);
         }
      });
   }

   protected void assertEventuallyRemoveEquals(final Map<String, V> map, final String key,
            final Object equals) throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            assertEquals(map.remove(key), equals);
         }
      });
   }

   protected void assertEventuallyGetEquals(final Map<String, V> map, final String key,
            final Object equals) throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            assertEquals(map.get(key), equals);
         }
      });
   }

   protected void assertEventuallyKeySize(final Map<String, V> map, final int size)
            throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            assertEquals(map.keySet().size(), size);
         }
      });
   }

   @Test(groups = { "integration", "live" })
   public void testContainsKey() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, V> map = createMap(context, bucketName);
         assertEventuallyDoesntContainKey(map);
         putString(map, "one", "apple");
         assertEventuallyContainsKey(map);
      } finally {
         returnContainer(bucketName);
      }
   }

   /**
    * containsValue() uses eTag comparison to bucket contents, so this can be subject to eventual
    * consistency problems.
    */
   protected void assertEventuallyContainsValue(final Map<String, V> map, final Object value)
            throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            assert map.containsValue(value);
         }
      });
   }

   protected void assertEventuallyContainsKey(final Map<String, V> map) throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            assert map.containsKey("one");
         }
      });
   }

   protected void assertEventuallyDoesntContainKey(final Map<String, V> map)
            throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            assert !map.containsKey("one");
         }
      });
   }

   @Test(groups = { "integration", "live" })
   public void testIsEmpty() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, V> map = createMap(context, bucketName);
         assertEventuallyEmpty(map);
         putString(map, "one", "apple");
         assertEventuallyNotEmpty(map);
      } finally {
         returnContainer(bucketName);
      }
   }

   protected void assertEventuallyNotEmpty(final Map<String, V> map) throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            assert !map.isEmpty();
         }
      });
   }

   protected void assertEventuallyEmpty(final Map<String, V> map) throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            assert map.isEmpty();
         }
      });
   }

   abstract protected void putString(Map<String, V> map, String key, String value)
            throws InterruptedException, ExecutionException, TimeoutException;

   protected void fourLeftRemovingOne(Map<String, V> map) throws InterruptedException,
            ExecutionException, TimeoutException {
      map.remove("one");
      assertEventuallyMapSize(map, 4);
      assertEventuallyKeySetEquals(map, new TreeSet<String>(ImmutableSet.of("two", "three", "four",
               "five")));
   }

   protected void assertEventuallyMapSize(final Map<String, V> map, final int size)
            throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            assertEquals(map.size(), size);
         }
      });
   }

   @Test(groups = { "integration", "live" })
   public abstract void testPut() throws IOException, InterruptedException, ExecutionException,
            TimeoutException;

   @Test(groups = { "integration", "live" })
   public void testListContainer() throws InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         ListableMap<?, ?, ?> map = (ListableMap<?, ?, ?>) createMap(context, bucketName);
         assertEventuallyListContainer(map, bucketName);
      } finally {
         returnContainer(bucketName);
      }
   }

   protected void assertEventuallyListContainer(final ListableMap<?, ?, ?> map,
            final String bucketName) throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            assertTrue(map.listContainer().size() >= 0);
         }
      });
   }

}