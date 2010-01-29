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
package org.jclouds.aws.ec2.compute;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withKeyName;
import static org.jclouds.compute.util.ComputeUtils.METADATA_TO_ID;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.domain.EC2Size;
import org.jclouds.aws.ec2.compute.domain.KeyPairCredentials;
import org.jclouds.aws.ec2.compute.domain.PortsRegionTag;
import org.jclouds.aws.ec2.compute.domain.RegionTag;
import org.jclouds.aws.ec2.compute.functions.CreateKeyPairIfNeeded;
import org.jclouds.aws.ec2.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.aws.ec2.compute.functions.RunningInstanceToNodeMetadata;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.options.RunInstancesOptions;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeSet;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.NodeSetImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.concurrent.ConcurrentUtils;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.internal.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class EC2ComputeService implements ComputeService {

   private static Function<RunningInstance, String> instanceToId = new Function<RunningInstance, String>() {
      @Override
      public String apply(RunningInstance from) {
         return from.getId();
      }
   };

   private static class NodeMatchesTag implements Predicate<NodeMetadata> {
      private final String tag;

      @Override
      public boolean apply(NodeMetadata from) {
         return from.getTag().equals(tag) && from.getState() != NodeState.TERMINATED;
      }

      public NodeMatchesTag(String tag) {
         super();
         this.tag = tag;
      }
   };

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final EC2Client ec2Client;
   protected final Map<RegionTag, KeyPairCredentials> credentialsMap;
   protected final Map<PortsRegionTag, String> securityGroupMap;
   protected final Provider<Map<String, ? extends Image>> images;
   protected final Provider<Map<String, ? extends Size>> sizes;
   protected final Provider<Map<String, ? extends Location>> locations;
   protected final CreateKeyPairIfNeeded createKeyPairIfNeeded;
   protected final CreateSecurityGroupIfNeeded createSecurityGroupIfNeeded;
   protected final Provider<TemplateBuilder> templateBuilderProvider;
   protected final Predicate<RunningInstance> instanceStateRunning;
   protected final Predicate<RunningInstance> instanceStateTerminated;
   protected final RunningInstanceToNodeMetadata runningInstanceToNodeMetadata;
   protected final ExecutorService executor;
   protected final ComputeUtils utils;

   @Inject
   public EC2ComputeService(EC2Client client, Provider<TemplateBuilder> templateBuilderProvider,
            Provider<Map<String, ? extends Image>> images,
            Provider<Map<String, ? extends Size>> sizes,
            Provider<Map<String, ? extends Location>> locations,
            Map<RegionTag, KeyPairCredentials> credentialsMap,
            Map<PortsRegionTag, String> securityGroupMap,
            CreateKeyPairIfNeeded createKeyPairIfNeeded,
            CreateSecurityGroupIfNeeded createSecurityGroupIfNeeded, ComputeUtils utils,
            @Named("RUNNING") Predicate<RunningInstance> instanceStateRunning,
            @Named("TERMINATED") Predicate<RunningInstance> instanceStateTerminated,
            RunningInstanceToNodeMetadata runningInstanceToNodeMetadata,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.templateBuilderProvider = templateBuilderProvider;
      this.ec2Client = client;
      this.images = images;
      this.utils = utils;
      this.sizes = sizes;
      this.locations = locations;
      this.credentialsMap = credentialsMap;
      this.createKeyPairIfNeeded = createKeyPairIfNeeded;
      this.createSecurityGroupIfNeeded = createSecurityGroupIfNeeded;
      this.securityGroupMap = securityGroupMap;
      this.instanceStateRunning = instanceStateRunning;
      this.instanceStateTerminated = instanceStateTerminated;
      this.runningInstanceToNodeMetadata = runningInstanceToNodeMetadata;
      this.executor = executor;
   }

   @Override
   public NodeSet runNodesWithTag(String tag, int count, final Template template) {
      checkArgument(tag.indexOf('-') == -1, "tag cannot contain hyphens");
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
      if (!credentialsMap.containsKey(regionTag)) {
         credentialsMap.put(regionTag, createKeyPairIfNeeded.apply(regionTag));
      }
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
      RunInstancesOptions instanceOptions = withKeyName(tag)// key
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
      final Set<NodeMetadata> nodes = Sets.newHashSet();
      Set<ListenableFuture<Void>> responses = Sets.newHashSet();
      for (final NodeMetadata node : Iterables.transform(Iterables.concat(ec2Client
               .getInstanceServices().describeInstancesInRegion(region,
                        Iterables.toArray(ids, String.class))), runningInstanceToNodeMetadata)) {
         responses.add(ConcurrentUtils.makeListenable(executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               try {
                  utils.runOptionsOnNode(node, template.getOptions());
                  logger.debug("<< options applied instance(%s)", node.getId());
                  nodes.add(node);
               } catch (Exception e) {
                  logger.error(e, "<< error applying instance(%s) [%s] destroying ", node.getId(),
                           e.getMessage());
                  destroyNode(node);
               }
               return null;
            }

         }), executor));
      }
      ConcurrentUtils.awaitCompletion(responses, executor, null, logger, "nodes");
      return new NodeSetImpl(nodes);
   }

   @Override
   public NodeMetadata getNodeMetadata(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");
      Region region = getRegionFromNodeOrDefault(node);
      RunningInstance runningInstance = Iterables.getOnlyElement(getAllRunningInstancesInRegion(
               region, node.getId()));
      return runningInstanceToNodeMetadata.apply(runningInstance);
   }

   private Iterable<RunningInstance> getAllRunningInstancesInRegion(Region region, String id) {
      return Iterables
               .concat(ec2Client.getInstanceServices().describeInstancesInRegion(region, id));
   }

   @Override
   public Map<String, NodeMetadata> getNodes() {
      logger.debug(">> listing servers");
      Map<String, NodeMetadata> nodes = doGetNodes();
      logger.debug("<< list(%d)", nodes.size());
      return nodes;
   }

   protected Map<String, NodeMetadata> doGetNodes() {
      Set<NodeMetadata> nodes = Sets.newHashSet();
      for (Region region : ImmutableSet.of(Region.US_EAST_1, Region.US_WEST_1, Region.EU_WEST_1)) {
         Iterables.addAll(nodes, Iterables.transform(Iterables.concat(ec2Client
                  .getInstanceServices().describeInstancesInRegion(region)),
                  runningInstanceToNodeMetadata));
      }
      return Maps.uniqueIndex(nodes, METADATA_TO_ID);
   }

   @Override
   public void destroyNode(ComputeMetadata metadata) {
      checkArgument(metadata.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + metadata.getType());
      checkNotNull(metadata.getId(), "node.id");
      NodeMetadata node = metadata instanceof NodeMetadata ? NodeMetadata.class.cast(metadata)
               : getNodeMetadata(metadata);
      String tag = checkNotNull(node.getTag(), "node.tag");

      Region region = getRegionFromNodeOrDefault(node);

      RunningInstance instance = getInstance(node, region);
      if (instance.getInstanceState() != InstanceState.TERMINATED) {
         logger.debug(">> terminating instance(%s)", node.getId());
         boolean success = false;
         while (!success) {
            ec2Client.getInstanceServices().terminateInstancesInRegion(region, node.getId());
            success = instanceStateTerminated.apply(getInstance(node, region));
         }
         logger.debug("<< terminated instance(%s) success(%s)", node.getId(), success);
      }
      if (Iterables.all(doGetNodes(tag), new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(NodeMetadata input) {
            return input.getState() == NodeState.TERMINATED;
         }
      })) {
         deleteKeyPair(region, tag);
         deleteSecurityGroup(region, tag);
      }
   }

   private RunningInstance getInstance(NodeMetadata node, Region region) {
      return Iterables.getOnlyElement(getAllRunningInstancesInRegion(region, node.getId()));
   }

   private void deleteSecurityGroup(Region region, String tag) {
      if (ec2Client.getSecurityGroupServices().describeSecurityGroupsInRegion(region, tag).size() > 0) {
         logger.debug(">> deleting securityGroup(%s)", tag);
         ec2Client.getSecurityGroupServices().deleteSecurityGroupInRegion(region, tag);
         securityGroupMap.remove(new PortsRegionTag(region, tag, null)); // TODO: test this clear
         // happens
         logger.debug("<< deleted securityGroup(%s)", tag);
      }
   }

   private void deleteKeyPair(Region region, String tag) {
      if (ec2Client.getKeyPairServices().describeKeyPairsInRegion(region, tag).size() > 0) {
         logger.debug(">> deleting keyPair(%s)", tag);
         ec2Client.getKeyPairServices().deleteKeyPairInRegion(region, tag);
         credentialsMap.remove(new RegionTag(region, tag)); // TODO: test this clear happens
         logger.debug("<< deleted keyPair(%s)", tag);
      }
   }

   private Region getRegionFromNodeOrDefault(ComputeMetadata node) {
      Location location = getLocations().get(node.getLocationId());
      Region region = location.getScope() == LocationScope.REGION ? Region.fromValue(location
               .getId()) : Region.fromValue(location.getParent());
      return region;
   }

   @Override
   public void destroyNodesWithTag(String tag) { // TODO parallel
      logger.debug(">> terminating servers by tag(%s)", tag);
      Set<ListenableFuture<Void>> responses = Sets.newHashSet();
      for (final NodeMetadata node : doGetNodes(tag)) {
         if (node.getState() != NodeState.TERMINATED)
            responses.add(ConcurrentUtils.makeListenable(executor.submit(new Callable<Void>() {
               @Override
               public Void call() throws Exception {
                  destroyNode(node);
                  return null;
               }
            }), executor));
      }
      ConcurrentUtils.awaitCompletion(responses, executor, null, logger, "nodes");
      logger.debug("<< destroyed");
   }

   @Override
   public Map<String, ? extends Location> getLocations() {
      return locations.get();
   }

   @Override
   public NodeSet getNodesWithTag(String tag) {
      logger.debug(">> listing servers by tag(%s)", tag);
      NodeSet nodes = doGetNodes(tag);
      logger.debug("<< list(%d)", nodes.size());
      return nodes;
   }

   protected NodeSet doGetNodes(String tag) {
      return new NodeSetImpl(Iterables.filter(doGetNodes().values(), new NodeMatchesTag(tag)));
   }

   @Override
   public Map<String, ? extends Size> getSizes() {
      return sizes.get();
   }

   @Override
   public Map<String, ? extends Image> getImages() {
      return images.get();
   }

   @Override
   public TemplateBuilder templateBuilder() {
      return templateBuilderProvider.get();
   }

}