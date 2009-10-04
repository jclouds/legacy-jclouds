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
package org.jclouds.blobstore.strategy.internal;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.integration.StubBlobStoreContextBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * 
 * Tests retry logic.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" }, testName = "blobstore.RetryOnNotFoundGetAllBlobsStrategyTest")
public class RetryOnNotFoundGetAllBlobsStrategyTest {

   Injector context;

   RetryOnNotFoundGetAllBlobsStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> map;

   @BeforeClass
   void addDefaultObjectsSoThatTestsWillPass() {
      context = new StubBlobStoreContextBuilder().buildInjector();
      map = context
               .getInstance(Key
                        .get(new TypeLiteral<RetryOnNotFoundGetAllBlobsStrategy<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
                        }));
   }

   @SuppressWarnings("unchecked")
   public void testIfNotFoundRetryOtherwiseAddToSet() throws InterruptedException,
            ExecutionException, TimeoutException {
      Future<Blob<BlobMetadata>> futureObject = createMock(Future.class);
      Blob<BlobMetadata> object = new Blob<BlobMetadata>("key");
      expect(futureObject.get(map.requestTimeoutMilliseconds, TimeUnit.MILLISECONDS)).andThrow(
               new KeyNotFoundException());
      expect(futureObject.get(map.requestTimeoutMilliseconds, TimeUnit.MILLISECONDS)).andReturn(
               object);
      replay(futureObject);
      Set<Blob<BlobMetadata>> objects = new HashSet<Blob<BlobMetadata>>();
      long time = System.currentTimeMillis();
      map.ifNotFoundRetryOtherwiseAddToSet("key", futureObject, objects);
      // should have retried once
      assert System.currentTimeMillis() >= time + map.requestRetryMilliseconds;
      assert objects.contains(object);
      assert !objects.contains(null);
   }

   @SuppressWarnings("unchecked")
   public void testIfNotFoundRetryOtherwiseAddToSetButNeverGetsIt() throws InterruptedException,
            ExecutionException, TimeoutException {
      Future<Blob<BlobMetadata>> futureObject = createMock(Future.class);
      Blob object = createNiceMock(Blob.class);
      expect(futureObject.get(map.requestTimeoutMilliseconds, TimeUnit.MILLISECONDS)).andThrow(
               new KeyNotFoundException()).atLeastOnce();
      replay(futureObject);
      Set<Blob<BlobMetadata>> objects = new HashSet<Blob<BlobMetadata>>();
      long time = System.currentTimeMillis();
      map.ifNotFoundRetryOtherwiseAddToSet("key", futureObject, objects);
      // should have retried thrice
      assert System.currentTimeMillis() >= time + map.requestRetryMilliseconds * 3;

      assert !objects.contains(object);
      assert !objects.contains(null);
   }

}