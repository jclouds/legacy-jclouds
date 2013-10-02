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
package org.jclouds.openstack.nova.v2_0.features;

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyMapOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.binders.BindMetadataToJsonPayload;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.functions.internal.OnlyMetadataValueOrNull;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseImageDetails;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseImages;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Images via the REST API.
 * <p/>
 * 
 * @see ImageApi
 * @author Jeremy Daggett
 */
@RequestFilters(AuthenticateRequest.class)
public interface ImageAsyncApi {

   /**
    * @see ImageApi#list()
    */
   @Named("image:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImages.class)
   @Transform(ParseImages.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends Resource>> list();

   /** @see ImageApi#list(PaginationOptions) */
   @Named("image:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImages.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends Resource>> list(PaginationOptions options);

   /**
    * @see ImageApi#listInDetail()
    */
   @Named("image:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImageDetails.class)
   @Transform(ParseImageDetails.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends Image>> listInDetail();

   /** @see ImageApi#listInDetail(PaginationOptions) */
   @Named("image:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImageDetails.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends Image>> listInDetail(PaginationOptions options);

   /**
    * @see ImageApi#get
    */
   @Named("image:get")
   @GET
   @SelectJson("image")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Image> get(@PathParam("id") String id);

   /**
    * @see ImageApi#delete
    */
   @Named("image:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/{id}")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@PathParam("id") String id);
   
   /**
    * @see ImageApi#getMetadata
    */
   @Named("image:getmetadata")
   @GET
   @SelectJson("metadata")
   @Path("/images/{id}/metadata")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   ListenableFuture<Map<String, String>> getMetadata(@PathParam("id") String id);

   /**
    * @see ImageApi#setMetadata
    */
   @Named("image:setmetadata")
   @PUT
   @SelectJson("metadata")
   @Path("/images/{id}/metadata")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Map<String, String>> setMetadata(@PathParam("id") String id, @PayloadParam("metadata") Map<String, String> metadata);

   /**
    * @see ImageApi#updateMetadata
    */
   @Named("image:updatemetadata")
   @POST
   @SelectJson("metadata")
   @Path("/images/{id}/metadata")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<? extends Map<String, String>> updateMetadata(@PathParam("id") String id, @PayloadParam("metadata") Map<String, String> metadata);

   /**
    * @see ImageApi#getMetadata
    */
   @Named("image:getmetadata")
   @GET
   @Path("/images/{id}/metadata/{key}")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(OnlyMetadataValueOrNull.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<String> getMetadata(@PathParam("id") String id, @PathParam("key") String key);
   
   /**
    * @see ImageApi#updateMetadata
    */
   @Named("image:updatemetadata")
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
   @Named("image:deletemetadata")
   @DELETE
   @Consumes
   @Path("/images/{id}/metadata/{key}")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteMetadata(@PathParam("id") String id, @PathParam("key") String key);
}
