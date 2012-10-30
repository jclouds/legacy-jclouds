/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.blobstore.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.util.Assertions;

import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseAsyncBlobStore implements AsyncBlobStore {

   protected final BlobStoreContext context;
   protected final BlobUtils blobUtils;
   protected final ExecutorService service;
   protected final Supplier<Location> defaultLocation;
   protected final Supplier<Set<? extends Location>> locations;

   @Inject
   protected BaseAsyncBlobStore(BlobStoreContext context, BlobUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations) {
      this.context = checkNotNull(context, "context");
      this.blobUtils = checkNotNull(blobUtils, "blobUtils");
      this.service = checkNotNull(service, "service");
      this.defaultLocation = checkNotNull(defaultLocation, "defaultLocation");
      this.locations = checkNotNull(locations, "locations");
   }

   @Override
   public BlobStoreContext getContext() {
      return context;
   }

   /**
    * invokes {@link BlobUtilsImpl#blobBuilder }
    */
   @Override
   public BlobBuilder blobBuilder(String name) {
      return blobUtils.blobBuilder().name(name);
   }

   /**
    * This implementation invokes
    * {@link #list(String,org.jclouds.blobstore.options.ListContainerOptions)}
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list(String container) {
      return this.list(container, org.jclouds.blobstore.options.ListContainerOptions.NONE);
   }

   /**
    * This implementation invokes {@link #countBlobs} with the
    * {@link ListContainerOptions#recursive} option.
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<Long> countBlobs(String container) {
      return countBlobs(container, recursive());
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#countBlobs}
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<Long> countBlobs(final String containerName, final ListContainerOptions options) {
      return org.jclouds.concurrent.Futures.makeListenable(service.submit(new Callable<Long>() {
         public Long call() throws Exception {
            return blobUtils.countBlobs(containerName, options);
         }

         @Override
         public String toString() {
            return "countBlobs(" + containerName + ")";
         }
      }), service);
   }

   /**
    * This implementation invokes {@link #clearContainer} with the
    * {@link ListContainerOptions#recursive} option.
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<Void> clearContainer(final String container) {
      return clearContainer(container, recursive());
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#clearContainer}
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<Void> clearContainer(final String containerName, final ListContainerOptions options) {
      return org.jclouds.concurrent.Futures.makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            blobUtils.clearContainer(containerName, options);
            return null;
         }

         @Override
         public String toString() {
            return "clearContainer(" + containerName + ")";
         }
      }), service);
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#deleteDirectory}.
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<Void> deleteDirectory(final String containerName, final String directory) {
      return org.jclouds.concurrent.Futures.makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            blobUtils.deleteDirectory(containerName, directory);
            return null;
         }

         @Override
         public String toString() {
            return "deleteDirectory(" + containerName + "," + directory + ")";
         }
      }), service);
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#directoryExists}
    * 
    * @param container
    *           container name
    * @param directory
    *           virtual path
    */
   public ListenableFuture<Boolean> directoryExists(final String containerName, final String directory) {
      return org.jclouds.concurrent.Futures.makeListenable(service.submit(new Callable<Boolean>() {

         public Boolean call() throws Exception {
            return blobUtils.directoryExists(containerName, directory);
         }

         @Override
         public String toString() {
            return "directoryExists(" + containerName + "," + directory + ")";
         }
      }), service);
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#createDirectory}
    * 
    * @param container
    *           container name
    * @param directory
    *           virtual path
    */

   public ListenableFuture<Void> createDirectory(final String containerName, final String directory) {

      return blobUtils.directoryExists(containerName, directory) ? Futures.immediateFuture((Void) null)
               : org.jclouds.concurrent.Futures.makeListenable(service.submit(new Callable<Void>() {
                  public Void call() throws Exception {
                     blobUtils.createDirectory(containerName, directory);
                     return null;
                  }

                  @Override
                  public String toString() {
                     return "createDirectory(" + containerName + "," + directory + ")";
                  }
               }), service);
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
    * This implementation invokes {@link #deleteAndEnsurePathGone}
    * 
    * @param container
    *           bucket name
    */
   @Override
   public ListenableFuture<Void> deleteContainer(final String container) {
      return org.jclouds.concurrent.Futures.makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            deleteAndEnsurePathGone(container);
            return null;
         }

         @Override
         public String toString() {
            return "deleteContainer(" + container + ")";
         }
      }), service);
   }

   protected void deleteAndEnsurePathGone(final String container) {
      try {
         if (!Assertions.eventuallyTrue(new Supplier<Boolean>() {
            public Boolean get() {
               try {
                  clearContainer(container, recursive());
                  return deleteAndVerifyContainerGone(container);
               } catch (ContainerNotFoundException e) {
                  return true;
               }
            }

         }, 30000)) {
            throw new IllegalStateException(container + " still exists after deleting!");
         }
      } catch (InterruptedException e) {
         throw new IllegalStateException(container + " interrupted during deletion!", e);
      }
   }

   @Override
   public ListenableFuture<Set<? extends Location>> listAssignableLocations() {
      return Futures.<Set<? extends Location>> immediateFuture(locations.get());
   }

   protected abstract boolean deleteAndVerifyContainerGone(String container);

}
