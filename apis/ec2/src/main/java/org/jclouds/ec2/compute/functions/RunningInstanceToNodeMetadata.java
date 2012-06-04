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
package org.jclouds.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.logging.Logger;
import org.jclouds.util.InetAddresses2.IsPrivateIPAddress;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class RunningInstanceToNodeMetadata implements Function<RunningInstance, NodeMetadata> {

   @Resource
   protected Logger logger = Logger.NULL;

   protected final Supplier<Set<? extends Location>> locations;
   protected final Supplier<Set<? extends Hardware>> hardware;
   protected final Supplier<LoadingCache<RegionAndName, ? extends Image>> imageMap;
   protected final Map<String, Credentials> credentialStore;
   protected final Map<InstanceState, Status> instanceToNodeStatus;
   protected final GroupNamingConvention.Factory namingConvention;

   @Inject
   protected RunningInstanceToNodeMetadata(Map<InstanceState, Status> instanceToNodeStatus,
            Map<String, Credentials> credentialStore, Supplier<LoadingCache<RegionAndName, ? extends Image>> imageMap,
            @Memoized Supplier<Set<? extends Location>> locations, @Memoized Supplier<Set<? extends Hardware>> hardware,
            GroupNamingConvention.Factory namingConvention) {
      this.locations = checkNotNull(locations, "locations");
      this.hardware = checkNotNull(hardware, "hardware");
      this.imageMap = checkNotNull(imageMap, "imageMap");
      this.instanceToNodeStatus = checkNotNull(instanceToNodeStatus, "instanceToNodeStatus");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
   }

   @Override
   public NodeMetadata apply(RunningInstance instance) {
      if (instance == null || instance.getId() == null)
         return null;
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder = buildInstance(instance, builder);
      return builder.build();
   }

   protected NodeMetadataBuilder buildInstance(final RunningInstance instance, NodeMetadataBuilder builder) {
      builder.providerId(instance.getId());
      builder.id(instance.getRegion() + "/" + instance.getId());
      String group = getGroupForInstance(instance);
      builder.group(group);
      // standard convention from aws-ec2, which might not be re-used outside.
      if (instance.getPrivateDnsName() != null)
         builder.hostname(instance.getPrivateDnsName().replaceAll("\\..*", ""));
      addCredentialsForInstance(builder, instance);
      builder.status(instanceToNodeStatus.get(instance.getInstanceState()));
      builder.backendStatus(instance.getRawState());

      // collect all ip addresses into one bundle in case the api mistakenly put a private address
      // into the public address field
      Builder<String> addressesBuilder = ImmutableSet.builder();
      if (Strings.emptyToNull(instance.getIpAddress()) != null)
         addressesBuilder.add(instance.getIpAddress());
      if (Strings.emptyToNull(instance.getPrivateIpAddress()) != null)
         addressesBuilder.add(instance.getPrivateIpAddress());

      Set<String> addresses = addressesBuilder.build();

      builder.publicAddresses(filter(addresses, not(IsPrivateIPAddress.INSTANCE)));
      builder.privateAddresses(filter(addresses, IsPrivateIPAddress.INSTANCE));
      builder.hardware(parseHardware(instance));
      Location location = getLocationForAvailabilityZoneOrRegion(instance);
      builder.location(location);
      builder.imageId(instance.getRegion() + "/" + instance.getImageId());

      // extract the operating system from the image
      RegionAndName regionAndName = new RegionAndName(instance.getRegion(), instance.getImageId());
      try {
         Image image = imageMap.get().getUnchecked(regionAndName);
         if (image != null)
            builder.operatingSystem(image.getOperatingSystem());
      } catch (CacheLoader.InvalidCacheLoadException e) {
         logger.debug("image not found for %s: %s", regionAndName, e);
      } catch (UncheckedExecutionException e) {
         logger.debug("error getting image for %s: %s", regionAndName, e);
      }
      return builder;
   }

   protected void addCredentialsForInstance(NodeMetadataBuilder builder, RunningInstance instance) {
      builder.credentials(LoginCredentials.fromCredentials(credentialStore.get("node#" + instance.getRegion() + "/"
            + instance.getId())));
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
      String group = parseGroupFrom(instance, instance.getGroupIds());
      if(group == null && instance.getKeyName() != null) {
         // when not using a generated security group, e.g. in VPC, try from key:
         group = parseGroupFrom(instance, Sets.newHashSet(instance.getKeyName()));
      }
      return group;
   }

   private String parseGroupFrom(final RunningInstance instance, final Set<String> data) {
      String group = null;
      try {
         Predicate<String> containsAnyGroup = namingConvention.create().containsAnyGroup();
         String encodedGroup = Iterables.getOnlyElement(Iterables.filter(data, containsAnyGroup));
         group = namingConvention.create().extractGroup(encodedGroup);
      } catch (NoSuchElementException e) {
         logger.debug("no group parsed from %s's data: %s", instance.getId(), data);
      } catch (IllegalArgumentException e) {
         logger.debug("too many groups match naming convention; %s's data: %s", instance.getId(), data);
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
      if (locationId == null)
         return null;
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
