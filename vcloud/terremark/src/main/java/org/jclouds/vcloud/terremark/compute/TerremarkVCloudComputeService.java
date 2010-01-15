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
package org.jclouds.vcloud.terremark.compute;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.InetAddress;
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
import org.jclouds.compute.domain.internal.CreateNodeResponseImpl;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.inject.internal.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudComputeService implements ComputeService {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final TerremarkVCloudComputeClient computeClient;
   private final TerremarkVCloudClient client;

   private static final Map<VAppStatus, NodeState> vAppStatusToNodeState = ImmutableMap
            .<VAppStatus, NodeState> builder().put(VAppStatus.OFF, NodeState.TERMINATED).put(
                     VAppStatus.ON, NodeState.RUNNING).put(VAppStatus.RESOLVED, NodeState.PENDING)
            .put(VAppStatus.SUSPENDED, NodeState.SUSPENDED).put(VAppStatus.UNRESOLVED,
                     NodeState.PENDING).build();

   @Inject
   public TerremarkVCloudComputeService(TerremarkVCloudClient tmClient,
            TerremarkVCloudComputeClient computeClient) {
      this.client = tmClient;
      this.computeClient = computeClient;
   }

   @Override
   public CreateNodeResponse startNodeInLocation(String location, String name, Profile profile,
            Image image) {
      if (checkNotNull(location, "location").equalsIgnoreCase("default"))
         location = client.getDefaultVDC().getId();
      String id = computeClient.start(name, image, 1, 512, ImmutableMap.<String, String> of());
      VApp vApp = client.getVApp(id);
      InetAddress publicIp = computeClient
               .createPublicAddressMappedToPorts(vApp, 22, 80, 8080, 443);
      return new CreateNodeResponseImpl(vApp.getId(), vApp.getName(), location, vApp.getLocation(),
               ImmutableMap.<String, String> of(), vAppStatusToNodeState.get(vApp.getStatus()),
               ImmutableSet.<InetAddress> of(publicIp), vApp.getNetworkToAddresses().values(), 22,
               LoginType.SSH, new Credentials("vcloud", "p4ssw0rd"), ImmutableMap
                        .<String, String> of());
   }

   @Override
   public NodeMetadata getNodeMetadata(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      return getNodeMetadataByIdInVDC(checkNotNull(node.getLocation(), "location"), checkNotNull(node.getId(),
               "node.id"));
   }

   private NodeMetadata getNodeMetadataByIdInVDC(String vDCId, String id) {
      VApp vApp = client.getVApp(id);
      Set<InetAddress> publicAddresses = computeClient.getPublicAddresses(vApp.getId());
      return new NodeMetadataImpl(vApp.getId(), vApp.getName(), vDCId, vApp.getLocation(),
               ImmutableMap.<String, String> of(), vAppStatusToNodeState.get(vApp.getStatus()),
               publicAddresses, vApp.getNetworkToAddresses().values(), 22, LoginType.SSH,
               ImmutableMap.<String, String> of());
   }

   @Override
   public Set<ComputeMetadata> listNodes() {
      Set<ComputeMetadata> nodes = Sets.newHashSet();
      for (NamedResource vdc : client.getDefaultOrganization().getVDCs().values()) {
         for (NamedResource resource : client.getVDC(vdc.getId()).getResourceEntities().values()) {
            if (resource.getType().equals(VCloudMediaType.VAPP_XML)) {
               nodes.add(getNodeMetadataByIdInVDC(vdc.getId(), resource.getId()));
            }
         }
      }
      return nodes;
   }

   @Override
   public void destroyNode(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      computeClient.stop(checkNotNull(node.getId(), "node.id"));
   }

   @Override
   public Map<String, Size> getSizes() {
      return null;// TODO
   }
}