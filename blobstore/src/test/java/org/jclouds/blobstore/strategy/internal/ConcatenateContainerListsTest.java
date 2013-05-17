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

import static org.testng.Assert.assertEquals;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.io.Closeables;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(testName = "ConcatenateContainerListsTest", singleThreaded = true)
public class ConcatenateContainerListsTest {
   private BlobStore blobstore;
   private ConcatenateContainerLists concatter;

   @BeforeClass
   void setupBlobStore() {
      Injector injector = ContextBuilder.newBuilder("transient").buildInjector();
      blobstore = injector.getInstance(BlobStore.class);
      concatter = injector.getInstance(ConcatenateContainerLists.class);
   }

   public void testLargerThanOnePageNoOptions() {
      blobstore.createContainerInLocation(null, "goodies");
      for (int i = 0; i < 1001; i++) {
         blobstore.putBlob("goodies", blobstore.blobBuilder(i + "").payload(i + "").build());
      }
      Iterable<? extends StorageMetadata> listing = concatter.execute("goodies", new ListContainerOptions());
      assertEquals(Iterables.size(listing), 1001);
   }

   public void testLargerThanOnePageInDirAndRecursive() {
      blobstore.createContainerInLocation(null, "foo");
      for (int i = 0; i < 1001; i++) {
         blobstore.putBlob("foo", blobstore.blobBuilder(i + "").payload(i + "").build());
      }
      for (int i = 0; i < 1001; i++) {
         blobstore.putBlob("foo", blobstore.blobBuilder("dir/" + i + "").payload(i + "").build());
      }
      Iterable<? extends StorageMetadata> listing = concatter.execute("foo", new ListContainerOptions());
      // TODO: this looks broke.  seems we should have 1002 (1001 + directory foo), not 1003
      assertEquals(Iterables.size(listing), 1003);
      listing = concatter.execute("foo", ListContainerOptions.Builder.inDirectory("dir"));
      assertEquals(Iterables.size(listing), 1001);
      listing = concatter.execute("foo", ListContainerOptions.Builder.recursive());
      assertEquals(Iterables.size(listing), 2002);
   }

   @AfterClass
   void close() {
      if (blobstore != null)
         Closeables.closeQuietly(blobstore.getContext());
   }
}
