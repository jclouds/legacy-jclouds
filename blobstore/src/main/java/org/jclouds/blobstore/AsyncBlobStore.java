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
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides hooks needed to run a blob store asynchronously
 * 
 * @see AsyncBlobStore
 */
public interface AsyncBlobStore {

   Blob newBlob(String name);

   /**
    * Lists all root-level resources available to the account.
    */
   ListenableFuture<? extends ListResponse<? extends ResourceMetadata>> list();

   /**
    * Lists all resources available at the specified path. Note that path may be a container, or a
    * path within it delimited by {@code /} characters.
    * 
    * @param parent
    *           - base path to list; non-recursive
    */
   ListenableFuture<? extends ListContainerResponse<? extends ResourceMetadata>> list(String container,
            ListContainerOptions... options);

   ListenableFuture<Boolean> containerExists(String container);

   ListenableFuture<Boolean> directoryExists(String container, String directory);

   ListenableFuture<Boolean> createContainer(String container);

   ListenableFuture<Void> createDirectory(String container, String directory);

   /**
    * This will delete a container recursively.
    * 
    * @param container
    */
   ListenableFuture<Void> deleteContainer(String container);

   /**
    * This will delete the contents of a container without removing it
    * 
    * @param container
    */
   ListenableFuture<Void> clearContainer(String container);

   ListenableFuture<String> putBlob(String container, Blob blob);

   ListenableFuture<? extends Blob> getBlob(String container, String key, GetOptions... options);

   ListenableFuture<BlobMetadata> blobMetadata(String container, String key);

   ListenableFuture<Void> removeBlob(String container, String key);

}
