/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.openstack.nova.compute.functions;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.*;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.domain.Address;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

/**
 * @author Adrian Cole
 */
@Singleton
public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final Supplier<Location> location;
   protected final Map<String, Credentials> credentialStore;
   protected final Map<ServerStatus, NodeState> serverToNodeState;
   protected final Supplier<Set<? extends Image>> images;
   protected final Supplier<Set<? extends Hardware>> hardwares;

   private static class FindImageForServer implements Predicate<Image> {
      private final Server instance;

      private FindImageForServer(Server instance) {
         this.instance = instance;
      }

      @Override
      public boolean apply(Image input) {
         return input.getUri().toString().equals(instance.getImageRef() + "");
      }
   }

   private static class FindHardwareForServer implements Predicate<Hardware> {
      private final Server instance;

      private FindHardwareForServer(Server instance) {
         this.instance = instance;
      }

      @Override
      public boolean apply(Hardware input) {
         return input.getUri().toString().equals(instance.getFlavorRef() + "");
      }
   }

   @Inject
   ServerToNodeMetadata(Map<ServerStatus, NodeState> serverStateToNodeState, Map<String, Credentials> credentialStore,
                        @Memoized Supplier<Set<? extends Image>> images, Supplier<Location> location,
                        @Memoized Supplier<Set<? extends Hardware>> hardwares) {
      this.serverToNodeState = checkNotNull(serverStateToNodeState, "serverStateToNodeState");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.images = checkNotNull(images, "images");
      this.location = checkNotNull(location, "location");
      this.hardwares = checkNotNull(hardwares, "hardwares");
   }

   @Override
   public NodeMetadata apply(Server from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getName());
      builder.location(new LocationBuilder().scope(LocationScope.HOST).id(from.getHostId()).description(
            from.getHostId()).parent(location.get()).build());
      builder.userMetadata(from.getMetadata());
      builder.group(parseGroupFromName(from.getName()));
      Image image = parseImage(from);
      if (image != null) {
         builder.imageId(image.getId());
         builder.operatingSystem(image.getOperatingSystem());
      }
      builder.hardware(parseHardware(from));
      builder.state(serverToNodeState.get(from.getStatus()));
      builder.publicAddresses(Iterables.transform(from.getAddresses().getPublicAddresses(), Address.newAddress2StringFunction()));
      builder.privateAddresses(Iterables.transform(from.getAddresses().getPrivateAddresses(), Address.newAddress2StringFunction()));
      builder.credentials(credentialStore.get("node#" + from.getId()));
      builder.uri(from.getURI());
      return builder.build();
   }

   protected Hardware parseHardware(Server from) {
      try {
         return Iterables.find(hardwares.get(), new FindHardwareForServer(from));
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching hardware for server %s", from);
      }
      return null;
   }
   
   protected Image parseImage(Server from) {
      try {
         return Iterables.find(images.get(), new FindImageForServer(from));
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching image for server %s in location %s", from, location);
      }
      return null;
   }
}
