/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.gogrid.compute.functions;
import static com.google.common.base.Preconditions.checkNotNull;

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
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.ServerState;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Oleksiy Yarmula
 */
@Singleton
public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {

   @Resource
   protected Logger logger = Logger.NULL;
   private final Map<ServerState, Status> serverStateToNodeStatus;
   private final Supplier<Set<? extends Image>> images;
   private final Supplier<Set<? extends Hardware>> hardwares;
   private final Supplier<Set<? extends Location>> locations;
   private final GroupNamingConvention nodeNamingConvention;

   static class FindImageForServer implements Predicate<Image> {
      private final Server instance;

      @Inject
      private FindImageForServer(Server instance) {
         this.instance = instance;
      }

      @Override
      public boolean apply(Image input) {
         return input.getProviderId().equals(instance.getImage().getId() + "")
                  && (input.getLocation() == null || input.getLocation().getId().equals(
                           instance.getDatacenter().getId() + ""));
      }
   }

   static class FindHardwareForServer implements Predicate<Hardware> {
      private final Server instance;

      @Inject
      private FindHardwareForServer(Server instance) {
         this.instance = instance;
      }

      @Override
      public boolean apply(Hardware input) {
         return input.getRam() == Integer.parseInt(instance.getRam().getName().replaceAll("[^0-9]", ""));
      }
   }

   @Inject
   ServerToNodeMetadata(Map<ServerState, Status> serverStateToNodeStatus,
         @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> hardwares,
         @Memoized Supplier<Set<? extends Location>> locations,
         GroupNamingConvention.Factory namingConvention) {
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.serverStateToNodeStatus = checkNotNull(serverStateToNodeStatus, "serverStateToNodeStatus");
      this.images = checkNotNull(images, "images");
      this.hardwares = checkNotNull(hardwares, "hardwares");
      this.locations = checkNotNull(locations, "locations");
   }

   @Override
   public NodeMetadata apply(Server from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getName());
      Location location = Iterables.find(locations.get(), LocationPredicates.idEquals(from.getDatacenter().getId() + ""));
      builder.location(location);
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.getName()));
      builder.hardware(parseHardware(from));
      builder.imageId(from.getImage().getId() + "");

      Image image = parseImage(from);
      if (image != null)
         builder.operatingSystem(image.getOperatingSystem());

      builder.status(serverStateToNodeStatus.get(from.getState()));
      builder.publicAddresses(ImmutableSet.of(from.getIp().getIp()));
      return builder.build();
   }

   protected Image parseImage(Server from) {
      Image image = null;
      try {
         image = Iterables.find(images.get(), new FindImageForServer(from));
      } catch (NoSuchElementException e) {
         logger.debug("could not find a matching image for server %s", from);
      }
      return image;
   }

   protected Hardware parseHardware(Server from) {
      Hardware hardware = null;
      try {
         hardware = Iterables.find(hardwares.get(), new FindHardwareForServer(from));
      } catch (NoSuchElementException e) {
         logger.debug("could not find a matching hardware for server %s", from);
      }
      return hardware;
   }
}
