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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Host Administration features via the REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see HostAdministrationClient
 * @see <a href= "http://docs.openstack.org/api/openstack-compute/2/content/Extensions-d1e1444.html"/>
 * @see <a href="http://nova.openstack.org/api_ext" />
 * @see <a href="http://nova.openstack.org/api/nova.api.openstack.compute.contrib.hosts.html" />
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.HOSTS)
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
@Path("/os-hosts")
@Consumes(MediaType.APPLICATION_JSON)
public interface HostAdministrationAsyncClient {

   /**
    * @see HostAdministrationClient#listHosts()
    */
   @GET
   @SelectJson("hosts")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Host>> listHosts();

   /**
    * @see HostAdministrationClient#getHostResourceUsage(String)
    */
   @GET
   @Path("/{id}")
   @SelectJson("host")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<HostResourceUsage>> getHostResourceUsage(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationClient#enableHost(String)
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   @Payload("{\"status\":\"enable\"}")
   @ResponseParser(StatusEnabledResponseParser.class)
   ListenableFuture<Boolean> enableHost(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationClient#disableHost(String) 
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   @Payload("{\"status\":\"disable\"}")
   @ResponseParser(StatusDisabledResponseParser.class)
   ListenableFuture<Boolean> disableHost(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationClient#startHostMaintenance(String)
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   @Payload("{\"maintenance_mode\":\"enable\"}")
   @ResponseParser(MaintenanceModeEnabledResponseParser.class)
   ListenableFuture<Boolean> startHostMaintenance(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationClient#stopHostMaintenance(String)
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   @Payload("{\"maintenance_mode\":\"disable\"}")
   @ResponseParser(MaintenanceModeDisabledResponseParser.class)
   ListenableFuture<Boolean> stopHostMaintenance(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationClient#startupHost(String)
    */
   @GET
   @Path("/{id}/startup")
   @ResponseParser(PowerIsStartupResponseParser.class)
   ListenableFuture<Boolean> startupHost(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationClient#shutdownHost(String)
    */
   @GET
   @Path("/{id}/shutdown")
   @ResponseParser(PowerIsShutdownResponseParser.class)
   ListenableFuture<Boolean> shutdownHost(@PathParam("id") String hostId);

   /**
    * @see HostAdministrationClient#rebootHost(String)
    */
   @GET
   @Path("/{id}/reboot")
   @ResponseParser(PowerIsRebootResponseParser.class)
   ListenableFuture<Boolean> rebootHost(@PathParam("id") String hostId);
}
