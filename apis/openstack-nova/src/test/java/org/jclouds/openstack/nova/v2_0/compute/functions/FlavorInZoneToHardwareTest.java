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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.Map;
import java.util.UUID;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.openstack.nova.v2_0.compute.functions.FlavorInZoneToHardware;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.FlavorInZone;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;

/**
 * Tests the function used to transform Flavor objects into Hardware objects
 * 
 * @author Matt Stephenson, Adrian Cole
 */
@Test(testName = "FlavorInZoneToHardwareTest")
public class FlavorInZoneToHardwareTest {
   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("openstack-nova").description(
            "openstack-nova").build();
   Location zone = new LocationBuilder().id("az-1.region-a.geo-1").description("az-1.region-a.geo-1").scope(
            LocationScope.ZONE).parent(provider).build();
   Supplier<Map<String, Location>> locationIndex = Suppliers.<Map<String, Location>> ofInstance(ImmutableMap
            .<String, Location> of("az-1.region-a.geo-1", zone));

   @Test
   public void testConversionWhereLocationFound() {

      UUID id = UUID.randomUUID();

      FlavorInZone flavorInZoneToConvert = new FlavorInZone(Flavor.builder().id(id.toString())
               .name("Test Flavor " + id).ram(262144).disk(10000).vcpus(16).build(), "az-1.region-a.geo-1");

      Hardware converted = new FlavorInZoneToHardware(locationIndex).apply(flavorInZoneToConvert);

      Flavor flavorToConvert = flavorInZoneToConvert.getFlavor();
      assertEquals(converted.getName(), flavorToConvert.getName());
      assertEquals(converted.getId(), flavorInZoneToConvert.slashEncode());
      assertEquals(converted.getProviderId(), flavorToConvert.getId());
      assertEquals(converted.getLocation(), locationIndex.get().get("az-1.region-a.geo-1"));

      assertEquals(converted.getRam(), flavorToConvert.getRam());

      assertNotNull(converted.getProcessors());
      assertFalse(converted.getProcessors().isEmpty());
      assertEquals(converted.getProcessors().iterator().next().getCores(), (double) flavorToConvert.getVcpus());

      assertNotNull(converted.getVolumes());
      assertFalse(converted.getVolumes().isEmpty());
      assertEquals(converted.getVolumes().iterator().next().getSize(), Float.valueOf(flavorToConvert.getDisk()));

   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testConversionWhereLocationNotFound() {

      UUID id = UUID.randomUUID();

      FlavorInZone flavorInZoneToConvert = new FlavorInZone(Flavor.builder().id(id.toString())
               .name("Test Flavor " + id).ram(262144).disk(10000).vcpus(16).build(), "South");
      new FlavorInZoneToHardware(locationIndex).apply(flavorInZoneToConvert);
   }

}
