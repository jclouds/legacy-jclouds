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
package org.jclouds.rackspace.cloudservers.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {
   public static final Pattern SECOND_FIELD_DELIMETED_BY_HYPHEN_ENDING_IN_HYPHEN_HEX = Pattern
            .compile("[^-]+-([^-]+)-[0-9a-f]+");
   private final Location location;
   private final Map<ServerStatus, NodeState> serverToNodeState;
   private final Map<String, ? extends Image> images;

   @Inject
   ServerToNodeMetadata(Map<ServerStatus, NodeState> serverStateToNodeState,
            Map<String, ? extends Image> images, Location location) {
      this.serverToNodeState = checkNotNull(serverStateToNodeState, "serverStateToNodeState");
      this.images = checkNotNull(images, "images");
      this.location = checkNotNull(location, "location");
   }

   @Override
   public NodeMetadata apply(Server from) {
      Matcher matcher = SECOND_FIELD_DELIMETED_BY_HYPHEN_ENDING_IN_HYPHEN_HEX.matcher(from
               .getName());
      final String tag = matcher.find() ? matcher.group(1) : null;
      return new NodeMetadataImpl(from.getId() + "", from.getName(), new LocationImpl(
               LocationScope.HOST, from.getHostId(), from.getHostId(), location), null, from
               .getMetadata(), tag, images.get(from.getImageId().toString()), serverToNodeState
               .get(from.getStatus()), from.getAddresses().getPublicAddresses(), from
               .getAddresses().getPrivateAddresses(), ImmutableMap.<String, String> of(), null);
   }
}