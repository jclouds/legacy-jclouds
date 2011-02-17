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

package org.jclouds.slicehost.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.slicehost.domain.Slice;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class SliceToNodeMetadata implements Function<Slice, NodeMetadata> {
   protected final Supplier<Location> location;
   protected final Map<Slice.Status, NodeState> sliceToNodeState;
   protected final Supplier<Set<? extends Image>> images;
   protected final Supplier<Set<? extends Hardware>> hardwares;
   protected final Map<String, Credentials> credentialStore;

   @Resource
   protected Logger logger = Logger.NULL;

   private static class FindImageForSlice implements Predicate<Image> {
      private final Slice slice;

      private FindImageForSlice(Slice slice) {
         this.slice = slice;
      }

      @Override
      public boolean apply(Image input) {
         return input.getProviderId().equals(slice.getImageId() + "");
      }
   }

   private static class FindHardwareForSlice implements Predicate<Hardware> {
      private final Slice slice;

      private FindHardwareForSlice(Slice slice) {
         this.slice = slice;
      }

      @Override
      public boolean apply(Hardware input) {
         return input.getProviderId().equals(slice.getFlavorId() + "");
      }
   }

   @Inject
   SliceToNodeMetadata(Map<Slice.Status, NodeState> sliceStateToNodeState, Map<String, Credentials> credentialStore,
            @Memoized Supplier<Set<? extends Image>> images, Supplier<Location> location,
            @Memoized Supplier<Set<? extends Hardware>> hardwares) {
      this.sliceToNodeState = checkNotNull(sliceStateToNodeState, "sliceStateToNodeState");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.images = checkNotNull(images, "images");
      this.location = checkNotNull(location, "location");
      this.hardwares = checkNotNull(hardwares, "hardwares");
   }

   @Override
   public NodeMetadata apply(Slice from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getName());
      builder.location(location.get());
      builder.group(parseGroupFromName(from.getName()));
      builder.imageId(from.getImageId() + "");
      builder.operatingSystem(parseOperatingSystem(from));
      builder.hardware(parseHardware(from));
      builder.state(sliceToNodeState.get(from.getStatus()));
      builder.publicAddresses(Iterables.filter(from.getAddresses(), new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            return !input.startsWith("10.");
         }

      }));
      builder.privateAddresses(Iterables.filter(from.getAddresses(), new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            return input.startsWith("10.");
         }

      }));
      builder.credentials(credentialStore.get("node#" + from.getId()));
      return builder.build();
   }

   protected Hardware parseHardware(Slice from) {
      try {
         return Iterables.find(hardwares.get(), new FindHardwareForSlice(from));
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching hardware for slice %s", from);
      }
      return null;
   }

   protected OperatingSystem parseOperatingSystem(Slice from) {
      try {
         return Iterables.find(images.get(), new FindImageForSlice(from)).getOperatingSystem();
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching image for slice %s in location %s", from, location);
      }
      return null;
   }
}
