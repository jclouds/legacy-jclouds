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
package org.jclouds.mezeo.pcs2;

import java.io.InputStream;
import java.net.URI;
import java.util.SortedSet;
import java.util.concurrent.Future;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.jclouds.blobstore.decorators.AddBlobEntityAsMultipartForm;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.mezeo.pcs2.decorators.AddDataAndLength;
import org.jclouds.mezeo.pcs2.decorators.AddContainerNameAsXmlEntity;
import org.jclouds.mezeo.pcs2.decorators.AddFileInfoAsXmlEntity;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.mezeo.pcs2.options.PutBlockOptions;
import org.jclouds.mezeo.pcs2.xml.FileListToContainerMetadataListHandler;
import org.jclouds.mezeo.pcs2.xml.FileListToFileMetadataListHandler;
import org.jclouds.rest.annotations.DecoratorParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to Mezeo PCS v2 via their REST API.
 * <p/>
 * 
 * @see <a href=
 *      "http://developer.mezeo.com/mezeo-developer-center/documentation/howto-using-curl-to-access-api"
 *      />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(BasicAuthentication.class)
public interface PCSConnection {

   @GET
   @XMLResponseParser(FileListToContainerMetadataListHandler.class)
   @Headers(keys = "X-Cloud-Depth", values = "2")
   @Path("/contents")
   @Endpoint(RootContainer.class)
   SortedSet<ContainerMetadata> listContainers();

   @POST
   @Path("/contents")
   @Endpoint(RootContainer.class)
   Future<URI> createContainer(@DecoratorParam(AddContainerNameAsXmlEntity.class) String container);

   @POST
   @Path("/contents")
   Future<URI> createContainer(@Endpoint URI parent,
            @DecoratorParam(AddContainerNameAsXmlEntity.class) String container);

   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   Future<Void> deleteContainer(@Endpoint URI container);

   @GET
   @XMLResponseParser(FileListToFileMetadataListHandler.class)
   @Headers(keys = "X-Cloud-Depth", values = "2")
   @Path("/contents")
   Future<? extends SortedSet<FileMetadata>> listFiles(@Endpoint URI container);

   @GET
   @XMLResponseParser(FileListToContainerMetadataListHandler.class)
   @Headers(keys = "X-Cloud-Depth", values = "2")
   @Path("/contents")
   Future<? extends SortedSet<ContainerMetadata>> listContainers(@Endpoint URI container);

   @POST
   @Path("/contents")
   Future<URI> uploadFile(@Endpoint URI container,
            @DecoratorParam(AddBlobEntityAsMultipartForm.class) PCSFile object);

   @POST
   @Path("/contents")
   Future<URI> createFile(@Endpoint URI container,
            @DecoratorParam(AddFileInfoAsXmlEntity.class) PCSFile object);
   
   @PUT
   @Path("/content")
   Future<Void> uploadBlock(@Endpoint URI file,
            @DecoratorParam(AddDataAndLength.class) PCSFile object, PutBlockOptions ... options);

   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   Future<Void> deleteFile(@Endpoint URI file);

   @GET
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("/content")
   Future<InputStream> downloadFile(@Endpoint URI file);

}
