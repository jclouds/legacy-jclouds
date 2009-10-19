/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.azure.storage.blob;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.azure.storage.AzureBlob;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.functions.ParseBlobFromHeadersAndHttpContent;
import org.jclouds.azure.storage.blob.functions.ParseBlobMetadataFromHeaders;
import org.jclouds.azure.storage.blob.functions.ParseContainerMetadataFromHeaders;
import org.jclouds.azure.storage.blob.functions.ReturnTrueIfContainerAlreadyExists;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.azure.storage.blob.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azure.storage.blob.xml.ContainerNameEnumerationResultsHandler;
import org.jclouds.azure.storage.domain.BoundedSortedSet;
import org.jclouds.azure.storage.filters.SharedKeyAuthentication;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.blobstore.binders.BindBlobToEntityAndUserMetadataToHeadersWithPrefix;
import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.blobstore.functions.BlobName;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ReturnTrueOn404;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to Azure Blob via their REST API.
 * <p/>
 * All commands return a Future of the result from Azure Blob. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd135733.aspx" />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(SharedKeyAuthentication.class)
@Headers(keys = AzureStorageHeaders.VERSION, values = "2009-07-17")
@Endpoint(AzureBlob.class)
public interface AzureBlobConnection {

   /**
    * The List Containers operation returns a list of the containers under the specified account.
    * <p />
    * The 2009-07-17 version of the List Containers operation times out after 30 seconds.
    * 
    * @param listOptions
    *           controls the number or type of results requested
    * @see ListOptions
    */
   @GET
   @XMLResponseParser(AccountNameEnumerationResultsHandler.class)
   @Path("/")
   @QueryParams(keys = "comp", values = "list")
   BoundedSortedSet<ContainerMetadata> listContainers(ListOptions... listOptions);

   /**
    * The Create Container operation creates a new container under the specified account. If the
    * container with the same name already exists, the operation fails.
    * <p/>
    * The container resource includes metadata and properties for that container. It does not
    * include a list of the blobs contained by the container.
    * 
    * @see CreateContainerOptions
    * 
    */
   @PUT
   @Path("{container}")
   @ExceptionParser(ReturnTrueIfContainerAlreadyExists.class)
   @QueryParams(keys = "restype", values = "container")
   Future<Boolean> createContainer(@PathParam("container") String container,
            CreateContainerOptions... options);

   /**
    * The Get Container Properties operation returns all user-defined metadata and system properties
    * for the specified container. The data returned does not include the container's list of blobs.
    */
   @HEAD
   @Path("{container}")
   @QueryParams(keys = "restype", values = "container")
   @ResponseParser(ParseContainerMetadataFromHeaders.class)
   ContainerMetadata getContainerProperties(@PathParam("container") String container);

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
   @PUT
   @Path("{container}")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "metadata" })
   void setContainerMetadata(@PathParam("container") String container,
            @BinderParam(BindMapToHeadersWithPrefix.class) Map<String, String> metadata);

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
   @DELETE
   @Path("{container}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @QueryParams(keys = "restype", values = "container")
   Future<Void> deleteContainer(@PathParam("container") String container);

   /**
    * The root container is a default container that may be inferred from a URL requesting a blob
    * resource. The root container makes it possible to reference a blob from the top level of the
    * storage account hierarchy, without referencing the container name.
    * <p/>
    * The container resource includes metadata and properties for that container. It does not
    * include a list of the blobs contained by the container.
    * 
    * @see CreateContainerOptions
    * 
    */
   @PUT
   @Path("$root")
   @ExceptionParser(ReturnTrueIfContainerAlreadyExists.class)
   @QueryParams(keys = "restype", values = "container")
   Future<Boolean> createRootContainer(CreateContainerOptions... options);

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
   @DELETE
   @Path("$root")
   @ExceptionParser(ReturnTrueOn404.class)
   @QueryParams(keys = "restype", values = "container")
   Future<Boolean> deleteRootContainer();

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
    * The delimiter parameter enables the caller to traverse the blob namespace by using a
    * user-configured delimiter. The delimiter may be a single character or a string. When the
    * request includes this parameter, the operation returns a BlobPrefix element. The BlobPrefix
    * element is returned in place of all blobs whose names begin with the same substring up to the
    * appearance of the delimiter character. The value of the BlobPrefix element is
    * substring+delimiter, where substring is the common substring that begins one or more blob
    * names, and delimiter is the value of the delimiter parameter.
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
   @GET
   @XMLResponseParser(ContainerNameEnumerationResultsHandler.class)
   @Path("{container}")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "list" })
   Future<ListBlobsResponse> listBlobs(@PathParam("container") String container,
            ListBlobsOptions... options);

   @GET
   @XMLResponseParser(ContainerNameEnumerationResultsHandler.class)
   @Path("$root")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "list" })
   Future<ListBlobsResponse> listBlobs(ListBlobsOptions... options);

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
    * (Request Entity Too Large). The Blob service also returns additional information about the
    * error in the response, including the maximum blob size permitted in bytes.
    * <p/>
    * A Put Blob operation is permitted 10 minutes per MB to complete. If the operation is taking
    * longer than 10 minutes per MB on average, the operation will timeout.
    */
   @PUT
   @Path("{container}/{key}")
   @ResponseParser(ParseETagHeader.class)
   Future<String> putBlob(@PathParam("container") String container,
            @PathParam("key") @ParamParser(BlobName.class) @BinderParam(BindBlobToEntityAndUserMetadataToHeadersWithPrefix.class) Blob object);

   /**
    * The Get Blob operation reads or downloads a blob from the system, including its metadata and
    * properties.
    */
   @GET
   @ResponseParser(ParseBlobFromHeadersAndHttpContent.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("{container}/{key}")
   Future<Blob> getBlob(@PathParam("container") String container, @PathParam("key") String key,
            GetOptions... options);

   /**
    * The Get Blob Properties operation returns all user-defined metadata, standard HTTP properties,
    * and system properties for the blob. It does not return the content of the blob.
    */
   @GET
   @Headers(keys = "Range", values = "bytes=0-0")
   // should use HEAD, this is a hack per http://code.google.com/p/jclouds/issues/detail?id=92
   @ResponseParser(ParseBlobMetadataFromHeaders.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("{container}/{key}")
   BlobMetadata getBlobProperties(@PathParam("container") String container,
            @PathParam("key") String key);

   @PUT
   @Path("{container}/{key}")
   @QueryParams(keys = { "comp" }, values = { "metadata" })
   void setBlobMetadata(@PathParam("container") String container, @PathParam("key") String key,
            @BinderParam(BindMapToHeadersWithPrefix.class) Map<String, String> metadata);

   /**
    * The Delete Blob operation marks the specified blob for deletion. The blob is later deleted
    * during garbage collection.
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("{container}/{key}")
   Future<Void> deleteBlob(@PathParam("container") String container, @PathParam("key") String key);

}
