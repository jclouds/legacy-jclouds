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
import java.util.SortedSet;
import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.atmosonline.saas.binders.BindAtmosObjectToEntityAndMetadataToHeaders;
import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.filters.SignRequest;
import org.jclouds.atmosonline.saas.functions.AtmosObjectName;
import org.jclouds.atmosonline.saas.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.atmosonline.saas.xml.ListDirectoryResponseHandler;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to EMC Atmos Online Storage resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.emc.com/community/labs/atmos_online" />
 * @author Adrian Cole
 */
@Endpoint(AtmosStorage.class)
@RequestFilters(SignRequest.class)
@SkipEncoding( { '/' })
public interface AtmosStorageClient {

   AtmosObject newObject();

   @GET
   @Path("/rest/namespace")
   @XMLResponseParser(ListDirectoryResponseHandler.class)
   @Consumes(MediaType.TEXT_XML)
   SortedSet<DirectoryEntry> listDirectories();

   @GET
   @Path("/rest/namespace/{directoryName}/")
   @XMLResponseParser(ListDirectoryResponseHandler.class)
   @Consumes(MediaType.TEXT_XML)
   SortedSet<DirectoryEntry> listDirectory(@PathParam("directoryName") String directoryName);

   @POST
   @Path("/rest/namespace/{directoryName}/")
   @Consumes(MediaType.WILDCARD)
   URI createDirectory(@PathParam("directoryName") String directoryName);

   @POST
   @Path("/rest/namespace/{parent}/{name}")
   @Consumes(MediaType.WILDCARD)
   Future<URI> createFile(
            @PathParam("parent") String parent,
            @PathParam("name") @ParamParser(AtmosObjectName.class) @BinderParam(BindAtmosObjectToEntityAndMetadataToHeaders.class) AtmosObject object);

   @GET
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("/rest/namespace/{path}")
   Future<AtmosObject> readFile(@PathParam("path") String path, GetOptions... options);

   // signature currently doesn't work
   // @POST
   // @QueryParams(keys = "acl")
   // @Headers(keys = { "x-emc-useracl", "x-emc-groupacl" }, values = { "root=FULL_CONTROL",
   // "other=READ" })
   // @Consumes(MediaType.WILDCARD)
   // void makePublic(@Endpoint URI url);

}
