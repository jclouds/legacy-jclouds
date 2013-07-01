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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.CreateZoneOptions;
import org.jclouds.cloudstack.options.UpdateZoneOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides synchronous access to CloudStack Account features available to Global
 * Admin users.
 *
 * @author Adrian Cole, Andrei Savu
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface GlobalZoneApi extends ZoneApi {

   /**
    * Create a new Zone
    *
    * @param name
    *          the name of the Zone
    * @param networkType
    *          network type of the zone, can be Basic or Advanced
    * @param dns1
    *          the first DNS for the Zone
    * @param internalDns1
    *          the first internal DNS for the Zone
    * @param options
    *          optional arguments
    * @return
    *          zone instance or null
    */
   @Named("createZone")
   @GET
   @QueryParams(keys = "command", values = "createZone")
   @SelectJson("zone")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Zone createZone(@QueryParam("name") String name, @QueryParam("networktype") NetworkType networkType,
      @QueryParam("dns1") String externalDns1, @QueryParam("internaldns1") String internalDns1, CreateZoneOptions... options);

   /**
    * Update a zone
    *
    * @param id
    *          the ID of the Zone
    * @param options
    *          optional arguments
    * @return
    */
   @Named("updateZone")
   @GET
   @QueryParams(keys = "command", values = "updateZone")
   @SelectJson("zone")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Zone updateZone(@QueryParam("id") String id, UpdateZoneOptions... options);

   /**
    * Delete a zone with a specific ID
    *
    * @param zoneId
    *          the ID of the Zone
    */
   @Named("deleteZone")
   @GET
   @QueryParams(keys = "command", values = "deleteZone")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteZone(@QueryParam("id") String id);
}
