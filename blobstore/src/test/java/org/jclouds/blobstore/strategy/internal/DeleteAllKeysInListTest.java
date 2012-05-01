/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.blobstore.strategy.internal;

import static org.testng.Assert.assertEquals;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.io.Closeables;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(testName = "DeleteAllKeysInListTest", singleThreaded = true)
public class DeleteAllKeysInListTest {
   private BlobStore blobstore;
   private DeleteAllKeysInList deleter;

   @BeforeClass
   void setupBlobStore() {
      Injector injector = ContextBuilder.newBuilder("transient").buildInjector();
      blobstore = injector.getInstance(BlobStore.class);
      deleter = injector.getInstance(DeleteAllKeysInList.class);
   }

   public void testExecuteWithoutOptionsClearsRecursively() {
      blobstore.createContainerInLocation(null, "goodies");
      for (int i = 0; i < 1001; i++) {
         blobstore.putBlob("goodies", blobstore.blobBuilder(i + "").payload(i + "").build());
      }
      assertEquals(blobstore.countBlobs("goodies"), 1001);
      deleter.execute("goodies");
      assertEquals(blobstore.countBlobs("goodies"), 0);
   }

   public void testExecuteNonRecursive() {
      blobstore.createContainerInLocation(null, "foo");
      for (int i = 0; i < 1001; i++) {
         blobstore.putBlob("foo", blobstore.blobBuilder(i + "").payload(i + "").build());
      }
      for (int i = 0; i < 1001; i++) {
         blobstore.putBlob("foo", blobstore.blobBuilder("dir/" + i + "").payload(i + "").build());
      }
      assertEquals(blobstore.countBlobs("foo"), 2002);
      deleter.execute("foo", ListContainerOptions.Builder.inDirectory("dir"));
      assertEquals(blobstore.countBlobs("foo"), 1001);
   }

   @AfterClass
   void close() {
      if (blobstore != null)
         Closeables.closeQuietly(blobstore.getContext());
   }
}
