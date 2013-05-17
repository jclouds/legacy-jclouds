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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.jclouds.softlayer.compute.functions.ProductItems;
import org.jclouds.softlayer.domain.Address;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemCategory;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.ProductPackage;
import org.jclouds.softlayer.domain.Region;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Tests behavior of {@code ProductPackageClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "ProductPackageClientLiveTest")
public class ProductPackageClientLiveTest extends BaseSoftLayerClientLiveTest {

   /**
    * Name of the package used for ordering virtual guests. For real this is
    * passed in using the property
    * 
    * @{code org.jclouds.softlayer.reference.SoftLayerConstants.
    *        PROPERTY_SOFTLAYER_VIRTUALGUEST_PACKAGE_NAME}
    */
   public static final String CLOUD_SERVER_PACKAGE_NAME = "Cloud Server";

   @BeforeGroups(groups = { "live" })
   public void setup() {
      super.setup();
      client = api.getProductPackageClient();
      accountClient = api.getAccountClient();

      // This is used several times, so cache to speed up the test.
      cloudServerPackageId = Iterables.find(accountClient.getActivePackages(), named(CLOUD_SERVER_PACKAGE_NAME))
            .getId();
      cloudServerProductPackage = client.getProductPackage(cloudServerPackageId);
   }

   private ProductPackageClient client;
   private AccountClient accountClient;

   private int cloudServerPackageId;
   private ProductPackage cloudServerProductPackage;

   @Test
   public void testGetProductPackage() {
      for (ProductPackage productPackage : accountClient.getActivePackages()) {
         ProductPackage response = client.getProductPackage(productPackage.getId());

         assert null != response;
         assert response.getId() > 0 : response;
         assert response.getName() != null : response;
         assert response.getDescription() != null : response;

         assertTrue(response.getItems().size() >= 0);
         for (ProductItem item : response.getItems()) {
            // ProductItem newDetails = client.getProductItem(item.getId());
            // assertEquals(item.getId(), newDetails.getId());
            checkProductItem(item);
         }

         assertTrue(response.getDatacenters().size() > 0);
         for (Datacenter datacenter : response.getDatacenters()) {
            checkDatacenter(datacenter);
         }
      }
   }

   @Test
   public void testDatacentersForCloudLayer() {

      ImmutableSet.Builder<Datacenter> builder = ImmutableSet.builder();
      builder.add(Datacenter.builder().id(18171).name("sea01").longName("Seattle").build());
      builder.add(Datacenter.builder().id(37473).name("wdc01").longName("Washington, DC").build());
      builder.add(Datacenter.builder().id(138124).name("dal05").longName("Dallas 5").build());
      builder.add(Datacenter.builder().id(168642).name("sjc01").longName("San Jose 1").build());
      builder.add(Datacenter.builder().id(224092).name("sng01").longName("Singapore 1").build());
      builder.add(Datacenter.builder().id(265592).name("ams01").longName("Amsterdam 1").build());

      Set<Datacenter> expected = builder.build();

      Set<Datacenter> datacenters = cloudServerProductPackage.getDatacenters();
      assert datacenters.size() == expected.size() : datacenters;
      assertTrue(datacenters.containsAll(expected));

      for (Datacenter dataCenter : datacenters) {
         Address address = dataCenter.getLocationAddress();
         assertNotNull(address);
         checkAddress(address);
      }
   }

   @Test
   public void testGetOneGBRamPrice() {
      // Predicate p =
      // Predicates.and(ProductItemPredicates.categoryCode("ram"),ProductItemPredicates.capacity(1.0f));
      Iterable<ProductItem> ramItems = Iterables.filter(cloudServerProductPackage.getItems(),
            Predicates.and(categoryCode("ram"), capacity(1.0f)));

      // capacity is key in GB (1Gb = 1.0f)
      Map<Float, ProductItem> ramToProductItem = Maps.uniqueIndex(ramItems, ProductItems.capacity());

      ProductItemPrice price = ProductItems.price().apply(ramToProductItem.get(1.0f));
      assert Integer.valueOf(1644).equals(price.getId());
   }

   @Test
   public void testGetTwoCPUCoresPrice() {
      // If use ProductItemPredicates.categoryCode("guest_core") get duplicate
      // capacities (units =
      // PRIVATE_CORE and N/A)
      Iterable<ProductItem> cpuItems = Iterables.filter(cloudServerProductPackage.getItems(),
            Predicates.and(units("PRIVATE_CORE"), capacity(2.0f)));

      // number of cores is the key
      Map<Float, ProductItem> coresToProductItem = Maps.uniqueIndex(cpuItems, ProductItems.capacity());

      ProductItemPrice price = ProductItems.price().apply(coresToProductItem.get(2.0f));
      assert Integer.valueOf(1963).equals(price.getId());
   }

   @Test
   public void testGetUbuntuPrice() {
      Iterable<ProductItem> operatingSystems = Iterables.filter(cloudServerProductPackage.getItems(),
            categoryCode("os"));

      Map<String, ProductItem> osToProductItem = Maps.uniqueIndex(operatingSystems, ProductItems.description());

      ProductItemPrice price = ProductItems.price().apply(
            osToProductItem.get("Ubuntu Linux 8 LTS Hardy Heron - Minimal Install (64 bit)"));
      assert Integer.valueOf(1693).equals(price.getId());
   }

   private void checkProductItem(ProductItem item) {
      assert item.getId() > 0 : item;
      assert item.getDescription() != null : item;
      checkCategories(item.getCategories());
      // units and capacity may be null

      assertTrue(item.getPrices().size() >= 0);

      for (ProductItemPrice price : item.getPrices()) {
         // ProductItemPrice newDetails =
         // client.getProductItemPrice(prices.getId());
         // assertEquals(item.getId(), newDetails.getId());
         checkPrice(price);
      }
   }

   private void checkPrice(ProductItemPrice price) {
      assert price.getId() > 0 : price;
      assert price.getItemId() > 0 : price;
      assert price.getRecurringFee() != null || price.getHourlyRecurringFee() != null : price;
   }

   private void checkDatacenter(Datacenter datacenter) {
      assert datacenter.getId() > 0 : datacenter;
      assert datacenter.getName() != null : datacenter;
      assert datacenter.getLongName() != null : datacenter;
      for (Region region : datacenter.getRegions())
         checkRegion(region);
   }

   private void checkRegion(Region region) {
      assert !region.getDescription().isEmpty() : region;
      assert !region.getKeyname().isEmpty() : region;
   }

   private void checkAddress(Address address) {
      assert address.getId() > 0 : address;
      assert address.getCountry() != null : address;
      if (!ImmutableSet.of("SG", "NL").contains(address.getCountry()))
         assert address.getState() != null : address;
   }

   private void checkCategories(Set<ProductItemCategory> categories) {
      for (ProductItemCategory category : categories) {
         assert category.getId() > 0 : category;
         assert category.getName() != null : category;
         assert category.getCategoryCode() != null : category;
      }
   }
}
