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
package org.jclouds.ec2.compute.functions;

import static org.jclouds.ec2.compute.domain.EC2HardwareBuilder.m1_small;
import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.compute.config.EC2ComputeServiceDependenciesModule;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.xml.DescribeInstancesResponseHandlerTest;
import org.jclouds.javax.annotation.Nullable;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "RunningInstanceToNodeMetadataTest")
public class RunningInstanceToNodeMetadataTest {

   public void testAllStatesCovered() {

      for (InstanceState status : InstanceState.values()) {
         assert EC2ComputeServiceDependenciesModule.toPortableNodeStatus.containsKey(status) : status;
      }

   }
   
   @Test
   public void testPrivateIpAddressIncorrectlyInPublicAddressFieldGoesToPrivateAddressCollection() {
      RunningInstance instance = RunningInstance.builder().instanceId("id").imageId("image").instanceType("m1.small")
               .instanceState(InstanceState.RUNNING).rawState("running").region("us-east-1").ipAddress("10.1.1.1").build();

      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.<Hardware> of(), ImmutableSet
               .<Location> of(), ImmutableSet.<Image> of(), ImmutableMap.<String, Credentials> of());

      assertEquals(parser.apply(instance).toString(), new NodeMetadataBuilder().status(Status.RUNNING).backendStatus("running").publicAddresses(
               ImmutableSet.<String> of()).privateAddresses(ImmutableSet.of("10.1.1.1")).id("us-east-1/id").imageId(
               "us-east-1/image").providerId("id").build().toString());
   }

   @Test
   public void testPublicIpAddressIncorrectlyInPrivateAddressFieldGoesToPublicAddressCollection() {
      RunningInstance instance = RunningInstance.builder().instanceId("id").imageId("image").instanceType("m1.small")
               .instanceState(InstanceState.RUNNING).rawState("running").region("us-east-1").privateIpAddress("1.1.1.1").build();

      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.<Hardware> of(), ImmutableSet
               .<Location> of(), ImmutableSet.<Image> of(), ImmutableMap.<String, Credentials> of());

      assertEquals(parser.apply(instance).toString(), new NodeMetadataBuilder().status(Status.RUNNING).backendStatus("running").privateAddresses(
               ImmutableSet.<String> of()).publicAddresses(ImmutableSet.of("1.1.1.1")).id("us-east-1/id").imageId(
               "us-east-1/image").providerId("id").build().toString());
   }
   
   static Location provider = new LocationBuilder().scope(LocationScope.REGION).id("us-east-1")
            .description("us-east-1").build();

   @Test
   public void testApplyWhereTagDoesntMatchAndImageHardwareAndLocationNotFoundButCredentialsFound()
            throws UnknownHostException {
      LoginCredentials creds = LoginCredentials.builder().user("root").password("abdce").build();

      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.<Hardware> of(), ImmutableSet
               .<Location> of(), ImmutableSet.<Image> of(), ImmutableMap.<String, Credentials> of(
               "node#us-east-1/i-0799056f", creds));

      RunningInstance server = firstInstanceFromResource("/describe_instances_running.xml");

      assertEquals(
            parser.apply(server).toString(),
            new NodeMetadataBuilder().status(Status.RUNNING).backendStatus("running").hostname("ip-10-243-42-70")
                  .publicAddresses(ImmutableSet.<String> of()).privateAddresses(ImmutableSet.of("10.243.42.70"))
                  .publicAddresses(ImmutableSet.of("174.129.81.68")).credentials(creds)
                  .imageId("us-east-1/ami-82e4b5c7").id("us-east-1/i-0799056f").providerId("i-0799056f").build().toString());
   }

   @Test
   public void testApplyWhereTagDoesntMatchAndImageHardwareAndLocationNotFound() throws UnknownHostException {
      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.<Hardware> of(), ImmutableSet
               .<Location> of(), ImmutableSet.<Image> of(), ImmutableMap.<String, Credentials> of());

      RunningInstance server = firstInstanceFromResource("/describe_instances_running.xml");

      assertEquals(parser.apply(server).toString(),
            new NodeMetadataBuilder().hostname("ip-10-243-42-70").status(Status.RUNNING).backendStatus("running")
                  .publicAddresses(ImmutableSet.<String> of()).privateAddresses(ImmutableSet.of("10.243.42.70"))
                  .publicAddresses(ImmutableSet.of("174.129.81.68")).imageId("us-east-1/ami-82e4b5c7")
                  .id("us-east-1/i-0799056f").providerId("i-0799056f").build().toString());
   }

   @Test
   public void testApplyWhereTagDoesntMatchAndLocationFoundAndImageAndHardwareNotFound() throws UnknownHostException {
      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.<Hardware> of(), ImmutableSet.of(provider),
               ImmutableSet.<Image> of(), ImmutableMap.<String, Credentials> of());

      RunningInstance server = firstInstanceFromResource("/describe_instances_running.xml");
      NodeMetadata expected = new NodeMetadataBuilder().hostname("ip-10-243-42-70").status(Status.RUNNING).backendStatus("running")
               .privateAddresses(ImmutableSet.of("10.243.42.70")).publicAddresses(ImmutableSet.of("174.129.81.68"))
               .imageId("us-east-1/ami-82e4b5c7").id("us-east-1/i-0799056f").providerId("i-0799056f")
               .location(provider).build();
      
      assertEquals(parser.apply(server).toString(), expected.toString());
   }

   @Test
   public void testApplyWhereTagDoesntMatchAndImageAndLocationFoundAndHardwareNotFound() throws UnknownHostException {
      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.<Hardware> of(), ImmutableSet.of(provider),
               EC2ImageParserTest.convertImages("/amzn_images.xml"), ImmutableMap.<String, Credentials> of());

      RunningInstance server = firstInstanceFromResource("/describe_instances_running.xml");

      assertEquals(
            parser.apply(server).toString(),
            new NodeMetadataBuilder()
                  .status(Status.RUNNING).backendStatus("running")
                  .hostname("ip-10-243-42-70")
                  .privateAddresses(ImmutableSet.of("10.243.42.70"))
                  .publicAddresses(ImmutableSet.of("174.129.81.68"))
                  .imageId("us-east-1/ami-82e4b5c7")
                  .operatingSystem(
                        new OperatingSystem.Builder().family(OsFamily.UNRECOGNIZED).version("").arch("paravirtual")
                              .description("137112412989/amzn-ami-0.9.7-beta.i386-ebs").is64Bit(false).build())
                  .id("us-east-1/i-0799056f").providerId("i-0799056f").location(provider).build().toString());
   }

   @Test
   public void testApplyWhereTagDoesntMatchAndImageHardwareAndLocationFound() throws UnknownHostException {
      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.of(m1_small().build()), ImmutableSet
               .of(provider), EC2ImageParserTest.convertImages("/amzn_images.xml"), ImmutableMap
               .<String, Credentials> of());

      RunningInstance server = firstInstanceFromResource("/describe_instances_running.xml");

      assertEquals(
            parser.apply(server).toString(),
            new NodeMetadataBuilder()
                  .hostname("ip-10-243-42-70")
                  .status(Status.RUNNING).backendStatus("running")
                  .privateAddresses(ImmutableSet.of("10.243.42.70"))
                  .publicAddresses(ImmutableSet.of("174.129.81.68"))
                  .imageId("us-east-1/ami-82e4b5c7")
                  .hardware(m1_small().build())
                  .operatingSystem(
                        new OperatingSystem.Builder().family(OsFamily.UNRECOGNIZED).version("").arch("paravirtual")
                              .description("137112412989/amzn-ami-0.9.7-beta.i386-ebs").is64Bit(false).build())
                  .id("us-east-1/i-0799056f").providerId("i-0799056f").location(provider).build().toString());
   }

   @Test
   public void testHandleMissingAMIs() {

      // Handle the case when the installed AMI no longer can be found in AWS.

      // Create a null-returning function to simulate that the AMI can't be found.
      CacheLoader<RegionAndName, Image> nullReturningFunction = new CacheLoader<RegionAndName, Image>() {

         @Override
         public Image load(@Nullable RegionAndName from) {
            return null;
         }
      };
      LoadingCache<RegionAndName, Image> instanceToImage = CacheBuilder.newBuilder().build(nullReturningFunction);

      RunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.of(m1_small().build()), ImmutableSet
               .of(provider), ImmutableMap.<String, Credentials> of(),
               EC2ComputeServiceDependenciesModule.toPortableNodeStatus, instanceToImage);

      RunningInstance server = firstInstanceFromResource("/describe_instances_running.xml");

      assertEquals(
            parser.apply(server).toString(),
            new NodeMetadataBuilder().hostname("ip-10-243-42-70").status(Status.RUNNING).backendStatus("running")
                  .privateAddresses(ImmutableSet.of("10.243.42.70")).publicAddresses(ImmutableSet.of("174.129.81.68"))
                  .imageId("us-east-1/ami-82e4b5c7").id("us-east-1/i-0799056f").providerId("i-0799056f")
                  .hardware(m1_small().build()).location(provider).build().toString());
   }

   @Test
   public void testGroupNameIsSetWhenCustomKeyNameIsSetAndSecurityGroupIsGenerated() {
      checkGroupName(RunningInstance.builder().instanceId("id").imageId("image").instanceType("m1.small")
              .instanceState(InstanceState.RUNNING).rawState("running").region("us-east-1").keyName("custom-key")
              .groupId("jclouds#groupname").build());
   }

   @Test
   public void testGroupNameIsSetWhenCustomSecurityGroupIsSetAndKeyNameIsGenerated() {
      checkGroupName(RunningInstance.builder().instanceId("id").imageId("image").instanceType("m1.small")
              .instanceState(InstanceState.RUNNING).rawState("running").region("us-east-1").groupId("custom-sec")
              .keyName("jclouds#groupname#23").build());
   }

   protected RunningInstance firstInstanceFromResource(String resource) {
      RunningInstance server = Iterables.get(Iterables.get(DescribeInstancesResponseHandlerTest
               .parseRunningInstances(resource), 0), 0);
      return server;
   }

   protected RunningInstanceToNodeMetadata createNodeParser(final ImmutableSet<Hardware> hardware,
            final ImmutableSet<Location> locations, final Set<org.jclouds.compute.domain.Image> images,
            Map<String, Credentials> credentialStore) {
      Map<InstanceState, Status> instanceToNodeStatus = EC2ComputeServiceDependenciesModule.toPortableNodeStatus;
      
      CacheLoader<RegionAndName, Image> getRealImage = new CacheLoader<RegionAndName, Image>() {

         @Override
         public Image load(@Nullable RegionAndName from) {
            return ImagesToRegionAndIdMap.imagesToMap(images).get(from);
         }
      };
      LoadingCache<RegionAndName, Image> instanceToImage = CacheBuilder.newBuilder().build(getRealImage);
      return createNodeParser(hardware, locations, credentialStore, instanceToNodeStatus, instanceToImage);
   }

   private void checkGroupName(RunningInstance instance) {
      assertEquals("groupname", createNodeParser(ImmutableSet.<Hardware> of(), ImmutableSet
            .<Location> of(), ImmutableSet.<Image> of(), ImmutableMap.<String, Credentials> of())
            .apply(instance).getGroup());
   }

   private RunningInstanceToNodeMetadata createNodeParser(final ImmutableSet<Hardware> hardware,
            final ImmutableSet<Location> locations, Map<String, Credentials> credentialStore,
            Map<InstanceState, Status> instanceToNodeStatus, LoadingCache<RegionAndName, Image> instanceToImage) {
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

      GroupNamingConvention.Factory namingConvention = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            Names.bindProperties(binder(),new EC2ApiMetadata().getDefaultProperties());
         }

      }).getInstance(GroupNamingConvention.Factory.class);

      RunningInstanceToNodeMetadata parser = new RunningInstanceToNodeMetadata(instanceToNodeStatus, credentialStore,
            Suppliers.<LoadingCache<RegionAndName, ? extends Image>> ofInstance(instanceToImage), locationSupplier,
            hardwareSupplier, namingConvention);
      return parser;
   }

}
