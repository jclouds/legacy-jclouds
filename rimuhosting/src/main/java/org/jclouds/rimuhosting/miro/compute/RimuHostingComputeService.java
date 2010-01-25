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
import java.net.UnknownHostException;
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
import org.jclouds.domain.Credentials;
import org.jclouds.domain.ResourceLocation;
import org.jclouds.logging.Logger;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.Server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Ivan Meredith
 */
@Singleton
public class RimuHostingComputeService implements ComputeService {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final RimuHostingClient client;
   protected final Provider<Set<? extends Image>> images;
   protected final Provider<Set<? extends Size>> sizes;
   protected final Provider<TemplateBuilder> templateBuilderProvider;
   private final String location;

   @Inject
   public RimuHostingComputeService(RimuHostingClient client,
            Provider<TemplateBuilder> templateBuilderProvider, @ResourceLocation String location,
            Provider<Set<? extends Image>> images, Provider<Set<? extends Size>> sizes) {
      this.client = client;
      this.location = location;
      this.images = images;
      this.sizes = sizes;
      this.templateBuilderProvider = templateBuilderProvider;
   }

   @Override
   public CreateNodeResponse runNode(String name, Template template) {
      return this.runNode(name, template, RunNodeOptions.NONE);
   }

   @Override
   public CreateNodeResponse runNode(String name, Template template, RunNodeOptions options) {
      NewServerResponse serverResponse = client.createServer(name, checkNotNull(template.getImage()
               .getId(), "imageId"), checkNotNull(template.getSize().getId(), "sizeId"));
      return new CreateNodeResponseImpl(serverResponse.getServer().getId().toString(),
               name, location, null, ImmutableMap.<String, String> of(), NodeState.UNKNOWN,// TODO
                                                                                           // need a
                                                                                           // real
                                                                                           // state!
               getPublicAddresses(serverResponse.getServer()),// no real useful data here..
               ImmutableList.<InetAddress> of(), new Credentials("root", serverResponse
                        .getNewInstanceRequest().getCreateOptions().getPassword()), ImmutableMap
                        .<String, String> of());
   }

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

   public Set<ComputeMetadata> listNodes() {
      Set<ComputeMetadata> serverSet = Sets.newLinkedHashSet();
      Set<Server> servers = client.getServerList();
      for (Server server : servers) {
         serverSet.add(toNode(server));
      }
      return serverSet;
   }

   private NodeMetadataImpl toNode(Server server) {
      return new NodeMetadataImpl(server.getId() + "", server.getName(), location, null,
               ImmutableMap.<String, String> of(), NodeState.UNKNOWN, getPublicAddresses(server),
               ImmutableList.<InetAddress> of(), ImmutableMap.<String, String> of("state", server
                        .getState()));
   }

   @Override
   public NodeMetadata getNodeMetadata(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");
      return toNode(client.getServer(Long.parseLong(node.getId())));
   }

   @Override
   public void destroyNode(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");
      client.destroyServer(new Long(node.getId()));
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
      return this.templateBuilderProvider.get();
   }
}
