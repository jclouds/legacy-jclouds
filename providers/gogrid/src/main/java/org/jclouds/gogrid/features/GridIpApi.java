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

/**
 * @author Oleksiy Yarmula
 */
@RequestFilters(SharedKeyLiteAuthentication.class)
@QueryParams(keys = VERSION, values = "{jclouds.api-version}")
public interface GridIpApi {

   /**
    * Returns all IPs in the system that match the options
    *
    * @param options
    *           options to narrow the search down
    * @return IPs found by the search
    */
   @GET
   @ResponseParser(ParseIpListFromJsonResponse.class)
   @Path("/grid/ip/list")
   Set<Ip> getIpList(GetIpListOptions... options);

   /**
    * Returns the list of unassigned IPs.
    *
    * NOTE: this returns both public and private IPs!
    *
    * @return unassigned IPs
    */
   @GET
   @ResponseParser(ParseIpListFromJsonResponse.class)
   @Path("/grid/ip/list")
   @QueryParams(keys = IP_STATE_KEY, values = "Unassigned")
   Set<Ip> getUnassignedIpList();

   /**
    * Returns the list of unassigned public IPs.
    *
    * @return unassigned public IPs
    */
   @GET
   @ResponseParser(ParseIpListFromJsonResponse.class)
   @Path("/grid/ip/list")
   @QueryParams(keys = { IP_STATE_KEY, IP_TYPE_KEY }, values = { "Unassigned", "Public" })
   Set<Ip> getUnassignedPublicIpList();

   /**
    * Returns the list of assigned IPs
    *
    * NOTE: this returns both public and private IPs!
    *
    * @return assigned IPs
    */
   @GET
   @ResponseParser(ParseIpListFromJsonResponse.class)
   @Path("/grid/ip/list")
   @QueryParams(keys = IP_STATE_KEY, values = "Assigned")
   Set<Ip> getAssignedIpList();

   /**
    * Retrieves the list of supported Datacenters to retrieve ips from. The objects will have
    * datacenter ID, name and description. In most cases, id or name will be used for
    * {@link #addServer}.
    *
    * @return supported datacenters
    */
   @GET
   @ResponseParser(ParseOptionsFromJsonResponse.class)
   @Path("/common/lookup/list")
   @QueryParams(keys = LOOKUP_LIST_KEY, values = "ip.datacenter")
   Set<Option> getDatacenters();
}
