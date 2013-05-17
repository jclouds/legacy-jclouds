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
package org.jclouds.blobstore.integration.internal;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Throwables.propagateIfPossible;
import static com.google.common.collect.Iterables.get;
import static com.google.common.hash.Hashing.md5;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.afterMarker;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.inDirectory;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
public class BaseContainerIntegrationTest extends BaseBlobStoreIntegrationTest {

   @Test(groups = { "integration", "live" })
   public void containerDoesntExist() {
      assert !view.getBlobStore().containerExists("forgetaboutit");
      assert !view.getBlobStore().containerExists("cloudcachestorefunctionalintegrationtest-first");
   }

   @Test(groups = { "integration", "live" })
   public void testPutTwiceIsOkAndDoesntOverwrite() throws InterruptedException {
      String containerName = getContainerName();
      try {
         view.getBlobStore().createContainerInLocation(null, containerName);

         Blob blob = view.getBlobStore().blobBuilder("hello").payload(TEST_STRING).build();
         view.getBlobStore().putBlob(containerName, blob);

         view.getBlobStore().createContainerInLocation(null, containerName);
         assertEquals(view.getBlobStore().countBlobs(containerName), 1);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testWithDetails() throws InterruptedException, IOException {
      String key = "hello";
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName,
         // NOTE all metadata in jclouds comes out as lowercase, in an effort to
         // normalize the providers.
               view.getBlobStore().blobBuilder(key).userMetadata(ImmutableMap.of("Adrian", "powderpuff"))
                     .payload(TEST_STRING).contentType(MediaType.TEXT_PLAIN).calculateMD5().build());
         validateContent(containerName, key);

         PageSet<? extends StorageMetadata> container = view.getBlobStore().list(containerName,
               maxResults(1).withDetails());

         BlobMetadata metadata = BlobMetadata.class.cast(get(container, 0));

         assert metadata.getContentMetadata().getContentType().startsWith("text/plain") : metadata.getContentMetadata()
               .getContentType();
         assertEquals(metadata.getContentMetadata().getContentLength(), Long.valueOf(TEST_STRING.length()));
         assertEquals(metadata.getUserMetadata().get("adrian"), "powderpuff");
         checkMD5(metadata);
      } finally {
         returnContainer(containerName);
      }
   }

   protected void checkMD5(BlobMetadata metadata) throws IOException {
      assertEquals(metadata.getContentMetadata().getContentMD5(), md5().hashString(TEST_STRING, UTF_8).asBytes());
   }

   @Test(groups = { "integration", "live" })
   public void testClearWhenContentsUnderPath() throws InterruptedException {
      String containerName = getContainerName();
      try {
         add5BlobsUnderPathAnd5UnderRootToContainer(containerName);
         view.getBlobStore().clearContainer(containerName);
         assertConsistencyAwareContainerSize(containerName, 0);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testListContainerMarker() throws InterruptedException {
      String containerName = getContainerName();
      try {
         addAlphabetUnderRoot(containerName);

         PageSet<? extends StorageMetadata> container = view.getBlobStore().list(containerName, maxResults(1));

         assert container.getNextMarker() != null;
         assertEquals(container.size(), 1);
         String marker = container.getNextMarker();

         container = view.getBlobStore().list(containerName, afterMarker(marker));
         assertEquals(container.getNextMarker(), null);
         assert container.size() == 25 : String.format("size should have been 25, but was %d: %s", container.size(),
               container);
         assert container.getNextMarker() == null;

      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testListRootUsesDelimiter() throws InterruptedException {
      String containerName = getContainerName();
      try {
         String prefix = "rootdelimiter";
         addTenObjectsUnderPrefix(containerName, prefix);
         add15UnderRoot(containerName);
         PageSet<? extends StorageMetadata> container = view.getBlobStore().list(containerName);
         assert container.getNextMarker() == null;
         assertEquals(container.size(), 16);
      } finally {
         returnContainer(containerName);
      }

   }

   @Test(groups = { "integration", "live" })
   public void testDirectory() throws InterruptedException {
      String containerName = getContainerName();
      try {
         String directory = "directory";

         assert !view.getBlobStore().directoryExists(containerName, directory);

         view.getBlobStore().createDirectory(containerName, directory);

         assert view.getBlobStore().directoryExists(containerName, directory);
         PageSet<? extends StorageMetadata> container = view.getBlobStore().list(containerName);
         // we should have only the directory under root
         assert container.getNextMarker() == null;
         assert container.size() == 1 : container;

         container = view.getBlobStore().list(containerName, inDirectory(directory));

         // we should have nothing in the directory
         assert container.getNextMarker() == null;
         assert container.size() == 0 : container;

         addTenObjectsUnderPrefix(containerName, directory);

         container = view.getBlobStore().list(containerName);
         // we should still have only the directory under root
         assert container.getNextMarker() == null;
         assert container.size() == 1 : container;

         container = view.getBlobStore().list(containerName, inDirectory(directory));
         // we should have only the 10 items under the directory
         assert container.getNextMarker() == null;
         assert container.size() == 10 : container;

         // try 2 level deep directory
         assert !view.getBlobStore().directoryExists(containerName, directory + "/" + directory);
         view.getBlobStore().createDirectory(containerName, directory + "/" + directory);
         assert view.getBlobStore().directoryExists(containerName, directory + "/" + directory);

         view.getBlobStore().clearContainer(containerName, inDirectory(directory));
         assert view.getBlobStore().directoryExists(containerName, directory);
         assert view.getBlobStore().directoryExists(containerName, directory + "/" + directory);

         // should have only the 2 level-deep directory above
         container = view.getBlobStore().list(containerName, inDirectory(directory));
         assert container.getNextMarker() == null;
         assert container.size() == 1 : container;

         view.getBlobStore().createDirectory(containerName, directory + "/" + directory);

         container = view.getBlobStore().list(containerName, inDirectory(directory).recursive());
         assert container.getNextMarker() == null;
         assert container.size() == 1 : container;

         view.getBlobStore().clearContainer(containerName, inDirectory(directory).recursive());

         // should no longer have the 2 level-deep directory above
         container = view.getBlobStore().list(containerName, inDirectory(directory));
         assert container.getNextMarker() == null;
         assert container.size() == 0 : container;

         container = view.getBlobStore().list(containerName);
         // should only have the directory
         assert container.getNextMarker() == null;
         assert container.size() == 1 : container;
         view.getBlobStore().deleteDirectory(containerName, directory);

         container = view.getBlobStore().list(containerName);
         // now should be completely empty
         assert container.getNextMarker() == null;
         assert container.size() == 0 : container;
      } finally {
         returnContainer(containerName);
      }

   }

   @Test(groups = { "integration", "live" })
   public void testListContainerPrefix() throws InterruptedException {
      String containerName = getContainerName();
      try {
         String prefix = "containerprefix";
         addTenObjectsUnderPrefix(containerName, prefix);
         add15UnderRoot(containerName);

         PageSet<? extends StorageMetadata> container = view.getBlobStore().list(containerName, inDirectory(prefix));
         assert container.getNextMarker() == null;
         assertEquals(container.size(), 10);
      } finally {
         returnContainer(containerName);
      }

   }

   @Test(groups = { "integration", "live" })
   public void testListContainerMaxResults() throws InterruptedException {
      String containerName = getContainerName();
      try {
         addAlphabetUnderRoot(containerName);

         PageSet<? extends StorageMetadata> container = view.getBlobStore().list(containerName, maxResults(5));
         assertEquals(container.size(), 5);
         assert container.getNextMarker() != null;
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void containerExists() throws InterruptedException {
      String containerName = getContainerName();
      try {
         assert view.getBlobStore().containerExists(containerName);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void deleteContainerWithContents() throws InterruptedException {
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, "test");
         view.getBlobStore().deleteContainer(containerName);
         assertNotExists(containerName);
      } finally {
         recycleContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmpty() throws InterruptedException {
      final String containerName = getContainerName();
      try {
         view.getBlobStore().deleteContainer(containerName);
         assertNotExists(containerName);
      } finally {
         // this container is now deleted, so we can't reuse it directly
         recycleContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testListContainer() throws InterruptedException, ExecutionException, TimeoutException {
      String containerName = getContainerName();
      try {
         add15UnderRoot(containerName);
         Set<? extends StorageMetadata> container = view.getBlobStore().list(containerName);
         assertEquals(container.size(), 15);
      } finally {
         returnContainer(containerName);
      }

   }

   protected void addAlphabetUnderRoot(String containerName) throws InterruptedException {
      for (char letter = 'a'; letter <= 'z'; letter++) {
         view.getBlobStore().putBlob(containerName,
               view.getBlobStore().blobBuilder(letter + "").payload(letter + "content").build());
      }
      assertContainerSize(containerName, 26);

   }

   protected void assertContainerSize(final String containerName, final int size) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            try {
               assertEquals(view.getBlobStore().countBlobs(containerName), size);
            } catch (Exception e) {
               propagateIfPossible(e);
            }
         }
      });
   }

   protected void add15UnderRoot(String containerName) throws InterruptedException {
      for (int i = 0; i < 15; i++) {
         view.getBlobStore().putBlob(containerName,
               view.getBlobStore().blobBuilder(i + "").payload(i + "content").build());
      }
   }

   protected void addTenObjectsUnderPrefix(String containerName, String prefix) throws InterruptedException {
      for (int i = 0; i < 10; i++) {
         view.getBlobStore().putBlob(containerName,
               view.getBlobStore().blobBuilder(prefix + "/" + i).payload(i + "content").build());
      }
   }
}
