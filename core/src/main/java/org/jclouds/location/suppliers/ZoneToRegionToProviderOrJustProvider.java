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

package org.jclouds.location.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Provider;
import org.jclouds.location.Zone;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ZoneToRegionToProviderOrJustProvider extends RegionToProviderOrJustProvider {
   private final Map<String, String> zoneToRegion;
   private Map<String, Set<String>> isoCodesById;

   @Inject
   ZoneToRegionToProviderOrJustProvider(@Iso3166 Set<String> isoCodes, @Provider String providerName,
            @Provider URI endpoint, @Iso3166 Map<String, Set<String>> isoCodesById,
            @Zone Map<String, String> zoneToRegion) {
      super(isoCodes, providerName, endpoint, ImmutableSet.copyOf(checkNotNull(zoneToRegion, "zoneToRegion").values()),
               isoCodesById);
      this.zoneToRegion = zoneToRegion;
      this.isoCodesById = checkNotNull(isoCodesById, "isoCodesById");
   }

   @Override
   public Set<? extends Location> get() {
      Builder<Location> locations = buildJustProviderOrRegions();
      ImmutableMap<String, Location> idToLocation = uniqueIndex(locations.build(), new Function<Location, String>() {
         @Override
         public String apply(Location from) {
            return from.getId();
         }
      });
      if (zoneToRegion.size() == 1)
         return locations.build();
      for (String zone : zoneToRegion.keySet()) {
         Location parent = idToLocation.get(zoneToRegion.get(zone));
         LocationBuilder builder = new LocationBuilder().scope(LocationScope.ZONE).id(zone).description(zone).parent(
                  parent);
         if (isoCodesById.containsKey(zone))
            builder.iso3166Codes(isoCodesById.get(zone));
         // be cautious.. only inherit iso codes if the parent is a region
         // regions may be added dynamically, and we prefer to inherit an
         // empty set of codes from a region, then a provider, whose code
         // are likely hard-coded.
         else if (parent.getScope() == LocationScope.REGION)
            builder.iso3166Codes(parent.getIso3166Codes());
         locations.add(builder.build());
      }
      return locations.build();
   }
}