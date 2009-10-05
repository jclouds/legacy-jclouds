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

import org.jclouds.azure.storage.blob.AzureBlobConnection;
import org.jclouds.azure.storage.blob.AzureBlobStore;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.TreeSetListBlobsResponse;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.azure.storage.domain.BoundedSortedSet;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.integration.internal.StubBlobStore;
import org.jclouds.http.options.GetOptions;
import org.jclouds.util.DateService;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link AzureBlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
public class StubAzureBlobConnection extends StubBlobStore<ContainerMetadata, BlobMetadata, Blob>
         implements AzureBlobConnection {

   @Inject
   protected StubAzureBlobConnection(Map<String, Map<String, Blob>> containerToBlobs,
            DateService dateService, Provider<ContainerMetadata> containerMetaProvider,
            Provider<Blob> blobProvider) {
      super(containerToBlobs, dateService, containerMetaProvider, blobProvider);
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

   public Future<Boolean> createContainer(String container, CreateContainerOptions... options) {
      throw new UnsupportedOperationException();
   }

   public Future<Void> deleteBlob(String container, String key) {
      throw new UnsupportedOperationException();
   }

   public Future<Boolean> deleteRootContainer() {
      throw new UnsupportedOperationException();
   }

   public Future<Blob> getBlob(String container, String key, GetOptions... options) {
      throw new UnsupportedOperationException();
   }

   public BlobMetadata getBlobProperties(String container, String key) {
      throw new UnsupportedOperationException();
   }

   public Future<ListBlobsResponse> listBlobs(String container, ListBlobsOptions... options) {
      throw new UnsupportedOperationException();
   }

   public Future<ListBlobsResponse> listBlobs(ListBlobsOptions... options) {
      throw new UnsupportedOperationException();
   }

   public BoundedSortedSet<ContainerMetadata> listContainers(ListOptions... listOptions) {
      throw new UnsupportedOperationException();
   }

   public Future<Boolean> createRootContainer(CreateContainerOptions... options) {
      throw new UnsupportedOperationException();
   }

   public ContainerMetadata getContainerProperties(String container) {
      throw new UnsupportedOperationException();
   }

   public void setContainerMetadata(String container, Multimap<String, String> metadata) {
      throw new UnsupportedOperationException();
   }

   public void setBlobMetadata(String container, String key, Multimap<String, String> metadata) {
      throw new UnsupportedOperationException();
   }
}
