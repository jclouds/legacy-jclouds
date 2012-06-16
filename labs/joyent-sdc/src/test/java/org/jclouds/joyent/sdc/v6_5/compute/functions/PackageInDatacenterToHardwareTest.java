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
package org.jclouds.joyent.sdc.v6_5.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.Map;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.joyent.sdc.v6_5.domain.Package;
import org.jclouds.joyent.sdc.v6_5.domain.datacenterscoped.PackageInDatacenter;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;

/**
 * Tests the function used to transform Package objects into Hardware objects
 * 
 * @author Adrian Cole
 */
@Test(testName = "PackageInDatacenterToHardwareTest")
public class PackageInDatacenterToHardwareTest {
   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("joyent-sdc").description(
            "joyent-sdc").build();
   Location zone = new LocationBuilder().id("us-sw-1").description("us-sw-1").scope(
            LocationScope.ZONE).parent(provider).build();
   Supplier<Map<String, Location>> locationIndex = Suppliers.<Map<String, Location>> ofInstance(ImmutableMap
            .<String, Location> of("us-sw-1", zone));
   
   Package packageToConvert = org.jclouds.joyent.sdc.v6_5.domain.Package.builder().name("Small 1GB").memorySizeMb(1024)
         .diskSizeGb(30720).swapSizeMb(2048).isDefault(true).build();

   @Test
   public void testConversionWhereLocationFound() {

      PackageInDatacenter packageInZoneToConvert = new PackageInDatacenter(packageToConvert, "us-sw-1");

      Hardware converted = new PackageInDatacenterToHardware(locationIndex).apply(packageInZoneToConvert);

      assertEquals(converted.getName(), packageToConvert.getName());
      assertEquals(converted.getId(), packageInZoneToConvert.slashEncode());
      assertEquals(converted.getProviderId(), packageToConvert.getName());
      assertEquals(converted.getLocation(), locationIndex.get().get("us-sw-1"));

      assertEquals(converted.getRam(), packageToConvert.getMemorySizeMb());

//TODO!
//      assertNotNull(converted.getProcessors());
//      assertFalse(converted.getProcessors().isEmpty());
//      assertEquals(converted.getProcessors().iterator().next().getCores(), (double) packageToConvert.getVcpus());

      assertNotNull(converted.getVolumes());
      assertFalse(converted.getVolumes().isEmpty());
      assertEquals(converted.getVolumes().iterator().next().getSize(), Float.valueOf(packageToConvert.getDiskSizeGb()));

   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testConversionWhereLocationNotFound() {

      PackageInDatacenter packageInZoneToConvert = new PackageInDatacenter(packageToConvert, "South");
      new PackageInDatacenterToHardware(locationIndex).apply(packageInZoneToConvert);
   }

}
