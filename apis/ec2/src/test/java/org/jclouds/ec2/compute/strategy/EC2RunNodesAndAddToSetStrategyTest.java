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

package org.jclouds.ec2.compute.strategy;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.Map;
import java.util.Set;

import org.easymock.IArgumentMatcher;
import org.jclouds.aws.domain.Region;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.functions.RunningInstanceToNodeMetadata;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.AvailabilityZone;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.services.InstanceClient;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class EC2RunNodesAndAddToSetStrategyTest {

   @Test
   public void testZoneAsALocation() {
      assertRegionAndZoneForLocation(ZONE_AP_SOUTHEAST_1A, Region.AP_SOUTHEAST_1, AvailabilityZone.AP_SOUTHEAST_1A);
   }

   @Test
   public void testRegionAsALocation() {
      assertRegionAndZoneForLocation(REGION_AP_SOUTHEAST_1, Region.AP_SOUTHEAST_1, null);
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
      // setup mocks
      EC2CreateNodesInGroupThenAddToSet strategy = setupStrategy();
      InputParams input = new InputParams(location);
      InstanceClient instanceClient = createMock(InstanceClient.class);
      RunInstancesOptions ec2Options = createMock(RunInstancesOptions.class);
      RunningInstance instance = createMock(RunningInstance.class);
      Reservation<? extends RunningInstance> reservation = new Reservation<RunningInstance>(region, ImmutableSet
               .<String> of(), ImmutableSet.<RunningInstance> of(instance), "ownerId", "requesterId", "reservationId");
      NodeMetadata nodeMetadata = createMock(NodeMetadata.class);

      // setup expectations
      expect(strategy.client.getInstanceServices()).andReturn(instanceClient).atLeastOnce();
      expect(
               strategy.createKeyPairAndSecurityGroupsAsNeededAndReturncustomize.execute(region, input.tag,
                        input.template)).andReturn(ec2Options);
      expect(input.template.getLocation()).andReturn(input.location).atLeastOnce();
      expect(input.template.getImage()).andReturn(input.image).atLeastOnce();
      expect(input.image.getProviderId()).andReturn(imageId).atLeastOnce();
      expect(instanceClient.runInstancesInRegion(region, zone, imageId, 1, input.count, ec2Options)).andReturn(
               (Reservation) reservation);
      expect(instance.getId()).andReturn(instanceCreatedId).atLeastOnce();
      // simulate a lazy credentials fetch
      Credentials creds = new Credentials("foo", "bar");
      expect(strategy.instanceToCredentials.apply(instance)).andReturn(creds);
      expect(instance.getRegion()).andReturn(region);
      expect(strategy.credentialStore.put("node#" + region + "/" + instanceCreatedId, creds)).andReturn(null);

      expect(strategy.instancePresent.apply(instance)).andReturn(true);
      expect(input.template.getOptions()).andReturn(input.options).atLeastOnce();

      expect(strategy.runningInstanceToNodeMetadata.apply(instance)).andReturn(nodeMetadata);
      expect(
               strategy.utils.customizeNodesAndAddToGoodMapOrPutExceptionIntoBadMap(eq(input.options),
                        containsNodeMetadata(nodeMetadata), eq(input.nodes), eq(input.badNodes),
                        eq(input.customization))).andReturn(null);

      // replay mocks
      replay(instanceClient);
      replay(ec2Options);
      replay(instance);
      replay(nodeMetadata);
      input.replayMe();
      replayStrategy(strategy);

      // run
      strategy.execute(input.tag, input.count, input.template, input.nodes, input.badNodes, input.customization);

      // verify mocks
      verify(instanceClient);
      verify(ec2Options);
      verify(instance);
      verify(nodeMetadata);
      input.verifyMe();
      verifyStrategy(strategy);
   }

   private static final Location REGION_AP_SOUTHEAST_1 = new LocationBuilder().scope(LocationScope.REGION).id(
            Region.AP_SOUTHEAST_1).description(Region.AP_SOUTHEAST_1).parent(
            new LocationBuilder().scope(LocationScope.PROVIDER).id("ec2").description("ec2").build()).build();
   private static final Location ZONE_AP_SOUTHEAST_1A = new LocationBuilder().scope(LocationScope.ZONE).id(
            AvailabilityZone.AP_SOUTHEAST_1A).description(AvailabilityZone.AP_SOUTHEAST_1A).parent(
            REGION_AP_SOUTHEAST_1).build();

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
      verify(strategy.instancePresent);
      verify(strategy.runningInstanceToNodeMetadata);
      verify(strategy.instanceToCredentials);
      verify(strategy.credentialStore);
      verify(strategy.utils);
   }

   @SuppressWarnings("unchecked")
   private EC2CreateNodesInGroupThenAddToSet setupStrategy() {
      EC2Client client = createMock(EC2Client.class);
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturncustomize = createMock(CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class);
      Predicate<RunningInstance> instanceStateRunning = createMock(Predicate.class);
      RunningInstanceToNodeMetadata runningInstanceToNodeMetadata = createMock(RunningInstanceToNodeMetadata.class);
      Function<RunningInstance, Credentials> instanceToCredentials = createMock(Function.class);
      Map<String, Credentials> credentialStore = createMock(Map.class);
      ComputeUtils utils = createMock(ComputeUtils.class);
      return new EC2CreateNodesInGroupThenAddToSet(client, createKeyPairAndSecurityGroupsAsNeededAndReturncustomize,
               instanceStateRunning, runningInstanceToNodeMetadata, instanceToCredentials, credentialStore, utils);
   }

   private void replayStrategy(EC2CreateNodesInGroupThenAddToSet strategy) {
      replay(strategy.createKeyPairAndSecurityGroupsAsNeededAndReturncustomize);
      replay(strategy.client);
      replay(strategy.instancePresent);
      replay(strategy.runningInstanceToNodeMetadata);
      replay(strategy.instanceToCredentials);
      replay(strategy.credentialStore);
      replay(strategy.utils);
   }

}
