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
package org.jclouds.aws.ec2.compute.strategy;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_GENERATE_INSTANCE_NAMES;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.compute.functions.PresentSpotRequestsAndInstances;
import org.jclouds.aws.ec2.domain.LaunchSpecification;
import org.jclouds.aws.ec2.functions.SpotInstanceRequestToAWSRunningInstance;
import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.aws.ec2.options.RequestSpotInstancesOptions;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.strategy.EC2CreateNodesInGroupThenAddToSet;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AWSEC2CreateNodesInGroupThenAddToSet extends EC2CreateNodesInGroupThenAddToSet {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   private Logger logger = Logger.NULL;

   @VisibleForTesting
   private final AWSEC2Client client;
   private final SpotInstanceRequestToAWSRunningInstance spotConverter;

   @Inject
   protected AWSEC2CreateNodesInGroupThenAddToSet(
         AWSEC2Client client,
         @Named("ELASTICIP") LoadingCache<RegionAndName, String> elasticIpCache,
         @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
         @Named(PROPERTY_EC2_GENERATE_INSTANCE_NAMES) boolean generateInstanceNames,
         CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturncustomize,
         PresentSpotRequestsAndInstances instancePresent,
         Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata,
         LoadingCache<RunningInstance, Optional<LoginCredentials>> instanceToCredentials,
         Map<String, Credentials> credentialStore, ComputeUtils utils,
         SpotInstanceRequestToAWSRunningInstance spotConverter) {
      super(client, elasticIpCache, nodeRunning, createKeyPairAndSecurityGroupsAsNeededAndReturncustomize,
            instancePresent, runningInstanceToNodeMetadata, instanceToCredentials, credentialStore, utils);
      this.client = checkNotNull(client, "client");
      this.spotConverter = checkNotNull(spotConverter, "spotConverter");
   }

   @Override
   protected Set<RunningInstance> createNodesInRegionAndZone(String region, String zone, String group,
            int count, Template template, RunInstancesOptions instanceOptions) {
      Float spotPrice = getSpotPriceOrNull(template.getOptions());
      if (spotPrice != null) {
         AWSEC2TemplateOptions awsOptions = AWSEC2TemplateOptions.class.cast(template.getOptions());
         LaunchSpecification spec = AWSRunInstancesOptions.class.cast(instanceOptions).getLaunchSpecificationBuilder()
               .imageId(template.getImage().getProviderId()).availabilityZone(zone).subnetId(awsOptions.getSubnetId())
               .iamInstanceProfileArn(awsOptions.getIAMInstanceProfileArn())
               .iamInstanceProfileName(awsOptions.getIAMInstanceProfileName()).build();
         RequestSpotInstancesOptions options = awsOptions.getSpotOptions();
         if (logger.isDebugEnabled())
            logger.debug(">> requesting %d spot instances region(%s) price(%f) spec(%s) options(%s)", count, region,
                     spotPrice, spec, options);
         return ImmutableSet.<RunningInstance> copyOf(transform(client.getSpotInstanceServices()
               .requestSpotInstancesInRegion(region, spotPrice, count, spec, options), spotConverter));
      }
      return super.createNodesInRegionAndZone(region, zone, group, count, template, instanceOptions);
   }
   
   private Float getSpotPriceOrNull(TemplateOptions options) {
      return options instanceof AWSEC2TemplateOptions ? AWSEC2TemplateOptions.class.cast(options).getSpotPrice() : null;
   }

}
