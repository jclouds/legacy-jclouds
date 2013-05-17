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

import static org.easymock.EasyMock.createNiceMock;
import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.parse.ParseBadVirtualGuest;
import org.jclouds.softlayer.parse.ParseVirtualGuestHaltedTest;
import org.jclouds.softlayer.parse.ParseVirtualGuestPausedTest;
import org.jclouds.softlayer.parse.ParseVirtualGuestRunningTest;
import org.jclouds.softlayer.parse.ParseVirtualGuestWithNoPasswordTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "VirtualGuestToNodeMetadataTest")
public class VirtualGuestToNodeMetadataTest {
   GroupNamingConvention.Factory namingConvention = Guice.createInjector().getInstance(GroupNamingConvention.Factory.class);

   @Test
   public void testApplyWhereVirtualGuestWithNoPassword() {

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualGuest guest = new ParseVirtualGuestWithNoPasswordTest().expected();

      // setup so that we have an expected Location to be parsed from the guest.
      Location expectedLocation = DatacenterToLocationTest.function.apply(guest.getDatacenter());
      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(expectedLocation));

      VirtualGuestToNodeMetadata parser = new VirtualGuestToNodeMetadata(
            locationSupplier, new GetHardwareForVirtualGuestMock(), new GetImageForVirtualGuestMock(), namingConvention);

      NodeMetadata node = parser.apply(guest);

      assertEquals(
            node,
            new NodeMetadataBuilder().ids("416788").name("node1000360500").hostname("node1000360500")
                  .location(expectedLocation).status(Status.PENDING)
                  .publicAddresses(ImmutableSet.of("173.192.29.186"))
                  .privateAddresses(ImmutableSet.of("10.37.102.194"))
                  .hardware(new GetHardwareForVirtualGuestMock().getHardware(guest))
                  .imageId(new GetImageForVirtualGuestMock().getImage(guest).getId())
                  .operatingSystem(new GetImageForVirtualGuestMock().getImage(guest).getOperatingSystem()).build());

   }

   @Test
   public void testApplyWhereVirtualIsBad() {

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualGuest guest = new ParseBadVirtualGuest().expected();

      // no location here
      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of());

      VirtualGuestToNodeMetadata parser = new VirtualGuestToNodeMetadata(locationSupplier,
            new GetHardwareForVirtualGuestMock(), new GetImageForVirtualGuestMock(), namingConvention);

      NodeMetadata node = parser.apply(guest);

      assertEquals(
            node,
            new NodeMetadataBuilder().ids("413348").name("foo-ef4").hostname("foo-ef4").group("foo")
                  .status(Status.PENDING).hardware(new GetHardwareForVirtualGuestMock().getHardware(guest))
                  .imageId(new GetImageForVirtualGuestMock().getImage(guest).getId())
                  .operatingSystem(new GetImageForVirtualGuestMock().getImage(guest).getOperatingSystem()).build());

   }

   @Test
   public void testApplyWhereVirtualGuestIsHalted() {

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualGuest guest = new ParseVirtualGuestHaltedTest().expected();

      // setup so that we have an expected Location to be parsed from the guest.
      Location expectedLocation = DatacenterToLocationTest.function.apply(guest.getDatacenter());
      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(expectedLocation));

      VirtualGuestToNodeMetadata parser = new VirtualGuestToNodeMetadata(locationSupplier,
            new GetHardwareForVirtualGuestMock(), new GetImageForVirtualGuestMock(), namingConvention);

      NodeMetadata node = parser.apply(guest);

      assertEquals(
            node,
            new NodeMetadataBuilder().ids("416700").name("node1703810489").hostname("node1703810489")
                  .location(expectedLocation).status(Status.PENDING)
                  .publicAddresses(ImmutableSet.of("173.192.29.187"))
                  .privateAddresses(ImmutableSet.of("10.37.102.195"))
                  .hardware(new GetHardwareForVirtualGuestMock().getHardware(guest))
                  .imageId(new GetImageForVirtualGuestMock().getImage(guest).getId())
                  .operatingSystem(new GetImageForVirtualGuestMock().getImage(guest).getOperatingSystem()).build());

   }

   @Test
   public void testApplyWhereVirtualGuestIsPaused() {

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualGuest guest = new ParseVirtualGuestPausedTest().expected();

      // setup so that we have an expected Location to be parsed from the guest.
      Location expectedLocation = DatacenterToLocationTest.function.apply(guest.getDatacenter());
      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(expectedLocation));

      VirtualGuestToNodeMetadata parser = new VirtualGuestToNodeMetadata(locationSupplier,
            new GetHardwareForVirtualGuestMock(), new GetImageForVirtualGuestMock(), namingConvention);

      NodeMetadata node = parser.apply(guest);

      assertEquals(
            node,
            new NodeMetadataBuilder().ids("416700").name("node1703810489").hostname("node1703810489")
                  .location(expectedLocation).status(Status.SUSPENDED)
                  .publicAddresses(ImmutableSet.of("173.192.29.187"))
                  .privateAddresses(ImmutableSet.of("10.37.102.195"))
                  .hardware(new GetHardwareForVirtualGuestMock().getHardware(guest))
                  .imageId(new GetImageForVirtualGuestMock().getImage(guest).getId())
                  .operatingSystem(new GetImageForVirtualGuestMock().getImage(guest).getOperatingSystem()).build());

   }

   @Test
   public void testApplyWhereVirtualGuestIsRunning() {

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualGuest guest = new ParseVirtualGuestRunningTest().expected();

      // setup so that we have an expected Location to be parsed from the guest.
      Location expectedLocation = DatacenterToLocationTest.function.apply(guest.getDatacenter());
      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(expectedLocation));

      VirtualGuestToNodeMetadata parser = new VirtualGuestToNodeMetadata(locationSupplier,
            new GetHardwareForVirtualGuestMock(), new GetImageForVirtualGuestMock(), namingConvention);

      NodeMetadata node = parser.apply(guest);

      assertEquals(
            node,
            new NodeMetadataBuilder().ids("416700").name("node1703810489").hostname("node1703810489")
                  .location(expectedLocation).status(Status.RUNNING)
                  .publicAddresses(ImmutableSet.of("173.192.29.187"))
                  .privateAddresses(ImmutableSet.of("10.37.102.195"))
                  .hardware(new GetHardwareForVirtualGuestMock().getHardware(guest))
                  .imageId(new GetImageForVirtualGuestMock().getImage(guest).getId())
                  .operatingSystem(new GetImageForVirtualGuestMock().getImage(guest).getOperatingSystem()).build());

   }

   private static class GetHardwareForVirtualGuestMock extends VirtualGuestToNodeMetadata.GetHardwareForVirtualGuest {
      @SuppressWarnings("unchecked")
      public GetHardwareForVirtualGuestMock() {
         super(createNiceMock(SoftLayerClient.class), createNiceMock(Function.class));
      }

      @Override
      public Hardware getHardware(VirtualGuest guest) {
         return new HardwareBuilder().ids("mocked hardware").build();
      }
   }

   private static class GetImageForVirtualGuestMock extends VirtualGuestToNodeMetadata.GetImageForVirtualGuest {
      public GetImageForVirtualGuestMock() {
         super(null);
      }

      @Override
      public Image getImage(VirtualGuest guest) {
         return new ImageBuilder().ids("123").description("mocked image")
               .operatingSystem(OperatingSystem.builder().description("foo os").build())
               .status(Image.Status.AVAILABLE).build();
      }
   }
}
