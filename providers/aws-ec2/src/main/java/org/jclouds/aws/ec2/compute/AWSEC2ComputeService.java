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

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.aws.ec2.domain.PlacementGroup.State;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.InitializeRunScriptOnNodeOrPlaceInBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.ec2.compute.EC2ComputeService;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.util.Preconditions2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Singleton
public class AWSEC2ComputeService extends EC2ComputeService {

   private final Map<RegionAndName, String> placementGroupMap;
   private final Predicate<PlacementGroup> placementGroupDeleted;
   private final AWSEC2Client ec2Client;

   @Inject
   protected AWSEC2ComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
            @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> sizes,
            @Memoized Supplier<Set<? extends Location>> locations, ListNodesStrategy listNodesStrategy,
            GetNodeMetadataStrategy getNodeMetadataStrategy, CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy,
            RebootNodeStrategy rebootNodeStrategy, DestroyNodeStrategy destroyNodeStrategy,
            ResumeNodeStrategy startNodeStrategy, SuspendNodeStrategy stopNodeStrategy,
            Provider<TemplateBuilder> templateBuilderProvider, Provider<TemplateOptions> templateOptionsProvider,
            @Named("NODE_RUNNING") Predicate<NodeMetadata> nodeRunning,
            @Named("NODE_TERMINATED") Predicate<NodeMetadata> nodeTerminated,
            @Named("NODE_SUSPENDED") Predicate<NodeMetadata> nodeSuspended,
            InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory, Timeouts timeouts,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, AWSEC2Client ec2Client,
            Map<RegionAndName, KeyPair> credentialsMap, @Named("SECURITY") Map<RegionAndName, String> securityGroupMap,
            @Named("PLACEMENT") Map<RegionAndName, String> placementGroupMap,
            @Named("DELETED") Predicate<PlacementGroup> placementGroupDeleted) {
      super(context, credentialStore, images, sizes, locations, listNodesStrategy, getNodeMetadataStrategy,
               runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy, startNodeStrategy,
               stopNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning, nodeTerminated,
               nodeSuspended, initScriptRunnerFactory, timeouts, executor, ec2Client, credentialsMap, securityGroupMap);
      this.ec2Client = ec2Client;
      this.placementGroupMap = placementGroupMap;
      this.placementGroupDeleted = placementGroupDeleted;
   }

   @VisibleForTesting
   void deletePlacementGroup(String region, String tag) {
      Preconditions2.checkNotEmpty(tag, "tag");
      String group = String.format("jclouds#%s#%s", tag, region);
      try {
         if (ec2Client.getPlacementGroupServices().describePlacementGroupsInRegion(region, group).size() > 0) {
            logger.debug(">> deleting placementGroup(%s)", group);
            try {
               ec2Client.getPlacementGroupServices().deletePlacementGroupInRegion(region, group);
               checkState(placementGroupDeleted.apply(new PlacementGroup(region, group, "cluster", State.PENDING)),
                        String.format("placementGroup region(%s) name(%s) failed to delete", region, group));
               placementGroupMap.remove(new RegionAndName(region, group));
               logger.debug("<< deleted placementGroup(%s)", group);
            } catch (IllegalStateException e) {
               logger.debug("<< inUse placementGroup(%s)", group);
            }
         }
      } catch (UnsupportedOperationException e) {
         logger.trace("<< placementGroups unsupported in region %s", region);
      }
   }

   protected void cleanUpIncidentalResources(Entry<String, String> regionTag) {
      super.cleanUpIncidentalResources(regionTag);
      deletePlacementGroup(regionTag.getKey(), regionTag.getValue());
   }

   /**
    * returns template options, except of type {@link EC2TemplateOptions}.
    */
   @Override
   public EC2TemplateOptions templateOptions() {
      return EC2TemplateOptions.class.cast(super.templateOptions());
   }

}
