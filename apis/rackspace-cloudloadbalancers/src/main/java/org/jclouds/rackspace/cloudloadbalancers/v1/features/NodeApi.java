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
import org.jclouds.rackspace.cloudloadbalancers.v1.binders.BindMetadataToJsonPayload;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AddNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Metadata;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Node;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.UpdateNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ParseMetadata;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ParseNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ParseNodes;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;

/**
 * Provides access to CloudLoadBalancers Node features.
 * <p/>
 * 
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface NodeApi {
   /**
    * Add a new node with the configuration defined by the request.
    */
   @Named("node:add")
   @POST
   @SelectJson("nodes")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/nodes")
   Set<Node> add(@WrapWith("nodes") Iterable<AddNode> addNodes);

   /**
    * Update the attributes of a node.
    */
   @Named("node:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/nodes/{id}")
   void update(@PathParam("id") int id, @WrapWith("node") UpdateNode updateNode);

   /**
    * List the nodes.
    */
   @Named("node:list")
   @GET
   @ResponseParser(ParseNodes.class)
   @Transform(ParseNodes.ToPagedIterable.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/nodes")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Node> list();

   /**
    * List the nodes with full control of pagination.
    */
   @Named("node:list")
   @GET
   @ResponseParser(ParseNodes.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/nodes")
   IterableWithMarker<Node> list(PaginationOptions options);

   /**
    * Get a node.
    */
   @Named("node:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseNode.class)
   @Path("/nodes/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   Node get(@PathParam("id") int id);

   /**
    * Remove a node from the load balancer.
    */
   @Named("node:remove")
   @DELETE
   @Path("/nodes/{id}")
   @Fallback(VoidOnNotFoundOr404.class)
   @Consumes("*/*")
   void remove(@PathParam("id") int id);
   
   /**
    * Batch remove nodes from the load balancer.
    */
   @Named("node:remove")
   @DELETE
   @Path("/nodes")
   @Fallback(VoidOnNotFoundOr404.class)
   @Consumes("*/*")
   void remove(@QueryParam("id") Iterable<Integer> ids);
   
   /**
    * When a metadata item is added, it is assigned a unique identifier that can be used for mutating operations such
    * as changing the value attribute or removing it. Key and value must be 256 characters or less. 
    * All UTF-8 characters are valid.
    */
   @Named("node:createmetadata")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseMetadata.class)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   @Path("/nodes/{id}/metadata")
   Metadata createMetadata(@PathParam("id") int id, 
                           @BinderParam(BindMetadataToJsonPayload.class) Map<String, String> metadata);
    
   /**
    * List a load balancer's metadata.
    */
   @Named("node:getmetadata")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseMetadata.class)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   @Path("/nodes/{id}/metadata")
   Metadata getMetadata(@PathParam("id") int lb);

   /**
    * Update metadatum. Key and value must be 256 characters or less. All UTF-8 characters are valid.
    * 
    * @return true on a successful update, false if the metadatum was not found
    */
   @Named("node:updatemetadatum")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes("*/*")
   @Fallback(FalseOnNotFoundOr404.class)
   @Payload("%7B\"meta\":%7B\"value\":\"{value}\"%7D%7D")
   @Path("/nodes/{id}/metadata/{metadatumId}")
   boolean updateMetadatum(@PathParam("id") int id, 
                           @PathParam("metadatumId") int metadatumId, 
                           @PayloadParam("value") String value);

   /**
    * Delete metadatum.
    * 
    * @see NodeApi#deleteMetadata(int, Iterable)
    * 
    * @return true on a successful removal, false if the metadatum was not found
    */
   @Named("node:deletemetadatum")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Consumes("*/*")
   @Path("/nodes/{id}/metadata/{metadatumId}")
   boolean deleteMetadatum(@PathParam("id") int id, @PathParam("metadatumId") int metadatumId);
   
   /**
    * Batch delete metadata given the specified ids.
    * 
    * The current default limit is ten ids per request. Any and all configuration data is immediately purged and is 
    * not recoverable. If one or more of the items in the list cannot be removed due to its current status, an 
    * exception is thrown along with the ids of the ones the system identified as potential failures for this request.
    * 
    * @return true on a successful removal, false if the metadata was not found
    */
   @Named("node:deletemetadata")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Consumes("*/*")
   @Path("/nodes/{id}/metadata")
   boolean deleteMetadata(@PathParam("id") int id, 
                          @QueryParam("id") Iterable<Integer> metadataIds);
}
