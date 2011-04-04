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

package org.jclouds.deltacloud.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class InstanceToNodeMetadata implements Function<Instance, NodeMetadata> {

   public static final Map<Instance.State, NodeState> instanceToNodeState = ImmutableMap
            .<Instance.State, NodeState> builder().put(Instance.State.STOPPED, NodeState.SUSPENDED).put(
                     Instance.State.RUNNING, NodeState.RUNNING).put(Instance.State.PENDING, NodeState.PENDING).put(
                     Instance.State.UNRECOGNIZED, NodeState.UNRECOGNIZED).put(Instance.State.SHUTTING_DOWN,
                     NodeState.PENDING).put(Instance.State.START, NodeState.PENDING).build();

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final Map<String, Credentials> credentialStore;
   protected final Supplier<Set<? extends Location>> locations;
   protected final Supplier<Set<? extends Image>> images;
   protected final Supplier<Set<? extends Hardware>> hardwares;

   private static class FindImageForInstance implements Predicate<Image> {
      private final Instance instance;

      private FindImageForInstance(Instance instance) {
         this.instance = instance;
      }

      @Override
      public boolean apply(Image input) {
         return input.getUri().equals(instance.getImage());
      }
   }

   private static class FindHardwareForInstance implements Predicate<Hardware> {
      private final Instance instance;

      private FindHardwareForInstance(Instance instance) {
         this.instance = instance;
      }

      @Override
      public boolean apply(Hardware input) {
         return input.getUri().equals(instance.getHardwareProfile());
      }
   }

   protected Hardware parseHardware(Instance from) {
      try {
         return Iterables.find(hardwares.get(), new FindHardwareForInstance(from));
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching hardware for instance %s", from);
      }
      return null;
   }

   protected OperatingSystem parseOperatingSystem(Instance from) {
      try {
         return Iterables.find(images.get(), new FindImageForInstance(from)).getOperatingSystem();
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching image for instance %s", from);
      }
      return null;
   }

   private static class FindLocationForInstance implements Predicate<Location> {
      private final Instance instance;

      private FindLocationForInstance(Instance instance) {
         this.instance = instance;
      }

      @Override
      public boolean apply(Location input) {
         return input.getId().equals(instance.getRealm().toASCIIString());
      }
   }

   protected Location parseLocation(Instance from) {
      try {
         return Iterables.find(locations.get(), new FindLocationForInstance(from));
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching realm for instance %s", from);
      }
      return null;
   }

   @Inject
   InstanceToNodeMetadata(Map<String, Credentials> credentialStore,
            @Memoized Supplier<Set<? extends Location>> locations, @Memoized Supplier<Set<? extends Image>> images,
            @Memoized Supplier<Set<? extends Hardware>> hardwares) {
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.images = checkNotNull(images, "images");
      this.locations = checkNotNull(locations, "locations");
      this.hardwares = checkNotNull(hardwares, "hardwares");
   }

   @Override
   public NodeMetadata apply(org.jclouds.deltacloud.domain.Instance from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getHref().toASCIIString());
      builder.name(from.getName());
      builder.location(parseLocation(from));
      builder.group(parseGroupFromName(from.getName()));
      builder.imageId(from.getImage().toASCIIString());
      builder.operatingSystem(parseOperatingSystem(from));
      builder.hardware(parseHardware(from));
      builder.state(instanceToNodeState.get(from.getState()));
      builder.publicAddresses(from.getPublicAddresses());
      builder.privateAddresses(from.getPrivateAddresses());
      builder.credentials(credentialStore.get(from.getHref().toASCIIString()));
      return builder.build();
   }
}
