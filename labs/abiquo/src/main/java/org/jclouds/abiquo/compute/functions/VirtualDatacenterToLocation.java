/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Converts a {@link VirtualDatacenter} to a {@link Location} one.
 * <p>
 * Virtual datacenters will be considered zones, since images will be deployed
 * in a virtual datacenter. Each zone will be scoped into a physical datacenter
 * (region).
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class VirtualDatacenterToLocation implements Function<VirtualDatacenter, Location> {
   private final Function<Datacenter, Location> datacenterToLocation;

   private final Supplier<Map<Integer, Datacenter>> regionMap;

   @Inject
   public VirtualDatacenterToLocation(final Function<Datacenter, Location> datacenterToLocation,
         @Memoized final Supplier<Map<Integer, Datacenter>> regionMap) {
      this.datacenterToLocation = checkNotNull(datacenterToLocation, "datacenterToLocation");
      this.regionMap = checkNotNull(regionMap, "regionMap");
   }

   @Override
   public Location apply(final VirtualDatacenter vdc) {
      LocationBuilder builder = new LocationBuilder();
      builder.id(vdc.getId().toString());
      builder.description(vdc.getName());
      builder.metadata(ImmutableMap.<String, Object> of());
      builder.scope(LocationScope.ZONE);
      builder.iso3166Codes(ImmutableSet.<String> of());

      Datacenter parent = regionMap.get().get(vdc.unwrap().getIdFromLink(ParentLinkName.DATACENTER));
      builder.parent(datacenterToLocation.apply(parent));

      return builder.build();
   }
}
