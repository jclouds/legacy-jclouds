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

import javax.inject.Inject;

import org.jclouds.blobstore.BlobStore;
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

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseBlobStore implements BlobStore {

   protected final BlobStoreContext context;
   protected final BlobUtils blobUtils;
   protected final Supplier<Location> defaultLocation;
   protected final Supplier<Set<? extends Location>> locations;

   @Inject
   protected BaseBlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
         @Memoized Supplier<Set<? extends Location>> locations) {
      this.context = checkNotNull(context, "context");
      this.blobUtils = checkNotNull(blobUtils, "blobUtils");
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
   public PageSet<? extends StorageMetadata> list(String container) {
      return this.list(container, org.jclouds.blobstore.options.ListContainerOptions.NONE);
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#directoryExists}
    * 
    * @param container
    *           container name
    * @param directory
    *           virtual path
    */
   @Override
   public boolean directoryExists(String containerName, String directory) {
      return blobUtils.directoryExists(containerName, directory);
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#createDirectory}
    * 
    * @param container
    *           container name
    * @param directory
    *           virtual path
    */
   @Override
   public void createDirectory(String containerName, String directory) {
      blobUtils.createDirectory(containerName, directory);
   }

   /**
    * This implementation invokes {@link #countBlobs} with the
    * {@link ListContainerOptions#recursive} option.
    * 
    * @param container
    *           container name
    */
   @Override
   public long countBlobs(String container) {
      return countBlobs(container, recursive());
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#countBlobs}
    * 
    * @param container
    *           container name
    */
   @Override
   public long countBlobs(String containerName, ListContainerOptions options) {
      return blobUtils.countBlobs(containerName, options);
   }

   /**
    * This implementation invokes {@link #clearContainer} with the
    * {@link ListContainerOptions#recursive} option.
    * 
    * @param container
    *           container name
    */
   @Override
   public void clearContainer(String containerName) {
      clearContainer(containerName, recursive());
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#clearContainer}
    * 
    * @param container
    *           container name
    */
   @Override
   public void clearContainer(String containerName, ListContainerOptions options) {
      blobUtils.clearContainer(containerName, options);
   }

   /**
    * This implementation invokes {@link BlobUtilsImpl#deleteDirectory}.
    * 
    * @param container
    *           container name
    */
   @Override
   public void deleteDirectory(String containerName, String directory) {
      blobUtils.deleteDirectory(containerName, directory);
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
   public Blob getBlob(String container, String key) {
      return getBlob(container, key, org.jclouds.blobstore.options.GetOptions.NONE);
   }

   /**
    * This implementation invokes {@link #deleteAndEnsurePathGone}
    * 
    * @param container
    *           bucket name
    */
   @Override
   public void deleteContainer(String container) {
      deletePathAndEnsureGone(container);
   }

   protected void deletePathAndEnsureGone(String path) {
      checkState(retry(new Predicate<String>() {
         public boolean apply(String in) {
            try {
               clearContainer(in, recursive());
               return deleteAndVerifyContainerGone(in);
            } catch (ContainerNotFoundException e) {
               return true;
            }
         }
      }, 30000).apply(path), "%s still exists after deleting!", path);
   }

   @Override
   public Set<? extends Location> listAssignableLocations() {
      return locations.get();
   }

   protected abstract boolean deleteAndVerifyContainerGone(String container);

}
