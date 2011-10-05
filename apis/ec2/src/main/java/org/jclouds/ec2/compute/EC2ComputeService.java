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
package org.jclouds.ec2.compute;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.util.Preconditions2.checkNotEmpty;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.internal.PersistNodeCredentials;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.InitializeRunScriptOnNodeOrPlaceInBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class EC2ComputeService extends BaseComputeService {
   private final EC2Client ec2Client;
   private final Cache<RegionAndName, KeyPair> credentialsMap;
   private final Cache<RegionAndName, String> securityGroupMap;

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
         InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory,
         RunScriptOnNode.Factory runScriptOnNodeFactory, InitAdminAccess initAdminAccess,
         PersistNodeCredentials persistNodeCredentials, Timeouts timeouts,
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, EC2Client ec2Client,
         Cache<RegionAndName, KeyPair> credentialsMap, @Named("SECURITY") Cache<RegionAndName, String> securityGroupMap) {
      super(context, credentialStore, images, sizes, locations, listNodesStrategy, getNodeMetadataStrategy,
            runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy, startNodeStrategy, stopNodeStrategy,
            templateBuilderProvider, templateOptionsProvider, nodeRunning, nodeTerminated, nodeSuspended,
            initScriptRunnerFactory, initAdminAccess, runScriptOnNodeFactory, persistNodeCredentials, timeouts,
            executor);
      this.ec2Client = ec2Client;
      this.credentialsMap = credentialsMap;
      this.securityGroupMap = securityGroupMap;
   }

   /**
    * @throws IllegalStateException If fails to delete security because it's in use by existing VMs
    */
   @VisibleForTesting
   void deleteSecurityGroup(String region, String group) {
      checkNotEmpty(region, "region");
      checkNotEmpty(group, "group");
      String groupName = String.format("jclouds#%s#%s", group, region);
      if (ec2Client.getSecurityGroupServices().describeSecurityGroupsInRegion(region, groupName).size() > 0) {
         logger.debug(">> deleting securityGroup(%s)", groupName);
         ec2Client.getSecurityGroupServices().deleteSecurityGroupInRegion(region, groupName);
         // TODO: test this clear happens
         securityGroupMap.invalidate(new RegionNameAndIngressRules(region, groupName, null, false));
         logger.debug("<< deleted securityGroup(%s)", groupName);
      }
   }

   @VisibleForTesting
   void deleteKeyPair(String region, String group) {
      for (KeyPair keyPair : ec2Client.getKeyPairServices().describeKeyPairsInRegion(region)) {
         if (
         // when the keypair is unique per group
         keyPair.getKeyName().equals("jclouds#" + group)
               || keyPair.getKeyName().matches(String.format("jclouds#%s#%s", group, "[0-9a-f]+"))
               // old keypair pattern too verbose as it has an unnecessary
               // region qualifier
               || keyPair.getKeyName().matches(String.format("jclouds#%s#%s#%s", group, region, "[0-9a-f]+"))) {
            Set<String> instancesUsingKeyPair = extractIdsFromInstances(filter(concat(ec2Client.getInstanceServices()
                  .describeInstancesInRegion(region)), usingKeyPairAndNotDead(keyPair)));
            if (instancesUsingKeyPair.size() > 0) {
               logger.debug("<< inUse keyPair(%s), by (%s)", keyPair.getKeyName(), instancesUsingKeyPair);
            } else {
               logger.debug(">> deleting keyPair(%s)", keyPair.getKeyName());
               ec2Client.getKeyPairServices().deleteKeyPairInRegion(region, keyPair.getKeyName());
               // TODO: test this clear happens
               credentialsMap.invalidate(new RegionAndName(region, keyPair.getKeyName()));
               logger.debug("<< deleted keyPair(%s)", keyPair.getKeyName());
            }
         }
      }
   }

   protected ImmutableSet<String> extractIdsFromInstances(Iterable<? extends RunningInstance> deadOnes) {
      return ImmutableSet.copyOf(transform(deadOnes, new Function<RunningInstance, String>() {

         @Override
         public String apply(RunningInstance input) {
            return input.getId();
         }

      }));
   }

   protected Predicate<RunningInstance> usingKeyPairAndNotDead(final KeyPair keyPair) {
      return new Predicate<RunningInstance>() {

         @Override
         public boolean apply(RunningInstance input) {
            switch (input.getInstanceState()) {
            case TERMINATED:
            case SHUTTING_DOWN:
               return false;
            }
            return keyPair.getKeyName().equals(input.getKeyName());
         }

      };
   }

   /**
    * like {@link BaseComputeService#destroyNode} except that this will
    * clean implicit keypairs and security groups.
    */
    @Override
   public void destroyNode(String id) {
      NodeMetadata destroyedNode = doDestroyNode(id);
      cleanUpIncidentalResourcesOfDeadNodes(Collections.singleton(destroyedNode));
   }

   /**
    * like {@link BaseComputeService#destroyNodesMatching} except that this will
    * clean implicit keypairs and security groups.
    */
   @Override
   public Set<? extends NodeMetadata> destroyNodesMatching(Predicate<NodeMetadata> filter) {
      Set<? extends NodeMetadata> deadOnes = super.destroyNodesMatching(filter);
      cleanUpIncidentalResourcesOfDeadNodes(deadOnes);
      return deadOnes;
   }

   protected void cleanUpIncidentalResourcesOfDeadNodes(Set<? extends NodeMetadata> deadNodes) {
      Builder<String, String> regionGroups = ImmutableMultimap.<String, String> builder();
      for (NodeMetadata nodeMetadata : deadNodes) {
         if (nodeMetadata.getGroup() != null)
            regionGroups.put(AWSUtils.parseHandle(nodeMetadata.getId())[0], nodeMetadata.getGroup());
         }
      for (Entry<String, String> regionGroup : regionGroups.build().entries()) {
         cleanUpIncidentalResources(regionGroup);
      }
   }

   protected void cleanUpIncidentalResources(Entry<String, String> regionGroup){
      // For issue #445, try to delete security groups first: ec2 throws exception if in use, but
      // deleting a key pair does not.
      // This is "belt-and-braces" because deleteKeyPair also does extractIdsFromInstances & usingKeyPairAndNotDead
      // for us to check if any instances are using the key-pair before we delete it. 
      // There is (probably?) still a race if someone is creating instances at the same time as deleting them: 
      // we may delete the key-pair just when the node-being-created was about to rely on the incidental 
      // resources existing.
      try {
         logger.debug(">> deleting incidentalResources(%s @ %s)", regionGroup.getKey(), regionGroup.getValue());
         deleteSecurityGroup(regionGroup.getKey(), regionGroup.getValue());
         deleteKeyPair(regionGroup.getKey(), regionGroup.getValue());
         logger.debug("<< deleted incidentalResources(%s @ %s)", regionGroup.getKey(), regionGroup.getValue());
      } catch (IllegalStateException e) {
         logger.debug("<< inUse incidentalResources(%s @ %s)", regionGroup.getKey(), regionGroup.getValue());
      }
   }

   /**
    * returns template options, except of type {@link EC2TemplateOptions}.
    */
   @Override
   public EC2TemplateOptions templateOptions() {
      return EC2TemplateOptions.class.cast(super.templateOptions());
   }

}
