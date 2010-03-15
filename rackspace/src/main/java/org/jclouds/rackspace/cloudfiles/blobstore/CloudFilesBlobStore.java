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
package org.jclouds.rackspace.cloudfiles.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobStoreListContainerOptionsToListContainerOptions;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobToObject;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ContainerToResourceList;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlob;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CloudFilesBlobStore extends BaseBlobStore {
   private final CloudFilesClient sync;
   private final ContainerToResourceMetadata container2ResourceMd;
   private final BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions;
   private final ContainerToResourceList container2ResourceList;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final ObjectToBlobMetadata object2BlobMd;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;
   private final Provider<FetchBlobMetadata> fetchBlobMetadataProvider;

   @Inject
   CloudFilesBlobStore(BlobStoreContext context, BlobStoreUtils blobUtils, CloudFilesClient sync,
            ContainerToResourceMetadata container2ResourceMd,
            BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions,
            ContainerToResourceList container2ResourceList, ObjectToBlob object2Blob,
            BlobToObject blob2Object, ObjectToBlobMetadata object2BlobMd,
            BlobToHttpGetOptions blob2ObjectGetOptions,
            Provider<FetchBlobMetadata> fetchBlobMetadataProvider) {
      super(context, blobUtils);
      this.sync = sync;
      this.container2ResourceMd = container2ResourceMd;
      this.container2ContainerListOptions = container2ContainerListOptions;
      this.container2ResourceList = container2ResourceList;
      this.object2Blob = object2Blob;
      this.blob2Object = blob2Object;
      this.object2BlobMd = object2BlobMd;
      this.blob2ObjectGetOptions = blob2ObjectGetOptions;
      this.fetchBlobMetadataProvider = checkNotNull(fetchBlobMetadataProvider,
               "fetchBlobMetadataProvider");
   }

   /**
    * This implementation invokes {@link CloudFilesClient#listContainers}
    */
   @Override
   public PageSet<? extends StorageMetadata> list() {
      return new Function<Set<ContainerMetadata>, org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>>() {
         public org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata> apply(
                  Set<ContainerMetadata> from) {
            return new PageSetImpl<StorageMetadata>(
                     Iterables.transform(from, container2ResourceMd), null);
         }
      }.apply(sync.listContainers());
   }

   /**
    * This implementation invokes {@link CloudFilesClient#containerExists}
    * 
    * @param container
    *           container name
    */
   @Override
   public boolean containerExists(String container) {
      return sync.containerExists(container);
   }

   /**
    * This implementation invokes {@link CloudFilesClient#putBucketInRegion}
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
    * This implementation invokes {@link CloudFilesClient#listObjects}
    * 
    * @param container
    *           container name
    */
   @Override
   public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {
      org.jclouds.rackspace.cloudfiles.options.ListContainerOptions httpOptions = container2ContainerListOptions
               .apply(options);
      PageSet<? extends StorageMetadata> list = container2ResourceList.apply(sync.listObjects(
               container, httpOptions));
      return options.isDetailed() ? fetchBlobMetadataProvider.get().setContainerName(container)
               .apply(list) : list;
   }

   /**
    * This implementation invokes {@link CloudFilesClient#blobExists}
    * 
    * @param container
    *           container name
    * @param key
    *           file name
    */
   @Override
   public boolean blobExists(String container, String key) {
      return sync.objectExists(container, key);
   }

   /**
    * This implementation invokes {@link CloudFilesClient#getObjectInfo}
    * 
    * @param container
    *           container name
    * @param key
    *           file name
    */
   @Override
   public BlobMetadata blobMetadata(String container, String key) {
      return object2BlobMd.apply(sync.getObjectInfo(container, key));
   }

   /**
    * This implementation invokes {@link CloudFilesClient#getObject}
    * 
    * @param container
    *           container name
    * @param key
    *           file name
    */
   @Override
   public Blob getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      return object2Blob.apply(sync.getObject(container, key, httpOptions));
   }

   /**
    * This implementation invokes {@link CloudFilesClient#putObject}
    * 
    * @param container
    *           container name
    * @param blob
    *           object
    */
   @Override
   public String putBlob(String container, Blob blob) {
      return sync.putObject(container, blob2Object.apply(blob));
   }

   /**
    * This implementation invokes {@link CloudFilesClient#removeObject}
    * 
    * @param container
    *           container name
    * @param key
    *           file name
    */
   @Override
   public void removeBlob(String container, String key) {
      sync.removeObject(container, key);
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(String container) {
      sync.deleteContainerIfEmpty(container);
      return !sync.containerExists(container);
   }
}
