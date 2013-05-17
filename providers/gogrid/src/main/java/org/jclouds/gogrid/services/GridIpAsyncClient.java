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
package org.jclouds.gogrid.services;

import static org.jclouds.gogrid.reference.GoGridHeaders.VERSION;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IP_STATE_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IP_TYPE_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.LOOKUP_LIST_KEY;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.filters.SharedKeyLiteAuthentication;
import org.jclouds.gogrid.functions.ParseIpListFromJsonResponse;
import org.jclouds.gogrid.functions.ParseOptionsFromJsonResponse;
import org.jclouds.gogrid.options.GetIpListOptions;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see org.jclouds.gogrid.services.GridImageClient
 * 
 * @author Oleksiy Yarmula
 */
@RequestFilters(SharedKeyLiteAuthentication.class)
@QueryParams(keys = VERSION, values = "{jclouds.api-version}")
public interface GridIpAsyncClient {

   /**
    * @see GridIpClient#getIpList(org.jclouds.gogrid.options.GetIpListOptions...)
    */
   @GET
   @ResponseParser(ParseIpListFromJsonResponse.class)
   @Path("/grid/ip/list")
   ListenableFuture<Set<Ip>> getIpList(GetIpListOptions... options);

   /**
    * @see org.jclouds.gogrid.services.GridIpClient#getUnassignedIpList()
    */
   @GET
   @ResponseParser(ParseIpListFromJsonResponse.class)
   @Path("/grid/ip/list")
   @QueryParams(keys = IP_STATE_KEY, values = "Unassigned")
   ListenableFuture<Set<Ip>> getUnassignedIpList();

   /**
    * @see org.jclouds.gogrid.services.GridIpClient#getUnassignedPublicIpList()
    */
   @GET
   @ResponseParser(ParseIpListFromJsonResponse.class)
   @Path("/grid/ip/list")
   @QueryParams(keys = { IP_STATE_KEY, IP_TYPE_KEY }, values = { "Unassigned", "Public" })
   ListenableFuture<Set<Ip>> getUnassignedPublicIpList();

   /**
    * @see org.jclouds.gogrid.services.GridIpClient#getAssignedIpList()
    */
   @GET
   @ResponseParser(ParseIpListFromJsonResponse.class)
   @Path("/grid/ip/list")
   @QueryParams(keys = IP_STATE_KEY, values = "Assigned")
   ListenableFuture<Set<Ip>> getAssignedIpList();

   /**
    * 
    * @see org.jclouds.gogrid.services.GridIpClient#getDatacenters
    */
   @GET
   @ResponseParser(ParseOptionsFromJsonResponse.class)
   @Path("/common/lookup/list")
   @QueryParams(keys = LOOKUP_LIST_KEY, values = "ip.datacenter")
   ListenableFuture<Set<Option>> getDatacenters();
}
