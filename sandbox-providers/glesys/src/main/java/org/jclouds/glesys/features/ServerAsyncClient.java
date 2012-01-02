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

import org.jclouds.glesys.domain.Server;
import org.jclouds.glesys.domain.ServerAllowedArguments;
import org.jclouds.glesys.domain.ServerConsole;
import org.jclouds.glesys.domain.ServerCreated;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.domain.ServerLimit;
import org.jclouds.glesys.domain.ServerStatus;
import org.jclouds.glesys.domain.ServerTemplate;
import org.jclouds.glesys.functions.ParseServerTemplatesFromHttpResponse;
import org.jclouds.glesys.options.ServerCloneOptions;
import org.jclouds.glesys.options.ServerCreateOptions;
import org.jclouds.glesys.options.ServerDestroyOptions;
import org.jclouds.glesys.options.ServerEditOptions;
import org.jclouds.glesys.options.ServerStatusOptions;
import org.jclouds.glesys.options.ServerStopOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.ExceptionParser;
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
    * @see ServerClient#getServerConsole
    */
   @POST
   @Path("/server/console/format/json")
   @SelectJson("remote")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ServerConsole> getServerConsole(@FormParam("serverid") String id);


   /**
    * @see ServerClient#getServerAllowedArguments
    */
   @GET
   @Path("/server/allowedarguments/format/json")
   @SelectJson("argumentslist")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Map<String, ServerAllowedArguments>> getServerAllowedArguments();
   
   /**
    * @see ServerClient#getTemplates
    */
   @GET
   @Path("/server/templates/format/json")
   @ResponseParser(ParseServerTemplatesFromHttpResponse.class)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Set<ServerTemplate>> getTemplates();
   
   /**
    * @see ServerClient#stopServer
    */
   @POST
   @Path("/server/resetlimit/format/json")
   ListenableFuture<Void> resetServerLimit(@FormParam("serverid") String id, @FormParam("type") String type);

   /**
    * @see ServerClient#rebootServer
    */
   @POST
   @Path("/server/reboot/format/json")
   ListenableFuture<Void> rebootServer(@FormParam("serverid") String id);

   /**
    * @see ServerClient#startServer
    */
   @POST
   @Path("/server/start/format/json")
   ListenableFuture<Void> startServer(@FormParam("serverid") String id);

   /**
    * @see ServerClient#stopServer
    */
   @POST
   @Path("/server/stop/format/json")
   ListenableFuture<Void> stopServer(@FormParam("serverid") String id, ServerStopOptions... options);

   /**
    * @see ServerClient#createServer
    */
   @POST
   @Path("/server/create/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerCreated> createServer(@FormParam("datacenter") String dataCenter,
                                       @FormParam("platform") String platform,
                                       @FormParam("hostname") String hostname,
                                       @FormParam("template") String template,
                                       @FormParam("disksize") int diskSize,
                                       @FormParam("memorysize") int memorySize,
                                       @FormParam("cpucores") int cpucores,
                                       @FormParam("rootpw") String rootpw,
                                       @FormParam("transfer") int transfer,
                                       ServerCreateOptions... options);

   /**
    * @see ServerClient#cloneServer
    */
   @POST
   @Path("/server/clone/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerCreated> cloneServer(@FormParam("serverid") String serverid,
                                               @FormParam("hostname") String hostname,
                                               ServerCloneOptions... options);

   /**
    * @see ServerClient#editServer
    */
   @POST
   @Path("/server/edit/format/json")
   ListenableFuture<Void> editServer(@FormParam("serverid") String serverid, ServerEditOptions... options);

   /**
    * @see ServerClient#destroyServer
    */
   @POST
   @Path("/server/destroy/format/json")
   ListenableFuture<Void> destroyServer(@FormParam("serverid") String id, ServerDestroyOptions keepIp);

   /**
    * @see ServerClient#resetPassword
    */
   @POST
   @Path("/server/destroy/format/json")
   ListenableFuture<Void> resetPassword(@FormParam("serverid") String id, @FormParam("newpassword") String password);
   
}
