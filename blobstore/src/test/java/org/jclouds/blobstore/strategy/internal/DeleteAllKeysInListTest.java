/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.blobstore.strategy.internal;

import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.util.Throwables2;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.Closeables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(testName = "DeleteAllKeysInListTest", singleThreaded = true)
public class DeleteAllKeysInListTest {
   private BlobStore blobstore;
   private DeleteAllKeysInList deleter;
   private static final String containerName = "container";
   private static final String directoryName = "directory";

   @BeforeMethod
   void setupBlobStore() {
      Injector injector = ContextBuilder.newBuilder("transient").buildInjector();
      blobstore = injector.getInstance(BlobStore.class);
      deleter = injector.getInstance(DeleteAllKeysInList.class);
      createDataSet();
   }

   @AfterMethod
   void close() {
      Closeables.closeQuietly(blobstore.getContext());
   }

   public void testExecuteWithoutOptionsClearsRecursively() {
      deleter.execute(containerName);
      assertEquals(blobstore.countBlobs(containerName), 0);
   }

   public void testExecuteRecursive() {
      deleter.execute(containerName, ListContainerOptions.Builder.recursive());
      assertEquals(blobstore.countBlobs(containerName), 0);
   }

   public void testExecuteNonRecursive() {
      deleter.execute(containerName, ListContainerOptions.NONE);
      assertEquals(blobstore.countBlobs(containerName), 2222);
   }

   public void testExecuteInDirectory() {
      deleter.execute(containerName, ListContainerOptions.Builder.inDirectory(directoryName));
      assertEquals(blobstore.countBlobs(containerName), 1111);
   }

   public void testListTimeoutException() throws Exception {
      ListenableFuture<PageSet<? extends StorageMetadata>> future = createMock(ListenableFuture.class);
      expect(future.get(anyLong(), anyObject(TimeUnit.class))).andThrow(new RuntimeException(new TimeoutException()));
      expect(future.cancel(true)).andReturn(true);
      replay(future);

      AsyncBlobStore asyncBlobStore = createMock(AsyncBlobStore.class);
      expect(asyncBlobStore.list(anyObject(String.class), anyObject(ListContainerOptions.class))).andReturn(future);
      replay(asyncBlobStore);

      deleter = new DeleteAllKeysInList(null, asyncBlobStore, null);
      try {
         deleter.execute(containerName, ListContainerOptions.NONE);
         fail();
      } catch (Exception e) {
         if (Throwables2.getFirstThrowableOfType(e, TimeoutException.class) == null) {
            throw e;
         }
      }
   }

   /**
    * Create a container "container" with 1111 blobs named "blob-%d".  Create a
    * subdirectory "directory" which contains 2222 more blobs named
    * "directory/blob-%d".
    */
   private void createDataSet() {
      String blobNameFmt = "blob-%d";
      String directoryBlobNameFmt = "%s/blob-%d";

      blobstore.createContainerInLocation(null, containerName);
      for (int i = 0; i < 1111; i++) {
         String blobName = String.format(blobNameFmt, i);
         blobstore.putBlob(containerName, blobstore.blobBuilder(blobName).payload(blobName).build());
      }
      for (int i = 0; i < 2222; i++) {
         String directoryBlobName = String.format(directoryBlobNameFmt, directoryName, i);
         blobstore.putBlob(containerName, blobstore.blobBuilder(directoryBlobName).payload(directoryBlobName).build());
      }
      assertEquals(blobstore.countBlobs(containerName), 3333);
   }
}
