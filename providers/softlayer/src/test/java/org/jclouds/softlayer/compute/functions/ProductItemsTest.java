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
package org.jclouds.softlayer.compute.functions;

import static org.jclouds.softlayer.compute.functions.ProductItems.capacity;
import static org.jclouds.softlayer.compute.functions.ProductItems.description;
import static org.jclouds.softlayer.compute.functions.ProductItems.item;
import static org.jclouds.softlayer.compute.functions.ProductItems.price;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.NoSuchElementException;

import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemCategory;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests {@code ProductItems}
 *
 * @author Jason King
 */
@Test(groups = "unit")
public class ProductItemsTest {

   private ProductItemCategory category;
   private ProductItemPrice price;
   private ProductItem item;

   @BeforeMethod
   public void setup() {

       category = ProductItemCategory.builder().id(1).categoryCode("category").build();

       price = ProductItemPrice.builder().id(1).build();

       item = ProductItem.builder().id(1)
                                   .capacity(2.0f)
                                   .description("an item")
                                   .prices(price)
                                   .build();
   }

   @Test
   public void testCapacity() {
       assertEquals(capacity().apply(item), 2.0f);
   }

   @Test
   public void testCapacityMissing() {
       ProductItem item = ProductItem.builder().id(1).build();
       assertNull(capacity().apply(item));
   }

   @Test
   public void testDescription() {
       assertEquals(description().apply(item),"an item");
   }

   @Test
   public void testDescriptionMissing() {
       ProductItem item = ProductItem.builder().id(1).build();
       assertNull(description().apply(item));
   }

   @Test
   public void testPrice() {
      assertEquals(price().apply(item),price);
   }

   @Test
   public void testPriceMultiplePrices() {
       ImmutableSet<ProductItemPrice> prices = ImmutableSet.of(price, ProductItemPrice.builder().id(2).build());
       ProductItem item2 = ProductItem.builder().prices(prices).build();
       assertEquals(price().apply(item2),price);
   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testPriceMissing() {
      ProductItem noPriceItem = ProductItem.builder().id(1).build();
      price().apply(noPriceItem);
   }
   
   @Test
   public void testItemCallGetsCategory() {
      ProductItemPrice price = ProductItemPrice.builder().id(1)
                                                         .categories(category)
                                                         .item(item)
                                                         .build();
      ProductItem newItem = item().apply(price);
      assertEquals(newItem.getCategories(), ImmutableSet.of(category));
   }

   @Test
   public void testItemCallNoCategoryOnPrice() {

      ProductItem item1 = item.toBuilder().categories(ImmutableSet.of(category)).build();

      ProductItemPrice price = ProductItemPrice.builder().id(1)
                                                         .item(item1)
                                                         .build();
      ProductItem newItem = item().apply(price);
      assertEquals(newItem.getCategories(), ImmutableSet.of(category));
   }

   @Test
   public void testItemCallCategoryExists() {

      ProductItemCategory category2 = ProductItemCategory.builder()
            .id(12)
            .categoryCode("new category")
            .build();

      ProductItem item1 = item.toBuilder().categories(ImmutableSet.of(category2)).build();

      ProductItemPrice price = ProductItemPrice.builder().id(1)
                                                         .categories(category)
                                                         .item(item1)
                                                         .build();
      ProductItem newItem = item().apply(price);
      assertEquals(newItem.getCategories(), ImmutableSet.of(category2));
   }


}
