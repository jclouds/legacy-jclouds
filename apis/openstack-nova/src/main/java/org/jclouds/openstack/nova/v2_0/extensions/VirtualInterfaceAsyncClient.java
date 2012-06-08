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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.VirtualInterface;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Virtual Interface features (VIFs).
 * 
 * @see VirtualInterfaceClient
 * @author Adam Lowe
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.VIRTUAL_INTERFACES)
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
public interface VirtualInterfaceAsyncClient {
   /**
    * @see VirtualInterfaceClient#listVirtualInterfacesForServer(String)
    */
   @GET
   @SelectJson("virtual_interfaces")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/servers/{server_id}/os-virtual-interfaces")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<VirtualInterface>> listVirtualInterfacesForServer(@PathParam("server_id") String serverId);
}
