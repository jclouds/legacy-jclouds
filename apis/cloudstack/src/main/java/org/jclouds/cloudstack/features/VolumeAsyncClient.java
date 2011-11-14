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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Set;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.Volume;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.options.ListVolumesOptions;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 *
 * @author Vijay Kiran
 * @see org.jclouds.cloudstack.features.VolumeClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 */
@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
public interface VolumeAsyncClient {
   /**
    * @see VolumeClient#listVolumes(org.jclouds.cloudstack.options.ListVolumesOptions...)
    */
   @GET
   @QueryParams(keys = "command", values = "listVolumes")
   @SelectJson("volume")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Volume>> listVolumes(ListVolumesOptions... options);


   /**
    * @see VolumeClient#createVolumeFromDiskOfferingInZone(String, long, long)
    */
   @GET
   @QueryParams(keys = "command", values = "createVolume")
   @SelectJson("volume")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<AsyncCreateResponse> createVolumeFromDiskOfferingInZone(@QueryParam("name") String name,
                                                                            @QueryParam("diskofferingid") long diskOfferingId,
                                                                            @QueryParam("zoneid") long zoneId);

   /**
    * @see VolumeClient#createVolumeWithSnapshot(String, long)
    */
   @GET
   @QueryParams(keys = "command", values = "createVolume")
   @SelectJson("volume")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<AsyncCreateResponse> createVolumeWithSnapshot(@QueryParam("name") String name,
                                                                  @QueryParam("snapshotid") long diskOfferingId);

   /**
    * @see VolumeClient#deleteVolume(long)
    */
   @GET
   @QueryParams(keys = "command", values = "deleteVolume")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteVolume(@QueryParam("id") long id);

}
