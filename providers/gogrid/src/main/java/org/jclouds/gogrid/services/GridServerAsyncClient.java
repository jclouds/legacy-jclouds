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
package org.jclouds.gogrid.services;

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

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
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

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to GoGrid via their REST API.
 * <p/>
 * 
 * @see GridServerClient
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API" />
 * @author Adrian Cole
 * @author Oleksiy Yarmula
 */
@RequestFilters(SharedKeyLiteAuthentication.class)
@QueryParams(keys = VERSION, values = "1.6")
public interface GridServerAsyncClient {

   /**
    * @see GridServerClient#getServerList(org.jclouds.gogrid.options.GetServerListOptions...)
    */
   @GET
   @SelectJson("list")
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Path("/grid/server/list")
   ListenableFuture<Set<Server>> getServerList(GetServerListOptions... getServerListOptions);

   /**
    * @see GridServerClient#getServersByName(String...)
    */
   @GET
   @SelectJson("list")
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Path("/grid/server/get")
   ListenableFuture<Set<Server>> getServersByName(
            @BinderParam(BindNamesToQueryParams.class) String... names);

   /**
    * @see GridServerClient#getServersById(Long...)
    */
   @GET
   @SelectJson("list")
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Path("/grid/server/get")
   ListenableFuture<Set<Server>> getServersById(
            @BinderParam(BindIdsToQueryParams.class) long... ids);

   /**
    * @see GridServerClient#getServerCredentialsList
    */
   @GET
   @ResponseParser(ParseServerNameToCredentialsMapFromJsonResponse.class)
   @Path("/support/password/list")
   ListenableFuture<Map<String, Credentials>> getServerCredentialsList();
   
   /**
    * @see GridServerClient#getServerCredentials
    */
   @GET
   @ResponseParser(ParseCredentialsFromJsonResponse.class)
   @Path("/support/grid/password/get")
   ListenableFuture<Credentials> getServerCredentials(@QueryParam("id") long id);

   /**
    * @see GridServerClient#addServer(String, String, String, String,
    *      org.jclouds.gogrid.options.AddServerOptions...)
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/add")
   ListenableFuture<Server> addServer(@QueryParam(NAME_KEY) String name,
            @QueryParam(IMAGE_KEY) String image, @QueryParam(SERVER_RAM_KEY) String ram,
            @QueryParam(IP_KEY) String ip, AddServerOptions... addServerOptions);

   /**
    * @see GridServerClient#power(String, org.jclouds.gogrid.domain.PowerCommand)
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/power")
   ListenableFuture<Server> power(
         @QueryParam(SERVER_ID_OR_NAME_KEY) String idOrName,
         @QueryParam(POWER_KEY) PowerCommand power);

   /**
    * @see GridServerClient#deleteById(Long)
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/delete")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Server> deleteById(@QueryParam(ID_KEY) long id);

   /**
    * @see GridServerClient#deleteByName(String)
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/delete")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Server> deleteByName(@QueryParam(NAME_KEY) String name);

   /**
    * @see GridServerClient#getRamSizes
    */
   @GET
   @ResponseParser(ParseOptionsFromJsonResponse.class)
   @Path("/common/lookup/list")
   @QueryParams(keys = LOOKUP_LIST_KEY, values = "server.ram")
   ListenableFuture<Set<Option>> getRamSizes();
   
   /**
    * @see GridServerClient#getTypes
    */
   @GET
   @ResponseParser(ParseOptionsFromJsonResponse.class)
   @Path("/common/lookup/list")
   @QueryParams(keys = LOOKUP_LIST_KEY, values = "server.type")
   ListenableFuture<Set<Option>> getTypes();

   /**
    * @see GridServerClient#getDatacenters
    */
   @GET
   @ResponseParser(ParseOptionsFromJsonResponse.class)
   @Path("/common/lookup/list")
   @QueryParams(keys = LOOKUP_LIST_KEY, values = "server.datacenter")
   ListenableFuture<Set<Option>> getDatacenters();
   

   /**
    * @see GridServerClient#editServerDescription
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/edit")
   ListenableFuture<Server> editServerDescription(@QueryParam("id") long id,
            @QueryParam("description") String newDescription);

   /**
    * @see GridServerClient#editServerRam
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/edit")
   ListenableFuture<Server> editServerRam(@QueryParam("id") long id,
            @QueryParam("server.ram") String ram);
   
   /**
    * @see GridServerClient#editServerType
    */
   @GET
   @SelectJson("list")
   @OnlyElement
   @Path("/grid/server/edit")
   ListenableFuture<Server> editServerType(@QueryParam("id") long id,
            @QueryParam("server.type") String newType);
}
