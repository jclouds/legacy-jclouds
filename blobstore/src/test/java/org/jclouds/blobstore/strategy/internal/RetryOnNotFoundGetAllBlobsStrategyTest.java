/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.blobstore.strategy.internal;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.integration.StubBlobStoreContextBuilder;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * Tests retry logic.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" }, testName = "blobstore.RetryOnNotFoundGetAllBlobsStrategyTest")
public class RetryOnNotFoundGetAllBlobsStrategyTest {
   private Factory blobProvider;

   @BeforeTest
   void setUp() {
      blobProvider = Guice.createInjector(new BlobStoreObjectModule()).getInstance(
               Blob.Factory.class);
   }

   @SuppressWarnings("unchecked")
   public void testIfNotFoundRetryOtherwiseAddToSet() throws InterruptedException,
            ExecutionException, TimeoutException, IOException {
      Injector context = new StubBlobStoreContextBuilder().buildInjector();
      GetAllBlobsInListAndRetryOnFailure map = context
               .getInstance(GetAllBlobsInListAndRetryOnFailure.class);
      context.getInstance(AsyncBlobStore.class).createContainer("container").get();

      Future<Blob> futureObject = createMock(Future.class);
      Blob object = blobProvider.create(null);
      object.getMetadata().setName("key");
      object.setPayload("goo");
      expect(futureObject.get(map.requestTimeoutMilliseconds, TimeUnit.MILLISECONDS)).andThrow(
               new KeyNotFoundException());
      context.getInstance(AsyncBlobStore.class).putBlob("container", object).get();
      replay(futureObject);
      Set<Blob> objects = new HashSet<Blob>();
      long time = System.currentTimeMillis();
      map.ifNotFoundRetryOtherwiseAddToSet("container", "key", futureObject, objects);
      // should have retried once
      assert System.currentTimeMillis() >= time + map.requestRetryMilliseconds;
      assertEquals(Utils.toStringAndClose((InputStream) objects.iterator().next().getContent()),
               "goo");
      assert !objects.contains(null);
   }

   @SuppressWarnings("unchecked")
   public void testIfNotFoundRetryOtherwiseAddToSetButNeverGetsIt() throws InterruptedException,
            ExecutionException, TimeoutException {
      Injector context = new StubBlobStoreContextBuilder().buildInjector();
      GetAllBlobsInListAndRetryOnFailure map = context
               .getInstance(GetAllBlobsInListAndRetryOnFailure.class);
      context.getInstance(AsyncBlobStore.class).createContainer("container").get();

      Future<Blob> futureObject = createMock(Future.class);
      Blob object = createMock(Blob.class);
      expect(futureObject.get(map.requestTimeoutMilliseconds, TimeUnit.MILLISECONDS)).andThrow(
               new KeyNotFoundException()).atLeastOnce();
      replay(futureObject);
      Set<Blob> objects = new HashSet<Blob>();
      long time = System.currentTimeMillis();
      map.ifNotFoundRetryOtherwiseAddToSet("container", "key1", futureObject, objects);
      // should have retried thrice
      assert System.currentTimeMillis() >= time + map.requestRetryMilliseconds * 3;
      assert !objects.contains(object);
      assert !objects.contains(null);
   }

}