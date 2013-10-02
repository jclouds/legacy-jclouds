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

import static com.google.common.net.HttpHeaders.EXPECT;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.TrueOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azure.storage.filters.SharedKeyLiteAuthentication;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.azureblob.AzureBlobFallbacks.FalseIfContainerAlreadyExists;
import org.jclouds.azureblob.binders.BindAzureBlobMetadataToRequest;
import org.jclouds.azureblob.binders.BindAzureBlocksToRequest;
import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.ContainerProperties;
import org.jclouds.azureblob.domain.ListBlobBlocksResponse;
import org.jclouds.azureblob.domain.ListBlobsResponse;
import org.jclouds.azureblob.domain.PublicAccess;
import org.jclouds.azureblob.functions.BlobName;
import org.jclouds.azureblob.functions.ParseBlobFromHeadersAndHttpContent;
import org.jclouds.azureblob.functions.ParseBlobPropertiesFromHeaders;
import org.jclouds.azureblob.functions.ParseContainerPropertiesFromHeaders;
import org.jclouds.azureblob.functions.ParsePublicAccessHeader;
import org.jclouds.azureblob.options.CreateContainerOptions;
import org.jclouds.azureblob.options.ListBlobsOptions;
import org.jclouds.azureblob.predicates.validators.BlockIdValidator;
import org.jclouds.azureblob.predicates.validators.ContainerNameValidator;
import org.jclouds.azureblob.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azureblob.xml.BlobBlocksResultsHandler;
import org.jclouds.azureblob.xml.ContainerNameEnumerationResultsHandler;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnContainerNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnKeyNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.NullOnContainerNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.NullOnKeyNotFound;
import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.Payload;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Provides;

/**
 * Provides asynchronous access to Azure Blob via their REST API.
 * <p/>
 * All commands return a ListenableFuture of the result from Azure Blob. Any exceptions incurred
 * during processing will be backend in an {@link ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd135733.aspx" />
 * @see AzureBlobClient
 * @author Adrian Cole
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(AzureBlobClient.class)} as
 *             {@link AzureBlobAsyncClient} interface will be removed in jclouds 1.7.
 */
@Deprecated
@RequestFilters(SharedKeyLiteAuthentication.class)
@Headers(keys = AzureStorageHeaders.VERSION, values = "2009-09-19")
@SkipEncoding({ '/', '$' })
@Path("/")
public interface AzureBlobAsyncClient {
   @Provides
   public org.jclouds.azureblob.domain.AzureBlob newBlob();

   /**
    * @see AzureBlobClient#listContainers
    */
   @Named("ListContainers")
   @GET
   @XMLResponseParser(AccountNameEnumerationResultsHandler.class)
   @QueryParams(keys = "comp", values = "list")
   ListenableFuture<? extends BoundedSet<ContainerProperties>> listContainers(ListOptions... listOptions);

   /**
    * @see AzureBlobClient#createContainer
    */
   @Named("CreateContainer")
   @PUT
   @Path("{container}")
   @Fallback(FalseIfContainerAlreadyExists.class)
   @QueryParams(keys = "restype", values = "container")
   ListenableFuture<Boolean> createContainer(
            @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
            CreateContainerOptions... options);

   /**
    * @see AzureBlobClient#getPublicAccessForContainer
    */
   @Named("GetContainerACL")
   @HEAD
   @Path("{container}")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "acl" })
   @ResponseParser(ParsePublicAccessHeader.class)
   @Fallback(NullOnContainerNotFound.class)
   ListenableFuture<PublicAccess> getPublicAccessForContainer(
            @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container);

   /**
    * @see AzureBlobClient#getContainerProperties
    */
   @Named("GetContainerProperties")
   @HEAD
   @Path("{container}")
   @QueryParams(keys = "restype", values = "container")
   @ResponseParser(ParseContainerPropertiesFromHeaders.class)
   @Fallback(NullOnContainerNotFound.class)
   ListenableFuture<ContainerProperties> getContainerProperties(
            @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container);

   /**
    * @see AzureBlobClient#containerExists
    */
   @Named("GetContainerProperties")
   @HEAD
   @Path("{container}")
   @QueryParams(keys = "restype", values = "container")
   @Fallback(FalseOnContainerNotFound.class)
   ListenableFuture<Boolean> containerExists(
            @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container);

   /**
    * @see AzureBlobClient#setResourceMetadata
    */
   @Named("SetContainerMetadata")
   @PUT
   @Path("{container}")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "metadata" })
   ListenableFuture<Void> setResourceMetadata(
            @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
            @BinderParam(BindMapToHeadersWithPrefix.class) Map<String, String> metadata);

   /**
    * @see AzureBlobClient#deleteContainer
    */
   @Named("DeleteContainer")
   @DELETE
   @Path("{container}")
   @Fallback(VoidOnNotFoundOr404.class)
   @QueryParams(keys = "restype", values = "container")
   ListenableFuture<Void> deleteContainer(
            @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container);

   /**
    * @see AzureBlobClient#createRootContainer
    */
   @Named("CreateContainer")
   @PUT
   @Path("$root")
   @Fallback(FalseIfContainerAlreadyExists.class)
   @QueryParams(keys = "restype", values = "container")
   ListenableFuture<Boolean> createRootContainer(CreateContainerOptions... options);

   /**
    * @see AzureBlobClient#deleteRootContainer
    */
   @Named("DeleteContainer")
   @DELETE
   @Path("$root")
   @Fallback(TrueOnNotFoundOr404.class)
   @QueryParams(keys = "restype", values = "container")
   ListenableFuture<Void> deleteRootContainer();

   /**
    * @see AzureBlobClient#listBlobs(String, ListBlobsOptions[])
    */
   @Named("ListBlobs")
   @GET
   @XMLResponseParser(ContainerNameEnumerationResultsHandler.class)
   @Path("{container}")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "list" })
   ListenableFuture<ListBlobsResponse> listBlobs(
            @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
            ListBlobsOptions... options);

   /**
    * @see AzureBlobClient#listBlobs(ListBlobsOptions[])
    */
   @Named("ListBlobs")
   @GET
   @XMLResponseParser(ContainerNameEnumerationResultsHandler.class)
   @Path("$root")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "list" })
   ListenableFuture<ListBlobsResponse> listBlobs(ListBlobsOptions... options);

   /**
    * @see AzureBlobClient#putBlob
    */
   @Named("PutBlob")
   @PUT
   @Path("{container}/{name}")
   @Headers(keys = EXPECT, values = "100-continue")
   @ResponseParser(ParseETagHeader.class)
   ListenableFuture<String> putBlob(
            @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
            @PathParam("name") @ParamParser(BlobName.class) @BinderParam(BindAzureBlobMetadataToRequest.class) org.jclouds.azureblob.domain.AzureBlob object);

   /**
    * @see AzureBlobClient#getBlob
    */
   @Named("GetBlob")
   @GET
   @ResponseParser(ParseBlobFromHeadersAndHttpContent.class)
   @Fallback(NullOnKeyNotFound.class)
   @Path("{container}/{name}")
   ListenableFuture<org.jclouds.azureblob.domain.AzureBlob> getBlob(
            @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
            @PathParam("name") String name, GetOptions... options);

   /**
    * @see AzureBlobClient#getBlobProperties
    */
   @Named("GetBlobProperties")
   @HEAD
   @ResponseParser(ParseBlobPropertiesFromHeaders.class)
   @Fallback(NullOnKeyNotFound.class)
   @Path("{container}/{name}")
   ListenableFuture<BlobProperties> getBlobProperties(
            @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
            @PathParam("name") String name);

   /**
    * @see AzureBlobClient#blobExists
    *
    */
   @Named("GetBlobProperties")
   @HEAD
   @Fallback(FalseOnKeyNotFound.class)
   @Path("{container}/{name}")
   ListenableFuture<Boolean> blobExists(
            @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
            @PathParam("name") String name);

   /**
    * @see AzureBlobClient#setBlobMetadata
    */
   @Named("SetBlobMetadata")
   @PUT
   @Path("{container}/{name}")
   @QueryParams(keys = { "comp" }, values = { "metadata" })
   ListenableFuture<Void> setBlobMetadata(
            @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
            @PathParam("name") String name, @BinderParam(BindMapToHeadersWithPrefix.class) Map<String, String> metadata);

   /**
    * @see AzureBlobClient#deleteBlob
    */
   @Named("DeleteBlob")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("{container}/{name}")
   ListenableFuture<Void> deleteBlob(
            @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
            @PathParam("name") String name);


   /**
    * @see AzureBlobClient#putBlock
    */
   @Named("PutBlock")
   @PUT
   @Path("{container}/{name}")
   @QueryParams(keys = { "comp" }, values = { "block" })
   ListenableFuture<Void> putBlock(@PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
                                   @PathParam("name") String name,
                                   @QueryParam("blockid") @ParamValidators(BlockIdValidator.class) String blockId, Payload part);


   /**
    * @see AzureBlobClient#putBlockList
    */
   @Named("PutBlockList")
   @PUT
   @Path("{container}/{name}")
   @ResponseParser(ParseETagHeader.class)
   @QueryParams(keys = { "comp" }, values = { "blocklist" })
   ListenableFuture<String> putBlockList(@PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
                                         @PathParam("name") String name,
                                         @BinderParam(BindAzureBlocksToRequest.class) List<String> blockIdList);

   /**
    * @see AzureBlobClient#getBlockList
    */
   @Named("GetBlockList")
   @GET
   @Path("{container}/{name}")
   @XMLResponseParser(BlobBlocksResultsHandler.class)
   @QueryParams(keys = { "comp" }, values = { "blocklist" })
   ListenableFuture<ListBlobBlocksResponse> getBlockList(@PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
                                                         @PathParam("name") String name);

}
