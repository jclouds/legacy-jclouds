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
package org.jclouds.openstack.nova.v1_1.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v1_1.compute.domain.ServerInZone;
import org.jclouds.openstack.nova.v1_1.compute.domain.ZoneAndId;
import org.jclouds.openstack.nova.v1_1.domain.Address;
import org.jclouds.openstack.nova.v1_1.domain.Server;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;

/**
 * A function for transforming a nova-specific Server into a generic NodeMetadata object.
 * 
 * @author Matt Stephenson, Adam Lowe, Adrian Cole
 */
public class ServerInZoneToNodeMetadata implements Function<ServerInZone, NodeMetadata> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final Supplier<Map<String, Location>> locationIndex;
   protected final LoadingCache<ZoneAndId, Iterable<String>> floatingIpCache;
   protected final Supplier<Set<? extends Image>> images;
   protected final Supplier<Set<? extends Hardware>> hardwares;

   @Inject
   public ServerInZoneToNodeMetadata(Supplier<Map<String, Location>> locationIndex,
            @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> hardwares,
            @Named("FLOATINGIP") LoadingCache<ZoneAndId, Iterable<String>> floatingIpCache) {
      this.locationIndex = checkNotNull(locationIndex, "locationIndex");
      this.floatingIpCache = checkNotNull(floatingIpCache, "cache");
      this.images = checkNotNull(images, "images");
      this.hardwares = checkNotNull(hardwares, "hardwares");
   }

   @Override
   public NodeMetadata apply(ServerInZone serverInZone) {
      Location zone = locationIndex.get().get(serverInZone.getZone());
      checkState(zone != null, "location %s not in locationIndex: %s", serverInZone.getZone(), locationIndex.get());
      Server from = serverInZone.getServer();

      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.id(serverInZone.slashEncode());
      builder.providerId(from.getId());
      builder.name(from.getName());
      builder.hostname(from.getName());
      builder.location(from.getHostId() != null ? new LocationBuilder().scope(LocationScope.HOST).id(from.getHostId())
               .description(from.getHostId()).parent(zone).build() : zone);
      builder.userMetadata(from.getMetadata());
      builder.group(parseGroupFromName(from.getName()));
      builder.imageId(ZoneAndId.fromZoneAndId(serverInZone.getZone(), from.getImage().getId()).slashEncode());
      builder.operatingSystem(findOperatingSystemForServerOrNull(serverInZone));
      builder.hardware(findHardwareForServerOrNull(serverInZone));
      builder.state(from.getStatus().getNodeState());
      builder.publicAddresses(getPublicAddresses(serverInZone));
      builder.privateAddresses(Iterables.transform(from.getPrivateAddresses(),
               AddressToStringTransformationFunction.INSTANCE));

      return builder.build();
   }

   protected Iterable<String> getPublicAddresses(ServerInZone serverInZone) {
      return Iterables.concat(Iterables.transform(serverInZone.getServer().getPublicAddresses(),
               AddressToStringTransformationFunction.INSTANCE), floatingIpCache.getUnchecked(serverInZone));
   }

   private enum AddressToStringTransformationFunction implements Function<Address, String> {
      INSTANCE;
      @Override
      public String apply(Address address) {
         return address.getAddr();
      }
   }

   protected Hardware findHardwareForServerOrNull(ServerInZone serverInZone) {
      return findObjectOfTypeForServerOrNull(hardwares.get(), "hardware", serverInZone.getServer().getFlavor().getId(),
               serverInZone);
   }

   protected OperatingSystem findOperatingSystemForServerOrNull(ServerInZone serverInZone) {
      Image image = findObjectOfTypeForServerOrNull(images.get(), "image", serverInZone.getServer().getImage().getId(),
               serverInZone);
      return (image != null) ? image.getOperatingSystem() : null;
   }

   public <T extends ComputeMetadata> T findObjectOfTypeForServerOrNull(Set<? extends T> supply, String type,
            final String objectId, final ZoneAndId serverInZone) {
      try {
         return Iterables.find(supply, new Predicate<T>() {
            @Override
            public boolean apply(T input) {
               return input.getId().equals(ZoneAndId.fromZoneAndId(serverInZone.getZone(), objectId).slashEncode());
            }
         });
      } catch (NoSuchElementException e) {
         logger.debug("could not find %s with id(%s) for server(%s)", type, objectId, serverInZone);
      }
      return null;
   }

}
