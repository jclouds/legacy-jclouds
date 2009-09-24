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
package org.jclouds.blobstore;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.testng.annotations.Test;

/**
 * 
 * Tests retry logic.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" }, testName = "blobstore.BaseBlobMapTest")
public abstract class BaseBlobMapTest<C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>, V> {

   class MockBaseBlobMap extends BaseBlobMap<C, M, B, String> {

      @SuppressWarnings("unchecked")
      public MockBaseBlobMap(String container) {
         super(createNiceMock(BlobStore.class), container);
      }

      public Set<java.util.Map.Entry<String, String>> entrySet() {
         return null;
      }

      public String get(Object key) {
         return null;
      }

      public String put(String key, String value) {
         return null;
      }

      public void putAll(Map<? extends String, ? extends String> t) {

      }

      public String remove(Object key) {
         return null;
      }

      public Collection<String> values() {
         return null;
      }

   }

   @SuppressWarnings("unchecked")
   public void testIfNotFoundRetryOtherwiseAddToSet() throws InterruptedException,
            ExecutionException, TimeoutException {
      BaseBlobMap<C, M, B, String> map = new MockBaseBlobMap("test");
      Future<B> futureObject = createMock(Future.class);
      B object = createBlob();
      expect(futureObject.get(map.requestTimeoutMilliseconds, TimeUnit.MILLISECONDS)).andThrow(
               new KeyNotFoundException());
      expect(futureObject.get(map.requestTimeoutMilliseconds, TimeUnit.MILLISECONDS)).andReturn(
               object);
      replay(futureObject);
      Set<B> objects = new HashSet<B>();
      long time = System.currentTimeMillis();
      map.ifNotFoundRetryOtherwiseAddToSet("key", futureObject, objects);
      // should have retried once
      assert System.currentTimeMillis() >= time + map.requestRetryMilliseconds;
      assert objects.contains(object);
      assert !objects.contains(null);
   }

   protected abstract B createBlob();

   @SuppressWarnings("unchecked")
   public void testIfNotFoundRetryOtherwiseAddToSetButNeverGetsIt() throws InterruptedException,
            ExecutionException, TimeoutException {
      BaseBlobMap<C, M, B, String> map = new MockBaseBlobMap("test");
      Future<B> futureObject = createMock(Future.class);
      Blob object = createNiceMock(Blob.class);
      expect(futureObject.get(map.requestTimeoutMilliseconds, TimeUnit.MILLISECONDS)).andThrow(
               new KeyNotFoundException()).atLeastOnce();
      replay(futureObject);
      Set<B> objects = new HashSet<B>();
      long time = System.currentTimeMillis();
      map.ifNotFoundRetryOtherwiseAddToSet("key", futureObject, objects);
      // should have retried thrice
      assert System.currentTimeMillis() >= time + map.requestRetryMilliseconds * 3;

      assert !objects.contains(object);
      assert !objects.contains(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testBlankContainerName() {
      new MockBaseBlobMap("test");
   }
}