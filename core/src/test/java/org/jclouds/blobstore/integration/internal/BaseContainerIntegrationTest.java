/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.blobstore.integration.internal;

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.afterMarker;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.maxResults;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.underPath;
import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
public class BaseContainerIntegrationTest<A, S> extends BaseBlobStoreIntegrationTest<A, S> {

   @Test(groups = { "integration", "live" })
   public void containerDoesntExist() {
      assert !context.getBlobStore().exists("forgetaboutit");
   }

   @Test(groups = { "integration", "live" })
   public void testPutTwiceIsOk() throws InterruptedException {
      String containerName = getContainerName();
      try {
         context.getBlobStore().createContainer(containerName);
         context.getBlobStore().createContainer(containerName);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testClearWhenContentsUnderPath() throws InterruptedException {
      String containerName = getContainerName();
      try {
         add5BlobsUnderPathAnd5UnderRootToContainer(containerName);
         context.getBlobStore().clearContainer(containerName);
         assertConsistencyAwareContainerSize(containerName, 0);
      } finally {
         returnContainer(containerName);
      }
   }

   public void testListContainerMarker() throws InterruptedException, UnsupportedEncodingException {
      String containerName = getContainerName();
      try {
         addAlphabetUnderRoot(containerName);
         ListResponse<? extends ResourceMetadata> container = context.getBlobStore().list(
                  containerName, afterMarker("y"));
         assertEquals(container.getMarker(), "y");
         assert !container.isTruncated();
         assertEquals(container.size(), 1);
      } finally {
         returnContainer(containerName);
      }
   }

   public void testListContainerDelimiter() throws InterruptedException,
            UnsupportedEncodingException {
      String containerName = getContainerName();
      try {
         String prefix = "apps";
         addTenObjectsUnderPrefix(containerName, prefix);
         add15UnderRoot(containerName);
         ListResponse<? extends ResourceMetadata> container = context.getBlobStore().list(
                  containerName);
         assert !container.isTruncated();
         assertEquals(container.size(), 16);
      } finally {
         returnContainer(containerName);
      }

   }

   public void testListContainerPrefix() throws InterruptedException, UnsupportedEncodingException {
      String containerName = getContainerName();
      try {
         String prefix = "apps";
         addTenObjectsUnderPrefix(containerName, prefix);
         add15UnderRoot(containerName);

         ListContainerResponse<? extends ResourceMetadata> container = context.getBlobStore().list(
                  containerName, underPath("apps/"));
         assert !container.isTruncated();
         assertEquals(container.size(), 10);
         assertEquals(container.getPath(), "apps/");
      } finally {
         returnContainer(containerName);
      }

   }

   public void testListContainerMaxResults() throws InterruptedException,
            UnsupportedEncodingException {
      String containerName = getContainerName();
      try {
         addAlphabetUnderRoot(containerName);
         ListResponse<? extends ResourceMetadata> container = context.getBlobStore().list(
                  containerName, maxResults(5));
         assertEquals(container.getMaxResults(), 5);
         assert container.isTruncated();
         assertEquals(container.size(), 5);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void containerExists() throws InterruptedException {
      String containerName = getContainerName();
      try {
         assert context.getBlobStore().exists(containerName);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void deleteContainerWithContents() throws InterruptedException {
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, "test");
         context.getBlobStore().deleteContainer(containerName);
         assertNotExists(containerName);
      } finally {
         recycleContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmpty() throws InterruptedException {
      final String containerName = getContainerName();
      try {
         context.getBlobStore().deleteContainer(containerName);
         assertNotExists(containerName);
      } finally {
         // this container is now deleted, so we can't reuse it directly
         recycleContainer(containerName);
      }
   }

   private void assertNotExists(final String containerName) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            try {
               assert !context.getBlobStore().exists(containerName) : "container " + containerName
                        + " still exists";
            } catch (Exception e) {
               Utils.<RuntimeException> rethrowIfRuntimeOrSameType(e);
            }
         }
      });
   }

   @Test(groups = { "integration", "live" })
   public void testListContainer() throws InterruptedException, ExecutionException,
            TimeoutException, UnsupportedEncodingException {
      String containerName = getContainerName();
      try {
         add15UnderRoot(containerName);
         SortedSet<? extends ResourceMetadata> container = context.getBlobStore().list(
                  containerName);
         assertEquals(container.size(), 15);
      } finally {
         returnContainer(containerName);
      }

   }

   protected void addAlphabetUnderRoot(String containerName) throws InterruptedException {
      for (char letter = 'a'; letter <= 'z'; letter++) {
         Blob blob = newBlob(letter + "");
         blob.setData(letter + "content");
         context.getBlobStore().putBlob(containerName, blob);
      }
   }

   protected void add15UnderRoot(String containerName) throws InterruptedException {
      for (int i = 0; i < 15; i++) {
         Blob blob = newBlob(i + "");
         blob.setData(i + "content");
         context.getBlobStore().putBlob(containerName, blob);
      }
   }

   protected void addTenObjectsUnderPrefix(String containerName, String prefix)
            throws InterruptedException {
      for (int i = 0; i < 10; i++) {
         Blob blob = newBlob(prefix + "/" + i);
         blob.setData(i + "content");
         context.getBlobStore().putBlob(containerName, blob);
      }
   }
}