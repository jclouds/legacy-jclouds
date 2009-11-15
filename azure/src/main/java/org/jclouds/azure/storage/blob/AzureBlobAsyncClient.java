/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import org.jclouds.azure.storage.blob.binders.BindAzureBlobToEntity;
import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.ListableContainerProperties;
import org.jclouds.azure.storage.blob.functions.BlobName;
import org.jclouds.azure.storage.blob.functions.ParseBlobFromHeadersAndHttpContent;
import org.jclouds.azure.storage.blob.functions.ParseBlobPropertiesFromHeaders;
import org.jclouds.azure.storage.blob.functions.ParseContainerPropertiesFromHeaders;
import org.jclouds.azure.storage.blob.functions.ReturnTrueIfContainerAlreadyExists;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.azure.storage.blob.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azure.storage.blob.xml.ContainerNameEnumerationResultsHandler;
import org.jclouds.azure.storage.domain.BoundedSortedSet;
import org.jclouds.azure.storage.filters.SharedKeyAuthentication;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ReturnFalseOn404;
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
 * Provides asynchronous access to Azure Blob via their REST API.
 * <p/>
 * All commands return a Future of the result from Azure Blob. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd135733.aspx" />
 * @see AzureBlobClient
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(SharedKeyAuthentication.class)
@Headers(keys = AzureStorageHeaders.VERSION, values = "2009-07-17")
@Endpoint(AzureBlob.class)
@ConsistencyModel(ConsistencyModels.STRICT)
public interface AzureBlobAsyncClient {

   public org.jclouds.azure.storage.blob.domain.AzureBlob newBlob();

   /**
    * @see AzureBlobClient#listContainers
    */
   @GET
   @XMLResponseParser(AccountNameEnumerationResultsHandler.class)
   @Path("/")
   @QueryParams(keys = "comp", values = "list")
   Future<? extends BoundedSortedSet<ListableContainerProperties>> listContainers(
            ListOptions... listOptions);

   /**
    * @see AzureBlobClient#createContainer
    */
   @PUT
   @Path("{container}")
   @ExceptionParser(ReturnTrueIfContainerAlreadyExists.class)
   @QueryParams(keys = "restype", values = "container")
   Future<Boolean> createContainer(@PathParam("container") String container,
            CreateContainerOptions... options);

   /**
    * @see AzureBlobClient#getContainerProperties
    */
   @HEAD
   @Path("{container}")
   @QueryParams(keys = "restype", values = "container")
   @ResponseParser(ParseContainerPropertiesFromHeaders.class)
   Future<ListableContainerProperties> getContainerProperties(
            @PathParam("container") String container);

   /**
    * @see AzureBlobClient#containerExists
    */
   @HEAD
   @Path("{container}")
   @QueryParams(keys = "restype", values = "container")
   @ExceptionParser(ReturnFalseOn404.class)
   Future<Boolean> containerExists(@PathParam("container") String container);

   /**
    * @see AzureBlobClient#setResourceMetadata
    */
   @PUT
   @Path("{container}")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "metadata" })
   Future<Void> setResourceMetadata(@PathParam("container") String container,
            @BinderParam(BindMapToHeadersWithPrefix.class) Map<String, String> metadata);

   /**
    * @see AzureBlobClient#deleteContainer
    */
   @DELETE
   @Path("{container}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @QueryParams(keys = "restype", values = "container")
   Future<Void> deleteContainer(@PathParam("container") String container);

   /**
    * @see AzureBlobClient#createRootContainer
    */
   @PUT
   @Path("$root")
   @ExceptionParser(ReturnTrueIfContainerAlreadyExists.class)
   @QueryParams(keys = "restype", values = "container")
   Future<Boolean> createRootContainer(CreateContainerOptions... options);

   /**
    * @see AzureBlobClient#deleteRootContainer
    */
   @DELETE
   @Path("$root")
   @ExceptionParser(ReturnTrueOn404.class)
   @QueryParams(keys = "restype", values = "container")
   Future<Boolean> deleteRootContainer();

   /**
    * @see AzureBlobClient#listBlobs(String, ListBlobsOptions)
    */
   @GET
   @XMLResponseParser(ContainerNameEnumerationResultsHandler.class)
   @Path("{container}")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "list" })
   Future<ListBlobsResponse> listBlobs(@PathParam("container") String container,
            ListBlobsOptions... options);

   /**
    * @see AzureBlobClient#listBlobs(ListBlobsOptions)
    */
   @GET
   @XMLResponseParser(ContainerNameEnumerationResultsHandler.class)
   @Path("$root")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "list" })
   Future<ListBlobsResponse> listBlobs(ListBlobsOptions... options);

   /**
    * @see AzureBlobClient#putBlob
    */
   @PUT
   @Path("{container}/{name}")
   @ResponseParser(ParseETagHeader.class)
   Future<String> putBlob(
            @PathParam("container") String container,
            @PathParam("name") @ParamParser(BlobName.class) @BinderParam(BindAzureBlobToEntity.class) org.jclouds.azure.storage.blob.domain.AzureBlob object);

   /**
    * @see AzureBlobClient#getBlob
    */
   @GET
   @ResponseParser(ParseBlobFromHeadersAndHttpContent.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("{container}/{name}")
   Future<org.jclouds.azure.storage.blob.domain.AzureBlob> getBlob(
            @PathParam("container") String container, @PathParam("name") String name,
            GetOptions... options);

   /**
    * @see AzureBlobClient#getBlobProperties
    */
   @HEAD
   @ResponseParser(ParseBlobPropertiesFromHeaders.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("{container}/{name}")
   Future<BlobProperties> getBlobProperties(@PathParam("container") String container,
            @PathParam("name") String name);

   /**
    * @see AzureBlobClient#setBlobMetadata
    */
   @PUT
   @Path("{container}/{name}")
   @QueryParams(keys = { "comp" }, values = { "metadata" })
   Future<Void> setBlobMetadata(@PathParam("container") String container,
            @PathParam("name") String name,
            @BinderParam(BindMapToHeadersWithPrefix.class) Map<String, String> metadata);

   /**
    * @see AzureBlobClient#deleteBlob
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("{container}/{name}")
   Future<Void> deleteBlob(@PathParam("container") String container, @PathParam("name") String name);

}
