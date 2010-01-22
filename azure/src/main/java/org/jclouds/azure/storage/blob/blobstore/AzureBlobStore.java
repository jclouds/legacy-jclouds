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

import static org.jclouds.azure.storage.options.ListOptions.Builder.includeMetadata;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.blobstore.util.BlobStoreUtils.returnNullOnKeyNotFoundOrPropagate;

import java.util.Set;
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
import org.jclouds.azure.storage.blob.domain.ContainerProperties;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
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

public class AzureBlobStore extends BaseAzureBlobStore implements BlobStore {
   private final AzureAsyncBlobStore aBlobStore;

   @Inject
   public AzureBlobStore(AzureAsyncBlobStore aBlobStore, AzureBlobAsyncClient async,
            AzureBlobClient sync, Factory blobFactory, LoggerFactory logFactory,
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
      this.aBlobStore = aBlobStore;
   }

   /**
    * This implementation uses the AzureBlob HEAD Object command to return the result
    */
   public BlobMetadata blobMetadata(String container, String key) {
      try {
         return object2BlobMd.apply(sync.getBlobProperties(container, key));
      } catch (Exception e) {
         return returnNullOnKeyNotFoundOrPropagate(e);
      }
   }

   public void clearContainer(final String container) {
      clearContainerStrategy.execute(container, recursive());
   }

   /**
    * Note that location is currently ignored.
    */
   public boolean createContainerInLocation(String location, String container) {
      return sync.createContainer(container);
   }

   public void deleteContainer(final String container) {
      sync.deleteContainer(container);
   }

   public boolean directoryExists(String containerName, String directory) {
      try {
         getDirectoryStrategy.execute(aBlobStore, containerName, directory);
         return true;
      } catch (KeyNotFoundException e) {
         return false;
      }
   }

   public void createDirectory(String containerName, String directory) {
      mkdirStrategy.execute(aBlobStore, containerName, directory);
   }

   public boolean containerExists(String container) {
      return sync.containerExists(container);
   }

   public Blob getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions... optionsList) {
      GetOptions azureOptions = blob2ObjectGetOptions.apply(optionsList);
      try {
         return object2Blob.apply(sync.getBlob(container, key, azureOptions));
      } catch (Exception e) {
         return returnNullOnKeyNotFoundOrPropagate(e);
      }
   }

   public ListResponse<? extends StorageMetadata> list() {
      return new Function<Set<ContainerProperties>, org.jclouds.blobstore.domain.ListResponse<? extends StorageMetadata>>() {
         public org.jclouds.blobstore.domain.ListResponse<? extends StorageMetadata> apply(
                  Set<ContainerProperties> from) {
            return new ListResponseImpl<StorageMetadata>(Iterables.transform(from,
                     container2ResourceMd), null, null, false);
         }
      }.apply(sync.listContainers(includeMetadata()));
   }

   public ListContainerResponse<? extends StorageMetadata> list(String container,
            ListContainerOptions... optionsList) {
      ListBlobsOptions azureOptions = container2ContainerListOptions.apply(optionsList);
      return container2ResourceList
               .apply(sync.listBlobs(container, azureOptions.includeMetadata()));
   }

   public String putBlob(String container, Blob blob) {
      return sync.putBlob(container, blob2Object.apply(blob));
   }

   public void removeBlob(String container, String key) {
      sync.deleteBlob(container, key);
   }

}
