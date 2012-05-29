/**
 * Lic§ensed to jclouds, Inc. (jclouds) under one or more
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
package org.jclouds.aws.ec2.compute.strategy;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.easymock.IArgumentMatcher;
import org.jclouds.aws.ec2.AWSEC2AsyncClient;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.compute.predicates.AWSEC2InstancePresent;
import org.jclouds.aws.ec2.functions.SpotInstanceRequestToAWSRunningInstance;
import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.aws.ec2.services.AWSInstanceClient;
import org.jclouds.aws.ec2.services.TagAsyncClient;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.predicates.AtomicNodeRunning;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.RunningInstanceToNodeMetadata;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.services.ElasticIPAddressClient;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.inject.util.Providers;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "EC2CreateNodesInGroupThenAddToSetTest")
public class AWSEC2CreateNodesInGroupThenAddToSetTest {

   public void testTagsIncludingNameAndGroup() {
      Location location = ZONE_AP_SOUTHEAST_1A;
      InputParams input = new InputParams(location);
      NodeMetadata nodeMetadata = new NodeMetadataBuilder()
            .id(input.region + "/" + input.instanceCreatedId)
            .providerId(input.instanceCreatedId)
            .state(NodeState.RUNNING)
            .userMetadata(ImmutableMap.of("Key", "Value"))
            .tags(ImmutableSet.of("Tag"))
            .build();

      // setup mocks
 
      // setup expectations
      expect(input.templateBuilder.imageId(input.region + "/" + input.imageId)).andReturn(input.templateBuilder);
      expect(input.templateBuilder.fromTemplate(input.template)).andReturn(input.templateBuilder);
      expect(input.templateBuilder.build()).andReturn(input.template);
      expect(input.template.getOptions()).andReturn(input.options).atLeastOnce();
      expect(input.template.getLocation()).andReturn(input.location).atLeastOnce();
      expect(input.template.getImage()).andReturn(input.image).atLeastOnce();
      expect(input.options.getUserMetadata()).andReturn(nodeMetadata.getUserMetadata()).atLeastOnce();
      expect(input.options.getTags()).andReturn(nodeMetadata.getTags()).atLeastOnce();
      expect(input.options.getSpotPrice()).andReturn(null).atLeastOnce();
      expect(input.image.getId()).andReturn(input.region + "/" + input.imageId).atLeastOnce();
      expect(input.image.getProviderId()).andReturn(input.imageId).atLeastOnce();
      expect(input.instanceClient.runInstancesInRegion(input.region, input.zone, input.imageId, 1, input.count, input.runOptions)).andReturn(Reservation.class.cast(input.reservation)).atLeastOnce();
      expect(input.instance.getId()).andReturn(input.instanceCreatedId).atLeastOnce();
      expect(input.instance.getRegion()).andReturn(input.region).atLeastOnce();

      // create strategy
      AWSEC2CreateNodesInGroupThenAddToSet strategy = setupStrategy(input, input.templateBuilder, nodeMetadata);
      expect(strategy.client.getInstanceServices()).andReturn(input.instanceClient).atLeastOnce();

      // replay mocks
      replay(input.templateBuilder);
      replay(input.instanceClient);
      replay(input.ipClient);
      replay(input.runOptions);
      replay(input.instance);
      input.replayMe();
      replayStrategy(strategy);

      // run
      strategy.execute(input.group, input.count, input.template, input.nodes, input.badNodes, input.customization);

      // verify mocks
      verify(input.templateBuilder);
      verify(input.instanceClient);
      verify(input.ipClient);
      verify(input.runOptions);
      verify(input.instance);
      input.verifyMe();
      verifyStrategy(strategy);

      // check metadata
      Map<String, String> actual = strategy.metadataForId(input.instanceCreatedId, input.group, ComputeServiceUtils.metadataAndTagsAsValuesOfEmptyString(input.options));
      assertEquals(actual, input.metadata, "The metadata should contain name andf group information");
   }

   /*
    * Fixtures
    *
    * TODO make this cleaner?
    */

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

   private static final Location REGION_AP_SOUTHEAST_1 = new LocationBuilder().scope(LocationScope.REGION)
         .id("ap-southeast-1").description("ap-southeast-1")
         .parent(new LocationBuilder().scope(LocationScope.PROVIDER).id("aws-ec2").description("aws-ec2").build())
         .build();
   private static final Location ZONE_AP_SOUTHEAST_1A = new LocationBuilder().scope(LocationScope.ZONE)
         .id("ap-southeast-1a").description("ap-southeast-1a").parent(REGION_AP_SOUTHEAST_1).build();

   private static class InputParams {
      String region = "ap-southeast-1";
      String zone = "ap-southeast-1a";
      String imageId = "ami1";
      String instanceCreatedId = "instance1";
      String group = "foo";
      Map<String, String> metadata = ImmutableMap.of("Name", instanceCreatedId, "Group", group, "Key", "Value", "Tag", "");
      int count = 1;
      final Location location;

      // mocks
      Template template = createMock(Template.class);
      Set<NodeMetadata> nodes = createMock(Set.class);
      Map<NodeMetadata, Exception> badNodes = createMock(Map.class);
      Multimap<NodeMetadata, CustomizationResponse> customization = createMock(Multimap.class);
      Hardware hardware = createMock(Hardware.class);
      Image image = createMock(Image.class);
      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);
      TemplateBuilder templateBuilder = createMock(TemplateBuilder.class);
      AWSInstanceClient instanceClient = createMock(AWSInstanceClient.class);
      ElasticIPAddressClient ipClient = createMock(ElasticIPAddressClient.class);
      AWSRunInstancesOptions runOptions = createMock(AWSRunInstancesOptions.class);
      RunningInstance instance = createMock(RunningInstance.class);
      Reservation<? extends RunningInstance> reservation = new Reservation<RunningInstance>(region,
            ImmutableSet.<String>of(), ImmutableSet.<RunningInstance>of(instance), "ownerId", "requesterId",
            "reservationId");
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturncustomize = createMock(CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions.class);

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
         replay(createKeyPairAndSecurityGroupsAsNeededAndReturncustomize);
      }

      void verifyMe() {
         verify(template);
         verify(hardware);
         verify(image);
         verify(nodes);
         verify(badNodes);
         verify(customization);
         verify(options);
         verify(createKeyPairAndSecurityGroupsAsNeededAndReturncustomize);
      }
   }

   private void verifyStrategy(AWSEC2CreateNodesInGroupThenAddToSet strategy) {
      verify(strategy.aclient);
      verify(strategy.client);
      verify(strategy.spotConverter);
   }

   private AWSEC2CreateNodesInGroupThenAddToSet setupStrategy(InputParams input, TemplateBuilder templateBuilder, final NodeMetadata node) {
      AWSEC2Client client = createMock(AWSEC2Client.class);
      AWSEC2AsyncClient aclient = createMock(AWSEC2AsyncClient.class);
      TagAsyncClient tagclient = createMock(TagAsyncClient.class);
      LoadingCache<RegionAndName, String> elasticIpCache = createMock(LoadingCache.class);

      GetNodeMetadataStrategy nodeRunning = new GetNodeMetadataStrategy() {
          @Override
          public NodeMetadata getNode(String input) {
             Assert.assertEquals(input, node.getId());
             return node;
          }
       };
      boolean generateInstanceNames = true;
      boolean encodeGroupInTags = true;

      expect(aclient.getTagServices()).andReturn(tagclient).atLeastOnce();
      expect(tagclient.createTagsInRegion(input.region, ImmutableSet.of(input.instanceCreatedId), input.metadata)).andReturn(null).atLeastOnce();
      expect(input.createKeyPairAndSecurityGroupsAsNeededAndReturncustomize.execute(input.region, input.group, input.template)).andReturn(input.runOptions).atLeastOnce();

      AWSEC2InstancePresent instancePresent = createMock(AWSEC2InstancePresent.class);
      RunningInstanceToNodeMetadata runningInstanceToNodeMetadata = createMock(RunningInstanceToNodeMetadata.class);
      LoadingCache<RunningInstance, Credentials> instanceToCredentials = createMock(LoadingCache.class);
      Map<String, Credentials> credentialStore = createMock(Map.class);
      ComputeUtils utils = createMock(ComputeUtils.class);
      SpotInstanceRequestToAWSRunningInstance spotConverter = createMock(SpotInstanceRequestToAWSRunningInstance.class);

      return new AWSEC2CreateNodesInGroupThenAddToSet(
              client,
              elasticIpCache,
              new AtomicNodeRunning(nodeRunning),
              aclient,
              generateInstanceNames,
              encodeGroupInTags,
              Providers.<TemplateBuilder>of(templateBuilder),
              input.createKeyPairAndSecurityGroupsAsNeededAndReturncustomize,
              instancePresent,
              runningInstanceToNodeMetadata,
              instanceToCredentials,
              credentialStore,
              utils,
              spotConverter);
   }

   private void replayStrategy(AWSEC2CreateNodesInGroupThenAddToSet strategy) {
      replay(strategy.client);
      replay(strategy.aclient);
      replay(strategy.spotConverter);
   }
}
