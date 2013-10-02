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
package org.jclouds.blobstore;

import static org.easymock.EasyMock.createMock;
import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.easymock.EasyMock;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.options.ListAllOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

@Test(singleThreaded = true, testName = "BlobStoresTest")
public class BlobStoresTest {

   private final String containerName = "mycontainer";

   @Test(expectedExceptions = { ContainerNotFoundException.class })
   public void testListAllForUnknownContainerFromTransientBlobStoreEagerly() throws Exception {
      ListContainerOptions containerOptions = ListContainerOptions.NONE;
      ListAllOptions listAllOptions = ListAllOptions.Builder.eager(true);
      BlobStoreContext context = blobStoreContext();
      try {
         BlobStore blobStore = context.getBlobStore();
         BlobStores.listAll(blobStore, "wrongcontainer", containerOptions, listAllOptions);
      } finally {
         context.close();
      }
   }

   /**
    * Default listAll is not eager, so test that exception is thrown when first attempt to iterate.
    */
   @Test(expectedExceptions = { ContainerNotFoundException.class })
   public void testListAllForUnknownContainerFromTransientBlobStore() throws Exception {
      ListContainerOptions options = ListContainerOptions.NONE;
      BlobStoreContext context = blobStoreContext();
      try {
         BlobStore blobStore = context.getBlobStore();
         Iterable<StorageMetadata> iterable = BlobStores.listAll(blobStore, "wrongcontainer", options);
         iterable.iterator().hasNext();
      } finally {
         context.close();
      }
   }

   protected BlobStoreContext blobStoreContext() {
      return ContextBuilder.newBuilder("transient").build(BlobStoreContext.class);
   }

   @Test
   public void testListAllFromTransientBlobStore() throws Exception {
      runListAllFromTransientBlobStore(false);
   }

   @Test
   public void testListAllFromTransientBlobStoreEagerly() throws Exception {
      runListAllFromTransientBlobStore(true);
   }

   private void runListAllFromTransientBlobStore(boolean eager) throws Exception {
      final int numTimesToIterate = 2;
      final int NUM_BLOBS = 31;
      ListContainerOptions containerOptions = ListContainerOptions.Builder.maxResults(10);
      BlobStoreContext context = blobStoreContext();
      BlobStore blobStore = null;
      try {
         blobStore = context.getBlobStore();
         blobStore.createContainerInLocation(null, containerName);
         Set<String> expectedNames = Sets.newHashSet();
         for (int i = 0; i < NUM_BLOBS; i++) {
            String blobName = "myname" + i;
            blobStore.putBlob(containerName, blobStore.blobBuilder(blobName).payload("payload" + i).build());
            expectedNames.add(blobName);
         }

         ListAllOptions listAllOptions = ListAllOptions.Builder.eager(eager);
         Iterable<StorageMetadata> iterable = BlobStores.listAll(blobStore, containerName, containerOptions,
                  listAllOptions);

         for (int i = 0; i < numTimesToIterate; i++) {
            Iterable<String> iterableNames = Iterables.transform(iterable, new Function<StorageMetadata, String>() {
               @Override
               public String apply(StorageMetadata input) {
                  return input.getName();
               }
            });

            // Note that blob.getMetadata being put does not equal blob metadata being retrieved
            // because uri is null in one and populated in the other.
            // Therefore we just compare names to ensure the iterator worked.
            assertEquals(ImmutableSet.copyOf(iterableNames), expectedNames);
         }
      } finally {
         if (blobStore != null)
            blobStore.deleteContainer(containerName);
         context.close();
      }
   }

   @Test
   public void testListAllWhenOnePage() throws Exception {
      BlobStore blobStore = createMock(BlobStore.class);
      ListContainerOptions options = ListContainerOptions.NONE;
      StorageMetadata v1 = createMock(StorageMetadata.class);
      PageSet<StorageMetadata> pageSet = new PageSetImpl<StorageMetadata>(ImmutableList.of(v1), null);

      EasyMock.<PageSet<? extends StorageMetadata>> expect(blobStore.list(containerName, options)).andReturn(pageSet)
               .once();
      EasyMock.replay(blobStore);

      Iterable<StorageMetadata> iterable = BlobStores.listAll(blobStore, containerName, options);
      assertEquals(ImmutableList.copyOf(iterable), ImmutableList.of(v1));
   }

   @Test
   public void testListAllWhenTwoPages() throws Exception {
      BlobStore blobStore = createMock(BlobStore.class);
      ListContainerOptions options = ListContainerOptions.NONE;
      ListContainerOptions options2 = ListContainerOptions.Builder.afterMarker("marker1");
      StorageMetadata v1 = createMock(StorageMetadata.class);
      StorageMetadata v2 = createMock(StorageMetadata.class);
      PageSet<StorageMetadata> pageSet = new PageSetImpl<StorageMetadata>(ImmutableList.of(v1), "marker1");
      PageSet<StorageMetadata> pageSet2 = new PageSetImpl<StorageMetadata>(ImmutableList.of(v2), null);

      EasyMock.<PageSet<? extends StorageMetadata>> expect(blobStore.list(containerName, options)).andReturn(pageSet)
               .once();
      EasyMock.<PageSet<? extends StorageMetadata>> expect(blobStore.list(containerName, options2)).andReturn(pageSet2)
               .once();
      EasyMock.replay(blobStore);

      Iterable<StorageMetadata> iterable = BlobStores.listAll(blobStore, containerName, options);
      assertEquals(ImmutableList.copyOf(iterable), ImmutableList.of(v1, v2));
   }
}
