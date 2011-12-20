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

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.glesys.domain.*;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

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
   ListenableFuture<ServerStatus> getServerStatus(@FormParam("serverid") String id);

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
   @SelectJson("server")
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
   @SelectJson("templates")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Map<String, Set<ServerTemplate>>> getTemplates();
   
   /**
    * @see ServerClient#stopServer
    */
   @POST
   @Path("/server/resetlimit/serverid/{id}/type/{type}/format/json")
   ListenableFuture<Void> resetServerLimit(@FormParam("id") String id, @FormParam("type") String type);

   /**
    * @see ServerClient#rebootServer
    */
   @POST
   @Path("/server/reboot/format/json")
   ListenableFuture<Void> rebootServer(@FormParam("id") String id);

   /**
    * @see ServerClient#startServer
    */
   @POST
   @Path("/server/start/format/json")
   ListenableFuture<Void> startServer(@FormParam("id") String id);

   /**
    * @see ServerClient#stopServer
    */
   @POST
   @Path("/server/stop/format/json")
   ListenableFuture<Void> stopServer(@FormParam("id") String id);

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
                                       String description,
                                       String ip);

   /**
    * @see ServerClient#createServer
    */
   @POST
   @Path("/server/clone/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ServerCreated> cloneServer(@FormParam("serverid") String serverid,
                                                @FormParam("hostname") String hostname,
                                                @FormParam("disksize") int diskSize,
                                                @FormParam("memorysize") int memorySize,
                                                @FormParam("cpucores") int cpucores,
                                                @FormParam("transfer") int transfer,
                                                @FormParam("description") String description,
                                                @FormParam("datacenter") String dataCenter);
   
   /**
    * @see ServerClient#destroyServer
    */
   @POST
   @Path("/server/destroy/format/json")
   ListenableFuture<Void> destroyServer(@FormParam("serverid") String id, @FormParam("keepip") int keepIp);

   /**
    * @see ServerClient#resetPassword
    */
   @POST
   @Path("/server/destroy/format/json")
   ListenableFuture<Void> resetPassword(@FormParam("serverid") String id, @FormParam("newpassword") String password);
   
}
