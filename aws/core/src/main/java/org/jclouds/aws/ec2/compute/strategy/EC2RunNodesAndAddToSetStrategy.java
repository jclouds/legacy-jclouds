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
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.aws.ec2.compute.util.EC2ComputeUtils.getRegionFromLocationOrNull;
import static org.jclouds.aws.ec2.compute.util.EC2ComputeUtils.getZoneFromLocationOrNull;
import static org.jclouds.aws.ec2.compute.util.EC2ComputeUtils.instanceToId;

import java.util.Map;
import java.util.Set;

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
import com.google.common.util.concurrent.ListenableFuture;

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
   final CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions;
   @VisibleForTesting
   final Predicate<RunningInstance> instanceStateRunning;
   @VisibleForTesting
   final RunningInstanceToNodeMetadata runningInstanceToNodeMetadata;
   @VisibleForTesting
   final ComputeUtils utils;

   @Inject
   EC2RunNodesAndAddToSetStrategy(
            EC2Client client,
            CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions,
            @Named("RUNNING") Predicate<RunningInstance> instanceStateRunning,
            RunningInstanceToNodeMetadata runningInstanceToNodeMetadata, ComputeUtils utils) {
      this.client = client;
      this.createKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions = createKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions;
      this.instanceStateRunning = instanceStateRunning;
      this.runningInstanceToNodeMetadata = runningInstanceToNodeMetadata;
      this.utils = utils;
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(String tag, int count, Template template,
            Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes) {

      Reservation reservation = createKeyPairAndSecurityGroupsAsNeededThenRunInstances(tag, count,
               template);

      Iterable<NodeMetadata> runningNodes = blockUntilInstancesAreRunningAndConvertToNodes(reservation);

      return utils.runOptionsOnNodesAndAddToGoodSetOrPutExceptionIntoBadMap(template.getOptions(),
               runningNodes, goodNodes, badNodes);
   }

   @VisibleForTesting
   Iterable<NodeMetadata> blockUntilInstancesAreRunningAndConvertToNodes(Reservation reservation) {
      return transform(blockUntilInstancesAreRunning(reservation), runningInstanceToNodeMetadata);
   }

   @VisibleForTesting
   Iterable<RunningInstance> blockUntilInstancesAreRunning(Reservation reservation) {
      Iterable<String> ids = transform(reservation, instanceToId);

      String idsString = Joiner.on(',').join(ids);
      logger.debug("<< started instances(%s)", idsString);
      all(reservation, instanceStateRunning);
      logger.debug("<< running instances(%s)", idsString);

      return getInstances(reservation.getRegion(), ids);
   }

   @VisibleForTesting
   Reservation createKeyPairAndSecurityGroupsAsNeededThenRunInstances(String tag, int count,
            Template template) {
      String region = getRegionFromLocationOrNull(template.getLocation());
      String zone = getZoneFromLocationOrNull(template.getLocation());

      RunInstancesOptions instanceOptions = createKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions
               .execute(region, tag, template);

      if (logger.isDebugEnabled())
         logger.debug(">> running %d instance region(%s) zone(%s) ami(%s) params(%s)", count,
                  region, zone, template.getImage().getProviderId(), instanceOptions
                           .buildFormParameters());

      return client.getInstanceServices().runInstancesInRegion(region, zone,
               template.getImage().getProviderId(), 1, count, instanceOptions);
   }

   private Iterable<RunningInstance> getInstances(String region, Iterable<String> ids) {
      return concat(client.getInstanceServices().describeInstancesInRegion(region,
               toArray(ids, String.class)));
   }

}