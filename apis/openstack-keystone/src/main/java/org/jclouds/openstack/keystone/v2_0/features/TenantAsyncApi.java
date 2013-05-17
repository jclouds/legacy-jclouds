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
package org.jclouds.openstack.keystone.v2_0.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.functions.internal.ParseTenants;
import org.jclouds.openstack.keystone.v2_0.functions.internal.ParseTenants.ToPagedIterable;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.openstack.v2_0.services.Identity;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Tenant via their REST API.
 * <p/>
 * 
 * @see TenantApi
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-identity-service/2.0/content/Tenant_Operations.html"
 *      />
 * @author Adam Lowe
 */
@org.jclouds.rest.annotations.Endpoint(Identity.class)
public interface TenantAsyncApi {

   /**
    * @see TenantApi#list()
    */
   @Named("tenant:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tenants")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseTenants.class)
   @Transform(ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends Tenant>> list();

   /** @see TenantApi#list(PaginationOptions) */
   @Named("tenant:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tenants")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseTenants.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends Tenant>> list(PaginationOptions options);

   /** @see TenantApi#get(String) */
   @Named("tenant:get")
   @GET
   @SelectJson("tenant")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tenants/{tenantId}")
   @RequestFilters(AuthenticateRequest.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Tenant> get(@PathParam("tenantId") String tenantId);

   /** @see TenantApi#getByName(String) */
   @Named("tenant:get")
   @GET
   @SelectJson("tenant")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tenants")
   @RequestFilters(AuthenticateRequest.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Tenant> getByName(@QueryParam("name") String tenantName);

}
