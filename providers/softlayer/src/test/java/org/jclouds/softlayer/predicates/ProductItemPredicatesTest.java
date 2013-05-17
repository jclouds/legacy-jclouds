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
package org.jclouds.softlayer.predicates;

import static org.testng.Assert.assertFalse;

import java.util.regex.Pattern;

import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemCategory;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(singleThreaded = true, groups = "unit")
public class ProductItemPredicatesTest {

   private ProductItemCategory ramCategory;
   private ProductItem item;
   private ProductItem emptyItem;

   @BeforeGroups(groups = { "unit" })
   public void setupContext() {
      ramCategory = ProductItemCategory.builder().id(1).categoryCode("ram").build();

      item = ProductItem.builder().id(1).description("a test item").categories(ImmutableSet.of(ramCategory)).capacity(
               2.0f).units("GB").build();

      emptyItem = ProductItem.builder().id(1).build();
   }

   @Test
   public void testCategoryCodePresent() {
      assert ProductItemPredicates.categoryCode("ram").apply(item);
   }

   @Test
   public void testCategoryCodePresentTwoCategories() {
      ProductItemCategory osCategory = ProductItemCategory.builder().id(2).categoryCode("os").build();

      ProductItem item = ProductItem.builder().categories(ImmutableSet.of(ramCategory, osCategory)).build();

      assert ProductItemPredicates.categoryCode("ram").apply(item);
   }

   @Test
   public void testCategoryCodeMissing() {
      assertFalse(ProductItemPredicates.categoryCode("missing").apply(emptyItem));
   }
   
   @Test
   public void testCategoryCodeMatches() {
      ProductItemPredicates.categoryCodeMatches(Pattern.compile("ra.*")).apply(item);
   }


   @Test
   public void testCapacityPresent() {
      assert ProductItemPredicates.capacity(2.0f).apply(item);
   }

   @Test
   public void testCapacityMissing() {
      assertFalse(ProductItemPredicates.capacity(1.0f).apply(item));
   }

   @Test
   public void testUnitsPresent() {
      assert ProductItemPredicates.units("GB").apply(item);
   }

   @Test
   public void testUnitsMissing() {
      assertFalse(ProductItemPredicates.units("Kg").apply(item));
   }

   @Test
   public void testMatchesRegex() {
      assert ProductItemPredicates.matches(Pattern.compile(".*test.*")).apply(item);
   }

   @Test
   public void testNoMatchRegex() {
      assertFalse(ProductItemPredicates.matches(Pattern.compile("no match")).apply(item));
   }
}
