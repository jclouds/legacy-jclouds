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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Pattern;

import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemCategory;

import com.google.common.base.Predicate;

public class ProductItemPredicates {

   /**
    * Tests if the ProductItem contains the required category.
    * 
    * @param category
    * @return true if it does, otherwise false.
    */
   public static Predicate<ProductItem> categoryCode(final String category) {
      checkNotNull(category, "category cannot be null");
      return new Predicate<ProductItem>() {
         @Override
         public boolean apply(ProductItem productItem) {
            checkNotNull(productItem, "productItem cannot ne null");
            for (ProductItemCategory productItemCategory : productItem.getCategories()) {
               if (category.equals(productItemCategory.getCategoryCode()))
                  return true;
            }
            return false;
         }

         @Override
         public String toString() {
            return "categoryCode(" + category + ")";
         }
      };
   }

   /**
    * Tests if the ProductItem contains a category that matches the supplied Pattern
    * 
    * @param category
    * @return true if it does, otherwise false.
    */
   public static Predicate<ProductItem> categoryCodeMatches(final Pattern category) {
      checkNotNull(category, "category cannot be null");
      return new Predicate<ProductItem>() {
         @Override
         public boolean apply(ProductItem productItem) {
            checkNotNull(productItem, "productItem cannot ne null");
            for (ProductItemCategory productItemCategory : productItem.getCategories()) {
               if (category.matcher(productItemCategory.getCategoryCode()).matches())
                  return true;
            }
            return false;
         }

         @Override
         public String toString() {
            return "categoryCodeMatches(" + category + ")";
         }
      };
   }

   /**
    * Tests if the ProductItem has the required capacity.
    * 
    * @param capacity
    * @return true if it does, otherwise false.
    */
   public static Predicate<ProductItem> capacity(final Float capacity) {
      checkNotNull(capacity, "capacity cannot be null");
      return new Predicate<ProductItem>() {
         @Override
         public boolean apply(ProductItem productItem) {
            checkNotNull(productItem, "productItem cannot ne null");
            Float productItemCapacity = productItem.getCapacity();
            if (productItemCapacity == null)
               return false;
            return capacity.equals(productItemCapacity);
         }

         @Override
         public String toString() {
            return "capacity(" + capacity + ")";
         }
      };
   }

   /**
    * Tests if the ProductItem has the required units.
    * 
    * @param units
    * @return true if it does, otherwise false.
    */
   public static Predicate<ProductItem> units(final String units) {
      checkNotNull(units, "units cannot be null");
      return new Predicate<ProductItem>() {
         @Override
         public boolean apply(ProductItem productItem) {
            checkNotNull(productItem, "productItem cannot ne null");
            return units.equals(productItem.getUnits());
         }

         @Override
         public String toString() {
            return "units(" + units + ")";
         }
      };
   }

   /**
    * Tests if the ProductItem's description matches the supplied regular expression.
    * 
    * @param regex
    *           a regular expression to match against.
    * @return true if it does, otherwise false.
    * @throws java.util.regex.PatternSyntaxException
    *            if the regex is invalid
    */
   public static Predicate<ProductItem> matches(final Pattern regex) {
      checkNotNull(regex, "regex cannot be null");

      return new Predicate<ProductItem>() {
         @Override
         public boolean apply(ProductItem productItem) {
            checkNotNull(productItem, "productItem cannot ne null");
            return regex.matcher(productItem.getDescription()).matches();
         }

         @Override
         public String toString() {
            return "regex(" + regex + ")";
         }
      };
   }
}
