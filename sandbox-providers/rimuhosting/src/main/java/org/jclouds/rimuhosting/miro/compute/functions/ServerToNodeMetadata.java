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

package org.jclouds.rimuhosting.miro.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {

   @Resource
   protected Logger logger = Logger.NULL;
   protected final Map<String, Credentials> credentialStore;
   protected final Supplier<Set<? extends Location>> locations;
   protected final Function<Server, Iterable<String>> getPublicAddresses;
   protected final Map<RunningState, NodeState> runningStateToNodeState;
   protected final Supplier<Set<? extends Image>> images;

   private static class FindImageForServer implements Predicate<Image> {
      private final Location location;
      private final Server instance;

      private FindImageForServer(Location location, Server instance) {
         this.location = location;
         this.instance = instance;
      }

      @Override
      public boolean apply(Image input) {
         return input.getProviderId().equals(instance.getImageId())
                  && (input.getLocation() == null || input.getLocation().equals(location) || input.getLocation()
                           .equals(location.getParent()));
      }
   }

   @Inject
   ServerToNodeMetadata(Function<Server, Iterable<String>> getPublicAddresses,
            @Memoized Supplier<Set<? extends Location>> locations, Map<String, Credentials> credentialStore,
            Map<RunningState, NodeState> runningStateToNodeState, @Memoized Supplier<Set<? extends Image>> images) {
      this.getPublicAddresses = checkNotNull(getPublicAddresses, "serverStateToNodeState");
      this.locations = checkNotNull(locations, "locations");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.runningStateToNodeState = checkNotNull(runningStateToNodeState, "serverStateToNodeState");
      this.images = checkNotNull(images, "images");
   }

   @Override
   public NodeMetadata apply(Server from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getName());
      Location location = findLocationWithId(from.getLocation().getId());
      builder.location(location);
      builder.group(parseGroupFromName(from.getName()));
      builder.imageId(from.getImageId() + "");
      builder.operatingSystem(parseOperatingSystem(from, location));
      builder.hardware(null);// TODO
      builder.state(runningStateToNodeState.get(from.getState()));
      builder.publicAddresses(getPublicAddresses.apply(from));
      builder.credentials(credentialStore.get("node#" + from.getId()));
      return builder.build();
   }

   private Location findLocationWithId(final String locationId) {
      try {
         Location location = Iterables.find(locations.get(), new Predicate<Location>() {

            @Override
            public boolean apply(Location input) {
               return input.getId().equals(locationId);
            }

         });
         return location;

      } catch (NoSuchElementException e) {
         logger.debug("couldn't match instance location %s in: %s", locationId, locations.get());
         return null;
      }
   }

   protected OperatingSystem parseOperatingSystem(Server from, Location location) {
      try {
         return Iterables.find(images.get(), new FindImageForServer(location, from)).getOperatingSystem();
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching image for server %s in location %s", from, location);
      }
      return null;
   }
}