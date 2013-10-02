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

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.QuotaClass;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Quota Classes via the REST API.
 *
 * @author Adam Lowe
 * @see QuotaClassApi
 * @see <a href="http://nova.openstack.org/api/nova.api.openstack.compute.contrib.quota_classes.html"/>
 * @see <a href="http://wiki.openstack.org/QuotaClass"/>
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.QUOTA_CLASSES)
@RequestFilters(AuthenticateRequest.class)
@Path("/os-quota-class-sets")
public interface QuotaClassAsyncApi {

   /**
    * @see QuotaClassApi#get
    */
   @Named("quotaclass:get")
   @GET
   @SelectJson("quota_class_set")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends QuotaClass> get(@PathParam("id") String id);

   /**
    * @see QuotaClassApi#update
    */
   @Named("quotaclass:update")
   @PUT
   @Path("/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Boolean> update(@PathParam("id") String id, @PayloadParam("quota_class_set") QuotaClass quotas);

}
