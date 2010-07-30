/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.aws.ec2.compute.util.EC2ComputeUtils.getRegionFromLocationOrNull;
import static org.jclouds.aws.ec2.compute.util.EC2ComputeUtils.getZoneFromLocationOrNull;
import static org.jclouds.aws.ec2.compute.util.EC2ComputeUtils.instanceToId;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.functions.RunningInstanceToNodeMetadata;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.options.RunInstancesOptions;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;

/**
 * creates futures that correlate to
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2RunNodesAndAddToSetStrategy implements RunNodesAndAddToSetStrategy {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @VisibleForTesting
   final EC2Client client;
   @VisibleForTesting
   final CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions;
   @VisibleForTesting
   final RunningInstanceToNodeMetadata runningInstanceToNodeMetadata;
   @VisibleForTesting
   final ComputeUtils utils;

   final Predicate<RunningInstance> instancePresent;

   @Inject
   EC2RunNodesAndAddToSetStrategy(
            EC2Client client,
            CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions,
            @Named("PRESENT") Predicate<RunningInstance> instancePresent,
            RunningInstanceToNodeMetadata runningInstanceToNodeMetadata, ComputeUtils utils) {
      this.client = client;
      this.instancePresent = instancePresent;
      this.createKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions = createKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions;
      this.runningInstanceToNodeMetadata = runningInstanceToNodeMetadata;
      this.utils = utils;
   }

   @Override
   public Map<?, Future<Void>> execute(String tag, int count, Template template, Set<NodeMetadata> goodNodes,
            Map<NodeMetadata, Exception> badNodes) {

      Reservation<? extends RunningInstance> reservation = createKeyPairAndSecurityGroupsAsNeededThenRunInstances(tag,
               count, template);

      Iterable<String> ids = transform(reservation, instanceToId);

      String idsString = Joiner.on(',').join(ids);

      logger.debug("<< started instances(%s)", idsString);
      all(reservation, instancePresent);
      logger.debug("<< present instances(%s)", idsString);

      return utils.runOptionsOnNodesAndAddToGoodSetOrPutExceptionIntoBadMap(template.getOptions(), transform(
               reservation, runningInstanceToNodeMetadata), goodNodes, badNodes);
   }

   @VisibleForTesting
   Reservation<? extends RunningInstance> createKeyPairAndSecurityGroupsAsNeededThenRunInstances(String tag, int count,
            Template template) {
      String region = getRegionFromLocationOrNull(template.getLocation());
      String zone = getZoneFromLocationOrNull(template.getLocation());

      RunInstancesOptions instanceOptions = createKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.execute(region,
               tag, template);

      if (logger.isDebugEnabled())
         logger.debug(">> running %d instance region(%s) zone(%s) ami(%s) params(%s)", count, region, zone, template
                  .getImage().getProviderId(), instanceOptions.buildFormParameters());

      return client.getInstanceServices().runInstancesInRegion(region, zone, template.getImage().getProviderId(), 1,
               count, instanceOptions);
   }

}