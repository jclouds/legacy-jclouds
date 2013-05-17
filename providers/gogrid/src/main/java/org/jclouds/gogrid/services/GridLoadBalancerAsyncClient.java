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

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Oleksiy Yarmula
 */
@RequestFilters(SharedKeyLiteAuthentication.class)
@QueryParams(keys = VERSION, values = "{jclouds.api-version}")
public interface GridLoadBalancerAsyncClient {

   /**
    * @see GridJobClient#getJobList(org.jclouds.gogrid.options.GetJobListOptions...)
    */
   @GET
   @ResponseParser(ParseLoadBalancerListFromJsonResponse.class)
   @Path("/grid/loadbalancer/list")
   ListenableFuture<Set<LoadBalancer>> getLoadBalancerList();

   /**
    * @see GridLoadBalancerClient#getLoadBalancersByName
    */
   @GET
   @ResponseParser(ParseLoadBalancerListFromJsonResponse.class)
   @Path("/grid/loadbalancer/get")
   ListenableFuture<Set<LoadBalancer>> getLoadBalancersByName(
            @BinderParam(BindNamesToQueryParams.class) String... names);

   /**
    * @see GridLoadBalancerClient#getLoadBalancersById
    */
   @GET
   @ResponseParser(ParseLoadBalancerListFromJsonResponse.class)
   @Path("/grid/loadbalancer/get")
   ListenableFuture<Set<LoadBalancer>> getLoadBalancersById(
            @BinderParam(BindIdsToQueryParams.class) Long... ids);

   /**
    * @see GridLoadBalancerClient#addLoadBalancer
    */
   @GET
   @ResponseParser(ParseLoadBalancerFromJsonResponse.class)
   @Path("/grid/loadbalancer/add")
   ListenableFuture<LoadBalancer> addLoadBalancer(@QueryParam(NAME_KEY) String name,
            @BinderParam(BindVirtualIpPortPairToQueryParams.class) IpPortPair virtualIp,
            @BinderParam(BindRealIpPortPairsToQueryParams.class) List<IpPortPair> realIps,
            AddLoadBalancerOptions... options);

   /**
    * @see GridLoadBalancerClient#editLoadBalancerNamed
    */
   @GET
   @ResponseParser(ParseLoadBalancerFromJsonResponse.class)
   @Path("/grid/loadbalancer/edit")
   ListenableFuture<LoadBalancer> editLoadBalancerNamed(@QueryParam(NAME_KEY) String name,
            @BinderParam(BindRealIpPortPairsToQueryParams.class) List<IpPortPair> realIps);

   /**
    * @see GridLoadBalancerClient#editLoadBalancer
    */
   @GET
   @ResponseParser(ParseLoadBalancerFromJsonResponse.class)
   @Path("/grid/loadbalancer/edit")
   ListenableFuture<LoadBalancer> editLoadBalancer(@QueryParam(ID_KEY) long id,
            @BinderParam(BindRealIpPortPairsToQueryParams.class) List<IpPortPair> realIps);

   /**
    * @see GridLoadBalancerClient#
    */
   @GET
   @ResponseParser(ParseLoadBalancerFromJsonResponse.class)
   @Path("/grid/loadbalancer/delete")
   ListenableFuture<LoadBalancer> deleteById(@QueryParam(ID_KEY) Long id);

   /**
    * @see GridLoadBalancerClient#
    */
   @GET
   @ResponseParser(ParseLoadBalancerFromJsonResponse.class)
   @Path("/grid/loadbalancer/delete")
   ListenableFuture<LoadBalancer> deleteByName(@QueryParam(NAME_KEY) String name);
   
   /**
    * @see GridLoadBalancerClient#getDatacenters
    */
   @GET
   @ResponseParser(ParseOptionsFromJsonResponse.class)
   @Path("/common/lookup/list")
   @QueryParams(keys = LOOKUP_LIST_KEY, values = "loadbalancer.datacenter")
   ListenableFuture<Set<Option>> getDatacenters();
}
