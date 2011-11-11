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
package org.jclouds.cloudstack.compute.functions;

import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import org.jclouds.cloudstack.compute.functions.VirtualMachineToNodeMetadata.FindHardwareForVirtualMachine;
import org.jclouds.cloudstack.compute.functions.VirtualMachineToNodeMetadata.FindImageForVirtualMachine;
import org.jclouds.cloudstack.compute.functions.VirtualMachineToNodeMetadata.FindLocationForVirtualMachine;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.parse.ListVirtualMachinesResponseTest;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "VirtualMachineToNodeMetadataTest")
public class VirtualMachineToNodeMetadataTest {

   @Test
   public void testApplyWhereVirtualMachineWithNoPassword() throws UnknownHostException {

      // note we are testing when no credentials are here. otherwise would be
      // ("node#416696", new
      // Credentials("root", "password"))
      Map<String, Credentials> credentialStore = ImmutableMap.<String, Credentials> of();

      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(ZoneToLocationTest.one, ZoneToLocationTest.two));

      Supplier<Set<? extends Hardware>> hardwareSupplier = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
            .<Hardware> of(ServiceOfferingToHardwareTest.one, ServiceOfferingToHardwareTest.two));

      Supplier<Set<? extends Image>> imageSupplier = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
            .<Image> of(TemplateToImageTest.one, TemplateToImageTest.two));

      VirtualMachineToNodeMetadata parser = new VirtualMachineToNodeMetadata(credentialStore,
            new FindLocationForVirtualMachine(locationSupplier), new FindHardwareForVirtualMachine(hardwareSupplier),
            new FindImageForVirtualMachine(imageSupplier));

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualMachine guest = Iterables.get(new ListVirtualMachinesResponseTest().expected(), 0);

      NodeMetadata node = parser.apply(guest);

      assertEquals(
            node.toString(),
            new NodeMetadataBuilder().id("1/54").providerId("54").name("i-3-54-VM").location(ZoneToLocationTest.one)
                  .state(NodeState.PENDING).privateAddresses(ImmutableSet.of("10.1.1.18"))
                  .hardware(ServiceOfferingToHardwareTest.one).imageId(TemplateToImageTest.one.getId())
                  .operatingSystem(TemplateToImageTest.one.getOperatingSystem()).build().toString());

      // because it wasn't present in the credential store.
      assertEquals(node.getCredentials(), null);
   }

   @Test
   public void testApplyWhereVirtualMachineWithPassword() throws UnknownHostException {

      Map<String, Credentials> credentialStore = ImmutableMap.<String, Credentials> of("node#1/54", new Credentials(
            "root", "password"));

      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(ZoneToLocationTest.one, ZoneToLocationTest.two));

      Supplier<Set<? extends Hardware>> hardwareSupplier = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
            .<Hardware> of(ServiceOfferingToHardwareTest.one, ServiceOfferingToHardwareTest.two));

      Supplier<Set<? extends Image>> imageSupplier = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
            .<Image> of(TemplateToImageTest.one, TemplateToImageTest.two));

      VirtualMachineToNodeMetadata parser = new VirtualMachineToNodeMetadata(credentialStore,
            new FindLocationForVirtualMachine(locationSupplier), new FindHardwareForVirtualMachine(hardwareSupplier),
            new FindImageForVirtualMachine(imageSupplier));

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualMachine guest = Iterables.get(new ListVirtualMachinesResponseTest().expected(), 0);

      NodeMetadata node = parser.apply(guest);

      assertEquals(
            node.toString(),
            new NodeMetadataBuilder().id("1/54").providerId("54").name("i-3-54-VM").location(ZoneToLocationTest.one)
                  .state(NodeState.PENDING).privateAddresses(ImmutableSet.of("10.1.1.18"))
                  .hardware(ServiceOfferingToHardwareTest.one).imageId(TemplateToImageTest.one.getId())
                  .credentials(new Credentials("root", "password"))
                  .operatingSystem(TemplateToImageTest.one.getOperatingSystem()).build().toString());

      assertEquals(node.getCredentials(), new Credentials("root", "password"));
   }

}
