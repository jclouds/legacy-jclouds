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

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Ivan Meredith
 */
@Singleton
public class RimuHostingComputeService extends BaseComputeService {

   private final RimuHostingClient client;
   private final Function<Server, NodeMetadata> serverToNodeMetadata;
   private final Map<RunningState, NodeState> runningStateToNodeState;
   private final Predicate<Server> serverRunning;
   private final Predicate<Server> serverDestroyed;
   private final Function<Server, Iterable<InetAddress>> getPublicAddresses;

   @Inject
   public RimuHostingComputeService(Provider<TemplateBuilder> templateBuilderProvider,
            Provider<Map<String, ? extends Image>> images,
            Provider<Map<String, ? extends Size>> sizes,
            Provider<Map<String, ? extends Location>> locations, ComputeUtils utils,
            RimuHostingClient client, Map<RunningState, NodeState> runningStateToNodeState,
            @Named("RUNNING") Predicate<Server> serverRunning,
            @Named("DESTROYED") Predicate<Server> serverDestroyed,
            Function<Server, NodeMetadata> serverToNodeMetadata,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            Function<Server, Iterable<InetAddress>> getPublicAddresses) {
      super(images, sizes, locations, templateBuilderProvider, "%s-%d", utils, executor);
      this.client = client;
      this.runningStateToNodeState = runningStateToNodeState;
      this.serverRunning = serverRunning;
      this.serverDestroyed = serverDestroyed;
      this.serverToNodeMetadata = serverToNodeMetadata;
      this.getPublicAddresses = getPublicAddresses;
   }

   @Override
   protected NodeMetadata startNode(final String tag, final String name, final Template template) {
      NewServerResponse serverResponse = client.createServer(name, checkNotNull(template.getImage()
               .getId(), "imageId"), checkNotNull(template.getSize().getId(), "sizeId"));
      serverRunning.apply(serverResponse.getServer());
      Server server = client.getServer(serverResponse.getServer().getId());
      // we have to lookup the new details in order to retrieve the currently assigned ip address.
      NodeMetadata node = new NodeMetadataImpl(server.getId().toString(), name, template
               .getLocation().getId(), null, ImmutableMap.<String, String> of(), tag,
               runningStateToNodeState.get(server.getState()), getPublicAddresses.apply(server),
               ImmutableList.<InetAddress> of(), ImmutableMap.<String, String> of(),
               new Credentials("root", serverResponse.getNewInstanceRequest().getCreateOptions()
                        .getPassword()));
      return node;
   }

   @Override
   public NodeMetadata getNodeMetadata(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");
      long serverId = Long.parseLong(node.getId());
      Server server = client.getServer(serverId);
      return server == null ? null : serverToNodeMetadata.apply(server);
   }

   @Override
   protected Iterable<NodeMetadata> doGetNodes() {
      return Iterables.transform(client.getServerList(), serverToNodeMetadata);
   }

   @Override
   protected boolean doDestroyNode(ComputeMetadata node) {
      long serverId = Long.parseLong(node.getId());
      client.destroyServer(serverId);
      return serverDestroyed.apply(client.getServer(serverId));
   }

}