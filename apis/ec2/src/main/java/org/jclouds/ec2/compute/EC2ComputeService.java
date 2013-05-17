/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.compute;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_DELIMITER;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.compute.util.ComputeServiceUtils.addMetadataAndParseTagsFromValuesOfEmptyString;
import static org.jclouds.compute.util.ComputeServiceUtils.metadataAndTagsAsValuesOfEmptyString;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_GENERATE_INSTANCE_NAMES;
import static org.jclouds.ec2.util.Tags.resourceToTagsAsMap;
import static org.jclouds.util.Predicates2.retry;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
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
import org.jclouds.ec2.domain.Tag;
import org.jclouds.ec2.util.TagFilterBuilder;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class EC2ComputeService extends BaseComputeService {
   private final EC2Client client;
   private final ConcurrentMap<RegionAndName, KeyPair> credentialsMap;
   private final LoadingCache<RegionAndName, String> securityGroupMap;
   private final Factory namingConvention;
   private final boolean generateInstanceNames;

   @Inject
   protected EC2ComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
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
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, EC2Client client,
            ConcurrentMap<RegionAndName, KeyPair> credentialsMap,
            @Named("SECURITY") LoadingCache<RegionAndName, String> securityGroupMap,
            Optional<ImageExtension> imageExtension, GroupNamingConvention.Factory namingConvention,
            @Named(PROPERTY_EC2_GENERATE_INSTANCE_NAMES) boolean generateInstanceNames) {
      super(context, credentialStore, images, sizes, locations, listNodesStrategy, getImageStrategy,
               getNodeMetadataStrategy, runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy,
               startNodeStrategy, stopNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning,
               nodeTerminated, nodeSuspended, initScriptRunnerFactory, initAdminAccess, runScriptOnNodeFactory,
               persistNodeCredentials, timeouts, userExecutor, imageExtension);
      this.client = client;
      this.credentialsMap = credentialsMap;
      this.securityGroupMap = securityGroupMap;
      this.namingConvention = namingConvention;
      this.generateInstanceNames = generateInstanceNames;
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count, final Template template)
            throws RunNodesException {
      Set<? extends NodeMetadata> nodes = super.createNodesInGroup(group, count, template);
      String region = AWSUtils.getRegionFromLocationOrNull(template.getLocation());
      if (client.getTagApiForRegion(region).isPresent()) {
         Map<String, String> common = metadataAndTagsAsValuesOfEmptyString(template.getOptions());
         if (common.size() > 0 || generateInstanceNames) {
            return addTagsToInstancesInRegion(common, nodes, region, group);
         }
      }
      return nodes;
   }

   private static final Function<NodeMetadata, String> instanceId = new Function<NodeMetadata, String>() {
      @Override
      public String apply(NodeMetadata in) {
         return in.getProviderId();
      }
   };
   
   private Set<NodeMetadata> addTagsToInstancesInRegion(Map<String, String> common, Set<? extends NodeMetadata> input,
         String region, String group) {
      Map<String, ? extends NodeMetadata> instancesById = Maps.uniqueIndex(input, instanceId);
      ImmutableSet.Builder<NodeMetadata> builder = ImmutableSet.<NodeMetadata> builder();
      if (generateInstanceNames && !common.containsKey("Name")) {
         for (Map.Entry<String, ? extends NodeMetadata> entry : instancesById.entrySet()) {
            String id = entry.getKey();
            NodeMetadata instance = entry.getValue();

            Map<String, String> tags = ImmutableMap.<String, String> builder().putAll(common)
                  .put("Name", id.replaceAll(".*-", group + "-")).build();
            logger.debug(">> applying tags %s to instance %s in region %s", tags, id, region);
            client.getTagApiForRegion(region).get().applyToResources(tags, ImmutableSet.of(id));
            builder.add(addTagsForInstance(tags, instancesById.get(id)));
         }
      } else {
         Iterable<String> ids = instancesById.keySet();
         logger.debug(">> applying tags %s to instances %s in region %s", common, ids, region);
         client.getTagApiForRegion(region).get().applyToResources(common, ids);
         for (NodeMetadata in : input)
            builder.add(addTagsForInstance(common, in));
      }
      if (logger.isDebugEnabled()) {
         Multimap<String, String> filter = new TagFilterBuilder().resourceIds(instancesById.keySet()).build();
         FluentIterable<Tag> tags = client.getTagApiForRegion(region).get().filter(filter);
         logger.debug("<< applied tags in region %s: %s", region, resourceToTagsAsMap(tags));
      }
      return builder.build();
   }

   private static NodeMetadata addTagsForInstance(Map<String, String> tags, NodeMetadata input) {
      NodeMetadataBuilder builder = NodeMetadataBuilder.fromNodeMetadata(input).name(tags.get("Name"));
      return addMetadataAndParseTagsFromValuesOfEmptyString(builder, tags).build();
   }

   @Inject(optional = true)
   @Named(RESOURCENAME_DELIMITER)
   char delimiter = '#';

   /**
    * @throws IllegalStateException If the security group was in use
    */
   @VisibleForTesting
   void deleteSecurityGroup(String region, String group) {
      checkNotNull(emptyToNull(region), "region must be defined");
      checkNotNull(emptyToNull(group), "group must be defined");
      String groupName = namingConvention.create().sharedNameForGroup(group);
      
      if (client.getSecurityGroupServices().describeSecurityGroupsInRegion(region, groupName).size() > 0) {
         logger.debug(">> deleting securityGroup(%s)", groupName);
         client.getSecurityGroupServices().deleteSecurityGroupInRegion(region, groupName);
         // TODO: test this clear happens
         securityGroupMap.invalidate(new RegionNameAndIngressRules(region, groupName, null, false));
         logger.debug("<< deleted securityGroup(%s)", groupName);
      }
   }

   @VisibleForTesting
   void deleteKeyPair(String region, String group) {
      for (KeyPair keyPair : client.getKeyPairServices().describeKeyPairsInRegion(region)) {
         String keyName = keyPair.getKeyName();
         Predicate<String> keyNameMatcher = namingConvention.create().containsGroup(group);
         String oldKeyNameRegex = String.format("jclouds#%s#%s#%s", group, region, "[0-9a-f]+").replace('#', delimiter);
         // old keypair pattern too verbose as it has an unnecessary region qualifier
         
         if (keyNameMatcher.apply(keyName) || keyName.matches(oldKeyNameRegex)) {
            Set<String> instancesUsingKeyPair = extractIdsFromInstances(filter(concat(client.getInstanceServices()
                  .describeInstancesInRegion(region)), usingKeyPairAndNotDead(keyPair)));
            if (instancesUsingKeyPair.size() > 0) {
               logger.debug("<< inUse keyPair(%s), by (%s)", keyPair.getKeyName(), instancesUsingKeyPair);
            } else {
               logger.debug(">> deleting keyPair(%s)", keyPair.getKeyName());
               client.getKeyPairServices().deleteKeyPairInRegion(region, keyPair.getKeyName());
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
      // given security group, if called very soon after the VM's state reports terminated. Empirically, it seems that
      // waiting a small time (e.g. enabling logging or debugging!) then the tests pass. We therefore retry.
      // TODO: this could be moved to a config module, also the narrative above made more concise
      retry(new Predicate<RegionAndName>() {
         public boolean apply(RegionAndName input) {
            try {
               logger.debug(">> deleting incidentalResources(%s)", input);
               deleteSecurityGroup(input.getRegion(), input.getName());
               deleteKeyPair(input.getRegion(), input.getName()); // not executed if securityGroup was in use
               logger.debug("<< deleted incidentalResources(%s)", input);
               return true;
            } catch (IllegalStateException e) {
               logger.debug("<< inUse incidentalResources(%s)", input);
               return false;
            }
         }
      }, SECONDS.toMillis(3), 50, 1000, MILLISECONDS).apply(new RegionAndName(region, group));
   }

   /**
    * returns template options, except of type {@link EC2TemplateOptions}.
    */
   @Override
   public EC2TemplateOptions templateOptions() {
      return EC2TemplateOptions.class.cast(super.templateOptions());
   }

}
