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
package org.jclouds.aws.ec2.compute.functions;

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.date.DateService;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.ec2.compute.config.EC2ComputeServiceDependenciesModule;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.ImagesToRegionAndIdMap;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.RootDeviceType;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class AWSRunningInstanceToNodeMetadataTest {

   private static final String defaultRegion = "us-east-1";
   static Location provider = new LocationBuilder().scope(LocationScope.REGION).id(defaultRegion).description(
            defaultRegion).build();

   private DateService dateService;

   @BeforeTest
   protected void setUpInjector() {
      dateService = Guice.createInjector().getInstance(DateService.class);
      assert dateService != null;
   }

   @Test
   public void test2Nodes() {

      AWSRunningInstanceToNodeMetadata parser = createNodeParser(ImmutableSet.<Hardware> of(), ImmutableSet
               .<Location> of(), ImmutableSet.<Image> of(), ImmutableMap.<String, Credentials> of());

      ImmutableSet<AWSRunningInstance> contents = ImmutableSet.of(new AWSRunningInstance.Builder()
               .region(defaultRegion)
               .instanceId("i-911444f0")
               .imageId("ami-63be790a")
               .instanceState(InstanceState.RUNNING)
               .privateDnsName("ip-10-212-81-7.ec2.internal")
               .dnsName("ec2-174-129-173-155.compute-1.amazonaws.com")
               .keyName("jclouds#zkclustertest#us-east-1#23")
               .amiLaunchIndex("0")
               .instanceType("t1.micro")
               .launchTime(dateService.iso8601DateParse("2011-08-16T13:40:50.000Z"))
               .availabilityZone("us-east-1c")
               .kernelId("aki-427d952b")
               .monitoringState(MonitoringState.DISABLED)
               .privateIpAddress("10.212.81.7")
               .ipAddress("174.129.173.155")
               .securityGroupIdToNames(ImmutableMap.<String, String> of("sg-ef052b86", "jclouds#zkclustertest#us-east-1"))
               .rootDeviceType(RootDeviceType.EBS)
               .rootDeviceName("/dev/sda1")
               .device("/dev/sda1", new BlockDevice("vol-5829fc32", Attachment.Status.ATTACHED, dateService.iso8601DateParse("2011-08-16T13:41:19.000Z"), true))
               .virtualizationType("paravirtual")
               .tag("Name", "foo")
               .tag("Empty", "")
               .build(),//
               new AWSRunningInstance.Builder()
                        .region(defaultRegion)
                        .instanceId("i-931444f2")
                        .imageId("ami-63be790a")
                        .instanceState(InstanceState.RUNNING)
                        .privateDnsName("ip-10-212-185-8.ec2.internal")
                        .dnsName("ec2-50-19-207-248.compute-1.amazonaws.com")
                        .keyName("jclouds#zkclustertest#us-east-1#23")
                        .amiLaunchIndex("0")
                        .instanceType("t1.micro")
                        .launchTime(dateService.iso8601DateParse("2011-08-16T13:40:50.000Z"))
                        .availabilityZone("us-east-1c")
                        .kernelId("aki-427d952b")
                        .monitoringState(MonitoringState.DISABLED)
                        .privateIpAddress("10.212.185.8")
                        .ipAddress("50.19.207.248")
                        .securityGroupIdToNames(ImmutableMap.<String, String>of("sg-ef052b86", "jclouds#zkclustertest#us-east-1"))
                        .rootDeviceType(RootDeviceType.EBS)
                        .rootDeviceName("/dev/sda1")
                        .device("/dev/sda1", new BlockDevice("vol-5029fc3a", Attachment.Status.ATTACHED, dateService.iso8601DateParse("2011-08-16T13:41:19.000Z"), true))
                        .virtualizationType("paravirtual")
                        .build());

      assertEquals(
            parser.apply(Iterables.get(contents, 0)).toString(),
            new NodeMetadataBuilder()
                  .state(NodeState.RUNNING)
                  .group("zkclustertest")
                  .name("foo")
                  .hostname("ip-10-212-81-7")
                  .privateAddresses(ImmutableSet.of("10.212.81.7"))
                  .publicAddresses(ImmutableSet.of("174.129.173.155"))
                  .imageId("us-east-1/ami-63be790a")
                  .id("us-east-1/i-911444f0")
                  .providerId("i-911444f0")
                  .tags(ImmutableSet.of("Empty"))
                  .userMetadata(ImmutableMap.of("Name", "foo")).build().toString());
      assertEquals(
              parser.apply(Iterables.get(contents, 1)), 
              new NodeMetadataBuilder()
                  .state(NodeState.RUNNING)
                  .group("zkclustertest")
                  .hostname("ip-10-212-185-8")
                  .privateAddresses(ImmutableSet.of("10.212.185.8"))
                  .publicAddresses(ImmutableSet.of("50.19.207.248"))
                  .imageId("us-east-1/ami-63be790a")
                  .id("us-east-1/i-931444f2")
                  .providerId("i-931444f2")
                  .build());
   }

   protected AWSRunningInstanceToNodeMetadata createNodeParser(final ImmutableSet<Hardware> hardware,
            final ImmutableSet<Location> locations, Set<org.jclouds.compute.domain.Image> images,
            Map<String, Credentials> credentialStore) {
      Map<InstanceState, NodeState> instanceToNodeState = EC2ComputeServiceDependenciesModule.instanceToNodeState;

      final Map<RegionAndName, ? extends Image> backing = ImagesToRegionAndIdMap.imagesToMap(images);

      Cache<RegionAndName, Image> instanceToImage = CacheBuilder.newBuilder().build(new CacheLoader<RegionAndName, Image> (){
    
         @Override
         public Image load(RegionAndName key) throws Exception {
            return backing.get(key);
         }
         
      });
            
          
      return createNodeParser(hardware, locations, credentialStore, instanceToNodeState, instanceToImage);
   }

   private AWSRunningInstanceToNodeMetadata createNodeParser(final ImmutableSet<Hardware> hardware,
            final ImmutableSet<Location> locations, Map<String, Credentials> credentialStore,
            Map<InstanceState, NodeState> instanceToNodeState, Cache<RegionAndName, ? extends Image> instanceToImage) {
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
      AWSRunningInstanceToNodeMetadata parser = new AWSRunningInstanceToNodeMetadata(instanceToNodeState,
            credentialStore, Suppliers.<Cache<RegionAndName, ? extends Image>> ofInstance(instanceToImage),
            locationSupplier, hardwareSupplier);
      return parser;
   }

}
