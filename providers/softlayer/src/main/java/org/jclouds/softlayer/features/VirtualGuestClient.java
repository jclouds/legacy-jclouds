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
import org.jclouds.softlayer.domain.ProductOrder;
import org.jclouds.softlayer.domain.ProductOrderReceipt;
import org.jclouds.softlayer.domain.VirtualGuest;

/**
 * Provides synchronous access to VirtualGuest.
 * <p/>
 * 
 * @see VirtualGuestAsyncClient
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 * @author Adrian Cole
 */
public interface VirtualGuestClient {

   /**
    * 
    * @return an account's associated virtual guest objects.
    */
   Set<VirtualGuest> listVirtualGuests();

   /**
    * 
    * @param id
    *           id of the virtual guest
    * @return virtual guest or null if not found
    */
   VirtualGuest getVirtualGuest(long id);

   /**
    * hard reboot the guest.
    * 
    * @param id
    *           id of the virtual guest
    */
   void rebootHardVirtualGuest(long id);

   /**
    * Power off a guest
    * 
    * @param id
    *           id of the virtual guest
    */
   void powerOffVirtualGuest(long id);

   /**
    * Power on a guest
    * 
    * @param id
    *           id of the virtual guest
    */
   void powerOnVirtualGuest(long id);

   /**
    * pause the guest.
    * 
    * @param id
    *           id of the virtual guest
    */
   void pauseVirtualGuest(long id);

   /**
    * resume the guest.
    * 
    * @param id
    *           id of the virtual guest
    */
   void resumeVirtualGuest(long id);


   /**
    * Cancel the resource or service for a billing Item
    *
    * @param id
    *            The id of the billing item to cancel
    * @return true or false
    */
   boolean cancelService(long id);

   /**
    * Use this method for placing server orders and additional services orders.
    * @param order
    *             Details required to order.
    * @return A receipt for the order
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Product_Order/placeOrder" />
    */
   ProductOrderReceipt orderVirtualGuest(ProductOrder order);

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
   ProductOrder getOrderTemplate(long id);
}
