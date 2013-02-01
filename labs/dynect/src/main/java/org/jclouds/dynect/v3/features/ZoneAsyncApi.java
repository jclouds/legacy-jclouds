/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
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
package org.jclouds.dynect.v3.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.dynect.v3.domain.Zone;
import org.jclouds.dynect.v3.filters.SessionManager;
import org.jclouds.dynect.v3.functions.ExtractNames;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;

import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @see ZoneApi
 * @see <a
 *      href="https://manage.dynect.net/help/docs/api2/rest/resources/Zone.html">doc</a>
 * @author Adrian Cole
 */
// required for all calls
@Produces(APPLICATION_JSON)
@Headers(keys = "API-Version", values = "{jclouds.api-version}")
@Path("/Zone")
@RequestFilters(SessionManager.class)
public interface ZoneAsyncApi {

   /**
    * @see ZoneApi#list
    */
   @Named("GET:ZoneList")
   @GET
   @SelectJson("data")
   @Transform(ExtractNames.class)
   ListenableFuture<FluentIterable<String>> list();
   
   /**
    * @see ZoneApi#isValid
    */
   @Named("GET:Zone")
   @GET
   @Path("/{name}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Zone> get(@PathParam("name") String name);
}
