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
package org.jclouds.trmk.vcloud_0_8.compute;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
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
import org.jclouds.scriptbuilder.functions.InitAdminAccess;
import org.jclouds.trmk.vcloud_0_8.compute.options.TerremarkVCloudTemplateOptions;
import org.jclouds.trmk.vcloud_0_8.compute.strategy.CleanupOrphanKeys;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudComputeService extends BaseComputeService {
   private final CleanupOrphanKeys cleanupOrphanKeys;

   @Inject
   protected TerremarkVCloudComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
            @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> sizes,
            @Memoized Supplier<Set<? extends Location>> locations, ListNodesStrategy listNodesStrategy,
            GetImageStrategy getImageStrategy, GetNodeMetadataStrategy getNodeMetadataStrategy,
            CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy, RebootNodeStrategy rebootNodeStrategy,
            DestroyNodeStrategy destroyNodeStrategy, ResumeNodeStrategy resumeNodeStrategy,
            SuspendNodeStrategy suspendNodeStrategy, Provider<TemplateBuilder> templateBuilderProvider,
            @Named("DEFAULT") Provider<TemplateOptions> templateOptionsProvider,
            @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
            @Named(TIMEOUT_NODE_TERMINATED) Predicate<AtomicReference<NodeMetadata>> nodeTerminated,
            @Named(TIMEOUT_NODE_SUSPENDED) Predicate<AtomicReference<NodeMetadata>> nodeSuspended,
            InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory,
            RunScriptOnNode.Factory runScriptOnNodeFactory, InitAdminAccess initAdminAccess,
            PersistNodeCredentials persistNodeCredentials, Timeouts timeouts,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, CleanupOrphanKeys cleanupOrphanKeys,
            Optional<ImageExtension> imageExtension) {
      super(context, credentialStore, images, sizes, locations, listNodesStrategy, getImageStrategy,
               getNodeMetadataStrategy, runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy,
               resumeNodeStrategy, suspendNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning,
               nodeTerminated, nodeSuspended, initScriptRunnerFactory, initAdminAccess, runScriptOnNodeFactory,
               persistNodeCredentials, timeouts, executor, imageExtension);
      this.cleanupOrphanKeys = cleanupOrphanKeys;
   }

   /**
    * Cleans implicit keypairs.
    */
   @Override
   protected void cleanUpIncidentalResourcesOfDeadNodes(Set<? extends NodeMetadata> deadNodes) {
      cleanupOrphanKeys.execute(deadNodes);
   }

   /**
    * returns template options, except of type
    * {@link TerremarkVCloudTemplateOptions}.
    */
   @Override
   public TerremarkVCloudTemplateOptions templateOptions() {
      return TerremarkVCloudTemplateOptions.class.cast(super.templateOptions());
   }

}
