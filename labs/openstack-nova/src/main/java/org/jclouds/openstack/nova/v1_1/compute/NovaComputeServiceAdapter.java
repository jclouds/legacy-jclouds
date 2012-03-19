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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.Zone;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.compute.domain.FlavorInZone;
import org.jclouds.openstack.nova.v1_1.compute.domain.ImageInZone;
import org.jclouds.openstack.nova.v1_1.compute.domain.ServerInZone;
import org.jclouds.openstack.nova.v1_1.compute.domain.ZoneAndId;
import org.jclouds.openstack.nova.v1_1.compute.functions.RemoveFloatingIpFromNodeAndDeallocate;
import org.jclouds.openstack.nova.v1_1.domain.Flavor;
import org.jclouds.openstack.nova.v1_1.domain.Image;
import org.jclouds.openstack.nova.v1_1.domain.RebootType;
import org.jclouds.openstack.nova.v1_1.domain.Server;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * The adapter used by the NovaComputeServiceContextModule to interface the nova-specific domain
 * model to the computeService generic domain model.
 * 
 * @author Matt Stephenson, Adrian Cole
 */
public class NovaComputeServiceAdapter implements
         ComputeServiceAdapter<ServerInZone, FlavorInZone, ImageInZone, Location> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final NovaClient novaClient;
   protected final Supplier<Set<String>> zoneIds;
   protected final RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate;

   @Inject
   public NovaComputeServiceAdapter(NovaClient novaClient, @Zone Supplier<Set<String>> zoneIds,
            RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate) {
      this.novaClient = checkNotNull(novaClient, "novaClient");
      this.zoneIds = checkNotNull(zoneIds, "zoneIds");
      this.removeFloatingIpFromNodeAndDeallocate = checkNotNull(removeFloatingIpFromNodeAndDeallocate,
               "removeFloatingIpFromNodeAndDeallocate");
   }

   @Override
   public NodeAndInitialCredentials<ServerInZone> createNodeWithGroupEncodedIntoName(String tag, String name,
            Template template) {
      String zoneId = template.getLocation().getId();
      Server server = novaClient.getServerClientForZone(zoneId).createServer(name, template.getImage().getProviderId(),
               template.getHardware().getProviderId());
      ServerInZone serverInZone = new ServerInZone(server, zoneId);
      return new NodeAndInitialCredentials<ServerInZone>(serverInZone, serverInZone.slashEncode(),
               LoginCredentials.builder().password(server.getAdminPass()).build());
   }

   @Override
   public Iterable<FlavorInZone> listHardwareProfiles() {
      Builder<FlavorInZone> builder = ImmutableSet.<FlavorInZone> builder();
      for (final String zoneId : zoneIds.get()) {
         builder.addAll(Iterables.transform(novaClient.getFlavorClientForZone(zoneId).listFlavorsInDetail(),
                  new Function<Flavor, FlavorInZone>() {

                     @Override
                     public FlavorInZone apply(Flavor arg0) {
                        return new FlavorInZone(arg0, zoneId);
                     }

                  }));
      }
      return builder.build();
   }

   @Override
   public Iterable<ImageInZone> listImages() {
      Builder<ImageInZone> builder = ImmutableSet.<ImageInZone> builder();
      for (final String zoneId : zoneIds.get()) {
         builder.addAll(Iterables.transform(novaClient.getImageClientForZone(zoneId).listImagesInDetail(),
                  new Function<Image, ImageInZone>() {

                     @Override
                     public ImageInZone apply(Image arg0) {
                        return new ImageInZone(arg0, zoneId);
                     }

                  }));
      }
      return builder.build();
   }

   @Override
   public Iterable<ServerInZone> listNodes() {
      Builder<ServerInZone> builder = ImmutableSet.<ServerInZone> builder();
      for (final String zoneId : zoneIds.get()) {
         builder.addAll(Iterables.transform(novaClient.getServerClientForZone(zoneId).listServersInDetail(),
                  new Function<Server, ServerInZone>() {

                     @Override
                     public ServerInZone apply(Server arg0) {
                        return new ServerInZone(arg0, zoneId);
                     }

                  }));
      }
      return builder.build();
   }

   @Override
   public Iterable<Location> listLocations() {
      // locations provided by keystone
      return ImmutableSet.of();
   }

   @Override
   public ServerInZone getNode(String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      Server server = novaClient.getServerClientForZone(zoneAndId.getZone()).getServer(zoneAndId.getId());
      return server == null ? null : new ServerInZone(server, zoneAndId.getZone());
   }

   @Override
   public void destroyNode(String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      if (novaClient.getFloatingIPExtensionForZone(zoneAndId.getZone()).isPresent()) {
         try {
            removeFloatingIpFromNodeAndDeallocate.apply(zoneAndId);
         } catch (RuntimeException e) {
            logger.warn(e, "<< error removing and deallocating ip from node(%s): %s", id, e.getMessage());
         }
      }
      novaClient.getServerClientForZone(zoneAndId.getZone()).deleteServer(zoneAndId.getId());
   }

   @Override
   public void rebootNode(String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      novaClient.getServerClientForZone(zoneAndId.getZone()).rebootServer(zoneAndId.getId(), RebootType.HARD);
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
