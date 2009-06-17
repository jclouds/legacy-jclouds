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
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.internal.BaseS3Map;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * Tests to cover @{link LiveS3ObjectMap}
 * 
 * @author Adrian Cole
 */
@Test(testName = "s3.S3InputStreamMapIntegrationTest")
public class S3InputStreamMapIntegrationTest extends BaseS3MapIntegrationTest<InputStream> {
   S3InputStreamMap map = null;

   @SuppressWarnings("unchecked")
   protected BaseS3Map<InputStream> createMap(S3Context context, String bucket) {
      map = context.createInputStreamMap(bucket);
      return (BaseS3Map<InputStream>) map;
   }

   @Override
   @Test(groups = { "integration", "live" })
   public void testValues() throws IOException {
      map.putAll(this.fiveInputs);
      Collection<InputStream> values = map.values();
      assertEquals(values.size(), 5);
      Set<String> valuesAsString = new HashSet<String>();
      for (InputStream stream : values) {
         valuesAsString.add(Utils.toStringAndClose(stream));
      }
      valuesAsString.removeAll(fiveStrings.values());
      assert valuesAsString.size() == 0;
   }

   @Test(groups = { "integration", "live" })
   public void testRemove() throws IOException, InterruptedException {
      putString("one", "two");
      InputStream old = map.remove("one");
      assertEquals(Utils.toStringAndClose(old), "two");
      old = map.remove("one");
      assert old == null;
      old = map.get("one");
      assert old == null;
      assertEventuallyKeySize(0);
   }

   @Override
   @Test(groups = { "integration", "live" })
   public void testEntrySet() throws IOException, InterruptedException {
      map.putAllStrings(this.fiveStrings);
      Set<Entry<String, InputStream>> entries = map.entrySet();
      assertEquals(entries.size(), 5);
      for (Entry<String, InputStream> entry : entries) {
         assertEquals(IOUtils.toString(entry.getValue()), fiveStrings.get(entry.getKey()));
         entry.setValue(IOUtils.toInputStream(""));
      }
      assertEventuallyMapSize(5);
      for (InputStream value : map.values()) {
         assertEquals(IOUtils.toString(value), "");
      }
   }

   @Test(groups = { "integration", "live" })
   public void testContainsStringValue() {
      map.putString("one", "apple");
      assert map.containsValue(fiveStrings.get("one"));
   }

   @Test(groups = { "integration", "live" })
   public void testContainsFileValue() {
      map.putString("one", "apple");
      assert map.containsValue(fiveFiles.get("one"));
   }

   @Test(groups = { "integration", "live" })
   public void testContainsInputStreamValue() {
      map.putString("one", "apple");
      assert map.containsValue(this.fiveInputs.get("one"));
   }

   @Test(groups = { "integration", "live" })
   public void testContainsBytesValue() {
      map.putString("one", "apple");
      assert map.containsValue(this.fiveBytes.get("one"));
   }

   @Override
   @Test(groups = { "integration", "live" })
   public void testPutAll() throws InterruptedException {
      map.putAll(this.fiveInputs);
      assertEventuallyMapSize(5);
      assertEventuallyKeySetEquals(new TreeSet<String>(fiveInputs.keySet()));
      fourLeftRemovingOne();
   }

   @Test(groups = { "integration", "live" })
   public void testPutAllBytes() throws InterruptedException {
      map.putAllBytes(this.fiveBytes);
      assertEventuallyMapSize(5);
      assertEventuallyKeySetEquals(new TreeSet<String>(fiveBytes.keySet()));
      fourLeftRemovingOne();
   }

   @Test(groups = { "integration", "live" })
   public void testPutAllFiles() throws InterruptedException {
      map.putAllFiles(this.fiveFiles);
      assertEventuallyMapSize(5);
      assertEventuallyKeySetEquals(new TreeSet<String>(fiveFiles.keySet()));
      fourLeftRemovingOne();
   }

   @Test(groups = { "integration", "live" })
   public void testPutAllStrings() throws InterruptedException {
      map.putAllStrings(this.fiveStrings);
      assertEventuallyMapSize(5);
      assertEventuallyKeySetEquals(new TreeSet<String>(fiveStrings.keySet()));
      fourLeftRemovingOne();
   }

   @Test(groups = { "integration", "live" })
   public void testPutString() throws IOException, InterruptedException {
      InputStream old = map.putString("one", "apple");
      getOneReturnsAppleAndOldValueIsNull(old);
      InputStream apple = map.putString("one", "bear");
      getOneReturnsBearAndOldValueIsApple(apple);
   }

   void getOneReturnsAppleAndOldValueIsNull(InputStream old) throws IOException,
            InterruptedException {
      assert old == null;
      assertEquals(Utils.toStringAndClose(map.get("one")), "apple");
      assertEventuallyMapSize(1);
   }

   void getOneReturnsBearAndOldValueIsApple(InputStream oldValue) throws IOException,
            InterruptedException {
      assertEquals(Utils.toStringAndClose(map.get("one")), "bear");
      assertEquals(Utils.toStringAndClose(oldValue), "apple");
      assertEventuallyMapSize(1);
   }

   @Test(groups = { "integration", "live" })
   public void testPutFile() throws IOException, InterruptedException {
      InputStream old = map.putFile("one", fiveFiles.get("one"));
      getOneReturnsAppleAndOldValueIsNull(old);
      InputStream apple = map.putFile("one", fiveFiles.get("two"));
      getOneReturnsBearAndOldValueIsApple(apple);
   }

   @Test(groups = { "integration", "live" })
   public void testPutBytes() throws IOException, InterruptedException {
      InputStream old = map.putBytes("one", "apple".getBytes());
      getOneReturnsAppleAndOldValueIsNull(old);
      InputStream apple = map.putBytes("one", "bear".getBytes());
      getOneReturnsBearAndOldValueIsApple(apple);
   }

   @Test(groups = { "integration", "live" })
   public void testPut() throws IOException, InterruptedException {
      InputStream old = map.put("one", IOUtils.toInputStream("apple"));
      getOneReturnsAppleAndOldValueIsNull(old);
      InputStream apple = map.put("one", IOUtils.toInputStream("bear"));
      getOneReturnsBearAndOldValueIsApple(apple);
   }

   @Override
   protected void putString(String key, String value) {
      map.putString(key, value);
   }

}
