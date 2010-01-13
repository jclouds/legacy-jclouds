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
package org.jclouds.vcloud.hostingdotcom.compute;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.NodeIdentity;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Profile;
import org.jclouds.compute.domain.internal.CreateNodeResponseImpl;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudClient;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.internal.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class HostingDotComVCloudComputeService implements ComputeService {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final HostingDotComVCloudComputeClient computeClient;
   private final HostingDotComVCloudClient hostingClient;

   private static final Map<VAppStatus, NodeState> vAppStatusToNodeState = ImmutableMap
            .<VAppStatus, NodeState> builder().put(VAppStatus.OFF, NodeState.TERMINATED).put(
                     VAppStatus.ON, NodeState.RUNNING).put(VAppStatus.RESOLVED, NodeState.PENDING)
            .put(VAppStatus.SUSPENDED, NodeState.SUSPENDED).put(VAppStatus.UNRESOLVED,
                     NodeState.PENDING).build();

   @Inject
   public HostingDotComVCloudComputeService(HostingDotComVCloudClient tmClient,
            HostingDotComVCloudComputeClient computeClient) {
      this.hostingClient = tmClient;
      this.computeClient = computeClient;

   }

   @Override
   public CreateNodeResponse createNode(String name, Profile profile, Image image) {
      Map<String, String> metaMap = computeClient.start(name, image, 1, 512, (10l * 1025 * 1024),
               ImmutableMap.<String, String> of());
      VApp vApp = hostingClient.getVApp(metaMap.get("id"));
      return new CreateNodeResponseImpl(vApp.getId(), vApp.getName(), vAppStatusToNodeState
               .get(vApp.getStatus()), vApp.getNetworkToAddresses().values(), ImmutableSet
               .<InetAddress> of(), 22, LoginType.SSH, new Credentials(metaMap.get("username"),
               metaMap.get("password")), ImmutableMap.<String, String> of());
   }

   @Override
   public NodeMetadata getNodeMetadata(String id) {
      VApp vApp = hostingClient.getVApp(id);
      return new NodeMetadataImpl(vApp.getId(), vApp.getName(), vAppStatusToNodeState.get(vApp
               .getStatus()), vApp.getNetworkToAddresses().values(), ImmutableSet
               .<InetAddress> of(), 22, LoginType.SSH, ImmutableMap.<String, String> of());
   }

   @Override
   public Set<NodeIdentity> getNodeByName(final String name) {
      return Sets.newHashSet(Iterables.filter(listNodes(), new Predicate<NodeIdentity>() {
         @Override
         public boolean apply(NodeIdentity input) {
            return input.getName().equalsIgnoreCase(name);
         }
      }));
   }

   @Override
   public Set<NodeIdentity> listNodes() {
      Set<NodeIdentity> servers = Sets.newTreeSet();
      for (NamedResource resource : hostingClient.getDefaultVDC().getResourceEntities().values()) {
         if (resource.getType().equals(VCloudMediaType.VAPP_XML)) {
            servers.add(getNodeMetadata(resource.getId()));
         }
      }
      return servers;
   }

   @Override
   public void destroyNode(String id) {
      computeClient.stop(id);
   }
}