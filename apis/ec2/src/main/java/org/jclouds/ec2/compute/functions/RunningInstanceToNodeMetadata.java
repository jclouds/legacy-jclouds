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

package org.jclouds.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.util.NullSafeCollections;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Adrian Cole
 */
@Singleton
public class RunningInstanceToNodeMetadata implements Function<RunningInstance, NodeMetadata> {

   @Resource
   protected Logger logger = Logger.NULL;

   protected final Supplier<Set<? extends Location>> locations;
   protected final Supplier<Set<? extends Hardware>> hardware;
   protected final Map<RegionAndName, Image> instanceToImage;
   protected final Map<String, Credentials> credentialStore;
   protected final Map<InstanceState, NodeState> instanceToNodeState;

   @Inject
   RunningInstanceToNodeMetadata(Map<InstanceState, NodeState> instanceToNodeState,
            Map<String, Credentials> credentialStore, Map<RegionAndName, Image> instanceToImage,
            @Memoized Supplier<Set<? extends Location>> locations, @Memoized Supplier<Set<? extends Hardware>> hardware) {
      this.locations = checkNotNull(locations, "locations");
      this.hardware = checkNotNull(hardware, "hardware");
      this.instanceToImage = checkNotNull(instanceToImage, "instanceToImage");
      this.instanceToNodeState = checkNotNull(instanceToNodeState, "instanceToNodeState");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
   }

   @Override
   public NodeMetadata apply(RunningInstance instance) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      String providerId = checkNotNull(instance, "instance").getId();
      builder.providerId(providerId);
      builder.id(instance.getRegion() + "/" + providerId);
      String group = getGroupForInstance(instance);
      builder.group(group);
      builder.credentials(credentialStore.get("node#" + instance.getRegion() + "/" + providerId));
      builder.state(instanceToNodeState.get(instance.getInstanceState()));
      builder.publicAddresses(NullSafeCollections.nullSafeSet(instance.getIpAddress()));
      builder.privateAddresses(NullSafeCollections.nullSafeSet(instance.getPrivateIpAddress()));
      builder.hardware(parseHardware(instance));
      Location location = getLocationForAvailabilityZoneOrRegion(instance);
      builder.location(location);
      builder.imageId(instance.getRegion() + "/" + instance.getImageId());

      // extract the operating system from the image
      RegionAndName regionAndName = new RegionAndName(instance.getRegion(), instance.getImageId());
      try {
         Image image = instanceToImage.get(regionAndName);
          if (image != null)
              builder.operatingSystem(image.getOperatingSystem());
      }
      catch (NullPointerException e) {
          // The instanceToImage Map may throw NullPointerException (actually subclass NullOutputException) if the
          // computing Function returns a null value.
          //
          // See the following for more information:
          // MapMaker.makeComputingMap()
          // RegionAndIdToImage.apply()
      }

      return builder.build();
   }

   protected Hardware parseHardware(final RunningInstance instance) {
      Hardware hardware = getHardwareForInstance(instance);

      if (hardware != null) {
         hardware = HardwareBuilder.fromHardware(hardware).volumes(addEBS(instance, hardware.getVolumes())).build();
      }
      return hardware;
   }

   @VisibleForTesting
   static List<Volume> addEBS(final RunningInstance instance, Iterable<? extends Volume> volumes) {
      Iterable<Volume> ebsVolumes = Iterables.transform(instance.getEbsBlockDevices().entrySet(),
               new Function<Entry<String, BlockDevice>, Volume>() {

                  @Override
                  public Volume apply(Entry<String, BlockDevice> from) {
                     return new VolumeImpl(from.getValue().getVolumeId(), Volume.Type.SAN, null, from.getKey(),
                              instance.getRootDeviceName() != null
                                       && instance.getRootDeviceName().equals(from.getKey()), true);
                  }
               });

      if (instance.getRootDeviceType() == RootDeviceType.EBS) {
         volumes = Iterables.filter(volumes, new Predicate<Volume>() {

            @Override
            public boolean apply(Volume input) {
               return !input.isBootDevice();
            }

         });

      }
      return Lists.newArrayList(Iterables.concat(volumes, ebsVolumes));

   }

   @VisibleForTesting
   String getGroupForInstance(final RunningInstance instance) {
      String group = null;
      try {
         group = Iterables.getOnlyElement(Iterables.filter(instance.getGroupIds(), new Predicate<String>() {

            @Override
            public boolean apply(String input) {
               return input.startsWith("jclouds#") && input.endsWith("#" + instance.getRegion());
            }

         })).substring(8).replaceAll("#" + instance.getRegion() + "$", "");
      } catch (NoSuchElementException e) {
         logger.debug("no group parsed from %s's security groups: %s", instance.getId(), instance.getGroupIds());
      } catch (IllegalArgumentException e) {
         logger
                  .debug("too many groups match %s; %s's security groups: %s", "jclouds#", instance.getId(), instance
                           .getGroupIds());
      }
      return group;
   }

   @VisibleForTesting
   Hardware getHardwareForInstance(final RunningInstance instance) {
      try {
         return Iterables.find(hardware.get(), new Predicate<Hardware>() {

            @Override
            public boolean apply(Hardware input) {
               return input.getId().equals(instance.getInstanceType());
            }

         });
      } catch (NoSuchElementException e) {
         logger.debug("couldn't match instance type %s in: %s", instance.getInstanceType(), hardware.get());
         return null;
      }
   }

   private Location getLocationForAvailabilityZoneOrRegion(final RunningInstance instance) {
      Location location = findLocationWithId(instance.getAvailabilityZone());
      if (location == null)
         location = findLocationWithId(instance.getRegion());
      return location;
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

}
