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
package org.jclouds.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.difference;
import static com.google.common.util.concurrent.Atomics.newReference;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.functions.DefaultCredentialsFromImageOrOverridingCredentials.overrideDefaultCredentialsWithOptionsIfPresent;
import static org.jclouds.ec2.compute.util.EC2ComputeUtils.getZoneFromLocationOrNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.PresentInstances;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.reference.EC2Constants;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * creates futures that correlate to
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2CreateNodesInGroupThenAddToSet implements CreateNodesInGroupThenAddToSet {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   @Named(EC2Constants.PROPERTY_EC2_AUTO_ALLOCATE_ELASTIC_IPS)
   @VisibleForTesting
   boolean autoAllocateElasticIps = false;

   @VisibleForTesting
   final EC2Client client;
   @VisibleForTesting
   final Predicate<AtomicReference<NodeMetadata>> nodeRunning;
   @VisibleForTesting
   final LoadingCache<RegionAndName, String> elasticIpCache;
   @VisibleForTesting
   final CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturncustomize;
   @VisibleForTesting
   final Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata;
   @VisibleForTesting
   final ComputeUtils utils;
   final PresentInstances presentInstances;
   final LoadingCache<RunningInstance, Optional<LoginCredentials>> instanceToCredentials;
   final Map<String, Credentials> credentialStore;

   @Inject
   protected EC2CreateNodesInGroupThenAddToSet(
         EC2Client client,
         @Named("ELASTICIP") LoadingCache<RegionAndName, String> elasticIpCache,
         @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
         CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturncustomize,
         PresentInstances presentInstances, Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata,
         LoadingCache<RunningInstance, Optional<LoginCredentials>> instanceToCredentials,
         Map<String, Credentials> credentialStore, ComputeUtils utils) {
      this.client = checkNotNull(client, "client");
      this.elasticIpCache = checkNotNull(elasticIpCache, "elasticIpCache");
      this.nodeRunning = checkNotNull(nodeRunning, "nodeRunning");
      this.presentInstances = checkNotNull(presentInstances, "presentInstances");
      this.createKeyPairAndSecurityGroupsAsNeededAndReturncustomize = checkNotNull(
            createKeyPairAndSecurityGroupsAsNeededAndReturncustomize,
            "createKeyPairAndSecurityGroupsAsNeededAndReturncustomize");
      this.runningInstanceToNodeMetadata = checkNotNull(runningInstanceToNodeMetadata, "runningInstanceToNodeMetadata");
      this.instanceToCredentials = checkNotNull(instanceToCredentials, "instanceToCredentials");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.utils = checkNotNull(utils, "utils");
   }

   public static final Function<RunningInstance, RegionAndName> instanceToRegionAndName = new Function<RunningInstance, RegionAndName>() {
      @Override
      public RegionAndName apply(RunningInstance from) {
         return new RegionAndName(from.getRegion(), from.getId());
      }
   };

   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template, Set<NodeMetadata> goodNodes,
         Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      Template mutableTemplate = template.clone();

      Set<RunningInstance> started = runInstancesAndWarnOnInvisible(group, count, mutableTemplate);
      if (started.size() == 0) {
         logger.warn("<< unable to start instances(%s)", mutableTemplate);
         return ImmutableMap.of();
      }
      populateCredentials(started, template.getOptions());

      if (autoAllocateElasticIps) // before customization as the elastic ips may be needed
         blockUntilRunningAndAssignElasticIpsToInstancesOrPutIntoBadMap(started, badNodes);

      return utils.customizeNodesAndAddToGoodMapOrPutExceptionIntoBadMap(mutableTemplate.getOptions(),
            transform(started, runningInstanceToNodeMetadata), goodNodes, badNodes, customizationResponses);
   }

   /**
    * attempts to start the specified count of instances. eventual consistency might cause a problem where instances
    * aren't immediately visible to the api. This method will warn when that occurs.
    */
   private Set<RunningInstance> runInstancesAndWarnOnInvisible(String group, int count, Template mutableTemplate) {
      Set<RunningInstance> started = createKeyPairAndSecurityGroupsAsNeededThenRunInstances(group, count,
            mutableTemplate);
      Set<RegionAndName> startedIds = ImmutableSet.copyOf(transform(started, instanceToRegionAndName));
      if (startedIds.size() == 0) {
         return ImmutableSet.copyOf(started);
      }
      logger.debug("<< started instances(%s)", startedIds);
      Set<RunningInstance> visible = presentInstances.apply(startedIds);
      Set<RegionAndName> visibleIds = ImmutableSet.copyOf(transform(visible, instanceToRegionAndName));
      logger.trace("<< visible instances(%s)", visibleIds);

      // add an exception for each of the nodes we cannot customize
      Set<RegionAndName> invisibleIds = difference(startedIds, visibleIds);
      if (invisibleIds.size() > 0) {
         logger.warn("<< not api visible instances(%s)", invisibleIds);
      }
      return started;
   }

   private void populateCredentials(Set<RunningInstance> input, TemplateOptions options) {
      LoginCredentials credentials = null;
      for (RunningInstance instance : input) {
         credentials = instanceToCredentials.apply(instance).orNull();
         if (credentials != null)
            break;
      }
      credentials = overrideDefaultCredentialsWithOptionsIfPresent(credentials, options);
      if (credentials != null)
         for (RegionAndName instance : transform(input, instanceToRegionAndName))
            credentialStore.put("node#" + instance.slashEncode(), credentials);
   }

   private void blockUntilRunningAndAssignElasticIpsToInstancesOrPutIntoBadMap(Set<RunningInstance> input,
         Map<NodeMetadata, Exception> badNodes) {
      Map<RegionAndName, RunningInstance> instancesById = Maps.uniqueIndex(input, instanceToRegionAndName);
      for (Map.Entry<RegionAndName, RunningInstance> entry : instancesById.entrySet()) {
         RegionAndName id = entry.getKey();
         RunningInstance instance = entry.getValue();
         try {
            logger.debug("<< allocating elastic IP instance(%s)", id);
            String ip = client.getElasticIPAddressServices().allocateAddressInRegion(id.getRegion());
            // block until instance is running
            logger.debug(">> awaiting status running instance(%s)", id);
            AtomicReference<NodeMetadata> node = newReference(runningInstanceToNodeMetadata
                  .apply(instance));
            nodeRunning.apply(node);
            logger.trace("<< running instance(%s)", id);
            logger.debug(">> associating elastic IP %s to instance %s", ip, id);
            client.getElasticIPAddressServices().associateAddressInRegion(id.getRegion(), ip, id.getName());
            logger.trace("<< associated elastic IP %s to instance %s", ip, id);
            // add mapping of instance to ip into the cache
            elasticIpCache.put(id, ip);
         } catch (RuntimeException e) {
            badNodes.put(runningInstanceToNodeMetadata.apply(instancesById.get(id)), e);
         }
      }
   }

   private Set<RunningInstance> createKeyPairAndSecurityGroupsAsNeededThenRunInstances(String group, int count,
         Template template) {
      String region = AWSUtils.getRegionFromLocationOrNull(template.getLocation());
      String zone = getZoneFromLocationOrNull(template.getLocation());
      RunInstancesOptions instanceOptions = createKeyPairAndSecurityGroupsAsNeededAndReturncustomize.execute(region,
            group, template);
      return createNodesInRegionAndZone(region, zone, group, count, template, instanceOptions);
   }

   protected Set<RunningInstance> createNodesInRegionAndZone(String region, String zone, String group, int count,
         Template template, RunInstancesOptions instanceOptions) {
      int countStarted = 0;
      int tries = 0;
      Set<RunningInstance> started = ImmutableSet.<RunningInstance> of();

      while (countStarted < count && tries++ < count) {
         if (logger.isDebugEnabled())
            logger.debug(">> running %d instance region(%s) zone(%s) ami(%s) params(%s)", count - countStarted, region,
                  zone, template.getImage().getProviderId(), instanceOptions.buildFormParameters());

         started = ImmutableSet.copyOf(concat(
               started,
               client.getInstanceServices().runInstancesInRegion(region, zone, template.getImage().getProviderId(), 1,
                     count - countStarted, instanceOptions)));

         countStarted = size(started);
         if (countStarted < count)
            logger.debug(">> not enough instances (%d/%d) started, attempting again", countStarted, count);
      }
      return started;
   }

}
