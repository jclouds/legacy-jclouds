/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
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
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.cloudloadbalancers.domain.LoadBalancerAttributes;
import org.jclouds.cloudloadbalancers.domain.LoadBalancerRequest;
import org.jclouds.cloudloadbalancers.functions.UnwrapLoadBalancer;
import org.jclouds.cloudloadbalancers.functions.UnwrapLoadBalancers;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access toRackspace Cloud Load Balancers via their REST API.
 * <p/>
 * 
 * @see LoadBalancerClient
 * @see <a
 *      href="http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch04s01.html"
 *      />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
public interface LoadBalancerAsyncClient {

   /**
    * @see LoadBalancerClient#createLoadBalancer
    */
   @POST
   @ResponseParser(UnwrapLoadBalancer.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/loadbalancers")
   ListenableFuture<LoadBalancer> createLoadBalancer(@WrapWith("loadBalancer") LoadBalancerRequest lb);

   /**
    * @see LoadBalancerClient#updateLoadBalancerAttributes
    */
   @PUT
   @ResponseParser(UnwrapLoadBalancer.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/loadbalancers/{id}")
   ListenableFuture<Void> updateLoadBalancerAttributes(@PathParam("id") int id,
            @WrapWith("loadBalancer") LoadBalancerAttributes attrs);

   /**
    * @see CloudServersClient#listLoadBalancers
    */
   @GET
   @ResponseParser(UnwrapLoadBalancers.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/loadbalancers")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<LoadBalancer>> listLoadBalancers();

   /**
    * @see LoadBalancerClient#getLoadBalancer
    */
   @GET
   @ResponseParser(UnwrapLoadBalancer.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}")
   ListenableFuture<LoadBalancer> getLoadBalancer(@PathParam("id") int id);

   /**
    * @see LoadBalancerClient#removeLoadBalancer
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("/loadbalancers/{id}")
   @Consumes("*/*")
   ListenableFuture<Void> removeLoadBalancer(@PathParam("id") int id);

}
