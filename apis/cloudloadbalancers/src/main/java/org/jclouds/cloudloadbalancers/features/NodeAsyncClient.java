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
package org.jclouds.cloudloadbalancers.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudloadbalancers.domain.Node;
import org.jclouds.cloudloadbalancers.domain.NodeAttributes;
import org.jclouds.cloudloadbalancers.domain.NodeRequest;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to CloudLoadBalancers Node features.
 * <p/>
 * 
 * @see NodeAsyncClient
 * @see <a
 *      href="http://docs.rackspace.com/loadbalancers/api/v1.0/clb-devguide/content/Nodes-d1e2173.html"
 *      />
 * @author Dan Lo Bianco
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
public interface NodeAsyncClient {

   /**
    * @see NodeClient#createNodesInLoadBalancer
    */
   @POST
   @SelectJson("nodes")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/loadbalancers/{lbid}/nodes")
   ListenableFuture<Set<Node>> createNodesInLoadBalancer(@WrapWith("nodes") Set<NodeRequest> nodes,
		   @PathParam("lbid") int lbid);

   /**
    * @see NodeClient#updateAttributesForNodeInLoadBalancer
    */
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/loadbalancers/{lbid}/nodes/{nid}")
   ListenableFuture<Void> updateAttributesForNodeInLoadBalancer(@WrapWith("node") NodeAttributes attrs,
		   @PathParam("nid") int nid,
		   @PathParam("lbid") int lbid);

   /**
    * @see NodeClient#listNodes
    */
   @GET
   @SelectJson("nodes")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/loadbalancers/{lbid}/nodes")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Node>> listNodes(@PathParam("lbid") int lbid);
   
   /**
    * @see NodeClient#getNodeInLoadBalancer
    */
   @GET
   @SelectJson("node")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/loadbalancers/{lbid}/nodes/{nid}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Node> getNodeInLoadBalancer(@PathParam("nid") int nid,
		   @PathParam("lbid") int lbid);
   
   /**
    * @see NodeClient#removeNodeFromLoadBalancer
    */
   @DELETE
   @Path("/loadbalancers/{lbid}/nodes/{nid}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Consumes("*/*")
   ListenableFuture<Void> removeNodeFromLoadBalancer(@PathParam("nid") int nid,
		   @PathParam("lbid") int lbid);
   
   /**
    * @see NodeClient#removeNodesFromLoadBalancer
    */
   @DELETE
   @Path("/loadbalancers/{lbid}/nodes")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Consumes("*/*")
   ListenableFuture<Void> removeNodesFromLoadBalancer(@QueryParam("id") Set<Integer> nids, 
		   @PathParam("lbid") int lbid);


}
