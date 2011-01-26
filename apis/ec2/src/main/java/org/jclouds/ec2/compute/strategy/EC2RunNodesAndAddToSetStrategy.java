/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.ec2.compute.strategy;

import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.ec2.compute.util.EC2ComputeUtils.getZoneFromLocationOrNull;
import static org.jclouds.ec2.compute.util.EC2ComputeUtils.instanceToId;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

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
   final CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturncustomize;
   @VisibleForTesting
   final Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata;
   @VisibleForTesting
   final ComputeUtils utils;
   final Predicate<RunningInstance> instancePresent;
   final Function<RunningInstance, Credentials> instanceToCredentials;
   final Map<String, Credentials> credentialStore;

   @Inject
   EC2RunNodesAndAddToSetStrategy(
            EC2Client client,
            CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions createKeyPairAndSecurityGroupsAsNeededAndReturncustomize,
            @Named("PRESENT") Predicate<RunningInstance> instancePresent,
            Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata,
            Function<RunningInstance, Credentials> instanceToCredentials, Map<String, Credentials> credentialStore,
            ComputeUtils utils) {
      this.client = client;
      this.instancePresent = instancePresent;
      this.createKeyPairAndSecurityGroupsAsNeededAndReturncustomize = createKeyPairAndSecurityGroupsAsNeededAndReturncustomize;
      this.runningInstanceToNodeMetadata = runningInstanceToNodeMetadata;
      this.instanceToCredentials = instanceToCredentials;
      this.credentialStore = credentialStore;
      this.utils = utils;
   }

   @Override
   public Map<?, Future<Void>> execute(String tag, int count, Template template, Set<NodeMetadata> goodNodes,
            Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      Reservation<? extends RunningInstance> reservation = createKeyPairAndSecurityGroupsAsNeededThenRunInstances(tag,
               count, template);

      Iterable<String> ids = transform(reservation, instanceToId);

      String idsString = Joiner.on(',').join(ids);
      if (Iterables.size(ids) > 0) {
         logger.debug("<< started instances(%s)", idsString);
         all(reservation, instancePresent);
         logger.debug("<< present instances(%s)", idsString);
         populateCredentials(reservation);
      }

      return utils.customizeNodesAndAddToGoodMapOrPutExceptionIntoBadMap(template.getOptions(), transform(reservation,
               runningInstanceToNodeMetadata), goodNodes, badNodes, customizationResponses);
   }

   protected void populateCredentials(Reservation<? extends RunningInstance> reservation) {
      RunningInstance instance1 = Iterables.get(reservation, 0);
      Credentials credentials = instanceToCredentials.apply(instance1);
      if (credentials != null)
         for (RunningInstance instance : reservation)
            credentialStore.put("node#" + instance.getRegion() + "/" + instance.getId(), credentials);
   }

   @VisibleForTesting
   Reservation<? extends RunningInstance> createKeyPairAndSecurityGroupsAsNeededThenRunInstances(String tag, int count,
            Template template) {
      String region = AWSUtils.getRegionFromLocationOrNull(template.getLocation());
      String zone = getZoneFromLocationOrNull(template.getLocation());

      RunInstancesOptions instanceOptions = createKeyPairAndSecurityGroupsAsNeededAndReturncustomize.execute(region,
               tag, template);

      if (EC2TemplateOptions.class.cast(template.getOptions()).isMonitoringEnabled())
         instanceOptions.enableMonitoring();

      if (logger.isDebugEnabled())
         logger.debug(">> running %d instance region(%s) zone(%s) ami(%s) params(%s)", count, region, zone, template
                  .getImage().getProviderId(), instanceOptions.buildFormParameters());

      return client.getInstanceServices().runInstancesInRegion(region, zone, template.getImage().getProviderId(), 1,
               count, instanceOptions);
   }

}
