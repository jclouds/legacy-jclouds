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
package org.jclouds.glesys.features;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

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
import org.jclouds.glesys.options.EditServerOptions;
import org.jclouds.glesys.options.ServerStatusOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Server via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 * @author Adam Lowe
 * @see ServerClient
 * @see <a href="https://customer.glesys.com/api.php" />
 */
@RequestFilters(BasicAuthentication.class)
public interface ServerAsyncClient {

   /**
    * @see ServerClient#listServers
    */
   @POST
   @Path("/server/list/format/json")
   @SelectJson("servers")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Server>> listServers();

   /**
    * @see ServerClient#getServerDetails
    */
   @POST
   @Path("/server/details/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   @FormParams(keys = "includestate", values = "true")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ServerDetails> getServerDetails(@FormParam("serverid") String id);

   /**
    * @see ServerClient#getServerStatus
    */
   @POST
   @Path("/server/status/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ServerStatus> getServerStatus(@FormParam("serverid") String id, ServerStatusOptions... options);

   /**
    * @see ServerClient#getServerLimits
    */
   @POST
   @Path("/server/limits/format/json")
   @SelectJson("limits")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<SortedMap<String, ServerLimit>> getServerLimits(@FormParam("serverid") String id);

   /**
    * @see ServerClient#getConsole
    */
   @POST
   @Path("/server/console/format/json")
   @SelectJson("console")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Console> getConsole(@FormParam("serverid") String id);

   /**
    * @see ServerClient#getAllowedArgumentsForCreateServerByPlatform
    */
   @GET
   @Path("/server/allowedarguments/format/json")
   @SelectJson("argumentslist")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Map<String, AllowedArgumentsForCreateServer>> getAllowedArgumentsForCreateServerByPlatform();

   /**
    * @see ServerClient#listTemplates
    */
   @GET
   @Path("/server/templates/format/json")
   @ResponseParser(ParseTemplatesFromHttpResponse.class)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Set<OSTemplate>> listTemplates();

   /**
    * @see ServerClient#stopServer
    */
   @POST
   @Path("/server/resetlimit/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<SortedMap<String, ServerLimit>> resetServerLimit(@FormParam("serverid") String id,
         @FormParam("type") String type);

   /**
    * @see ServerClient#rebootServer
    */
   @POST
   @SelectJson("server")
   @Path("/server/reboot/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> rebootServer(@FormParam("serverid") String id);

   /**
    * @see ServerClient#startServer
    */
   @POST
   @SelectJson("server")
   @Path("/server/start/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> startServer(@FormParam("serverid") String id);

   /**
    * @see ServerClient#stopServer
    */
   @POST
   @SelectJson("server")
   @Path("/server/stop/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> stopServer(@FormParam("serverid") String id);

   /**
    * @see ServerClient#hardStopServer
    */
   @POST
   @SelectJson("server")
   @Path("/server/stop/format/json")
   @FormParams(keys = "type", values = "hard")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> hardStopServer(@FormParam("serverid") String id);

   /**
    * @see ServerClient#createServerWithHostnameAndRootPassword
    */
   @POST
   @SelectJson("server")
   @Path("/server/create/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @MapBinder(CreateServerOptions.class)
   ListenableFuture<ServerDetails> createServerWithHostnameAndRootPassword(ServerSpec serverSpec,
         @PayloadParam("hostname") String hostname, @PayloadParam("rootpassword") String rootPassword,
         CreateServerOptions... options);

   /**
    * @see ServerClient#cloneServer
    */
   @POST
   @Path("/server/clone/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> cloneServer(@FormParam("serverid") String serverid,
         @FormParam("hostname") String hostname, CloneServerOptions... options);

   /**
    * @see ServerClient#editServer
    */
   @POST
   @Path("/server/edit/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> editServer(@FormParam("serverid") String serverid, EditServerOptions... options);

   /**
    * @see ServerClient#destroyServer
    */
   @POST
   @Path("/server/destroy/format/json")
   ListenableFuture<Void> destroyServer(@FormParam("serverid") String id, DestroyServerOptions keepIp);

   /**
    * @see ServerClient#resetPassword
    */
   @POST
   @Path("/server/resetpassword/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerDetails> resetPassword(@FormParam("serverid") String id, @FormParam("rootpassword") String password);

   /**
    * @see ServerClient#getResourceUsage
    */
   @POST
   @Path("/server/resourceusage/format/json")
   @SelectJson("usage")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ResourceUsage> getResourceUsage(@FormParam("serverid") String id, @FormParam("resource") String resource,
         @FormParam("resolution") String resolution);

}
