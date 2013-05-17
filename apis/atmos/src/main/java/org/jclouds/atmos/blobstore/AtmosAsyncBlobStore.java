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
package org.jclouds.atmos.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.transform;
import static org.jclouds.atmos.options.PutOptions.Builder.publicRead;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.atmos.AtmosAsyncClient;
import org.jclouds.atmos.AtmosClient;
import org.jclouds.atmos.blobstore.functions.BlobStoreListOptionsToListOptions;
import org.jclouds.atmos.blobstore.functions.BlobToObject;
import org.jclouds.atmos.blobstore.functions.DirectoryEntryListToResourceMetadataList;
import org.jclouds.atmos.blobstore.functions.ObjectToBlob;
import org.jclouds.atmos.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.domain.BoundedSet;
import org.jclouds.atmos.domain.DirectoryEntry;
import org.jclouds.atmos.options.ListOptions;
import org.jclouds.atmos.util.AtmosUtils;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseAsyncBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.crypto.Crypto;
import org.jclouds.domain.Location;
import org.jclouds.http.options.GetOptions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * @author Adrian Cole
 * @deprecated will be removed in jclouds 1.7, as async interfaces are no longer
 *             supported. Please use {@link AtmosBlobStore}
 */
@Deprecated
@Singleton
public class AtmosAsyncBlobStore extends BaseAsyncBlobStore {
   private final AtmosAsyncClient async;
   private final AtmosClient sync;
   private final ObjectToBlob object2Blob;
   private final ObjectToBlobMetadata object2BlobMd;
   private final BlobToObject blob2Object;
   private final BlobStoreListOptionsToListOptions container2ContainerListOptions;
   private final DirectoryEntryListToResourceMetadataList container2ResourceList;
   private final Crypto crypto;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;
   private final Provider<FetchBlobMetadata> fetchBlobMetadataProvider;
   private final LoadingCache<String, Boolean> isPublic;

   @Inject
   AtmosAsyncBlobStore(BlobStoreContext context, BlobUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, AtmosAsyncClient async, AtmosClient sync,
            ObjectToBlob object2Blob, ObjectToBlobMetadata object2BlobMd, BlobToObject blob2Object,
            BlobStoreListOptionsToListOptions container2ContainerListOptions,
            DirectoryEntryListToResourceMetadataList container2ResourceList, Crypto crypto,
            BlobToHttpGetOptions blob2ObjectGetOptions, Provider<FetchBlobMetadata> fetchBlobMetadataProvider,
            LoadingCache<String, Boolean> isPublic) {
      super(context, blobUtils, userExecutor, defaultLocation, locations);
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
      this.sync = checkNotNull(sync, "sync");
      this.async = checkNotNull(async, "async");
      this.container2ContainerListOptions = checkNotNull(container2ContainerListOptions,
               "container2ContainerListOptions");
      this.container2ResourceList = checkNotNull(container2ResourceList, "container2ResourceList");
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.object2BlobMd = checkNotNull(object2BlobMd, "object2BlobMd");
      this.crypto = checkNotNull(crypto, "crypto");
      this.fetchBlobMetadataProvider = checkNotNull(fetchBlobMetadataProvider, "fetchBlobMetadataProvider");
      this.isPublic = checkNotNull(isPublic, "isPublic");
   }

   /**
    * This implementation invokes {@link AtmosAsyncClient#headFile}
    */
   @Override
   public ListenableFuture<BlobMetadata> blobMetadata(String container, String key) {
      return transform(async.headFile(container + "/" + key), new Function<AtmosObject, BlobMetadata>() {
         @Override
         public BlobMetadata apply(AtmosObject from) {
            return object2BlobMd.apply(from);
         }
      }, userExecutor);
   }

   /**
    * This implementation invokes {@link AtmosAsyncClient#createDirectory}
    * <p/>
    * Note location is ignored
    */
   @Override
   public ListenableFuture<Boolean> createContainerInLocation(Location location, String container) {
      return transform(async.createDirectory(container), new Function<URI, Boolean>() {
         public Boolean apply(URI from) {
            return true;
         }
      }, userExecutor);
   }

   /**
    * This implementation invokes {@link AtmosAsyncClient#createDirectory}
    */
   @Override
   public ListenableFuture<Void> createDirectory(String container, String directory) {
      return transform(async.createDirectory(container + "/" + directory), new Function<URI, Void>() {
         public Void apply(URI from) {
            return null;// no etag
         }
      }, userExecutor);
   }

   /**
    * This implementation invokes {@link AtmosAsyncClient#deletePath} followed by
    * {@link AtmosAsyncClient#pathExists} until it is true.
    */
   protected boolean deleteAndVerifyContainerGone(final String container) {
      sync.deletePath(container + "/");
      return !sync.pathExists(container + "/");
   }

   /**
    * This implementation invokes {@link AtmosAsyncClient#pathExists}
    */
   @Override
   public ListenableFuture<Boolean> containerExists(String container) {
      return async.pathExists(container + "/");
   }

   /**
    * This implementation invokes {@link AtmosAsyncClient#pathExists}
    */
   @Override
   public ListenableFuture<Boolean> directoryExists(String container, String directory) {
      return async.pathExists(container + "/" + directory + "/");
   }

   /**
    * This implementation invokes {@link #removeBlob}
    */
   @Override
   public ListenableFuture<Void> deleteDirectory(String containerName, String directory) {
      return removeBlob(containerName, directory + "/");
   }

   /**
    * This implementation invokes {@link AtmosAsyncClient#pathExists}
    * 
    * @param container
    *           container
    * @param key
    *           file name
    */
   @Override
   public ListenableFuture<Boolean> blobExists(String container, String key) {
      return async.pathExists(container + "/" + key);
   }

   /**
    * This implementation invokes {@link AtmosAsyncClient#readFile}
    */
   @Override
   public ListenableFuture<Blob> getBlob(String container, String key, org.jclouds.blobstore.options.GetOptions options) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(options);
      ListenableFuture<AtmosObject> returnVal = async.readFile(container + "/" + key, httpOptions);
      return transform(returnVal, object2Blob, userExecutor);
   }

   /**
    * This implementation invokes {@link AtmosAsyncClient#listDirectories}
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list() {
      return transform(async.listDirectories(), container2ResourceList, userExecutor);
   }

   /**
    * This implementation invokes {@link AtmosAsyncClient#listDirectory}
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list(String container,
            org.jclouds.blobstore.options.ListContainerOptions options) {
      container = AtmosUtils.adjustContainerIfDirOptionPresent(container, options);
      ListOptions nativeOptions = container2ContainerListOptions.apply(options);
      ListenableFuture<BoundedSet<? extends DirectoryEntry>> returnVal = async.listDirectory(container, nativeOptions);
      ListenableFuture<PageSet<? extends StorageMetadata>> list = transform(returnVal, container2ResourceList,
            userExecutor);
      return options.isDetailed() ? transform(list,
               fetchBlobMetadataProvider.get().setContainerName(container)) : list;
   }

   /**
    * This implementation invokes {@link AtmosAsyncClient#createFile}
    * <p/>
    * Since there is no etag support in atmos, we just return the path.
    */
   @Override
   public ListenableFuture<String> putBlob(final String container, final Blob blob) {
      final org.jclouds.atmos.options.PutOptions options = new org.jclouds.atmos.options.PutOptions();
      try {
         if (isPublic.getUnchecked(container + "/"))
            options.publicRead();
      } catch (CacheLoader.InvalidCacheLoadException e) {
         // nulls not permitted
      }
      return userExecutor.submit(new Callable<String>() {

         @Override
         public String call() throws Exception {
            return AtmosUtils.putBlob(sync, crypto, blob2Object, container, blob, options);
         }

         @Override
         public String toString() {
            return "putBlob(" + container + "," + blob.getMetadata().getName() + ")";
         }
      });

   }

   /**
    * This implementation invokes {@link AtmosAsyncClient#deletePath}
    */
   @Override
   public ListenableFuture<Void> removeBlob(String container, String key) {
      return async.deletePath(container + "/" + key);
   }

   @Override
   public ListenableFuture<String> putBlob(String container, Blob blob, PutOptions options) {
      // TODO implement options
      return putBlob(container, blob);
   }

   @Override
   public ListenableFuture<Boolean> createContainerInLocation(Location location, String container,
            CreateContainerOptions options) {
      if (options.isPublicRead())
         return transform(async.createDirectory(container, publicRead()), new Function<URI, Boolean>() {

            public Boolean apply(URI from) {
               return true;
            }

         }, userExecutor);
      return createContainerInLocation(location, container);
   }

}
