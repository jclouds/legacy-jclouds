/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.transform;
import static org.jclouds.blobstore.util.BlobStoreUtils.createParentIfNeededAsync;

import java.util.Set;

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
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.http.options.GetOptions;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.blobstore.functions.BlobStoreListContainerOptionsToListContainerOptions;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.blobstore.functions.ContainerToResourceList;
import org.jclouds.openstack.swift.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlob;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.openstack.swift.blobstore.strategy.internal.AsyncMultipartUploadStrategy;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.domain.SwiftObject;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * 
 * @author Adrian Cole
 * @deprecated will be removed in jclouds 1.7, as async interfaces are no longer
 *             supported. Please use {@link SwiftBlobStore}
 */
@Deprecated
@Singleton
public class SwiftAsyncBlobStore extends BaseAsyncBlobStore {
   private final CommonSwiftClient sync;
   private final CommonSwiftAsyncClient async;
   private final ContainerToResourceMetadata container2ResourceMd;
   private final BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions;
   private final ContainerToResourceList container2ResourceList;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final ObjectToBlobMetadata object2BlobMd;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;
   private final Provider<FetchBlobMetadata> fetchBlobMetadataProvider;
   private final Provider<AsyncMultipartUploadStrategy> multipartUploadStrategy;

   @Inject
   protected SwiftAsyncBlobStore(BlobStoreContext context, BlobUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, CommonSwiftClient sync,
            CommonSwiftAsyncClient async, ContainerToResourceMetadata container2ResourceMd,
            BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions,
            ContainerToResourceList container2ResourceList, ObjectToBlob object2Blob, BlobToObject blob2Object,
            ObjectToBlobMetadata object2BlobMd, BlobToHttpGetOptions blob2ObjectGetOptions,
            Provider<FetchBlobMetadata> fetchBlobMetadataProvider,
            Provider<AsyncMultipartUploadStrategy> multipartUploadStrategy) {
      super(context, blobUtils, userExecutor, defaultLocation, locations);
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
      this.multipartUploadStrategy = multipartUploadStrategy;
   }

   /**
    * This implementation invokes {@link CommonSwiftAsyncClient#listContainers}
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list() {
      return transform(async.listContainers(),
               new Function<Set<ContainerMetadata>, org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>>() {
                  public org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata> apply(
                           Set<ContainerMetadata> from) {
                     return new PageSetImpl<StorageMetadata>(Iterables.transform(from, container2ResourceMd), null);
                  }
               }, userExecutor);
   }

   /**
    * This implementation invokes {@link CommonSwiftAsyncClient#containerExists}
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
    * This implementation invokes {@link CommonSwiftAsyncClient#listBucket}
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list(String container, ListContainerOptions options) {
      org.jclouds.openstack.swift.options.ListContainerOptions httpOptions = container2ContainerListOptions
               .apply(options);
      ListenableFuture<PageSet<ObjectInfo>> returnVal = async.listObjects(container, httpOptions);
      ListenableFuture<PageSet<? extends StorageMetadata>> list = transform(returnVal, container2ResourceList,
               userExecutor);
      return options.isDetailed() ? transform(list, fetchBlobMetadataProvider.get().setContainerName(container),
               userExecutor) : list;
   }

   /**
    * This implementation invokes {@link CommonSwiftAsyncClient#objectExists}
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
    * This implementation invokes {@link CommonSwiftAsyncClient#headObject}
    * 
    * @param container
    *           container name
    * @param key
    *           object key
    */
   @Override
   public ListenableFuture<BlobMetadata> blobMetadata(String container, String key) {
      return transform(async.getObjectInfo(container, key),
               new Function<MutableObjectInfoWithMetadata, BlobMetadata>() {

                  @Override
                  public BlobMetadata apply(MutableObjectInfoWithMetadata from) {
                     return object2BlobMd.apply(from);
                  }

               }, userExecutor);
   }

   /**
    * This implementation invokes {@link CommonSwiftAsyncClient#getObject}
    * 
    * @param container
    *           container name
    * @param key
    *           object key
    */
   @Override
   public ListenableFuture<Blob> getBlob(String container, String key, org.jclouds.blobstore.options.GetOptions options) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(options);
      ListenableFuture<SwiftObject> returnVal = async.getObject(container, key, httpOptions);
      return transform(returnVal, object2Blob, userExecutor);
   }

   /**
    * This implementation invokes {@link CommonSwiftAsyncClient#putObject}
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
    * This implementation invokes {@link CommonSwiftAsyncClient#removeObject}
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

   @Override
   public ListenableFuture<String> putBlob(String container, Blob blob, PutOptions options) {
      if (options.isMultipart()) {
          return multipartUploadStrategy.get().execute(container, blob, options, blob2Object);
      } else {
        return putBlob(container, blob);
      }
   }

   @Override
   public ListenableFuture<Boolean> createContainerInLocation(Location location, String container,
            CreateContainerOptions options) {
      if (options.isPublicRead())
         throw new UnsupportedOperationException("publicRead");
      return createContainerInLocation(location, container);
   }
}
