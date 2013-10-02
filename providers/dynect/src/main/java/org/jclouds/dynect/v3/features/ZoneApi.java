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
import org.jclouds.dynect.v3.DynECTExceptions.JobStillRunningException;
import org.jclouds.dynect.v3.DynECTExceptions.TargetExistsException;
import org.jclouds.dynect.v3.domain.CreatePrimaryZone;
import org.jclouds.dynect.v3.domain.CreatePrimaryZone.ToFQDN;
import org.jclouds.dynect.v3.domain.Job;
import org.jclouds.dynect.v3.domain.Zone;
import org.jclouds.dynect.v3.domain.Zone.SerialStyle;
import org.jclouds.dynect.v3.filters.AlwaysAddContentType;
import org.jclouds.dynect.v3.filters.SessionManager;
import org.jclouds.dynect.v3.functions.ExtractLastPathComponent;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.ResourceNotFoundException;
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

/**
 * @author Adrian Cole
 */
@Headers(keys = "API-Version", values = "{jclouds.api-version}")
@RequestFilters({ AlwaysAddContentType.class, SessionManager.class })
public interface ZoneApi {
   /**
    * Lists all zone ids.
    * 
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Named("ListZones")
   @GET
   @Path("/Zone")
   @SelectJson("data")
   @Transform(ExtractLastPathComponent.class)
   FluentIterable<String> list() throws JobStillRunningException;

   /**
    * Schedules addition of a new primary zone into the current session. Calling
    * {@link ZoneApi#publish(String)} will publish the zone, creating the zone.
    * 
    * @param zone
    *           required parameters to create the zone.
    * @return job relating to the scheduled creation.
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    * @throws TargetExistsException
    *            if the same fqdn exists
    */
   @Named("CreatePrimaryZone")
   @POST
   @Path("/Zone/{fqdn}")
   @Consumes(APPLICATION_JSON)
   Job scheduleCreate(
         @PathParam("fqdn") @ParamParser(ToFQDN.class) @BinderParam(BindToJsonPayload.class) CreatePrimaryZone createZone)
         throws JobStillRunningException, TargetExistsException;

   /**
    * Schedules addition of a new primary zone with one hour default TTL and
    * {@link SerialStyle#INCREMENT} into the current session. Calling
    * {@link ZoneApi#publish(String)} will publish the zone, creating the zone.
    * 
    * @param fqdn
    *           fqdn of the zone to create {@ex. jclouds.org}
    * @param contact
    *           email address of the contact
    * @return job relating to the scheduled creation.
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    * @throws TargetExistsException
    *            if the same fqdn exists
    */
   @Named("CreatePrimaryZone")
   @POST
   @Produces(APPLICATION_JSON)
   @Payload("%7B\"rname\":\"{contact}\",\"serial_style\":\"increment\",\"ttl\":3600%7D")
   @Path("/Zone/{fqdn}")
   @Consumes(APPLICATION_JSON)
   Job scheduleCreateWithContact(@PathParam("fqdn") String fqdn, @PayloadParam("contact") String contact)
         throws JobStillRunningException, TargetExistsException;

   /**
    * Retrieves information about the specified zone.
    * 
    * @param fqdn
    *           fqdn of the zone to get information about. ex
    *           {@code jclouds.org}
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Named("GetZone")
   @GET
   @Path("/Zone/{fqdn}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Zone get(@PathParam("fqdn") String fqdn) throws JobStillRunningException;

   /**
    * Deletes the zone. No need to call @link ZoneApi#publish(String)}.
    * 
    * @param fqdn
    *           zone to delete
    * @return job relating to the scheduled deletion or null, if the zone never
    *         existed.
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Named("DeleteZone")
   @DELETE
   @Path("/Zone/{fqdn}")
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(APPLICATION_JSON)
   @Nullable
   Job delete(@PathParam("fqdn") String fqdn) throws JobStillRunningException;

   /**
    * Deletes changes to the specified zone that have been created during the
    * current session but not yet published to the zone.
    * 
    * @param fqdn
    *           fqdn of the zone to delete changes from ex {@code jclouds.org}
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Named("DeleteZoneChanges")
   @DELETE
   @Path("/ZoneChanges/{fqdn}")
   @Consumes(APPLICATION_JSON)
   Job deleteChanges(@PathParam("fqdn") String fqdn) throws JobStillRunningException;

   /**
    * Publishes the current zone
    * 
    * @param fqdn
    *           fqdn of the zone to publish. ex {@code jclouds.org}
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    * @throws ResourceNotFoundException
    *            if the zone doesn't exist
    */
   @Named("PublishZone")
   @PUT
   @Path("/Zone/{fqdn}")
   @Produces(APPLICATION_JSON)
   @Payload("{\"publish\":true}")
   @SelectJson("data")
   Zone publish(@PathParam("fqdn") String fqdn) throws JobStillRunningException, ResourceNotFoundException;

   /**
    * freezes the specified zone.
    * 
    * @param fqdn
    *           fqdn of the zone to freeze ex {@code jclouds.org}
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Named("FreezeZone")
   @PUT
   @Path("/Zone/{fqdn}")
   @Produces(APPLICATION_JSON)
   @Payload("{\"freeze\":true}")
   @Consumes(APPLICATION_JSON)
   Job freeze(@PathParam("fqdn") String fqdn) throws JobStillRunningException;

   /**
    * thaws the specified zone.
    * 
    * @param fqdn
    *           fqdn of the zone to thaw ex {@code jclouds.org}
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Named("ThawZone")
   @PUT
   @Path("/Zone/{fqdn}")
   @Produces(APPLICATION_JSON)
   @Payload("{\"thaw\":true}")
   @Consumes(APPLICATION_JSON)
   Job thaw(@PathParam("fqdn") String fqdn) throws JobStillRunningException;
}
