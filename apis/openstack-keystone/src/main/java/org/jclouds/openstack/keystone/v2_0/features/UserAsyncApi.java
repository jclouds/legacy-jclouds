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
package org.jclouds.openstack.keystone.v2_0.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.functions.ReturnEmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.functions.internal.ParseUsers;
import org.jclouds.openstack.keystone.v2_0.functions.internal.ParseUsers.ToPagedIterable;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.openstack.v2_0.services.Identity;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.functions.ReturnEmptyPagedIterableOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to User via their REST API.
 * <p/>
 * 
 * @see UserApi
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-identity-service/2.0/content/User_Operations.html"
 *      />
 * @author Adam Lowe
 */
@org.jclouds.rest.annotations.Endpoint(Identity.class)
@SkipEncoding({ '/', '=' })
public interface UserAsyncApi {

   /**
    * @see UserApi#list()
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseUsers.class)
   @Transform(ToPagedIterable.class)
   @ExceptionParser(ReturnEmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends User>> list();

   /** @see UserApi#list(PaginationOptions) */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseUsers.class)
   @ExceptionParser(ReturnEmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends User>> list(PaginationOptions options);

   /** @see UserApi#get(String) */
   @GET
   @SelectJson("user")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users/{userId}")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends User> get(@PathParam("userId") String userId);

   /** @see UserApi#getByName(String) */
   @GET
   @SelectJson("user")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends User> getByName(@QueryParam("name") String userName);

   /** @see UserApi#listRolesOfUser(String) */
   @GET
   @SelectJson("roles")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users/{userId}/roles")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<? extends Role>> listRolesOfUser(@PathParam("userId") String userId);

   /** @see UserApi#listRolesOfUserOnTenant(String, String) */
   @GET
   @SelectJson("roles")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tenants/{tenantId}/users/{userId}/roles")
   @RequestFilters(AuthenticateRequest.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<? extends Role>> listRolesOfUserOnTenant(@PathParam("userId") String userId,
            @PathParam("tenantId") String tenantId);
}
