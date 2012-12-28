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

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 *
 * @see org.jclouds.cloudstack.features.GlobalVlanClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html" />
 * @author Richard Downer
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface GlobalVlanAsyncClient {

   /**
    * Get the details of an IP range by its id.
    * @param id the required IP range.
    * @return the requested IP range.
    */
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listVlanIpRanges", "true" })
   @SelectJson("vlaniprange")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<VlanIPRange> getVlanIPRange(@QueryParam("id") String id);

   /**
    * Lists all VLAN IP ranges.
    *
    * @param options optional arguments.
    * @return the list of IP ranges that match the criteria.
    */
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listVlanIpRanges", "true" })
   @SelectJson("vlaniprange")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<VlanIPRange>> listVlanIPRanges(ListVlanIPRangesOptions... options);

   /**
    * Creates a VLAN IP range.
    *
    * @param startIP the beginning IP address in the VLAN IP range
    * @param endIP the ending IP address in the VLAN IP range
    * @param options optional arguments
    * @return the newly-create IP range.
    */
   @GET
   @QueryParams(keys = "command", values = "createVlanIpRange")
   @SelectJson("vlaniprange")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<VlanIPRange> createVlanIPRange(@QueryParam("startip") String startIP, @QueryParam("endip") String endIP, CreateVlanIPRangeOptions... options);

   /**
    * Deletes a VLAN IP range.
    * @param rangeId the id of the VLAN IP range
    * @return void
    */
   @GET
   @QueryParams(keys = "command", values = "deleteVlanIpRange")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteVlanIPRange(@QueryParam("id") String rangeId);
}
