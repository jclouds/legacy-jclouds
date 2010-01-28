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
import static org.jclouds.compute.util.ComputeUtils.METADATA_TO_ID;
import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;
import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
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
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.domain.internal.NodeSetImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.concurrent.ConcurrentUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudservers.CloudServersClient;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;
import org.jclouds.rackspace.cloudservers.options.ListOptions;
import org.jclouds.rackspace.reference.RackspaceConstants;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Adrian Cole
 */
@Singleton
public class CloudServersComputeService implements ComputeService {

   private static class NodeMatchesTag implements Predicate<NodeMetadata> {
      private final String tag;

      @Override
      public boolean apply(NodeMetadata from) {
         return from.getTag().equals(tag);
      }

      public NodeMatchesTag(String tag) {
         super();
         this.tag = tag;
      }
   };

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final org.jclouds.rackspace.cloudservers.CloudServersClient client;
   protected final Provider<Map<String, ? extends Image>> images;
   protected final Provider<Map<String, ? extends Size>> sizes;
   protected final Provider<Map<String, ? extends Location>> locations;
   protected final Provider<TemplateBuilder> templateBuilderProvider;
   private final ComputeUtils utils;
   private final Predicate<Server> serverActive;
   private final ServerToNodeMetadata serverToNodeMetadata;
   private final Predicate<Server> serverDeleted;
   protected final ExecutorService executor;
   private final String account;

   @Inject
   public CloudServersComputeService(CloudServersClient client,
            Provider<TemplateBuilder> templateBuilderProvider,
            Provider<Map<String, ? extends Image>> images,
            Provider<Map<String, ? extends Size>> sizes,
            Provider<Map<String, ? extends Location>> locations, ComputeUtils utils,
            @Named("ACTIVE") Predicate<Server> serverActive,
            @Named("DELETED") Predicate<Server> serverDeleted,
            @Named(RackspaceConstants.PROPERTY_RACKSPACE_USER) String account,
            ServerToNodeMetadata serverToNodeMetadata,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.client = client;
      this.images = images;
      this.sizes = sizes;
      this.locations = locations;
      this.utils = utils;
      this.templateBuilderProvider = templateBuilderProvider;
      this.serverActive = serverActive;
      this.serverDeleted = serverDeleted;
      this.account = account;
      this.serverToNodeMetadata = serverToNodeMetadata;
      this.executor = executor;
   }

   private static Map<ServerStatus, NodeState> serverToNodeState = ImmutableMap
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
   public NodeSet runNodes(final String tag, int max, final Template template) {
      checkArgument(tag.indexOf('-') == -1, "tag cannot contain hyphens");
      logger.debug(">> running server image(%s) flavor(%s)", template.getImage().getId(), template
               .getSize().getId());

      final Set<NodeMetadata> nodes = Sets.newHashSet();
      Set<ListenableFuture<Void>> responses = Sets.newHashSet();
      for (int i = 0; i < max; i++) {
         final String name = String.format("%s-%s-%d", account, tag, i + 1);
         responses.add(ConcurrentUtils.makeListenable(executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               Server server = client.createServer(name, Integer.parseInt(template.getImage()
                        .getId()), Integer.parseInt(template.getSize().getId()));

               NodeMetadata node = new NodeMetadataImpl(server.getId() + "", name, null, null,
                        server.getMetadata(), tag, NodeState.RUNNING, server.getAddresses()
                                 .getPublicAddresses(),
                        server.getAddresses().getPrivateAddresses(), ImmutableMap
                                 .<String, String> of(), new Credentials("root", server
                                 .getAdminPass()));
               nodes.add(node);
               logger.debug("<< started server(%s)", server.getId());
               serverActive.apply(server);
               logger.debug("<< running server(%s)", server.getId());
               if (template.getOptions().getRunScript() != null) {
                  utils.runScriptOnNode(node, template.getOptions().getRunScript());
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
      return serverToNodeMetadata.apply(client.getServer(Integer.parseInt(node.getId())));
   }

   public static final Pattern TAG_PATTERN = Pattern.compile("[^-]+-([^-]+)-[0-9]+");

   @Singleton
   private static class ServerToNodeMetadata implements Function<Server, NodeMetadata> {
      private final Location location;

      @SuppressWarnings("unused")
      @Inject
      ServerToNodeMetadata(Location location) {
         this.location = location;
      }

      @Override
      public NodeMetadata apply(Server from) {
         Matcher matcher = TAG_PATTERN.matcher(from.getName());
         final String tag = matcher.find() ? matcher.group(1) : null;
         return new NodeMetadataImpl(from.getId() + "", from.getName(), location.getId(), null,
                  from.getMetadata(), tag, serverToNodeState.get(from.getStatus()), from
                           .getAddresses().getPublicAddresses(), from.getAddresses()
                           .getPrivateAddresses(), ImmutableMap.<String, String> of(), null);
      }
   }

   @Override
   public Map<String, ? extends ComputeMetadata> getNodes() {
      logger.debug(">> listing servers");
      ImmutableMap<String, NodeMetadata> map = doGetNodes();
      logger.debug("<< list(%d)", map.size());
      return map;
   }

   private ImmutableMap<String, NodeMetadata> doGetNodes() {
      ImmutableMap<String, NodeMetadata> map = Maps.uniqueIndex(Iterables.transform(client
               .listServers(ListOptions.Builder.withDetails()), serverToNodeMetadata),
               METADATA_TO_ID);
      return map;
   }

   @Override
   public void destroyNode(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");

      logger.debug(">> deleting server(%s)", node.getId());
      int serverId = Integer.parseInt(node.getId());
      client.deleteServer(serverId);
      boolean successful = serverDeleted.apply(client.getServer(serverId));
      logger.debug("<< deleted server(%s) success(%s)", node.getId(), successful);
   }

   @Override
   public void destroyNodes(String tag) { // TODO parallel
      logger.debug(">> terminating servers by tag(%s)", tag);
      Set<ListenableFuture<Void>> responses = Sets.newHashSet();
      for (final NodeMetadata node : doGetNodes(tag)) {
         responses.add(makeListenable(executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               destroyNode(node);
               return null;
            }
         }), executor));
      }
      awaitCompletion(responses, executor, null, logger, "nodes");
      logger.debug("<< destroyed");
   }

   @Override
   public Map<String, ? extends Location> getLocations() {
      return locations.get();
   }

   @Override
   public NodeSet getNodes(String tag) {
      logger.debug(">> listing servers by tag(%s)", tag);
      NodeSet nodes = doGetNodes(tag);
      logger.debug("<< list(%d)", nodes.size());
      return nodes;
   }

   protected NodeSet doGetNodes(final String tag) {
      Iterable<NodeMetadata> nodes = Iterables.filter(Iterables.transform(doGetNodes().values(),
               new Function<ComputeMetadata, NodeMetadata>() {

                  @Override
                  public NodeMetadata apply(ComputeMetadata from) {
                     return getNodeMetadata(from);
                  }

               }), new Predicate<NodeMetadata>() {

         @Override
         public boolean apply(NodeMetadata input) {
            return tag.equals(input.getTag());
         }

      });
      return new NodeSetImpl(Iterables.filter(nodes, new NodeMatchesTag(tag)));
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