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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ParseNestedString;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

/**
 * An error page is the html file that is shown to an end user who is attempting to access a load balancer node that 
 * is offline/unavailable. During provisioning, every load balancer is configured with a default error page that gets 
 * displayed when traffic is requested for an offline node. A single custom error page may be added to a load 
 * balancer with an HTTP-based protocol. Page updates will override existing content.
 * <p/>
 * 
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface ErrorPageApi {
   /**
    * Specify the HTML content for the custom error page. Must be 65536 characters or less.
    */
   @Named("errorpage:create")
   @PUT
   @Consumes(MediaType.WILDCARD)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   @Payload("%7B\"errorpage\":%7B\"content\":\"{content}\"%7D%7D")
   @Path("/errorpage")
   void create(@PayloadParam("content") String content);
   
   /**
    * Get the error page HTML content.
    */
   @Named("errorpage:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseNestedString.class)
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/errorpage")
   String get();
   
   /**
    * If a custom error page is deleted, or the load balancer is changed to a non-HTTP protocol, the default error 
    * page will be restored.
    */
   @Named("errorpage:delete")
   @DELETE
   @Consumes(MediaType.WILDCARD)
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/errorpage")
   boolean delete();
}
