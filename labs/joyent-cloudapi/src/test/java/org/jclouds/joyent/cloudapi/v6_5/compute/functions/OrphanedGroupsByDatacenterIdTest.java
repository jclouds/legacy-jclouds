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
package org.jclouds.joyent.cloudapi.v6_5.compute.functions;

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.joyent.cloudapi.v6_5.compute.config.JoyentCloudComputeServiceContextModule;
import org.jclouds.joyent.cloudapi.v6_5.compute.functions.MachineInDatacenterToNodeMetadata;
import org.jclouds.joyent.cloudapi.v6_5.compute.functions.OrphanedGroupsByDatacenterId;
import org.jclouds.joyent.cloudapi.v6_5.domain.Machine.State;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.DatacenterAndName;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.MachineInDatacenter;
import org.jclouds.joyent.cloudapi.v6_5.parse.ParseMachineTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(testName = "OrphanedGroupsByDatacenterIdTest")
public class OrphanedGroupsByDatacenterIdTest {

   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("joyent-cloudapi").description(
            "joyent-cloudapi").build();
   Location datacenter = new LocationBuilder().id("us-east-1").description("us-east-1").scope(
            LocationScope.ZONE).parent(provider).build();
   Supplier<Map<String, Location>> locationIndex = Suppliers.<Map<String, Location>> ofInstance(ImmutableMap
            .<String, Location> of("us-east-1", datacenter));

   GroupNamingConvention.Factory namingConvention = Guice.createInjector().getInstance(GroupNamingConvention.Factory.class);

   MachineInDatacenter machine1 = new MachineInDatacenter(new ParseMachineTest().expected().toBuilder().name("test-fe2").state(State.DELETED).build(), "us-east-1");
   MachineInDatacenter machine2 = new MachineInDatacenter(new ParseMachineTest().expected().toBuilder().name("sample-fe1").state(State.DELETED).build(), "us-east-1");
   
   @Test
   public void testWhenComputeServiceSaysAllNodesAreDeadBothGroupsAreReturned() {

      
      MachineInDatacenterToNodeMetadata converter = new MachineInDatacenterToNodeMetadata(
               JoyentCloudComputeServiceContextModule.toPortableNodeStatus, locationIndex, Suppliers
               .<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of()), Suppliers
               .<Set<? extends Hardware>> ofInstance(ImmutableSet.<Hardware> of()), namingConvention);

      Set<? extends NodeMetadata> set = ImmutableSet.of(converter.apply(machine2), converter.apply(machine1));

      assertEquals(new OrphanedGroupsByDatacenterId(Predicates.<DatacenterAndName> alwaysTrue()).apply(set), ImmutableMultimap
               .<String, String> builder().putAll("us-east-1", "sample", "test").build());
   }

   @Test
   public void testWhenComputeServiceSaysAllNodesAreDeadNoGroupsAreReturned() {

      MachineInDatacenter machine1 = new MachineInDatacenter(new ParseMachineTest().expected(), "us-east-1");
      MachineInDatacenter machine2 = new MachineInDatacenter(new ParseMachineTest().expected(), "us-east-1");

      MachineInDatacenterToNodeMetadata converter = new MachineInDatacenterToNodeMetadata(
               JoyentCloudComputeServiceContextModule.toPortableNodeStatus, locationIndex, Suppliers
                        .<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of()), Suppliers
                        .<Set<? extends Hardware>> ofInstance(ImmutableSet.<Hardware> of()), namingConvention);

      Set<? extends NodeMetadata> set = ImmutableSet.of(converter.apply(machine2), converter.apply(machine1));

      assertEquals(new OrphanedGroupsByDatacenterId(Predicates.<DatacenterAndName> alwaysFalse()).apply(set), ImmutableMultimap
               .<String, String> of());

   }
}
