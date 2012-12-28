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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerAttributes;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerRequest;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseLoadBalancers;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Rackspace Cloud Load Balancers via their REST API.
 * <p/>
 * 
 * @see LoadBalancerApi
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface LoadBalancerAsyncApi {

   /**
    * @see LoadBalancerApi#create(LoadBalancerRequest)
    */
   @POST
   @ResponseParser(ParseLoadBalancer.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers")
   ListenableFuture<LoadBalancer> create(@WrapWith("loadBalancer") LoadBalancerRequest lb);

   /**
    * @see LoadBalancerApi#update(int, LoadBalancerAttributes)
    */
   @PUT
   @ResponseParser(ParseLoadBalancer.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/loadbalancers/{id}")
   ListenableFuture<Void> update(@PathParam("id") int id, @WrapWith("loadBalancer") LoadBalancerAttributes attrs);

   /**
    * @see LoadBalancerApi#list()
    */
   @GET
   @ResponseParser(ParseLoadBalancers.class)
   @Transform(ParseLoadBalancers.ToPagedIterable.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/loadbalancers")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<PagedIterable<LoadBalancer>> list();

   /** 
    * @see LoadBalancerApi#list(PaginationOptions) 
    */
   @GET
   @ResponseParser(ParseLoadBalancers.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/loadbalancers")
   ListenableFuture<IterableWithMarker<LoadBalancer>> list(PaginationOptions options);

   /**
    * @see LoadBalancerApi#get(int)
    */
   @GET
   @ResponseParser(ParseLoadBalancer.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}")
   ListenableFuture<LoadBalancer> get(@PathParam("id") int id);

   /**
    * @see LoadBalancerApi#remove(int)
    */
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}")
   @Consumes("*/*")
   ListenableFuture<Void> remove(@PathParam("id") int id);

}
