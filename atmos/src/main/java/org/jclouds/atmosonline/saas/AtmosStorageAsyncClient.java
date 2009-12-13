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
package org.jclouds.atmosonline.saas;

import java.net.URI;
import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.atmosonline.saas.binders.BindAtmosObjectToPayloadAndMetadataToHeaders;
import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.BoundedSortedSet;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.SystemMetadata;
import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.atmosonline.saas.filters.SignRequest;
import org.jclouds.atmosonline.saas.functions.AtmosObjectName;
import org.jclouds.atmosonline.saas.functions.ParseDirectoryListFromContentAndHeaders;
import org.jclouds.atmosonline.saas.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.atmosonline.saas.functions.ParseSystemMetadataFromHeaders;
import org.jclouds.atmosonline.saas.functions.ReturnEndpointIfAlreadyExists;
import org.jclouds.atmosonline.saas.options.ListOptions;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.functions.ReturnFalseOn404;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

/**
 * Provides asynchronous access to EMC Atmos Online Storage resources via their REST API.
 * <p/>
 * 
 * @see AtmosStorageClient
 * @see <a href="https://community.emc.com/community/labs/atmos_online" />
 * @author Adrian Cole
 */
@Endpoint(AtmosStorage.class)
@RequestFilters(SignRequest.class)
@SkipEncoding( { '/' })
@ConsistencyModel(ConsistencyModels.EVENTUAL)
public interface AtmosStorageAsyncClient {

   AtmosObject newObject();

   /**
    * @see AtmosStorageClient#listDirectories
    */
   @GET
   @Path("/rest/namespace")
   @ResponseParser(ParseDirectoryListFromContentAndHeaders.class)
   @Consumes(MediaType.TEXT_XML)
   Future<? extends BoundedSortedSet<? extends DirectoryEntry>> listDirectories(
            ListOptions... options);

   /**
    * @see AtmosStorageClient#listDirectory
    */
   @GET
   @Path("/rest/namespace/{directoryName}/")
   @ResponseParser(ParseDirectoryListFromContentAndHeaders.class)
   @Consumes(MediaType.TEXT_XML)
   Future<? extends BoundedSortedSet<? extends DirectoryEntry>> listDirectory(
            @PathParam("directoryName") String directoryName, ListOptions... options);

   /**
    * @see AtmosStorageClient#createDirectory
    */
   @POST
   @Path("/rest/namespace/{directoryName}/")
   @ExceptionParser(ReturnEndpointIfAlreadyExists.class)
   @Consumes(MediaType.WILDCARD)
   Future<URI> createDirectory(@PathParam("directoryName") String directoryName);

   /**
    * @see AtmosStorageClient#createFile
    */
   @POST
   @Path("/rest/namespace/{parent}/{name}")
   @Consumes(MediaType.WILDCARD)
   Future<URI> createFile(
            @PathParam("parent") String parent,
            @PathParam("name") @ParamParser(AtmosObjectName.class) @BinderParam(BindAtmosObjectToPayloadAndMetadataToHeaders.class) AtmosObject object);

   /**
    * @see AtmosStorageClient#updateFile
    */
   @PUT
   @Path("/rest/namespace/{parent}/{name}")
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Consumes(MediaType.WILDCARD)
   Future<Void> updateFile(
            @PathParam("parent") String parent,
            @PathParam("name") @ParamParser(AtmosObjectName.class) @BinderParam(BindAtmosObjectToPayloadAndMetadataToHeaders.class) AtmosObject object);

   /**
    * @see AtmosStorageClient#readFile
    */
   @GET
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("/rest/namespace/{path}")
   @Consumes(MediaType.WILDCARD)
   Future<AtmosObject> readFile(@PathParam("path") String path, GetOptions... options);

   /**
    * @see AtmosStorageClient#headFile
    */
   @HEAD
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("/rest/namespace/{path}")
   @Consumes(MediaType.WILDCARD)
   Future<AtmosObject> headFile(@PathParam("path") String path);

   /**
    * @see AtmosStorageClient#getSystemMetadata
    */
   @HEAD
   @ResponseParser(ParseSystemMetadataFromHeaders.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   // currently throws 403 errors @QueryParams(keys = "metadata/system")
   @Path("/rest/namespace/{path}")
   @Consumes(MediaType.WILDCARD)
   Future<SystemMetadata> getSystemMetadata(@PathParam("path") String path);

   /**
    * @see AtmosStorageClient#getUserMetadata
    */
   @HEAD
   @ResponseParser(ParseSystemMetadataFromHeaders.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("/rest/namespace/{path}")
   @QueryParams(keys = "metadata/user")
   @Consumes(MediaType.WILDCARD)
   Future<UserMetadata> getUserMetadata(@PathParam("path") String path);

   /**
    * @see AtmosStorageClient#deletePath
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("/rest/namespace/{path}")
   @Consumes(MediaType.WILDCARD)
   Future<Void> deletePath(@PathParam("path") String path);

   /**
    * @see AtmosStorageClient#pathExists
    */
   @HEAD
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/rest/namespace/{path}")
   @Consumes(MediaType.WILDCARD)
   Future<Boolean> pathExists(@PathParam("path") String path);

   // signature currently doesn't work
   // @POST
   // @QueryParams(keys = "acl")
   // @Headers(keys = { "x-emc-useracl", "x-emc-groupacl" }, values = { "root=FULL_CONTROL",
   // "other=READ" })
   // @Consumes(MediaType.WILDCARD)
   // void makePublic(@Endpoint URI url);

}
