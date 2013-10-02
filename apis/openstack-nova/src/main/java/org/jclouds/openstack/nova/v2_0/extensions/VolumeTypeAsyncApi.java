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
package org.jclouds.openstack.nova.v2_0.extensions;

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

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyMapOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.VolumeType;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeTypeOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Volume Type features
 *
 * @author Adam Lowe
 * @see VolumeTypeApi
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.VOLUME_TYPES)
@RequestFilters(AuthenticateRequest.class)
@Path("/os-volume-types")
@Consumes(MediaType.APPLICATION_JSON)
public interface VolumeTypeAsyncApi {

   /**
    * @see VolumeTypeApi#list
    */
   @Named("volumetype:list")
   @GET
   @SelectJson("volume_types")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<? extends FluentIterable<? extends VolumeType>> list();


   /**
    * @see VolumeTypeApi#get
    */
   @Named("volumetype:get")
   @GET
   @Path("/{id}")
   @SelectJson("volume_type")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends VolumeType> get(@PathParam("id") String id);

   /**
    * @see VolumeTypeApi#create
    */
   @Named("volumetype:create")
   @POST
   @SelectJson("volume_type")
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("volume_type")
   ListenableFuture<? extends VolumeType> create(@PayloadParam("name") String name, CreateVolumeTypeOptions... options);

   /**
    * @see VolumeTypeApi#delete
    */
   @Named("volumetype:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> delete(@PathParam("id") String id);

   /**
    * @see VolumeTypeApi#getExtraSpecs(String)
    */
   @Named("volumetype:getextraspecs")
   @GET
   @SelectJson("extra_specs")
   @Path("/{id}/extra_specs")
   @Fallback(EmptyMapOnNotFoundOr404.class)
   ListenableFuture<Map<String, String>> getExtraSpecs(@PathParam("id") String id);

   /**
    * @see VolumeTypeApi#updateExtraSpecs(String, java.util.Map)
    */
   @Named("volumetype:udpateextraspecs")
   @POST
   @Path("/{id}/extra_specs")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Boolean> updateExtraSpecs(@PathParam("id") String id, @PayloadParam("extra_specs") Map<String, String> specs);

   /**
    * @see VolumeTypeApi#getExtraSpec(String, String)
    */
   @Named("volumetype:getextraspec")
   @GET
   @Path("/{id}/extra_specs/{key}")
   @Unwrap
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<String> getExtraSpec(@PathParam("id") String id, @PathParam("key") String key);

   /**
    * @see VolumeTypeApi#updateExtraSpec(String, String, String)
    */
   @Named("volumetype:updateextraspec")
   @PUT
   @Path("/{id}/extra_specs/{key}")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"{key}\":\"{value}\"%7D")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> updateExtraSpec(@PathParam("id") String id,
                                          @PathParam("key") @PayloadParam("key") String key,
                                          @PayloadParam("value") String value);

   /**
    * @see VolumeTypeApi#deleteExtraSpec(String, String)
    */
   @Named("volumetype:deleteextraspec")
   @DELETE
   @Path("/{id}/extra_specs/{key}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> deleteExtraSpec(@PathParam("id") String id,
                                             @PathParam("key") String key);

}
