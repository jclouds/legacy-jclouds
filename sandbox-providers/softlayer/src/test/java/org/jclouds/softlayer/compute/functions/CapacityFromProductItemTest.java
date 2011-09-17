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
package org.jclouds.softlayer.compute.functions;

import org.jclouds.softlayer.domain.ProductItem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.NoSuchElementException;

import static org.testng.Assert.assertEquals;

/**
 * Tests {@code DescriptionFromProductItem}
 *
 * @author Jason King
 */
@Test(groups = "unit")
public class CapacityFromProductItemTest {

   private CapacityFromProductItem function;

   @BeforeMethod
   public void setup() {
       function = new CapacityFromProductItem();
   }

   @Test
   public void testCapacity() {
       ProductItem item = ProductItem.builder()
                                      .id(1).capacity(2.0f)
                                      .build();
       assertEquals(function.apply(item),2.0f);
   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testCapacityMissing() {
       ProductItem item = ProductItem.builder()
                                      .id(1).build();
       function.apply(item);
   }
}
