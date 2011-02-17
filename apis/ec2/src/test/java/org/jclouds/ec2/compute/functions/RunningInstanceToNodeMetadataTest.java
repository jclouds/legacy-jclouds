/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.ec2.compute.functions;

import static org.jclouds.ec2.compute.domain.EC2HardwareBuilder.m1_small;
import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.ec2.compute.config.EC2ComputeServiceDependenciesModule;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.xml.DescribeInstancesResponseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class RunningInstanceToNodeMetadataTest {

   public void testAllStatesCovered() {

      for (InstanceState state : InstanceState.values()) {
         assert EC2ComputeServiceDependenciesModule.instanceToNodeState.containsKey(state) : state;
      }

   }

   static Location provider = new LocationBuilder().scope(LocationScope.REGION).id("us-east-1")
            .description("us-east-1").build();

   @Test
   public void testApplyWhereTagDoesntMatchAndImageHardwareAndLocationNotFoundButCredentialsFound()
            throws UnknownHostException {
      Credentials creds = new Credentials("root", "abdce");

      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.<Hardware> of(), ImmutableSet
               .<Location> of(), ImmutableSet.<Image> of(), ImmutableMap.<String, Credentials> of(
               "node#us-east-1/i-0799056f", creds));

      RunningInstance server = firstInstanceFromResource("/describe_instances_running.xml");

      assertEquals(parser.apply(server), new NodeMetadataBuilder().state(NodeState.RUNNING).publicAddresses(
               ImmutableSet.<String> of()).privateAddresses(ImmutableSet.of("10.243.42.70")).publicAddresses(
               ImmutableSet.of("174.129.81.68")).credentials(creds).imageId("us-east-1/ami-82e4b5c7").id(
               "us-east-1/i-0799056f").providerId("i-0799056f").build());
   }

   @Test
   public void testApplyWhereTagDoesntMatchAndImageHardwareAndLocationNotFound() throws UnknownHostException {
      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.<Hardware> of(), ImmutableSet
               .<Location> of(), ImmutableSet.<Image> of(), ImmutableMap.<String, Credentials> of());

      RunningInstance server = firstInstanceFromResource("/describe_instances_running.xml");

      assertEquals(parser.apply(server), new NodeMetadataBuilder().state(NodeState.RUNNING).publicAddresses(
               ImmutableSet.<String> of()).privateAddresses(ImmutableSet.of("10.243.42.70")).publicAddresses(
               ImmutableSet.of("174.129.81.68")).imageId("us-east-1/ami-82e4b5c7").id("us-east-1/i-0799056f")
               .providerId("i-0799056f").build());
   }

   @Test
   public void testApplyWhereTagDoesntMatchAndLocationFoundAndImageAndHardwareNotFound() throws UnknownHostException {
      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.<Hardware> of(), ImmutableSet.of(provider),
               ImmutableSet.<Image> of(), ImmutableMap.<String, Credentials> of());

      RunningInstance server = firstInstanceFromResource("/describe_instances_running.xml");

      assertEquals(parser.apply(server), new NodeMetadataBuilder().state(NodeState.RUNNING).privateAddresses(
               ImmutableSet.of("10.243.42.70")).publicAddresses(ImmutableSet.of("174.129.81.68")).imageId(
               "us-east-1/ami-82e4b5c7").id("us-east-1/i-0799056f").providerId("i-0799056f").location(provider).build());
   }

   @Test
   public void testApplyWhereTagDoesntMatchAndImageAndLocationFoundAndHardwareNotFound() throws UnknownHostException {
      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.<Hardware> of(), ImmutableSet.of(provider),
               EC2ImageParserTest.convertImages("/amzn_images.xml"), ImmutableMap.<String, Credentials> of());

      RunningInstance server = firstInstanceFromResource("/describe_instances_running.xml");

      assertEquals(parser.apply(server), new NodeMetadataBuilder().state(NodeState.RUNNING).privateAddresses(
               ImmutableSet.of("10.243.42.70")).publicAddresses(ImmutableSet.of("174.129.81.68")).imageId(
               "us-east-1/ami-82e4b5c7").operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.UNRECOGNIZED).version("").arch("paravirtual").description(
                        "137112412989/amzn-ami-0.9.7-beta.i386-ebs").is64Bit(false).build()).id("us-east-1/i-0799056f")
               .providerId("i-0799056f").location(provider).build());
   }

   @Test
   public void testApplyWhereTagDoesntMatchAndImageHardwareAndLocationFound() throws UnknownHostException {
      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.of(m1_small().build()), ImmutableSet
               .of(provider), EC2ImageParserTest.convertImages("/amzn_images.xml"), ImmutableMap
               .<String, Credentials> of());

      RunningInstance server = firstInstanceFromResource("/describe_instances_running.xml");

      assertEquals(parser.apply(server), new NodeMetadataBuilder().state(NodeState.RUNNING).privateAddresses(
               ImmutableSet.of("10.243.42.70")).publicAddresses(ImmutableSet.of("174.129.81.68")).imageId(
               "us-east-1/ami-82e4b5c7").hardware(m1_small().build()).operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.UNRECOGNIZED).version("").arch("paravirtual").description(
                        "137112412989/amzn-ami-0.9.7-beta.i386-ebs").is64Bit(false).build()).id("us-east-1/i-0799056f")
               .providerId("i-0799056f").location(provider).build());
   }

   @Test
   public void testHandleMissingAMIs() {

      // Handle the case when the installed AMI no longer can be found in AWS.

      // Create a null-returning function to simulate that the AMI can't be found.
      Function<RegionAndName, Image> nullReturningFunction = new Function<RegionAndName, Image>() {

         @Override
         public Image apply(@Nullable RegionAndName from) {
            return null;
         }
      };
      Map<RegionAndName, Image> instanceToImage = new MapMaker().makeComputingMap(nullReturningFunction);

      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.of(m1_small().build()), ImmutableSet
               .of(provider), ImmutableMap.<String, Credentials> of(),
               EC2ComputeServiceDependenciesModule.instanceToNodeState, instanceToImage);

      RunningInstance server = firstInstanceFromResource("/describe_instances_running.xml");

      assertEquals(parser.apply(server), new NodeMetadataBuilder().state(NodeState.RUNNING).privateAddresses(
               ImmutableSet.of("10.243.42.70")).publicAddresses(ImmutableSet.of("174.129.81.68")).imageId(
               "us-east-1/ami-82e4b5c7").id("us-east-1/i-0799056f").providerId("i-0799056f").hardware(
               m1_small().build()).location(provider).build());
   }

   protected RunningInstance firstInstanceFromResource(String resource) {
      RunningInstance server = Iterables.get(Iterables.get(DescribeInstancesResponseHandlerTest
               .parseRunningInstances(resource), 0), 0);
      return server;
   }

   protected RunningInstanceToNodeMetadata createNodeParser(final ImmutableSet<Hardware> hardware,
            final ImmutableSet<Location> locations, Set<org.jclouds.compute.domain.Image> images,
            Map<String, Credentials> credentialStore) {
      Map<InstanceState, NodeState> instanceToNodeState = EC2ComputeServiceDependenciesModule.instanceToNodeState;

      Map<RegionAndName, Image> instanceToImage = Maps.uniqueIndex(images, new Function<Image, RegionAndName>() {

         @Override
         public RegionAndName apply(Image from) {
            return new RegionAndName(from.getLocation().getId(), from.getProviderId());
         }

      });

      return createNodeParser(hardware, locations, credentialStore, instanceToNodeState, instanceToImage);
   }

   private RunningInstanceToNodeMetadata createNodeParser(final ImmutableSet<Hardware> hardware,
            final ImmutableSet<Location> locations, Map<String, Credentials> credentialStore,
            Map<InstanceState, NodeState> instanceToNodeState, Map<RegionAndName, Image> instanceToImage) {
      Supplier<Set<? extends Location>> locationSupplier = new Supplier<Set<? extends Location>>() {

         @Override
         public Set<? extends Location> get() {
            return locations;
         }

      };
      Supplier<Set<? extends Hardware>> hardwareSupplier = new Supplier<Set<? extends Hardware>>() {

         @Override
         public Set<? extends Hardware> get() {
            return hardware;
         }

      };
      RunningInstanceToNodeMetadata parser = new RunningInstanceToNodeMetadata(instanceToNodeState, credentialStore,
               instanceToImage, locationSupplier, hardwareSupplier);
      return parser;
   }

}
