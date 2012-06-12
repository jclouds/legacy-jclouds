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
package org.jclouds.aws.ec2.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_GENERATE_INSTANCE_NAMES;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.ec2.AWSEC2AsyncClient;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.aws.ec2.domain.PlacementGroup.State;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.functions.GroupNamingConvention;
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
import org.jclouds.ec2.compute.EC2ComputeService;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;
import org.jclouds.util.Preconditions2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class AWSEC2ComputeService extends EC2ComputeService {

   private final LoadingCache<RegionAndName, String> placementGroupMap;
   private final Predicate<PlacementGroup> placementGroupDeleted;
   private final AWSEC2Client ec2Client;
   private final AWSEC2AsyncClient aclient;
   private final boolean generateInstanceNames;

   @Inject
   protected AWSEC2ComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
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
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, AWSEC2Client ec2Client,
            ConcurrentMap<RegionAndName, KeyPair> credentialsMap,
            @Named("SECURITY") LoadingCache<RegionAndName, String> securityGroupMap,
            @Named("PLACEMENT") LoadingCache<RegionAndName, String> placementGroupMap,
            @Named("DELETED") Predicate<PlacementGroup> placementGroupDeleted,
            @Named(PROPERTY_EC2_GENERATE_INSTANCE_NAMES) boolean generateInstanceNames, AWSEC2AsyncClient aclient,
            Optional<ImageExtension> imageExtension, GroupNamingConvention.Factory namingConvention) {
      super(context, credentialStore, images, sizes, locations, listNodesStrategy, getImageStrategy, getNodeMetadataStrategy,
               runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy, startNodeStrategy,
               stopNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning, nodeTerminated,
               nodeSuspended, initScriptRunnerFactory, runScriptOnNodeFactory, initAdminAccess, persistNodeCredentials,
               timeouts, executor, ec2Client, credentialsMap, securityGroupMap, imageExtension, namingConvention);
      this.ec2Client = ec2Client;
      this.placementGroupMap = placementGroupMap;
      this.placementGroupDeleted = placementGroupDeleted;
      this.generateInstanceNames = generateInstanceNames;
      this.aclient = checkNotNull(aclient, "aclient");
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count, final Template template)
            throws RunNodesException {
      Set<? extends NodeMetadata> nodes = super.createNodesInGroup(group, count, template);
      // tags from spot requests do not propagate to running instances
      // automatically
      if (templateWasASpotRequestWithUserMetadata(template)) {
         addTagsToNodesFromUserMetadataInTemplate(nodes, group, template);
         nodes = addUserMetadataFromTemplateOptionsToNodes(template, group, nodes);
      }
      return nodes;
   }

   protected void addTagsToNodesFromUserMetadataInTemplate(Set<? extends NodeMetadata> nodes, String group,
            final Template template) {
      String region = AWSUtils.getRegionFromLocationOrNull(template.getLocation());
      if (template.getOptions().getUserMetadata().size() > 0 || generateInstanceNames) {
         for (String id : transform(nodes, new Function<NodeMetadata, String>() {

            @Override
            public String apply(NodeMetadata arg0) {
               return arg0.getProviderId();
            }

         }))
            aclient.getTagServices().createTagsInRegion(region, ImmutableSet.of(id),
                     metadataForId(id, group, template.getOptions().getUserMetadata()));
      }
   }

   private Map<String, String> metadataForId(String id, String group, Map<String, String> metadata) {
      return generateInstanceNames && !metadata.containsKey("Name") ? ImmutableMap.<String, String> builder().putAll(
               metadata).put("Name", id.replaceAll(".*-", group + "-")).build() : metadata;
   }

   protected boolean templateWasASpotRequestWithUserMetadata(final Template template) {
      return template.getOptions().getUserMetadata().size() > 0
               && AWSEC2TemplateOptions.class.cast(template.getOptions()).getSpotPrice() != null;
   }

   protected Set<? extends NodeMetadata> addUserMetadataFromTemplateOptionsToNodes(final Template template,
            final String group, Set<? extends NodeMetadata> nodes) {
      nodes = ImmutableSet.copyOf(Iterables.transform(nodes, new Function<NodeMetadata, NodeMetadata>() {

         @Override
         public NodeMetadata apply(NodeMetadata arg0) {
            Map<String, String> md = metadataForId(arg0.getProviderId(), group, template.getOptions().getUserMetadata());
            return NodeMetadataBuilder.fromNodeMetadata(arg0).name(md.get("Name")).userMetadata(md).build();
         }

      }));
      return nodes;
   }

   @VisibleForTesting
   void deletePlacementGroup(String region, String group) {
      Preconditions2.checkNotEmpty(group, "group");
      // placementGroupName must be unique within an account per
      // http://docs.amazonwebservices.com/AWSEC2/latest/UserGuide/index.html?using_cluster_computing.html
      String placementGroup = String.format("jclouds#%s#%s", group, region);
      try {
         if (ec2Client.getPlacementGroupServices().describePlacementGroupsInRegion(region, placementGroup).size() > 0) {
            logger.debug(">> deleting placementGroup(%s)", placementGroup);
            try {
               ec2Client.getPlacementGroupServices().deletePlacementGroupInRegion(region, placementGroup);
               checkState(placementGroupDeleted.apply(new PlacementGroup(region, placementGroup, "cluster",
                        State.PENDING)), String.format("placementGroup region(%s) name(%s) failed to delete", region,
                        placementGroup));
               placementGroupMap.invalidate(new RegionAndName(region, placementGroup));
               logger.debug("<< deleted placementGroup(%s)", placementGroup);
            } catch (IllegalStateException e) {
               logger.debug("<< inUse placementGroup(%s)", placementGroup);
            }
         }
      } catch (UnsupportedOperationException e) {
         logger.trace("<< placementGroups unsupported in region %s", region);
      }
   }

   @Override
   protected void cleanUpIncidentalResources(String region, String group) {
      super.cleanUpIncidentalResources(region, group);
      deletePlacementGroup(region, group);
   }

   /**
    * returns template options, except of type {@link EC2TemplateOptions}.
    */
   @Override
   public EC2TemplateOptions templateOptions() {
      return EC2TemplateOptions.class.cast(super.templateOptions());
   }

}
