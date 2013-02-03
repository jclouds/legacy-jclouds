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
package org.jclouds.cloudstack.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.Volume;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.ListVolumesOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 *
 * @author Vijay Kiran
 * @see org.jclouds.cloudstack.features.VolumeClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface VolumeAsyncClient {
   /**
    * @see VolumeClient#listVolumes(org.jclouds.cloudstack.options.ListVolumesOptions...)
    */
   @Named("listVolumes")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listVolumes", "true" })
   @SelectJson("volume")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Volume>> listVolumes(ListVolumesOptions... options);

   /**
    * @see VolumeClient#getVolume(String)
    */
   @Named("listVolumes")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = { "command", "listAll" }, values = { "listVolumes", "true" })
   @SelectJson("volume")
   @OnlyElement
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Volume> getVolume(@QueryParam("id") String id);


   /**
    * @see VolumeClient#createVolumeFromDiskOfferingInZone(String, String, String)
    */
   @Named("createVolume")
   @GET
   @QueryParams(keys = "command", values = "createVolume")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> createVolumeFromDiskOfferingInZone(@QueryParam("name") String name,
                                                                            @QueryParam("diskofferingid") String diskOfferingId,
                                                                            @QueryParam("zoneid") String zoneId);

   /**
    * @see VolumeClient#createVolumeFromSnapshotInZone(String, String, String)
    */
   @Named("createVolume")
   @GET
   @QueryParams(keys = "command", values = "createVolume")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> createVolumeFromSnapshotInZone(@QueryParam("name") String name,
                                                                        @QueryParam("snapshotid") String snapshotId,
                                                                        @QueryParam("zoneid") String zoneId);

   /**
    * @see VolumeClient#attachVolume(String, String)
    */
   @Named("attachVolume")
   @GET
   @QueryParams(keys = "command", values = "attachVolume")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> attachVolume(@QueryParam("id") String volumeId,
                                                      @QueryParam("virtualmachineid") String virtualMachineId);

   /**
    * @see VolumeClient#detachVolume(String)
    */
   @Named("detachVolume")
   @GET
   @QueryParams(keys = "command", values = "detachVolume")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> detachVolume(@QueryParam("id") String volumeId);

   /**
    * @see VolumeClient#deleteVolume(String)
    */
   @Named("deleteVolume")
   @GET
   @QueryParams(keys = "command", values = "deleteVolume")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteVolume(@QueryParam("id") String id);

}
