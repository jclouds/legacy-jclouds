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
package org.jclouds.cloudstack.options;

import static org.jclouds.cloudstack.options.ListZonesOptions.Builder.available;
import static org.jclouds.cloudstack.options.ListZonesOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListZonesOptions.Builder.id;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListZonesOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListZonesOptionsTest {

   public void testId() {
      ListZonesOptions options = new ListZonesOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testAvailable() {
      ListZonesOptions options = new ListZonesOptions().available(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("available"));
   }

   public void testDomainId() {
      ListZonesOptions options = new ListZonesOptions().domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testIdStatic() {
      ListZonesOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testAvailableStatic() {
      ListZonesOptions options = available(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("available"));
   }

   public void testDomainIdStatic() {
      ListZonesOptions options = domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }
}
