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
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.concurrent.internal.ConcurrentUtils.makeListenable;
import static org.jclouds.azure.storage.options.ListOptions.Builder.*;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

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
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.ContainerProperties;
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
            ClearListStrategy clearContainerStrategy, BlobPropertiesToBlobMetadata object2BlobMd,
            AzureBlobToBlob object2Blob, BlobToAzureBlob blob2Object,
            ListOptionsToListBlobsOptions container2ContainerListOptions,
            BlobToHttpGetOptions blob2ObjectGetOptions, GetDirectoryStrategy getDirectoryStrategy,
            MkdirStrategy mkdirStrategy, ContainerToResourceMetadata container2ResourceMd,
            ListBlobsResponseToResourceList container2ResourceList, ExecutorService service) {
      super(async, sync, blobFactory, logFactory, clearContainerStrategy, object2BlobMd,
               object2Blob, blob2Object, container2ContainerListOptions, blob2ObjectGetOptions,
               getDirectoryStrategy, mkdirStrategy, container2ResourceMd, container2ResourceList,
               service);
   }

   /**
    * This implementation uses the AzureBlob HEAD Object command to return the result
    */
   public ListenableFuture<BlobMetadata> blobMetadata(String container, String key) {
      return compose(async.getBlobProperties(container, key),
               new Function<BlobProperties, BlobMetadata>() {

                  @Override
                  public BlobMetadata apply(BlobProperties from) {
                     return object2BlobMd.apply(from);
                  }

               }, service);
   }

   public ListenableFuture<Void> clearContainer(final String container) {
      return makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            return null;
         }

      }), service);
   }

   /**
    * Note that location is currently ignored.
    */
   public ListenableFuture<Boolean> createContainerInLocation(String location, String container) {
      return async.createContainer(container);
   }

   public ListenableFuture<Void> deleteContainer(final String container) {
      return async.deleteContainer(container);

   }

   public ListenableFuture<Boolean> containerExists(String container) {
      return async.containerExists(container);
   }

   public ListenableFuture<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions... optionsList) {
      GetOptions azureOptions = blob2ObjectGetOptions.apply(optionsList);
      ListenableFuture<AzureBlob> returnVal = async.getBlob(container, key, azureOptions);
      return compose(returnVal, object2Blob, service);
   }

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

   public ListenableFuture<? extends ListContainerResponse<? extends StorageMetadata>> list(
            String container, ListContainerOptions... optionsList) {
      ListBlobsOptions azureOptions = container2ContainerListOptions.apply(optionsList);
      ListenableFuture<ListBlobsResponse> returnVal = async.listBlobs(container, azureOptions
               .includeMetadata());
      return compose(returnVal, container2ResourceList, service);
   }

   public ListenableFuture<String> putBlob(String container, Blob blob) {
      return async.putBlob(container, blob2Object.apply(blob));
   }

   public ListenableFuture<Void> removeBlob(String container, String key) {
      return async.deleteBlob(container, key);
   }

   public ListenableFuture<Void> createDirectory(final String container, final String directory) {
      return makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            mkdirStrategy.execute(AzureAsyncBlobStore.this, container, directory);
            return null;
         }

      }), service);
   }

   public ListenableFuture<Boolean> directoryExists(final String container, final String directory) {
      return makeListenable(service.submit(new Callable<Boolean>() {

         public Boolean call() throws Exception {
            try {
               getDirectoryStrategy.execute(AzureAsyncBlobStore.this, container, directory);
               return true;
            } catch (KeyNotFoundException e) {
               return false;
            }
         }

      }), service);
   }

}
