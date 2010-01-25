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

import static com.google.common.util.concurrent.Futures.compose;
import static org.jclouds.azure.storage.options.ListOptions.Builder.includeMetadata;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.concurrent.ConcurrentUtils.convertExceptionToValue;
import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.azure.storage.blob.AzureBlobAsyncClient;
import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.blob.blobstore.functions.AzureBlobToBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobPropertiesToBlobMetadata;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobToAzureBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.azure.storage.blob.blobstore.functions.ListBlobsResponseToResourceList;
import org.jclouds.azure.storage.blob.blobstore.functions.ListOptionsToListBlobsOptions;
import org.jclouds.azure.storage.blob.blobstore.internal.BaseAzureBlobStore;
import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.azure.storage.blob.domain.ContainerProperties;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.domain.internal.ListResponseImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.GetDirectoryStrategy;
import org.jclouds.blobstore.strategy.MkdirStrategy;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
public class AzureAsyncBlobStore extends BaseAzureBlobStore implements AsyncBlobStore {

   @Inject
   public AzureAsyncBlobStore(AzureBlobAsyncClient async, AzureBlobClient sync,
            Factory blobFactory, LoggerFactory logFactory,
            ClearListStrategy clearContainerStrategy, BlobPropertiesToBlobMetadata blob2BlobMd,
            AzureBlobToBlob blob2Blob, BlobToAzureBlob blob2Object,
            ListOptionsToListBlobsOptions container2ContainerListOptions,
            BlobToHttpGetOptions blob2ObjectGetOptions, GetDirectoryStrategy getDirectoryStrategy,
            MkdirStrategy mkdirStrategy, ContainerToResourceMetadata container2ResourceMd,
            ListBlobsResponseToResourceList container2ResourceList,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service) {
      super(async, sync, blobFactory, logFactory, clearContainerStrategy, blob2BlobMd, blob2Blob,
               blob2Object, container2ContainerListOptions, blob2ObjectGetOptions,
               getDirectoryStrategy, mkdirStrategy, container2ResourceMd, container2ResourceList,
               service);
   }

   /**
    * This implementation invokes {@link AzureBlobAsyncClient#listContainers} with the
    * {@link org.jclouds.azure.storage.options.ListOptions#includeMetadata} option.
    */
   @Override
   public ListenableFuture<? extends org.jclouds.blobstore.domain.ListResponse<? extends StorageMetadata>> list() {
      return compose(
               async.listContainers(includeMetadata()),
               new Function<Set<ContainerProperties>, org.jclouds.blobstore.domain.ListResponse<? extends StorageMetadata>>() {
                  public org.jclouds.blobstore.domain.ListResponse<? extends StorageMetadata> apply(
                           Set<ContainerProperties> from) {
                     return new ListResponseImpl<StorageMetadata>(Iterables.transform(from,
                              container2ResourceMd), null, null, false);
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
   public ListenableFuture<Boolean> createContainerInLocation(String location, String container) {
      return async.createContainer(container);
   }

   /**
    * This implementation invokes
    * {@link #list(String,org.jclouds.blobstore.options.ListContainerOptions)}
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<? extends ListContainerResponse<? extends StorageMetadata>> list(
            String container) {
      return this.list(container, org.jclouds.blobstore.options.ListContainerOptions.NONE);
   }

   /**
    * This implementation invokes {@link AzureBlobAsyncClient#listBucket}
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<? extends ListContainerResponse<? extends StorageMetadata>> list(
            String container, ListContainerOptions options) {
      ListBlobsOptions azureOptions = container2ContainerListOptions.apply(options);
      ListenableFuture<ListBlobsResponse> returnVal = async.listBlobs(container, azureOptions
               .includeMetadata());
      return compose(returnVal, container2ResourceList, service);
   }

   /**
    * This implementation invokes {@link ClearListStrategy#execute} with the
    * {@link ListContainerOptions#recursive} option.
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<Void> clearContainer(final String container) {
      return makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            return null;
         }

      }), service);
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
    * This implementation invokes {@link GetDirectoryStrategy#execute}
    * 
    * @param container
    *           container name
    * @param directory
    *           virtual path
    */
   @Override
   public ListenableFuture<Boolean> directoryExists(final String container, final String directory) {
      return makeListenable(service.submit(new Callable<Boolean>() {

         public Boolean call() throws Exception {
            try {
               getDirectoryStrategy.execute(container, directory);
               return true;
            } catch (KeyNotFoundException e) {
               return false;
            }
         }

      }), service);
   }

   /**
    * This implementation invokes {@link MkdirStrategy#execute}
    * 
    * @param container
    *           container name
    * @param directory
    *           virtual path
    */
   @Override
   public ListenableFuture<Void> createDirectory(final String container, final String directory) {
      return makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            mkdirStrategy.execute(container, directory);
            return null;
         }

      }), service);
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
      return compose(convertExceptionToValue(async.getBlobProperties(container, key),
               KeyNotFoundException.class, null), new Function<BlobProperties, BlobMetadata>() {

         @Override
         public BlobMetadata apply(BlobProperties from) {
            return blob2BlobMd.apply(from);
         }

      }, service);
   }

   /**
    * This implementation invokes
    * {@link #getBlob(String,String,org.jclouds.blobstore.options.GetOptions)}
    * 
    * @param container
    *           container name
    * @param key
    *           blob key
    */
   @Override
   public ListenableFuture<Blob> getBlob(String container, String key) {
      return getBlob(container, key, org.jclouds.blobstore.options.GetOptions.NONE);
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
      return compose(convertExceptionToValue(returnVal, KeyNotFoundException.class, null),
               blob2Blob, service);
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
      return async.putBlob(container, blob2Object.apply(blob));
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

}
