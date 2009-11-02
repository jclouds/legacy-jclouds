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
package org.jclouds.azure.storage.blob.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;

import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.blob.blobstore.functions.AzureBlobToBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobPropertiesToBlobMetadata;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobToAzureBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.azure.storage.blob.blobstore.functions.ListBlobsResponseToResourceList;
import org.jclouds.azure.storage.blob.blobstore.functions.ListOptionsToListBlobsOptions;
import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.ListableContainerProperties;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.internal.ListResponseImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.concurrent.FutureFunctionWrapper;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

@ConsistencyModel(ConsistencyModels.STRICT)
public class AzureBlobStore implements BlobStore {
   private final AzureBlobClient connection;
   private final Blob.Factory blobFactory;
   private final LoggerFactory logFactory;
   private final ClearListStrategy clearContainerStrategy;
   private final BlobPropertiesToBlobMetadata object2BlobMd;
   private final AzureBlobToBlob object2Blob;
   private final BlobToAzureBlob blob2Object;
   private final ListOptionsToListBlobsOptions container2ContainerListOptions;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;
   private final ContainerToResourceMetadata container2ResourceMd;
   private final ListBlobsResponseToResourceList container2ResourceList;
   private final ExecutorService service;

   @Inject
   private AzureBlobStore(AzureBlobClient connection, Blob.Factory blobFactory,
            LoggerFactory logFactory, ClearListStrategy clearContainerStrategy,
            BlobPropertiesToBlobMetadata object2BlobMd, AzureBlobToBlob object2Blob,
            BlobToAzureBlob blob2Object,
            ListOptionsToListBlobsOptions container2ContainerListOptions,
            BlobToHttpGetOptions blob2ObjectGetOptions,
            ContainerToResourceMetadata container2ResourceMd,
            ListBlobsResponseToResourceList container2ResourceList, ExecutorService service) {
      this.connection = checkNotNull(connection, "connection");
      this.blobFactory = checkNotNull(blobFactory, "blobFactory");
      this.logFactory = checkNotNull(logFactory, "logFactory");
      this.clearContainerStrategy = checkNotNull(clearContainerStrategy, "clearContainerStrategy");
      this.object2BlobMd = checkNotNull(object2BlobMd, "object2BlobMd");
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.container2ContainerListOptions = checkNotNull(container2ContainerListOptions,
               "container2ContainerListOptions");
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
      this.container2ResourceMd = checkNotNull(container2ResourceMd, "container2ResourceMd");
      this.container2ResourceList = checkNotNull(container2ResourceList, "container2ResourceList");
      this.service = checkNotNull(service, "service");
   }

   protected <F, T> Future<T> wrapFuture(Future<? extends F> future, Function<F, T> function) {
      return new FutureFunctionWrapper<F, T>(future, function, logFactory.getLogger(function
               .getClass().getName()));
   }

   /**
    * This implementation uses the AzureBlob HEAD Object command to return the result
    */
   public BlobMetadata blobMetadata(String container, String key) {
      return object2BlobMd.apply(connection.getBlobProperties(container, key));
   }

   public Future<Void> clearContainer(final String container) {
      return service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            return null;
         }

      });
   }

   public Future<Boolean> createContainer(String container) {
      return connection.createContainer(container);
   }

   public Future<Void> deleteContainer(final String container) {
      return connection.deleteContainer(container);

   }

   public boolean exists(String container) {
      return connection.containerExists(container);
   }

   public Future<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions... optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      Future<AzureBlob> returnVal = connection.getBlob(container, key, httpOptions);
      return wrapFuture(returnVal, object2Blob);
   }

   public Future<? extends org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata>> list() {
      return wrapFuture(
               connection.listContainers(),
               new Function<SortedSet<ListableContainerProperties>, org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata>>() {
                  public org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata> apply(
                           SortedSet<ListableContainerProperties> from) {
                     return new ListResponseImpl<ResourceMetadata>(Iterables.transform(from,
                              container2ResourceMd), null, null, false);
                  }
               });
   }

   public Future<? extends ListContainerResponse<? extends ResourceMetadata>> list(String container,
            ListContainerOptions... optionsList) {
      ListBlobsOptions httpOptions = container2ContainerListOptions.apply(optionsList);
      Future<ListBlobsResponse> returnVal = connection.listBlobs(container, httpOptions);
      return wrapFuture(returnVal, container2ResourceList);
   }

   public Future<String> putBlob(String container, Blob blob) {
      return connection.putBlob(container, blob2Object.apply(blob));
   }

   public Future<Void> removeBlob(String container, String key) {
      return connection.deleteBlob(container, key);
   }

   public Blob newBlob() {
      return blobFactory.create(null);
   }

}
