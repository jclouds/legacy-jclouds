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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.softlayer.binders.ProductOrderToJson;
import org.jclouds.softlayer.domain.ProductOrder;
import org.jclouds.softlayer.domain.ProductOrderReceipt;
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
   public static String LIST_GUEST_MASK = "virtualGuests.powerState;virtualGuests.networkVlans;virtualGuests.operatingSystem.passwords;virtualGuests.datacenter;virtualGuests.billingItem";
   public static String GUEST_MASK = "powerState;networkVlans;operatingSystem.passwords;datacenter;billingItem";

   /**
    * @see VirtualGuestClient#listVirtualGuests
    */
   @GET
   @Path("/SoftLayer_Account/VirtualGuests.json")
   @QueryParams(keys = "objectMask", values = LIST_GUEST_MASK)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<VirtualGuest>> listVirtualGuests();

   /**
    * @see VirtualGuestClient#getVirtualGuest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}.json")
   @QueryParams(keys = "objectMask", values = GUEST_MASK)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<VirtualGuest> getVirtualGuest(@PathParam("id") long id);

   /**
    * @see VirtualGuestClient#rebootHardVirtualGuest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/rebootHard.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> rebootHardVirtualGuest(@PathParam("id") long id);

   /**
    * @see VirtualGuestClient#powerOffVirtualGuest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/powerOff.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> powerOffVirtualGuest(@PathParam("id") long id);

   /**
    * @see VirtualGuestClient#powerOnVirtualGuest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/powerOn.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> powerOnVirtualGuest(@PathParam("id") long id);

   /**
    * @see VirtualGuestClient#pauseVirtualGuest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/pause.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> pauseVirtualGuest(@PathParam("id") long id);

   /**
    * @see VirtualGuestClient#resumeVirtualGuest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/resume.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> resumeVirtualGuest(@PathParam("id") long id);

   /**
    * @see VirtualGuestClient#cancelService
    */
   @GET
   @Path("/SoftLayer_Billing_Item/{id}/cancelService.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> cancelService(@PathParam("id") long id);

   /**
    * @see org.jclouds.softlayer.features.VirtualGuestClient#orderVirtualGuest
    */
   @POST
   @Path("/SoftLayer_Product_Order/placeOrder.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<ProductOrderReceipt> orderVirtualGuest(@BinderParam(ProductOrderToJson.class)ProductOrder order);

   /**
    * Throws an Internal Server Error if called on bad orders (mapped to HttpResponseException)
    * @see VirtualGuestClient#getOrderTemplate
    * @throws org.jclouds.http.HttpResponseException if called with a 'bad' order.
    */
   @GET
   @Path("SoftLayer_Virtual_Guest/{id}/getOrderTemplate/MONTHLY.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<ProductOrder> getOrderTemplate(@PathParam("id") long id);

}
