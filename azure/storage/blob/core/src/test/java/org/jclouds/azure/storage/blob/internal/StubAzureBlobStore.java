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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.azure.storage.blob.AzureBlobStore;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.TreeSetListBlobsResponse;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.domain.BoundedSortedSet;
import org.jclouds.azure.storage.options.CreateOptions;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.integration.internal.StubBlobStore;
import org.jclouds.util.DateService;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link AzureBlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
public class StubAzureBlobStore extends StubBlobStore<ContainerMetadata, BlobMetadata, Blob>
         implements AzureBlobStore {

   @Inject
   protected StubAzureBlobStore(Map<String, Map<String, Blob>> containerToBlobs,
            DateService dateService, Provider<ContainerMetadata> containerMetaProvider,
            Provider<Blob> blobProvider) {
      super(containerToBlobs, dateService, containerMetaProvider, blobProvider);
   }

   public BoundedSortedSet<ContainerMetadata> listContainers(ListOptions options) {
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

            return new TreeSetListBlobsResponse(URI.create("http://localhost"), contents, null,
                     null, 5000, null, null, null);
         }
      };
   }

}
