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
package org.jclouds.dynect.v3;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.dynect.v3.domain.Job;
import org.jclouds.dynect.v3.features.SessionAsyncApi;
import org.jclouds.dynect.v3.features.ZoneAsyncApi;
import org.jclouds.dynect.v3.filters.SessionManager;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to DynECT Managed DNS through the API2 api
 * <p/>
 * 
 * @see DynECTApi
 * @see <a href="https://manage.dynect.net/help/docs/api2/rest/" />
 * @author Adrian Cole
 */
public interface DynECTAsyncApi {
   /**
    * @see DynECTApi#getJob
    */
   @Named("GetJob")
   @GET
   @Path("/Job/{jobId}")
   @Produces(APPLICATION_JSON)
   @RequestFilters(SessionManager.class)
   @Headers(keys = "API-Version", values = "{jclouds.api-version}")
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(APPLICATION_JSON)
   ListenableFuture<Job> getJob(@PathParam("jobId") long jobId);

   /**
    * Provides asynchronous access to Session features.
    */
   @Delegate
   SessionAsyncApi getSessionApi();

   /**
    * Provides asynchronous access to Zone features.
    */
   @Delegate
   ZoneAsyncApi getZoneApi();
}
