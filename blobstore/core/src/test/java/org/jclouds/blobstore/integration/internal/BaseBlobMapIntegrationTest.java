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

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.testng.annotations.Test;

/**
 * Tests to cover @{link LiveS3ObjectMap}
 * 
 * @author Adrian Cole
 */
public class BaseBlobMapIntegrationTest<S, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         extends BaseMapIntegrationTest<S, C, M, B, B> {

   @Override
   @Test(groups = { "integration", "live" })
   public void testValues() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, B> map = createMap(context, bucketName);

         putFiveStrings(map);
         Collection<B> values = map.values();
         assertEventuallyMapSize(map, 5);
         Set<String> valuesAsString = new HashSet<String>();
         for (B object : values) {
            valuesAsString.add(BlobStoreUtils.getContentAsStringAndClose(object));
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
         Map<String, B> map = createMap(context, bucketName);
         putString(map, "one", "two");
         assertEventuallyContentEquals(map, "one", "two");
         // TODO track how often this occurs and potentially update map implementation
         assertEventuallyRemoveEquals(map, "one", null);
         assertEventuallyGetEquals(map, "one", null);
         assertEventuallyKeySize(map, 0);
      } finally {
         returnContainer(bucketName);
      }
   }

   private void assertEventuallyContentEquals(final Map<String, B> map, final String key,
            final String value) throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            B old = map.remove(key);
            try {
               assertEquals(BlobStoreUtils.getContentAsStringAndClose(old), value);
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
         }
      });
   }

   @Override
   @Test(groups = { "integration", "live" })
   public void testEntrySet() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, B> map = createMap(context, bucketName);
         putFiveStrings(map);
         Set<Entry<String, B>> entries = map.entrySet();
         assertEquals(entries.size(), 5);
         for (Entry<String, B> entry : entries) {
            assertEquals(fiveStrings.get(entry.getKey()), BlobStoreUtils
                     .getContentAsStringAndClose(entry.getValue()));
            B value = entry.getValue();
            value.setData("");
            value.generateMD5();
            entry.setValue(value);
         }
         assertEventuallyMapSize(map, 5);
         for (B value : map.values()) {
            assertEquals(BlobStoreUtils.getContentAsStringAndClose(value), "");
         }
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testContains() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, B> map = createMap(context, bucketName);
         putString(map, "one", "apple");
         B object = context.newBlob("one");
         object.setData("apple");
         assertEventuallyContainsValue(map, object);
      } finally {
         returnContainer(bucketName);
      }
   }

   void getOneReturnsAppleAndOldValueIsNull(Map<String, B> map, B old) throws IOException,
            InterruptedException {
      assert old == null;
      assertEquals(BlobStoreUtils.getContentAsStringAndClose(map.get("one")), "apple");
      assertEventuallyMapSize(map, 1);
   }

   void getOneReturnsBearAndOldValueIsApple(Map<String, B> map, B oldValue) throws IOException,
            InterruptedException {
      assertEquals(BlobStoreUtils.getContentAsStringAndClose(map.get("one")), "bear");
      assertEquals(BlobStoreUtils.getContentAsStringAndClose(oldValue), "apple");
      assertEventuallyMapSize(map, 1);
   }

   @Test(groups = { "integration", "live" })
   public void testPut() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, B> map = createMap(context, bucketName);
         B object = context.newBlob("one");
         object.setData(IOUtils.toInputStream("apple"));
         object.generateMD5();
         B old = map.put(object.getName(), object);
         getOneReturnsAppleAndOldValueIsNull(map, old);
         object.setData(IOUtils.toInputStream("bear"));
         object.generateMD5();
         B apple = map.put(object.getName(), object);
         getOneReturnsBearAndOldValueIsApple(map, apple);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPutAll() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         Map<String, B> map = createMap(context, bucketName);
         Map<String, B> newMap = new HashMap<String, B>();
         for (String key : fiveInputs.keySet()) {
            B object = context.newBlob(key);
            object.setData(fiveInputs.get(key));
            object.getMetadata().setSize(fiveBytes.get(key).length);
            newMap.put(key, object);
         }
         map.putAll(newMap);
         assertEventuallyMapSize(map, 5);
         assertEventuallyKeySetEquals(map, new TreeSet<String>(fiveInputs.keySet()));
         fourLeftRemovingOne(map);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Override
   protected void putString(Map<String, B> map, String key, String value) {
      B object = context.newBlob(key);
      object.setData(value);
      map.put(key, object);
   }

   protected void putFiveStrings(Map<String, B> map) {
      Map<String, B> newMap = new HashMap<String, B>();
      for (Map.Entry<String, String> entry : fiveStrings.entrySet()) {
         B object = context.newBlob(entry.getKey());
         object.setData(entry.getValue());
         newMap.put(entry.getKey(), object);
      }
      map.putAll(newMap);
   }

   @SuppressWarnings("unchecked")
   protected Map<String, B> createMap(BlobStoreContext context, String bucket) {
      return context.createBlobMap(bucket);
   }

}
