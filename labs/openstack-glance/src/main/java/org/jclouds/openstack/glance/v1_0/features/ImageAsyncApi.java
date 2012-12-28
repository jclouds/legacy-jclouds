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
package org.jclouds.openstack.glance.v1_0.features;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.io.Payload;
import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;
import org.jclouds.openstack.glance.v1_0.functions.ParseImageDetailsFromHeaders;
import org.jclouds.openstack.glance.v1_0.functions.internal.ParseImageDetails;
import org.jclouds.openstack.glance.v1_0.functions.internal.ParseImages;
import org.jclouds.openstack.glance.v1_0.options.CreateImageOptions;
import org.jclouds.openstack.glance.v1_0.options.ListImageOptions;
import org.jclouds.openstack.glance.v1_0.options.UpdateImageOptions;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Image Services
 * 
 * @see ImageApi
 * @author Adrian Cole
 * @author Adam Lowe
 * @see <a href="http://glance.openstack.org/glanceapi.html">api doc</a>
 * @see <a href="https://github.com/openstack/glance/blob/master/glance/api/v1/images.py">api src</a>
 */
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
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends Image>> list();

   /** @see ImageApi#list(ListImageOptions) */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImages.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends Image>> list(ListImageOptions options);

   /**
    * @see ImageApi#listInDetail()
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImageDetails.class)
   @Transform(ParseImageDetails.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends ImageDetails>> listInDetail();

   /** @see ImageApi#listInDetail(ListImageOptions) */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/images/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseImageDetails.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends ImageDetails>> listInDetail(ListImageOptions options);

   
   /**
    * @see ImageApi#get
    */
   @HEAD
   @Path("/images/{id}")
   @ResponseParser(ParseImageDetailsFromHeaders.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<ImageDetails> get(@PathParam("id") String id);
   
   /**
    * @see ImageApi#getAsStream
    */
   @GET
   @Path("/images/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<InputStream> getAsStream(@PathParam("id") String id);

   /**
    * @see ImageApi#create
    */
   @POST
   @Path("/images")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @SelectJson("image")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<? extends ImageDetails> create(@HeaderParam("x-image-meta-name") String name, Payload payload, CreateImageOptions... options);

   /**
    * @see ImageApi#reserve
    */
   @POST
   @Path("/images")
   @SelectJson("image")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<? extends ImageDetails> reserve(@HeaderParam("x-image-meta-name") String name, CreateImageOptions... options);

   /**
    * @see ImageApi#upload
    */
   @PUT
   @Path("/images/{id}")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @SelectJson("image")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<? extends ImageDetails> upload(@PathParam("id") String id, Payload imageData, UpdateImageOptions... options);

   /**
    * @see ImageApi#update
    */
   @PUT
   @Path("/images/{id}")
   @SelectJson("image")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<? extends ImageDetails> update(@PathParam("id") String id, UpdateImageOptions... options);

   /**
    * @see ImageApi#delete
    */
   @DELETE
   @Path("/images/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> delete(@PathParam("id") String id);
}
