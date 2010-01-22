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
package org.jclouds.rackspace.cloudservers.compute;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.CreateNodeResponseImpl;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.options.RunNodeOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.ResourceLocation;
import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudservers.CloudServersClient;
import org.jclouds.rackspace.cloudservers.compute.domain.CloudServersImage;
import org.jclouds.rackspace.cloudservers.compute.domain.CloudServersSize;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;
import org.jclouds.rackspace.cloudservers.options.ListOptions;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class CloudServersComputeService implements ComputeService {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final org.jclouds.rackspace.cloudservers.CloudServersClient client;
   protected final Provider<Set<? extends Image>> images;
   protected final Provider<Set<? extends Size>> sizes;
   protected final Provider<TemplateBuilder> templateBuilderProvider;
   private final String location;
   private final ComputeUtils utils;
   private final Predicate<Server> serverActive;
   private final ServerToNodeMetadata serverToNodeMetadata;

   @Inject
   public CloudServersComputeService(CloudServersClient client,
            Provider<TemplateBuilder> templateBuilderProvider, @ResourceLocation String location,
            Provider<Set<? extends Image>> images, Provider<Set<? extends Size>> sizes,
            ComputeUtils utils, Predicate<Server> serverActive,
            ServerToNodeMetadata serverToNodeMetadata) {
      this.location = location;
      this.client = client;
      this.images = images;
      this.sizes = sizes;
      this.utils = utils;
      this.templateBuilderProvider = templateBuilderProvider;
      this.serverActive = serverActive;
      this.serverToNodeMetadata = serverToNodeMetadata;
   }

   private static Map<ServerStatus, NodeState> instanceToNodeState = ImmutableMap
            .<ServerStatus, NodeState> builder().put(ServerStatus.ACTIVE, NodeState.RUNNING)//
            .put(ServerStatus.SUSPENDED, NodeState.SUSPENDED)//
            .put(ServerStatus.DELETED, NodeState.TERMINATED)//
            .put(ServerStatus.QUEUE_RESIZE, NodeState.PENDING)//
            .put(ServerStatus.PREP_RESIZE, NodeState.PENDING)//
            .put(ServerStatus.RESIZE, NodeState.PENDING)//
            .put(ServerStatus.VERIFY_RESIZE, NodeState.PENDING)//
            .put(ServerStatus.QUEUE_MOVE, NodeState.PENDING)//
            .put(ServerStatus.PREP_MOVE, NodeState.PENDING)//
            .put(ServerStatus.MOVE, NodeState.PENDING)//
            .put(ServerStatus.VERIFY_MOVE, NodeState.PENDING)//
            .put(ServerStatus.RESCUE, NodeState.PENDING)//
            .put(ServerStatus.ERROR, NodeState.ERROR)//
            .put(ServerStatus.BUILD, NodeState.PENDING)//
            .put(ServerStatus.RESTORING, NodeState.PENDING)//
            .put(ServerStatus.PASSWORD, NodeState.PENDING)//
            .put(ServerStatus.REBUILD, NodeState.PENDING)//
            .put(ServerStatus.DELETE_IP, NodeState.PENDING)//
            .put(ServerStatus.SHARE_IP_NO_CONFIG, NodeState.PENDING)//
            .put(ServerStatus.SHARE_IP, NodeState.PENDING)//
            .put(ServerStatus.REBOOT, NodeState.PENDING)//
            .put(ServerStatus.HARD_REBOOT, NodeState.PENDING)//
            .put(ServerStatus.UNKNOWN, NodeState.UNKNOWN).build();

   @Override
   public CreateNodeResponse runNode(String name, Template template) {
      return this.runNode(name, template, RunNodeOptions.NONE);
   }

   @Override
   public CreateNodeResponse runNode(String name, Template template, RunNodeOptions options) {
      checkArgument(template.getImage() instanceof CloudServersImage,
               "unexpected image type. should be CloudServersImage, was: "
                        + template.getImage().getClass());
      CloudServersImage cloudServersImage = CloudServersImage.class.cast(template.getImage());
      checkArgument(template.getSize() instanceof CloudServersSize,
               "unexpected size type. should be CloudServersSize, was: "
                        + template.getSize().getClass());
      CloudServersSize cloudServersSize = CloudServersSize.class.cast(template.getSize());

      logger.debug(">> running instance location(%s) image(%s) flavor(%s)", location,
               cloudServersImage.getId(), template.getSize().getId());

      Server server = client.createServer(name, cloudServersImage.getImage().getId(),
               cloudServersSize.getFlavor().getId());

      CreateNodeResponse node = new CreateNodeResponseImpl(server.getId() + "", name, location,
               null, server.getMetadata(), NodeState.RUNNING, server.getAddresses()
                        .getPublicAddresses(), server.getAddresses().getPrivateAddresses(),
               new Credentials("root", server.getAdminPass()), ImmutableMap.<String, String> of());
      logger.debug("<< started instance(%s)", server.getId());
      serverActive.apply(server);
      logger.debug("<< running instance(%s)", server.getId());
      if (options.getRunScript() != null) {
         utils.runScriptOnNode(node, options.getRunScript());
      }
      return node;
   }

   @Override
   public NodeMetadata getNodeMetadata(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");
      return serverToNodeMetadata.apply(client.getServer(Integer.parseInt(node.getId())));
   }

   @Singleton
   private static class ServerToNodeMetadata implements Function<Server, NodeMetadata> {
      private final String location;

      @SuppressWarnings("unused")
      @Inject
      ServerToNodeMetadata(@ResourceLocation String location) {
         this.location = location;
      }

      @Override
      public NodeMetadata apply(Server from) {
         return new NodeMetadataImpl(from.getId() + "", from.getName(), location, null, from
                  .getMetadata(), instanceToNodeState.get(from.getStatus()), from.getAddresses()
                  .getPublicAddresses(), from.getAddresses().getPrivateAddresses(), ImmutableMap
                  .<String, String> of());
      }
   }

   @Override
   public Set<ComputeMetadata> listNodes() {
      logger.debug(">> listing servers");
      Set<ComputeMetadata> servers = Sets.newHashSet();
      Iterables.addAll(servers, Iterables.transform(client.listServers(ListOptions.Builder
               .withDetails()), serverToNodeMetadata));
      logger.debug("<< list(%d)", servers.size());
      return servers;
   }

   @Override
   public void destroyNode(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");

      logger.debug(">> terminating instance(%s)", node.getId());
      boolean success = client.deleteServer(Integer.parseInt(node.getId()));
      logger.debug("<< terminated instance(%s) success(%s)", node.getId(), success);
   }

   @Override
   public Set<? extends Size> listSizes() {
      return sizes.get();
   }

   @Override
   public Set<? extends Image> listImages() {
      return images.get();
   }

   @Override
   public TemplateBuilder templateBuilder() {
      return templateBuilderProvider.get();
   }

}