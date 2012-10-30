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
package org.jclouds.rimuhosting.miro.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.Image;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.PricingPlan;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.util.Iterables2;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * defines the connection between the {@link RimuHostingClient} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class RimuHostingComputeServiceAdapter implements ComputeServiceAdapter<Server, Hardware, Image, Location> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   private Logger logger = Logger.NULL;

   private final RimuHostingClient client;
   private final Supplier<Set<? extends Location>> locations;

   @Inject
   protected RimuHostingComputeServiceAdapter(RimuHostingClient client,
            @Memoized Supplier<Set<? extends Location>> locations) {
      this.client = checkNotNull(client, "client");
      this.locations = checkNotNull(locations, "locations");
   }

   @Override
   public NodeAndInitialCredentials<Server> createNodeWithGroupEncodedIntoName(String group, String name,
            Template template) {
      NewServerResponse server = client.createServer(name,
               checkNotNull(template.getImage().getProviderId(), "imageId"), checkNotNull(template.getHardware()
                        .getProviderId(), "hardwareId"));

      return new NodeAndInitialCredentials<Server>(server.getServer(), server.getServer().getId() + "",
               LoginCredentials.builder().password(server.getNewInstanceRequest().getCreateOptions().getPassword())
                        .build());
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      final Set<Hardware> sizes = Sets.newHashSet();
      for (final PricingPlan from : client.getPricingPlanList()) {
         try {

            final Location location = Iterables.find(locations.get(), new Predicate<Location>() {

               @Override
               public boolean apply(Location input) {
                  return input.getId().equals(from.getDataCenter().getId());
               }

            });
            sizes.add(new HardwareBuilder().ids(from.getId()).location(location).processors(
                     ImmutableList.of(new Processor(1, 1.0))).ram(from.getRam()).volumes(
                     ImmutableList.<Volume> of(new VolumeImpl((float) from.getDiskSize(), true, true))).build());
         } catch (NullPointerException e) {
            logger.warn("datacenter not present in " + from.getId());
         }
      }
      return sizes;
   }

   @Override
   public Iterable<Image> listImages() {
      return Iterables2.concreteCopy(client.getImageList());
   }

   @Override
   public Iterable<Server> listNodes() {
      return Iterables2.concreteCopy(client.getServerList());
   }

   @Override
   public Iterable<Location> listLocations() {
      // Not using the adapter to determine locations
      return ImmutableSet.<Location> of();
   }

   @Override
   public Server getNode(String id) {
      long serverId = Long.parseLong(id);
      return client.getServer(serverId);
   }

   // cheat until we have a getImage command
   @Override
   public Image getImage(final String id) {
      return Iterables.find(listImages(), new Predicate<Image>() {

         @Override
         public boolean apply(Image input) {
            return input.getId().equals(id);
         }

      }, null);
   }

   @Override
   public void destroyNode(String id) {
      Long serverId = Long.parseLong(id);
      client.destroyServer(serverId);
   }

   @Override
   public void rebootNode(String id) {
      Long serverId = Long.parseLong(id);
      // if false server wasn't around in the first place
      client.restartServer(serverId).getState();
   }

   @Override
   public void resumeNode(String id) {
      throw new UnsupportedOperationException("suspend not supported");
   }

   @Override
   public void suspendNode(String id) {
      throw new UnsupportedOperationException("suspend not supported");
   }

}