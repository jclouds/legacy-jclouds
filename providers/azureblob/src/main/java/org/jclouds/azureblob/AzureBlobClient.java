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
package org.jclouds.azureblob;

import java.util.List;
import java.util.Map;
import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azureblob.domain.AzureBlob;
import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.ContainerProperties;
import org.jclouds.azureblob.domain.ListBlobBlocksResponse;
import org.jclouds.azureblob.domain.ListBlobsResponse;
import org.jclouds.azureblob.domain.PublicAccess;
import org.jclouds.azureblob.options.CreateContainerOptions;
import org.jclouds.azureblob.options.ListBlobsOptions;
import org.jclouds.http.options.GetOptions;

import com.google.inject.Provides;
import org.jclouds.io.Payload;

/**
 * Provides access to Azure Blob via their REST API.
 * <p/>
 * All commands return a Future of the result from Azure Blob. Any exceptions incurred during
 * processing will be backend in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd135733.aspx" />
 * @author Adrian Cole
 */
public interface AzureBlobClient {
   @Provides
   public AzureBlob newBlob();

   /**
    * The List Containers operation returns a list of the containers under the specified identity.
    * <p />
    * The 2009-07-17 version of the List Containers operation times out after 30 seconds.
    * 
    * @param listOptions
    *           controls the number or type of results requested
    * @see ListOptions
    */
   BoundedSet<ContainerProperties> listContainers(ListOptions... listOptions);

   /**
    * The Create Container operation creates a new container under the specified identity. If the
    * container with the same name already exists, the operation fails.
    * <p/>
    * The container resource includes metadata and properties for that container. It does not
    * include a list of the blobs contained by the container.
    * 
    * @return true, if the bucket was created or false, if the container was already present
    * 
    * @see CreateContainerOptions
    * 
    */
   boolean createContainer(String container, CreateContainerOptions... options);

   /**
    * The Get Container Properties operation returns all user-defined metadata and system properties
    * for the specified container. The data returned does not include the container's list of blobs.
    */
   ContainerProperties getContainerProperties(String container);

   /**
    * Issues a HEAD command to determine if the container exists or not.
    */
   boolean containerExists(String container);

   /**
    * The Set Container Metadata operation sets one or more user-defined name/value pairs for the
    * specified container. <h4>Remarks</h4>
    * 
    * 
    * Calling the Set Container Metadata operation overwrites all existing metadata that is
    * associated with the container. It's not possible to modify an individual name/value pair.
    * <p/>
    * You may also set metadata for a container at the time it is created.
    * <p/>
    * Calling Set Container Metadata updates the ETag for the container.
    */
   void setResourceMetadata(String container, Map<String, String> metadata);

   /**
    * The Delete Container operation marks the specified container for deletion. The container and
    * any blobs contained within it are later deleted during garbage collection.
    * <p/>
    * When a container is deleted, a container with the same name cannot be created for at least 30
    * seconds; the container may not be available for more than 30 seconds if the service is still
    * processing the request. While the container is being deleted, attempts to create a container
    * of the same name will fail with status code 409 (Conflict), with the service returning
    * additional error information indicating that the container is being deleted. All other
    * operations, including operations on any blobs under the container, will fail with status code
    * 404 (Not Found) while the container is being deleted.
    * 
    */
   void deleteContainer(String container);

   /**
    * The root container is a default container that may be inferred from a URL requesting a blob
    * resource. The root container makes it possible to reference a blob from the top level of the
    * storage identity hierarchy, without referencing the container name.
    * <p/>
    * The container resource includes metadata and properties for that container. It does not
    * include a list of the blobs contained by the container.
    * 
    * @see CreateContainerOptions
    * 
    */
   boolean createRootContainer(CreateContainerOptions... options);

   /**
    * 
    * 
    * @param container
    * @return whether data in the container may be accessed publicly and the level of access
    */
   PublicAccess getPublicAccessForContainer(String container);

   /**
    * The Delete Container operation marks the specified container for deletion. The container and
    * any blobs contained within it are later deleted during garbage collection. <h4>Remarks</h4>
    * When a container is deleted, a container with the same name cannot be created for at least 30
    * seconds; the container may not be available for more than 30 seconds if the service is still
    * processing the request. While the container is being deleted, attempts to create a container
    * of the same name will fail with status code 409 (Conflict), with the service returning
    * additional error information indicating that the container is being deleted. All other
    * operations, including operations on any blobs under the container, will fail with status code
    * 404 (Not Found) while the container is being deleted.
    * 
    * @see deleteContainer(String)
    * @see createRootContainer(CreateContainerOptions)
    */
   void deleteRootContainer();

   /**
    * The List Blobs operation enumerates the list of blobs under the specified container.
    * <p/>
    * <h4>Authorization</h4>
    * 
    * If the container's access control list (ACL) is set to allow anonymous access, any client may
    * call this operation.
    * <h4>Remarks</h4>
    * 
    * If you specify a value for the maxresults parameter and the number of blobs to return exceeds
    * this value, or exceeds the default value for maxresults, the response body will contain a
    * NextMarker element that indicates the next blob to return on a subsequent request. To return
    * the next set of items, specify the value of NextMarker as the marker parameter on the URI for
    * the subsequent request.
    * <p/>
    * Note that the value of NextMarker should be treated as opaque.
    * <p/>
    * The delimiter parameter enables the caller to traverse the blob keyspace by using a
    * user-configured delimiter. The delimiter may be a single character or a string. When the
    * request includes this parameter, the operation returns a BlobPrefix element. The BlobPrefix
    * element is returned in place of all blobs whose keys begin with the same substring up to the
    * appearance of the delimiter character. The value of the BlobPrefix element is
    * substring+delimiter, where substring is the common substring that begins one or more blob
    * keys, and delimiter is the value of the delimiter parameter.
    * <p/>
    * You can use the value of BlobPrefix to make a subsequent call to list the blobs that begin
    * with this prefix, by specifying the value of BlobPrefix for the prefix parameter on the
    * request URI. In this way, you can traverse a virtual hierarchy of blobs as though it were a
    * file system.
    * <p/>
    * Note that each BlobPrefix element returned counts toward the maximum result, just as each Blob
    * element does.
    * <p/>
    * Blobs are listed in alphabetical order in the response body.
    */
   ListBlobsResponse listBlobs(String container, ListBlobsOptions... options);

   ListBlobsResponse listBlobs(ListBlobsOptions... options);

   /**
    * The Put Blob operation creates a new blob or updates the content of an existing blob.
    * <p/>
    * Updating an existing blob overwrites any existing metadata on the blob. Partial updates are
    * not supported; the content of the existing blob is overwritten with the content of the new
    * blob.
    * <p/>
    * <h4>Remarks</h4>
    * The maximum upload size for a blob is 64 MB. If your blob is larger than 64 MB, you may upload
    * it as a set of blocks. For more information, see the Put Block and Put Block List operations.
    * <p/>
    * If you attempt to upload a blob that is larger than 64 MB, the service returns status code 413
    * (Request Payload Too Large). The Blob service also returns additional information about the
    * error in the response, including the maximum blob size permitted in bytes.
    */
   String putBlob(String container, AzureBlob object);

   /**
    * The Get Blob operation reads or downloads a blob from the system, including its metadata and
    * properties.
    */
   AzureBlob getBlob(String container, String name, GetOptions... options);

   /**
    *  The Put Block operation creates a block blob on Azure which can be later assembled into
    *  a single, large blob object with the Put Block List operation.
    *
    *  @see <a href="http://msdn.microsoft.com/en-us/library/windowsazure/dd135726.aspx">Put Blob</a>
    */
   void putBlock(String container, String name, String blockId, Payload object);


   /**
    *  The Put Block List assembles a list of blocks previously uploaded with Put Block into a single
    *  blob. Blocks are either already committed to a blob or uncommitted. The blocks ids passed here
    *  are searched for first in the uncommitted block list; then committed using the "latest" strategy.
    *
    *  @see <a href="http://msdn.microsoft.com/en-us/library/windowsazure/dd179467.aspx">Put Block List</a>
    */
   String putBlockList(String container, String name, List<String> blockIdList);

   /**
    * Get Block ID List for a blob
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/windowsazure/dd179400.aspx">Get Block List</a>
    */
   ListBlobBlocksResponse getBlockList(String container, String name);

   /**
    * The Get Blob Properties operation returns all user-defined metadata, standard HTTP properties,
    * and system properties for the blob. It does not return the content of the blob.
    */
   BlobProperties getBlobProperties(String container, String name);

   void setBlobMetadata(String container, String name, Map<String, String> metadata);

   /**
    * The Delete Blob operation marks the specified blob for deletion. The blob is later deleted
    * during garbage collection.
    */
   void deleteBlob(String container, String name);

   /**
    * @throws ContainerNotFoundException
    *            if the container is not present.
    */
   boolean blobExists(String container, String name);

}
