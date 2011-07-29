/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

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
import org.jclouds.ec2.compute.strategy.EC2CreateNodesInGroupThenAddToSet;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

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

   @Inject
   protected AWSEC2CreateNodesInGroupThenAddToSet(
            AWSEC2Client client,
            Provider<TemplateBuilder> templateBuilderProvider,
            CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturncustomize,
            AWSEC2InstancePresent instancePresent,
            Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata,
            Function<RunningInstance, Credentials> instanceToCredentials, Map<String, Credentials> credentialStore,
            ComputeUtils utils, SpotInstanceRequestToAWSRunningInstance spotConverter) {
      super(client, templateBuilderProvider, createKeyPairAndSecurityGroupsAsNeededAndReturncustomize, instancePresent,
               runningInstanceToNodeMetadata, instanceToCredentials, credentialStore, utils);
      this.client = checkNotNull(client, "client");
      this.spotConverter = checkNotNull(spotConverter, "spotConverter");
   }

   protected Iterable<? extends RunningInstance> createNodesInRegionAndZone(String region, String zone, int count,
            Template template, RunInstancesOptions instanceOptions) {
      Float spotPrice = getSpotPriceOrNull(template.getOptions());
      if (spotPrice != null) {
         LaunchSpecification spec = AWSRunInstancesOptions.class.cast(instanceOptions).getLaunchSpecificationBuilder()
                  .imageId(template.getImage().getProviderId()).availabilityZone(zone).build();
         RequestSpotInstancesOptions options = AWSEC2TemplateOptions.class.cast(template.getOptions()).getSpotOptions();
         if (logger.isDebugEnabled())
            logger.debug(">> requesting %d spot instances region(%s) price(%f) spec(%s) options(%s)", count, region,
                     spotPrice, spec, options);

         return Iterables.transform(client.getSpotInstanceServices().requestSpotInstancesInRegion(region, spotPrice,
                  count, spec, options), spotConverter);
      } else {
         return super.createNodesInRegionAndZone(region, zone, count, template, instanceOptions);
      }

   }

   private Float getSpotPriceOrNull(TemplateOptions options) {
      return options instanceof AWSEC2TemplateOptions ? AWSEC2TemplateOptions.class.cast(options).getSpotPrice() : null;
   }

}