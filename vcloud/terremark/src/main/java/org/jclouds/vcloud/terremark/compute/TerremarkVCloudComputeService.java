/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.compute;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;
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
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.domain.InternetService;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.internal.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudComputeService implements ComputeService {
   @Resource
   protected Logger logger = Logger.NULL;
   private final TerremarkVCloudComputeClient computeClient;
   private final TerremarkVCloudClient tmClient;

   private static final Map<VAppStatus, ServerState> vAppStatusToServerState = ImmutableMap
            .<VAppStatus, ServerState> builder().put(VAppStatus.OFF, ServerState.TERMINATED).put(
                     VAppStatus.ON, ServerState.RUNNING).put(VAppStatus.RESOLVED,
                     ServerState.PENDING).put(VAppStatus.SUSPENDED, ServerState.SUSPENDED).put(
                     VAppStatus.UNRESOLVED, ServerState.PENDING).build();

   @Inject
   public TerremarkVCloudComputeService(TerremarkVCloudClient tmClient,
            TerremarkVCloudComputeClient computeClient) {
      this.tmClient = tmClient;
      this.computeClient = computeClient;

   }

   @Override
   public CreateServerResponse createServer(String name, Profile profile, Image image) {
      String id = computeClient.start(name, 1, 512, image);
      VApp vApp = tmClient.getVApp(id);
      // bug creating more than one internet service returns 503 or 500
      // InetAddress publicIp = computeClient.createPublicAddressMappedToPorts(vApp, 22, 80, 8080);
      InetAddress publicIp = computeClient.createPublicAddressMappedToPorts(vApp, 22);
      return new CreateServerResponseImpl(vApp.getId(), vApp.getName(), vAppStatusToServerState
               .get(vApp.getStatus()), ImmutableSet.<InetAddress> of(publicIp), vApp
               .getNetworkToAddresses().values(), 22, LoginType.SSH, new Credentials("vcloud",
               "p4ssw0rd"));
   }

   @Override
   public ServerMetadata getServerMetadata(String id) {
      VApp vApp = tmClient.getVApp(id);
      // TODO
      Set<InetAddress> publicAddresses = ImmutableSet.<InetAddress> of();
      return new ServerMetadataImpl(vApp.getId(), vApp.getName(), vAppStatusToServerState.get(vApp
               .getStatus()), publicAddresses, vApp.getNetworkToAddresses().values(), 22,
               LoginType.SSH);
   }

   public SortedSet<InternetService> getInternetServicesByName(final String name) {
      return Sets.newTreeSet(Iterables.filter(tmClient.getAllInternetServices(),
               new Predicate<InternetService>() {
                  @Override
                  public boolean apply(InternetService input) {
                     return input.getName().equalsIgnoreCase(name);
                  }
               }));
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
      for (NamedResource resource : tmClient.getDefaultVDC().getResourceEntities().values()) {
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