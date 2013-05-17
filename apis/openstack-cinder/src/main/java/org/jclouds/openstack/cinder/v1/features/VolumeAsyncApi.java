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
package org.jclouds.openstack.cinder.v1.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.options.CreateVolumeOptions;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;

import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Volumes.
 * 
 * @see VolumeApi
 * @see <a href="http://api.openstack.org/">API Doc</a>
 * @author Everett Toews
 */
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
public interface VolumeAsyncApi {
   /**
    * @see VolumeApi#list()
    */
   @Named("volume:list")
   @GET
   @Path("/volumes")
   @SelectJson("volumes")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<? extends FluentIterable<? extends Volume>> list();

   /**
    * @see VolumeApi#listInDetail()
    */
   @Named("volume:list")
   @GET
   @Path("/volumes/detail")
   @SelectJson("volumes")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<? extends FluentIterable<? extends Volume>> listInDetail();

   /**
    * @see VolumeApi#get(String)
    */
   @Named("volume:get")
   @GET
   @Path("/volumes/{id}")
   @SelectJson("volume")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Volume> get(@PathParam("id") String volumeId);

   /**
    * @see VolumeApi#create(int, CreateVolumeOptions...)
    */
   @Named("volume:create")
   @POST
   @Path("/volumes")
   @SelectJson("volume")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(CreateVolumeOptions.class)
   ListenableFuture<? extends Volume> create(@PayloadParam("size") int sizeGB, CreateVolumeOptions... options);

   /**
    * @see VolumeApi#delete(String)
    */
   @Named("volume:delete")
   @DELETE
   @Path("/volumes/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> delete(@PathParam("id") String volumeId);
}
