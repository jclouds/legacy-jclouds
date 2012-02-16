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
package org.jclouds.openstack.nova.v1_1.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v1_1.domain.FloatingIP;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Floating IPs via the REST API.
 * <p/>
 * 
 * @see FloatingIPClient
 * @author Jeremy Daggett
 */
@SkipEncoding({ '/', '=' })
@RequestFilters(AuthenticateRequest.class)
public interface FloatingIPAsyncClient {

   /**
    * @see FloatingIPClient#listFloatingIPs
    */
   @GET
   @SelectJson("floating_ips")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/os-floating-ips")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<FloatingIP>> listFloatingIPs();

   /**
    * @see FloatingIPClient#getFloatingIP
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/os-floating-ips/{id}")
   ListenableFuture<FloatingIP> getFloatingIP(@PathParam("id") String id);
   
   /** SHOULD THIS GO IN ServerAsyncClient???
    * 
   @POST
   @Path("/servers/{server}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"addFloatingIp\":%7B\"server\":\"{server}\",\"address\":\"{address}\"%7D%7D")
   ListenableFuture<Void> addFloatingIP(@PayloadParam("server") String server, @PayloadParam("address") String address);

   @POST
   @Path("/servers/{server}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"removeFloatingIp\":%7B\"server\":\"{server}\",\"address\":\"{address}\"%7D%7D")
   ListenableFuture<Void> removeFloatingIP(@PayloadParam("server") String serverId, @PayloadParam("address") String address);
   
   *
   */
   
}
