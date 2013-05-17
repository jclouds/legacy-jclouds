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

import java.io.IOException;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class BiggerThanPageSizeTest {
   private BlobStore blobstore;

   @BeforeTest
   void setupBlobStore() {
      blobstore =  ContextBuilder.newBuilder("transient").build(BlobStoreContext.class).getBlobStore();
   }

   public void test() throws IOException {
      blobstore.createContainerInLocation(null, "goodies");
      for (int i = 0; i < 1001; i++) {
         blobstore.putBlob("goodies", blobstore.blobBuilder(i + "").payload(i + "").build());
      }
      assertEquals(blobstore.countBlobs("goodies"), 1001);
      blobstore.clearContainer("goodies");
      assertEquals(blobstore.countBlobs("goodies"), 0);
   }

   public void testStrategies() throws IOException {
      blobstore.createContainerInLocation(null, "poo");
      for (int i = 0; i < 1001; i++) {
         blobstore.putBlob("poo", blobstore.blobBuilder(i + "").payload(i + "").build());
      }

      ListContainerAndRecurseThroughFolders lister = new ListContainerAndRecurseThroughFolders(
            new ConcatenateContainerLists(blobstore));
      assertEquals(lister.execute("poo", ListContainerOptions.NONE).size(), 1001);
      blobstore.clearContainer("poo");
      assertEquals(lister.execute("poo", ListContainerOptions.NONE).size(), 0);
   }

}
