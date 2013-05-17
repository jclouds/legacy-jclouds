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
package org.jclouds.atmos;

import static com.google.common.net.HttpHeaders.EXPECT;

import java.io.Closeable;
import java.net.URI;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.atmos.binders.BindMetadataToHeaders;
import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.domain.BoundedSet;
import org.jclouds.atmos.domain.DirectoryEntry;
import org.jclouds.atmos.domain.SystemMetadata;
import org.jclouds.atmos.domain.UserMetadata;
import org.jclouds.atmos.fallbacks.EndpointIfAlreadyExists;
import org.jclouds.atmos.filters.SignRequest;
import org.jclouds.atmos.functions.AtmosObjectName;
import org.jclouds.atmos.functions.ParseDirectoryListFromContentAndHeaders;
import org.jclouds.atmos.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.atmos.functions.ParseSystemMetadataFromHeaders;
import org.jclouds.atmos.functions.ParseUserMetadataFromHeaders;
import org.jclouds.atmos.functions.ReturnTrueIfGroupACLIsOtherRead;
import org.jclouds.atmos.options.ListOptions;
import org.jclouds.atmos.options.PutOptions;
import org.jclouds.blobstore.BlobStoreFallbacks.ThrowContainerNotFoundOn404;
import org.jclouds.blobstore.BlobStoreFallbacks.ThrowKeyNotFoundOn404;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Provides;

/**
 * Provides asynchronous access to EMC Atmos Online Storage resources via their REST API.
 * <p/>
 * 
 * @see AtmosClient
 * @see <a href="https://community.emc.com/community/labs/atmos_online" />
 * @author Adrian Cole
 * 
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(AtmosClient.class)} as
 *             {@link AtmosAsyncClient} interface will be removed in jclouds 1.7.
 */
@Deprecated
@RequestFilters(SignRequest.class)
@Path("/rest/namespace")
public interface AtmosAsyncClient extends Closeable {
   /**
    * Creates a default implementation of AtmosObject
    */
   @Provides
   AtmosObject newObject();

   /**
    * @see AtmosClient#listDirectories
    */
   @Named("ListDirectory")
   @GET
   @ResponseParser(ParseDirectoryListFromContentAndHeaders.class)
   @Consumes(MediaType.TEXT_XML)
   ListenableFuture<BoundedSet<? extends DirectoryEntry>> listDirectories(ListOptions... options);

   /**
    * @see AtmosClient#listDirectory
    */
   @Named("ListDirectory")
   @GET
   @Path("/{directoryName}/")
   @ResponseParser(ParseDirectoryListFromContentAndHeaders.class)
   @Fallback(ThrowContainerNotFoundOn404.class)
   @Consumes(MediaType.TEXT_XML)
   ListenableFuture<BoundedSet<? extends DirectoryEntry>> listDirectory(
            @PathParam("directoryName") String directoryName, ListOptions... options);

   /**
    * @see AtmosClient#createDirectory
    */
   @Named("CreateDirectory")
   @POST
   @Path("/{directoryName}/")
   @Fallback(EndpointIfAlreadyExists.class)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<URI> createDirectory(@PathParam("directoryName") String directoryName, PutOptions... options);

   /**
    * @see AtmosClient#createFile
    */
   @Named("CreateObject")
   @POST
   @Path("/{parent}/{name}")
   @Headers(keys = EXPECT, values = "100-continue")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<URI> createFile(
            @PathParam("parent") String parent,
            @PathParam("name") @ParamParser(AtmosObjectName.class) @BinderParam(BindMetadataToHeaders.class) AtmosObject object,
            PutOptions... options);

   /**
    * @see AtmosClient#updateFile
    */
   @Named("UpdateObject")
   @PUT
   @Path("/{parent}/{name}")
   @Headers(keys = EXPECT, values = "100-continue")
   @Fallback(ThrowKeyNotFoundOn404.class)
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<Void> updateFile(
            @PathParam("parent") String parent,
            @PathParam("name") @ParamParser(AtmosObjectName.class) @BinderParam(BindMetadataToHeaders.class) AtmosObject object,
            PutOptions... options);

   /**
    * @see AtmosClient#readFile
    */
   @Named("ReadObject")
   @GET
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<AtmosObject> readFile(@PathParam("path") String path, GetOptions... options);

   /**
    * @see AtmosClient#headFile
    */
   @Named("GetObjectMetadata")
   @HEAD
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<AtmosObject> headFile(@PathParam("path") String path);

   /**
    * @see AtmosClient#getSystemMetadata
    */
   @Named("GetSystemMetadata")
   @HEAD
   @ResponseParser(ParseSystemMetadataFromHeaders.class)
   @Fallback(NullOnNotFoundOr404.class)
   // currently throws 403 errors @QueryParams(keys = "metadata/system")
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<SystemMetadata> getSystemMetadata(@PathParam("path") String path);

   /**
    * @see AtmosClient#getUserMetadata
    */
   @Named("GetUserMetadata")
   @HEAD
   @ResponseParser(ParseUserMetadataFromHeaders.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{path}")
   @QueryParams(keys = "metadata/user")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<UserMetadata> getUserMetadata(@PathParam("path") String path);

   /**
    * @see AtmosClient#deletePath
    */
   @Named("DeleteObject")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<Void> deletePath(@PathParam("path") String path);

   /**
    * @see AtmosClient#pathExists
    */
   @Named("GetObjectMetadata")
   @HEAD
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<Boolean> pathExists(@PathParam("path") String path);

   /**
    * @see AtmosClient#isPublic
    */
   @Named("GetObjectMetadata")
   @HEAD
   @ResponseParser(ReturnTrueIfGroupACLIsOtherRead.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> isPublic(@PathParam("path") String path);

}
