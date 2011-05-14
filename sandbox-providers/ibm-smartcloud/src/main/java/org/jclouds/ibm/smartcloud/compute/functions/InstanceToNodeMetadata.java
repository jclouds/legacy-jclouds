/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibm.smartcloud.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.ibm.smartcloud.domain.IP;
import org.jclouds.ibm.smartcloud.domain.Instance;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class InstanceToNodeMetadata implements Function<Instance, NodeMetadata> {

   @VisibleForTesting
   public static final Map<Instance.Status, NodeState> instanceStatusToNodeState = ImmutableMap
            .<Instance.Status, NodeState> builder().put(Instance.Status.ACTIVE, NodeState.RUNNING)//
            .put(Instance.Status.STOPPED, NodeState.SUSPENDED)//
            .put(Instance.Status.REMOVED, NodeState.TERMINATED)//
            .put(Instance.Status.DEPROVISIONING, NodeState.PENDING)//
            .put(Instance.Status.FAILED, NodeState.ERROR)//
            .put(Instance.Status.NEW, NodeState.PENDING)//
            .put(Instance.Status.PROVISIONING, NodeState.PENDING)//
            .put(Instance.Status.REJECTED, NodeState.ERROR)//
            .put(Instance.Status.RESTARTING, NodeState.PENDING)//
            .put(Instance.Status.STARTING, NodeState.PENDING)//
            .put(Instance.Status.STOPPING, NodeState.PENDING)//
            .put(Instance.Status.DEPROVISION_PENDING, NodeState.PENDING)//
            .put(Instance.Status.UNKNOWN, NodeState.UNRECOGNIZED).build();
   @Resource
   protected Logger logger = Logger.NULL;
   private final Map<Instance.Status, NodeState> instanceStateToNodeState;
   private final Supplier<Map<String, ? extends Image>> images;
   private final Map<String, String> credentialsMap;
   private final Supplier<Map<String, ? extends Location>> locations;

   @Inject
   InstanceToNodeMetadata(Map<Instance.Status, NodeState> instanceStateToNodeState,
            Supplier<Map<String, ? extends Image>> images, @Named("CREDENTIALS") Map<String, String> credentialsMap,
            Supplier<Map<String, ? extends Location>> locations) {
      this.instanceStateToNodeState = checkNotNull(instanceStateToNodeState, "instanceStateToNodeState");
      this.images = checkNotNull(images, "images");
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      this.locations = checkNotNull(locations, "locations");
   }

   private static Function<IP, String> ipFromIP = new Function<IP, String>() {

      @Override
      public String apply(IP arg0) {
         return arg0 != null ? arg0.getIP() : null;
      }

   };

   @Override
   public NodeMetadata apply(Instance from) {
      // TODO hardware
      String group = parseGroupFromName(from.getName());
      String ip = ipFromIP.apply(from.getPrimaryIP());
      Set<String> ipSet = ip != null ? ImmutableSet.of(ip) : ImmutableSet.<String> of();
      Image image = images.get().get(from.getImageId());
      String key = credentialsMap.get(from.getKeyName());
      return new NodeMetadataBuilder().ids(from.getId() + "").name(from.getName()).location(
               locations.get().get(image.getLocation())).group(group).imageId(from.getImageId()).state(
               instanceStateToNodeState.get(from.getStatus())).operatingSystem(
               image != null ? image.getOperatingSystem() : null).publicAddresses(ipSet).privateAddresses(
               Iterables.filter(Iterables.transform(from.getSecondaryIPs(), ipFromIP), Predicates.notNull()))
               .credentials(new Credentials(image.getDefaultCredentials().identity, key)).build();
   }
}
