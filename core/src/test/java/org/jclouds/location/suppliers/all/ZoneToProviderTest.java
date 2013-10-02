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
package org.jclouds.location.suppliers.all;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ZoneToProviderTest")
public class ZoneToProviderTest {
   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("servo").description("http://servo")
            .iso3166Codes(ImmutableSet.of("US")).build();
   JustProvider justProvider = new JustProvider("servo", Suppliers.ofInstance(URI.create("http://servo")), ImmutableSet.of("US"));

   @Test
   public void test() {
      Supplier<Set<String>> zoneIdsSupplier = Suppliers.<Set<String>>ofInstance(ImmutableSet.of("zone1", "zone2"));
      Supplier<Map<String, Supplier<Set<String>>>> locationToIsoCodes = Suppliers.<Map<String, Supplier<Set<String>>>>ofInstance(
               ImmutableMap.of(
                        "servo", Suppliers.<Set<String>>ofInstance(ImmutableSet.of("US")),
                        "zone1", Suppliers.<Set<String>>ofInstance(ImmutableSet.of("US-CA")),
                        "zone2", Suppliers.<Set<String>>ofInstance(ImmutableSet.of("US-VA"))
                        ));
      ZoneToProvider fn = new ZoneToProvider(justProvider, zoneIdsSupplier, locationToIsoCodes);
      
      assertEquals(fn.get(), ImmutableSet.of(
               new LocationBuilder().scope(LocationScope.ZONE).id("zone1").description("zone1").iso3166Codes(ImmutableSet.of("US-CA")).parent(provider).build(),
               new LocationBuilder().scope(LocationScope.ZONE).id("zone2").description("zone2").iso3166Codes(ImmutableSet.of("US-VA")).parent(provider).build()
               ));
   }
   
   @Test(expectedExceptions = IllegalStateException.class)
   public void testWhenNoZones() {
      Supplier<Set<String>> zoneIdsSupplier = Suppliers.<Set<String>>ofInstance(ImmutableSet.<String>of());
      Supplier<Map<String, Supplier<Set<String>>>> locationToIsoCodes = Suppliers.<Map<String, Supplier<Set<String>>>>ofInstance(
               ImmutableMap.<String, Supplier<Set<String>>>of());
      ZoneToProvider fn = new ZoneToProvider(justProvider, zoneIdsSupplier, locationToIsoCodes);
      fn.get();
   }
}
