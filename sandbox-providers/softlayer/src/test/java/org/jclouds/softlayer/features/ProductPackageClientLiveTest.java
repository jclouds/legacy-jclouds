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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.google.common.collect.ImmutableSet;
import org.jclouds.softlayer.domain.*;
import org.jclouds.softlayer.util.SoftLayerUtils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.util.Set;

/**
 * Tests behavior of {@code ProductPackageClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class ProductPackageClientLiveTest extends BaseSoftLayerClientLiveTest {
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getProductPackageClient();
      accountClient = context.getApi().getAccountClient();
   }

   private static final String CLOUD_SERVER_PACKAGE_NAME = "Cloud Server";

   private ProductPackageClient client;
   private AccountClient accountClient;

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
      builder.add(Datacenter.builder().id(3).name("dal01").longName("Dallas").build());
      builder.add(Datacenter.builder().id(18171).name("sea01").longName("Seattle").build());
      builder.add(Datacenter.builder().id(37473).name("wdc01").longName("Washington, DC").build());
      builder.add(Datacenter.builder().id(138124).name("dal05").longName("Dallas 5").build());
      builder.add(Datacenter.builder().id(168642).name("sjc01").longName("San Jose 1").build());

      Set<Datacenter> expected = builder.build();

      Long productPackageId = SoftLayerUtils.getProductPackageId(accountClient,CLOUD_SERVER_PACKAGE_NAME);
      assertNotNull(productPackageId);

      ProductPackage productPackage = client.getProductPackage(productPackageId);
      Set<Datacenter> datacenters = productPackage.getDatacenters();
      assertEquals(datacenters.size(), expected.size());
      assertTrue(datacenters.containsAll(expected));

      for(Datacenter dataCenter: datacenters) {
         Address address = dataCenter.getLocationAddress();
         assertNotNull(address);
         checkAddress(address);
      }
   }

   private void checkProductItem(ProductItem item) {
      assert item.getId() > 0 : item;
      assert item.getDescription() != null : item;
      // units and capacity may be null

      assertTrue(item.getPrices().size() >= 0);

      for (ProductItemPrice price : item.getPrices()) {
         // ProductItemPrice newDetails =
         // client.getProductItemPrice(price.getId());
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
   }

   private void checkAddress(Address address) {
      assert address.getId() >0 : address;
      assert address.getCountry() != null : address;
      assert address.getState() != null : address;
   }
}
