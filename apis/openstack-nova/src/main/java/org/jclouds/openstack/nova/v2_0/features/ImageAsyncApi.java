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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.functions.ReturnEmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.nova.v2_0.binders.BindMetadataToJsonPayload;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.functions.internal.OnlyMetadataValueOrNull;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseImageDetails;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseImages;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.functions.ReturnEmptyMapOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnEmptyPagedIterableOnNotFoundOr404;
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
    * @see ImageApi#list()
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImages.class)
   @Transform(ParseImages.ToPagedIterable.class)
   @ExceptionParser(ReturnEmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends Resource>> list();

   /** @see ImageApi#list(PaginationOptions) */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImages.class)
   @ExceptionParser(ReturnEmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends Resource>> list(PaginationOptions options);

   /**
    * @see ImageApi#listInDetail()
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImageDetails.class)
   @Transform(ParseImageDetails.ToPagedIterable.class)
   @ExceptionParser(ReturnEmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends Image>> listInDetail();

   /** @see ImageApi#listInDetail(PaginationOptions) */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImageDetails.class)
   @ExceptionParser(ReturnEmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends Image>> listInDetail(PaginationOptions options);

   /**
    * @see ImageApi#get
    */
   @GET
   @SelectJson("image")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Image> get(@PathParam("id") String id);

   /**
    * @see ImageApi#delete
    */
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/{id}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@PathParam("id") String id);
   
   /**
    * @see ImageApi#getMetadata
    */
   @GET
   @SelectJson("metadata")
   @Path("/images/{id}/metadata")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptyMapOnNotFoundOr404.class)
   ListenableFuture<Map<String, String>> getMetadata(@PathParam("id") String id);

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
    * @see ImageApi#getMetadata
    */
   @GET
   @Path("/images/{id}/metadata/{key}")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(OnlyMetadataValueOrNull.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<String> getMetadata(@PathParam("id") String id, @PathParam("key") String key);
   
   /**
    * @see ImageApi#updateMetadata
    */
   @PUT
   @Path("/images/{id}/metadata/{key}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @ResponseParser(OnlyMetadataValueOrNull.class)
   @MapBinder(BindMetadataToJsonPayload.class)
   ListenableFuture<String> updateMetadata(@PathParam("id") String id,
            @PathParam("key") @PayloadParam("key") String key, @PathParam("value") @PayloadParam("value") String value);

   
   /**
    * @see ImageApi#deleteMetadata
    */
   @DELETE
   @Consumes
   @Path("/images/{id}/metadata/{key}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteMetadata(@PathParam("id") String id, @PathParam("key") String key);
}
