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
package org.jclouds.rackspace.cloudfiles;

import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.binders.BindBlobToEntityAndUserMetadataToHeadersWithPrefix;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.BlobName;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ReturnFalseOn404;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rackspace.CloudFiles;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.functions.ClearAndDeleteContainerIfNotEmpty;
import org.jclouds.rackspace.cloudfiles.functions.ParseBlobMetadataListFromJsonResponse;
import org.jclouds.rackspace.cloudfiles.functions.ParseContainerListFromJsonResponse;
import org.jclouds.rackspace.cloudfiles.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.rackspace.cloudfiles.functions.ParseObjectMetadataFromHeaders;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.jclouds.rackspace.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

/**
 * Provides access to Cloud Files via their REST API.
 * <p/>
 * All commands return a Future of the result from Cloud Files. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090311.pdf" />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
@Endpoint(CloudFiles.class)
public interface CloudFilesBlobStore extends
         BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> {
   @GET
   @ResponseParser(ParseContainerListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/")
   SortedSet<ContainerMetadata> listContainers();

   @GET
   @ResponseParser(ParseContainerListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/")
   SortedSet<ContainerMetadata> listContainers(ListContainerOptions options);

   @HEAD
   @Path("{container}")
   @ExceptionParser(ReturnFalseOn404.class)
   boolean containerExists(@PathParam("container") String container);

   @PUT
   @Path("{container}")
   Future<Boolean> createContainer(@PathParam("container") String container);

   @DELETE
   @ExceptionParser(ClearAndDeleteContainerIfNotEmpty.class)
   @Path("{container}")
   Future<Void> deleteContainer(@PathParam("container") String container);

   @GET
   @QueryParams(keys = "format", values = "json")
   @ResponseParser(ParseBlobMetadataListFromJsonResponse.class)
   @Path("{container}")
   Future<? extends SortedSet<BlobMetadata>> listBlobs(@PathParam("container") String container);

   @PUT
   @Path("{container}/{key}")
   @ResponseParser(ParseETagHeader.class)
   Future<String> putBlob(
            @PathParam("container") String container,
            @PathParam("key") @ParamParser(BlobName.class) @BinderParam(BindBlobToEntityAndUserMetadataToHeadersWithPrefix.class) Blob<BlobMetadata> object);

   @GET
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("{container}/{key}")
   Future<Blob<BlobMetadata>> getBlob(@PathParam("container") String container,
            @PathParam("key") String key);

   @GET
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("{container}/{key}")
   Future<Blob<BlobMetadata>> getBlob(@PathParam("container") String container,
            @PathParam("key") String key, GetOptions options);

   @HEAD
   @ResponseParser(ParseObjectMetadataFromHeaders.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("{container}/{key}")
   BlobMetadata blobMetadata(@PathParam("container") String container, @PathParam("key") String key);

   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("{container}/{key}")
   Future<Void> removeBlob(@PathParam("container") String container, @PathParam("key") String key);

}
