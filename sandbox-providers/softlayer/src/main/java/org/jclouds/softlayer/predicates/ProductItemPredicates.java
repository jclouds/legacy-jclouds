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
package org.jclouds.softlayer.predicates;

import com.google.common.base.Predicate;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemCategory;

public class ProductItemPredicates {

   /**
    * Tests if the ProductItem contains the required category.
    * @param category
    * @return true if it does, otherwise false.
    */
    public static Predicate<ProductItem> categoryCode(final String category) {
        return new Predicate<ProductItem>() {
            @Override
            public boolean apply(ProductItem productItem) {
                for(ProductItemCategory productItemCategory: productItem.getCategories()) {
                    if(category.equals(productItemCategory.getCategoryCode())) return true;
                }
                return false;
            }

            @Override
            public String toString() {
                return "categoryCode("+category+")";
            }
        };
    }

    /**
    * Tests if the ProductItem has the required capacity.
    * @param capacity
    * @return true if it does, otherwise false.
    */
    public static Predicate<ProductItem> capacity(final Float capacity) {
        return new Predicate<ProductItem>() {
            @Override
            public boolean apply(ProductItem productItem) {
                Float productItemCapacity = productItem.getCapacity();
                if (productItemCapacity == null) return false;
                return productItemCapacity.equals(capacity);
            }

            @Override
            public String toString() {
                return "capacity("+capacity+")";
            }
        };
    }
}
