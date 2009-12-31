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
import java.util.SortedSet;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.CreateServerResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.Profile;
import org.jclouds.compute.domain.ServerIdentity;
import org.jclouds.compute.domain.ServerMetadata;
import org.jclouds.compute.domain.ServerState;
import org.jclouds.compute.domain.internal.CreateServerResponseImpl;
import org.jclouds.compute.domain.internal.ServerMetadataImpl;
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
   protected Logger logger = Logger.NULL;
   private final HostingDotComVCloudComputeClient computeClient;
   private final HostingDotComVCloudClient hostingClient;

   private static final Map<VAppStatus, ServerState> vAppStatusToServerState = ImmutableMap
            .<VAppStatus, ServerState> builder().put(VAppStatus.OFF, ServerState.TERMINATED).put(
                     VAppStatus.ON, ServerState.RUNNING).put(VAppStatus.RESOLVED,
                     ServerState.PENDING).put(VAppStatus.SUSPENDED, ServerState.SUSPENDED).put(
                     VAppStatus.UNRESOLVED, ServerState.PENDING).build();

   @Inject
   public HostingDotComVCloudComputeService(HostingDotComVCloudClient tmClient,
            HostingDotComVCloudComputeClient computeClient) {
      this.hostingClient = tmClient;
      this.computeClient = computeClient;

   }

   @Override
   public CreateServerResponse createServer(String name, Profile profile, Image image) {
      Map<String, String> metaMap = computeClient.start(name, image, 1, 512, (10l * 1025 * 1024),
               ImmutableMap.<String, String> of());
      VApp vApp = hostingClient.getVApp(metaMap.get("id"));
      return new CreateServerResponseImpl(vApp.getId(), vApp.getName(), vAppStatusToServerState
               .get(vApp.getStatus()), vApp.getNetworkToAddresses().values(), ImmutableSet
               .<InetAddress> of(), 22, LoginType.SSH, new Credentials(metaMap.get("username"),
               metaMap.get("password")));
   }

   @Override
   public ServerMetadata getServerMetadata(String id) {
      VApp vApp = hostingClient.getVApp(id);
      return new ServerMetadataImpl(vApp.getId(), vApp.getName(), vAppStatusToServerState.get(vApp
               .getStatus()), vApp.getNetworkToAddresses().values(), ImmutableSet
               .<InetAddress> of(), 22, LoginType.SSH);
   }

   @Override
   public SortedSet<ServerIdentity> getServerByName(final String name) {
      return Sets.newTreeSet(Iterables.filter(listServers(), new Predicate<ServerIdentity>() {
         @Override
         public boolean apply(ServerIdentity input) {
            return input.getName().equalsIgnoreCase(name);
         }
      }));
   }

   @Override
   public SortedSet<ServerIdentity> listServers() {
      SortedSet<ServerIdentity> servers = Sets.newTreeSet();
      for (NamedResource resource : hostingClient.getDefaultVDC().getResourceEntities().values()) {
         if (resource.getType().equals(VCloudMediaType.VAPP_XML)) {
            servers.add(getServerMetadata(resource.getId()));
         }
      }
      return servers;
   }

   @Override
   public void destroyServer(String id) {
      computeClient.stop(id);
   }
}