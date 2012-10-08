/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.atmos;

import java.net.URI;

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

import org.jclouds.atmos.binders.BindMetadataToHeaders;
import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.domain.BoundedSet;
import org.jclouds.atmos.domain.DirectoryEntry;
import org.jclouds.atmos.domain.SystemMetadata;
import org.jclouds.atmos.domain.UserMetadata;
import org.jclouds.atmos.filters.SignRequest;
import org.jclouds.atmos.functions.AtmosObjectName;
import org.jclouds.atmos.functions.ParseDirectoryListFromContentAndHeaders;
import org.jclouds.atmos.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.atmos.functions.ParseSystemMetadataFromHeaders;
import org.jclouds.atmos.functions.ParseUserMetadataFromHeaders;
import org.jclouds.atmos.functions.ReturnEndpointIfAlreadyExists;
import org.jclouds.atmos.functions.ReturnTrueIfGroupACLIsOtherRead;
import org.jclouds.atmos.options.ListOptions;
import org.jclouds.atmos.options.PutOptions;
import org.jclouds.blobstore.functions.ThrowContainerNotFoundOn404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;

import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Provides;

/**
 * Provides asynchronous access to EMC Atmos Online Storage resources via their REST API.
 * <p/>
 * 
 * @see AtmosClient
 * @see <a href="https://community.emc.com/community/labs/atmos_online" />
 * @author Adrian Cole
 */
@RequestFilters(SignRequest.class)
@SkipEncoding('/')
@Path("/rest/namespace")
public interface AtmosAsyncClient {
   /**
    * Creates a default implementation of AtmosObject
    */
   @Provides
   AtmosObject newObject();

   /**
    * @see AtmosClient#listDirectories
    */
   @GET
   @ResponseParser(ParseDirectoryListFromContentAndHeaders.class)
   @Consumes(MediaType.TEXT_XML)
   ListenableFuture<BoundedSet<? extends DirectoryEntry>> listDirectories(ListOptions... options);

   /**
    * @see AtmosClient#listDirectory
    */
   @GET
   @Path("/{directoryName}/")
   @ResponseParser(ParseDirectoryListFromContentAndHeaders.class)
   @ExceptionParser(ThrowContainerNotFoundOn404.class)
   @Consumes(MediaType.TEXT_XML)
   ListenableFuture<BoundedSet<? extends DirectoryEntry>> listDirectory(
            @PathParam("directoryName") String directoryName, ListOptions... options);

   /**
    * @see AtmosClient#createDirectory
    */
   @POST
   @Path("/{directoryName}/")
   @ExceptionParser(ReturnEndpointIfAlreadyExists.class)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<URI> createDirectory(@PathParam("directoryName") String directoryName, PutOptions... options);

   /**
    * @see AtmosClient#createFile
    */
   @POST
   @Path("/{parent}/{name}")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<URI> createFile(
            @PathParam("parent") String parent,
            @PathParam("name") @ParamParser(AtmosObjectName.class) @BinderParam(BindMetadataToHeaders.class) AtmosObject object,
            PutOptions... options);

   /**
    * @see AtmosClient#updateFile
    */
   @PUT
   @Path("/{parent}/{name}")
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<Void> updateFile(
            @PathParam("parent") String parent,
            @PathParam("name") @ParamParser(AtmosObjectName.class) @BinderParam(BindMetadataToHeaders.class) AtmosObject object,
            PutOptions... options);

   /**
    * @see AtmosClient#readFile
    */
   @GET
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<AtmosObject> readFile(@PathParam("path") String path, GetOptions... options);

   /**
    * @see AtmosClient#headFile
    */
   @HEAD
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<AtmosObject> headFile(@PathParam("path") String path);

   /**
    * @see AtmosClient#getSystemMetadata
    */
   @HEAD
   @ResponseParser(ParseSystemMetadataFromHeaders.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   // currently throws 403 errors @QueryParams(keys = "metadata/system")
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<SystemMetadata> getSystemMetadata(@PathParam("path") String path);

   /**
    * @see AtmosClient#getUserMetadata
    */
   @HEAD
   @ResponseParser(ParseUserMetadataFromHeaders.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/{path}")
   @QueryParams(keys = "metadata/user")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<UserMetadata> getUserMetadata(@PathParam("path") String path);

   /**
    * @see AtmosClient#deletePath
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<Void> deletePath(@PathParam("path") String path);

   /**
    * @see AtmosClient#pathExists
    */
   @HEAD
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   ListenableFuture<Boolean> pathExists(@PathParam("path") String path);

   /**
    * @see AtmosClient#isPublic
    */
   @HEAD
   @ResponseParser(ReturnTrueIfGroupACLIsOtherRead.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> isPublic(@PathParam("path") String path);

}
