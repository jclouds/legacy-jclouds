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
package org.jclouds.cloudstack.compute.functions;

import static org.testng.AssertJUnit.assertEquals;

import java.net.URI;
import java.util.Set;

import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.parse.ListZonesResponseTest;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.suppliers.all.JustProvider;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests {@code ZoneToLocation}
 * 
 * @author Jason King
 */
@Test(singleThreaded = true, groups = "unit")
public class ZoneToLocationTest {

   static JustProvider justProvider = new JustProvider("cloudstack", Suppliers.ofInstance(URI.create("foo")),
            ImmutableSet.<String> of());
   static ZoneToLocation function = new ZoneToLocation(justProvider);
   static Location one = new LocationBuilder().parent(Iterables.get(justProvider.get(), 0)).scope(LocationScope.ZONE)
         .description("San Jose 1").id("1").build();
   static Location two = new LocationBuilder().parent(Iterables.get(justProvider.get(), 0)).scope(LocationScope.ZONE)
         .description("Chicago").id("2").build();

   @Test
   public void test() {

      Set<Location> expected = ImmutableSet.of(one, two);

      Set<Zone> zones = new ListZonesResponseTest().expected();

      Iterable<Location> locations = Iterables.transform(zones, function);

      assertEquals(locations.toString(), expected.toString());
   }
}
