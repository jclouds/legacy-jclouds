/*
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

package org.jclouds.googlecompute.compute;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.ListeningExecutorService;
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
import org.jclouds.compute.reference.ComputeServiceConstants;
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
import org.jclouds.googlecompute.GoogleComputeApi;
import org.jclouds.googlecompute.compute.options.GoogleComputeTemplateOptions;
import org.jclouds.googlecompute.config.UserProject;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.http.HttpResponse;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.googlecompute.GoogleComputeConstants.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecompute.GoogleComputeConstants.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.util.Predicates2.retry;

/**
 * @author David Alves
 */
public class GoogleComputeService extends BaseComputeService {

   private final Function<Set<? extends NodeMetadata>, Set<String>> findOrphanedGroups;
   private final GroupNamingConvention.Factory namingConvention;
   private final GoogleComputeApi api;
   private final Supplier<String> project;
   private final Predicate<AtomicReference<Operation>> operationDonePredicate;
   private final long operationCompleteCheckInterval;
   private final long operationCompleteCheckTimeout;

   @Inject
   protected GoogleComputeService(ComputeServiceContext context,
                                  Map<String, Credentials> credentialStore,
                                  @Memoized Supplier<Set<? extends Image>> images,
                                  @Memoized Supplier<Set<? extends Hardware>> hardwareProfiles,
                                  @Memoized Supplier<Set<? extends Location>> locations,
                                  ListNodesStrategy listNodesStrategy,
                                  GetImageStrategy getImageStrategy,
                                  GetNodeMetadataStrategy getNodeMetadataStrategy,
                                  CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy,
                                  RebootNodeStrategy rebootNodeStrategy,
                                  DestroyNodeStrategy destroyNodeStrategy,
                                  ResumeNodeStrategy resumeNodeStrategy,
                                  SuspendNodeStrategy suspendNodeStrategy,
                                  Provider<TemplateBuilder> templateBuilderProvider,
                                  @Named("DEFAULT") Provider<TemplateOptions> templateOptionsProvider,
                                  @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
                                  @Named(TIMEOUT_NODE_TERMINATED) Predicate<AtomicReference<NodeMetadata>>
                                          nodeTerminated,
                                  @Named(TIMEOUT_NODE_SUSPENDED)
                                  Predicate<AtomicReference<NodeMetadata>> nodeSuspended,
                                  InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory,
                                  InitAdminAccess initAdminAccess,
                                  RunScriptOnNode.Factory runScriptOnNodeFactory,
                                  PersistNodeCredentials persistNodeCredentials,
                                  ComputeServiceConstants.Timeouts timeouts,
                                  @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                                  Optional<ImageExtension> imageExtension,
                                  Function<Set<? extends NodeMetadata>, Set<String>> findOrphanedGroups,
                                  GroupNamingConvention.Factory namingConvention,
                                  GoogleComputeApi api,
                                  @UserProject Supplier<String> project,
                                  Predicate<AtomicReference<Operation>> operationDonePredicate,
                                  @Named(OPERATION_COMPLETE_INTERVAL) Long operationCompleteCheckInterval,
                                  @Named(OPERATION_COMPLETE_TIMEOUT) Long operationCompleteCheckTimeout) {

      super(context, credentialStore, images, hardwareProfiles, locations, listNodesStrategy, getImageStrategy,
              getNodeMetadataStrategy, runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy,
              resumeNodeStrategy, suspendNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning,
              nodeTerminated, nodeSuspended, initScriptRunnerFactory, initAdminAccess, runScriptOnNodeFactory,
              persistNodeCredentials, timeouts, userExecutor, imageExtension);
      this.findOrphanedGroups = checkNotNull(findOrphanedGroups, "find orphaned groups function");
      this.namingConvention = checkNotNull(namingConvention, "naming convention factory");
      this.api = checkNotNull(api, "google compute api");
      this.project = checkNotNull(project, "user project name");
      this.operationDonePredicate = checkNotNull(operationDonePredicate, "operation completed predicate");
      this.operationCompleteCheckInterval = checkNotNull(operationCompleteCheckInterval,
              "operation completed check interval");
      this.operationCompleteCheckTimeout = checkNotNull(operationCompleteCheckTimeout,
              "operation completed check timeout");
   }

   @Override
   protected synchronized void cleanUpIncidentalResourcesOfDeadNodes(Set<? extends NodeMetadata> deadNodes) {
      Set<String> orphanedGroups = findOrphanedGroups.apply(deadNodes);
      for (String orphanedGroup : orphanedGroups) {
         cleanUpNetworksAndFirewallsForGroup(orphanedGroup);
      }
   }


   protected void cleanUpNetworksAndFirewallsForGroup(String groupName) {
      String resourceName = namingConvention.create().sharedNameForGroup(groupName);
      AtomicReference<Operation> operation = new AtomicReference<Operation>(api.getFirewallApiForProject(project.get())
              .delete(resourceName));

      retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval,
              MILLISECONDS).apply(operation);

      if (operation.get().getHttpError().isPresent()) {
         HttpResponse response = operation.get().getHttpError().get();
         logger.warn("delete orphaned firewall failed. Http Error Code: " + response.getStatusCode() +
                 " HttpError: " + response.getMessage());
      }

      operation = new AtomicReference<Operation>(api.getNetworkApiForProject(project.get()).delete(resourceName));

      retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval,
              MILLISECONDS).apply(operation);

      if (operation.get().getHttpError().isPresent()) {
         HttpResponse response = operation.get().getHttpError().get();
         logger.warn("delete orphaned network failed. Http Error Code: " + response.getStatusCode() +
                 " HttpError: " + response.getMessage());
      }
   }


   /**
    * returns template options, except of type {@link GoogleComputeTemplateOptions}.
    */
   @Override
   public GoogleComputeTemplateOptions templateOptions() {
      return GoogleComputeTemplateOptions.class.cast(super.templateOptions());
   }
}
