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

import java.util.Map;
import java.util.UUID;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.openstack.nova.v2_0.compute.config.NovaComputeServiceContextModule;
import org.jclouds.openstack.nova.v2_0.compute.functions.ImageInZoneToImage;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ImageInZone;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;

/**
 * Tests the function that transforms nova-specific images to generic images.
 * 
 * @author Matt Stephenson, Adrian Cole
 */
@Test(testName = "ImageInZoneToHardwareTest")
public class ImageInZoneToImageTest {

   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("openstack-nova").description(
            "openstack-nova").build();
   Location zone = new LocationBuilder().id("az-1.region-a.geo-1").description("az-1.region-a.geo-1").scope(
            LocationScope.ZONE).parent(provider).build();
   Supplier<Map<String, Location>> locationIndex = Suppliers.<Map<String, Location>> ofInstance(ImmutableMap
            .<String, Location> of("az-1.region-a.geo-1", zone));
   
   @Test
   public void testConversionWhereLocationFound() {
      UUID id = UUID.randomUUID();
      Image novaImageToConvert = Image.builder().id(id.toString()).name("Test Image " + id).status(Image.Status.DELETED).build();
      OperatingSystem operatingSystem = new OperatingSystem(OsFamily.UBUNTU, "My Test OS", "My Test Version", "x86",
               "My Test OS", true);
      ImageInZoneToImage converter = new ImageInZoneToImage(NovaComputeServiceContextModule.toPortableImageStatus,
               constant(operatingSystem), locationIndex);

      ImageInZone novaImageInZoneToConvert = new ImageInZone(novaImageToConvert, "az-1.region-a.geo-1");

      org.jclouds.compute.domain.Image convertedImage = converter.apply(novaImageInZoneToConvert);

      assertEquals(convertedImage.getId(), novaImageInZoneToConvert.slashEncode());
      assertEquals(convertedImage.getProviderId(), novaImageToConvert.getId());
      assertEquals(convertedImage.getLocation(), locationIndex.get().get("az-1.region-a.geo-1"));

      assertEquals(convertedImage.getName(), novaImageToConvert.getName());
      assertEquals(convertedImage.getStatus(), org.jclouds.compute.domain.Image.Status.DELETED);
      assertEquals(convertedImage.getOperatingSystem(), operatingSystem);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testConversionWhereLocationNotFound() {
      UUID id = UUID.randomUUID();
      Image novaImageToConvert = Image.builder().id(id.toString()).name("Test Image " + id).build();
      OperatingSystem operatingSystem = new OperatingSystem(OsFamily.UBUNTU, "My Test OS", "My Test Version", "x86",
               "My Test OS", true);
      ImageInZoneToImage converter = new ImageInZoneToImage(NovaComputeServiceContextModule.toPortableImageStatus,
               constant(operatingSystem), locationIndex);

      ImageInZone novaImageInZoneToConvert = new ImageInZone(novaImageToConvert, "South");

      converter.apply(novaImageInZoneToConvert);
   }

   @SuppressWarnings("unchecked")
   private static Function<Image, OperatingSystem> constant(OperatingSystem operatingSystem){
      return Function.class.cast(Functions.constant(operatingSystem));
   }
}
