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
package org.jclouds.blobstore.integration.internal;

import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
public class BaseContainerIntegrationTest<S, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         extends BaseBlobStoreIntegrationTest<S, C, M, B> {

   @Test(groups = { "integration", "live" })
   public void containerDoesntExist() throws Exception {
      assert !context.getBlobStore().containerExists("forgetaboutit");
   }

   @Test(groups = { "integration", "live" })
   public void testPutTwiceIsOk() throws Exception {
      String containerName = getContainerName();
      try {
         context.getBlobStore().createContainer(containerName).get(60, TimeUnit.SECONDS);
         context.getBlobStore().createContainer(containerName).get(60, TimeUnit.SECONDS);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void containerExists() throws Exception {
      String containerName = getContainerName();
      try {
         assert context.getBlobStore().containerExists(containerName);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void deleteContainerWithContents() throws Exception {
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, "test");
         context.getBlobStore().deleteContainer(containerName).get(60, TimeUnit.SECONDS);
         assertNotExists(containerName);
      } finally {
         recycleContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmpty() throws Exception {
      final String containerName = getContainerName();
      try {
         context.getBlobStore().deleteContainer(containerName).get(60, TimeUnit.SECONDS);
         assertNotExists(containerName);
      } finally {
         // this container is now deleted, so we can't reuse it directly
         recycleContainer(containerName);
      }
   }

   private void assertNotExists(final String containerName) throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            try {
               assert !context.getBlobStore().containerExists(containerName) : "container "
                        + containerName + " still exists";
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
         SortedSet<M> container = context.getBlobStore().listBlobs(containerName).get(60,
                  TimeUnit.SECONDS);
         assertEquals(container.size(), 15);
      } finally {
         returnContainer(containerName);
      }

   }

   protected void addAlphabetUnderRoot(String containerName) throws InterruptedException,
            ExecutionException, TimeoutException {
      for (char letter = 'a'; letter <= 'z'; letter++) {
         B blob = context.newBlob(letter + "");
         blob.setData(letter + "content");
         context.getBlobStore().putBlob(containerName, blob).get(60, TimeUnit.SECONDS);
      }
   }

   protected void add15UnderRoot(String containerName) throws InterruptedException,
            ExecutionException, TimeoutException {
      for (int i = 0; i < 15; i++) {
         B blob = context.newBlob(i + "");
         blob.setData(i + "content");
         context.getBlobStore().putBlob(containerName, blob).get(60, TimeUnit.SECONDS);
      }
   }

   protected void addTenObjectsUnderPrefix(String containerName, String prefix)
            throws InterruptedException, ExecutionException, TimeoutException {
      for (int i = 0; i < 10; i++) {
         B blob = context.newBlob(prefix + "/" + i);
         blob.setData(i + "content");
         context.getBlobStore().putBlob(containerName, blob).get(60, TimeUnit.SECONDS);
      }
   }
}