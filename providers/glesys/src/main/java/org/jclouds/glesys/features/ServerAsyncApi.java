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
package org.jclouds.glesys.features;

import java.util.Map;
import java.util.SortedMap;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.glesys.domain.AllowedArgumentsForCreateServer;
import org.jclouds.glesys.domain.Console;
import org.jclouds.glesys.domain.OSTemplate;
import org.jclouds.glesys.domain.ResourceUsage;
import org.jclouds.glesys.domain.Server;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.domain.ServerLimit;
import org.jclouds.glesys.domain.ServerSpec;
import org.jclouds.glesys.domain.ServerStatus;
import org.jclouds.glesys.functions.ParseTemplatesFromHttpResponse;
import org.jclouds.glesys.options.CloneServerOptions;
import org.jclouds.glesys.options.CreateServerOptions;
import org.jclouds.glesys.options.DestroyServerOptions;
import org.jclouds.glesys.options.ServerStatusOptions;
import org.jclouds.glesys.options.UpdateServerOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Server via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 * @author Adam Lowe
 * @see ServerApi
 * @see <a href="https://github.com/GleSYS/API/wiki/API-Documentation" />
 */
@RequestFilters(BasicAuthentication.class)
public interface ServerAsyncApi {

   /**
    * @see ServerApi#list
    */
   @Named("server:list")
   @POST
   @Path("/server/list/format/json")
   @SelectJson("servers")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<FluentIterable<Server>> list();

   /**
    * @see ServerApi#get
    */
   @Named("server:details")
   @POST
   @Path("/server/details/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   @FormParams(keys = "includestate", values = "true")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<ServerDetails> get(@FormParam("serverid") String id);

   /**
    * @see ServerApi#getStatus
    */
   @Named("server:status")
   @POST
   @Path("/server/status/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<ServerStatus> getStatus(@FormParam("serverid") String id, ServerStatusOptions... options);

   /**
    * @see ServerApi#getLimits
    */
   @Named("server:limits")
   @POST
   @Path("/server/limits/format/json")
   @SelectJson("limits")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<SortedMap<String, ServerLimit>> getLimits(@FormParam("serverid") String id);

   /**
    * @see ServerApi#getConsole
    */
   @Named("server:console")
   @POST
   @Path("/server/console/format/json")
   @SelectJson("console")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Console> getConsole(@FormParam("serverid") String id);

   /**
    * @see ServerApi#getAllowedArgumentsForCreateByPlatform
    */
   @Named("server:allowedarguments")
   @GET
   @Path("/server/allowedarguments/format/json")
   @SelectJson("argumentslist")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Map<String, AllowedArgumentsForCreateServer>> getAllowedArgumentsForCreateByPlatform();

   /**
    * @see ServerApi#listTemplates
    */
   @Named("server:templates")
   @GET
   @Path("/server/templates/format/json")
   @ResponseParser(ParseTemplatesFromHttpResponse.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<FluentIterable<OSTemplate>> listTemplates();

   /**
    * @see ServerApi#stop
    */
   @Named("server:resetlimit")
   @POST
   @Path("/server/resetlimit/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<SortedMap<String, ServerLimit>> resetLimit(@FormParam("serverid") String id,
         @FormParam("type") String type);

   /**
    * @see ServerApi#reboot
    */
   @Named("server:reboot")
   @POST
   @SelectJson("server")
   @Path("/server/reboot/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> reboot(@FormParam("serverid") String id);

   /**
    * @see ServerApi#start
    */
   @Named("server:start")
   @POST
   @SelectJson("server")
   @Path("/server/start/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> start(@FormParam("serverid") String id);

   /**
    * @see ServerApi#stop
    */
   @Named("server:stop")
   @POST
   @SelectJson("server")
   @Path("/server/stop/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> stop(@FormParam("serverid") String id);

   /**
    * @see ServerApi#hardStop
    */
   @Named("server:stop:hard")
   @POST
   @SelectJson("server")
   @Path("/server/stop/format/json")
   @FormParams(keys = "type", values = "hard")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> hardStop(@FormParam("serverid") String id);

   /**
    * @see ServerApi#createWithHostnameAndRootPassword
    */
   @Named("server:create")
   @POST
   @SelectJson("server")
   @Path("/server/create/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @MapBinder(CreateServerOptions.class)
   ListenableFuture<ServerDetails> createWithHostnameAndRootPassword(ServerSpec serverSpec,
         @PayloadParam("hostname") String hostname, @PayloadParam("rootpassword") String rootPassword,
         CreateServerOptions... options);

   /**
    * @see ServerApi#clone
    */
   @Named("server:clone")
   @POST
   @Path("/server/clone/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> clone(@FormParam("serverid") String serverid,
         @FormParam("hostname") String hostname, CloneServerOptions... options);

   /**
    * @see ServerApi#update
    */
   @Named("server:edit")
   @POST
   @Path("/server/edit/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> update(@FormParam("serverid") String serverid, UpdateServerOptions options);

   /**
    * @see ServerApi#destroy
    */
   @Named("server:destroy")
   @POST
   @Path("/server/destroy/format/json")
   ListenableFuture<Void> destroy(@FormParam("serverid") String id, DestroyServerOptions keepIp);

   /**
    * @see ServerApi#resetPassword
    */
   @Named("server:resetpassword")
   @POST
   @Path("/server/resetpassword/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> resetPassword(@FormParam("serverid") String id, @FormParam("rootpassword") String password);

   /**
    * @see ServerApi#getResourceUsage
    */
   @Named("server:resourceusage")
   @POST
   @Path("/server/resourceusage/format/json")
   @SelectJson("usage")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ResourceUsage> getResourceUsage(@FormParam("serverid") String id, @FormParam("resource") String resource,
         @FormParam("resolution") String resolution);

}
