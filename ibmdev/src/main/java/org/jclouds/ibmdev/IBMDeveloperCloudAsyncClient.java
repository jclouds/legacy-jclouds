/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibmdev;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.ibmdev.binders.BindImageVisibilityToJsonPayload;
import org.jclouds.ibmdev.domain.Image;
import org.jclouds.ibmdev.functions.ParseImageFromJson;
import org.jclouds.ibmdev.functions.ParseImagesFromJson;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to IBMDeveloperCloud via their REST API.
 * <p/>
 * 
 * @see IBMDeveloperCloudClient
 * @see <a href="http://www-180.ibm.com/cloud/enterprise/beta/support" />
 * @author Adrian Cole
 */
@Endpoint(IBMDeveloperCloud.class)
@RequestFilters(BasicAuthentication.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface IBMDeveloperCloudAsyncClient {

   /**
    * @see IBMDeveloperCloudClient#listImages()
    */
   @GET
   @Path("/images")
   @ResponseParser(ParseImagesFromJson.class)
   ListenableFuture<Set<? extends Image>> listImages();

   /**
    * @see IBMDeveloperCloudClient#getImage(long)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/images/{imageId}")
   @ResponseParser(ParseImageFromJson.class)
   ListenableFuture<Image> getImage(@PathParam("imageId") long id);

   /**
    * @see IBMDeveloperCloudClient#deleteImage
    */
   @DELETE
   @Path("/images/{imageId}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteImage(@PathParam("imageId") long id);

   /**
    * @see IBMDeveloperCloudClient#setImageVisibility(long, Image.Visibility)
    */
   @PUT
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/images/{imageId}")
   @ResponseParser(ParseImageFromJson.class)
   ListenableFuture<Image> setImageVisibility(@PathParam("imageId") long id,
            @BinderParam(BindImageVisibilityToJsonPayload.class) Image.Visibility visibility);

}
