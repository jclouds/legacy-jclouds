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
package org.jclouds.joyent.cloudapi.v6_5.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
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
import org.jclouds.joyent.cloudapi.v6_5.JoyentCloudApi;
import org.jclouds.joyent.cloudapi.v6_5.compute.internal.KeyAndPrivateKey;
import org.jclouds.joyent.cloudapi.v6_5.compute.options.JoyentCloudTemplateOptions;
import org.jclouds.joyent.cloudapi.v6_5.domain.Key;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.DatacenterAndName;
import org.jclouds.joyent.cloudapi.v6_5.features.KeyApi;
import org.jclouds.joyent.cloudapi.v6_5.predicates.KeyPredicates;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * @author Adrian Cole
 */
@Singleton
public class JoyentCloudComputeService extends BaseComputeService {
   protected final JoyentCloudApi novaApi;
   protected final LoadingCache<DatacenterAndName, KeyAndPrivateKey> keyCache;
   protected final Function<Set<? extends NodeMetadata>, Multimap<String, String>> orphanedGroupsByDatacenterId;
   protected final GroupNamingConvention.Factory namingConvention;

   @Inject
   protected JoyentCloudComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
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
         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, JoyentCloudApi novaApi,
         LoadingCache<DatacenterAndName, KeyAndPrivateKey> keyCache,
         Function<Set<? extends NodeMetadata>, Multimap<String, String>> orphanedGroupsByDatacenterId,
         GroupNamingConvention.Factory namingConvention, Optional<ImageExtension> imageExtension) {
      super(context, credentialStore, images, sizes, locations, listNodesStrategy, getImageStrategy,
            getNodeMetadataStrategy, runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy,
            startNodeStrategy, stopNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning,
            nodeTerminated, nodeSuspended, initScriptRunnerFactory, initAdminAccess, runScriptOnNodeFactory,
            persistNodeCredentials, timeouts, userExecutor, imageExtension);
      this.novaApi = checkNotNull(novaApi, "novaApi");
      this.keyCache = checkNotNull(keyCache, "keyCache");
      this.orphanedGroupsByDatacenterId = checkNotNull(orphanedGroupsByDatacenterId, "orphanedGroupsByDatacenterId");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
   }

   @Override
   protected void cleanUpIncidentalResourcesOfDeadNodes(Set<? extends NodeMetadata> deadNodes) {
      Multimap<String, String> zoneToZoneAndGroupNames = orphanedGroupsByDatacenterId.apply(deadNodes);
      for (Map.Entry<String, Collection<String>> entry : zoneToZoneAndGroupNames.asMap().entrySet()) {
         cleanupOrphanedKeysInZone(ImmutableSet.copyOf(entry.getValue()), entry.getKey());
      }
   }

   private void cleanupOrphanedKeysInZone(Set<String> groups, String datacenterId) {
      KeyApi keyApi = novaApi.getKeyApi();
      for (String group : groups) {
         for (Key key : Iterables.filter(keyApi.list(),
               KeyPredicates.nameMatches(namingConvention.create().containsGroup(group)))) {
            DatacenterAndName datacenterAndName = DatacenterAndName.fromDatacenterAndName(datacenterId, key.getName());
            logger.debug(">> deleting key(%s)", datacenterAndName);
            keyApi.delete(key.getName());
            // TODO: test this clear happens
            keyCache.invalidate(datacenterAndName);
            logger.debug("<< deleted key(%s)", datacenterAndName);
         }

         keyCache.invalidate(DatacenterAndName.fromDatacenterAndName(datacenterId, namingConvention.create()
               .sharedNameForGroup(group)));
      }
   }

   /**
    * returns template options, except of type {@link JoyentCloudTemplateOptions}.
    */
   @Override
   public JoyentCloudTemplateOptions templateOptions() {
      return JoyentCloudTemplateOptions.class.cast(super.templateOptions());
   }

}
