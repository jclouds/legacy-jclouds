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
package org.jclouds.softlayer.compute.functions;

import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterables;
import org.jclouds.compute.domain.*;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.softlayer.compute.functions.VirtualGuestToNodeMetadata.FindLocationForVirtualGuest;
import org.jclouds.softlayer.domain.Password;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.parse.*;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "VirtualGuestToNodeMetadataTest")
public class VirtualGuestToNodeMetadataTest {

   @Test
   public void testApplyWhereVirtualGuestWithNoPassword() throws UnknownHostException {

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualGuest guest = new ParseVirtualGuestWithNoPasswordTest().expected();

      // note we are testing when no credentials are here. otherwise would be ("node#416696", new
      // Credentials("root", "password"))
      Map<String, Credentials> credentialStore = ImmutableMap.<String, Credentials> of();

      // setup so that we have an expected Location to be parsed from the guest.
      Location expectedLocation = DatacenterToLocationTest.function.apply(guest.getDatacenter());
      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(expectedLocation));

      VirtualGuestToNodeMetadata parser = new VirtualGuestToNodeMetadata(credentialStore,
               new FindLocationForVirtualGuest(locationSupplier),new GetHardwareForVirtualGuestMock(),new GetImageForVirtualGuestMock());

      NodeMetadata node = parser.apply(guest);

      assertEquals(node, new NodeMetadataBuilder().ids("416788")
            .name("node1000360500").hostname("node1000360500")
            .location(expectedLocation).state(NodeState.PENDING)
            .publicAddresses(ImmutableSet.of("173.192.29.186")).privateAddresses(ImmutableSet.of("10.37.102.194"))
            .hardware(new GetHardwareForVirtualGuestMock().getHardware(guest))
            .imageId(new GetImageForVirtualGuestMock().getImage(guest).getId())
            .operatingSystem(new GetImageForVirtualGuestMock().getImage(guest).getOperatingSystem())
            .build());

      // because it wasn't present in the credential store.
      assertEquals(node.getCredentials(), null);
   }

   @Test
   public void testApplyWhereVirtualIsBad() throws UnknownHostException {

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualGuest guest = new ParseBadVirtualGuest().expected();

      // note we are testing when no credentials are here. otherwise would be ("node#416696", new
      // Credentials("root", "password"))
      Map<String, Credentials> credentialStore = ImmutableMap.<String, Credentials> of();

      // no location here
      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of());

      VirtualGuestToNodeMetadata parser = new VirtualGuestToNodeMetadata(credentialStore,
               new FindLocationForVirtualGuest(locationSupplier),new GetHardwareForVirtualGuestMock(),new GetImageForVirtualGuestMock());

      NodeMetadata node = parser.apply(guest);

      assertEquals(node, new NodeMetadataBuilder().ids("413348")
            .name("foo-ef4").hostname("foo-ef4").group("foo")
            .state(NodeState.PENDING)
            .hardware(new GetHardwareForVirtualGuestMock().getHardware(guest))
            .imageId(new GetImageForVirtualGuestMock().getImage(guest).getId())
            .operatingSystem(new GetImageForVirtualGuestMock().getImage(guest).getOperatingSystem())
            .build());

      // because it wasn't present in the credential store.
      assertEquals(node.getCredentials(), null);
   }

   @Test
   public void testApplyWhereVirtualGuestIsHalted() throws UnknownHostException {

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualGuest guest = new ParseVirtualGuestHaltedTest().expected();

      Password password = Iterables.get(guest.getOperatingSystem().getPasswords(), 0);
      Credentials credentials = new Credentials(password.getUsername(),password.getPassword());
      Map<String, Credentials> credentialStore = ImmutableMap.<String, Credentials> of("node#416700",credentials);

      // setup so that we have an expected Location to be parsed from the guest.
      Location expectedLocation = DatacenterToLocationTest.function.apply(guest.getDatacenter());
      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(expectedLocation));

      VirtualGuestToNodeMetadata parser = new VirtualGuestToNodeMetadata(credentialStore,
               new FindLocationForVirtualGuest(locationSupplier),new GetHardwareForVirtualGuestMock(),new GetImageForVirtualGuestMock());

      NodeMetadata node = parser.apply(guest);

      assertEquals(node, new NodeMetadataBuilder().ids("416700")
            .name("node1703810489").hostname("node1703810489")
            .location(expectedLocation).state(NodeState.PENDING).credentials(credentials)
            .publicAddresses(ImmutableSet.of("173.192.29.187")).privateAddresses(ImmutableSet.of("10.37.102.195"))
            .hardware(new GetHardwareForVirtualGuestMock().getHardware(guest))
            .imageId(new GetImageForVirtualGuestMock().getImage(guest).getId())
            .operatingSystem(new GetImageForVirtualGuestMock().getImage(guest).getOperatingSystem())
            .build());

      // because it wasn't present in the credential store.
      assertEquals(node.getCredentials(), credentials);
   }

   @Test
   public void testApplyWhereVirtualGuestIsPaused() throws UnknownHostException {

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualGuest guest = new ParseVirtualGuestPausedTest().expected();

      Password password = Iterables.get(guest.getOperatingSystem().getPasswords(),0);
      Credentials credentials = new Credentials(password.getUsername(),password.getPassword());
      Map<String, Credentials> credentialStore = ImmutableMap.<String, Credentials> of("node#416700",credentials);

      // setup so that we have an expected Location to be parsed from the guest.
      Location expectedLocation = DatacenterToLocationTest.function.apply(guest.getDatacenter());
      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(expectedLocation));

      VirtualGuestToNodeMetadata parser = new VirtualGuestToNodeMetadata(credentialStore,
               new FindLocationForVirtualGuest(locationSupplier),new GetHardwareForVirtualGuestMock(),new GetImageForVirtualGuestMock());

      NodeMetadata node = parser.apply(guest);

      assertEquals(node, new NodeMetadataBuilder().ids("416700")
            .name("node1703810489").hostname("node1703810489")
            .location(expectedLocation).state(NodeState.SUSPENDED).credentials(credentials)
            .publicAddresses(ImmutableSet.of("173.192.29.187")).privateAddresses(ImmutableSet.of("10.37.102.195"))
            .hardware(new GetHardwareForVirtualGuestMock().getHardware(guest))
            .imageId(new GetImageForVirtualGuestMock().getImage(guest).getId())
            .operatingSystem(new GetImageForVirtualGuestMock().getImage(guest).getOperatingSystem())
            .build());

      // because it wasn't present in the credential store.
      assertEquals(node.getCredentials(), credentials);
   }

   @Test
   public void testApplyWhereVirtualGuestIsRunning() throws UnknownHostException {

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualGuest guest = new ParseVirtualGuestRunningTest().expected();

      Password password = Iterables.get(guest.getOperatingSystem().getPasswords(),0);
      Credentials credentials = new Credentials(password.getUsername(),password.getPassword());
      Map<String, Credentials> credentialStore = ImmutableMap.<String, Credentials> of("node#416700",credentials);

      // setup so that we have an expected Location to be parsed from the guest.
      Location expectedLocation = DatacenterToLocationTest.function.apply(guest.getDatacenter());
      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(expectedLocation));

      VirtualGuestToNodeMetadata parser = new VirtualGuestToNodeMetadata(credentialStore,
               new FindLocationForVirtualGuest(locationSupplier),new GetHardwareForVirtualGuestMock(),new GetImageForVirtualGuestMock());

      NodeMetadata node = parser.apply(guest);

      assertEquals(node, new NodeMetadataBuilder().ids("416700")
            .name("node1703810489").hostname("node1703810489")
            .location(expectedLocation).state(NodeState.RUNNING).credentials(credentials)
            .publicAddresses(ImmutableSet.of("173.192.29.187")).privateAddresses(ImmutableSet.of("10.37.102.195"))
            .hardware(new GetHardwareForVirtualGuestMock().getHardware(guest))
            .imageId(new GetImageForVirtualGuestMock().getImage(guest).getId())
            .operatingSystem(new GetImageForVirtualGuestMock().getImage(guest).getOperatingSystem())
            .build());

      // because it wasn't present in the credential store.
      assertEquals(node.getCredentials(), credentials);
   }

   private static class GetHardwareForVirtualGuestMock extends VirtualGuestToNodeMetadata.GetHardwareForVirtualGuest {
      public GetHardwareForVirtualGuestMock() {
         super(null);
      }

      @Override
      public Hardware getHardware(VirtualGuest guest) {
         return new HardwareBuilder().id("mocked hardware").build();
      }
   }

   private static class GetImageForVirtualGuestMock extends VirtualGuestToNodeMetadata.GetImageForVirtualGuest {
      public GetImageForVirtualGuestMock() {
         super(null);
      }

      @Override
      public Image getImage(VirtualGuest guest) {
         return new ImageBuilder().id("123").description("mocked image")
               .operatingSystem(OperatingSystem.builder().description("foo os").build())
               .build();
      }
   }
}
