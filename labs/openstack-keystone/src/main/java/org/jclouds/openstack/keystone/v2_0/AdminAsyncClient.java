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
package org.jclouds.openstack.keystone.v2_0;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Admin via their REST API.
 * <p/>
 * 
 * @see AdminClient
 * @author Adam Lowe
 */
@SkipEncoding({ '/', '=' })
public interface AdminAsyncClient {

   /**
    * @see UserClient#getApiMetadata()
    */
   @GET
   @SelectJson("version")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ApiMetadata> getApiMetadata();

   /**
    * @see UserClient#listTenants()
    */
   @GET
   @SelectJson("tenants")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tenants")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Tenant>> listTenants();
   
   /** @see AdminClient#getToken(String) */
   @GET
   @SelectJson("token")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tokens/{token}")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Token> getToken(@PathParam("token") String token);

   /** @see AdminClient#getUserOfToken(String) */
   @GET
   @SelectJson("user")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tokens/{token}")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<User> getUserOfToken(@PathParam("token") String token);

   /** @see AdminClient#checkTokenIsValid(String) */
   @HEAD
   @Path("/tokens/{token}")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> checkTokenIsValid(@PathParam("token") String token);

   /** @see AdminClient#getEndpointsForToken(String) */
   @GET
   @SelectJson("endpoints")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tokens/{token}/endpoints")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Endpoint>> getEndpointsForToken(@PathParam("token") String token);

   /** @see AdminClient#getTenant(String) */
   @GET
   @SelectJson("tenant")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tenants/{tenantId}")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Tenant> getTenant(@PathParam("tenantId") String tenantId);

   /** @see AdminClient#getTenantByName(String) */
   @GET
   @SelectJson("tenant")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tenants")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Tenant> getTenantByName(@QueryParam("name") String tenantName);

   /** @see AdminClient#listUsers() */
   @GET
   @SelectJson("users")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<User>> listUsers();

   /** @see AdminClient#getUser(String) */
   @GET
   @SelectJson("user")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users/{userId}")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<User> getUser(@PathParam("userId") String userId);
   
   /** @see AdminClient#getUserByName(String) */
   @GET
   @SelectJson("user")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<User> getUserByName(@QueryParam("name") String userName);
   
   /** @see AdminClient#listRolesOfUser(String) */
   @GET
   @SelectJson("roles")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users/{userId}/roles")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Role>> listRolesOfUser(@PathParam("userId") String userId);

   /** @see AdminClient#listRolesOfUserOnTenant(String, String) */
   @GET
   @SelectJson("roles")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tenants/{tenantId}/users/{userId}/roles")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Role>> listRolesOfUserOnTenant(@PathParam("userId") String userId, @PathParam("tenantId") String tenantId);
}
