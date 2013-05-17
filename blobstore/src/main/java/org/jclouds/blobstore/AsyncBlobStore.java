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
package org.jclouds.blobstore;

import java.util.Set;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.domain.Location;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides hooks needed to run a blob store asynchronously
 * 
 * @see BlobStore
 * @deprecated will be removed in jclouds 1.7, as async interfaces are no longer
 *             supported. Please use {@link org.jclouds.blobstore.BlobStore}
 */
@Deprecated
public interface AsyncBlobStore {
   /**
    * @see BlobStore#getContext
    */
   BlobStoreContext getContext();

   /**
    * @see BlobStore#blobBuilder
    */
   BlobBuilder blobBuilder(String name);

   /**
    * @see BlobStore#listAssignableLocations
    */
   ListenableFuture<Set<? extends Location>> listAssignableLocations();

   /**
    * @see BlobStore#list
    */
   ListenableFuture<PageSet<? extends StorageMetadata>> list();

   /**
    * @see BlobStore#containerExists
    */
   ListenableFuture<Boolean> containerExists(String container);

   /**
    * @see BlobStore#createContainerInLocation(Location, String)
    */
   ListenableFuture<Boolean> createContainerInLocation(@Nullable Location location, String container);

   /**
    * @see BlobStore#createContainerInLocation(Location,String,CreateContainerOptions)
    */
   ListenableFuture<Boolean> createContainerInLocation(@Nullable Location location, String container,
            CreateContainerOptions options);

   /**
    * @see BlobStore#list(String)
    */
   ListenableFuture<PageSet<? extends StorageMetadata>> list(String container);

   /**
    * @see BlobStore#list(String, ListContainerOptions)
    */
   ListenableFuture<PageSet<? extends StorageMetadata>> list(String container, ListContainerOptions options);

   /**
    * @see BlobStore#clearContainer(String)
    */
   ListenableFuture<Void> clearContainer(String container);

   /**
    * @see BlobStore#clearContainer(String, ListContainerOptions)
    */
   ListenableFuture<Void> clearContainer(String container, ListContainerOptions options);

   /**
    * @see BlobStore#deleteContainer
    */
   ListenableFuture<Void> deleteContainer(String container);

   /**
    * @see BlobStore#directoryExists
    */
   ListenableFuture<Boolean> directoryExists(String container, String directory);

   /**
    * @see BlobStore#createDirectory
    */
   ListenableFuture<Void> createDirectory(String container, String directory);

   /**
    * @see BlobStore#deleteDirectory
    */
   ListenableFuture<Void> deleteDirectory(String containerName, String name);

   /**
    * @see BlobStore#blobExists
    */
   ListenableFuture<Boolean> blobExists(String container, String name);

   /**
    * @see BlobStore#putBlob(String,Blob)
    */
   ListenableFuture<String> putBlob(String container, Blob blob);

   /**
    * @see BlobStore#putBlob(String,Blob,PutOptions)
    */
   ListenableFuture<String> putBlob(String container, Blob blob, PutOptions options);

   /**
    * @see BlobStore#blobMetadata
    */
   ListenableFuture<BlobMetadata> blobMetadata(String container, String key);

   /**
    * @see BlobStore#getBlob(String, String)
    */
   ListenableFuture<Blob> getBlob(String container, String key);

   /**
    * @see BlobStore#getBlob(String, String, GetOptions)
    */
   ListenableFuture<Blob> getBlob(String container, String key, GetOptions options);

   /**
    * @see BlobStore#removeBlob
    */
   ListenableFuture<Void> removeBlob(String container, String key);

   /**
    * @see BlobStore#countBlobs(String)
    */
   ListenableFuture<Long> countBlobs(String container);

   /**
    * @see BlobStore#countBlobs(String,ListContainerOptions)
    */
   ListenableFuture<Long> countBlobs(String container, ListContainerOptions options);

}
