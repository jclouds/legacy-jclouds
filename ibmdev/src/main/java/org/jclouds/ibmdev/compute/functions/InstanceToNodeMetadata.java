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
package org.jclouds.ibmdev.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class InstanceToNodeMetadata implements Function<Instance, NodeMetadata> {
   public static final Pattern ALL_BEFORE_HYPHEN_HEX = Pattern.compile("([^-]+)-[0-9a-f]+");

   @Resource
   protected Logger logger = Logger.NULL;
   private final Map<Instance.Status, NodeState> instanceStateToNodeState;
   private final Map<String, ? extends Image> images;
   private final Map<String, String> credentialsMap;
   private final Map<String, ? extends Location> locations;

   @Inject
   InstanceToNodeMetadata(Map<Instance.Status, NodeState> instanceStateToNodeState,
            Map<String, ? extends Image> images,
            @Named("CREDENTIALS") Map<String, String> credentialsMap,
            Map<String, ? extends Location> locations) {
      this.instanceStateToNodeState = checkNotNull(instanceStateToNodeState,
               "instanceStateToNodeState");
      this.images = checkNotNull(images, "images");
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      this.locations = checkNotNull(locations, "locations");
   }

   @Override
   public NodeMetadata apply(Instance from) {
      Matcher matcher = ALL_BEFORE_HYPHEN_HEX.matcher(from.getName());
      final String tag = matcher.find() ? matcher.group(1) : null;
      Set<String> ipSet = from.getIp() != null ? ImmutableSet.of(from.getIp()) : ImmutableSet
               .<String> of();
      NodeState state = instanceStateToNodeState.get(from.getStatus());
      Image image = images.get(from.getImageId());
      String key = tag != null ? credentialsMap.get(tag) : null;
      return new NodeMetadataImpl(from.getId() + "", from.getName(), from.getId() + "", locations
               .get(image.getLocation()), null, ImmutableMap.<String, String> of(), tag, image,
               state, ipSet, ImmutableList.<String> of(), ImmutableMap.<String, String> of(),
               new Credentials(image.getDefaultCredentials().account, key));
   }
}