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

package org.jclouds.aws.ec2.compute;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.aws.ec2.util.EC2Utils.parseHandle;
import static org.jclouds.util.Utils.checkNotEmpty;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.aws.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.aws.ec2.domain.PlacementGroup.State;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Location;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
@Singleton
public class EC2ComputeService extends BaseComputeService {
   private final EC2Client ec2Client;
   private final Map<RegionAndName, KeyPair> credentialsMap;
   private final Map<RegionAndName, String> securityGroupMap;
   private final Map<RegionAndName, String> placementGroupMap;
   private final Predicate<PlacementGroup> placementGroupDeleted;

   @Inject
   protected EC2ComputeService(ComputeServiceContext context, Provider<Set<? extends Image>> images,
            Provider<Set<? extends Size>> sizes, Provider<Set<? extends Location>> locations,
            ListNodesStrategy listNodesStrategy, GetNodeMetadataStrategy getNodeMetadataStrategy,
            RunNodesAndAddToSetStrategy runNodesAndAddToSetStrategy, RebootNodeStrategy rebootNodeStrategy,
            DestroyNodeStrategy destroyNodeStrategy, Provider<TemplateBuilder> templateBuilderProvider,
            Provider<TemplateOptions> templateOptionsProvider,
            @Named("NODE_RUNNING") Predicate<NodeMetadata> nodeRunning,
            @Named("NODE_TERMINATED") Predicate<NodeMetadata> nodeTerminated, ComputeUtils utils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, EC2Client ec2Client,
            Map<RegionAndName, KeyPair> credentialsMap, @Named("SECURITY") Map<RegionAndName, String> securityGroupMap,
            @Named("PLACEMENT") Map<RegionAndName, String> placementGroupMap,
            @Named("DELETED") Predicate<PlacementGroup> placementGroupDeleted) {
      super(context, images, sizes, locations, listNodesStrategy, getNodeMetadataStrategy, runNodesAndAddToSetStrategy,
               rebootNodeStrategy, destroyNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning,
               nodeTerminated, utils, executor);
      this.ec2Client = ec2Client;
      this.credentialsMap = credentialsMap;
      this.securityGroupMap = securityGroupMap;
      this.placementGroupMap = placementGroupMap;
      this.placementGroupDeleted = placementGroupDeleted;
   }

   @VisibleForTesting
   void deletePlacementGroup(String region, String tag) {
      checkNotEmpty(tag, "tag");
      String group = String.format("jclouds#%s#%s", tag, region);
      if (ec2Client.getPlacementGroupServices().describePlacementGroupsInRegion(region, group).size() > 0) {
         logger.debug(">> deleting placementGroup(%s)", group);
         try {
            ec2Client.getPlacementGroupServices().deletePlacementGroupInRegion(region, group);
            checkState(placementGroupDeleted.apply(new PlacementGroup(region, group, "cluster", State.PENDING)), String
                     .format("placementGroup region(%s) name(%s) failed to delete", region, group));
            placementGroupMap.remove(new RegionAndName(region, tag));
            logger.debug("<< deleted placementGroup(%s)", group);
         } catch (AWSResponseException e) {
            if (e.getError().getCode().equals("InvalidPlacementGroup.InUse")) {
               logger.debug("<< inUse placementGroup(%s)", group);
            } else {
               throw e;
            }
         }
      }
   }

   @VisibleForTesting
   void deleteSecurityGroup(String region, String tag) {
      checkNotEmpty(tag, "tag");
      String group = String.format("jclouds#%s#%s", tag, region);
      if (ec2Client.getSecurityGroupServices().describeSecurityGroupsInRegion(region, group).size() > 0) {
         logger.debug(">> deleting securityGroup(%s)", group);
         ec2Client.getSecurityGroupServices().deleteSecurityGroupInRegion(region, group);
         // TODO: test this clear happens
         securityGroupMap.remove(new RegionNameAndIngressRules(region, tag, null, false));
         logger.debug("<< deleted securityGroup(%s)", group);
      }
   }

   @VisibleForTesting
   void deleteKeyPair(String region, String tag) {
      for (KeyPair keyPair : ec2Client.getKeyPairServices().describeKeyPairsInRegion(region)) {
         if (keyPair.getKeyName().matches(String.format("jclouds#%s#%s#%s", tag, region, "[0-9a-f]+"))) {
            logger.debug(">> deleting keyPair(%s)", keyPair.getKeyName());
            ec2Client.getKeyPairServices().deleteKeyPairInRegion(region, keyPair.getKeyName());
            // TODO: test this clear happens
            credentialsMap.remove(new RegionAndName(region, keyPair.getKeyName()));
            logger.debug("<< deleted keyPair(%s)", keyPair.getKeyName());
         }
      }
   }

   /**
    * like {@link BaseComputeService#destroyNodesMatching} except that this will clean implicit
    * keypairs and security groups.
    */
   @Override
   public Set<? extends NodeMetadata> destroyNodesMatching(Predicate<NodeMetadata> filter) {
      Set<? extends NodeMetadata> deadOnes = super.destroyNodesMatching(filter);
      Map<String, String> regionTags = Maps.newHashMap();
      for (NodeMetadata nodeMetadata : deadOnes) {
         if (nodeMetadata.getTag() != null)
            regionTags.put(parseHandle(nodeMetadata.getId())[0], nodeMetadata.getTag());
      }
      for (Entry<String, String> regionTag : regionTags.entrySet()) {
         deleteKeyPair(regionTag.getKey(), regionTag.getValue());
         deleteSecurityGroup(regionTag.getKey(), regionTag.getValue());
         deletePlacementGroup(regionTag.getKey(), regionTag.getValue());
      }
      return deadOnes;
   }

   /**
    * returns template options, except of type {@link EC2TemplateOptions}.
    */
   @Override
   public EC2TemplateOptions templateOptions() {
      return EC2TemplateOptions.class.cast(super.templateOptions());
   }

}