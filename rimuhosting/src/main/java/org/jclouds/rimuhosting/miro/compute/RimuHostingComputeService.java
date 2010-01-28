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
package org.jclouds.rimuhosting.miro.compute;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeUtils.METADATA_TO_ID;
import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;
import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
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
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.Server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Ivan Meredith
 */
@Singleton
public class RimuHostingComputeService implements ComputeService {

   @VisibleForTesting
   static Iterable<InetAddress> getPublicAddresses(Server server) {
      Iterable<String> addresses = Iterables.concat(ImmutableList.of(server.getIpAddresses()
               .getPrimaryIp()), server.getIpAddresses().getSecondaryIps());
      return Iterables.transform(addresses, new Function<String, InetAddress>() {

         @Override
         public InetAddress apply(String from) {
            try {
               return InetAddress.getByName(from);
            } catch (UnknownHostException e) {
               // TODO: log the failure.
               return null;
            }
         }
      });
   }

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
   private final RimuHostingClient client;
   protected final Provider<Map<String, ? extends Image>> images;
   protected final Provider<Map<String, ? extends Size>> sizes;
   protected final Provider<Map<String, ? extends Location>> locations;
   protected final Provider<TemplateBuilder> templateBuilderProvider;
   private final ComputeUtils utils;
   private final ServerToNodeMetadata serverToNodeMetadata;
   protected final ExecutorService executor;

   @Inject
   public RimuHostingComputeService(RimuHostingClient client,
            Provider<TemplateBuilder> templateBuilderProvider,
            Provider<Map<String, ? extends Image>> images,
            Provider<Map<String, ? extends Size>> sizes, ServerToNodeMetadata serverToNodeMetadata,
            Provider<Map<String, ? extends Location>> locations, ComputeUtils utils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.client = client;
      this.images = images;
      this.sizes = sizes;
      this.locations = locations;
      this.utils = utils;
      this.templateBuilderProvider = templateBuilderProvider;
      this.serverToNodeMetadata = serverToNodeMetadata;
      this.executor = executor;
   }

   @Override
   public NodeSet runNodes(final String tag, int max, final Template template) {
      checkArgument(tag.indexOf('-') == -1, "tag cannot contain hyphens");
      logger.debug(">> running server image(%s) flavor(%s)", template.getImage().getId(), template
               .getSize().getId());

      final Set<NodeMetadata> nodes = Sets.newHashSet();
      Set<ListenableFuture<Void>> responses = Sets.newHashSet();
      for (int i = 0; i < max; i++) {
         final String name = String.format("%s-%d", tag, i + 1);
         responses.add(ConcurrentUtils.makeListenable(executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               NewServerResponse serverResponse = client.createServer(name, checkNotNull(template
                        .getImage().getId(), "imageId"), checkNotNull(template.getSize().getId(),
                        "sizeId"));
               NodeMetadata node = new NodeMetadataImpl(serverResponse.getServer().getId()
                        .toString(), name, template.getLocation().getId(),
                        null,
                        ImmutableMap.<String, String> of(),
                        tag,
                        NodeState.UNKNOWN,// TODO
                        // need a
                        // real
                        // state!
                        getPublicAddresses(serverResponse.getServer()),// no real useful data here..
                        ImmutableList.<InetAddress> of(), ImmutableMap.<String, String> of(),
                        new Credentials("root", serverResponse.getNewInstanceRequest()
                                 .getCreateOptions().getPassword()));
               nodes.add(node);
               logger.debug("<< started server(%s)", node.getId());
               // TODO! serverActive.apply(server);
               logger.debug("<< running server(%s)", node.getId());
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
      return serverToNodeMetadata.apply(client.getServer(Long.parseLong(node.getId())));
   }

   @Override
   public void destroyNode(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");

      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");

      logger.debug(">> deleting server(%s)", node.getId());
      client.destroyServer(new Long(node.getId()));
      logger.debug("<< deleted server(%s)", node.getId());
   }

   public static final Pattern TAG_PATTERN = Pattern.compile("[^-]+-([^-]+)-[0-9]+");

   @Singleton
   private static class ServerToNodeMetadata implements Function<Server, NodeMetadata> {

      @Override
      public NodeMetadata apply(Server from) {
         String locationId = "//TODO";
         String tag = from.getName().replaceAll("-[0-9]+", "");
         Credentials creds = null;
         NodeState state = NodeState.UNKNOWN;
         return new NodeMetadataImpl(from.getId() + "", from.getName(), locationId, null,
                  ImmutableMap.<String, String> of(), tag, state, getPublicAddresses(from),
                  ImmutableList.<InetAddress> of(), ImmutableMap.<String, String> of("state", from
                           .getState()), creds);
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
               .getServerList(), serverToNodeMetadata), METADATA_TO_ID);
      return map;
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
