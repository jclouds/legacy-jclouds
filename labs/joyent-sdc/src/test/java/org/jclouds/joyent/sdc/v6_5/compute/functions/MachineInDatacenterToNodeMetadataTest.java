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
import static org.testng.Assert.assertNotNull;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.joyent.sdc.v6_5.compute.config.SDCComputeServiceContextModule;
import org.jclouds.joyent.sdc.v6_5.domain.Machine;
import org.jclouds.joyent.sdc.v6_5.domain.datacenterscoped.MachineInDatacenter;
import org.jclouds.joyent.sdc.v6_5.parse.ParseCreatedMachineTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

/**
 * Tests for the function for transforming a sdc specific Machine into a generic
 * NodeMetadata object.
 * 
 * @author Adrian Cole
 */
@Test(testName = "MachineInDatacenterToNodeMetadataTest")
public class MachineInDatacenterToNodeMetadataTest {

   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("openstack-sdc")
         .description("openstack-sdc").build();
   Location zone = new LocationBuilder().id("us-sw-1").description("us-sw-1").scope(LocationScope.ZONE)
         .parent(provider).build();
   Supplier<Map<String, Location>> locationIndex = Suppliers.<Map<String, Location>> ofInstance(ImmutableMap
         .<String, Location> of("us-sw-1", zone));

   GroupNamingConvention.Factory namingConvention = Guice.createInjector().getInstance(
         GroupNamingConvention.Factory.class);

   @Test
   public void testWhenNoHardwareOrImageMatchImageIdIsStillSet() {

      Hardware existingHardware = new HardwareBuilder().id("us-sw-1/FOOOOOOOO").providerId("FOOOOOOOO").location(zone)
            .build();
      Image existingImage = new ImageBuilder().id("us-sw-1/FOOOOOOOO")
            .operatingSystem(OperatingSystem.builder().family(OsFamily.LINUX).description("foobuntu").build())
            .providerId("FOOOOOOOO").description("foobuntu").location(zone).status(Image.Status.AVAILABLE).build();

      checkHardwareAndImageStatus(null, existingHardware, "us-sw-1/sdc:sdc:centos-5.7:1.2.1", null, existingImage);
   }

   @Test
   public void testWhenHardwareAndImageMatchHardwareOperatingSystemAndImageIdAreSet() {

      Hardware existingHardware = new HardwareBuilder().id("us-sw-1/Small 1GB").providerId("Small 1GB").ram(1024)
            .volume(new VolumeImpl(Float.valueOf(61440), true, true)).location(zone).build();
      Image existingImage = new ImageBuilder().id("us-sw-1/sdc:sdc:centos-5.7:1.2.1")
            .operatingSystem(OperatingSystem.builder().family(OsFamily.LINUX).description("foobuntu").build())
            .providerId("sdc:sdc:centos-5.7:1.2.1").description("foobuntu").status(Image.Status.AVAILABLE)
            .location(zone).build();

      checkHardwareAndImageStatus(existingHardware, existingHardware, existingImage.getId(),
            existingImage.getOperatingSystem(), existingImage);
   }

   // TODO: clean up this syntax
   private void checkHardwareAndImageStatus(Hardware expectedHardware, Hardware existingHardware,
         String expectedImageId, OperatingSystem expectedOs, Image existingImage) {

      Set<Image> images = existingImage == null ? ImmutableSet.<Image> of() : ImmutableSet.of(existingImage);
      Set<Hardware> hardwares = existingHardware == null ? ImmutableSet.<Hardware> of() : ImmutableSet
            .of(existingHardware);
      Machine machineToConvert = new ParseCreatedMachineTest().expected();

      MachineInDatacenter machineInDatacenterToConvert = new MachineInDatacenter(machineToConvert, "us-sw-1");

      MachineInDatacenterToNodeMetadata converter = new MachineInDatacenterToNodeMetadata(
            SDCComputeServiceContextModule.toPortableNodeStatus, locationIndex,
            Suppliers.<Set<? extends Image>> ofInstance(images),
            Suppliers.<Set<? extends Hardware>> ofInstance(hardwares), namingConvention);

      NodeMetadata convertedNodeMetadata = converter.apply(machineInDatacenterToConvert);

      assertEquals(machineInDatacenterToConvert.slashEncode(), convertedNodeMetadata.getId());
      assertEquals(machineToConvert.getId(), convertedNodeMetadata.getProviderId());

      assertEquals(convertedNodeMetadata.getLocation().getScope(), LocationScope.ZONE);
      assertEquals(convertedNodeMetadata.getLocation().getId(), "us-sw-1");

      assertEquals(machineToConvert.getName(), convertedNodeMetadata.getName());
      assertEquals(convertedNodeMetadata.getGroup(), "sample");

      assertEquals(convertedNodeMetadata.getImageId(), expectedImageId);
      assertEquals(convertedNodeMetadata.getOperatingSystem(), expectedOs);

      assertEquals(convertedNodeMetadata.getHardware(), expectedHardware);

      assertEquals(SDCComputeServiceContextModule.toPortableNodeStatus.get(machineToConvert.getState()),
            convertedNodeMetadata.getStatus());

      assertNotNull(convertedNodeMetadata.getPrivateAddresses());
      assertEquals(convertedNodeMetadata.getPrivateAddresses(), ImmutableSet.of("10.224.0.63"));

      assertNotNull(convertedNodeMetadata.getPublicAddresses());
      assertEquals(convertedNodeMetadata.getPublicAddresses(), ImmutableSet.of("37.153.96.62"));

      assertNotNull(convertedNodeMetadata.getUserMetadata());
      assertEquals(convertedNodeMetadata.getUserMetadata(),
            ImmutableMap.<String, String> of("root_authorized_keys", "ssh-rsa XXXXXX== test@xxxx.ovh.net\n"));
   }

}
