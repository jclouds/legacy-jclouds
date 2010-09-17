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
import static org.jclouds.compute.util.ComputeServiceUtils.parseTagFromName;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.logging.Logger;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {

   @Resource
   protected Logger logger = Logger.NULL;
   private final Function<Server, Iterable<String>> getPublicAddresses;
   private final Map<RunningState, NodeState> runningStateToNodeState;
   private final Supplier<Set<? extends Image>> images;

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
            Map<RunningState, NodeState> runningStateToNodeState, Supplier<Set<? extends Image>> images) {
      this.getPublicAddresses = checkNotNull(getPublicAddresses, "serverStateToNodeState");
      this.runningStateToNodeState = checkNotNull(runningStateToNodeState, "serverStateToNodeState");
      this.images = checkNotNull(images, "images");
   }

   @Override
   public NodeMetadata apply(Server from) {
      // TODO properly look up location
      Location location = new LocationImpl(LocationScope.ZONE, from.getLocation().getId(),
               from.getLocation().getName(), null);
      String tag = parseTagFromName(from.getName());
      Credentials creds = null;

      Image image = null;
      try {
         image = Iterables.find(images.get(), new FindImageForServer(location, from));
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching image for server %s in location %s", from, location);
      }

      NodeState state = runningStateToNodeState.get(from.getState());
      return new NodeMetadataImpl(from.getId() + "", from.getName(), from.getId() + "", location, null, ImmutableMap
               .<String, String> of(), tag, null, from.getImageId(), image != null ? image.getOperatingSystem() : null,
               state, getPublicAddresses.apply(from), ImmutableList.<String> of(), creds);

   }
}