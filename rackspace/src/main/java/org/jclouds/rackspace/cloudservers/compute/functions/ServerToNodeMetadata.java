/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.rackspace.cloudservers.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseTagFromName;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {
   private final Supplier<Location> location;
   private final Map<ServerStatus, NodeState> serverToNodeState;
   private final Supplier<Set<? extends Image>> images;
   private final Supplier<Set<? extends Hardware>> hardwares;

   @Resource
   protected Logger logger = Logger.NULL;

   private static class FindImageForServer implements Predicate<Image> {
      private final Location location;
      private final Server instance;

      private FindImageForServer(Location location, Server instance) {
         this.location = location;
         this.instance = instance;
      }

      @Override
      public boolean apply(Image input) {
         return input.getProviderId().equals(instance.getImageId() + "")
                  && (input.getLocation() == null || input.getLocation().equals(location.getParent()));
      }
   }

   private static class FindHardwareForServer implements Predicate<Hardware> {
      private final Server instance;

      private FindHardwareForServer(Server instance) {
         this.instance = instance;
      }

      @Override
      public boolean apply(Hardware input) {
         return input.getProviderId().equals(instance.getFlavorId() + "");
      }
   }

   @Inject
   ServerToNodeMetadata(Map<ServerStatus, NodeState> serverStateToNodeState, Supplier<Set<? extends Image>> images,
            Supplier<Location> location, Supplier<Set<? extends Hardware>> hardwares) {
      this.serverToNodeState = checkNotNull(serverStateToNodeState, "serverStateToNodeState");
      this.images = checkNotNull(images, "images");
      this.location = checkNotNull(location, "location");
      this.hardwares = checkNotNull(hardwares, "hardwares");
   }

   @Override
   public NodeMetadata apply(Server from) {
      String tag = parseTagFromName(from.getName());
      Location host = new LocationImpl(LocationScope.HOST, from.getHostId(), from.getHostId(), location.get());
      Image image = null;
      try {
         image = Iterables.find(images.get(), new FindImageForServer(host, from));
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching image for server %s in location %s", from, location);
      }
      Hardware hardware = null;
      try {
         hardware = Iterables.find(hardwares.get(), new FindHardwareForServer(from));
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching hardware for server %s", from);
      }
      return new NodeMetadataImpl(from.getId() + "", from.getName(), from.getId() + "", host, null, from.getMetadata(),
               tag, hardware, from.getImageId() + "", image != null ? image.getOperatingSystem() : null,
               serverToNodeState.get(from.getStatus()), from.getAddresses().getPublicAddresses(), from.getAddresses()
                        .getPrivateAddresses(), null);
   }

}