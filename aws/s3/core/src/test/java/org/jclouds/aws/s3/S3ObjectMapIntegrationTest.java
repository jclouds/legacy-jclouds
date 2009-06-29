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
package org.jclouds.aws.s3;

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
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.internal.BaseS3Map;
import org.jclouds.aws.s3.util.S3Utils;
import org.testng.annotations.Test;

/**
 * Tests to cover @{link LiveS3ObjectMap}
 * 
 * @author Adrian Cole
 */
@Test(testName = "s3.S3ObjectMapIntegrationTest")
public class S3ObjectMapIntegrationTest extends BaseS3MapIntegrationTest<S3Object> {

   @SuppressWarnings("unchecked")
   protected BaseS3Map<S3Object> createMap(S3Context context, String bucket) {
      S3ObjectMap map = context.createS3ObjectMap(bucket);
      return (BaseS3Map<S3Object>) map;
   }

   @Override
   @Test(groups = { "integration", "live" })
   public void testValues() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getBucketName();
      try {
         BaseS3Map<S3Object> map = createMap(context, bucketName);

         putFiveStrings(map);
         Collection<S3Object> values = map.values();
         assertEventuallyMapSize(map, 5);
         Set<String> valuesAsString = new HashSet<String>();
         for (S3Object object : values) {
            valuesAsString.add(S3Utils.getContentAsStringAndClose(object));
         }
         valuesAsString.removeAll(fiveStrings.values());
         assert valuesAsString.size() == 0;
      } finally {
         returnBucket(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testRemove() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getBucketName();
      try {
         BaseS3Map<S3Object> map = createMap(context, bucketName);
         putString(map, "one", "two");
         assertEventuallyContentEquals(map, "one", "two");
         // TODO track how often this occurs and potentially update map implementation
         assertEventuallyRemoveEquals(map, "one", S3Object.NOT_FOUND);
         assertEventuallyGetEquals(map, "one", S3Object.NOT_FOUND);
         assertEventuallyKeySize(map, 0);
      } finally {
         returnBucket(bucketName);
      }
   }

   private void assertEventuallyContentEquals(final BaseS3Map<S3Object> map, final String key,
            final String value) throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            S3Object old = map.remove(key);
            try {
               assertEquals(S3Utils.getContentAsStringAndClose(old), value);
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
      String bucketName = getBucketName();
      try {
         BaseS3Map<S3Object> map = createMap(context, bucketName);
         putFiveStrings(map);
         Set<Entry<String, S3Object>> entries = map.entrySet();
         assertEquals(entries.size(), 5);
         for (Entry<String, S3Object> entry : entries) {
            assertEquals(S3Utils.getContentAsStringAndClose(entry.getValue()), fiveStrings
                     .get(entry.getKey()));
            S3Object value = entry.getValue();
            value.setData("");
            value.generateMd5();
            entry.setValue(value);
         }
         assertEventuallyMapSize(map, 5);
         for (S3Object value : map.values()) {
            assertEquals(S3Utils.getContentAsStringAndClose(value), "");
         }
      } finally {
         returnBucket(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testContains() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getBucketName();
      try {
         BaseS3Map<S3Object> map = createMap(context, bucketName);
         putString(map, "one", "apple");
         S3Object object = new S3Object("one");
         object.setData("apple");
         assertEventuallyContainsValue(map, object);
      } finally {
         returnBucket(bucketName);
      }
   }

   void getOneReturnsAppleAndOldValueIsNull(BaseS3Map<S3Object> map, S3Object old)
            throws IOException, InterruptedException {
      assert old == S3Object.NOT_FOUND;
      assertEquals(S3Utils.getContentAsStringAndClose(map.get("one")), "apple");
      assertEventuallyMapSize(map, 1);
   }

   void getOneReturnsBearAndOldValueIsApple(BaseS3Map<S3Object> map, S3Object oldValue)
            throws IOException, InterruptedException {
      assertEquals(S3Utils.getContentAsStringAndClose(map.get("one")), "bear");
      assertEquals(S3Utils.getContentAsStringAndClose(oldValue), "apple");
      assertEventuallyMapSize(map, 1);
   }

   @Test(groups = { "integration", "live" })
   public void testPut() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getBucketName();
      try {
         BaseS3Map<S3Object> map = createMap(context, bucketName);
         S3Object object = new S3Object("one");
         object.setData(IOUtils.toInputStream("apple"));
         object.generateMd5();
         S3Object old = map.put(object.getKey(), object);
         getOneReturnsAppleAndOldValueIsNull(map, old);
         object.setData(IOUtils.toInputStream("bear"));
         object.generateMd5();
         S3Object apple = map.put(object.getKey(), object);
         getOneReturnsBearAndOldValueIsApple(map, apple);
      } finally {
         returnBucket(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPutAll() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getBucketName();
      try {
         BaseS3Map<S3Object> map = createMap(context, bucketName);
         Map<String, S3Object> newMap = new HashMap<String, S3Object>();
         for (String key : fiveInputs.keySet()) {
            S3Object object = new S3Object(key);
            object.setData(fiveInputs.get(key));
            object.getMetadata().setSize(fiveBytes.get(key).length);
            newMap.put(key, object);
         }
         map.putAll(newMap);
         assertEventuallyMapSize(map, 5);
         assertEventuallyKeySetEquals(map, new TreeSet<String>(fiveInputs.keySet()));
         fourLeftRemovingOne(map);
      } finally {
         returnBucket(bucketName);
      }
   }

   @Override
   protected void putString(BaseS3Map<S3Object> map, String key, String value) {
      S3Object object = new S3Object(key);
      object.setData(value);
      map.put(key, object);
   }

   protected void putFiveStrings(BaseS3Map<S3Object> map) {
      Map<String, S3Object> newMap = new HashMap<String, S3Object>();
      for (Map.Entry<String, String> entry : fiveStrings.entrySet()) {
         S3Object object = new S3Object(entry.getKey());
         object.setData(entry.getValue());
         newMap.put(entry.getKey(), object);
      }
      map.putAll(newMap);
   }

}
