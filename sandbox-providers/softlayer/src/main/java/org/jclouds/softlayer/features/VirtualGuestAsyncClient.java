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
package org.jclouds.softlayer.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.softlayer.domain.VirtualGuest;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to VirtualGuest via their REST API.
 * <p/>
 * 
 * @see VirtualGuestClient
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Path("/v{jclouds.api-version}")
public interface VirtualGuestAsyncClient {
   public static String GUEST_MASK = "powerState;networkVlans;operatingSystem.passwords;datacenter";

   /**
    * @see VirtualGuestClient#listVirtualGuests
    */
   @GET
   @Path("/SoftLayer_Account/VirtualGuests.json")
   @QueryParams(keys = "objectMask", values = GUEST_MASK)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<VirtualGuest>> listVirtualGuests();

   /**
    * @see VirtualGuestClient#getVirtualGuest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}.json")
   @QueryParams(keys = "objectMask", values = GUEST_MASK)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VirtualGuest> getVirtualGuest(@PathParam("id") long id);

   /**
    * @see VirtualGuestClient#rebootHardVirtualGuest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/rebootHard.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> rebootHardVirtualGuest(@PathParam("id") long id);

   /**
    * @see VirtualGuestClient#powerOffVirtualGuest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/powerOff.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> powerOffVirtualGuest(@PathParam("id") long id);

   /**
    * @see VirtualGuestClient#powerOnVirtualGuest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/powerOn.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> powerOnVirtualGuest(@PathParam("id") long id);

   /**
    * @see VirtualGuestClient#pauseVirtualGuest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/pause.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> pauseVirtualGuest(@PathParam("id") long id);

   /**
    * @see VirtualGuestClient#resumeVirtualGuest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/resume.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> resumeVirtualGuest(@PathParam("id") long id);
}
