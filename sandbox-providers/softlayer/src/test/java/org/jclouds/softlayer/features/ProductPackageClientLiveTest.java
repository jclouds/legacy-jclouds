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

import static org.testng.Assert.assertTrue;

import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.ProductPackage;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

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
   }

   private ProductPackageClient client;

   @Test
   public void testGetProductPackage() {
      ProductPackage response = client.getProductPackage(46);
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

}
