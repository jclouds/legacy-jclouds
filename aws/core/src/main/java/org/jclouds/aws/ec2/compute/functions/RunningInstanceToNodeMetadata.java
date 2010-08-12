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
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
@Singleton
public class RunningInstanceToNodeMetadata implements Function<RunningInstance, NodeMetadata> {

   @Resource
   protected Logger logger = Logger.NULL;

   @VisibleForTesting
   static class FindImageForInstance implements Predicate<Image> {
      private final Location location;
      private final RunningInstance instance;

      FindImageForInstance(Location location, RunningInstance instance) {
         this.location = checkNotNull(location, "location");
         this.instance = checkNotNull(instance, "instance");
      }

      @Override
      public boolean apply(Image input) {
         return input.getProviderId().equals(instance.getImageId())
               && (input.getLocation() == null || input.getLocation().equals(location) || input.getLocation().equals(
                     location.getParent()));
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((instance.getId() == null) ? 0 : instance.getId().hashCode());
         result = prime * result + ((location.getId() == null) ? 0 : location.getId().hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         FindImageForInstance other = (FindImageForInstance) obj;
         if (instance.getId() == null) {
            if (other.instance.getId() != null)
               return false;
         } else if (!instance.getId().equals(other.instance.getId()))
            return false;
         if (location.getId() == null) {
            if (other.location.getId() != null)
               return false;
         } else if (!location.getId().equals(other.location.getId()))
            return false;
         return true;
      }
   }

   @VisibleForTesting
   static final Map<InstanceState, NodeState> instanceToNodeState = ImmutableMap.<InstanceState, NodeState> builder()
         .put(InstanceState.PENDING, NodeState.PENDING).put(InstanceState.RUNNING, NodeState.RUNNING).put(
               InstanceState.SHUTTING_DOWN, NodeState.PENDING).put(InstanceState.TERMINATED, NodeState.TERMINATED).put(
               InstanceState.STOPPING, NodeState.PENDING).put(InstanceState.STOPPED, NodeState.SUSPENDED).build();

   private final EC2Client client;
   private final Map<RegionAndName, KeyPair> credentialsMap;
   private final PopulateDefaultLoginCredentialsForImageStrategy credentialProvider;
   private final Provider<Set<? extends Image>> images;
   private final Set<? extends Location> locations;
   private final Function<RunningInstance, Map<String, String>> instanceToStorageMapping;
   private final ConcurrentMap<RegionAndName, Image> imageMap;

   @Inject
   RunningInstanceToNodeMetadata(EC2Client client, Map<RegionAndName, KeyPair> credentialsMap,
         PopulateDefaultLoginCredentialsForImageStrategy credentialProvider,
         Provider<Set<? extends Image>> images, // to facilitate on-demand
         // refresh of image list
         ConcurrentMap<RegionAndName, Image> imageMap, Set<? extends Location> locations,
         @Named("volumeMapping") Function<RunningInstance, Map<String, String>> instanceToStorageMapping) {
      this.client = checkNotNull(client, "client");
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      this.credentialProvider = checkNotNull(credentialProvider, "credentialProvider");
      this.images = checkNotNull(images, "images");
      this.locations = checkNotNull(locations, "locations");
      this.instanceToStorageMapping = checkNotNull(instanceToStorageMapping, "instanceToStorageMapping");
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

      Map<String, String> extra = getExtra(instance);

      Location location = getLocationForAvailabilityZone(instance);

      Image image = resolveImageForInstanceInLocation(instance, location);

      return new NodeMetadataImpl(id, name, instance.getRegion() + "/" + instance.getId(), location, uri, userMetadata,
            tag, image, state, publicAddresses, privateAddresses, extra, credentials);
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
               .debug("too many groups match %s; %s's groups: %s", "jclouds#", instance.getId(), instance.getGroupIds());
      }
      return tag;
   }

   private Location getLocationForAvailabilityZone(final RunningInstance instance) {
      final String locationId = instance.getAvailabilityZone();

      Location location = Iterables.find(locations, new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getId().equals(locationId);
         }

      });
      return location;
   }

   @VisibleForTesting
   Image resolveImageForInstanceInLocation(final RunningInstance instance, final Location location) {
      Image image = null;
      try {
         image = Iterables.find(images.get(), new FindImageForInstance(location, instance));
      } catch (NoSuchElementException e) {
         RegionAndName key = new RegionAndName(instance.getRegion(), instance.getImageId());
         try {
            image = imageMap.get(key);
         } catch (NullPointerException nex) {
            logger.debug("could not find a matching image for instance %s in location %s", instance, location);
         }
      }
      return image;
   }

   /**
    * Set extras for the node.
    * 
    * Extras are derived from either additional API calls or hard-coded values.
    * 
    * @param instance
    *           instance for which the extras are retrieved
    * @return map with extras
    */
   @VisibleForTesting
   Map<String, String> getExtra(RunningInstance instance) {
      Map<String, String> extra = Maps.newHashMap();
      extra.put("virtualizationType", instance.getVirtualizationType());
      if (instance.getPlacementGroup() != null)
         extra.put("placementGroup", instance.getPlacementGroup());
      if (instance.getSubnetId() != null)
         extra.put("subnetId", instance.getSubnetId());
      // put storage info
      /* TODO: only valid for UNIX */
      extra.putAll(instanceToStorageMapping.apply(instance));

      return extra;
   }

   @VisibleForTesting
   String getPrivateKeyOrNull(RunningInstance instance, String tag) {
      KeyPair keyPair = credentialsMap.get(new RegionAndName(instance.getRegion(), instance.getKeyName()));
      return keyPair != null ? keyPair.getKeyMaterial() : null;
   }

   @VisibleForTesting
   String getLoginAccountFor(RunningInstance from) {
      org.jclouds.aws.ec2.domain.Image image = Iterables.getOnlyElement(client.getAMIServices().describeImagesInRegion(
            from.getRegion(), DescribeImagesOptions.Builder.imageIds(from.getImageId())));
      return checkNotNull(credentialProvider.execute(image), "login from image: " + from.getImageId()).identity;
   }

}