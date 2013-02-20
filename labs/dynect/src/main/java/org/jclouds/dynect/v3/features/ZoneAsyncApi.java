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
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.dynect.v3.DynECTExceptions.TargetExistsException;
import org.jclouds.dynect.v3.DynECTExceptions.JobStillRunningException;
import org.jclouds.dynect.v3.domain.CreatePrimaryZone;
import org.jclouds.dynect.v3.domain.CreatePrimaryZone.ToFQDN;
import org.jclouds.dynect.v3.domain.Job;
import org.jclouds.dynect.v3.domain.Zone;
import org.jclouds.dynect.v3.filters.SessionManager;
import org.jclouds.dynect.v3.functions.ExtractZoneNames;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

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
@RequestFilters(SessionManager.class)
public interface ZoneAsyncApi {

   /**
    * @see ZoneApi#list
    */
   @Named("ListZones")
   @GET
   @Path("/Zone")
   @SelectJson("data")
   @Transform(ExtractZoneNames.class)
   ListenableFuture<FluentIterable<String>> list() throws JobStillRunningException;

   /**
    * @see ZoneApi#get
    */
   @Named("GetZone")
   @GET
   @Path("/Zone/{fqdn}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Zone> get(@PathParam("fqdn") String fqdn) throws JobStillRunningException;

   /**
    * @see ZoneApi#create
    */
   @Named("CreatePrimaryZone")
   @POST
   @Path("/Zone/{fqdn}")
   @SelectJson("data")
   ListenableFuture<Zone> create(
         @PathParam("fqdn") @ParamParser(ToFQDN.class) @BinderParam(BindToJsonPayload.class) CreatePrimaryZone createZone)
         throws JobStillRunningException, TargetExistsException;

   /**
    * @see ZoneApi#createWithContact
    */
   @Named("CreatePrimaryZone")
   @POST
   @Payload("%7B\"rname\":\"{contact}\",\"serial_style\":\"increment\",\"ttl\":3600%7D")
   @Path("/Zone/{fqdn}")
   @SelectJson("data")
   ListenableFuture<Zone> createWithContact(@PathParam("fqdn") String fqdn, @PayloadParam("contact") String contact)
         throws JobStillRunningException, TargetExistsException;

   /**
    * @see ZoneApi#delete
    */
   @Named("DeleteZone")
   @DELETE
   @Path("/Zone/{fqdn}")
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(APPLICATION_JSON)
   ListenableFuture<Job> delete(@PathParam("fqdn") String fqdn) throws JobStillRunningException;

   /**
    * @see ZoneApi#deleteChanges
    */
   @Named("DeleteZoneChanges")
   @DELETE
   @Path("/ZoneChanges/{fqdn}")
   @Consumes(APPLICATION_JSON)
   ListenableFuture<Job> deleteChanges(@PathParam("fqdn") String fqdn) throws JobStillRunningException;

   /**
    * @see ZoneApi#publish
    */
   @Named("PublishZone")
   @PUT
   @Payload("{\"publish\":true}")
   @Path("/Zone/{fqdn}")
   @SelectJson("data")
   ListenableFuture<Zone> publish(@PathParam("fqdn") String fqdn) throws JobStillRunningException;
   
   /**
    * @see ZoneApi#freeze
    */
   @Named("FreezeZone")
   @PUT
   @Path("/Zone/{fqdn}")
   @Payload("{\"freeze\":true}")
   @Consumes(APPLICATION_JSON)
   ListenableFuture<Job> freeze(@PathParam("fqdn") String fqdn) throws JobStillRunningException;

   /**
    * @see ZoneApi#thaw
    */
   @Named("ThawZone")
   @PUT
   @Path("/Zone/{fqdn}")
   @Payload("{\"thaw\":true}")
   @Consumes(APPLICATION_JSON)
   ListenableFuture<Job> thaw(@PathParam("fqdn") String fqdn) throws JobStillRunningException;
}
