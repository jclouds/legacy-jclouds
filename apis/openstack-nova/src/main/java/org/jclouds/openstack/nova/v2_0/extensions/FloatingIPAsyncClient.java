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
package org.jclouds.openstack.nova.v2_0.extensions;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.features.ExtensionAsyncClient;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
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
 * @see ExtensionAsyncClient
 * @see <a href= "http://docs.openstack.org/api/openstack-compute/2/content/Extensions-d1e1444.html"
 *      />
 * @see <a href="http://nova.openstack.org/api_ext" />
 * @see <a href="http://wiki.openstack.org/os_api_floating_ip"/>
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.FLOATING_IPS)
@SkipEncoding( { '/', '=' })
@RequestFilters(AuthenticateRequest.class)
public interface FloatingIPAsyncClient {

   /**
    * @see FloatingIPClient#listFloatingIPs
    */
   @GET
   @Path("/os-floating-ips")
   @SelectJson("floating_ips")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<FloatingIP>> listFloatingIPs();

   /**
    * @see FloatingIPClient#getFloatingIP
    */
   @GET
   @Path("/os-floating-ips/{id}")
   @SelectJson("floating_ip")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<FloatingIP> getFloatingIP(@PathParam("id") String id);

   /**
    * @see FloatingIPClient#allocate
    */
   @POST
   @Path("/os-floating-ips")
   @SelectJson("floating_ip")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Payload("{}")
   ListenableFuture<FloatingIP> allocate();

   /**
    * @see FloatingIPClient#deallocate
    */
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/os-floating-ips/{id}")
   ListenableFuture<Void> deallocate(@PathParam("id") String id);

   /**
    * @see FloatingIPClient#addFloatingIPToServer
    */
   @POST
   @Path("/servers/{server}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"addFloatingIp\":%7B\"address\":\"{address}\"%7D%7D")
   ListenableFuture<Void> addFloatingIPToServer(@PayloadParam("address") String address,
            @PathParam("server") String serverId);

   /**
    * @see FloatingIPClient#removeFloatingIPFromServer
    */
   @POST
   @Path("/servers/{server}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"removeFloatingIp\":%7B\"address\":\"{address}\"%7D%7D")
   ListenableFuture<Void> removeFloatingIPFromServer(@PayloadParam("address") String address,
            @PathParam("server") String serverId);

}
