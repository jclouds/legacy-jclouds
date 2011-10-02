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

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jclouds.softlayer.compute.functions.ProductItems;
import org.jclouds.softlayer.domain.*;
import org.jclouds.softlayer.reference.SoftLayerConstants;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.jclouds.softlayer.predicates.ProductItemPredicates.*;
import static org.jclouds.softlayer.predicates.ProductPackagePredicates.named;
import static org.testng.Assert.*;

/**
 * Tests behavior of {@code VirtualGuestClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class VirtualGuestClientLiveTest extends BaseSoftLayerClientLiveTest {

   private static final String TEST_HOSTNAME_PREFIX = "livetest";

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getVirtualGuestClient();
   }

   private VirtualGuestClient client;

   @Test
   public void testListVirtualGuests() throws Exception {
      Set<VirtualGuest> response = client.listVirtualGuests();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (VirtualGuest vg : response) {
         VirtualGuest newDetails = client.getVirtualGuest(vg.getId());
         assertEquals(vg.getId(), newDetails.getId());
         checkVirtualGuest(vg);
      }
   }

   @Test
   public void testCancelAndPlaceOrder() {

      // TODO: Should also check if there are active transactions before trying to cancel.
      // objectMask: virtualGuests.activeTransaction
      for( VirtualGuest guest: client.listVirtualGuests()) {
         if (guest.getHostname().startsWith(TEST_HOSTNAME_PREFIX)) {
            if(guest.getBillingItemId()!=-1) {
               client.cancelService(guest.getBillingItemId());
            }
         }
      }

      int pkgId = Iterables.find(context.getApi().getAccountClient().getActivePackages(),named(ProductPackageClientLiveTest.CLOUD_SERVER_PACKAGE_NAME)).getId();
      ProductPackage productPackage = context.getApi().getProductPackageClient().getProductPackage(pkgId);

      Iterable<ProductItem> ramItems = Iterables.filter(productPackage.getItems(),
            Predicates.and(categoryCode("ram"), capacity(2.0f)));

       Map<Float, ProductItem> ramToProductItem = Maps.uniqueIndex(ramItems, ProductItems.capacity());

       ProductItemPrice ramPrice = ProductItems.price().apply(ramToProductItem.get(2.0f));

       Iterable<ProductItem> cpuItems = Iterables.filter(productPackage.getItems(), Predicates.and(units("PRIVATE_CORE"), capacity(2.0f)));
       Map<Float, ProductItem> coresToProductItem = Maps.uniqueIndex(cpuItems, ProductItems.capacity());

       ProductItemPrice cpuPrice = ProductItems.price().apply(coresToProductItem.get(2.0f));

       Iterable<ProductItem> operatingSystems = Iterables.filter(productPackage.getItems(), categoryCode("os"));
       Map<String, ProductItem> osToProductItem = Maps.uniqueIndex(operatingSystems, ProductItems.description());
       ProductItemPrice osPrice = ProductItems.price().apply(osToProductItem.get("Ubuntu Linux 8 LTS Hardy Heron - Minimal Install (64 bit)"));

       Set<ProductItemPrice> prices = Sets.<ProductItemPrice>newLinkedHashSet();
       prices.addAll(SoftLayerConstants.DEFAULT_VIRTUAL_GUEST_PRICES);
       prices.add(ramPrice);
       prices.add(cpuPrice);
       prices.add(osPrice);

       VirtualGuest guest = VirtualGuest.builder().domain("jclouds.org")
                                                 .hostname(TEST_HOSTNAME_PREFIX+new Random().nextInt())
                                                 .build();

       String location = ""+Iterables.get(productPackage.getDatacenters(),0).getId();
       ProductOrder order = ProductOrder.builder()
                                       .packageId(pkgId)
                                       .location(location)
                                       .quantity(1)
                                       .useHourlyPricing(true)
                                       .prices(prices)
                                       .virtualGuest(guest)
                                       .build();

       ProductOrderReceipt receipt = context.getApi().getVirtualGuestClient().orderVirtualGuest(order);
       assertNotNull(receipt);
   }


   private void checkVirtualGuest(VirtualGuest vg) {
      if (vg.getBillingItemId()==-1) return;//Quotes and shutting down guests

      assert vg.getAccountId() > 0 : vg;
      assert vg.getCreateDate() != null : vg;
      assert vg.getDomain() != null : vg;
      assert vg.getFullyQualifiedDomainName() != null : vg;
      assert vg.getHostname() != null : vg;
      assert vg.getId() > 0 : vg;
      assert vg.getMaxCpu() > 0 : vg;
      assert vg.getMaxCpuUnits() != null : vg;
      assert vg.getMaxMemory() > 0 : vg;
      assert vg.getMetricPollDate() != null : vg;
      assert vg.getModifyDate() != null : vg;
      assert vg.getStartCpus() > 0 : vg;
      assert vg.getStatusId() >= 0 : vg;
      assert vg.getUuid() != null : vg;
      assert vg.getPrimaryBackendIpAddress() != null : vg;
      assert vg.getPrimaryIpAddress() != null : vg;
   }

}
