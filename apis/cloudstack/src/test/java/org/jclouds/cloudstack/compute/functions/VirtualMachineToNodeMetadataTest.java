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
package org.jclouds.cloudstack.compute.functions;

import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.Set;

import org.jclouds.cloudstack.domain.GuestIPType;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.NIC;
import org.jclouds.cloudstack.domain.TrafficType;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.parse.ListVirtualMachinesResponseTest;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.domain.Location;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;

/**
 * @author Adrian Cole, Andrei Savu
 */
@Test(groups = "unit", testName = "VirtualMachineToNodeMetadataTest")
public class VirtualMachineToNodeMetadataTest {

   GroupNamingConvention.Factory namingConvention = Guice.createInjector().getInstance(GroupNamingConvention.Factory.class);

   @Test
   public void testApplyWhereVirtualMachineWithIPForwardingRule() throws UnknownHostException {

      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
         .<Location>of(ZoneToLocationTest.one, ZoneToLocationTest.two));

      Supplier<Set<? extends Image>> imageSupplier = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
         .<Image>of(TemplateToImageTest.one, TemplateToImageTest.two));
      VirtualMachineToNodeMetadata parser = new VirtualMachineToNodeMetadata(locationSupplier, imageSupplier,
            CacheBuilder.newBuilder().<String, Set<IPForwardingRule>> build(
                  new CacheLoader<String, Set<IPForwardingRule>>() {
                     @Override
                     public Set<IPForwardingRule> load(String arg0) throws Exception {
                        return ImmutableSet.of(IPForwardingRule.builder().id("1234l").IPAddress("1.1.1.1").build());
                     }
                  }), namingConvention);

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualMachine guest = Iterables.get(new ListVirtualMachinesResponseTest().expected(), 0);

      NodeMetadata node = parser.apply(guest);

      assertEquals(
            node.toString(),
            new NodeMetadataBuilder().id("54").providerId("54").name("i-3-54-VM").group("i-3-54")
                  .location(ZoneToLocationTest.one).status(Status.PENDING).hostname("i-3-54-VM")
                  .privateAddresses(ImmutableSet.of("10.1.1.18")).publicAddresses(ImmutableSet.of("1.1.1.1"))
                  .hardware(addHypervisor(ServiceOfferingToHardwareTest.one, "XenServer"))
                  .imageId(TemplateToImageTest.one.getId())
                  .operatingSystem(TemplateToImageTest.one.getOperatingSystem()).build().toString());

   }

   @Test
   public void testApplyWhereVirtualMachineHasNoIpForwardingRuleAndAPublicIP() throws UnknownHostException {

      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
         .<Location>of(ZoneToLocationTest.one, ZoneToLocationTest.two));

      Supplier<Set<? extends Image>> imageSupplier = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
         .<Image>of(TemplateToImageTest.one, TemplateToImageTest.two));

      VirtualMachineToNodeMetadata parser = new VirtualMachineToNodeMetadata(locationSupplier, imageSupplier,
            CacheBuilder.newBuilder().<String, Set<IPForwardingRule>> build(
                  new CacheLoader<String, Set<IPForwardingRule>>() {
                     @Override
                     public Set<IPForwardingRule> load(String arg0) throws Exception {
                        return ImmutableSet.of();
                     }
                  }), namingConvention);

      VirtualMachine guest =VirtualMachine.builder()
         .id("54")
         .name("i-3-54-VM")
         .displayName("i-3-54-VM")
         .account("adrian")
         .domainId("1")
         .domain("ROOT")
         .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-16T14:28:37-0800"))
         .state(VirtualMachine.State.STARTING)
         .isHAEnabled(false)
         .zoneId("1")
         .zoneName("San Jose 1")
         .templateId("2")
         .templateName("CentOS 5.3(64-bit) no GUI (XenServer)")
         .templateDisplayText("CentOS 5.3(64-bit) no GUI (XenServer)")
         .passwordEnabled(false)
         .serviceOfferingId("1")
         .serviceOfferingName("Small Instance")
         .cpuCount(1)
         .cpuSpeed(500)
         .memory(512)
         .guestOSId("11")
         .rootDeviceId("0")
         .rootDeviceType("NetworkFilesystem")
         .jobId("63l")
         .jobStatus(0)
         .nics(ImmutableSet.of(NIC.builder().id("72").networkId("204").netmask("255.255.255.0").gateway("1.1.1.1")
            .IPAddress("1.1.1.5").trafficType(TrafficType.GUEST).guestIPType(GuestIPType.VIRTUAL)
            .isDefault(true).build())).hypervisor("XenServer").build();

      NodeMetadata node = parser.apply(guest);

      assertEquals(
         node.toString(),
         new NodeMetadataBuilder().id("54").providerId("54").name("i-3-54-VM").group("i-3-54")
            .location(ZoneToLocationTest.one).status(Status.PENDING).hostname("i-3-54-VM")
            .privateAddresses(ImmutableSet.<String>of())
            .publicAddresses(ImmutableSet.<String>of("1.1.1.5"))
            .hardware(addHypervisor(ServiceOfferingToHardwareTest.one, "XenServer"))
            .imageId(TemplateToImageTest.one.getId())
            .operatingSystem(TemplateToImageTest.one.getOperatingSystem()).build().toString());
   }

   @Test
   public void testApplyWhereVirtualMachineWithNoPassword() throws UnknownHostException {

      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
         .<Location>of(ZoneToLocationTest.one, ZoneToLocationTest.two));

      Supplier<Set<? extends Image>> imageSupplier = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
         .<Image>of(TemplateToImageTest.one, TemplateToImageTest.two));
      VirtualMachineToNodeMetadata parser = new VirtualMachineToNodeMetadata(locationSupplier, imageSupplier,
            CacheBuilder.newBuilder().<String, Set<IPForwardingRule>> build(
                  new CacheLoader<String, Set<IPForwardingRule>>() {

                     @Override
                     public Set<IPForwardingRule> load(String arg0) throws Exception {
                        throw new ResourceNotFoundException("no ip forwarding rule for: " + arg0);
                     }

                  }), namingConvention);

      // notice if we've already parsed this properly here, we can rely on it.
      VirtualMachine guest = Iterables.get(new ListVirtualMachinesResponseTest().expected(), 0);

      NodeMetadata node = parser.apply(guest);

      assertEquals(
            node.toString(),
            new NodeMetadataBuilder().id("54").providerId("54").name("i-3-54-VM").group("i-3-54")
                  .location(ZoneToLocationTest.one).status(Status.PENDING).hostname("i-3-54-VM")
                  .privateAddresses(ImmutableSet.of("10.1.1.18"))
                  .hardware(addHypervisor(ServiceOfferingToHardwareTest.one, "XenServer"))
                  .imageId(TemplateToImageTest.one.getId())
                  .operatingSystem(TemplateToImageTest.one.getOperatingSystem()).build().toString());
   }

   protected Hardware addHypervisor(Hardware in, String hypervisor) {
      return HardwareBuilder.fromHardware(in).hypervisor(hypervisor).build();
   }

}
