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

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyMapOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provide access to extra metadata for Nova flavors.
 *
 * @author Adam Lowe
 * @see <a href="http://nova.openstack.org/api/nova.api.openstack.compute.contrib.flavorextraspecs.html"/>
 * @see org.jclouds.openstack.nova.v2_0.features.FlavorApi
 * @see FlavorExtraSpecsApi
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.FLAVOR_EXTRA_SPECS)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface FlavorExtraSpecsAsyncApi {

   /**
    * @see FlavorExtraSpecsApi#getMetadata(String)
    */
   @Named("flavor:getmetadata")
   @GET
   @SelectJson("extra_specs")
   @Path("/flavors/{flavor_id}/os-extra_specs")
   @Fallback(EmptyMapOnNotFoundOr404.class)
   ListenableFuture<Map<String, String>> getMetadata(@PathParam("flavor_id") String flavorId);

   /**
    * @see FlavorExtraSpecsApi#updateMetadataEntry(String, String, String)
    */
   @Named("flavor:updatemetadata")
   @POST
   @Path("/flavors/{flavor_id}/os-extra_specs")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Boolean> updateMetadata(@PathParam("flavor_id") String flavorId, @PayloadParam("extra_specs") Map<String, String> specs);

   /**
    * @see FlavorExtraSpecsApi#getMetadataKey(String, String)
    */
   @Named("flavor:getmetadata")
   @GET
   @Path("/flavors/{flavor_id}/os-extra_specs/{key}")
   @Unwrap
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<String> getMetadataKey(@PathParam("flavor_id") String flavorId, @PathParam("key") String key);

   /**
    * @see FlavorExtraSpecsApi#updateMetadataEntry(String, String, String)
    */
   @Named("flavor:updatemetadata")
   @PUT
   @Path("/flavors/{flavor_id}/os-extra_specs/{key}")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   @Payload("%7B\"{key}\":\"{value}\"%7D")
   ListenableFuture<Boolean> updateMetadataEntry(@PathParam("flavor_id") String flavorId,
                                          @PathParam("key") @PayloadParam("key") String key,
                                          @PayloadParam("value") String value);

   /**
    * @see FlavorExtraSpecsApi#deleteMetadataKey(String, String)
    */
   @Named("flavor:deletemetadata")
   @DELETE
   @Path("/flavors/{flavor_id}/os-extra_specs/{key}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> deleteMetadataKey(@PathParam("flavor_id") String flavorId,
                                             @PathParam("key") String key);

}
