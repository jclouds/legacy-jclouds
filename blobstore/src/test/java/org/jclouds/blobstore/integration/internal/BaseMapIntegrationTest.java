/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ListableMap;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public abstract class BaseMapIntegrationTest<A, S, V> extends BaseBlobStoreIntegrationTest<A, S> {

   public abstract void testPutAll() throws InterruptedException, ExecutionException,
            TimeoutException;

   public abstract void testEntrySet() throws IOException, InterruptedException,
            ExecutionException, TimeoutException;

   public abstract void testValues() throws IOException, InterruptedException, ExecutionException,
            TimeoutException;

   protected Map<String, byte[]> fiveBytes = Maps.transformValues(fiveStrings,
            new Function<String, byte[]>() {
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
            return IOUtils.toInputStream(from);
         }
      });
   }

   @BeforeClass(groups = { "integration", "live" })
   @Parameters( { "basedir" })
   protected void setUpTempDir(@Optional String basedir) throws InterruptedException,
            ExecutionException, FileNotFoundException, IOException, TimeoutException {
      if (basedir == null) {
         basedir = System.getProperty("java.io.tmpdir");
      }
      tmpDirectory = basedir + File.separator + "target" + File.separator + "testFiles"
               + File.separator + getClass().getSimpleName();
      new File(tmpDirectory).mkdirs();
      fiveFiles = Maps.newHashMap();
      for (Entry<String, String> entry : fiveStrings.entrySet()) {
         File file = new File(tmpDirectory, entry.getKey());
         IOUtils.write(entry.getValue().getBytes(), new FileOutputStream(file));
         fiveFiles.put(entry.getKey(), file);
      }
   }

   protected abstract Map<String, V> createMap(BlobStoreContext<?, ?> context, String bucket);

   @Test(groups = { "integration", "live" })
   public void testClear() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, V> map = createMap(context, bucketName);
         assertConsistencyAwareMapSize(map, 0);
         putStringWithMD5(map, "one", "apple");
         assertConsistencyAwareMapSize(map, 1);
         map.clear();
         assertConsistencyAwareMapSize(map, 0);
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
         assertConsistencyAwareKeySize(map, 0);
         putStringWithMD5(map, "one", "two");
         assertConsistencyAwareKeySize(map, 1);
         assertConsistencyAwareKeySetEquals(map, ImmutableSet.of("one"));
      } finally {
         returnContainer(bucketName);
      }
   }

   protected void assertConsistencyAwareKeySetEquals(final Map<String, V> map, final Object toEqual)
            throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assertEquals(map.keySet(), toEqual);
         }
      });
   }

   protected void assertConsistencyAwareRemoveEquals(final Map<String, V> map, final String key,
            final Object equals) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assertEquals(map.remove(key), equals);
         }
      });
   }

   protected void assertConsistencyAwareGetEquals(final Map<String, V> map, final String key,
            final Object equals) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assertEquals(map.get(key), equals);
         }
      });
   }

   protected void assertConsistencyAwareKeySize(final Map<String, V> map, final int size)
            throws InterruptedException {
      assertConsistencyAware(new Runnable() {
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
         assertConsistencyAwareDoesntContainKey(map);
         putStringWithMD5(map, "one", "apple");
         assertConsistencyAwareContainsKey(map);
      } finally {
         returnContainer(bucketName);
      }
   }

   /**
    * containsValue() uses eTag comparison to bucket contents, so this can be subject to eventual
    * consistency problems.
    */
   protected void assertConsistencyAwareContainsValue(final Map<String, V> map, final Object value)
            throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assert map.containsValue(value);
         }
      });
   }

   protected void assertConsistencyAwareContainsKey(final Map<String, V> map)
            throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assert map.containsKey("one");
         }
      });
   }

   protected void assertConsistencyAwareDoesntContainKey(final Map<String, V> map)
            throws InterruptedException {
      assertConsistencyAware(new Runnable() {
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
         assertConsistencyAwareEmpty(map);
         putStringWithMD5(map, "one", "apple");
         assertConsistencyAwareNotEmpty(map);
      } finally {
         returnContainer(bucketName);
      }
   }

   protected void assertConsistencyAwareNotEmpty(final Map<String, V> map)
            throws InterruptedException {
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

   abstract protected void putStringWithMD5(Map<String, V> map, String key, String value)
            throws InterruptedException, ExecutionException, TimeoutException;

   protected void fourLeftRemovingOne(Map<String, V> map) throws InterruptedException {
      map.remove("one");
      assertConsistencyAwareMapSize(map, 4);
      assertConsistencyAwareKeySetEquals(map, new TreeSet<String>(ImmutableSet.of("two", "three",
               "four", "five")));
   }

   protected void assertConsistencyAwareMapSize(final Map<String, V> map, final int size)
            throws InterruptedException {
      assertConsistencyAware(new Runnable() {
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
         ListableMap<?, ?> map = (ListableMap<?, ?>) createMap(context, bucketName);
         assertConsistencyAwareListContainer(map, bucketName);
      } finally {
         returnContainer(bucketName);
      }
   }

   protected void assertConsistencyAwareListContainer(final ListableMap<?, ?> map,
            final String bucketName) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            assertTrue(map.list().size() >= 0);
         }
      });
   }

}