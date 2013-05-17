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

import static org.jclouds.softlayer.predicates.ProductItemPredicates.capacity;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.units;
import static org.jclouds.softlayer.predicates.ProductPackagePredicates.named;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.compute.functions.ProductItems;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.ProductOrder;
import org.jclouds.softlayer.domain.ProductOrderReceipt;
import org.jclouds.softlayer.domain.ProductPackage;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VirtualGuestClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class VirtualGuestClientLiveTest extends BaseSoftLayerClientLiveTest {

   private static final String TEST_HOSTNAME_PREFIX = "livetest";

   @Test
   public void testListVirtualGuests() throws Exception {
      Set<VirtualGuest> response = api().listVirtualGuests();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (VirtualGuest vg : response) {
         VirtualGuest newDetails = api().getVirtualGuest(vg.getId());
         assertEquals(vg.getId(), newDetails.getId());
         checkVirtualGuest(vg);
      }
   }

   @Test(enabled = false, groups = "live")
   public void testCancelAndPlaceOrder() {

      // This method was not working needs testing out.

      // TODO: Should also check if there are active transactions before trying to cancel.
      // objectMask: virtualGuests.activeTransaction
      for (VirtualGuest guest : api().listVirtualGuests()) {
         if (guest.getHostname().startsWith(TEST_HOSTNAME_PREFIX)) {
            if (guest.getBillingItemId() != -1) {
               api().cancelService(guest.getBillingItemId());
            }
         }
      }

      int pkgId = Iterables.find(api.getAccountClient().getActivePackages(),
               named(ProductPackageClientLiveTest.CLOUD_SERVER_PACKAGE_NAME)).getId();
      ProductPackage productPackage = api.getProductPackageClient().getProductPackage(pkgId);

      Iterable<ProductItem> ramItems = Iterables.filter(productPackage.getItems(), Predicates.and(categoryCode("ram"),
               capacity(2.0f)));

      Map<Float, ProductItem> ramToProductItem = Maps.uniqueIndex(ramItems, ProductItems.capacity());

      ProductItemPrice ramPrice = ProductItems.price().apply(ramToProductItem.get(2.0f));

      Iterable<ProductItem> cpuItems = Iterables.filter(productPackage.getItems(), Predicates.and(
               units("PRIVATE_CORE"), capacity(2.0f)));
      Map<Float, ProductItem> coresToProductItem = Maps.uniqueIndex(cpuItems, ProductItems.capacity());

      ProductItemPrice cpuPrice = ProductItems.price().apply(coresToProductItem.get(2.0f));

      Iterable<ProductItem> operatingSystems = Iterables.filter(productPackage.getItems(), categoryCode("os"));
      Map<String, ProductItem> osToProductItem = Maps.uniqueIndex(operatingSystems, ProductItems.description());
      ProductItemPrice osPrice = ProductItems.price().apply(
               osToProductItem.get("Ubuntu Linux 8 LTS Hardy Heron - Minimal Install (64 bit)"));

      Builder<ProductItemPrice> prices = ImmutableSet.builder();
      prices.addAll(defaultPrices);
      prices.add(ramPrice);
      prices.add(cpuPrice);
      prices.add(osPrice);

      VirtualGuest guest = VirtualGuest.builder().domain("jclouds.org").hostname(
               TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();

      ProductOrder order = ProductOrder.builder().packageId(pkgId).quantity(1).useHourlyPricing(true).prices(
               prices.build()).virtualGuests(guest).build();

      ProductOrderReceipt receipt = api().orderVirtualGuest(order);
      ProductOrder order2 = receipt.getOrderDetails();
      VirtualGuest result = Iterables.get(order2.getVirtualGuests(), 0);

      ProductOrder order3 = api().getOrderTemplate(result.getId());

      assertEquals(order.getPrices(), order3.getPrices());
      assertNotNull(receipt);
   }

   private Iterable<ProductItemPrice> defaultPrices;

   @Override
   protected SoftLayerClient create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      defaultPrices = injector.getInstance(Key.get(new TypeLiteral<Iterable<ProductItemPrice>>() {
      }));
      return injector.getInstance(SoftLayerClient.class);
   }

   private VirtualGuestClient api() {
      return api.getVirtualGuestClient();
   }

   private void checkVirtualGuest(VirtualGuest vg) {
      if (vg.getBillingItemId() == -1)
         return;// Quotes and shutting down guests

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
