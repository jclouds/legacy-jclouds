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
package org.jclouds.openstack.nova.v2_0.extensions;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.features.ExtensionAsyncApi;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Floating IPs via the REST API.
 * <p/>
 * 
 * @see FloatingIPApi
 * @author Jeremy Daggett
 * @see ExtensionAsyncApi
 * @see <a href= "http://docs.openstack.org/api/openstack-compute/2/content/Extensions-d1e1444.html"
 *      />
 * @see <a href="http://nova.openstack.org/api_ext" />
 * @see <a href="http://wiki.openstack.org/os_api_floating_ip"/>
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.FLOATING_IPS)
@RequestFilters(AuthenticateRequest.class)
public interface FloatingIPAsyncApi {

   /**
    * @see FloatingIPApi#list
    */
   @Named("floatingip:list")
   @GET
   @Path("/os-floating-ips")
   @SelectJson("floating_ips")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<? extends FluentIterable<? extends FloatingIP>> list();

   /**
    * @see FloatingIPApi#get
    */
   @Named("floatingip:get")
   @GET
   @Path("/os-floating-ips/{id}")
   @SelectJson("floating_ip")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends FloatingIP> get(@PathParam("id") String id);

   /**
    * @see FloatingIPApi#create
    */
   @Named("floatingip:create")
   @POST
   @Path("/os-floating-ips")
   @SelectJson("floating_ip")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Payload("{}")
   ListenableFuture<? extends FloatingIP> create();

   /**
    * @see org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi#allocateFromPool
    */
   @Named("floatingip:create")
   @POST
   @Path("/os-floating-ips")
   @SelectJson("floating_ip")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Payload("%7B\"pool\":\"{pool}\"%7D")
   ListenableFuture<? extends FloatingIP> allocateFromPool(@PayloadParam("pool") String pool);


   /**
    * @see FloatingIPApi#delete
    */
   @Named("floatingip:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/os-floating-ips/{id}")
   ListenableFuture<Void> delete(@PathParam("id") String id);

   /**
    * @see FloatingIPApi#addToServer
    */
   @Named("floatingip:add")
   @POST
   @Path("/servers/{server}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"addFloatingIp\":%7B\"address\":\"{address}\"%7D%7D")
   ListenableFuture<Void> addToServer(@PayloadParam("address") String address,
            @PathParam("server") String serverId);

   /**
    * @see FloatingIPApi#removeFromServer
    */
   @Named("floatingip:remove")
   @POST
   @Path("/servers/{server}/action")
   @Consumes
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"removeFloatingIp\":%7B\"address\":\"{address}\"%7D%7D")
   ListenableFuture<Void> removeFromServer(@PayloadParam("address") String address,
            @PathParam("server") String serverId);

}
