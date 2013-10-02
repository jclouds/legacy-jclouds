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
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudstack.domain.VlanIPRange;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.CreateVlanIPRangeOptions;
import org.jclouds.cloudstack.options.ListVlanIPRangesOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides synchronous access to cloudstack via their REST API.
 * <p/>
 *
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html" />
 * @author Richard Downer
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface GlobalVlanApi {

   /**
    * Get the details of an IP range by its id.
    * @param id the required IP range.
    * @return the requested IP range.
    */
   @Named("listVlanIpRanges")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listVlanIpRanges", "true" })
   @SelectJson("vlaniprange")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   VlanIPRange getVlanIPRange(@QueryParam("id") String id);

   /**
    * Lists all VLAN IP ranges.
    *
    * @param options optional arguments.
    * @return the list of IP ranges that match the criteria.
    */
   @Named("listVlanIpRanges")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listVlanIpRanges", "true" })
   @SelectJson("vlaniprange")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<VlanIPRange> listVlanIPRanges(ListVlanIPRangesOptions... options);

   /**
    * Creates a VLAN IP range.
    *
    * @param startIP the beginning IP address in the VLAN IP range
    * @param endIP the ending IP address in the VLAN IP range
    * @param options optional arguments
    * @return the newly-create IP range.
    */
   @Named("createVlanIpRange")
   @GET
   @QueryParams(keys = "command", values = "createVlanIpRange")
   @SelectJson("vlaniprange")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   VlanIPRange createVlanIPRange(@QueryParam("startip") String startIP, @QueryParam("endip") String endIP, CreateVlanIPRangeOptions... options);

   /**
    * Deletes a VLAN IP range.
    * @param rangeId the id of the VLAN IP range
    * @return void
    */
   @Named("deleteVlanIpRange")
   @GET
   @QueryParams(keys = "command", values = "deleteVlanIpRange")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteVlanIPRange(@QueryParam("id") String rangeId);
}
