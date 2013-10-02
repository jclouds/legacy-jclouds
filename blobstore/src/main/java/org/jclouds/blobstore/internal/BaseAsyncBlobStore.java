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
package org.jclouds.blobstore.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.util.Predicates2.retry;

import java.util.Set;
import java.util.concurrent.Callable;

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
import org.jclouds.blobstore.util.internal.BlobUtilsImpl;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * 
 * @author Adrian Cole
 * @deprecated will be removed in jclouds 1.7, as async interfaces are no longer
 *             supported. Please use {@link org.jclouds.blobstore.BlobStore}
 */
@Deprecated
public abstract class BaseAsyncBlobStore implements AsyncBlobStore {

   protected final BlobStoreContext context;
   protected final BlobUtils blobUtils;
   protected final ListeningExecutorService userExecutor;
   protected final Supplier<Location> defaultLocation;
   protected final Supplier<Set<? extends Location>> locations;

   @Inject
   protected BaseAsyncBlobStore(BlobStoreContext context, BlobUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations) {
      this.context = checkNotNull(context, "context");
      this.blobUtils = checkNotNull(blobUtils, "blobUtils");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
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
      return userExecutor.submit(new Callable<Long>() {
         public Long call() throws Exception {
            return blobUtils.countBlobs(containerName, options);
         }

         @Override
         public String toString() {
            return "countBlobs(" + containerName + ")";
         }
      });
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
      return userExecutor.submit(new Callable<Void>() {

         public Void call() throws Exception {
            blobUtils.clearContainer(containerName, options);
            return null;
         }

         @Override
         public String toString() {
            return "clearContainer(" + containerName + ")";
         }
      });
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#deleteDirectory}.
    * 
    * @param container
    *           container name
    */
   @Override
   public ListenableFuture<Void> deleteDirectory(final String containerName, final String directory) {
      return userExecutor.submit(new Callable<Void>() {

         public Void call() throws Exception {
            blobUtils.deleteDirectory(containerName, directory);
            return null;
         }

         @Override
         public String toString() {
            return "deleteDirectory(" + containerName + "," + directory + ")";
         }
      });
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
      return userExecutor.submit(new Callable<Boolean>() {

         public Boolean call() throws Exception {
            return blobUtils.directoryExists(containerName, directory);
         }

         @Override
         public String toString() {
            return "directoryExists(" + containerName + "," + directory + ")";
         }
      });
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
               : userExecutor.submit(new Callable<Void>() {
                  public Void call() throws Exception {
                     blobUtils.createDirectory(containerName, directory);
                     return null;
                  }

                  @Override
                  public String toString() {
                     return "createDirectory(" + containerName + "," + directory + ")";
                  }
               });
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
      return userExecutor.submit(new Callable<Void>() {

         public Void call() throws Exception {
            deletePathAndEnsureGone(container);
            return null;
         }

         @Override
         public String toString() {
            return "deleteContainer(" + container + ")";
         }
      });
   }

   protected void deletePathAndEnsureGone(String path) {
      checkState(retry(new Predicate<String>() {
         public boolean apply(String in) {
            try {
               blobUtils.clearContainer(in, recursive());
               return deleteAndVerifyContainerGone(in);
            } catch (ContainerNotFoundException e) {
               return true;
            }
         }
      }, 30000).apply(path), "%s still exists after deleting!", path);
   }

   @Override
   public ListenableFuture<Set<? extends Location>> listAssignableLocations() {
      return Futures.<Set<? extends Location>> immediateFuture(locations.get());
   }

   protected abstract boolean deleteAndVerifyContainerGone(String container);

}
