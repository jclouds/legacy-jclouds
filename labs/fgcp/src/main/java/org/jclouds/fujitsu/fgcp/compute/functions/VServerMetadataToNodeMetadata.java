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
package org.jclouds.fujitsu.fgcp.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
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
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.fujitsu.fgcp.compute.strategy.VServerMetadata;
import org.jclouds.fujitsu.fgcp.domain.VServer;
import org.jclouds.fujitsu.fgcp.domain.VServerStatus;
import org.jclouds.fujitsu.fgcp.domain.VServerWithVNICs;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Dies Koper
 */
@Singleton
public class VServerMetadataToNodeMetadata implements
      Function<VServerMetadata, NodeMetadata> {

   public static final Map<VServerStatus, Status> vServerToStatus = ImmutableMap
         .<VServerStatus, Status> builder()
         .put(VServerStatus.DEPLOYING, Status.PENDING)
         .put(VServerStatus.RUNNING, Status.RUNNING)
         .put(VServerStatus.STOPPING, Status.PENDING)
         .put(VServerStatus.STOPPED, Status.SUSPENDED)
         .put(VServerStatus.STARTING, Status.PENDING)
         .put(VServerStatus.FAILOVER, Status.RUNNING)
         .put(VServerStatus.UNEXPECTED_STOP, Status.SUSPENDED)
         .put(VServerStatus.RESTORING, Status.PENDING)
         .put(VServerStatus.BACKUP_ING, Status.PENDING)
         .put(VServerStatus.ERROR, Status.ERROR)
         .put(VServerStatus.START_ERROR, Status.ERROR)
         .put(VServerStatus.STOP_ERROR, Status.ERROR)
         .put(VServerStatus.CHANGE_TYPE, Status.PENDING)
         .put(VServerStatus.REGISTERING, Status.PENDING)
         .put(VServerStatus.UNRECOGNIZED, Status.UNRECOGNIZED).build();

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final Supplier<Set<? extends Location>> locations;
   protected final Supplier<Set<? extends Image>> images;
   protected final Supplier<Set<? extends Hardware>> hardwares;
   protected final GroupNamingConvention nodeNamingConvention;

   private static class FindImageForVServer implements Predicate<Image> {
      private final VServer server;

      private FindImageForVServer(VServer server) {
         this.server = server;
      }

      @Override
      public boolean apply(Image input) {
         return input.getId().equals(server.getDiskimageId());
      }
   }

   protected Image parseImage(VServer from) {
      try {
         return Iterables.find(images.get(), new FindImageForVServer(from));
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching image for server %s", from);
      }
      return null;
   }

   private static class FindHardwareForServerType implements
         Predicate<Hardware> {
      private final String type;

      private FindHardwareForServerType(String type) {
         this.type = type;
      }

      @Override
      public boolean apply(Hardware input) {
         return input.getName().equals(type);
      }
   }

   protected Hardware parseHardware(String from) {
      try {
         return Iterables.find(hardwares.get(),
               new FindHardwareForServerType(from));
      } catch (NoSuchElementException e) {
         logger.warn(
               "could not find a matching hardware for server type %s",
               from);
      }
      return null;
   }

   private static class FindLocationForVServer implements Predicate<Location> {
      private final VServerWithVNICs server;

      private FindLocationForVServer(VServerWithVNICs server) {
         this.server = server;
      }

      @Override
      public boolean apply(Location input) {
         return input.getId().equals(
               Iterables.getLast(server.getVnics()).getNetworkId());
      }
   }

   protected Location parseLocation(VServerWithVNICs from) {
      try {
         return Iterables.find(locations.get(), new FindLocationForVServer(
               from));
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching realm for server %s", from);
      }
      return null;
   }

   @Inject
   VServerMetadataToNodeMetadata(
         @Memoized Supplier<Set<? extends Location>> locations,
         @Memoized Supplier<Set<? extends Image>> images,
         @Memoized Supplier<Set<? extends Hardware>> hardwares,
         GroupNamingConvention.Factory namingConvention) {
      this.images = checkNotNull(images, "images");
      this.locations = checkNotNull(locations, "locations");
      this.hardwares = checkNotNull(hardwares, "hardwares");
      this.nodeNamingConvention = checkNotNull(namingConvention,
            "namingConvention").createWithoutPrefix();
   }

   @Override
   public NodeMetadata apply(VServerMetadata from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();

      builder.ids(from.getId());
      builder.name(from.getName());
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from
            .getName()));
      if (from.getStatus() == null)
         System.out.println("status null for: " + from.getId() + ": "
               + from.getName());

      builder.status(vServerToStatus.get(from.getStatus()));
      builder.privateAddresses(ImmutableSet.<String> of());
      builder.publicAddresses(ImmutableSet.<String> of());

      //
      // if (from.getIps() != null) {
      //
      // builder.publicAddresses(Collections2.transform(from.getIps(),
      // new Function<PublicIP, String>() {
      //
      // @Override
      // public String apply(PublicIP input) {
      // return input.getAddress();
      // }
      //
      // }));
      // }

      if (from.getServer() != null) {

         builder.imageId(from.getServer().getDiskimageId());
         builder.hardware(parseHardware(from.getServer().getType()));

         LoginCredentials.Builder credentialsBuilder = LoginCredentials
               .builder().password(from.getInitialPassword());

         Image image = parseImage(from.getServer());
         // image will not be found if server was created a while back and
         // the image has since been destroyed or discontinued (like an old
         // CentOS version)
         if (image != null) {

            builder.operatingSystem(image.getOperatingSystem());
            String user = image.getDefaultCredentials().getUser();
            credentialsBuilder.identity(user);
         }

         builder.credentials(credentialsBuilder.build());

         if (from.getServer() instanceof VServerWithVNICs) {

            VServerWithVNICs server = (VServerWithVNICs) from.getServer();
            builder.location(parseLocation(server));
            List<String> ips = Lists.newArrayList();
            if (server.getVnics() != null && server.getVnics().iterator().next().getPrivateIp() != null) {
               ips.add(server.getVnics().iterator().next().getPrivateIp());
            }
            builder.privateAddresses(ips);
         }
      }
      if (from.getTemplate() != null) {
         // when creating a new node
         builder.location(from.getTemplate().getLocation());
      }

      return builder.build();
   }
}
