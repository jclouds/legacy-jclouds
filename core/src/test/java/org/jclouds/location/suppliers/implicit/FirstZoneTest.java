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
package org.jclouds.location.suppliers.implicit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code FirstZone}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "FirstZoneTest")
public class FirstZoneTest {
   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("servo").description("http://servo")
         .build();
   Location region = new LocationBuilder().scope(LocationScope.REGION).id("servo-r").description("http://r.servo")
         .parent(provider).build();
   Location zone = new LocationBuilder().scope(LocationScope.ZONE).id("servo-z").description("http://z.r.servo")
         .parent(region).build();

   @Test
   public void testDidntFindZoneThrowsNSEEWithReasonableMessage() {
      Supplier<Set<? extends Location>> supplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(provider, region));
      FirstZone fn = new FirstZone(supplier);

      try {
         fn.get();
         fail("Expected NoSuchElementException");
      } catch (NoSuchElementException e) {
         assertEquals(e.getMessage(), "none to of the locations are scope ZONE: [servo:PROVIDER, servo-r:REGION]");
      }
   }

   @Test
   public void testFirstZone() {
      Supplier<Set<? extends Location>> supplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(provider, region, zone));
      FirstZone fn = new FirstZone(supplier);
      assertEquals(fn.get(), zone);
   }
}
