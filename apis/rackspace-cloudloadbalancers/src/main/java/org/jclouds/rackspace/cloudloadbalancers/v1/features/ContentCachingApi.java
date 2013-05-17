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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ParseNestedBoolean;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

/**
 * When content caching is enabled, recently-accessed files are stored on the load balancer for easy retrieval by web 
 * clients. Content caching improves the performance of high traffic web sites by temporarily storing data that was 
 * recently accessed. While it's cached, requests for that data will be served by the load balancer, which in turn 
 * reduces load off the back end nodes. The result is improved response times for those requests and less load on the 
 * web server.
 * <p/>
 * 
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface ContentCachingApi {
   /**
    * Determine if the load balancer is content caching.
    */
   @Named("contentcaching:state")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseNestedBoolean.class)
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/contentcaching")
   boolean isContentCaching();
   
   /**
    * Enable content caching.
    */
   @Named("contentcaching:state")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   @Payload("{\"contentCaching\":{\"enabled\":true}}")
   @Path("/contentcaching")
   void enable();
   
   /**
    * Disable content caching.
    */
   @Named("contentcaching:state")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   @Payload("{\"contentCaching\":{\"enabled\":false}}")
   @Path("/contentcaching")
   void disable();
}
