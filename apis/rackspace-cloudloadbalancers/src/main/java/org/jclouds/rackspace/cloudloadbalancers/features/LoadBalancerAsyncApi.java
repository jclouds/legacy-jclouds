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

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyMapOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rackspace.cloudloadbalancers.binders.BindMetadataToJsonPayload;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerAttributes;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerRequest;
import org.jclouds.rackspace.cloudloadbalancers.domain.Metadata;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseLoadBalancers;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseMetadata;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
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
   @Named("lb:create")
   @POST
   @ResponseParser(ParseLoadBalancer.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers")
   ListenableFuture<LoadBalancer> create(@WrapWith("loadBalancer") LoadBalancerRequest lb);

   /**
    * @see LoadBalancerApi#update(int, LoadBalancerAttributes)
    */
   @Named("lb:update")
   @PUT
   @ResponseParser(ParseLoadBalancer.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/loadbalancers/{id}")
   ListenableFuture<Void> update(@PathParam("id") int id, @WrapWith("loadBalancer") LoadBalancerAttributes attrs);

   /**
    * @see LoadBalancerApi#list()
    */
   @Named("lb:list")
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
   @Named("lb:list")
   @GET
   @ResponseParser(ParseLoadBalancers.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/loadbalancers")
   ListenableFuture<IterableWithMarker<LoadBalancer>> list(PaginationOptions options);

   /**
    * @see LoadBalancerApi#get(int)
    */
   @Named("lb:get")
   @GET
   @ResponseParser(ParseLoadBalancer.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}")
   ListenableFuture<LoadBalancer> get(@PathParam("id") int id);

   /**
    * @see LoadBalancerApi#delete(int)
    */
   @Named("lb:delete")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}")
   @Consumes("*/*")
   ListenableFuture<Void> delete(@PathParam("id") int id);

   /**
    * @see LoadBalancerApi#createMetadata(int, Iterable)
    */
   @Named("lb:createmetadata")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseMetadata.class)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}/metadata")
   ListenableFuture<Metadata> createMetadata(
         @PathParam("id") int id, 
         @BinderParam(BindMetadataToJsonPayload.class) Map<String, String> metadata);

   /**
    * @see LoadBalancerApi#getMetadata(int)
    */
   @Named("lb:getmetadata")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseMetadata.class)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}/metadata")
   ListenableFuture<Metadata> getMetadata(@PathParam("id") int lb);

   /**
    * @see LoadBalancerApi#updateMetadatum(int, int, String)
    */
   @Named("lb:updatemetadatum")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes("*/*")
   @Fallback(FalseOnNotFoundOr404.class)
   @Payload("%7B\"meta\":%7B\"value\":\"{value}\"%7D%7D")
   @Path("/loadbalancers/{id}/metadata/{metadatumId}")
   ListenableFuture<Boolean> updateMetadatum(@PathParam("id") int id, 
                                             @PathParam("metadatumId") int metadatumId, 
                                             @PayloadParam("value") String value);

   /**
    * @see LoadBalancerApi#deleteMetadatum(int, int)
    */
   @Named("lb:deletemetadatum")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Consumes("*/*")
   @Path("/loadbalancers/{id}/metadata/{metadatumId}")
   ListenableFuture<Boolean> deleteMetadatum(@PathParam("id") int id, @PathParam("metadatumId") int metadatumId);

   /**
    * @see LoadBalancerApi#deleteMetadata(int, Iterable)
    */
   @Named("lb:deletemetadata")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Consumes("*/*")
   @Path("/loadbalancers/{id}/metadata")
   ListenableFuture<Boolean> deleteMetadata(@PathParam("id") int id, 
                                            @QueryParam("id") Iterable<Integer> metadataIds);
}
