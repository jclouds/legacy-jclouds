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
import static org.testng.Assert.assertEquals;

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
import org.jclouds.blobstore.integration.StubBlobStoreContextBuilder;
import org.jclouds.blobstore.internal.InputStreamMapImpl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * 
 * Tests retry logic.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" }, testName = "blobstore.BaseBlobMapTest")
public class BaseBlobMapTest {

   BlobStoreContext<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> context;

   InputStreamMapImpl<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> map;

   @SuppressWarnings("unchecked")
   @BeforeClass
   void addDefaultObjectsSoThatTestsWillPass() {
      context = new StubBlobStoreContextBuilder().buildContext();
      map = (InputStreamMapImpl<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>) context
               .createInputStreamMap("test");
   }

   @SuppressWarnings("unchecked")
   public void testTypes() {
      TypeLiteral type0 = new TypeLiteral<Map<String, Map<String, Blob<BlobMetadata>>>>() {
      };
      TypeLiteral type1 = TypeLiteral.get(Types.newParameterizedType(Map.class, String.class, Types
               .newParameterizedType(Map.class, String.class, Types.newParameterizedType(
                        Blob.class, BlobMetadata.class))));
      assertEquals(type0, type1);

      TypeLiteral type2 = new TypeLiteral<BlobMap.Factory<BlobMetadata, Blob<BlobMetadata>>>() {
      };
      TypeLiteral type3 = TypeLiteral.get(Types.newParameterizedTypeWithOwner(BlobMap.class,
               BlobMap.Factory.class, BlobMetadata.class, Types.newParameterizedType(Blob.class,
                        BlobMetadata.class)));

      assertEquals(type2, type3);

      TypeLiteral type4 = new TypeLiteral<Blob<BlobMetadata>>() {
      };
      TypeLiteral type5 = TypeLiteral.get(Types
               .newParameterizedType(Blob.class, BlobMetadata.class));

      assertEquals(type4, type5);

      TypeLiteral type6 = new TypeLiteral<BlobStoreContextImpl<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
      };

      TypeLiteral type7 = TypeLiteral.get(Types.newParameterizedType(BlobStoreContextImpl.class,
               Types.newParameterizedType(BlobStore.class, ContainerMetadata.class,
                        BlobMetadata.class, Types.newParameterizedType(Blob.class,
                                 BlobMetadata.class)), ContainerMetadata.class, BlobMetadata.class,
               Types.newParameterizedType(Blob.class, BlobMetadata.class)));
      assertEquals(type6, type7);

   }

   @SuppressWarnings("unchecked")
   public void testIfNotFoundRetryOtherwiseAddToSet() throws InterruptedException,
            ExecutionException, TimeoutException {
      Future<Blob<BlobMetadata>> futureObject = createMock(Future.class);
      Blob<BlobMetadata> object = context.newBlob("key");
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