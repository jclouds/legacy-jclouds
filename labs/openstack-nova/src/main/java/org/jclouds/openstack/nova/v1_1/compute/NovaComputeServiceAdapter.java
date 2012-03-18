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
package org.jclouds.openstack.nova.v1_1.compute;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v1_1.domain.Flavor;
import org.jclouds.openstack.nova.v1_1.domain.Image;
import org.jclouds.openstack.nova.v1_1.domain.RebootType;
import org.jclouds.openstack.nova.v1_1.domain.Server;
import org.jclouds.openstack.nova.v1_1.features.FlavorClient;
import org.jclouds.openstack.nova.v1_1.features.ImageClient;
import org.jclouds.openstack.nova.v1_1.features.ServerClient;
import org.jclouds.openstack.nova.v1_1.reference.NovaConstants;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * The adapter used by the NovaComputeServiceContextModule to interface the
 * nova-specific domain model to the computeService generic domain model.
 *
 * @author Matt Stephenson
 */
public class NovaComputeServiceAdapter implements ComputeServiceAdapter<Server, Flavor, Image, Location> {

   private final NovaClient novaClient;
   private final ServerClient defaultLocationServerClient;
   private final FlavorClient defaultFlavorClient;
   private final ImageClient defaultImageClient;
   private final Set<String> regions;
   private final String defaultRegion;

   @Inject
   @Named(NovaConstants.PROPERTY_NOVA_AUTO_ALLOCATE_FLOATING_IPS)
   @VisibleForTesting
   boolean autoAllocateFloatingIps = false;

   @Inject
   public NovaComputeServiceAdapter(NovaClient novaClient) {
      this.novaClient = novaClient;
      regions = novaClient.getConfiguredRegions();
      if (regions.isEmpty()) {
         throw new IllegalStateException(
               "No regions exist for this compute service.  The Nova compute service requires at least 1 region.");
      }
      this.defaultRegion = regions.iterator().next();
      this.defaultLocationServerClient = novaClient.getServerClientForRegion(defaultRegion);
      this.defaultFlavorClient = novaClient.getFlavorClientForRegion(defaultRegion);
      this.defaultImageClient = novaClient.getImageClientForRegion(defaultRegion);
   }

   @Override
   public NodeAndInitialCredentials<Server> createNodeWithGroupEncodedIntoName(String tag, String name,
         Template template) {
      String region = template.getLocation() == null ? defaultRegion : template.getLocation().getId();
      ServerClient serverClient = template.getLocation() != null ? novaClient.getServerClientForRegion(template.getLocation().getId()) : defaultLocationServerClient;
      // TODO: make NovaTemplateOptions with the following:
      // security group, key pair, floating ip (attach post server-create?)
      NovaTemplateOptions templateOptions = NovaTemplateOptions.class.cast(template.getOptions());
      
      boolean autoAllocateFloatingIps = 
            (this.autoAllocateFloatingIps || templateOptions.isAutoAssignFloatingIp());
      
      String floatingIp = null;
      if (autoAllocateFloatingIps) {
         checkArgument(novaClient.getFloatingIPExtensionForRegion(region).isPresent(), "Floating IP settings are required by configuration, but the extension is not available!");
         floatingIp = novaClient.getFloatingIPExtensionForRegion(region).get().allocate().getId();
      }

      Server server = serverClient.createServer(name, template.getImage().getId(), template.getHardware().getId());

      // Attaching floating ip(s) to server
      if (floatingIp != null) 
         novaClient.getFloatingIPExtensionForRegion(region).get().addFloatingIP(server.getId(), floatingIp);
    
      return new NodeAndInitialCredentials<Server>(server, server.getId() + "", LoginCredentials.builder()
            .password(server.getAdminPass()).build());
   }

   @Override
   public Iterable<Flavor> listHardwareProfiles() {
      return defaultFlavorClient.listFlavorsInDetail();
   }

   @Override
   public Iterable<Image> listImages() {
      return defaultImageClient.listImagesInDetail();
   }

   @Override
   public Iterable<Location> listLocations() {
      return Iterables.transform(novaClient.getConfiguredRegions(), new Function<String, Location>() {

         @Override
         public Location apply(@Nullable String region) {
            return new LocationBuilder().id(region).description(region).build();
         }
      });
   }

   @Override
   public Server getNode(String id) {
      return defaultLocationServerClient.getServer(id);
   }

   @Override
   public void destroyNode(String id) {
      defaultLocationServerClient.deleteServer(id);
   }

   @Override
   public void rebootNode(String id) {
      defaultLocationServerClient.rebootServer(id, RebootType.SOFT);
   }

   @Override
   public void resumeNode(String id) {
      throw new UnsupportedOperationException("suspend not supported");
   }

   @Override
   public void suspendNode(String id) {
      throw new UnsupportedOperationException("suspend not supported");
   }

   @Override
   public Iterable<Server> listNodes() {
      ImmutableSet.Builder<Server> servers = new ImmutableSet.Builder<Server>();

      for (String region : regions) {
         servers.addAll(novaClient.getServerClientForRegion(region).listServersInDetail());
      }

      return servers.build();
   }
}
