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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.SimpleTenantUsage;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Simple Tenant Usage via the REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see SimpleTenantUsageApi
 * @see <a href= "http://docs.openstack.org/api/openstack-compute/2/content/Extensions-d1e1444.html" />
 * @see <a href="http://nova.openstack.org/api_ext" />
 * @see <a href="http://nova.openstack.org/api/nova.api.openstack.compute.contrib.simple_tenant_usage.html" />
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.SIMPLE_TENANT_USAGE)
@RequestFilters(AuthenticateRequest.class)
public interface SimpleTenantUsageAsyncApi {

   /**
    * @see SimpleTenantUsageApi#list()
    */
   @Named("tenantusage:list")
   @GET
   @Path("/os-simple-tenant-usage")
   @SelectJson("tenant_usages")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<? extends FluentIterable<? extends SimpleTenantUsage>> list();

   /**
    * @see SimpleTenantUsageApi#get(String)
    */
   @Named("tenantusage:get")
   @GET
   @Path("/os-simple-tenant-usage/{id}")
   @SelectJson("tenant_usage")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends SimpleTenantUsage> get(@PathParam("id") String tenantId);

}
