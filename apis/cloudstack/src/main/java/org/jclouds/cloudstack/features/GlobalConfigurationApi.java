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
package org.jclouds.cloudstack.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.domain.ConfigurationEntry;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.ListConfigurationEntriesOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides synchronous access to CloudStack Configuration features available to Global
 * Admin users.
 *
 * @author Andrei Savu
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface GlobalConfigurationApi extends ConfigurationApi {

   /**
    * List all configuration entries
    *
    * @param options
    *          result set filtering options
    * @return
    *          a set of entries or empty
    */
   @Named("listConfigurations")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listConfigurations", "true" })
   @SelectJson("configuration")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<ConfigurationEntry> listConfigurationEntries(ListConfigurationEntriesOptions... options);

   /**
    * Update a configuration entry
    *
    * @param name
    *          the name of the configuration
    * @param value
    *          the value of the configuration
    * @return
    *          the updated configuration value
    */
   @Named("updateConfiguration")
   @GET
   @QueryParams(keys = "command", values = "updateConfiguration")
   @SelectJson("configuration")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ConfigurationEntry updateConfigurationEntry(
      @QueryParam("name") String name, @QueryParam("value") String value);
}
