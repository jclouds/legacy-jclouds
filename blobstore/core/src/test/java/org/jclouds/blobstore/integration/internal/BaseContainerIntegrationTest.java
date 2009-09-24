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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
public class BaseContainerIntegrationTest<S extends BlobStore<C, M, B>, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         extends BaseBlobStoreIntegrationTest<S, C, M, B> {

   @Test(groups = { "integration", "live" })
   public void containerDoesntExist() throws Exception {
      assert !client.containerExists("forgetaboutit");
   }

   @Test(groups = { "integration", "live" })
   public void testPutTwiceIsOk() throws Exception {
      String containerName = getContainerName();
      try {
         client.createContainer(containerName).get(10, TimeUnit.SECONDS);
         client.createContainer(containerName).get(10, TimeUnit.SECONDS);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void containerExists() throws Exception {
      String containerName = getContainerName();
      try {
         assert client.containerExists(containerName);
      } finally {
         returnContainer(containerName);
      }
   }

   /**
    * this method overrides containerName to ensure it isn't found
    */
   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmptyNotFound() throws Exception {
      assert client.deleteContainer("dbienf").get(10, TimeUnit.SECONDS);
   }

   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmptyButHasContents() throws Exception {
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, "test");
         assert !client.deleteContainer(containerName).get(10, TimeUnit.SECONDS);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmpty() throws Exception {
      final String containerName = getContainerName();
      try {
         assert client.deleteContainer(containerName).get(10, TimeUnit.SECONDS);

         assertEventually(new Runnable() {
            public void run() {
               try {
                  assert !client.containerExists(containerName) : "container " + containerName
                           + " still exists";
               } catch (Exception e) {
                  Utils.<RuntimeException> rethrowIfRuntimeOrSameType(e);
               }
            }
         });
      } finally {
         // this container is now deleted, so we can't reuse it directly
         recycleContainer(containerName);
      }
   }

   protected void addAlphabetUnderRoot(String containerName) throws InterruptedException,
            ExecutionException, TimeoutException {
      for (char letter = 'a'; letter <= 'z'; letter++) {
         B blob = objectFactory.createBlob(letter + "");
         blob.setData(letter + "content");
         client.putBlob(containerName, blob).get(10, TimeUnit.SECONDS);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testListContainer() throws InterruptedException, ExecutionException,
            TimeoutException, UnsupportedEncodingException {
      String containerName = getContainerName();
      try {
         String prefix = "apps";
         addTenObjectsUnderPrefix(containerName, prefix);
         List<M> container = client.listBlobs(containerName).get(10, TimeUnit.SECONDS);
         assertEquals(container.size(), 10);
      } finally {
         returnContainer(containerName);
      }

   }

   protected void add15UnderRoot(String containerName) throws InterruptedException,
            ExecutionException, TimeoutException {
      for (int i = 0; i < 15; i++) {
         B blob = objectFactory.createBlob(i + "");
         blob.setData(i + "content");
         client.putBlob(containerName, blob).get(10, TimeUnit.SECONDS);
      }
   }

   protected void addTenObjectsUnderPrefix(String containerName, String prefix)
            throws InterruptedException, ExecutionException, TimeoutException {
      for (int i = 0; i < 10; i++) {
         B blob = objectFactory.createBlob(prefix + "/" + i);
         blob.setData(i + "content");
         client.putBlob(containerName, blob).get(10, TimeUnit.SECONDS);
      }
   }
}