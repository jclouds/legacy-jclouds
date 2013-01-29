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
package org.jclouds.route53.options;

import static org.jclouds.route53.options.ListZonesOptions.Builder.afterMarker;
import static org.jclouds.route53.options.ListZonesOptions.Builder.maxItems;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code ListZonesOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ListZonesOptionsTest")
public class ListZonesOptionsTest {

   public void testMarker() {
      ListZonesOptions options = new ListZonesOptions().afterMarker("FFFFF");
      assertEquals(ImmutableSet.of("FFFFF"), options.buildQueryParameters().get("marker"));
   }

   public void testMarkerStatic() {
      ListZonesOptions options = afterMarker("FFFFF");
      assertEquals(ImmutableSet.of("FFFFF"), options.buildQueryParameters().get("marker"));
   }

   public void testMaxItems() {
      ListZonesOptions options = new ListZonesOptions().maxItems(1000);
      assertEquals(ImmutableSet.of("1000"), options.buildQueryParameters().get("maxitems"));
   }

   public void testMaxItemsStatic() {
      ListZonesOptions options = maxItems(1000);
      assertEquals(ImmutableSet.of("1000"), options.buildQueryParameters().get("maxitems"));
   }
}
