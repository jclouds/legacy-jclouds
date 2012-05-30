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
package org.jclouds.ec2.compute.strategy;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;

import java.util.Map;
import java.util.Set;

import org.easymock.IArgumentMatcher;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.predicates.AtomicNodeRunning;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.RunningInstanceToNodeMetadata;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.compute.predicates.InstancePresent;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.services.ElasticIPAddressClient;
import org.jclouds.ec2.services.InstanceClient;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.inject.util.Providers;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "EC2CreateNodesInGroupThenAddToSetTest")
public class EC2CreateNodesInGroupThenAddToSetTest {

   @SuppressWarnings("unchecked")
   public void testIpAllocationThenAfterNodeRunningAssignThenUpdateCache() {
      Location location = ZONE_AP_SOUTHEAST_1A;
      String region = "ap-southeast-1";
      String zone = "ap-southeast-1a";

      String imageId = "ami1";
      String instanceCreatedId = "instance1";
      NodeMetadata nodeMetadata = new NodeMetadataBuilder().id(region + "/" + instanceCreatedId)
            .providerId(instanceCreatedId).status(Status.RUNNING).build();
      // setup mocks
      TemplateBuilder templateBuilder = createMock(TemplateBuilder.class);
      EC2CreateNodesInGroupThenAddToSet strategy = setupStrategy(templateBuilder, nodeMetadata);
      InputParams input = new InputParams(location);
      InstanceClient instanceClient = createMock(InstanceClient.class);
      ElasticIPAddressClient ipClient = createMock(ElasticIPAddressClient.class);
      RunInstancesOptions ec2Options = createMock(RunInstancesOptions.class);
      RunningInstance instance = createMock(RunningInstance.class);
      Reservation<? extends RunningInstance> reservation = new Reservation<RunningInstance>(region,
            ImmutableSet.<String> of(), ImmutableSet.<RunningInstance> of(instance), "ownerId", "requesterId",
            "reservationId");

      // enable auto-allocation
      strategy.autoAllocateElasticIps = true;

      // setup expectations
      expect(templateBuilder.imageId(region + "/" + imageId)).andReturn(templateBuilder);
      expect(templateBuilder.fromTemplate(input.template)).andReturn(templateBuilder);
      expect(templateBuilder.build()).andReturn(input.template);
      expect(strategy.client.getInstanceServices()).andReturn(instanceClient).atLeastOnce();
      expect(
            strategy.createKeyPairAndSecurityGroupsAsNeededAndReturncustomize
                  .execute(region, input.tag, input.template)).andReturn(ec2Options);
      expect(strategy.client.getElasticIPAddressServices()).andReturn(ipClient).atLeastOnce();

      expect(input.template.getLocation()).andReturn(input.location).atLeastOnce();
      expect(input.template.getImage()).andReturn(input.image).atLeastOnce();
      expect(input.image.getId()).andReturn(region + "/" + imageId).atLeastOnce();
      expect(input.image.getProviderId()).andReturn(imageId).atLeastOnce();

      // differences when ip allocation
      expect(ipClient.allocateAddressInRegion(region)).andReturn("1.1.1.1");
      expect(strategy.runningInstanceToNodeMetadata.apply(instance)).andReturn(nodeMetadata).atLeastOnce();
      ipClient.associateAddressInRegion(region, "1.1.1.1", instanceCreatedId);
      strategy.elasticIpCache.put(new RegionAndName(region, instanceCreatedId), "1.1.1.1");

      expect(instanceClient.runInstancesInRegion(region, zone, imageId, 1, input.count, ec2Options)).andReturn(
            Reservation.class.cast(reservation));
      expect(instance.getId()).andReturn(instanceCreatedId).atLeastOnce();
      // simulate a lazy credentials fetch
      Credentials creds = new Credentials("foo", "bar");
      expect(strategy.instanceToCredentials.apply(instance)).andReturn(creds);
      expect(instance.getRegion()).andReturn(region).atLeastOnce();
      expect(strategy.credentialStore.put("node#" + region + "/" + instanceCreatedId, creds)).andReturn(null);

      expect(strategy.instancePresent.apply(new RegionAndName(region, instanceCreatedId))).andReturn(true);
      expect(input.template.getOptions()).andReturn(input.options).atLeastOnce();

      expect(
            strategy.utils.customizeNodesAndAddToGoodMapOrPutExceptionIntoBadMap(eq(input.options),
                  containsNodeMetadata(nodeMetadata), eq(input.nodes), eq(input.badNodes), eq(input.customization)))
            .andReturn(null);

      // replay mocks
      replay(templateBuilder);
      replay(instanceClient);
      replay(ipClient);
      replay(ec2Options);
      replay(instance);
      input.replayMe();
      replayStrategy(strategy);

      // run
      strategy.execute(input.tag, input.count, input.template, input.nodes, input.badNodes, input.customization);

      // verify mocks
      verify(templateBuilder);
      verify(instanceClient);
      verify(ipClient);
      verify(ec2Options);
      verify(instance);
      input.verifyMe();
      verifyStrategy(strategy);
   }

   @Test
   public void testZoneAsALocation() {
      assertRegionAndZoneForLocation(ZONE_AP_SOUTHEAST_1A, "ap-southeast-1", "ap-southeast-1a");
   }

   @Test
   public void testRegionAsALocation() {
      assertRegionAndZoneForLocation(REGION_AP_SOUTHEAST_1, "ap-southeast-1", null);
   }

   // // fixtures

   public static Iterable<NodeMetadata> containsNodeMetadata(final NodeMetadata in) {
      reportMatcher(new IArgumentMatcher() {

         @Override
         public void appendTo(StringBuffer buffer) {
            buffer.append("contains(");
            buffer.append(in);
            buffer.append(")");
         }

         @Override
         public boolean matches(Object arg) {
            return Iterables.contains((Iterable<?>) arg, in);
         }

      });
      return null;
   }

   @SuppressWarnings("unchecked")
   private void assertRegionAndZoneForLocation(Location location, String region, String zone) {
      String imageId = "ami1";
      String instanceCreatedId = "instance1";
      NodeMetadata nodeMetadata = new NodeMetadataBuilder().id(region + "/" + instanceCreatedId)
            .providerId(instanceCreatedId).status(Status.RUNNING).build();

      // setup mocks
      TemplateBuilder templateBuilder = createMock(TemplateBuilder.class);
      EC2CreateNodesInGroupThenAddToSet strategy = setupStrategy(templateBuilder, nodeMetadata);
      InputParams input = new InputParams(location);
      InstanceClient instanceClient = createMock(InstanceClient.class);
      RunInstancesOptions ec2Options = createMock(RunInstancesOptions.class);
      RunningInstance instance = createMock(RunningInstance.class);
      Reservation<? extends RunningInstance> reservation = new Reservation<RunningInstance>(region,
            ImmutableSet.<String> of(), ImmutableSet.<RunningInstance> of(instance), "ownerId", "requesterId",
            "reservationId");

      // setup expectations
      expect(templateBuilder.imageId(region + "/" + imageId)).andReturn(templateBuilder);
      expect(templateBuilder.fromTemplate(input.template)).andReturn(templateBuilder);
      expect(templateBuilder.build()).andReturn(input.template);
      expect(strategy.client.getInstanceServices()).andReturn(instanceClient).atLeastOnce();
      expect(
            strategy.createKeyPairAndSecurityGroupsAsNeededAndReturncustomize
                  .execute(region, input.tag, input.template)).andReturn(ec2Options);
      expect(input.template.getLocation()).andReturn(input.location).atLeastOnce();
      expect(input.template.getImage()).andReturn(input.image).atLeastOnce();
      expect(input.image.getId()).andReturn(region + "/" + imageId).atLeastOnce();
      expect(input.image.getProviderId()).andReturn(imageId).atLeastOnce();
      expect(instanceClient.runInstancesInRegion(region, zone, imageId, 1, input.count, ec2Options)).andReturn(
            Reservation.class.cast(reservation));
      expect(instance.getId()).andReturn(instanceCreatedId).atLeastOnce();
      // simulate a lazy credentials fetch
      Credentials creds = new Credentials("foo", "bar");
      expect(strategy.instanceToCredentials.apply(instance)).andReturn(creds);
      expect(instance.getRegion()).andReturn(region).atLeastOnce();
      expect(strategy.credentialStore.put("node#" + region + "/" + instanceCreatedId, creds)).andReturn(null);

      expect(strategy.instancePresent.apply(new RegionAndName(region, instanceCreatedId))).andReturn(true);
      expect(input.template.getOptions()).andReturn(input.options).atLeastOnce();

      expect(strategy.runningInstanceToNodeMetadata.apply(instance)).andReturn(nodeMetadata);
      expect(
            strategy.utils.customizeNodesAndAddToGoodMapOrPutExceptionIntoBadMap(eq(input.options),
                  containsNodeMetadata(nodeMetadata), eq(input.nodes), eq(input.badNodes), eq(input.customization)))
            .andReturn(null);

      // replay mocks
      replay(templateBuilder);
      replay(instanceClient);
      replay(ec2Options);
      replay(instance);
      input.replayMe();
      replayStrategy(strategy);

      // run
      strategy.execute(input.tag, input.count, input.template, input.nodes, input.badNodes, input.customization);

      // verify mocks
      verify(templateBuilder);
      verify(instanceClient);
      verify(ec2Options);
      verify(instance);
      input.verifyMe();
      verifyStrategy(strategy);
   }

   private static final Location REGION_AP_SOUTHEAST_1 = new LocationBuilder().scope(LocationScope.REGION)
         .id("ap-southeast-1").description("ap-southeast-1")
         .parent(new LocationBuilder().scope(LocationScope.PROVIDER).id("aws-ec2").description("aws-ec2").build())
         .build();
   private static final Location ZONE_AP_SOUTHEAST_1A = new LocationBuilder().scope(LocationScope.ZONE)
         .id("ap-southeast-1a").description("ap-southeast-1a").parent(REGION_AP_SOUTHEAST_1).build();

   // /////////////////////////////////////////////////////////////////////
   @SuppressWarnings("unchecked")
   private static class InputParams {
      String tag = "foo";
      int count = 1;
      Template template = createMock(Template.class);
      Set<NodeMetadata> nodes = createMock(Set.class);
      Map<NodeMetadata, Exception> badNodes = createMock(Map.class);
      Multimap<NodeMetadata, CustomizationResponse> customization = createMock(Multimap.class);
      Hardware hardware = createMock(Hardware.class);
      Image image = createMock(Image.class);
      final Location location;
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);

      public InputParams(Location location) {
         this.location = location;
      }

      void replayMe() {
         replay(template);
         replay(hardware);
         replay(image);
         replay(nodes);
         replay(badNodes);
         replay(customization);
         replay(options);
      }

      void verifyMe() {
         verify(template);
         verify(hardware);
         verify(image);
         verify(nodes);
         verify(badNodes);
         verify(customization);
         verify(options);
      }
   }

   private void verifyStrategy(EC2CreateNodesInGroupThenAddToSet strategy) {
      verify(strategy.createKeyPairAndSecurityGroupsAsNeededAndReturncustomize);
      verify(strategy.client);
      verify(strategy.elasticIpCache);
      verify(strategy.instancePresent);
      verify(strategy.runningInstanceToNodeMetadata);
      verify(strategy.instanceToCredentials);
      verify(strategy.credentialStore);
      verify(strategy.utils);
   }

   @SuppressWarnings("unchecked")
   private EC2CreateNodesInGroupThenAddToSet setupStrategy(TemplateBuilder template, final NodeMetadata node) {
      EC2Client client = createMock(EC2Client.class);
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturncustomize = createMock(CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class);
      InstancePresent instancePresent = createMock(InstancePresent.class);
      RunningInstanceToNodeMetadata runningInstanceToNodeMetadata = createMock(RunningInstanceToNodeMetadata.class);
      LoadingCache<RunningInstance, Credentials> instanceToCredentials = createMock(LoadingCache.class);
      LoadingCache<RegionAndName, String> elasticIpCache = createMock(LoadingCache.class);
      GetNodeMetadataStrategy nodeRunning = new GetNodeMetadataStrategy(){

         @Override
         public NodeMetadata getNode(String input) {
            Assert.assertEquals(input, node.getId());
            return node;
         }
         
      };
      Map<String, Credentials> credentialStore = createMock(Map.class);
      ComputeUtils utils = createMock(ComputeUtils.class);
      return new EC2CreateNodesInGroupThenAddToSet(client, elasticIpCache, new AtomicNodeRunning(nodeRunning),
            Providers.<TemplateBuilder> of(template), createKeyPairAndSecurityGroupsAsNeededAndReturncustomize,
            instancePresent, runningInstanceToNodeMetadata, instanceToCredentials, credentialStore, utils);
   }

   private void replayStrategy(EC2CreateNodesInGroupThenAddToSet strategy) {
      replay(strategy.createKeyPairAndSecurityGroupsAsNeededAndReturncustomize);
      replay(strategy.client);
      replay(strategy.elasticIpCache);
      replay(strategy.instancePresent);
      replay(strategy.runningInstanceToNodeMetadata);
      replay(strategy.instanceToCredentials);
      replay(strategy.credentialStore);
      replay(strategy.utils);
   }

}
