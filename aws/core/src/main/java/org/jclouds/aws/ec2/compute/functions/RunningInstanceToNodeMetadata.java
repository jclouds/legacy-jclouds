/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.net.InetAddress;
import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.aws.ec2.services.AMIClient;
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

   private static class FindImageForInstance implements Predicate<Image> {
      private final Location location;
      private final RunningInstance instance;

      private FindImageForInstance(Location location, RunningInstance instance) {
         this.location = location;
         this.instance = instance;
      }

      @Override
      public boolean apply(Image input) {
         return input.getId().equals(instance.getImageId())
                  && (input.getLocation() == null || input.getLocation().equals(location) || input
                           .getLocation().equals(location.getParent()));
      }
   }

   private static final Map<InstanceState, NodeState> instanceToNodeState = ImmutableMap
            .<InstanceState, NodeState> builder().put(InstanceState.PENDING, NodeState.PENDING)
            .put(InstanceState.RUNNING, NodeState.RUNNING).put(InstanceState.SHUTTING_DOWN,
                     NodeState.PENDING).put(InstanceState.TERMINATED, NodeState.TERMINATED).build();

   private final AMIClient amiClient;
   private final Map<RegionAndName, KeyPair> credentialsMap;
   private final PopulateDefaultLoginCredentialsForImageStrategy credentialProvider;
   private final Set<? extends Image> images;
   private final Set<? extends Location> locations;
   private final Function<RunningInstance, Map<String, String>> instanceToStorageMapping;

   @Inject
   RunningInstanceToNodeMetadata(
            AMIClient amiClient,
            Map<RegionAndName, KeyPair> credentialsMap,
            PopulateDefaultLoginCredentialsForImageStrategy credentialProvider,
            Set<? extends Image> images,
            Set<? extends Location> locations,
            @Named("volumeMapping") Function<RunningInstance, Map<String, String>> instanceToStorageMapping) {
      this.amiClient = checkNotNull(amiClient, "amiClient");
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      this.credentialProvider = checkNotNull(credentialProvider, "credentialProvider");
      this.images = checkNotNull(images, "images");
      this.locations = checkNotNull(locations, "locations");
      this.instanceToStorageMapping = checkNotNull(instanceToStorageMapping);
   }

   @Override
   public NodeMetadata apply(final RunningInstance instance) {
      String id = checkNotNull(instance, "instance").getId();
      String name = null; // user doesn't determine a node name;
      URI uri = null; // no uri to get rest access to host info

      String tag = String.format("NOTAG-%s", instance.getId());// default
      try {
         tag = Iterables.getOnlyElement(
                  Iterables.filter(instance.getGroupIds(), new Predicate<String>() {

                     @Override
                     public boolean apply(String input) {
                        return input.startsWith("jclouds#");
                     }

                  })).substring(8);
      } catch (NoSuchElementException e) {
         logger
                  .warn("no tag parsed from %s's groups: %s", instance.getId(), instance
                           .getGroupIds());
      } catch (IllegalArgumentException e) {
         logger.warn("too many groups match %s; %s's groups: %s", "jclouds#", instance.getId(),
                  instance.getGroupIds());
      }

      Credentials credentials = null;// default if no keypair exists

      if (instance.getKeyName() != null) {
         credentials = new Credentials(getLoginAccountFor(instance), getPrivateKeyOrNull(instance,
                  tag));
      }

      Map<String, String> userMetadata = ImmutableMap.<String, String> of();

      NodeState state = instanceToNodeState.get(instance.getInstanceState());

      Set<InetAddress> publicAddresses = nullSafeSet(instance.getIpAddress());
      Set<InetAddress> privateAddresses = nullSafeSet(instance.getPrivateIpAddress());

      final String locationId = instance.getAvailabilityZone();

      Map<String, String> extra = getExtra(instance);

      final Location location = Iterables.find(locations, new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getId().equals(locationId);
         }

      });

      Image image = null;
      try {
         image = Iterables.find(images, new FindImageForInstance(location, instance));
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching image for instance %s in location %s", instance,
                  location);
      }
      return new NodeMetadataImpl(id, name, location, uri, userMetadata, tag, image, state,
               publicAddresses, privateAddresses, extra, credentials);
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

      // put storage info
      /* TODO: only valid for UNIX */
      extra.putAll(instanceToStorageMapping.apply(instance));

      return extra;
   }

   @VisibleForTesting
   String getPrivateKeyOrNull(RunningInstance instance, String tag) {
      KeyPair keyPair = credentialsMap.get(new RegionAndName(instance.getRegion(), instance
               .getKeyName()));
      return keyPair != null ? keyPair.getKeyMaterial() : null;
   }

   @VisibleForTesting
   String getLoginAccountFor(RunningInstance from) {
      org.jclouds.aws.ec2.domain.Image image = Iterables.getOnlyElement(amiClient
               .describeImagesInRegion(from.getRegion(), DescribeImagesOptions.Builder
                        .imageIds(from.getImageId())));
      return checkNotNull(credentialProvider.execute(image), "login from image: "
               + from.getImageId()).account;
   }

}