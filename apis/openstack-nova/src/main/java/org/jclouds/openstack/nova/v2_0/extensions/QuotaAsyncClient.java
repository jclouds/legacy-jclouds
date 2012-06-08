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

import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.Quotas;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provide access to Quota information for Nova tenants.
 *
 * @author Adam Lowe
 * @see QuotaClient
 * @see <a href="http://nova.openstack.org/api_ext/ext_quotas.html"/>
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.QUOTAS)
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
@RequestFilters(AuthenticateRequest.class)
@Path("/os-quota-sets")
public interface QuotaAsyncClient {

   /**
    * @see QuotaClient#getDefaultQuotasForTenant(String)
    */
   @GET
   @SelectJson("quota_set")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/{tenant_id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Quotas> getQuotasForTenant(@PathParam("tenant_id") String tenantId);

   /**
    * @see QuotaClient#updateQuotasForTenant(String, org.jclouds.openstack.nova.v2_0.domain.Quotas)
    */
   @PUT
   @Path("/{tenant_id}")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Boolean> updateQuotasForTenant(@PathParam("tenant_id") String tenantId, @PayloadParam("quota_set") Quotas quotas);

   /**
    * @see QuotaClient#getDefaultQuotasForTenant(String)
    */
   @GET
   @SelectJson("quota_set")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/{tenant_id}/defaults")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Quotas> getDefaultQuotasForTenant(@PathParam("tenant_id") String tenantId);

}