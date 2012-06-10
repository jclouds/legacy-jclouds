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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.functions.ReturnEmptyMapOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provide access to extra metadata for Nova flavors.
 *
 * @author Adam Lowe
 * @see <a href="http://nova.openstack.org/api/nova.api.openstack.compute.contrib.flavorextraspecs.html"/>
 * @see org.jclouds.openstack.nova.v2_0.features.FlavorClient
 * @see FlavorExtraSpecsClient
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.FLAVOR_EXTRA_SPECS)
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface FlavorExtraSpecsAsyncClient {

   /**
    * @see FlavorExtraSpecsClient#getAllExtraSpecs(String)
    */
   @GET
   @SelectJson("extra_specs")
   @Path("/flavors/{flavor_id}/os-extra_specs")
   @ExceptionParser(ReturnEmptyMapOnNotFoundOr404.class)
   ListenableFuture<Map<String, String>> getAllExtraSpecs(@PathParam("flavor_id") String flavorId);

   /**
    * @see FlavorExtraSpecsClient#setExtraSpec(String, String, String)
    */
   @POST
   @Path("/flavors/{flavor_id}/os-extra_specs")
   @Produces(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Boolean> setAllExtraSpecs(@PathParam("flavor_id") String flavorId, @PayloadParam("extra_specs") Map<String, String> specs);

   /**
    * @see FlavorExtraSpecsClient#getExtraSpec(String, String)
    */
   @GET
   @Path("/flavors/{flavor_id}/os-extra_specs/{key}")
   @Unwrap
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<String> getExtraSpec(@PathParam("flavor_id") String flavorId, @PathParam("key") String key);

   /**
    * @see FlavorExtraSpecsClient#setExtraSpec(String, String, String)
    */
   @PUT
   @Path("/flavors/{flavor_id}/os-extra_specs/{key}")
   @Produces(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   @Payload("%7B\"{key}\":\"{value}\"%7D")
   ListenableFuture<Boolean> setExtraSpec(@PathParam("flavor_id") String flavorId,
                                          @PathParam("key") @PayloadParam("key") String key,
                                          @PayloadParam("value") String value);

   /**
    * @see FlavorExtraSpecsClient#deleteExtraSpec(String, String)
    */
   @DELETE
   @Path("/flavors/{flavor_id}/os-extra_specs/{key}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> deleteExtraSpec(@PathParam("flavor_id") String flavorId,
                                             @PathParam("key") String key);

}