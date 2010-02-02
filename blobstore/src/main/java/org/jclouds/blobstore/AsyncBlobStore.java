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
package org.jclouds.blobstore;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides hooks needed to run a blob store asynchronously
 * 
 * @see AsyncBlobStore
 */
public interface AsyncBlobStore {

   /**
    * @see BlobStore#newBlob
    */
   Blob newBlob(String name);

   /**
    * @see BlobStore#list
    */
   ListenableFuture<? extends PageSet<? extends StorageMetadata>> list();

   /**
    * @see BlobStore#containerExists
    */
   ListenableFuture<Boolean> containerExists(String container);

   /**
    * @see BlobStore#createContainerInLocation(String, String)
    */
   ListenableFuture<Boolean> createContainerInLocation(String location, String container);

   /**
    * @see BlobStore#list(String)
    */
   ListenableFuture<? extends PageSet<? extends StorageMetadata>> list(String container);

   /**
    * @see BlobStore#list(String, ListContainerOptions)
    */
   ListenableFuture<? extends PageSet<? extends StorageMetadata>> list(String container,
            ListContainerOptions options);

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
    * @see BlobStore#putBlob
    */
   ListenableFuture<String> putBlob(String container, Blob blob);

   /**
    * @see BlobStore#blobMetadata
    */
   ListenableFuture<BlobMetadata> blobMetadata(String container, String key);

   /**
    * @see BlobStore#getBlob(String, String)
    */
   ListenableFuture<? extends Blob> getBlob(String container, String key);

   /**
    * @see BlobStore#getBlob(String, String, GetOptions)
    */
   ListenableFuture<? extends Blob> getBlob(String container, String key, GetOptions options);

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
