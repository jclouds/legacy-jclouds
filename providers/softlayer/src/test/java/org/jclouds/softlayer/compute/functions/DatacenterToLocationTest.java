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
package org.jclouds.softlayer.compute.functions;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.net.URI;
import java.util.Set;

import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.all.JustProvider;
import org.jclouds.softlayer.domain.Address;
import org.jclouds.softlayer.domain.Datacenter;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;

/**
 * Tests {@code DatacenterToLocation}
 * 
 * @author Jason King
 */
@Test(singleThreaded = true, groups = "unit")
public class DatacenterToLocationTest {

   static DatacenterToLocation function = new DatacenterToLocation(new JustProvider("softlayer", Suppliers
            .ofInstance(URI.create("foo")), ImmutableSet.<String> of()));

   @Test
   public void testDatacenterToLocation() {
      Address address = Address.builder().country("US").state("TX").description("This is Texas!").build();

      Datacenter datacenter = Datacenter.builder().id(1).longName("Texas Datacenter").locationAddress(address).build();

      Location location = function.apply(datacenter);

      assertEquals(location.getId(), Long.toString(datacenter.getId()));
      Set<String> iso3166Codes = location.getIso3166Codes();
      assertEquals(iso3166Codes.size(), 1);
      assertTrue(iso3166Codes.contains("US-TX"));
   }

   @Test
   public void testGetIso3166CodeNoCountryAndState() {
      Datacenter datacenter = Datacenter.builder().id(1).longName("Nowhere").build();

      Location location = function.apply(datacenter);

      assertEquals(location.getId(), Long.toString(datacenter.getId()));
      Set<String> iso3166Codes = location.getIso3166Codes();
      assertEquals(iso3166Codes.size(), 0);
   }

   @Test
   public void testGetIso3166CodeCountryOnly() {
      Address address = Address.builder().country("US").description("This is North America!").build();

      Datacenter datacenter = Datacenter.builder().id(1).longName("Nowhere").locationAddress(address).build();

      Location location = function.apply(datacenter);

      assertEquals(location.getId(), Long.toString(datacenter.getId()));
      Set<String> iso3166Codes = location.getIso3166Codes();
      assertEquals(iso3166Codes.size(), 1);
      assertTrue(iso3166Codes.contains("US"));
   }

   @Test
   public void testGetIso3166CodeWhitespaceTrimmer() {
      Address address = Address.builder().country(" US ").state("  TX  ").description("This is spaced out Texas")
            .build();

      Datacenter datacenter = Datacenter.builder().id(1).longName("Nowhere").locationAddress(address).build();

      Location location = function.apply(datacenter);

      assertEquals(location.getId(), Long.toString(datacenter.getId()));
      Set<String> iso3166Codes = location.getIso3166Codes();
      assertEquals(iso3166Codes.size(), 1);
      assertTrue(iso3166Codes.contains("US-TX"));
   }
}
