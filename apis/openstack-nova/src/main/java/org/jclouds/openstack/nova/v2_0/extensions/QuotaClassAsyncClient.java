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
import org.jclouds.openstack.nova.v2_0.domain.QuotaClass;
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
 * Provides asynchronous access to Quota Classes via the REST API.
 *
 * @author Adam Lowe
 * @see QuotaClassClient
 * @see <a href="http://nova.openstack.org/api/nova.api.openstack.compute.contrib.quota_classes.html"/>
 * @see <a href="http://wiki.openstack.org/QuotaClass"/>
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.QUOTA_CLASSES)
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
@RequestFilters(AuthenticateRequest.class)
@Path("/os-quota-class-sets")
public interface QuotaClassAsyncClient {

   /**
    * @see QuotaClassClient#getQuotaClass
    */
   @GET
   @SelectJson("quota_class_set")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<QuotaClass> getQuotaClass(@PathParam("id") String id);

   /**
    * @see QuotaClassClient#updateQuotaClass
    */
   @PUT
   @Path("/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Boolean> updateQuotaClass(@PathParam("id") String id, @PayloadParam("quota_class_set") QuotaClass quotas);

}