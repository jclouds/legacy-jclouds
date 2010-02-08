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
import org.jclouds.rackspace.cloudservers.CloudServersClient;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.options.ListOptions;
import org.jclouds.rackspace.reference.RackspaceConstants;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class CloudServersComputeService extends BaseComputeService {

   private final CloudServersClient client;
   private final Predicate<Server> serverActive;
   private final Predicate<Server> serverDeleted;
   private final Function<Server, NodeMetadata> serverToNodeMetadata;

   @Inject
   public CloudServersComputeService(Provider<TemplateBuilder> templateBuilderProvider,
            Provider<Map<String, ? extends Image>> images,
            Provider<Map<String, ? extends Size>> sizes,
            Provider<Map<String, ? extends Location>> locations, ComputeUtils utils,
            CloudServersClient client, Function<Server, NodeMetadata> serverToNodeMetadata,
            @Named("ACTIVE") Predicate<Server> serverActive,
            @Named("DELETED") Predicate<Server> serverDeleted,
            @Named(RackspaceConstants.PROPERTY_RACKSPACE_USER) String account,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      super(images, sizes, locations, templateBuilderProvider, account + "-%s-%d", utils, executor);
      this.client = client;
      this.serverActive = serverActive;
      this.serverDeleted = serverDeleted;
      this.serverToNodeMetadata = serverToNodeMetadata;
   }

   @Override
   protected NodeMetadata startNode(final String tag, final String name, final Template template) {
      Server server = client.createServer(name, Integer.parseInt(template.getImage().getId()),
               Integer.parseInt(template.getSize().getId()));
      serverActive.apply(server);
      return new NodeMetadataImpl(server.getId() + "", name, template.getLocation().getId(), null, server.getMetadata(), tag,
               NodeState.RUNNING, server.getAddresses().getPublicAddresses(), server.getAddresses()
                        .getPrivateAddresses(), ImmutableMap.<String, String> of(),
               new Credentials("root", server.getAdminPass()));
   }

   @Override
   public NodeMetadata getNodeMetadata(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");
      int serverId = Integer.parseInt(node.getId());
      Server server = client.getServer(serverId);
      return server == null ? null : serverToNodeMetadata.apply(server);
   }

   @Override
   protected Iterable<NodeMetadata> doGetNodes() {
      return Iterables.transform(client.listServers(ListOptions.Builder.withDetails()),
               serverToNodeMetadata);
   }

   @Override
   protected boolean doDestroyNode(ComputeMetadata node) {
      int serverId = Integer.parseInt(node.getId());
      // if false server wasn't around in the first place
      if (!client.deleteServer(serverId))
         return false;
      Server server = client.getServer(serverId);
      return server == null ? false : serverDeleted.apply(server);
   }

}