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
package org.jclouds.ec2.compute.strategy;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.ec2.compute.util.EC2ComputeUtils.getZoneFromLocationOrNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.predicates.InstancePresent;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.reference.EC2Constants;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableSet.Builder;

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
   final InstancePresent instancePresent;
   final LoadingCache<RunningInstance, Credentials> instanceToCredentials;
   final Map<String, Credentials> credentialStore;
   final Provider<TemplateBuilder> templateBuilderProvider;



   @Inject
   protected EC2CreateNodesInGroupThenAddToSet(
            EC2Client client,
            @Named("ELASTICIP")
            LoadingCache<RegionAndName, String> elasticIpCache, 
            @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
            Provider<TemplateBuilder> templateBuilderProvider,
            CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturncustomize,
            InstancePresent instancePresent, Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata,
            LoadingCache<RunningInstance, Credentials> instanceToCredentials, Map<String, Credentials> credentialStore,
            ComputeUtils utils) {
      this.client = checkNotNull(client, "client");
      this.elasticIpCache = checkNotNull(elasticIpCache, "elasticIpCache");
      this.nodeRunning = checkNotNull(nodeRunning, "nodeRunning");
      this.templateBuilderProvider = checkNotNull(templateBuilderProvider, "templateBuilderProvider");
      this.instancePresent = checkNotNull(instancePresent, "instancePresent");
      this.createKeyPairAndSecurityGroupsAsNeededAndReturncustomize = checkNotNull(
               createKeyPairAndSecurityGroupsAsNeededAndReturncustomize,
               "createKeyPairAndSecurityGroupsAsNeededAndReturncustomize");
      this.runningInstanceToNodeMetadata = checkNotNull(runningInstanceToNodeMetadata, "runningInstanceToNodeMetadata");
      this.instanceToCredentials = checkNotNull(instanceToCredentials, "instanceToCredentials");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.utils = checkNotNull(utils, "utils");
   }

   public static Function<RunningInstance, RegionAndName> instanceToRegionAndName = new Function<RunningInstance, RegionAndName>() {
      @Override
      public RegionAndName apply(RunningInstance from) {
         return new RegionAndName(from.getRegion(), from.getId());
      }
   };

   @Override
   public Map<?, Future<Void>> execute(String group, int count, Template template, Set<NodeMetadata> goodNodes,
            Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      
      Template mutableTemplate = templateBuilderProvider.get().imageId(template.getImage().getId()).fromTemplate(template)
                  .build();

      Iterable<String> ips = allocateElasticIpsInRegion(count, template);

      Iterable<? extends RunningInstance> started = createKeyPairAndSecurityGroupsAsNeededThenRunInstances(group,
               count, mutableTemplate);

      Iterable<RegionAndName> ids = transform(started, instanceToRegionAndName);

      String idsString = Joiner.on(',').join(ids);
      if (Iterables.size(ids) > 0) {
         logger.debug("<< started instances(%s)", idsString);
         all(ids, instancePresent);
         logger.debug("<< present instances(%s)", idsString);
         populateCredentials(started);
      }
      
      assignElasticIpsToInstances(ips, started);
      
      return utils.customizeNodesAndAddToGoodMapOrPutExceptionIntoBadMap(mutableTemplate.getOptions(), transform(started,
               runningInstanceToNodeMetadata), goodNodes, badNodes, customizationResponses);
   }

   protected void populateCredentials(Iterable<? extends RunningInstance> started) {
      Credentials credentials = null;
      for (RunningInstance instance : started) {
         credentials = instanceToCredentials.apply(instance);
         if (credentials != null)
            break;
      }
      if (credentials != null)
         for (RunningInstance instance : started)
            credentialStore.put("node#" + instance.getRegion() + "/" + instance.getId(), credentials);
   }

   protected Iterable<String> allocateElasticIpsInRegion(int count, Template template) {
      
      Builder<String> ips = ImmutableSet.builder();
      if (!autoAllocateElasticIps)
         return ips.build();

      String region = AWSUtils.getRegionFromLocationOrNull(template.getLocation());
      logger.debug("<< allocating %d elastic IPs for nodes in region (%s)", count, region);

      for (int i = 0; i < count; ++i) {
         ips.add(client.getElasticIPAddressServices().allocateAddressInRegion(region));
      }
      return ips.build();
   }

   protected void assignElasticIpsToInstances(Iterable<String> ips, Iterable<? extends RunningInstance> startedInstances) {

      if (!autoAllocateElasticIps)
         return;

      // TODO parallel
      int i = 0;
      for (RunningInstance startedInstance : startedInstances) {
         String ip = Iterables.get(ips, i);
         String region = startedInstance.getRegion();
         String id = startedInstance.getId();
         RegionAndName coordinates = new RegionAndName(region, id);

         // block until instance is running
         logger.debug(">> awaiting status running instance(%s)", coordinates);
         AtomicReference<NodeMetadata> node = new AtomicReference<NodeMetadata>(runningInstanceToNodeMetadata.apply(startedInstance));
         nodeRunning.apply(node);
         logger.trace("<< running instance(%s)", coordinates);
         logger.debug(">> associating elastic IP %s to instance %s", ip, coordinates);
         client.getElasticIPAddressServices().associateAddressInRegion(region, ip, id);
         logger.trace("<< associated elastic IP %s to instance %s", ip, coordinates);
         // add mapping of instance to ip into the cache
         elasticIpCache.put(coordinates, ip);
      }
   }

   protected Iterable<? extends RunningInstance> createKeyPairAndSecurityGroupsAsNeededThenRunInstances(String group,
            int count, Template template) {
      String region = AWSUtils.getRegionFromLocationOrNull(template.getLocation());
      String zone = getZoneFromLocationOrNull(template.getLocation());
      RunInstancesOptions instanceOptions = createKeyPairAndSecurityGroupsAsNeededAndReturncustomize.execute(region,
               group, template);
      return createNodesInRegionAndZone(region, zone, group, count, template, instanceOptions);
   }

   protected Iterable<? extends RunningInstance> createNodesInRegionAndZone(String region, String zone, String group,
            int count, Template template, RunInstancesOptions instanceOptions) {
      int countStarted = 0;
      int tries = 0;
      Iterable<? extends RunningInstance> started = ImmutableSet.<RunningInstance> of();

      while (countStarted < count && tries++ < count) {
         if (logger.isDebugEnabled())
            logger.debug(">> running %d instance region(%s) zone(%s) ami(%s) params(%s)", count - countStarted, region,
                     zone, template.getImage().getProviderId(), instanceOptions.buildFormParameters());

         started = Iterables.concat(started, client.getInstanceServices().runInstancesInRegion(region, zone,
                  template.getImage().getProviderId(), 1, count - countStarted, instanceOptions));

         countStarted = Iterables.size(started);
         if (countStarted < count)
            logger.debug(">> not enough instances (%d/%d) started, attempting again", countStarted, count);
      }
      return started;
   }

}
