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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr422;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.cloudloadbalancers.domain.ConnectionThrottle;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseConnectionThrottle;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseNestedBoolean;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Rackspace Cloud Load Balancers via their REST API.
 * <p/>
 * 
 * @see ConnectionApi
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface ConnectionAsyncApi {

   /**
    * @see ConnectionApi#createOrUpdateConnectionThrottle(ConnectionThrottle)
    */
   @Named("connectionthrottle:create")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON) 
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/connectionthrottle")
   ListenableFuture<Void> createOrUpdateConnectionThrottle(
         @WrapWith("connectionThrottle") ConnectionThrottle connectionThrottle);

   /**
    * @see ConnectionApi#getConnectionThrottle()
    */
   @Named("connectionthrottle:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseConnectionThrottle.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/connectionthrottle")
   ListenableFuture<ConnectionThrottle> getConnectionThrottle();

   /**
    * @see ConnectionApi#removeConnectionThrottle()
    */
   @Named("connectionthrottle:remove")
   @DELETE
   @Fallback(FalseOnNotFoundOr422.class)
   @Path("/connectionthrottle")
   @Consumes("*/*")
   ListenableFuture<Boolean> removeConnectionThrottle();

   /**
    * @see ConnectionApi#isConnectionLogging()
    */
   @Named("connectionlogging:state")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseNestedBoolean.class)
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/connectionlogging")
   ListenableFuture<Boolean> isConnectionLogging();

   /**
    * @see ConnectionApi#enableConnectionLogging()
    */
   @Named("connectionlogging:state")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   @Payload("{\"connectionLogging\":{\"enabled\":true}}")
   @Path("/connectionlogging")
   ListenableFuture<Void> enableConnectionLogging();

   /**
    * @see ConnectionApi#disableConnectionLogging()
    */
   @Named("connectionlogging:state")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   @Payload("{\"connectionLogging\":{\"enabled\":false}}")
   @Path("/connectionlogging")
   ListenableFuture<Void> disableConnectionLogging();
}
