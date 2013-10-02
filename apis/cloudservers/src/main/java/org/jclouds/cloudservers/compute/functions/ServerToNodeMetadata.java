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
package org.jclouds.cloudservers.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.addMetadataAndParseTagsFromCommaDelimitedValue;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cloudservers.domain.Server;
import org.jclouds.cloudservers.domain.ServerStatus;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final Supplier<Location> location;
   protected final Map<ServerStatus, Status> serverToNodeStatus;
   protected final Supplier<Set<? extends Image>> images;
   protected final Supplier<Set<? extends Hardware>> hardwares;
   protected final GroupNamingConvention nodeNamingConvention;

   private static class FindImageForServer implements Predicate<Image> {
      private final Server instance;

      private FindImageForServer(Server instance) {
         this.instance = instance;
      }

      @Override
      public boolean apply(Image input) {
         return input.getProviderId().equals(instance.getImageId() + "");
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
   ServerToNodeMetadata(Map<ServerStatus, Status> serverStateToNodeStatus,
            @Memoized Supplier<Set<? extends Image>> images, Supplier<Location> location,
            @Memoized Supplier<Set<? extends Hardware>> hardwares,
            GroupNamingConvention.Factory namingConvention) {
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.serverToNodeStatus = checkNotNull(serverStateToNodeStatus, "serverStateToNodeStatus");
      this.images = checkNotNull(images, "images");
      this.location = checkNotNull(location, "location");
      this.hardwares = checkNotNull(hardwares, "hardwares");
   }

   @Override
   public NodeMetadata apply(Server from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getName());
      builder.hostname(from.getName());
      builder.location(new LocationBuilder().scope(LocationScope.HOST).id(from.getHostId()).description(
               from.getHostId()).parent(location.get()).build());
      addMetadataAndParseTagsFromCommaDelimitedValue(builder, from.getMetadata());
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.getName()));
      builder.imageId(from.getImageId() + "");
      builder.operatingSystem(parseOperatingSystem(from));
      builder.hardware(parseHardware(from));
      builder.status(serverToNodeStatus.get(from.getStatus()));
      builder.publicAddresses(from.getAddresses().getPublicAddresses());
      builder.privateAddresses(from.getAddresses().getPrivateAddresses());
      return builder.build();
   }

   protected Hardware parseHardware(Server from) {
      try {
         return Iterables.find(hardwares.get(), new FindHardwareForServer(from));
      } catch (NoSuchElementException e) {
         logger.debug("could not find a matching hardware for server %s", from);
      }
      return null;
   }

   protected OperatingSystem parseOperatingSystem(Server from) {
      try {
         return Iterables.find(images.get(), new FindImageForServer(from)).getOperatingSystem();
      } catch (NoSuchElementException e) {
         logger.debug("could not find a matching image for server %s in location %s", from, location.get());
      }
      return null;
   }
}
