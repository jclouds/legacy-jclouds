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
package org.jclouds.softlayer.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.softlayer.binders.ProductOrderToJson;
import org.jclouds.softlayer.domain.ProductOrder;
import org.jclouds.softlayer.domain.ProductOrderReceipt;
import org.jclouds.softlayer.domain.VirtualGuest;

/**
 * Provides synchronous access to VirtualGuest.
 * <p/>
 * 
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Path("/v{jclouds.api-version}")
public interface VirtualGuestApi {
   public static String LIST_GUEST_MASK = "virtualGuests.powerState;virtualGuests.networkVlans;virtualGuests.operatingSystem.passwords;virtualGuests.datacenter;virtualGuests.billingItem";
   public static String GUEST_MASK = "powerState;networkVlans;operatingSystem.passwords;datacenter;billingItem";

   /**
    *
    * @return an account's associated virtual guest objects.
    */
   @GET
   @Path("/SoftLayer_Account/VirtualGuests.json")
   @QueryParams(keys = "objectMask", values = LIST_GUEST_MASK)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   Set<VirtualGuest> listVirtualGuests();

   /**
    *
    * @param id
    *           id of the virtual guest
    * @return virtual guest or null if not found
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}.json")
   @QueryParams(keys = "objectMask", values = GUEST_MASK)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VirtualGuest getVirtualGuest(@PathParam("id") long id);

   /**
    * hard reboot the guest.
    *
    * @param id
    *           id of the virtual guest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/rebootHard.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void rebootHardVirtualGuest(@PathParam("id") long id);

   /**
    * Power off a guest
    *
    * @param id
    *           id of the virtual guest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/powerOff.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void powerOffVirtualGuest(@PathParam("id") long id);

   /**
    * Power on a guest
    *
    * @param id
    *           id of the virtual guest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/powerOn.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void powerOnVirtualGuest(@PathParam("id") long id);

   /**
    * pause the guest.
    *
    * @param id
    *           id of the virtual guest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/pause.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void pauseVirtualGuest(@PathParam("id") long id);

   /**
    * resume the guest.
    *
    * @param id
    *           id of the virtual guest
    */
   @GET
   @Path("/SoftLayer_Virtual_Guest/{id}/resume.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void resumeVirtualGuest(@PathParam("id") long id);

   /**
    * Cancel the resource or service for a billing Item
    *
    * @param id
    *            The id of the billing item to cancel
    * @return true or false
    */
   @GET
   @Path("/SoftLayer_Billing_Item/{id}/cancelService.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean cancelService(@PathParam("id") long id);

   /**
    * Use this method for placing server orders and additional services orders.
    * @param order
    *             Details required to order.
    * @return A receipt for the order
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Product_Order/placeOrder" />
    */
   @POST
   @Path("/SoftLayer_Product_Order/placeOrder.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ProductOrderReceipt orderVirtualGuest(@BinderParam(ProductOrderToJson.class)ProductOrder order);

   /**
    * Obtain an order container that is ready to be sent to the orderVirtualGuest method.
    * This container will include all services that the selected computing instance has.
    * If desired you may remove prices which were returned.
    * @see <a href=" @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Product_Order/placeOrder" />
    * @param id
    *          The id of the existing Virtual Guest
    * @return
    *          The ProductOrder used to create the VirtualGust or null if not available
    */
   @GET
   @Path("SoftLayer_Virtual_Guest/{id}/getOrderTemplate/MONTHLY.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ProductOrder getOrderTemplate(@PathParam("id") long id);

}
