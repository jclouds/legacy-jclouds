/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.azureblob.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.azure.storage.options.ListOptions.Builder.includeMetadata;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azureblob.AzureBlobAsyncClient;
import org.jclouds.azureblob.blobstore.functions.AzureBlobToBlob;
import org.jclouds.azureblob.blobstore.functions.BlobPropertiesToBlobMetadata;
import org.jclouds.azureblob.blobstore.functions.BlobToAzureBlob;
import org.jclouds.azureblob.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.azureblob.blobstore.functions.ListBlobsResponseToResourceList;
import org.jclouds.azureblob.blobstore.functions.ListOptionsToListBlobsOptions;
import org.jclouds.azureblob.domain.AzureBlob;
import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.ContainerProperties;
import org.jclouds.azureblob.domain.ListBlobsResponse;
import org.jclouds.azureblob.domain.PublicAccess;
import org.jclouds.azureblob.options.ListBlobsOptions;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseAsyncBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.concurrent.Futures;
import org.jclouds.domain.Location;
import org.jclouds.http.options.GetOptions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Adrian Cole
 */
@Singleton
public class AzureAsyncBlobStore extends BaseAsyncBlobStore {
   private final AzureBlobAsyncClient async;
   private final ContainerToResourceMetadata container2ResourceMd;
   private final ListOptionsToListBlobsOptions blobStore2AzureContainerListOptions;
   private final ListBlobsResponseToResourceList azure2BlobStoreResourceList;
   private final AzureBlobToBlob azureBlob2Blob;
   private final BlobToAzureBlob blob2AzureBlob;
   private final BlobPropertiesToBlobMetadata blob2BlobMd;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;

   @Inject
   AzureAsyncBlobStore(BlobStoreContext context, BlobUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, AzureBlobAsyncClient async,
            ContainerToResourceMetadata container2ResourceMd,
            ListOptionsToListBlobsOptions blobStore2AzureContainerListOptions,
            ListBlobsResponseToResourceList azure2BlobStoreResourceList, AzureBlobToBlob azureBlob2Blob,
            BlobToAzureBlob blob2AzureBlob, BlobPropertiesToBlobMetadata blob2BlobMd,
            BlobToHttpGetOptions blob2ObjectGetOptions) {
      super(context, blobUtils, service, defaultLocation, locations);
      this.async = checkNotNull(async, "async");
      this.container2ResourceMd = checkNotNull(container2ResourceMd, "container2ResourceMd");
      this.blobStore2AzureContainerListOptions = checkNotNull(blobStore2AzureContainerListOptions,
               "blobStore2AzureContainerListOptions");
      this.azure2BlobStoreResourceList = checkNotNull(azure2BlobStoreResourceList, "azure2BlobStoreResourceList");
      this.azureBlob2Blob = checkNotNull(azureBlob2Blob, "azureBlob2Blob");
      this.blob2AzureBlob = checkNotNull(blob2AzureBlob, "blob2AzureBlob");
      this.blob2BlobMd = checkNotNull(blob2BlobMd, "blob2BlobMd");
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
   }

   /**
    * This implementation invokes {@link AzureBlobAsyncClient#listContainers} with the
    * {@link org.jclouds.azure.storage.options.ListOptions#includeMetadata} option.
    */
   @Override
   public ListenableFuture<org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>> list() {
      return Futures
               .compose(
                        async.listContainers(includeMetadata()),
                        new Function<BoundedSet<ContainerProperties>, org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>>() {
                           public org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata> apply(
                                    BoundedSet<ContainerProperties> from) {
                              return new PageSetImpl<StorageMetadata>(Iterables.transform(from, container2ResourceMd),
                                       from.getNextMarker());
                           }
                        }, service);
   }

   /**
    * This implementation invokes {@link AzureBlobAsyncClient#containerExists}
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<Boolean> containerExists(String container) {
      return async.containerExists(container);
   }

   /**
    * This implementation invokes {@link AzureBlobAsyncClient#createContainer}
    * 
    * @param location
    *           ignored
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<Boolean> createContainerInLocation(Location location, String container) {
      return async.createContainer(container);
   }

   /**
    * This implementation invokes {@link AzureBlobAsyncClient#listBucket}
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list(String container, ListContainerOptions options) {
      ListBlobsOptions azureOptions = blobStore2AzureContainerListOptions.apply(options);
      ListenableFuture<ListBlobsResponse> returnVal = async.listBlobs(container, azureOptions.includeMetadata());
      return Futures.compose(returnVal, azure2BlobStoreResourceList, service);
   }

   /**
    * This implementation invokes {@link AzureBlobAsyncClient#deleteContainer}
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<Void> deleteContainer(final String container) {
      return async.deleteContainer(container);
   }

   /**
    * This implementation invokes {@link AzureBlobAsyncClient#getBlob}
    * 
    * @param container
    *           container name
    * @param key
    *           blob key
    */
   @Override
   public ListenableFuture<Blob> getBlob(String container, String key, org.jclouds.blobstore.options.GetOptions options) {
      GetOptions azureOptions = blob2ObjectGetOptions.apply(options);
      ListenableFuture<AzureBlob> returnVal = async.getBlob(container, key, azureOptions);
      return Futures.compose(returnVal, azureBlob2Blob, service);
   }

   /**
    * This implementation invokes {@link AzureBlobAsyncClient#putBlob}
    * 
    * @param container
    *           container name
    * @param blob
    *           blob
    */
   @Override
   public ListenableFuture<String> putBlob(String container, Blob blob) {
      return async.putBlob(container, blob2AzureBlob.apply(blob));
   }

   /**
    * This implementation invokes {@link AzureBlobAsyncClient#deleteObject}
    * 
    * @param container
    *           container name
    * @param key
    *           blob key
    */
   @Override
   public ListenableFuture<Void> removeBlob(String container, String key) {
      return async.deleteBlob(container, key);
   }

   /**
    * This implementation invokes {@link AzureBlobAsyncClient#blobExists}
    * 
    * @param container
    *           bucket name
    * @param credential
    *           object key
    */
   @Override
   public ListenableFuture<Boolean> blobExists(String container, String name) {
      return async.blobExists(container, name);
   }

   /**
    * This implementation invokes {@link AzureBlobAsyncClient#getBlobProperties}
    * 
    * @param container
    *           container name
    * @param key
    *           blob key
    */
   @Override
   public ListenableFuture<BlobMetadata> blobMetadata(String container, String key) {
      return Futures.compose(async.getBlobProperties(container, key), new Function<BlobProperties, BlobMetadata>() {

         @Override
         public BlobMetadata apply(BlobProperties from) {
            return blob2BlobMd.apply(from);
         }

      }, service);
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(String container) {
      throw new UnsupportedOperationException("please use deleteContainer");
   }

   @Override
   public ListenableFuture<String> putBlob(String container, Blob blob, PutOptions options) {
      // TODO implement options
      return putBlob(container, blob);
   }

   @Override
   public ListenableFuture<Boolean> createContainerInLocation(Location location, String container,
            CreateContainerOptions options) {
      org.jclouds.azureblob.options.CreateContainerOptions createContainerOptions = new org.jclouds.azureblob.options.CreateContainerOptions();
      if (options.isPublicRead())
         createContainerOptions.withPublicAccess(PublicAccess.CONTAINER);
      return async.createContainer(container, createContainerOptions);
   }
}
