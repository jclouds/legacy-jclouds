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

package org.jclouds.ibmdev.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseTagFromName;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.ibmdev.domain.Instance;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class InstanceToNodeMetadata implements Function<Instance, NodeMetadata> {

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

   @Override
   public NodeMetadata apply(Instance from) {
      String tag = parseTagFromName(from.getName());
      Set<String> ipSet = from.getIp() != null ? ImmutableSet.of(from.getIp()) : ImmutableSet.<String> of();
      NodeState state = instanceStateToNodeState.get(from.getStatus());
      Image image = images.get().get(from.getImageId());
      String key = tag != null ? credentialsMap.get(tag) : null;
      return new NodeMetadataImpl(from.getId() + "", from.getName(), from.getId() + "", locations.get().get(
               image.getLocation()), null, ImmutableMap.<String, String> of(), tag, from.getImageId(),
               image != null ? image.getOperatingSystem() : null, state, ipSet, ImmutableList.<String> of(),
               ImmutableMap.<String, String> of(), new Credentials(image.getDefaultCredentials().identity, key));
   }
}