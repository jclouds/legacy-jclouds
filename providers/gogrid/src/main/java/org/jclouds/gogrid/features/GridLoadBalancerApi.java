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
import static org.jclouds.gogrid.reference.GoGridQueryParams.LOOKUP_LIST_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.NAME_KEY;

import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jclouds.gogrid.binders.BindIdsToQueryParams;
import org.jclouds.gogrid.binders.BindNamesToQueryParams;
import org.jclouds.gogrid.binders.BindRealIpPortPairsToQueryParams;
import org.jclouds.gogrid.binders.BindVirtualIpPortPairToQueryParams;
import org.jclouds.gogrid.domain.IpPortPair;
import org.jclouds.gogrid.domain.LoadBalancer;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.filters.SharedKeyLiteAuthentication;
import org.jclouds.gogrid.functions.ParseLoadBalancerFromJsonResponse;
import org.jclouds.gogrid.functions.ParseLoadBalancerListFromJsonResponse;
import org.jclouds.gogrid.functions.ParseOptionsFromJsonResponse;
import org.jclouds.gogrid.options.AddLoadBalancerOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

/**
 * @author Oleksiy Yarmula
 */
@RequestFilters(SharedKeyLiteAuthentication.class)
@QueryParams(keys = VERSION, values = "{jclouds.api-version}")
public interface GridLoadBalancerApi {


   /**
    * Returns all load balancers found for the current user.
    *
    * @return load balancers found
    */
   @GET
   @ResponseParser(ParseLoadBalancerListFromJsonResponse.class)
   @Path("/grid/loadbalancer/list")
   Set<LoadBalancer> getLoadBalancerList();

   /**
    * Returns the load balancer(s) by unique name(s).
    *
    * Given a name or a set of names, finds one or multiple load balancers.
    *
    * @param names
    *           to get the load balancers
    * @return load balancer(s) matching the name(s)
    */
   @GET
   @ResponseParser(ParseLoadBalancerListFromJsonResponse.class)
   @Path("/grid/loadbalancer/get")
   Set<LoadBalancer> getLoadBalancersByName(
           @BinderParam(BindNamesToQueryParams.class) String... names);

   /**
    * Returns the load balancer(s) by unique id(s).
    *
    * Given an id or a set of ids, finds one or multiple load balancers.
    *
    * @param ids
    *           to get the load balancers
    * @return load balancer(s) matching the ids
    */
   @GET
   @ResponseParser(ParseLoadBalancerListFromJsonResponse.class)
   @Path("/grid/loadbalancer/get")
   Set<LoadBalancer> getLoadBalancersById(
           @BinderParam(BindIdsToQueryParams.class) Long... ids);

   /**
    * Creates a load balancer with given properties.
    *
    * @param name
    *           name of the load balancer
    * @param virtualIp
    *           virtual IP with IP address set in {@link org.jclouds.gogrid.domain.Ip#ip} and port
    *           set in {@link IpPortPair#port}
    * @param realIps
    *           real IPs to bind the virtual IP to, with IP address set in
    *           {@link org.jclouds.gogrid.domain.Ip#ip} and port set in {@link IpPortPair#port}
    * @param options
    *           options that specify load balancer's type (round robin, least load), persistence
    *           strategy, or description.
    * @return created load balancer object
    */
   @GET
   @ResponseParser(ParseLoadBalancerFromJsonResponse.class)
   @Path("/grid/loadbalancer/add")
   LoadBalancer addLoadBalancer(@QueryParam(NAME_KEY) String name,
                                @BinderParam(BindVirtualIpPortPairToQueryParams.class) IpPortPair virtualIp,
                                @BinderParam(BindRealIpPortPairsToQueryParams.class) List<IpPortPair> realIps,
                                AddLoadBalancerOptions... options);

   /**
    * Edits the existing load balancer to change the real IP mapping.
    *
    * @param name
    *           id of the existing load balancer
    * @param realIps
    *           real IPs to bind the virtual IP to, with IP address set in
    *           {@link org.jclouds.gogrid.domain.Ip#ip} and port set in {@link IpPortPair#port}
    * @return edited object
    */
   @GET
   @ResponseParser(ParseLoadBalancerFromJsonResponse.class)
   @Path("/grid/loadbalancer/edit")
   LoadBalancer editLoadBalancerNamed(@QueryParam(NAME_KEY) String name,
                                      @BinderParam(BindRealIpPortPairsToQueryParams.class) List<IpPortPair> realIps);

   /**
    * Edits the existing load balancer to change the real IP mapping.
    *
    * @param id
    *           name of the existing load balancer
    * @param realIps
    *           real IPs to bind the virtual IP to, with IP address set in
    *           {@link org.jclouds.gogrid.domain.Ip#ip} and port set in {@link IpPortPair#port}
    * @return edited object
    */
   @GET
   @ResponseParser(ParseLoadBalancerFromJsonResponse.class)
   @Path("/grid/loadbalancer/edit")
   LoadBalancer editLoadBalancer(@QueryParam(ID_KEY) long id,
                                 @BinderParam(BindRealIpPortPairsToQueryParams.class) List<IpPortPair> realIps);

   /**
    * Deletes the load balancer by Id
    *
    * @param id
    *           id of the load balancer to delete
    * @return load balancer before the command is executed
    */
   @GET
   @ResponseParser(ParseLoadBalancerFromJsonResponse.class)
   @Path("/grid/loadbalancer/delete")
   LoadBalancer deleteById(@QueryParam(ID_KEY) Long id);

   /**
    * Deletes the load balancer by name;
    *
    * NOTE: Using this parameter may generate an error if one or more load balancers share a
    * non-unique name.
    *
    * @param name
    *           name of the load balancer to be deleted
    *
    * @return load balancer before the command is executed
    */
   @GET
   @ResponseParser(ParseLoadBalancerFromJsonResponse.class)
   @Path("/grid/loadbalancer/delete")
   LoadBalancer deleteByName(@QueryParam(NAME_KEY) String name);

   /**
    * Retrieves the list of supported Datacenters to launch servers into. The objects will have
    * datacenter ID, name and description. In most cases, id or name will be used for
    * {@link #addLoadBalancer}.
    *
    * @return supported datacenters
    */
   @GET
   @ResponseParser(ParseOptionsFromJsonResponse.class)
   @Path("/common/lookup/list")
   @QueryParams(keys = LOOKUP_LIST_KEY, values = "loadbalancer.datacenter")
   Set<Option> getDatacenters();

}
