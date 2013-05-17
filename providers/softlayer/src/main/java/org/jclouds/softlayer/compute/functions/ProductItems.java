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

import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemCategory;
import org.jclouds.softlayer.domain.ProductItemPrice;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class ProductItems {

   /**
    * Creates a function to get the capacity from a product item.
    */
   public static Function<ProductItem, Float> capacity() {
      return new Function<ProductItem, Float>() {
         @Override
         public Float apply(ProductItem productItem) {
            return productItem.getCapacity();
         }
      };
   }

   /**
    * Creates a function to get the description from a product item.
    */
   public static Function<ProductItem, String> description() {
      return new Function<ProductItem, String>() {
         @Override
         public String apply(ProductItem productItem) {
            return productItem.getDescription();
         }
      };
   }

   /**
    * Creates a function to get the ProductItemPrice for the ProductItem. Currently returns the
    * first prices. This will need to be changed if more than one prices is returned.
    */
   public static Function<ProductItem, ProductItemPrice> price() {
      return new Function<ProductItem, ProductItemPrice>() {
         @Override
         public ProductItemPrice apply(ProductItem productItem) {
            if (productItem.getPrices().size() < 1)
               throw new NoSuchElementException("ProductItem has no prices:" + productItem);
            return Iterables.get(productItem.getPrices(), 0);
         }
      };
   }

   /**
    * Creates a function to get the ProductItem for the ProductItemPrice. Copies the category
    * information from the prices to the item if necessary The ProductItemPrices must have
    * ProductItems.
    */
   public static Function<ProductItemPrice, ProductItem> item() {
      return new Function<ProductItemPrice, ProductItem>() {
         @Override
         public ProductItem apply(ProductItemPrice productItemPrice) {
            Set<ProductItemCategory> categories = productItemPrice.getCategories();
            ProductItem item = productItemPrice.getItem();
            ProductItem.Builder builder = productItemPrice.getItem().toBuilder();
            if (item.getCategories().size() == 0 && categories.size() != 0) {
               builder.categories(categories);
            }

            return builder.build();
         }
      };
   }
}
