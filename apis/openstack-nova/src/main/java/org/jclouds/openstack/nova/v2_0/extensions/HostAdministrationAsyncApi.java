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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.Host;
import org.jclouds.openstack.nova.v2_0.domain.HostResourceUsage;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.MaintenanceModeDisabledResponseParser;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.MaintenanceModeEnabledResponseParser;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.PowerIsRebootResponseParser;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.PowerIsShutdownResponseParser;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.PowerIsStartupResponseParser;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.StatusDisabledResponseParser;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.StatusEnabledResponseParser;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Host Administration features via the REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see HostAdministrationApi
 * @see <a href= "http://docs.openstack.org/api/openstack-compute/2/content/Extensions-d1e1444.html"/>
 * @see <a href="http://nova.openstack.org/api_ext" />
 * @see <a href="http://nova.openstack.org/api/nova.api.openstack.compute.contrib.hosts.html" />
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.HOSTS)
@RequestFilters(AuthenticateRequest.class)
@Path("/os-hosts")
@Consumes(MediaType.APPLICATION_JSON)
public interface HostAdministrationAsyncApi {

   /**
    * @see HostAdministrationApi#list()
    */
   @Named("hostadmin:list")
   @GET
   @SelectJson("hosts")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<? extends FluentIterable<? extends Host>> list();

   /**
    * @see HostAdministrationApi#listResourceUsage(String)
    */
   @Named("hostadmin:listresource")
   @GET
   @Path("/{id}")
   @SelectJson("host")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<? extends FluentIterable<? extends HostResourceUsage>> listResourceUsage(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationApi#enable(String)
    */
   @Named("hostadmin:enable")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   @Payload("{\"status\":\"enable\"}")
   @ResponseParser(StatusEnabledResponseParser.class)
   ListenableFuture<Boolean> enable(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationApi#disable(String) 
    */
   @Named("hostadmin:disable")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   @Payload("{\"status\":\"disable\"}")
   @ResponseParser(StatusDisabledResponseParser.class)
   ListenableFuture<Boolean> disable(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationApi#startMaintenance(String)
    */
   @Named("hostadmin:startmaintenance")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   @Payload("{\"maintenance_mode\":\"enable\"}")
   @ResponseParser(MaintenanceModeEnabledResponseParser.class)
   ListenableFuture<Boolean> startMaintenance(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationApi#stopMaintenance(String)
    */
   @Named("hostadmin:stopmaintenance")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   @Payload("{\"maintenance_mode\":\"disable\"}")
   @ResponseParser(MaintenanceModeDisabledResponseParser.class)
   ListenableFuture<Boolean> stopMaintenance(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationApi#startup(String)
    */
   @Named("hostadmin:startup")
   @GET
   @Path("/{id}/startup")
   @ResponseParser(PowerIsStartupResponseParser.class)
   ListenableFuture<Boolean> startup(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationApi#shutdown(String)
    */
   @Named("hostadmin:shutdown")
   @GET
   @Path("/{id}/shutdown")
   @ResponseParser(PowerIsShutdownResponseParser.class)
   ListenableFuture<Boolean> shutdown(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationApi#reboot(String)
    */
   @Named("hostadmin:reboot")
   @GET
   @Path("/{id}/reboot")
   @ResponseParser(PowerIsRebootResponseParser.class)
   ListenableFuture<Boolean> reboot(@PathParam("id") String hostId);
}
