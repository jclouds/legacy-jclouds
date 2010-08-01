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
import static org.jclouds.blobstore.util.BlobStoreUtils.createParentIfNeededAsync;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseAsyncBlobStore;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.concurrent.Futures;
import org.jclouds.domain.Location;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobStoreListContainerOptionsToListContainerOptions;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobToObject;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ContainerToResourceList;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlob;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CloudFilesAsyncBlobStore extends BaseAsyncBlobStore {
   private final CloudFilesClient sync;
   private final CloudFilesAsyncClient async;
   private final ContainerToResourceMetadata container2ResourceMd;
   private final BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions;
   private final ContainerToResourceList container2ResourceList;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final ObjectToBlobMetadata object2BlobMd;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;
   private final Provider<FetchBlobMetadata> fetchBlobMetadataProvider;

   @Inject
   CloudFilesAsyncBlobStore(BlobStoreContext context, BlobUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service, Location defaultLocation,
            Set<? extends Location> locations, CloudFilesClient sync, CloudFilesAsyncClient async,
            ContainerToResourceMetadata container2ResourceMd,
            BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions,
            ContainerToResourceList container2ResourceList, ObjectToBlob object2Blob, BlobToObject blob2Object,
            ObjectToBlobMetadata object2BlobMd, BlobToHttpGetOptions blob2ObjectGetOptions,
            Provider<FetchBlobMetadata> fetchBlobMetadataProvider) {
      super(context, blobUtils, service, defaultLocation, locations);
      this.sync = sync;
      this.async = async;
      this.container2ResourceMd = container2ResourceMd;
      this.container2ContainerListOptions = container2ContainerListOptions;
      this.container2ResourceList = container2ResourceList;
      this.object2Blob = object2Blob;
      this.blob2Object = blob2Object;
      this.object2BlobMd = object2BlobMd;
      this.blob2ObjectGetOptions = blob2ObjectGetOptions;
      this.fetchBlobMetadataProvider = checkNotNull(fetchBlobMetadataProvider, "fetchBlobMetadataProvider");
   }

   /**
    * This implementation invokes {@link CloudFilesAsyncClient#listContainers}
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list() {
      return Futures.compose(async.listContainers(),
               new Function<Set<ContainerMetadata>, org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>>() {
                  public org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata> apply(
                           Set<ContainerMetadata> from) {
                     return new PageSetImpl<StorageMetadata>(Iterables.transform(from, container2ResourceMd), null);
                  }
               }, service);
   }

   /**
    * This implementation invokes {@link CloudFilesAsyncClient#containerExists}
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<Boolean> containerExists(String container) {
      return async.containerExists(container);
   }

   /**
    * Note that location is currently ignored.
    */
   @Override
   public ListenableFuture<Boolean> createContainerInLocation(Location location, String container) {
      return async.createContainer(container);
   }

   /**
    * This implementation invokes {@link CloudFilesAsyncClient#listBucket}
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list(String container, ListContainerOptions options) {
      org.jclouds.rackspace.cloudfiles.options.ListContainerOptions httpOptions = container2ContainerListOptions
               .apply(options);
      ListenableFuture<PageSet<ObjectInfo>> returnVal = async.listObjects(container, httpOptions);
      ListenableFuture<PageSet<? extends StorageMetadata>> list = Futures.compose(returnVal, container2ResourceList,
               service);
      return options.isDetailed() ? Futures.compose(list, fetchBlobMetadataProvider.get().setContainerName(container),
               service) : list;
   }

   /**
    * This implementation invokes {@link CloudFilesAsyncClient#objectExists}
    * 
    * @param container
    *           container name
    * @param key
    *           object key
    */
   @Override
   public ListenableFuture<Boolean> blobExists(String container, String key) {
      return async.objectExists(container, key);
   }

   /**
    * This implementation invokes {@link CloudFilesAsyncClient#headObject}
    * 
    * @param container
    *           container name
    * @param key
    *           object key
    */
   @Override
   public ListenableFuture<BlobMetadata> blobMetadata(String container, String key) {
      return Futures.compose(async.getObjectInfo(container, key),
               new Function<MutableObjectInfoWithMetadata, BlobMetadata>() {

                  @Override
                  public BlobMetadata apply(MutableObjectInfoWithMetadata from) {
                     return object2BlobMd.apply(from);
                  }

               }, service);
   }

   /**
    * This implementation invokes {@link CloudFilesAsyncClient#getObject}
    * 
    * @param container
    *           container name
    * @param key
    *           object key
    */
   @Override
   public ListenableFuture<Blob> getBlob(String container, String key, org.jclouds.blobstore.options.GetOptions options) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(options);
      ListenableFuture<CFObject> returnVal = async.getObject(container, key, httpOptions);
      return Futures.compose(returnVal, object2Blob, service);
   }

   /**
    * This implementation invokes {@link CloudFilesAsyncClient#putObject}
    * 
    * @param container
    *           container name
    * @param blob
    *           object
    */
   @Override
   public ListenableFuture<String> putBlob(String container, Blob blob) {
      createParentIfNeededAsync(this, container, blob);
      return async.putObject(container, blob2Object.apply(blob));
   }

   /**
    * This implementation invokes {@link CloudFilesAsyncClient#removeObject}
    * 
    * @param container
    *           container name
    * @param key
    *           object key
    */
   @Override
   public ListenableFuture<Void> removeBlob(String container, String key) {
      return async.removeObject(container, key);
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(String container) {
      sync.deleteContainerIfEmpty(container);
      return !sync.containerExists(container);
   }

}
