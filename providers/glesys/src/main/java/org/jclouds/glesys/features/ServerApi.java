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

import org.jclouds.Fallbacks;
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

/**
 * Provides synchronous access to Server.
 * <p/>
 *
 * @author Adrian Cole
 * @author Adam Lowe
 * @see <a href="https://github.com/GleSYS/API/wiki/API-Documentation" />
 */
@RequestFilters(BasicAuthentication.class)
public interface ServerApi {

   /**
    * Get a list of all servers on this account.
    *
    * @return an account's associated server objects.
    */
   @Named("server:list")
   @POST
   @Path("/server/list/format/json")
   @SelectJson("servers")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Server> list();

   /**
    * Get detailed information about a server such as hostname, hardware
    * configuration (cpu, memory and disk), ip addresses, cost, transfer, os and
    * more.
    *
    * @param id id of the server
    * @return server or null if not found
    */
   @Named("server:details")
   @POST
   @Path("/server/details/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   @FormParams(keys = "includestate", values = "true")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ServerDetails get(@FormParam("serverid") String id);

   /**
    * Get detailed information about a server status including up-time and
    * hardware usage (cpu, disk, memory and bandwidth)
    *
    * @param id      id of the server
    * @param options optional parameters
    * @return the status of the server or null if not found
    */
   @Named("server:status")
   @POST
   @Path("/server/status/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ServerStatus getStatus(@FormParam("serverid") String id, ServerStatusOptions... options);

   /**
    * Get detailed information about a server's limits (for OpenVZ only).
    * <p/>
    *
    * @param id id of the server
    * @return the requested information about the server or null if not found
    */
   @Named("server:limits")
   @POST
   @Path("/server/limits/format/json")
   @SelectJson("limits")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   SortedMap<String, ServerLimit> getLimits(@FormParam("serverid") String id);

   /**
    * Get information about how to connect to a server via VNC
    *
    * @param id id of the server
    * @return the requested information about the server or null if not found
    */
   @Named("server:console")
   @POST
   @Path("/server/console/format/json")
   @SelectJson("console")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Console getConsole(@FormParam("serverid") String id);

   /**
    * Get information about the OS templates available
    *
    * @return the set of information about each template
    */
   @Named("server:allowedarguments")
   @GET
   @Path("/server/allowedarguments/format/json")
   @SelectJson("argumentslist")
   @Consumes(MediaType.APPLICATION_JSON)
   Map<String, AllowedArgumentsForCreateServer> getAllowedArgumentsForCreateByPlatform();

   /**
    * Get information about valid arguments to #createServer for each platform
    *
    * @return a map of argument lists, keyed on platform
    */
   @Named("server:templates")
   @GET
   @Path("/server/templates/format/json")
   @ResponseParser(ParseTemplatesFromHttpResponse.class)
   @Fallback(Fallbacks.EmptyFluentIterableOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_JSON)
   FluentIterable<OSTemplate> listTemplates();

   /**
    * Reset the fail count for a server limit (for OpenVZ only).
    *
    * @param id   id of the server
    * @param type the type of limit to reset
    */
   @Named("server:resetlimit")
   @POST
   @Path("/server/resetlimit/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   SortedMap<String, ServerLimit> resetLimit(@FormParam("serverid") String id,
                                             @FormParam("type") String type);

   /**
    * Reboot a server
    *
    * @param id id of the server
    */
   @Named("server:reboot")
   @POST
   @SelectJson("server")
   @Path("/server/reboot/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ServerDetails reboot(@FormParam("serverid") String id);

   /**
    * Start a server
    *
    * @param id id of the server
    */
   @Named("server:start")
   @POST
   @SelectJson("server")
   @Path("/server/start/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ServerDetails start(@FormParam("serverid") String id);

   /**
    * Stop a server
    *
    * @param id id of the server
    */
   @Named("server:stop")
   @POST
   @SelectJson("server")
   @Path("/server/stop/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ServerDetails stop(@FormParam("serverid") String id);

   /**
    * hard stop a server
    *
    * @param id id of the server
    */
   @Named("server:stop:hard")
   @POST
   @SelectJson("server")
   @Path("/server/stop/format/json")
   @FormParams(keys = "type", values = "hard")
   @Consumes(MediaType.APPLICATION_JSON)
   ServerDetails hardStop(@FormParam("serverid") String id);

   /**
    * Create a new server
    *
    * @param hostname     the host name of the new server
    * @param rootPassword the root password to use
    * @param options      optional settings ex. description
    */
   @Named("server:create")
   @POST
   @SelectJson("server")
   @Path("/server/create/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @MapBinder(CreateServerOptions.class)
   ServerDetails createWithHostnameAndRootPassword(ServerSpec serverSpec,
                                                   @PayloadParam("hostname") String hostname, @PayloadParam("rootpassword") String rootPassword,
                                                   CreateServerOptions... options);

   /**
    * Clone a server
    *
    * @param serverid the serverId of the server to clone
    * @param hostname the new host name of the cloned server
    * @param options  the settings to change
    */
   @Named("server:clone")
   @POST
   @Path("/server/clone/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   ServerDetails clone(@FormParam("serverid") String serverid,
                       @FormParam("hostname") String hostname, CloneServerOptions... options);

   /**
    * Update the configuration of a server
    *
    * @param serverid the serverId of the server to edit
    * @param options  the settings to change
    */
   @Named("server:edit")
   @POST
   @Path("/server/edit/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   ServerDetails update(@FormParam("serverid") String serverid, UpdateServerOptions options);

   /**
    * Destroy a server
    *
    * @param id     the id of the server
    * @param keepIp if DestroyServerOptions.keepIp(true) the servers ip will be retained for use in your GleSYS account
    */
   @Named("server:destroy")
   @POST
   @Path("/server/destroy/format/json")
   void destroy(@FormParam("serverid") String id, DestroyServerOptions keepIp);

   /**
    * Reset the root password of a server
    *
    * @param id       the id of the server
    * @param password the new password to use
    */
   @Named("server:resetpassword")
   @POST
   @Path("/server/resetpassword/format/json")
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   ServerDetails resetPassword(@FormParam("serverid") String id, @FormParam("rootpassword") String password);

   /**
    * Return resource usage over time for server
    *
    * @param id       the id of the server
    * @param resource the name of the resource to retrieve usage information for (e.g. "cpuusage")
    * @param resolution the time-period to extract data for (one of "minute", "hour" or "day)
    */
   @Named("server:resourceusage")
   @POST
   @Path("/server/resourceusage/format/json")
   @SelectJson("usage")
   @Consumes(MediaType.APPLICATION_JSON)
   ResourceUsage getResourceUsage(@FormParam("serverid") String id, @FormParam("resource") String resource,
                                  @FormParam("resolution") String resolution);

}
