/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.azure.storage.blob.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.compose;
import static org.jclouds.azure.storage.options.ListOptions.Builder.includeMetadata;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.azure.storage.blob.AzureBlobAsyncClient;
import org.jclouds.azure.storage.blob.blobstore.functions.AzureBlobToBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobPropertiesToBlobMetadata;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobToAzureBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.azure.storage.blob.blobstore.functions.ListBlobsResponseToResourceList;
import org.jclouds.azure.storage.blob.blobstore.functions.ListOptionsToListBlobsOptions;
import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.azure.storage.blob.domain.ContainerProperties;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseAsyncBlobStore;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.domain.Location;
import org.jclouds.http.options.GetOptions;

import com.google.common.base.Function;
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
   AzureAsyncBlobStore(BlobStoreContext context, BlobStoreUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service,
            Location defaultLocation, Set<? extends Location> locations,

            AzureBlobAsyncClient async, ContainerToResourceMetadata container2ResourceMd,
            ListOptionsToListBlobsOptions blobStore2AzureContainerListOptions,
            ListBlobsResponseToResourceList azure2BlobStoreResourceList,
            AzureBlobToBlob azureBlob2Blob, BlobToAzureBlob blob2AzureBlob,
            BlobPropertiesToBlobMetadata blob2BlobMd, BlobToHttpGetOptions blob2ObjectGetOptions) {
      super(context, blobUtils, service, defaultLocation, locations);
      this.async = checkNotNull(async, "async");
      this.container2ResourceMd = checkNotNull(container2ResourceMd, "container2ResourceMd");
      this.blobStore2AzureContainerListOptions = checkNotNull(blobStore2AzureContainerListOptions,
               "blobStore2AzureContainerListOptions");
      this.azure2BlobStoreResourceList = checkNotNull(azure2BlobStoreResourceList,
               "azure2BlobStoreResourceList");
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
   public ListenableFuture<? extends org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>> list() {
      return compose(
               async.listContainers(includeMetadata()),
               new Function<BoundedSet<ContainerProperties>, org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>>() {
                  public org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata> apply(
                           BoundedSet<ContainerProperties> from) {
                     return new PageSetImpl<StorageMetadata>(Iterables.transform(from,
                              container2ResourceMd), from.getNextMarker());
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
   public ListenableFuture<? extends PageSet<? extends StorageMetadata>> list(String container,
            ListContainerOptions options) {
      ListBlobsOptions azureOptions = blobStore2AzureContainerListOptions.apply(options);
      ListenableFuture<ListBlobsResponse> returnVal = async.listBlobs(container, azureOptions
               .includeMetadata());
      return compose(returnVal, azure2BlobStoreResourceList, service);
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
   public ListenableFuture<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions options) {
      GetOptions azureOptions = blob2ObjectGetOptions.apply(options);
      ListenableFuture<AzureBlob> returnVal = async.getBlob(container, key, azureOptions);
      return compose(returnVal, azureBlob2Blob, service);
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
    * @param key
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
      return compose(async.getBlobProperties(container, key),
               new Function<BlobProperties, BlobMetadata>() {

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

}
