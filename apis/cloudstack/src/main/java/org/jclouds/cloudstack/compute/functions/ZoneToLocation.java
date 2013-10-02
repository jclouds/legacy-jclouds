/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.suppliers.all.JustProvider;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Converts an Zone into a Location.
 */
public class ZoneToLocation implements Function<Zone, Location> {
   private final JustProvider provider;

   // allow us to lazy discover the provider of a resource
   @Inject
   public ZoneToLocation(JustProvider provider) {
      this.provider = checkNotNull(provider, "provider");
   }

   @Override
   public Location apply(Zone zone) {
      return new LocationBuilder().scope(LocationScope.ZONE).metadata(ImmutableMap.<String, Object> of())
          .description(zone.getName()).id(zone.getId())
            .parent(Iterables.getOnlyElement(provider.get())).build();
   }

}
