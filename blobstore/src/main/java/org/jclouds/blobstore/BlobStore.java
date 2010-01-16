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
   
   /**
    * Adds a {@code Blob} representing the data at location {@code container/blob.metadata.name}
    * 
    * @param container
    *           container to place the blob.
    * @param blob
    *           fully qualified name relative to the container.
    * @param options
    *           byte range or condition options
    * @return etag of the blob you uploaded, possibly null where etags are unsupported
    * @throws ContainerNotFoundException
    *            if the container doesn't exist
    */
   String putBlob(String container, Blob blob);

   /**
    * Retrieves a {@code Blob} representing the data at location {@code container/name}
    * 
    * @param container
    *           container where this exists.
    * @param name
    *           fully qualified name relative to the container.
    * @param options
    *           byte range or condition options
    * @return the blob you intended to receive.
    * @throws ContainerNotFoundException
    *            if the container doesn't exist
    * @throws KeyNotFoundException
    *            if the container doesn't exist
    */
   Blob getBlob(String container, String name, GetOptions... options);

   /**
    * Retrieves the metadata of a {@code Blob} at location {@code container/name}
    * 
    * @param container
    *           container where this exists.
    * @param name
    *           fully qualified name relative to the container.
    * @return null if name isn't present or the blob you intended to receive.
    * @throws ContainerNotFoundException
    *            if the container doesn't exist
    */
   BlobMetadata blobMetadata(String container, String name);

   /**
    * Deletes a {@code Blob} representing the data at location {@code container/name}
    * 
    * @param container
    *           container where this exists.
    * @param name
    *           fully qualified name relative to the container.
    * @throws ContainerNotFoundException
    *            if the container doesn't exist
    */
   void removeBlob(String container, String name);

}
