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

import java.util.Map;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.joyent.sdc.v6_5.domain.Dataset;
import org.jclouds.joyent.sdc.v6_5.domain.datacenterscoped.DatasetInDatacenter;
import org.jclouds.joyent.sdc.v6_5.parse.ParseDatasetTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;

/**
 * Tests the function that transforms sdc-specific images to generic images.
 * 
 * @author Adrian Cole
 */
@Test(testName = "DatasetInDatacenterToHardwareTest")
public class DatasetInDatacenterToImageTest {

   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("joyent-sdc")
         .description("joyent-sdc").build();
   Location zone = new LocationBuilder().id("us-sw-1").description("us-sw-1").scope(LocationScope.ZONE)
         .parent(provider).build();
   Supplier<Map<String, Location>> locationIndex = Suppliers.<Map<String, Location>> ofInstance(ImmutableMap
         .<String, Location> of("us-sw-1", zone));

   Dataset datasetToConvert = new ParseDatasetTest().expected();

   @Test
   public void testConversionWhereLocationFound() {
      OperatingSystem operatingSystem = new OperatingSystem(OsFamily.UBUNTU, "My Test OS", "My Test Version", "x86",
            "My Test OS", true);
      DatasetInDatacenterToImage converter = new DatasetInDatacenterToImage(constant(operatingSystem), locationIndex);

      DatasetInDatacenter datasetInZoneToConvert = new DatasetInDatacenter(datasetToConvert, "us-sw-1");

      org.jclouds.compute.domain.Image convertedImage = converter.apply(datasetInZoneToConvert);

      assertEquals(convertedImage.getId(), datasetInZoneToConvert.slashEncode());
      assertEquals(convertedImage.getProviderId(), datasetToConvert.getId());
      assertEquals(convertedImage.getLocation(), locationIndex.get().get("us-sw-1"));

      assertEquals(convertedImage.getName(), datasetToConvert.getName());
      assertEquals(convertedImage.getStatus(), org.jclouds.compute.domain.Image.Status.AVAILABLE);
      assertEquals(convertedImage.getOperatingSystem(), operatingSystem);
      assertEquals(convertedImage.getDescription(), datasetToConvert.getUrn());
      assertEquals(convertedImage.getVersion(), datasetToConvert.getVersion());
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testConversionWhereLocationNotFound() {
      OperatingSystem operatingSystem = new OperatingSystem(OsFamily.UBUNTU, "My Test OS", "My Test Version", "x86",
            "My Test OS", true);
      DatasetInDatacenterToImage converter = new DatasetInDatacenterToImage(constant(operatingSystem), locationIndex);

      DatasetInDatacenter datasetInZoneToConvert = new DatasetInDatacenter(datasetToConvert, "South");

      converter.apply(datasetInZoneToConvert);
   }

   @SuppressWarnings("unchecked")
   private static Function<Dataset, OperatingSystem> constant(OperatingSystem operatingSystem) {
      return Function.class.cast(Functions.constant(operatingSystem));
   }
}
