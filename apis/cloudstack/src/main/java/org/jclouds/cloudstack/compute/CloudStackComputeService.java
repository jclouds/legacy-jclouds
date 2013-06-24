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
package org.jclouds.cloudstack.compute;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.cloudstack.predicates.SshKeyPairPredicates.nameMatches;
import static org.jclouds.cloudstack.predicates.ZonePredicates.supportsSecurityGroups;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.functions.GroupNamingConvention;
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
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.domain.SshKeyPair;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.domain.ZoneAndName;
import org.jclouds.cloudstack.domain.ZoneSecurityGroupNamePortsCidrs;
import org.jclouds.cloudstack.predicates.SecurityGroupPredicates;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * @author Adrian Cole
 */
@Singleton
public class CloudStackComputeService extends BaseComputeService {
   protected final CloudStackClient client;
   protected final LoadingCache<ZoneAndName, SecurityGroup> securityGroupMap;
   protected final LoadingCache<String, SshKeyPair> keyPairCache;
   protected final Function<Set<? extends NodeMetadata>, Multimap<String, String>> orphanedGroupsByZoneId;
   protected final GroupNamingConvention.Factory namingConvention;
   protected final Supplier<LoadingCache<String, Zone>> zoneIdToZone;

   @Inject
   protected CloudStackComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
            @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> sizes,
            @Memoized Supplier<Set<? extends Location>> locations, ListNodesStrategy listNodesStrategy,
            GetImageStrategy getImageStrategy, GetNodeMetadataStrategy getNodeMetadataStrategy,
            CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy, RebootNodeStrategy rebootNodeStrategy,
            DestroyNodeStrategy destroyNodeStrategy, ResumeNodeStrategy startNodeStrategy,
            SuspendNodeStrategy stopNodeStrategy, Provider<TemplateBuilder> templateBuilderProvider,
            @Named("DEFAULT") Provider<TemplateOptions> templateOptionsProvider,
            @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
            @Named(TIMEOUT_NODE_TERMINATED) Predicate<AtomicReference<NodeMetadata>> nodeTerminated,
            @Named(TIMEOUT_NODE_SUSPENDED) Predicate<AtomicReference<NodeMetadata>> nodeSuspended,
            InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory,
            RunScriptOnNode.Factory runScriptOnNodeFactory, InitAdminAccess initAdminAccess,
            PersistNodeCredentials persistNodeCredentials, Timeouts timeouts,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, CloudStackClient client,
            LoadingCache<ZoneAndName, SecurityGroup> securityGroupMap,
            LoadingCache<String, SshKeyPair> keyPairCache,
            Function<Set<? extends NodeMetadata>, Multimap<String, String>> orphanedGroupsByZoneId,
            GroupNamingConvention.Factory namingConvention,
            Supplier<LoadingCache<String, Zone>> zoneIdToZone,
            Optional<ImageExtension> imageExtension) {
      super(context, credentialStore, images, sizes, locations, listNodesStrategy, getImageStrategy,
               getNodeMetadataStrategy, runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy,
               startNodeStrategy, stopNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning,
               nodeTerminated, nodeSuspended, initScriptRunnerFactory, initAdminAccess, runScriptOnNodeFactory,
               persistNodeCredentials, timeouts, userExecutor, imageExtension);
      this.zoneIdToZone = checkNotNull(zoneIdToZone, "zoneIdToZone");
      this.client = checkNotNull(client, "client");
      this.securityGroupMap = checkNotNull(securityGroupMap, "securityGroupMap");
      this.keyPairCache = checkNotNull(keyPairCache, "keyPairCache");
      this.orphanedGroupsByZoneId = checkNotNull(orphanedGroupsByZoneId, "orphanedGroupsByZoneId");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
   }

   @Override
   protected void cleanUpIncidentalResourcesOfDeadNodes(Set<? extends NodeMetadata> deadNodes) {
      Multimap<String, String> zoneToZoneAndGroupNames = orphanedGroupsByZoneId.apply(deadNodes);
      for (String zoneId : zoneToZoneAndGroupNames.keySet()) {
         cleanOrphanedGroupsInZone(ImmutableSet.copyOf(zoneToZoneAndGroupNames.get(zoneId)), zoneId);
      }
   }

   protected void cleanOrphanedGroupsInZone(Set<String> groups, String zoneId) {
      cleanupOrphanedSecurityGroupsInZone(groups, zoneId);
      cleanupOrphanedKeyPairsInZone(groups, zoneId);
   }

   private void cleanupOrphanedSecurityGroupsInZone(Set<String> groups, String zoneId) {
      Zone zone = zoneIdToZone.get().getUnchecked(zoneId);

      if (supportsSecurityGroups().apply(zone)) {
         for (String group : groups) {
            for (SecurityGroup securityGroup : Iterables.filter(client.getSecurityGroupClient().listSecurityGroups(),
                     SecurityGroupPredicates.nameMatches(namingConvention.create().containsGroup(group)))) {
               ZoneAndName zoneAndName = ZoneAndName.fromZoneAndName(zoneId, securityGroup.getName());
               logger.debug(">> deleting securityGroup(%s)", zoneAndName);
               client.getSecurityGroupClient().deleteSecurityGroup(securityGroup.getId());
               // TODO: test this clear happens
               securityGroupMap.invalidate(zoneAndName);
               logger.debug("<< deleted securityGroup(%s)", zoneAndName);
            }
         }
      }
   }

   private void cleanupOrphanedKeyPairsInZone(Set<String> groups, String zoneId) {
      for (String group : groups) {
         for (SshKeyPair pair : Iterables.filter(client.getSSHKeyPairClient().listSSHKeyPairs(),
                                                 nameMatches(namingConvention.create().containsGroup(group)))) {
            logger.debug(">> deleting keypair(%s)", pair.getName());
            client.getSSHKeyPairClient().deleteSSHKeyPair(pair.getName());
            // TODO: test this clear happens
            keyPairCache.invalidate(pair.getName());
            logger.debug("<< deleted keypair(%s)", pair.getName());
         }
         keyPairCache.invalidate(namingConvention.create().sharedNameForGroup(group));
      }
   }

   /**
    * returns template options, as {@link CloudStackTemplateOptions}.
    */
   @Override
   public CloudStackTemplateOptions templateOptions() {
      return CloudStackTemplateOptions.class.cast(super.templateOptions());
   }
   
   

}
