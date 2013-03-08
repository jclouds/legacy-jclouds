/*
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

package org.jclouds.googlecompute.compute.functions;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.googlecompute.domain.Zone;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.googlecompute.GoogleComputeConstants.GOOGLE_PROVIDER_LOCATION;

/**
 * Transforms a google compute domain specific zone to a generic Zone object.
 *
 * @author David Alves
 */
public class ZoneToLocation implements Function<Zone, Location> {

   @Override
   public Location apply(Zone input) {
      return new LocationBuilder()
              .description(input.getDescription().orNull())
              .metadata(ImmutableMap.of("selfLink", (Object) checkNotNull(input.getSelfLink(), "zone URI")))
              .id(input.getName())
              .scope(LocationScope.ZONE)
              .parent(GOOGLE_PROVIDER_LOCATION)
              .build();
   }
}
