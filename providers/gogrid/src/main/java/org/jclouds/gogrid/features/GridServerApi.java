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
package org.jclouds.gogrid.features;

import static org.jclouds.gogrid.reference.GoGridHeaders.VERSION;
import static org.jclouds.gogrid.reference.GoGridQueryParams.ID_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IMAGE_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IP_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.LOOKUP_LIST_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.NAME_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.POWER_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.SERVER_ID_OR_NAME_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.SERVER_RAM_KEY;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks;
import org.jclouds.domain.Credentials;
import org.jclouds.gogrid.binders.BindIdsToQueryParams;
import org.jclouds.gogrid.binders.BindNamesToQueryParams;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.filters.SharedKeyLiteAuthentication;
import org.jclouds.gogrid.functions.ParseCredentialsFromJsonResponse;
import org.jclouds.gogrid.functions.ParseOptionsFromJsonResponse;
import org.jclouds.gogrid.functions.ParseServerNameToCredentialsMapFromJsonResponse;
import org.jclouds.gogrid.options.AddServerOptions;
import org.jclouds.gogrid.options.GetServerListOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides synchronous access to GoGrid.
 * <p/>
 * 
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API" />
 * 
 * @author Adrian Cole
 * @author Oleksiy Yarmula
 */
@RequestFilters(SharedKeyLiteAuthentication.class)
@QueryParams(keys = VERSION, values = "1.6")
public interface GridServerApi {

   /**
    * Returns the list of all servers.
    *
    * The result can be narrowed down by providing the options.
    *
    * @param getServerListOptions
    *           options to narrow down the result
    * @return servers found by the request
    */
   @GET
   @SelectJson("list")
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   @Path("/grid/server/list")
   Set<Server> getServerList(GetServerListOptions... getServerListOptions);

   /**
    * Returns the server(s) by unique name(s).
    *
    * Given a name or a set of names, finds one or multiple servers.
    *
    * @param names
    *           to get the servers
    * @return server(s) matching the name(s)
    */
   @GET
   @SelectJson("list")
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   @Path("/grid/server/get")
   Set<Server> getServersByName(
           @BinderParam(BindNamesToQueryParams.class) String... names);

   /**
    * Returns the server(s) by unique id(s).
    *
    * Given an id or a set of ids, finds one or multiple servers.
    *
    * @param ids
    *           to get the servers
    * @return server(s) matching the ids
    */
   @GET
   @SelectJson("list")
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   @Path("/grid/server/get")
   Set<Server> getServersById(
           @BinderParam(BindIdsToQueryParams.class) long... ids);

   /**
    * Returns a map of running servers' names to the log in credentials.
    *
    * @return map <String server name => Credentials>
    */
   @GET
   @ResponseParser(ParseServerNameToCredentialsMapFromJsonResponse.class)
   @Path("/support/password/list")
   Map<String, Credentials> getServerCredentialsList();

   /**
    *
    * @return the login user and password of a server, or null if none found
    */
   @GET
   @ResponseParser(ParseCredentialsFromJsonResponse.class)
   @Path("/support/grid/password/get")
   Credentials getServerCredentials(@QueryParam("id") long id);

   /**
    * Adds a server with specified attributes
    *
    * @param name
    *           name of the server
    * @param image
    *           image (id or name)
    * @param ram
    *           ram type (id or name)
    * @param ip
    *           ip address
    * @param addServerOptions
    *           options to make it a sandbox instance or/and description
    * @return created server
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/add")
   Server addServer(@QueryParam(NAME_KEY) String name,
                    @QueryParam(IMAGE_KEY) String image, @QueryParam(SERVER_RAM_KEY) String ram,
                    @QueryParam(IP_KEY) String ip, AddServerOptions... addServerOptions);

   /**
    * Changes the server's state according to {@link PowerCommand}
    *
    * @param idOrName
    *           id or name of the server to apply the command
    * @param power
    *           new desired state
    * @return server immediately after applying the command
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/power")
   Server power(
           @QueryParam(SERVER_ID_OR_NAME_KEY) String idOrName,
           @QueryParam(POWER_KEY) PowerCommand power);

   /**
    * Deletes the server by Id
    *
    * @param id
    *           id of the server to delete
    * @return server before the command is executed
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/delete")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Server deleteById(@QueryParam(ID_KEY) long id);

   /**
    * Deletes the server by name;
    *
    * NOTE: Using this parameter may generate an error if one or more servers
    * share a non-unique name.
    *
    * @param name
    *           name of the server to be deleted
    *
    * @return server before the command is executed
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/delete")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Server deleteByName(@QueryParam(NAME_KEY) String name);

   /**
    * Retrieves the list of supported RAM configurations. The objects will have
    * RAM ID, name and description. In most cases, id or name will be used for
    * {@link #addServer}.
    *
    * To see how RAM maps to CPU and disk space (as of March 2010), see
    * {@link org.jclouds.gogrid.compute.config.GoGridComputeServiceContextModule#provideSizeToRam}
    * .
    *
    * @return supported ram sizes
    */
   @GET
   @ResponseParser(ParseOptionsFromJsonResponse.class)
   @Path("/common/lookup/list")
   @QueryParams(keys = LOOKUP_LIST_KEY, values = "server.ram")
   Set<Option> getRamSizes();

   /**
    * Retrieves the list of supported server types, for example Web/App Server and Database Server. In most cases, id
    * or name will be used for {@link #editServerType}.
    *
    * @return supported server types
    */
   @GET
   @ResponseParser(ParseOptionsFromJsonResponse.class)
   @Path("/common/lookup/list")
   @QueryParams(keys = LOOKUP_LIST_KEY, values = "server.type")
   Set<Option> getTypes();

   /**
    * Retrieves the list of supported Datacenters to launch servers into. The
    * objects will have datacenter ID, name and description. In most cases, id
    * or name will be used for {@link #addServer}.
    *
    * @return supported datacenters
    */
   @GET
   @ResponseParser(ParseOptionsFromJsonResponse.class)
   @Path("/common/lookup/list")
   @QueryParams(keys = LOOKUP_LIST_KEY, values = "server.datacenter")
   Set<Option> getDatacenters();

   /**
    * Edits an existing server
    *
    * @param id
    *           id of the existing server
    * @param newDescription
    *           description to replace the current one
    * @return edited server
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/edit")
   Server editServerDescription(@QueryParam("id") long id,
                                @QueryParam("description") String newDescription);

   /**
    * Edits an existing server
    *
    * @param id
    *           id of the existing server
    * @param ram
    *           ram to replace the current one
    * @return edited server
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/edit")
   Server editServerRam(@QueryParam("id") long id,
                        @QueryParam("server.ram") String ram);

   /**
    * Edits an existing server
    *
    * @param id
    *           id of the existing server
    * @param newType
    *           type to replace the current one
    * @return edited server
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/edit")
   Server editServerType(@QueryParam("id") long id,
                         @QueryParam("server.type") String newType);}
