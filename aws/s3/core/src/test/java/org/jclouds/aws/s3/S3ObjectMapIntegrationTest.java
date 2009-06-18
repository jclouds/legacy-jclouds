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
   S3ObjectMap map = null;

   @SuppressWarnings("unchecked")
   protected BaseS3Map<S3Object> createMap(S3Context context, String bucket) {
      map = context.createS3ObjectMap(bucket);
      return (BaseS3Map<S3Object>) map;
   }

   @Override
   @Test(groups = { "integration", "live" })
   public void testValues() throws IOException, InterruptedException {
      putFiveStrings();
      Collection<S3Object> values = map.values();
      assertEventuallyMapSize(5);
      Set<String> valuesAsString = new HashSet<String>();
      for (S3Object object : values) {
         valuesAsString.add(S3Utils.getContentAsStringAndClose(object));
      }
      valuesAsString.removeAll(fiveStrings.values());
      assert valuesAsString.size() == 0;
   }

   @Test(groups = { "integration", "live" })
   public void testRemove() throws IOException, InterruptedException {
      putString("one", "two");
      S3Object old = map.remove("one");
      assertEquals(S3Utils.getContentAsStringAndClose(old), "two");
      old = map.remove("one");
      assert old == S3Object.NOT_FOUND;
      old = map.get("one");
      assert old == S3Object.NOT_FOUND;
      assertEventuallyKeySize(0);
   }

   @Override
   @Test(groups = { "integration", "live" })
   public void testEntrySet() throws IOException, InterruptedException {
      putFiveStrings();
      Set<Entry<String, S3Object>> entries = map.entrySet();
      assertEquals(entries.size(), 5);
      for (Entry<String, S3Object> entry : entries) {
         assertEquals(S3Utils.getContentAsStringAndClose(entry.getValue()), fiveStrings.get(entry
                  .getKey()));
         S3Object value = entry.getValue();
         value.setData("");
         value.generateMd5();
         entry.setValue(value);
      }
      assertEventuallyMapSize(5);
      for (S3Object value : map.values()) {
         assertEquals(S3Utils.getContentAsStringAndClose(value), "");
      }
   }

   @Test(groups = { "integration", "live" })
   public void testContains() throws InterruptedException {
      putString("one", "apple");
      S3Object object = new S3Object("one");
      object.setData("apple");
      assertEventuallyContainsValue(object);
   }

   void getOneReturnsAppleAndOldValueIsNull(S3Object old) throws IOException, InterruptedException {
      assert old == S3Object.NOT_FOUND;
      assertEquals(S3Utils.getContentAsStringAndClose(map.get("one")), "apple");
      assertEventuallyMapSize(1);
   }

   void getOneReturnsBearAndOldValueIsApple(S3Object oldValue) throws IOException,
            InterruptedException {
      assertEquals(S3Utils.getContentAsStringAndClose(map.get("one")), "bear");
      assertEquals(S3Utils.getContentAsStringAndClose(oldValue), "apple");
      assertEventuallyMapSize(1);
   }

   @Test(groups = { "integration", "live" })
   public void testPut() throws IOException, InterruptedException {
      S3Object object = new S3Object("one");
      object.setData(IOUtils.toInputStream("apple"));
      object.generateMd5();
      S3Object old = map.put(object.getKey(), object);
      getOneReturnsAppleAndOldValueIsNull(old);
      object.setData(IOUtils.toInputStream("bear"));
      object.generateMd5();
      S3Object apple = map.put(object.getKey(), object);
      getOneReturnsBearAndOldValueIsApple(apple);
   }

   @Test(groups = { "integration", "live" })
   public void testPutAll() throws InterruptedException {
      Map<String, S3Object> newMap = new HashMap<String, S3Object>();
      for (String key : fiveInputs.keySet()) {
         S3Object object = new S3Object(key);
         object.setData(fiveInputs.get(key));
         object.getMetadata().setSize(fiveBytes.get(key).length);
         newMap.put(key, object);
      }
      map.putAll(newMap);
      assertEventuallyMapSize(5);
      assertEventuallyKeySetEquals(new TreeSet<String>(fiveInputs.keySet()));
      fourLeftRemovingOne();
   }

   @Override
   protected void putString(String key, String value) {
      S3Object object = new S3Object(key);
      object.setData(value);
      map.put(key, object);
   }

   protected void putFiveStrings() {
      Map<String, S3Object> newMap = new HashMap<String, S3Object>();
      for (Map.Entry<String, String> entry : fiveStrings.entrySet()) {
         S3Object object = new S3Object(entry.getKey());
         object.setData(entry.getValue());
         newMap.put(entry.getKey(), object);
      }
      map.putAll(newMap);
   }

}
