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
package org.jclouds.rackspace.cloudfiles;

import java.net.URI;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowContainerNotFoundOn404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ReturnFalseOn404;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rackspace.CloudFiles;
import org.jclouds.rackspace.CloudFilesCDN;
import org.jclouds.rackspace.cloudfiles.binders.BindCFObjectToPayload;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;
import org.jclouds.rackspace.cloudfiles.functions.ObjectName;
import org.jclouds.rackspace.cloudfiles.functions.ParseAccountMetadataResponseFromHeaders;
import org.jclouds.rackspace.cloudfiles.functions.ParseCdnUriFromHeaders;
import org.jclouds.rackspace.cloudfiles.functions.ParseContainerCDNMetadataFromHeaders;
import org.jclouds.rackspace.cloudfiles.functions.ParseContainerCDNMetadataListFromJsonResponse;
import org.jclouds.rackspace.cloudfiles.functions.ParseContainerListFromJsonResponse;
import org.jclouds.rackspace.cloudfiles.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.rackspace.cloudfiles.functions.ParseObjectInfoFromHeaders;
import org.jclouds.rackspace.cloudfiles.functions.ParseObjectInfoListFromJsonResponse;
import org.jclouds.rackspace.cloudfiles.functions.ReturnTrueOn404FalseOn409;
import org.jclouds.rackspace.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.jclouds.rackspace.cloudfiles.reference.CloudFilesHeaders;
import org.jclouds.rackspace.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Cloud Files via their REST API.
 * <p/>
 * All commands return a ListenableFuture of the result from Cloud Files. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in {@link ListenableFuture#get()}.
 * 
 * @see CloudFilesClient
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090812.pdf" />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
@Endpoint(CloudFiles.class)
@ConsistencyModel(ConsistencyModels.STRICT)
public interface CloudFilesAsyncClient {

   CFObject newCFObject();

   /**
    * @see CloudFilesClient#getAccountStatistics
    */
   @HEAD
   @ResponseParser(ParseAccountMetadataResponseFromHeaders.class)
   @Path("/")
   ListenableFuture<AccountMetadata> getAccountStatistics();

   /**
    * @see CloudFilesClient#listContainers
    */
   @GET
   @ResponseParser(ParseContainerListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/")
   ListenableFuture<? extends SortedSet<ContainerMetadata>> listContainers(ListContainerOptions... options);

   /**
    * @see CloudFilesClient#setObjectInfo
    */
   @POST
   @Path("{container}/{name}")
   ListenableFuture<Boolean> setObjectInfo(@PathParam("container") String container,
            @PathParam("name") String name,
            @BinderParam(BindMapToHeadersWithPrefix.class) Map<String, String> userMetadata);

   /**
    * @see CloudFilesClient#listCDNContainers
    */
   @GET
   @ResponseParser(ParseContainerCDNMetadataListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/")
   @Endpoint(CloudFilesCDN.class)
   ListenableFuture<? extends SortedSet<ContainerCDNMetadata>> listCDNContainers(
            ListCdnContainerOptions... options);

   // TODO: Container name is not included in CDN HEAD response headers, so we cannot populate it
   // here.
   /**
    * @see CloudFilesClient#getCDNMetadata
    */
   @HEAD
   @ResponseParser(ParseContainerCDNMetadataFromHeaders.class)
   @ExceptionParser(ThrowContainerNotFoundOn404.class)
   @Path("{container}")
   @Endpoint(CloudFilesCDN.class)
   ListenableFuture<ContainerCDNMetadata> getCDNMetadata(@PathParam("container") String container);

   /**
    * @see CloudFilesClient#enableCDN(String, long);
    */
   @POST
   @Path("{container}")
   @Headers(keys = CloudFilesHeaders.CDN_ENABLED, values = "True")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CloudFilesCDN.class)
   ListenableFuture<URI> enableCDN(@PathParam("container") String container,
            @HeaderParam(CloudFilesHeaders.CDN_TTL) long ttl);

   /**
    * @see CloudFilesClient#enableCDN(String)
    */
   @POST
   @Path("{container}")
   @Headers(keys = CloudFilesHeaders.CDN_ENABLED, values = "True")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CloudFilesCDN.class)
   ListenableFuture<URI> enableCDN(@PathParam("container") String container);

   /**
    * @see CloudFilesClient#updateCDN
    */
   @POST
   @Path("{container}")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CloudFilesCDN.class)
   ListenableFuture<URI> updateCDN(@PathParam("container") String container,
            @HeaderParam(CloudFilesHeaders.CDN_TTL) long ttl);

   /**
    * @see CloudFilesClient#disableCDN
    */
   @POST
   @Path("{container}")
   @Headers(keys = CloudFilesHeaders.CDN_ENABLED, values = "False")
   @Endpoint(CloudFilesCDN.class)
   ListenableFuture<Boolean> disableCDN(@PathParam("container") String container);

   /**
    * @see CloudFilesClient#createContainer
    */
   @PUT
   @Path("{container}")
   ListenableFuture<Boolean> createContainer(@PathParam("container") String container);

   /**
    * @see CloudFilesClient#deleteContainerIfEmpty
    */
   @DELETE
   @ExceptionParser(ReturnTrueOn404FalseOn409.class)
   @Path("{container}")
   ListenableFuture<Boolean> deleteContainerIfEmpty(@PathParam("container") String container);

   /**
    * @see CloudFilesClient#listObjects
    */
   @GET
   @QueryParams(keys = "format", values = "json")
   @ResponseParser(ParseObjectInfoListFromJsonResponse.class)
   @Path("{container}")
   ListenableFuture<ListContainerResponse<ObjectInfo>> listObjects(@PathParam("container") String container,
            ListContainerOptions... options);

   /**
    * @see CloudFilesClient#containerExists
    */
   @HEAD
   @Path("{container}")
   @ExceptionParser(ReturnFalseOn404.class)
   ListenableFuture<Boolean> containerExists(@PathParam("container") String container);

   /**
    * @see CloudFilesClient#putObject
    */
   @PUT
   @Path("{container}/{name}")
   @ResponseParser(ParseETagHeader.class)
   ListenableFuture<String> putObject(
            @PathParam("container") String container,
            @PathParam("name") @ParamParser(ObjectName.class) @BinderParam(BindCFObjectToPayload.class) CFObject object);

   /**
    * @see CloudFilesClient#getObject
    */
   @GET
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("{container}/{name}")
   ListenableFuture<CFObject> getObject(@PathParam("container") String container,
            @PathParam("name") String name, GetOptions... options);

   /**
    * @see CloudFilesClient#getObjectInfo
    */
   @HEAD
   @ResponseParser(ParseObjectInfoFromHeaders.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("{container}/{name}")
   ListenableFuture<MutableObjectInfoWithMetadata> getObjectInfo(@PathParam("container") String container,
            @PathParam("name") String name);

   /**
    * @see CloudFilesClient#removeObject
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("{container}/{name}")
   ListenableFuture<Void> removeObject(@PathParam("container") String container,
            @PathParam("name") String name);

}
