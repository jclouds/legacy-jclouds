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
package org.jclouds.openstack.nova.v2_0.features;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.functions.ReturnEmptyMapOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Images via the REST API.
 * <p/>
 * 
 * @see ImageApi
 * @author Jeremy Daggett
 */
@SkipEncoding({ '/', '=' })
@RequestFilters(AuthenticateRequest.class)
public interface ImageAsyncApi {

   /**
    * @see ImageApi#listImages
    */
   @GET
   @SelectJson("images")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<? extends Resource>> listImages();

   /**
    * @see ImageApi#listImagesInDetail
    */
   @GET
   @SelectJson("images")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/detail")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<? extends Image>> listImagesInDetail();

   /**
    * @see ImageApi#getImage
    */
   @GET
   @SelectJson("image")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Image> getImage(@PathParam("id") String id);

   /**
    * @see ImageApi#deleteImage
    */
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/{id}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteImage(@PathParam("id") String id);
   
   /**
    * @see ImageApi#listMetadata
    */
   @GET
   @SelectJson("metadata")
   @Path("/images/{id}/metadata")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptyMapOnNotFoundOr404.class)
   ListenableFuture<Map<String, String>> listMetadata(@PathParam("id") String id);

   /**
    * @see ImageApi#setMetadata
    */
   @PUT
   @SelectJson("metadata")
   @Path("/images/{id}/metadata")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptyMapOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Map<String, String>> setMetadata(@PathParam("id") String id, @PayloadParam("metadata") Map<String, String> metadata);

   /**
    * @see ImageApi#updateMetadata
    */
   @POST
   @SelectJson("metadata")
   @Path("/images/{id}/metadata")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptyMapOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<? extends Map<String, String>> updateMetadata(@PathParam("id") String id, @PayloadParam("metadata") Map<String, String> metadata);

   /**
    * @see ImageApi#getMetadataItem
    */
   @GET
   @SelectJson("metadata")
   @Path("/images/{id}/metadata/{key}")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Map<String, String>> getMetadataItem(@PathParam("id") String id, @PathParam("key") String key);

   
   /**
    * @see ImageApi#setMetadataItem
    */
   @PUT
   @SelectJson("metadata")
   @Path("/images/{id}/metadata/{key}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptyMapOnNotFoundOr404.class)
   @Payload("%7B\"metadata\":%7B\"{key}\":\"{value}\"%7D%7D")
   ListenableFuture<? extends Map<String, String>> setMetadataItem(@PathParam("id") String id, @PathParam("key") String key, @PathParam("value") String value);

   
   /**
    * @see ImageApi#deleteMetadataItem
    */
   @DELETE
   @Consumes
   @Path("/images/{id}/metadata/{key}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteMetadataItem(@PathParam("id") String id, @PathParam("key") String key);
}
