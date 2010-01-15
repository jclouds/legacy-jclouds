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
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Profile;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.ComputeMetadataImpl;
import org.jclouds.compute.domain.internal.CreateNodeResponseImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
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

   RimuHostingClient rhClient;

   @Inject
   public RimuHostingComputeService(RimuHostingClient rhClient) {
      this.rhClient = rhClient;
   }

   private Map<Image, String> imageNameMap = ImmutableMap.<Image, String> builder().put(
            Image.CENTOS_53, "centos53").put(Image.UBUNTU_90, "ubuntu904").build();
   private Map<Profile, String> profileNameMap = ImmutableMap.<Profile, String> builder().put(
            Profile.SMALLEST, "MIRO1B").build();

   @Override
   public CreateNodeResponse startNodeInLocation(String location, String name, Profile profile,
            Image image) {
      NewServerResponse serverResponse = rhClient.createServer(name, checkNotNull(imageNameMap
               .get(image), "image not supported: " + image), checkNotNull(profileNameMap
               .get(profile), "profile not supported: " + profile));
      return new CreateNodeResponseImpl(serverResponse.getServer().getId().toString(),
               serverResponse.getServer().getName(), "default", null, ImmutableMap
                        .<String, String> of(),
               NodeState.RUNNING,// TODO need a real state!
               getPublicAddresses(serverResponse.getServer()), ImmutableList.<InetAddress> of(),
               22, LoginType.SSH, new Credentials("root", serverResponse.getNewInstanceRequest()
                        .getCreateOptions().getPassword()), ImmutableMap.<String, String> of());
   }

   @VisibleForTesting
   static Iterable<InetAddress> getPublicAddresses(Server rhServer) {
      Iterable<String> addresses = Iterables.concat(ImmutableList.of(rhServer.getIpAddresses()
               .getPrimaryIp()), rhServer.getIpAddresses().getSecondaryIps());
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
      Set<Server> rhNodes = rhClient.getServerList();
      for (Server rhNode : rhNodes) {
         serverSet.add(new ComputeMetadataImpl(ComputeType.NODE, rhNode.getId() + "", rhNode
                  .getName(), null, null, ImmutableMap.<String, String> of()));
      }
      return serverSet;
   }

   @Override
   public NodeMetadata getNodeMetadata(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");
      throw new UnsupportedOperationException("not yet implemented");
   }

   @Override
   public void destroyNode(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");
      rhClient.destroyServer(new Long(node.getId()));
   }

   @Override
   public Map<String, Size> getSizes() {
      return null;
   }
}