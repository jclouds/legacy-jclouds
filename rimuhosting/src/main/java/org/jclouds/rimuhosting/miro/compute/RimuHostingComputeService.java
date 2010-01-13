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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeIdentity;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Profile;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.Server;

import com.google.common.collect.ImmutableMap;

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
   public CreateNodeResponse createNode(String name, Profile profile, Image image) {
      NewServerResponse serverResponse = rhClient.createServer(name, checkNotNull(imageNameMap
               .get(image), "image not supported: " + image), checkNotNull(profileNameMap
               .get(profile), "profile not supported: " + profile));
      return new RimuHostingCreateNodeResponse(serverResponse);
   }

   public SortedSet<NodeIdentity> listNodes() {
      SortedSet<NodeIdentity> servers = new TreeSet<NodeIdentity>();
      SortedSet<Server> rhNodes = rhClient.getServerList();
      for (Server rhNode : rhNodes) {
         servers.add(new RimuHostingNodeIdentity(rhNode, rhClient));
      }
      return servers;
   }

   public NodeMetadata getNodeMetadata(String id) {
      throw new UnsupportedOperationException("not yet implemented");
   }

   @Override
   public SortedSet<NodeIdentity> getNodeByName(String id) {
      SortedSet<NodeIdentity> serverSet = new TreeSet<NodeIdentity>();
      for (Server rhNode : rhClient.getServerList()) {
         if (rhNode.getName().equals(id)) {
            serverSet.add(new RimuHostingNodeIdentity(rhNode, rhClient));
         }
      }
      return serverSet;
   }

   @Override
   public void destroyNode(String id) {
      rhClient.destroyServer(new Long(id));
   }
}