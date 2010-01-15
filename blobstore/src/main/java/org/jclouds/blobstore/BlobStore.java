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
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;

/**
 * Provides hooks needed to run a blob store
 * 
 * @see AsyncBlobStore
 */
public interface BlobStore {

   Blob newBlob(String name);

   /**
    * Lists all root-level resources available to the account.
    */
   ListResponse<? extends StorageMetadata> list();

   /**
    * Lists all resources available at the specified path. Note that path may be a container, or a
    * path within it delimited by {@code /} characters.
    * 
    * @param parent
    *           - base path to list; non-recursive
    */
   ListContainerResponse<? extends StorageMetadata> list(String container,
            ListContainerOptions... options);

   boolean containerExists(String container);

   boolean directoryExists(String container, String directory);

   boolean createContainerInLocation(String location, String container);

   void createDirectory(String container, String directory);

   /**
    * This will delete a container recursively.
    * 
    * @param container
    */
   void deleteContainer(String container);

   /**
    * This will delete the contents of a container without removing it
    * 
    * @param container
    */
   void clearContainer(String container);

   String putBlob(String container, Blob blob);

   Blob getBlob(String container, String key, GetOptions... options);

   BlobMetadata blobMetadata(String container, String key);

   void removeBlob(String container, String key);

}
