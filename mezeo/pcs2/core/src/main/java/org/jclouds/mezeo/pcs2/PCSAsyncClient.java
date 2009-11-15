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
package org.jclouds.mezeo.pcs2;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.mezeo.pcs2.binders.BindContainerNameToXmlEntity;
import org.jclouds.mezeo.pcs2.binders.BindDataToEntity;
import org.jclouds.mezeo.pcs2.binders.BindFileInfoToXmlEntity;
import org.jclouds.mezeo.pcs2.binders.BindPCSFileToMultipartForm;
import org.jclouds.mezeo.pcs2.domain.ContainerList;
import org.jclouds.mezeo.pcs2.domain.FileInfoWithMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.mezeo.pcs2.functions.AddMetadataItemIntoMap;
import org.jclouds.mezeo.pcs2.options.PutBlockOptions;
import org.jclouds.mezeo.pcs2.xml.ContainerHandler;
import org.jclouds.mezeo.pcs2.xml.FileHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.binders.BindToStringEntity;

/**
 * Provides asynchronous access to Mezeo PCS v2 via their REST API.
 * <p/>
 * 
 * @see PCSClient
 * @see <a href=
 *      "http://developer.mezeo.com/mezeo-developer-center/documentation/howto-using-curl-to-access-api"
 *      />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(BasicAuthentication.class)
public interface PCSAsyncClient {
   PCSFile newFile();

   /**
    * @see PCSAsyncClient#list()
    */
   @GET
   @XMLResponseParser(ContainerHandler.class)
   @Headers(keys = "X-Cloud-Depth", values = "2")
   @Endpoint(RootContainer.class)
   Future<? extends ContainerList> list();

   /**
    * @see PCSAsyncClient#list(URI)
    */
   @GET
   @XMLResponseParser(ContainerHandler.class)
   @Headers(keys = "X-Cloud-Depth", values = "2")
   Future<? extends ContainerList> list(@Endpoint URI container);

   /**
    * @see PCSAsyncClient#createContainer
    */
   @POST
   @Path("/contents")
   @Endpoint(RootContainer.class)
   Future<URI> createContainer(@BinderParam(BindContainerNameToXmlEntity.class) String container);

   /**
    * @see PCSAsyncClient#createContainer
    */
   @POST
   @Path("/contents")
   Future<URI> createContainer(@Endpoint URI parent,
            @BinderParam(BindContainerNameToXmlEntity.class) String container);

   /**
    * @see PCSAsyncClient#deleteContainer
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   Future<Void> deleteContainer(@Endpoint URI container);

   /**
    * @see PCSAsyncClient#uploadFile
    */
   @POST
   @Path("/contents")
   Future<URI> uploadFile(@Endpoint URI container,
            @BinderParam(BindPCSFileToMultipartForm.class) PCSFile object);

   /**
    * @see PCSAsyncClient#createFile
    */
   @POST
   @Path("/contents")
   Future<URI> createFile(@Endpoint URI container,
            @BinderParam(BindFileInfoToXmlEntity.class) PCSFile object);

   /**
    * @see PCSAsyncClient#uploadBlock
    */
   @PUT
   @Path("/content")
   Future<Void> uploadBlock(@Endpoint URI file,
            @BinderParam(BindDataToEntity.class) PCSFile object, PutBlockOptions... options);

   /**
    * @see PCSAsyncClient#deleteFile
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   Future<Void> deleteFile(@Endpoint URI file);

   /**
    * @see PCSAsyncClient#downloadFile
    */
   @GET
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("/content")
   Future<InputStream> downloadFile(@Endpoint URI file);

   /**
    * @see PCSAsyncClient#getFileInfo
    */
   @GET
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @XMLResponseParser(FileHandler.class)
   @Headers(keys = "X-Cloud-Depth", values = "2")
   Future<FileInfoWithMetadata> getFileInfo(@Endpoint URI file);

   /**
    * @see PCSAsyncClient#putMetadataItem
    */
   @PUT
   @Path("/metadata/{key}")
   Future<Void> putMetadataItem(@Endpoint URI resource, @PathParam("key") String key,
            @BinderParam(BindToStringEntity.class) String value);

   /**
    * @see PCSAsyncClient#addMetadataItemToMap
    */
   @GET
   @ResponseParser(AddMetadataItemIntoMap.class)
   @Path("/metadata/{key}")
   Future<Void> addMetadataItemToMap(@Endpoint URI resource, @PathParam("key") String key,
            Map<String, String> map);
}
