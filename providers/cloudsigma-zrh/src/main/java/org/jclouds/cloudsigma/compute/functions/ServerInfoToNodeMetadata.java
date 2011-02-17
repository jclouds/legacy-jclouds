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

package org.jclouds.cloudsigma.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudsigma.domain.Device;
import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.cloudsigma.domain.Server;
import org.jclouds.cloudsigma.domain.ServerInfo;
import org.jclouds.cloudsigma.domain.ServerStatus;
import org.jclouds.collect.FindResourceInSet;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ServerInfoToNodeMetadata implements Function<ServerInfo, NodeMetadata> {
   public static final Map<ServerStatus, NodeState> serverStatusToNodeState = ImmutableMap
         .<ServerStatus, NodeState> builder().put(ServerStatus.ACTIVE, NodeState.RUNNING)//
         .put(ServerStatus.STOPPED, NodeState.SUSPENDED)//
         .put(ServerStatus.PAUSED, NodeState.SUSPENDED)//
         .put(ServerStatus.DUMPED, NodeState.PENDING)//
         .put(ServerStatus.DEAD, NodeState.TERMINATED)//
         .put(ServerStatus.UNRECOGNIZED, NodeState.UNRECOGNIZED)//
         .build();

   private final Function<Server, String> getImageIdFromServer;
   private final Function<String, Image> findImageForId;
   private final Map<String, Credentials> credentialStore;
   private final Supplier<Location> locationSupplier;
   private final Function<Device, Volume> deviceToVolume;

   @Inject
   ServerInfoToNodeMetadata(Map<String, Credentials> credentialStore, Function<Server, String> getImageIdFromServer,
         Function<String, Image> findImageForId, Function<Device, Volume> deviceToVolume,
         Supplier<Location> locationSupplier) {
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.locationSupplier = checkNotNull(locationSupplier, "locationSupplier");
      this.deviceToVolume = checkNotNull(deviceToVolume, "deviceToVolume");
      this.findImageForId = checkNotNull(findImageForId, "findImageForId");
      this.getImageIdFromServer = checkNotNull(getImageIdFromServer, "getImageIdFromServer");
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public NodeMetadata apply(ServerInfo from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getUuid());
      builder.name(from.getName());
      builder.location(locationSupplier.get());
      builder.group(parseGroupFromName(from.getName()));

      String imageId = getImageIdFromServer.apply(from);
      if (imageId != null) {
         Image image = findImageForId.apply(imageId);
         if (image != null) {
            builder.operatingSystem(image.getOperatingSystem());
            builder.adminPassword(image.getAdminPassword());
         }
      }
      builder.hardware(new HardwareBuilder().ids(from.getUuid())
            .processors(ImmutableList.of(new Processor(1, from.getCpu()))).ram(from.getMem())
            .volumes((List) ImmutableList.of(Iterables.transform(from.getDevices().values(), deviceToVolume))).build());
      builder.state(serverStatusToNodeState.get(from.getStatus()));
      builder.publicAddresses(ImmutableSet.<String> of(from.getVnc().getIp()));
      builder.privateAddresses(ImmutableSet.<String> of());
      builder.credentials(credentialStore.get(from.getUuid()));
      return builder.build();
   }

   @Singleton
   public static final class DeviceToVolume implements Function<Device, Volume> {
      private final Map<String, DriveInfo> cache;

      @Inject
      public DeviceToVolume(Map<String, DriveInfo> cache) {
         this.cache = checkNotNull(cache, "cache");
      }

      @Override
      public Volume apply(Device input) {
         VolumeBuilder builder = new VolumeBuilder();
         builder.id(input.getId());
         DriveInfo drive = cache.get(input.getDriveUuid());
         if (drive != null) {
            builder.size(drive.getSize() / 1024 / 1024f);
         }
         return new VolumeBuilder().durable(true).type(Volume.Type.NAS).build();
      }
   }

   /**
    * When we create the boot drive of the server, by convention we set the name to the image it
    * came from.
    * 
    * @author Adrian Cole
    * 
    */
   @Singleton
   public static class GetImageIdFromServer implements Function<Server, String> {
      private final Map<String, DriveInfo> cache;

      @Inject
      public GetImageIdFromServer(Map<String, DriveInfo> cache) {
         this.cache = cache;
      }

      @Override
      public String apply(Server from) {
         String imageId = null;
         String bootDeviceId = Iterables.get(from.getBootDeviceIds(), 0);
         Device bootDevice = from.getDevices().get(bootDeviceId);
         if (bootDevice != null) {
            try {
               imageId = cache.get(bootDevice.getDriveUuid()).getName();
            } catch (NullPointerException e) {

            }
         }
         return imageId;
      }
   }

   @Singleton
   public static class FindImageForId extends FindResourceInSet<String, Image> {

      @Inject
      public FindImageForId(@Memoized Supplier<Set<? extends Image>> images) {
         super(images);
      }

      @Override
      public boolean matches(String from, Image input) {
         return input.getProviderId().equals(from);
      }
   }

}
