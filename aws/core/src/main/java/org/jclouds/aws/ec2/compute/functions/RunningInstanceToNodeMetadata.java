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

package org.jclouds.aws.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.Utils.nullSafeSet;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.RootDeviceType;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.domain.RunningInstance.EbsBlockDevice;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ComputationException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class RunningInstanceToNodeMetadata implements Function<RunningInstance, NodeMetadata> {

   @Resource
   protected Logger logger = Logger.NULL;

   @VisibleForTesting
   static final Map<InstanceState, NodeState> instanceToNodeState = ImmutableMap.<InstanceState, NodeState> builder()
            .put(InstanceState.PENDING, NodeState.PENDING).put(InstanceState.RUNNING, NodeState.RUNNING).put(
                     InstanceState.SHUTTING_DOWN, NodeState.PENDING)
            .put(InstanceState.TERMINATED, NodeState.TERMINATED).put(InstanceState.STOPPING, NodeState.PENDING).put(
                     InstanceState.STOPPED, NodeState.SUSPENDED)
            .put(InstanceState.UNRECOGNIZED, NodeState.UNRECOGNIZED).build();

   private final EC2Client client;
   private final Map<RegionAndName, KeyPair> credentialsMap;
   private final PopulateDefaultLoginCredentialsForImageStrategy credentialProvider;
   private final Supplier<Set<? extends Location>> locations;
   private final Supplier<Set<? extends Hardware>> hardware;
   private final ConcurrentMap<RegionAndName, Image> imageMap;

   @Inject
   RunningInstanceToNodeMetadata(EC2Client client, Map<RegionAndName, KeyPair> credentialsMap,
            PopulateDefaultLoginCredentialsForImageStrategy credentialProvider,
            ConcurrentMap<RegionAndName, Image> imageMap, Supplier<Set<? extends Location>> locations,
            Supplier<Set<? extends Hardware>> hardware) {
      this.client = checkNotNull(client, "client");
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      this.credentialProvider = checkNotNull(credentialProvider, "credentialProvider");
      this.locations = checkNotNull(locations, "locations");
      this.hardware = checkNotNull(hardware, "hardware");
      this.imageMap = checkNotNull(imageMap, "imageMap");
   }

   @Override
   public NodeMetadata apply(final RunningInstance instance) {
      String id = checkNotNull(instance, "instance").getId();

      String name = null; // user doesn't determine a node name;
      URI uri = null; // no uri to get rest access to host info

      String tag = getTagForInstance(instance);

      Credentials credentials = getCredentialsForInstanceWithTag(instance, tag);

      Map<String, String> userMetadata = ImmutableMap.<String, String> of();

      NodeState state = instanceToNodeState.get(instance.getInstanceState());

      Set<String> publicAddresses = nullSafeSet(instance.getIpAddress());
      Set<String> privateAddresses = nullSafeSet(instance.getPrivateIpAddress());

      Hardware hardware = getHardwareForInstance(instance);

      if (hardware != null) {
         hardware = ComputeServiceUtils.replacesVolumes(hardware, addEBS(instance, hardware.getVolumes()));
      }

      Location location = getLocationForAvailabilityZoneOrRegion(instance);

      Image image = resolveImageForInstanceInLocation(instance, location);

      return new NodeMetadataImpl(id, name, instance.getRegion() + "/" + instance.getId(), location, uri, userMetadata,
               tag, hardware, instance.getRegion() + "/" + instance.getImageId(), image != null ? image
                        .getOperatingSystem() : null, state, publicAddresses, privateAddresses, credentials);
   }

   @VisibleForTesting
   static Iterable<? extends Volume> addEBS(final RunningInstance instance, Iterable<? extends Volume> volumes) {
      Iterable<Volume> ebsVolumes = Iterables.transform(instance.getEbsBlockDevices().entrySet(),
               new Function<Entry<String, EbsBlockDevice>, Volume>() {

                  @Override
                  public Volume apply(Entry<String, EbsBlockDevice> from) {
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
      return Iterables.concat(volumes, ebsVolumes);

   }

   private Credentials getCredentialsForInstanceWithTag(final RunningInstance instance, String tag) {
      Credentials credentials = null;// default if no keypair exists

      if (instance.getKeyName() != null) {
         credentials = new Credentials(getLoginAccountFor(instance), getPrivateKeyOrNull(instance, tag));
      }
      return credentials;
   }

   @VisibleForTesting
   String getTagForInstance(final RunningInstance instance) {
      String tag = String.format("NOTAG-%s", instance.getId());// default
      try {
         tag = Iterables.getOnlyElement(Iterables.filter(instance.getGroupIds(), new Predicate<String>() {

            @Override
            public boolean apply(String input) {
               return input.startsWith("jclouds#") && input.endsWith("#" + instance.getRegion());
            }

         })).substring(8).replaceAll("#" + instance.getRegion() + "$", "");
      } catch (NoSuchElementException e) {
         logger.debug("no tag parsed from %s's groups: %s", instance.getId(), instance.getGroupIds());
      } catch (IllegalArgumentException e) {
         logger
                  .debug("too many groups match %s; %s's groups: %s", "jclouds#", instance.getId(), instance
                           .getGroupIds());
      }
      return tag;
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

   @VisibleForTesting
   Image resolveImageForInstanceInLocation(final RunningInstance instance, final Location location) {
      Image image = null;
      RegionAndName key = new RegionAndName(instance.getRegion(), instance.getImageId());
      try {
         image = imageMap.get(key);
      } catch (NullPointerException nex) {
         logger.debug("could not find a matching image for instance %s in location %s", instance, location);
      } catch (ComputationException nex) {
         logger.debug("could not find a matching image for instance %s in location %s", instance, location);
      }
      return image;
   }

   @VisibleForTesting
   String getPrivateKeyOrNull(RunningInstance instance, String tag) {
      KeyPair keyPair = credentialsMap.get(new RegionAndName(instance.getRegion(), instance.getKeyName()));
      return keyPair != null ? keyPair.getKeyMaterial() : null;
   }

   @VisibleForTesting
   String getLoginAccountFor(RunningInstance from) {
      org.jclouds.aws.ec2.domain.Image image = null;
      try {
         image = Iterables.getOnlyElement(client.getAMIServices().describeImagesInRegion(from.getRegion(),
                  DescribeImagesOptions.Builder.imageIds(from.getImageId())));
      } catch (NoSuchElementException e) {
         logger.debug("couldn't find image %s/%s", from.getRegion(), from.getImageId());
      }
      return checkNotNull(credentialProvider.execute(image), "login from image: " + from.getImageId()).identity;
   }

}