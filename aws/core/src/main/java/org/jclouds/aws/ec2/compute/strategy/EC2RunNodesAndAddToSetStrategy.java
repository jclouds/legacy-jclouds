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

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withKeyName;
import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.domain.EC2Size;
import org.jclouds.aws.ec2.compute.domain.PortsRegionTag;
import org.jclouds.aws.ec2.compute.domain.RegionTag;
import org.jclouds.aws.ec2.compute.functions.CreateNewKeyPair;
import org.jclouds.aws.ec2.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.aws.ec2.compute.functions.RunningInstanceToNodeMetadata;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.options.RunInstancesOptions;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
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

   private static Function<RunningInstance, String> instanceToId = new Function<RunningInstance, String>() {
      @Override
      public String apply(RunningInstance from) {
         return from.getId();
      }
   };
   protected final ComputeService computeService;
   protected final EC2Client ec2Client;
   protected final Map<RegionTag, KeyPair> credentialsMap;
   protected final Map<PortsRegionTag, String> securityGroupMap;
   protected final CreateNewKeyPair createNewKeyPair;
   protected final CreateSecurityGroupIfNeeded createSecurityGroupIfNeeded;
   protected final Predicate<RunningInstance> instanceStateRunning;
   protected final RunningInstanceToNodeMetadata runningInstanceToNodeMetadata;
   protected final ComputeUtils utils;

   @Inject
   protected EC2RunNodesAndAddToSetStrategy(ComputeService computeService, EC2Client ec2Client,
            Map<RegionTag, KeyPair> credentialsMap, Map<PortsRegionTag, String> securityGroupMap,
            CreateNewKeyPair createKeyPairIfNeeded,
            CreateSecurityGroupIfNeeded createSecurityGroupIfNeeded,
            @Named("RUNNING") Predicate<RunningInstance> instanceStateRunning,
            RunningInstanceToNodeMetadata runningInstanceToNodeMetadata, ComputeUtils utils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.computeService = computeService;
      this.ec2Client = ec2Client;
      this.credentialsMap = credentialsMap;
      this.securityGroupMap = securityGroupMap;
      this.createNewKeyPair = createKeyPairIfNeeded;
      this.createSecurityGroupIfNeeded = createSecurityGroupIfNeeded;
      this.instanceStateRunning = instanceStateRunning;
      this.runningInstanceToNodeMetadata = runningInstanceToNodeMetadata;
      this.utils = utils;
      this.executor = executor;
   }

   protected final ExecutorService executor;

   @Override
   public Map<?, ListenableFuture<Void>> execute(final String tag, final int count,
            final Template template, final Set<NodeMetadata> nodes) {
      checkArgument(template.getSize() instanceof EC2Size,
               "unexpected image type. should be EC2Size, was: " + template.getSize().getClass());
      EC2Size ec2Size = EC2Size.class.cast(template.getSize());

      // parse the availability zone of the request
      AvailabilityZone zone = template.getLocation().getScope() == LocationScope.ZONE ? AvailabilityZone
               .fromValue(template.getLocation().getId())
               : null;

      // if the location has a parent, it must be an availability zone.
      Region region = zone == null ? Region.fromValue(template.getLocation().getId()) : Region
               .fromValue(template.getLocation().getParent());

      // get or create incidental resources
      // TODO race condition. we were using MapMaker, but it doesn't seem to refresh properly when
      // another thread
      // deletes a key
      RegionTag regionTag = new RegionTag(region, tag);

      KeyPair keyPair = createNewKeyPair.apply(regionTag);

      credentialsMap.put(new RegionTag(region, keyPair.getKeyName()), keyPair);

      TemplateOptions options = template.getOptions();
      PortsRegionTag portsRegionTag = new PortsRegionTag(region, tag, options.getInboundPorts());
      if (!securityGroupMap.containsKey(portsRegionTag)) {
         securityGroupMap.put(portsRegionTag, createSecurityGroupIfNeeded.apply(portsRegionTag));
      }

      logger
               .debug(
                        ">> running %d instance region(%s) zone(%s) ami(%s) type(%s) keyPair(%s) securityGroup(%s)",
                        count, region, zone, template.getImage().getId(),
                        ec2Size.getInstanceType(), tag, tag);
      RunInstancesOptions instanceOptions = withKeyName(keyPair.getKeyName())// key
               .asType(ec2Size.getInstanceType())// instance size
               .withSecurityGroup(tag)// group I created above
               .withAdditionalInfo(tag);

      Reservation reservation = ec2Client.getInstanceServices().runInstancesInRegion(region, zone,
               template.getImage().getId(), 1, count, instanceOptions);
      Iterable<String> ids = Iterables.transform(reservation, instanceToId);

      String idsString = Joiner.on(',').join(ids);
      logger.debug("<< started instances(%s)", idsString);
      Iterables.all(reservation, instanceStateRunning);
      logger.debug("<< running instances(%s)", idsString);
      Map<NodeMetadata, ListenableFuture<Void>> responses = Maps.newHashMap();
      for (final NodeMetadata node : Iterables.transform(getInstances(region, ids),
               runningInstanceToNodeMetadata)) {
         responses.put(node, makeListenable(executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               try {
                  utils.runOptionsOnNode(node, template.getOptions());
                  logger.debug("<< options applied node(%s)", node.getId());
                  nodes.add(computeService.getNodeMetadata(node));
               } catch (Exception e) {
                  logger.error(e, "<< problem applying options to node(%s): ", node.getId(),
                           Throwables.getRootCause(e).getMessage());
                  if (!template.getOptions().shouldDestroyOnError())
                     nodes.add(computeService.getNodeMetadata(node));
               }
               return null;
            }

         }), executor));
      }
      return responses;
   }

   private Iterable<RunningInstance> getInstances(Region region, Iterable<String> ids) {
      return Iterables.concat(ec2Client.getInstanceServices().describeInstancesInRegion(region,
               Iterables.toArray(ids, String.class)));
   }

}