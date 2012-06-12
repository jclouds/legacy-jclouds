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
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.VolumeType;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeTypeOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.functions.ReturnEmptyMapOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Volume Type features
 *
 * @author Adam Lowe
 * @see VolumeTypeClient
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.VOLUME_TYPES)
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
@Path("/os-volume-types")
@Consumes(MediaType.APPLICATION_JSON)
public interface VolumeTypeAsyncClient {

   /**
    * @see VolumeTypeClient#listVolumeTypes
    */
   @GET
   @SelectJson("volume_types")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<VolumeType>> listVolumeTypes();


   /**
    * @see VolumeTypeClient#getVolumeType
    */
   @GET
   @Path("/{id}")
   @SelectJson("volume_type")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VolumeType> getVolumeType(@PathParam("id") String id);

   /**
    * @see VolumeTypeClient#createVolumeType
    */
   @POST
   @SelectJson("volume_type")
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("volume_type")
   ListenableFuture<VolumeType> createVolumeType(@PayloadParam("name") String name, CreateVolumeTypeOptions... options);

   /**
    * @see VolumeTypeClient#deleteVolumeType
    */
   @DELETE
   @Path("/{id}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> deleteVolumeType(@PathParam("id") String id);

   /**
    * @see VolumeTypeClient#getAllExtraSpecs(String)
    */
   @GET
   @SelectJson("extra_specs")
   @Path("/{id}/extra_specs")
   @ExceptionParser(ReturnEmptyMapOnNotFoundOr404.class)
   ListenableFuture<Map<String, String>> getAllExtraSpecs(@PathParam("id") String id);

   /**
    * @see VolumeTypeClient#setAllExtraSpecs(String, java.util.Map)
    */
   @POST
   @Path("/{id}/extra_specs")
   @Produces(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Boolean> setAllExtraSpecs(@PathParam("id") String id, @PayloadParam("extra_specs") Map<String, String> specs);

   /**
    * @see VolumeTypeClient#getExtraSpec(String, String)
    */
   @GET
   @Path("/{id}/extra_specs/{key}")
   @Unwrap
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<String> getExtraSpec(@PathParam("id") String id, @PathParam("key") String key);

   /**
    * @see VolumeTypeClient#setExtraSpec(String, String, String)
    */
   @PUT
   @Path("/{id}/extra_specs/{key}")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"{key}\":\"{value}\"%7D")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> setExtraSpec(@PathParam("id") String id,
                                          @PathParam("key") @PayloadParam("key") String key,
                                          @PayloadParam("value") String value);

   /**
    * @see VolumeTypeClient#deleteExtraSpec(String, String)
    */
   @DELETE
   @Path("/{id}/extra_specs/{key}")
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> deleteExtraSpec(@PathParam("id") String id,
                                             @PathParam("key") String key);

}
