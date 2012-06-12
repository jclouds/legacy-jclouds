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
import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_DELIMITER;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.util.Preconditions2.checkNotEmpty;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

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
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.functions.GroupNamingConvention.Factory;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.internal.PersistNodeCredentials;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetImageStrategy;
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
import org.jclouds.predicates.Retryables;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class EC2ComputeService extends BaseComputeService {
   private final EC2Client ec2Client;
   private final ConcurrentMap<RegionAndName, KeyPair> credentialsMap;
   private final LoadingCache<RegionAndName, String> securityGroupMap;
   private final Factory namingConvention;

   @Inject
   protected EC2ComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
            @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> sizes,
            @Memoized Supplier<Set<? extends Location>> locations, ListNodesStrategy listNodesStrategy,
            GetImageStrategy getImageStrategy, GetNodeMetadataStrategy getNodeMetadataStrategy,
            CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy, RebootNodeStrategy rebootNodeStrategy,
            DestroyNodeStrategy destroyNodeStrategy, ResumeNodeStrategy startNodeStrategy,
            SuspendNodeStrategy stopNodeStrategy, Provider<TemplateBuilder> templateBuilderProvider,
            Provider<TemplateOptions> templateOptionsProvider,
            @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
            @Named(TIMEOUT_NODE_TERMINATED) Predicate<AtomicReference<NodeMetadata>> nodeTerminated,
            @Named(TIMEOUT_NODE_SUSPENDED) Predicate<AtomicReference<NodeMetadata>> nodeSuspended,
            InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory,
            RunScriptOnNode.Factory runScriptOnNodeFactory, InitAdminAccess initAdminAccess,
            PersistNodeCredentials persistNodeCredentials, Timeouts timeouts,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, EC2Client ec2Client,
            ConcurrentMap<RegionAndName, KeyPair> credentialsMap,
            @Named("SECURITY") LoadingCache<RegionAndName, String> securityGroupMap,
            Optional<ImageExtension> imageExtension, GroupNamingConvention.Factory namingConvention) {
      super(context, credentialStore, images, sizes, locations, listNodesStrategy, getImageStrategy,
               getNodeMetadataStrategy, runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy,
               startNodeStrategy, stopNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning,
               nodeTerminated, nodeSuspended, initScriptRunnerFactory, initAdminAccess, runScriptOnNodeFactory,
               persistNodeCredentials, timeouts, executor, imageExtension);
      this.ec2Client = ec2Client;
      this.credentialsMap = credentialsMap;
      this.securityGroupMap = securityGroupMap;
      this.namingConvention = namingConvention;
   }

   @Inject(optional = true)
   @Named(RESOURCENAME_DELIMITER)
   char delimiter = '#';

   /**
    * @throws IllegalStateException If the security group was in use
    */
   @VisibleForTesting
   void deleteSecurityGroup(String region, String group) {
      checkNotEmpty(region, "region");
      checkNotEmpty(group, "group");
      String groupName = namingConvention.create().sharedNameForGroup(group);
      
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
         String keyName = keyPair.getKeyName();
         Predicate<String> keyNameMatcher = namingConvention.create().containsGroup(group);
         String oldKeyNameRegex = String.format("jclouds#%s#%s#%s", group, region, "[0-9a-f]+").replace('#', delimiter);
         // old keypair pattern too verbose as it has an unnecessary region qualifier
         
         if (keyNameMatcher.apply(keyName) || keyName.matches(oldKeyNameRegex)) {
            Set<String> instancesUsingKeyPair = extractIdsFromInstances(filter(concat(ec2Client.getInstanceServices()
                  .describeInstancesInRegion(region)), usingKeyPairAndNotDead(keyPair)));
            if (instancesUsingKeyPair.size() > 0) {
               logger.debug("<< inUse keyPair(%s), by (%s)", keyPair.getKeyName(), instancesUsingKeyPair);
            } else {
               logger.debug(">> deleting keyPair(%s)", keyPair.getKeyName());
               ec2Client.getKeyPairServices().deleteKeyPairInRegion(region, keyPair.getKeyName());
               // TODO: test this clear happens
               credentialsMap.remove(new RegionAndName(region, keyPair.getKeyName()));
               credentialsMap.remove(new RegionAndName(region, group));
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
    * Cleans implicit keypairs and security groups.
    */
   @Override
   protected void cleanUpIncidentalResourcesOfDeadNodes(Set<? extends NodeMetadata> deadNodes) {
      Builder<String, String> regionGroups = ImmutableMultimap.builder();
      for (NodeMetadata nodeMetadata : deadNodes) {
         if (nodeMetadata.getGroup() != null)
            regionGroups.put(AWSUtils.parseHandle(nodeMetadata.getId())[0], nodeMetadata.getGroup());
         }
      for (Entry<String, String> regionGroup : regionGroups.build().entries()) {
         cleanUpIncidentalResources(regionGroup.getKey(), regionGroup.getValue());
      }
   }

   protected void cleanUpIncidentalResources(final String region, final String group){
      // For issue #445, tries to delete security groups first: ec2 throws exception if in use, but
      // deleting a key pair does not.
      // This is "belt-and-braces" because deleteKeyPair also does extractIdsFromInstances & usingKeyPairAndNotDead
      // for us to check if any instances are using the key-pair before we delete it. 
      // There is (probably?) still a race if someone is creating instances at the same time as deleting them: 
      // we may delete the key-pair just when the node-being-created was about to rely on the incidental 
      // resources existing.

      // Also in #445, in aws-ec2 the deleteSecurityGroup sometimes fails after terminating the final VM using a 
      // given security group, if called very soon after the VM's state reports terminated. Emprically, it seems that
      // waiting a small time (e.g. enabling logging or debugging!) then the tests pass. We therefore retry.
      final int maxAttempts = 3;
      Retryables.retryNumTimes(new Predicate<Void>() {
            @Override
            public boolean apply(Void input) {
               try {
                  logger.debug(">> deleting incidentalResources(%s @ %s)", region, group);
                  deleteSecurityGroup(region, group);
                  deleteKeyPair(region, group); // not executed if securityGroup was in use
                  logger.debug("<< deleted incidentalResources(%s @ %s)", region, group);
                  return true;
               } catch (IllegalStateException e) {
                  logger.debug("<< inUse incidentalResources(%s @ %s)", region, group);
                  return false;
               }
            }
         }, (Void)null, maxAttempts);
   }

   /**
    * returns template options, except of type {@link EC2TemplateOptions}.
    */
   @Override
   public EC2TemplateOptions templateOptions() {
      return EC2TemplateOptions.class.cast(super.templateOptions());
   }

}
