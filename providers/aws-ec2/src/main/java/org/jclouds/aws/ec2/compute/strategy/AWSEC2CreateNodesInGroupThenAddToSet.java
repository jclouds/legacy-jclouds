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
package org.jclouds.aws.ec2.compute.strategy;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_GENERATE_INSTANCE_NAMES;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.util.ComputeServiceUtils.metadataAndTagsAsValuesOfEmptyString;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2AsyncClient;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.compute.predicates.AWSEC2InstancePresent;
import org.jclouds.aws.ec2.domain.LaunchSpecification;
import org.jclouds.aws.ec2.functions.SpotInstanceRequestToAWSRunningInstance;
import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.aws.ec2.options.RequestSpotInstancesOptions;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.strategy.EC2CreateNodesInGroupThenAddToSet;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AWSEC2CreateNodesInGroupThenAddToSet extends EC2CreateNodesInGroupThenAddToSet {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @VisibleForTesting
   final AWSEC2Client client;
   final SpotInstanceRequestToAWSRunningInstance spotConverter;
   final AWSEC2AsyncClient aclient;
   final boolean generateInstanceNames;

   @Inject
   protected AWSEC2CreateNodesInGroupThenAddToSet(
            AWSEC2Client client,
            @Named("ELASTICIP") LoadingCache<RegionAndName, String> elasticIpCache,
            @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
            AWSEC2AsyncClient aclient,
            @Named(PROPERTY_EC2_GENERATE_INSTANCE_NAMES) boolean generateInstanceNames,
            Provider<TemplateBuilder> templateBuilderProvider,
            CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturncustomize,
            AWSEC2InstancePresent instancePresent,
            Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata,
            LoadingCache<RunningInstance, Credentials> instanceToCredentials, Map<String, Credentials> credentialStore,
            ComputeUtils utils, SpotInstanceRequestToAWSRunningInstance spotConverter) {
      super(client, elasticIpCache, nodeRunning, templateBuilderProvider, createKeyPairAndSecurityGroupsAsNeededAndReturncustomize,
               instancePresent, runningInstanceToNodeMetadata, instanceToCredentials, credentialStore, utils);
      this.client = checkNotNull(client, "client");
      this.aclient = checkNotNull(aclient, "aclient");
      this.spotConverter = checkNotNull(spotConverter, "spotConverter");
      this.generateInstanceNames = generateInstanceNames;
   }

   @Override
   protected Iterable<? extends RunningInstance> createNodesInRegionAndZone(String region, String zone, String group,
            int count, Template template, RunInstancesOptions instanceOptions) {
      Map<String, String> tags = metadataAndTagsAsValuesOfEmptyString(template.getOptions());
      Float spotPrice = getSpotPriceOrNull(template.getOptions());
      if (spotPrice != null) {
         LaunchSpecification spec = AWSRunInstancesOptions.class.cast(instanceOptions).getLaunchSpecificationBuilder()
                  .imageId(template.getImage().getProviderId()).availabilityZone(zone).build();
         RequestSpotInstancesOptions options = AWSEC2TemplateOptions.class.cast(template.getOptions()).getSpotOptions();
         if (logger.isDebugEnabled())
            logger.debug(">> requesting %d spot instances region(%s) price(%f) spec(%s) options(%s)", count, region,
                     spotPrice, spec, options);
         return addTagsToInstancesInRegion(tags, transform(client
                  .getSpotInstanceServices().requestSpotInstancesInRegion(region, spotPrice, count, spec, options),
                  spotConverter), region, group);
      } else {
         return addTagsToInstancesInRegion(tags, super.createNodesInRegionAndZone(
                  region, zone, group, count, template, instanceOptions), region, group);
      }

   }

   public Iterable<? extends RunningInstance> addTagsToInstancesInRegion(Map<String, String> metadata,
            Iterable<? extends RunningInstance> iterable, String region, String group) {
      if (metadata.size() > 0 || generateInstanceNames) {
         for (String id : transform(iterable, new Function<RunningInstance, String>() {

            @Override
            public String apply(RunningInstance arg0) {
               return arg0.getId();
            }

         }))
            aclient.getTagServices()
                     .createTagsInRegion(region, ImmutableSet.of(id), metadataForId(id, group, metadata));
      }
      return iterable;
   }

   private Map<String, String> metadataForId(String id, String group, Map<String, String> metadata) {
      return generateInstanceNames && !metadata.containsKey("Name") ? ImmutableMap.<String, String> builder().putAll(
               metadata).put("Name", id.replaceAll(".*-", group + "-")).build() : metadata;
   }

   private Float getSpotPriceOrNull(TemplateOptions options) {
      return options instanceof AWSEC2TemplateOptions ? AWSEC2TemplateOptions.class.cast(options).getSpotPrice() : null;
   }

}