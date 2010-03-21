/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.gogrid.services;

import com.google.common.util.concurrent.ListenableFuture;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jclouds.domain.Credentials;
import org.jclouds.gogrid.GoGrid;
import org.jclouds.gogrid.binders.BindIdsToQueryParams;
import org.jclouds.gogrid.binders.BindNamesToQueryParams;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.filters.SharedKeyLiteAuthentication;
import org.jclouds.gogrid.functions.ParseOptionsFromJsonResponse;
import org.jclouds.gogrid.functions.ParseServerFromJsonResponse;
import org.jclouds.gogrid.functions.ParseServerListFromJsonResponse;
import org.jclouds.gogrid.functions.ParseServerNameToCredentialsMapFromJsonResponse;
import org.jclouds.gogrid.options.AddServerOptions;
import org.jclouds.gogrid.options.GetServerListOptions;
import org.jclouds.rest.annotations.*;

import java.util.Map;
import java.util.Set;

import static org.jclouds.gogrid.reference.GoGridQueryParams.*;
import static org.jclouds.gogrid.reference.GoGridHeaders.VERSION;

/**
 * Provides asynchronous access to GoGrid via their REST API.
 * <p/>
 *
 * @see GridServerClient
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API" />
 * @author Adrian Cole
 * @author Oleksiy Yarmula
 */
@Endpoint(GoGrid.class)
@RequestFilters(SharedKeyLiteAuthentication.class)
@QueryParams(keys = VERSION, values = "1.3")
public interface GridServerAsyncClient {

    /**
    * @see GridServerClient#getServerList(org.jclouds.gogrid.options.GetServerListOptions...)
    */
    @GET
    @ResponseParser(ParseServerListFromJsonResponse.class)
    @Path("/grid/server/list")
    ListenableFuture<Set<Server>> getServerList(GetServerListOptions... getServerListOptions);

    /**
    * @see GridServerClient#getServersByName(String...)
    */
    @GET
    @ResponseParser(ParseServerListFromJsonResponse.class)
    @Path("/grid/server/get")
    ListenableFuture<Set<Server>> getServersByName(@BinderParam(BindNamesToQueryParams.class) String... names);

    /**
    * @see GridServerClient#getServersById(Long...)
    */
    @GET
    @ResponseParser(ParseServerListFromJsonResponse.class)
    @Path("/grid/server/get")
    ListenableFuture<Set<Server>> getServersById(@BinderParam(BindIdsToQueryParams.class) Long... ids);

    /**
    * @see GridServerClient#getServerCredentialsList
    */
    @GET
    @ResponseParser(ParseServerNameToCredentialsMapFromJsonResponse.class)
    @Path("/support/password/list")
    ListenableFuture<Map<String, Credentials>> getServerCredentialsList();

    /**
    * @see GridServerClient#addServer(String, String, String, String, org.jclouds.gogrid.options.AddServerOptions...)
    */
    @GET
    @ResponseParser(ParseServerFromJsonResponse.class)
    @Path("/grid/server/add")
    ListenableFuture<Server> addServer(@QueryParam(NAME_KEY) String name,
                                            @QueryParam(IMAGE_KEY) String image,
                                            @QueryParam(SERVER_RAM_KEY) String ram,
                                            @QueryParam(IP_KEY) String ip,
                                            AddServerOptions... addServerOptions);

    /**
    * @see GridServerClient#power(String, org.jclouds.gogrid.domain.PowerCommand)
    */
    @GET
    @ResponseParser(ParseServerFromJsonResponse.class)
    @Path("/grid/server/power")
    ListenableFuture<Server> power(@QueryParam(SERVER_ID_OR_NAME_KEY) String idOrName,
                                   @QueryParam(POWER_KEY) PowerCommand power);

    /**
    * @see GridServerClient#deleteById(Long)
    */
    @GET
    @ResponseParser(ParseServerFromJsonResponse.class)
    @Path("/grid/server/delete")
    ListenableFuture<Server> deleteById(@QueryParam(ID_KEY) Long id);

    /**
    * @see GridServerClient#deleteByName(String)     
    */
    @GET
    @ResponseParser(ParseServerFromJsonResponse.class)
    @Path("/grid/server/delete")
    ListenableFuture<Server> deleteByName(@QueryParam(NAME_KEY) String name);

    /**
     * @see GridServerClient#getRamSizes
     */
    @GET
    @ResponseParser(ParseOptionsFromJsonResponse.class)
    @Path("/common/lookup/list")
    @QueryParams(keys = LOOKUP_LIST_KEY, values = "server.ram")
    ListenableFuture<Set<Option>> getRamSizes();
}
