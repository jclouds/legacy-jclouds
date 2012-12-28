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
package org.jclouds.cloudstack.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.cloudstack.domain.Event;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.functions.ParseEventTypesFromHttpResponse;
import org.jclouds.cloudstack.options.ListEventsOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 *
 * @author Vijay Kiran
 * @see org.jclouds.cloudstack.features.AccountClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = { "response", "listAll" }, values = { "json", "true" })
public interface EventAsyncClient {
   /**
    * @see EventClient#listEventTypes()
    */
   @GET
   @QueryParams(keys = "command", values = "listEventTypes")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseEventTypesFromHttpResponse.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<String>> listEventTypes();

   /**
    * @see EventClient#listEventTypes()
    */
   @GET
   @QueryParams(keys = "command", values = "listEvents")
   @SelectJson("event")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Event>> listEvents(ListEventsOptions...options);

}
