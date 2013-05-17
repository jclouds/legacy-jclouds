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
package org.jclouds.location.suppliers.all;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Zone;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ZoneToRegionToProviderOrJustProvider implements LocationsSupplier {

   @Resource
   protected Logger logger = Logger.NULL;

   private final RegionToProviderOrJustProvider regionToProviderOrJustProvider;
   private final Supplier<Set<String>> zoneIdsSupplier;
   private final Supplier<Map<String, Supplier<Set<String>>>> isoCodesByIdSupplier;
   private final Supplier<Map<String, Supplier<Set<String>>>> regionIdToZoneIdsSupplier;

   @Inject
   ZoneToRegionToProviderOrJustProvider(RegionToProviderOrJustProvider regionToProviderOrJustProvider,
            @Zone Supplier<Set<String>> zoneIdsSupplier,
            @Iso3166 Supplier<Map<String, Supplier<Set<String>>>> isoCodesByIdSupplier,
            @Zone Supplier<Map<String, Supplier<Set<String>>>> regionIdToZoneIdsSupplier) {
      this.regionToProviderOrJustProvider = checkNotNull(regionToProviderOrJustProvider,
               "regionToProviderOrJustProvider");
      this.zoneIdsSupplier = checkNotNull(zoneIdsSupplier, "zoneIdsSupplier");
      this.regionIdToZoneIdsSupplier = checkNotNull(regionIdToZoneIdsSupplier, "regionIdToZoneIdsSupplier");
      this.isoCodesByIdSupplier = checkNotNull(isoCodesByIdSupplier, "isoCodesByIdSupplier");
   }

   @Override
   public Set<? extends Location> get() {
      Set<? extends Location> regionsOrJustProvider = regionToProviderOrJustProvider.get();
      Set<String> zoneIds = zoneIdsSupplier.get();
      if (zoneIds.size() == 0)
         return regionsOrJustProvider;
      Map<String, Location> zoneIdToParent = setParentOfZoneToRegionOrProvider(zoneIds, regionsOrJustProvider);
      Map<String, Supplier<Set<String>>> isoCodesById = isoCodesByIdSupplier.get();

      Builder<Location> locations = ImmutableSet.builder();
      if (!Iterables.all(regionsOrJustProvider, LocationPredicates.isProvider()))
         locations.addAll(regionsOrJustProvider);
      for (Map.Entry<String, Location> entry : zoneIdToParent.entrySet()) {
         String zoneId = entry.getKey();
         Location parent = entry.getValue();
         LocationBuilder builder = new LocationBuilder().scope(LocationScope.ZONE).id(zoneId).description(zoneId)
                  .parent(parent);
         if (isoCodesById.containsKey(zoneId))
            builder.iso3166Codes(isoCodesById.get(zoneId).get());
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

   private Map<String, Location> setParentOfZoneToRegionOrProvider(Set<String> zoneIds,
            Set<? extends Location> locations) {
      // mutable, so that we can query current state when adding. safe as its temporary
      Map<String, Location> zoneIdToParent = Maps.newLinkedHashMap();

      Location provider = Iterables.find(locations, LocationPredicates.isProvider(), null);
      if (locations.size() == 1 && provider != null) {
         for (String zone : zoneIds)
            zoneIdToParent.put(zone, provider);
      } else {
         // note that we only call regionIdToZoneIdsSupplier if there are region locations present
         // they cannot be, if the above is true
         Map<String, Supplier<Set<String>>> regionIdToZoneIds = regionIdToZoneIdsSupplier.get();
         for (Location region : Iterables.filter(locations, LocationPredicates.isRegion())) {
            provider = region.getParent();
            if (regionIdToZoneIds.containsKey(region.getId())) {
               for (String zoneId : regionIdToZoneIds.get(region.getId()).get())
                  zoneIdToParent.put(zoneId, region);
            } else {
               logger.debug("no zones configured for region: %s", region);
            }
         }
      }

      SetView<String> orphans = Sets.difference(zoneIds, zoneIdToParent.keySet());
      if (orphans.size() > 0) {
         // any unmatched zones should have their parents set to the provider
         checkState(
                  provider != null,
                  "cannot configure zones %s as we need a parent, and the only available location [%s] is not a provider",
                  zoneIds, locations);
         for (String orphanedZoneId : orphans)
            zoneIdToParent.put(orphanedZoneId, provider);
      }
      
      checkState(zoneIdToParent.keySet().containsAll(zoneIds), "orphaned zones: %s ", Sets.difference(zoneIds,
               zoneIdToParent.keySet()));
      return zoneIdToParent;
   }
}
