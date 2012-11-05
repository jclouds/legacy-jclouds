/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.fujitsu.fgcp.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.fujitsu.fgcp.FGCPApi;
import org.jclouds.fujitsu.fgcp.FGCPAsyncApi;
import org.jclouds.fujitsu.fgcp.compute.functions.ResourceIdToFirewallId;
import org.jclouds.fujitsu.fgcp.compute.functions.ResourceIdToSystemId;
import org.jclouds.fujitsu.fgcp.compute.predicates.ServerStopped;
import org.jclouds.fujitsu.fgcp.compute.predicates.SystemStatusNormal;
import org.jclouds.fujitsu.fgcp.compute.strategy.VServerMetadata.Builder;
import org.jclouds.fujitsu.fgcp.domain.DiskImage;
import org.jclouds.fujitsu.fgcp.domain.ServerType;
import org.jclouds.fujitsu.fgcp.domain.VServerStatus;
import org.jclouds.fujitsu.fgcp.domain.VServerWithDetails;
import org.jclouds.fujitsu.fgcp.domain.VServerWithVNICs;
import org.jclouds.fujitsu.fgcp.domain.VSystem;
import org.jclouds.fujitsu.fgcp.domain.VSystemWithDetails;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Defines the connection between the {@link org.jclouds.fujitsu.fgcp.FGCPApi}
 * implementation and the jclouds {@link org.jclouds.compute.ComputeService}.
 * Bound in FGCPComputeServiceAdapter.
 * 
 * @author Dies Koper
 */
@Singleton
public class FGCPComputeServiceAdapter implements
      ComputeServiceAdapter<VServerMetadata, ServerType, DiskImage, Location> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final FGCPApi api;
   private final FGCPAsyncApi asyncApi;
   protected Predicate<String> serverStopped = null;
   protected Predicate<String> serverCreated = null;
   protected Predicate<String> systemNormal = null;
   protected ResourceIdToFirewallId toFirewallId = null;
   protected ResourceIdToSystemId toSystemId = null;

   @Inject
   public FGCPComputeServiceAdapter(FGCPApi api, FGCPAsyncApi asyncApi,
         ServerStopped serverStopped, SystemStatusNormal systemNormal,
         Timeouts timeouts, ResourceIdToFirewallId toFirewallId,
         ResourceIdToSystemId toSystemId) {
      this.api = checkNotNull(api, "api");
      this.asyncApi = checkNotNull(asyncApi, "asyncApi");
      this.serverStopped = new RetryablePredicate<String>(
            checkNotNull(serverStopped), timeouts.nodeSuspended);
      this.serverCreated = new RetryablePredicate<String>(
            checkNotNull(serverStopped), timeouts.nodeRunning);
      this.systemNormal = new RetryablePredicate<String>(
            checkNotNull(systemNormal), timeouts.nodeTerminated);
      this.toFirewallId = checkNotNull(toFirewallId, "ResourceIdToFirewallId");
      this.toSystemId = checkNotNull(toSystemId, "ResourceIdToSystemId");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NodeAndInitialCredentials<VServerMetadata> createNodeWithGroupEncodedIntoName(
         String group, String name, Template template) {
      // Find vsys (how? create new? default to first found?)
      // Target network DMZ/SECURE1/SECURE2 (how? default to DMZ?)
      // Determine remaining params: [vserverType,diskImageId,networkId]
      // what if no vsys exists yet? Location.AU(.contractId) creates 3? tier
      // skeleton vsys and DMZ is picked?
      String id = api.getVirtualSystemApi().createServer(name,
            template.getHardware().getName(), template.getImage().getId(),
            template.getLocation().getId());

      // wait until fully created (i.e. transitions to stopped status)
      serverCreated.apply(id);
      resumeNode(id);
      VServerMetadata server = getNode(id);

      //do we need this?
      server.setTemplate(template);
      String user = template.getImage().getOperatingSystem().getFamily() == OsFamily.WINDOWS ? "Administrator"
            : "root";

      return new NodeAndInitialCredentials<VServerMetadata>(server,
            id, LoginCredentials.builder().identity(user)
                  .password(server.getInitialPassword()).build());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<ServerType> listHardwareProfiles() {
      return api.getVirtualDCApi().listServerTypes();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<DiskImage> listImages() {
      return api.getVirtualDCApi().listDiskImages();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DiskImage getImage(String id) {
      return api.getDiskImageApi().get(id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<Location> listLocations() {
      // Not using the adapter to determine locations
      // see SystemAndNetworkSegmentToLocationSupplier
      return ImmutableSet.<Location> of();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public VServerMetadata getNode(String id) {
      Builder builder = VServerMetadata.builder();
      builder.id(id);

      List<ListenableFuture<?>> futures = Lists.newArrayList();

      futures.add(asyncApi.getVirtualServerApi().getDetails(id));
      futures.add(asyncApi.getVirtualServerApi().getStatus(id));
      futures.add(asyncApi.getVirtualServerApi().getInitialPassword(id));
      // mapped public ips?
      String fwId = toFirewallId.apply(id);
//      futures.add(asyncApi.getBuiltinServerApi().getConfiguration(fwId,
//            BuiltinServerConfiguration.SLB_RULE));
      try {
         List<Object> results = Futures.successfulAsList(futures).get();
         VServerWithDetails server = (VServerWithDetails) results.get(0);
         VServerStatus status = (VServerStatus) results.get(1);
         System.out.println("getNode(" + id + ")'s getDetails: " + status +" - " + server);
         if (server == null) {
            server = api.getVirtualServerApi().getDetails(id);
            System.out.println("getNode(" + id + ")'s getDetails(2) returns: " + server);
         }
         builder.serverWithDetails(server);
         builder.status(status == null ? VServerStatus.UNRECOGNIZED : status);
//         System.out.println("status in adapter#getNode: " 
//         + (VServerStatus) results.get(1) 
//         +" for " 
//         + server.getId());
         builder.initialPassword((String) results.get(2));
//         SLB slb = ((BuiltinServer) results.get(4)).;
//         slb.
      } catch (InterruptedException e) {
         throw Throwables.propagate(e);
      } catch (ExecutionException e) {
         throw Throwables.propagate(e);
      }
      return builder.build();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<VServerMetadata> listNodes() {
      ImmutableSet.Builder<VServerMetadata> servers = ImmutableSet
            .<VServerMetadata> builder();

      Set<VSystem> systems = api.getVirtualDCApi().listVirtualSystems();
      List<ListenableFuture<VSystemWithDetails>> futures = Lists.newArrayList();
      for (VSystem system : systems) {

         futures.add(asyncApi.getVirtualSystemApi().getDetails(
               system.getId()));
      }
      try {
         for (VSystemWithDetails system : Futures.successfulAsList(futures)
               .get()) {

            if (system != null) {

               for (VServerWithVNICs server : system.getServers()) {

                  // skip FW (S-0001) and SLBs (>0 for SLB)
                  if (!server.getId().endsWith("-S-0001") && server.getVnics().iterator().next().getNicNo() == 0) {

                     servers.add(getNode(server.getId()));
//                    Builder builder = VServerMetadata.builder();
//                    builder.server(server);
//                    builder.status(VServerStatus.UNRECOGNIZED);
//                    servers.add(builder.build());
                  }
               }
            }
         }
      } catch (InterruptedException e) {
         throw Throwables.propagate(e);
      } catch (ExecutionException e) {
         throw Throwables.propagate(e);
      }

      return servers.build();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void destroyNode(String id) {
      api.getVirtualServerApi().destroy(id);
      // wait until fully destroyed
      String systemId = toSystemId.apply(id);
      systemNormal.apply(systemId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void rebootNode(String id) {
      suspendNode(id);
      // wait until fully stopped
      serverStopped.apply(id);
      resumeNode(id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void resumeNode(String id) {
      api.getVirtualServerApi().start(id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void suspendNode(String id) {
      api.getVirtualServerApi().stop(id);
   }
}
