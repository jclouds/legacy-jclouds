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
import static org.jclouds.azure.storage.options.ListOptions.Builder.includeMetadata;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.blob.blobstore.functions.AzureBlobToBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobPropertiesToBlobMetadata;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobToAzureBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.azure.storage.blob.blobstore.functions.ListBlobsResponseToResourceList;
import org.jclouds.azure.storage.blob.blobstore.functions.ListOptionsToListBlobsOptions;
import org.jclouds.azure.storage.blob.domain.ContainerProperties;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.http.options.GetOptions;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class AzureBlobStore extends BaseBlobStore {
   private final AzureBlobClient sync;
   private final ContainerToResourceMetadata container2ResourceMd;
   private final ListOptionsToListBlobsOptions blobStore2AzureContainerListOptions;
   private final ListBlobsResponseToResourceList azure2BlobStoreResourceList;
   private final AzureBlobToBlob azureBlob2Blob;
   private final BlobToAzureBlob blob2AzureBlob;
   private final BlobPropertiesToBlobMetadata blob2BlobMd;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;

   @Inject
   AzureBlobStore(BlobStoreUtils blobUtils, AzureBlobClient sync,
            ContainerToResourceMetadata container2ResourceMd,
            ListOptionsToListBlobsOptions blobStore2AzureContainerListOptions,
            ListBlobsResponseToResourceList azure2BlobStoreResourceList,
            AzureBlobToBlob azureBlob2Blob, BlobToAzureBlob blob2AzureBlob,
            BlobPropertiesToBlobMetadata blob2BlobMd, BlobToHttpGetOptions blob2ObjectGetOptions) {
      super(blobUtils);
      this.sync = checkNotNull(sync, "sync");
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
    * This implementation invokes {@link AzureBlobClient#listContainers}
    */
   @Override
   public PageSet<? extends StorageMetadata> list() {
      return new Function<BoundedSet<ContainerProperties>, org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>>() {
         public org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata> apply(
                  BoundedSet<ContainerProperties> from) {
            return new PageSetImpl<StorageMetadata>(
                     Iterables.transform(from, container2ResourceMd), from.getNextMarker());
         }
         // TODO this may be a list that isn't complete due to 1000 container limit
      }.apply(sync.listContainers(includeMetadata()));
   }

   /**
    * This implementation invokes {@link AzureBlobClient#bucketExists}
    * 
    * @param container
    *           container name
    */
   @Override
   public boolean containerExists(String container) {
      return sync.containerExists(container);
   }

   /**
    * This implementation invokes {@link AzureBlobClient#putBucketInRegion}
    * 
    * @param location
    *           currently ignored
    * @param container
    *           container name
    */
   @Override
   public boolean createContainerInLocation(String location, String container) {
      return sync.createContainer(container);
   }

   /**
    * This implementation invokes {@link AzureBlobClient#listBlobs}
    * 
    * @param container
    *           container name
    */
   @Override
   public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {
      ListBlobsOptions azureOptions = blobStore2AzureContainerListOptions.apply(options);
      return azure2BlobStoreResourceList.apply(sync.listBlobs(container, azureOptions
               .includeMetadata()));
   }

   /**
    * This implementation invokes {@link AzureBlobClient#deleteContainer}
    * 
    * @param container
    *           container name
    */
   @Override
   public void deleteContainer(final String container) {
      sync.deleteContainer(container);
   }

   /**
    * This implementation invokes {@link AzureBlobClient#blobExists}
    * 
    * @param container
    *           container name
    * @param key
    *           blob key
    */
   @Override
   public boolean blobExists(String container, String key) {
      return sync.blobExists(container, key);
   }

   /**
    * This implementation invokes {@link AzureBlobClient#getBlob}
    * 
    * @param container
    *           container name
    * @param key
    *           blob key
    */
   @Override
   public Blob getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions options) {
      GetOptions azureOptions = blob2ObjectGetOptions.apply(options);
      return azureBlob2Blob.apply(sync.getBlob(container, key, azureOptions));

   }

   /**
    * This implementation invokes {@link AzureBlobClient#putObject}
    * 
    * @param container
    *           container name
    * @param blob
    *           object
    */
   @Override
   public String putBlob(String container, Blob blob) {
      return sync.putBlob(container, blob2AzureBlob.apply(blob));
   }

   /**
    * This implementation invokes {@link AzureBlobClient#deleteObject}
    * 
    * @param container
    *           container name
    * @param key
    *           blob key
    */
   @Override
   public void removeBlob(String container, String key) {
      sync.deleteBlob(container, key);
   }

   /**
    * This implementation invokes {@link AzureBlobClient#getBlobProperties}
    * 
    * @param container
    *           container name
    * @param key
    *           blob key
    */
   @Override
   public BlobMetadata blobMetadata(String container, String key) {
      return blob2BlobMd.apply(sync.getBlobProperties(container, key));
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(String container) {
      throw new UnsupportedOperationException("please use deleteContainer");
   }

}
