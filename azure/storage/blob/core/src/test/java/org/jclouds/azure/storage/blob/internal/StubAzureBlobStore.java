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
package org.jclouds.azure.storage.blob.internal;

import java.net.URI;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jclouds.azure.storage.blob.AzureBlobStore;
import org.jclouds.azure.storage.blob.domain.ArrayListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.domain.BoundedList;
import org.jclouds.azure.storage.options.CreateOptions;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.integration.internal.StubBlobStore;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link AzureBlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
public class StubAzureBlobStore extends StubBlobStore<ContainerMetadata, BlobMetadata, Blob>
         implements AzureBlobStore {

   @Override
   protected Blob createBlob(String name) {
      return new Blob(name);
   }

   @Override
   protected Blob createBlob(BlobMetadata metadata) {
      return new Blob(metadata);
   }

   @Override
   protected ContainerMetadata createContainerMetadata(String name) {
      return new ContainerMetadata(name);
   }

   /**
    * note this must be final and static so that tests coming from multiple threads will pass.
    */
   private static final Map<String, Map<String, Blob>> containerToBlobs = new ConcurrentHashMap<String, Map<String, Blob>>();

   @Override
   public Map<String, Map<String, Blob>> getContainerToBlobs() {
      return containerToBlobs;
   }

   public BoundedList<ContainerMetadata> listContainers(ListOptions options) {
      return null;
   }

   public Future<Boolean> createContainer(String container, CreateContainerOptions options) {
      return null;
   }

   public Future<Boolean> createRootContainer() {
      return null;
   }

   public Future<Boolean> createRootContainer(CreateOptions options) {
      return null;
   }

   public Future<Boolean> deleteRootContainer() {
      return null;
   }

   public Future<ListBlobsResponse> listBlobs() {
      return null;
   }

   public Future<ListBlobsResponse> listBlobs(final String name) {
      return new FutureBase<ListBlobsResponse>() {
         public ListBlobsResponse get() throws InterruptedException, ExecutionException {
            final Map<String, Blob> realContents = getContainerToBlobs().get(name);

            if (realContents == null)
               throw new ContainerNotFoundException(name);
            SortedSet<BlobMetadata> contents = Sets.newTreeSet(Iterables.transform(realContents
                     .keySet(), new Function<String, BlobMetadata>() {
               public BlobMetadata apply(String key) {
                  return realContents.get(key).getMetadata();
               }
            }));

            return new ArrayListBlobsResponse(URI.create("http://localhost"), Lists
                     .newArrayList(contents), null, null, 5000, null, null, null);
         }
      };
   }

}
