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
package org.jclouds.rackspace.cloudloadbalancers.v1.features;

import java.util.Date;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.HistoricalUsage;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancerStats;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancerUsage;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Protocol;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.DateParser;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ParseAlgorithms;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ParseLoadBalancerUsages;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ParseLoadBalancers;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;

/**
 * Reporting for load balancers.
 * <p/>
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface ReportApi {
   /**
    * List billable load balancers for the given date range.
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseLoadBalancers.class)
   @Transform(ParseLoadBalancers.ToPagedIterable.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/billable")
   PagedIterable<LoadBalancer> listBillableLoadBalancers(
         @ParamParser(DateParser.class) @QueryParam("startTime") Date startTime, 
         @ParamParser(DateParser.class) @QueryParam("endTime") Date endTime);
   
   @Named("report:list")
   @GET
   @ResponseParser(ParseLoadBalancers.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/loadbalancers/billable")
   IterableWithMarker<LoadBalancer> listBillableLoadBalancers(PaginationOptions options);

   /**
    * View all transfer activity, average number of connections, and number of virtual IPs associated with the load
    * balancing service. Historical usage data is available for up to 90 days of service activity.
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/usage")
   HistoricalUsage getHistoricalUsage(
         @ParamParser(DateParser.class) @QueryParam("startTime") Date startTime, 
         @ParamParser(DateParser.class) @QueryParam("endTime") Date endTime);
   
   /**
    * Historical usage data is available for up to 90 days of service activity.
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseLoadBalancerUsages.class)
   @Transform(ParseLoadBalancerUsages.ToPagedIterable.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}/usage")
   PagedIterable<LoadBalancerUsage> listLoadBalancerUsage(@PathParam("id") int loadBalancerId,
         @ParamParser(DateParser.class) @QueryParam("startTime") Date startTime, 
         @ParamParser(DateParser.class) @QueryParam("endTime") Date endTime);

   @Named("report:list")
   @GET
   @ResponseParser(ParseLoadBalancerUsages.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}/usage")
   IterableWithMarker<LoadBalancerUsage> listLoadBalancerUsage(PaginationOptions options);
   
   /**
    * Current usage represents all usage recorded within the preceding 24 hours.
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseLoadBalancerUsages.class)
   @Transform(ParseLoadBalancerUsages.ToPagedIterable.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}/usage/current")
   PagedIterable<LoadBalancerUsage> listCurrentLoadBalancerUsage(@PathParam("id") int loadBalancerId);

   @Named("report:list")
   @GET
   @ResponseParser(ParseLoadBalancerUsages.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}/usage/current")
   IterableWithMarker<LoadBalancerUsage> listCurrentLoadBalancerUsage(PaginationOptions options);
   
   /**
    * Current usage represents all usage recorded within the preceding 24 hours.
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}/stats")
   LoadBalancerStats getLoadBalancerStats(@PathParam("id") int loadBalancerId);
   
   /**
    * All load balancers must define the protocol of the service which is being load balanced. The protocol selection 
    * should be based on the protocol of the back-end nodes. When configuring a load balancer, the default port for 
    * the given protocol will be selected from this list unless otherwise specified.
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("protocols")
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/protocols")
   Iterable<Protocol> listProtocols();
   
   /**
    * Get all of the possible algorthims usable by load balancers.
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseAlgorithms.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/algorithms")
   Iterable<String> listAlgorithms();
}
