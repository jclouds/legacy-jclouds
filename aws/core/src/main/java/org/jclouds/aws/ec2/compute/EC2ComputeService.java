/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.compute;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.config.EC2ComputeServiceContextModule.GetRegionFromNodeOrDefault;
import org.jclouds.aws.ec2.compute.domain.PortsRegionTag;
import org.jclouds.aws.ec2.compute.domain.RegionTag;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Location;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class EC2ComputeService extends BaseComputeService {
   protected final EC2Client ec2Client;
   protected final GetRegionFromNodeOrDefault getRegionFromNodeOrDefault;
   protected final Map<RegionTag, KeyPair> credentialsMap;
   protected final Map<PortsRegionTag, String> securityGroupMap;
   protected final Predicate<RunningInstance> instanceStateTerminated;

   @Inject
   protected EC2ComputeService(ComputeServiceContext context,
            Provider<Map<String, ? extends Image>> images,
            Provider<Map<String, ? extends Size>> sizes,
            Provider<Map<String, ? extends Location>> locations,
            ListNodesStrategy listNodesStrategy, GetNodeMetadataStrategy getNodeMetadataStrategy,
            RunNodesAndAddToSetStrategy runNodesAndAddToSetStrategy,
            RebootNodeStrategy rebootNodeStrategy, DestroyNodeStrategy destroyNodeStrategy,
            Provider<TemplateBuilder> templateBuilderProvider, ComputeUtils utils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, EC2Client ec2Client,
            GetRegionFromNodeOrDefault getRegionFromNodeOrDefault,
            Map<RegionTag, KeyPair> credentialsMap, Map<PortsRegionTag, String> securityGroupMap,
            @Named("TERMINATED") Predicate<RunningInstance> instanceStateTerminated) {
      super(context, images, sizes, locations, listNodesStrategy, getNodeMetadataStrategy,
               runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy,
               templateBuilderProvider, utils, executor);
      this.ec2Client = ec2Client;
      this.getRegionFromNodeOrDefault = getRegionFromNodeOrDefault;
      this.credentialsMap = credentialsMap;
      this.securityGroupMap = securityGroupMap;
      this.instanceStateTerminated = instanceStateTerminated;
   }

   private void deleteSecurityGroup(String region, String tag) {
      if (ec2Client.getSecurityGroupServices().describeSecurityGroupsInRegion(region, tag).size() > 0) {
         logger.debug(">> deleting securityGroup(%s)", tag);
         ec2Client.getSecurityGroupServices().deleteSecurityGroupInRegion(region, tag);
         securityGroupMap.remove(new PortsRegionTag(region, tag, null)); // TODO: test this clear
         // happens
         logger.debug("<< deleted securityGroup(%s)", tag);
      }
   }

   private void deleteKeyPair(String region, String tag) {
      for (KeyPair keyPair : ec2Client.getKeyPairServices().describeKeyPairsInRegion(region)) {
         if (keyPair.getKeyName().matches(tag + "-[0-9]+")) {
            logger.debug(">> deleting keyPair(%s)", tag);
            ec2Client.getKeyPairServices().deleteKeyPairInRegion(region, keyPair.getKeyName());
            credentialsMap.remove(new RegionTag(region, keyPair.getKeyName())); // TODO: test this
                                                                                // clear happens
            logger.debug("<< deleted keyPair(%s)", keyPair.getKeyName());
         }
      }
   }

   @Override
   public void destroyNodesWithTag(String tag) {
      super.destroyNodesWithTag(tag);
      Iterable<? extends NodeMetadata> nodes = Iterables.filter(getNodesWithTag(tag).values(),
               new Predicate<NodeMetadata>() {
                  @Override
                  public boolean apply(NodeMetadata input) {
                     return input.getState() == NodeState.TERMINATED;
                  }
               });
      if (Iterables.size(nodes) > 0) {
         String region = getRegionFromNodeOrDefault.apply(Iterables.get(nodes, 0));
         deleteKeyPair(region, tag);
         deleteSecurityGroup(region, tag);
      }
   }
}