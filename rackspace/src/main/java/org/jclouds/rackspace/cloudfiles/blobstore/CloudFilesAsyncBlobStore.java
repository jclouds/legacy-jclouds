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

import static com.google.common.util.concurrent.Futures.compose;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.concurrent.ConcurrentUtils.convertExceptionToValue;
import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;

import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.domain.internal.ListResponseImpl;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.GetDirectoryStrategy;
import org.jclouds.blobstore.strategy.MkdirStrategy;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobStoreListContainerOptionsToListContainerOptions;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobToObject;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobToObjectGetOptions;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ContainerToResourceList;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlob;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.rackspace.cloudfiles.blobstore.internal.BaseCloudFilesBlobStore;
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
public class CloudFilesAsyncBlobStore extends BaseCloudFilesBlobStore implements AsyncBlobStore {

   @Inject
   public CloudFilesAsyncBlobStore(CloudFilesAsyncClient async, CloudFilesClient sync,
            Factory blobFactory, LoggerFactory logFactory,
            ClearListStrategy clearContainerStrategy, ObjectToBlobMetadata object2BlobMd,
            ObjectToBlob object2Blob, BlobToObject blob2Object,
            BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions,
            BlobToObjectGetOptions blob2ObjectGetOptions,
            GetDirectoryStrategy getDirectoryStrategy, MkdirStrategy mkdirStrategy,
            ContainerToResourceMetadata container2ResourceMd,
            ContainerToResourceList container2ResourceList,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service) {
      super(async, sync, blobFactory, logFactory, clearContainerStrategy, object2BlobMd,
               object2Blob, blob2Object, container2ContainerListOptions, blob2ObjectGetOptions,
               getDirectoryStrategy, mkdirStrategy, container2ResourceMd, container2ResourceList,
               service);
   }

   /**
    * This implementation invokes {@link CloudFilesAsyncClient#listContainers}
    */
   @Override
   public ListenableFuture<? extends ListResponse<? extends StorageMetadata>> list() {
      return compose(
               async.listContainers(),
               new Function<SortedSet<ContainerMetadata>, org.jclouds.blobstore.domain.ListResponse<? extends StorageMetadata>>() {
                  public org.jclouds.blobstore.domain.ListResponse<? extends StorageMetadata> apply(
                           SortedSet<ContainerMetadata> from) {
                     return new ListResponseImpl<StorageMetadata>(Iterables.transform(from,
                              container2ResourceMd), null, null, false);
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
    * This implementation invokes {@link CloudFilesAsyncClient#listBucket}
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<? extends ListContainerResponse<? extends StorageMetadata>> list(
            String container, ListContainerOptions options) {
      org.jclouds.rackspace.cloudfiles.options.ListContainerOptions httpOptions = container2ContainerListOptions
               .apply(options);
      ListenableFuture<ListContainerResponse<ObjectInfo>> returnVal = async.listObjects(container,
               httpOptions);
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
    * This implementation invokes {@link ClearListStrategy#execute} with the
    * {@link ListContainerOptions#recursive} option. Then, it invokes
    * {@link CloudFilesAsyncClient#deleteContainerIfEmpty}
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<Void> deleteContainer(final String container) {
      return makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            async.deleteContainerIfEmpty(container).get();
            return null;
         }

      }), service);
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

      return compose(convertExceptionToValue(async.getObjectInfo(container, key),
               KeyNotFoundException.class, null),
               new Function<MutableObjectInfoWithMetadata, BlobMetadata>() {

                  @Override
                  public BlobMetadata apply(MutableObjectInfoWithMetadata from) {
                     return object2BlobMd.apply(from);
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
    *           object key
    */
   @Override
   public ListenableFuture<Blob> getBlob(String container, String key) {
      return getBlob(container, key, org.jclouds.blobstore.options.GetOptions.NONE);
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
   public ListenableFuture<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions options) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(options);
      ListenableFuture<CFObject> returnVal = async.getObject(container, key, httpOptions);
      return compose(convertExceptionToValue(returnVal, KeyNotFoundException.class, null),
               object2Blob, service);
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

}
