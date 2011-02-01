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

package org.jclouds.ec2.compute;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeService;
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
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.util.Preconditions2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
@Singleton
public class EC2ComputeService extends BaseComputeService {
   private final EC2Client ec2Client;
   private final Map<RegionAndName, KeyPair> credentialsMap;
   private final Map<RegionAndName, String> securityGroupMap;

   @Inject
   protected EC2ComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
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
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, EC2Client ec2Client,
            Map<RegionAndName, KeyPair> credentialsMap, @Named("SECURITY") Map<RegionAndName, String> securityGroupMap) {
      super(context, credentialStore, images, sizes, locations, listNodesStrategy, getNodeMetadataStrategy,
               runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy, startNodeStrategy,
               stopNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning, nodeTerminated,
               nodeSuspended, initScriptRunnerFactory, timeouts, executor);
      this.ec2Client = ec2Client;
      this.credentialsMap = credentialsMap;
      this.securityGroupMap = securityGroupMap;

   }

   @VisibleForTesting
   void deleteSecurityGroup(String region, String group) {
      Preconditions2.checkNotEmpty(group, "group");
      String groupName = String.format("jclouds#%s#%s", group, region);
      if (ec2Client.getSecurityGroupServices().describeSecurityGroupsInRegion(region, groupName).size() > 0) {
         logger.debug(">> deleting securityGroup(%s)", groupName);
         try {
            ec2Client.getSecurityGroupServices().deleteSecurityGroupInRegion(region, groupName);
            // TODO: test this clear happens
            securityGroupMap.remove(new RegionNameAndIngressRules(region, groupName, null, false));
            logger.debug("<< deleted securityGroup(%s)", groupName);
         } catch (IllegalStateException e) {
            logger.debug("<< inUse securityGroup(%s)", groupName);
         }
      }
   }

   @VisibleForTesting
   void deleteKeyPair(String region, String group) {
      for (KeyPair keyPair : ec2Client.getKeyPairServices().describeKeyPairsInRegion(region)) {
         if (keyPair.getKeyName().matches(String.format("jclouds#%s#%s#%s", group, region, "[0-9a-f]+"))) {
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
         if (nodeMetadata.getGroup() != null)
            regionTags.put(AWSUtils.parseHandle(nodeMetadata.getId())[0], nodeMetadata.getGroup());
      }
      for (Entry<String, String> regionTag : regionTags.entrySet()) {
         cleanUpIncidentalResources(regionTag);
      }
      return deadOnes;
   }

   protected void cleanUpIncidentalResources(Entry<String, String> regionTag) {
      deleteKeyPair(regionTag.getKey(), regionTag.getValue());
      deleteSecurityGroup(regionTag.getKey(), regionTag.getValue());
   }

   /**
    * returns template options, except of type {@link EC2TemplateOptions}.
    */
   @Override
   public EC2TemplateOptions templateOptions() {
      return EC2TemplateOptions.class.cast(super.templateOptions());
   }

}
