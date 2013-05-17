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

import org.jclouds.Fallbacks.AbsentOn403Or404Or500;
import org.jclouds.Fallbacks.EmptyMapOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.binders.BindMetadataToJsonPayload;
import org.jclouds.openstack.nova.v2_0.domain.RebootType;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.functions.ParseImageIdFromLocationHeader;
import org.jclouds.openstack.nova.v2_0.functions.internal.OnlyMetadataValueOrNull;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseDiagnostics;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseServerDetails;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseServers;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.options.RebuildServerOptions;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Server via their REST API.
 * <p/>
 * 
 * @see ServerApi
 * @see <a href= "http://docs.openstack.org/api/openstack-compute/1.1/content/Servers-d1e2073.html"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticateRequest.class)
public interface ServerAsyncApi {

   /**
    * @see ServerApi#list()
    */
   @Named("server:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/servers")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseServers.class)
   @Transform(ParseServers.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends Resource>> list();

   /** @see ServerApi#list(PaginationOptions) */
   @Named("server:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/servers")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseServers.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends Resource>> list(PaginationOptions options);

   /**
    * @see ServerApi#listInDetail()
    */
   @Named("server:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/servers/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseServerDetails.class)
   @Transform(ParseServerDetails.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends Server>> listInDetail();

   /** @see ServerApi#listInDetail(PaginationOptions) */
   @Named("server:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/servers/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseServerDetails.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends Server>> listInDetail(PaginationOptions options);

   /**
    * @see ServerApi#get
    */
   @Named("server:get")
   @GET
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/servers/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Server> get(@PathParam("id") String id);

   /**
    * @see ServerApi#delete
    */
   @Named("server:delete")
   @DELETE
   @Consumes
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/servers/{id}")
   ListenableFuture<Boolean> delete(@PathParam("id") String id);

   /**
    * @see ServerApi#start
    */
   @Named("server:start")
   @POST
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"os-start\":null}")
   ListenableFuture<Void> start(@PathParam("id") String id);

   /**
    * @see ServerApi#stop
    */
   @Named("server:stop")
   @POST
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"os-stop\":null}")
   ListenableFuture<Void> stop(@PathParam("id") String id);

   /**
    * @see ServerApi#reboot
    */
   @Named("server:reboot")
   @POST
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"reboot\":%7B\"type\":\"{type}\"%7D%7D")
   ListenableFuture<Void> reboot(@PathParam("id") String id, @PayloadParam("type") RebootType rebootType);

   /**
    * @see ServerApi#resize
    */
   @Named("server:resize")
   @POST
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"resize\":%7B\"flavorRef\":{flavorId}%7D%7D")
   ListenableFuture<Void> resize(@PathParam("id") String id, @PayloadParam("flavorId") String flavorId);

   /**
    * @see ServerApi#confirmResize
    */
   @Named("server:resize")
   @POST
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"confirmResize\":null}")
   ListenableFuture<Void> confirmResize(@PathParam("id") String id);

   /**
    * @see ServerApi#revertResize
    */
   @Named("server:resize")
   @POST
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"revertResize\":null}")
   ListenableFuture<Void> revertResize(@PathParam("id") String id);

   /**
    * @see ServerApi#create
    */
   @Named("server:create")
   @POST
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/servers")
   @MapBinder(CreateServerOptions.class)
   ListenableFuture<ServerCreated> create(@PayloadParam("name") String name, @PayloadParam("imageRef") String imageRef,
            @PayloadParam("flavorRef") String flavorRef, CreateServerOptions... options);

   /**
    * @see ServerApi#rebuild
    */
   @Named("server:rebuild")
   @POST
   @Path("/servers/{id}/action")
   @Consumes
   @MapBinder(RebuildServerOptions.class)
   ListenableFuture<Void> rebuild(@PathParam("id") String id, RebuildServerOptions... options);

   /**
    * @see ServerApi#changeAdminPass
    */
   @Named("server:changeadminpass")
   @POST
   @Path("/servers/{id}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"changePassword\":%7B\"adminPass\":\"{adminPass}\"%7D%7D")
   ListenableFuture<Void> changeAdminPass(@PathParam("id") String id, @PayloadParam("adminPass") String adminPass);

   /**
    * @see ServerApi#rename
    */
   @Named("server:rename")
   @PUT
   @Path("/servers/{id}")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"server\":%7B\"name\":\"{name}\"%7D%7D")
   ListenableFuture<Void> rename(@PathParam("id") String id, @PayloadParam("name") String newName);

   /**
    * @see ServerApi#createImageFromServer
    */
   @Named("server:create")
   @POST
   @Path("/servers/{id}/action")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"createImage\":%7B\"name\":\"{name}\", \"metadata\": %7B%7D%7D%7D")
   @Fallback(MapHttp4xxCodesToExceptions.class)
   @ResponseParser(ParseImageIdFromLocationHeader.class)
   ListenableFuture<String> createImageFromServer(@PayloadParam("name") String name, @PathParam("id") String id);

   /**
    * @see ServerApi#getMetadata
    */
   @Named("server:getmetadata")
   @GET
   @SelectJson("metadata")
   @Path("/servers/{id}/metadata")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   ListenableFuture<? extends Map<String, String>> getMetadata(@PathParam("id") String id);

   /**
    * @see ServerApi#setMetadata
    */
   @Named("server:setmetadata")
   @PUT
   @SelectJson("metadata")
   @Path("/servers/{id}/metadata")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<? extends Map<String, String>> setMetadata(@PathParam("id") String id,
            @PayloadParam("metadata") Map<String, String> metadata);

   /**
    * @see ServerApi#updateMetadata
    */
   @Named("server:updatemetadata")
   @POST
   @SelectJson("metadata")
   @Path("/servers/{id}/metadata")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<? extends Map<String, String>> updateMetadata(@PathParam("id") String id,
            @PayloadParam("metadata") Map<String, String> metadata);

   /**
    * @see ServerApi#getMetadata
    */
   @Named("server:getmetadata")
   @GET
   @Path("/servers/{id}/metadata/{key}")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(OnlyMetadataValueOrNull.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<String> getMetadata(@PathParam("id") String id, @PathParam("key") String key);

   /**
    * @see ServerApi#updateMetadata
    */
   @Named("server:updatemetadata")
   @PUT
   @Path("/servers/{id}/metadata/{key}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @ResponseParser(OnlyMetadataValueOrNull.class)
   @MapBinder(BindMetadataToJsonPayload.class)
   ListenableFuture<String> updateMetadata(@PathParam("id") String id,
            @PathParam("key") @PayloadParam("key") String key, @PathParam("value") @PayloadParam("value") String value);

   /**
    * @see ServerApi#deleteMetadata
    */
   @Named("server:deletemetadata")
   @DELETE
   @Consumes
   @Path("/servers/{id}/metadata/{key}")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteMetadata(@PathParam("id") String id, @PathParam("key") String key);

   
   /**
    * @see ServerApi#getDiagnostics
    */
   @Named("server:getdiagnostics")
   @GET
   @Path("/servers/{id}/diagnostics")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(AbsentOn403Or404Or500.class)
   @ResponseParser(ParseDiagnostics.class)
   ListenableFuture<Optional<Map<String, String>>> getDiagnostics(@PathParam("id") String id);
}
