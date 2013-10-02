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
import static org.testng.Assert.assertTrue;

import org.jclouds.softlayer.domain.ProductPackage;
import org.testng.annotations.Test;

/**
 * Tests {@code ProductPackagePredicates}
 *
 * @author Jason King
 */
@Test(sequential = true,groups = "unit")
public class ProductPackagePredicatesTest {

   @Test
   public void testMatches() {
      ProductPackage productPackage = ProductPackage.builder().name("foo").build();
      assertTrue(ProductPackagePredicates.named("foo").apply(productPackage));
   }

    @Test
   public void testDoesNotMatch() {
      ProductPackage productPackage = ProductPackage.builder().name("foo").build();
      assertFalse(ProductPackagePredicates.named("bar").apply(productPackage));
   }
}
