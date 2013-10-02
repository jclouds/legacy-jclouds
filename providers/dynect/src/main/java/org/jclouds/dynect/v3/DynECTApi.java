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
package org.jclouds.dynect.v3;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.dynect.v3.domain.Job;
import org.jclouds.dynect.v3.features.GeoRegionGroupApi;
import org.jclouds.dynect.v3.features.GeoServiceApi;
import org.jclouds.dynect.v3.features.RecordApi;
import org.jclouds.dynect.v3.features.SessionApi;
import org.jclouds.dynect.v3.features.ZoneApi;
import org.jclouds.dynect.v3.filters.AlwaysAddContentType;
import org.jclouds.dynect.v3.filters.SessionManager;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;

/**
 * Provides access to DynECT Managed DNS through the API2 api
 * <p/>
 * 
 * @see <a href="https://manage.dynect.net/help/docs/api2/rest/" />
 * @author Adrian Cole
 */
public interface DynECTApi extends Closeable {
   /**
    * returns the current status of a job.
    * 
    * @param jobId
    *           The ID of the job
    * @return null, if not found
    */
   @Named("GetJob")
   @GET
   @Path("/Job/{jobId}")
   @RequestFilters({ AlwaysAddContentType.class, SessionManager.class })
   @Headers(keys = "API-Version", values = "{jclouds.api-version}")
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(APPLICATION_JSON)
   @Nullable
   Job getJob(@PathParam("jobId") long jobId);

   /**
    * Provides access to Session features.
    */
   @Delegate
   SessionApi getSessionApi();

   /**
    * Provides access to Zone features.
    */
   @Delegate
   ZoneApi getZoneApi();

   /**
    * Provides access to Record features
    */
   @Delegate
   RecordApi getRecordApiForZone(@PathParam("zone") String zone);

   /**
    * Provides access to Geo features.
    */
   @Delegate
   GeoServiceApi getGeoServiceApi();

   /**
    * Provides access to Geo region group features
    */
   @Delegate
   GeoRegionGroupApi getGeoRegionGroupApiForService(@PathParam("serviceName") String serviceName);
}
