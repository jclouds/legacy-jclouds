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

import static org.jclouds.util.Utils.checkNotEmpty;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.config.EC2ComputeServiceContextModule.GetRegionFromNodeOrDefault;
import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
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

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class EC2ComputeService extends BaseComputeService {
   private final EC2Client ec2Client;
   private final GetRegionFromNodeOrDefault getRegionFromNodeOrDefault;
   private final Map<RegionAndName, KeyPair> credentialsMap;
   private final Map<RegionAndName, String> securityGroupMap;

   @Inject
   protected EC2ComputeService(ComputeServiceContext context,
            Provider<Set<? extends Image>> images, Provider<Set<? extends Size>> sizes,
            Provider<Set<? extends Location>> locations, ListNodesStrategy listNodesStrategy,
            GetNodeMetadataStrategy getNodeMetadataStrategy,
            RunNodesAndAddToSetStrategy runNodesAndAddToSetStrategy,
            RebootNodeStrategy rebootNodeStrategy, DestroyNodeStrategy destroyNodeStrategy,
            Provider<TemplateBuilder> templateBuilderProvider, ComputeUtils utils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, EC2Client ec2Client,
            GetRegionFromNodeOrDefault getRegionFromNodeOrDefault,
            Map<RegionAndName, KeyPair> credentialsMap,
            Map<RegionAndName, String> securityGroupMap) {
      super(context, images, sizes, locations, listNodesStrategy, getNodeMetadataStrategy,
               runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy,
               templateBuilderProvider, utils, executor);
      this.ec2Client = ec2Client;
      this.getRegionFromNodeOrDefault = getRegionFromNodeOrDefault;
      this.credentialsMap = credentialsMap;
      this.securityGroupMap = securityGroupMap;
   }

   private void deleteSecurityGroup(String region, String tag) {
      checkNotEmpty(tag, "tag");
      String group = "jclouds#" + tag;
      if (ec2Client.getSecurityGroupServices().describeSecurityGroupsInRegion(region, group).size() > 0) {
         logger.debug(">> deleting securityGroup(%s)", group);
         ec2Client.getSecurityGroupServices().deleteSecurityGroupInRegion(region, group);
         // TODO: test this clear happens
         securityGroupMap.remove(new RegionNameAndIngressRules(region, tag, null, false));
         logger.debug("<< deleted securityGroup(%s)", group);
      }
   }

   private void deleteKeyPair(String region, String tag) {
      for (KeyPair keyPair : ec2Client.getKeyPairServices().describeKeyPairsInRegion(region)) {
         if (keyPair.getKeyName().matches("jclouds#" + tag + "-[0-9]+")) {
            logger.debug(">> deleting keyPair(%s)", keyPair.getKeyName());
            ec2Client.getKeyPairServices().deleteKeyPairInRegion(region, keyPair.getKeyName());
            // TODO: test this clear happens
            credentialsMap.remove(new RegionAndName(region, keyPair.getKeyName()));
            logger.debug("<< deleted keyPair(%s)", keyPair.getKeyName());
         }
      }
   }

   @Override
   public void destroyNodesWithTag(String tag) {
      super.destroyNodesWithTag(tag);
      Set<String> regions = Sets.newHashSet(Iterables.transform(listNodesWithTag(tag),
               new Function<NodeMetadata, String>() {
                  @Override
                  public String apply(@Nullable NodeMetadata nodeMetadata) {
                     return getRegionFromNodeOrDefault.apply(nodeMetadata);
                  }
               }));
      for (String region : regions) {
         deleteKeyPair(region, tag);
         deleteSecurityGroup(region, tag);
      }
   }
}