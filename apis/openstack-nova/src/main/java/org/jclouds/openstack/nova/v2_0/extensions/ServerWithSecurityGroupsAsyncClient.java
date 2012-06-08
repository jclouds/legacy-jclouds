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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.ServerWithSecurityGroups;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides synchronous access to Servers with Security Groups.
 *
 * @author Adam Lowe
 * @see org.jclouds.openstack.nova.v2_0.features.ServerAsyncClient
 * @see ServerWithSecurityGroupsClient
 * @see <a href="http://nova.openstack.org/api/nova.api.openstack.compute.contrib.createserverext.html"/>
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.CREATESERVEREXT)
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
public interface ServerWithSecurityGroupsAsyncClient {

   /**
    * @see ServerWithSecurityGroupsClient#getServer(String)
    */
   @GET
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/os-create-server-ext/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ServerWithSecurityGroups> getServer(@PathParam("id") String id);

}
