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
import java.util.Set;

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
import org.jclouds.rackspace.cloudloadbalancers.domain.Metadata;
import org.jclouds.rackspace.cloudloadbalancers.domain.Node;
import org.jclouds.rackspace.cloudloadbalancers.domain.NodeAttributes;
import org.jclouds.rackspace.cloudloadbalancers.domain.NodeRequest;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseMetadata;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseNode;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseNodes;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Cloud Load Balancers Node features.
 * <p/>
 * 
 * @see NodeAsyncApi
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface NodeAsyncApi {

   /**
    * @see NodeApi#add(Set)
    */
   @Named("node:add")
   @POST
   @SelectJson("nodes")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/nodes")
   ListenableFuture<Set<Node>> add(@WrapWith("nodes") Iterable<NodeRequest> nodes);

   /**
    * @see NodeApi#update(int, NodeAttributes)
    */
   @Named("node:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/nodes/{id}")
   ListenableFuture<Void> update(@PathParam("id") int id, @WrapWith("node") NodeAttributes attrs);
   
   /**
    * @see NodeApi#list()
    */
   @Named("node:list")
   @GET
   @ResponseParser(ParseNodes.class)
   @Transform(ParseNodes.ToPagedIterable.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/nodes")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<PagedIterable<Node>> list();

   /** 
    * @see NodeApi#list(PaginationOptions) 
    */
   @Named("node:list")
   @GET
   @ResponseParser(ParseNodes.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/nodes")
   ListenableFuture<IterableWithMarker<LoadBalancer>> list(PaginationOptions options);
   
   /**
    * @see NodeApi#get(int)
    */
   @Named("node:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseNode.class)
   @Path("/nodes/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Node> get(@PathParam("id") int id);
   
   /**
    * @see NodeApi#remove(int)
    */
   @Named("node:remove")
   @DELETE
   @Path("/nodes/{id}")
   @Fallback(VoidOnNotFoundOr404.class)
   @Consumes("*/*")
   ListenableFuture<Void> remove(@PathParam("id") int id);
   
   /**
    * @see NodeApi#remove(Set)
    */
   @Named("node:remove")
   @DELETE
   @Path("/nodes")
   @Fallback(VoidOnNotFoundOr404.class)
   @Consumes("*/*")
   ListenableFuture<Void> remove(@QueryParam("id") Iterable<Integer> ids);

   /**
    * @see NodeApi#createMetadata(int, Iterable)
    */
   @Named("node:createmetadata")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseMetadata.class)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   @Path("/nodes/{id}/metadata")
   ListenableFuture<Metadata> createMetadata(
         @PathParam("id") int id, 
         @BinderParam(BindMetadataToJsonPayload.class) Map<String, String> metadata);

   /**
    * @see NodeApi#getMetadata(int)
    */
   @Named("node:getmetadata")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseMetadata.class)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   @Path("/nodes/{id}/metadata")
   ListenableFuture<Metadata> getMetadata(@PathParam("id") int lb);

   /**
    * @see NodeApi#updateMetadatum(int, int, String)
    */
   @Named("node:updatemetadatum")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes("*/*")
   @Fallback(FalseOnNotFoundOr404.class)
   @Payload("%7B\"meta\":%7B\"value\":\"{value}\"%7D%7D")
   @Path("/nodes/{id}/metadata/{metadatumId}")
   ListenableFuture<Boolean> updateMetadatum(@PathParam("id") int id, 
                                             @PathParam("metadatumId") int metadatumId, 
                                             @PayloadParam("value") String value);

   /**
    * @see NodeApi#deleteMetadatum(int, int)
    */
   @Named("node:deletemetadatum")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Consumes("*/*")
   @Path("/nodes/{id}/metadata/{metadatumId}")
   ListenableFuture<Boolean> deleteMetadatum(@PathParam("id") int id, @PathParam("metadatumId") int metadatumId);

   /**
    * @see NodeApi#deleteMetadata(int, Iterable)
    */
   @Named("node:deletemetadata")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Consumes("*/*")
   @Path("/nodes/{id}/metadata")
   ListenableFuture<Boolean> deleteMetadata(@PathParam("id") int id, 
                                            @QueryParam("id") Iterable<Integer> metadataIds);
}
