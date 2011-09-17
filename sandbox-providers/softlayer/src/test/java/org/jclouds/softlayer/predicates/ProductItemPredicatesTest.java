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

import com.google.common.collect.ImmutableSet;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemCategory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

@Test(sequential = true,groups = "unit")
public class ProductItemPredicatesTest {

    @Test
    public void testCategoryCodePresent() {
        ProductItemCategory category = ProductItemCategory.builder()
                                            .id(1).categoryCode("ram")
                                            .build();

        ProductItem item = ProductItem.builder()
                                      .categories(ImmutableSet.of(category))
                                      .build();

        assert ProductItemPredicates.categoryCode("ram").apply(item);
    }

    @Test
    public void testCategoryCodePresentTwoCategories() {
        ProductItemCategory category1 = ProductItemCategory.builder()
                                            .id(1).categoryCode("os")
                                            .build();

        ProductItemCategory category2 = ProductItemCategory.builder()
                                            .id(2).categoryCode("ram")
                                            .build();

        ProductItem item = ProductItem.builder()
                                      .categories(ImmutableSet.of(category1, category2))
                                      .build();

        assert ProductItemPredicates.categoryCode("ram").apply(item);
    }

    @Test
    public void testCategoryCodeMissing() {
        ProductItem item = ProductItem.builder()
                                      .categories(ImmutableSet.<ProductItemCategory>of())
                                      .build();

        assertFalse(ProductItemPredicates.categoryCode("ram").apply(item));
    }

    @Test
    public void testCapacity() {
        ProductItem item = ProductItem.builder()
                                      .id(1).capacity(2.0f)
                                      .build();

        assert ProductItemPredicates.capacity(2.0f).apply(item);
    }

    @Test
    public void testCapacityDifferent() {
        ProductItem item = ProductItem.builder()
                                      .id(1).capacity(2.0f)
                                      .build();

        assertFalse(ProductItemPredicates.capacity(1.0f).apply(item));
    }

    @Test
    public void testCapacityMissing() {
        ProductItem item = ProductItem.builder()
                                      .id(1).build();

        assertFalse(ProductItemPredicates.capacity(2.0f).apply(item));
    }
}
