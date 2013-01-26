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
package org.jclouds.rackspace.cloudloadbalancers.features;

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
import org.jclouds.rackspace.cloudloadbalancers.domain.HistoricalUsage;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerStats;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerUsage;
import org.jclouds.rackspace.cloudloadbalancers.domain.Protocol;
import org.jclouds.rackspace.cloudloadbalancers.functions.DateParser;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseAlgorithms;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseLoadBalancerUsages;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseLoadBalancers;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Rackspace Cloud Load Balancers via their REST API.
 * <p/>
 * 
 * @see ReportApi
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface ReportAsyncApi {
   /**
    * @see ReportApi#listBillableLoadBalancers(Date, Date)
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseLoadBalancers.class)
   @Transform(ParseLoadBalancers.ToPagedIterable.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/billable")
   ListenableFuture<PagedIterable<LoadBalancer>> listBillableLoadBalancers(
         @ParamParser(DateParser.class) @QueryParam("startTime") Date startTime, 
         @ParamParser(DateParser.class) @QueryParam("endTime") Date endTime);

   /** 
    * @see ReportApi#listBillableLoadBalancers(PaginationOptions) 
    */
   @Named("report:list")
   @GET
   @ResponseParser(ParseLoadBalancers.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/loadbalancers/billable")
   ListenableFuture<IterableWithMarker<LoadBalancer>> listBillableLoadBalancers(PaginationOptions options);

   /**
    * @see ReportApi#getHistoricalUsage(Date, Date)
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/usage")
   ListenableFuture<HistoricalUsage> getHistoricalUsage(
         @ParamParser(DateParser.class) @QueryParam("startTime") Date startTime, 
         @ParamParser(DateParser.class) @QueryParam("endTime") Date endTime);

   /**
    * @see ReportApi#listLoadBalancerUsage(int, Date, Date)
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseLoadBalancerUsages.class)
   @Transform(ParseLoadBalancerUsages.ToPagedIterable.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}/usage")
   ListenableFuture<PagedIterable<LoadBalancerUsage>> listLoadBalancerUsage(
         @PathParam("id") int loadBalancerId,
         @ParamParser(DateParser.class) @QueryParam("startTime") Date startTime, 
         @ParamParser(DateParser.class) @QueryParam("endTime") Date endTime);

   /** 
    * @see ReportApi#listLoadBalancerUsage(PaginationOptions)
    */
   @Named("report:list")
   @GET
   @ResponseParser(ParseLoadBalancerUsages.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}/usage")
   ListenableFuture<IterableWithMarker<LoadBalancerUsage>> listLoadBalancerUsage(PaginationOptions options);

   /**
    * @see ReportApi#listCurrentLoadBalancerUsage(int)
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseLoadBalancerUsages.class)
   @Transform(ParseLoadBalancerUsages.ToPagedIterable.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}/usage/current")
   ListenableFuture<PagedIterable<LoadBalancerUsage>> listCurrentLoadBalancerUsage(
         @PathParam("id") int loadBalancerId);

   /** 
    * @see ReportApi#listCurrentLoadBalancerUsage(PaginationOptions)
    */
   @Named("report:list")
   @GET
   @ResponseParser(ParseLoadBalancerUsages.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}/usage/current")
   ListenableFuture<IterableWithMarker<LoadBalancerUsage>> listCurrentLoadBalancerUsage(PaginationOptions options);

   /**
    * @see ReportApi#getLoadBalancerStats(int)
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}/stats")
   ListenableFuture<LoadBalancerStats> getLoadBalancerStats(
         @PathParam("id") int loadBalancerId);

   /**
    * @see ReportApi#listProtocols()
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("protocols")
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/protocols")
   ListenableFuture<Iterable<Protocol>> listProtocols();

   /**
    * @see ReportApi#listAlgorithms()
    */
   @Named("report:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseAlgorithms.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/algorithms")
   ListenableFuture<Iterable<String>> listAlgorithms();
}
