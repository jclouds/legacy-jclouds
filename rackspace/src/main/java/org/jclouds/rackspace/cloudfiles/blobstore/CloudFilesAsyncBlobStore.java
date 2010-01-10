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
import static com.google.common.util.concurrent.Futures.makeListenable;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;

import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
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
            ContainerToResourceList container2ResourceList, ExecutorService service) {
      super(async, sync, blobFactory, logFactory, clearContainerStrategy, object2BlobMd,
               object2Blob, blob2Object, container2ContainerListOptions, blob2ObjectGetOptions,
               getDirectoryStrategy, mkdirStrategy, container2ResourceMd, container2ResourceList,
               service);
   }

   /**
    * This implementation uses the CloudFiles HEAD Object command to return the result
    */
   public ListenableFuture<BlobMetadata> blobMetadata(String container, String key) {

      return compose(async.getObjectInfo(container, key),
               new Function<MutableObjectInfoWithMetadata, BlobMetadata>() {

                  @Override
                  public BlobMetadata apply(MutableObjectInfoWithMetadata from) {
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

      }));
   }

   public ListenableFuture<Boolean> createContainer(String container) {
      return async.createContainer(container);
   }

   public ListenableFuture<Void> deleteContainer(final String container) {
      return makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            async.deleteContainerIfEmpty(container).get();
            return null;
         }

      }));
   }

   public ListenableFuture<Boolean> containerExists(String container) {
      return async.containerExists(container);
   }

   public ListenableFuture<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions... optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      ListenableFuture<CFObject> returnVal = async.getObject(container, key, httpOptions);
      return compose(returnVal, object2Blob, service);
   }

   public ListenableFuture<? extends ListResponse<? extends ResourceMetadata>> list() {
      return compose(
               async.listContainers(),
               new Function<SortedSet<ContainerMetadata>, org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata>>() {
                  public org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata> apply(
                           SortedSet<ContainerMetadata> from) {
                     return new ListResponseImpl<ResourceMetadata>(Iterables.transform(from,
                              container2ResourceMd), null, null, false);
                  }
               }, service);
   }

   public ListenableFuture<? extends ListContainerResponse<? extends ResourceMetadata>> list(
            String container, ListContainerOptions... optionsList) {
      org.jclouds.rackspace.cloudfiles.options.ListContainerOptions httpOptions = container2ContainerListOptions
               .apply(optionsList);
      ListenableFuture<ListContainerResponse<ObjectInfo>> returnVal = async.listObjects(container,
               httpOptions);
      return compose(returnVal, container2ResourceList, service);
   }

   public ListenableFuture<String> putBlob(String container, Blob blob) {
      return async.putObject(container, blob2Object.apply(blob));
   }

   public ListenableFuture<Void> removeBlob(String container, String key) {
      return async.removeObject(container, key);
   }

   public ListenableFuture<Void> createDirectory(final String container, final String directory) {
      return makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            mkdirStrategy.execute(CloudFilesAsyncBlobStore.this, container, directory);
            return null;
         }

      }));
   }

   public ListenableFuture<Boolean> directoryExists(final String container, final String directory) {
      return makeListenable(service.submit(new Callable<Boolean>() {
         public Boolean call() throws Exception {
            try {
               getDirectoryStrategy.execute(CloudFilesAsyncBlobStore.this, container, directory);
               return true;
            } catch (KeyNotFoundException e) {
               return false;
            }
         }

      }));
   }

}
