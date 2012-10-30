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
package org.jclouds.slicehost.compute.functions;

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.slicehost.compute.config.SlicehostComputeServiceContextModule;
import org.jclouds.slicehost.domain.Slice;
import org.jclouds.slicehost.xml.SliceHandlerTest;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SliceToNodeMetadataTest")
public class SliceToNodeMetadataTest {
   Location provider = new LocationBuilder().scope(LocationScope.ZONE).id("dallas").description("description").build();
   GroupNamingConvention.Factory namingConvention = Guice.createInjector().getInstance(GroupNamingConvention.Factory.class);

   @Test
   public void testApplyWhereImageAndHardwareNotFound() {
      Map<Slice.Status, Status> sliceStateToNodeStatus = SlicehostComputeServiceContextModule.sliceStatusToNodeStatus;
      Set<org.jclouds.compute.domain.Image> images = ImmutableSet.of();
      Set<org.jclouds.compute.domain.Hardware> hardwares = ImmutableSet.of();
      Slice slice = SliceHandlerTest.parseSlice();

      SliceToNodeMetadata parser = new SliceToNodeMetadata(sliceStateToNodeStatus, Suppliers
               .<Set<? extends Image>> ofInstance(images), Suppliers.ofInstance(provider), Suppliers
               .<Set<? extends Hardware>> ofInstance(hardwares), namingConvention);

      NodeMetadata metadata = parser.apply(slice);

      assertEquals(metadata, new NodeMetadataBuilder().status(Status.PENDING).publicAddresses(
               ImmutableSet.of("174.143.212.229")).privateAddresses(ImmutableSet.of("10.176.164.199")).group("jclouds")
               .imageId("2").id("1").providerId("1").name("jclouds-foo").hostname("jclouds-foo").location(provider)
               .userMetadata(ImmutableMap.of("Server Label", "Web Head 1", "Image Version", "2.1")).build());
   }

   @Test
   public void testApplyWhereImageFoundAndHardwareNotFound() {
      Map<Slice.Status, Status> sliceStateToNodeStatus = SlicehostComputeServiceContextModule.sliceStatusToNodeStatus;
      org.jclouds.compute.domain.Image jcImage = SlicehostImageToImageTest.convertImage();
      Set<org.jclouds.compute.domain.Image> images = ImmutableSet.of(jcImage);
      Set<org.jclouds.compute.domain.Hardware> hardwares = ImmutableSet.of();
      Slice slice = SliceHandlerTest.parseSlice();

      SliceToNodeMetadata parser = new SliceToNodeMetadata(sliceStateToNodeStatus, Suppliers
               .<Set<? extends Image>> ofInstance(images), Suppliers.ofInstance(provider), Suppliers
               .<Set<? extends Hardware>> ofInstance(hardwares), namingConvention);

      NodeMetadata metadata = parser.apply(slice);
      assertEquals(metadata, new NodeMetadataBuilder().status(Status.PENDING).publicAddresses(
               ImmutableSet.of("174.143.212.229")).privateAddresses(ImmutableSet.of("10.176.164.199")).group("jclouds")
               .imageId("2").operatingSystem(
                        new OperatingSystem.Builder().family(OsFamily.CENTOS).description("CentOS 5.2").version("5.2")
                                 .is64Bit(true).build()).id("1").providerId("1").name("jclouds-foo").hostname(
                        "jclouds-foo").location(provider).userMetadata(
                        ImmutableMap.of("Server Label", "Web Head 1", "Image Version", "2.1")).build());
   }

   @Test
   public void testApplyWhereImageAndHardwareFound() {
      Map<Slice.Status, Status> sliceStateToNodeStatus = SlicehostComputeServiceContextModule.sliceStatusToNodeStatus;
      Set<org.jclouds.compute.domain.Image> images = ImmutableSet.of(SlicehostImageToImageTest.convertImage());
      Set<org.jclouds.compute.domain.Hardware> hardwares = ImmutableSet.of(FlavorToHardwareTest.convertFlavor());
      Slice slice = SliceHandlerTest.parseSlice();

      SliceToNodeMetadata parser = new SliceToNodeMetadata(sliceStateToNodeStatus, Suppliers
               .<Set<? extends Image>> ofInstance(images), Suppliers.ofInstance(provider), Suppliers
               .<Set<? extends Hardware>> ofInstance(hardwares), namingConvention);

      NodeMetadata metadata = parser.apply(slice);
      assertEquals(metadata, new NodeMetadataBuilder().status(Status.PENDING).publicAddresses(
               ImmutableSet.of("174.143.212.229")).privateAddresses(ImmutableSet.of("10.176.164.199")).group("jclouds")
               .imageId("2").hardware(
                        new HardwareBuilder().ids("1").name("256 slice").processors(
                                 ImmutableList.of(new Processor(0.25, 1.0))).ram(256).volumes(
                                 ImmutableList.of(new VolumeBuilder().type(Volume.Type.LOCAL).size(1.0f).durable(true)
                                          .bootDevice(true).build())).build()).operatingSystem(
                        new OperatingSystem.Builder().family(OsFamily.CENTOS).description("CentOS 5.2").version("5.2")
                                 .is64Bit(true).build()).id("1").providerId("1").name("jclouds-foo").hostname(
                        "jclouds-foo").location(provider).userMetadata(
                        ImmutableMap.of("Server Label", "Web Head 1", "Image Version", "2.1")).build());
   }
}
